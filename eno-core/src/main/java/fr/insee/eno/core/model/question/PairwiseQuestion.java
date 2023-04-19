package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.Constant;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.response.Response;
import fr.insee.lunatic.model.flat.LabelType;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Class that represent the "pairwise question"
 * For now, this corresponds to a DDI QuestionItem object (could be QuestionGrid later on). */
@Getter
@Setter
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = PairwiseLinks.class)
public class PairwiseQuestion extends SingleResponseQuestion {

    //TODO: doc here

    Response response = null;
    boolean mandatory = true; //TODO: see if it should be true, false or null

    /** Variable to loop over */
    @DDI(contextType = QuestionItemType.class,
            field = "getInParameterArray(0).getParameterNameArray(0).getStringArray(0).getStringValue()")
    @Lunatic(contextType = PairwiseLinks.class,
            field = "T(fr.insee.eno.core.model.question.PairwiseQuestion).computeLunaticAxes(#this, #param)")
    String loopVariableName;

    @DDI(contextType = QuestionItemType.class, field = "T(java.util.List).of(#this)")
    @Lunatic(contextType = PairwiseLinks.class, field = "getComponents()")
    List<UniqueChoiceQuestion> uniqueChoiceQuestions = new ArrayList<>();

    public static void computeLunaticAxes(PairwiseLinks lunaticPairwiseLinks, String loopVariableName) {
        LabelType xAxis = new LabelType();
        LabelType yAxis = new LabelType();
        String vtlExpression = "count("+loopVariableName+")";
        // TODO: see if xAxis and yAxis in PairwiseLinks are of type 'VTL' or 'VTL|MD'
        xAxis.setValue(vtlExpression);
        xAxis.setType(Constant.LUNATIC_LABEL_VTL_MD);
        yAxis.setValue(vtlExpression);
        yAxis.setType(Constant.LUNATIC_LABEL_VTL_MD);
        lunaticPairwiseLinks.setXAxisIterations(xAxis);
        lunaticPairwiseLinks.setYAxisIterations(yAxis);
    }

    @Lunatic(contextType = PairwiseLinks.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "PAIRWISE_LINKS";

}
