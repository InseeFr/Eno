<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:enoxml="http://xml.insee.fr/apps/eno/xml" version="2.0">

    <!-- This xsl stylesheet is used in the fods2xsl target (imported by xml2xsl.xsl)-->
    <!-- Its purpose is to retrieve several elements in a xml file (xml.tmp) containing Generic and DefinedElement created -->
    <!-- by fods2xml -->
    <!-- The information retrieved will then be used in xml2xsl.xsl in order to create the output xsl file -->

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>Generation from xml</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>Starting the transformation from xml by the root element</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/" mode="source">
        <xsl:apply-templates select="Root" mode="source"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that returns the name of the element (not the xml name but the value of
                the name attribute that represents the element)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoxml:get-element-name" as="xs:string">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-element-name"/>
    </xsl:function>

    <!-- Only called by the DefinedElement object-->
    <xsl:template match="DefinedElement" mode="enoxml:get-element-name" as="xs:string">
        <xsl:value-of select="@name"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that returns the value of an element from the xml file</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoxml:get-value" as="xs:string">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-value"/>
    </xsl:function>

    <!-- Only called by the DefinedElement object -->
    <xsl:template match="DefinedElement" mode="enoxml:get-value" as="xs:string">
        <xsl:value-of select="text()"/>
    </xsl:template>

</xsl:stylesheet>
