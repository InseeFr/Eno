package fr.insee.eno.core.model.question.table;

import datacollection33.GridResponseDomainInMixedType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.question.TableCell;
import fr.insee.eno.core.model.question.UniqueChoiceQuestion;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** A UniqueChoiceCell object is the content of a table.
 * A cell is neither part of the header nor of the left column. */
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Getter @Setter
public class UniqueChoiceCell extends TableCell {

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain().getGenericOutputFormat().getStringValue().equals('radio-button') ? " +
                    "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).RADIO : " +
                    "getResponseDomain().getGenericOutputFormat().getStringValue().equals('checkbox') ? " +
                    "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).CHECKBOX : " +
                    "getResponseDomain().getGenericOutputFormat().getStringValue().equals('drop-down-list') ? " +
                    "T(fr.insee.eno.core.model.question.UniqueChoiceQuestion.DisplayFormat).DROPDOWN : null")
    UniqueChoiceQuestion.DisplayFormat displayFormat;

    @DDI(contextType = QuestionItemType.class,
            field = "#index.get(#this.getResponseDomain().getCodeListReference().getIDArray(0).getStringValue()).getCodeList()")
    @Lunatic(contextType = BodyCell.class, field = "getOptions()")
    List<CodeItem> codeList = new ArrayList<>();
}

