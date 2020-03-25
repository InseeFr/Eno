<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="2.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
    xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ev="http://www.w3.org/2001/xml-events">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
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
    
    
    <!-- Première colonne des tableaux de l'Esa -->
    
    <xsl:template match="Groupe[@typeGroupe='REPARTITION_CA']">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:element name="output-REPARTITION_CA1"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xf:bind[@id='REPARTITION_CA1-bind']">
        <xsl:variable name="groupe" select="'REPARTITION_CA'"/>
        <xsl:copy>
            <xsl:attribute name="id" select="concat('output-',@id)"/>
            <xsl:attribute name="name" select="concat('output-',@name)"/>
            <xsl:attribute name="ref" select="concat('output-',@name)"/>
            <xsl:attribute name="relevant" select="'instance(''fr-form-instance'')//Groupe[@typeGroupe=''REPARTITION_CA'' and @idGroupe = current()/ancestor::Groupe[@typeGroupe=''REPARTITION_CA'']/@idGroupe]//Variable[@idVariable=''REPARTITION_CA2''] != '''''"/>
        </xsl:copy>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="relevant" select="'not(instance(''fr-form-instance'')//Groupe[@typeGroupe=''REPARTITION_CA'' and @idGroupe = current()/ancestor::Groupe[@typeGroupe=''REPARTITION_CA'']/@idGroupe]//Variable[@idVariable=''REPARTITION_CA2''] != '''')'"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xf:repeat[@id='REPARTITION_CA']/xhtml:tr/xhtml:td[1]">
        <xsl:copy>
            <xsl:apply-templates select="node()"/>
            <xf:output>
                <xsl:attribute name="id" select="concat('output-',node()/@id)"/>
                <xsl:attribute name="name" select="concat('output-',node()/@name)"/>
                <xsl:attribute name="bind" select="concat('output-',node()/@bind)"/>
                <xsl:copy-of select="@xxf:order"/>
                <xf:label>
                    <xsl:attribute name="ref" select="'replace($form-resources/output-REPARTITION_CA1/label
                        ,''¤REPARTITION_CA1¤'',instance(''fr-form-instance'')//Groupe[@typeGroupe=''REPARTITION_CA'' and @idGroupe = current()/ancestor::Groupe[@typeGroupe=''REPARTITION_CA'']/@idGroupe]//Variable[@idVariable=''REPARTITION_CA1''])
                        '"/>
                    <xsl:attribute name="mediatype" select="'text/html'"/>
                </xf:label>
            </xf:output>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xf:repeat[@id='REPARTITION_CA']/xhtml:tr/xhtml:td[2]">
        <xsl:copy>
            <xf:output>
                <xsl:apply-templates select="*/@id"/>
                <xsl:apply-templates select="*/@name"/>
                <xsl:apply-templates select="*/@bind"/>
            </xf:output>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()='REPARTITION_CA1']">
        <xsl:element name="output-REPARTITION_CA1">
            <label><xsl:value-of select="'¤REPARTITION_CA1¤'"/></label>
        </xsl:element>
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <!-- Complément avec les libellés de cases -->
    <!-- Initialisation des données fixes des tableaux dynamiques -->
    
    
    
    <xsl:template match="xf:action[@ev:event='xforms-ready']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xf:action xxf:iterate="instance('fr-form-instance')//Groupe[@typeGroupe='REPARTITION_CA']">
                <xf:action if="not(output-REPARTITION_CA1)">
                    <xf:insert origin="instance('fr-form-loop-model')/Groupe/Groupe[@typeGroupe='REPARTITION_CA']/output-REPARTITION_CA1"
                        context="instance('fr-form-instance')//Groupe[@typeGroupe='REPARTITION_CA'][not(output-REPARTITION_CA1)][1]/*[last()]"
                        nodeset="instance('fr-form-instance')//Groupe[@typeGroupe='REPARTITION_CA'][not(output-REPARTITION_CA1)][1]/*[last()]"
                        position="after"/>
                </xf:action>
            </xf:action>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>        
    </xsl:template>
    
</xsl:transform>
