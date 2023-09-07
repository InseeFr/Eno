package fr.insee.eno.core.converter;

import fr.insee.eno.core.exceptions.technical.ConversionException;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.label.QuestionnaireLabel;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.*;
import fr.insee.eno.core.model.question.table.TableCell;
import fr.insee.eno.core.model.response.CodeResponse;
import fr.insee.eno.core.model.response.Response;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
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
            return new fr.insee.lunatic.model.flat.Sequence();
        if (enoObject instanceof Subsequence)
            return new fr.insee.lunatic.model.flat.Subsequence();
        if (isInstanceOfLunaticDeclaration(enoObject))
            return new DeclarationType();
        if (enoObject instanceof Control)
            return new ControlType();
        if (enoObject instanceof ComponentFilter)
            return new ConditionFilterType();
        if (enoObject instanceof Loop)
            return new fr.insee.lunatic.model.flat.Loop();
        if (enoObject instanceof StandaloneLoop.LoopIterations)
            return new LinesLoop();
        if (enoObject instanceof SingleResponseQuestion singleResponseQuestion)
            return instantiateFrom(singleResponseQuestion);
        if (enoObject instanceof MultipleResponseQuestion multipleResponseQuestion)
            return instantiateFrom(multipleResponseQuestion);
        if (enoObject instanceof TableCell)
            return new BodyCell();
        if (enoObject instanceof Response)
            return new ResponseType();
        if (enoObject instanceof CodeItem)
            return new Options();
        if (enoObject instanceof CodeResponse)
            return new ResponsesCheckboxGroup();
        if (isInstanceOfLunaticLAbel(enoObject))
            return new LabelType();
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
        if (textQuestion.getLengthType() == null)
            throw new ConversionException("Length type has not been set in Eno question " + textQuestion);
        return switch (textQuestion.getLengthType()) {
            case SHORT -> new Input();
            case LONG -> new Textarea();
        };
    }

    private static Object ucqComponentConversion(SingleResponseQuestion enoQuestion, UniqueChoiceQuestion uniqueChoiceQuestion) {
        if (uniqueChoiceQuestion.getDisplayFormat() == null)
            throw new ConversionException("Display format has not been set in Eno question " + enoQuestion);
        return switch (((UniqueChoiceQuestion) enoQuestion).getDisplayFormat()) {
            case RADIO -> new Radio();
            case CHECKBOX -> new CheckboxOne();
            case DROPDOWN -> new Dropdown();
        };
    }

    private static Object instantiateFrom(MultipleResponseQuestion enoQuestion) {
        if (enoQuestion instanceof SimpleMultipleChoiceQuestion)
            return new CheckboxGroup();
        if (enoQuestion instanceof ComplexMultipleChoiceQuestion)
            return new Table();
        if (enoQuestion instanceof TableQuestion)
            return new Table();
        if (enoQuestion instanceof DynamicTableQuestion)
            return new RosterForLoop();
        //
        throw new ConversionException(unimplementedMessage(enoQuestion));
    }

    private static String unimplementedMessage(Object enoObject) {
        return "Lunatic conversion for Eno type " + enoObject.getClass() + " not implemented.";
    }
}
