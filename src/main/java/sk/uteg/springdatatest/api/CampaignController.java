package sk.uteg.springdatatest.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import sk.uteg.springdatatest.api.model.CampaignSummary;
import sk.uteg.springdatatest.service.CampaignService;

@RestController("campaign")
public class CampaignController {

    private final CampaignService campaignService;
    
    public CampaignController(CampaignService campaignService)
    {
        this.campaignService = campaignService;
    }

    @GetMapping(value = "/summary/{uuid}")
    public ResponseEntity<CampaignSummary> getSummary(@PathVariable UUID uuid) {
        CampaignSummary result = campaignService.getSummary(uuid);

        if (result == null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
