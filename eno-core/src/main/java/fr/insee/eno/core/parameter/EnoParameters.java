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

/** This class contains all the parameters for each transformation.
 * Attributes of this class are contextual parameters (such as collection context and mode)
 * and eno "core" parameters, i.e. parameters that can be applied on every transformation.
 * It also contains parameter objects for output formats. */
@Getter
@Setter
@Slf4j
public class EnoParameters {

    public enum Context {
        /** Questionnaire for businesses. */
        HOUSEHOLD,
        /** Questionnaire for households. */
        BUSINESS,
        /** Default context for visualizing the questionnaire. */
        DEFAULT
    }
    public enum ModeParameter {
        /** Collection by face-to-face interviewer. */
        CAPI,
        /** Collection by telephone interviewer. */
        CATI,
        /** Self-administered web collection. */
        CAWI,
        /** Self-administered collection in paper format. */
        PAPI,
        /** This mode corresponds to the questionnaire revision phases. */
        PROCESS
    }
    public enum Language {FR, EN, IT, ES, DE}
    public enum QuestionNumberingMode {
        /** No numeration in question labels. */
        NONE,
        /** Numerate questions by sequence. */
        SEQUENCE,
        /** Increment the numeration at each question in the questionnaire. */
        ALL
    }

    public static final String DEFAULT_CAMPAIGN_NAME = "test-2020-x00";

    // Context parameters
    /** Survey context. */
    private Context context;
    /** Collection mode of the generated questionnaire. */
    private ModeParameter modeParameter;
    /** Output format of the questionnaire to be generated. */
    private Format outFormat;
    /** Name of the survey campaign. This parameter is useless for now. */
    private String campaignName = DEFAULT_CAMPAIGN_NAME;
    /** Survey language. Unused yet. */
    private Language language = Language.FR;

    // Eno core parameters
    /** If this parameter is set to true, add an 'identification' question in the beginning of the questionnaire. */
    private boolean identificationQuestion;
    /** If this parameter is set to true, add a question about time spent for completing the questionnaire. */
    private boolean responseTimeQuestion;
    /** If this parameter is set to true, add a comment section at the end of the questionnaire. */
    private boolean commentSection;
    /** If this parameter is set to true, add numeration in sequence labels. */
    private boolean sequenceNumbering;
    /** Parameter to specify how the numeration of question should be displayed (can be "none"). */
    private QuestionNumberingMode questionNumberingMode;
    /** If this parameter is set to true, an arrow character is added at the beginning of question labels. */
    private boolean arrowCharInQuestions;

    /** Modes that correspond with the questionnaire to be generated. This parameter is designed to filter elements
     * that doesn't belong in the selected modes.
     * Note: with current implementation, only "modeParameter" is actually relevant. */
    private List<Mode> selectedModes = new ArrayList<>();

    // Lunatic parameters
    /** Parameters that only concern the 'Lunatic' output format. */
    @JsonProperty("lunatic")
    private LunaticParameters lunaticParameters;

    private EnoParameters() {}

    /**
     * Deserializes the given input stream into an Eno parameters object. The input stream is expected to correspond
     * to a json representation of Eno parameters.
     * @param parametersInputStream Input stream of json Eno parameters.
     * @return Given json parameters in an Eno parameters object.
     * @throws IOException if the input stream cannot be read.
     * @throws EnoParametersException If the json content of the stream is invalid.
     */
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

    /**
     * Serializes given Eno parameters object to a json string.
     * @param enoParameters An Eno parameters object.
     * @return A json string.
     * @throws JsonProcessingException if serialization fails.
     */
    public static String serialize(EnoParameters enoParameters) throws JsonProcessingException {
        log.info("Serializing parameters file");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(enoParameters);
    }

    /**
     * Returns an Eno parameters instance with empty values.
     * Note: primitive types get default values, for instance boolean are false.
     * @return An Eno parameters instance.
     */
    public static EnoParameters emptyValues() {
        return new EnoParameters();
    }

    /**
     * Constructor method to get an Eno parameters instance with core-parameters with values that correspond to the
     * given context and mode.
     * @param context Context parameter.
     * @param modeParameter Mode parameter.
     * @return An Eno parameter instance.
     */
    public static EnoParameters of(Context context, ModeParameter modeParameter) {
        log.info("Parameters with context {}, mode {}", context, modeParameter);
        EnoParameters enoParameters = new EnoParameters();
        //
        enoParameters.enoValues(context, modeParameter);
        //
        return enoParameters;
    }

    /**
     * Constructor method to get an Eno parameters instance for the given format with values that correspond to the
     * given context and mode.
     * @param context Context parameter.
     * @param modeParameter Mode parameter.
     * @param outFormat Out format of the questionnaire to be generated.
     * @return An Eno parameter instance.
     */
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
        boolean isWebMode = ModeParameter.CAWI.equals(modeParameter);
        boolean isProcessMode = ModeParameter.PROCESS.equals(modeParameter);
        //
        this.setIdentificationQuestion(Context.BUSINESS.equals(context) && !isWebMode);
        this.setResponseTimeQuestion(Context.BUSINESS.equals(context));
        this.setCommentSection(Context.BUSINESS.equals(context) || (isInterviewMode || isProcessMode));
        this.setSequenceNumbering(true);
        this.setQuestionNumberingMode(questionNumberingModeValue(context, modeParameter));
        this.setArrowCharInQuestions(!isWebMode);
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

}
