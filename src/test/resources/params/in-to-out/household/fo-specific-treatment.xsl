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

    <xsl:template match="fo:block[@id='NATIONAISS' or @id='PAYSNAISS']">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="#current"/>
            <fo:block color="black" font-weight="normal" font-size="10pt" padding="1mm">
                <xsl:for-each select="1 to 28">
                    <fo:external-graphic src="mask_number.png"/>
                </xsl:for-each>
            </fo:block>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fo:block[@id='DEPTFP']">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="#current"/>
            <fo:block color="black" font-weight="normal" font-size="10pt" padding="1mm">
                <xsl:for-each select="1 to 3">
                    <fo:external-graphic src="mask_number.png"/>
                </xsl:for-each>
            </fo:block>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:block[@id='EMPL_PAR']//fo:table-row/fo:table-cell[1]">
        <xsl:copy>
            <xsl:attribute name="width" select="'25mm'"/>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fo:block[@id=('EMPL_PAR')]//fo:table-row/fo:table-cell[2]/fo:block/fo:inline" mode="#all">
        <fo:block>
            <fo:inline>
                <xsl:apply-templates select="node()" mode="#current"/>    
            </fo:inline>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="fo:block[@id=('EMPL_PAR')]//fo:table-row" mode="#all">
        <xsl:copy>
            <xsl:attribute name="height" select="'20mm'"/>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>