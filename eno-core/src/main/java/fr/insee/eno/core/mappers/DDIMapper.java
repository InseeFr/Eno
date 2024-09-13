package fr.insee.eno.core.mappers;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.ddi.lifecycle33.instance.DDIInstanceType;
import fr.insee.ddi.lifecycle33.reusable.AbstractIdentifiableType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.InAnnotationValues;
import fr.insee.eno.core.converter.DDIConverter;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.eno.core.utils.DDIUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Mapper implementation for the DDI input format.
 * While mapping a DDI object to an Eno object, the mapper builds an index of Eno objects.
 */
@Slf4j
public class DDIMapper extends InMapper {

    public DDIMapper() {
        super(Format.DDI, new DDIConverter());
    }

    @Override
    void specificSetup(Object inputObject) {
        AbstractIdentifiableType ddiObject = (AbstractIdentifiableType) inputObject;
        log.debug("DDI mapping entry object: " + DDIUtils.ddiToString(ddiObject));
        DDIIndex ddiIndex = new DDIIndex();
        ddiIndex.indexDDIObject(ddiObject);
        ((DDIConverter) inConverter).setIndex(ddiIndex);
        spelEngine.getContext().setVariable("index", ddiIndex);
    }

    public void mapDDI(@NonNull DDIInstanceDocument ddiInstanceDocument, @NonNull EnoQuestionnaire enoQuestionnaire) {
        mapDDI(ddiInstanceDocument.getDDIInstance(), enoQuestionnaire);
    }

    public void mapDDI(@NonNull DDIInstanceType ddiInstanceType, @NonNull EnoQuestionnaire enoQuestionnaire) {
        log.info("Starting mapping between DDI instance and Eno questionnaire.");
        mapInputObject(ddiInstanceType, enoQuestionnaire);
        log.info("Finished mapping between DDI instance and Eno questionnaire.");
    }

    public void mapDDIObject(AbstractIdentifiableType ddiObject, EnoObject enoObject) {
        mapInputObject(ddiObject, enoObject);
    }

    @Override
    InAnnotationValues readAnnotation(TypeDescriptor typeDescriptor) {
        DDI ddiAnnotation = typeDescriptor.getAnnotation(DDI.class);
        if (ddiAnnotation == null)
            return null;
        return new InAnnotationValues(ddiAnnotation.value(), ddiAnnotation.allowNullList(), ddiAnnotation.debug());
    }

}
