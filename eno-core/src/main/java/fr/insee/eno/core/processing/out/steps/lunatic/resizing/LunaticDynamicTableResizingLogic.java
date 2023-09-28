package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.model.lunatic.LunaticResizingEntry;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.RosterForLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LunaticDynamicTableResizingLogic {

    private final Questionnaire lunaticQuestionnaire;

    public LunaticDynamicTableResizingLogic(Questionnaire lunaticQuestionnaire) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
    }

    public List<LunaticResizingEntry> buildResizingEntries(RosterForLoop dynamicTable) {
        log.warn("Dynamic table '{}': no resizing entries will be added.", dynamicTable.getId());
        log.debug("(Questionnaire '{}')", lunaticQuestionnaire.getId());
        return new ArrayList<>();
    }

}
