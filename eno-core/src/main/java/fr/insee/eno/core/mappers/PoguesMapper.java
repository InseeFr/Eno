package fr.insee.eno.core.mappers;

import fr.insee.eno.core.annotations.InAnnotationValues;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.converter.PoguesConverter;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.Format;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.VariableType;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Map;
import java.util.stream.Collectors;

public class PoguesMapper extends InMapper {

    public PoguesMapper() {
        super(Format.POGUES, new PoguesConverter());
    }

    public void mapPoguesQuestionnaire(Questionnaire poguesQuestionnaire, EnoQuestionnaire enoQuestionnaire) {
        mapInputObject(poguesQuestionnaire, enoQuestionnaire);
    }

    @Override
    void specificSetup(Object poguesObject) {
        if (! (poguesObject instanceof Questionnaire poguesQuestionnaire))
            return;
        if (poguesQuestionnaire.getVariables() == null)
            return;
        // For now only index variables of Pogues questionnaire
        Map<String, VariableType> variableIndex = poguesQuestionnaire.getVariables().getVariable().stream()
                .collect(Collectors.toMap(VariableType::getId, poguesVariable -> poguesVariable));
        spelEngine.getContext().setVariable("poguesIndex", variableIndex);
    }

    @Override
    InAnnotationValues readAnnotation(TypeDescriptor typeDescriptor) {
        Pogues poguesAnnotation = typeDescriptor.getAnnotation(Pogues.class);
        if (poguesAnnotation == null)
            return null;
        return new InAnnotationValues(poguesAnnotation.value(), poguesAnnotation.allowNullList(), poguesAnnotation.debug());
    }

}
