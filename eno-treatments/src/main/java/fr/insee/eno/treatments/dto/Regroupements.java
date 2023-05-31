package fr.insee.eno.treatments.dto;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public class Regroupements {
    private final List<Regroupement> regroupementsList;

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

    public Optional<Regroupement> getRegroupementForVariable(String variable) {
        return regroupementsList.stream()
                .filter(regroupement -> regroupement.hasVariable(variable))
                .findFirst();
    }

    public int count() {
        return regroupementsList.size();
    }

    public boolean isEmpty() {
        return regroupementsList.isEmpty();
    }

    private void checkVariableExistsOnlyInOneRegroupement(String variable) {
        long variableAppearsInRegroupementsCount = regroupementsList.stream()
                .filter(regroupement -> regroupement.hasVariable(variable))
                .count();

        if (variableAppearsInRegroupementsCount > 1) {
            throw new IllegalArgumentException("Same variable cannot appear more than once in regroupements");
        }
    }
}
