package org.fundraiser.contoller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.fundraiser.domain.Donation;
import org.fundraiser.dto.info.Order;
import org.fundraiser.dto.info.PaymentDetails;
import org.fundraiser.dto.info.RefundResponse;
import org.fundraiser.service.PaypalService;
import org.fundraiser.utils.AESUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/api/paypal")
public class PaypalController {

    private static final String ENCRYPTION_FORMAT = "UTF-8";

    private final PaypalService paypalService;

    private final AESUtils aesUtils;

    public PaypalController(PaypalService paypalService, AESUtils aesUtils) {
        this.paypalService = paypalService;
        this.aesUtils = aesUtils;
    }

    @GetMapping
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public String defaultResponse() {
        return "home";
    }

    @PostMapping("/pay")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<String> payment(@RequestBody Order order) {
        try {
            PaymentDetails paymentDetails = preparePaymentDetails(order);
            Payment payment = paypalService.createPayment(paymentDetails, order.getPayer());
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return ResponseEntity.ok(new RedirectView(link.getHref()).getUrl());
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to find approval URL");
        } catch (PayPalRESTException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException | UnsupportedEncodingException |
                 InvalidAlgorithmParameterException e) {
            return handleException(e);
        }
    }

    private PaymentDetails preparePaymentDetails(Order order) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        String baseUrl = "http://ec2-3-123-128-129.eu-central-1.compute.amazonaws.com:8080/api/paypal";
        String cancelUrl = baseUrl + "/pay/cancel";

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setTotal(String.valueOf(order.getPrice()).replace(",", "."));
        paymentDetails.setCurrency(order.getCurrency());
        paymentDetails.setMethod(order.getMethod());
        paymentDetails.setIntent(order.getIntent());
        paymentDetails.setDescription(order.getDescription());
        paymentDetails.setCampaignId(String.valueOf(order.getCampaignId()));
        paymentDetails.setCancelUrl(cancelUrl);
        paymentDetails.setSuccessUrl(String.format("%s/pay/success?payer=%s&amount=%s&currency=%s&campaignId=%s",
                baseUrl,
                URLEncoder.encode(encryptData(order.getPayer()), ENCRYPTION_FORMAT),
                URLEncoder.encode(formatAndEncryptAmount(order.getPrice()), ENCRYPTION_FORMAT),
                URLEncoder.encode(encryptData(order.getCurrency()), ENCRYPTION_FORMAT),
                URLEncoder.encode(encryptData(String.valueOf(order.getCampaignId())), ENCRYPTION_FORMAT)));
        return paymentDetails;
    }

    private String encryptData(String data) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        return aesUtils.encrypt(data);
    }

    private String formatAndEncryptAmount(Double amount) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        return aesUtils.encrypt(formatAmount(amount));
    }

    private String formatAmount(Double amount) {
        return String.format("%.2f", amount).replace(",", ".");
    }

    private ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @GetMapping("/pay/cancel")
    public ResponseEntity<String> cancelPay() {
        return ResponseEntity.status(HttpStatus.OK).body("Payment Cancelled successfully");
    }

    @GetMapping("/pay/success")
    public ResponseEntity<String> successPay(@RequestParam("payer") String username,
                                             @RequestParam("amount") String total,
                                             @RequestParam("currency") String currency,
                                             @RequestParam("campaignId") String campaignId,
                                             @RequestParam("paymentId") String paymentId,
                                             @RequestParam("PayerID") String payerId) {
        try {
            String decyptedUsername = aesUtils.decrypt(username);
            String decyptedTotal = aesUtils.decrypt(total);
            String decyptedCurrency = aesUtils.decrypt(currency);
            String decyptedCampaignId = aesUtils.decrypt(campaignId);
            Payment payment = paypalService.executePayment(decyptedUsername, paymentId, payerId, decyptedTotal, decyptedCurrency, decyptedCampaignId);
            if ("approved".equalsIgnoreCase(payment.getState())) {
                paypalService.setTransactionId(decyptedUsername, paypalService.getTransactionId(payment));
                paypalService.createPayoutIfFundsCollected(decyptedCampaignId);
                return ResponseEntity.ok("Payment Successful from user: " + decyptedUsername + " with Payer ID: " + payerId + " and  Transaction ID: " + paypalService.getTransactionId(payment));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Payment not approved");
            }
        } catch (PayPalRESTException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            log.error("Payment execution failed due to error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment execution failed: " + e.getMessage());
        }
    }

    @PostMapping("/refund")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<String> refundPayment(@RequestParam String saleId) {
        try {
            Donation donation = paypalService.getDonation(saleId);
            RefundResponse refund = paypalService.refundSale(donation);
            return ResponseEntity.ok(refund.toString());
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
