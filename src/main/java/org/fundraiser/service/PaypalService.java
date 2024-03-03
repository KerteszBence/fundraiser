package org.fundraiser.service;

import com.paypal.api.payments.Currency;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.fundraiser.domain.Campaign;
import org.fundraiser.domain.Donation;
import org.fundraiser.dto.info.PaymentDetails;
import org.fundraiser.dto.info.RefundResponse;
import org.fundraiser.event.DonationRefundCampaignEvent;
import org.fundraiser.event.DonationRefundCreatorEvent;
import org.fundraiser.event.DonationRefundDonorEvent;
import org.fundraiser.event.DonationRefundedEvent;
import org.fundraiser.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@Slf4j
public class PaypalService {

    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode;

    private final DonationRepository donationRepository;
    private final DonationService donationService;
    private final CampaignService campaignService;
    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public PaypalService(DonationService donationService, CampaignService campaignService, AccountService accountService,
                         DonationRepository donationRepository, ApplicationEventPublisher eventPublisher) {
        this.donationService = donationService;
        this.campaignService = campaignService;
        this.accountService = accountService;
        this.donationRepository = donationRepository;
        this.eventPublisher = eventPublisher;
    }

    private APIContext getNewAPIContext() {
        return new APIContext(clientId, clientSecret, mode);
    }

    public Payment createPayment(PaymentDetails paymentDetails, String username) throws PayPalRESTException {
        checkCampaign(Long.parseLong(paymentDetails.getCampaignId()));
        checkAccount(username);
        return createAndSetPayment(paymentDetails);
    }

    private void checkCampaign(Long campaignid) throws PayPalRESTException {
        if (!campaignService.getCampaignInfo(campaignid).isOpen()) {
            throw new PayPalRESTException("The campaign doesn't accept donations anymore");
        }
    }

    private void checkAccount(String username) throws PayPalRESTException {
        if (!accountService.findAccountByUserName(username).getIsEnabled()) {
            throw new PayPalRESTException("Please verify your email address to make donations");
        }
    }

    private Payment createAndSetPayment(PaymentDetails paymentDetails) throws PayPalRESTException {
        Amount amount = createAmount(paymentDetails.getTotal(), paymentDetails.getCurrency());
        Transaction transaction = createTransaction(amount, paymentDetails.getDescription());
        List<Transaction> transactions = Collections.singletonList(transaction);
        Payer payerObject = createPayer(paymentDetails.getMethod());
        RedirectUrls redirectUrls = createRedirectUrls(paymentDetails.getCancelUrl(), paymentDetails.getSuccessUrl());
        Payment payment = new Payment();
        payment.setIntent(paymentDetails.getIntent());
        payment.setPayer(payerObject);
        payment.setTransactions(transactions);
        payment.setRedirectUrls(redirectUrls);
        return payment.create(getNewAPIContext());
    }

    private Amount createAmount(String total, String currency) {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(total);
        return amount;
    }

    private Transaction createTransaction(Amount amount, String description) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);
        return transaction;
    }

    private Payer createPayer(String method) {
        Payer payer = new Payer();
        payer.setPaymentMethod(method);
        return payer;
    }

    private RedirectUrls createRedirectUrls(String cancelUrl, String successUrl) {
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        return redirectUrls;
    }

    public Payment executePayment(String username, String paymentId, String payerId, String total, String currency, String campaignid) throws PayPalRESTException {
        checkCampaign(Long.valueOf(campaignid));
        checkAccount(username);

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        donationService.createDonation(username, Long.parseLong(campaignid), total, currency);

        donationService.setPaypalId(username, payerId);

        return payment.execute(getNewAPIContext(), paymentExecution);
    }

    public String getTransactionId(Payment payment) {
        if (payment == null || payment.getTransactions() == null) {
            return "Payment not found";
        }

        return getSaleIdFromTransactions(payment.getTransactions());
    }

    private String getSaleIdFromTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            String saleId = getSaleIdFromTransaction(transaction);

            if (saleId != null) {
                return saleId;
            }
        }
        return "Payment not found";
    }

    private String getSaleIdFromTransaction(Transaction transaction) {
        if (transaction.getRelatedResources() == null) {
            return null;
        }
        return getSaleIdFromRelatedResources(transaction.getRelatedResources());
    }

    private String getSaleIdFromRelatedResources(List<RelatedResources> relatedResources) {
        for (RelatedResources relatedResource : relatedResources) {
            if (relatedResource.getSale() != null) {
                return relatedResource.getSale().getId();
            }
        }
        return null;
    }

    public void setTransactionId(String username, String transactionId) {
        donationService.setTransactionId(username, transactionId);
    }

    public void createPayoutIfFundsCollected(String campaignId) throws PayPalRESTException {
        Campaign campaign = campaignService.findCampaignById(Long.parseLong(campaignId));
        double targetAmount = campaign.getTarget();
        double collectedAmount = campaign.getCollected();

        if (targetAmount <= collectedAmount) {
            campaign.setOpen(false);
            String formattedAmount = String.format("%.2f", collectedAmount).replace(",", ".");
            createPayout(campaign.getAccount().getPaypalAccountEmail(), formattedAmount, campaign.getCurrency());
        }
    }

    public void createPayout(String recipientEmail, String amount, String currency) throws PayPalRESTException {
        PayoutItem payoutItem = createPayoutItem(recipientEmail, amount, currency);
        PayoutSenderBatchHeader senderBatchHeader = createPayoutSenderBatchHeader();

        Payout payout = new Payout();
        payout.setSenderBatchHeader(senderBatchHeader);
        List<PayoutItem> items = new ArrayList<>();
        items.add(payoutItem);
        payout.setItems(items);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("sync_mode", "false");

        payout.create(getNewAPIContext(), parameters);
    }

    private PayoutItem createPayoutItem(String recipientEmail, String amount, String currency) {
        PayoutItem payoutItem = new PayoutItem();
        payoutItem.setRecipientType("EMAIL");
        payoutItem.setReceiver(recipientEmail);
        payoutItem.setAmount(new Currency(currency, amount));
        payoutItem.setNote("Congratulations, your fundraising goal has been met!");
        return payoutItem;
    }

    private PayoutSenderBatchHeader createPayoutSenderBatchHeader() {
        PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();
        senderBatchHeader.setSenderBatchId("batch_" + System.currentTimeMillis());
        senderBatchHeader.setEmailSubject("You've received a payout!");
        return senderBatchHeader;
    }

    public Donation getDonation(String saleId){
        return donationRepository.findDonationByTransactionId(saleId);
    }

    @Scheduled(fixedRate = 60000) //60 másodpercenként fut
    public void refundClosedCampaignDonations() {
        try {
            List<Campaign> campaigns = campaignService.closeExpiredCampaigns();
            for (Campaign campaign : campaigns) {
                refundCampaignDonations(campaign);
            }
        } catch (PayPalRESTException e) {
            log.error("Error while refunding expired campaigns", e.getMessage());
        }
    }

    public void refundCampaignDonations(Campaign campaign) throws PayPalRESTException {
        List<Donation> donationList = donationService.getAllDonationsByCampaignId(campaign.getId());
        for (Donation donation : donationList) {
            log.info("sending donation to refund method: " + donation);
            refundSale(donation);
        }
    }

    public RefundResponse refundSale(Donation donation) throws PayPalRESTException {
        try {
            if (Boolean.TRUE.equals(donation.getRefunded())) {
                log.info("donation was already refunded: " + donation);
                RefundResponse refundResponse = new RefundResponse();
                refundResponse.setCurrency(donation.getCurrency());
                refundResponse.setAmount(donation.getBalance().toString());
                refundResponse.setStatus("Already refunded");
                return refundResponse;
            }

            log.info("donation wasn't refunded yet, attempting refund of: " + donation);

            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setAmount(new Amount(donation.getCurrency(), String.valueOf(donation.getBalance()).replace(",", ".")));

            Sale sale = new Sale();
            sale.setId(donation.getTransactionId());

            log.info("refunding sale for donation: " + donation);

            Refund refund = sale.refund(getNewAPIContext(), refundRequest);

            RefundResponse refundResponse = new RefundResponse();
            refundResponse.setRefundId(refund.getId());
            refundResponse.setCurrency(refund.getAmount().getCurrency());
            refundResponse.setAmount(refund.getAmount().getTotal());
            refundResponse.setStatus(refund.getState());

            donation.setRefunded(true);
            donationRepository.save(donation);

            log.info(donation + " has been refunded");

            return refundResponse;
        } catch (PayPalRESTException e) {
            log.error("Error while refunding sale for donation: " + donation, e.getMessage());
            throw e;
        }
    }

    @EventListener
    public void onDonationRefundCampaignEvent(DonationRefundCampaignEvent donationRefundEvent) {
        log.info("DonationRefundCampaignEvent received with: " + donationRefundEvent.getDonation());
        List<Donation> campaignDonationList = donationRefundEvent.getDonation();

        for (Donation donation : campaignDonationList) {
            try {
                refundSale(donation);
            } catch (PayPalRESTException e) {
                log.error("Error while refunding sale for donation: " + donation, e.getMessage());
            }
            eventPublisher.publishEvent(new DonationRefundedEvent(this, donation, donationRefundEvent.getAccount()));
        }

        log.info("DonationRefundCampaignEvent complete with: " + donationRefundEvent.getDonation());
    }

    @EventListener
    public void onDonationRefundCreatorEvent(DonationRefundCreatorEvent donationRefundEvent) {
        log.info("onDonationRefundCreatorEvent received with: " + donationRefundEvent.getAccount().getUsername());

        List<Donation> accountDonationList = donationRefundEvent.getAccount().getOutgoingDonations();
        accountDonationList.addAll(donationRefundEvent.getAccount().getIncomingDonations());

        for (Donation donation : accountDonationList) {
            eventPublisher.publishEvent(new DonationRefundedEvent(this, donation, donationRefundEvent.getAccount()));
        }

        log.info("onDonationRefundCreatorEvent complete with: " + donationRefundEvent.getAccount().getUsername());
    }

    @EventListener
    public void onDonationRefundDonorEvent(DonationRefundDonorEvent donationRefundEvent) {
        log.info("onDonationRefundDonorEvent received with: " + donationRefundEvent.getAccount().getUsername());

        List<Donation> donationList = donationRefundEvent.getAccount().getOutgoingDonations();

        for (Donation donation : donationList) {
            eventPublisher.publishEvent(new DonationRefundedEvent(this, donation, donationRefundEvent.getAccount()));
        }
        log.info("onDonationRefundDonorEvent complete with: " + donationRefundEvent.getAccount().getUsername());
    }

}
