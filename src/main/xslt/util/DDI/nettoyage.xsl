<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2" xmlns:g="ddi:group:3_2"
    xmlns:s="ddi:studyunit:3_2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="no" encoding="UTF-8"/>
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

    <!-- A DEGAGER UN JOUR, cette modif a pour effet de pallier à un bug BOOTSTRAP/ORBEON à signaler -->
    <!-- Les balises br créent une erreur lorsque le label dans lequel elles sont contenues est sujet à un comportement dynamique
    Exemple: dans un label d'un relevant-->
    <!-- On va les transformer en span du coup (un span auquel on associe un saut de ligne en css)
    Bref, c'est pas très propre-->
    <xsl:template match="xhtml:p[xhtml:br]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:for-each-group select="node()" group-ending-with="xhtml:br">
                <xhtml:span class="bloc">
                    <xsl:for-each select="current-group()[not(name()='xhtml:br')]">
                        <xsl:apply-templates select="."/>
                    </xsl:for-each>
                </xhtml:span>
            </xsl:for-each-group>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="r:CommandContent">
        <xsl:variable name="commande">
            <xsl:value-of select="."/>
        </xsl:variable>
        <xsl:copy>
            <!-- On crée une liste d'anciens identifiants -->
            <xsl:variable name="anciensIdentifiants">
                <xsl:for-each select="parent::r:Command/r:InParameter">
                    <!-- On les trie pour avoir les plus longs en premier
                    Il arrive que la chaîne correspondant à un identifiant soit contenue dans un autre ce qui crée des soucis
                    En les triant ainsi, ce problème est résolu -->
                    <xsl:sort select="string-length(r:ID)" order="descending"/>
                    <xsl:copy-of select="r:ID"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:variable name="nouveauxIdentifiants">
                <!-- On crée une liste de nouveaux identifiants -->
                <xsl:for-each select="parent::r:Command/r:InParameter">
                    <xsl:sort select="string-length(r:ID)" order="descending"/>
                    <xsl:variable name="ancienIdentifiant">
                        <xsl:value-of select="r:ID"/>
                    </xsl:variable>
                    <!-- On récupère l'id du paramètre de la question source -->
                    <xsl:copy-of
                        select="parent::r:Command/r:Binding[r:TargetParameterReference/r:ID=$ancienIdentifiant]/r:SourceParameterReference/r:ID"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:call-template name="modificationCommande">
                <xsl:with-param name="anciensIdentifiants" select="$anciensIdentifiants"/>
                <xsl:with-param name="nouveauxIdentifiants" select="$nouveauxIdentifiants"/>
                <xsl:with-param name="commande" select="$commande"/>
                <xsl:with-param name="min" select="number(1)"/>
                <xsl:with-param name="max" select="count($anciensIdentifiants/r:ID)"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="modificationCommande">
        <xsl:param name="anciensIdentifiants"/>
        <xsl:param name="nouveauxIdentifiants"/>
        <xsl:param name="commande"/>
        <xsl:param name="min"/>
        <xsl:param name="max"/>
        <xsl:variable name="nouvelIdentifiant">
            <xsl:value-of select="concat('//',$nouveauxIdentifiants/r:ID[$min])"/>
        </xsl:variable>
        <xsl:variable name="commandeModifiee">
            <xsl:value-of select="replace($commande,$anciensIdentifiants/r:ID[$min],$nouvelIdentifiant)"/>
        </xsl:variable>
        <xsl:if test="number($min) &lt; number($max)">
            <xsl:call-template name="modificationCommande">
                <xsl:with-param name="anciensIdentifiants" select="$anciensIdentifiants"/>
                <xsl:with-param name="nouveauxIdentifiants" select="$nouveauxIdentifiants"/>
                <xsl:with-param name="commande" select="$commandeModifiee"/>
                <xsl:with-param name="min" select="$min + 1"/>
                <xsl:with-param name="max" select="$max"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="number($min) = number($max)">
            <xsl:value-of select="replace($commandeModifiee,'&#8709;','&#39;&#39;&#39;&#39;')"/>
        </xsl:if>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>On supprime le lien Internet (et on le remplace par un r:Descritpion/r:Content,
                voir en dessous)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:sup"/>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>On recopie tout</xd:p>
            <xd:p>appliquer le template xhtml:sup ne fera rien (voir au dessus)</xd:p>
            <xd:p>On crée un élément r:Description/r:Content pour chaque élément rencontré</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Category[r:Label//xhtml:sup]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:element name="r:Description">
                <xsl:for-each select="r:Label//xhtml:sup">
                    <xsl:variable name="id" select="substring(xhtml:a/@href,2)"/>
                    <xsl:variable name="langue">
                        <xsl:value-of select="ancestor::r:Content/@xml:lang"/>
                    </xsl:variable>
                    <!-- On crée un élément r:Content -->
                    <xsl:element name="r:Content">
                        <xsl:attribute name="xml:lang" select="$langue"/>
                        <xsl:element name="xhtml:p">
                            <xsl:attribute name="class" select="'help'"/>
                            <!-- On applique le template du lien correspondant -->
                            <xsl:apply-templates
                                select="//xhtml:p[@id=$id and @xml:lang=$langue]/node()"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@xml:lang[.='fr-FR']">
        <xsl:attribute name="xml:lang" select="'fr'"/>
    </xsl:template>

    <xsl:template match="@xml:lang[.='en-IE']">
        <xsl:attribute name="xml:lang" select="'en'"/>
    </xsl:template>

    <xsl:template match="xhtml:a[not(contains(@href,'#'))]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="target">
                <xsl:value-of select="string('_blank')"/>
            </xsl:attribute>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <!--<xsl:template match="xhtml:a[contains(@href,'#')]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>-->

    <xsl:template
        match="xhtml:p[ancestor::d:Instruction[d:InstructionName/r:String/text()='Renvoi/Note']]">
        <xsl:copy>
            <xsl:variable name="identifiant">
                <xsl:value-of select="concat('#',@id)"/>
            </xsl:variable>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="concat(//xhtml:a[@href=$identifiant]/text(),' ')"/>
            <xsl:copy-of select="node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:transform>
