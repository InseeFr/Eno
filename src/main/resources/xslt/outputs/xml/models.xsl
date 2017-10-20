<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoxml="http://xml.insee.fr/apps/eno/xml"
    version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>An xslt stylesheet who transforms an input into XML through generic driver templates.</xd:p>
            <xd:p>The real input is mapped with the drivers.</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>The default matching element, the root of the produced xml.</xd:p>
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
            <xd:p>A generic xml element. Helpful to structure the different levels of the input format.</xd:p>
            <xd:p>For example, for a spreadsheet, each line of the document will correspond to a generic element.</xd:p>
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
            <xd:p>An xml element.</xd:p>
            <xd:p>Calls a function to name the element through an attribute and another one to get its value.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="DefinedElement" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="element-name">
            <xsl:apply-templates select="enoxml:get-element-name($source-context)"/>
        </xsl:variable>
        <xsl:variable name="element-content">
            <xsl:apply-templates select="enoxml:get-value($source-context)"/>
        </xsl:variable>
        <xsl:if test="$element-name/text() or $element-content/text()">
            <xsl:element name="DefinedElement">
                <xsl:attribute name="name" select="$element-name"/>
                <xsl:value-of select="$element-content"/>
            </xsl:element>            
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
