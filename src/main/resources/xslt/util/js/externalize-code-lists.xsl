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
        
    <xsl:template match="h:Questionnaire">
        <Questionnaire>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="h:label"/>
            <xsl:apply-templates select="h:components"/>
            <xsl:apply-templates select="descendant::h:codeLists[not(@id = preceding::h:codeLists/@id)]"/>
            <xsl:apply-templates select="h:variables"/>
        </Questionnaire>
    </xsl:template>
    
    <xsl:template match="h:components[@xsi:type='Sequence' or @xsi:type='Subsequence']">
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="./*[not(descendant-or-self::h:components)]"/>
            <xsl:apply-templates select="h:components"/>
        </components>
    </xsl:template>
        
    <xsl:template match="h:components">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="./*[not(self::h:codeLists)]"/>
            <xsl:if test="h:codeLists">
                <codeListReference>
                    <xsl:value-of select="h:codeLists/@id"/>
                </codeListReference>
            </xsl:if>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:components[@xsi:type='Table']">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="./*[not(self::h:columns)]"/>
            <xsl:apply-templates select="h:columns"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:columns">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="./*[not(self::h:codeLists)]"/>
            <xsl:if test="h:codeLists">
                <codeListReference>
                    <xsl:value-of select="h:codeLists/@id"/>
                </codeListReference>
            </xsl:if>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:codeLists | h:variables">
        <xsl:copy-of select="."/>
    </xsl:template>
</xsl:stylesheet>