<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:barcode="http://barcode4j.krysalis.org/ns"
    exclude-result-prefixes="xs"
    version="2.0">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Template de racine, on applique les templates de tous les enfants</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    <xd:doc>
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
    
    <xsl:template match="barcode:barcode/@message">
        <xsl:variable name="descendant-id" select="ancestor::pageViewport/descendant::block[@prod-id][1]/@prod-id"/>
        <xsl:variable name="page-id">
            <xsl:choose>
                <xsl:when test="$descendant-id !=''">
                    <xsl:analyze-string select="$descendant-id" regex="^(.+)-1$">
                        <xsl:matching-substring>
                            <xsl:value-of select="regex-group(1)"/>
                        </xsl:matching-substring>
                        <xsl:non-matching-substring>
                            <xsl:analyze-string select="$descendant-id" regex="^(.+)-([0-9])+$">
                                <xsl:matching-substring>
                                    <xsl:value-of select="concat(regex-group(1),'-0')"/>
                                </xsl:matching-substring>
                                <xsl:non-matching-substring>
                                    <xsl:value-of select="$descendant-id"/>
                                </xsl:non-matching-substring>
                            </xsl:analyze-string>
                        </xsl:non-matching-substring>
                    </xsl:analyze-string>                        
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'no-id-page'"/>
                </xsl:otherwise>
            </xsl:choose>                
        </xsl:variable>

        <xsl:attribute name="message" select="replace(.,'#page-id#',$page-id)"/>
    </xsl:template>
    
</xsl:stylesheet>