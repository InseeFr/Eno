<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" encoding="utf-8"/>
    <!-- Transformation pour ajouter la page d'accueil des différents questionnaires -->

    <!-- En paramètre le fichier de paramétrage qui contient les informations provenant de Pilotage -->
    <xsl:param name="fichier-parametrage"/>
    <xsl:variable name="parametres" select="doc($fichier-parametrage)"/>

    <!-- Fichier de propriétés eno -->
    <xsl:param name="fichier-proprietes"/>
    <xsl:variable name="proprietes" select="doc($fichier-proprietes)"/>

    <!-- On récupère ces informations, si le modèle de données change, on pourra simplement modifier cette partie -->
    <xsl:variable name="LibelleEnquete" select="$parametres/InformationsCollecte/Campagne/Libelle"/>
<!--    <xsl:variable name="LibelleSource" select="$parametres/InformationsCollecte/Source/LibelleLong"/>
    <xsl:variable name="AnneeCampagne"
        select="$parametres/InformationsCollecte/Campagne/AnneeReference"/>
    <xsl:variable name="PeriodeCampagne"
        select="$parametres/InformationsCollecte/Campagne/PeriodeReference"/>-->
    <xsl:variable name="ArticleServiceProducteur"
        select="if($parametres/InformationsCollecte/ServiceProducteur/Article/text()='l''')
        then($parametres/InformationsCollecte/ServiceProducteur/Article)
        else (concat($parametres/InformationsCollecte/ServiceProducteur/Article/text(),' '))"/>
    <xsl:variable name="LibelleServiceProducteur"
        select="concat($ArticleServiceProducteur,$parametres/InformationsCollecte/ServiceProducteur/Libelle)"/>
    <xsl:variable name="MinistereTutellesServiceProducteur"
        select="$parametres/InformationsCollecte/ServiceProducteur/MinistereTutelle[text()]"/>
    <xsl:variable name="AnneeCollecte"
        select="$parametres/InformationsCollecte/Enquete/AnneeCollecte"/>
    <xsl:variable name="FrequenceEnquete"
        select="$parametres/InformationsCollecte/Source/Frequence/Libelle"/>
    <!-- La liste des fréquences qui font afficher la période, séparées par des virgules -->
    <xsl:variable name="frequences"
        select="('mensuelle','trimestrielle','bimestrielle','semestrielle')" as="xs:string *"/>
    <xsl:variable name="ObjectifsEnquete"
        select="normalize-space($parametres/InformationsCollecte/Enquete/ObjectifsCourts)"/>
    <xsl:variable name="CaractereObligatoireEnquete"
        select="upper-case($parametres/InformationsCollecte/Enquete/CaractereObligatoire)"/>
    <!-- Valeur pour laquelle on affiche le fait que l'enquête a un caractère obligatoire -->
    <xsl:variable name="CaractereObligatoireEnqueteReference" select="string('OUI')"/>
    <xsl:variable name="SimplificationEntreprises"
        select="upper-case($parametres/InformationsCollecte/Enquete/SimplificationEntreprises)"/>
    <!-- Valeur pour laquelle on affiche le fait que l'enquête a un caractère obligatoire -->
    <xsl:variable name="SimplificationEntreprisesReference" select="string('OUI')"/>

    <xsl:variable name="StatutEnquete"
        select="$parametres/InformationsCollecte/Enquete/StatutEnquete"/>
    <xsl:variable name="NumeroVisa" select="$parametres/InformationsCollecte/Enquete/NumeroVisa"/>

    <xsl:variable name="espace" select="string('&#160;')"/>

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

    <!-- On remplace la page de début générique par une page d'accueil coltrane -->
    <xsl:template match="Beginning[parent::form[parent::xf:instance[@id='fr-form-instance']]]">
        <ACCUEIL>
            <ACCUEIL-1/>
            <ACCUEIL-2/>
            <ACCUEIL-3/>
            <ACCUEIL-4/>
            <ACCUEIL-5/>
            <ACCUEIL-6/>
            <ACCUEIL-7/>
        </ACCUEIL>
    </xsl:template>

    <!-- Le bind correspondant -->
    <xsl:template match="xf:bind[@name='beginning']">
        <xf:bind id="ACCUEIL-bind" name="ACCUEIL" ref="ACCUEIL">
            <xf:bind id="ACCUEIL-1-bind" name="ACCUEIL-1" ref="ACCUEIL-1"/>
            <xf:bind id="ACCUEIL-2-bind" name="ACCUEIL-2" ref="ACCUEIL-2"/>
            <xf:bind id="ACCUEIL-3-bind" name="ACCUEIL-3" ref="ACCUEIL-3"/>
            <xf:bind id="ACCUEIL-4-bind" name="ACCUEIL-4" ref="ACCUEIL-4"/>
            <xf:bind id="ACCUEIL-5-bind" name="ACCUEIL-5" ref="ACCUEIL-5"/>
            <xf:bind id="ACCUEIL-6-bind" name="ACCUEIL-6" ref="ACCUEIL-6"/>
            <xf:bind id="ACCUEIL-7-bind" name="ACCUEIL-7" ref="ACCUEIL-7"
                relevant="if (instance('fr-form-util')/cadreLegal/text()!='') then (true()) else (false())"
            />
        </xf:bind>
    </xsl:template>

    <!-- Les ressources correspondantes -->
    <xsl:template match="Beginning[ancestor::xf:instance[@id='fr-form-resources']]">
        <ACCUEIL>
            <label>ACCUEIL</label>
        </ACCUEIL>
        <ACCUEIL-1>
            <label>
                <xsl:text>&lt;p&gt;Bienvenue sur le questionnaire de réponse à l'enquête &lt;b&gt;</xsl:text>
                <xsl:value-of select="$LibelleEnquete"/>
                <xsl:text>&lt;/b&gt;</xsl:text>
                <xsl:text>&lt;span title="</xsl:text>
                <xsl:value-of select="$ObjectifsEnquete"/>
                <xsl:text>"&gt;&#160;&lt;img src="/img/Help-browser.svg.png"/&gt;&#160;&lt;/span&gt;</xsl:text>
                <xsl:text>&lt;/p&gt;</xsl:text>
            </label>
       </ACCUEIL-1>
        <ACCUEIL-2>
            <label>
                <xsl:text>&lt;div class="frame"&gt;&lt;p&gt;Cette enquête</xsl:text>
                <xsl:if test="$CaractereObligatoireEnquete=$CaractereObligatoireEnqueteReference">
                    <xsl:text>,
                        à &lt;b&gt;&lt;span style="text-decoration:underline"&gt;caractère
                        obligatoire&lt;/span&gt;&lt;/b&gt;,</xsl:text>
                </xsl:if>
                <xsl:text> est reconnue d'&lt;b&gt;</xsl:text>
                <xsl:value-of select="$StatutEnquete"/>
                <xsl:text>&lt;/b&gt;.&lt;/p&gt;&lt;p&gt;Merci de répondre avant le :
                    &lt;b&gt;øDATE_RETOUR_SOUHAITEEø&lt;/b&gt;&lt;/p&gt;&lt;/div&gt;</xsl:text>
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
                <xsl:text>&lt;div class="frameAvertissement"&gt;&lt;p&gt;&lt;b&gt;Pensez à enregistrer
                    régulièrement votre questionnaire.&lt;/b&gt; Au bout de 30 minutes sans
                    activité, pour des raisons de sécurité, vous serez en effet automatiquement
                    déconnecté. La sauvegarde des données vous incombant, les données saisies depuis
                    la dernière sauvegarde seront perdues.&lt;/p&gt;
                    &lt;p&gt;Lorsque vous quitterez ce questionnaire, privilégiez la fermeture par le bouton &lt;b&gt;Déconnexion&lt;/b&gt; afin d'éviter d'éventuels problèmes de navigation ultérieurs.&lt;/p&gt;
                    &lt;p&gt;&lt;b&gt;Veuillez utiliser les boutons &lt;img src="</xsl:text>
                <xsl:value-of
                    select="concat('/',$proprietes//images/dossier,'/',$proprietes//images/precedent)"/>
                <xsl:text>"/&gt; et &lt;img
                    src="</xsl:text>
                <xsl:value-of
                    select="concat('/',$proprietes//images/dossier,'/',$proprietes//images/suivant)"/>
                <xsl:text>"/&gt; pour naviguer dans le
                    questionnaire&lt;/b&gt; et non pas les boutons "Précédent" et "Suivant" de votre
                    navigateur.&lt;/p&gt;&lt;/div&gt;
                    </xsl:text>
            </label>
        </ACCUEIL-5>
        <ACCUEIL-6>
            <label>&lt;p&gt; &lt;/p&gt;</label>
        </ACCUEIL-6>
        <ACCUEIL-7>
            <label>
                <xsl:text>&lt;div class="frame"&gt;&lt;p&gt;Vu l'avis favorable du Conseil national de
                    l'information statistique, cette enquête</xsl:text>
                <xsl:choose>
                    <xsl:when
                        test="$CaractereObligatoireEnquete=$CaractereObligatoireEnqueteReference">
                        <xsl:text>, reconnue d'&lt;b&gt;</xsl:text>
                        <xsl:value-of select="$StatutEnquete"/>
                        <!--<xsl:text>, est
                            obligatoire.&lt;/b&gt;</xsl:text>-->
                        <xsl:text>, est
                            obligatoire, &lt;/b&gt;en application de la &lt;a
                    href="</xsl:text>
                <xsl:value-of select="$proprietes//lois/statistique"/>
                <xsl:text>" target="_blank"&gt;loi n° 51-711 du 7 juin 1951 modifiée&lt;/a&gt; sur l’obligation, la coordination et le secret en matière de statistiques.</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> est reconnue d'&lt;b&gt;</xsl:text>
                        <xsl:value-of select="$StatutEnquete"/>
                       <!-- <xsl:text>&lt;/b&gt; sans avoir de caractère obligatoire.</xsl:text>-->
                        <xsl:text>&lt;/b&gt; sans avoir de caractère obligatoire, en application de la &lt;a
                    href="</xsl:text>
                <xsl:value-of select="$proprietes//lois/statistique"/>
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
                <xsl:for-each select="$MinistereTutellesServiceProducteur">
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
                        <xsl:value-of select="$proprietes//lois/rgpd"/>
                        <xsl:text>" target="_blank"&gt;règlement général 2016/679 du 27 avril 2016&lt;/a&gt; sur la protection des données (RGPD)</xsl:text>
                        <xsl:text> ainsi que la &lt;a href="</xsl:text>
                        <xsl:value-of select="$proprietes//lois/informatique"/>
                        <xsl:text>" target="_blank"&gt;loi n°78-17 du 6 janvier 1978 modifiée&lt;/a&gt; relative à l'informatique, aux fichiers et aux libertés s'appliquent à la présente enquête pour les données à caractère personnel. Ces droits, rappelés dans la lettre-avis, peuvent être exercés auprès de </xsl:text>
                        <xsl:value-of select="$LibelleServiceProducteur"/>
                        <xsl:text>.&lt;/p&gt;</xsl:text>
                        <xsl:text>.&lt;/div&gt;</xsl:text>
            </label>
        </ACCUEIL-7>
    </xsl:template>

    <xsl:template match="GenericBeginningText[ancestor::xf:instance[@id='fr-form-resources']]"/>
    
    

    <!-- On remplace la page dans l'instance qui sert à gérer le questionnaire -->
    <xsl:template match="Beginning[ancestor::xf:instance[@id='fr-form-util']]">
        <ACCUEIL/>
    </xsl:template>

    <!-- On place le corps html ici -->
    <xsl:template match="fr:section[@name='beginning']">
        <fr:section id="ACCUEIL-control" bind="ACCUEIL-bind" name="ACCUEIL">
            <xf:label ref="$form-resources/ACCUEIL/label"/>
            <xf:output id="ACCUEIL-1-control" name="ACCUEIL-1" bind="ACCUEIL-1-bind"
                xxf:order="label help control hint alert" class="icone-help">
                <xf:label ref="$form-resources/ACCUEIL-1/label" mediatype="text/html"/>
                <xf:help ref="$form-resources/ACCUEIL-1/help" mediatype="text/html"/>
            </xf:output>
            <xf:output id="ACCUEIL-2-control" name="ACCUEIL-2" bind="ACCUEIL-2-bind"
                xxf:order="label control hint help alert">
                <xf:label ref="$form-resources/ACCUEIL-2/label" mediatype="text/html"/>
            </xf:output>
            <xf:output id="ACCUEIL-3-control" name="ACCUEIL-3" bind="ACCUEIL-3-bind"
                xxf:order="label control hint help alert">
                <xf:label ref="$form-resources/ACCUEIL-3/label" mediatype="text/html"/>
            </xf:output>
            <xf:output id="ACCUEIL-4-control" name="ACCUEIL-4" bind="ACCUEIL-4-bind"
                xxf:order="label control hint help alert">
                <xf:label ref="$form-resources/ACCUEIL-4/label" mediatype="text/html"/>
            </xf:output>
            <xf:output id="ACCUEIL-5-control" name="ACCUEIL-5" bind="ACCUEIL-5-bind"
                xxf:order="label control hint help alert">
                <xf:label ref="$form-resources/ACCUEIL-5/label" mediatype="text/html"/>
            </xf:output>
            <xf:output id="ACCUEIL-6-control" name="ACCUEIL-6" bind="ACCUEIL-6-bind"
                xxf:order="label control hint help alert">
                <xf:label ref="$form-resources/ACCUEIL-6/label" mediatype="text/html"/>
            </xf:output>
            <xf:trigger id="cadreLegal" appearance="minimal">
                <xf:label>
                    <xf:output
                        value="concat(if (instance('fr-form-util')/cadreLegal/text()!='') then ('-') else ('+'),' Connaître le cadre légal de l''enquête ?')"
                    />
                </xf:label>
                <xf:setvalue ev:event="DOMActivate" ref="instance('fr-form-util')/cadreLegal"
                    value="if (instance('fr-form-util')/cadreLegal/text()!='') then ('') else ('affiche')"
                />
            </xf:trigger>
            <xf:output id="ACCUEIL-7-control" name="ACCUEIL-7" bind="ACCUEIL-7-bind"
                xxf:order="label control hint help alert">
                <xf:label ref="$form-resources/ACCUEIL-7/label" mediatype="text/html"/>
            </xf:output>
        </fr:section>
    </xsl:template>

    <!-- On fait apparaître/disparaître le cadre legal de la page d'accueil à l'aide d'un lien -->
    <xsl:template match="xf:instance [@id='fr-form-util']/Util">
        <xsl:copy>
            <cadreLegal/>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template
        match="xf:bind[@id='current-section-name-bind']/@calculate 
        | xf:action[@ev:event='page-change']/xf:action/@if">
        <xsl:attribute name="{name()}">
            <xsl:for-each select="tokenize(., '''')">
                <xsl:if test="position()>1">
                    <xsl:value-of select="''''"/>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="string(number(.))!='NaN'">
                        <xsl:value-of select="number(.)+1"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="."/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xf:bind[@id='progress-percent-bind']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="calculate">
                <xsl:choose>
                    <xsl:when test="contains(@calculate,'&lt;')">
                        <xsl:variable name="old-loop-number"
                            select="substring-before(substring-after(@calculate,'&lt;'),')')"/>
                        <xsl:variable name="new-loop-number" select="number($old-loop-number)+1"/>
                        <xsl:value-of
                            select="replace(replace(replace(@calculate,
                            concat('number\(instance\(''fr-form-instance''\)/Util/CurrentSection\) &lt;',$old-loop-number),
                            concat('number(instance(''fr-form-instance'')/Util/CurrentSection) &lt;',$new-loop-number)),
                            concat('instance\(''fr-form-instance''\)/Util/CurrentSection =',$old-loop-number),
                            concat('instance(''fr-form-instance'')/Util/CurrentSection =',$new-loop-number)),
                            concat('round\(\(',$old-loop-number,'-2'),
                            concat('round((',$new-loop-number,'-2'))"
                        />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@calculate"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xf:action[@ev:event='page-change-done']/xf:action/@if">
        <xsl:attribute name="if">
            <xsl:for-each select="tokenize(.,' ')">
                <xsl:if test="position()>1">
                    <xsl:value-of select="' '"/>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="string(number(.))!='NaN'">
                        <xsl:value-of select="number(.)+1"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="."/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:attribute>
    </xsl:template>

</xsl:transform>
