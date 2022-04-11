package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.Lunatic;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LunaticMapper {

    public static void map(Object enoInstance, Object lunaticInstance) {

        BeanWrapper beanWrapper = new BeanWrapperImpl(enoInstance);

        Arrays.stream(beanWrapper.getPropertyDescriptors())
                .filter(propertyDescriptor -> !propertyDescriptor.getName().equals("class"))
                .forEach(propertyDescriptor -> {

                    TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName());
                    Class<?> classType = propertyDescriptor.getPropertyType();

                    Lunatic lunaticAnnotation = typeDescriptor.getAnnotation(Lunatic.class);
                    if(lunaticAnnotation != null) {
                        Expression expression = new SpelExpressionParser().parseExpression(lunaticAnnotation.field());

                        if (String.class.isAssignableFrom(classType)) {
                            EvaluationContext context = new StandardEvaluationContext();
                            context.setVariable("param",
                                    beanWrapper.getPropertyValue(propertyDescriptor.getName()));
                            expression.getValue(context, lunaticInstance);
                        }

                        else if (List.class.isAssignableFrom(classType)) {
                            Collection<Object> lunaticCollection = expression.getValue(lunaticInstance, Collection.class);
                            Collection<Object> enoCollection = null;
                            // pb ici : par exemple: getVariables() renvoie une liste de IVariableType (type abstrait)
                            //Class<?> lunaticCollectionType = expression.getValueTypeDescriptor(lunaticInstance)
                            //        .getResolvableType().getGeneric(0).getRawClass();*/
                            try {
                                enoCollection = (Collection<Object>) propertyDescriptor.getReadMethod().invoke(enoInstance);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            for (Object enoInstance2 : enoCollection) {
                                Object lunaticInstance2 = null;
                                try {
                                    lunaticInstance2 = lunaticAnnotation.instanceType().getDeclaredConstructor().newInstance();
                                    //lunaticInstance2 = lunaticCollectionType.getDeclaredConstructor().newInstance();
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                }
                                lunaticCollection.add(lunaticInstance2);
                                map(enoInstance2, lunaticInstance2);
                            }
                        }

                        else {
                            //TODO
                        }
                    }
                });

    }
}
