<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:il="http://xml/insee.fr/xslt/lib" xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
    xmlns:iatfods="http://xml/insee.fr/xslt/apply-templates/fods"
    xmlns:iatxml="http://xml/insee.fr/xslt/apply-templates/xml"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" exclude-result-prefixes="#all"
    version="2.0">

    <!-- xsl stylesheet applied to preformate.tmp in the temporary process of xsl files creation (fods2xml then xml2xsl) -->
    <!-- This stylesheet will read the preformate.tmp, get the different informations required (with source.xsl) -->
    <!-- models.xml will then use the different retrieved information to create xml.tmp -->
    <!-- The content of this file (fods2xml.xsl will help linking the different elements with each other -->
    <!-- lib.xsl : used to parse a file with defined constraints -->

    <!-- Importing the different resources -->
    <xsl:import href="../inputs/fods/source.xsl"/>
    <xsl:import href="../outputs/xml/models.xsl"/>
    <xsl:import href="../lib.xsl"/>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 6, 2013</xd:p>
            <xd:p>Transforms fods to XML!</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template match="/">
        <xsl:apply-templates select="/" mode="source"/>
    </xsl:template>

    <xd:desc>
        <xd:p>Linking and element's root the table element</xd:p>
    </xd:desc>
    <xsl:template match="table:table" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('Root',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:desc>
        <xd:p>To the first table:table-row, not linking anything (it will contain the columns names)</xd:p>
    </xd:desc>
    <xsl:template match="table:table-row[position()=1]" mode="source"/>

    <xd:desc>
        <xd:p>To the other table:table-row, linking the GenericElement element</xd:p>
    </xd:desc>
    <xsl:template match="table:table-row[position()>1]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('GenericElement',$driver)"
            mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:desc>
        <xd:p>Linking the DefinedElement element to the table:table-table-cell element</xd:p>
    </xd:desc>
    <xsl:template match="table:table-cell" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('DefinedElement',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:desc>
        <xd:p>Linking the column name getter function to the xml element name getter function</xd:p>
    </xd:desc>
    <xsl:function name="iatxml:get-element-name">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="iatfods:get-column-name($context)"/>
    </xsl:function>

    <xd:desc>
        <xd:p>Linking the cell content getter function to the xml element value getter function</xd:p>
    </xd:desc>
    <xsl:function name="iatxml:get-value">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="iatfods:get-content($context)"/>
    </xsl:function>

</xsl:stylesheet>
