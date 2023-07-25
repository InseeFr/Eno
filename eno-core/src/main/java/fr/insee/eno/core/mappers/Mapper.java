package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.Contexts;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Mapper {

    Format format;

    public static Iterator<PropertyDescriptor> propertyDescriptorIterator(BeanWrapper beanWrapper) {
        return Arrays.stream(beanWrapper.getPropertyDescriptors())
                .filter(propertyDescriptor -> !propertyDescriptor.getName().equals("class"))
                .iterator();
    }

    public static boolean isSimpleType(Class<?> classType) {
        return classType!=null
                &&
                (CharSequence.class.isAssignableFrom(classType)
                || int.class.isAssignableFrom(classType)
                || Number.class.isAssignableFrom(classType)
                || double.class.isAssignableFrom(classType)
                || boolean.class.isAssignableFrom(classType)
                || Boolean.class.isAssignableFrom(classType)
                || Enum.class.isAssignableFrom(classType));

        // TODO: other simple types later (probably)
    }

    void compatibilityCheck(Object mappedObject, EnoObject enoObject) {
        Class<?>[] contextTypes = getContextTypes(enoObject);
        if (hasNoneAssignableMatch(mappedObject, contextTypes))
            throw new IllegalArgumentException(String.format(
                    "Object of type '%s' is not compatible with Eno object of type '%s'",
                    mappedObject.getClass(), enoObject.getClass()));
    }

    private boolean hasNoneAssignableMatch(Object mappedObject, Class<?>[] contextTypes) {
        return Arrays.stream(contextTypes).noneMatch(contextType ->
                mappedObject.getClass().isAssignableFrom(contextType));
    }

    private Class<?>[] getContextTypes(EnoObject enoObject) {
        Optional<Contexts.Context> contextAnnotation = Arrays.stream(enoObject.getClass().getAnnotationsByType(Contexts.Context.class))
                .filter(context -> format.equals(context.format()))
                .findAny();
        if (contextAnnotation.isEmpty())
            throw new MappingException(String.format(
                    "Context is not defined in Eno model class %s for format %s",
                    enoObject.getClass().getSimpleName(), format));
        return contextAnnotation.get().type();
    }

    @SuppressWarnings("unchecked")
    Collection<Object> readCollection(PropertyDescriptor propertyDescriptor, EnoObject enoObject) {
        try {
            return (Collection<Object>) propertyDescriptor.getReadMethod().invoke(enoObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.debug("hint: Make sure that collection has been initialized (i.e. is not null) in model class.");
            log.debug("hint: Example: List<SomeEnoObject> = new ArrayList<>();");
            throw new MappingException(
                    String.format("Unable to get collection instance on property '%s' of class '%s'.",
                            propertyDescriptor.getName(), enoObject.getClass()),
                    e);
        }
    }

    void unknownTypeException(Class<?> classType, PropertyDescriptor propertyDescriptor, Class<?> modelContextType) {
        log.debug(String.format(
                "Type '%s' found on Eno model property '%s' of model class '%s' " +
                        "is neither a simple type nor an Eno object.",
                classType, propertyDescriptor.getName(), modelContextType.getSimpleName()));
        log.debug("hint: If it should be a simple type, check isSimpleType method in Mapper class.");
        log.debug("hint: If it should be a complex type, make sure that the object inherits EnoObject.");
        throw new MappingException(String.format("Unknown type '%s' encountered in Eno model.", classType));
    }

}
