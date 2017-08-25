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
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-name"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-agency">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-agency"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-id"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-declaration-text">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-declaration-text"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-value"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-lang">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-lang"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-version">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-version"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-visualization-hint">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-visualization-hint"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-type"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-type-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-type-name"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-max-length">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-max-length"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-mandatory">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-mandatory"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-generic-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-generic-name"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-sequences">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-sequences"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-questions">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-questions"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-instructions"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-decimals">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-decimals"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-minimum">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-minimum"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-maximum">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-maximum"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:is-discrete">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:is-discrete"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-dynamic">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-dynamic"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:exist-boolean">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:exist-boolean"/>
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
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-name">
      <xsl:value-of select="pogues:Name"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Declaration" mode="enopogues:get-name">
      <xsl:value-of select="@declarationType"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-agency">
      <xsl:value-of select="ancestor-or-self::pogues:Questionnaire/@agency"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-id">
      <xsl:value-of select="@id"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:CodeListReference" mode="enopogues:get-id">
      <xsl:value-of select="./text()"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Declaration" mode="enopogues:get-declaration-text">
      <xsl:value-of select="pogues:Text"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-value">
      <xsl:value-of select="pogues:Value"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-version">
      <xsl:value-of select="'0.1.1'"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-lang">
      <xsl:value-of select="'fr-FR'"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Response" mode="enopogues:get-visualization-hint">
      <xsl:value-of select="pogues:Datatype/@visualizationHint"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Response" mode="enopogues:get-type">
      <xsl:value-of select="pogues:Datatype/@xsi:type"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='QuestionType']"
                 mode="enopogues:get-type">
      <xsl:value-of select="@questionType"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Response" mode="enopogues:get-type-name">
      <xsl:value-of select="pogues:Datatype/@typeName"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype" mode="enopogues:get-max-length">
      <xsl:value-of select="pogues:MaxLength"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Response" mode="enopogues:get-mandatory">
      <xsl:value-of select="@mandatory"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='SequenceType']"
                 mode="enopogues:get-generic-name">
      <xsl:value-of select="@genericName"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-questions">
      <xsl:apply-templates select="//pogues:Child[@xsi:type='QuestionType']" mode="with-tag"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-sequences">
      <xsl:apply-templates select="//pogues:Child[@xsi:type='SequenceType']" mode="with-tag"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-instructions">
      <xsl:apply-templates select="//pogues:Declaration" mode="with-tag"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype" mode="enopogues:get-decimals">
      <xsl:value-of select="pogues:Decimals"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[not(@visualizationHint) and @xsi:type='NumericDatatypeType']"
                 mode="enopogues:get-minimum">
      <xsl:value-of select="pogues:Minimum"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Datatype[not(@visualizationHint) and @xsi:type='NumericDatatypeType']"
                 mode="enopogues:get-maximum">
      <xsl:value-of select="pogues:Maximum"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Code" mode="enopogues:is-discrete">
      <xsl:value-of select="'true'"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Dimension" mode="enopogues:get-dynamic">
      <xsl:value-of select="@dynamic"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Questionnaire" mode="enopogues:exist-boolean">
      <xsl:value-of select="pogues:Datatype[not(@visualizationHint) and @xsi:type='BooleanDatatypeType']"/>
   </xsl:template>
</xsl:stylesheet>
