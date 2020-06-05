<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:fn="http://www.w3.org/2005/xpath-functions" 
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno" 
    xmlns:enojs="http://xml.insee.fr/apps/eno/out/js"
    xmlns:h="http://xml.insee.fr/schema/applis/lunatic-h"
    xmlns="http://xml.insee.fr/schema/applis/lunatic-h"
    exclude-result-prefixes="xs fn xd eno enojs h" version="2.0">	
    
    <xsl:output indent="yes"/>
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>An xslt stylesheet who transforms an input into js through generic driver templates.</xd:p>
            <xd:p>The real input is mapped with the drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
        
    <xsl:template match="h:Questionnaire">
        <Questionnaire>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
            <xsl:apply-templates select="descendant::h:variables"/>
        </Questionnaire>
    </xsl:template>
    
    <xsl:template match="h:components">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:cells">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:variables">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="./*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>