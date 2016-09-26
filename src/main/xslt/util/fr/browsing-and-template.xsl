<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" indent="yes" encoding="utf-8"/>
    <!-- Transformation used to add the home page on the different questionnaires. -->

    <!-- The campaign -->
    <xsl:param name="campaign" as="xs:string"/>
    <!-- The model -->
    <xsl:param name="model" as="xs:string"/>
    <!-- Eno properties file -->
    <xsl:param name="properties-file"/>
    
    <xsl:variable name="properties" select="doc($properties-file)"/>


    <xsl:variable name="choice">
        <xsl:value-of
            select="string('{instance(&quot;fr-form-instance&quot;)/stromae/util/CurrentSection}')"
        />
    </xsl:variable>

    <xsl:variable name="nb-of-modules">
        <xsl:value-of
            select="count(//*[parent::form[parent::xf:instance[@id='fr-form-instance']] and child::*])"
        />
    </xsl:variable>

    <xsl:template match="/">
        <xsl:apply-templates select="xhtml:html"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply coying to the output file</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>


    <!-- Adding those elements to the main instance -->
    <xsl:template match="xf:instance[@id='fr-form-instance']/form">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <Validation>
                <dummy/>
            </Validation>
            <Confirmation>
                <dummy/>
            </Confirmation>
            <End>
                <dummy/>
            </End>
            <stromae>
                <ProgressBarContainer/>
                <util>
                    <CurrentSection>1</CurrentSection>
                    <CurrentSectionName/>
                    <expedie>non</expedie>
                    <extrait>non</extrait>
                    <dateHeure/>
                </util>
            </stromae>
        </xsl:copy>
    </xsl:template>

    <!-- Adding those elements in the corresponding bind -->
    <xsl:template match="xf:bind[@id='fr-form-instance-binds']">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xf:bind id="validation-bind" name="validation" ref="Validation"
                relevant="instance('fr-form-instance')/stromae/util/expedie='non'"/>
            <xf:bind id="confirmation-bind" name="confirmation" ref="Confirmation"
                relevant="instance('fr-form-instance')/stromae/util/expedie='non'"/>
            <xf:bind id="end-bind" name="end" ref="End"/>
            <xf:bind id="current-section-name-bind" name="current-section-name"
                ref="stromae/util/CurrentSectionName"
                calculate="(instance('fr-form-instance')/*[child::* and not(name()='stromae')])[position()=number(instance('fr-form-instance')/stromae/util/CurrentSection)]/name()"
            />
        <xf:bind id="progress-bar-container-bind" ref="ProgressBarContainer"/>
        </xsl:copy>
    </xsl:template>

    <!-- Adding those elements to the resources -->
    <xsl:template match="resource[ancestor::xf:instance[@id='fr-form-resources']]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <Validation>
                <label>VALIDATION</label>
            </Validation>
            <Confirmation>
                <label>CONFIRMATION</label>
            </Confirmation>
            <End>
                <label>FIN</label>
            </End>
            <ProgressBarContainer>
                <label>Votre </label>
            </ProgressBarContainer>
            <Progress>
                <label>&lt;p&gt;&lt;b&gt;Avancement&lt;/b&gt;&lt;/p&gt;</label>
            </Progress>
        </xsl:copy>
    </xsl:template>

    <!-- Adding many elements to the model -->
    <xsl:template match="xf:model[@id='fr-form-model']">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>

            <!-- Instance in charge of the navigation -->
            <xf:instance id="fr-form-util">
                <util>
                    <Start/>
                    <Previous/>
                    <Next/>
                    <Send/>
                    <ConfirmationNo/>
                    <ConfirmationYes/>
                    <Sending/>
                    <!-- Element that prevent the user from clicking 2 times on the sending button -->
                    <Clickable/>
                    <!--                            <deblocage/>-->
                    <ProgressPercent/>
                    <Progress/>
                    <PageTop/>
                    <pages>
                        <xsl:for-each
                            select="//*[parent::form[parent::xf:instance[@id='fr-form-instance']] and not(name()='stromae') and child::*]">
                            <xsl:element name="{name()}"/>
                        </xsl:for-each>
                        <Validation/>
                        <Confirmation/>
                        <End/>
                    </pages>
                    <PreviousNext/>
                    <PageChangeDone/>
                    <ConfirmationMessage/>
                </util>
            </xf:instance>

            <!-- The corresponding binds -->
            <xf:bind xmlns:dataModel="java:org.orbeon.oxf.fb.DataModel" id="fr-form-util-binds"
                ref="instance('fr-form-util')">
                <xf:bind id="start-bind"
                    relevant="instance('fr-form-instance')/stromae/util/CurrentSection='1'"
                    ref="Start"/>
                <xf:bind id="previous-bind"
                    relevant="not(instance('fr-form-instance')/stromae/util/CurrentSection='1' or number(instance('fr-form-instance')/stromae/util/CurrentSection)&gt;count(instance('fr-form-util')/pages/*)-2)"
                    ref="Previous"/>
                <xf:bind id="next-bind"
                    relevant="not(instance('fr-form-instance')/stromae/util/CurrentSection='1' or number(instance('fr-form-instance')/stromae/util/CurrentSection)&gt;count(instance('fr-form-util')/pages/*)-3)"
                    ref="Next"/>
                <!-- The sending button is readonly if he's not clickable -->
                <xf:bind id="send-bind" ref="Send"
                    readonly="instance('fr-form-util')/Clickable='non'"/>
                <xf:bind id="confirmation-no-bind" ref="ConfirmationNo"/>
                <xf:bind id="confirmation-yes-bind" ref="ConfirmationYes"/>
                <xf:bind id="sending-bind"
                    relevant="instance('fr-form-instance')/stromae/util/CurrentSection=string(count(instance('fr-form-instance')/*[child::* and not(name()='stromae')])) and instance('fr-form-instance')/stromae/util/expedie='non'"
                    ref="Sending"/>
                <xf:bind id="progress-percent-bind" name="progress-percent"
                    ref="ProgressPercent"
                    calculate="if (number(instance('fr-form-instance')/stromae/util/CurrentSection)=1) then '0'
                    else (if (number(instance('fr-form-instance')/stromae/util/CurrentSection)&gt;count(instance('fr-form-util')/pages/*)-2) then '100'
                    else round(((number(instance('fr-form-instance')/stromae/util/CurrentSection)-2) div number(count(instance('fr-form-util')/pages/*)-4))*100))"/>
                <xf:bind id="progress-bind" ref="Progress"/>
                <xf:bind id="page-top-bind" ref="PageTop"/>
                <xf:bind id="confirmation-message-bind" ref="ConfirmationMessage"
                    name="confirmation-message"
                    calculate="concat('Votre questionnaire a bien été expédié le ',instance('fr-form-instance')/stromae/util/dateHeure)"/>
                <xf:bind id="pages-bind" ref="pages">
                    <xsl:for-each
                        select="//*[parent::form[parent::xf:instance[@id='fr-form-instance']] and child::*]">
                        <xf:bind id="{concat('page-',name(),'-bind')}" name="{name()}"
                            ref="{name()}">
                            <xf:calculate
                                value="{concat('xxf:evaluate-bind-property(&#34;',concat(name(),'-bind'),'&#34;,&#34;relevant&#34;)')}"/>
                            <!-- Creating a constraint equals to the sum of warning-level constraints -->
                            <xsl:variable name="moduleName" select="name()"/>
                            <xsl:variable name="constraint">
                                <xsl:for-each
                                    select="//xf:bind[@name=$moduleName]//xf:constraint[@level='warning']">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:text> and </xsl:text>
                                    </xsl:if>
                                    <xsl:value-of select="replace(@value,'//','instance(&#34;fr-form-instance&#34;)//')"/>
                                </xsl:for-each>
                            </xsl:variable>
                            <xsl:if test="$constraint[not(text()='')]">
                                <xf:constraint value="{$constraint/text()}"/>
                            </xsl:if>
                        </xf:bind>
                    </xsl:for-each>
                </xf:bind>
            </xf:bind>

            <!--  Saving : be careful on the parameters order : formulaire must stand before unite-enquete -->
            <xf:submission id="save" method="post" ref="instance('fr-form-instance')"
                replace="none" relevant="false">
                <xsl:variable name="resource">
                    <xsl:value-of
                        select="concat('{xxf:property(&#34;server-exist-orbeon&#34;)}/restxq/{xxf:property(&#34;enregistrer-service&#34;)}/',$campaign,'/',$model,'/{xxf:get-request-parameter(&quot;unite-enquete&quot;)}')"
                    />
                </xsl:variable>
                <xsl:attribute name="resource" select="$resource"/>
                <xf:action ev:event="xforms-submit-error">
                    <xf:message>Problème lors de l'enregistrement de vos réponses.</xf:message>
                </xf:action>
                <xf:action ev:event="xforms-submit-done">
                    <!-- Only displayed if a page change happened. Old code from time where there would've been a save button -->
                    <xf:action if="instance('fr-form-util')/PreviousNext='0'">
                        <xf:message>Vos réponses ont bien été enregistrées.</xf:message>
                    </xf:action>
                    <xf:setvalue ref="xxf:instance('fr-persistence-instance')/data-safe-override"
                        >true</xf:setvalue>
                </xf:action>
            </xf:submission>

            <!--  Submitting. -->
            <!--  Hint / Instruction generation : the model must be configured, rest is static. -->
            <xf:submission id="submit" method="post" ref="instance('fr-form-instance')"
                replace="none" relevant="false">

                <!-- Trying to save -->
                <xsl:variable name="resource">
                    <xsl:value-of
                        select="concat('{xxf:property(&#34;server-exist-orbeon&#34;)}/restxq/{xxf:property(&#34;enregistrer-service&#34;)}/',$campaign,'/',$model,'/{xxf:get-request-parameter(&quot;unite-enquete&quot;)}')"
                    />
                </xsl:variable>
                <xsl:attribute name="resource" select="$resource"/>
                <!-- If somehow it crashes, we register the survey as not submitted and the page marker is brought down-->
                <xf:action ev:event="xforms-submit-error">
                    <xf:setvalue ref="instance('fr-form-instance')/stromae/util/expedie"
                        value="string('non')"/>
                    <xf:setvalue ref="instance('fr-form-instance')/stromae/util/CurrentSection"
                        value="string(number(instance('fr-form-instance')/stromae/util/CurrentSection)-1)"/>
                    <xf:message>Problème lors de l'expédition de vos réponses.</xf:message>
                </xf:action>
                <xf:action ev:event="xforms-submit-done">
                    <!-- Switching page -->
                    <xf:toggle case="{$choice}"/>
                    <!-- Going to the top of the page -->
                    <xf:setfocus control="page-top-control"/>
                    <!-- This helps to avoid native Orbeon alert messages when wanting to leave the survey even thought the datas are saved. -->
                    <xf:setvalue ref="xxf:instance('fr-persistence-instance')/data-safe-override"
                        >true</xf:setvalue>
                </xf:action>
            </xf:submission>
            <xf:submission id="submit-pdf" method="post" replace="none">
                <xsl:variable name="resource">
                    <xsl:value-of
                        select="concat('/expedier/',$campaign,'/{xxf:get-request-parameter(&quot;unite-enquete&quot;)}?modele=',$model)"
                    />
                </xsl:variable>
                <xsl:attribute name="resource" select="$resource"/>
            </xf:submission>
            <!-- Initialization action -->
            <!-- Initialization of all variable text fields -->
            <xf:action ev:event="xforms-ready">
                <!-- Going back to the page we left -->
                <xf:toggle case="{$choice}"/>
                <!-- If this isn't submitted yet, and we're not on the first page -->
                <xxf:show
                    if="instance('fr-form-instance')/stromae/util/expedie='non' and not(instance('fr-form-instance')/stromae/util/CurrentSection='1')"
                    dialog="welcome-back"/>
            </xf:action>

            <!-- Page changing action -->
            <xf:action ev:event="page-change">

                <!-- Iterating on every field of the current page and doing a DOMFocusOut in order to display potential error messages -->
                <xf:action
                    iterate="instance('fr-form-instance')/*[name()=instance('fr-form-instance')/stromae/util/CurrentSectionName]//*">
                    <xf:dispatch name="DOMFocusOut">
                        <xsl:attribute name="target">
                            <xsl:value-of
                                select="string('{concat(context()/@idVariable,&quot;-control&quot;)}')"/>
                        </xsl:attribute>
                    </xf:dispatch>
                </xf:action>

                <!-- Forcing this to false to notify that the page change isn't done yet. -->
                <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                    value="string('false')"/>


                <!-- Every action below is exclusive (works as a switch case)
                        The result of each action can initiate another one, but we don't want that.
                        Therefore, an action will only occur when the PageChangeDone property will be false.
                        Also, each action will end by setting this property to true (which will prevent other actions from triggering-->
                <xf:action
                    if="instance('fr-form-util')/PageChangeDone='false'
                    and not(xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/stromae/util/CurrentSectionName],true(),true()))">
                    <!-- Displaying the dialog window that correspond to an error -->
                    <xxf:show ev:event="DOMActivate" dialog="error"/>
                    <!-- And we don't change page -->

                    <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                        value="string('true')"/>
                </xf:action>
                <xf:action
                    if="instance('fr-form-util')/PageChangeDone='false'
                    and xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/stromae/util/CurrentSectionName],true(),true())
                    and xxf:evaluate-bind-property(concat('page-',instance('fr-form-instance')/stromae/util/CurrentSectionName,'-bind'),'constraint')=false()">
                    <!-- Displaying the dialog window that correspond to an error -->
                    <xxf:show ev:event="DOMActivate" dialog="warning"/>
                    <!-- And we don't change page. The page change can happen at the level of this dialog window -->

                    <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                        value="string('true')"/>
                </xf:action>
                <xf:action
                    if="instance('fr-form-util')/PageChangeDone='false'
                    and xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/stromae/util/CurrentSectionName],true(),true())
                    and not(xxf:evaluate-bind-property(concat('page-',instance('fr-form-instance')/stromae/util/CurrentSectionName,'-bind'),'constraint')=false())">
                    <!-- The page change happens -->
                    <xf:dispatch name="page-change-done" targetid="fr-form-model"/>
                </xf:action>

            </xf:action>

            <!-- What happens when the page change is effective -->
            <xf:action ev:event="page-change-done">
                <xf:var name="new-number-of-following-pages" as="xs:number"
                    value="{string('if (instance(&#34;fr-form-util&#34;)/PreviousNext=&#34;1&#34;) then count((instance(&#34;fr-form-util&#34;)/pages/*[name()=instance(&#34;fr-form-instance&#34;)/stromae/util/CurrentSectionName]/following-sibling::*[not(text()=&#34;false&#34;)])[1]/following-sibling::*) else count((instance(&#34;fr-form-util&#34;)/pages/*[name()=instance(&#34;fr-form-instance&#34;)/stromae/util/CurrentSectionName]/preceding-sibling::*[not(text()=&#34;false&#34;)])[last()]/following-sibling::*)')}"/>
                <xf:var name="old-number-of-following-pages" as="xs:number"
                    value="{string('count(instance(&#34;fr-form-util&#34;)/pages/*[name()=instance(&#34;fr-form-instance&#34;)/stromae/util/CurrentSectionName]/following-sibling::*)')}"/>
                <xf:var name="number-of-left-page" as="xs:number"
                    value="{string('number(instance(&#34;fr-form-instance&#34;)/stromae/util/CurrentSection)')}"/>

                <!-- The current section is modified -->
                <xf:setvalue ref="instance('fr-form-instance')/stromae/util/CurrentSection"
                    value="{string('string($number-of-left-page + $old-number-of-following-pages - $new-number-of-following-pages)')}"/>

                <!-- Saving the time when the saving happened -->
                <xf:setvalue ref="instance('fr-form-instance')/stromae/util/dateHeure"
                    value="fn:format-dateTime(fn:current-dateTime(),
                    '[D01]-[M01]-[Y0001] à [H01]:[m01]')"/>

                <!-- Switching page -->
                <xf:toggle case="{$choice}"/>
                <!-- Saving -->
                <xf:send submission="save"/>
                <!-- Going at the top of the page -->
                <xf:setfocus control="page-top-control"/>
            </xf:action>

        </xsl:copy>
    </xsl:template>

    <!-- Adding buttons at the end of the survey-->
    <xsl:template match="fr:view">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <fr:buttons>
                <xf:trigger bind="start-bind">
                    <xf:label>Commencer</xf:label>
                    <xf:action ev:event="DOMActivate">
                        <xf:setvalue ref="instance('fr-form-util')/PreviousNext" value="1"/>
                        <xf:dispatch name="page-change" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
                <xf:trigger bind="previous-bind">
                    <xf:label>Retour</xf:label>
                    <xf:action ev:event="DOMActivate">
                        <xf:setvalue ref="instance('fr-form-util')/PreviousNext" value="-1"/>
                        <xf:dispatch name="page-change" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
                <xf:trigger bind="next-bind">
                    <xf:label>Enregistrer et continuer</xf:label>
                    <xf:action ev:event="DOMActivate">
                        <xf:setvalue ref="instance('fr-form-util')/PreviousNext" value="1"/>
                        <xf:dispatch name="page-change" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
            </fr:buttons>
        </xsl:copy>
    </xsl:template>

    <!-- Adding element to the body -->
    <xsl:template match="fr:body">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <!-- This helps being placed at the top of the page when changing page -->
            <xf:input id="page-top-control" bind="page-top-bind" class="page-top"/>
            <!-- Representing the progress-bar element on every page -->
            <xhtml:div class="progress-bar-container">
                <!-- This element gives information on the questionned unit -->
                <xf:output id="progress-bar-container-control" bind="progress-bar-container-bind">
                    <xf:label ref="$form-resources/ProgressBarContainer/label" mediatype="text/html"/>
                </xf:output>
                <!-- This element measures the survey's progress -->
                <xhtml:span class="right">
                    <xf:output id="progress-control" bind="progress-bind">
                        <xf:label ref="$form-resources/Progress/label">
                            <xsl:attribute name="mediatype">text/html</xsl:attribute>
                        </xf:label>
                    </xf:output>
                    <xhtml:progress id="progress" max="100">
                        <xsl:attribute name="value">
                            <xsl:value-of
                                select="string('{instance(&quot;fr-form-util&quot;)/ProgressPercent}')"
                            />
                        </xsl:attribute>
                    </xhtml:progress>
                    <xf:output id="progress-percent"
                        ref="instance('fr-form-util')/ProgressPercent"/> %</xhtml:span>
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
            <!-- Using a switch in order to display each module on the same page -->
            <xsl:apply-templates select="*[not(name()='fr:section')]"/>
            <xf:switch id="section-body">
                <xsl:apply-templates select="*[name()='fr:section']"/>
                <xf:case id="{string(number($nb-of-modules)+1)}">
                    <fr:section id="validation-control" bind="validation-bind" name="validation">
                        <xf:label ref="$form-resources/Validation/label"/>
                        <xhtml:div class="center">
                            <xhtml:div class="frame">
                                <xhtml:p>
                                    <xhtml:b>Vous êtes arrivé à la fin du questionnaire.</xhtml:b>
                                </xhtml:p>
                                <xhtml:p class="indentation-with-bullet"><xhtml:b>Si vous avez terminé de
                                        renseigner&#160;</xhtml:b>votre questionnaire, pour le
                                    transmettre à l'Insee, merci de cliquer ci-dessous sur le bouton
                                    : "Envoyer".</xhtml:p>
                                <xhtml:p class="simple-identation">
                                    <xhtml:b>Une fois le questionnaire envoyé :</xhtml:b>
                                </xhtml:p>
                                <xhtml:p class="double-indentation">- vous ne pourrez&#160;<xhtml:b>plus
                                        modifier vos réponses </xhtml:b>&#160;;</xhtml:p>
                                <xhtml:p class="double-indentation">- vous pourrez télécharger
                                        le&#160;<xhtml:b>récapitulatif de vos réponses au format
                                        pdf</xhtml:b>.</xhtml:p>
                                <xhtml:div class="center-body">
                                    <xf:trigger bind="send-bind">
                                        <xf:label>Envoyer</xf:label>
                                        <!-- When clicking on the sending button, making it not clickable -->
                                        <xf:action ev:event="DOMActivate">
                                            <xf:setvalue ref="instance('fr-form-util')/Clickable"
                                                value="string('non')"/>
                                            <xf:setvalue
                                                ref="instance('fr-form-util')/PreviousNext"
                                                value="1"/>
                                            <xf:dispatch name="page-change-done"
                                                targetid="fr-form-model"/>
                                        </xf:action>
                                    </xf:trigger>
                                </xhtml:div>
                                <xhtml:p class="indentation-with-bullet"><xhtml:b>Si vous souhaitez y apporter
                                        des modifications</xhtml:b>, vous pouvez :</xhtml:p>
                                <xhtml:p class="double-indentation">- revenir dessus dès à présent en
                                    cliquant sur le bouton "Retour" ;</xhtml:p>
                                <xhtml:p class="double-indentation">- ou plus tard en cliquant sur le
                                    bouton "Fermer le questionnaire" et en vous authentifiant à
                                    nouveau.</xhtml:p>
                                <xhtml:p class="double-indentation"><xsl:text>Dans les deux cas, vos données seront
                                    enregistrées mais</xsl:text>&#160;<xhtml:b><xsl:text>le questionnaire ne sera pas
                                        envoyé à </xsl:text>l'Insee</xhtml:b>.</xhtml:p>
                                <xsl:variable name="link">
                                    <xsl:value-of
                                        select="string('{concat(xxf:property(&#34;url-orbeon&#34;),xxf:property(&#34;lien-deconnexion&#34;))}')"
                                    />
                                </xsl:variable>
                                <xhtml:p class="center-body">
                                    <xhtml:a href="{$link}">Fermer le questionnaire</xhtml:a>
                                </xhtml:p>
                            </xhtml:div>
                        </xhtml:div>
                    </fr:section>
                </xf:case>
                <xf:case id="{string(number($nb-of-modules)+2)}">
                    <fr:section id="confirmation-control" bind="confirmation-bind"
                        name="confirmation">
                        <xf:label ref="$form-resources/Confirmation/label"/>
                        <xhtml:div class="center center-body">
                            <xhtml:div class="frame">
                                <xhtml:p>
                                    <xhtml:b>CONFIRMER VOTRE ENVOI</xhtml:b>
                                </xhtml:p>
                                <xhtml:p>Votre réponse est définitive et vous souhaitez
                                    l'envoyer.</xhtml:p>
                                <xf:trigger bind="confirmation-yes-bind">
                                    <xf:label>Je confirme l'envoi</xf:label>
                                    <xf:action ev:event="DOMActivate">
                                        <!-- Writing in the instance that the survey is submitted -->
                                        <xf:setvalue
                                            ref="instance('fr-form-instance')/stromae/util/expedie"
                                            value="string('oui')"/>
                                        <xf:setvalue
                                            ref="instance('fr-form-instance')/stromae/util/dateHeure"
                                            value="fn:format-dateTime(fn:current-dateTime(),
                                            '[D01]-[M01]-[Y0001] à [H01]:[m01]')"/>
                                        <!-- Setting the page indicator + 1-->
                                        <xf:setvalue
                                            ref="instance('fr-form-instance')/stromae/util/CurrentSection"
                                            value="string(number(instance('fr-form-instance')/stromae/util/CurrentSection)+1)"/>
                                        <!-- Dispatching the submission -->
                                        <xf:send submission="submit"/>
                                        <xf:send submission="submit-pdf"/>
                                    </xf:action>
                                </xf:trigger>
                                <xf:trigger bind="confirmation-no-bind">
                                    <xf:label>Ne pas envoyer</xf:label>
                                    <xf:action ev:event="DOMActivate">
                                        <!-- When clicking on this button, we make the Sending button clickable again -->
                                        <xf:setvalue ref="instance('fr-form-util')/Clickable"
                                            value="string('oui')"/>
                                        <xf:setvalue ref="instance('fr-form-util')/PreviousNext"
                                            value="-1"/>
                                        <xf:dispatch name="page-change-done"
                                            targetid="fr-form-model"/>
                                    </xf:action>
                                </xf:trigger>
                            </xhtml:div>
                        </xhtml:div>
                    </fr:section>
                </xf:case>
                <xf:case id="{string(number($nb-of-modules)+3)}">
                    <fr:section id="end-control" bind="end-bind" name="end">
                        <xf:label ref="$form-resources/End/label"/>
                        <xhtml:div class="center center-body">
                            <xhtml:div class="frame">
                                <xf:output id="confirmation-message" bind="confirmation-message-bind"
                                    class="confirmation-message"
                                    xxf:order="label control hint help alert"/>
                                <xhtml:p>
                                    <xhtml:a href="PDFSummary">Télécharger le récapitulatif de
                                        vos réponses au format PDF</xhtml:a>.
                                    <xhtml:img src="{concat('/',$properties//images/dossier,'/',$properties//images/pdf)}"/>
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
            <xxf:dialog id="error" draggable="false" close="false">
                <xf:label>Erreur bloquante</xf:label>
                <xhtml:p>Certains champs de cette page sont indiqués en erreur.</xhtml:p>
                <xhtml:p>Vous devez corriger ces erreurs avant de poursuivre le remplissage de ce
                    questionnaire.</xhtml:p>
                <xf:trigger>
                    <xf:label>Corriger</xf:label>
                    <xxf:hide ev:event="DOMActivate" dialog="error"/>
                </xf:trigger>
            </xxf:dialog>
            <xxf:dialog id="warning" close="false" draggable="false">
                <xf:label>Avertissement</xf:label>
                <xhtml:p>Certains champs de cette page sont indiqués en avertissement.</xhtml:p>
                <xhtml:p>Souhaitez-vous corriger ces avertissements avant de poursuivre le
                    remplissage de ce questionnaire ?</xhtml:p>
                <xf:trigger>
                    <xf:label>Corriger</xf:label>
                    <xxf:hide ev:event="DOMActivate" dialog="warning"/>
                </xf:trigger>
                <xf:trigger>
                    <xf:label>Poursuivre</xf:label>
                    <xxf:hide ev:event="DOMActivate" dialog="warning"/>
                    <xf:action ev:event="DOMActivate">
                        <xf:dispatch name="page-change-done" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
            </xxf:dialog>
            <xxf:dialog id="welcome-back" close="false" draggable="false">
                <xf:label>Bienvenue</xf:label>
                <xhtml:p>Vous avez déjà commencé à renseigner le questionnaire. Pour poursuivre
                    votre saisie dans le questionnaire, que souhaitez-vous faire ?</xhtml:p>
                <xf:trigger>
                    <xf:label>Revenir à la dernière page accédée</xf:label>
                    <xxf:hide ev:event="DOMActivate" dialog="welcome-back"/>
                </xf:trigger>
                <xf:trigger>
                    <xf:label>Aller à la première page</xf:label>
                    <xf:action ev:event="DOMActivate">
                        <xxf:hide dialog="welcome-back"/>
                        <!-- Always going back to the first page except if the survey is submitted -->
                        <xf:setvalue
                            ref="instance(&quot;fr-form-instance&quot;)/stromae/util/CurrentSection"
                            value="'1'"/>
                        <xf:toggle case="{$choice}"/>
                    </xf:action>
                </xf:trigger>
            </xxf:dialog>
        </xsl:copy>
    </xsl:template>

    <!-- Wrapping the existing modules in a xf:case -->
    <xsl:template match="fr:section[parent::fr:body]">
        <xsl:variable name="index" select="number($nb-of-modules)-count(following-sibling::fr:section)"/>
        <xf:case id="{$index}">
            <xsl:copy>
                <xsl:apply-templates select="node() | @*"/>
            </xsl:copy>
        </xf:case>
    </xsl:template>


    <!-- Never used, a menu. Moved from models.xsl : to redo. -->
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
