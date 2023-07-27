package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.model.variable.Variable;
import fr.insee.lunatic.model.flat.VariableTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariableTest {

    @Test
    void collectionTypeConversion() {
        assertEquals(VariableTypeEnum.COLLECTED, Variable.lunaticCollectionType(Variable.CollectionType.COLLECTED));
        assertEquals(VariableTypeEnum.CALCULATED, Variable.lunaticCollectionType(Variable.CollectionType.CALCULATED));
        assertEquals(VariableTypeEnum.EXTERNAL, Variable.lunaticCollectionType(Variable.CollectionType.EXTERNAL));
    }

}
