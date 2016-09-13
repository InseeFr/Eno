<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:il="http://xml/insee.fr/xslt/lib"
    xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
    xmlns:iatddi="http://xml/insee.fr/xslt/apply-templates/ddi"
    xmlns:iatfr="http://xml/insee.fr/xslt/apply-templates/form-runner"
    xmlns:d="ddi:datacollection:3_2" xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2"
    exclude-result-prefixes="xd" version="2.0">

    <xsl:import href="../../entrees/ddi/source.xsl"/>
    <xsl:import href="../../sorties/orbeon-form-runner/models.xsl"/>
    <xsl:import href="../../lib.xsl"/>

    <xsl:output method="xml" indent="yes"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Apr 9, 2013</xd:p>
            <xd:p><xd:b>Author:</xd:b> vdv</xd:p>
            <xd:p>Transforms DDI into Orbeon Form Builder!</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template match="/">
        <xsl:apply-templates select="/" mode="source"/>
    </xsl:template>

    <!-- On met ça là, c'est en effet très dépendant du langage d'entrée et de sortie -->
    <xsl:template
        match="d:Instruction[descendant::d:ConditionalText[r:SourceParameterReference] and not(ancestor::d:ComputationItem)]"
        mode="iatddi:get-conditionned-text" priority="1">
        <xsl:variable name="condition">
            <xsl:copy-of select="descendant::d:ConditionalText"/>
        </xsl:variable>
        <xsl:variable name="texte">
            <xsl:value-of select="il:serialize(descendant::d:LiteralText/d:Text/node())"/>
        </xsl:variable>
        <xsl:variable name="resultat">
            <xsl:text>concat(''</xsl:text>
            <xsl:for-each select="tokenize($texte,'&#248;')">
                <xsl:text>,</xsl:text>
                <xsl:choose>
                    <xsl:when
                        test=".=$condition/d:ConditionalText/r:SourceParameterReference/r:OutParameter/r:ID/text()">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="."/>
                        </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>'</xsl:text>
                        <!-- On remplace les singles quotes par deux singles quotes
                        car on génère un concat, on a besoin de doubler les quotes pour
                        ne pas déclencher une erreur lors du concat générer dans xforms-->
                        <xsl:value-of select='replace(.,"&apos;","&apos;&apos;")'/>
                        <xsl:text>'</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:text>)</xsl:text>
        </xsl:variable>
        <xsl:value-of select="$resultat"/>
    </xsl:template>

    <!-- On met ça là, c'est en effet très dépendant du langage d'entrée et de sortie -->
    <xsl:template
        match="d:Instruction[descendant::d:ConditionalText[r:SourceParameterReference] and ancestor::d:ComputationItem]"
        mode="iatddi:get-conditionned-text-bis" priority="1">
        <xsl:variable name="condition">
            <xsl:copy-of select="descendant::d:ConditionalText"/>
        </xsl:variable>
        <xsl:variable name="texte">
            <xsl:value-of select="il:serialize(descendant::d:LiteralText/d:Text/node())"/>
        </xsl:variable>
        <xsl:variable name="resultat">
            <xsl:text>concat(''</xsl:text>
            <xsl:for-each select="tokenize($texte,'&#248;')">
                <xsl:text>,</xsl:text>
                <xsl:choose>
                    <xsl:when
                        test=".=$condition/d:ConditionalText/r:SourceParameterReference/r:OutParameter/r:ID/text()">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="."/>
                        </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>'</xsl:text>
                        <!-- On remplace les singles quotes par deux singles quotes
                        car on génère un concat, on a besoin de doubler les quotes pour
                        ne pas déclencher une erreur lors du concat générer dans xforms-->
                        <xsl:value-of select='replace(.,"&apos;","&apos;&apos;")'/>
                        <xsl:text>'</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:text>)</xsl:text>
        </xsl:variable>
        <xsl:value-of select="$resultat"/>
    </xsl:template>

    <xsl:template
        match="d:Instruction[descendant::d:ConditionalText[d:Expression] and not(ancestor::d:ComputationItem)]"
        mode="iatddi:get-conditionned-text" priority="1">
        <xsl:variable name="condition">
            <xsl:copy-of select="descendant::d:ConditionalText"/>
        </xsl:variable>
        <xsl:variable name="texte">
            <xsl:value-of select="il:serialize(descendant::d:LiteralText/d:Text/node())"/>
        </xsl:variable>
        <xsl:variable name="resultat">
            <xsl:text>concat(''</xsl:text>
            <xsl:for-each select="tokenize($texte,'&#248;')[not(.='')]">
                <xsl:text>,</xsl:text>
                <xsl:choose>
                    <xsl:when
                        test="contains($condition/d:ConditionalText/d:Expression/r:Command/r:CommandContent/text(),.)">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="."/>
                        </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>'</xsl:text>
                        <!-- On remplace les singles quotes par deux singles quotes
                        car on génère un concat, on a besoin de doubler les quotes pour
                        ne pas déclencher une erreur lors du concat générer dans xforms-->
                        <xsl:value-of select='replace(.,"&apos;","&apos;&apos;")'/>
                        <xsl:text>'</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:text>)</xsl:text>
        </xsl:variable>
        <xsl:value-of select="$resultat"/>
    </xsl:template>

    <xsl:template
        match="d:Instruction[descendant::d:ConditionalText[d:Expression] and ancestor::d:ComputationItem]"
        mode="iatddi:get-conditionned-text-bis" priority="1">
        <xsl:variable name="condition">
            <xsl:copy-of select="descendant::d:ConditionalText"/>
        </xsl:variable>
        <xsl:variable name="texte">
            <xsl:value-of select="il:serialize(descendant::d:LiteralText/d:Text/node())"/>
        </xsl:variable>
        <xsl:variable name="resultat">
            <xsl:text>concat(''</xsl:text>
            <xsl:for-each select="tokenize($texte,'&#248;')[not(.='')]">
                <xsl:text>,</xsl:text>
                <xsl:choose>
                    <xsl:when
                        test="contains($condition/d:ConditionalText/d:Expression/r:Command/r:CommandContent/text(),.)">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="."/>
                        </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>'</xsl:text>
                        <!-- On remplace les singles quotes par deux singles quotes
                        car on génère un concat, on a besoin de doubler les quotes pour
                        ne pas déclencher une erreur lors du concat générer dans xforms-->
                        <xsl:value-of select='replace(.,"&apos;","&apos;&apos;")'/>
                        <xsl:text>'</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:text>)</xsl:text>
        </xsl:variable>
        <xsl:value-of select="$resultat"/>
    </xsl:template>

</xsl:stylesheet>
