<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
  xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:d="ddi:datacollection:3_3"
  xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
  xmlns:eno="http://xml.insee.fr/apps/eno" version="2.0">

  <xsl:import href="../../lib.xsl"/>

  <xd:doc scope="stylesheet">
    <xd:desc>
      <xd:p>This xslt stylesheet is used before the dereferencing step.</xd:p>
      <xd:p>The purpose is to keep the relevant components for the target output.</xd:p>
      <xd:p>In its current form, it only works for multi-modal declarations.</xd:p>
    </xd:desc>
  </xd:doc>

  <!-- The output file generated will be xml type -->
  <xsl:output method="xml" indent="no" encoding="UTF-8"/>

  <!--<xsl:strip-space elements="*"/>-->

  <xd:doc>
    <xd:desc>
      <xd:p>The properties file used by the stylesheet.</xd:p>
      <xd:p>It's on a transformation level.</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:param name="properties-file"/>

  <xd:doc>
    <xd:desc>
      <xd:p>The properties file is charged as an xml tree.</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:variable name="properties" select="$properties-file"/>

  <xd:doc>
    <xd:desc>
      <xd:p>Root template.</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:template match="/">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xd:doc>
    <xd:desc>
      <xd:p>Default template for every element and every attribute, simply copying to the
        output.</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="node() | @*"/>
    </xsl:copy>
  </xsl:template>


  <xd:doc>
    <xd:desc>
      <xd:p>This template is meant to keep Instructions if and only if the Instruction is meant to
        be kept for the output</xd:p>
      <xd:p>Rules are as follow :</xd:p>
      <xd:p> - Fo output only keeps SelfAdministeredQuestionnaire.Paper</xd:p>
      <xd:p> - Xforms output only keeps SelfAdministeredQuestionnaire.WebBased</xd:p>
      <xd:p> - Lunatic-XML output keeps everything except SelfAdministeredQuestionnaire.Paper</xd:p>
      <xd:p>Since Fo and Xforms keep only one type of Instruction, the InstructionName items
        describing the collcetion mode can be deleted</xd:p>
      <xd:p>Since Lunatic keeps everything but SelfAdministeredQuestionnaire.Paper, InstructionName
        items need to be kept to decide later what to valorize depending on the collection
        mode</xd:p>
      <xd:p>To allow compatibility with older ddi, an Instruction with no match with an
        InstructionName describing a collection mode shall be kept</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:template match="d:Instruction">
    <xsl:choose>
      <xsl:when
        test=".[d:InstructionName/r:String = 'SelfAdministeredQuestionnaire.WebBased'] and $properties//OutFormat = 'xforms'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when
        test=".[d:InstructionName/r:String = 'SelfAdministeredQuestionnaire.Paper'] and $properties//OutFormat = 'fo'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when
        test="count(d:InstructionName[matches(r:String, 'SelfAdministeredQuestionnaire.WebBased|Interview')]) > 0 and $properties//OutFormat = 'lunatic-xml'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when
        test="count(d:InstructionName[matches(r:String, 'SelfAdministeredQuestionnaire|Interview')]) = 0">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>

  <xd:doc>
    <xd:desc>
      <xd:p>This template is used to copy InstructionName relative to collection mode only if the
        output format is lunatic-xml</xd:p>
      <xd:p>Fo and Xforms do not need to valorize later the collection mode information</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:template match="d:InstructionName">
    <xsl:if
      test="$properties//OutFormat = ('xforms', 'fo') and not(matches(r:String, 'SelfAdministeredQuestionnaire|Interview'))">
      <xsl:copy>
        <xsl:apply-templates select="node() | @*"/>
      </xsl:copy>
    </xsl:if>
    <xsl:if
      test="$properties//OutFormat = 'lunatic-xml' and not(r:String = 'SelfAdministeredQuestionnaire.Paper')">
      <xsl:copy>
        <xsl:apply-templates select="node() | @*"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
