<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:xf="http://www.w3.org/2002/xforms"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
    xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:ev="http://www.w3.org/2001/xml-events"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xd" version="2.0">

    <!-- This stylesheet is applied to basic-form.tmp (previously created in the ddi2fr target) -->
    <!-- It adds orbeon related elements to enable the desired navigation. -->
    <!-- Transformation used to add the home page on the different questionnaires. -->

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:strip-space elements="*"/>

    <!-- Eno properties file -->
    <xsl:param name="properties-file"/>

    <xsl:variable name="properties" select="doc($properties-file)"/>

    <!-- Saving the CurrentSection in a variable -->
    <xsl:variable name="choice">
        <xsl:value-of select="'{instance(''fr-form-instance'')/Util/CurrentSection}'"/>
    </xsl:variable>

    <!-- Counting the number of modules and storing it -->
    <xsl:variable name="number-of-modules">
        <xsl:value-of select="count(//fr:body/*[name()='fr:section' or name()='xf:repeat'])"/>
    </xsl:variable>

    <xsl:variable name="number-of-pages-before-progress-start" as="xs:integer">
        <xsl:value-of select="number(1)"/>
    </xsl:variable>
    <xsl:variable name="number-of-pages-after-progress-end" as="xs:integer">
        <xsl:value-of select="number(1)"/>
    </xsl:variable>

    <xsl:template match="/">
        <xsl:apply-templates select="xhtml:html"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply coying to the
                output file</xd:p>
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
            <xsl:apply-templates select="@*"/>
            <Beginning>
                <Dummy/>
            </Beginning>
            <xsl:apply-templates select="node()"/>
            <End>
                <Dummy/>
            </End>
            <Util>
                <CurrentSection>1</CurrentSection>
                <CurrentSectionName/>
                <xsl:apply-templates select="//fr:body/xf:repeat" mode="page-loop"/>
                <Send>false</Send>
                <DateTime/>
            </Util>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xf:repeat" mode="page-loop">
        <xsl:variable name="loop-name" select="@id"/>
        <CurrentLoopElement loop-name="{$loop-name}">0</CurrentLoopElement>
    </xsl:template>

    <!-- Adding those elements in the corresponding bind -->
    <xsl:template match="xf:bind[@id='fr-form-instance-binds']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xf:bind id="beginning-bind" name="beginning" ref="Beginning"/>
            <xsl:apply-templates select="node()"/>
            <xf:bind id="end-bind" name="end" ref="End"/>
            <xf:bind id="current-section-name-bind" name="current-section-name"
                ref="Util/CurrentSectionName">
                <xsl:attribute name="calculate">
                    <xsl:for-each select="//fr:body/xf:repeat">
                        <xsl:variable name="section-position">
                            <xsl:value-of select="count(preceding-sibling::*)+1"/>
                        </xsl:variable>
                        <xsl:variable name="loop-module" select="fr:section/@name"/>
                        <xsl:value-of
                            select="concat('(if (instance(''fr-form-instance'')/Util/CurrentSection=''',$section-position,''')
                            then ''',$loop-module,''' else ')"
                        />
                    </xsl:for-each>
                    <xsl:value-of
                        select="'(instance(''fr-form-instance'')/*[child::* and not(name()=''Util'')])[position()=number(instance(''fr-form-instance'')/Util/CurrentSection)]/name()'"/>
                    <xsl:for-each select="//fr:body/xf:repeat">
                        <xsl:value-of select="')'"/>
                    </xsl:for-each>
                </xsl:attribute>
            </xf:bind>
        </xsl:copy>
    </xsl:template>

    <!-- Direct child of a loop at the root of the questionnaire : it means this child is considered as a module -->
    <xsl:template match="xf:bind[@id='fr-form-instance-binds']/xf:bind[@nodeset]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="relevant">
                <xsl:value-of
                    select="concat('(count(preceding-sibling::*[name()=''',@name,'''])+1)=instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@name,''']')"
                />
            </xsl:attribute>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>


    <!-- Adding those elements to the resources -->
    <xsl:template
        match="resource[@xml:lang='en' and ancestor::xf:instance[@id='fr-form-resources']]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <Beginning>
                <label>BEGINNING</label>
            </Beginning>
            <xsl:apply-templates select="node()"/>
            <End>
                <label>END</label>
            </End>
            <Progress>
                <label>&lt;p&gt;&lt;b&gt;Progress&lt;/b&gt;&lt;/p&gt;</label>
            </Progress>
            <Start>
                <label>Start</label>
            </Start>
            <Previous>
                <label>Go Back</label>
            </Previous>
            <Next>
                <label>Save and continue</label>
            </Next>
            <Send>
                <label>Send</label>
            </Send>
            <ConfirmationMessage>
                <label>Your questionnaire was submitted the</label>
            </ConfirmationMessage>
            <FatalError>
                <label>There was a problem with saving/submitting your answers.</label>
            </FatalError>
            <Correct>
                <label>Correct</label>
            </Correct>
            <Continue>
                <label>Continue</label>
            </Continue>
            <GoBack>
                <label>Go back to the last accessed page</label>
            </GoBack>
            <GoToFirstPage>
                <label>Go to the first page</label>
            </GoToFirstPage>
            <WelcomeBack>
                <label>Welcome</label>
            </WelcomeBack>
            <Warning>
                <label>Warning</label>
            </Warning>
            <Error>
                <label>Blocking error</label>
            </Error>
            <WelcomeBackText>
                <label>&lt;p&gt;You started filling the questionnaire. To continue, what do you wish
                    to do ?&lt;/p&gt;</label>
            </WelcomeBackText>
            <WarningText>
                <label>&lt;p&gt;Some fields of this page are marked as
                    warnings.&lt;/p&gt;&lt;p&gt;Do you wish to correct those warnings before going
                    on filling the questionnaire ?&lt;/p&gt;</label>
            </WarningText>
            <ErrorText>
                <label>&lt;p&gt;Some fields of this page are marked as errors.&lt;/p&gt;&lt;p&gt;You
                    need to correct those warnings before going on filling the
                    questionnaire.&lt;/p&gt;</label>
            </ErrorText>
        </xsl:copy>
    </xsl:template>
    <xsl:template
        match="resource[@xml:lang='fr' and ancestor::xf:instance[@id='fr-form-resources']]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <Beginning>
                <label>DEBUT</label>
            </Beginning>
            <xsl:apply-templates select="node()"/>
            <End>
                <label>FIN</label>
            </End>
            <Progress>
                <label>&lt;p&gt;&lt;b&gt;Avancement&lt;/b&gt;&lt;/p&gt;</label>
            </Progress>
            <Start>
                <label>Commencer</label>
            </Start>
            <Previous>
                <label>Retour</label>
            </Previous>
            <Next>
                <label>Enregistrer et continuer</label>
            </Next>
            <Send>
                <label>Envoyer</label>
            </Send>
            <ConfirmationMessage>
                <label>Votre questionnaire a bien été expédié le</label>
            </ConfirmationMessage>
            <FatalError>
                <label>Problème lors de l'enregistrement/l'expédition de vos réponses.</label>
            </FatalError>
            <Correct>
                <label>Corriger</label>
            </Correct>
            <Continue>
                <label>Poursuivre</label>
            </Continue>
            <GoBack>
                <label>Revenir à la dernière page accédée</label>
            </GoBack>
            <GoToFirstPage>
                <label>Aller à la première page</label>
            </GoToFirstPage>
            <WelcomeBack>
                <label>Bienvenue</label>
            </WelcomeBack>
            <Warning>
                <label>Avertissement</label>
            </Warning>
            <Error>
                <label>Erreur bloquante</label>
            </Error>
            <WelcomeBackText>
                <label>&lt;p&gt;Vous avez déjà commencé à renseigner le questionnaire. Pour
                    poursuivre votre saisie dans le questionnaire, que souhaitez-vous faire
                    ?&lt;/p&gt;</label>
            </WelcomeBackText>
            <WarningText>
                <label>&lt;p&gt;Certains champs de cette page sont indiqués en
                    avertissement.&lt;/p&gt;&lt;p&gt;Souhaitez-vous corriger ces avertissements
                    avant de poursuivre le remplissage de ce questionnaire ?&lt;/p&gt;</label>
            </WarningText>
            <ErrorText>
                <label>&lt;p&gt;Certains champs de cette page sont indiqués en
                    erreur.&lt;/p&gt;&lt;p&gt;Vous devez corriger ces erreurs avant de poursuivre le
                    remplissage de ce questionnaire.&lt;/p&gt;</label>
            </ErrorText>
        </xsl:copy>
    </xsl:template>

    <!-- Adding many elements to the model -->
    <xsl:template match="xf:model[@id='fr-form-model']">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>

            <!-- Instance in charge of the navigation -->
            <xf:instance id="fr-form-util">
                <Util>
                    <Start/>
                    <Previous/>
                    <Next/>
                    <Send/>
                    <ProgressPercent/>
                    <Progress/>
                    <PageTop/>
                    <Pages>
                        <Beginning/>
                        <xsl:for-each
                            select="//*[parent::form[parent::xf:instance[@id='fr-form-instance']] and not(name()='Util') and child::*]">
                            <xsl:element name="{name()}"/>
                        </xsl:for-each>
                        <End/>
                    </Pages>
                    <PreviousNext/>
                    <PageChangeDone/>
                    <ConfirmationMessage/>
                    <FatalError/>
                    <ErrorText/>
                    <WarningText/>
                    <WelcomeBackText/>
                </Util>
            </xf:instance>

            <!-- The corresponding binds -->
            <xf:bind id="fr-form-util-binds" ref="instance('fr-form-util')">
                <xf:bind id="start-bind"
                    relevant="instance('fr-form-instance')/Util/CurrentSection='1'"
                    ref="Start"/>
                <xf:bind id="previous-bind"
                    relevant="not(instance('fr-form-instance')/Util/CurrentSection='1' or number(instance('fr-form-instance')/Util/CurrentSection)&gt;count(instance('fr-form-util')/Pages/*))"
                    ref="Previous"/>
                <xf:bind id="next-bind"
                    relevant="not(instance('fr-form-instance')/Util/CurrentSection='1' or number(instance('fr-form-instance')/Util/CurrentSection)&gt;count(instance('fr-form-util')/Pages/*)-1)"
                    ref="Next"/>
                <xf:bind id="send-bind" ref="Send"
                    relevant="instance('fr-form-instance')/Util/Send='false'"/>
                <xf:bind id="progress-percent-bind" name="progress-percent" ref="ProgressPercent">
                    <xsl:attribute name="calculate">
                        <xsl:variable name="total-group-pages-count">
                            <xsl:for-each select="//fr:body/xf:repeat">
                                <xsl:variable name="group-pages-number" select="count(fr:section)"/>
                                <xsl:variable name="group-name" select="@id"/>
                                <xsl:value-of
                                    select="concat('+count(instance(''fr-form-instance'')/',$group-name,')*',$group-pages-number,'-1')"
                                />
                            </xsl:for-each>
                        </xsl:variable>
                        <xsl:variable name="denominator">
                            <xsl:value-of
                                select="concat('(count(instance(''fr-form-util'')/Pages/*)-4',$total-group-pages-count,')')"
                            />
                        </xsl:variable>

                        <xsl:value-of
                            select="'if (number(instance(''fr-form-instance'')/Util/CurrentSection)=1) then ''0'' '"/>
                        <xsl:value-of
                            select="'else (if (number(instance(''fr-form-instance'')/Util/CurrentSection)&gt;count(instance(''fr-form-util'')/Pages/*)-2) then ''100'''"/>
                        <xsl:for-each select="//fr:body/xf:repeat">
                            <xsl:variable name="occurrence-position"
                                select="count(//fr:body/xf:repeat/preceding-sibling::fr:section)+1"/>
                            <xsl:variable name="group-name" select="@id"/>
                            <xsl:variable name="previous-group-pages-count">
                                <xsl:for-each
                                    select="//fr:body/xf:repeat[following-sibling::xf:repeat/@id=$group-name]">
                                    <xsl:variable name="previous-group-pages-number"
                                        select="count(fr:section)"/>
                                    <xsl:variable name="previous-group-name" select="@id"/>
                                    <xsl:value-of
                                        select="concat('+(count(instance(''fr-form-instance'')/',$previous-group-name,')*',$previous-group-pages-number,'-1)')"
                                    />
                                </xsl:for-each>
                            </xsl:variable>
                            <xsl:variable name="group-pages-number" select="count(fr:section)"/>

                            <xsl:value-of
                                select="concat(' else (if (number(instance(''fr-form-instance'')/Util/CurrentSection) &lt;',$occurrence-position,')')"/>
                            <xsl:value-of
                                select="concat(' then round((number(instance(''fr-form-instance'')/Util/CurrentSection)-2',$previous-group-pages-count,')')"/>
                            <xsl:value-of select="concat(' div ',$denominator,'*100)')"/>
                            <xsl:value-of
                                select="concat(' else (if (instance(''fr-form-instance'')/Util/CurrentSection =',$occurrence-position,')')"/>
                            <!-- pages due to sections previous from the repeat + pages due to previous occurrences + pages due to previous sections in the current occurrence -->
                            <xsl:value-of
                                select="concat(' then round((',$occurrence-position,'-2',$previous-group-pages-count)"/>
                            <xsl:value-of
                                select="concat('+(number(instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',$group-name,'''])-1)*',$group-pages-number)"/>
                            <xsl:value-of
                                select="'+count(instance(''fr-form-instance'')/*[name()=instance(''fr-form-instance'')/Util/CurrentSectionName]/preceding-sibling::*))'"/>
                            <xsl:value-of select="concat(' div ',$denominator,'*100)')"/>
                        </xsl:for-each>
                        <xsl:value-of
                            select="' else round((number(instance(''fr-form-instance'')/Util/CurrentSection)-2',$total-group-pages-count,')'"/>
                        <xsl:value-of select="concat(' div ',$denominator,'*100))')"/>
                        <!-- if ends -->
                        <xsl:for-each select="//fr:body/xf:repeat">
                            <xsl:value-of select="'))'"/>
                        </xsl:for-each>
                    </xsl:attribute>
                </xf:bind>
                <xf:bind id="progress-bind" ref="Progress"/>
                <xf:bind id="page-top-bind" ref="PageTop"/>
                <xf:bind id="confirmation-message-bind" ref="ConfirmationMessage"
                    name="confirmation-message"
                    relevant="instance('fr-form-instance')/Util/Send='true'"/>
                <xf:bind id="pages-bind" ref="Pages">
                    <xsl:apply-templates
                        select="//xf:instance[@id='fr-form-instance']/form/*[child::*]"
                        mode="page-bind"/>
                </xf:bind>
            </xf:bind>

            <!--  Saving : be careful on the parameters order : formulaire must stand before unite-enquete -->
            <xsl:comment>You can add a resource attribute for the save submission by processing the result with your own xslt.</xsl:comment>
            <xf:submission id="save" method="post" ref="instance('fr-form-instance')" replace="none"
                relevant="false">
                <xf:action ev:event="xforms-submit-error">
                    <xxf:show ev:event="DOMActivate" dialog="fatal-error"/>
                </xf:action>
                <xf:action ev:event="xforms-submit-done">
                    <xf:setvalue ref="xxf:instance('fr-persistence-instance')/data-safe-override"
                        >true</xf:setvalue>
                </xf:action>
            </xf:submission>

            <!--  Submitting. -->
            <!--  Hint / Instruction generation : the model must be configured, rest is static. -->
            <xsl:comment>You can add a resource attribute for the submit submission by processing the result with your own xslt.</xsl:comment>
            <xf:submission id="submit" method="post" ref="instance('fr-form-instance')"
                replace="none" relevant="false">
                <!-- If somehow it crashes, we register the survey as not submitted and the page marker is brought down-->
                <xf:action ev:event="xforms-submit-error">
                    <xf:setvalue ref="instance('fr-form-instance')/Util/Send"
                        value="string('false')"/>
                    <xxf:show ev:event="DOMActivate" dialog="fatal-error"/>
                </xf:action>
                <xf:action ev:event="xforms-submit-done">
                    <!-- This helps to avoid native Orbeon alert messages when wanting to leave the survey even thought the datas are saved. -->
                    <xf:setvalue ref="xxf:instance('fr-persistence-instance')/data-safe-override"
                        >true</xf:setvalue>
                </xf:action>
            </xf:submission>
            <!-- Initialization action -->
            <!-- Initialization of all variable text fields -->
            <xf:action ev:event="xforms-ready">
                <!-- Going back to the page we left -->
                <xf:toggle case="{$choice}"/>
                <!-- If this isn't submitted yet, and we're not on the first page -->
                <xxf:show
                    if="instance('fr-form-instance')/Util/Send='false' and not(instance('fr-form-instance')/Util/CurrentSection='1')"
                    dialog="welcome-back"/>
            </xf:action>

            <!-- Page changing action -->
            <xf:action ev:event="page-change">

                <!-- Iterating on every field of the current page and doing a DOMFocusOut in order to display potential error messages -->
                <xf:action
                    iterate="instance('fr-form-instance')/*[name()=instance('fr-form-instance')/Util/CurrentSectionName]//*">
                    <xf:dispatch name="DOMFocusOut">
                        <xsl:attribute name="target">
                            <xsl:value-of select="'{concat(context()/name(),''-control'')}'"/>
                        </xsl:attribute>
                    </xf:dispatch>
                </xf:action>
                <!-- The same for loops of pages -->
                <xsl:for-each select="//fr:body/xf:repeat">
                    <xsl:variable name="section-position">
                        <xsl:value-of select="count(preceding-sibling::*)+1"/>
                    </xsl:variable>
                    <xsl:variable name="loop-name" select="@id"/>
                    <xf:action>
                        <xsl:attribute name="if">
                            <xsl:value-of
                                select="concat('instance(''fr-form-instance'')/Util/CurrentSection=''',$section-position,'''')"
                            />
                        </xsl:attribute>
                        <xf:action>
                            <xsl:attribute name="iterate"
                                select="concat('instance(''fr-form-instance'')/',$loop-name,
                                '[count(preceding-sibling::',$loop-name,
                                ')+1=number(instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',$loop-name,
                                '''])]/*[name()=instance(''fr-form-instance'')/Util/CurrentSectionName]//*')"/>
                            <xf:dispatch name="DOMFocusOut">
                                <xsl:attribute name="target">
                                    <xsl:value-of select="'{concat(context()/name(),''-control'')}'"
                                    />
                                </xsl:attribute>
                            </xf:dispatch>
                        </xf:action>
                    </xf:action>
                </xsl:for-each>


                <!-- Forcing this to false to notify that the page change isn't done yet. -->
                <xf:setvalue ref="instance('fr-form-util')/PageChangeDone" value="string('false')"/>


                <!-- Every action below is exclusive (works as a switch case)
                        The result of each action can initiate another one, but we don't want that.
                        Therefore, an action will only occur when the PageChangeDone property will be false.
                        Also, each action will end by setting this property to true (which will prevent other actions from triggering-->
                <xf:action
                    if="instance('fr-form-util')/PageChangeDone='false'
                    and not(xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/Util/CurrentSectionName],true(),true()))">
                    <!-- Displaying the dialog window that correspond to an error -->
                    <xxf:show ev:event="DOMActivate" dialog="error"/>
                    <!-- And we don't change page -->
                    <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                        value="string('true')"/>
                </xf:action>
                <!-- The same for loops of pages -->
                <xsl:for-each select="//fr:body/xf:repeat">
                    <xsl:variable name="section-position">
                        <xsl:value-of select="count(preceding-sibling::*)+1"/>
                    </xsl:variable>
                    <xsl:variable name="loop-name" select="@id"/>
                    <xf:action>
                        <xsl:attribute name="if">
                            <xsl:value-of
                                select="concat('instance(''fr-form-instance'')/Util/CurrentSection=''',$section-position,'''')"
                            />
                        </xsl:attribute>
                        <xf:action>
                            <xsl:attribute name="if"
                                select="concat('instance(''fr-form-util'')/PageChangeDone=''false''
                                and not(xxf:valid(instance(''fr-form-instance'')/',$loop-name,
                                '[count(preceding-sibling::',$loop-name,
                                ')+1=number(instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',$loop-name,
                                '''])]/*[name()=instance(''fr-form-instance'')/Util/CurrentSectionName],true(),true()))')"/>
                            <xxf:show ev:event="DOMActivate" dialog="error"/>
                            <!-- And we don't change page -->
                            <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                                value="string('true')"/>
                        </xf:action>
                    </xf:action>
                </xsl:for-each>

                <xf:action
                    if="instance('fr-form-util')/PageChangeDone='false'
                    and xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/Util/CurrentSectionName],true(),true())
                    and xxf:evaluate-bind-property(concat('page-',instance('fr-form-instance')/Util/CurrentSectionName,'-bind'),'constraint')=false()">
                    <!-- Displaying the dialog window that correspond to an error -->
                    <xxf:show ev:event="DOMActivate" dialog="warning"/>
                    <!-- And we don't change page. The page change can happen at the level of this dialog window -->
                    <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                        value="string('true')"/>
                </xf:action>
                <!-- The same for loops of pages -->
                <xsl:for-each select="//fr:body/xf:repeat">
                    <xsl:variable name="section-position">
                        <xsl:value-of select="count(preceding-sibling::*)+1"/>
                    </xsl:variable>
                    <xsl:variable name="loop-name" select="@id"/>
                    <xf:action>
                        <xsl:attribute name="if">
                            <xsl:value-of
                                select="concat('instance(''fr-form-instance'')/Util/CurrentSection=''',$section-position,'''')"
                            />
                        </xsl:attribute>
                        <xf:action>
                            <xsl:attribute name="if"
                                select="concat('instance(''fr-form-util'')/PageChangeDone=''false''
                                and xxf:valid(instance(''fr-form-instance'')/',$loop-name,
                                '[count(preceding-sibling::',$loop-name,
                                ')+1=number(instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',$loop-name,
                                '''])]/*[name()=instance(''fr-form-instance'')/Util/CurrentSectionName],true(),true())
                                and xxf:evaluate-bind-property(concat(''page-'',instance(''fr-form-instance'')/Util/CurrentSectionName,''-bind''),''constraint'')=false()')"/>
                            <xxf:show ev:event="DOMActivate" dialog="warning"/>
                            <!-- And we don't change page -->
                            <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                                value="string('true')"/>
                        </xf:action>
                    </xf:action>
                </xsl:for-each>

                <xf:action
                    if="instance('fr-form-util')/PageChangeDone='false'
                    and xxf:valid(instance('fr-form-instance')/*[name()=instance('fr-form-instance')/Util/CurrentSectionName],true(),true())
                    and not(xxf:evaluate-bind-property(concat('page-',instance('fr-form-instance')/Util/CurrentSectionName,'-bind'),'constraint')=false())">
                    <!-- The page change happens -->
                    <xf:dispatch name="page-change-done" targetid="fr-form-model"/>
                </xf:action>
                <!-- The same for loops of pages -->
                <xsl:for-each select="//fr:body/xf:repeat">
                    <xsl:variable name="section-position">
                        <xsl:value-of select="count(preceding-sibling::*)+1"/>
                    </xsl:variable>
                    <xsl:variable name="loop-name" select="@id"/>
                    <xf:action>
                        <xsl:attribute name="if">
                            <xsl:value-of
                                select="concat('instance(''fr-form-instance'')/Util/CurrentSection=''',$section-position,'''')"
                            />
                        </xsl:attribute>
                        <xf:action>
                            <xsl:attribute name="if"
                                select="concat('instance(''fr-form-util'')/PageChangeDone=''false''
                                and xxf:valid(instance(''fr-form-instance'')/',$loop-name,
                                '[count(preceding-sibling::',$loop-name,
                                ')+1=number(instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',$loop-name,
                                '''])]/*[name()=instance(''fr-form-instance'')/Util/CurrentSectionName],true(),true())
                                and not(xxf:evaluate-bind-property(concat(''page-'',instance(''fr-form-instance'')/Util/CurrentSectionName,''-bind''),''constraint'')=false())')"/>
                            <xf:dispatch name="page-change-done" targetid="fr-form-model"/>
                        </xf:action>
                    </xf:action>
                </xsl:for-each>

            </xf:action>

            <!-- What happens when the page change is effective -->
            <xf:action ev:event="page-change-done">
                <xf:setvalue ref="instance('fr-form-util')/PageChangeDone" value="string('false')"/>

                <!-- For each CurrentLoopElement, we calculate the corresponding value -->
                <xsl:apply-templates select="//fr:body/xf:repeat" mode="page-change"/>

                <xf:action
                    if="instance('fr-form-util')/PageChangeDone='false' and instance('fr-form-util')/PreviousNext='1'">
                    <!-- Not handled : loop whose all elements are hidden -->
                    <xf:setvalue ref="instance('fr-form-instance')/Util/CurrentSection"
                        value="{string('count(instance(''fr-form-util'')/Pages/*[position()=instance(''fr-form-instance'')/Util/CurrentSection]/following-sibling::*[not(text()=''false'')][1]/preceding-sibling::*)+1')}"/>
                    <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                        value="string('true')"/>
                </xf:action>
                <xf:action
                    if="instance('fr-form-util')/PageChangeDone='false' and instance('fr-form-util')/PreviousNext='-1'">
                    <!-- Non handled : loop whose all elements are hidden -->
                    <xf:setvalue ref="instance('fr-form-instance')/Util/CurrentSection"
                        value="{string('count(instance(''fr-form-util'')/Pages/*[position()=instance(''fr-form-instance'')/Util/CurrentSection]/preceding-sibling::*[not(text()=''false'')][1]/preceding-sibling::*)+1')}"/>
                    <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                        value="string('true')"/>
                </xf:action>

                <!-- Saving the time when the saving happened -->
                <xf:setvalue ref="instance('fr-form-instance')/Util/DateTime"
                    value="fn:format-dateTime(fn:current-dateTime(),'[D01]-[M01]-[Y0001] à [H01]:[m01]')"/>

                <!-- Switching page -->
                <xf:toggle case="{$choice}"/>
                <!-- Saving -->
                <xf:send submission="save"/>
                <!-- Going at the top of the page -->
                <xf:setfocus control="page-top-control"/>
            </xf:action>

            <!-- What happens when the form is submitted -->
            <xf:action ev:event="submit-form">
                <!-- Writing in the instance that the survey is submitted -->
                <xf:setvalue ref="instance('fr-form-instance')/Util/Send"
                    value="string('true')"/>
                <xf:setvalue ref="instance('fr-form-instance')/Util/DateTime"
                    value="fn:format-dateTime(fn:current-dateTime(),'[D01]-[M01]-[Y0001] à [H01]:[m01]')"/>
                <!-- Dispatching the submission -->
                <xf:send submission="submit"/>
            </xf:action>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*[not(ends-with(name(),'-Loop'))]" mode="page-bind">
        <xf:bind id="{concat('page-',name(),'-bind')}" name="{name()}" ref="{name()}">
            <xf:calculate
                value="{concat('xxf:evaluate-bind-property(''',concat(name(),'-bind'),''',''relevant'')')}"/>
            <!-- Creating a constraint equals to the sum of warning-level constraints -->
            <xsl:variable name="module-name" select="name()"/>
            <xsl:variable name="constraint">
                <xsl:value-of select="'('"/>
                <xsl:for-each
                    select="//xf:bind[@name=$module-name]//xf:constraint[@level='warning']">
                    <xsl:if test="not(position()=1)">
                        <xsl:text>) and (</xsl:text>
                    </xsl:if>
                    <xsl:value-of
                        select="replace(replace(@value,'//','instance(''fr-form-instance'')//'),
                        '\]instance\(''fr-form-instance''\)',']')"
                    />
                </xsl:for-each>
                <xsl:value-of select="')'"/>
            </xsl:variable>
            <xsl:if test="$constraint[not(text()='()')]">
                <xf:constraint value="{$constraint}"/>
            </xsl:if>
        </xf:bind>
    </xsl:template>

    <xsl:template match="*[ends-with(name(),'-Loop')]" mode="page-bind">
        <xf:bind id="{concat('page-',name(),'-bind')}" name="{name()}" nodeset="{name()}">
            <xsl:apply-templates select="child::*[child::*]" mode="page-bind"/>
        </xf:bind>
    </xsl:template>

    <xsl:template match="xf:repeat" mode="page-change">
        <xsl:variable name="module-position">
            <xsl:value-of select="count(preceding-sibling::*)+1"/>
        </xsl:variable>
        <!-- if we're on the group and next button was clicked then : if all modules are hidden then 0 else next minimum value -->
        <xf:action
            if="{concat('instance(''fr-form-util'')/PreviousNext=''1''
            and number(instance(''fr-form-instance'')/Util/CurrentSection) = ',$module-position)}">
            <xf:setvalue
                ref="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,''']')}"
                value="{concat('string(if (count(instance(''fr-form-instance'')/',@id,'[(count(preceding-sibling::*[name()=''',@id,'''])+1) 
                &gt; instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] and not(text()=''false'')]) &gt; 0)
                then (count(instance(''fr-form-instance'')/',@id,'[(count(preceding-sibling::*[name()=''',@id,'''])+1) 
                &gt; instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] and not(text()=''false'')][1]/preceding-sibling::*)+1)
                else 0)')}"/>
            <xf:action
                if="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] &gt; 0')}">
                <xf:setvalue ref="instance('fr-form-util')/PageChangeDone" value="string('true')"/>
            </xf:action>
        </xf:action>
        <!-- if we're on the group and previous button was clicked then : if all modules are hidden then 0 else next maximum value -->
        <xf:action
            if="{concat('instance(''fr-form-util'')/PreviousNext=''-1''
            and number(instance(''fr-form-instance'')/Util/CurrentSection) = ',$module-position)}">
            <xf:setvalue
                ref="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,''']')}"
                value="{concat('string(if (count(instance(''fr-form-instance'')/',@id,'[(count(preceding-sibling::*[name()=''',@id,'''])+1) 
                &lt; instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] and not(text()=''false'')]) &gt; 0)
                then (count(instance(''fr-form-instance'')/',@id,'[(count(preceding-sibling::*[name()=''',@id,'''])+1)
                &lt; instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] and not(text()=''false'')][last()]/preceding-sibling::*)+1)
                else 0)')}"/>
            <xf:action
                if="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] &gt; 0')}">
                <xf:setvalue ref="instance('fr-form-util')/PageChangeDone" value="string('true')"/>
            </xf:action>
        </xf:action>
        <!-- if we're on a previous group and next button was clicked then : if all modules are hidden then 0 else minimum value -->
        <xf:action
            if="{concat('instance(''fr-form-util'')/PreviousNext=''1''
            and number(instance(''fr-form-instance'')/Util/CurrentSection) &lt; ',$module-position)}">
            <xf:setvalue
                ref="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,''']')}"
                value="{concat('string(if (count(instance(''fr-form-instance'')/',@id,'[not(text()=''false'')]) &gt; 0)
                then (count(instance(''fr-form-instance'')/',@id,'[not(text()=''false'')][1]/preceding-sibling::*[name()=''',@id,'''])+1)
                else 0)')}"
            />
        </xf:action>
        <!-- if we're on a next group and previous button was clicked then : if all modules are hidden then 0 else maximum value -->
        <xf:action
            if="{concat('instance(''fr-form-util'')/PreviousNext=''-1''
            and number(instance(''fr-form-instance'')/Util/CurrentSection) &gt; ',$module-position)}">
            <xf:setvalue
                ref="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,''']')}"
                value="{concat('string(if (count(instance(''fr-form-instance'')/',@id,'[not(text()=''false'')]) &gt; 0)
                then (count(instance(''fr-form-instance'')/',@id,'[not(text()=''false'')][last()]/preceding-sibling::*[name()=''',@id,'''])+1)
                else 0)')}"
            />
        </xf:action>

        <!--        <xf:action
            if="{concat('instance(''fr-form-util'')/PreviousNext=''1''
            and number(instance(''fr-form-instance'')/Util/CurrentSection) = ',$module-position)}">
            <xf:setvalue
                ref="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,''']')}"
                value="{concat('string(if (count(instance(''fr-form-instance'')/',@id,'/*[position() 
                &gt; instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] and not(text()=''false'')]) &gt; 0)
                then (count(instance(''fr-form-instance'')/',@id,'/*[position() 
                &gt; instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] and not(text()=''false'')][1]/preceding-sibling::*)+1)
                else 0)')}"/>
            <xf:action
                if="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] &gt; 0')}">
                <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                    value="string('true')"/>
            </xf:action>
        </xf:action>
        <xf:action
            if="{concat('instance(''fr-form-util'')/PreviousNext=''-1''
            and number(instance(''fr-form-instance'')/Util/CurrentSection) = ',$module-position)}">
            <xf:setvalue
                ref="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,''']')}"
                value="{concat('string(if (count(instance(''fr-form-instance'')/',@id,'/*[position() 
                &lt; instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] and not(text()=''false'')]) &gt; 0)
                then (count(instance(''fr-form-instance'')/',@id,'/*[position() 
                &lt; instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] and not(text()=''false'')][last()]/preceding-sibling::*)+1)
                else 0)')}"/>
            <xf:action
                if="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,'''] &gt; 0')}">
                <xf:setvalue ref="instance('fr-form-util')/PageChangeDone"
                    value="string('true')"/>
            </xf:action>
        </xf:action>
        <xf:action
            if="{concat('instance(''fr-form-util'')/PreviousNext=''1''
            and number(instance(''fr-form-instance'')/Util/CurrentSection) &lt; ',$module-position)}">
            <xf:setvalue
                ref="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,''']')}"
                value="{concat('string(if (count(instance(''fr-form-instance'')/',@id,'/*[not(text()=''false'')]) &gt; 0)
                then (count(instance(''fr-form-instance'')/',@id,'/*[not(text()=''false'')][1]/preceding-sibling::*)+1)
                else 0)')}"
            />
        </xf:action>
        <xf:action
            if="{concat('instance(''fr-form-util'')/PreviousNext=''-1''
            and number(instance(''fr-form-instance'')/Util/CurrentSection) &gt; ',$module-position)}">
            <xf:setvalue
                ref="{concat('instance(''fr-form-instance'')/Util/CurrentLoopElement[@loop-name=''',@id,''']')}"
                value="{concat('string(if (count(instance(''fr-form-instance'')/',@id,'/*[not(text()=''false'')]) &gt; 0)
                then (count(instance(''fr-form-instance'')/',@id,'/*[not(text()=''false'')][last()]/preceding-sibling::*)+1)
                else 0)')}"
            />
        </xf:action>
-->
    </xsl:template>

    <!-- Adding buttons at the end of the survey-->
    <xsl:template match="fr:view">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <fr:buttons>
                <xf:trigger id="start" bind="start-bind">
                    <xf:label ref="$form-resources/Start/label"/>
                    <xf:action ev:event="DOMActivate">
                        <xf:setvalue ref="instance('fr-form-util')/PreviousNext" value="1"/>
                        <xf:dispatch name="page-change" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
                <xf:trigger id="previous" bind="previous-bind">
                    <xf:label ref="$form-resources/Previous/label"/>
                    <xf:action ev:event="DOMActivate">
                        <xf:setvalue ref="instance('fr-form-util')/PreviousNext" value="-1"/>
                        <xf:dispatch name="page-change" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
                <xf:trigger id="next" bind="next-bind">
                    <xf:label ref="$form-resources/Next/label"/>
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
                <!-- This element measures the survey's progress -->
                <xhtml:span class="right">
                    <xf:output id="progress-control" bind="progress-bind">
                        <xf:label ref="$form-resources/Progress/label">
                            <xsl:attribute name="mediatype">text/html</xsl:attribute>
                        </xf:label>
                    </xf:output>
                    <xhtml:progress id="progress" max="100">
                        <xsl:attribute name="value">
                            <xsl:value-of select="'{instance(''fr-form-util'')/ProgressPercent}'"/>
                        </xsl:attribute>
                    </xhtml:progress>
                    <xf:output id="progress-percent" ref="instance('fr-form-util')/ProgressPercent"
                    /> %</xhtml:span>
            </xhtml:div>

            <!-- Using a switch in order to display each module on the same page -->
            <xsl:apply-templates select="*[not(name()='fr:section') and not(name()='xf:repeat')]"/>
            <xf:switch id="section-body">
                <xf:case id="1">
                    <fr:section id="beginning-control" bind="beginning-bind" name="beginning">
                        <xf:label ref="$form-resources/Beginning/label"/>
                        <xhtml:div class="center">
                            <xhtml:p>Generic beginning page, can be overridden or removed by
                                processing your own xslt on the result.</xhtml:p>
                        </xhtml:div>
                    </fr:section>
                </xf:case>
                <xsl:apply-templates select="*[name()='fr:section' or name()='xf:repeat']"/>
                <xf:case id="{string(number($number-of-modules)+2)}">
                    <fr:section id="end-control" bind="end-bind" name="end">
                        <xf:label ref="$form-resources/End/label"/>
                        <xhtml:div class="center">
                            <xhtml:p>Generic end page, can be overridden or removed by processing
                                your own xslt on the result.</xhtml:p>
                            <xf:trigger id="send" bind="send-bind">
                                <xf:label ref="$form-resources/Send/label"/>
                                <xf:action ev:event="DOMActivate">
                                    <xf:dispatch name="submit-form" targetid="fr-form-model"/>
                                </xf:action>
                            </xf:trigger>
                            <xf:output id="confirmation-message" bind="confirmation-message-bind">
                                <xf:label
                                    ref="concat($form-resources/ConfirmationMessage/label,' ',instance('fr-form-instance')/Util/DateTime)"
                                />
                            </xf:output>
                        </xhtml:div>
                    </fr:section>
                </xf:case>
            </xf:switch>
            <xxf:dialog id="error" draggable="false" close="false">
                <xf:label ref="$form-resources/Error/label"/>
                <xf:output ref="instance('fr-form-util')/ErrorText">
                    <xf:label ref="$form-resources/ErrorText/label" mediatype="text/html"/>
                </xf:output>
                <xf:trigger id="correct-error">
                    <xf:label ref="$form-resources/Correct/label"/>
                    <xxf:hide ev:event="DOMActivate" dialog="error"/>
                </xf:trigger>
            </xxf:dialog>
            <xxf:dialog id="warning" close="false" draggable="false">
                <xf:label ref="$form-resources/Warning/label"/>
                <xf:output ref="instance('fr-form-util')/WarningText">
                    <xf:label ref="$form-resources/WarningText/label" mediatype="text/html"/>
                </xf:output>
                <xf:trigger id="correct-warning">
                    <xf:label ref="$form-resources/Correct/label"/>
                    <xxf:hide ev:event="DOMActivate" dialog="warning"/>
                </xf:trigger>
                <xf:trigger id="continue">
                    <xf:label ref="$form-resources/Continue/label"/>
                    <xxf:hide ev:event="DOMActivate" dialog="warning"/>
                    <xf:action ev:event="DOMActivate">
                        <xf:dispatch name="page-change-done" targetid="fr-form-model"/>
                    </xf:action>
                </xf:trigger>
            </xxf:dialog>
            <xxf:dialog id="welcome-back" close="false" draggable="false">
                <xf:label ref="$form-resources/WelcomeBack/label"/>
                <xf:output ref="instance('fr-form-util')/WelcomeBackText">
                    <xf:label ref="$form-resources/WelcomeBackText/label" mediatype="text/html"/>
                </xf:output>
                <xf:trigger id="go-back">
                    <xf:label ref="$form-resources/GoBack/label"/>
                    <xxf:hide ev:event="DOMActivate" dialog="welcome-back"/>
                </xf:trigger>
                <xf:trigger id="go-to-first-page">
                    <xf:label ref="$form-resources/GoToFirstPage/label"/>
                    <xf:action ev:event="DOMActivate">
                        <xxf:hide dialog="welcome-back"/>
                        <!-- Always going back to the first page except if the survey is submitted -->
                        <xf:setvalue ref="instance('fr-form-instance')/Util/CurrentSection"
                            value="'1'"/>
                        <xf:toggle case="{$choice}"/>
                    </xf:action>
                </xf:trigger>
            </xxf:dialog>
            <xxf:dialog id="fatal-error" close="true" draggable="false">
                <xf:label ref="$form-resources/Error/label"/>
                <xf:output ref="instance('fr-form-util')/FatalError">
                    <xf:label ref="$form-resources/FatalError/label"/>
                </xf:output>
            </xxf:dialog>
        </xsl:copy>
    </xsl:template>

    <!-- Wrapping the existing modules in a xf:case -->
    <xsl:template match="fr:section[parent::fr:body] | xf:repeat[parent::fr:body]">
        <xsl:variable name="index"
            select="number($number-of-modules)-count(following-sibling::fr:section)-count(following-sibling::xf:repeat)+1"/>
        <xf:case id="{$index}">
            <xsl:copy>
                <xsl:apply-templates select="node() | @*"/>
            </xsl:copy>
        </xf:case>
    </xsl:template>

</xsl:stylesheet>
