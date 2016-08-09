<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2" xmlns:g="ddi:group:3_2"
    xmlns:s="ddi:studyunit:3_2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="fichier_principal"/>
    <xsl:param name="fichier_secondaire"/>
    <xsl:param name="fichier_variables"/>
    <xsl:param name="dossierSortie"/>
    <xsl:output method="xml" indent="no" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Variable qui concatène le DDI</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="ddiTotal">
        <conteneur>
            <xsl:copy-of select="document($fichier_principal)"/>
            <xsl:copy-of select="document($fichier_secondaire)"/>
        </conteneur>
    </xsl:variable>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de racine, on applique les templates de tous les enfants</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <!-- Les différents cas -->
        <xsl:choose>
            <!-- Cas des studyUnit, on va déréférencer chaque instrument -->
            <xsl:when test="document($fichier_principal)//d:Instrument">
                <xsl:variable name="racine">
                    <xsl:value-of
                        select="replace(document($fichier_principal)//s:StudyUnit/r:ID/text(), '-SU', '')"
                    />
                </xsl:variable>
                <xsl:for-each select="document($fichier_principal)//d:Instrument">
                    <xsl:result-document
                        href="{lower-case(concat('file:///',replace($dossierSortie, '\\' , '/'),'/',replace(r:ID/text(), concat($racine/text(),'-In-'), ''),'.xml'))}"
                        method="xml">
                        <DDIInstance xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                            <s:StudyUnit>
                                <xsl:apply-templates select="."/>
                            </s:StudyUnit>
							<!-- On va recopier uniquement les variables qui ne correspondent pas à une question -->
                            <xsl:apply-templates
                                select="document($fichier_variables)//g:ResourcePackage"/>
                        </DDIInstance>
                    </xsl:result-document>
                </xsl:for-each>
            </xsl:when>

            <!-- Cas de la banque de ControlConstructScheme, on déréférence le ControlConstructScheme -->
            <xsl:when test="document($fichier_principal)//d:ControlConstructScheme">
                <d:ControlConstructScheme>
                    <xsl:apply-templates
                        select="document($fichier_principal)//d:Sequence[d:TypeOfSequence/text()='Modele']"
                    />
                </d:ControlConstructScheme>
            </xsl:when>

            <!-- Cas de la banque de questions/interviews/catégories/codes. On déréférence les élements principaux : les questions et les interviews -->
            <xsl:otherwise>
                <g:ResourcePackage>
                    <xsl:apply-templates
                        select="document($fichier_principal)//d:InterviewerInstructionScheme"/>
                    <xsl:apply-templates select="document($fichier_principal)//d:QuestionScheme"/>
                </g:ResourcePackage>
            </xsl:otherwise>
        </xsl:choose>
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

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>On ne récupère pas les Variable qui correspondent à une question</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="l:Variable[r:QuestionReference or r:SourceParameterReference]"
        priority="1"/>
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>On récupère uniquement les variables qui ne correspondent pas à une question</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="l:Variable[not(r:QuestionReference or r:SourceParameterReference)]"
        priority="1">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de base pour les éléments qui correspondent à une référence</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node()[ends-with(name(),'Reference') and not(parent::r:Binding)]/r:ID">
        <xsl:variable name="ID" select="."/>
        <!-- On va recopier l'élément -->
        <!-- On fait attention à recopier un élément qui n'est pas lui même à l'intérieur d'une référence (et qui ne serait donc pas l'élément de base mais une référence déjà déréférencée) -->
        <xsl:apply-templates
            select="$ddiTotal//*[r:ID=$ID and not(ancestor-or-self::node()[ends-with(name(),'Reference')])]"
        />
    </xsl:template>

</xsl:transform>
