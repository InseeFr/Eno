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

    private boolean commentSection;
    private boolean sequenceNumbering;
    private QuestionNumberingMode questionNumberingMode;
    private boolean arrowCharInQuestions;
    private List<Mode> selectedModes = new ArrayList<>();

    public EnoParameters() {
        defaultParameters();
    }

    private void defaultParameters() {
        commentSection = true;
        sequenceNumbering = true;
        questionNumberingMode = QuestionNumberingMode.SEQUENCE;
        arrowCharInQuestions = true;
        selectedModes.addAll(List.of(CAPI, CATI, CAWI, PAPI));
    }

}
