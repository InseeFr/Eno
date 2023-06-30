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
        if (enoObject instanceof Sequence)
            return new SequenceType();
        if (enoObject instanceof Subsequence)
            return new fr.insee.lunatic.model.flat.Subsequence();
        if (isInstanceOfLunaticDeclaration(enoObject))
            return new DeclarationType();
        if (enoObject instanceof Control)
            return new ControlType();
        if (enoObject instanceof Filter)
            return new ConditionFilterType();
        if (enoObject instanceof SingleResponseQuestion singleResponseQuestion)
            return instantiateFrom(singleResponseQuestion);
        if (enoObject instanceof MultipleResponseQuestion multipleResponseQuestion)
            return instantiateFrom(multipleResponseQuestion);
        if (enoObject instanceof Response)
            return new ResponseType();
        if (enoObject instanceof CodeItem)
            return new Options();
        if (enoObject instanceof CodeResponse)
            return new ResponsesCheckboxGroup();
        if (isInstanceOfLunaticLAbel(enoObject))
            return new LabelType();
        if (enoObject instanceof TableCell)
            throw new ConversionException(
                    "Eno TableCell object '%s' called by basic converted method, this should not happen. " +
                            "TableCell conversion for Lunatic is implemented in a dedicated class. " +
                            "PLEASE REPORT THIS EXCEPTION TO ENO DEV TEAM.");
        //
        throw new ConversionException(unimplementedMessage(enoObject));
    }

    private static boolean isInstanceOfLunaticDeclaration(Object enoObject) {
        return enoObject instanceof Declaration
                || enoObject instanceof Instruction;
    }

    private static boolean isInstanceOfLunaticLAbel(Object enoObject) {
        return enoObject instanceof Label || enoObject instanceof QuestionnaireLabel
                || enoObject instanceof DynamicLabel
                || enoObject instanceof CalculatedExpression;
    }

    private static Object instantiateFrom(SingleResponseQuestion enoQuestion) {
        if (enoQuestion instanceof TextQuestion textQuestion) {
            return textComponentConversion(textQuestion);
        }
        if (enoQuestion instanceof NumericQuestion)
            return new InputNumber();
        if (enoQuestion instanceof BooleanQuestion)
            return new CheckboxBoolean();
        if (enoQuestion instanceof DateQuestion)
            return new Datepicker();
        if (enoQuestion instanceof DurationQuestion) {
            log.warn("Duration questions is not supported in Lunatic yet. " + enoQuestion);
            return null;
        }
        if (enoQuestion instanceof UniqueChoiceQuestion uniqueChoiceQuestion) {
            return ucqComponentConversion(enoQuestion, uniqueChoiceQuestion);
        }
        if (enoQuestion instanceof PairwiseQuestion)
            return new PairwiseLinks();
        //
        throw new ConversionException(unimplementedMessage(enoQuestion));
    }

    private static ComponentType textComponentConversion(TextQuestion textQuestion) {
        // Setting the component type enum value here breaks the single responsibility principle a bit
        // Yet, this property might be directly supported by Lunatic-Model later,
        // or the use of conversion annotations in Eno would allow to map this property directly,
        // using model annotation (see comment in TextQuestion class)
        if (textQuestion.getMaxLength().intValue() < Constant.LUNATIC_SMALL_TEXT_LIMIT) {
            Input input = new Input();
            input.setComponentType(ComponentTypeEnum.INPUT);
            return input;
        }
        else {
            Textarea textarea = new Textarea();
            textarea.setComponentType(ComponentTypeEnum.TEXTAREA);
            return textarea;
        }
    }

    private static Object ucqComponentConversion(SingleResponseQuestion enoQuestion, UniqueChoiceQuestion uniqueChoiceQuestion) {
        if (uniqueChoiceQuestion.getDisplayFormat() == null) {
            throw new ConversionException("Display format has not been set in Eno question " + enoQuestion);
        }
        return switch (((UniqueChoiceQuestion) enoQuestion).getDisplayFormat()) {
            case RADIO -> new Radio();
            case CHECKBOX -> new CheckboxOne();
            case DROPDOWN -> new Dropdown();
        };
    }

    private static Object instantiateFrom(MultipleResponseQuestion enoQuestion) {
        if (enoQuestion instanceof MultipleChoiceQuestion.Simple)
            return new CheckboxGroup();
        if (enoQuestion instanceof MultipleChoiceQuestion.Complex)
            return new Table();
        if (enoQuestion instanceof TableQuestion enoTable)
            return LunaticTableConverter.convertEnoTable(enoTable);
        if (enoQuestion instanceof DynamicTableQuestion)
            return new Table();
        //
        throw new ConversionException(unimplementedMessage(enoQuestion));
    }

    private static String unimplementedMessage(Object enoObject) {
        return "Lunatic conversion for Eno type " + enoObject.getClass() + " not implemented.";
    }
}
