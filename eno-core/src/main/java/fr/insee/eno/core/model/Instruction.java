package fr.insee.eno.core.model;

import datacollection33.InstructionType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.lunatic.model.flat.DeclarationType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Text displayed after a question or sequence. */
@Getter
@Setter
public class Instruction extends EnoIdentifiableObject implements DeclarationInterface {

    // TODO: why does Eno-V2 concatenate sequence id and declaration id for Lunatic ? -> no concrete reason, ok

    /** Type of instruction (either instruction / help / warning / ...)
     * In DDI, this information is on the same level as the information on the modes concerned by the instruction.
     * (In other words: concerned modes and instruction type are all "instruction name" elements.)
     * To get the instruction type, the list of instruction names is filtered from the concerned modes,
     * then there should be only remaining instruction name which the one we want.
     * TODO: conversion between DDI and Lunatic using toUpperCase works, but is weak, dedicated static methods in the class would be better.
     */
    @DDI(contextType = InstructionType.class,
            field = "getInstructionNameList()" +
                    ".?[!T(fr.insee.eno.core.model.Mode).isDDIMode(#this.getStringArray(0).getStringValue())].get(0)" +
                    ".getStringArray(0).getStringValue()")
    @Lunatic(contextType = DeclarationType.class,
            field = "setDeclarationType(T(fr.insee.lunatic.model.flat.DeclarationTypeEnum).valueOf(#param.toUpperCase()))")
    String declarationType;

    @DDI(contextType = InstructionType.class,
            field = "getInstructionTextArray(0)")
    @Lunatic(contextType = DeclarationType.class, field = "setLabel(#param)")
    DynamicLabel label;

    /** List of variable names that are used in the declarations' label.
     * This list is filled in an Eno processing, and used in Lunatic processing to fill 'bindingDependencies'. */
    List<String> variableNames = new ArrayList<>();

    @Lunatic(contextType = DeclarationType.class,
            field = "setPosition(T(fr.insee.lunatic.model.flat.DeclarationPositionEnum).valueOf(#param))")
    String position = "AFTER_QUESTION_TEXT";

    @DDI(contextType = InstructionType.class,
            field = "getInstructionNameList()" +
                    ".?[T(fr.insee.eno.core.model.Mode).isDDIMode(#this.getStringArray(0).getStringValue())]" +
                    ".![T(fr.insee.eno.core.model.Mode).convertDDIMode(#this.getStringArray(0).getStringValue())]")
    private final List<Mode> modes = new ArrayList<>();

}
