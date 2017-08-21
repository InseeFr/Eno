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
         <xd:p/>
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
         <xd:p/>
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
         <xd:p/>
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
         <xd:p/>
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
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Child[@xsi:type='QuestionType']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Question',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
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
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-citation">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-label($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-name($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-agency">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-agency($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-id($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-text">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-declaration-text($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-label($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-value($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-lang">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-lang($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-version">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-version($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-generic-output-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-visualization-hint($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-type($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-type-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-type-name($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-max-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-max-length($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-mandatory">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-mandatory($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-sequence-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-generic-name($context)"/>
   </xsl:function>
</xsl:stylesheet>
