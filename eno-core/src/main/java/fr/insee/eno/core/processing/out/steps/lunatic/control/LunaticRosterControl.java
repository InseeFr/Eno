package fr.insee.eno.core.processing.out.steps.lunatic.control;

import fr.insee.lunatic.model.flat.ControlContextType;
import fr.insee.lunatic.model.flat.ControlType;
import fr.insee.lunatic.model.flat.RosterForLoop;

import java.util.List;

public class LunaticRosterControl implements LunaticFormatControl<RosterForLoop> {

    @Override
    public List<ControlType> generateFormatControls(RosterForLoop roster) {
        List<ControlType> controls = LunaticTableControls.getFormatControlsForBodyCells(roster.getComponents());

        // The format controls of a roster for loop (dynamic table) are row-level controls
        controls.forEach(control -> control.setType(ControlContextType.ROW));

        return controls;
    }

}
