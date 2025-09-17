package fr.insee.eno.ws.controller;

import fr.insee.eno.parameters.Context;
import fr.insee.eno.parameters.Mode;
import fr.insee.eno.parameters.OutFormat;
import org.junit.Assert;
import org.junit.Test;

public class ParametersFileNameTest {

    @Test
    public void businessCAWIXformsCase() {
        // Given
        Context context = Context.BUSINESS;
        Mode mode = Mode.CAWI;
        OutFormat outFormat = OutFormat.XFORMS;
        // When + Then
        Assert.assertEquals("eno-parameters-BUSINESS-CAWI-XFORMS.xml",
                ParametersController.parametersFileName(context, mode, outFormat));
    }

    @Test
    public void defaultDDI_nullMode() {
        // Given
        Context context = Context.DEFAULT;
        OutFormat outFormat = OutFormat.DDI;
        // When + Then
        Assert.assertEquals("eno-parameters-DEFAULT-DDI.xml",
                ParametersController.parametersFileName(context, null, outFormat));
    }

}
