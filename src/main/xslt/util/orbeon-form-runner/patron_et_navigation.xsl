<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" indent="yes" encoding="utf-8"/>
    <!-- Transformation pour ajouter la page d'accueil des différents questionnaires -->

    <!-- La campagne -->
    <xsl:param name="campagne" as="xs:string"/>
    <!-- Le modèle -->
    <xsl:param name="modele" as="xs:string"/>
    <!-- Fichier de propriétés eno -->
    <xsl:param name="fichier-proprietes"/>
    
    <xsl:variable name="proprietes" select="doc($fichier-proprietes)"/>


    <xsl:variable name="choix">
        <xsl:value-of
            select="string('{instance(&quot;fr-form-instance&quot;)/stromae/util/sectionCourante}')"
        />
    </xsl:variable>

    <xsl:variable name="nbModules">
        <xsl:value-of
            select="count(//*[parent::form[parent::xf:instance[@id='fr-form-instance']] and child::*])"
        />
    </xsl:variable>

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


    <!-- On rajoute ces éléments dans l'instance principale -->
    <xsl:template match="xf:instance[@id='fr-form-instance']/form">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <VALIDATION>
                <dummy/>
            </VALIDATION>
            <CONFIRMATION>
                <dummy/>
            </CONFIRMATION>
            <FIN>
                <dummy/>
            </FIN>
            <stromae>
                <BarreFixe/>
                <util>
                    <sectionCourante>1</sectionCourante>
                    <nomSectionCourante/>
                    <expedie>non</expedie>
                    <extrait>non</extrait>
                    <dateHeure/>
                </util>
            </stromae>
        </xsl:copy>
    </xsl:template>

    <!-- On rajoute ces éléments dans le bind correspondant -->
    <xsl:template match="xf:bind[@id='fr-form-instance-binds']">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xf:bind id="VALIDATION-bind" name="VALIDATION" ref="VALIDATION"
                relevant="instance('fr-form-instance')/stromae/util/expedie='non'"/>
            <xf:bind id="CONFIRMATION-bind" name="CONFIRMATION" ref="CONFIRMATION"
                relevant="instance('fr-form-instance')/stromae/util/expedie='non'"/>
            <xf:bind id="FIN-bind" name="FIN" ref="FIN"/>
            <xf:bind id="nomSectionCourante-bind" name="nomSectionCourante"
                ref="stromae/util/nomSectionCourante"
                calculate="(instance('fr-form-instance')/*[child::* and not(name()='stromae')])[position()=number(instance('fr-form-instance')/stromae/util/sectionCourante)]/name()"
            />
        <xf:bind id="BarreFixe-bind" ref="BarreFixe"/>
        </xsl:copy>
    </xsl:template>

    <!-- On rajoute des éléments aux ressources -->
    <xsl:template match="resource[ancestor::xf:instance[@id='fr-form-resources']]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <VALIDATION>
                <label>VALIDATION</label>
            </VALIDATION>
            <CONFIRMATION>
                <label>CONFIRMATION</label>
            </CONFIRMATION>
            <FIN>
                <label>FIN</label>
            </FIN>
            <BarreFixe>
                <label>Votre </label>
            </BarreFixe>
            <avancement>
                <label>&lt;p&gt;&lt;b&gt;Avancement&lt;/b&gt;&lt;/p&gt;</label>
            </avancement>
        </xsl:copy>
    </xsl:template>

    <!-- On rajoute plein d'éléments au modèle -->
    <xsl:template match="xf:model[@id='fr-form-model']">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>

            <!-- Une instance pour gérer la navigation -->
            <xf:instance id="fr-form-util">
                <util>
                    <commencer/>
                    <precedent/>
                    <suivant/>
                    <envoyer/>
                    <confirmationNon/>
                    <confirmationOui/>
                    <envoi/>
                    <!-- Un élément pour empêcher pour empêcher de cliquer deux fois sur le bouton envoyer -->
                    <cliquable/>
                    <!--                            <deblocage/>-->
                    <pourcentageAvancement/>
                    <avancement/>
                    <remontePage/>
                    <pages>
                        <xsl:for-each
                            select="//*[parent::form[parent::xf:instance[@id='fr-form-instance']] and not(name()='stromae') and child::*]">
                            <xsl:element name="{name()}"/>
                        </xsl:for-each>
                        <VALIDATION/>
                        <CONFIRMATION/>
                        <FIN/>
                    </pages>
                    <precedentSuivant/>
                    <changementPageEffectue/>
                    <messageConfirmation/>
                </util>
            </xf:instance>

            <!-- Les binds correspondants -->
            <xf:bind xmlns:dataModel="java:org.orbeon.oxf.fb.DataModel" id="fr-form-util-binds"
                ref="instance('fr-form-util')">
                <xf:bind id="commencer-bind"
                    relevant="instance('fr-form-instance')/stromae/util/sectionCourante='1'"
                    ref="commencer"/>
                <xf:bind id="precedent-bind"
                    relevant="not(instance('fr-form-instance')/stromae/util/sectionCourante='1' or number(instance('fr-form-instance')/stromae/util/sectionCourante)&gt;count(instance('fr-form-util')/pages/*)-2)"
                    ref="precedent"/>
                <xf:bind id="suivant-bind"
                    relevant="not(instance('fr-form-instance')/stromae/util/sectionCourante='1' or number(instance('fr-form-instance')/stromae/util/sectionCourante)&gt;count(instance('fr-form-util')/pages/*)-3)"
                    ref="suivant"/>
                <!-- Le bouton envoyer est readonly s'il est non cliquable -->
                <xf:bind id="envoyer-bind" ref="envoyer"
                    readonly="instance('fr-form-util')/cliquable='non'"/>
                <xf:bind id="confirmationNon-bind" ref="confirmationNon"/>
                <xf:bind id="confirmationOui-bind" ref="confirmationOui"/>
                <xf:bind id="envoi-bind"
                    relevant="instance('fr-form-instance')/stromae/util/sectionCourante=string(count(instance('fr-form-instance')/*[child::* and not(name()='stromae')])) and instance('fr-form-instance')/stromae/util/expedie='non'"
                    ref="envoi"/>
                <xf:bind id="pourcentageAvancement-bind" name="pourcentageAvancement"
                    ref="pourcentageAvancement"
                    calculate="if (number(instance('fr-form-instance')/stromae/util/sectionCourante)=1) then '0'
                    else (if (number(instance('fr-form-instance')/stromae/util/sectionCourante)&gt;count(instance('fr-form-util')/pages/*)-2) then '100'
                    else round(((number(instance('fr-form-instance')/stromae/util/sectionCourante)-2) div number(count(instance('fr-form-util')/pages/*)-4))*100))"/>
                <xf:bind id="avancement-bind" ref="avancement"/>
                <xf:bind id="remontePage-bind" ref="remontePage"/>
                <xf:bind id="messageConfirmation-bind" ref="messageConfirmation"
                    name="messageConfirmation"
                    calculate="concat('Votre questionnaire a bien été expédié le ',instance('fr-form-instance')/stromae/util/dateHeure)"/>
                <xf:bind id="pages-bind" ref="pages">
                    <xsl:for-each
                        select="//*[parent::form[parent::xf:instance[@id='fr-form-instance']] and child::*]">
                        <xf:bind id="{concat('page-',name(),'-bind')}" name="{name()}"
                            ref="{name()}">
                            <xf:calculate
                                value="{concat('xxf:evaluate-bind-property(&#34;',concat(name(),'-bind'),'&#34;,&#34;relevant&#34;)')}"/>
                            <!-- On crée une contrainte égale à la somme des contraintes de niveau warning -->
                            <xsl:variable name="nomModule" select="name()"/>
                            <xsl:variable name="contrainte">
                                <xsl:for-each
                                    select="//xf:bind[@name=$nomModule]//xf:constraint[@level='warning']">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:text> and </xsl:text>
                                    </xsl:if>
                                    <xsl:value-of select="replace(@value,'//','instance(&#34;fr-form-instance&#34;)//')"/>
                                </xsl:for-each>
                            </xsl:variable>
                            <xsl:if test="$contrainte[not(text()='')]">
                                <xf:constraint value="{$contrainte/text()}"/>
                            </xsl:if>
                        </xf:bind>
                    </xsl:for-each>
                </xf:bind>
            </xf:bind>

            <!--  Enregistrer . Attention à l'ordre des 'paramètres' : formulaire doit être devant unite-enquete -->
            <xf:submission id="enregistrer" method="post" ref="instance('fr-form-instance')"
                replace="none" relevant="false">
                <xsl:variable name="ressource">
                    <xsl:value-of
                        select="concat('{xxf:property(&#34;server-exist-orbeon&#34;)}/restxq/{xxf:property(&#34;enregistrer-service&#34;)}/',$campagne,'/',$modele,'/{xxf:get-request-parameter(&quot;unite-enquete&quot;)}')"
                    />
                </xsl:variable>
                <xsl:attribute name="resource" select="$ressource"/>
                <xf:action ev:event="xforms-submit-error">
                    <xf:message>Problème lors de l'enregistrement de vos réponses.</xf:message>
                </xf:action>
                <xf:action ev:event="xforms-submit-done">
                    <!-- Ne s'affiche que s'il n'y a pas de changement de page. Cela date de l'époque où il y aurait eu un bouton Enregistrer -->
                    <xf:action if="instance('fr-form-util')/precedentSuivant='0'">
                        <xf:message>Vos réponses ont bien été enregistrées.</xf:message>
                    </xf:action>
                    <xf:setvalue ref="xxf:instance('fr-persistence-instance')/data-safe-override"
                        >true</xf:setvalue>
                </xf:action>
            </xf:submission>

            <!--  Expedier. -->
            <!-- !!!Génération consigne : Le modèle doit être paramétré, le reste est statique. -->
            <xf:submission id="expedier" method="post" ref="instance('fr-form-instance')"
                replace="none" relevant="false">

                <!-- On essaye d'enregistrer -->
                <xsl:variable name="ressource">
                    <xsl:value-of
                        select="concat('{xxf:property(&#34;server-exist-orbeon&#34;)}/restxq/{xxf:property(&#34;enregistrer-service&#34;)}/',$campagne,'/',$modele,'/{xxf:get-request-parameter(&quot;unite-enquete&quot;)}')"
                    />
                </xsl:variable>
                <xsl:attribute name="resource" select="$ressource"/>
                <!-- Si par hasard ça plante, on inscrit que le questionnaire n'a pas été expédié et on redescend l'indicateur de page-->
                <xf:action ev:event="xforms-submit-error">
                    <xf:setvalue ref="instance('fr-form-instance')/stromae/util/expedie"
                        value="string('non')"/>
                    <xf:setvalue ref="instance('fr-form-instance')/stromae/util/sectionCourante"
                        value="string(number(instance('fr-form-instance')/stromae/util/sectionCourante)-1)"/>
                    <xf:message>Problème lors de l'expédition de vos réponses.</xf:message>
                </xf:action>
                <xf:action ev:event="xforms-submit-done">
                    <!-- On switche la page -->
                    <xf:toggle case="{$choix}"/>
                    <!-- On remonte en haut de la page -->
                    <xf:setfocus control="remontePage-control"/>
                    <!-- Cette action permet d'éviter les messages d'alertes natives Orbeon quand on veut quitter le questionnaire alors qu'on a bien sauvegardé ses données. -->
                    <xf:setvalue ref="xxf:instance('fr-persistence-instance')/data-safe-override"
                        >true</xf:setvalue>
                </xf:action>
            </xf:submission>
            <xf:submission id="expedierPdf" method="post" replace="none">
                <xsl:variable name="ressource">
                    <xsl:value-of
                        select="concat('/expedier/',$campagne,'/{xxf:get-request-parameter(&quot;unite-enquete&quot;)}?modele=',$modele)"
                    />
                </xsl:variable>
                <xsl:attribute name="resource" select="$ressource"/>
            </xf:submission>
            <!-- Une action d'initialisation -->
            <!-- On va initialiser tous les champs texte variable -->
            <!-- On va intitialiser... -->
            <xf:action ev:event="xforms-ready">
                <!-- On revient sur la page qu'on avait quittée -->
                <xf:toggle case="{$choix}"/>
                <!-- Si c'est pas déjà expédié, et si on n'est pas sur la première page -->
                <xxf:show
                    if="instance('fr-form-instance')/stromae/util/expedie='non' and not(instance('fr-form-instance')/stromae/util/sectionCourante='1')"
                    dialog="bienvenue"/>
            </xf:action>

            <!-- L'action de changer de page -->
            <xf:action ev:event="ChangementPage">

                <!-- On itère sur l'ensemble des champs de la page courante et à faire un DOMFocusOut dessus pour afficher les éventuels messages d'erreur -->
                <xf:action
                    iterate="instance('fr-form-instance')/*[name()=instance('fr-form-instance')/stromae/util/nomSectionCourante]//*">
                    <xf:dispatch name="DOMFocusOut">
                        <xsl:attribute name="target">
                            <xsl:value-of
                                select="string('{concat(context()/@idVariable,&quot;-control&quot;)}')"/>
                        </xsl:attribute>
                    </xf:dispatch>
                </xf:action>

                <!-- On met cette propriété à false pour marquer le fait que le changement de page n'a pas eu lieu -->
                <xf:setvalue ref="instance('fr-form-util')/changementPageEffectue"
                    value="string('false')"/>

                <!-- Chacune des trois actions suivantes sont exclusives, mais on ne peut malheureusement pas faire de switch dans une action.
                        Et le résultat d'une des trois actions peut en déclencher une autre ce qu'on ne souhaite pas.
                        C'est pour cela qu'une action ne se déclenchera que quand la propriété changementPageEffectue est à false.
                        Chacune des trois actions finira par l'action de mettre cette propriété à true pour empêcher les suivantes de se déclencher.-->
                <xf:action
                    if="instance('fr-form-util')/changementPageEffectue='false'
                    and not(xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/stromae/util/nomSectionCourante],true(),true()))">
                    <!-- On affiche la fenêtre de dialogue qui correspond à une erreur -->
                    <xxf:show ev:event="DOMActivate" dialog="erreur"/>
                    <!-- Et on ne change pas de page -->

                    <xf:setvalue ref="instance('fr-form-util')/changementPageEffectue"
                        value="string('true')"/>
                </xf:action>
                <xf:action
                    if="instance('fr-form-util')/changementPageEffectue='false'
                    and xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/stromae/util/nomSectionCourante],true(),true())
                    and xxf:evaluate-bind-property(concat('page-',instance('fr-form-instance')/stromae/util/nomSectionCourante,'-bind'),'constraint')=false()">
                    <!-- On affiche la fenêtre de dialogue qui correspond à une erreur -->
                    <xxf:show ev:event="DOMActivate" dialog="avertissement"/>
                    <!-- Et on ne change pas de page. Le changement de page peut se produire au niveau de la fenêtre de dialogue -->

                    <xf:setvalue ref="instance('fr-form-util')/changementPageEffectue"
                        value="string('true')"/>
                </xf:action>
                <xf:action
                    if="instance('fr-form-util')/changementPageEffectue='false'
                    and xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/stromae/util/nomSectionCourante],true(),true())
                    and not(xxf:evaluate-bind-property(concat('page-',instance('fr-form-instance')/stromae/util/nomSectionCourante,'-bind'),'constraint')=false())">
                    <!-- Le changement de page a lieu -->
                    <xf:dispatch name="ChangementPageEffectif" targetid="fr-form-model"/>
                </xf:action>

            </xf:action>

            <!-- Ce qui se passe quand le changement de page est validé/effectif -->
            <xf:action ev:event="ChangementPageEffectif">
                <xf:var name="nbPagesApresPageFuture" as="xs:number"
                    value="{string('if (instance(&#34;fr-form-util&#34;)/precedentSuivant=&#34;1&#34;) then count((instance(&#34;fr-form-util&#34;)/pages/*[name()=instance(&#34;fr-form-instance&#34;)/stromae/util/nomSectionCourante]/following-sibling::*[not(text()=&#34;false&#34;)])[1]/following-sibling::*) else count((instance(&#34;fr-form-util&#34;)/pages/*[name()=instance(&#34;fr-form-instance&#34;)/stromae/util/nomSectionCourante]/preceding-sibling::*[not(text()=&#34;false&#34;)])[last()]/following-sibling::*)')}"/>
                <xf:var name="nbPagesApresPageCourante" as="xs:number"
                    value="{string('count(instance(&#34;fr-form-util&#34;)/pages/*[name()=instance(&#34;fr-form-instance&#34;)/stromae/util/nomSectionCourante]/following-sibling::*)')}"/>
                <xf:var name="pageCourante" as="xs:number"
                    value="{string('number(instance(&#34;fr-form-instance&#34;)/stromae/util/sectionCourante)')}"/>

                <!-- La section courante est modifiée -->
                <xf:setvalue ref="instance('fr-form-instance')/stromae/util/sectionCourante"
                    value="{string('string($pageCourante + $nbPagesApresPageCourante - $nbPagesApresPageFuture)')}"/>

                <!-- On enregistre la date et l'heure d'enregistrement -->
                <xf:setvalue ref="instance('fr-form-instance')/stromae/util/dateHeure"
                    value="fn:format-dateTime(fn:current-dateTime(),
                    '[D01]-[M01]-[Y0001] à [H01]:[m01]')"/>

                <!-- On switche la page -->
                <xf:toggle case="{$choix}"/>
                <!-- On enregistre -->
                <xf:send submission="enregistrer"/>
                <!-- On remonte en haut de la page -->
                <xf:setfocus control="remontePage-control"/>
            </xf:action>

        </xsl:copy>
    </xsl:template>

    <!-- On rajoute des boutons à la fin du questionnaire-->
    <xsl:template match="fr:view">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <fr:buttons>
                <xf:trigger bind="commencer-bind">
                    <xf:label>Commencer</xf:label>
                    <xf:action ev:event="DOMActivate">
                        <xf:setvalue ref="instance('fr-form-util')/precedentSuivant" value="1"/>
                        <xf:dispatch name="ChangementPage" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
                <xf:trigger bind="precedent-bind">
                    <xf:label>Retour</xf:label>
                    <xf:action ev:event="DOMActivate">
                        <xf:setvalue ref="instance('fr-form-util')/precedentSuivant" value="-1"/>
                        <xf:dispatch name="ChangementPage" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
                <xf:trigger bind="suivant-bind">
                    <xf:label>Enregistrer et continuer</xf:label>
                    <xf:action ev:event="DOMActivate">
                        <xf:setvalue ref="instance('fr-form-util')/precedentSuivant" value="1"/>
                        <xf:dispatch name="ChangementPage" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
            </fr:buttons>
        </xsl:copy>
    </xsl:template>

    <!-- On rajoute des éléments dans le corps -->
    <xsl:template match="fr:body">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <!-- Un élément pour bien être placé en haut de la page lors d'un changement de page -->
            <xf:input id="remontePage-control" bind="remontePage-bind" class="remontePage"/>
            <!-- Un élément correspondant à une barre présente sur toutes les pages -->
            <xhtml:div class="barreFixe">
                <!-- Un élément qui donne des infos sur l'unité interrogée -->
                <xf:output id="BarreFixe-control" bind="BarreFixe-bind">
                    <xf:label ref="$form-resources/BarreFixe/label" mediatype="text/html"/>
                </xf:output>
                <!-- Un élément qui mesure l'avancement dans le questionnaire -->
                <xhtml:span class="droite">
                    <xf:output id="avancement-control" bind="avancement-bind">
                        <xf:label ref="$form-resources/avancement/label">
                            <xsl:attribute name="mediatype">text/html</xsl:attribute>
                        </xf:label>
                    </xf:output>
                    <xhtml:progress id="avancement" max="100">
                        <xsl:attribute name="value">
                            <xsl:value-of
                                select="string('{instance(&quot;fr-form-util&quot;)/pourcentageAvancement}')"
                            />
                        </xsl:attribute>
                    </xhtml:progress>
                    <xf:output id="pourcentageAvancement"
                        ref="instance('fr-form-util')/pourcentageAvancement"/> %</xhtml:span>
            </xhtml:div>
            <!--<xhtml:div class="menu">
                            <xhtml:ul>
                                <xsl:apply-templates select="iat:child-fields($source-context)"
                                    mode="source">
                                    <xsl:with-param name="driver"
                                        select="il:append-empty-element('menu', .)" tunnel="yes"/>
                                    <xsl:with-param name="languages" select="$languages"
                                        tunnel="yes"/>
                                </xsl:apply-templates>
                            </xhtml:ul>
                        </xhtml:div>-->
            <!-- On rajoute un switch pour que chaque module s'affiche sur une seule page -->
            <xsl:apply-templates select="*[not(name()='fr:section')]"/>
            <xf:switch id="corpsSection">
                <xsl:apply-templates select="*[name()='fr:section']"/>
                <xf:case id="{string(number($nbModules)+1)}">
                    <fr:section id="VALIDATION-control" bind="VALIDATION-bind" name="VALIDATION">
                        <xf:label ref="$form-resources/VALIDATION/label"/>
                        <xhtml:div class="centre">
                            <xhtml:div class="frame">
                                <xhtml:p>
                                    <xhtml:b>Vous êtes arrivé à la fin du questionnaire.</xhtml:b>
                                </xhtml:p>
                                <xhtml:p class="retraitMarque"><xhtml:b>Si vous avez terminé de
                                        renseigner&#160;</xhtml:b>votre questionnaire, pour le
                                    transmettre à l'Insee, merci de cliquer ci-dessous sur le bouton
                                    : "Envoyer".</xhtml:p>
                                <xhtml:p class="retraitNonMarque">
                                    <xhtml:b>Une fois le questionnaire envoyé :</xhtml:b>
                                </xhtml:p>
                                <xhtml:p class="doubleRetrait">- vous ne pourrez&#160;<xhtml:b>plus
                                        modifier vos réponses </xhtml:b>&#160;;</xhtml:p>
                                <xhtml:p class="doubleRetrait">- vous pourrez télécharger
                                        le&#160;<xhtml:b>récapitulatif de vos réponses au format
                                        pdf</xhtml:b>.</xhtml:p>
                                <xhtml:div class="contenuCentre">
                                    <xf:trigger bind="envoyer-bind">
                                        <xf:label>Envoyer</xf:label>
                                        <!-- Lorsqu'on clique sur le bouton Envoyer, on le rend non cliquable -->
                                        <xf:action ev:event="DOMActivate">
                                            <xf:setvalue ref="instance('fr-form-util')/cliquable"
                                                value="string('non')"/>
                                            <xf:setvalue
                                                ref="instance('fr-form-util')/precedentSuivant"
                                                value="1"/>
                                            <xf:dispatch name="ChangementPageEffectif"
                                                targetid="fr-form-model"/>
                                        </xf:action>
                                    </xf:trigger>
                                </xhtml:div>
                                <xhtml:p class="retraitMarque"><xhtml:b>Si vous souhaitez y apporter
                                        des modifications</xhtml:b>, vous pouvez :</xhtml:p>
                                <xhtml:p class="doubleRetrait">- revenir dessus dès à présent en
                                    cliquant sur le bouton "Retour" ;</xhtml:p>
                                <xhtml:p class="doubleRetrait">- ou plus tard en cliquant sur le
                                    bouton "Fermer le questionnaire" et en vous authentifiant à
                                    nouveau.</xhtml:p>
                                <xhtml:p class="doubleRetrait"><xsl:text>Dans les deux cas, vos données seront
                                    enregistrées mais</xsl:text>&#160;<xhtml:b><xsl:text>le questionnaire ne sera pas
                                        envoyé à </xsl:text>l'Insee</xhtml:b>.</xhtml:p>
                                <xsl:variable name="lien">
                                    <xsl:value-of
                                        select="string('{concat(xxf:property(&#34;url-orbeon&#34;),xxf:property(&#34;lien-deconnexion&#34;))}')"
                                    />
                                </xsl:variable>
                                <xhtml:p class="contenuCentre">
                                    <xhtml:a href="{$lien}">Fermer le questionnaire</xhtml:a>
                                </xhtml:p>
                            </xhtml:div>
                        </xhtml:div>
                    </fr:section>
                </xf:case>
                <xf:case id="{string(number($nbModules)+2)}">
                    <fr:section id="CONFIRMATION-control" bind="CONFIRMATION-bind"
                        name="CONFIRMATION">
                        <xf:label ref="$form-resources/CONFIRMATION/label"/>
                        <xhtml:div class="centre contenuCentre">
                            <xhtml:div class="frame">
                                <xhtml:p>
                                    <xhtml:b>CONFIRMER VOTRE ENVOI</xhtml:b>
                                </xhtml:p>
                                <xhtml:p>Votre réponse est définitive et vous souhaitez
                                    l'envoyer.</xhtml:p>
                                <xf:trigger bind="confirmationOui-bind">
                                    <xf:label>Je confirme l'envoi</xf:label>
                                    <xf:action ev:event="DOMActivate">
                                        <!-- On inscrit dans l'instance le fait que le questionnaire est expédié -->
                                        <xf:setvalue
                                            ref="instance('fr-form-instance')/stromae/util/expedie"
                                            value="string('oui')"/>
                                        <xf:setvalue
                                            ref="instance('fr-form-instance')/stromae/util/dateHeure"
                                            value="fn:format-dateTime(fn:current-dateTime(),
                                            '[D01]-[M01]-[Y0001] à [H01]:[m01]')"/>
                                        <!-- On augmente l'indicateur de page de +1 -->
                                        <xf:setvalue
                                            ref="instance('fr-form-instance')/stromae/util/sectionCourante"
                                            value="string(number(instance('fr-form-instance')/stromae/util/sectionCourante)+1)"/>
                                        <!-- On dispatch l'expédition -->
                                        <xf:send submission="expedier"/>
                                        <xf:send submission="expedierPdf"/>
                                    </xf:action>
                                </xf:trigger>
                                <xf:trigger bind="confirmationNon-bind">
                                    <xf:label>Ne pas envoyer</xf:label>
                                    <xf:action ev:event="DOMActivate">
                                        <!-- Lorsqu'on clique sur ce bouton, on rend de nouveau le bouton Envoyer cliquable -->
                                        <xf:setvalue ref="instance('fr-form-util')/cliquable"
                                            value="string('oui')"/>
                                        <xf:setvalue ref="instance('fr-form-util')/precedentSuivant"
                                            value="-1"/>
                                        <xf:dispatch name="ChangementPageEffectif"
                                            targetid="fr-form-model"/>
                                    </xf:action>
                                </xf:trigger>
                            </xhtml:div>
                        </xhtml:div>
                    </fr:section>
                </xf:case>
                <xf:case id="{string(number($nbModules)+3)}">
                    <fr:section id="FIN-control" bind="FIN-bind" name="FIN">
                        <xf:label ref="$form-resources/FIN/label"/>
                        <xhtml:div class="centre contenuCentre">
                            <xhtml:div class="frame">
                                <xf:output id="messageConfirmation" bind="messageConfirmation-bind"
                                    class="messageConfirmation"
                                    xxf:order="label control hint help alert"/>
                                <xhtml:p>
                                    <xhtml:a href="recapitulatifPdf">Télécharger le récapitulatif de
                                        vos réponses au format PDF</xhtml:a>.
                                    <xhtml:img src="{concat('/',$proprietes//images/dossier,'/',$proprietes//images/pdf)}"/>
                                </xhtml:p>
                                <xhtml:p>
                                    <xhtml:b>La Statistique publique vous remercie de votre
                                        collaboration à cette enquête.</xhtml:b>
                                </xhtml:p>
                            </xhtml:div>
                        </xhtml:div>
                    </fr:section>
                </xf:case>
            </xf:switch>
            <xxf:dialog id="erreur" draggable="false" close="false">
                <xf:label>Erreur bloquante</xf:label>
                <xhtml:p>Certains champs de cette page sont indiqués en erreur.</xhtml:p>
                <xhtml:p>Vous devez corriger ces erreurs avant de poursuivre le remplissage de ce
                    questionnaire.</xhtml:p>
                <xf:trigger>
                    <xf:label>Corriger</xf:label>
                    <xxf:hide ev:event="DOMActivate" dialog="erreur"/>
                </xf:trigger>
            </xxf:dialog>
            <xxf:dialog id="avertissement" close="false" draggable="false">
                <xf:label>Avertissement</xf:label>
                <xhtml:p>Certains champs de cette page sont indiqués en avertissement.</xhtml:p>
                <xhtml:p>Souhaitez-vous corriger ces avertissements avant de poursuivre le
                    remplissage de ce questionnaire ?</xhtml:p>
                <xf:trigger>
                    <xf:label>Corriger</xf:label>
                    <xxf:hide ev:event="DOMActivate" dialog="avertissement"/>
                </xf:trigger>
                <xf:trigger>
                    <xf:label>Poursuivre</xf:label>
                    <xxf:hide ev:event="DOMActivate" dialog="avertissement"/>
                    <xf:action ev:event="DOMActivate">
                        <xf:dispatch name="ChangementPageEffectif" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
            </xxf:dialog>
            <xxf:dialog id="bienvenue" close="false" draggable="false">
                <xf:label>Bienvenue</xf:label>
                <xhtml:p>Vous avez déjà commencé à renseigner le questionnaire. Pour poursuivre
                    votre saisie dans le questionnaire, que souhaitez-vous faire ?</xhtml:p>
                <xf:trigger>
                    <xf:label>Revenir à la dernière page accédée</xf:label>
                    <xxf:hide ev:event="DOMActivate" dialog="bienvenue"/>
                </xf:trigger>
                <xf:trigger>
                    <xf:label>Aller à la première page</xf:label>
                    <xf:action ev:event="DOMActivate">
                        <xxf:hide dialog="bienvenue"/>
                        <!-- Pour toujours revenir à la première page sauf si le questionnaire est expédié -->
                        <xf:setvalue
                            ref="instance(&quot;fr-form-instance&quot;)/stromae/util/sectionCourante"
                            value="'1'"/>
                        <xf:toggle case="{$choix}"/>
                    </xf:action>
                </xf:trigger>
            </xxf:dialog>
        </xsl:copy>
    </xsl:template>

    <!-- On encapsule les modules existants dans un xf:case -->
    <xsl:template match="fr:section[parent::fr:body]">
        <xsl:variable name="index" select="number($nbModules)-count(following-sibling::fr:section)"/>
        <xf:case id="{$index}">
            <xsl:copy>
                <xsl:apply-templates select="node() | @*"/>
            </xsl:copy>
        </xf:case>
    </xsl:template>


    <!-- JAMAIS MIS EN PLACE, UN MENU. DEPLACE DEPUIS models.xsl. A REFAIRE -->
    <!--<xsl:template match="menu/module" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xsl:variable name="index" select="string(number(iatfr:get-index($source-context)))"/>
        <xhtml:li>
            <xsl:variable name="cssDynamique">
                <xsl:value-of
                    select="concat('{if(instance(&quot;fr-form-instance&quot;)/stromae/util/sectionCourante = string(&quot;',$index,'&quot;)) then (&quot;active&quot;) else()}')"
                />
            </xsl:variable>
            <xf:trigger class="{$cssDynamique}">
                <xf:label ref="$form-resources/{$name}/label"/>
                <!-\-                <xf:action ev:event="DOMActivate"
                    if="instance('fr-form-instance')/stromae/util/sectionCourante">
                    <xf:dispatch name="ChangementPage">
                        <xsl:attribute name="target">
                            <xsl:value-of
                                select="string('{concat(instance(&quot;fr-form-instance&quot;)/stromae/util/nomSectionCourante,&quot;-control&quot;)}')"
                            />
                        </xsl:attribute>
                    </xf:dispatch>
                    <xf:action
                        if="xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/stromae/util/nomSectionCourante],true(),true())">
                        <xf:setvalue ref="instance('fr-form-instance')/stromae/util/sectionCourante"
                            value="{$index}"/>
                        <xf:toggle case="{$choix}"/>
                        <xf:send submission="enregistrer"/>
                    </xf:action>
                </xf:action>-\->
            </xf:trigger>
        </xhtml:li>
    </xsl:template>-->

</xsl:transform>
