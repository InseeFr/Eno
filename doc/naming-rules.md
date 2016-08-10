# Naming rules for Eno

This document describes the naming rules for the different components found in Eno:
* Files and folders
* Namespaces
* XML elements and attributes
* XSLT variables and parameters
* XSLT modes, templates and functions
* XForms components (instances, elements, events, submissions, messages, etc.)
* Ant targets
* Ant properties and parameters
* Eno specific components
* CSS classes

Constraints:
* constraint on modes and getters
* constraint on XForms instance names (imposed by Orbeon)
* XSLT modes should be named with the name of the template of function to which they correspond.

## General rules

All components are names with English terms or combinations of terms.

Except stated otherwise in the specific rules below, all names are formed with English terms or combination of terms in lower cases, with hyphens as separators.

Verbs will be used for functions and equivalents: Ant targets, XSLT templates

TODO Give examples.

## Specific rules

### Files and folders

Folders should be name with simple terms or tokens, in lower cases containing no spaces. Use Maven directory structure when possible.
Except stated otherwise, files should be named with simple or compound names; hyphens will be used as separators instead of spaces. All names will be in lower cases.

Specific rules for transformations: the pattern is 'in'2'out'.xsl, where 'in' and 'out' refer to formats of the input files. 'in' and 'out' can be, respectively:
* ddi, xml, fods
* xml, xsl, fr (for Form Runner)

TODO: add something on the use of '-fixed'.

All extensions should correspond to the format of the files, except temporary files that should have extension '.tmp'.

### XML elements and attributes

XML elements are named with nouns (simple or compound) and attributes with adjectives.

XML elements are in upper camel case (e.g. `SendContainer`) and attributes in lower camel case (e.g. `genericName`).

###  XSLT variables and parameters

Differences in scope between parameters and variables imply that more attention should be given to the naming of the parameters.

### XSLT modes, templates and functions

When it corresponds to a template or function, a mode should be named like this template (see constraint). By extension, a mode shall be named with a verb even when it has no associated template. Modes and corresponding templates and functions have qualified names using one of the eno* names.

### Ant properties

Following the usual convention, Ant properties can have structured names where a dot is used as separator.

Examples: `questionnaire.path`, `temp.home`.

### Eno specific components

Eno has two specific components: getters and drivers.

Getters are named like functions, with a 'get-' prefix (e.g. `get-help-instruction`).

Drivers correspond to XML elements and should be named accordingly. When the function of the driver is to create an element in a namepace associated to the target format, the driver should be named with the qualified name of the target element, where the colon is replaced by a hyphen (e.g. `xf-output`).
