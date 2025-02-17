package fr.insee.eno.core.model.response;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ArbitraryType;
import fr.insee.pogues.model.ResponseType;
import lombok.Getter;
import lombok.Setter;

/** Arbitrary response for suggesters.
 * This information is not present in DDI. */
@Getter
@Setter
@Context(format = Format.POGUES, type = ResponseType.class)
@Context(format = Format.LUNATIC, type = ArbitraryType.class)
public class ArbitraryResponse extends EnoObject {

    @Pogues("#this")
    @Lunatic("setResponse(#param)")
    private Response response;

}
