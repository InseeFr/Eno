package fr.insee.eno.core.processing.in.steps.ddi;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.Filter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * In some cases variable references can overlap. This could lead to incorrect replacement of references by the
     * corresponding variable name. This nested class contains a group of tests for these cases.
     */
    @Nested
    class OverlappingCases {

        /**
         * This test uses an ordered implementation of Set for binding references, to simulate what could happen with
         * real data.
         */
        @Test
        void filterExpression_overlappingReferences_ascendingCase() {
            //
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            Filter filter = new Filter();
            CalculatedExpression calculatedExpression= new CalculatedExpression();
            calculatedExpression.setValue("foo-ref-1 = 1 and foo-ref-10 = 1");
            List<BindingReference> bindingReferences = new ArrayList<>();
            bindingReferences.add(new BindingReference("foo-ref-1", "FOO_A"));
            bindingReferences.add(new BindingReference("foo-ref-10", "FOO_K"));
            calculatedExpression.setBindingReferences(bindingReferences);
            filter.setExpression(calculatedExpression);
            enoQuestionnaire.getFilters().add(filter);
            //
            new DDIResolveVariableReferencesInExpressions().apply(enoQuestionnaire);
            //
            assertEquals("FOO_A = 1 and FOO_K = 1",
                    enoQuestionnaire.getFilters().getFirst().getExpression().getValue());
        }

        /**
         * Same test with binding references in the reverse order. (These two tests could have been a parametrized
         * test, but it would have been harder to read.)
         */
        @Test
        void filterExpression_overlappingReferences_descendingCase() {
            //
            EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
            Filter filter = new Filter();
            CalculatedExpression calculatedExpression= new CalculatedExpression();
            calculatedExpression.setValue("foo-ref-1 = 1 and foo-ref-10 = 1");
            List<BindingReference> bindingReferences = new ArrayList<>();
            bindingReferences.add(new BindingReference("foo-ref-10", "FOO_K"));
            bindingReferences.add(new BindingReference("foo-ref-1", "FOO_A"));
            calculatedExpression.setBindingReferences(bindingReferences);
            filter.setExpression(calculatedExpression);
            enoQuestionnaire.getFilters().add(filter);
            //
            new DDIResolveVariableReferencesInExpressions().apply(enoQuestionnaire);
            //
            assertEquals("FOO_A = 1 and FOO_K = 1",
                    enoQuestionnaire.getFilters().get(0).getExpression().getValue());
        }

    }

}
