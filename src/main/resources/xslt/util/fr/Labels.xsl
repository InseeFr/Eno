<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
    xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:boom="boom"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="#all">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Aug 31, 2017</xd:p>
            <xd:p><xd:b>Author:</xd:b> nirnfv</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    <xd:doc>
        suppression of "^##{" and "}$" in Labels
        <xd:desc/>
    </xd:doc>  
    <xsl:template match="label[contains(text(),'{')]">
        <xsl:copy>
            <xsl:copy-of select="@* | comment() | processing-instruction()"/> 
            <xsl:value-of select="substring-after(.,'}')"/>
            <xsl:apply-templates select="./*"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="node()">
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="./*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>