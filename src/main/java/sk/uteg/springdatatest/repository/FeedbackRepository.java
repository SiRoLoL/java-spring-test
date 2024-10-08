package sk.uteg.springdatatest.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.repository.*;

import sk.uteg.springdatatest.db.model.Campaign;
import sk.uteg.springdatatest.db.model.Feedback;


public interface FeedbackRepository extends CrudRepository<Feedback, UUID>
{
    public List<Feedback> findByCampaign(Campaign campaign);
}