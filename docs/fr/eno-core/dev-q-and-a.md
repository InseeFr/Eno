# Notes techniques

### Une annotation est définie sur un attribut, mais sa valeur reste `null`

Les mappers utilisent l'introspection pour accéder ou modifier les objets du modèle. Vérifier que les getters et setters sont définis pour l'attribut en question.

En général, les getters et setters sont implémentés via les annotations `@Getter` and `@Setter` de Lombok. Vérifier si ces annotations sont présentes au niveau de la classe.

### "Model collection is null"

```
Cannot invoke "java.util.Collection.addAll(java.util.Collection)" because "modelCollection" is null
java.lang.NullPointerException: Cannot invoke "java.util.Collection.addAll(java.util.Collection)" because "modelCollection" is null
```

Les mappers supposent que les listes sont initialisées. Vérifier que les attributs qui sont une liste sont initialisés à la création de la classe.

Incorrect :

```java
@Getter @Setter
class SomeModelClass extends EnoObject {
	//...
    List<SomeEnoObject> someEnoObjects;
	//...
}
```

Correct :

```java
@Getter @Setter
class SomeModelClass extends EnoObject {
	//...
    List<SomeEnoObject> someEnoObjects = new ArrayList();
	//...
}
```

Note : on pourrait optimiser l'espace mémoire en modifiant les mappers pour initialiser les listes uniquement quand c'est nécessaire, ou modifier les getters pour les listes (exemple en-dessous). Peut-être plus tard (ça semble peu utile pour l'instant).

```java
@Getter @Setter
class SomeModelClass extends EnoObject {
	//...
    List<SomeEnoObject> someEnoObjects;

    // Surcharge du getter par défaut pour initialiser la liste à la demande :
    public List<SomeEnoObject> getSomeEnoObjects() {
        if (someEnoObjects == null) 
            someEnoObjects = new ArrayList<>();
        return someEnoObjects;
    }

	//...
}
```

### Certaines expressions SpEL sont invalides à la compilation

Exemple :

```java
public class Instruction {
    
    @DDI(contextType = InstructionType.class,
            field = "getInstructionTextArray(0).getTextContentArray(0).getText().getStringValue()")
    String label;
    
}
```

Ici, `getTextContentArray(0)` renvoie un objet `TextContentType`. 
La classe `TextContentType` n'a pas de méthode `getText()`.
Cependant, dans ce contexte, et conformément à la modélisation Insee des questionnaires, on sait qu'on aura au runtime un objet `LiteralTextType` (qui hérite de `TextContentType`). (Garder à l'esprit que les expressions des annotations seront évaluées au runtime.)
