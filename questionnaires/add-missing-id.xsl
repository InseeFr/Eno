<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    exclude-result-prefixes="xs xd"
    version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Aug 22, 2017</xd:p>
            <xd:p><xd:b>Author:</xd:b> nirnfv</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/> 
    
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="./*"/>
    </xsl:template>
    
    <xd:doc>
        id attribute
        <xd:desc/>
    </xd:doc>
    <xsl:template match="pogues:Questionnaire | pogues:Declaration | pogues:CodeList | pogues:Code | pogues:Child | pogues:Response">
        <xsl:copy>
            <xsl:if test="not(@id)">
                <xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
            </xsl:if>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="*"/>
        </xsl:copy>    
    </xsl:template>

    <xd:doc>
        id value
        <xd:desc/>
    </xd:doc>
    <xsl:template match="pogues:CodeListReference">
        <xsl:copy>
             <xsl:if test="not(text())">
                <xsl:value-of select="generate-id()"/>
            </xsl:if>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="*"/>
        </xsl:copy>    
    </xsl:template>
    
    <xd:doc>
        no id
        <xd:desc/>
    </xd:doc>
    <xsl:template match="pogues:GoTo | pogues:Survey | pogues:ComponentGroup | pogues:MemberReference | pogues:Datatype | pogues:Dimension | pogues:Label | pogues:Name | pogues:Text | pogues:Expression | pogues:IfTrue | pogues:Value | pogues:MaxLength | pogues:Pattern | pogues:Minimum | pogues:Maximum | pogues:Decimal | pogues:Format | pogues:ResponseStructure | pogues:TotalLabel | pogues:Control | pogues:FailMessage | pogues:CodeLists | pogues:Decimals">
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="*"/>
        </xsl:copy>    
    </xsl:template>
    
    <xsl:template match="node()" >
        <xsl:message>Je vous deteste : <xsl:value-of select="name()"/></xsl:message>
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="*"/>
        </xsl:copy>    
    </xsl:template>
    
</xsl:stylesheet>