package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.RomanNumber;
import fr.insee.eno.core.utils.VtlSyntaxUtils;

public class EnoAddNumberingInSequences implements ProcessingStep<EnoQuestionnaire> {

    public static final String SEQUENCE_NUMBERING_SEPARATOR = "-";

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        int sequenceNumber = 1;
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            Label sequenceLabel = sequence.getLabel();
            sequenceLabel.setValue(addNumberInLabel(sequenceNumber, sequenceLabel));
            sequenceNumber ++;
        }
    }

    private static String addNumberInLabel(int sequenceNumber, Label sequenceLabel) {
        String numberingPrefix = RomanNumber.toRoman(sequenceNumber) + " " + SEQUENCE_NUMBERING_SEPARATOR + " ";
        return VtlSyntaxUtils.concatenateStrings(numberingPrefix, sequenceLabel.getValue());
    }

}
