<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:enoxml="http://xml.insee.fr/apps/eno/xml" version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>A library of getter functions for a generic xml structure with their
                implementations for different elements.</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that returns the name of the element (not the xml name but the value of
                the name attribute that represents the element).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoxml:get-element-name" as="xs:string">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-element-name"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>For the 'DefinedElement' it returns the 'name' attribute value.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="DefinedElement" mode="enoxml:get-element-name" as="xs:string">
        <xsl:value-of select="@name"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that returns the value of an element.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoxml:get-value" as="xs:string">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-value"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>For the 'DefinedElement' it returns the text().</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="DefinedElement" mode="enoxml:get-value" as="xs:string">
        <xsl:value-of select="text()"/>
    </xsl:template>

</xsl:stylesheet>
