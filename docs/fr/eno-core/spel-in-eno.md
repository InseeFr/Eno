# Utilisation du Spring Expression Language dans Eno

La syntaxe du SpEL est proche de la syntaxe Java, avec quelques éléments de syntaxe particuliers.

Cette page décrit les mot-clés qui peuvent être utilisés dans les annotations sur le modèle Eno.

La deuxième section est un résumé des éléments de syntaxe SpEL qui sont utilisés dans les annotations. 

## Mot-clés définis dans Eno

- ``param``: Utilisé dans les formats de sortie pour récupérer la valeur de l'attribut du modèle.
- ``index``: Utilisé dans le mapper DDI pour accéder à l'index DDI (map à plat contenant tous les objets du DDI mappé).

## Fonctionnalités / syntaxe SpEL

_Référence:_ 

https://docs.spring.io/spring-framework/docs/current/reference/html/core.html

### Mot-clés SpEL

- ``#this``
- ``#root``

### Opérateurs logiques

- ``!`` (not)
- ``==``, ``!=``
- ``<``, ``<=``, ``>``, ``>=``
- ``and``, ``or``
- ``instanceof``

### Projection

Équivalent du `.map()` dans un stream sur une liste : permet de modifier les éléments d'une liste.

``someList.![<expression using #this>]``

### Sélection

Équivalent du `.filter()` dans un stream sur une liste : permet de filtrer les élements d'une liste.

``someList.?[<filter condition using #this>]``

### Safe navigation operator

Permet de chaîner des méthodes sans générer d'exception si l'objet est `null`.

``somethingThatMightBeNull?.getSomething()``

### Expression ternaire

``<condition> ? <value if true> : <value if false>``

### Elvis operator

Permet d'affecter une valeur par défaut si le résultat d'une expression peut être `null`.

``<expression> ?: <value if null>``

### Types

Syntaxe pour faire référence à une classe.

``T(fully.qualified.ClassName)``

### Enums / static method calls

``T(fully.qualified.name.SomeEnum).SOME_VALUE``
``T(fully.qualified.name.SomeEnum).valueOf('SOME_VALUE')``
``T(fully.qualified.name.SomeClass).someStaticMethod(...)``

### Chaîne de caractères

Les guillements simples `'` sont autorisés pour les chaines de caractères.

```java
String someExpression = "'some string'";
String sameExpression = "\"some string\"";
```
