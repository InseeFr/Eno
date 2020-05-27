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

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="h:label">
        <xsl:variable name="bindingsDependency" as="xs:boolean">
            <xsl:variable name="temp" select="ancestor::h:components[1]/h:bindingsDependency"/>
            <xsl:choose>
                <xsl:when test="$temp!=''"><xsl:value-of select="$temp"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="false()"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <label>
            <xsl:choose>
                <xsl:when test="not($bindingsDependency)">
                    <xsl:value-of select="substring(.,2,string-length(.)-2)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </label>
    </xsl:template>

    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

</xsl:stylesheet>