package fr.insee.eno.core.model.label;

/** Interface to allow polymorphism between label objects.
 * @see Label */
public interface EnoLabel {

    String getValue();
    void setValue(String value);

}
