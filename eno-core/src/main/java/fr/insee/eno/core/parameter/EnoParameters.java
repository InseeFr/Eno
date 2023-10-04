package fr.insee.eno.core.parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.eno.core.model.mode.Mode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
public class EnoParameters {

    public enum Context {HOUSEHOLD, BUSINESS, DEFAULT}
    public enum ModeParameter {CAPI, CATI, CAWI, PAPI, PROCESS}
    public enum Language {FR, EN, IT, ES, DE}
    public enum QuestionNumberingMode {NONE, SEQUENCE, ALL}
    public enum LunaticPaginationMode {NONE, SEQUENCE, QUESTION}

    public static final String DEFAULT_CAMPAIGN_NAME = "test-2020-x00";

    // Context parameters
    private Context context;
    private ModeParameter modeParameter;
    private Format outFormat;
    private String campaignName = DEFAULT_CAMPAIGN_NAME; // unused yet
    private Language language = Language.FR; // unused yet

    // Eno core parameters
    private boolean identificationQuestion;
    private boolean responseTimeQuestion;
    private boolean commentSection;
    private boolean sequenceNumbering;
    private QuestionNumberingMode questionNumberingMode;
    private boolean arrowCharInQuestions;
    private List<Mode> selectedModes = new ArrayList<>();

    // Lunatic parameters
    private boolean controls;
    private boolean toolTip; // Not implemented yet in Lunatic
    private boolean missingVariables;
    private boolean filterResult;
    private boolean filterDescription;
    private LunaticPaginationMode lunaticPaginationMode;

    private EnoParameters() {}

    public static EnoParameters parse(InputStream parametersInputStream) throws IOException {
        log.info("Parsing Eno parameters from input stream");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(parametersInputStream, EnoParameters.class);
    }

    public static String serialize(EnoParameters enoParameters) throws JsonProcessingException {
        log.info("Serializing parameters file");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(enoParameters);
    }

    public static EnoParameters emptyValues() {
        return new EnoParameters();
    }

    public static EnoParameters of(Context context, ModeParameter modeParameter) {
        log.info("Parameters with context {}, mode {}", context, modeParameter);
        EnoParameters enoParameters = new EnoParameters();
        //
        enoParameters.enoValues(context, modeParameter);
        //
        return enoParameters;
    }

    public static EnoParameters of(Context context, ModeParameter modeParameter, Format outFormat) {
        log.info("Parameters with context {}, out format {}, mode {}", context, outFormat, modeParameter);
        EnoParameters enoParameters = new EnoParameters();
        //
        if (! Format.LUNATIC.equals(outFormat))
            throw new UnsupportedOperationException("Only Lunatic out format is supported for now.");
        enoParameters.setOutFormat(outFormat);
        //
        enoParameters.enoValues(context, modeParameter);
        //
        enoParameters.lunaticValues(modeParameter);
        //
        return enoParameters;
    }

    private void enoValues(Context context, ModeParameter modeParameter) {
        //
        this.setContext(context);
        this.setModeParameter(modeParameter);
        //
        switch (modeParameter) {
            case CAPI -> this.setSelectedModes(List.of(Mode.CAPI));
            case CATI -> this.setSelectedModes(List.of(Mode.CATI));
            case CAWI -> this.setSelectedModes(List.of(Mode.CAWI));
            case PAPI -> this.setSelectedModes(List.of(Mode.PAPI));
            case PROCESS -> this.setSelectedModes(List.of(Mode.CAPI, Mode.CATI, Mode.CAWI, Mode.PAPI));
        }
        //
        this.setIdentificationQuestion(Context.BUSINESS.equals(context));
        this.setResponseTimeQuestion(Context.BUSINESS.equals(context));
        this.setCommentSection(Context.HOUSEHOLD.equals(context) || Context.BUSINESS.equals(context));
        this.setQuestionNumberingMode(
                Context.HOUSEHOLD.equals(context) ? QuestionNumberingMode.ALL : QuestionNumberingMode.SEQUENCE);
        this.sequenceNumbering = true;
        this.arrowCharInQuestions = true;
    }

    private void lunaticValues(ModeParameter modeParameter) {
        //
        if (ModeParameter.PAPI.equals(modeParameter))
            throw new IllegalArgumentException("Mode 'PAPI' is not compatible with Lunatic format.");
        //
        boolean isInterview = ModeParameter.CAPI.equals(modeParameter) || ModeParameter.CATI.equals(modeParameter);
        boolean isWeb = ModeParameter.CAWI.equals(modeParameter);
        boolean isProcess = ModeParameter.PROCESS.equals(modeParameter);
        this.setControls(isWeb || isProcess);
        this.setToolTip(isWeb || isProcess);
        this.setFilterDescription(isProcess);
        this.setFilterResult(isWeb);
        this.setMissingVariables(isInterview);
        this.setLunaticPaginationMode(
                isInterview || isWeb ? LunaticPaginationMode.QUESTION : LunaticPaginationMode.NONE);
    }

}
