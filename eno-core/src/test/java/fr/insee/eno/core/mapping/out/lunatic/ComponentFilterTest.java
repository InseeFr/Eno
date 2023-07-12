package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComponentFilterTest {

    private ComponentFilter enoComponentFilter;
    private ConditionFilterType lunaticConditionFilter;

    @BeforeEach
    void createObjects() {
         enoComponentFilter = new ComponentFilter();
         lunaticConditionFilter = new ConditionFilterType();
    }

    @Test
    void defaultComponentFilter() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoComponentFilter, lunaticConditionFilter);
        //
        assertEquals(ComponentFilter.DEFAULT_FILTER_VALUE, lunaticConditionFilter.getValue());
        assertEquals(Constant.LUNATIC_LABEL_VTL, lunaticConditionFilter.getType());
        assertTrue(lunaticConditionFilter.getBindingDependencies().isEmpty());
    }

    @Test
    void componentFilter_withBindingReferences() {
        //
        enoComponentFilter.setValue("(FOO_VARIABLE = 1)");
        enoComponentFilter.setType(Constant.LUNATIC_LABEL_VTL);
        enoComponentFilter.getBindingReferences().add(new BindingReference("foo-id", "FOO_VARIABLE"));
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoComponentFilter, lunaticConditionFilter);
        //
        assertEquals("(FOO_VARIABLE = 1)", lunaticConditionFilter.getValue());
        assertEquals(Constant.LUNATIC_LABEL_VTL, lunaticConditionFilter.getType());
        assertEquals(1, lunaticConditionFilter.getBindingDependencies().size());
        assertEquals("FOO_VARIABLE", lunaticConditionFilter.getBindingDependencies().get(0));
    }

}
