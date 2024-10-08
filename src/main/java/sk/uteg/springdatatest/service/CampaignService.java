package sk.uteg.springdatatest.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sk.uteg.springdatatest.api.model.CampaignSummary;
import sk.uteg.springdatatest.api.model.QuestionSummary;
import sk.uteg.springdatatest.db.model.Answer;
import sk.uteg.springdatatest.db.model.Campaign;
import sk.uteg.springdatatest.db.model.Feedback;
import sk.uteg.springdatatest.db.model.Question;
import sk.uteg.springdatatest.mapper.QuestionMapper;
import sk.uteg.springdatatest.repository.CampaignRepository;
import sk.uteg.springdatatest.repository.FeedbackRepository;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final FeedbackRepository feedbackRepository;

    private final QuestionMapper questionMapper;

    public CampaignService(
        CampaignRepository campaignRepository,
        FeedbackRepository feedbackRepository)
    {
        this.campaignRepository = campaignRepository;
        this.feedbackRepository = feedbackRepository;
        this.questionMapper = new QuestionMapper();
    }
    
    // Get campaign summary for campaign UID
    public CampaignSummary getSummary(UUID id)
    {
        // Get campaign
        Optional<Campaign> campaign = campaignRepository.findById(id);
        if (campaign.isEmpty())
        {
            return null; // Return null if campaign not found
        }

        // Get feedbacks for campaign
        List<Feedback> feedbacks = feedbackRepository.findByCampaign(campaign.get());

        // Get question summaries for campaign summary
        List<Answer> answers = getAnswers(feedbacks);
        List<QuestionSummary> questionSummaries = getQuestionSummaries(campaign.get(), answers);

        CampaignSummary result = new CampaignSummary();
        result.setQuestionSummaries(questionSummaries);
        result.setTotalFeedbacks(feedbacks.size());

        return result;
    }

    // Get list of answers from a list of feedbacks
    private List<Answer> getAnswers(List<Feedback> feedbacks)
    {
        List<Answer> result = feedbacks
            .stream()
            .flatMap(feedback -> feedback.getAnswers().stream())
            .collect(Collectors.toList());

        return result;
    }

    // Get list of question summaries from campaign and a list of answers
    private List<QuestionSummary> getQuestionSummaries(Campaign campaign, List<Answer> answers)
    {
        List<Question> questions = campaign.getQuestions(); // Get questions from campaign

        // Map questions to question summaries
        List<QuestionSummary> questionSummaries = questions
            .stream()
            .map(question -> {
                List<Answer> validAnswers = answers
                    .stream()
                    .filter(answer -> answer.getQuestion() == question)
                    .collect(Collectors.toList());
                return questionMapper.map(question, validAnswers);
            })
            .collect(Collectors.toList());
        return questionSummaries;
    }
}
