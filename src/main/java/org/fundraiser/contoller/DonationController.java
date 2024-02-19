package org.fundraiser.contoller;

import lombok.extern.slf4j.Slf4j;
import org.fundraiser.dto.info.DonationListItem;
import org.fundraiser.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/donations")
@Slf4j
public class DonationController {

    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping("{id}")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<DonationListItem> getDonationById(@PathVariable("id") Long id) {
        log.info("Http request, GET, / /api/donations/{id} with id: " + id);
        return new ResponseEntity<>(donationService.getDonationById(id), HttpStatus.OK);
    }

}
