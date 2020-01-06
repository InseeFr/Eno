<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
    exclude-result-prefixes="xd eno enofr" version="2.0">

<!--    <xsl:import href="../../transformations/ddi2fr/ddi2fr.xsl"/>-->

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>An xslt stylesheet who transforms an input into Xforms (Orbeon Form-Runner) through generic driver templates.</xd:p>
            <xd:p>The real input is mapped with the drivers.</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>The highest driver, which starts the generation of the xforms.</xd:p>
            <xd:p>It writes codes on different levels for a same driver by adding an element to the virtuel tree :</xd:p>
            <xd:p>- Instance : to write the main instance</xd:p>
            <xd:p>- Bind : to writes the binds associated to the elements of the instance</xd:p>
            <xd:p>- Resource : an instance which stores the externalized texts used in the body part (xforms labels, hints, helps, alerts)</xd:p>
            <xd:p>- Body : to write the fields</xd:p>
            <xd:p>- Model : to write model elements of the instance which could be potentially added by the user in the instance</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Form" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="languages" select="enofr:get-form-languages($source-context)" as="xs:string +"/>
        <xhtml:html>
            <xhtml:head>
                <xhtml:title>
                    <xsl:value-of select="enofr:get-form-title($source-context, $languages[1])"/>
                </xhtml:title>
                <xsl:choose>
                    <xsl:when test="$parameters//StudyUnit='business'">
                        <xsl:for-each select="$properties//Css/CommonBusinness">
                            <xhtml:link rel="stylesheet" href="/{$properties//Css/Folder}/{.}"/>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:for-each select="$properties//Css/Common">
                            <xhtml:link rel="stylesheet" href="/{$properties//Css/Folder}/{.}"/>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>                
                <xsl:for-each select="$parameters//Css">
                    <xsl:if test=".!=''">
                        <xhtml:link rel="stylesheet" href="/{$properties//Css/Folder}/{.}"/>
                    </xsl:if>
                </xsl:for-each>
                <xf:model id="fr-form-model" xxf:expose-xpath-types="true" xxf:noscript-support="true">

                    <!-- Main instance, it contains the elements linked to fields, and which will be stored when the form will be submitted -->
                    <xf:instance id="fr-form-instance">
                        <form modele="{enofr:get-form-model($source-context)}">
                            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                                <xsl:with-param name="driver" select="eno:append-empty-element('Instance', .)" tunnel="yes"/>
                            </xsl:apply-templates>
                        </form>
                    </xf:instance>

                    <xf:instance id="fr-form-loop-model">
                        <LoopModels>
                            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                                <xsl:with-param name="driver" select="eno:append-empty-element('Model', .)" tunnel="yes"/>
                            </xsl:apply-templates>
                        </LoopModels>
                    </xf:instance>

                    <!-- Bindings -->
                    <xf:bind id="fr-form-instance-binds" ref="instance('fr-form-instance')">
                        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                            <xsl:with-param name="driver" select="eno:append-empty-element('Bind', .)" tunnel="yes"/>
                            <!-- the instance ancestor is used for having the absolute, not relative, address of the element -->
                            <xsl:with-param name="instance-ancestor" select="''" tunnel="yes"/>
                        </xsl:apply-templates>
                    </xf:bind>

                    <!-- Metadata -->
                    <xf:instance id="fr-form-metadata" xxf:readonly="true">
                        <metadata>
                            <application-name>
                                <xsl:value-of select="enofr:get-application-name($source-context)"/>
                            </application-name>
                            <form-name>
                                <xsl:value-of select="enofr:get-form-name($source-context)"/>
                            </form-name>
                            <xsl:for-each select="$languages">
                                <title xml:lang="{.}">
                                    <xsl:value-of select="enofr:get-form-title($source-context, .)"/>
                                </title>
                            </xsl:for-each>
                        </metadata>
                    </xf:instance>

                    <!-- Attachments -->
                    <xf:instance id="fr-form-attachments">
                        <attachments>
                            <css mediatype="text/css" filename="" size=""/>
                            <pdf mediatype="application/pdf" filename="" size=""/>
                        </attachments>
                    </xf:instance>

                    <!-- All form resources -->
                    <!-- Don't make readonly by default in case a service modifies the resources -->
                    <xf:instance id="fr-form-resources">
                        <resources>
                            <xsl:variable name="driver" select="."/>
                            <xsl:for-each select="$languages">
                                <xsl:variable name="language" select="."/>
                                <resource xml:lang="{.}">
                                    <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                                        <xsl:with-param name="driver" select="eno:append-empty-element('Resource', $driver)" tunnel="yes"/>
                                        <xsl:with-param name="language" select="$language" tunnel="yes"/>
                                    </xsl:apply-templates>
                                    <AddLine>
                                        <label><xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/AddLine"/></label>
                                    </AddLine>
                                </resource>
                            </xsl:for-each>
                        </resources>
                    </xf:instance>

                    <!-- Utility instances for services -->
                    <xf:instance id="fr-service-request-instance" xxf:exclude-result-prefixes="#all">
                        <request/>
                    </xf:instance>

                    <xf:instance id="fr-service-response-instance" xxf:exclude-result-prefixes="#all">
                        <response/>
                    </xf:instance>

                </xf:model>
            </xhtml:head>
            <xhtml:body>
                <fr:view>
                    <!-- Writing the main body -->
                    <fr:body>
                        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                            <xsl:with-param name="driver" select="eno:append-empty-element('Body', .)" tunnel="yes"/>
                            <xsl:with-param name="languages" select="$languages" tunnel="yes"/>
                            <!-- the instance ancestor is used for having the absolute, not relative, address of the element -->
                            <xsl:with-param name="instance-ancestor" select="''" tunnel="yes"/>
                        </xsl:apply-templates>
                    </fr:body>
                </fr:view>
            </xhtml:body>
        </xhtml:html>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>GoTo are to be removed.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="GoTo" mode="model"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for Instance for the drivers.</xd:p>
            <xd:p>The element is created and we continue to parse the input tree next to the created element.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Instance//*" mode="model" priority="-1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Instance for those drivers.</xd:p>
            <xd:p>The element is created and we continue to parse the input tree next within the created element.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Instance//*[name() = ('xf-group', 'Module','Clarification')]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Instance and Bind for SingleResponseQuestion.</xd:p>
            <xd:p>The element is invisible and absorbed in its Response.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="SingleResponseQuestion[ancestor::Instance or ancestor::Bind]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Resource and ResourceBind for SingleResponseQuestion.</xd:p>
            <xd:p>The element is invisible, but sends its label to its Response.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="SingleResponseQuestion[ancestor::Resource or ancestor::ResourceBind]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>

        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
            <xsl:with-param name="question-label" select="eno:serialize(enofr:get-label($source-context,$language))" tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Special template for Instance for the RowLoop driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Instance//RowLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:element name="{enofr:get-container-name($source-context)}">
            <xsl:element name="{$name}">
                <xsl:attribute name="id" select="concat($name,'-1')"/>
                <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                    <xsl:with-param name="driver" select="." tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:element>
        <xsl:element name="{$name}-Count">
            <xsl:value-of select="enofr:get-minimum-lines($source-context)"/>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Instance for those drivers.</xd:p>
            <xd:p>The element is created and we continue to parse the input tree next within the created element.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Instance//QuestionLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:element name="{enofr:get-container-name($source-context)}">
            <xsl:element name="{$name}">
                <xsl:attribute name="id" select="concat($name,'-1')"/>
                <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                    <xsl:with-param name="driver" select="." tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Special template for Instance for the TableLoop driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Instance//TableLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>

        <xsl:element name="{$name}"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
        <xsl:if test="not(enofr:get-maximum-lines($source-context)!='') or (number(enofr:get-minimum-lines($source-context)) &lt; number(enofr:get-maximum-lines($source-context)))">
            <xsl:element name="{enofr:get-business-name($source-context)}-AddLine"/>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Special template for Instance for the DurationDomain and DateTimeDomain drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Instance//DurationDomain | Instance//DateTimeDomain" mode="model" >
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="current-driver" select="self::*/local-name()"/>

        <xsl:variable name="layout-list" as="node()">
            <xsl:call-template name="dateduration-layout">
                <xsl:with-param name="variable-name" select="$name"/>
                <xsl:with-param name="driver" select="$current-driver"/>
                <xsl:with-param name="format" select="upper-case(enofr:get-format($source-context))"/>
                <xsl:with-param name="minimum" select="enofr:get-minimum($source-context)"/>
                <xsl:with-param name="maximum" select="enofr:get-maximum($source-context)"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:element name="{$name}"/>
        <xsl:if test="count($layout-list//format) &gt; 1 or $current-driver = 'DurationDomain'">
            <xsl:for-each select="$layout-list//format">
                <xsl:element name="{$name}-layout-{@id}"/>
            </xsl:for-each>
            <xsl:element name="{$name}-dateduration-constraint"/>
        </xsl:if>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for Model for the drivers : doing nothing and continuing parsing.</xd:p>
            <xd:p>Only if the Instance element wasn't added to the virtual tree (to write the instance part into the model).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Model//*[not(ancestor::Instance)]" mode="model" priority="-1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for RowLoop and QuestionLoop.</xd:p>
            <xd:p>An element is created and we copy the Instance part into this model.</xd:p>
            <xd:p>It goes down the tree to check if there are no other loops.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Model//RowLoop | Model//QuestionLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <!-- create element with same name and acts like what is done for the instance part -->
        <xsl:element name="{enofr:get-container-name($source-context)}">
            <xsl:element name="{enofr:get-name($source-context)}">
                <xsl:attribute name="id"/>
                <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                    <xsl:with-param name="driver" select="eno:append-empty-element('Instance', .)" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:element>
        <!-- keep going down the tree in case there are other loops -->
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for Bind for the drivers.</xd:p>
            <xd:p>It builds the bind by using different enofr functions then the process goes on next to the created bind.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Bind//*" mode="model" priority="-1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="required" select="enofr:is-required($source-context)" as="xs:boolean"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="variable-calculate" select="enofr:get-variable-calculation($source-context)"/>
        <xsl:variable name="type" select="enofr:get-type($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>
        <xsl:variable name="constraint" select="enofr:get-constraint($source-context)"/>
        <xsl:variable name="format-constraint" select="enofr:get-format-constraint($source-context)"/>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="$required">
                <xsl:attribute name="required" select="'true()'"/>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="normalize-space($relevant)"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$variable-calculate != ''">
                <xsl:attribute name="calculate">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="normalize-space($variable-calculate)"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-variable-calculation-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="normalize-space($readonly)"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                    <xsl:value-of select="')'"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$constraint != ''">
                <xsl:element name="xf:constraint">
                    <xsl:variable name="alert-level" select="enofr:get-alert-level($source-context)"/>
                    <xsl:if test="$alert-level != ''">
                        <xsl:attribute name="level" select="$alert-level"/>
                    </xsl:if>
                    <xsl:attribute name="value">
                        <xsl:if test="self::ConsistencyCheck and enofr:get-readonly-ancestors($source-context) != ''">
                            <xsl:variable name="initial-readonly-ancestors">
                                <xsl:for-each select="enofr:get-readonly-ancestors($source-context)">
                                    <xsl:value-of select="concat('not(',.,') or ')"/>
                                </xsl:for-each>
                            </xsl:variable>
                            <xsl:call-template name="replaceVariablesInFormula">
                                <xsl:with-param name="formula" select="normalize-space($initial-readonly-ancestors)"/>
                                <xsl:with-param name="variables" as="node()">
                                    <Variables>
                                        <xsl:for-each select="enofr:get-readonly-ancestors-variables($source-context)">
                                            <xsl:sort select="string-length(.)" order="descending"/>
                                            <Variable><xsl:value-of select="."/></Variable>
                                        </xsl:for-each>
                                    </Variables>
                                </xsl:with-param>
                                <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                            </xsl:call-template>
                        </xsl:if>
                        <xsl:call-template name="replaceVariablesInFormula">
                            <xsl:with-param name="formula" select="normalize-space($constraint)"/>
                            <xsl:with-param name="variables" as="node()">
                                <Variables>
                                    <xsl:for-each select="tokenize(enofr:get-control-variables($source-context),' ')">
                                        <xsl:sort select="string-length(.)" order="descending"/>
                                        <Variable><xsl:value-of select="."/></Variable>
                                    </xsl:for-each>
                                </Variables>
                            </xsl:with-param>
                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                        </xsl:call-template>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
            <xsl:if test="$format-constraint != ''">
                <xsl:element name="xf:constraint">
                    <xsl:attribute name="value" select="concat('matches(.,''',$format-constraint,''') or .=''''')"/>
                </xsl:element>
            </xsl:if>
        </xf:bind>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for Bind for the drivers.</xd:p>
            <xd:p>It builds the bind by using different enofr functions then the process goes on next to the created bind.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Bind//TextDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="required" select="enofr:is-required($source-context)" as="xs:boolean"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="type" select="enofr:get-type($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>
        <xsl:variable name="format-constraint" select="enofr:get-format-constraint($source-context)"/>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="$required">
                <xsl:attribute name="required" select="'true()'"/>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="normalize-space($relevant)"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="normalize-space($readonly)"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                    <xsl:value-of select="')'"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$format-constraint != ''">
                <xsl:element name="xf:constraint">
                    <xsl:attribute name="value" select="concat('matches(.,''',$format-constraint,''') or .=''''')"/>
                </xsl:element>
            </xsl:if>
        </xf:bind>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for Bind for the drivers.</xd:p>
            <xd:p>It builds the bind by using different enofr functions then the process goes on next to the created bind.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Bind//NumericDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="required" select="enofr:is-required($source-context)" as="xs:boolean"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="type" select="enofr:get-type($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>
        <xsl:variable name="format-constraint" select="enofr:get-format-constraint($source-context)"/>
        <xsl:variable name="number-of-decimals" select="enofr:get-number-of-decimals($source-context)"/>
        <xsl:variable name="minimum" select="enofr:get-minimum($source-context)"/>
        <xsl:variable name="maximum" select="enofr:get-maximum($source-context)"/>
        <xsl:variable name="type-of-number">
            <xsl:choose>
                <xsl:when test="number($number-of-decimals) &gt; 0">xs:float</xsl:when>
                <xsl:otherwise>xs:integer</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="$required">
                <xsl:attribute name="required" select="'true()'"/>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="normalize-space($relevant)"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="normalize-space($readonly)"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                    <xsl:value-of select="')'"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:element name="xf:constraint">
                <xsl:attribute name="value">
                    <xsl:value-of select="concat('if(. castable as ',$type-of-number,') then (',$type-of-number,'(.)&lt;=')"/>
                    <xsl:choose>
                        <xsl:when test="string-length($maximum) &gt; 9 and $type-of-number='xs:float'">
                            <xsl:value-of select="concat('xs:float(',$maximum,')')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$maximum"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="concat(' and ',$type-of-number,'(.)&gt;=')"/>
                    <xsl:choose>
                        <xsl:when test="string-length($minimum) &gt; 9 and $type-of-number='xs:float'">
                            <xsl:value-of select="concat('xs:float(',$minimum,')')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$minimum"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <!-- The regex for number depends on the sign of minimum and maximum ; each case calls the named template : number-regexp -->
                    <xsl:if test="$type-of-number='xs:float'">
                        <xsl:value-of select="' and matches(.,'''"/>
                        <xsl:choose>
                            <xsl:when test="number($minimum) = 0">
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="$maximum"/>
                                    <xsl:with-param name="start" select="''" tunnel="yes"/>
                                </xsl:call-template>
                                <xsl:value-of select="'$'''"/>
                            </xsl:when>
                            <xsl:when test="number($maximum)+number($minimum) = 0">
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="$maximum"/>
                                    <xsl:with-param name="start" select="'-?'" tunnel="yes"/>
                                </xsl:call-template>
                                <xsl:value-of select="'$'''"/>
                            </xsl:when>
                            <xsl:when test="number($maximum) = 0">
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="substring($minimum,2)"/>
                                    <xsl:with-param name="start" select="'-'" tunnel="yes"/>
                                </xsl:call-template>
                                <xsl:value-of select="'$'''"/>
                            </xsl:when>
                            <xsl:when test="number($minimum) &gt; 0 ">
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="$maximum"/>
                                    <xsl:with-param name="start" select="''" tunnel="yes"/>
                                </xsl:call-template>
                                <xsl:value-of select="'$'') and not(matches(.,'''"/>
                                <xsl:variable name="excluded-minimum">
                                    <xsl:variable name="power">
                                        <xsl:value-of select="'1'"/>
                                        <xsl:for-each select="1 to $number-of-decimals">
                                            <xsl:value-of select="'0'"/>
                                        </xsl:for-each>
                                    </xsl:variable>
                                    <xsl:variable name="format">
                                        <xsl:value-of select="'''0.'"/>
                                        <xsl:for-each select="1 to $number-of-decimals">
                                            <xsl:value-of select="'0'"/>
                                        </xsl:for-each>
                                        <xsl:value-of select="''''"/>
                                    </xsl:variable>
                                    <xsl:value-of select="substring-before(substring-after(format-number((number($minimum) * $power -1) div $power,$format),''''),'''')"/>
                                </xsl:variable>
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="$excluded-minimum"/>
                                    <xsl:with-param name="start" select="''" tunnel="yes"/>
                                </xsl:call-template>
                                <xsl:value-of select="'$'')'"/>
                            </xsl:when>
                            <xsl:when test="number($minimum) &lt; 0 and number($maximum) &gt; 0">
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="$maximum"/>
                                    <xsl:with-param name="start" select="''" tunnel="yes"/>
                                </xsl:call-template>
                                <xsl:value-of select="'$'') or matches(.,'''"/>
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="substring($minimum,2)"/>
                                    <xsl:with-param name="start" select="'-'" tunnel="yes"/>
                                </xsl:call-template>
                                <xsl:value-of select="'$'''"/>
                            </xsl:when>
                            <xsl:when test="number($maximum) &lt; 0 ">
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="substring($minimum,2)"/>
                                    <xsl:with-param name="start" select="'-'" tunnel="yes"/>
                                </xsl:call-template>
                                <xsl:value-of select="'$'') and not(matches(.,'''"/>
                                <xsl:variable name="excluded-maximum">
                                    <xsl:variable name="power">
                                        <xsl:value-of select="'1'"/>
                                        <xsl:for-each select="1 to $number-of-decimals">
                                            <xsl:value-of select="'0'"/>
                                        </xsl:for-each>
                                    </xsl:variable>
                                    <xsl:variable name="format">
                                        <xsl:value-of select="'''0.'"/>
                                        <xsl:for-each select="1 to $number-of-decimals">
                                            <xsl:value-of select="'0'"/>
                                        </xsl:for-each>
                                        <xsl:value-of select="''''"/>
                                    </xsl:variable>
                                    <xsl:value-of select="substring-before(substring-after(format-number((number($maximum) * $power +1) div $power,$format),''''),'''')"/>
                                </xsl:variable>
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="substring($maximum,2)"/>
                                    <xsl:with-param name="start" select="'-'" tunnel="yes"/>
                                </xsl:call-template>
                                <xsl:value-of select="'$'')'"/>
                            </xsl:when>
                        </xsl:choose>
                        <xsl:value-of select="')'"/>
                    </xsl:if>
                    <xsl:value-of select="') else (.='''')'"/>
                </xsl:attribute>
            </xsl:element>
        </xf:bind>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>Recursive named template which calculated the regex of a positive number</xd:desc>
        <xd:desc>5 cases :
            - only 9s (ex : 99999999.999999)
            - only 9s except the first digit
            - only 9s after the dot ; something different for a digit before the dot and different from the first one
            - something different from 9s after the dot and the number made by the digits before the 9s is less than 10 (ex : 123456798.00000079999 or 12.5)
            - something different from 9s after the dot and the number made by the digits before the 9s is more than 9 (ex : 12.109)
            Each case adds a regex and, except the first one, calls itself or a previous one with at least one digit less different from 9 at the end
            Ex : numbers less than 31.01299 : numbers between 31.01 and 31.01299 + numbers between 31 and 31.00999 + numbers between 30 and 30.999999 + numbers between 10 and 29.99999 + numbers between 0 and 9.99999
        </xd:desc>
    </xd:doc>
    <xsl:template name="number-regexp">
        <xsl:param name="number"/>
        <xsl:param name="start" tunnel="yes"/>
        <xsl:param name="decimal-whole-part" select="false()" as="xs:boolean"/>

        <xsl:analyze-string select="$number" regex="^([9]+)(\.9+)?$">
            <xsl:matching-substring>
                <xsl:value-of select="concat('^',$start,'(0|[1-9]')"/>
                <xsl:if test="string-length(regex-group(1)) != 1">
                    <xsl:value-of select="concat('[0-9]{0,',string-length(regex-group(1))-1,'}')"/>
                </xsl:if>
                <xsl:if test="not($decimal-whole-part)">
                    <xsl:value-of select="')'"/>
                    <xsl:if test="string-length(regex-group(2)) != 0">
                        <xsl:value-of select="concat('(\.[0-9]{1,',string-length(regex-group(2))-1,'})?')"/>
                    </xsl:if>
                </xsl:if>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:analyze-string select="$number" regex="^([0-8])([9]*)(\.9+)?$">
                    <xsl:matching-substring>
                        <xsl:choose>
                            <xsl:when test="string-length(regex-group(2)) = 0">
                                <xsl:value-of select="concat('^',$start,'(0')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:call-template name="number-regexp">
                                    <xsl:with-param name="number" select="regex-group(2)"/>
                                    <xsl:with-param name="decimal-whole-part" select="true()"/>
                                </xsl:call-template>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:value-of select="'|'"/>
                        <xsl:value-of select="concat('[1-',regex-group(1),']')"/>
                        <xsl:if test="string-length(regex-group(2)) != 0">
                            <xsl:value-of select="concat('[0-9]{',string-length(regex-group(2)),'}')"/>
                        </xsl:if>
                        <xsl:if test="not($decimal-whole-part)">
                            <xsl:value-of select="')'"/>
                            <xsl:if test="string-length(regex-group(3)) != 0">
                                <xsl:value-of select="concat('(\.[0-9]{1,',string-length(regex-group(3))-1,'})?')"/>
                            </xsl:if>
                        </xsl:if>
                    </xsl:matching-substring>
                    <xsl:non-matching-substring>
                        <xsl:analyze-string select="$number" regex="^([1-9])([0-9]*)([0-8])([9]*)(\.9+)?$">
                            <xsl:matching-substring>
                                <xsl:variable name="integer-begin" select="string(number(concat(regex-group(1),regex-group(2)))-1)"/>
                                <xsl:choose>
                                    <xsl:when test="contains($integer-begin,'E')">
                                        <xsl:call-template name="number-regexp">
                                            <xsl:with-param name="number" select="concat(replace(substring-before($integer-begin,'E'),'\.',''),'9',regex-group(4))"/>
                                            <xsl:with-param name="decimal-whole-part" select="true()"/>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:call-template name="number-regexp">
                                            <xsl:with-param name="number" select="concat($integer-begin,'9',regex-group(4))"/>
                                            <xsl:with-param name="decimal-whole-part" select="true()"/>
                                        </xsl:call-template>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:value-of select="concat('|',regex-group(1),regex-group(2),'[0-',regex-group(3),']')"/>
                                <xsl:if test="string-length(regex-group(4)) != 0">
                                    <xsl:value-of select="concat('[0-9]{',string-length(regex-group(4)),'}')"/>
                                </xsl:if>
                                <xsl:if test="not($decimal-whole-part)">
                                    <xsl:value-of select="')'"/>
                                    <xsl:if test="string-length(regex-group(5)) != 0">
                                        <xsl:value-of select="concat('(\.[0-9]{1,',string-length(regex-group(5))-1,'})?')"/>
                                    </xsl:if>
                                </xsl:if>
                            </xsl:matching-substring>
                            <xsl:non-matching-substring>
                                <xsl:analyze-string select="$number" regex="^([0-9]*)\.(0*)([0-8])([9]*)$">
                                    <xsl:matching-substring>
                                        <xsl:choose>
                                            <xsl:when test="regex-group(1) = '0'">
                                                <xsl:value-of select="concat('^',$start)"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:variable name="integer-begin" select="string(number(regex-group(1))-1)"/>
                                                <xsl:choose>
                                                    <xsl:when test="contains($integer-begin,'E')">
                                                        <xsl:call-template name="number-regexp">
                                                            <xsl:with-param name="number" select="concat(replace(substring-before($integer-begin,'E'),'\.',''),'.',replace(regex-group(2),'0','9'),'9',regex-group(4))"/>
                                                            <xsl:with-param name="decimal-whole-part" select="false()"/>
                                                        </xsl:call-template>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:call-template name="number-regexp">
                                                            <xsl:with-param name="number" select="concat($integer-begin,'.',replace(regex-group(2),'0','9'),'9',regex-group(4))"/>
                                                            <xsl:with-param name="decimal-whole-part" select="false()"/>
                                                        </xsl:call-template>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:value-of select="concat('$|^',$start)"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <xsl:value-of select="concat(regex-group(1),'(\.',regex-group(2),'[0-',regex-group(3),']')"/>
                                        <xsl:if test="string-length(regex-group(4)) != 0">
                                            <xsl:value-of select="concat('[0-9]{0,',string-length(regex-group(4)),'}')"/>
                                        </xsl:if>
                                        <xsl:value-of select="')?'"/>
                                    </xsl:matching-substring>
                                    <xsl:non-matching-substring>
                                        <xsl:analyze-string select="$number" regex="^([0-9]*)\.(0*)([1-9][0-9]*)([0-8])([9]*)$">
                                            <xsl:matching-substring>
                                                <xsl:variable name="decimal-begin" select="string(number(regex-group(3))-1)"/>
                                                <xsl:variable name="new-number">
                                                    <xsl:value-of select="concat(regex-group(1),'.',regex-group(2))"/>
                                                    <xsl:if test="string-length($decimal-begin) &lt; string-length(regex-group(3))">
                                                        <xsl:value-of select="'0'"/>
                                                    </xsl:if>
                                                    <xsl:value-of select="concat($decimal-begin,'9',regex-group(5))"/>
                                                </xsl:variable>
                                                <xsl:call-template name="number-regexp">
                                                    <xsl:with-param name="number" select="$new-number"/>
                                                </xsl:call-template>
                                                <xsl:value-of select="concat('$|^',$start,regex-group(1),'\.',regex-group(2),regex-group(3),'([0-',regex-group(4),']')"/>
                                                <xsl:if test="string-length(regex-group(5)) != 0">
                                                    <xsl:value-of select="concat('[0-9]{0,',string-length(regex-group(5)),'}')"/>
                                                </xsl:if>
                                                <xsl:value-of select="')?'"/>
                                            </xsl:matching-substring>
                                            <xsl:non-matching-substring>
                                                <xsl:value-of select="'NotANumber'"/>
                                                <xsl:value-of select="$number"/>
                                                <xsl:value-of select="'NotANumber'"/>
                                            </xsl:non-matching-substring>
                                        </xsl:analyze-string>
                                    </xsl:non-matching-substring>
                                </xsl:analyze-string>
                            </xsl:non-matching-substring>
                        </xsl:analyze-string>
                    </xsl:non-matching-substring>
                </xsl:analyze-string>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Bind for the following drivers.</xd:p>
            <xd:p>It builds the bind by using different enofr functions then the process goes on within the created bind.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Bind//*[name() = ('xf-group', 'Module','Clarification')]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="normalize-space($relevant)"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="normalize-space($readonly)"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                    <xsl:value-of select="')'"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:bind>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Bind for the following drivers.</xd:p>
            <xd:p>It uses the nodeset attribute instead of the ref attribute.</xd:p>
            <xd:p>It builds the bind then the process goes on within the created bind.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Bind//RowLoop | Bind//QuestionLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="business-name" select="enofr:get-business-name($source-context)"/>
        <xsl:variable name="container" select="enofr:get-container-name($source-context)"/>
        
        <xf:bind id="{$container}-bind" name="{$container}" nodeset="{$container}/{$name}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
                <!-- the absolute address of the element in enriched for RowLoop and QuestionLoop, for which several instances are possible -->
                <xsl:with-param name="instance-ancestor" select="if ($instance-ancestor='') then $business-name else concat($instance-ancestor,' ',$business-name)" tunnel="yes"/>
            </xsl:apply-templates>
        </xf:bind>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Bind for the following drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Bind//Table | Bind//TableLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="business-name" select="enofr:get-business-name($source-context)"/>
        <xsl:variable name="container" select="enofr:get-container-name($source-context)"/>
        <xsl:variable name="instance-ancestor-label">
            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
            <xsl:for-each select="tokenize($instance-ancestor,' ')">
                <xsl:value-of select="concat(.,'[@id = current()/ancestor::',.,'/@id]//')"/>
            </xsl:for-each>
        </xsl:variable>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
        <xsl:choose>
            <xsl:when test="not(enofr:get-maximum-lines($source-context)!='') and enofr:get-minimum-lines($source-context)!=''">
                <xf:bind id="{$business-name}-addline-bind" ref="{$business-name}-AddLine"/>
            </xsl:when>
            <xsl:when test="number(enofr:get-minimum-lines($source-context)) &lt; number(enofr:get-maximum-lines($source-context))">
                <xf:bind id="{$business-name}-addline-bind" ref="{$business-name}-AddLine"
                    relevant="count({$instance-ancestor-label}{$container}/{$business-name}) &lt; {enofr:get-maximum-lines($source-context)}"/>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Bind for the DurationDomain and DateTimeDomain drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Bind//DurationDomain | Bind//DateTimeDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="current-driver" select="self::*/local-name()"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="required" select="enofr:is-required($source-context)" as="xs:boolean"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>
        <xsl:variable name="dateduration-format" select="upper-case(enofr:get-format($source-context))"/>
        <xsl:variable name="minimum" select="enofr:get-minimum($source-context)"/>
        <xsl:variable name="maximum" select="enofr:get-maximum($source-context)"/>
        <xsl:variable name="layout-list" as="node()">
            <xsl:call-template name="dateduration-layout">
                <xsl:with-param name="variable-name" select="$name"/>
                <xsl:with-param name="driver" select="$current-driver"/>
                <xsl:with-param name="format" select="$dateduration-format"/>
                <xsl:with-param name="minimum" select="$minimum"/>
                <xsl:with-param name="maximum" select="$maximum"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Creating one calculated element that correspond to the concatenation of the layout ones -->
        <xsl:if test="count($layout-list//format) &gt; 1 or $current-driver = 'DurationDomain'">
            <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="'if ('"/>
                    <xsl:choose>
                        <xsl:when test="upper-case($dateduration-format) = 'MM/AAAA' or $dateduration-format='YYYY-MM'">
                            <xsl:value-of select="concat($name,'-layout-Y = ''''')"/>
                        </xsl:when>
                        <xsl:when test="$dateduration-format='HH:CH'">
                            <xsl:value-of select="concat('../',$name,'-layout-H = ''''')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="$layout-list//format">
                                <xsl:if test="position() != 1">
                                    <xsl:choose>
                                        <xsl:when test="$current-driver = 'DurationDomain'">
                                            <xsl:value-of select="' and '"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="' or '"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:if>
                                <xsl:value-of select="concat('../',@variable,' = '''' ')"/>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="') then '''' else (concat( '"/>
                    <xsl:choose>
                        <xsl:when test="$dateduration-format = 'YYYY-MM' or upper-case($dateduration-format) = 'MM/AAAA'">
                            <xsl:value-of select="concat(' ../',$name,'-layout-Y,')"/>
                            <xsl:value-of select="concat(' if (string-length(../',$name,'-layout-M) = 0) then '''' else if (string-length(../',$name,'-layout-M) = 1) then ''-0'' else ''-''')"/>
                            <xsl:value-of select="concat(', ../',$name,'-layout-M')"/>
                        </xsl:when>
                        <xsl:when test="$current-driver='DateTimeDomain'">
                            <xsl:for-each select="$layout-list//format">
                                    <xsl:if test="position() != 1">
                                        <xsl:value-of select="',''-'','"/>
                                    </xsl:if>
                                    <xsl:value-of select="concat(' if (string-length(../',@variable,') &lt;= 1) then ''0'' else '''' ,')"/>
                                    <xsl:value-of select="concat(' ../',@variable)"/>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:when test="$dateduration-format='HH:CH'">
                            <xsl:value-of select="concat('../',$layout-list//format[1]/@variable,' ,')"/>
                            <xsl:value-of select="''':'','"/>
                            <xsl:value-of select="concat(' if (string-length(../',$layout-list//format[2]/@variable,') = 1) then ''0'' else '''' ,')"/>
                            <xsl:value-of select="concat('../',$layout-list//format[2]/@variable)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="'''P'''"/>
                            <xsl:for-each select="$layout-list//format[@id='Y' or @id='M' or @id='D']">
                                <xsl:value-of select="concat(', if (../',@variable,' != '''') then concat(../',@variable,',''',@id,''') else ''''')"/>
                            </xsl:for-each>
                            <xsl:if test="contains($dateduration-format,'T')">
                                <xsl:value-of select="', if('"/>
                                <xsl:for-each select="$layout-list//format[@id!='Y' and @id!='M' and @id!='D']">
                                    <xsl:if test="position() != 1">
                                        <xsl:value-of select="' or '"/>
                                    </xsl:if>
                                    <xsl:value-of select="concat('../',@variable,' != ''''')"/>
                                </xsl:for-each>
                                <xsl:value-of select="') then ''T'' else '''''"/>
                            </xsl:if>
                            <xsl:for-each select="$layout-list//format[@id!='Y' and @id!='M' and @id!='D']">
                                <xsl:value-of select="concat(', if (../',@variable,' != '''') then concat(../',@variable,',''',upper-case(@id),''') else ''''')"/>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="' ))'"/>
                </xsl:attribute>
            </xf:bind>
        </xsl:if>
        <!-- real element or layout ones -->
        <xsl:for-each select="$layout-list//format">
            <xf:bind id="{@variable}-bind" name="{@variable}" ref="{@variable}">
                <xsl:if test="$dateduration-format = 'YYYY-MM-DD' or upper-case($dateduration-format) = 'JJ/MM/AAAA'">
                    <xsl:attribute name="type" select="'xf:date'"/>
                </xsl:if>
                <xsl:if test="self::DurationDomain">
                    <xsl:attribute name="type" select="'xf:number'"/>
                </xsl:if>
                <xsl:if test="$required">
                    <xsl:attribute name="required" select="'true()'"/>
                </xsl:if>
                <xsl:if test="$relevant != ''">
                    <xsl:attribute name="relevant">
                        <xsl:call-template name="replaceVariablesInFormula">
                            <xsl:with-param name="formula" select="normalize-space($relevant)"/>
                            <xsl:with-param name="variables" as="node()">
                                <Variables>
                                    <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                        <xsl:sort select="string-length(.)" order="descending"/>
                                        <Variable><xsl:value-of select="."/></Variable>
                                    </xsl:for-each>
                                </Variables>
                            </xsl:with-param>
                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                        </xsl:call-template>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="not($readonly = ('false()', ''))">
                    <xsl:attribute name="readonly">
                        <xsl:value-of select="'not('"/>
                        <xsl:call-template name="replaceVariablesInFormula">
                            <xsl:with-param name="formula" select="normalize-space($readonly)"/>
                            <xsl:with-param name="variables" as="node()">
                                <Variables>
                                    <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                        <xsl:sort select="string-length(.)" order="descending"/>
                                        <Variable><xsl:value-of select="."/></Variable>
                                    </xsl:for-each>
                                </Variables>
                            </xsl:with-param>
                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                        </xsl:call-template>
                        <xsl:value-of select="')'"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="($dateduration-format = 'YYYY-MM-DD' or upper-case($dateduration-format) = 'JJ/MM/AAAA') and ($minimum != '' or $maximum != '')">
                    <xsl:element name="xf:constraint">
                        <xsl:attribute name="value">
                            <xsl:value-of select="'if (string(.) != '''' and . castable as xs:date) then ('"/>
                            <xsl:if test="$minimum != ''">
                                <xsl:choose>
                                    <xsl:when test="contains($minimum,'-date()')">
                                        <xsl:value-of select="'. &gt;= xs:date(local-date())'"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="concat('. &gt;= xs:date(''',$minimum,''')')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:if>
                            <xsl:if test="$minimum != '' and $maximum != ''">
                                <xsl:value-of select="' and '"/>
                            </xsl:if>
                            <xsl:if test="$maximum != ''">
                                <xsl:choose>
                                    <xsl:when test="contains($maximum,'-date()')">
                                        <xsl:value-of select="'. &lt;= xs:date(local-date())'"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="concat('. &lt;= xs:date(''',$maximum,''')')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:if>
                            <xsl:value-of select="') else (string(.) = '''')'"/>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:if>
                <xsl:if test="$current-driver = 'DurationDomain'">
                    <xsl:element name="xf:constraint">
                        <xsl:attribute name="value" select="concat('if (. castable as xs:integer) then (xs:integer(.)&lt;=',@maximum,' and xs:integer(.)&gt;=',@minimum,') else (.='''')')"/>
                    </xsl:element>
                </xsl:if>
                <xsl:if test="($dateduration-format='YYYY-MM' or upper-case($dateduration-format)='MM/AAAA') and @id='Y'">
                    <xsl:element name="xf:constraint">
                        <xsl:attribute name="value" select="concat('if (string(.)='''') then string(../',$name,'-layout-M)='''' else (string(../',$name,'-layout-M)!='''')')"/>
                    </xsl:element>
                </xsl:if>
            </xf:bind>
        </xsl:for-each>
        <xsl:if test="count($layout-list//format) &gt; 1 or $current-driver = 'DurationDomain'">
            <xf:bind id="{$name}-dateduration-constraint-bind" name="{$name}-dateduration-constraint" ref="{$name}-dateduration-constraint">
                <xsl:if test="$minimum != '' or $maximum != '' or $current-driver != 'DurationDomain'">
                    <xsl:element name="xf:constraint">
                        <xsl:attribute name="value">
                            <xsl:choose>
                                <xsl:when test="$current-driver = 'DurationDomain'">
                                    <xsl:value-of select="concat('if (string(../',$name,') != '''') then ((')"/>
                                    <xsl:if test="$minimum != ''">
                                        <xsl:for-each select="$layout-list//format">
                                            <xsl:if test="position() != 1">
                                                <xsl:value-of select="') or ('"/>
                                            </xsl:if>
                                            <xsl:for-each select="preceding-sibling::format">
                                                <xsl:value-of select="concat('(',lower-case(@unit),'s-from-duration(../',$name,') = ',@global-minimum,') and ')"/>
                                            </xsl:for-each>
                                            <xsl:value-of select="concat('(',lower-case(@unit),'s-from-duration(../',$name,') &gt;')"/>
                                            <xsl:if test="not(following-sibling::format)">
                                                <xsl:value-of select="'='"/>
                                            </xsl:if>
                                            <xsl:value-of select="concat(' ',@global-minimum,')')"/>
                                        </xsl:for-each>
                                    </xsl:if>
                                    <xsl:if test="$minimum != '' and $minimum != ''">
                                        <xsl:value-of select="')) and (('"/>
                                    </xsl:if>
                                    <xsl:if test="$maximum != ''">
                                        <xsl:for-each select="$layout-list//format">
                                            <xsl:if test="position() != 1">
                                                <xsl:value-of select="') or ('"/>
                                            </xsl:if>
                                            <xsl:for-each select="preceding-sibling::format">
                                                <xsl:value-of select="concat('(',lower-case(@unit),'s-from-duration(../',$name,') = ',@global-maximum,') and ')"/>
                                            </xsl:for-each>
                                            <xsl:value-of select="concat('(',lower-case(@unit),'s-from-duration(../',$name,') &lt;')"/>
                                            <xsl:if test="not(following-sibling::format)">
                                                <xsl:value-of select="'='"/>
                                            </xsl:if>
                                            <xsl:value-of select="concat(' ',@global-maximum,')')"/>
                                        </xsl:for-each>
                                    </xsl:if>
                                    <xsl:value-of select="concat(')) else (../',$name,'='''')')"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="concat('if(string(../',$name,') != '''') then (')"/>
                                    <xsl:if test="$minimum != ''">
                                        <xsl:choose>
                                            <xsl:when test="contains($minimum,'-date()')">
                                                <xsl:value-of select="concat('string(../',$name,') &gt;= substring(local-date(),1,7)')"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="concat('string(../',$name,') &gt;= ''',$minimum,'''')"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:if>
                                    <xsl:if test="$minimum != '' and $maximum != ''">
                                        <xsl:value-of select="' and '"/>
                                    </xsl:if>
                                    <xsl:if test="$maximum != ''">
                                        <xsl:choose>
                                            <xsl:when test="contains($maximum,'-date()')">
                                                <xsl:value-of select="concat('string(../',$name,') &lt;= substring(local-date(),1,7)')"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="concat('string(../',$name,') &lt;= ''',$maximum,'''')"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:if>
                                    <xsl:value-of select="concat(') else (../',$name,'='''')')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:if>
            </xf:bind>
        </xsl:if>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for Resource for the drivers.</xd:p>
            <xd:p>It builds the resources by using different enofr functions then the process goes on next to the created resource.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Resource//*[not(ancestor::ResourceItem)]" mode="model" priority="-1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:param name="question-label" tunnel="yes"/>

        <xsl:variable name="label" select="enofr:get-label($source-context, $language)"/>
        <xsl:variable name="hint" select="enofr:get-hint($source-context, $language)"/>
        <xsl:variable name="help" select="enofr:get-help($source-context, $language)"/>
        <xsl:variable name="alert" select="enofr:get-alert($source-context, $language)"/>

        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:if test="$label!='' or $question-label!=''">
                <label>
                    <xsl:choose>
                        <xsl:when test="$question-label!=''">
                            <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
                            <xsl:choose>
                                <xsl:when test="$css-class != ''">
                                    <xsl:value-of select="replace($question-label,'block question',concat('block question ',$css-class))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$question-label"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="eno:serialize($label)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </label>
            </xsl:if>
            <xsl:if test="$hint != ''">
                <hint>
                    <xsl:value-of select="eno:serialize($hint)"/>
                </hint>
            </xsl:if>
            <xsl:if test="$help != ''">
                <help>
                    <xsl:value-of select="eno:serialize($help)"/>
                </help>
            </xsl:if>
            <xsl:if test="$alert != ''">
                <alert>
                    <xsl:value-of select="eno:serialize($alert)"/>
                </alert>
            </xsl:if>
            <xsl:if test="self::CodeDomain or self::BooleanDomain">
                <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                    <xsl:with-param name="driver" select="eno:append-empty-element('ResourceItem', .)" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:if>
        </xsl:element>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Resource for the driver ConsistencyCheck.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Resource//ConsistencyCheck" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>

        <xsl:variable name="alert" select="eno:serialize(enofr:get-label($source-context, $language))"/>

        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:if test="$alert!=''">
                <alert>
                    <xsl:value-of select="$alert"/>
                </alert>
            </xsl:if>
        </xsl:element>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Template for Resource for the drivers QuestionLoop and Rowloop.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Resource//QuestionLoop | Resource//RowLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>

        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Resource for the driver FixedCell.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Resource//FixedCell" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>

        <xsl:variable name="label" select="eno:serialize(enofr:get-label($source-context, $language))"/>
        <xsl:variable name="value" select="eno:serialize(enofr:get-cell-value($source-context))"/>

        <xsl:element name="{enofr:get-name($source-context)}">
            <label>
                <xsl:choose>
                    <xsl:when test="$label != '' and $value !=''">
                        <xsl:choose>
                            <xsl:when test="contains($label,'xhtml:p') and contains($value,'xhtml:p')">
                                <xsl:value-of select="concat($label,$value)"/>
                            </xsl:when>
                            <xsl:when test="contains($value,'xhtml:p')">
                                <xsl:value-of select="concat('&lt;xhtml:p&gt;',$label,'&lt;/xhtml:p&gt;',$value)"/>
                            </xsl:when>
                            <xsl:when test="contains($label,'xhtml:p')">
                                <xsl:value-of select="concat($label,'&lt;xhtml:p&gt;',$value,'&lt;/xhtml:p&gt;')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat($label,' ',$value)"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat($label,$value)"/>
                    </xsl:otherwise>
                </xsl:choose>
                </label>
        </xsl:element>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for ResourceItem for xf-item driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="ResourceItem//xf-item" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>

        <xsl:variable name="image" select="enofr:get-image($source-context)"/>

        <item>
            <label>
                <xsl:choose>
                    <xsl:when test="$image = ''">
                        <xsl:value-of select="eno:serialize(enofr:get-label($source-context, $language))"/>
                    </xsl:when>
                    <xsl:when test="starts-with($image,'http')">
                        <xsl:value-of select="concat('&lt;img src=&quot;',$image,
                            '&quot; title=&quot;',eno:serialize(enofr:get-label($source-context, $language)),'&quot; /&gt;')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat('&lt;img src=&quot;/',$properties//Images/Folder,'/',$image,
                            '&quot; title=&quot;',eno:serialize(enofr:get-label($source-context, $language)),'&quot; /&gt;')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </label>
            <value>
                <xsl:value-of select="enofr:get-value($source-context)"/>
            </value>
        </item>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>The xf-item driver produces something only in the ResourceItem part.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[name() = ('Instance', 'Bind', 'Body')]//xf-item" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    <xsl:template match="Resource//xf-item[not(ancestor::ResourceItem)]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Resource for DurationDomain and DateTimeDomain drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Resource//DurationDomain[not(ancestor::ResourceItem)] | Resource//DateTimeDomain[not(ancestor::ResourceItem)]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:param name="question-label" tunnel="yes"/>

        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label" select="enofr:get-label($source-context, $language)"/>
        <xsl:variable name="hint" select="enofr:get-hint($source-context, $language)"/>

        <xsl:variable name="current-driver" select="self::*/local-name()"/>
        <xsl:variable name="dateduration-format" select="upper-case(enofr:get-format($source-context))"/>
        <xsl:variable name="layout-list" as="node()">
            <xsl:call-template name="dateduration-layout">
                <xsl:with-param name="variable-name" select="$name"/>
                <xsl:with-param name="driver" select="$current-driver"/>
                <xsl:with-param name="format" select="$dateduration-format"/>
                <xsl:with-param name="minimum" select="enofr:get-minimum($source-context)"/>
                <xsl:with-param name="maximum" select="enofr:get-maximum($source-context)"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:for-each select="$layout-list//format">
            <xsl:element name="{@variable}">
                <xsl:if test="($label!='' or $question-label!='') and ((position() = 1 and $current-driver='DurationDomain') or (position() = last() and $current-driver='DateTimeDomain'))">
                    <label>
                        <xsl:choose>
                            <xsl:when test="$question-label!=''">
                                <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
                                <xsl:choose>
                                    <xsl:when test="$css-class != ''">
                                        <xsl:value-of select="replace($question-label,'block question',concat('block question ',$css-class))"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="$question-label"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="eno:serialize($label)"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </label>
                </xsl:if>
                <xsl:if test="$hint != '' and $question-label != '' and (position() = last() or $current-driver='DurationDomain')">
                    <hint>
                        <xsl:value-of select="$hint"/>
                    </hint>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="$current-driver = 'DurationDomain'">
                        <alert>
                            <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Integer"/>
                            <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/Between,' ',@minimum)"/>
                            <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/And,' ',@maximum)"/>
                        </alert>
                    </xsl:when>
                    <xsl:when test="$dateduration-format = 'YYYY-MM-DD' or upper-case($dateduration-format) = 'JJ/MM/AAAA'">
                        <alert>
                            <xsl:value-of select="enofr:get-alert($source-context, $language)"/>
                        </alert>
                    </xsl:when>
                    <xsl:when test="($dateduration-format = 'YYYY-MM' or upper-case($dateduration-format) = 'MM/AAAA') and @unit='Year'">
                        <alert>
                            <xsl:value-of select="enofr:get-alert($source-context, $language)"/>
                        </alert>
                    </xsl:when>
                </xsl:choose>
                <xsl:if test="$current-driver = 'DateTimeDomain' and @unit != ''">
                    <xsl:variable name="unit-id" select="@id"/>
                    <xsl:variable name="number-order">
                        <xsl:choose>
                            <xsl:when test="$unit-id = 'Y'">
                                <xsl:value-of select="'descending'"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="'ascending'"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:variable name="min" as="xs:integer">
                        <xsl:choose>
                            <xsl:when test="contains(@minimum,'-date()')">
                                <xsl:value-of select="year-from-date(current-date())"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="number(@minimum)"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:variable name="max" as="xs:integer">
                        <xsl:choose>
                            <xsl:when test="contains(@maximum,'-date()')">
                                <xsl:value-of select="year-from-date(current-date())"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="number(@maximum)"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:for-each select="$min to $max">
                        <xsl:sort select="." order="{$number-order}"/>
                        <xsl:variable name="current-value" select="."/>
                        <item>
                            <label>
                                <xsl:choose>
                                    <xsl:when test="$unit-id = 'M'">
                                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/DateTime/Months/*[position()=$current-value]/text()"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="."/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </label>
                            <value>
                                <xsl:value-of select="."/>
                            </value>
                        </item>
                    </xsl:for-each>
                </xsl:if>
            </xsl:element>
        </xsl:for-each>
        <xsl:if test="$current-driver='DurationDomain' or $dateduration-format = 'YYYY-MM' or upper-case($dateduration-format) = 'MM/AAAA'">
            <xsl:element name="{$name}-dateduration-constraint">
                <alert>
                    <xsl:value-of select="enofr:get-alert($source-context, $language)"/>
                </alert>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the Module driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//Module" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>

        <fr:section id="{$name}-control" bind="{$name}-bind" name="{$name}">
            <xf:label>
                <xsl:attribute name="ref">
                    <xsl:call-template name="label-ref-condition">
                        <xsl:with-param name="source-context" select="$source-context"/>
                        <xsl:with-param name="label" select="concat('$form-resources/',$name,'/label')"/>
                        <xsl:with-param name="conditioning-variables" select="enofr:get-label-conditioning-variables($source-context, $languages[1])"/>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                </xsl:attribute>
            </xf:label>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </fr:section>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the SubModule and Group drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//SubModule | Body//Group" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label" select="enofr:get-label($source-context, $languages[1])"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
        <xsl:variable name="title-level">
            <xsl:choose>
                <xsl:when test="self::SubModule">
                    <xsl:value-of select="'xhtml:h3'"/>
                </xsl:when>
                <xsl:when test="self::Group">
                    <xsl:value-of select="'xhtml:h4'"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>

        <xhtml:div>
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class" select="$css-class"/>
            </xsl:if>
            <xsl:if test="$label != ''">
                <xsl:element name="{$title-level}">
                    <xf:output id="{$name}-control" bind="{$name}-bind">
                        <xf:label>
                            <xsl:attribute name="ref">
                                <xsl:call-template name="label-ref-condition">
                                    <xsl:with-param name="source-context" select="$source-context"/>
                                    <xsl:with-param name="label" select="concat('$form-resources/',$name,'/label')"/>
                                    <xsl:with-param name="conditioning-variables" select="enofr:get-label-conditioning-variables($source-context, $languages[1])"/>
                                    <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                </xsl:call-template>
                            </xsl:attribute>
                            <xsl:if test="$css-class != ''">
                                <xsl:attribute name="class" select="$css-class"/>
                            </xsl:if>
                            <xsl:if test="eno:is-rich-content($label)">
                                <xsl:attribute name="mediatype">text/html</xsl:attribute>
                            </xsl:if>
                        </xf:label>
                    </xf:output>
                </xsl:element>
            </xsl:if>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:div>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the xf-group or the Clarification drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//xf-group | Body//Clarification" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xf:group id="{$name}-control" bind="{$name}-bind">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:group>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the MultipleQuestion or MultipleChoiceQuestion driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//MultipleQuestion | Body//MultipleChoiceQuestion" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:div class="question">
            <!-- A new virtual tree is created as driver -->
            <xsl:variable name="new-driver">
                <Body>
                    <xf-output/>
                </Body>
            </xsl:variable>
            <!-- This new driver is applied on the same source-context -->
            <xsl:apply-templates select="$new-driver//xf-output" mode="model"/>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:div>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the SingleResponseQuestion driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//SingleResponseQuestion" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
            <xsl:with-param name="question-label" select="eno:serialize(enofr:get-label($source-context,$languages[1]))" tunnel="yes"/>
            <xsl:with-param name="question-label-variables" select="enofr:get-label-conditioning-variables($source-context, $languages[1])" as="xs:string *" tunnel="yes"/>
            <xsl:with-param name="rich-question-label" select="eno:is-rich-content(enofr:get-label($source-context, $languages[1]))" tunnel="yes" as="xs:boolean"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for Body for the drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//*" mode="model" priority="-1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:param name="question-label" tunnel="yes"/>
        <xsl:param name="question-label-variables" tunnel="yes"/>
        <xsl:param name="rich-question-label" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="appearance" select="enofr:get-appearance($source-context)"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
        <xsl:variable name="length" select="enofr:get-length($source-context)"/>
        <xsl:variable name="suffix" select="enofr:get-suffix($source-context, $languages[1])"/>
        <xsl:variable name="label" select="enofr:get-label($source-context, $languages[1])"/>
        <xsl:variable name="hint" select="enofr:get-hint($source-context, $languages[1])"/>
        <xsl:variable name="help" select="enofr:get-help($source-context, $languages[1])"/>
        <xsl:variable name="alert" select="enofr:get-alert($source-context, $languages[1])"/>
        <xsl:variable name="instance-ancestor-label">
            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
            <xsl:for-each select="tokenize($instance-ancestor,' ')">
                <xsl:value-of select="concat(.,'[@id = current()/ancestor::',.,'/@id]//')"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="xforms-element">
            <xsl:choose>
                <xsl:when test="self::TextDomain">
                    <xsl:value-of select="'xf:input'"/>
                </xsl:when>
                <xsl:when test="self::NumericDomain">
                    <xsl:value-of select="'fr:number'"/>
                </xsl:when>
                <xsl:when test="self::TextareaDomain">
                    <xsl:value-of select="'xf:textarea'"/>
                </xsl:when>
                <xsl:when test="self::xf-output">
                    <xsl:value-of select="'xf:output'"/>
                </xsl:when>
                <xsl:when test="self::BooleanDomain">
                    <xsl:value-of select="'xf:select'"/>
                </xsl:when>
                <xsl:when test="self::CodeDomain and $appearance='checkbox'">
                    <xsl:value-of select="'xf:select'"/>
                </xsl:when>
                <xsl:when test="self::CodeDomain">
                    <xsl:value-of select="'xf:select1'"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>

        <xsl:element name="{$xforms-element}">
            <xsl:attribute name="id" select="concat($name, '-control')"/>
            <xsl:attribute name="name" select="$name"/>
            <xsl:attribute name="bind" select="concat($name, '-bind')"/>
            <xsl:if test="$appearance != ''">
                <xsl:choose>
                    <xsl:when test="$appearance = 'drop-down-list'">
                        <xsl:attribute name="appearance" select="'minimal'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="appearance" select="'full'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$css-class != '' or $question-label!=''">
                <xsl:choose>
                    <xsl:when test="$question-label!='' and $css-class!=''">
                        <xsl:attribute name="class" select="concat('question ',$css-class)"/>
                    </xsl:when>
                    <xsl:when test="$question-label !=''">
                        <xsl:attribute name="class" select="'question'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="class" select="$css-class"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="not($length = '')">
                <xsl:attribute name="xxf:maxlength" select="$length"/>
            </xsl:if>
            <xsl:if test="self::NumericDomain">
                <xsl:attribute name="grouping-separator" select="' '"/>
                <xsl:choose>
                    <xsl:when test="number(enofr:get-number-of-decimals($source-context)) &gt; 0">
                        <xsl:attribute name="decimal-separator" select="$decimal-separator"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="xxf:fraction-digits" select="'0'"/>
                        <xsl:if test="number(enofr:get-minimum($source-context)) &gt;= 0">
                            <xsl:attribute name="xxf:non-negative" select="'true()'"/>
                        </xsl:if>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="not($suffix = '')">
                    <xsl:attribute name="suffix" select="$suffix"/>
                </xsl:if>
            </xsl:if>
            <xsl:if test="$label != '' or $question-label!= ''">
                <xsl:variable name="conditioning-variables" as="xs:string*">
                    <xsl:choose>
                        <xsl:when test="$question-label-variables != ''">
                            <xsl:sequence select="$question-label-variables"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:sequence select="enofr:get-label-conditioning-variables($source-context, $languages[1])"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xf:label>
                    <xsl:attribute name="ref">
                        <xsl:call-template name="label-ref-condition">
                            <xsl:with-param name="source-context" select="$source-context"/>
                            <xsl:with-param name="label" select="concat('$form-resources/',$name,'/label')"/>
                            <xsl:with-param name="conditioning-variables" select="$conditioning-variables"/>
                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                        </xsl:call-template>
                    </xsl:attribute>
                    <xsl:if test="$rich-question-label or eno:is-rich-content($label)">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:label>
            </xsl:if>
            <xsl:if test="$hint != ''">
                <xf:hint ref="$form-resources/{$name}/hint">
                    <xsl:if test="eno:is-rich-content($hint)">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:hint>
            </xsl:if>
            <xsl:if test="$help != ''">
                <xf:help ref="$form-resources/{$name}/help">
                    <xsl:if test="eno:is-rich-content($help)">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:help>
            </xsl:if>
            <xsl:if test="$alert != ''">
                <xf:alert ref="$form-resources/{$name}/alert">
                    <xsl:if test="enofr:get-alert-level($source-context) != ''">
                        <xsl:attribute name="level" select="enofr:get-alert-level($source-context)"/>
                    </xsl:if>
                    <xsl:if test="eno:is-rich-content($alert)">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:alert>
            </xsl:if>
            <xsl:if test="self::CodeDomain or self::BooleanDomain">
                <xsl:if test="$appearance = 'drop-down-list'">
                    <xf:item>
                        <xf:label/>
                        <xf:value/>
                    </xf:item>
                </xsl:if>
                <xf:itemset ref="$form-resources/{$name}/item">
                    <xf:label>
                        <xsl:attribute name="ref">
                            <xsl:call-template name="label-ref-condition">
                                <xsl:with-param name="source-context" select="$source-context"/>
                                <xsl:with-param name="label" select="'label'"/>
                                <xsl:with-param name="conditioning-variables" select="enofr:get-item-label-conditioning-variables($source-context)"/>
                                <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                            </xsl:call-template>
                        </xsl:attribute>
                        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                            <xsl:with-param name="driver" select="eno:append-empty-element('Rich-Body', .)" tunnel="yes"/>
                        </xsl:apply-templates>
                    </xf:label>
                    <xf:value ref="value"/>
                </xf:itemset>
            </xsl:if>
            <!-- In this select case, if there is still something after a space in the current value, that means that 2 boxes are checked.
            We replace the value with what was after the space and that corresponds to the value of the last checked box.
            This unchecks the first box that was checked -->
            <xsl:if test="self::CodeDomain and $appearance = 'checkbox'">
                <xf:action ev:event="xforms-value-changed" if="substring-after({$instance-ancestor-label}{$name},' ') ne ''">
                    <!-- if the collected variable is in a loop, instance-ancestor helps choosing the good collected variable -->
                    <xf:setvalue ref="{$instance-ancestor-label}{$name}" value="substring-after({$instance-ancestor-label}{$name},' ')"/>
                </xf:action>
            </xsl:if>
            <!-- For each element which relevance depends on this field, we erase the data if it became unrelevant -->
            <xsl:for-each select="enofr:get-relevant-dependencies($source-context)">
                <!-- if the filter is in a loop, instance-ancestor helps choosing the good filter -->
                <!-- if a TableLoop is un the filter, don't empty its counter -->
                <xf:action ev:event="xforms-value-changed"
                    if="not(xxf:evaluate-bind-property('{.}-bind','relevant'))"
                    iterate="{$instance-ancestor-label}{.}//*[not(descendant::*) and not(ends-with(name(),'-Count'))]">
                    <xf:setvalue ref="." value="''"/>
                </xf:action>
            </xsl:for-each>
            <!-- For each element which readonly status depends on this field, we erase the data if it became readonly -->
            <!-- change in the point of view : we keep then now -->
            <!--            <xsl:for-each select="enofr:get-readonly-dependencies($source-context)">
                <xf:action ev:event="xforms-value-changed"
                    if="{concat('xxf:evaluate-bind-property(''',.,'-bind'',''readonly'')')}"
                    iterate="{concat($instance-ancestor-label,.,'//*[not(descendant::*)]')}">
                    <xf:setvalue ref="." value="''"/>
                </xf:action>
            </xsl:for-each>-->

            <xsl:for-each select="enofr:get-constraint-dependencies($source-context)">
                <xsl:element name="xf:dispatch">
                    <xsl:attribute name="ev:event">DOMFocusOut xforms-value-changed</xsl:attribute>
                    <xsl:attribute name="name">DOMFocusOut</xsl:attribute>
                    <xsl:attribute name="target" select="concat(., '-control')"/>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
        <xsl:if test="not($suffix = '') and not(self::NumericDomain)">
            <xsl:element name="xhtml:span">
                <xsl:attribute name="class" select="'suffix'"/>
                <xsl:copy-of select="$suffix" copy-namespaces="no"/>
            </xsl:element>
        </xsl:if>
        <xsl:if test="self::CodeDomain or self::BooleanDomain">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for Body for the ConsistencyCheck.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//ConsistencyCheck" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>

        <xsl:element name="xf:output">
            <xsl:attribute name="id" select="concat($name, '-control')"/>
            <xsl:attribute name="name" select="$name"/>
            <xsl:attribute name="bind" select="concat($name, '-bind')"/>
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class" select="$css-class"/>
            </xsl:if>
            <xf:alert>
                <xsl:attribute name="ref">
                    <xsl:call-template name="label-ref-condition">
                        <xsl:with-param name="source-context" select="$source-context"/>
                        <xsl:with-param name="label" select="concat('$form-resources/',$name,'/alert')"/>
                        <xsl:with-param name="conditioning-variables" select="enofr:get-label-conditioning-variables($source-context, $languages[1])"/>
                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                    </xsl:call-template>
                </xsl:attribute>
                <xsl:if test="enofr:get-alert-level($source-context) != ''">
                    <xsl:attribute name="level" select="enofr:get-alert-level($source-context)"/>
                </xsl:if>
                <xsl:if test="eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:alert>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>Template to add mediatype html/css to rich text items</xd:desc>
    </xd:doc>

    <xsl:template match="Rich-Body//xf-item" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>

        <xsl:if test="enofr:get-image($source-context) != '' or eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
            <xsl:attribute name="mediatype">text/html</xsl:attribute>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the Table driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//Table" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <!-- A new virtual tree is created as driver -->
        <xsl:variable name="new-driver">
            <Body>
                <xf-output/>
            </Body>
        </xsl:variable>
        <!-- This new driver is applied on the same source-context -->
        <xsl:apply-templates select="$new-driver//xf-output" mode="model"/>

        <xsl:variable name="ancestors">
            <xsl:copy-of select="root(.)"/>
        </xsl:variable>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
        <xsl:variable name="isLongTable">
            <xsl:if test="count(enofr:get-body-lines($source-context))>=$lengthOfLongTable">
                <xsl:value-of select="'long-table'"/>
            </xsl:if>
        </xsl:variable>

        <xhtml:table name="{enofr:get-name($source-context)}">
            <xsl:choose>
                <xsl:when test="$isLongTable!=''">
                    <xsl:attribute name="class" select="concat($isLongTable,' ',$css-class)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class" select="$css-class"/>
                </xsl:otherwise>
            </xsl:choose>
            <xhtml:colgroup>
                <xhtml:col span="{count(enofr:get-header-columns($source-context))}"/>
            </xhtml:colgroup>
            <xhtml:thead>
                <xsl:for-each select="enofr:get-header-lines($source-context)">
                    <xhtml:tr>
                        <xsl:apply-templates select="enofr:get-header-line($source-context, position())" mode="source">
                            <xsl:with-param name="driver" select="$ancestors//*[not(child::*) and not(name() = 'driver')]" tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xsl:for-each>
            </xhtml:thead>
            <xhtml:tbody>
                <xsl:for-each select="enofr:get-body-lines($source-context)">
                    <xhtml:tr>
                        <xsl:apply-templates select="enofr:get-body-line($source-context, position())" mode="source">
                            <xsl:with-param name="driver" select="$ancestors//*[not(child::*) and not(name() = 'driver')]" tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xsl:for-each>
            </xhtml:tbody>
        </xhtml:table>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the TableLoop driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//TableLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="table-title">
            <Body>
                <xf-output/>
            </Body>
        </xsl:variable>
        <xsl:variable name="ancestors">
            <xsl:copy-of select="root(.)"/>
        </xsl:variable>
        <xsl:variable name="table-name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="loop-name" select="enofr:get-business-name($source-context)"/>
        <xsl:variable name="container-name" select="enofr:get-container-name($source-context)"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
        <xsl:variable name="isLongTable">
            <xsl:if test="count(enofr:get-body-lines($source-context))>=$lengthOfLongTable">
                <xsl:value-of select="'long-table'"/>
            </xsl:if>
        </xsl:variable>
        <xsl:variable name="instance-ancestor-label">
            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
            <xsl:for-each select="tokenize($instance-ancestor,' ')">
                <xsl:value-of select="concat(.,'[@id = current()/ancestor::',.,'/@id]//')"/>
            </xsl:for-each>
        </xsl:variable>

        <xsl:apply-templates select="$table-title//xf-output" mode="model"/>
        <xhtml:table name="{$table-name}">
            <xsl:choose>
                <xsl:when test="$isLongTable!=''">
                    <xsl:attribute name="class" select="concat($isLongTable,' ',$css-class)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class" select="$css-class"/>
                </xsl:otherwise>
            </xsl:choose>
            <xhtml:colgroup>
                <xhtml:col span="{count(enofr:get-header-columns($source-context))}"/>
            </xhtml:colgroup>
            <xhtml:thead>
                <xsl:for-each select="enofr:get-header-lines($source-context)">
                    <xhtml:tr>
                        <xsl:apply-templates select="enofr:get-header-line($source-context, position())" mode="source">
                            <xsl:with-param name="driver" select="$ancestors//*[not(child::*) and not(name() = 'driver')]" tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xsl:for-each>
            </xhtml:thead>
            <xhtml:tbody>
                <!-- if the loop is in a loop, instance-ancestor helps choosing the good ancestor loop instance -->
                <xf:repeat id="{$container-name}" nodeset="{$instance-ancestor-label}{$container-name}/{$loop-name}">
                    <xf:var name="{$container-name}-position" value="position()"/>
                    <!-- the table has a repeated zone that may have more than one line -->
                    <xsl:for-each select="enofr:get-body-lines($source-context)">
                        <xhtml:tr>
                            <xsl:apply-templates select="enofr:get-body-line($source-context, position())" mode="source">
                                <xsl:with-param name="driver" select="$ancestors//*[not(child::*) and not(name() = 'driver')]" tunnel="yes"/>
                                <!-- the absolute address of the element in enriched for TableLoop, for which several instances of RowLoop are possible -->
                                <xsl:with-param name="instance-ancestor" select="if ($instance-ancestor='') then $loop-name else concat($instance-ancestor,' ',$loop-name)" tunnel="yes"/>
                            </xsl:apply-templates>
                        </xhtml:tr>
                    </xsl:for-each>
                </xf:repeat>
            </xhtml:tbody>
        </xhtml:table>

        <xsl:variable name="max-lines" select="enofr:get-maximum-lines($source-context)"/>

        <xsl:if test="not($max-lines != '') or number($max-lines) &gt; number(enofr:get-minimum-lines($source-context))">
            <xsl:variable name="container" select="enofr:get-container-name($source-context)"/>
            <xf:trigger id="{$loop-name}-addline" bind="{$loop-name}-addline-bind">
                <xf:label ref="$form-resources/AddLine/label"/>
                <xf:action ev:event="DOMActivate">
                    <xf:setvalue ref="{$instance-ancestor-label}{$loop-name}-Count"
                        value="number({$instance-ancestor-label}{$loop-name}-Count) +1"/>
                    <xsl:for-each select="enofr:get-linked-containers($source-context)">
                        <xf:insert context="{$instance-ancestor-label}{.}"
                            nodeset="{$instance-ancestor-label}{.}/{$loop-name}" position="after"
                            origin="instance('fr-form-loop-model')/{.}/{$loop-name}"/>
                        <xf:setvalue ref="{$instance-ancestor-label}{.}/{$loop-name}[last()]/@id"
                            value="concat('{$loop-name}-',{$instance-ancestor-label}{$loop-name}-Count)"/>                        
                    </xsl:for-each>
                </xf:action>
            </xf:trigger>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the TextCell driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//TextCell" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="depth" select="enofr:get-code-depth($source-context)"/>

        <xhtml:th colspan="{enofr:get-colspan($source-context)}" rowspan="{enofr:get-rowspan($source-context)}">
            <xsl:if test="$depth != '1' and $depth != ''">
                <xsl:attribute name="class" select="concat('depth',$depth)"/>
            </xsl:if>
            <!-- A new virtual tree is created as driver -->
            <xsl:variable name="new-driver">
                <Body>
                    <xf-output/>
                </Body>
            </xsl:variable>
            <!-- This new driver is applied on the same source-context -->
            <xsl:apply-templates select="$new-driver//xf-output" mode="model"/>
        </xhtml:th>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the Cell driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//Cell" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:td align="center">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:td>
    </xsl:template>

    <xd:doc>
        <xd:desc>No other - give details out of cells</xd:desc>
    </xd:doc>
    <xsl:template match="Body//Clarification[(ancestor::Table or ancestor::TableLoop) and not(ancestor::Cell)]" mode="model" priority="2"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The Cell driver produces something only in the Body part but its children can produce something.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[name() = ('Instance', 'Bind', 'Resource')]//*[name() = ('Cell')]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the FixedCell driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//FixedCell" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label" select="enofr:get-label($source-context, $languages)"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
        <xsl:variable name="conditioning-variables" as="xs:string*">
            <xsl:sequence select="enofr:get-label-conditioning-variables($source-context, $languages[1])"/>
            <xsl:sequence select="enofr:get-cell-value-variables($source-context)"/>
        </xsl:variable>

        <xhtml:td colspan="{enofr:get-colspan($source-context)}" rowspan="{enofr:get-rowspan($source-context)}">
            <xf:output id="{$name}-control" bind="{$name}-bind">
                <xsl:if test="$css-class != ''">
                    <xsl:attribute name="class" select="$css-class"/>
                </xsl:if>
                <xf:label>
                    <xsl:attribute name="ref">
                        <xsl:call-template name="label-ref-condition">
                            <xsl:with-param name="source-context" select="$source-context"/>
                            <xsl:with-param name="label" select="concat('$form-resources/',$name,'/label')"/>
                            <xsl:with-param name="conditioning-variables" select="$conditioning-variables"/>
                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                        </xsl:call-template>
                    </xsl:attribute>
                <xsl:if test="eno:is-rich-content($label)">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:label>
            </xf:output>
        </xhtml:td>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the EmptyCell driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//EmptyCell" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:td colspan="{enofr:get-colspan($source-context)}" rowspan="{enofr:get-rowspan($source-context)}"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>The EmptyCell driver produces something only in the Body part.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[name() = ('Instance', 'Bind', 'Resource')]//EmptyCell" mode="model"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the QuestionLoop driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//QuestionLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="loop-name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="container-name" select="enofr:get-container-name($source-context)"/>
        <xsl:variable name="business-name" select="enofr:get-business-name($source-context)"/>
        <xsl:variable name="instance-ancestor-label">
            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
            <xsl:for-each select="tokenize($instance-ancestor,' ')">
                <xsl:value-of select="concat(.,'[@id = current()/ancestor::',.,'/@id]//')"/>
            </xsl:for-each>
        </xsl:variable>

        <xf:repeat id="{$container-name}" nodeset="{$instance-ancestor-label}{$container-name}/{$loop-name}">
            <xf:var name="{$container-name}-position" value="position()"/>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
                <!-- the absolute address of the element in enriched for Loops, for which several instances are possible -->
                <xsl:with-param name="instance-ancestor" select="if ($instance-ancestor='') then $business-name else concat($instance-ancestor,' ',$business-name)" tunnel="yes"/>
            </xsl:apply-templates>
        </xf:repeat>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the DurationDomain driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//DurationDomain | Body//DateTimeDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:param name="question-label" tunnel="yes"/>
        <xsl:param name="question-label-variables" tunnel="yes"/>
        <xsl:param name="rich-question-label" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label" select="enofr:get-label($source-context, $languages[1])"/>
        <xsl:variable name="instance-ancestor-label">
            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
            <xsl:for-each select="tokenize($instance-ancestor,' ')">
                <xsl:value-of select="concat(.,'[@id = current()/ancestor::',.,'/@id]//')"/>
            </xsl:for-each>
        </xsl:variable>

        <xsl:variable name="current-driver" select="self::*/local-name()"/>
        <xsl:variable name="dateduration-format" select="upper-case(enofr:get-format($source-context))"/>
        <xsl:variable name="layout-list" as="node()">
            <xsl:call-template name="dateduration-layout">
                <xsl:with-param name="variable-name" select="$name"/>
                <xsl:with-param name="driver" select="$current-driver"/>
                <xsl:with-param name="format" select="$dateduration-format"/>
                <xsl:with-param name="minimum" select="enofr:get-minimum($source-context)"/>
                <xsl:with-param name="maximum" select="enofr:get-maximum($source-context)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="ordered-layout-list" as="node()">
            <formats>
                <xsl:choose>
                    <xsl:when test="$current-driver = 'DateTimeDomain'">
                        <xsl:for-each select="$layout-list//format">
                            <xsl:sort select="position()" order="descending"/>
                            <xsl:copy-of select="."/>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:for-each select="$layout-list//format">
                            <xsl:copy-of select="."/>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
            </formats>
        </xsl:variable>
        <xsl:variable name="input-format">
            <xsl:choose>
                <xsl:when test="$dateduration-format = 'YYYY-MM-DD' or upper-case($dateduration-format) = 'JJ/MM/AAAA'">
                    <xsl:value-of select="'xf:input'"/>
                </xsl:when>
                <xsl:when test="$current-driver='DurationDomain'">
                    <xsl:value-of select="'fr:number'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'xf:select1'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:for-each select="$ordered-layout-list//format">
            <xsl:element name="{$input-format}">
                <xsl:attribute name="id" select="concat(@variable, '-control')"/>
                <xsl:attribute name="name" select="@variable"/>
                <xsl:attribute name="bind" select="concat(@variable, '-bind')"/>
                <xsl:if test="$input-format='xf:select1'">
                    <xsl:attribute name="appearance" select="'minimal'"/>
                </xsl:if>
                <xsl:if test="$input-format='fr:number'">
                    <xsl:attribute name="xxf:fraction-digits" select="'0'"/>
                    <xsl:attribute name="xxf:non-negative" select="'true()'"/>
                </xsl:if>
                <xsl:attribute name="class">
                    <xsl:choose>
                        <xsl:when test="$question-label !='' and $current-driver='DurationDomain'">
                            <xsl:value-of select="'question duration'"/>
                        </xsl:when>
                        <xsl:when test="$current-driver='DurationDomain'">
                            <xsl:value-of select="'duration'"/>
                        </xsl:when>
                        <xsl:when test="$question-label !=''">
                            <xsl:value-of select="'question date'"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="'date'"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:if test="$current-driver = 'DurationDomain'">
                    <xsl:attribute name="xxf:maxlength" select="if (string-length(@minimum) &gt; string-length(@maximum)) then string-length(@minimum) else string-length(@maximum)"/>
                    <xsl:attribute name="suffix" select="$labels-resource/Languages/Language[@xml:lang=$languages[1]]/Duration/*[name()=current()/@unit]/text()"/>
                </xsl:if>
                <xsl:if test="position() = 1 and ($label != '' or $question-label!= '')">
                    <xsl:variable name="conditioning-variables" as="xs:string*">
                        <xsl:choose>
                            <xsl:when test="$question-label-variables != ''">
                                <xsl:sequence select="$question-label-variables"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:sequence select="enofr:get-label-conditioning-variables($source-context, $languages[1])"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xf:label>
                        <xsl:attribute name="ref">
                            <xsl:call-template name="label-ref-condition">
                                <xsl:with-param name="source-context" select="$source-context"/>
                                <xsl:with-param name="label" select="concat('$form-resources/',@variable,'/label')"/>
                                <xsl:with-param name="conditioning-variables" select="$conditioning-variables"/>
                                <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                            </xsl:call-template>
                        </xsl:attribute>
                        <xsl:if test="$rich-question-label or eno:is-rich-content($label)">
                            <xsl:attribute name="mediatype">text/html</xsl:attribute>
                        </xsl:if>
                    </xf:label>
                </xsl:if>
                <xsl:if test="($dateduration-format = 'YYYY-MM-DD' or upper-case($dateduration-format) = 'JJ/MM/AAAA' or (($dateduration-format='YYYY-MM' or upper-case($dateduration-format)='MM/AAAA') and position() = last()))
                           and $question-label !=''">
                    <xf:hint ref="$form-resources/{@variable}/hint"/>
                </xsl:if>
                <xsl:if test="$current-driver = 'DurationDomain' or $dateduration-format = 'YYYY-MM-DD' or upper-case($dateduration-format) = 'JJ/MM/AAAA' or (($dateduration-format='YYYY-MM' or upper-case($dateduration-format)='MM/AAAA') and @id='Y')">
                    <xf:alert ref="$form-resources/{@variable}/alert">
                        <xsl:if test="enofr:get-alert-level($source-context) != ''">
                            <xsl:attribute name="level" select="enofr:get-alert-level($source-context)"/>
                        </xsl:if>
                        <xsl:if test="eno:is-rich-content(enofr:get-alert($source-context, $languages[1]))">
                            <xsl:attribute name="mediatype">text/html</xsl:attribute>
                        </xsl:if>
                    </xf:alert>
                </xsl:if>
                <xsl:for-each select="enofr:get-relevant-dependencies($source-context)">
                    <!-- if the filter is in a loop, instance-ancestor helps choosing the good filter -->
                    <xf:action ev:event="xforms-value-changed"
                        if="not(xxf:evaluate-bind-property('{.}-bind','relevant'))"
                        iterate="{$instance-ancestor-label}{.}//*[not(descendant::*) and not(ends-with(name(),'-Count'))]">
                        <xf:setvalue ref="." value="''"/>
                    </xf:action>
                </xsl:for-each>
                <!-- For each element which readonly status depends on this field, we erase the data if it became readonly -->
                <!-- change in the point of view : we keep then now -->
                <!--            <xsl:for-each select="enofr:get-readonly-dependencies($source-context)">
                <xf:action ev:event="xforms-value-changed"
                    if="{concat('xxf:evaluate-bind-property(''',.,'-bind'',''readonly'')')}"
                    iterate="{concat($instance-ancestor-label,.,'//*[not(descendant::*)]')}">
                    <xf:setvalue ref="." value="''"/>
                </xf:action>
            </xsl:for-each>-->

                <xsl:for-each select="enofr:get-constraint-dependencies($source-context)">
                    <xsl:element name="xf:dispatch">
                        <xsl:attribute name="ev:event">DOMFocusOut xforms-value-changed</xsl:attribute>
                        <xsl:attribute name="name">DOMFocusOut</xsl:attribute>
                        <xsl:attribute name="target" select="concat(., '-control')"/>
                    </xsl:element>
                </xsl:for-each>
                <xsl:if test="$input-format = 'xf:select1'">
                    <xf:item>
                        <xf:label/>
                        <xf:value/>
                    </xf:item>
                    <xf:itemset ref="$form-resources/{@variable}/item">
                        <xf:label ref="label"/>
                        <xf:value ref="value"/>
                    </xf:itemset>
                </xsl:if>
            </xsl:element>
        </xsl:for-each>
        <xsl:if test="$current-driver = 'DurationDomain' or count($layout-list//format) &gt; 1">
            <xf:output id="{$name}-dateduration-constraint-control" name="{$name}-dateduration-constraint" bind="{$name}-dateduration-constraint-bind">
                <xf:alert ref="$form-resources/{$name}-dateduration-constraint/alert" level="error"/>
            </xf:output>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the ResponseElement and CalculatedVariable drivers.</xd:p>
            <xd:p>It corresponds to elements which will be present in the Instance and Bind but not in the Resource and the Body.</xd:p>
            <xd:p>Their prefilled value can have an impact on other elements of the form.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[name() = ('Resource', 'Body')]//*[name() = ('ResponseElement','CalculatedVariable')]" mode="model"/>

    <xd:doc>
        <xd:desc>lists the layout variables for DurationDomain</xd:desc>
    </xd:doc>
    <xsl:template name="dateduration-layout">
        <xsl:param name="variable-name"/>
        <xsl:param name="driver"/>
        <xsl:param name="format"/>
        <xsl:param name="minimum"/>
        <xsl:param name="maximum"/>

        <xsl:variable name="multiple-layout" select="string-length($format) &gt; 4" as="xs:boolean"/>

        <formats>
            <xsl:choose>
                <xsl:when test="$format='YYYY-MM-DD' or upper-case($format)='JJ/MM/AAAA'">
                    <format id="" unit="" minimum="{$minimum}" maximum="{$maximum}" variable="{$variable-name}"/>
                </xsl:when>
                <xsl:when test="$format='HH:CH'">
                    <format id="H" unit="Hour" minimum="20" maximum="59" variable="{$variable-name}-layout-H"/>
                    <format id="CH" unit="Hundredth" minimum="0" maximum="99" variable="{$variable-name}-layout-CH"/>
                </xsl:when>
                <xsl:when test="$driver = 'DateTimeDomain'">
                    <!-- The extremum are different from duration ones ; order is different between date and duration -->
                    <xsl:if test="contains($format,'Y') or contains($format,'A')">
                        <format id="Y" unit="Year">
                            <xsl:attribute name="minimum">
                                <xsl:choose>
                                    <xsl:when test="contains($minimum,'-date()')">
                                        <xsl:value-of select="'year-from-date(xs:date(local-date()))'"/>
                                    </xsl:when>
                                    <xsl:when test="contains($minimum,'-')">
                                        <xsl:value-of select="substring-before($minimum,'-')"/>
                                    </xsl:when>
                                    <xsl:when test="$minimum != ''">
                                        <xsl:value-of select="$minimum"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="1900"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:attribute name="maximum">
                                <xsl:choose>
                                    <xsl:when test="contains($maximum,'-date()')">
                                        <xsl:value-of select="'year-from-date(xs:date(local-date()))'"/>
                                    </xsl:when>
                                    <xsl:when test="contains($maximum,'-')">
                                        <xsl:value-of select="substring-before($maximum,'-')"/>
                                    </xsl:when>
                                    <xsl:when test="$maximum != ''">
                                        <xsl:value-of select="$maximum"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="'year-from-date(xs:date(local-date()))'"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout">
                                    <xsl:value-of select="'-layout-Y'"/>
                                </xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'M') and not(contains(substring-before($format,'M'),'T'))">
                        <format id="M" unit="Month">
                            <xsl:attribute name="minimum">
                                <xsl:choose>
                                    <xsl:when test="not(contains($format,'Y') or contains($format,'A')) and contains($minimum,'-')">
                                        <xsl:value-of select="substring-before($minimum,'-')"/>
                                    </xsl:when>
                                    <xsl:when test="not(contains($format,'Y') or contains($format,'A')) and $minimum != ''">
                                        <xsl:value-of select="$minimum"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="1"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:attribute name="maximum">
                                <xsl:choose>
                                    <xsl:when test="not(contains($format,'Y') or contains($format,'A')) and contains($maximum,'-')">
                                        <xsl:value-of select="substring-before($maximum,'-')"/>
                                    </xsl:when>
                                    <xsl:when test="not(contains($format,'Y') or contains($format,'A')) and $maximum != ''">
                                        <xsl:value-of select="$maximum"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="12"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout">
                                    <xsl:value-of select="'-layout-M'"/>
                                </xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'D') or contains($format,'J')">
                        <format id="D" unit="Day">
                            <xsl:attribute name="minimum">
                                <xsl:choose>
                                    <xsl:when test="not(contains($format,'Y') or contains($format,'A')) and not(contains($format,'M') and not(contains(substring-before($format,'M'),'T'))) and $minimum != ''">
                                        <xsl:value-of select="$minimum"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="1"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:attribute name="maximum">
                                <xsl:choose>
                                    <xsl:when test="not(contains($format,'Y') or contains($format,'A')) and not(contains($format,'M') and not(contains(substring-before($format,'M'),'T'))) and $maximum != ''">
                                        <xsl:value-of select="$maximum"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="31"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout">
                                    <xsl:value-of select="'-layout-D'"/>
                                </xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <!-- duration -->
                    <xsl:if test="contains($format,'Y') or contains($format,'A')">
                        <format id="Y" unit="Year" variable="{$variable-name}-layout-Y">
                            <xsl:choose>
                                <xsl:when test="$minimum != ''">
                                    <xsl:analyze-string select="$minimum" regex="^P([0-9]+)Y.*$">
                                        <xsl:matching-substring>
                                            <xsl:attribute name="minimum" select="regex-group(1)"/>
                                            <xsl:attribute name="global-minimum" select="regex-group(1)"/>
                                        </xsl:matching-substring>
                                        <xsl:non-matching-substring>
                                            <xsl:attribute name="minimum" select="'0'"/>
                                            <xsl:attribute name="global-minimum" select="'0'"/>
                                        </xsl:non-matching-substring>
                                    </xsl:analyze-string>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="minimum" select="'0'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$maximum != ''">
                                    <xsl:analyze-string select="$maximum" regex="^P([0-9]+)Y.*$">
                                        <xsl:matching-substring>
                                            <xsl:attribute name="maximum" select="regex-group(1)"/>
                                            <xsl:attribute name="global-maximum" select="regex-group(1)"/>
                                        </xsl:matching-substring>
                                        <xsl:non-matching-substring>
                                            <xsl:attribute name="maximum" select="'99'"/>
                                            <xsl:attribute name="global-maximum" select="'0'"/>
                                        </xsl:non-matching-substring>
                                    </xsl:analyze-string>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="maximum" select="'99'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'M') and not(contains(substring-before($format,'M'),'T'))">
                        <format id="M" unit="Month" variable="{$variable-name}-layout-M">
                            <xsl:choose>
                                <xsl:when test="$minimum != ''">
                                    <xsl:analyze-string select="$minimum" regex="^P([0-9]+)M.*$">
                                        <xsl:matching-substring>
                                            <xsl:attribute name="minimum" select="regex-group(1)"/>
                                            <xsl:attribute name="global-minimum" select="regex-group(1)"/>
                                        </xsl:matching-substring>
                                        <xsl:non-matching-substring>
                                            <xsl:attribute name="minimum" select="'0'"/>
                                            <xsl:attribute name="global-minimum" select="months-from-duration(xs:duration($minimum))"/>
                                        </xsl:non-matching-substring>
                                    </xsl:analyze-string>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="minimum" select="'0'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:analyze-string select="$format" regex="^PN+M.*$">
                                <xsl:matching-substring>
                                    <xsl:choose>
                                        <xsl:when test="$maximum != ''">
                                            <xsl:analyze-string select="$maximum" regex="^P([0-9]+)M.*$">
                                                <xsl:matching-substring>
                                                    <xsl:attribute name="maximum" select="regex-group(1)"/>
                                                    <xsl:attribute name="global-maximum" select="regex-group(1)"/>
                                                </xsl:matching-substring>
                                                <xsl:non-matching-substring>
                                                    <xsl:message select="concat('format ',$format,' incompatible avec le maximum ',$maximum)"/>
                                                </xsl:non-matching-substring>
                                            </xsl:analyze-string>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="maximum" select="'99'"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:matching-substring>
                                <xsl:non-matching-substring>
                                    <xsl:attribute name="maximum" select="'11'"/>
                                    <xsl:attribute name="global-maximum" select="months-from-duration(xs:duration($maximum))"/>
                                </xsl:non-matching-substring>
                            </xsl:analyze-string>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'D') or contains($format,'J')">
                        <format id="D" unit="Day" variable="{$variable-name}-layout-D">
                            <xsl:choose>
                                <xsl:when test="$minimum != ''">
                                    <xsl:analyze-string select="$minimum" regex="^P([0-9]+)D.*$">
                                        <xsl:matching-substring>
                                            <xsl:attribute name="minimum" select="regex-group(1)"/>
                                            <xsl:attribute name="global-minimum" select="regex-group(1)"/>
                                        </xsl:matching-substring>
                                        <xsl:non-matching-substring>
                                            <xsl:attribute name="minimum" select="'0'"/>
                                            <xsl:attribute name="global-minimum" select="days-from-duration(xs:duration($minimum))"/>
                                        </xsl:non-matching-substring>
                                    </xsl:analyze-string>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="minimum" select="'0'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:analyze-string select="$format" regex="^PN+D.*$">
                                <xsl:matching-substring>
                                    <xsl:choose>
                                        <xsl:when test="$maximum != ''">
                                            <xsl:analyze-string select="$maximum" regex="^P([0-9]+)D.*$">
                                                <xsl:matching-substring>
                                                    <xsl:attribute name="maximum" select="regex-group(1)"/>
                                                    <xsl:attribute name="global-maximum" select="regex-group(1)"/>
                                                </xsl:matching-substring>
                                                <xsl:non-matching-substring>
                                                    <xsl:message select="concat('format ',$format,' incompatible avec le maximum ',$maximum)"/>
                                                </xsl:non-matching-substring>
                                            </xsl:analyze-string>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="maximum" select="'99'"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:matching-substring>
                                <xsl:non-matching-substring>
                                    <xsl:attribute name="maximum" select="'30'"/>
                                    <xsl:attribute name="global-maximum" select="days-from-duration(xs:duration($maximum))"/>
                                </xsl:non-matching-substring>
                            </xsl:analyze-string>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'H')">
                        <format id="H" unit="Hour" variable="{$variable-name}-layout-H">
                            <xsl:choose>
                                <xsl:when test="$minimum != ''">
                                    <xsl:analyze-string select="$minimum" regex="^PT([0-9]+)H.*$">
                                        <xsl:matching-substring>
                                            <xsl:attribute name="minimum" select="regex-group(1)"/>
                                            <xsl:attribute name="global-minimum" select="regex-group(1)"/>
                                        </xsl:matching-substring>
                                        <xsl:non-matching-substring>
                                            <xsl:attribute name="minimum" select="'0'"/>
                                            <xsl:attribute name="global-minimum" select="hours-from-duration(xs:duration($minimum))"/>
                                        </xsl:non-matching-substring>
                                    </xsl:analyze-string>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="minimum" select="'0'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:analyze-string select="$format" regex="^PTN+H.*$">
                                <xsl:matching-substring>
                                    <xsl:choose>
                                        <xsl:when test="$maximum != ''">
                                            <xsl:analyze-string select="$maximum" regex="^PT([0-9]+)H.*$">
                                                <xsl:matching-substring>
                                                    <xsl:attribute name="maximum" select="regex-group(1)"/>
                                                    <xsl:attribute name="global-maximum" select="regex-group(1)"/>
                                                </xsl:matching-substring>
                                                <xsl:non-matching-substring>
                                                    <xsl:message select="concat('format ',$format,' incompatible avec le maximum ',$maximum)"/>
                                                </xsl:non-matching-substring>
                                            </xsl:analyze-string>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="maximum" select="'99'"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:matching-substring>
                                <xsl:non-matching-substring>
                                    <xsl:attribute name="maximum" select="'23'"/>
                                    <xsl:attribute name="global-maximum" select="hours-from-duration(xs:duration($maximum))"/>
                                </xsl:non-matching-substring>
                            </xsl:analyze-string>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'T') and contains(substring-after($format,'T'),'M')">
                        <format id="m" unit="Minute" variable="{$variable-name}-layout-m">
                            <xsl:choose>
                                <xsl:when test="$minimum != ''">
                                    <xsl:analyze-string select="$minimum" regex="^PT([0-9]+)M.*$">
                                        <xsl:matching-substring>
                                            <xsl:attribute name="minimum" select="regex-group(1)"/>
                                            <xsl:attribute name="global-minimum" select="regex-group(1)"/>
                                        </xsl:matching-substring>
                                        <xsl:non-matching-substring>
                                            <xsl:attribute name="minimum" select="'0'"/>
                                            <xsl:attribute name="global-minimum" select="minutes-from-duration(xs:duration($minimum))"/>
                                        </xsl:non-matching-substring>
                                    </xsl:analyze-string>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="minimum" select="'0'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:analyze-string select="$format" regex="^PTN+M.*$">
                                <xsl:matching-substring>
                                    <xsl:choose>
                                        <xsl:when test="$maximum != ''">
                                            <xsl:analyze-string select="$maximum" regex="^PT([0-9]+)M.*$">
                                                <xsl:matching-substring>
                                                    <xsl:attribute name="maximum" select="regex-group(1)"/>
                                                    <xsl:attribute name="global-maximum" select="regex-group(1)"/>
                                                </xsl:matching-substring>
                                                <xsl:non-matching-substring>
                                                    <xsl:message select="concat('format ',$format,' incompatible avec le maximum ',$maximum)"/>
                                                </xsl:non-matching-substring>
                                            </xsl:analyze-string>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="maximum" select="'99'"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:matching-substring>
                                <xsl:non-matching-substring>
                                    <xsl:attribute name="maximum" select="'59'"/>
                                    <xsl:attribute name="global-maximum" select="minutes-from-duration(xs:duration($maximum))"/>
                                </xsl:non-matching-substring>
                            </xsl:analyze-string>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'S')">
                        <format id="S" unit="Second" variable="{$variable-name}-layout-S">
                            <xsl:choose>
                                <xsl:when test="$minimum != ''">
                                    <xsl:analyze-string select="$minimum" regex="^PT([0-9]+)S$">
                                        <xsl:matching-substring>
                                            <xsl:attribute name="minimum" select="regex-group(1)"/>
                                            <xsl:attribute name="global-minimum" select="regex-group(1)"/>
                                        </xsl:matching-substring>
                                        <xsl:non-matching-substring>
                                            <xsl:attribute name="minimum" select="'0'"/>
                                            <xsl:attribute name="global-minimum" select="seconds-from-duration(xs:duration($minimum))"/>
                                        </xsl:non-matching-substring>
                                    </xsl:analyze-string>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="minimum" select="'0'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:analyze-string select="$format" regex="^PTN+S$">
                                <xsl:matching-substring>
                                    <xsl:choose>
                                        <xsl:when test="$maximum != ''">
                                            <xsl:analyze-string select="$maximum" regex="^PT([0-9]+)S$">
                                                <xsl:matching-substring>
                                                    <xsl:attribute name="maximum" select="regex-group(1)"/>
                                                    <xsl:attribute name="global-maximum" select="regex-group(1)"/>
                                                </xsl:matching-substring>
                                                <xsl:non-matching-substring>
                                                    <xsl:message select="concat('format ',$format,' incompatible avec le maximum ',$maximum)"/>
                                                </xsl:non-matching-substring>
                                            </xsl:analyze-string>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="maximum" select="'99'"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:matching-substring>
                                <xsl:non-matching-substring>
                                    <xsl:attribute name="maximum" select="'59'"/>
                                    <xsl:attribute name="global-maximum" select="seconds-from-duration(xs:duration($maximum))"/>
                                </xsl:non-matching-substring>
                            </xsl:analyze-string>
                        </format>
                    </xsl:if>
                </xsl:otherwise>
            </xsl:choose>
        </formats>
    </xsl:template>

    <xd:doc>
        <xd:desc>references the label and indicates how to customize it</xd:desc>
    </xd:doc>
    <xsl:template name="label-ref-condition">
        <xsl:param name="source-context"/>
        <xsl:param name="label"/>
        <xsl:param name="conditioning-variables" as="xs:string*"/>
        <xsl:param name="instance-ancestor"/>

        <xsl:choose>
            <xsl:when test="$conditioning-variables != ''">
                <xsl:for-each select="$conditioning-variables">
                    <xsl:value-of select="'replace('"/>
                </xsl:for-each>
                <xsl:value-of select="$label"/>
                <xsl:for-each select="$conditioning-variables">
                    <xsl:variable name="conditioning-variable" select="."/>
                    <xsl:value-of select="concat(',''',$conditioning-variable-begin,$conditioning-variable,$conditioning-variable-end,''',')"/>
                    <xsl:choose>
                        <xsl:when test="ends-with($conditioning-variable,'-position') and substring-before($conditioning-variable,'-position') = $list-of-groups//Group/@name">
                            <xsl:value-of select="concat('string($',$conditioning-variable,')')"/>
                        </xsl:when>
                        <xsl:when test="enofr:get-conditioning-variable-formula($source-context,$conditioning-variable) != ''">
                            <xsl:call-template name="replaceVariablesInFormula">
                                <xsl:with-param name="formula" select="normalize-space(enofr:get-conditioning-variable-formula($source-context,$conditioning-variable))"/>
                                <xsl:with-param name="variables" as="node()">
                                    <Variables>
                                        <xsl:for-each select="tokenize(enofr:get-conditioning-variable-formula-variables($source-context,$conditioning-variable),' ')">
                                            <xsl:sort select="string-length(.)" order="descending"/>
                                            <Variable><xsl:value-of select="."/></Variable>
                                        </xsl:for-each>
                                    </Variables>
                                </xsl:with-param>
                                <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
                            <xsl:variable name="variable-ancestors" select="enofr:get-variable-business-ancestors($source-context,$conditioning-variable)"/>
                            <xsl:if test="$variable-ancestors != ''">
                                <xsl:for-each select="tokenize($variable-ancestors,' ')">
                                    <xsl:value-of select="concat(.,'[@id = current()/ancestor::',.,'/@id]//')"/>
                                </xsl:for-each>
                            </xsl:if>
                            <xsl:value-of select="enofr:get-variable-business-name($source-context,$conditioning-variable)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="')'"/>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$label"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template named: replaceVariablesInFormula.</xd:p>
            <xd:p>It replaces variables in a all formulas (Filter, ConsistencyCheck, CalculatedVariable, personalized text).</xd:p>
            <xd:p>"variable" -> "variableBusinessName"</xd:p>
            <xd:p>or more complicated for numeric variables</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="replaceVariablesInFormula">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="formula"/>
        <xsl:param name="instance-ancestor"/>
        <xsl:param name="variables" as="node()"/>

        <xsl:variable name="instance-group" select="tokenize($instance-ancestor,' ')[last()]"/>

        <xsl:choose>
            <xsl:when test="$variables/Variable">
                <xsl:variable name="current-variable" select="$variables/Variable[1]"/>
                <xsl:variable name="variable-business-name">
                    <xsl:variable name="variable-ancestors" select="enofr:get-variable-business-ancestors($source-context,$current-variable)"/>
                    <xsl:variable name="business-name" select="enofr:get-variable-business-name($source-context,$current-variable)"/>
                    
                    <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
                    <xsl:for-each select="tokenize($variable-ancestors,' ')">
                        <xsl:if test=". = tokenize($instance-ancestor,' ')">
                            <xsl:value-of select="concat(.,'[@id = current()/ancestor::',.,'/@id]//')"/>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:value-of select="$business-name"/>
                </xsl:variable>
                <xsl:variable name="variable-representation" select="enofr:get-variable-representation($source-context,$current-variable)"/>
                <xsl:choose>
                    <xsl:when test="$variable-representation = 'number' and contains($formula,concat($conditioning-variable-begin,$current-variable,$conditioning-variable-end))">
                        <!-- former default formula for variableId : simplify before analyzing again -->
                        <xsl:analyze-string select="$formula" regex="^(.*)number\(if \({$conditioning-variable-begin}{$current-variable}{$conditioning-variable-end}=''\) then '0' else {$conditioning-variable-begin}{$current-variable}{$conditioning-variable-end}\)(.*)$">
                            <xsl:matching-substring>
                                <xsl:call-template name="replaceVariablesInFormula">
                                    <xsl:with-param name="formula" select="concat(regex-group(1),$conditioning-variable-begin,$current-variable,$conditioning-variable-end,regex-group(2))"/>
                                    <xsl:with-param name="variables" as="node()" select="$variables"/>
                                    <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                </xsl:call-template>
                            </xsl:matching-substring>
                            <xsl:non-matching-substring>
                                <!-- sum or mean or count or min or max with at most 1 group of conditions : [group of conditions without [condition inside condition] inside] -->
                                <!-- e.g. sum ( variableId[condition] ) becomes sum(variableName[string() castable as xs:decimal][condition], 0) -->
                                <xsl:analyze-string select="$formula" regex="^(.*)(sum|mean|count|min|max) *\( *{$conditioning-variable-begin}{$current-variable}{$conditioning-variable-end}(\[[^(\[|\])]+\])? *\)(.*)$">
                                    <xsl:matching-substring>
                                        <xsl:call-template name="replaceVariablesInFormula">
                                            <xsl:with-param name="formula" select="regex-group(1)"/>
                                            <xsl:with-param name="variables" as="node()" select="$variables"/>
                                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                        </xsl:call-template>
                                        <xsl:value-of select="concat(regex-group(2),'(',$variable-business-name,'[string() castable as xs:decimal]')"/>
                                        <xsl:call-template name="replaceVariablesInFormula">
                                            <xsl:with-param name="formula" select="regex-group(3)"/>
                                            <xsl:with-param name="variables" as="node()" select="$variables"/>
                                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                        </xsl:call-template>
                                        <xsl:choose>
                                            <xsl:when test="regex-group(2) = ('sum','mean','count')">
                                                <!-- equal to 0, when all empty for sum, mean and count -->
                                                <xsl:value-of select="', 0)'"/>        
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <!-- equal to blank, when all empty for min and max -->
                                                <xsl:value-of select="')'"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        
                                        <xsl:call-template name="replaceVariablesInFormula">
                                            <xsl:with-param name="formula" select="regex-group(4)"/>
                                            <xsl:with-param name="variables" as="node()" select="$variables"/>
                                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                        </xsl:call-template>
                                    </xsl:matching-substring>
                                    <xsl:non-matching-substring>
                                        <!-- ='' or !='' -->
                                        <!-- e.g.  variableId != '' becomes variableName/string()!='' -->
                                        <xsl:analyze-string select="$formula" regex="^(.*){$conditioning-variable-begin}{$current-variable}{$conditioning-variable-end} *(!)?= *''(.*)$">
                                            <xsl:matching-substring>
                                                <xsl:call-template name="replaceVariablesInFormula">
                                                    <xsl:with-param name="formula" select="regex-group(1)"/>
                                                    <xsl:with-param name="variables" as="node()" select="$variables"/>
                                                    <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                                </xsl:call-template>
                                                <xsl:value-of select="concat($variable-business-name,'/string()',regex-group(2),'=''''')"/>
                                                <xsl:call-template name="replaceVariablesInFormula">
                                                    <xsl:with-param name="formula" select="regex-group(3)"/>
                                                    <xsl:with-param name="variables" as="node()" select="$variables"/>
                                                    <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                                </xsl:call-template>
                                            </xsl:matching-substring>
                                            <xsl:non-matching-substring>
                                                <!-- string(var) ='' or !='' -->
                                                <!-- e.g.  string ( variableId ) != '' becomes variableName/string()!='' -->
                                                <xsl:analyze-string select="$formula" regex="^(.*)string *\( *{$conditioning-variable-begin}{$current-variable}{$conditioning-variable-end} *\) *(!)?= *''(.*)$">
                                                    <xsl:matching-substring>
                                                        <xsl:call-template name="replaceVariablesInFormula">
                                                            <xsl:with-param name="formula" select="regex-group(1)"/>
                                                            <xsl:with-param name="variables" as="node()" select="$variables"/>
                                                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                                        </xsl:call-template>
                                                        <xsl:value-of select="concat($variable-business-name,'/string()',regex-group(2),'=''''')"/>
                                                        <xsl:call-template name="replaceVariablesInFormula">
                                                            <xsl:with-param name="formula" select="regex-group(3)"/>
                                                            <xsl:with-param name="variables" as="node()" select="$variables"/>
                                                            <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                                        </xsl:call-template>
                                                    </xsl:matching-substring>
                                                    <xsl:non-matching-substring>
                                                        <!-- =0 or !=0 or <=0 or >=0 -->
                                                        <!-- the same as the default case except that empty value is not transformed into 0 -->
                                                        <!-- e.g.  variableId != 0 becomes (if (variableName/string()='') then 1 else variableName)!=0 -->
                                                        <xsl:analyze-string select="$formula" regex="^(.*){$conditioning-variable-begin}{$current-variable}{$conditioning-variable-end} *(&lt;|!|&gt;)?= *0([^\.](.*))?$">
                                                            <xsl:matching-substring>
                                                                <xsl:call-template name="replaceVariablesInFormula">
                                                                    <xsl:with-param name="formula" select="regex-group(1)"/>
                                                                    <xsl:with-param name="variables" as="node()" select="$variables"/>
                                                                    <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                                                </xsl:call-template>
                                                                <xsl:value-of select="concat('number(if (',$variable-business-name,'/string()='''') then ')"/>
                                                                <xsl:if test="regex-group(2) = '&gt;'">
                                                                    <xsl:value-of select="'-'"/>
                                                                </xsl:if>
                                                                <xsl:value-of select="concat('1 else ',$variable-business-name,')',regex-group(2),'=0')"/>
                                                                <xsl:call-template name="replaceVariablesInFormula">
                                                                    <xsl:with-param name="formula" select="regex-group(3)"/>
                                                                    <xsl:with-param name="variables" as="node()" select="$variables"/>
                                                                    <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                                                </xsl:call-template>
                                                            </xsl:matching-substring>
                                                            <xsl:non-matching-substring>
                                                                <!-- all the numeric default case -->
                                                                <!-- e.g.  variableId + variable2Id becomes (if (variableName/string()='') then 0 else variableName) + (if (variableName/string()='' then 0 else variableName) -->
                                                                <xsl:for-each select="tokenize($formula,concat($conditioning-variable-begin,$current-variable,$conditioning-variable-end))">
                                                                    <xsl:if test="not(position()=1)">
                                                                        <xsl:value-of select="concat('number(if (',$variable-business-name,'/string()='''') then 0 else ',$variable-business-name,')')"/>
                                                                    </xsl:if>
                                                                    <xsl:call-template name="replaceVariablesInFormula">
                                                                        <xsl:with-param name="formula" select="current()"/>
                                                                        <xsl:with-param name="variables" as="node()">
                                                                            <Variables>
                                                                                <xsl:copy-of select="$variables/Variable[position() != 1 ]"/>
                                                                            </Variables>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                                                                    </xsl:call-template>
                                                                </xsl:for-each>
                                                            </xsl:non-matching-substring>
                                                        </xsl:analyze-string>
                                                    </xsl:non-matching-substring>
                                                </xsl:analyze-string>
                                            </xsl:non-matching-substring>
                                        </xsl:analyze-string>
                                    </xsl:non-matching-substring>
                                </xsl:analyze-string>
                            </xsl:non-matching-substring>
                        </xsl:analyze-string>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:for-each select="tokenize($formula,concat($conditioning-variable-begin,$current-variable,$conditioning-variable-end))">
                            <xsl:if test="not(position()=1)">
                                <xsl:value-of select="$variable-business-name"/>
                            </xsl:if>
                            <xsl:call-template name="replaceVariablesInFormula">
                                <xsl:with-param name="formula" select="current()"/>
                                <xsl:with-param name="variables" as="node()">
                                    <Variables>
                                        <xsl:copy-of select="$variables/Variable[position() != 1 ]"/>
                                    </Variables>
                                </xsl:with-param>
                                <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formula"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
