<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:il="http://xml/insee.fr/xslt/lib" xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
    xmlns:iatfods="http://xml/insee.fr/xslt/apply-templates/fods"
    xmlns:iatxml="http://xml/insee.fr/xslt/apply-templates/xml"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" exclude-result-prefixes="#all"
    version="2.0">

    <xsl:import href="../entrees/fods/source.xsl"/>
    <xsl:import href="../sorties/xml/models.xsl"/>
    <xsl:import href="../lib.xsl"/>

    <xsl:output method="xml" indent="yes"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 6, 2013</xd:p>
            <xd:p>Transforms fods to XML!</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template match="/">
        <xsl:apply-templates select="/" mode="source"/>
    </xsl:template>

    
    <xd:desc>
        <xd:p>A l'élément table, on associe la racine d'un élément</xd:p>
    </xd:desc>
    <xsl:template match="table:table" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('racine',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:desc>
        <xd:p>Au premier table:table-row, on associe rien (elle va contenir le nom des colonnes)</xd:p>
    </xd:desc>
    <xsl:template match="table:table-row[position()=1]" mode="source"/>

    <xd:desc>
        <xd:p>Aux autres table:table-row, on associe l'élément elementGenerique</xd:p>
    </xd:desc>
    <xsl:template match="table:table-row[position()>1]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('elementGenerique',$driver)"
            mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:desc>
        <xd:p>A l'élément table:table-table-cell on associe l'élément elementDefini</xd:p>
    </xd:desc>
    <xsl:template match="table:table-cell" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('elementDefini',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:desc>
        <xd:p>A la fonction de recuperation du nom d'un élément xml, on associe la fonction d'envoi de nom d'une colonne</xd:p>
    </xd:desc>
    <xsl:function name="iatxml:get-nomElement">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="iatfods:get-nomColonne($context)"/>
    </xsl:function>

    <xd:desc>
        <xd:p>A la fonction de recuperation de la valeur d'un élément xml, on associe la fonction d'envoi du contenu d'une cellule</xd:p>
    </xd:desc>
    <xsl:function name="iatxml:get-valeur">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="iatfods:get-contenu($context)"/>
    </xsl:function>

</xsl:stylesheet>
