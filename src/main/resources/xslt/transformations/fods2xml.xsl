<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enofods="http://xml.insee.fr/apps/eno/fods"
    xmlns:enoxml="http://xml.insee.fr/apps/eno/xml"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    version="2.0">

    <!-- Importing the different resources -->
    <xsl:import href="../inputs/fods/source.xsl"/>
    <xsl:import href="../outputs/xml/models.xsl"/>
    <xsl:import href="../lib.xsl"/>
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This stylesheet is used to transform fods into xml.</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:strip-space elements="*"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template :</xd:p>
            <xd:p>Only the table:table elements are used.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="//table:table" mode="source"/>
    </xsl:template>

    <xd:desc>
        <xd:p>The table:table element is linked to the 'Root' driver (the root of the generated xml tree).</xd:p>
    </xd:desc>
    <xsl:template match="table:table" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('Root',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:desc>
        <xd:p>The table:table-row element (except the first one which contains the column titles), is linked to the 'GenericElement' driver.</xd:p>
    </xd:desc>
    <xsl:template match="table:table-row[position()>1]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('GenericElement',$driver)"
            mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:desc>
        <xd:p>The table:table-cell element is linked to the 'DefinedElement' driver.</xd:p>
        <xd:p>Except the cells of the first row (column names).</xd:p>
    </xd:desc>
    <xsl:template match="table:table-cell[parent::table:table-row[preceding-sibling::table:table-row]]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('DefinedElement',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:desc>
        <xd:p>Linking the column name getter function to the xml element name getter function.</xd:p>
    </xd:desc>
    <xsl:function name="enoxml:get-element-name">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="enofods:get-column-name($context)"/>
    </xsl:function>

    <xd:desc>
        <xd:p>Linking the content getter function to the xml element value getter function.</xd:p>
    </xd:desc>
    <xsl:function name="enoxml:get-value">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="enofods:get-content($context)"/>
    </xsl:function>

</xsl:stylesheet>
