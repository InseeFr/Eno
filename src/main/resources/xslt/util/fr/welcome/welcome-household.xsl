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
    <xsl:variable name="Identifiant" select="$parametres/metadonnees/Identifiant"/>
    <xsl:variable name="LibelleCourtEnquete" select="$parametres/metadonnees/LibelleCourtEnquete"/>
    <xsl:variable name="LibelleCourtEnqueteMillesime" select="$parametres/metadonnees/LibelleCourtEnqueteMillesime"/>
    <xsl:variable name="Frequence" select="$parametres/metadonnees/Frequence"/>
    <xsl:variable name="AnneeReference" select="$parametres/metadonnees/AnneeReference"/>
    <xsl:variable name="Idvague" select="$parametres/metadonnees/Idvague"/>
    <xsl:variable name="DateOuverture" select="$parametres/metadonnees/DateOuverture"/>
    <xsl:variable name="DateFermeture" select="$parametres/metadonnees/DateFermeture"/>
    <xsl:variable name="BddVille" select="$parametres/metadonnees/BddVille"/>
    <xsl:variable name="BddDateOuverture" select="$parametres/metadonnees/BddDateOuverture"/>
    <xsl:variable name="BddArticleServiceProducteur" select="$parametres/metadonnees/BddArticleServiceProducteur"/>
    <xsl:variable name="BddNomServiceProducteur" select="$parametres/metadonnees/BddNomServiceProducteur"/>
    <xsl:variable name="BddNomServiceProducteurCourt" select="$parametres/metadonnees/BddNomServiceProducteurCourt"/>
    <xsl:variable name="BddObjectifsLongs" select="$parametres/metadonnees/BddObjectifsLongs"/>
    <xsl:variable name="BddCaractereObligatoire" select="$parametres/metadonnees/BddCaractereObligatoire"/>
    <xsl:variable name="BddTypeUniteEnquetees" select="$parametres/metadonnees/BddTypeUniteEnquetees"/>
    <xsl:variable name="BddTempsPassation" select="$parametres/metadonnees/BddTempsPassation"/>
    <xsl:variable name="BddAdresseInternetEnquete" select="$parametres/metadonnees/BddAdresseInternetEnquete"/>
    <xsl:variable name="BddURLRenseignement" select="$parametres/metadonnees/BddURLRenseignement"/>
    <xsl:variable name="BddURLDiffusion" select="$parametres/metadonnees/BddURLDiffusion"/>
    <xsl:variable name="BddServiceCollecteurSignataireFonction" select="$parametres/metadonnees/BddServiceCollecteurSignataireFonction"/>
    <xsl:variable name="BddServiceCollecteurSignataireNom" select="$parametres/metadonnees/BddServiceCollecteurSignataireNom"/>
    <xsl:variable name="BddServiceCollecteurSignataireSexe" select="$parametres/metadonnees/BddServiceCollecteurSignataireSexe"/>
    <xsl:variable name="BddNumeroVisa" select="$parametres/metadonnees/BddNumeroVisa"/>
    <xsl:variable name="BddMinistereTutelle" select="$parametres/metadonnees/BddMinistereTutelle"/>
    <xsl:variable name="BddAnneeCollecte" select="$parametres/metadonnees/BddAnneeCollecte"/>
    <xsl:variable name="BddParutionJO" select="$parametres/metadonnees/BddParutionJO"/>
    <xsl:variable name="BddDateParutionJO" select="$parametres/metadonnees/BddDateParutionJO"/>
    <xsl:variable name="BddNomServiceRecours" select="$parametres/metadonnees/BddNomServiceRecours"/>
    <xsl:variable name="BddStatutEnquete" select="$parametres/metadonnees/BddStatutEnquete"/>
    <xsl:variable name="BddMetropole" select="$parametres/metadonnees/BddMetropole"/>
    <xsl:variable name="BddTypeUniteEnquetee" select="$parametres/metadonnees/BddTypeUniteEnquetee"/>
    <xsl:variable name="BddRapprochementDonnees" select="$parametres/metadonnees/BddRapprochementDonnees"/>
    <xsl:variable name="LibelleEnquete" select="$parametres/metadonnees/LibelleEnquete"/>
    <xsl:variable name="ObjectifsEnquete" select="normalize-space($parametres/metadonnees/ObjectifsCourts)"/>
    <!-- Valeur pour laquelle on affiche le fait que l'enquête a un caractère obligatoire -->
    <xsl:variable name="CaractereObligatoireEnqueteReference" select="string('OUI')"/>
    
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
        <Variable idVariable="QuiRepond1"/>
        <Variable idVariable="QuiRepond2"/>
        <Variable idVariable="QuiRepond3"/>
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
            <xf:bind id="ACCUEIL-7-bind" name="ACCUEIL-7" ref="ACCUEIL-7" relevant="if (instance('fr-form-util')/cadreLegal/text()!='') then (true()) else (false())"/>
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
                <xsl:text>&lt;div class="frame"&gt;&lt;p&gt;&lt;b&gt;Qui doit répondre à ce questionnaire ?&lt;/b&gt;&lt;/p&gt;</xsl:text>
                <xsl:text>&lt;p&gt;¤QuiRepond1¤&lt;/p&gt;</xsl:text>
                <xsl:text>&lt;p&gt;¤QuiRepond2¤&lt;/p&gt;</xsl:text>
                <xsl:text>&lt;p&gt;¤QuiRepond3¤&lt;/p&gt;</xsl:text>
                <xsl:text>&lt;/div&gt;</xsl:text>
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
                    déconnecté. Les données saisies depuis la dernière sauvegarde seront perdues.&lt;/p&gt;&lt;p&gt;&lt;b&gt;Veuillez
                    utiliser les boutons &lt;button class="btn txt" type="button" style="cursor: default;" tabIndex="-1"&gt;Retour &lt;/button&gt; et &lt;button class="btn txt" type="button" style="cursor: default;" tabIndex="-1"&gt;Enregistrer et continuer &lt;/button&gt; pour naviguer dans le
                    questionnaire&lt;/b&gt; et non pas les boutons "Précédent" et "Suivant" de votre
                    navigateur.&lt;/p&gt;&lt;p&gt;Pour visualiser le contenu des info-bulles, il faut survoler le 
                    symbole &lt;span title="Les symboles similaires contiennent une aide sur la question ou ses mots-clefs"&gt;&#160;&lt;img src="/img/Help-browser.svg.png"/&gt;&#160;&lt;/span&gt;&lt;/p&gt;&lt;/div&gt;
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
                        test="$BddCaractereObligatoire=$CaractereObligatoireEnqueteReference">
                        <xsl:text>, reconnue d’intérêt général et de qualité statistique, est obligatoire</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> est reconnue d’intérêt général et de qualité statistique sans avoir de caractère obligatoire</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text>, en application de la &lt;a href="</xsl:text>
                <xsl:value-of select="$proprietes//lois/statistique"/>
                <xsl:text>" target="_blank"&gt;loi n° 51-711 du 7 juin 1951&lt;/a&gt; sur l’obligation, la coordination et le secret en matière de statistiques.</xsl:text>
                
                <xsl:text>&lt;/p&gt;&lt;p&gt;Visa n°</xsl:text>
                <xsl:value-of select="$BddNumeroVisa"/>
                <xsl:text> </xsl:text>
                <xsl:for-each select="$BddMinistereTutelle">
                    <xsl:text>du </xsl:text>
                    <xsl:value-of select="."/>
                    <xsl:text>, </xsl:text>
                </xsl:for-each>
                <xsl:text>valable pour l'année </xsl:text>
                <xsl:value-of select="$BddAnneeCollecte"/>
                <xsl:choose>
                    <xsl:when test="$BddParutionJO = 'oui'">
                        <xsl:text> - Arrêté en date du </xsl:text>
                        <xsl:value-of select="$BddDateParutionJO"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> - Arrêté en cours de parution</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text>.&lt;/p&gt;&lt;p&gt;Les réponses à ce questionnaire sont protégées par le secret statistique et destinées à </xsl:text>
                <xsl:value-of select="$BddNomServiceProducteurCourt"/>
                <xsl:text>. Le  &lt;a href="</xsl:text>
                <xsl:value-of select="$proprietes//lois/rgpd"/>
                <xsl:text>" target="_blank"&gt;règlement général 2016/679 du 27 avril 2016 sur la protection des données (RGPD)&lt;/a&gt; ainsi </xsl:text>
                <xsl:text>que la  &lt;a href="</xsl:text>
                <xsl:value-of select="$proprietes//lois/informatique"/>
                <xsl:text>" target="_blank"&gt;loi n° 78-17 du 6 janvier 1978 relative à l'informatique, aux fichiers et aux libertés&lt;/a&gt;, s'appliquent à la présente enquête. </xsl:text>
                <xsl:text>Les droits des personnes, rappelés dans la lettre-avis, peuvent être exercés auprès de </xsl:text>
                <xsl:value-of select="$BddNomServiceRecours"/>
                <xsl:text>.&lt;/p&gt;&lt;/div&gt;</xsl:text>
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
                xxf:order="label control hint help alert" class="icone-help">
                <xf:label ref="$form-resources/ACCUEIL-1/label" mediatype="text/html"/>
                <xf:help ref="$form-resources/ACCUEIL-1/help" mediatype="text/html"/>
            </xf:output>
            <xf:output id="ACCUEIL-2-control" name="ACCUEIL-2" bind="ACCUEIL-2-bind"
                xxf:order="label control hint help alert">
                <xf:label ref="replace(replace(replace($form-resources/ACCUEIL-2/label,'¤QuiRepond1¤',instance('fr-form-instance')//Variable[@idVariable='QuiRepond1']),'¤QuiRepond2¤',instance('fr-form-instance')//Variable[@idVariable='QuiRepond2']),'¤QuiRepond3¤',instance('fr-form-instance')//Variable[@idVariable='QuiRepond3'])" mediatype="text/html"/>
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

    <xsl:template match="xf:bind[@id='current-section-name-bind']/@calculate | xf:action[@ev:event='page-change']/xf:action/@if">
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
