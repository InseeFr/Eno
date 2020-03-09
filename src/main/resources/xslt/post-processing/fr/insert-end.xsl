<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" indent="no" encoding="UTF-8" />

    <xsl:param name="properties-file" />
    <xsl:param name="parameters-file" />
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <xsl:variable name="business" select="'business'" />
    <xsl:variable name="household" select="'household'" />

    <xsl:variable name="properties" select="doc($properties-file)" />
    <xsl:variable name="parameters">
        <xsl:choose>
            <xsl:when test="$parameters-node/*">
                <xsl:copy-of select="$parameters-node" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="doc($parameters-file)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="studyUnit">
        <xsl:choose>
            <xsl:when test="$parameters//StudyUnit != ''">
                <xsl:value-of select="$parameters//StudyUnit" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//StudyUnit" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="deblocage-questionnaire" select="$parameters//Deblocage" as="xs:boolean" />
    <xsl:variable name="enquete-satisfaction" select="$parameters//Satisfaction" as="xs:boolean" />
    <!-- metadata : donnees-pilotage -->
    <xsl:param name="metadata-file" />
    <xsl:param name="metadata-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <xsl:variable name="metadata">
        <xsl:choose>
            <xsl:when test="$metadata-node/*">
                <xsl:copy-of select="$metadata-node" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="doc($metadata-file)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="ArticleServiceProducteur"
        select="if($metadata/InformationsCollecte/ServiceProducteur/Article/text()='l''')
        then($metadata/InformationsCollecte/ServiceProducteur/Article)
        else (concat($metadata/InformationsCollecte/ServiceProducteur/Article/text(),' '))"/>
        <xsl:variable name="LibelleServiceProducteur"
        select="concat($ArticleServiceProducteur,$metadata/InformationsCollecte/ServiceProducteur/Libelle)"/>

    <xsl:template match="/">
        <xsl:apply-templates select="xhtml:html" />
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de base pour tous les éléments et tous les attributs, on recopie
                simplement en sortie</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>

    <!-- The generic end page is replaced by 3 pages -->
    <!-- Instance : -->
    <xsl:template match="End[parent::form[parent::xf:instance[@id='fr-form-instance']]]">
        <Validation />
        <xsl:if test="$studyUnit=$business">
            <Confirmation />
        </xsl:if>
        <xsl:copy />
    </xsl:template>

    <!-- We add the extracted tag in Util -->
    <xsl:template match="Send[parent::Util/parent::form[parent::xf:instance[@id='fr-form-instance']]]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
        <extrait>non</extrait>
    </xsl:template>

    <!-- The binds -->
    <xsl:template match="xf:bind[@name='end' and ancestor::xf:bind[@id='fr-form-instance-binds']]">
        <xf:bind id="validation-bind" name="validation" ref="Validation" relevant="instance('fr-form-instance')/Util/Send='false'" />
        <xsl:if test="$studyUnit=$business">
            <xf:bind id="confirmation-bind" name="confirmation" ref="Confirmation" relevant="instance('fr-form-instance')/Util/Send='false'" />
        </xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="@*" />
        </xsl:copy>
    </xsl:template>

    <!-- The resources -->
    <xsl:template match="End[ancestor::xf:instance[@id='fr-form-resources']]">
        <Validation>
            <label>VALIDATION</label>
        </Validation>
        <xsl:if test="$studyUnit=$business">
            <Confirmation>
                <label>CONFIRMATION</label>
            </Confirmation>
        </xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>
    <xsl:template match="GenericEndText[ancestor::xf:instance[@id='fr-form-resources']]" />

    <!-- Pages in the util instance -->
    <xsl:template match="End[ancestor::xf:instance[@id='fr-form-util']]">
        <Validation />
        <xsl:if test="$studyUnit=$business">
            <Confirmation />
        </xsl:if>
        <xsl:copy />
    </xsl:template>

    <!-- In the html -->
    <xsl:template match="xf:case[fr:section[@name='end']]">
        <xsl:variable name="index">
            <xsl:value-of select="number(@id)" />
        </xsl:variable>
        <xsl:variable name="link">
            <xsl:value-of select="'{concat(xxf:property(''url-orbeon''),xxf:property(''lien-deconnexion''))}'" />
        </xsl:variable>

        <xf:case id="{$index}">
            <xsl:if test="$studyUnit=$business">
                <fr:section id="validation-control" bind="validation-bind" name="validation">
                    <xf:label ref="$form-resources/Validation/label"/>
                    <xhtml:div class="center">
                        <xhtml:div class="frame">
                            <xhtml:p>
                                <xhtml:b>Vous êtes arrivé à la fin du questionnaire.</xhtml:b>
                            </xhtml:p>
                            <xsl:choose>
                                <xsl:when test="$deblocage-questionnaire">
                                    <xhtml:p>
                                        <xhtml:b>Si vous avez terminé de renseigner </xhtml:b>votre questionnaire, vous pouvez :
                                    </xhtml:p>
                                    <xhtml:p class="indentation-with-bullet">
                                        <xhtml:b> transmettre à nos services&#160;</xhtml:b>vos réponses en cliquant ci-dessous sur le bouton :
                                    </xhtml:p>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xhtml:p class="indentation-with-bullet"><xhtml:b>Si vous avez terminé de renseigner&#160;</xhtml:b>votre questionnaire,
                                        pour le transmettre à nos services, merci de cliquer ci-dessous sur le bouton : "Envoyer".</xhtml:p>
                                    <xhtml:p class="simple-identation">
                                        <xhtml:b>Une fois le questionnaire envoyé :</xhtml:b>
                                    </xhtml:p>
                                    <xhtml:p class="double-indentation">- vous ne pourrez&#160;<xhtml:b>plus modifier vos réponses</xhtml:b>&#160;;</xhtml:p>
                                    <xhtml:p class="double-indentation">- vous pourrez télécharger le&#160;<xhtml:b>récapitulatif de vos réponses au format pdf</xhtml:b>.</xhtml:p>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xhtml:div class="center-body">
                                <xf:trigger id="send" bind="send-bind">
                                    <xf:label ref="$form-resources/Send/label"/>
                                    <xf:action ev:event="DOMActivate">
                                        <xf:setvalue ref="instance('fr-form-util')/cliquable"
                                            value="string('non')"/>
                                        <xf:setvalue ref="instance('fr-form-util')/PreviousNext"
                                            value="1"/>
                                        <xf:dispatch name="page-change-done"
                                            targetid="fr-form-model"/>
                                    </xf:action>
                                </xf:trigger>
                            </xhtml:div>
                            <xsl:choose>
                                <xsl:when test="$deblocage-questionnaire">
                                    <xhtml:p class="simple-identation">Une fois transmis il vous sera possible d'expédier à nouveau celui-ci si une modification des réponses vous semble nécessaire.</xhtml:p>
                                    <xhtml:p class="simple-identation">Attention : si vous rouvrez un questionnaire après nous l’avoir transmis, que ce soit pour le modifier ou simplement 
                                        le consulter, veillez à nous le transmettre à nouveau (bouton "Envoyer" ci-dessus).</xhtml:p>
                                    <xhtml:p class="indentation-with-bullet">
                                        <xhtml:b> ne pas transmettre le questionnaire et revenir dès à présent </xhtml:b>sur vos réponses en cliquant sur le bouton : 
                                        "Retour" en bas à droite ou revenir plus tard sur vos réponses en cliquant sur le lien : "Fermer le questionnaire" et en vous authentifiant à nouveau.
                                    </xhtml:p>
                                    <xhtml:p class="simple-identation">
                                        <xsl:text>Dans ce cas-là, vos données seront enregistrées mais</xsl:text>&#160;<xhtml:b><xsl:text>le questionnaire ne sera pas envoyé à</xsl:text>&#160;<xsl:value-of select="$LibelleServiceProducteur"/>.</xhtml:b>
                                    </xhtml:p>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xhtml:p class="indentation-with-bullet">
                                        <xhtml:b>Si vous souhaitez y apporter des modifications</xhtml:b>, vous pouvez :</xhtml:p>
                                    <xhtml:p class="double-indentation">- revenir dessus dès à présent en cliquant sur le bouton "Retour" ;</xhtml:p>
                                    <xhtml:p class="double-indentation">- ou plus tard en cliquant sur le bouton "Fermer le questionnaire" et en vous authentifiant à nouveau.</xhtml:p>
                                    <xhtml:p class="double-indentation"><xsl:text>Dans les deux cas, vos données seront enregistrées mais</xsl:text>&#160;<xhtml:b><xsl:text>le questionnaire ne sera pas
                                        envoyé à </xsl:text><xsl:value-of select="$LibelleServiceProducteur"/></xhtml:b>.</xhtml:p>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xhtml:p class="center-body">
                                <xhtml:a href="{$link}">Fermer le questionnaire</xhtml:a>
                            </xhtml:p>
                        </xhtml:div>
                    </xhtml:div>
                </fr:section>
            </xsl:if>
            <xsl:if test="$studyUnit=$household">
                <fr:section id="validation-control" bind="validation-bind" name="validation">
                    <xf:label ref="$form-resources/Validation/label" />
                    <xhtml:div class="center">
                        <xhtml:div class="frame">
                            <xhtml:p>
                                <xhtml:b>Vous êtes arrivé à la fin du questionnaire.</xhtml:b>
                            </xhtml:p>
                            <xhtml:p>
                                Merci de&#160;
                                <xhtml:b>cliquer</xhtml:b>
                                &#160;sur le bouton "
                                <xhtml:b>Envoyer</xhtml:b>
                                " pour le transmettre à l'Insee.
                            </xhtml:p>
                            <xhtml:p>
                                Après envoi, vous ne pourrez&#160;
                                <xhtml:b>plus modifier vos réponses</xhtml:b>
                                &#160; en ligne.
                            </xhtml:p>
                            <xhtml:p>
                                Pour toute modification, &#160;
                                <xhtml:b>cliquer</xhtml:b>
                                &#160;sur le bouton "
                                <xhtml:b>Retour</xhtml:b>
                                ".
                            </xhtml:p>
                            <xhtml:div class="center-body">
                                <xf:trigger id="send" bind="send-bind">
                                    <xf:label ref="$form-resources/Send/label" />
                                    <xf:action ev:event="DOMActivate">
                                        <xf:setvalue ref="instance('fr-form-util')/cliquable" value="string('non')" />
                                        <xf:setvalue ref="instance('fr-form-util')/PreviousNext" value="1" />
                                        <xf:setvalue ref="instance('fr-form-instance')/Util/Send" value="string('oui')" />
                                        <xf:setvalue ref="instance('fr-form-instance')/Util/DateTime" value="fn:format-dateTime(fn:current-dateTime(),'[D01]-[M01]-[Y0001] à [H01]:[m01]')" />
                                        <xf:setvalue ref="instance('fr-form-instance')/Util/CurrentSection" value="string(number(instance('fr-form-instance')/Util/CurrentSection)+1)" />
                                        <xf:send submission="submit" />
                                        <xf:send submission="expedierPdf" />
                                    </xf:action>
                                </xf:trigger>
                            </xhtml:div>
                        </xhtml:div>
                    </xhtml:div>
                </fr:section>
            </xsl:if>
        </xf:case>

        <xf:case id="{$index+1}">
            <xsl:if test="$studyUnit=$business">
                <fr:section id="confirmation-control" bind="confirmation-bind" name="confirmation">
                    <xf:label ref="$form-resources/Confirmation/label"/>
                    <xhtml:div class="center center-body">
                        <xhtml:div class="frame">
                            <xhtml:p>
                                <xhtml:b>CONFIRMER VOTRE ENVOI</xhtml:b>
                            </xhtml:p>
                            <xsl:choose>
                                <xsl:when test="$deblocage-questionnaire">
                                    <xhtml:p>Il vous sera possible d'expédier à nouveau celui-ci si une modification des réponses vous semble nécessaire.</xhtml:p>                                
                                </xsl:when>
                                <xsl:otherwise>
                                    <xhtml:p>Votre réponse est définitive et vous souhaitez l'envoyer.</xhtml:p>        
                                </xsl:otherwise>
                            </xsl:choose>
                            <xf:trigger bind="confirmationOui-bind">
                                <xf:label>Je confirme l'envoi</xf:label>
                                <xf:action ev:event="DOMActivate">
                                    <xf:setvalue ref="instance('fr-form-instance')/Util/Send"
                                        value="string('oui')"/>
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
                            <xf:trigger bind="confirmationNon-bind">
                                <xf:label>Ne pas envoyer</xf:label>
                                <xf:action ev:event="DOMActivate">
                                    <xf:setvalue ref="instance('fr-form-util')/cliquable"
                                        value="string('oui')"/>
                                    <xf:setvalue ref="instance('fr-form-util')/PreviousNext"
                                        value="-1"/>
                                    <xf:dispatch name="page-change-done" targetid="fr-form-model"
                                    />
                                </xf:action>
                            </xf:trigger>
                        </xhtml:div>
                    </xhtml:div>
                </fr:section>
            </xsl:if>
            <xsl:if test="$studyUnit=$household">
                <fr:section id="end-control" bind="end-bind" name="end">
                    <xf:label ref="$form-resources/End/label" />
                    <xhtml:div class="center center-body">
                        <xhtml:div class="frame">
                            <xf:output id="confirmation-message" bind="confirmation-message-bind" class="confirmation-message" />
                            <xhtml:p>
                                <xhtml:b>L'Insee vous remercie de votre collaboration à cette enquête.</xhtml:b>
                            </xhtml:p>
                            <xhtml:p>
                                <xhtml:a href="recapitulatifPdf">Télécharger la preuve de votre participation à l'enquête </xhtml:a>
                                .
                                <xhtml:img src="/img/pdf.png" />
                            </xhtml:p>
                            <xhtml:p>
                                Pour quitter l’enquête,&#160;
                                <xhtml:b>cliquer</xhtml:b>
                                &#160;sur le bouton "
                                <xhtml:b>Déconnexion</xhtml:b>
                                "
                            </xhtml:p>
                        </xhtml:div>
                    </xhtml:div>
                </fr:section>
            </xsl:if>
        </xf:case>

        <xsl:if test="$studyUnit=$business">
            <xf:case id="{$index+2}">

                <fr:section id="end-control" bind="end-bind" name="end">
                    <xf:label ref="$form-resources/End/label"/>
                    <xhtml:div class="center center-body">
                        <xhtml:div class="frame">
                            <xf:output id="confirmation-message" bind="confirmation-message-bind"
                                class="confirmation-message"/>
                            <xsl:if test="$deblocage-questionnaire">
                                <xf:trigger bind="debloquer-bind" appearance="minimal" class="lienDeblocage">
                                    <xf:label><xhtml:p>Souhaitez-vous retourner sur votre questionnaire et compléter votre réponse ?</xhtml:p></xf:label>
                                    <xf:action ev:event="DOMActivate">
                                        <xf:setvalue ref="instance('fr-form-instance')/Util/Send" value="string('non')"/>
                                        <xf:setvalue ref="instance('fr-form-instance')/Util/extrait" value="string('non')"/>
                                        <xsl:for-each select="//xf:instance[@id='fr-form-instance']/form/Util/CurrentLoopElement">
                                            <xsl:element name="xf:setvalue">
                                                <xsl:attribute name="ref" select="concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@loop-name,''']')"/>
                                                <xsl:attribute name="value" select="'0'"/>
                                            </xsl:element>    
                                        </xsl:for-each>
                                        <xf:setvalue ref="instance('fr-form-util')/cliquable" value="string('oui')"/>
                                        <xf:setvalue ref="instance('fr-form-instance')/Util/CurrentSection" value="1"/>
                                        <xf:toggle case="{string('{instance(''fr-form-instance'')/Util/CurrentSection}')}"/>
                                        <xf:send submission="enregistrer-deblocage"/>
                                    </xf:action>
                                </xf:trigger>
                            </xsl:if>
                            <xhtml:p>
                                <xhtml:a href="recapitulatifPdf">Télécharger le récapitulatif de vos
                                    réponses au format PDF</xhtml:a>. <xhtml:img
                                        src="{concat('/',$properties//images/dossier,'/',$properties//images/pdf)}"
                                    />
                            </xhtml:p>
                            <xhtml:p>
                                <xhtml:b>La Statistique publique vous remercie de votre collaboration à
                                    cette enquête.</xhtml:b>
                            </xhtml:p>
                            
                            <!-- link to the satisfaction questionnaire-->
                            <xsl:if test="$enquete-satisfaction">
                                <xhtml:p>
                                    <xhtml:a href="{$properties//satisfaction}" target="_blank">Aidez-nous à améliorer notre site en répondant à notre enquête de satisfaction !</xhtml:a>
                                </xhtml:p>
                            </xsl:if>
                            
                        </xhtml:div>
                    </xhtml:div>
                </fr:section>

            </xf:case>
        </xsl:if>
    </xsl:template>

    <!-- Some navigational elements are added for these last pages -->
    <xsl:template match="Util[parent::xf:instance[@id='fr-form-util']]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
            <xsl:if test="$studyUnit=$business">
                <confirmationNon />
                <confirmationOui />
            </xsl:if>
            <cliquable />
            <xsl:if test="$studyUnit=$business and $deblocage-questionnaire">
                <debloquer />
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <!-- And the corresponding binds -->
    <xsl:template match="xf:bind[@id='fr-form-util-binds']">
        <xsl:choose>
            <xsl:when test="$studyUnit=$business">
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*" />
                    <xf:bind id="confirmationNon-bind" ref="confirmationNon" />
                    <xf:bind id="confirmationOui-bind" ref="confirmationOui" />
                    <xsl:if test="$deblocage-questionnaire">
                        <xf:bind id="debloquer-bind" ref="debloquer" />
                    </xsl:if>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*" />
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- We modify the bind of the Send button to avoid double clicking. -->
    <xsl:template match="xf:bind[@id='send-bind']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:attribute name="readonly">
                <xsl:value-of select="'instance(''fr-form-util'')/cliquable=''non'''" />
            </xsl:attribute>
        </xsl:copy>
    </xsl:template>

    <!-- The expedier submission is modified by chaining the end pages. -->
    <!-- If problem -->
    <xsl:template match="xf:action[@ev:event='xforms-submit-error' and parent::xf:submission[@id='submit']]">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <!-- Decrease the index by 1 -->
            <xf:setvalue ref="instance('fr-form-instance')/Util/CurrentSection" value="string(number(instance('fr-form-instance')/Util/CurrentSection)-1)" />
            <xsl:apply-templates select="node()" />
        </xsl:copy>
    </xsl:template>
    <!-- If the submission success -->
    <xsl:template match="xf:action[@ev:event='xforms-submit-done' and parent::xf:submission[@id='submit']]">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <!-- We're moving to a new page -->
            <xsl:variable name="choix">
                <xsl:value-of select="'{instance(''fr-form-instance'')/Util/CurrentSection}'" />
            </xsl:variable>
            <xf:toggle case="{$choix}" />
            <xf:setfocus control="page-top-control" />
            <xsl:apply-templates select="node()" />
        </xsl:copy>
    </xsl:template>

    <!-- the forced posting method corrects the conventional method at the margin. -->
    <xsl:template match="xf:submission[@id='save']">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
        <xsl:if test="$deblocage-questionnaire">
            <xsl:copy>
                <xsl:attribute name="id" select="'enregistrer-deblocage'" />
                <xsl:apply-templates select="@method | @ref | @replace | @relevant" />
                <xsl:attribute name="resource" select="substring-before(@resource,'?')" />
                <xsl:apply-templates select="node()" />
            </xsl:copy>
        </xsl:if>
    </xsl:template>


</xsl:stylesheet>