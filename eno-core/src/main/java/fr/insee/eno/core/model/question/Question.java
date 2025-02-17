package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionGridType;
import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.declaration.Declaration;
import fr.insee.eno.core.model.declaration.Instruction;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.navigation.Control;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentMultipleResponseType;
import fr.insee.lunatic.model.flat.ComponentSimpleResponseType;
import fr.insee.lunatic.model.flat.PairwiseLinks;
import fr.insee.pogues.model.QuestionType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Context(format = Format.POGUES, type = QuestionType.class)
@Context(format = Format.DDI, type = {QuestionItemType.class, QuestionGridType.class})
@Context(format = Format.LUNATIC,
        type = {ComponentSimpleResponseType.class, ComponentMultipleResponseType.class, PairwiseLinks.class})
public abstract class Question extends EnoIdentifiableObject implements EnoComponent {

    /**
     * Business name of the question.
     * Attribute is defined here to factor toString methods,
     * but DDI mapping is done in subclasses since DDI classes are different. */
    private String name;

    @Pogues("getLabel().getFirst()")
    @DDI("getQuestionTextArray(0)")
    @Lunatic("setLabel(#param)")
    private DynamicLabel label;

    /**
     * In DDI, declarations are mapped in EnoQuestionnaire and inserted here with a processing.
     * @see fr.insee.eno.core.processing.in.steps.ddi.DDIInsertDeclarations
     */
    @Pogues("getDeclaration().?[#this.getPosition().value() == 'BEFORE_QUESTION_TEXT']")
    @Lunatic("getDeclarations()")
    private final List<Declaration> declarations = new ArrayList<>();

    @Pogues("getDeclaration().?[#this.getPosition().value() == 'AFTER_QUESTION_TEXT']")
    @DDI("getInterviewerInstructionReferenceList().![#index.get(#this.getIDArray(0).getStringValue())]")
    @Lunatic("getDeclarations()")
    private final List<Instruction> instructions = new ArrayList<>();

    /** Controls applied to the question.
     * In DDI, the controls are mapped in the questionnaire object, and are put here through a processing.
     * In Lunatic, the question components have a list of controls. */
    @Lunatic("getControls()")
    private final List<Control> controls = new ArrayList<>();

    @Lunatic("setConditionFilter(#param)")
    private ComponentFilter componentFilter = new ComponentFilter();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[id="+this.getId()+", name="+getName()+"]";
    }

}
