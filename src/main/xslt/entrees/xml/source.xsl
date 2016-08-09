<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:iatxml="http://xml/insee.fr/xslt/apply-templates/xml" exclude-result-prefixes="xs xd"
    version="2.0">
    <xsl:output method="xml" indent="yes"/>
    

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 6, 2013</xd:p>
            <xd:p>Generation from xml</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>On démarrera une transformation depuis xml par l'élément racine</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/" mode="source">
        <xsl:apply-templates select="racine" mode="source"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Fonction qui donne le nom d'un élément (pas le nom xml mais la valeur de
                l'attribut nom du noeud xml représentant l'élément</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iatxml:get-nomElement" as="xs:string">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-nomElement"/>
    </xsl:function>

    <!-- Elle n'est appelée que par l'objet elementDefini ici-->
    <xsl:template match="elementDefini" mode="iatxml:get-nomElement" as="xs:string">
        <xsl:value-of select="@nom"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Fonction qui donne la valeur d'un élément du fichier xml</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iatxml:get-valeur" as="xs:string">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-valeur"/>
    </xsl:function>

    <!-- Elle n'est appelée que par l'objet elementDefini ici -->
    <xsl:template match="elementDefini" mode="iatxml:get-valeur" as="xs:string">
        <xsl:value-of select="text()"/>
    </xsl:template>

</xsl:stylesheet>
