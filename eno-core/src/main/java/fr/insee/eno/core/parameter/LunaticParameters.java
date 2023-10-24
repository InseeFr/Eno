package fr.insee.eno.core.parameter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LunaticParameters {

    private boolean controls;
    private boolean toolTip; // Not implemented yet in Lunatic
    private boolean missingVariables;
    private boolean filterResult;
    private boolean filterDescription;
    private EnoParameters.LunaticPaginationMode lunaticPaginationMode;

    private LunaticParameters() {}

    public static LunaticParameters emptyValues() {
        return new LunaticParameters();
    }

    public static LunaticParameters of(EnoParameters.Context context, EnoParameters.ModeParameter modeParameter) {
        LunaticParameters parameters = new LunaticParameters();
        parameters.lunaticValues(context, modeParameter);
        return parameters;
    }

    private void lunaticValues(EnoParameters.Context context, EnoParameters.ModeParameter modeParameter) {
        //
        if (EnoParameters.ModeParameter.PAPI.equals(modeParameter))
            throw new IllegalArgumentException("Mode 'PAPI' is not compatible with Lunatic format.");
        //
        boolean isInterview = EnoParameters.ModeParameter.CAPI.equals(modeParameter) || EnoParameters.ModeParameter.CATI.equals(modeParameter);
        boolean isWeb = EnoParameters.ModeParameter.CAWI.equals(modeParameter);
        boolean isProcess = EnoParameters.ModeParameter.PROCESS.equals(modeParameter);
        this.setControls(isWeb || isProcess);
        this.setToolTip(isWeb || isProcess);
        this.setFilterDescription(isProcess);
        this.setFilterResult(isWeb);
        this.setMissingVariables(isInterview);
        this.setLunaticPaginationMode(
                EnoParameters.Context.BUSINESS.equals(context) ?
                        EnoParameters.LunaticPaginationMode.SEQUENCE :
                        EnoParameters.LunaticPaginationMode.QUESTION);
    }

}
