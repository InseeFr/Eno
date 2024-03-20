package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.model.suggester.SuggesterConfigurationDTO;
import fr.insee.eno.core.model.suggester.SuggesterFieldDTO;
import fr.insee.eno.core.model.suggester.SuggesterOrderDTO;
import fr.insee.eno.core.model.suggester.SuggesterQueryParserDTO;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.math.BigInteger;

/**
 * In Eno model, suggester configurations are hold by code lists objects.
 * This processing fills the "suggesters" section of the Lunatic questionnaire using these.
 */
public class LunaticSuggestersConfiguration implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;

    public LunaticSuggestersConfiguration(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        enoQuestionnaire.getCodeLists().stream()
                .filter(codeList -> codeList.getSuggesterConfiguration() != null)
                .forEach(codeList -> {
                    SuggesterType lunaticSuggester = createLunaticSuggester(codeList);
                    lunaticQuestionnaire.getSuggesters().add(lunaticSuggester);
                });
    }

    public SuggesterType createLunaticSuggester(CodeList enoCodeList) {
        SuggesterType lunaticSuggester = new SuggesterType();

        lunaticSuggester.setName(enoCodeList.getName());

        SuggesterConfigurationDTO enoSuggesterConfiguration = enoCodeList.getSuggesterConfiguration();

        enoSuggesterConfiguration.getFields().forEach(suggesterFieldDTO -> {
            SuggesterField suggesterField = createLunaticSuggesterField(suggesterFieldDTO);
            lunaticSuggester.getFields().add(suggesterField);
        });

        lunaticSuggester.setMeloto(enoSuggesterConfiguration.getMeloto());
        if (enoSuggesterConfiguration.getMax() != null)
            lunaticSuggester.setMax(BigInteger.valueOf(enoSuggesterConfiguration.getMax()));
        lunaticSuggester.setStopWords(enoSuggesterConfiguration.getStopWords());

        SuggesterOrder suggesterOrder = createLunaticSuggesterOrder(enoSuggesterConfiguration.getOrder());
        lunaticSuggester.setOrder(suggesterOrder);

        SuggesterQueryParser suggesterQueryParser = createLunaticQueryParser(enoSuggesterConfiguration.getQueryParser());
        lunaticSuggester.setQueryParser(suggesterQueryParser);

        lunaticSuggester.setUrl(enoSuggesterConfiguration.getUrl());
        if (enoSuggesterConfiguration.getVersion() != null)
            lunaticSuggester.setVersion(BigInteger.valueOf(enoSuggesterConfiguration.getVersion()));

        return lunaticSuggester;
    }

    private static SuggesterField createLunaticSuggesterField(SuggesterFieldDTO enoSuggesterField) {
        SuggesterField suggesterField = new SuggesterField();

        suggesterField.setName(enoSuggesterField.getName());
        suggesterField.setLanguage(enoSuggesterField.getLanguage());
        if (enoSuggesterField.getMin() != null)
            suggesterField.setMin(BigInteger.valueOf(enoSuggesterField.getMin()));
        suggesterField.setStemmer(enoSuggesterField.getStemmer());

        if (! enoSuggesterField.getRules().isEmpty()) {
            FieldRules fieldRules = new FieldRules();
            if (FieldRules.SOFT_RULE.equals(enoSuggesterField.getRules().getFirst()))
                fieldRules.setRule(FieldRules.SOFT_RULE);
            else
                enoSuggesterField.getRules().forEach(fieldRules::addPattern);
            suggesterField.setRules(fieldRules);
        }

        enoSuggesterField.getSynonyms().forEach(fieldSynonymDTO ->
                suggesterField.getSynonyms().put(fieldSynonymDTO.getSource(), fieldSynonymDTO.getTarget()));

        return suggesterField;
    }

    private static SuggesterOrder createLunaticSuggesterOrder(SuggesterOrderDTO enoSuggesterOrder) {
        if (enoSuggesterOrder == null)
            return null;
        SuggesterOrder suggesterOrder = new SuggesterOrder();
        suggesterOrder.setField(enoSuggesterOrder.getField());
        suggesterOrder.setType(enoSuggesterOrder.getType());
        return suggesterOrder;
    }

    private SuggesterQueryParser createLunaticQueryParser(SuggesterQueryParserDTO enoQueryParser) {
        //
        if (enoQueryParser == null)
            return null;
        SuggesterQueryParser queryParser = new SuggesterQueryParser();
        queryParser.setType(enoQueryParser.getType());
        if (enoQueryParser.getParams() == null)
            return queryParser;
        //
        SuggesterQueryParserParams queryParserParams = new SuggesterQueryParserParams();
        queryParserParams.setLanguage(enoQueryParser.getParams().getLanguage());
        if (enoQueryParser.getParams().getMin() != null)
            queryParserParams.setMin(BigInteger.valueOf(enoQueryParser.getParams().getMin()));
        queryParserParams.setPattern(enoQueryParser.getParams().getPattern());
        queryParserParams.setStemmer(enoQueryParser.getParams().getStemmer());
        queryParser.setParams(queryParserParams);
        return queryParser;
    }

}
