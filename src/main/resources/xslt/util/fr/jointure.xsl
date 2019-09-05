<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="dossier"/>
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de racine</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:message><xsl:value-of select="$dossier"/></xsl:message>
        <total>
            <xsl:for-each select="collection(concat('file:///', replace($dossier, '\\' , '/'), '?select=form.xhtml;recurse=yes'))/xhtml:html">
            <xsl:copy-of select="."/>
        </xsl:for-each>
        </total>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de base pour tous les éléments et tous les attributs, on recopie simplement en sortie</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

</xsl:transform>
