package fr.insee.eno.core.mappers;

import fr.insee.eno.core.model.EnoObject;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.AbstractIdentifiableType;

import java.beans.PropertyDescriptor;

public class MapperTestUtils {

    /** Map property between objects with default evaluation context. */
    public void mapProperty(AbstractIdentifiableType ddiObject, EnoObject enoObject, String propertyName) {
        EvaluationContext context = new StandardEvaluationContext();
        mapProperty(ddiObject, enoObject, propertyName, context);
    }

    /** Map property between objects with given evaluation context. */
    public void mapProperty(AbstractIdentifiableType ddiObject, EnoObject enoObject, String propertyName,
                            EvaluationContext context) {
        DDIMapper ddiMapper = new DDIMapper();
        BeanWrapper beanWrapper = new BeanWrapperImpl(enoObject);
        PropertyDescriptor propertyDescriptor = beanWrapper.getPropertyDescriptor(propertyName);
        ddiMapper.propertyMapping(ddiObject, enoObject, beanWrapper, propertyDescriptor, context);
    }

}
