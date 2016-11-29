<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoxml="http://xml.insee.fr/apps/eno/xml" exclude-result-prefixes="#all"
    version="2.0">

    <!-- This xsl stylesheet is used in the fods2xsl target (imported by fods2xml.xsl)-->
    <!-- Its purpose is to participate in the creation of the temporary output xml file (xml.tmp) from a fods input -->
    <!-- xml.tmp will be a tree of GenericElement/DefinedElement with information stored in the @name attribute -->

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>Generation of XML!</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>The default matching element</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Root" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="Root">
            <!-- This will call children elements that will create an xml structure -->
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>A generic xml element. Helpful to structure the different levels over the input format</xd:p>
            <xd:p>For example, for a fods file, each line of the document will correspond to a generic element (except the first one)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="GenericElement" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="GenericElement">
            <!-- This will call children elements that will create an xml structure -->
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>An xml element</xd:p>
            <xd:p>Calls an element name function and another one to get the value.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="DefinedElement" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="element-name">
            <xsl:apply-templates select="enoxml:get-element-name($source-context)"/>
        </xsl:variable>
        <xsl:element name="DefinedElement">
            <xsl:attribute name="name" select="$element-name"/>
            <xsl:apply-templates select="enoxml:get-value($source-context)"/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
