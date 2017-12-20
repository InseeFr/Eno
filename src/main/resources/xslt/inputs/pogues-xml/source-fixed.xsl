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
    
    <xsl:template match="//pogues:Child[@xsi:type='QuestionType']" mode="with-tag">
        <xsl:sequence select="."/>
    </xsl:template>
    
    <xsl:template match="//pogues:Child[@xsi:type='SequenceType']" mode="with-tag">
        <xsl:sequence select="."/>
    </xsl:template>
    
    <xsl:template match="//pogues:Declaration" mode="with-tag">
        <xsl:sequence select="."/>
    </xsl:template>
    
    <xsl:template match="//pogues:IfThenElse" mode="with-tag">
        <xsl:sequence select="."/>
    </xsl:template>
    
    <xsl:template match="pogues:Variable" mode="enopogues:get-related-question">
        <xsl:param name="id" tunnel="yes"/>
        <xsl:sequence select="//pogues:Child[pogues:Response/pogues:CollectedVariableReference = $id]"/>
    </xsl:template>
    
</xsl:stylesheet>