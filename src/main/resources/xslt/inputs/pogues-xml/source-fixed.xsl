<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:enopogues="http://xml.insee.fr/apps/eno/in/pogues-xml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xd:doc>
        <xd:desc>
            <xd:p>For each element, the default behaviour is to return empty text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="#all" priority="-1">
        <xsl:text/>
    </xsl:template>
    
    <xsl:template match="*" mode="with-tag">
        <xsl:sequence select="."/>
    </xsl:template>
    
   <!-- <xsl:template match="pogues:Variable" mode="enopogues:get-related-response">
        <xsl:sequence select="//pogues:Response[pogues:CollectedVariableReference = current/@id]"/>
    </xsl:template>-->
    
    <xsl:template match="pogues:Control" mode="enopogues:get-ip-id">
        <xsl:param name="index" tunnel="yes"/>        
        <xsl:value-of select="concat(enopogues:get-id(.),'-IP-',$index)"/>
    </xsl:template>
    
    
    <xsl:template match="pogues:Response" mode="enopogues:get-related-variable">
        <xsl:variable name="idVariable" select="pogues:CollectedVariableReference"/>
        <xsl:sequence select="//pogues:Variable[@id = $idVariable]"/>
    </xsl:template>
    
    <xsl:template match="pogues:Control" mode="enopogues:get-related-variable">
        <xsl:variable name="expressionVariable" select="tokenize(pogues:Expression,'\$')"/>
        <xsl:variable name="variables" select="//pogues:Variables"/>        
        <xsl:sequence select="$variables/pogues:Variable[some $x in $expressionVariable satisfies substring-before($x,' ')=pogues:Name/text()]"></xsl:sequence>
        <!-- 
        <xsl:sequence select="//pogues:Variables/pogues:Variable[some $x in tokenize(pogues:Expression,'\$') satisfies substring-before($x,' ')=pogues:Name/text()]"></xsl:sequence>        
        -->
    </xsl:template>
    
    
</xsl:stylesheet>