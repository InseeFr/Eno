package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.InputNumber;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.pogues.model.NumericDatatypeType;
import fr.insee.pogues.model.QuestionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Eno model class to represent numeric questions, that is to say questions where data collected can only be a
 * numeric value.
 * In DDI, it corresponds to a QuestionItem.
 * In Lunatic, it corresponds to the InputNumber component.
 */
@Getter
@Setter
@Context(format = Format.POGUES, type = QuestionType.class)
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = InputNumber.class)
public class NumericQuestion extends SingleResponseQuestion {

    /**
     * Minimum value allowed.
     */
    @Pogues("getResponse().getFirst().getDatatype().getMinimum().doubleValue()")
    @DDI("getResponseDomain()?.getNumberRangeList()?.get(0)?.getLow()?.getStringValue() != null ? " +
            "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getLow().getStringValue()) : null")
    @Lunatic("setMin(#param)")
    Double minValue;

    /**
     * Maximum value allowed.
     */
    @Pogues("getResponse().getFirst().getDatatype().getMaximum().doubleValue()")
    @DDI("getResponseDomain()?.getNumberRangeList()?.get(0)?.getHigh()?.getStringValue() != null ? " +
            "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getHigh().getStringValue()) : null")
    @Lunatic("setMax(#param)")
    Double maxValue;

    /**
     * Maximum number of decimals authorized during data collected.
     * In Pogues or DDI, a null value is equivalent to 0.
     * Represented as a BigInteger since it is the type used in both DDI and Lunatic for this property.
     */
    @Pogues("getResponse().getFirst().getDatatype().getDecimals() ?: T(java.math.BigInteger).valueOf('0')")
    @DDI("getResponseDomain()?.getDecimalPositions() ?: T(java.math.BigInteger).valueOf('0')")
    @Lunatic("setDecimals(#param)")
    BigInteger numberOfDecimals;

    /** Unit associated to the collected value.
     * In DDI, this information is not available (even through references) in 'QuestionItem' object.
     * It is mapped in Variable class and moved here by a processing. */
    @Pogues("T(fr.insee.eno.core.model.question.NumericQuestion).mapPoguesUnit(" +
            "#this.getResponse().getFirst().getDatatype())")
    @Lunatic("setUnit(#param)")
    DynamicLabel unit;

    /** A unit can be either fixed or dynamic. */
    @Pogues("getResponse().getFirst().getDatatype().isIsDynamicUnit() != null ? " +
            "getResponse().getFirst().getDatatype().isIsDynamicUnit() : false")
    private Boolean isUnitDynamic;

    /** Indicates whether the response is mandatory for this component. */
    @DDI("getResponseDomain()?.getResponseCardinality()?.getMinimumResponses() != null ? " +
            "getResponseDomain().getResponseCardinality().getMinimumResponses().intValue() > 0 : false")
    @Lunatic("setMandatory(#param)")
    boolean mandatory; // TODO: should probably be removed here

    /**
     * Creates a dynamic label object that fits the 'unit' property of numeric questions with the label value given.
     * A number question doesn't necessarily have a unit, so the result can be null if the value given is null.
     * In Lunatic, unit labels must be of type 'VTL', so the type of the returned dynamic label is set to this value.
     * @param value Label value. Can be null.
     * @return Dynamic label object with given value. null if the given value is null.
     */
    public static DynamicLabel createUnit(String value) {
        if (value == null)
            return null;
        DynamicLabel label = new DynamicLabel();
        label.setValue(value);
        label.setType(LabelTypeEnum.VTL.value());
        return label;
    }

    // TODO: actual unit should be saved in the pogues questionnaire file
    // currently Pogues is dependent of some RMeS web-service to get units
    // Eno transformation don't rely on a external API, so the conversion is duplicated and hard-coded here for now
    public static String mapPoguesUnit(NumericDatatypeType poguesNumericDatatype) {
        String poguesUnit = poguesNumericDatatype.getUnit();
        // If it is a dynamic unit, the VTL expression is stored in the unit property
        if (Boolean.TRUE.equals(poguesNumericDatatype.isIsDynamicUnit()))
            return poguesUnit;
        // Otherwise it is a fixed unit represented as a URI thing
        return switch (poguesUnit) {
            case null -> null;
            case "" -> null;
            case "http://id.insee.fr/unit/euro" -> "€";
            case "http://id.insee.fr/unit/keuro" -> "k€";
            case "http://id.insee.fr/unit/percent" -> "%";
            case "http://id.insee.fr/unit/heure" -> "heures";
            case "http://id.insee.fr/unit/jour" -> "jours";
            case "http://id.insee.fr/unit/semaine" -> "semaines";
            case "http://id.insee.fr/unit/mois" -> "mois";
            case "http://id.insee.fr/unit/annee" -> "années";
            case "http://id.insee.fr/unit/an" -> "ans";
            case "http://id.insee.fr/unit/watt" -> "W";
            case "http://id.insee.fr/unit/kilowatt" -> "kW";
            case "http://id.insee.fr/unit/megawarr" -> "MW";
            case "http://id.insee.fr/unit/megawattheurepcs" -> "MWh PCS";
            case "http://id.insee.fr/unit/megawattheure" -> "MWh";
            case "http://id.insee.fr/unit/megawattpcs" -> "MW PCS";
            case "http://id.insee.fr/unit/kilowattthermique" -> "kWth";
            case "http://id.insee.fr/unit/kg" -> "kg";
            case "http://id.insee.fr/unit/tonne" -> "tonnes";
            case "http://id.insee.fr/unit/tonnematiereseche" -> "tonnes matières sèches";
            case "http://id.insee.fr/unit/degrecelsius" -> "°C";
            case "http://id.insee.fr/unit/bar" -> "bars";
            case "http://id.insee.fr/unit/litre" -> "litres";
            case "http://id.insee.fr/unit/metre" -> "mètres";
            case "http://id.insee.fr/unit/centimetre" -> "centimètres";
            case "http://id.insee.fr/unit/metrecarre" -> "mètres carrés";
            default -> throw new IllegalPoguesElementException("Unknown unit: " + poguesUnit);
        };
    }

}
