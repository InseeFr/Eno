<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This xsl stylesheet is used to copy a generated xsl stylesheet into an existing one.</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:strip-space elements="*"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The generated xsl stylesheet to be copied.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="generated-file"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The debug mode changes the models.xsl imported.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="debug" select="false()"/>
     
    <xsl:variable name="debug2" select="xs:boolean($debug)"/>
    
    
    <xd:doc>
        <xd:desc>
            <xd:p>The generated xsl stylesheet is charged as a document.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="templates">
        <xsl:copy-of select="document($generated-file)"/>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template. The xsl is applied on the existing xsl stylesheet.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="xsl:stylesheet"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply copying to the output file.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The two xsl stylesheets are merged.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xsl:stylesheet">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()"/>
            <!-- The content of the generated xsl stylesheet is copied within the existing one. -->
            <xsl:apply-templates select="$templates/xsl:stylesheet/node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xsl:import[contains(@href,'models.xsl')]">     
        <xsl:copy>
            <xsl:attribute name="href" select="if($debug2=true()) then(concat(substring-before(@href,'.xsl'),'-debug.xsl')) else(@href)"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>