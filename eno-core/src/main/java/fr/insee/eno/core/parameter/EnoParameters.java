package fr.insee.eno.core.parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.eno.core.model.mode.Mode;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.model.mode.Mode.*;

@Getter
@Setter
public class EnoParameters {

    public enum Context {HOUSEHOLD, BUSINESS, DEFAULT}
    public enum ModeParameter {CAPI, CATI, CAWI, PAPI, PROCESS, DEFAULT}
    public enum Language {FR, EN, IT, ES, DE}
    public enum QuestionNumberingMode {NONE, SEQUENCE, ALL}
    public enum LunaticPaginationMode {NONE, SEQUENCE, QUESTION}

    public static final String DEFAULT_CAMPAIGN_NAME = "test-2020-x00";

    // Context parameters
    private Context context;
    private String campaignName = DEFAULT_CAMPAIGN_NAME;
    private ModeParameter modeParameter;
    private Language language = Language.FR; // unused yet

    // Eno core parameters
    private boolean identificationQuestion; //TODO
    private boolean responseTimeQuestion;
    private boolean commentSection;
    private boolean sequenceNumbering;
    private QuestionNumberingMode questionNumberingMode;
    private boolean arrowCharInQuestions;
    private List<Mode> selectedModes = new ArrayList<>(); //TODO: maybe public class SelectedModes extends List<Mode>

    // Lunatic parameters
    private boolean controls;
    private boolean toolTip; // Not implemented in Lunatic
    private boolean missingVariables;
    private boolean filterResult; //TODO
    private boolean filterDescription; // TODO related to a processing for Generic app
    private boolean unusedVariables; //TODO? processing to remove calculated variables not used in questionnaire
    private LunaticPaginationMode lunaticPaginationMode;

    public static EnoParameters parse(InputStream parametersInputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(parametersInputStream, EnoParameters.class);
    }

    public static String serialize(EnoParameters enoParameters) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(enoParameters);
    }

    public EnoParameters() {
        defaultParameters();
    }

    private void defaultParameters() {
        // Eno core
        commentSection = true;
        sequenceNumbering = true;
        questionNumberingMode = QuestionNumberingMode.SEQUENCE;
        arrowCharInQuestions = true;
        selectedModes.addAll(List.of(CAPI, CATI, CAWI, PAPI));
        // Lunatic
        missingVariables = false;
        lunaticPaginationMode = LunaticPaginationMode.QUESTION;
    }

    public static String lunaticNumberingMode(LunaticPaginationMode paginationMode) {
        return switch (paginationMode) {
            case NONE -> "none"; //TODO: check what is the correct value for Lunatic
            case SEQUENCE -> "sequence"; //TODO: check what is the correct value for Lunatic
            case QUESTION -> "question";
        };
    }

}
