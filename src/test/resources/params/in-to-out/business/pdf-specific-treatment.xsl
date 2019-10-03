<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xs"
    version="2.0">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de racine, on applique les templates de tous les enfants</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de base pour tous les éléments et tous les attributs, on recopie
                simplement en sortie</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*" mode="#all" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>

   <xsl:template match="fo:block[starts-with(@id,'DOMAINES_ACT')]//fo:table-header"/>
    
    <xsl:template match="fo:block[starts-with(@id,'DOMAINES_ACT')]//fo:table-body/fo:table-row/fo:table-cell[1]">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="width">173mm</xsl:attribute>
            
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
        
    </xsl:template>
    
    <xsl:template match="fo:block[starts-with(@id,'DOMAINES_ACT')]//fo:table-body/fo:table-row/fo:table-cell[2]">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="width">7mm</xsl:attribute>
            
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
        
    </xsl:template>
        
    <xsl:template match="fo:block[starts-with(@id,'DOMAINES_ACT')]//fo:external-graphic"/>
    <xsl:template match="fo:block[starts-with(@id,'DOMAINES_ACT')]//fo:table-row/fo:table-cell[1]//fo:block/@padding">
        <xsl:attribute name="padding" select="'2px'"/>
<!--        3px ne passe pas après le module courrier (dépasse dans les marges)-->
    </xsl:template>
    
<!--    sert à qqch ??-->
    <xsl:template match="fo:block[starts-with(@id,'DOMAINES_ACT')]//fo:table-row/fo:table-cell[2]//fo:block/@padding">
        <xsl:attribute name="padding" select="'0px'"/>
    </xsl:template>
    
    <xsl:template match="fo:block[@id='DOMAINES_ACT02']//fo:table-body">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>  
            <xsl:apply-templates select="//fo:block[@id='DOMAINES_ACT03']//fo:table-body/*"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="fo:block[@id='DOMAINES_ACT03']"/>

</xsl:stylesheet>