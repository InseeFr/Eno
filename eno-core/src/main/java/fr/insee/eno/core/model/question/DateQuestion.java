package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.Datepicker;
import lombok.Getter;
import lombok.Setter;

/**
 * Eno model class to represent date questions.
 * In DDI, it corresponds to a QuestionItem object.
 * In Lunatic, it corresponds to the Datepicker component.
 */
@Getter
@Setter
public class DateQuestion extends SingleResponseQuestion {

    /**
     * Minimum date value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain() != null ? " +
                    "getResponseDomain().getRangeArray(0)?.getMinimumValue()?.getStringValue() : " +
                    "#index.get(#this.getResponseDomainReference().getIDArray(0).getStringValue())" +
                    ".getRangeArray(0)?.getMinimumValue()?.getStringValue()")
    @Lunatic(contextType = Datepicker.class, field = "setMin(#param)")
    private String minValue;

    /**
     * Maximum date value allowed.
     */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain() != null ? " +
            "getResponseDomain().getRangeArray(0)?.getMaximumValue()?.getStringValue() : " +
            "#index.get(#this.getResponseDomainReference().getIDArray(0).getStringValue())" +
            ".getRangeArray(0)?.getMaximumValue()?.getStringValue()")
    @Lunatic(contextType = Datepicker.class, field = "setMax(#param)")
    private String maxValue;

    /**
     * Date format.
     * This property is a String in both DDI and Lunatic.
     */
    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain() != null ? " +
            "getResponseDomain().getDateFieldFormat().getStringValue() : " +
            "#index.get(#this.getResponseDomainReference().getIDArray(0).getStringValue())" +
            ".getDateFieldFormat().getStringValue()")
    @Lunatic(contextType = Datepicker.class, field = "setDateFormat(#param)")
    private String format;

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic(contextType = Datepicker.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "DATEPICKER";
}
