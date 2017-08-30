<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:eno="http://xml.insee.fr/apps/eno"
                xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
                xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
                xmlns:enoddi2fr="http://xml.insee.fr/apps/eno/ddi2form-runner"
                xmlns:d="ddi:datacollection:3_2"
                xmlns:r="ddi:reusable:3_2"
                xmlns:l="ddi:logicalproduct:3_2"
                xmlns:enoddi32="http://xml.insee.fr/apps/eno/out/ddi32"
                xmlns:enopogues="http://xml.insee.fr/apps/eno/in/pogues-xml"
                version="2.0"><!-- Importing the different resources -->
   <xsl:import href="../../inputs/pogues-xml/source.xsl"/>
   <xsl:import href="../../outputs/ddi/models.xsl"/>
   <xsl:import href="../../lib.xsl"/>
   <xd:doc scope="stylesheet">
      <xd:desc>
         <xd:p>This stylesheet is used to transform a DDI input into an Xforms form (containing orbeon form runner adherences).</xd:p>
      </xd:desc>
   </xd:doc>
   <!-- The output file generated will be xml type -->
   <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
   <xsl:strip-space elements="*"/>
   <xd:doc>
      <xd:desc>
         <xd:p>The parameter file used by the stylesheet.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:param name="parameters-file"/>
   <xd:doc>
      <xd:desc>
         <xd:p>The parameters are charged as an xml tree.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:variable name="parameters" select="doc($parameters-file)"/>
   <xd:doc>
      <xd:desc>
         <xd:p>Root template :</xd:p>
         <xd:p>The transformation starts with the main Sequence.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="/">
      <xsl:apply-templates select="/pogues:Questionnaire" mode="source"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The main Sequence activates the higher driver 'Form'.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Questionnaire" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Form',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A data to be colected with all it's implementation</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Response" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('ResponseDomain',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>An instuction from the interviewer</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Declaration" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Instruction',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A list a categories to be called in questions</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:CodeList" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('CodeList',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A refernce calling a CodeList</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:CodeListReference" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('CodeListReference',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A category, generaly stored in CodeLists</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Code" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Code',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The simplest question with a type of data</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='QuestionType' and @questionType='SIMPLE' ]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('QuestionSimple',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A question with a choice to be made between categories or elementsof a CodeList</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='QuestionType' and @questionType='SINGLE_CHOICE' ]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('QuestionSingleChoice',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A question with multiple choices to be made between categories or elements of CodeLists</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='QuestionType' and @questionType='MULTIPLE_CHOICE' ]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('QuestionMultipleChoice',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A question table with each cell representing an answer</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='QuestionType' and @questionType='TABLE' ]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('QuestionTable',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A sequence that can contains other sequenses or questions</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='SequenceType']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Sequence',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Not implemented yet</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:GoTo" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('GoTo',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Not implemented yet</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Survey | pogues:DataCollection" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('DataCollection',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Not implemented yet</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:ComponentGroup" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('CompoenentGroup',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Not implemented yet</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:MemberReference" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('MemberReference',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Datatype&gt; n'ayant pas d'atribut "visualizationHint" et dont l'atribut "xsi:type" est egal à "TextDatatypeType"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[not(@visualizationHint) and @xsi:type='TextDatatypeType']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('TextDomain',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Datatype&gt; n'ayant pas d'atribut "visualizationHint" et dont l'atribut "xsi:type" est egal à "NumericDatatypeType"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[not(@visualizationHint) and @xsi:type='NumericDatatypeType']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('NumericDomain',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Datatype&gt; n'ayant pas d'atribut "visualizationHint" et dont l'atribut "xsi:type" est egal à "DateDatatypeType"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[not(@visualizationHint) and @xsi:type='DateDatatypeType']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('DateTimeDomain',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Datatype&gt; n'ayant pas d'atribut "visualizationHint" et dont l'atribut "xsi:type" est egal à "BooleanDatatypeType"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[not(@visualizationHint) and @xsi:type='BooleanDatatypeType']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('BooleanDomain',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Datatype&gt; dont l'atribut "visualizationHint" est "RADIO" et dont l'atribut "xsi:type" est egal à "TextDatatypeType"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[@visualizationHint='RADIO' and @xsi:type='TextDatatypeType']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('RadioDomain',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Datatype&gt; dont l'atribut "visualizationHint" est "CHECKBOX" et dont l'atribut "xsi:type" est egal à "TextDatatypeType"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[@visualizationHint='CHECKBOX' and @xsi:type='TextDatatypeType']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('CheckBoxDomain',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Datatype&gt; dont l'atribut "visualizationHint" est "DROPDOWN" et dont l'atribut "xsi:type" est egal à "TextDatatypeType"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[@visualizationHint='DROPDOWN' and @xsi:type='TextDatatypeType']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('DropDownListDomain',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Dimension&gt; dont l'atribut "dimensionType" est "PRIMARY" et dont l'atribut "dynamic" est egal à "0"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Dimension[@dimensionType='PRIMARY' and @dynamic != '0']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('RosterDimension',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Dimension&gt; dont l'atribut "dimensionType" est "MEASURE" et dont l'atribut "dynamic" est egal à "0"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Dimension[@dimensionType='MEASURE' and @dynamic='0']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('UnknownDimension',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>s'applique aux &lt;Dimension&gt; dont l'atribut "dimensionType" est "PRIMARY" et dont l'atribut "dynamic" est egal à "0"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Dimension[@dimensionType='PRIMARY' and @dynamic='0']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('CodeDomainDimension',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A specific sequence construct that can hide it's childs</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:IfThenElse" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('IfThenElse',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>An information about the structure of the answers of a complex question</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:ResponseStructure" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('GridDimension',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Function that returns the label of a pogues element.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-citation">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-label($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Name is the default element for names in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-name($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the agency that created the survey</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-agency">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-agency($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the ID attribute of the element</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-id($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the text of the Declaration</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-text">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-declaration-text($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Value is the default element for values in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-label($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return a lang for the survey. As this information in not available in PoguesXML, it is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-value($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return a version for the survey. As this information in not available in PoguesXML, it is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-lang">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-lang($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the type of visualisation of a response (checkbox, radio-button,..)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-version">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-version($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the type of a question or of the data of a response</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-generic-output-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-visualization-hint($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the name of the type of data of a response (TEXT, NUMERIC,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-type($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the maximum length of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-type-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-type-name($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the attribut coding if the anwser is mandatory. This part is not implemented</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-max-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-max-length($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the attribut coding the level of the sequence (QUESTIONNAIRE, MODULE, SUBMODULE,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-mandatory">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-mandatory($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all Sequenses elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-sequence-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-generic-name($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all Questions elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-sequences">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-sequences($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all Declarations elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-questions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-questions($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the number of decimals of the data type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-instructions($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the minimal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-decimal-positions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-decimals($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the maximal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-low">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-minimum($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the string expected as value of the isDiscrete attribut of l:code in ddi3.2 . This value is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-high">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-maximum($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the dynamic attribute of the pogues:Dimension. This value is used to compute positions in grids</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:is-discrete">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:is-discrete($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>This is used to implement the code of booleans if this return something</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-dynamic">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-dynamic($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Expression is the default element for expressions in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:exist-boolean">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:exist-boolean($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the ID of the element that result a true condition.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-expression">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-expression($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the ID to which the GoTo aim</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-if-true">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-if-true($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all IfThenElses elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-ifthenelses">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-ifthenelses($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-parent-id">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-parent-id($context)"/>
   </xsl:function>
</xsl:stylesheet>
