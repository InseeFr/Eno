package fr.insee.eno.core.mappers;

import org.springframework.beans.BeanWrapper;

import java.beans.PropertyDescriptor;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;

public class Mapper {

    public static Iterator<PropertyDescriptor> propertyDescriptorIterator(BeanWrapper beanWrapper) {
        return Arrays.stream(beanWrapper.getPropertyDescriptors())
                .filter(propertyDescriptor -> !propertyDescriptor.getName().equals("class"))
                .iterator();
    }

    public static boolean isSimpleType(Class<?> classType) {
        return String.class.isAssignableFrom(classType)
                || int.class.isAssignableFrom(classType)
                || BigInteger.class.isAssignableFrom(classType)
                || double.class.isAssignableFrom(classType)
                || boolean.class.isAssignableFrom(classType)
                || Enum.class.isAssignableFrom(classType);
        // TODO: other simple types later (probably)
    }

}
