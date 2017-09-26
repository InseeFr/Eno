<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:enofods="http://xml.insee.fr/apps/eno/fods"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>A library of getter functions for fods with their implementations for different elements.</xd:p>
            <xd:p>Getters implemented :</xd:p>
            <xd:ul>
                <xd:li>get-content : send the cell content back</xd:li>
                <xd:li>get-column-name : send the column name of a cell back based on the first line of the table (supposed to be a header line)</xd:li>
            </xd:ul>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that gets the content from a fods element.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofods:get-content">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enofods:get-content"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>For a table-cell object, the funtion will return the text inside the cell.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="table:table-cell" mode="enofods:get-content">
        <xsl:value-of select="text:p/text()"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that gets the name of a column.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofods:get-column-name">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enofods:get-column-name"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>For a table-cell object, the function will return the text of the first table-cell of the same column.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="table:table-cell" mode="enofods:get-column-name">
        <!-- The index of the cell is calculated within the row -->
        <xsl:variable name="index">
            <xsl:value-of select="count(preceding-sibling::table:table-cell)+1"/>
        </xsl:variable>
        <!-- The text of the cell with the same index in the first line is returned -->
        <xsl:value-of
            select="ancestor::table:table/table:table-row[1]/table:table-cell[position()=$index]/text:p/text()"
        />
    </xsl:template>

</xsl:stylesheet>
