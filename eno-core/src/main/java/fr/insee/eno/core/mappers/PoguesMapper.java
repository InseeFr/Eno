package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.InAnnotationValues;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.converter.PoguesConverter;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.Format;
import fr.insee.pogues.model.Questionnaire;
import org.springframework.core.convert.TypeDescriptor;

public class PoguesMapper extends InMapper {

    public PoguesMapper() {
        super(Format.POGUES, new PoguesConverter());
    }

    /**
     * Nothing special to do
     * @param inputObject Pogues model object.
     */
    @Override
    void specificSetup(Object inputObject) {}

    public void mapPoguesQuestionnaire(Questionnaire poguesQuestionnaire, EnoQuestionnaire enoQuestionnaire) {
        mapInputObject(poguesQuestionnaire, enoQuestionnaire);
    }

    @Override
    InAnnotationValues readAnnotation(TypeDescriptor typeDescriptor) {
        Pogues poguesAnnotation = typeDescriptor.getAnnotation(Pogues.class);
        if (poguesAnnotation == null)
            return null;
        return new InAnnotationValues(poguesAnnotation.value(), poguesAnnotation.allowNullList(), poguesAnnotation.debug());
    }

}
