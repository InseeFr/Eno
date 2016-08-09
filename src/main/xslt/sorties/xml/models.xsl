<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
    xmlns:iatxml="http://xml/insee.fr/xslt/apply-templates/xml" exclude-result-prefixes="#all"
    version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 6, 2013</xd:p>
            <xd:p>Generation of XML!</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:output name="concise-xml" method="xml" indent="no" omit-xml-declaration="yes"
        exclude-result-prefixes="#all"/>

    <xd:doc>
        <xd:desc>
            <xd:p>L'élément de base à matcher</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="racine" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="racine">
            <!-- Il va appeler des éléments enfants qui vont créer une structure xml -->
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Un élément xml générique. Il permet de structurer des niveaux par rapport au
                format d'entrée</xd:p>
            <xd:p>Par exemple, pour un fods, chaque ligne du document va correspondre à un élément générique
                (à part la première ligne)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="elementGenerique" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="elementGenerique">
            <!-- Il va appeler des éléments enfants qui vont créer une structure xml -->
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Un élément xml.</xd:p>
            <xd:p>Il appelle une fonction de nom d'élément, et une fonction pour récupérer une valeur.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="elementDefini" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="nomElement">
            <xsl:apply-templates select="iatxml:get-nomElement($source-context)"/>
        </xsl:variable>
        <xsl:element name="elementDefini">
            <xsl:attribute name="nom" select="$nomElement"/>
            <xsl:apply-templates select="iatxml:get-valeur($source-context)"/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
