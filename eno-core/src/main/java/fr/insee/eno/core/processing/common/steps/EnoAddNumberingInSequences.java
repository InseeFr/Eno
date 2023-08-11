package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.RomanNumber;
import fr.insee.eno.core.utils.VtlSyntaxUtils;

public class EnoAddNumberingInSequences implements ProcessingStep<EnoQuestionnaire> {

    private static final String SEQUENCE_NUMBERING_SEPARATOR = "-";

    private final EnoParameters.ModeParameter modeParameter;

    public EnoAddNumberingInSequences(EnoParameters.ModeParameter modeParameter) {
        this.modeParameter = modeParameter;
    }

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        int sequenceNumber = 1;
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            Label sequenceLabel = sequence.getLabel();
            sequenceLabel.setValue(addNumberInLabel(sequenceNumber, sequenceLabel.getValue()));
            sequenceNumber ++;
        }
    }

    private String addNumberInLabel(int sequenceNumber, String sequenceLabelValue) {
        String numberingPrefix = RomanNumber.toRoman(sequenceNumber) + " " + SEQUENCE_NUMBERING_SEPARATOR + " ";
        // In paper case: no VTL
        if (EnoParameters.ModeParameter.PAPI.equals(modeParameter))
            return numberingPrefix + sequenceLabelValue.replace("\"", "");
        // Standard case: VTL concatenation
        return VtlSyntaxUtils.concatenateStrings("\""+numberingPrefix+"\"", sequenceLabelValue);
    }

}
