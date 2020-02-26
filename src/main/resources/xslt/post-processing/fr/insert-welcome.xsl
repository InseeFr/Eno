<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" indent="no" encoding="UTF-8" />

    <!-- Eno properties file -->
    <xsl:param name="properties-file" />
    <!-- Eno parameters file -->
    <xsl:param name="parameters-file" />
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <!-- metadata file coming from database "Pilotageé) -->
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

    <!-- Retrieving metadata -->
    <xsl:variable name="Identifiant" select="$metadata//Identifiant" />
    <xsl:variable name="LibelleCourtEnquete" select="$metadata//LibelleCourtEnquete" />
    <xsl:variable name="LibelleCourtEnqueteMillesime" select="$metadata//LibelleCourtEnqueteMillesime" />
    <xsl:variable name="Frequence" select="$metadata//Frequence" />
    <xsl:variable name="AnneeReference" select="$metadata//AnneeReference" />
    <xsl:variable name="Idvague" select="$metadata//Idvague" />
    <xsl:variable name="DateFermeture" select="$metadata//DateFermeture" />
    <xsl:variable name="Ville" select="$metadata//Ville" />
    <xsl:variable name="DateOuverture" select="$metadata//DateOuverture" />
    <xsl:variable name="ArticleServiceProducteur" select="$metadata//ArticleServiceProducteur" />
    <xsl:variable name="NomServiceProducteur" select="$metadata//NomServiceProducteur" />
    <xsl:variable name="NomServiceProducteurCourt" select="$metadata//NomServiceProducteurCourt" />
    <xsl:variable name="ObjectifsLongs" select="$metadata//ObjectifsLongs" />
    <xsl:variable name="CaractereObligatoire" select="$metadata//CaractereObligatoire" />
    <xsl:variable name="TypeUniteEnquetees" select="$metadata//TypeUniteEnquetees" />
    <xsl:variable name="TempsPassation" select="$metadata//TempsPassation" />
    <xsl:variable name="AdresseInternetEnquete" select="$metadata//AdresseInternetEnquete" />
    <xsl:variable name="URLRenseignement" select="$metadata//URLRenseignement" />
    <xsl:variable name="URLDiffusion" select="$metadata//URLDiffusion" />
    <xsl:variable name="ServiceCollecteurSignataireFonction" select="$metadata//ServiceCollecteurSignataireFonction" />
    <xsl:variable name="ServiceCollecteurSignataireNom" select="$metadata//ServiceCollecteurSignataireNom" />
    <xsl:variable name="ServiceCollecteurSignataireSexe" select="$metadata//ServiceCollecteurSignataireSexe" />
    <xsl:variable name="NumeroVisa" select="$metadata//NumeroVisa" />
    <xsl:variable name="MinistereTutelle" select="$metadata//MinistereTutelle" />
    <xsl:variable name="AnneeCollecte" select="$metadata//AnneeCollecte" />
    <xsl:variable name="ParutionJO" select="$metadata//ParutionJO" />
    <xsl:variable name="DateParutionJO" select="$metadata//DateParutionJO" />
    <xsl:variable name="NomServiceRecours" select="$metadata//NomServiceRecours" />
    <xsl:variable name="StatutEnquete" select="$metadata//StatutEnquete" />
    <xsl:variable name="Metropole" select="$metadata//Metropole" />
    <xsl:variable name="TypeUniteEnquetee" select="$metadata//TypeUniteEnquetee" />
    <xsl:variable name="RapprochementDonnees" select="$metadata//RapprochementDonnees" />
    <xsl:variable name="LibelleEnquete">
        <xsl:choose>
            <xsl:when test="$metadata//LibelleEnquete!=''">
                <xsl:value-of select="$metadata//LibelleEnquete" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$metadata//Campagne/Libelle" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="LibelleServiceProducteur">
        <xsl:value-of select="concat($metadata//ServiceProducteur/Article,$metadata//ServiceProducteur/Libelle)"/>
    </xsl:variable>
    <xsl:variable name="ObjectifsEnquete" select="normalize-space($metadata//ObjectifsCourts)" />
    <xsl:variable name="SimplificationEntreprises" select="$metadata//SimplificationEntreprises" />
    <!-- Value for which the fact that the survey is mandatory is displayed -->
    <xsl:variable name="CaractereObligatoireEnqueteReference" select="string('oui')" />
    <xsl:variable name="SimplificationEntreprisesReference" select="string('oui')" />

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

    <!-- The generic start page is replaced by a coltrane home page. -->
    <xsl:template match="Beginning[parent::form[parent::xf:instance[@id='fr-form-instance']]]">
        <xsl:if test="$studyUnit=$household">
            <Variable idVariable="QuiRepond1" />
            <Variable idVariable="QuiRepond2" />
            <Variable idVariable="QuiRepond3" />
        </xsl:if>
        <ACCUEIL>
            <ACCUEIL-1 />
            <ACCUEIL-2 />
            <ACCUEIL-3 />
            <ACCUEIL-4 />
            <ACCUEIL-5 />
            <ACCUEIL-6 />
            <ACCUEIL-7 />
        </ACCUEIL>
    </xsl:template>

    <!-- The corresponding bind -->
    <xsl:template match="xf:bind[@name='beginning']">
        <xf:bind id="ACCUEIL-bind" name="ACCUEIL" ref="ACCUEIL">
            <xf:bind id="ACCUEIL-1-bind" name="ACCUEIL-1" ref="ACCUEIL-1" />
            <xf:bind id="ACCUEIL-2-bind" name="ACCUEIL-2" ref="ACCUEIL-2" />
            <xf:bind id="ACCUEIL-3-bind" name="ACCUEIL-3" ref="ACCUEIL-3" />
            <xf:bind id="ACCUEIL-4-bind" name="ACCUEIL-4" ref="ACCUEIL-4" />
            <xf:bind id="ACCUEIL-5-bind" name="ACCUEIL-5" ref="ACCUEIL-5" />
            <xf:bind id="ACCUEIL-6-bind" name="ACCUEIL-6" ref="ACCUEIL-6" />
            <xf:bind id="ACCUEIL-7-bind" name="ACCUEIL-7" ref="ACCUEIL-7" relevant="if (instance('fr-form-util')/cadreLegal/text()!='') then (true()) else (false())" />
        </xf:bind>
    </xsl:template>

    <!-- The corresponding resources -->
    <xsl:template match="Beginning[ancestor::xf:instance[@id='fr-form-resources']]">
        <ACCUEIL>
            <label>ACCUEIL</label>
        </ACCUEIL>
        <ACCUEIL-1>
            <label>
                <xsl:text>&lt;p&gt;Bienvenue sur le questionnaire de réponse à l'enquête &lt;b&gt;</xsl:text>
                <xsl:value-of select="$LibelleEnquete" />
                <xsl:text>&lt;/b&gt;</xsl:text>
                <xsl:text>&lt;span title="</xsl:text>
                <xsl:value-of select="$ObjectifsEnquete" />
                <xsl:text>"&gt;&#160;&lt;img src="/img/Help-browser.svg.png"/&gt;&#160;&lt;/span&gt;</xsl:text>
                <xsl:text>&lt;/p&gt;</xsl:text>
            </label>
        </ACCUEIL-1>
        <ACCUEIL-2>
            <label>
                <xsl:if test="$studyUnit=$household">
                    <xsl:text>&lt;div class="frame"&gt;&lt;p&gt;&lt;b&gt;Qui doit répondre à ce questionnaire ?&lt;/b&gt;&lt;/p&gt;</xsl:text>
                    <xsl:text>&lt;p&gt;¤QuiRepond1¤&lt;/p&gt;</xsl:text>
                    <xsl:text>&lt;p&gt;¤QuiRepond2¤&lt;/p&gt;</xsl:text>
                    <xsl:text>&lt;p&gt;¤QuiRepond3¤&lt;/p&gt;</xsl:text>
                    <xsl:text>&lt;/div&gt;</xsl:text>
                </xsl:if>
                <xsl:if test="$studyUnit=$business">
                    <xsl:text>&lt;div class="frame"&gt;&lt;p&gt;Cette enquête</xsl:text>
                    <xsl:if test="$CaractereObligatoire=$CaractereObligatoireEnqueteReference">
                        <xsl:text>,
                        à &lt;b&gt;&lt;span style="text-decoration:underline"&gt;caractère
                        obligatoire&lt;/span&gt;&lt;/b&gt;,</xsl:text>
                    </xsl:if>
                    <xsl:text> est reconnue d'&lt;b&gt;</xsl:text>
                    <xsl:value-of select="$StatutEnquete" />
                    <xsl:text>&lt;/b&gt;.&lt;/p&gt;&lt;p&gt;Merci de répondre avant le :
                    &lt;b&gt;øDATE_RETOUR_SOUHAITEEø&lt;/b&gt;&lt;/p&gt;&lt;/div&gt;</xsl:text>
                </xsl:if>
            </label>
        </ACCUEIL-2>
        <ACCUEIL-3>
            <label>&lt;p&gt; &lt;/p&gt;</label>
        </ACCUEIL-3>
        <ACCUEIL-4>
            <label>&lt;p&gt;&lt;b&gt;Informations pratiques pour le
                remplissage&lt;/b&gt;&lt;/p&gt;</label>
        </ACCUEIL-4>
        <ACCUEIL-5>
            <label>
                <xsl:if test="$studyUnit=$household">
                    <xsl:text>&lt;div class="frameAvertissement"&gt;&lt;p&gt;&lt;b&gt;Pensez à enregistrer
                    régulièrement votre questionnaire.&lt;/b&gt; Au bout de 30 minutes sans
                    activité, pour des raisons de sécurité, vous serez en effet automatiquement
                    déconnecté. Les données saisies depuis la dernière sauvegarde seront perdues.&lt;/p&gt;&lt;p&gt;&lt;b&gt;Veuillez
                    utiliser les boutons &lt;button class="btn txt" type="button" style="cursor: default;" tabIndex="-1"&gt;Retour &lt;/button&gt; et &lt;button class="btn txt" type="button" style="cursor: default;" tabIndex="-1"&gt;Enregistrer et continuer &lt;/button&gt; pour naviguer dans le
                    questionnaire&lt;/b&gt; et non pas les boutons "Précédent" et "Suivant" de votre
                    navigateur.&lt;/p&gt;&lt;p&gt;Pour visualiser le contenu des info-bulles, il faut survoler le 
                    symbole &lt;span title="Les symboles similaires contiennent une aide sur la question ou ses mots-clefs"&gt;&#160;&lt;img src="/img/Help-browser.svg.png"/&gt;&#160;&lt;/span&gt;&lt;/p&gt;&lt;/div&gt;
                    </xsl:text>
                </xsl:if>
                <xsl:if test="$studyUnit=$business">
                    <xsl:text>&lt;div class="frameAvertissement"&gt;&lt;p&gt;&lt;b&gt;Pensez à enregistrer
                    régulièrement votre questionnaire.&lt;/b&gt; Au bout de 30 minutes sans
                    activité, pour des raisons de sécurité, vous serez en effet automatiquement
                    déconnecté. La sauvegarde des données vous incombant, les données saisies depuis
                    la dernière sauvegarde seront perdues.&lt;/p&gt;
                    &lt;p&gt;Lorsque vous quitterez ce questionnaire, privilégiez la fermeture par le bouton &lt;b&gt;Déconnexion&lt;/b&gt; afin d'éviter d'éventuels problèmes de navigation ultérieurs.&lt;/p&gt;
                    &lt;p&gt;&lt;b&gt;Veuillez utiliser les boutons &lt;button class="btn txt" type="button" style="cursor: default;" tabIndex="-1"&gt;Retour &lt;/button&gt; et &lt;button class="btn txt" type="button" style="cursor: default;" tabIndex="-1"&gt;Enregistrer et continuer &lt;/button&gt; pour naviguer dans le
                    questionnaire&lt;/b&gt; et non pas les boutons "Précédent" et "Suivant" de votre
                    navigateur.&lt;/p&gt;&lt;/div&gt;
                    </xsl:text>
                </xsl:if>
            </label>
        </ACCUEIL-5>
        <ACCUEIL-6>
            <label>&lt;p&gt; &lt;/p&gt;</label>
        </ACCUEIL-6>
        <ACCUEIL-7>
            <label>
                <xsl:if test="$studyUnit=$household">
                    <xsl:text>&lt;div class="frame"&gt;&lt;p&gt;Vu l'avis favorable du Conseil national de
                    l'information statistique, cette enquête</xsl:text>
                    <xsl:choose>
                        <xsl:when test="$CaractereObligatoire=$CaractereObligatoireEnqueteReference">
                            <xsl:text>, reconnue d’intérêt général et de qualité statistique, est obligatoire</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text> est reconnue d’intérêt général et de qualité statistique sans avoir de caractère obligatoire</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text>, en application de la &lt;a href="</xsl:text>
                    <xsl:value-of select="$properties//lois/statistique" />
                    <xsl:text>" target="_blank"&gt;loi n° 51-711 du 7 juin 1951&lt;/a&gt; sur l’obligation, la coordination et le secret en matière de statistiques.</xsl:text>

                    <xsl:text>&lt;/p&gt;&lt;p&gt;Visa n°</xsl:text>
                    <xsl:value-of select="$NumeroVisa" />
                    <xsl:text> </xsl:text>
                    <xsl:for-each select="$MinistereTutelle">
                        <xsl:text>du </xsl:text>
                        <xsl:value-of select="." />
                        <xsl:text>, </xsl:text>
                    </xsl:for-each>
                    <xsl:text>valable pour l'année </xsl:text>
                    <xsl:value-of select="$AnneeCollecte" />
                    <xsl:choose>
                        <xsl:when test="$ParutionJO = 'oui'">
                            <xsl:text> - Arrêté en date du </xsl:text>
                            <xsl:value-of select="$DateParutionJO" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text> - Arrêté en cours de parution</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text>.&lt;/p&gt;&lt;p&gt;Les réponses à ce questionnaire sont protégées par le secret statistique et destinées à </xsl:text>
                    <xsl:value-of select="$NomServiceProducteurCourt" />
                    <xsl:text>. Le  &lt;a href="</xsl:text>
                    <xsl:value-of select="$properties//lois/rgpd" />
                    <xsl:text>" target="_blank"&gt;règlement général 2016/679 du 27 avril 2016 sur la protection des données (RGPD)&lt;/a&gt; ainsi </xsl:text>
                    <xsl:text>que la  &lt;a href="</xsl:text>
                    <xsl:value-of select="$properties//lois/informatique" />
                    <xsl:text>" target="_blank"&gt;loi n° 78-17 du 6 janvier 1978 relative à l'informatique, aux fichiers et aux libertés&lt;/a&gt;, s'appliquent à la présente enquête. </xsl:text>
                    <xsl:text>Les droits des personnes, rappelés dans la lettre-avis, peuvent être exercés auprès de </xsl:text>
                    <xsl:value-of select="$NomServiceRecours" />
                    <xsl:text>.&lt;/p&gt;&lt;/div&gt;</xsl:text>
                </xsl:if>
                <xsl:if test="$studyUnit=$business">
                    <xsl:text>&lt;div class="frame"&gt;&lt;p&gt;Vu l'avis favorable du Conseil national de
                    l'information statistique, cette enquête</xsl:text>
                    <xsl:choose>
                        <xsl:when
                            test="$CaractereObligatoire=$CaractereObligatoireEnqueteReference">
                            <xsl:text>, reconnue d'&lt;b&gt;</xsl:text>
                            <xsl:value-of select="$StatutEnquete"/>
                            <xsl:text>, est
                            obligatoire, &lt;/b&gt;en application de la &lt;a
                    href="</xsl:text>
                            <xsl:value-of select="$properties//lois/statistique"/>
                            <xsl:text>" target="_blank"&gt;loi n° 51-711 du 7 juin 1951 modifiée&lt;/a&gt; sur l’obligation, la coordination et le secret en matière de statistiques.</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text> est reconnue d'&lt;b&gt;</xsl:text>
                            <xsl:value-of select="$StatutEnquete"/>
                            <xsl:text>&lt;/b&gt; sans avoir de caractère obligatoire, en application de la &lt;a
                    href="</xsl:text>
                            <xsl:value-of select="$properties//lois/statistique"/>
                            <xsl:text>" target="_blank"&gt;loi n° 51-711 du 7 juin 1951 modifiée&lt;/a&gt; sur l’obligation, la coordination et le secret en matière de statistiques.</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="$SimplificationEntreprises=$SimplificationEntreprisesReference">
                        <xsl:text> Elle entre dans le champ de la circulaire n° 2015-11 du 5 novembre 2015, qui s’inscrit 
                            dans le cadre des mesures de simplification pour les entreprises.</xsl:text>
                    </xsl:if>
                    <xsl:text>&lt;/p&gt;&lt;p&gt;Visa n°</xsl:text>
                    <xsl:value-of select="$NumeroVisa"/>
                    <xsl:text> </xsl:text>
                    <xsl:for-each select="$MinistereTutelle">
                        <xsl:text>du Ministre </xsl:text>
                        <xsl:value-of select="."/>
                        <xsl:text>, </xsl:text>
                    </xsl:for-each>
                    <xsl:text>valable pour l'année </xsl:text>
                    <xsl:value-of select="$AnneeCollecte"/>
                    
                    <xsl:text>.&lt;/p&gt;&lt;p&gt;Les réponses à ce questionnaire sont protégées par le secret statistique et destinées à </xsl:text>
                    <xsl:value-of select="$LibelleServiceProducteur"/>
                    <xsl:text>.&lt;/p&gt;</xsl:text>
                    
                    <xsl:text>&lt;p&gt;Le &lt;a href="</xsl:text>
                    <xsl:value-of select="$properties//lois/rgpd"/>
                    <xsl:text>" target="_blank"&gt;règlement général 2016/679 du 27 avril 2016&lt;/a&gt; sur la protection des données (RGPD)</xsl:text>
                    <xsl:text> ainsi que la &lt;a href="</xsl:text>
                    <xsl:value-of select="$properties//lois/informatique"/>
                    <xsl:text>" target="_blank"&gt;loi n°78-17 du 6 janvier 1978 modifiée&lt;/a&gt; relative à l'informatique, aux fichiers et aux libertés s'appliquent à la présente enquête pour les données à caractère personnel. Ces droits, rappelés dans la lettre-avis, peuvent être exercés auprès de </xsl:text>
                    <xsl:value-of select="$LibelleServiceProducteur"/>
                    <xsl:text>.&lt;/p&gt;</xsl:text>
                    <xsl:text>&lt;/div&gt;</xsl:text>                    
                </xsl:if>

            </label>
        </ACCUEIL-7>
    </xsl:template>

    <xsl:template match="GenericBeginningText[ancestor::xf:instance[@id='fr-form-resources']]" />

    <!-- Replace the page in the instance that is used to manage the questionnaire -->
    <xsl:template match="Beginning[ancestor::xf:instance[@id='fr-form-util']]">
        <ACCUEIL />
    </xsl:template>

    <!-- We place the html body here -->
    <xsl:template match="fr:section[@name='beginning']">
        <fr:section id="ACCUEIL-control" bind="ACCUEIL-bind" name="ACCUEIL">
            <xf:label ref="$form-resources/ACCUEIL/label" />
            <xf:output id="ACCUEIL-1-control" name="ACCUEIL-1" bind="ACCUEIL-1-bind" class="icone-help">
                <xf:label ref="$form-resources/ACCUEIL-1/label" mediatype="text/html" />
                <xf:help ref="$form-resources/ACCUEIL-1/help" mediatype="text/html" />
            </xf:output>
            <xf:output id="ACCUEIL-2-control" name="ACCUEIL-2" bind="ACCUEIL-2-bind">
                <xsl:if test="$studyUnit=$household">
                    <xf:label ref="replace(replace(replace($form-resources/ACCUEIL-2/label,'¤QuiRepond1¤',instance('fr-form-instance')//Variable[@idVariable='QuiRepond1']),'¤QuiRepond2¤',instance('fr-form-instance')//Variable[@idVariable='QuiRepond2']),'¤QuiRepond3¤',instance('fr-form-instance')//Variable[@idVariable='QuiRepond3'])" mediatype="text/html" />
                </xsl:if>
                <xsl:if test="$studyUnit=$business">
                    <xf:label ref="$form-resources/ACCUEIL-2/label" mediatype="text/html" />
                </xsl:if>
            </xf:output>
            <xf:output id="ACCUEIL-3-control" name="ACCUEIL-3" bind="ACCUEIL-3-bind">
                <xf:label ref="$form-resources/ACCUEIL-3/label" mediatype="text/html" />
            </xf:output>
            <xf:output id="ACCUEIL-4-control" name="ACCUEIL-4" bind="ACCUEIL-4-bind">
                <xf:label ref="$form-resources/ACCUEIL-4/label" mediatype="text/html" />
            </xf:output>
            <xf:output id="ACCUEIL-5-control" name="ACCUEIL-5" bind="ACCUEIL-5-bind">
                <xf:label ref="$form-resources/ACCUEIL-5/label" mediatype="text/html" />
            </xf:output>
            <xf:output id="ACCUEIL-6-control" name="ACCUEIL-6" bind="ACCUEIL-6-bind">
                <xf:label ref="$form-resources/ACCUEIL-6/label" mediatype="text/html" />
            </xf:output>
            <xf:trigger id="cadreLegal" appearance="minimal">
                <xf:label>
                    <xf:output value="concat(if (instance('fr-form-util')/cadreLegal/text()!='') then ('-') else ('+'),' Connaître le cadre légal de l''enquête ?')" />
                </xf:label>
                <xf:setvalue ev:event="DOMActivate" ref="instance('fr-form-util')/cadreLegal" value="if (instance('fr-form-util')/cadreLegal/text()!='') then ('') else ('affiche')" />
            </xf:trigger>
            <xf:output id="ACCUEIL-7-control" name="ACCUEIL-7" bind="ACCUEIL-7-bind">
                <xf:label ref="$form-resources/ACCUEIL-7/label" mediatype="text/html" />
            </xf:output>
        </fr:section>
    </xsl:template>

    <!-- The legal framework of the homepage is displayed/displayed by means of a link. -->
    <xsl:template match="xf:instance [@id='fr-form-util']/Util">
        <xsl:copy>
            <cadreLegal />
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xf:bind[@id='current-section-name-bind']/@calculate | xf:action[@ev:event='page-change']/xf:action/@if">
        <xsl:attribute name="{name()}">
            <xsl:for-each select="tokenize(., '''')">
                <xsl:if test="position()>1">
                    <xsl:value-of select="''''" />
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="string(number(.))!='NaN'">
                        <xsl:value-of select="number(.)+1" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="." />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xf:bind[@id='progress-percent-bind']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:attribute name="calculate">
                <xsl:choose>
                    <xsl:when test="contains(@calculate,'&lt;')">
                        <xsl:variable name="old-loop-number" select="substring-before(substring-after(@calculate,'&lt;'),')')" />
                        <xsl:variable name="new-loop-number" select="number($old-loop-number)+1" />
                        <xsl:value-of select="replace(replace(replace(@calculate,
                            concat('number\(instance\(''fr-form-instance''\)/Util/CurrentSection\) &lt;',$old-loop-number),
                            concat('number(instance(''fr-form-instance'')/Util/CurrentSection) &lt;',$new-loop-number)),
                            concat('instance\(''fr-form-instance''\)/Util/CurrentSection =',$old-loop-number),
                            concat('instance(''fr-form-instance'')/Util/CurrentSection =',$new-loop-number)),
                            concat('round\(\(',$old-loop-number,'-2'),
                            concat('round((',$new-loop-number,'-2'))" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@calculate" />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="node()" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xf:action[@ev:event='page-change-done']/xf:action/@if">
        <xsl:attribute name="if">
            <xsl:for-each select="tokenize(.,' ')">
                <xsl:if test="position()>1">
                    <xsl:value-of select="' '" />
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="string(number(.))!='NaN'">
                        <xsl:value-of select="number(.)+1" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="." />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:attribute>
    </xsl:template>


</xsl:stylesheet>