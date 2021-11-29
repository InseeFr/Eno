<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" exclude-result-prefixes="xs fn xd xsi "
    version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:variable name="default-parameters-adress" select="'../../../params/default/parameters.xml'"/>
    
    <xsl:variable name="default-parameters" select="document($default-parameters-adress)"/>
    
    <xsl:variable name="root" select="root(.)"/>
    
    <xsl:variable name="out-format" select="$root//Pipeline/OutFormat"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="$default-parameters/*"/>
    </xsl:template>
    
    <xsl:template match="Pipeline | Languages" priority="3">
        <xsl:variable name="name" select="name(.)"/>
        <xsl:choose>
            <xsl:when test="$root//.[name(.) = $name]">
                <xsl:copy-of select="$root//.[name(.) = $name]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="Mode" priority="3">
        <Mode>
    <xsl:choose>
        <xsl:when test="$root/ENOParameters/Mode/text() != ''">
            <xsl:copy-of select="$root/ENOParameters/Mode/text()"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:choose>
                <xsl:when test="$root/ENOParameters/Pipeline/OutFormat = 'xforms'"><xsl:value-of select="'cawi'"/></xsl:when>
                <xsl:when test="$root/ENOParameters/Pipeline/OutFormat = 'ddi'"><xsl:value-of select="'all'"/></xsl:when>
                <xsl:when test="$root/ENOParameters/Pipeline/OutFormat = 'fodt'"><xsl:value-of select="'all'"/></xsl:when>
                <xsl:when test="$root/ENOParameters/Pipeline/OutFormat = 'fo'"><xsl:value-of select="'papi'"/></xsl:when>
            </xsl:choose>
        </xsl:otherwise>
    </xsl:choose>
        </Mode>
    </xsl:template>
     
    <xsl:template
        match="*[ends-with(name(), '-parameters') and not(starts-with(name(), concat($out-format, '-')))]"
        priority="2"/>
    
    <xsl:template match="*[count(.//*) > 0]" priority="1">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*[count(.//*) = 0]" priority="1">
        
        <xsl:variable name="name" select="name(.)"/>
        <xsl:variable name="grand-parent" select="name(../..)"/>
        <xsl:variable name="parent" select="name(..)"/>
        
        <xsl:variable name="node-root-name"
            select="$root//*[name(.) = $name and name(..) = $parent and name(../..) = $grand-parent]"/>
        
        <xsl:choose>
            <xsl:when test="$node-root-name/text() != ''">
                <xsl:copy-of select="$node-root-name"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
</xsl:stylesheet>
