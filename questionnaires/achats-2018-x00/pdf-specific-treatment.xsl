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
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Correct total -->
    
    <xsl:template match="fo:table-cell[@background-color='#CCCCCC']">
        <fo:table-cell text-align="left"
            border-color="black"
            border-style="solid"
            padding="1mm">
            <fo:block text-align="right" padding-top="0px" padding-bottom="0px">
                <fo:block color="black"
                    font-weight="normal"
                    font-size="10pt"
                    padding="1mm"
                    padding-bottom="0mm"
                    padding-top="0mm">
                    <xsl:for-each select="1 to 7">
                        <fo:external-graphic src="mask_number.png"/>
                    </xsl:for-each>
                    <fo:inline>k€</fo:inline>
                </fo:block>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    <!-- correct other - give details -->
    
    <!-- remove "Précisez" cell -->
    
    <xsl:template match="fo:table-row[starts-with(fo:table-cell[1]/descendant::*[text()][1]/text(),'Autres') and starts-with(fo:table-cell[last()]/descendant::*[text()][1]/text(),'Précisez')]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()[not(name()='fo:table-cell') or following-sibling::fo:table-cell]"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- add "Précisez" cell in "Autres" cell -->
    
    <xsl:template match="fo:block[text()='Autres']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <fo:block>
                <xsl:apply-templates select="ancestor::fo:table-row/fo:table-cell[last()]/fo:block/fo:block[1]/@*"/>
                <xsl:value-of select="concat('Autres : ',ancestor::fo:table-row/fo:table-cell[last()]/fo:block/fo:block[1]//text())"/>
            </fo:block>
            <xsl:apply-templates select="ancestor::fo:table-row/fo:table-cell[last()]/fo:block/fo:block[2]"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fo:inline[fo:inline[starts-with(text(),'Autre')]]">
        <fo:block>
            <fo:block>
                <xsl:apply-templates select="ancestor::fo:table-row/fo:table-cell[last()]/fo:block/fo:block[1]/@*"/>
                <xsl:value-of select="concat(fo:inline/text(),' : ',ancestor::fo:table-row/fo:table-cell[last()]/fo:block/fo:block[1]//text())"/>
            </fo:block>
            <xsl:apply-templates select="ancestor::fo:table-row/fo:table-cell[last()]/fo:block/fo:block[2]"/>            
        </fo:block>
    </xsl:template>
    
    <xsl:template match="@min-width">
        <xsl:attribute name="{name()}">
            <xsl:choose>
                <xsl:when test=".='200mm'">
                    <xsl:value-of select="'100mm'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>
    
</xsl:stylesheet>