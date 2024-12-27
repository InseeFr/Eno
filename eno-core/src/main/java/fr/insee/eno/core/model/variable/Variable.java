package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.exceptions.business.IllegalPoguesElementException;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;
import fr.insee.pogues.model.NumericDatatypeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.POGUES, type = fr.insee.pogues.model.VariableType.class)
@Context(format = Format.DDI, type = fr.insee.ddi.lifecycle33.logicalproduct.VariableType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.variable.VariableType.class)
public abstract class Variable extends EnoObject {

    public enum CollectionType {COLLECTED, CALCULATED, EXTERNAL}

    /** Variable collection type. */
    private CollectionType collectionType;

    /** Variables doesn't have an identifier in Lunatic. */
    @Pogues("getId()")
    @DDI("getIDArray(0).getStringValue()")
    private String id;

    /** In DDI, when a variable is used in a dynamic label, it is referred to through its reference (not by its id),
     * surrounded with a special character.
     * Calculated variables have it in the 'Binding' in their 'VariableRepresentation'. */
    private String reference;
    // TODO: see pairwise DDI with variable 'l0v32sjd': mistake or actual case?

    /** Variable name. */
    @Pogues("getName()")
    @DDI("!getVariableNameList().isEmpty() ? getVariableNameArray(0).getStringArray(0)?.getStringValue() : null")
    @Lunatic("setName(#param)")
    private String name;

    /** Measurement unit (for numeric variables). */
    @Pogues("T(fr.insee.pogues.model.DatatypeTypeEnum).NUMERIC.equals(#this.getDatatype().getTypeName()) ? " +
            "T(fr.insee.eno.core.model.variable.Variable).mapPoguesUnit(#this.getDatatype()) : null")
    @DDI("getVariableRepresentation()?.getValueRepresentation()?.getMeasurementUnit()?.getStringValue()")
    private String unit;

    /** A unit can be either fixed or dynamic. */
    @Pogues("T(fr.insee.pogues.model.DatatypeTypeEnum).NUMERIC.equals(#this.getDatatype().getTypeName()) ? " +
            "getDatatype().isIsDynamicUnit() != null ? getDatatype().isIsDynamicUnit() : false : null") // TODO: extract this double ternary to simplify
    @DDI("getVariableRepresentation()?.getValueRepresentation()?.getMeasurementUnit()?.getControlledVocabularyName() != null ? " +
            "getVariableRepresentation().getValueRepresentation().getMeasurementUnit().getControlledVocabularyName() == 'personalizedUnit' : false")
    private Boolean isUnitDynamic;

    /** Method to convert an Eno-model CollectionType object (from the enum class)
     * to the value expected in Lunatic-Model. */
    public static VariableTypeEnum lunaticCollectionType(CollectionType enoCollectionType) {
        return VariableTypeEnum.valueOf(enoCollectionType.name()); // (For now names coincide.)
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
