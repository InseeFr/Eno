package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.CodeResponse;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MultipleChoiceQuestion extends MultipleResponseQuestion {

    @DDI(contextType = QuestionGridType.class, field = "getOutParameterList()")
    @Lunatic(contextType = CheckboxGroup.class, field = "getResponses()")
    List<CodeResponse> codeList = new ArrayList<>();

}
