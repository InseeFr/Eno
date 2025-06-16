package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.response.Response;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DetailResponseTest {

    private fr.insee.eno.core.model.response.DetailResponse enoDetailResponse;
    private fr.insee.lunatic.model.flat.DetailResponse lunaticDetailResponse;

    private final LunaticMapper lunaticMapper = new LunaticMapper();

    @BeforeEach
    void createDetailResponseObjects() {
        enoDetailResponse = new fr.insee.eno.core.model.response.DetailResponse();
        lunaticDetailResponse = new fr.insee.lunatic.model.flat.DetailResponse();
    }

    @Test
    void mapDetailResponse() {
        //
        enoDetailResponse.setResponse(new Response());
        enoDetailResponse.getResponse().setVariableName("FOO_DETAIL");
        enoDetailResponse.setLabel(new Label());
        enoDetailResponse.getLabel().setValue("\"Please specify\"");
        enoDetailResponse.setMaxLength(BigInteger.valueOf(5));
        //
        lunaticMapper.mapEnoObject(enoDetailResponse, lunaticDetailResponse);
        //
        assertEquals("FOO_DETAIL", lunaticDetailResponse.getResponse().getName());
        assertEquals("\"Please specify\"", lunaticDetailResponse.getLabel().getValue());
        assertEquals(LabelTypeEnum.VTL_MD, lunaticDetailResponse.getLabel().getType());
        assertEquals(BigInteger.valueOf(5), lunaticDetailResponse.getMaxLength());
    }

}