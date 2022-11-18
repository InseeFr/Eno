package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
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

@Getter
@Setter
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = PairwiseLinks.class) //TODO: temp class before it actually comes in Lunatic-Model
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
        String labelType = "VTL"; //TODO: enum here
        xAxis.setValue(vtlExpression);
        xAxis.setType(labelType);
        yAxis.setValue(vtlExpression);
        yAxis.setType(labelType);
        lunaticPairwiseLinks.setXAxisIterations(xAxis);
        lunaticPairwiseLinks.setYAxisIterations(yAxis);
    }

}
