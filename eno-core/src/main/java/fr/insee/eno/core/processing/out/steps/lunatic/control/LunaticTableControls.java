package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.lunatic.model.flat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LunaticTableControls implements LunaticFormatControl<Table> {

    private final EnoParameters.Language language;

    public LunaticTableControls(EnoParameters.Language language) {
        this.language = language;
    }

    @Override
    public List<ControlType> generateFormatControls(Table table) {
        List<ControlType> controls = new ArrayList<>();
        for(BodyLine bodyLine : table.getBodyLines()) {
            controls.addAll(getFormatControlsForBodyCells(bodyLine.getBodyCells(), language));
        }
        return controls;
    }

    static List<ControlType> getFormatControlsForBodyCells(List<BodyCell> bodyCells, EnoParameters.Language language) {
        List<ControlType> controls = new ArrayList<>();

        bodyCells.stream()
                .filter(Objects::nonNull)
                .forEach(bodyCell -> {
                    if(ComponentTypeEnum.INPUT_NUMBER.equals(bodyCell.getComponentType())) {
                        controls.addAll(
                                LunaticInputNumberControl.getFormatControlsFromInputNumberAttributes(
                                        bodyCell.getId(), (Double) bodyCell.getMin(), (Double) bodyCell.getMax(),
                                        bodyCell.getDecimals().intValue(), bodyCell.getResponse().getName())
                        );
                        return;
                    }

                    if(ComponentTypeEnum.DATEPICKER.equals(bodyCell.getComponentType())) {
                        new LunaticDatepickerControl(language).getFormatControlFromDatepickerAttributes(
                                bodyCell.getId(), (String) bodyCell.getMin(), (String) bodyCell.getMax(),
                                bodyCell.getDateFormat(), bodyCell.getResponse().getName())
                                .ifPresent(controls::add);
                    }
                });
        return controls;
    }

}
