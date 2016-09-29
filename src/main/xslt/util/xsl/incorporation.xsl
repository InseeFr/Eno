<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


    <!-- This stylesheet is used in the ENOPreprocessing target after the xsl files (functions.xsl etc.) are created. -->
    <!-- In order to create ddi2fr.xsl and source.xsl (inputs/ddi), the xsl files (converted from fods) need to be merged -->
    <!-- together. Therefore, the purpose of this stylesheet is to copy the content of an input file into an output file. -->

    <!-- Parameter given in the build-non-regression.xml -->
    <xsl:param name="generated-file"/>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Variable that concatenates the DDI</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="templates">
        <xsl:copy-of select="document($generated-file)"/>
    </xsl:variable>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Root template</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="xsl:stylesheet"/>
    </xsl:template>
    
    <xsl:template match="xsl:stylesheet">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()"/>
            <xsl:apply-templates select="$templates/xsl:stylesheet/node()"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply copying to the output file</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

</xsl:transform>
