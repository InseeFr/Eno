# Usage of Spring Expression Language in Eno

This page provides documentation of the parameters that can be used in annotations on the Eno model
(these parameters are hardcoded in mappers).

The second part is a tutorial of the SpEL syntax and corresponding functionalities 
that are used in the different annotations. 
This tutorial provides a comprehensive guide for the contributor 
to the project.

## Eno parameter keywords

- ``param``: used in out mapper to set property values
- ``index``: used in DDI mapper to reference the map that contains DDI objects

## SpEL functionalities / syntax

_Reference:_ 

https://docs.spring.io/spring-framework/docs/current/reference/html/core.html

### SpEL keywords

- ``#this``
- ``#root``

### Logical operators

- ``!`` (not)
- ``==``, ``!=``
- ``<``, ``<=``, ``>``, ``>=``
- ``and``, ``or``
- ``instanceof``

### Projection

``someList.![<condition on each element using #this>]``

### Selection

``someList.?[<condition on each element using #this>]``

### Safe navigation operator

``somethingThatMightBeNull?.getSomething()``

### Ternary operator

``<condition> ? <value if true> : <value if false>``

### Elvis operator

``<expression> ?: <value if null>``

### Types

``T()``

### Enums / static method calls

``T(fully.qualified.name.SomeEnum).SOME_VALUE``
``T(fully.qualified.name.SomeEnum).valueOf('SOME_VALUE')``
``T(fully.qualified.name.SomeClass).someStaticMethod(...)``

### Inheritance

````java
public class Instruction {
    
    @DDI(contextType = InstructionType.class,
            field = "getInstructionTextArray(0).getTextContentArray(0).getText().getStringValue()")
    String label;
    
}
````

Here, `getTextContentArray(0)` returns a `TextContentType` object. 
The `TextContentType` class does not have a `getText()` method.
Yet, in this context, we know that the object we will have at runtime 
is a `LiteralTextType` (which is a superclass of `TextContentType`) that has this method.

