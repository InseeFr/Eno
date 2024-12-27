package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.code.CodeItem;
import fr.insee.eno.core.model.code.CodeList;
import fr.insee.eno.core.processing.ProcessingStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PoguesNestedCodeLists implements ProcessingStep<EnoQuestionnaire> {

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getCodeLists().forEach(this::organizeCodeItems);
    }

    private void organizeCodeItems(CodeList codeList) {
        Map<String, CodeItem> codeItemMap = codeList.getCodeItems().stream()
                .collect(Collectors.toMap(CodeItem::getValue, codeItem -> codeItem));

        List<CodeItem> organized = new ArrayList<>();

        codeList.getCodeItems().forEach(codeItem -> {
            if (codeItem.getParentValue() == null || codeItem.getParentValue().isEmpty()) {
                organized.add(codeItem);
                return;
            }
            CodeItem parentCodeItem = codeItemMap.get(codeItem.getParentValue());
            if (parentCodeItem == null)
                throw new IllegalPoguesElementException(String.format(
                        "%s has a code of value %s, its parent code %s cannot be found.",
                        codeList, codeItem.getValue(), codeItem.getParentValue()));
            parentCodeItem.getCodeItems().add(codeItem);
        });

        codeList.setCodeItems(organized);
    }

}
