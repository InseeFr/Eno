package fr.insee.eno.core.reference;

import instance33.DDIInstanceDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import reusable33.AbstractIdentifiableType;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class DDIIndex extends HashMap<String, Object> {

    public void indexDDI(DDIInstanceDocument ddiInstanceDocument) {
        if (this.isEmpty()) {
            recursiveIndexing(ddiInstanceDocument.getDDIInstance());
        } else {
            log.debug("Indexing already done.");
        }
    }

    private void recursiveIndexing(AbstractIdentifiableType ddiObject) {

        String ddiObjectId = ddiObject.getIDArray(0).getStringValue();
        this.merge(ddiObjectId, ddiObject, (oldDDIObject, newDDIObject) -> {
            throw new RuntimeException(String.format("Duplicate ID \"%s\" found in given DDI.", ddiObjectId));
        });

        BeanWrapper beanWrapper = new BeanWrapperImpl(ddiObject);

        Arrays.stream(beanWrapper.getPropertyDescriptors())
                .filter(propertyDescriptor -> !propertyDescriptor.getName().equals("class"))
                .forEach(propertyDescriptor -> {
                    if (List.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                        Class<?> listContentType = beanWrapper.getPropertyTypeDescriptor(propertyDescriptor.getName())
                                .getResolvableType().getGeneric(0).getRawClass();
                        if(listContentType == null) {
                            log.debug(propertyDescriptor.getReadMethod().getGenericReturnType().getTypeName());
                            log.debug("ok");
                        }
                        if (listContentType != null && AbstractIdentifiableType.class.isAssignableFrom(listContentType)) {
                            try {
                                @SuppressWarnings("unchecked") // https://stackoverflow.com/a/4388173/13425151
                                Collection<AbstractIdentifiableType> ddiCollection = (Collection<AbstractIdentifiableType>) propertyDescriptor.getReadMethod().invoke(ddiObject);
                                for (Object ddiObject2 : ddiCollection) {
                                    recursiveIndexing((AbstractIdentifiableType) ddiObject2);
                                }
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException("Error when reading property descriptor", e);
                            }
                        }
                    }
                });
    }
}
