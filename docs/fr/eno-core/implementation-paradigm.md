# Paradigme d'implémentation

## Cycle de de vie d'une transformation

```
Fichier d'entrée
   --Deserializer--> Objet d'entrée
   --Mapper--> Objet du modèle Eno
   --InProcessing--> Objet du modèle Eno
   --EnoProcessing--> Objet du modèle Eno
   --Mapper--> Objet de sortie
   --OutProcessing--> Objet de sortie
   --Serializer--> Fichier de sortie
```

## Principe de fonctionnement des mappers

Le principe central d'Eno java est de décrire la logique des relations entre les formats d'entrée et de sortie dans des annotations posées sur un modèle intermédiaire (modèle Eno).

Les mappers vont parcourir les annotations des objets Eno et récupérer les valeurs du format d'entrée ou affecter les valeurs sur le format de sortie en évaluant les expressions dans les annotations.

Les expressions utilisent le [Spring Element Language (SpEL)](./spel-in-eno.md).

Exemple (simplifié) :

```java
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = InputNumber.class)
public class NumericQuestion extends EnoObject {

    @In(format = Format.DDI, expression = "getIDList().get(0).getStringValue()")
    @Out(format = Format.LUNATIC, expression = "setId(#param)")
    private String id;

    @In(format = Format.DDI, expression = "getIfThenElseList.get(0)")
    @Out(format = Format.LUNATIC, expression = "setConditionFilter(#param)")
    private Filter filter;

    @In(format = Format.DDI, expression = "getStatementItemList()")
    @Out(format = Format.LUNATIC, expression = "getDeclarations()")
    private List<Declaration> declarations;

    // Getters/setters...
}
```

Deux types d'annotations :

- pour les formats d'entrée
- pour les formats de sortie

Trois types d'attributs sur le modèle :

- types simples (nombres, chaînes de caractères etc.)
- types complexes (objets Eno)
- listes (pouvant contenir un type simple ou complexe)

Voir la javadoc des annotations et des mappers pour plus de détails.

## Packages

### `parsers`

Classes pour lire/désérialiser les fichiers d'entrée.

### `output`

Classes pour écrire/sérialiser les objets de sortie.

### `model`

Package central contenant les classes du modèle Eno.

Les classes qui correspondent à des concepts proches sont regroupées en sous-packages.

### `annotations`

Annotations destinées à être posées sur les classes du modèle Eno pour définir la logique de mapping et de conversion des objets.

### `mappers`

Les mappers font de l'introspection sur les classes du modèle Eno pour évaluer les expressions définies dans les annotations.

Principe général d'un mapper :

- mapper in : mappe les informations d'un objet d'entrée dans l'objet correspondant du modèle Eno.
- mapper out : mappe les informations d'un objet du modèle Eno dans l'objet de sortie correspondant (selon le format souhaité).

Il est fortement déconseillé de modifier les mappers, mais il est important de comprendre leur fonctionnement.

### `converters`

Dans certains cas, il existe une ambiguïté entre le type de l'objet in/out et le type Eno.

La logique de conversion est portée par des annotations dédiées. Pendant le mapping, quand une ambiguité est rencontrée, le mapper va appeler un converter pour réaliser la conversion.

Cardinalités possibles :

```
In object (1 *) 
           \
            \ 
            (1 *) Eno object (1) 
                              \
                               \
                              (1 *)  Out object
```

### `reference`

Classes contenant une/des maps à plat pour référencer différents objets :

- Classes d'index pour faciliter le mapping.
- Classes catalogues pour faciliter les traitements.

### `processing`

Modification d'un objet Eno ou d'une objet de sortie.

Les traitements réalisés peuvent :

- Être d'ordre technique pour compléter la tâche du mapper : en fonction du format, la disposition de certains éléments ne permet pas d'effectuer le mapping directement via les annotations.

- Correspondre à un traitement métier. Voir la documentations des [paramètres et des traitements associés](./eno-parameters.md).

### `parameter`

Objets contenant les différents paramètres métier.

### `utils`

Utilitaires.

### `exceptions`

Exceptions d'ordre technique ou métier définies par Eno.
