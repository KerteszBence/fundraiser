package org.fundraiser.contoller;

import lombok.extern.slf4j.Slf4j;
import org.fundraiser.config.CampaignCategory;
import org.fundraiser.dto.create.CreateCampaignCommand;
import org.fundraiser.dto.info.CampaignInfo;
import org.fundraiser.dto.info.CampaignResponse;
import org.fundraiser.dto.info.DonationListItem;
import org.fundraiser.dto.info.MyCampaignInfo;
import org.fundraiser.dto.update.*;
import org.fundraiser.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@Slf4j
public class CampaignController {

    private final CampaignService campaignService;

    private static final String WITH_BODY = "with body: ";

    @Autowired
    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/create")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> createCampaign(@Valid @RequestBody CreateCampaignCommand command) {
        log.info(("Http request, POST, / /api/campaigns: " + command.toString()));
        campaignService.createCampaign(command);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{username}/mycampaign/{id}")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<MyCampaignInfo> getMyCampaignInfo(@PathVariable("username") String username, @PathVariable("id") Long id) {
        log.info("Http request, GET, / /api/campaigns/{username}/mycampaign/{id} with username: " + username + " and id: " + id);
        return new ResponseEntity<>(campaignService.getMyCampaignInfo(username, id), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignInfo> getCampaignInfo(@PathVariable("id") Long id) {
        log.info("Http request, GET, / /api/campaigns/{id} with id: " + id);
        return new ResponseEntity<>(campaignService.getCampaignInfo(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/alldonations")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<List<DonationListItem>> getAllDonationByCampaign(@PathVariable("id") Long id) {
        log.info("Http request, GET, / /api/campaigns/{id}/alldonations with id: " + id);
        return new ResponseEntity<>(campaignService.getAllDonationByCampaign(id), HttpStatus.OK);
    }

    @GetMapping("/getallcampaigns")
    public ResponseEntity<CampaignResponse> getAllCampaigns(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                                      @RequestParam(value = "pageSize", defaultValue = "12", required = false) int pageSize) {
        log.info("Http request, GET, / /api/campaigns/getallcampaignbypageable");
        return new ResponseEntity<>(campaignService.getAllCampaignsByPageable(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("/allcampaign_by_category/{category}")
    public ResponseEntity<CampaignResponse> getAllCampaignByCategory(@PathVariable("category") CampaignCategory category,
                                                                     @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                                     @RequestParam(value = "pageSize", defaultValue = "12", required = false) int pageSize) {
        log.info("Http request, GET, / /api/campaigns/allcampaign_by_category/{category} with category: " + category);
        return new ResponseEntity<>(campaignService.getAllCampaignsByCategory(category, pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("/allcampaign_by_creator/{username}")
    public ResponseEntity<CampaignResponse> getAllCampaignByCreator(@PathVariable("username") String username,
                                                                    @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                                    @RequestParam(value = "pageSize", defaultValue = "12", required = false) int pageSize) {
        log.info("Http request, GET, / /api/campaigns/allcampaign_by_creator/{username} with username: " + username);
        return new ResponseEntity<>(campaignService.getAllCampaignsByCreator(username, pageNo, pageSize), HttpStatus.OK);
    }

    @PutMapping("/{id}/avatarid")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> updateCampaignAvatarId(@PathVariable("id") Long id, @Valid @RequestBody UpdateCampaignAvatarIdCommand command) {
        log.info("Http request, PUT, / /api/campaigns/{id}/avatarurl with variable: " + id + WITH_BODY + command.toString());
        campaignService.updateCampaignAvatarId(id, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/coverid")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> updateCampaignCoverid(@PathVariable("id") Long id, @Valid @RequestBody UpdateCampaignCoverIdCommand command) {
        log.info("Http request, PUT, / /api/campaigns/{id}/coverurl with variable: " + id + WITH_BODY + command.toString());
        campaignService.updateCampaignCoverId(id, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/introduction")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> updateCampaignIntroduction(@PathVariable("id") Long id, @Valid @RequestBody UpdateCampaignIntroductionCommand command) {
        log.info("Http request, PUT, / /api/campaigns/{id}/introduction with variable: " + id + WITH_BODY + command.toString());
        campaignService.updateCampaignIntroduction(id, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/introvideoid")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> updateCampaignIntroVideoid(@PathVariable("id") Long id, @Valid @RequestBody UpdateCampaignIntroVideoIdCommand command) {
        log.info("Http request, PUT, / /api/campaigns/{id}/introvideourl with variable: " + id + WITH_BODY + command.toString());
        campaignService.updateCampaignIntroVideoId(id, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/projectdescription")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> updateCampaignProjectDescription(@PathVariable("id") Long id, @Valid @RequestBody UpdateCampaignProjectDescriptionCommand command) {
        log.info("Http request, PUT, / /api/campaigns/{id}/projectdescription with variable: " + id + WITH_BODY + command.toString());
        campaignService.updateCampaignProjectDescription(id, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/target")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> updateCampaignTarget(@PathVariable("id") Long id, @Valid @RequestBody UpdateCampaignTargetCommand command) {
        log.info("Http request, PUT, / /api/campaigns/{id}/target with variable: " + id + WITH_BODY + command.toString());
        campaignService.updateCampaignTarget(id, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/title")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> updateCampaignTitle(@PathVariable("id") Long id, @Valid @RequestBody UpdateCampaignTitleCommand command) {
        log.info("Http request, PUT, / /api/campaigns/{id}/title with variable: " + id + WITH_BODY + command.toString());
        campaignService.updateCampaignTitle(id, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> updateCampaignStatus(@PathVariable("id") Long id, @Valid @RequestBody UpdateCampaignStatusCommand command) {
        log.info("Http request, PUT, / /api/campaigns/{id}/title with variable: " + id + WITH_BODY + command.toString());
        campaignService.updateCampaignStatus(id, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> deleteCampaign(@PathVariable("id") Long id) {
        log.info("Http request, DELETE, / /api/campaigns/{id} with variable: " + id);
        campaignService.deleteCampaign(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
