<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
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
</xsl:stylesheet>
