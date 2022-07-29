package fr.insee.eno.core.parameter;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EnoParameters {

    public enum QuestionNumberingMode {NONE, SEQUENCE, ALL}

    private boolean commentSection;
    private boolean sequenceNumbering;
    private QuestionNumberingMode questionNumberingMode;
    private boolean arrowCharInQuestions;

    public EnoParameters() {
        defaultParameters();
    }

    private void defaultParameters() {
        commentSection = true;
        sequenceNumbering = true;
        questionNumberingMode = QuestionNumberingMode.SEQUENCE;
        arrowCharInQuestions = true;
    }

}
