package fr.insee.eno.core.model.declaration;

import datacollection33.InstructionType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.mode.Mode;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.DeclarationType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.annotations.Contexts.Context;

/** Text displayed after a question or sequence. */
@Getter
@Setter
@Context(format = Format.DDI, type = InstructionType.class)
@Context(format = Format.LUNATIC, type = DeclarationType.class)
public class Instruction extends EnoIdentifiableObject implements DeclarationInterface {

    // TODO: why does Eno-V2 concatenate sequence id and declaration id for Lunatic ? -> no concrete reason, ok

    /** Type of instruction (either instruction / help / warning / ...)
     * In DDI, this information is on the same level as the information on the modes concerned by the instruction.
     * (In other words: concerned modes and instruction type are all "instruction name" elements.)
     * To get the instruction type, the list of instruction names is filtered from the concerned modes,
     * then there should be only remaining instruction name which the one we want.
     * TODO: conversion between DDI and Lunatic using toUpperCase works, but is weak, dedicated static methods in the class would be better.
     */
    @DDI("getInstructionNameList()" +
            ".?[!T(fr.insee.eno.core.model.mode.Mode).isDDIMode(#this.getStringArray(0).getStringValue())].get(0)" +
            ".getStringArray(0).getStringValue()")
    @Lunatic("setDeclarationType(T(fr.insee.lunatic.model.flat.DeclarationTypeEnum).valueOf(#param.toUpperCase()))")
    String declarationType;

    @DDI("getInstructionTextArray(0)")
    @Lunatic("setLabel(#param)")
    DynamicLabel label;

    @Lunatic("setPosition(T(fr.insee.lunatic.model.flat.DeclarationPositionEnum).valueOf(#param))")
    String position = "AFTER_QUESTION_TEXT";

    @DDI("getInstructionNameList()" +
            ".?[T(fr.insee.eno.core.model.mode.Mode).isDDIMode(#this.getStringArray(0).getStringValue())]" +
            ".![T(fr.insee.eno.core.model.mode.Mode).convertDDIMode(#this.getStringArray(0).getStringValue())]")
    private final List<Mode> modes = new ArrayList<>();

}
