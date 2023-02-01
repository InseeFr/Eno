package fr.insee.eno.core.converter;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.label.QuestionnaireLabel;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LunaticConverter {

    private LunaticConverter() {}

    /**
     * Return a Lunatic instance type that corresponds to the given Eno object.
     * @param enoObject An object from the Eno model.
     * @return An instance from Lunatic flat model.
     * @throws IllegalArgumentException if the given object is not in package 'fr.insee.eno.core.model'.
     */
    public static Object instantiateFromEnoObject(Object enoObject) {
        //
        if (! enoObject.getClass().getPackageName().startsWith("fr.insee.eno.core.model"))
            throw new IllegalArgumentException("Not an Eno object.");
        //
        if (enoObject instanceof Variable)
            return new VariableType();
        else if (enoObject instanceof Sequence)
            return new SequenceType();
        else if (enoObject instanceof Subsequence)
            return new fr.insee.lunatic.model.flat.Subsequence();
        else if (enoObject instanceof Declaration
                || enoObject instanceof Instruction)
            return new DeclarationType();
        else if (enoObject instanceof Control)
            return new ControlType();
        else if (enoObject instanceof Filter)
            return new ConditionFilterType();
        else if (enoObject instanceof SingleResponseQuestion singleResponseQuestion)
            return instantiateFrom(singleResponseQuestion);
        else if (enoObject instanceof MultipleResponseQuestion multipleResponseQuestion)
            return instantiateFrom(multipleResponseQuestion);
        else if (enoObject instanceof Response)
            return new ResponseType();
        else if (enoObject instanceof CodeItem)
            return new Options();
        else if (enoObject instanceof CodeResponse)
            return new ResponsesCheckboxGroup();
        else if (enoObject instanceof Label || enoObject instanceof QuestionnaireLabel
                || enoObject instanceof DynamicLabel
                || enoObject instanceof CalculatedExpression)
            return new LabelType();
        else if (enoObject instanceof TableCell)
            throw new ConversionException(
                    "Eno TableCell object '%s' called by basic converted method, this should not happen. " +
                            "TableCell conversion for Lunatic is implemented in a dedicated class. " +
                            "PLEASE REPORT THIS EXCEPTION TO ENO DEV TEAM.");
        else
            throw new ConversionException(unimplementedMessage(enoObject));
    }

    private static Object instantiateFrom(SingleResponseQuestion enoQuestion) {
        if (enoQuestion instanceof TextQuestion textQuestion) {
            if (textQuestion.getMaxLength().intValue() < Constant.LUNATIC_SMALL_TEXT_LIMIT)
                return new Input();
            else
                return new Textarea();
        }
        else if (enoQuestion instanceof NumericQuestion)
            return new InputNumber();
        else if (enoQuestion instanceof BooleanQuestion)
            return new CheckboxBoolean();
        else if (enoQuestion instanceof DateQuestion)
            return new Datepicker();
        else if (enoQuestion instanceof UniqueChoiceQuestion uniqueChoiceQuestion) {
            if (uniqueChoiceQuestion.getDisplayFormat() == null) {
                throw new ConversionException("Display format has not been set in Eno question " + enoQuestion);
            }
            return switch (((UniqueChoiceQuestion) enoQuestion).getDisplayFormat()) {
                case RADIO -> new Radio();
                case CHECKBOX -> new CheckboxOne();
                case DROPDOWN -> new Dropdown();
            };
        }
        else if (enoQuestion instanceof PairwiseQuestion)
            return new PairwiseLinks();
        else
            throw new ConversionException(unimplementedMessage(enoQuestion));
    }

    private static Object instantiateFrom(MultipleResponseQuestion enoQuestion) {
        if (enoQuestion instanceof MultipleChoiceQuestion.Simple)
            return new CheckboxGroup();
        else if (enoQuestion instanceof MultipleChoiceQuestion.Complex)
            return new Table();
        else if (enoQuestion instanceof TableQuestion enoTable)
            return LunaticTableConverter.convertEnoTable(enoTable);
        else if (enoQuestion instanceof DynamicTableQuestion)
            return new Table();
        else
            throw new ConversionException(unimplementedMessage(enoQuestion));
    }

    private static String unimplementedMessage(Object enoObject) {
        return "Lunatic conversion for Eno type " + enoObject.getClass() + " not implemented.";
    }
}
