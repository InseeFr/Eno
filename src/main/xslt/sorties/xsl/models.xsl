<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
    xmlns:iatxsl="http://xml/insee.fr/xslt/apply-templates/xsl"
    exclude-result-prefixes="xd" version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 6, 2013</xd:p>
            <xd:p>Generation of XSL!</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:output name="concise-xml" method="xml" indent="no" omit-xml-declaration="yes"
        exclude-result-prefixes="#all"/>

    <xd:doc>
        <xd:desc>
            <xd:p>L'élément de base à matcher</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="feuille" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <!-- On crée l'élément racine d'une feuille xsl -->
        <xsl:element name='xsl:stylesheet'>
            <!-- Il va appeler des éléments enfants qui vont créer une structure xml -->
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Un template</xd:p>
            <xd:p>Il appelle une fonction pour récupérer l'élément permettant de générer de la documentation.</xd:p>
            <xd:p>Il appelle une fonction pour récupérer le xpath à matcher.</xd:p>
            <xd:p>Il appelle une fonction pour récupérer le driver à lancer associé au xpath.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="template" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="'source'"/>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'driver'"/>
                <xsl:attribute name="tunnel" select="'yes'"/>
                <xsl:element name="driver"/>
            </xsl:element>
            <xsl:element name="xsl:apply-templates">
                <xsl:attribute name="select"
                    select="concat('il:append-empty-element(''',normalize-space(iatxsl:get-driver($source-context)),''',$driver)')"/>
                <xsl:attribute name="mode" select="'model'"/>
                <xsl:element name="xsl:with-param">
                    <xsl:attribute name="name" select="'source-context'"/>
                    <xsl:attribute name="select" select="'.'"/>
                    <xsl:attribute name="tunnel" select="'yes'"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Une fonction</xd:p>
            <xd:p>Il appelle une fonction pour récupérer l'élément permettant de générer de la documentation.</xd:p>
            <xd:p>Il appelle une fonction pour récupérer le nom de la fonction.</xd:p>
            <xd:p>Il appelle une fonction pour récupérer la valeur de la fonction associée côté source.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="fonctionPassage" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:function">
            <xsl:attribute name="name" select="normalize-space(iatxsl:get-fonction-sortie($source-context))"/>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'context'"/>
                <xsl:attribute name="as" select="'item()'"/>
            </xsl:element>
            <xsl:variable name="parameters" select="iatxsl:get-parameters($source-context)" as="xs:string +"/>
            <xsl:if test="$parameters!=''">
                <xsl:for-each select="$parameters">
                    <xsl:element name="xsl:param">
                        <xsl:attribute name="name" select="current()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:if>
            <xsl:variable name="parametresFonction">
                <xsl:text>$context</xsl:text>
                <xsl:if test="$parameters!=''">
                    <xsl:for-each select="$parameters">
                        <xsl:text>,$</xsl:text>
                        <xsl:value-of select="."/>
                    </xsl:for-each>
                </xsl:if>
            </xsl:variable>
            <xsl:element name="xsl:sequence">
                <xsl:attribute name="select"
                    select="concat(normalize-space(iatxsl:get-fonction-entree($source-context)),'(',$parametresFonction/text(),')')"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Une fonction non supportée</xd:p>
            <xd:p>Fonction encore non supportée.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="fonctionNonSupportee" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:function">
            <xsl:attribute name="name" select="normalize-space(iatxsl:get-fonction-sortie($source-context))"/>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'context'"/>
                <xsl:attribute name="as" select="'item()'"/>
            </xsl:element>
            <xsl:variable name="parameters" select="iatxsl:get-parameters($source-context)" as="xs:string +"/>
            <xsl:if test="$parameters!=''">
                <xsl:for-each select="$parameters">
                    <xsl:element name="xsl:param">
                        <xsl:attribute name="name" select="current()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:if>
            <xsl:element name="xsl:text"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Une implémentation simple d'une fonction source</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="implementation_simple" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="normalize-space(iatxsl:get-mode-xpath($source-context))"/>
            <xsl:element name="xsl:value-of">
                <xsl:attribute name="select" select="normalize-space(iatxsl:get-match($source-context))"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Une implémentation complexe d'une fonction source</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="implementation_complexe" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="normalize-space(iatxsl:get-mode-xpath($source-context))"/>
            <xsl:element name="xsl:apply-templates">
                <xsl:attribute name="select" select="normalize-space(iatxsl:get-match($source-context))"/>
                <xsl:attribute name="mode" select="normalize-space(iatxsl:get-mode-match($source-context))"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Une implémentation d'une fonction source qui ne renverra rien</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="implementation_vide" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="normalize-space(iatxsl:get-mode-xpath($source-context))"/>
            <xsl:element name="xsl:text"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Une fonction définie pour la source</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="fonctionSource" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="nomFonction">
            <xsl:value-of select="normalize-space(iatxsl:get-fonction($source-context))"/>
        </xsl:variable>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:function">
            <xsl:attribute name="name" select="$nomFonction"/>
            <xsl:variable name="type" select="iatxsl:get-as($source-context)"/>
            <xsl:if test="$type!=''">
                <xsl:attribute name="as" select="$type"/>
            </xsl:if>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'context'"/>
                <xsl:attribute name="as" select="'item()'"/>
            </xsl:element>
            <xsl:variable name="parameters" select="iatxsl:get-parameters($source-context)" as="xs:string +"/>
            <xsl:if test="$parameters!=''">
                <xsl:for-each select="$parameters">
                    <xsl:element name="xsl:param">
                        <xsl:attribute name="name" select="current()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:if>
            <xsl:element name="xsl:apply-templates">
                <xsl:attribute name="select" select="'$context'"/>
                <xsl:attribute name="mode" select="$nomFonction"/>
                <xsl:if test="$parameters!=''">
                    <xsl:for-each select="$parameters">
                        <xsl:element name="xsl:with-param">
                            <xsl:attribute name="name" select="current()"/>
                            <xsl:attribute name="select" select="concat('$',current())"/>
                            <xsl:attribute name="tunnel" select="'yes'"/>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:if>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Un template permettant la récupération d'enfants</xd:p>
            <xd:p>Il appelle une fonction pour récupérer l'élément permettant de générer de la documentation.</xd:p>
            <xd:p>Il appelle une fonction pour récupérer le xpath à matcher (parent).</xd:p>
            <xd:p>Il appelle une fonction pour récupérer le xpath renvoyé (les enfants).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="recuperationEnfants" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-parent($source-context))"/>
            <xsl:attribute name="mode" select="'iat:child-fields'"/>
            <xsl:attribute name="as" select="'node()*'"/>
            <xsl:element name="xsl:sequence">
                <xsl:attribute name="select"
                    select="normalize-space(iatxsl:get-enfants($source-context))"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>

</xsl:stylesheet>
