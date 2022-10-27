package fr.insee.eno.core.model.mode;

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
}
