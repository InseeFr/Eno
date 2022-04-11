package fr.insee.eno.core.mappers;

import org.springframework.beans.BeanWrapper;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Iterator;

public class Mapper {

    static Iterator<PropertyDescriptor> propertyDescriptorIterator(BeanWrapper beanWrapper) {
        return Arrays.stream(beanWrapper.getPropertyDescriptors())
                .filter(propertyDescriptor -> !propertyDescriptor.getName().equals("class"))
                .iterator();
    }

}
