package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.reference.EnoCatalog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DDIResolveVariableReferencesInLabelsTest {

    @Test
    void resolveReference_sequence() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        Sequence sequence = new Sequence();
        enoQuestionnaire.getSequences().add(sequence);
        //
        Label label = new Label();
        label.setValue("\"Label with reference \" || ¤foo-reference¤");
        sequence.setLabel(label);
        //
        Variable variable = new CollectedVariable();
        variable.setName("FOO");
        variable.setReference("foo-reference");
        enoQuestionnaire.getVariables().add(variable);
        //
        EnoCatalog enoCatalog = new EnoCatalog(enoQuestionnaire);

        //
        DDIResolveVariableReferencesInLabels processing = new DDIResolveVariableReferencesInLabels(enoCatalog);
        processing.apply(enoQuestionnaire);

        //
        assertEquals("\"Label with reference \" || FOO", sequence.getLabel().getValue());
    }

    // TODO: other tests on this

}
