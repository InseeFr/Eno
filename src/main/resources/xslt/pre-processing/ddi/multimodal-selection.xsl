<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
  xmlns:d="ddi:datacollection:3_3" xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3" version="2.0">

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
      <xd:p>The output format provided by the call to the transformation</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:param name="output-format"/>

  <xd:doc>
    <xd:desc>
      <xd:p>The parameter file used by the stylesheet.</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:param name="parameters-file"/>
  <xsl:param name="parameters-node" as="node()" required="no">
    <empty/>
  </xsl:param>
  
  <xd:doc>
    <xd:desc>
      <xd:p>The mode parameter is stored in a variable.</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:variable name="mode">
    <xsl:choose>
      <xsl:when test="$parameters-node//Mode">
        <xsl:copy-of select="$parameters-node//Mode"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="document($parameters-file)//Mode"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

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
      <xd:p>This template is meant to keep instructions if and only if the Instruction (AFTER_QUESTION_TEXT in Pogues) is meant to
        be kept for the mode</xd:p>
      <xd:p>Rules are as follow :</xd:p>
      <xd:p> - all mode (ddi and fodt output) to keep everything</xd:p>
      <xd:p> - papi mode (Fo output) only keeps SelfAdministeredQuestionnaire.Paper</xd:p>
      <xd:p> - cawi mode (Xforms output and some Lunatic output) only keeps SelfAdministeredQuestionnaire.WebBased</xd:p>
      <xd:p> - capi-cati mode (some Lunatic output) keeps Interview.Telephone.CATI and Interview.FaceToFace.CAPIorCAMI</xd:p>
      <xd:p> - process mode (some Lunatic output) keeps everything (so for now, same as all)</xd:p>
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
      <xsl:when test=".[d:InstructionName/r:String = 'SelfAdministeredQuestionnaire.WebBased'] and $mode = 'cawi'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when test=".[d:InstructionName/r:String = 'SelfAdministeredQuestionnaire.Paper'] and $mode = 'papi'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when test="count(d:InstructionName[matches(r:String, 'Interview')]) > 0 and $mode = 'capi-cati'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when test="$mode = 'all' or $mode = 'process'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when test="count(d:InstructionName[matches(r:String, 'SelfAdministeredQuestionnaire|Interview')]) = 0">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>
  
  <xd:doc>
    <xd:desc>
      <xd:p>This template is meant to keep instructions if and only if the StatementItem (BEFORE_QUESTION_TEXT in Pogues) is meant to
        be kept for the mode</xd:p>
      <xd:p>Rules are as follow :</xd:p>
      <xd:p> - all mode (ddi and fodt output) to keep everything</xd:p>
      <xd:p> - papi mode (Fo output) only keeps SelfAdministeredQuestionnaire.Paper</xd:p>
      <xd:p> - cawi mode (Xforms output and some Lunatic output) only keeps SelfAdministeredQuestionnaire.WebBased</xd:p>
      <xd:p> - capi-cati mode (some Lunatic output) keeps Interview.Telephone.CATI and Interview.FaceToFace.CAPIorCAMI</xd:p>
      <xd:p> - process mode (some Lunatic output) keeps everything (so for now, same as all)</xd:p>
      <xd:p>Since Fo and Xforms keep only one type of Instruction, the InstructionName items
        describing the collcetion mode can be deleted</xd:p>
      <xd:p>Since Lunatic keeps everything but SelfAdministeredQuestionnaire.Paper, InstructionName
        items need to be kept to decide later what to valorize depending on the collection
        mode</xd:p>
      <xd:p>To allow compatibility with older ddi, an Instruction with no match with an
        InstructionName describing a collection mode shall be kept</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:template match="d:StatementItem">
    <xsl:choose>
      <xsl:when test=".[d:ConstructName/r:String = 'SelfAdministeredQuestionnaire.WebBased'] and $mode = 'cawi'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when test=".[d:ConstructName/r:String = 'SelfAdministeredQuestionnaire.Paper'] and $mode = 'papi'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when test="count(d:ConstructName[matches(r:String, 'Interview')]) > 0 and $mode = 'capi-cati'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when test="$mode = 'all' or $mode = 'process'">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:when test="count(d:ConstructName[matches(r:String, 'SelfAdministeredQuestionnaire|Interview')]) = 0">
        <xsl:copy>
          <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>

  <xd:doc>
    <xd:desc>
      <xd:p>This template is used to NOT copy the InstructionName specifying the mode -> else it makes Eno crash in further treatment of instructions</xd:p>
      <xd:p>Later, it might be overloaded to rather keep that information for some cases (all ? process ?) so it might be valorized</xd:p>
      <xd:p>Thus the idea now is : the Instruction to keep for the mode specified have been filtered by the previous template</xd:p>
      <xd:p>And the template below will delete (for those kept Instruction) the InstructionName in the DDI referring to the mode</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:template match="d:InstructionName">
    <xsl:if test="not(matches(r:String, 'SelfAdministeredQuestionnaire|Interview'))">
      <xsl:copy>
        <xsl:apply-templates select="node() | @*"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>
  
  <xd:doc>
    <xd:desc>
      <xd:p>This template is used to NOT copy the ConstructName specifying the mode -> else it might make Eno crash in further treatment of instructions</xd:p>
      <xd:p>Later, it might be overloaded to rather keep that information for some cases (all ? process ?) so it might be valorized</xd:p>
      <xd:p>Not necessary as with InstructionName, but keeps things tidier</xd:p>
    </xd:desc>
  </xd:doc>
  <xsl:template match="d:ConstructName">
    <xsl:if test="not(matches(r:String, 'SelfAdministeredQuestionnaire|Interview'))">
      <xsl:copy>
        <xsl:apply-templates select="node() | @*"/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
