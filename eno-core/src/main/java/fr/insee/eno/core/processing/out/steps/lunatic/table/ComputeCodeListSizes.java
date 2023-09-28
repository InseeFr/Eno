package fr.insee.eno.core.processing.out.steps.lunatic.table;

import fr.insee.eno.core.model.code.CodeList;

public class ComputeCodeListSizes {

    private ComputeCodeListSizes() {}

    /** Iterates recursively of the code items of the code list to compute the size properties. */
    public static void of(CodeList codeList) {
        // TODO: (see CodeList class) the computation of sizes logic should be refactored here
        codeList.computeSizes();
    }

}
