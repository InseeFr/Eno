package fr.insee.eno.core.processing.out.steps.lunatic.loop;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

/**
 * La logique des filtres combinée à celles des boucles rend le calcul du filtre niveau "Racine de la boucle (ou du rond-point) " complexe.
 * Après analyse du fonctionnement de Lunatic et amélioration de son fonctionnement,
 * Nous pouvons désormais avoir les filtres qui fonctionnent dans tous les cas.
 *
 * Pseudo algo:
 * Pour chacune des filtres du questionnaire
 *      Si l'élément "début" de la boucle (ou du rond-point) est strictement inclus entre l'élément début et fin de filtre (strictement -> doit être différente)
 *      Alors, on ajoute ce filtre dans la condition
 *      Sinon, on ne fait rien
 *
 * Dans le cas où l'élément début de la boucle (ou du rond-point) et l'élément début du filtre coïncide,
 * alors le filtre n'apparaitra que dans les éléments enfant de la boucle, et pas dans la condition du filtre de la boucle elle-même.
 * C'est suffisant pour Lunatic pour filtrer les éléments si nécessaire.
 *
 * Cela permet d'éviter les erreurs de scope au niveau de la boucle, car aujourd'hui, il y a des questionnaires dont le filtre de la boucle contiennent des formules VTL
 * qui ne sont valides qu'au niveau individu.
 * Pourquoi ? Parce qu'on pensait qu'il fallait mettre toutes les formules de filtres des composants enfant de la boucle au niveau du filtre de composant Boucle (ou rond-point)
 */
public class LunaticLoopFilter {

    private LunaticLoopFilter(){
        throw new IllegalStateException("Utility class");
    }


    public static ConditionFilterType computeConditionFilter(Loop enoLoop, EnoQuestionnaire enoQuestionnaire) {

        List<Filter> filtersForLoop = enoQuestionnaire.getFilters().stream()
                .filter(filter -> isFilterIncludingLoop(filter, enoLoop))
                .toList();

        if(filtersForLoop.isEmpty()) {
            ConditionFilterType defaultConditionFilterType = new ConditionFilterType();
            defaultConditionFilterType.setValue("true");
            defaultConditionFilterType.setType(LabelTypeEnum.VTL);
            return defaultConditionFilterType;
        }
        return computeLoopFilterExpression(filtersForLoop);
    }

    /**
     * Compute if the filter include strictly the Loop.
     * i.e if start and the end element of loop are inside strictly loop
     * @param filter: enoFilter to check
     * @param enoLoop: enoLoop
     * @return boolean
     */
    private static boolean isFilterIncludingLoop(Filter filter, Loop enoLoop){
        String occurrenceFilterId = enoLoop.getOccurrenceFilterId();


        // do not include occurrence filter
        if(occurrenceFilterId != null && occurrenceFilterId.equals(filter.getId())) return false;
        String startLoopElementId = enoLoop.getLoopScope().getFirst().getId();
        String endLoopElementId = enoLoop.getLoopScope().getLast().getId();

        String startFilterElementId = filter.getFilterScope().getFirst().getId();
        String endFilterElementId = filter.getFilterScope().getLast().getId();

        // prevent scope calculating error
        if(startLoopElementId.equals(startFilterElementId) || endLoopElementId.equals(endFilterElementId)) return false;

        boolean isStartOfLoopInsideFilter = false;
        boolean isEndOfLoopInsideFilter = false;
        for(StructureItemReference structureItemReference : filter.getFilterScope()){
            String referenceId = structureItemReference.getId();
            if(startLoopElementId.equals(referenceId)) isStartOfLoopInsideFilter = true;
            if(endLoopElementId.equals(referenceId)) isEndOfLoopInsideFilter = true;
            if(isStartOfLoopInsideFilter && isEndOfLoopInsideFilter) return true;
        }
        return false;
    }


    /**
     * Join by and logic all found expression and filter
     * @param loopStructureFilters instance of EnoFilter
     * @return Lunatic ConditionFilter with bindingDependencies
     */
    private static ConditionFilterType computeLoopFilterExpression(List<Filter> loopStructureFilters) {
        ConditionFilterType loopFilter = new ConditionFilterType();
        String expression = VtlSyntaxUtils.joinByANDLogicExpression(
                loopStructureFilters.stream()
                        .map(Filter::getExpression)
                        .map(CalculatedExpression::getValue)
                        .toList()
        );
        loopFilter.setValue(expression);
        loopFilter.setType(LabelTypeEnum.VTL);
        List<String> bindingDependencies = loopStructureFilters.stream()
                .flatMap(filter -> filter.getExpression().getBindingReferences().stream().map(BindingReference::getVariableName))
                .distinct()
                .toList();
        loopFilter.setBindingDependencies(bindingDependencies);
        return loopFilter;
    }

}
