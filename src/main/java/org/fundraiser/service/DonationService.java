package org.fundraiser.service;

import lombok.extern.slf4j.Slf4j;
import org.fundraiser.domain.Account;
import org.fundraiser.domain.Campaign;
import org.fundraiser.domain.Donation;
import org.fundraiser.dto.info.DonationListItem;
import org.fundraiser.event.DonationRefundedEvent;
import org.fundraiser.exceptionhandling.donation.DonationNotFindByIdException;
import org.fundraiser.repository.DonationRepository;
import org.fundraiser.utils.mapper.DonationListItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class DonationService {

    private final DonationRepository donationRepository;
    private final AccountService accountService;
    private final CampaignService campaignService;
    private final FixerService fixerService;

    @Autowired
    public DonationService(DonationRepository donationRepository, AccountService accountService, CampaignService campaignService, FixerService fixerService) {
        this.donationRepository = donationRepository;
        this.accountService = accountService;
        this.campaignService = campaignService;
        this.fixerService = fixerService;
    }

    public Donation findDonationById(Long id) {
        Optional<Donation> donationOptional = donationRepository.findById(id);
        if (donationOptional.isEmpty()) {
            throw new DonationNotFindByIdException(id);
        }
        return donationOptional.get();
    }

    public void createDonation(String sourceUsername, Long campaignId, String total, String currency) { // CreateDonationCommand Order-re alakítva a paypal miatt
        Account sourceAccount = accountService.findAccountByUserName(sourceUsername);
        Campaign campaign = campaignService.findCampaignById(campaignId);
        Account targetAccount = campaign.getAccount();
        Donation donationToSave = new Donation();

        Double balance = Double.parseDouble(total) * 0.96;

        donationToSave.setBalance(balance);
        donationToSave.setSource(sourceAccount);
        donationToSave.setTarget(targetAccount);
        donationToSave.setCampaign(campaign);
        donationToSave.setCurrency(currency);
        donationToSave.setRefunded(false);
        donationToSave.setTransactionTime(LocalDateTime.now());
        donationRepository.save(donationToSave);
        if (currency.equalsIgnoreCase("EUR")) {
            campaign.setCollected(campaign.getCollected() + fixerService.convertCurrencyFromEuro(campaign.getCurrency(), balance));
        } else {
            try {
                Double euro = fixerService.convertCurrencyToEuro(currency, balance);
                double collected = campaign.getCollected() + fixerService.convertCurrencyFromEuro(campaign.getCurrency(), euro);
                campaign.setCollected(Double.valueOf(String.format("%.2f", collected).replace(",", ".")));
            } catch (Exception e) {
                log.error("An error occurred while converting currency: ", e.getMessage());
            }
        }
    }

    public void setPaypalId(String username, String paypalId) {
        Donation donation = getLastDonationByUsername(username);
        Account account = accountService.findAccountByUserName(username);
        account.setPaypalId(paypalId);
        donation.setPaypalId(paypalId);
    }

    public void setTransactionId(String username, String transactionId) {
        Donation donation = getLastDonationByUsername(username);
        donation.setTransactionId(transactionId);
    }

    public Donation getLastDonationByUsername(String username) {
        Page<Donation> page = donationRepository.findLastDonationBySourceUsername(username, PageRequest.of(0, 1));
        return page.getContent().isEmpty() ? null : page.getContent().get(0);
    }

    public DonationListItem getDonationById(Long id) {
        return DonationListItemMapper.donationListItemMapper(findDonationById(id));
    }

    public List<Donation> getAllDonationsByCampaignId(Long id) {
        return donationRepository.findAllCampaignDonations(id);
    }

    @EventListener
    public void onDonationRefundedEvent(DonationRefundedEvent event) {
        log.info("onDonationRefundedEvent received with: " + event.getDonation());
        Donation donation = event.getDonation();
        donation.setCampaign(null);
        if (event.getAccount().getId().equals(donation.getSource().getId())) {
            donation.setSource(null);
        } else {
            donation.setTarget(null);
        }
        donationRepository.save(donation);
        log.info("onDonationRefundedEvent complete with: " + event.getDonation());
    }

}
