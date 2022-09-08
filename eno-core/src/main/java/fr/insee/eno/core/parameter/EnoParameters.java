package fr.insee.eno.core.parameter;

import fr.insee.eno.core.model.Mode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.eno.core.model.Mode.*;

@Getter
@Setter
public class EnoParameters {

    public enum QuestionNumberingMode {NONE, SEQUENCE, ALL}
    public enum LunaticPaginationMode {NONE, SEQUENCE, QUESTION}

    // Eno core parameters
    private boolean commentSection;
    private boolean sequenceNumbering;
    private QuestionNumberingMode questionNumberingMode;
    private boolean arrowCharInQuestions;
    private List<Mode> selectedModes = new ArrayList<>(); //TODO: maybe public class SelectedModes extends List<Mode>

    // Lunatic parameters
    private boolean missingVariables;
    private LunaticPaginationMode lunaticPaginationMode;

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
