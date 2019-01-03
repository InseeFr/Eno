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
            <xd:p>The properties file used by the stylesheet.</xd:p>
            <xd:p>It's on a transformation level.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="properties-file"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The properties file is charged as an xml tree.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="properties" select="doc($properties-file)"/>

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
                <xhtml:link rel="stylesheet" href="/{$properties//Css/Folder}/{$properties//Css/Common}"/>
                <xf:model id="fr-form-model" xxf:expose-xpath-types="true" xxf:noscript-support="true">

                    <!-- Main instance, it contains the elements linked to fields, and which will be stored when the form will be submitted -->
                    <xf:instance id="fr-form-instance">
                        <form>
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
    <xsl:template match="Instance//*[name() = ('xf-group', 'Module')]" mode="model">
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
        <xsl:param name="instance-ancestor" tunnel="yes"/>
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
        <xsl:element name="{$name}-Container">
            <xsl:element name="{$name}">
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
        <xsl:element name="{$name}-Container">
            <xsl:element name="{$name}">
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

        <xsl:variable name="dateduration-format" select="enofr:get-format($source-context)"/>
        <xsl:variable name="layout-list" as="node()">
            <xsl:call-template name="dateduration-layout">
                <xsl:with-param name="variable-name" select="$name"/>
                <xsl:with-param name="driver" select="$current-driver"/>
                <xsl:with-param name="format" select="$dateduration-format"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:element name="{$name}"/>
        <xsl:if test="count($layout-list//format) &gt; 1">
            <xsl:for-each select="$layout-list//format">
                <xsl:element name="{$name}-layout-{@id}"/>
            </xsl:for-each>
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
        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('Instance', .)" tunnel="yes"/>
            </xsl:apply-templates>
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
        <xsl:variable name="required" select="enofr:get-required($source-context)"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="calculate" select="enofr:get-variable-calculation($source-context)"/>
        <xsl:variable name="type" select="enofr:get-type($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>
        <xsl:variable name="constraint" select="enofr:get-constraint($source-context)"/>
        <xsl:variable name="format-constraint" select="enofr:get-format-constraint($source-context)"/>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not($required = ('false()', ''))">
                <xsl:attribute name="required" select="$required"/>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="concat('ancestor::',tokenize($instance-ancestor,' ')[last()],'[')"/>
                    </xsl:if>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$relevant"/>
                        <xsl:with-param name="calcul-aim" select="'filter'"/>
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
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="']'"/>
                    </xsl:if>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$calculate != ''">
                <xsl:attribute name="calculate">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$calculate"/>
                        <xsl:with-param name="calcul-aim" select="'calculation'"/>
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
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="concat('ancestor::',tokenize($instance-ancestor,' ')[last()],'[')"/>
                    </xsl:if>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$readonly"/>
                        <xsl:with-param name="calcul-aim" select="'filter'"/>
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
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="']'"/>
                    </xsl:if>
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
                        <xsl:if test="$instance-ancestor != ''">
                            <xsl:value-of select="concat('ancestor::',tokenize($instance-ancestor,' ')[last()],'[')"/>
                        </xsl:if>
                        <xsl:if test="self::ConsistencyCheck and enofr:get-readonly-ancestors($source-context) != ''">
                            <xsl:variable name="initial-readonly-ancestors">
                                <xsl:for-each select="enofr:get-readonly-ancestors($source-context)">
                                    <xsl:value-of select="concat('not(',.,') or ')"/>
                                </xsl:for-each>
                            </xsl:variable>
                            <xsl:call-template name="replaceVariablesInFormula">
                                <xsl:with-param name="formula" select="$initial-readonly-ancestors"/>
                                <xsl:with-param name="calcul-aim" select="'check'"/>
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
                            <xsl:with-param name="formula" select="$constraint"/>
                            <xsl:with-param name="calcul-aim" select="'check'"/>
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
                        <xsl:if test="$instance-ancestor != ''">
                            <xsl:value-of select="']'"/>
                        </xsl:if>
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
    <xsl:template match="Bind//xf-input" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="required" select="enofr:get-required($source-context)"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="type" select="enofr:get-type($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>
        <xsl:variable name="format-constraint" select="enofr:get-format-constraint($source-context)"/>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not($required = ('false()', ''))">
                <xsl:attribute name="required" select="$required"/>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="concat('ancestor::',tokenize($instance-ancestor,' ')[last()],'[')"/>
                    </xsl:if>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$relevant"/>
                        <xsl:with-param name="calcul-aim" select="'filter'"/>
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
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="']'"/>
                    </xsl:if>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="concat('ancestor::',tokenize($instance-ancestor,' ')[last()],'[')"/>
                    </xsl:if>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$readonly"/>
                        <xsl:with-param name="calcul-aim" select="'filter'"/>
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
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="']'"/>
                    </xsl:if>
                    <xsl:value-of select="')'"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$format-constraint != ''">
                <xsl:element name="xf:constraint">
                    <xsl:attribute name="value" select="concat('matches(.,''',$format-constraint,''') or .=''''')"/>
                </xsl:element>
            </xsl:if>
            <xsl:if test="enofr:get-type($source-context)='number'">
                <xsl:variable name="number-of-decimals" select="enofr:get-number-of-decimals($source-context)"/>
                <xsl:variable name="minimum" select="enofr:get-minimum($source-context)"/>
                <xsl:variable name="maximum" select="enofr:get-maximum($source-context)"/>
                <xsl:variable name="type-of-number">
                    <xsl:choose>
                        <xsl:when test="number($number-of-decimals) &gt; 0">xs:float</xsl:when>
                        <xsl:otherwise>xs:integer</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

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
            </xsl:if>
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
    <xsl:template match="Bind//*[name() = ('xf-group', 'Module')]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="concat('ancestor::',tokenize($instance-ancestor,' ')[last()],'[')"/>
                    </xsl:if>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$relevant"/>
                        <xsl:with-param name="calcul-aim" select="'filter'"/>
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
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="']'"/>
                    </xsl:if>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="concat('ancestor::',tokenize($instance-ancestor,' ')[last()],'[')"/>
                    </xsl:if>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$readonly"/>
                        <xsl:with-param name="calcul-aim" select="'filter'"/>
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
                    <xsl:if test="$instance-ancestor != ''">
                        <xsl:value-of select="']'"/>
                    </xsl:if>
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
        <xf:bind id="{$business-name}-Container-bind" name="{$business-name}-Container" nodeset="{$name}-Container/{$name}">
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
        <xsl:variable name="instance-ancestor-label">
            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
            <xsl:for-each select="tokenize($instance-ancestor,' ')">
                <xsl:value-of select="concat('*[name()=''',.,'''][$',.,'-position]//')"/>
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
                    relevant="count({$instance-ancestor-label}{$business-name}) &lt; {enofr:get-maximum-lines($source-context)}"/>
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
        <xsl:variable name="required" select="enofr:get-required($source-context)"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>
        <xsl:variable name="dateduration-format" select="enofr:get-format($source-context)"/>
        <xsl:variable name="layout-list" as="node()">
            <xsl:call-template name="dateduration-layout">
                <xsl:with-param name="variable-name" select="$name"/>
                <xsl:with-param name="driver" select="$current-driver"/>
                <xsl:with-param name="format" select="$dateduration-format"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Creating one calculated element that correspond to the concatenation of the layout ones -->
        <xsl:if test="count($layout-list//format) &gt; 1">
            <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="'if ('"/>
                    <xsl:for-each select="$layout-list//format">
                        <xsl:if test="position() != 1">
                            <xsl:value-of select="' and '"/>
                        </xsl:if>
                        <xsl:value-of select="concat('../',@variable,' != '''' ')"/>
                    </xsl:for-each>
                    <xsl:value-of select="') then '''' else (concat( '"/>
                    <xsl:choose>
                        <xsl:when test="$current-driver='DateTimeDomain'">
                            <xsl:for-each select="$layout-list//format">
                                <xsl:if test="position() != 1">
                                    <xsl:value-of select="',-,'"/>
                                </xsl:if>
                                <xsl:value-of select="concat(' ../',@variable)"/>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:when test="$dateduration-format='HH:CH'">
                            <xsl:value-of select="concat('100*(../',$layout-list//format[1]/@variable,') + ../',$layout-list//format[2]/@variable)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="'''P'''"/>
                            <xsl:for-each select="$layout-list//format[@id='Y' or @id='M' or @id='D']">
                                <xsl:value-of select="concat(', ../',@variable,',''',@id,'''')"/>
                            </xsl:for-each>
                            <xsl:if test="contains($dateduration-format,'T')">
                                <xsl:value-of select="',''T'''"/>
                            </xsl:if>
                            <xsl:for-each select="$layout-list//format[@id!='Y' and @id!='M' and @id!='D']">
                                <xsl:value-of select="concat(', ../',@variable,',''',upper-case(@id),'''')"/>
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
                <xsl:if test="$dateduration-format = 'YYYY-MM-DD'">
                    <xsl:attribute name="type" select="'xf:date'"/>
                </xsl:if>
                <xsl:if test="self::DurationDomain">
                    <xsl:attribute name="type" select="'xf:number'"/>
                </xsl:if>
                <xsl:if test="not($required = ('false()', ''))">
                    <xsl:attribute name="required" select="$required"/>
                </xsl:if>
                <xsl:if test="$relevant != ''">
                    <xsl:attribute name="relevant">
                        <xsl:if test="$instance-ancestor != ''">
                            <xsl:value-of select="concat('ancestor::',tokenize($instance-ancestor,' ')[last()],'[')"/>
                        </xsl:if>
                        <xsl:call-template name="replaceVariablesInFormula">
                            <xsl:with-param name="formula" select="$relevant"/>
                            <xsl:with-param name="calcul-aim" select="'filter'"/>
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
                        <xsl:if test="$instance-ancestor != ''">
                            <xsl:value-of select="']'"/>
                        </xsl:if>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="not($readonly = ('false()', ''))">
                    <xsl:attribute name="readonly">
                        <xsl:value-of select="'not('"/>
                        <xsl:if test="$instance-ancestor != ''">
                            <xsl:value-of select="concat('ancestor::',tokenize($instance-ancestor,' ')[last()],'[')"/>
                        </xsl:if>
                        <xsl:call-template name="replaceVariablesInFormula">
                            <xsl:with-param name="formula" select="$readonly"/>
                            <xsl:with-param name="calcul-aim" select="'filter'"/>
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
                        <xsl:if test="$instance-ancestor != ''">
                            <xsl:value-of select="']'"/>
                        </xsl:if>
                        <xsl:value-of select="')'"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="$current-driver = 'DurationDomain'">
                    <xsl:element name="xf:constraint">
                        <xsl:attribute name="value" select="concat('if (. castable as xs:integer) then (xs:integer(.)&lt;=',@maximum,' and xs:integer(.)&gt;=',@minimum,') else (.='''')')"/>
                    </xsl:element>
                </xsl:if>
            </xf:bind>
        </xsl:for-each>
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
        <xsl:param name="instance-ancestor" tunnel="yes"/>

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
            <xsl:if test="self::xf-select1 or self::xf-select">
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
        <xsl:param name="instance-ancestor" tunnel="yes"/>

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

        <xsl:variable name="current-driver" select="self::*/local-name()"/>
        <xsl:variable name="dateduration-format" select="enofr:get-format($source-context)"/>
        <xsl:variable name="layout-list" as="node()">
            <xsl:call-template name="dateduration-layout">
                <xsl:with-param name="variable-name" select="$name"/>
                <xsl:with-param name="driver" select="$current-driver"/>
                <xsl:with-param name="format" select="$dateduration-format"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:for-each select="$layout-list//format">
            <xsl:element name="{@variable}">
                <xsl:if test="position()=1 and ($label!='' or $question-label!='')">
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
                <alert>
                    <xsl:value-of select="'Quel est le message d''erreur ? Et où dois-je le mettre ?'"/>
                </alert>
                <xsl:if test="$current-driver = 'DateTimeDomain' and @unit != ''">
                    <xsl:for-each select="xs:integer(number(@minimum)) to xs:integer(number(@maximum))">
                        <item>
                            <label>
                                <xsl:value-of select="."/>
                            </label>
                            <value>
                                <xsl:value-of select="."/>
                            </value>
                        </item>
                    </xsl:for-each>
                </xsl:if>
            </xsl:element>
        </xsl:for-each>
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
            <xd:p>Template for Body for the xf-group driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//xf-group" mode="model">
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
                <xsl:value-of select="concat('*[name()=''',.,'''][$',.,'-position]//')"/>
            </xsl:for-each>
        </xsl:variable>

        <xsl:element name="{translate(name(), '-', ':')}">
            <xsl:attribute name="id" select="concat($name, '-control')"/>
            <xsl:attribute name="name" select="$name"/>
            <xsl:attribute name="bind" select="concat($name, '-bind')"/>
            <xsl:if test="$appearance != ''">
                <xsl:attribute name="appearance" select="$appearance"/>
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
            <xsl:attribute name="xxf:order" select="'label control hint help alert'"/>
            <xsl:if test="not($length = '')">
                <xsl:attribute name="xxf:maxlength" select="$length"/>
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
                    <xsl:if test="$rich-question-label or eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:label>
            </xsl:if>
            <xsl:if test="$hint != ''">
                <xf:hint ref="$form-resources/{$name}/hint">
                    <xsl:if test="eno:is-rich-content(enofr:get-hint($source-context, $languages[1]))">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:hint>
            </xsl:if>
            <xsl:if test="$help != ''">
                <xf:help ref="$form-resources/{$name}/help">
                    <xsl:if test="eno:is-rich-content(enofr:get-help($source-context, $languages[1]))">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:help>
            </xsl:if>
            <xsl:if test="$alert != ''">
                <xf:alert ref="$form-resources/{$name}/alert">
                    <xsl:if test="enofr:get-alert-level($source-context) != ''">
                        <xsl:attribute name="level" select="enofr:get-alert-level($source-context)"/>
                    </xsl:if>
                    <xsl:if test="eno:is-rich-content(enofr:get-alert($source-context, $languages[1]))">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:alert>
            </xsl:if>
            <xsl:if test="self::xf-select1 or self::xf-select">
                <xsl:if test="$appearance = 'minimal'">
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
            <xsl:if test="self::xf-select">
                <xf:action ev:event="xforms-value-changed" if="substring-after({$instance-ancestor-label}{$name},' ') ne ''">
                    <!-- if the collected variable is in a loop, instance-ancestor helps choosing the good collected variable -->
                    <xf:setvalue ref="{$instance-ancestor-label}{$name}" value="substring-after({$instance-ancestor-label}{$name},' ')"/>
                </xf:action>
            </xsl:if>
            <!-- For each element which relevance depends on this field, we erase the data if it became unrelevant -->
            <xsl:for-each select="enofr:get-relevant-dependencies($source-context)">
                <!-- if the filter is in a loop, instance-ancestor helps choosing the good filter -->
                <xf:action ev:event="xforms-value-changed"
                    if="not(xxf:evaluate-bind-property('{.}-bind','relevant'))"
                    iterate="{$instance-ancestor-label}{.}//*[not(descendant::*)]">
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
        <xsl:if test="not($suffix = '')">
            <xsl:element name="xhtml:span">
                <xsl:attribute name="class" select="'suffixe'"/>
                <xsl:copy-of select="$suffix" copy-namespaces="no"/>
            </xsl:element>
        </xsl:if>
        <xsl:if test="self::xf-select1 or self::xf-select">
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
            <xsl:attribute name="xxf:order" select="'label control hint help alert'"/>
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

        <xhtml:table name="{enofr:get-name($source-context)}">
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class" select="$css-class"/>
            </xsl:if>
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
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
        <xsl:variable name="instance-ancestor-label">
            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
            <xsl:for-each select="tokenize($instance-ancestor,' ')">
                <xsl:value-of select="concat('*[name()=''',.,'''][$',.,'-position]//')"/>
            </xsl:for-each>
        </xsl:variable>

        <xsl:apply-templates select="$table-title//xf-output" mode="model"/>
        <xhtml:table name="{$table-name}">
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class" select="$css-class"/>
            </xsl:if>
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
                <xf:repeat id="{$loop-name}" nodeset="{$instance-ancestor-label}{$loop-name}">
                    <xf:var name="{$loop-name}-position" value="position()"/>
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
            <xf:trigger id="{$loop-name}-addline" bind="{$loop-name}-addline-bind">
                <xf:label ref="$form-resources/AddLine/label"/>
                <xf:insert ev:event="DOMActivate" context="{$instance-ancestor-label}{$loop-name}-Container"
                    nodeset="{$instance-ancestor-label}{$loop-name}" position="after"
                    origin="instance('fr-form-loop-model')/{$loop-name}"/>
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
                            <xsl:with-param name="conditioning-variables" select="enofr:get-label-conditioning-variables($source-context, $languages[1])"/>
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
        <xsl:variable name="business-name" select="enofr:get-business-name($source-context)"/>
        <xsl:variable name="instance-ancestor-label">
            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
            <xsl:for-each select="tokenize($instance-ancestor,' ')">
                <xsl:value-of select="concat('*[name()=''',.,'''][$',.,'-position]//')"/>
            </xsl:for-each>
        </xsl:variable>

        <xf:repeat id="{$loop-name}" nodeset="{$instance-ancestor-label}{$loop-name}">
            <xf:var name="{$loop-name}-position" value="position()"/>
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
        <xsl:variable name="alert" select="enofr:get-alert($source-context, $languages[1])"/>
        <xsl:variable name="instance-ancestor-label">
            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
            <xsl:for-each select="tokenize($instance-ancestor,' ')">
                <xsl:value-of select="concat('*[name()=''',.,'''][$',.,'-position]//')"/>
            </xsl:for-each>
        </xsl:variable>

        <xsl:variable name="current-driver" select="self::*/local-name()"/>
        <xsl:variable name="dateduration-format" select="enofr:get-format($source-context)"/>
        <xsl:variable name="layout-list" as="node()">
            <xsl:call-template name="dateduration-layout">
                <xsl:with-param name="variable-name" select="$name"/>
                <xsl:with-param name="driver" select="$current-driver"/>
                <xsl:with-param name="format" select="$dateduration-format"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="input-format">
            <xsl:choose>
                <xsl:when test="$current-driver='DurationDomain' or $dateduration-format = 'YYYY-MM-DD'">
                    <xsl:value-of select="'xf:input'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'xf:select1'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:for-each select="$layout-list//format">
            <xsl:element name="{$input-format}">
                <xsl:attribute name="id" select="concat(@variable, '-control')"/>
                <xsl:attribute name="name" select="@variable"/>
                <xsl:attribute name="bind" select="concat(@variable, '-bind')"/>
                <xsl:if test="$input-format='xf:select1'">
                    <xsl:attribute name="appearance" select="'minimal'"/>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="$question-label !='' and $current-driver='DurationDomain'">
                        <xsl:attribute name="class" select="'question duration'"/>
                    </xsl:when>
                    <xsl:when test="$current-driver='DurationDomain'">
                        <xsl:attribute name="class" select="'duration'"/>
                    </xsl:when>
                    <xsl:when test="$question-label !=''">
                        <xsl:attribute name="class" select="'question date'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="class" select="'date'"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:attribute name="xxf:order" select="'label control hint help alert'"/>
                <xsl:if test="$current-driver = 'DurationDomain'">
                    <xsl:attribute name="xxf:maxlength" select="'2'"/>    
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
                        <xsl:if test="$rich-question-label or eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
                            <xsl:attribute name="mediatype">text/html</xsl:attribute>
                        </xsl:if>
                    </xf:label>
                </xsl:if>
                <xsl:if test="$alert != ''">
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
                        iterate="{$instance-ancestor-label}{.}//*[not(descendant::*)]">
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
            <xsl:if test="$current-driver = 'DurationDomain'">
                <xsl:element name="xhtml:span">
                    <xsl:attribute name="class" select="'double-duration-suffix'"/>
                    <xsl:value-of select="@unit"/>
                </xsl:element>                
            </xsl:if>
        </xsl:for-each>
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

        <xsl:variable name="multiple-layout" select="string-length($format) &gt; 4" as="xs:boolean"/>

        <formats>
            <xsl:choose>
                <xsl:when test="$format='YYYY-MM-DD'">
                    <format id="" unit="" minimum="" maximum="" variable="{$variable-name}"/>
                </xsl:when>
                <xsl:when test="$format='HH:CH'">
                    <format id="H" unit="heures" minimum="20" maximum="59" variable="{$variable-name}-layout-H"/>
                    <format id="CH" unit="centièmes" minimum="0" maximum="99" variable="{$variable-name}-layout-CH"/>
                </xsl:when>
                <xsl:when test="$driver = 'DateTimeDomain'">
                    <!-- The order is different between date dans duration -->
                    <xsl:if test="contains($format,'D')">
                        <format id="D" unit="jours" minimum="1" maximum="31">
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout"><xsl:value-of select="'-layout-D'"/></xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'M') and not(contains(substring-before($format,'M'),'T'))">
                        <format id="M" unit="mois" minimum="1" maximum="12">
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout"><xsl:value-of select="'-layout-M'"/></xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'Y')">
                        <format id="Y" unit="ans" minimum="1970" maximum="{year-from-date(current-date())}">
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout"><xsl:value-of select="'-layout-Y'"/></xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:if test="contains($format,'Y')">
                        <format id="Y" unit="ans" minimum="0" maximum="99">
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout"><xsl:value-of select="'-layout-Y'"/></xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'M') and not(contains(substring-before($format,'M'),'T'))">
                        <format id="M" unit="mois" minimum="0" maximum="11">
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout"><xsl:value-of select="'-layout-M'"/></xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'D')">
                        <format id="D" unit="jours" minimum="0" maximum="30">
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout"><xsl:value-of select="'-layout-D'"/></xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'H')">
                        <format id="H" unit="heures" minimum="0" maximum="23">
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout"><xsl:value-of select="'-layout-H'"/></xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'T') and contains(substring-after($format,'T'),'M')">
                        <format id="m" unit="minutes" minimum="0" maximum="59">
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout"><xsl:value-of select="'-layout-m'"/></xsl:if>
                            </xsl:attribute>
                        </format>
                    </xsl:if>
                    <xsl:if test="contains($format,'S')">
                        <format id="S" unit="secondes" minimum="0" maximum="59">
                            <xsl:attribute name="variable">
                                <xsl:value-of select="$variable-name"/>
                                <xsl:if test="$multiple-layout"><xsl:value-of select="'-layout-S'"/></xsl:if>
                            </xsl:attribute>
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
                                <xsl:with-param name="formula" select="enofr:get-conditioning-variable-formula($source-context,$conditioning-variable)"/>
                                <xsl:with-param name="calcul-aim" select="'label'"/>
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
                                    <xsl:value-of select="concat(.,'[$',.,'-position]//')"/>
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
            <xd:p>Template named:replaceVariablesInFormula.</xd:p>
            <xd:p>It replaces variables in a all formula (Filter, ConsistencyCheck, CalculatedVariable).</xd:p>
            <xd:p>"variable" -> "variableBusinessName"</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="replaceVariablesInFormula">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="formula"/>
        <xsl:param name="calcul-aim"/>
        <xsl:param name="instance-ancestor"/>
        <xsl:param name="variables" as="node()"/>

        <xsl:variable name="instance-group" select="tokenize($instance-ancestor,' ')[last()]"/>

        <xsl:choose>
            <xsl:when test="$variables/Variable">
                <xsl:variable name="current-variable" select="$variables/Variable[1]"/>
                <xsl:variable name="variable-business-name">
                    <xsl:variable name="variable-ancestors" select="enofr:get-variable-business-ancestors($source-context,$current-variable)"/>
                    <xsl:variable name="business-name" select="enofr:get-variable-business-name($source-context,$current-variable)"/>
                    <xsl:choose>
                        <!-- the calculation or the variable directly depends on the root : the variable is directly called from the root -->
                        <xsl:when test="not($instance-group) or not($variable-ancestors)">
                            <xsl:value-of select="concat('//',$business-name)"/>
                        </xsl:when>
                        <!-- the calculation and the variable depend on loops and the calculation is a label -->
                        <!-- the variable is called with its whole absolute address -->
                        <xsl:when test="$calcul-aim = 'label'">
                            <xsl:value-of select="'instance(''fr-form-instance'')//'"/>
                            <xsl:for-each select="tokenize($variable-ancestors,' ')">
                                <xsl:if test=". = tokenize($instance-ancestor,' ')">
                                    <xsl:value-of select="concat(.,'[\$',.,'-position]//')"/>
                                </xsl:if>
                            </xsl:for-each>
                            <xsl:value-of select="$business-name"/>
                        </xsl:when>
                        <!-- the calculation and the variable depend on loops and the calculation is a calculated variable -->
                        <!-- the variable is called with "ancestor::" before the last ancestor -->
                        <xsl:when test="$calcul-aim = 'calculation'">
                            <xsl:value-of select="concat('ancestor::',tokenize($variable-ancestors,' ')[last()],'//',$business-name)"/>
                        </xsl:when>
                        <!-- the calculation and the variable depend on loops and the calculation is a filter or a check and the variable depends on the calculation's loop -->
                        <!-- the variable is called as a descendant of the filter or check instance -->
                        <xsl:when test="$instance-group = tokenize($variable-ancestors,' ')">
                            <xsl:value-of select="concat('descendant::',$business-name)"/>
                        </xsl:when>
                        <!-- the calculation and the variable depend on loops and the calculation is a filter or a check and the variable does not depend on the calculation's loop -->
                        <!-- the variable is called as a descendant of the nearest possible ancestor -->
                        <xsl:otherwise>
                            <xsl:value-of select="concat('ancestor::*[descendant::',$business-name,'][1]/descendant::',$business-name)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:call-template name="replaceVariablesInFormula">
                    <xsl:with-param name="formula" select="replace($formula,
                                                                   concat($conditioning-variable-begin,$current-variable,$conditioning-variable-end),
                                                                   $variable-business-name)"/>
                    <xsl:with-param name="calcul-aim" select="$calcul-aim"/>
                    <xsl:with-param name="variables" as="node()">
                        <Variables>
                            <xsl:copy-of select="$variables/Variable[position() != 1 ]"/>
                        </Variables>
                    </xsl:with-param>
                    <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formula"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
