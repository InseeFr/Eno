package fr.insee.eno.core.model.lunatic;

/** Define an interface for Lunatic resizing objects,
 * so that we can use something a bit less vague than Object,
 * and we can make some polymorphism. */
public interface LunaticResizingInterface {
    String getName();
}
