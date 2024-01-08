package fr.insee.eno.core.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.eno.core.exceptions.business.EnoParametersException;
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
    @JsonProperty("lunatic")
    private LunaticParameters lunaticParameters;

    private EnoParameters() {}

    public static EnoParameters parse(InputStream parametersInputStream) throws IOException, EnoParametersException {
        log.info("Parsing Eno parameters from input stream");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(parametersInputStream, EnoParameters.class);
        } catch (JsonProcessingException jsonProcessingException) {
            // TODO: more detailed exception message (e.g. using a json schema)
            throw new EnoParametersException("Error while processing json content.", jsonProcessingException);
        }

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
        enoParameters.setLunaticParameters(LunaticParameters.of(context, modeParameter));
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
        boolean isInterviewMode = ModeParameter.CAPI.equals(modeParameter) || ModeParameter.CATI.equals(modeParameter);
        boolean isProcessMode = ModeParameter.PROCESS.equals(modeParameter);
        //
        this.setIdentificationQuestion(Context.BUSINESS.equals(context));
        this.setResponseTimeQuestion(Context.BUSINESS.equals(context));
        this.setCommentSection(Context.BUSINESS.equals(context) || (isInterviewMode || isProcessMode));
        this.setSequenceNumbering(true);
        this.setQuestionNumberingMode(questionNumberingModeValue(context, modeParameter));
        this.setArrowCharInQuestions(arrowCharValue(context, modeParameter));
    }

    private QuestionNumberingMode questionNumberingModeValue(Context context, ModeParameter modeParameter) {
        return switch (context) {
            case DEFAULT -> QuestionNumberingMode.ALL;
            case HOUSEHOLD ->
                    (ModeParameter.PAPI.equals(modeParameter) || ModeParameter.PROCESS.equals(modeParameter)) ?
                            QuestionNumberingMode.ALL :
                            QuestionNumberingMode.NONE;
            case BUSINESS -> QuestionNumberingMode.SEQUENCE;
        };
    }

    private boolean arrowCharValue(Context context, ModeParameter modeParameter) {
        return switch (context) {
            case DEFAULT, BUSINESS -> true;
            case HOUSEHOLD -> ! ModeParameter.CAWI.equals(modeParameter);
        };
    }

}
