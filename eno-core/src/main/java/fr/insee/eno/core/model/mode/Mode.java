package fr.insee.eno.core.model.mode;

import fr.insee.pogues.model.SurveyModeEnum;

import java.util.Map;

public enum Mode {

    CAPI,
    CATI,
    CAWI,
    PAPI;

    private static final Map<String, Mode> ddiModes = Map.of(
            "SelfAdministeredQuestionnaire.WebBased", CAWI,
            "SelfAdministeredQuestionnaire.Paper", PAPI,
            "Interview.Telephone.CATI", CATI,
            "Interview.FaceToFace.CAPIorCAMI", CAPI);

    public static boolean isDDIMode(String ddiMode) {
        return ddiModes.containsKey(ddiMode);
    }

    public static Mode convertDDIMode(String ddiMode) {
        return ddiModes.get(ddiMode);
    }

    private static final Map<SurveyModeEnum, Mode> poguesModes = Map.of(
            SurveyModeEnum.CATI, CATI,
            SurveyModeEnum.CAPI, CAPI,
            SurveyModeEnum.PAPI, PAPI,
            SurveyModeEnum.CAWI, CAWI);

    public  static Mode convertSurveyModeEnumMode(SurveyModeEnum poguesMode){
        return poguesModes.get(poguesMode);
    }
}
