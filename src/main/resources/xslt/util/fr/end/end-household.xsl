<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" encoding="utf-8"/>
    <!-- Transformation pour ajouter la page d'accueil des différents questionnaires -->

    <!-- La campagne -->
    <xsl:param name="campagne" as="xs:string"/>
    <!-- Le modèle -->
    <xsl:param name="modele" as="xs:string"/>

    <!-- Fichier de propriétés eno -->
    <xsl:param name="fichier-proprietes"/>
    
    <!-- En paramètre le fichier de paramétrage qui contient les informations provenant de Pilotage -->
    <xsl:param name="fichier-parametrage"/>

    <!-- En paramètre le fichier de paramétrage qui contient les informations provenant de Pilotage -->
    <xsl:param name="parameters-file"/>

    <xsl:variable name="proprietes" select="doc($fichier-proprietes)"/>

    <xsl:variable name="parametres" select="doc($fichier-parametrage)"/>

    <xsl:variable name="parameter-file-content" select="doc($parameters-file)"/>
    
    <!-- On récupère ces informations, si le modèle de données change, on pourra simplement modifier cette partie -->
   
    <xsl:template match="/">
        <xsl:apply-templates select="xhtml:html"/>
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

    <!-- La page de fin générique est remplacée par 3 pages -->
    <!-- Côté instance -->
    <xsl:template match="End[parent::form[parent::xf:instance[@id='fr-form-instance']]]">
        <Validation/>
        <xsl:copy/>
    </xsl:template>
    
    <!-- On rajoute la balise extrait dans Util -->
    <xsl:template match="Send[parent::Util/parent::form[parent::xf:instance[@id='fr-form-instance']]]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
        <extrait>non</extrait>
    </xsl:template>
    

    <!-- Côté bind -->
    <xsl:template match="xf:bind[@name='end' and ancestor::xf:bind[@id='fr-form-instance-binds']]">
        <xf:bind id="validation-bind" name="validation" ref="Validation"
            relevant="instance('fr-form-instance')/Util/Send='false'"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
        </xsl:copy>
    </xsl:template>

    <!-- Côté ressources -->
    <xsl:template match="End[ancestor::xf:instance[@id='fr-form-resources']]">
        <Validation>
            <label>VALIDATION</label>
        </Validation>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="GenericEndText[ancestor::xf:instance[@id='fr-form-resources']]"/>

    <!-- Côté pages dans l'instance util -->
    <xsl:template match="End[ancestor::xf:instance[@id='fr-form-util']]">
        <Validation/>
        <xsl:copy/>
    </xsl:template>

    <!-- Et dans le html -->
    <xsl:template match="xf:case[fr:section[@name='end']]">
        <xsl:variable name="index">
            <xsl:value-of select="number(@id)"/>
        </xsl:variable>
        <xsl:variable name="link">
            <xsl:value-of select="'{concat(xxf:property(''url-orbeon''),xxf:property(''lien-deconnexion''))}'"/>
        </xsl:variable>
        
        <xf:case id="{$index}">
            <fr:section id="validation-control" bind="validation-bind" name="validation">
                <xf:label ref="$form-resources/Validation/label"/>
                <xhtml:div class="center">
                    <xhtml:div class="frame">
                        <xhtml:p>
                            <xhtml:b>Vous êtes arrivé à la fin du questionnaire.</xhtml:b>
                        </xhtml:p>
                        <xhtml:p>Merci de&#160;<xhtml:b>cliquer</xhtml:b>&#160;sur le bouton "<xhtml:b>Envoyer</xhtml:b>" pour le transmettre à l'Insee.</xhtml:p>
                        <xhtml:p>Après envoi, vous ne pourrez&#160;<xhtml:b>plus modifier vos réponses</xhtml:b>&#160; en ligne.</xhtml:p>
                        <xhtml:p>Pour toute modification, &#160;<xhtml:b>cliquer</xhtml:b>&#160;sur le bouton "<xhtml:b>Retour</xhtml:b>".</xhtml:p>
                        <xhtml:div class="center-body">
                            <xf:trigger id="send" bind="send-bind">
                                <xf:label ref="$form-resources/Send/label"/>
                                <xf:action ev:event="DOMActivate">
                                    <xf:setvalue ref="instance('fr-form-util')/cliquable" value="string('non')"/>
                                    <xf:setvalue ref="instance('fr-form-util')/PreviousNext" value="1"/>
                                    <xf:setvalue ref="instance('fr-form-instance')/Util/Send" value="string('oui')"/>
                                    <xf:setvalue
                                        ref="instance('fr-form-instance')/Util/DateTime"
                                        value="fn:format-dateTime(fn:current-dateTime(),'[D01]-[M01]-[Y0001] à [H01]:[m01]')"/>
                                    <xf:setvalue
                                        ref="instance('fr-form-instance')/Util/CurrentSection"
                                        value="string(number(instance('fr-form-instance')/Util/CurrentSection)+1)"/>
                                    <xf:send submission="submit"/>
                                    <xf:send submission="expedierPdf"/>
                                </xf:action>
                            </xf:trigger>
                        </xhtml:div>
                    </xhtml:div>
                </xhtml:div>
            </fr:section>
        </xf:case>
        <xf:case id="{$index+1}">
            <fr:section id="end-control" bind="end-bind" name="end">
                <xf:label ref="$form-resources/End/label"/>
                <xhtml:div class="center center-body">
                    <xhtml:div class="frame">
                        <xf:output id="confirmation-message" bind="confirmation-message-bind"
                            class="confirmation-message" xxf:order="label control hint help alert"/>
                        <xhtml:p>
                            <xhtml:b>L'Insee vous remercie de votre collaboration à cette enquête.</xhtml:b>
                        </xhtml:p>
                        <xhtml:p>
                            <xhtml:a href="recapitulatifPdf">Télécharger la preuve de votre participation à l'enquête </xhtml:a>. <xhtml:img
                                    src="/img/pdf.png"
                                />
                        </xhtml:p>
                        <xhtml:p>
                            Pour quitter l’enquête,&#160;<xhtml:b>cliquer</xhtml:b>&#160;sur le bouton "<xhtml:b>Déconnexion</xhtml:b>"
                        </xhtml:p>
                    </xhtml:div>
                </xhtml:div>
            </fr:section>
        </xf:case>
    </xsl:template>

    <!-- On rajoute certains éléments liés à la navigation pour ces dernières pages -->
    <xsl:template match="Util[parent::xf:instance[@id='fr-form-util']]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <cliquable/>
        </xsl:copy>
    </xsl:template>

    <!-- On modifie le bind du bouton Envoyer pour éviter qu'on puisse cliquer deux fois de suite -->
    <xsl:template match="xf:bind[@id='send-bind']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="readonly">
                <xsl:value-of select="'instance(''fr-form-util'')/cliquable=''non'''"/>
            </xsl:attribute>
        </xsl:copy>
    </xsl:template>
    
    <!-- La submission expedier est modifiée suite à l'enchaînement des pages de fin -->
    <!-- Si problème -->
    <xsl:template match="xf:action[@ev:event='xforms-submit-error' and parent::xf:submission[@id='submit']]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <!-- On diminue l'index de 1 -->
            <xf:setvalue ref="instance('fr-form-instance')/Util/CurrentSection"
                value="string(number(instance('fr-form-instance')/Util/CurrentSection)-1)"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>
    <!-- Si la submission est réussie -->
    <xsl:template match="xf:action[@ev:event='xforms-submit-done' and parent::xf:submission[@id='submit']]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <!-- On change de page -->
            <xsl:variable name="choix">
                <xsl:value-of select="'{instance(''fr-form-instance'')/Util/CurrentSection}'"/>
            </xsl:variable>
            <xf:toggle case="{$choix}"/>
            <xf:setfocus control="page-top-control"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:transform>
