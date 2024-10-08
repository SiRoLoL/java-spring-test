package sk.uteg.springdatatest.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.repository.*;

import sk.uteg.springdatatest.db.model.Campaign;

public interface CampaignRepository extends CrudRepository<Campaign, UUID>
{
    public Optional<Campaign> findById(UUID id);
}
