package fr.insee.eno.ws.legacy.parameters;

/**
 * Parameter for legacy transformations that are not yet implemented in Eno Java core library and redirected to the
 * Eno legacy xml service.
 */
public enum OutFormat {

    /** DDI metadata representation. */
    DDI,

    /** Xforms web questionnaire. */
    XFORMS,

    /** Paper format. */
    FO,

    /** Description of the questionnaire in Open Document format. */
    FODT

}
