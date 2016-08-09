<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:iatfods="http://xml/insee.fr/xslt/apply-templates/fods"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" exclude-result-prefixes="xs xd"
    version="2.0">
    <xsl:output method="xml" indent="yes"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 6, 2013</xd:p>
            <xd:p>Generation from fods</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>On démarrera une transformation depuis un fods par l'élément table:table</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/" mode="source">
        <xsl:apply-templates select="//table:table" mode="source"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Fonction qui donne le contenu d'un élément d'un fods</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iatfods:get-contenu">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatfods:get-contenu"/>
    </xsl:function>

    <!-- Elle n'est appelée que par l'objet cellule ici-->
    <xsl:template match="table:table-cell" mode="iatfods:get-contenu">
        <xsl:value-of select="text:p/text()"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Fonction qui donne le nom d'une colonne</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iatfods:get-nomColonne">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatfods:get-nomColonne"/>
    </xsl:function>

    <!-- Elle n'est appelée que par l'objet cellule ici, les titres sont situés sur la première ligne du document-->
    <xsl:template match="table:table-cell" mode="iatfods:get-nomColonne">
        <xsl:variable name="index">
            <xsl:value-of select="count(preceding-sibling::table:table-cell)+1"/>
        </xsl:variable>
        <xsl:value-of
            select="ancestor::table:table/table:table-row[1]/table:table-cell[position()=$index]/text:p/text()"
        />
    </xsl:template>

</xsl:stylesheet>
