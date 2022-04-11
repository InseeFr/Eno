package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.DDI;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DDIMapper extends Mapper {

    public static void map(Object modelItemInstance, Object ddiItemInstance) {

        BeanWrapper beanWrapper = new BeanWrapperImpl(modelItemInstance);

        for (Iterator<PropertyDescriptor> iterator = propertyDescriptorIterator(beanWrapper); iterator.hasNext();) {

            PropertyDescriptor propertyDescriptor = iterator.next();
            TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName());
            Class<?> classType = typeDescriptor.getType();

            DDI ddiAnnotation = typeDescriptor.getAnnotation(DDI.class);
            if (ddiAnnotation != null) {
                Expression expression = new SpelExpressionParser().parseExpression(ddiAnnotation.field());

                if (String.class.isAssignableFrom(classType)) { //todo: || other simple types
                    beanWrapper.setPropertyValue(propertyDescriptor.getName(),
                            expression.getValue(ddiItemInstance, classType));
                }

                else if (List.class.isAssignableFrom(classType)) {
                    Collection<?> ddiCollection = expression.getValue(ddiItemInstance, Collection.class);
                    Collection<Object> modelCollection = null;
                    try {
                        modelCollection = (Collection<Object>) propertyDescriptor.getReadMethod().invoke(modelItemInstance);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    for (Object ddiItemInstance2 : ddiCollection) {
                        try {
                            Object modelItemInstance2 = beanWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName())
                                    .getResolvableType().getGeneric(0).getRawClass()
                                    .getDeclaredConstructor().newInstance();
                            //
                            modelCollection.add(modelItemInstance2);
                            //
                            map(modelItemInstance2, ddiItemInstance2);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                }

                else {
                    try {
                        Object modelInstance2 = typeDescriptor.getType().getDeclaredConstructor().newInstance();
                        map(modelInstance2, expression.getValue(ddiItemInstance));
                        beanWrapper.setPropertyValue(propertyDescriptor.getName(), modelInstance2);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }

}
