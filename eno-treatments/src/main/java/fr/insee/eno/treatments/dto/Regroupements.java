package fr.insee.eno.treatments.dto;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Contain the regroupement list of variables needed to be regrouped in a same page
 */
public class Regroupements {
    private final List<Regroupement> regroupementsList;

    /**
     * @param regroupementsList regroupement list
     */
    public Regroupements(@NonNull List<Regroupement> regroupementsList) {
        this.regroupementsList = regroupementsList;
        boolean notEnoughRegroupements = regroupementsList.stream()
                .anyMatch(regroupement -> regroupement.getVariables().size() < 2);

        if(notEnoughRegroupements) {
            throw new IllegalArgumentException("Deux variables au minimum doivent être définies pour un regroupement");
        }
        regroupementsList.stream()
                .flatMap(regroupement -> regroupement.getVariables().stream())
                .forEach(this::checkVariableExistsOnlyInOneRegroupement);
    }

    /**
     * retrieve the regroupement associated with a variable
     * @param variable variable to find
     * @return the regroupement associated to the variable if any
     */
    public Optional<Regroupement> getRegroupementForVariable(String variable) {
        return regroupementsList.stream()
                .filter(regroupement -> regroupement.hasVariable(variable))
                .findFirst();
    }

    /**
     * @return size of the regroupement list
     */
    public int count() {
        return regroupementsList.size();
    }

    /**
     *
     * @return true if list is empty, false otherwise
     */
    public boolean isEmpty() {
        return regroupementsList.isEmpty();
    }

    /**
     * Check if a variable exist in multiple regroupements (should not happen)
     * @param variable variable to find
     */
    private void checkVariableExistsOnlyInOneRegroupement(String variable) {
        long variableAppearsInRegroupementsCount = regroupementsList.stream()
                .filter(regroupement -> regroupement.hasVariable(variable))
                .count();

        if (variableAppearsInRegroupementsCount > 1) {
            throw new IllegalArgumentException("Same variable cannot appear more than once in regroupements");
        }
    }
}
