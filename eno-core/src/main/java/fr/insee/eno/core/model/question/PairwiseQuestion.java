package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.Constant;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

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
     */
    @DDI("T(fr.insee.eno.core.model.question.PairwiseQuestion).createQuestionForUniqueChoice(#this)")
    @Lunatic("getComponents()")
    List<UniqueChoiceQuestion> uniqueChoiceQuestions = new ArrayList<>();

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "PAIRWISE_LINKS";

    public static void computeLunaticAxes(PairwiseLinks lunaticPairwiseLinks, String loopVariableName) {
        LabelType xAxis = new LabelType();
        LabelType yAxis = new LabelType();
        String vtlExpression = "count("+loopVariableName+")";
        xAxis.setValue(vtlExpression);
        xAxis.setType(Constant.LUNATIC_LABEL_VTL);
        yAxis.setValue(vtlExpression);
        yAxis.setType(Constant.LUNATIC_LABEL_VTL);
        lunaticPairwiseLinks.setXAxisIterations(xAxis);
        lunaticPairwiseLinks.setYAxisIterations(yAxis);
    }

    /**
     *
     * @param questionItemType question item type corresponding to the pairwise
     * @return question item type for the ucq
     */
    public static List<QuestionItemType> createQuestionForUniqueChoice(QuestionItemType questionItemType) {
        ModelMapper mapper = new ModelMapper();
        QuestionItemType questionItemUniqueChoice = mapper.map(questionItemType, QuestionItemType.class);
        questionItemUniqueChoice.getIDArray(0).setStringValue(questionItemType.getIDArray(0).getStringValue()+"-pairwise-dropdown");
        return List.of(questionItemUniqueChoice);
    }
}
