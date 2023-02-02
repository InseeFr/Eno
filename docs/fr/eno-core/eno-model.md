# Modèle Eno

Rappel : principe

```
Format d'entrée 
    --Deserializer--> Objet d'entrée
    --Mapper--> Objet du modèle Eno
    --Mapper--> Objet de sortie
    --Serializer--> Format de sortie
```

## Labels

### Labels en DDI

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

### Labels dans Lunatic

| Nom de la propriété | Type   | Classes Lunatic                                                                                                                                                  | 
|---------------------|--------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| "label"             | VTL/MD | Sequence, Subsquence                                                                                                                                             |
| "label"             | VTL/MD | Input, Textarea, InputNumber, CheckboxBoolean, Datepicker, CheckboxOne, Radio, Dropdown, CheckboxGroup, Table                                                    |
| "label"             | VTL/MD | dans les "options" d'un CheckboxOne                                                                                                                              |
| "label"             | VTL/MD | dans les "responses" d'un CheckboxGroup                                                                                                                          |
| "label"             | VTL/MD | Declaration (dans les "declarations" d'un objet composant)                                                                                                       |
| "control"           | VTL    | Control (dans les "controls" d'un objet composant)                                                                                                               |
| "errorMessage"      | VTL/MD | Control (dans les "controls" d'un objet composant)                                                                                                               |
| "value" + "type"    | VTL    | Filter (champ "filter" dans les objets composant) _Cas batard, on devrait avoir une propriété "expression" (ou autre, peu importe, mais utiliser l'objet Label)_ |
| "expression"        | VTL    | Variable (avec "variableType" : "CALCULATED")                                                                                                                    |

Rules from Eno-V2 source code:

```xml
<xsl:function name="enolunatic:get-label-type">
<xsl:param name="locationOfLabel"/>
<xsl:choose>
<xsl:when test="$locationOfLabel='label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
<xsl:when test="$locationOfLabel='responses.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
<xsl:when test="$locationOfLabel='hierarchy.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
<xsl:when test="$locationOfLabel='hierarchy.subSequence.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
<xsl:when test="$locationOfLabel='hierarchy.sequence.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
<xsl:when test="$locationOfLabel='declarations.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
<xsl:when test="$locationOfLabel='controls.control'"><xsl:value-of select="'VTL'"/></xsl:when>
<xsl:when test="$locationOfLabel='controls.errorMessage'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
<xsl:when test="$locationOfLabel='options.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
<xsl:when test="$locationOfLabel='lines.min'"><xsl:value-of select="'VTL'"/></xsl:when>
<xsl:when test="$locationOfLabel='lines.max'"><xsl:value-of select="'VTL'"/></xsl:when>
<xsl:when test="$locationOfLabel='iterations'"><xsl:value-of select="'VTL'"/></xsl:when>
<xsl:when test="$locationOfLabel='conditionFilter'"><xsl:value-of select="'VTL'"/></xsl:when>
<xsl:when test="$locationOfLabel='expression'"><xsl:value-of select="'VTL'"/></xsl:when>
<xsl:otherwise><xsl:value-of select="'VTL|MD'"/></xsl:otherwise>
</xsl:choose>
</xsl:function>
```
