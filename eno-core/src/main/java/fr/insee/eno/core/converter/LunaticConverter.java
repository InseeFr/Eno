package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.Sequence;
import fr.insee.eno.core.model.Subsequence;
import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LunaticConverter {

    public static final int SMALL_TEXT_LIMIT = 250; //TODO: Constants class??

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
        else if (enoObject instanceof SingleResponseQuestion)
            return instantiateFrom((SingleResponseQuestion) enoObject);
        else if (enoObject instanceof MultipleResponseQuestion)
            return instantiateFrom((MultipleResponseQuestion) enoObject);
        else if (enoObject instanceof Response)
            return new ResponseType();
        else if (enoObject instanceof CodeItem)
            return new Options();
        else if (enoObject instanceof CodeResponse)
            return new ResponsesCheckboxGroup();
        else if (enoObject instanceof Label
                || enoObject instanceof DynamicLabel
                || enoObject instanceof CalculatedExpression)
            return new LabelType();
        else if (enoObject instanceof TableLine)
            throw new RuntimeException("TableLine conversion not implemented."); //FIXME: (todo: annotation for conversion)
        else if (enoObject instanceof TableCell)
            throw new RuntimeException("TableCell conversion not implemented."); //FIXME: (todo: annotation for conversion)
        else
            throw new RuntimeException(unimplementedMessage(enoObject));
    }

    private static Object instantiateFrom(SingleResponseQuestion enoQuestion) {
        if (enoQuestion instanceof TextQuestion)
            if (((TextQuestion) enoQuestion).getMaxLength().intValue() < SMALL_TEXT_LIMIT)
                return new Input();
            else
                return new Textarea();
        else if (enoQuestion instanceof NumericQuestion)
            return new InputNumber();
        else if (enoQuestion instanceof BooleanQuestion)
            return new CheckboxBoolean();
        else if (enoQuestion instanceof DateQuestion)
            return new Datepicker();
        else if (enoQuestion instanceof UniqueChoiceQuestion) {
            if (((UniqueChoiceQuestion) enoQuestion).getDisplayFormat() == null) {
                throw new RuntimeException("Display format has not been set in Eno question " + enoQuestion); //TODO: toString() for UniqueChoiceQuestion (and others...)
            }
            return switch (((UniqueChoiceQuestion) enoQuestion).getDisplayFormat()) {
                case RADIO -> new Radio();
                case CHECKBOX -> new CheckboxOne();
                case DROPDOWN -> new Dropdown();
            };
        }
        else
            throw new RuntimeException(unimplementedMessage(enoQuestion));
    }

    private static Object instantiateFrom(MultipleResponseQuestion enoQuestion) {
        if (enoQuestion instanceof MultipleChoiceQuestion)
            return new CheckboxGroup();
        else if (enoQuestion instanceof TableQuestion)
            return new Table();
        else
            throw new RuntimeException(unimplementedMessage(enoQuestion));
    }

    private static String unimplementedMessage(Object enoObject) {
        return "Lunatic conversion for Eno type " + enoObject.getClass() + " not implemented.";
    }
}
