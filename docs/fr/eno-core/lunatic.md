# Lunatic

:arrow_right: _documentation à déplacer dans Lunatic-Model_

_(en cours de rédaction)_

## Modèle de questionnaire Lunatic

### Labels

L'objet `Label` correspond aux libellés (statiques ou dynamiques) qui figurent dans les séquences, questions etc. et aux expressions VTL qui figurent dans les contrôles, filtres etc.

L'objet label dispose d'un attribut `type` qui indique si la valeur du label doit être interprétée comme du markdown (MD), du VTL, ou les deux.

| Nom de la propriété | Type   | Classes Lunatic                                                                                                                                                  | 
|---------------------|--------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| "label"             | VTL/MD | Sequence, Subsquence                                                                                                                                             |
| "label"             | VTL/MD | Input, Textarea, InputNumber, CheckboxBoolean, Datepicker, CheckboxOne, Radio, Dropdown, CheckboxGroup, Table                                                    |
| "label"             | VTL/MD | Options (dans les "options" d'un CheckboxOne)                                                                                                                              |
| "label"             | VTL/MD | ResponsesCheckboxGroup (dans les "responses" d'un CheckboxGroup)                                                                                                                          |
| "label"             | VTL/MD | Declaration (dans les "declarations" d'un objet composant)                                                                                                       |
| "control"           | VTL    | Control (dans les "controls" d'un objet composant)                                                                                                               |
| "errorMessage"      | VTL/MD | Control (dans les "controls" d'un objet composant)                                                                                                               |
| "value" + "type"    | VTL    | Filter (champ "filter" dans les objets composant) _Cas batard, on devrait avoir une propriété "expression" (ou autre, peu importe, mais utiliser l'objet Label)_ |
| "expression"        | VTL    | Variable (avec "variableType" : "CALCULATED")                                                                                                                    |
_Note : Les types de labels sont valorisés dans Eno._

Implémenté dans Eno v2 (:warning: pas dans la branche main actuellement) :

`src/main/resources/xslt/transformations/ddi2lunatic-xml/ddi2lunatic-xml-fixed.xsl`
