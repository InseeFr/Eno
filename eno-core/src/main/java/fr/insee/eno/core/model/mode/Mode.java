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

    /** The information from Pogues objects is mapped onto objects of the Eno model to decouple input/output. */
    private static final Map<SurveyModeEnum, Mode> poguesModes = Map.of(
            SurveyModeEnum.CAPI, CAPI,
            SurveyModeEnum.CATI, CATI,
            SurveyModeEnum.CAWI, CAWI,
            SurveyModeEnum.PAPI, PAPI);

    public static Mode convertSurveyModeEnumMode(SurveyModeEnum poguesMode){
        return poguesModes.get(poguesMode);
    }
}
