package fr.insee.eno.core.model.question;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PairwiseQuestion extends SingleResponseQuestion {

    Response response = null;
    boolean mandatory = true; //TODO: see if it should be true, false or null

    /** Variable to loop over */
    String loopVariableName;

    List<UniqueChoiceQuestion> uniqueChoiceQuestions = new ArrayList<>();

}
