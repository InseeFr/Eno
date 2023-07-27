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

import static fr.insee.eno.core.model.mode.Mode.*;

@Getter
@Setter
@Slf4j
public class EnoParameters {

    public enum Context {HOUSEHOLD, BUSINESS, DEFAULT}
    public enum ModeParameter {CAPI, CATI, CAWI, PAPI, PROCESS, DEFAULT}
    public enum Language {FR, EN, IT, ES, DE}
    public enum QuestionNumberingMode {NONE, SEQUENCE, ALL}
    public enum LunaticPaginationMode {NONE, SEQUENCE, QUESTION}

    public static final String DEFAULT_CAMPAIGN_NAME = "test-2020-x00";

    // Context parameters
    private Context context;
    private String campaignName = DEFAULT_CAMPAIGN_NAME; // unused yet
    private ModeParameter modeParameter;
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
    private boolean unusedVariables; // Not maintained in Eno v3 for now
    private LunaticPaginationMode lunaticPaginationMode;

    private EnoParameters() {}

    public static EnoParameters emptyValues() {
        return new EnoParameters();
    }

    public static EnoParameters defaultValues() {
        EnoParameters enoParameters = new EnoParameters();
        enoParameters.setDefaultValues();
        enoParameters.setContext(Context.DEFAULT);
        return enoParameters;
    }

    public static EnoParameters of(Context context, Format outFormat) {
        log.info("Parameters with context {} and out format {}", context, outFormat);
        // TODO: values in function of context & out format
        EnoParameters enoParameters = new EnoParameters();
        enoParameters.setDefaultValues();
        enoParameters.setContext(context);
        return enoParameters;
    }

    private void setDefaultValues() {
        // Eno core
        identificationQuestion = false;
        responseTimeQuestion = false;
        commentSection = true;
        sequenceNumbering = true;
        questionNumberingMode = QuestionNumberingMode.SEQUENCE;
        arrowCharInQuestions = true;
        selectedModes = new ArrayList<>(List.of(CAPI, CATI, CAWI, PAPI));
        // Lunatic
        controls = true;
        missingVariables = false;
        filterResult = false;
        filterDescription = false;
        lunaticPaginationMode = LunaticPaginationMode.QUESTION;
    }

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

}
