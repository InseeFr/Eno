package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.lunatic.model.flat.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class MultipleChoiceQuestion extends MultipleResponseQuestion {

    @Getter
    @Setter
    public static class Simple extends MultipleResponseQuestion {
        @DDI(contextType = QuestionGridType.class, field = "getOutParameterList()")
        @Lunatic(contextType = CheckboxGroup.class, field = "getResponses()")
        List<CodeResponse> codeList = new ArrayList<>();

        @Lunatic(contextType = CheckboxGroup.class,
                field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
        String lunaticComponentType = "CHECKBOX_GROUP";

    }

    @Getter
    @Setter
    public static class Complex extends MultipleResponseQuestion {

        @Lunatic(contextType = Table.class,
                field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
        String lunaticComponentType = "TABLE";

    }
}
