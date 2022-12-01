# Labels

## Labels en DDI

Classe Eno          Attribut                            Expression SpEL (DDI)

AbstractSequence    String label                        getLabelArray(0).getContentArray(0).getStringValue()

Question            String label                        getQuestionTextArray(0).getTextContentArray(0).getText().getStringValue()

Declaration         String label                        getDisplayTextArray(0).getTextContentArray(0).getText().getStringValue()
                    List<String> variableNames
Instruction         idem                                getInstructionTextArray(0).getTextContentArray(0).getText().getStringValue()

Control             String expression                   getCommandCode().getCommandArray(0).getCommandContent()
                    List<String> bindingReferences
Filter              idem                                getIfCondition().getCommandArray(0).getCommandContent()
Variable            idem                                ..getCommandCodeArray(0).getCommandArray(0).getCommandContent()

Control             String (message) label              getDescription().getContentArray(0).getStringValue()
                                                        ou :
                                                        getConstructNameArray(0).getStringArray(0).getStringValue()

Control             String message                      ..getInstructionTextArray(0).getTextContentArray(0).getText().getStringValue()

CodeResponse        String label                        ..getLabelArray(0).getContentArray(0).getStringValue()

CodeItem            String label                        ..getLabelArray(0).getContentArray(0).getStringValue()

=> 3 objets de libellés au niveau de la modélisation DDI :

Description                 Classe Eno              Classes ayant ce type de libellé
Les libellés statiques      Label                   AbstractSequence CodeResponse CodeItem Control(message)
Les libellés dynamiques     DynamicLabel            Question Declaration Instruction
Les expressions VTL         CalculatedExpression    Control(expression) Filter Variable

NB : les libellés dynamiques sont aussi des expressions VTL, c'est juste modélisé différemment en DDI.

NB : au niveau Lunatic, les 3 types de libellés correspondent au même objet.

## Labels dans Lunatic

| Nom de la propriété | Classes Lunatic | 
| --- | --- |
| "label" | Sequence, Subsquence |
| "label" | Input, Textarea, InputNumber, CheckboxBoolean, Datepicker, CheckboxOne, Radio, Dropdown, CheckboxGroup, Table |
| "label" | dans les "options" d'un CheckboxOne |
| "label" | dans les "responses" d'un CheckboxGroup |
| "label" | Declaration (dans les "declarations" d'un objet composant) |
| "control" | Control (dans les "controls" d'un objet composant) |
| "errorMessage" | Control (dans les "controls" d'un objet composant) |
| "value" + "type" | Filter (champ "filter" dans les objets composant) _Cas batard, on devrait avoir une propriété "expression" (ou autre, peu importe, mais utiliser l'objet Label)_ |
| "expression" | Variable (avec "variableType" : "CALCULATED") |
