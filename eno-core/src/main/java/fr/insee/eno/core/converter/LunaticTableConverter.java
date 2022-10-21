package fr.insee.eno.core.converter;

import fr.insee.eno.core.model.question.TableQuestion;
import fr.insee.lunatic.model.flat.Table;
import lombok.extern.slf4j.Slf4j;

/** Class that holds the conversion logic between model tables and Lunatic tables. */
@Slf4j
public class LunaticTableConverter {

    public static Table convertEnoTable(TableQuestion enoTable) {
        //
        Table lunaticTable = new Table();
        //
        log.warn("Table question conversion to Lunatic is not implemented.");
        //
        return lunaticTable;
    }
}
