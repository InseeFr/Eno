package fr.insee.eno.core.annotations;

/**
 * Inheritance of annotations is not possible in Java.
 * The record is designed to hold the information of an input annotation, and to be used in a polymorphic way.
 */
public record InAnnotationValues(
        String expression,
        boolean allowNullList,
        boolean debug) {}
