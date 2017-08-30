<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:enopogues="http://xml.insee.fr/apps/eno/in/pogues-xml"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xs"
                version="2.0">
   <xd:doc>
      <xd:desc>
         <xd:p>For each element, the default behaviour is to return empty text.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="#all" priority="-1">
      <xsl:text/>
   </xsl:template>
   <xsl:template match="//pogues:Child[@xsi:type='QuestionType']" mode="with-tag">
      <xsl:sequence select="."/>
   </xsl:template>
   <xsl:template match="//pogues:Child[@xsi:type='SequenceType']" mode="with-tag">
      <xsl:sequence select="."/>
   </xsl:template>
   <xsl:template match="//pogues:Declaration" mode="with-tag">
      <xsl:sequence select="."/>
   </xsl:template>
   <xsl:template match="//pogues:IfThenElse" mode="with-tag">
      <xsl:sequence select="."/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Function that returns the label of a pogues element.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-label"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Name is the default element for names in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-name"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the agency that created the survey</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-agency">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-agency"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the ID attribute of the element</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-id"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the text of the Declaration</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-declaration-text">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-declaration-text"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Value is the default element for values in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-value"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return a lang for the survey. As this information in not available in PoguesXML, it is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-lang">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-lang"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return a version for the survey. As this information in not available in PoguesXML, it is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-version">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-version"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the type of visualisation of a response (checkbox, radio-button,..)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-visualization-hint">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-visualization-hint"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the type of a question or of the data of a response</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-type"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the name of the type of data of a response (TEXT, NUMERIC,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-type-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-type-name"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the maximum length of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-max-length">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-max-length"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the attribut coding if the anwser is mandatory. This part is not implemented</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-mandatory">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-mandatory"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the attribut coding the level of the sequence (QUESTIONNAIRE, MODULE, SUBMODULE,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-generic-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-generic-name"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all Sequenses elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-sequences">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-sequences"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all Questions elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-questions">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-questions"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all Declarations elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-instructions"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the number of decimals of the data type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-decimals">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-decimals"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the minimal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-minimum">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-minimum"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the maximal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-maximum">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-maximum"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the string expected as value of the isDiscrete attribut of l:code in ddi3.2 . This value is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:is-discrete">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:is-discrete"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the dynamic attribute of the pogues:Dimension. This value is used to compute positions in grids</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-dynamic">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-dynamic"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>This is used to implement the code of booleans if this return something</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:exist-boolean">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:exist-boolean"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Expression is the default element for expressions in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-expression">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-expression"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the ID of the element that result a true condition.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-then-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-then-id"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the ID to which the GoTo aim</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-if-true">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-if-true"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all IfThenElses elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-ifthenelses">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-ifthenelses"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-parent-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-parent-id"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Label is the default element for labels in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-label">
      <xsl:value-of select="pogues:Label"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Name is the default element for names in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-name">
      <xsl:value-of select="pogues:Name"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The &lt;d:InstructionName&gt; of ddi InterviwerInstruction is equal to the Declaration@declarationType of PoguesXML</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Declaration" mode="enopogues:get-name">
      <xsl:value-of select="@declarationType"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the agency that created the survey</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-agency">
      <xsl:value-of select="ancestor-or-self::pogues:Questionnaire/@agency"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the ID attribute of the element</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-id">
      <xsl:value-of select="@id"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The CodeListRefence element contains the ID of the CodeList it reference. It doesn't have an ID attribute</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:CodeListReference" mode="enopogues:get-id">
      <xsl:value-of select="./text()"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the text of the Declaration</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Declaration" mode="enopogues:get-declaration-text">
      <xsl:value-of select="pogues:Text"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Value is the default element for values in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-value">
      <xsl:value-of select="pogues:Value"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return a version for the survey. As this information in not available in PoguesXML, it is hard-coded in this document</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-version">
      <xsl:value-of select="'0.1.1'"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return a lang for the survey. As this information in not available in PoguesXML, it is hard-coded in this document</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-lang">
      <xsl:value-of select="'fr-FR'"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the type of visualisation of a response (checkbox, radio-button,..)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Response" mode="enopogues:get-visualization-hint">
      <xsl:value-of select="pogues:Datatype/@visualizationHint"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the type of data of a response (TextDatatypeType, NumericDatatypeType,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Response" mode="enopogues:get-type">
      <xsl:value-of select="pogues:Datatype/@xsi:type"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the type of the question (SIMPLE, SINGLE_CHOICE, MULTIPLE_CHOICE, TABLE)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='QuestionType']"
                 mode="enopogues:get-type">
      <xsl:value-of select="@questionType"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the name of the type of data of a response (TEXT, NUMERIC,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Response" mode="enopogues:get-type-name">
      <xsl:value-of select="pogues:Datatype/@typeName"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the maximum length of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype" mode="enopogues:get-max-length">
      <xsl:value-of select="pogues:MaxLength"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the attribut coding if the anwser is mandatory. This part is not implemented</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Response" mode="enopogues:get-mandatory">
      <xsl:value-of select="@mandatory"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the attribut coding the level of the sequence (QUESTIONNAIRE, MODULE, SUBMODULE,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='SequenceType']"
                 mode="enopogues:get-generic-name">
      <xsl:value-of select="@genericName"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all Questions elements of the survey. The with-tag of the Match_Mode column means that this template is hard-coded in the src\main\resources\xslt\inputs\pogues-xml\source-fixed.xml. This is to keep the tags as well as their content by makig a sequence instead of a value-of</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-questions">
      <xsl:apply-templates select="//pogues:Child[@xsi:type='QuestionType']" mode="with-tag"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all Sequenses elements of the survey. The with-tag of the Match_Mode column means that this template is hard-coded in the src\main\resources\xslt\inputs\pogues-xml\source-fixed.xml. This is to keep the tags as well as their content by makig a sequence instead of a value-of</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-sequences">
      <xsl:apply-templates select="//pogues:Child[@xsi:type='SequenceType']" mode="with-tag"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all Declarations elements of the survey. The with-tag of the Match_Mode column means that this template is hard-coded in the src\main\resources\xslt\inputs\pogues-xml\source-fixed.xml. This is to keep the tags as well as their content by makig a sequence instead of a value-of</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-instructions">
      <xsl:apply-templates select="//pogues:Declaration" mode="with-tag"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the number of decimals of the data type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype" mode="enopogues:get-decimals">
      <xsl:value-of select="pogues:Decimals"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the minimal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[not(@visualizationHint) and @xsi:type='NumericDatatypeType']"
                 mode="enopogues:get-minimum">
      <xsl:value-of select="pogues:Minimum"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the maximal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[not(@visualizationHint) and @xsi:type='NumericDatatypeType']"
                 mode="enopogues:get-maximum">
      <xsl:value-of select="pogues:Maximum"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the string expected as value of the isDiscrete attribut of l:code in ddi3.2 . This value is hard-coded in this document</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Code" mode="enopogues:is-discrete">
      <xsl:value-of select="'true'"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the dynamic attribute of the pogues:Dimension. This value is used to compute positions in grids</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Dimension" mode="enopogues:get-dynamic">
      <xsl:value-of select="@dynamic"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>This is used to implement the code of booleans if this return something</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Questionnaire" mode="enopogues:exist-boolean">
      <xsl:value-of select="pogues:Datatype[not(@visualizationHint) and @xsi:type='BooleanDatatypeType']"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Expression is the default element for expressions in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:*" mode="enopogues:get-expression">
      <xsl:value-of select="pogues:Expression"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the ID of the element that result a true condition.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:IfThenElse" mode="enopogues:get-then-id">
      <xsl:value-of select="pogues:IfTrue/@id"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return the ID to which the GoTo aim</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:GoTo" mode="enopogues:get-if-true">
      <xsl:value-of select="pogues:IfTrue"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Return all IfThenElses elements of the survey. The with-tag of the Match_Mode column means that this template is hard-coded in the src\main\resources\xslt\inputs\pogues-xml\source-fixed.xml. This is to keep the tags as well as their content by makig a sequence instead of a value-of</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-ifthenelses">
      <xsl:apply-templates select="//pogues:IfThenElse" mode="with-tag"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Dimension" mode="enopogues:get-parent-id">
      <xsl:value-of select="../@id"/>
   </xsl:template>
</xsl:stylesheet>
