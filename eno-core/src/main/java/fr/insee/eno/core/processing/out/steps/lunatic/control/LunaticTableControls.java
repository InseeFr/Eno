package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.lunatic.model.flat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LunaticTableControls implements LunaticFormatControl<Table> {

    @Override
    public List<ControlType> generateFormatControls(Table table) {
        List<ControlType> controls = new ArrayList<>();
        for(BodyLine bodyLine : table.getBodyLines()) {
            controls.addAll(getFormatControlsForBodyCells(bodyLine.getBodyCells()));
        }
        return controls;
    }

    static List<ControlType> getFormatControlsForBodyCells(List<BodyCell> bodyCells) {
        List<ControlType> controls = new ArrayList<>();

        bodyCells.stream()
                .filter(Objects::nonNull)
                .forEach(bodyCell -> {
                    if(ComponentTypeEnum.INPUT_NUMBER.equals(bodyCell.getComponentType())) {
                        controls.addAll(
                                LunaticInputNumberControl.getFormatControlsFromInputNumberAttributes(
                                        bodyCell.getId(), bodyCell.getMin(), bodyCell.getMax(),
                                        bodyCell.getDecimals().intValue(), bodyCell.getResponse().getName())
                        );
                    }

                    // TODO: Implements date pickers components correctly in tables/rosters
                    /*
                    if(ComponentTypeEnum.DATEPICKER.equals(bodyCell.getComponentType())) {
                        controls.add(
                                getFormatControlFromDatepickerAttributes(bodyCell.getId(), bodyCell.getMin(), bodyCell.getMax(),
                                        bodyCell.getDecimals().intValue(), bodyCell.getResponse().getName()));
                    }*/
                });
        return controls;
    }

}
