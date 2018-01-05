<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
    xmlns:enoddi2fr="http://xml.insee.fr/apps/eno/ddi2form-runner"
    xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2"
    xmlns:enoddi32="http://xml.insee.fr/apps/eno/out/ddi32"
    xmlns:enopogues="http://xml.insee.fr/apps/eno/in/pogues-xml"
    version="2.0">

    <!-- Importing the different resources -->
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
    
    <!--TODO The implementation of retrieving reference id (aka qc-id,cc-id...) should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi32:get-reference-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi32:get-reference-id"/>
    </xsl:function>
    
    <xsl:template match="*[enopogues:is-question(.) = 'true']" mode="enoddi32:get-reference-id">
        <xsl:value-of select="enoddi32:get-qc-id(.)"/>
    </xsl:template>
    
    <xsl:template match="*[enopogues:is-sequence(.) = 'true']" mode="enoddi32:get-reference-id">
        <xsl:value-of select="enoddi32:get-id(.)"/>
    </xsl:template>
    
    <xsl:template match="pogues:Control" mode="enoddi32:get-reference-id">
        <xsl:value-of select="enoddi32:get-id(.)"/>
    </xsl:template>
    
    <xsl:template match="pogues:Response[@mandatory='true']" mode="enoddi32:get-reference-id">
        <xsl:value-of select="enoddi32:get-ci-id(.)"/>
    </xsl:template>
    
    
    <!--TODO The implementation of retrieving reference element name (aka Sequence,QuestionConstruct...) should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi32:get-reference-element-name">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi32:get-reference-element-name"/>
    </xsl:function>
    
    <xsl:template match="*[enopogues:is-question(.) = 'true']" mode="enoddi32:get-reference-element-name">
        <xsl:value-of select="'QuestionConstruct'"/>
    </xsl:template>
    
    <xsl:template match="*[enopogues:is-sequence(.) = 'true']" mode="enoddi32:get-reference-element-name">
        <xsl:value-of select="'Sequence'"/>
    </xsl:template>
    
    <xsl:template match="pogues:Control | pogues:Response[@mandatory='true']" mode="enoddi32:get-reference-element-name">
        <xsl:value-of select="'ComputationItem'"/>
    </xsl:template>
    
    <!--TODO The implementation of building id should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi32:get-si-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi32:get-si-id"/>
    </xsl:function>
    
    <xsl:template match="pogues:Declaration" mode="enoddi32:get-si-id">
        <xsl:value-of select="concat(enoddi32:get-id(.),'-SI')"/>
    </xsl:template>
    
    <!--TODO The implementation of ComputationItem type should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi32:get-ci-type">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi32:get-ci-type"/>
    </xsl:function>
    
    <xsl:template match="pogues:Control" mode="enoddi32:get-ci-type">
        <xsl:value-of select="enoddi32:get-type(.)"/>
    </xsl:template>
    
    <xsl:template match="pogues:Response[@mandatory='true']" mode="enoddi32:get-ci-type">
        <xsl:value-of select="'mandatory'"/>
    </xsl:template>
    
    <!--TODO The implementation of ComputationItem type should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi32:get-ci-name">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi32:get-ci-name"/>
    </xsl:function>
    
    <xsl:template match="pogues:Control" mode="enoddi32:get-ci-name">
        <xsl:value-of select="enoddi32:get-description(.)"/>
    </xsl:template>
    
    <xsl:template match="pogues:Response[@mandatory='true']" mode="enoddi32:get-ci-name">
        <xsl:value-of select="concat('ComputationItem for the mandatory question ',enoddi32:get-id(.))"/>
    </xsl:template>
    
    
    
    
</xsl:stylesheet>
