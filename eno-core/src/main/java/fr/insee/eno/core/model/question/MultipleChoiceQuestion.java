package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Format;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.CodeResponse;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

@Getter
@Setter
public abstract class MultipleChoiceQuestion extends MultipleResponseQuestion {

    public static class Simple extends MultipleResponseQuestion {
        @DDI(contextType = QuestionGridType.class, field = "getOutParameterList()")
        @Lunatic(contextType = CheckboxGroup.class, field = "getResponses()")
        List<CodeResponse> codeList = new ArrayList<>();
    }

    public static class Complex extends MultipleResponseQuestion {}
}
