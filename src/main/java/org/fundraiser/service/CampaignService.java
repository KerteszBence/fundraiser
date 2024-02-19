package org.fundraiser.service;

import lombok.extern.slf4j.Slf4j;
import org.fundraiser.config.AccountType;
import org.fundraiser.config.CampaignCategory;
import org.fundraiser.domain.Account;
import org.fundraiser.domain.Campaign;
import org.fundraiser.dto.create.CreateCampaignCommand;
import org.fundraiser.dto.info.CampaignInfo;
import org.fundraiser.dto.info.CampaignResponse;
import org.fundraiser.dto.info.DonationListItem;
import org.fundraiser.dto.info.MyCampaignInfo;
import org.fundraiser.dto.update.*;
import org.fundraiser.event.*;
import org.fundraiser.exceptionhandling.account.AccountIsNotEnabledException;
import org.fundraiser.exceptionhandling.account.CreatorAccountNotFindByUserNameException;
import org.fundraiser.exceptionhandling.campaign.CampaignNotFindByIdException;
import org.fundraiser.exceptionhandling.campaign.CampaignNotFoundByAccountException;
import org.fundraiser.exceptionhandling.campaign.CampaignNotFoundByCategoryException;
import org.fundraiser.exceptionhandling.security.UnauthorizedAccountException;
import org.fundraiser.repository.CampaignRepository;
import org.fundraiser.utils.CampaignUtils;
import org.fundraiser.utils.mapper.CampaignInfoMapper;
import org.fundraiser.utils.mapper.DonationListItemMapper;
import org.fundraiser.utils.mapper.MyCampaignInfoMapper;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CampaignService(CampaignRepository campaignRepository, ModelMapper modelMapper, AccountService accountService, ApplicationEventPublisher eventPublisher) {
        this.campaignRepository = campaignRepository;
        this.modelMapper = modelMapper;
        this.accountService = accountService;
        this.eventPublisher = eventPublisher;
    }

    public Campaign findCampaignById(Long id) {
        Optional<Campaign> campaignOptional = campaignRepository.findById(id);
        if (campaignOptional.isEmpty()) {
            throw new CampaignNotFindByIdException(id);
        }
        return campaignOptional.get();
    }

    public void createCampaign(CreateCampaignCommand command) {
        Account account = accountService.findAccountByUserName(command.getUsername());
        if (!account.getIsEnabled()) {
            throw new AccountIsNotEnabledException(account.getFullName());
        } else {
            Campaign campaignToSave = modelMapper.map(command, Campaign.class);
            campaignToSave.setCollected(CampaignUtils.CAMPAIGN_BASIC_COLLECTED);
            campaignToSave.setCreated(LocalDateTime.now());
            campaignToSave.setLikeCounter(CampaignUtils.ACCOUNT_BASIC_LIKE_COUNTER);
            campaignToSave.setAccount(account);
            campaignToSave.setCategory(command.getCampaignCategory());
            campaignToSave.setLastModification(LocalDateTime.now()); //setcategory
            campaignToSave.setOpen(true);
            campaignToSave.setCurrency(command.getCurrency());
            campaignRepository.save(campaignToSave);
        }
    }

    public MyCampaignInfo getMyCampaignInfo(String username, Long campaignId) {
        Campaign campaign = findCampaignById(campaignId);
        authorizeUser(username, campaign);
        return MyCampaignInfoMapper.myCampaignInfoMapper(campaign);
    }

    private void authorizeUser(String username, Campaign campaign) {
        if (!username.equals(campaign.getAccount().getUsername())) {
            throw new UnauthorizedAccountException(username);
        }
    }

    public CampaignInfo getCampaignInfo(Long id) {
        return CampaignInfoMapper.campaignInfoMapper(findCampaignById(id));
    }

    public CampaignResponse getAllCampaignsByPageable(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Campaign> campaigns = campaignRepository.findAllIsOpen(pageable);
        List<Campaign> campaignList = campaigns.getContent();

        return getCampaignResponse(campaigns, campaignList);
    }

    public List<CampaignInfo> getAllCampaigns() {
        return campaignRepository.findAll()
                .stream()
                .filter(Campaign::isOpen)
                .map(campaign -> modelMapper.map(campaign, CampaignInfo.class))
                .collect(Collectors.toList());
    }

    @NotNull
    private CampaignResponse getCampaignResponse(Page<Campaign> campaigns, List<Campaign> campaignList) {
        List<CampaignInfo> content = campaignList.stream().map(CampaignInfoMapper::campaignInfoMapper).collect(Collectors.toList());

        CampaignResponse campaignResponse = new CampaignResponse();
        campaignResponse.setContent(content);
        campaignResponse.setPageNo(campaigns.getNumber());
        campaignResponse.setPageSize(campaigns.getSize());
        campaignResponse.setTotalElements(campaigns.getTotalElements());
        campaignResponse.setTotalPages(campaigns.getTotalPages());
        campaignResponse.setLast(campaigns.isLast());

        return campaignResponse;
    }

    public CampaignResponse getAllCampaignsByCategory(CampaignCategory category, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Campaign> campaigns = campaignRepository.findAllByCategory(category, pageable);
        List<Campaign> campaignList = campaigns.getContent();
        if (campaignList.isEmpty()) {
            throw new CampaignNotFoundByCategoryException(category.toString());
        }
        return getCampaignResponse(campaigns, campaignList);
    }

    public CampaignResponse getAllCampaignsByCreator(String username, int pageNo, int pageSize) {
        Account account = accountService.findAccountByUserName(username);
        if (account.getAccountType().equals(AccountType.ROLE_CREATOR)) {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<Campaign> campaigns = campaignRepository.findCampaignByAccount(account, pageable);
            List<Campaign> campaignList = campaigns.getContent();
            if (campaignList.isEmpty()) {
                throw new CampaignNotFoundByAccountException(username);
            }
            return getCampaignResponse(campaigns, campaignList);
        } else {
            throw new CreatorAccountNotFindByUserNameException(username);
        }
    }

    public void updateCampaignAvatarId(Long id, UpdateCampaignAvatarIdCommand command) {
        Campaign campaign = findCampaignById(id);
        modelMapper.map(command, campaign);
        campaign.setLastModification(LocalDateTime.now());
    }

    public void updateCampaignCoverId(Long id, UpdateCampaignCoverIdCommand command) {
        Campaign campaign = findCampaignById(id);
        modelMapper.map(command, campaign);
        campaign.setLastModification(LocalDateTime.now());
    }

    public void updateCampaignIntroduction(Long id, UpdateCampaignIntroductionCommand command) {
        Campaign campaign = findCampaignById(id);
        modelMapper.map(command, campaign);
        campaign.setLastModification(LocalDateTime.now());
    }

    public void updateCampaignIntroVideoId(Long id, UpdateCampaignIntroVideoIdCommand command) {
        Campaign campaign = findCampaignById(id);
        modelMapper.map(command, campaign);
        campaign.setLastModification(LocalDateTime.now());
    }

    public void updateCampaignProjectDescription(Long id, UpdateCampaignProjectDescriptionCommand command) {
        Campaign campaign = findCampaignById(id);
        modelMapper.map(command, campaign);
        campaign.setLastModification(LocalDateTime.now());
    }

    public void updateCampaignTarget(Long id, UpdateCampaignTargetCommand command) {
        Campaign campaign = findCampaignById(id);
        modelMapper.map(command, campaign);
        campaign.setLastModification(LocalDateTime.now());
    }

    public void updateCampaignTitle(Long id, UpdateCampaignTitleCommand command) {
        Campaign campaign = findCampaignById(id);
        modelMapper.map(command, campaign);
        campaign.setLastModification(LocalDateTime.now());
    }

    public void updateCampaignStatus(Long id, UpdateCampaignStatusCommand command) {
        Campaign campaign = findCampaignById(id);
        campaign.setOpen(command.getIsOpen());
        campaign.setLastModification(LocalDateTime.now());
    }

    public List<DonationListItem> getAllDonationByCampaign(Long campaignId) {
        Campaign campaign = findCampaignById(campaignId);
        return campaign.getDonations().stream()
                .map(DonationListItemMapper::donationListItemMapper)
                .collect(Collectors.toList());
    }

    public void deleteCampaign(Long id) {
        Campaign campaign = findCampaignById(id);
        campaignRepository.delete(campaign);
    }


    public List<Campaign> closeExpiredCampaigns() {
        LocalDateTime now = LocalDateTime.now();
        List<Campaign> campaignsToCheck = campaignRepository.findActiveExpiredCampaigns(now); //visszaküldi az összes kampányt, ami open és már lejárt
        for (Campaign campaign : campaignsToCheck) {
            campaign.setOpen(false);
            campaignRepository.save(campaign);
        }
        return campaignsToCheck;
    }

    @EventListener
    public void onAccountDeletionEvent(AccountDeletionEvent event) {
        log.info("onAccountDeletionEvent with account: " + event.getAccount().getUsername());
        log.info("Sending FileDeletionEvent with account: " + event.getAccount().getUsername());
        eventPublisher.publishEvent(new FileDeletionEvent(this, event.getAccount()));
        log.info("Complete FileDeletionEvent with account: " + event.getAccount().getUsername());
        if (event.getAccount().getAccountType().equals(AccountType.ROLE_CREATOR)) {
            List<Campaign> campaignList = event.getAccount().getCampaigns();
            for (Campaign campaign : campaignList) {
                if (campaign.isOpen()) {
                    log.info("sending DonationRefundEvent with campaign: " + campaign.getTitle());
                    eventPublisher.publishEvent(new DonationRefundCampaignEvent(this, campaign.getDonations(), event.getAccount()));
                    log.info("DonationRefundEvent complete with campaign: " + campaign.getTitle());
                }
                campaignRepository.delete(campaign);
            }
            eventPublisher.publishEvent(new DonationRefundCreatorEvent(this, event.getAccount()));
        } else {
            log.info("sending DonationRefundEvent with account: " + event.getAccount().getUsername());
            eventPublisher.publishEvent(new DonationRefundDonorEvent(this, event.getAccount()));
            log.info("DonationRefundEvent complete with account: " + event.getAccount().getUsername());
        }
        log.info("onAccountDeletionEvent complete with account: " + event.getAccount().getUsername());
    }
}
