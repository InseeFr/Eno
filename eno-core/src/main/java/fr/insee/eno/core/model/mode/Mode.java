package fr.insee.eno.core.model.mode;

import fr.insee.pogues.model.SurveyModeEnum;

import java.util.Map;

public enum Mode {

    CAPI,
    CATI,
    CAWI,
    PAPI;

    private static final Map<String, Mode> ddiModes = Map.of(
            "Interview.FaceToFace.CAPIorCAMI", CAPI,
            "Interview.Telephone.CATI", CATI,
            "SelfAdministeredQuestionnaire.WebBased", CAWI,
            "SelfAdministeredQuestionnaire.Paper", PAPI);

    public static boolean isDDIMode(String ddiMode) {
        return ddiModes.containsKey(ddiMode);
    }

    public static Mode convertDDIMode(String ddiMode) {
        return ddiModes.get(ddiMode);
    }

    /** A logical check raises an error in a flow step of the hasNoSelectedMode method
     * (class EnoModeSelection): indeed, the code handles objects of type SurveyModeEnum
     *  as if they were instances of the Mode class. Therefore, it is necessary to perform
     *  a conversion of these modes beforehand, even though they are quite similar. */
    private static final Map<SurveyModeEnum, Mode> poguesModes = Map.of(
            SurveyModeEnum.CAPI, CAPI,
            SurveyModeEnum.CATI, CATI,
            SurveyModeEnum.CAWI, CAWI,
            SurveyModeEnum.PAPI, PAPI);

    public static Mode convertSurveyModeEnumMode(SurveyModeEnum poguesMode){
        return poguesModes.get(poguesMode);
    }
}
