package sk.uteg.springdatatest.mapper;

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import sk.uteg.springdatatest.api.model.OptionSummary;
import sk.uteg.springdatatest.api.model.QuestionSummary;
import sk.uteg.springdatatest.db.model.Answer;
import sk.uteg.springdatatest.db.model.Option;
import sk.uteg.springdatatest.db.model.Question;
import sk.uteg.springdatatest.db.model.QuestionType;

// Maps a Question object (and answers) to a QuestionSummary object
public class QuestionMapper {
    public QuestionSummary map(Question question, List<Answer> answers)
    {
        QuestionSummary result = new QuestionSummary();
        result.setType(question.getType());
        result.setName(question.getText());

        if (question.getType() == QuestionType.CHOICE)
        {
            result.setRatingAverage(BigDecimal.ZERO);
            result.setOptionSummaries(getOptionSummaries(question, answers));
        }
        else
        {
            result.setRatingAverage(getRatingAverage(question, answers));
            result.setOptionSummaries(List.of());
        }
        return result;
    }

    private List<OptionSummary> getOptionSummaries(Question question, List<Answer> answers)
    {
        // Get the amount of occurences for each option
        Dictionary<Option, Integer> optionAmounts = new Hashtable<>();
        question.getOptions().forEach(option -> optionAmounts.put(option, 0));
        answers.forEach(
            answer -> answer.getSelectedOptions().forEach(
                option -> optionAmounts.put(option, optionAmounts.get(option) + 1)
            )
        );

        // Map options to option summaries
        List<OptionSummary> result = question.getOptions()
            .stream()
            .map(option -> {
                OptionSummary optionSummary = new OptionSummary();
                optionSummary.setText(option.getText());
                optionSummary.setOccurrences(optionAmounts.get(option));
                return optionSummary;
            })
            .collect(Collectors.toList());

        return result;
    }

    private BigDecimal getRatingAverage(Question question, List<Answer> answers)
    {
        OptionalDouble result = answers
            .stream()
            .mapToInt(answer -> answer.getRatingValue())
            .average();

        if (result.isEmpty())
        {
            return BigDecimal.ZERO;
        }
        else
        {
            return BigDecimal.valueOf(result.getAsDouble());
        }
    }
}
