package fr.insee.eno.core.mappers;

import fr.insee.eno.core.model.EnoObject;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.AbstractIdentifiableType;

import java.beans.PropertyDescriptor;

public class MapperTestUtils {

    /** Map Lunatic property between objects. */
    public void mapLunaticProperty(EnoObject enoObject, Object lunaticObject, String propertyName) {
        LunaticMapper lunaticMapper = new LunaticMapper();
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoObject);
        PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor(propertyName);
        lunaticMapper.propertyMapping(enoObject, lunaticObject, beanWrapper, propertyDescriptor);
    }

}
