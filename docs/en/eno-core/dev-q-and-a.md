# Technical notes

_Not updated: please refer to french version._

### My objects mapped by my annotations are null, why?

Mapper uses reflection to access/set model values. Make sure that attributes have their getters and setters. 

In the code, these are implemented using Lombok `@Getter` and `@Setter` annotations. Make sure that these annotations are put over the model class definition.

### "Model collection is null"

```
Cannot invoke "java.util.Collection.addAll(java.util.Collection)" because "modelCollection" is null
java.lang.NullPointerException: Cannot invoke "java.util.Collection.addAll(java.util.Collection)" because "modelCollection" is null
```

The in mapper expects collections to be initialized. Make sure that each attribute that is a list is initialized.

Example:

Not working:

```java
@Getter @Setter
class SomeModelClass extends EnoObject {
	//...
    List<SomeEnoObject> someEnoObjects;
	//...
}
```

Fix:

```java
@Getter @Setter
class SomeModelClass extends EnoObject {
	//...
    List<SomeEnoObject> someEnoObjects = new ArrayList();
	//...
}
```
