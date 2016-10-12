<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:iatfods="http://xml/insee.fr/xslt/apply-templates/fods"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" exclude-result-prefixes="xs xd"
    version="2.0">

    <!-- This xsl stylesheet is used in the fods2xsl target (imported by fods2xml.xsl)-->
    <!-- Its purpose is to retrieve several elements in a fods file (like cell content, column name etc) -->
    <!-- The information retrieved will then be used in fods2xml.xsl in order to create the output xml file -->

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 6, 2013</xd:p>
            <xd:p>Generation from fods</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>Starting the transformation from a fods file by the element : table:table</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/" mode="source">
        <xsl:apply-templates select="//table:table" mode="source"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that gets the element content from a fods</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iatfods:get-content">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatfods:get-content"/>
    </xsl:function>

    <!-- Only called by the cell object-->
    <xsl:template match="table:table-cell" mode="iatfods:get-content">
        <xsl:value-of select="text:p/text()"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that gets the name of a column</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iatfods:get-column-name">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatfods:get-column-name"/>
    </xsl:function>

    <!-- Only called by the cell object here, titles are located on the first line of the document-->
    <xsl:template match="table:table-cell" mode="iatfods:get-column-name">
        <xsl:variable name="index">
            <xsl:value-of select="count(preceding-sibling::table:table-cell)+1"/>
        </xsl:variable>
        <xsl:value-of
            select="ancestor::table:table/table:table-row[1]/table:table-cell[position()=$index]/text:p/text()"
        />
    </xsl:template>

</xsl:stylesheet>
