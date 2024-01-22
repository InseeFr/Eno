package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.Filter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DDIResolveVariableReferencesInExpressionsTest {

    @Test
    void filterExpression_oneReference() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        Filter filter = new Filter();
        CalculatedExpression calculatedExpression= new CalculatedExpression();
        calculatedExpression.setValue("foo-reference = 1");
        calculatedExpression.getBindingReferences()
                .add(new BindingReference("foo-reference", "FOO"));
        filter.setExpression(calculatedExpression);
        enoQuestionnaire.getFilters().add(filter);
        //
        new DDIResolveVariableReferencesInExpressions().apply(enoQuestionnaire);
        //
        assertEquals("FOO = 1", enoQuestionnaire.getFilters().get(0).getExpression().getValue());
    }

}
