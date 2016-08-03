<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:il="http://xml/insee.fr/xslt/lib" xmlns:iat="http://xml/insee.fr/xslt/apply-templates" exclude-result-prefixes="#all" version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Apr 9, 2013</xd:p>
            <xd:p><xd:b>Author:</xd:b> vdv</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>Appends a new empty element in a fragment as a last child of $target</xd:desc>
    </xd:doc>
    <xsl:function name="il:append-empty-element" as="element()">
        <xsl:param name="source" as="xs:string"/>
        <xsl:param name="target" as="item()"/>
        <xsl:variable name="new-element" as="element()">
            <xsl:element name="{$source}">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('_', count(root($target)//*))"/>
                </xsl:attribute>
            </xsl:element>
        </xsl:variable>
        <xsl:variable name="new-fragment">
            <xsl:apply-templates select="$target/root()" mode="il:append">
                <xsl:with-param name="source" select="$new-element" tunnel="yes"/>
                <xsl:with-param name="target" select="$target" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:sequence select="$new-fragment//*[@id=$new-element/@id]"/>
    </xsl:function>

    <xsl:template match="/|*" mode="il:append">
        <xsl:param name="source" as="item()" tunnel="yes"/>
        <xsl:param name="target" as="item()" tunnel="yes"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()" mode="il:append"/>
            <xsl:if test=". is $target">
                <xsl:copy-of select="$source"/>
            </xsl:if>
        </xsl:copy>
    </xsl:template>


    <xsl:function name="il:is-rich-content" as="xs:boolean">
        <xsl:param name="node-set"/>
        <xsl:sequence select="if ($node-set instance of xs:string) then false() else boolean($node-set//node())"/>
    </xsl:function>


    <!-- Pour faire apparaître un arbre xml comme un chaîne texte -->
    <xsl:function name="il:serialize" as="xs:string">
        <!-- On a un noeud en entrée -->
        <xsl:param name="node-set"/>
        <!-- On crée une variable -->
        <xsl:variable name="rooted">
            <root>
                <xsl:choose>
                    <!-- Si le noeud n'est en fait que du texte, on recopie -->
                    <xsl:when test="$node-set instance of xs:string">
                        <xsl:copy-of select="$node-set"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- S'il s'agit d'un véritable arbre xml, on enlève les balises et on les met en tant que texte -->
                        <xsl:apply-templates select="$node-set" mode="il:remove-xml"/>
                    </xsl:otherwise>
                </xsl:choose>
            </root>
        </xsl:variable>
        <!-- Et on renvoie le texte -->
        <xsl:sequence select="$rooted/root"/>
    </xsl:function>

    <!-- Pour transformer le xml en texte, pour les attributs -->
    <xsl:template match="@*" mode="il:remove-xml">
        <xsl:text> </xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text>="</xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>"</xsl:text>
    </xsl:template>

    <!-- Lorsqu'il s'agit déjà d'un texte, on le renvoie, il n'y a rien à faire -->
    <xsl:template match="text()" mode="il:remove-xml">
        <xsl:value-of select="."/>
    </xsl:template>

    <!-- on textualise le noeud qui n'a pas d'enfants -->
    <xsl:template match="*[not(child::node())]" mode="il:remove-xml">
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:apply-templates select="@*" mode="il:remove-xml"/>
        <xsl:text>/&gt;</xsl:text>
    </xsl:template>

    <!-- on textualise le noeud qui a des enfants -->
    <xsl:template match="*[child::node()]" mode="il:remove-xml">
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:apply-templates select="@*" mode="il:remove-xml"/>
        <xsl:text>&gt;</xsl:text>
        <xsl:apply-templates select="node()" mode="il:remove-xml"/>
        <xsl:text>&lt;/</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:text>&gt;</xsl:text>
    </xsl:template>

    <!-- On dégage les instructions et les commentaires -->
    <xsl:template match="processing-instruction()|comment()" mode="il:remove-xml"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Fonction pour récupérer les "enfants" d'un élément, elle est commune à toutes les entrées et sorties.</xd:p>
            <xd:p>En effet toute sortie demandera la poursuite de l'arbre en entrée et toute entrée devra avoir une fonction décrivant le parcours de l'arbre.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iat:child-fields" as="node()*">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iat:child-fields"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Par défaut on parcourt l'arbre xml classiquement en récupérant les enfants d'un élément.</xd:p>
            <xd:p>Mais la fonction est surchargeable si on souhaite parcourir un arbre de manière non classique.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="@*|node()" mode="iat:child-fields" as="node()*">
        <xsl:sequence select="./(@*|node())"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Par défaut si un noeud ne renvoie rien, on passera à ses éléments enfants</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="source">
        <xsl:apply-templates select="iat:child-fields(.)" mode="source"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Par défaut les éléments autre qu'un noeud ne déclenchent rien côté source</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="@*|text()|processing-instruction()|comment()" mode="source"/>

</xsl:stylesheet>
