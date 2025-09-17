<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This xslt stylesheet is used to clean tables in OpenDocument xml files.</xd:p>
            <xd:p>It removes empty lines, creates the right number of cells in each row, and adds an attribute to know the position of a cell in a row.</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:strip-space elements="*"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template.</xd:p>
            <xd:p>At first, the empty lines are removed from the fods and the cells are repeated as many times as the table:number-columns-repeated attribute.</xd:p>
            <xd:p>Then, a fictional attribute cell-position (that doesn't exist in fods format) is added in the output.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:variable name="preformatted">
            <xsl:copy>
                <xsl:apply-templates select="node()"/>
            </xsl:copy>
        </xsl:variable>
        <!-- Then applying the templates that will add the position -->
        <xsl:apply-templates select="$preformatted" mode="add-position"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute (and both modes), simply copying to the
                output.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Removing empty lines from the fods file.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="table:table-row[normalize-space(string(.))='']"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Removing the table:number-columns-repeated attribute from the fods file.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="@table:number-columns-repeated" mode="#all"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Elements with @table:number-columns-repeated are copied as many times as the attribute's value, through the 'repetition' template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[@table:number-columns-repeated &gt; 1]">
        <xsl:call-template name="repetition">
            <xsl:with-param name="repetition-number" select="@table:number-columns-repeated"
                tunnel="yes"/>
            <xsl:with-param name="element" select="." tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>The template to copy many times a same element.</xd:p>
        </xd:desc>
        <xd:param name="repetition-number">The number of times the element must be copied.</xd:param>
        <xd:param name="element">The element to copy.</xd:param>
        <xd:param name="index">The index is incremented at each copy.</xd:param>
    </xd:doc>
    <xsl:template name="repetition">
        <xsl:param name="repetition-number" select="1" tunnel="yes"/>
        <xsl:param name="element" tunnel="yes"/>
        <xsl:param name="index" select="1"/>
        <xsl:apply-templates select="$element" mode="copy"/>
        <xsl:if test="$repetition-number - $index &gt; 0">
            <xsl:call-template name="repetition">
                <xsl:with-param name="index" select="$index + 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    

    <xd:doc>
        <xd:desc>
            <xd:p>A different mode to add postions to cell descendants.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="table:table" mode="add-position">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="table:table-row" mode="add-position"/>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A different mode to add postions to cell descendants.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="table:table-row" mode="add-position">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*" mode="add-position"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Add a cell position to children of table:table-row.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="table:table-row/*" mode="add-position">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="table:cell-position" select="position()"/>
            <xsl:copy-of select="*"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
