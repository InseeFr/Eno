package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Class that represent the "pairwise question".
 * For now, this corresponds to a DDI QuestionItem object (could be QuestionGrid later on). */
@Getter
@Setter
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = PairwiseLinks.class)
public class PairwiseQuestion extends SingleResponseQuestion {

    /* TODO: pairwise question is weird, should not inherit the Question class
        and should be in a separate list in Questionnaire class. */

    //!\ We need to redefine response here as parent class is mapping a wrong response attribute for the pairwise case
    // not a really good solution, could be refactored
    Response response = null;

    /** Name of the variable to be used for the question iterations. */
    @DDI("getInParameterArray(0).getParameterNameArray(0).getStringArray(0).getStringValue()")
    @Lunatic("T(fr.insee.eno.core.model.question.PairwiseQuestion).computeLunaticAxes(#this, #param)")
    String loopVariableName;

    /**
     * The pairwise question object encapsulates a unique choice question.
     * (During data collection, the collection is iterated several times to establish each link between individuals.)
     * In DDI, the information of this unique choice question is in the same object as the pairwise question.
     * @see fr.insee.eno.core.processing.in.steps.ddi.DDIManagePairwiseId
     */
    @DDI("T(java.util.List).of(#this)")
    @Lunatic("getComponents()")
    List<UniqueChoiceQuestion> uniqueChoiceQuestions = new ArrayList<>();

    public static void computeLunaticAxes(PairwiseLinks lunaticPairwiseLinks, String loopVariableName) {
        LabelType xAxis = new LabelType();
        LabelType yAxis = new LabelType();
        String vtlExpression = "count("+loopVariableName+")";
        xAxis.setValue(vtlExpression);
        xAxis.setType(LabelTypeEnum.VTL);
        yAxis.setValue(vtlExpression);
        yAxis.setType(LabelTypeEnum.VTL);
        lunaticPairwiseLinks.setXAxisIterations(xAxis);
        lunaticPairwiseLinks.setYAxisIterations(yAxis);
    }

}
