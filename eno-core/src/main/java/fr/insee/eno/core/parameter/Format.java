package fr.insee.eno.core.parameter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;
import java.util.Set;

/**
 * Enum for in and out formats.
 */
public enum Format {

    POGUES,
    DDI,
    LUNATIC;

}
