package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.processing.EnoProcessingInterface;
import fr.insee.eno.core.utils.RomanNumber;

public class EnoAddNumberingInSequences implements EnoProcessingInterface {

    public static final String SEQUENCE_NUMBERING_SEPARATOR = " -";

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        int sequenceNumber = 1;
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            Label sequenceLabel = sequence.getLabel();
            sequenceLabel.setValue(RomanNumber.toRoman(sequenceNumber) + SEQUENCE_NUMBERING_SEPARATOR + " " + sequenceLabel.getValue());
            sequenceNumber ++;
        }
    }

}
