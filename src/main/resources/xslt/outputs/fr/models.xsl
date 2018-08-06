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
            <xd:p>- ResourceBind : to write the few binds of the elements of the resource instance which are calculated</xd:p>
            <xd:p>- Body : to write the fields</xd:p>
            <xd:p>- Model : to write model elements of the instance which could be potentially added by the user in the instance</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Form" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
            as="xs:string +"/>
        <xhtml:html>
            <xhtml:head>
                <xhtml:title>
                    <xsl:value-of select="enofr:get-form-title($source-context, $languages[1])"/>
                </xhtml:title>
                <xhtml:link rel="stylesheet"
                    href="/{$properties//Css/Folder}/{$properties//Css/Common}"/>
                <xf:model id="fr-form-model" xxf:expose-xpath-types="true"
                    xxf:noscript-support="true">

                    <!-- Main instance, it contains the elements linked to fields, and which will be stored when the form will be submitted -->
                    <xf:instance id="fr-form-instance">
                        <form>
                            <xsl:apply-templates select="eno:child-fields($source-context)"
                                mode="source">
                                <xsl:with-param name="driver"
                                    select="eno:append-empty-element('Instance', .)" tunnel="yes"/>
                            </xsl:apply-templates>
                        </form>
                    </xf:instance>

                    <xf:instance id="fr-form-loop-model">
                        <LoopModels>
                            <xsl:apply-templates select="eno:child-fields($source-context)"
                                mode="source">
                                <xsl:with-param name="driver"
                                    select="eno:append-empty-element('Model', .)" tunnel="yes"/>
                            </xsl:apply-templates>
                        </LoopModels>
                    </xf:instance>

                    <!-- Bindings -->
                    <xf:bind id="fr-form-instance-binds" ref="instance('fr-form-instance')">
                        <xsl:apply-templates select="eno:child-fields($source-context)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="eno:append-empty-element('Bind', .)" tunnel="yes"/>
                            <!-- the instance ancestor is used for having the absolute, not relative, address of the element -->
                            <xsl:with-param name="instance-ancestor"
                                select="'instance(''fr-form-instance'')//'" tunnel="yes"/>
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
                                    <xsl:value-of select="enofr:get-form-title($source-context, .)"
                                    />
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
                                    <xsl:apply-templates select="eno:child-fields($source-context)"
                                        mode="source">
                                        <xsl:with-param name="driver"
                                            select="eno:append-empty-element('Resource', $driver)"
                                            tunnel="yes"/>
                                        <xsl:with-param name="language" select="$language" tunnel="yes"/>
                                    </xsl:apply-templates>
                                    <AddLine>
                                        <label><xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/AddLine"/></label>
                                    </AddLine>
                                </resource>
                            </xsl:for-each>
                        </resources>
                    </xf:instance>

                    <!-- Bind of resources for the ones that are dynamic (text depends from the answer to another question) -->
                    <xf:bind id="fr-form-resources-bind" ref="instance('fr-form-resources')">
                        <xsl:variable name="driver" select="."/>
                        <xsl:for-each select="$languages">
                            <xf:bind id="bind-resource-{.}"
                                name="resource-{.}"
                                ref="resource[@xml:lang='{.}']">
                                <xsl:apply-templates select="eno:child-fields($source-context)"
                                    mode="source">
                                    <xsl:with-param name="driver"
                                        select="eno:append-empty-element('ResourceBind', $driver)"
                                        tunnel="yes"/>
                                    <xsl:with-param name="language" select="." tunnel="yes"/>
                                    <!-- the instance ancestor is used for having the absolute, not relative, address of the element -->
                                    <xsl:with-param name="instance-ancestor"
                                        select="'instance(''fr-form-instance'')//'" tunnel="yes"/>
                                </xsl:apply-templates>
                            </xf:bind>
                        </xsl:for-each>
                    </xf:bind>

                    <!-- Utility instances for services -->
                    <xf:instance id="fr-service-request-instance" xxf:exclude-result-prefixes="#all">
                        <request/>
                    </xf:instance>

                    <xf:instance id="fr-service-response-instance"
                        xxf:exclude-result-prefixes="#all">
                        <response/>
                    </xf:instance>

                </xf:model>
            </xhtml:head>
            <xhtml:body>
                <fr:view>
                    <!-- Writing the main body -->
                    <fr:body>
                        <xsl:apply-templates select="eno:child-fields($source-context)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="eno:append-empty-element('Body', .)" tunnel="yes"/>
                            <xsl:with-param name="languages" select="$languages" tunnel="yes"/>
                            <!-- the instance ancestor is used for having the absolute, not relative, address of the element -->
                            <xsl:with-param name="instance-ancestor"
                                select="'instance(''fr-form-instance'')//'" tunnel="yes"/>
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
            <xsl:with-param name="question-calculate-label" select="eno:serialize(enofr:get-calculate-text($source-context,$language,$instance-ancestor))" tunnel="yes"/>
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
        <xsl:if test="enofr:get-minimum-lines($source-context) &lt; enofr:get-maximum-lines($source-context)">
            <xsl:element name="{enofr:get-business-name($source-context)}-AddLine"/>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Special template for Instance for the DoubleDuration driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Instance//DoubleDuration" mode="model" >
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>

        <xsl:element name="{$name}"/>
        <xsl:element name="{replace($name,'-','-A-')}"/>
        <xsl:element name="{replace($name,'-','-B-')}"/>
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
                <xsl:with-param name="driver" select="eno:append-empty-element('Instance', .)"
                    tunnel="yes"/>
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
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$relevant"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$calculate != ''">
                <xsl:attribute name="calculate">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$calculate"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-variable-calculation-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$readonly"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
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
                        <xsl:if test="enofr:get-readonly-ancestors($source-context) != ''">
                            <xsl:for-each select="enofr:get-readonly-ancestors($source-context)">
                                <xsl:value-of select="'not('"/>
                                <xsl:call-template name="replaceGroupsInFormula">
                                    <xsl:with-param name="formula" select="."/>
                                    <xsl:with-param name="position" select="1"/>
                                </xsl:call-template>
                                <xsl:value-of select="') or '"/>
                                <!--<xsl:value-of select="concat('not(',.,') or ')"/>-->
                            </xsl:for-each>
                        </xsl:if>
                        <xsl:call-template name="replaceVariablesInFormula">
                            <xsl:with-param name="formula" select="$constraint"/>
                            <xsl:with-param name="variables" as="node()">
                                <Variables>
                                    <xsl:for-each select="tokenize(enofr:get-control-variables($source-context),' ')">
                                        <xsl:sort select="string-length(.)" order="descending"/>
                                        <Variable><xsl:value-of select="."/></Variable>
                                    </xsl:for-each>
                                </Variables>
                            </xsl:with-param>
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
    <xsl:template match="Bind//xf-input" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="required" select="enofr:get-required($source-context)"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="type" select="enofr:get-type($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>
        <!--<xsl:variable name="constraint" select="enofr:get-constraint($source-context)"/>-->
        <xsl:variable name="format-constraint" select="enofr:get-format-constraint($source-context)"/>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not($required = ('false()', ''))">
                <xsl:attribute name="required" select="$required"/>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$relevant"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$type = 'date'">
                <xsl:attribute name="type" select="concat('xf:', $type)"/>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$readonly"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                    </xsl:call-template>
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
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$relevant"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$readonly"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
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
        <xf:bind id="{$name}-bind" name="{$name}" nodeset="{$name}-Container/{$name}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
                <!-- the absolute address of the element in enriched for RowLoop and QuestionLoop, for which several instances are possible -->
                <xsl:with-param name="instance-ancestor"
                    select="concat($instance-ancestor,'*[name()=''',$business-name,''' and position()= $',$business-name,'-position ]//')"
                    tunnel="yes"/>
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

        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
        <xsl:if test="enofr:get-minimum-lines($source-context) &lt; enofr:get-maximum-lines($source-context)">
            <xf:bind id="{$business-name}-addline-bind" ref="{$business-name}-AddLine"
                relevant="count({$instance-ancestor}{$business-name}) &lt; {enofr:get-maximum-lines($source-context)}"/>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Bind for the DoubleDuration driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Bind//DoubleDuration" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="required" select="enofr:get-required($source-context)"/>
        <xsl:variable name="relevant" select="enofr:get-relevant($source-context)"/>
        <xsl:variable name="readonly" select="enofr:get-readonly($source-context)"/>

        <!-- Creating one element that correspond to the concatenation of the two ones -->
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:attribute name="calculate" select="
                        concat('if (not(instance(''fr-form-instance'')//', replace($name, '-', '-A-'),
                        ' castable as xs:integer or instance(''fr-form-instance'')//', replace($name, '-', '-B-'),
                        ' castable as xs:integer)) then '''' else (100*number(if (instance(''fr-form-instance'')//', replace($name, '-', '-A-'),
                        ' castable as xs:integer) then instance(''fr-form-instance'')//', replace($name, '-', '-A-'),
                        ' else 0)+number(if (instance(''fr-form-instance'')//', replace($name, '-', '-B-'),
                        ' castable as xs:integer) then instance(''fr-form-instance'')//', replace($name, '-', '-B-'),
                        ' else 0))')"
            />
        </xf:bind>
        <xsl:variable name="nameA" select="replace($name, '-', '-A-')"/>
        <xf:bind id="{$nameA}-bind" name="{$nameA}" ref="{$nameA}">
            <xsl:if test="not($required = ('false()', ''))">
                <xsl:attribute name="required" select="$required"/>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$relevant"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$readonly"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:value-of select="')'"/>
                </xsl:attribute>
            </xsl:if>
        </xf:bind>
        <xsl:variable name="nameB" select="replace($name, '-', '-B-')"/>
        <xf:bind id="{$nameB}-bind" name="{$nameB}" ref="{$nameB}">
            <xsl:if test="not($required = ('false()', ''))">
                <xsl:attribute name="required" select="$required"/>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$relevant"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-hideable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="'not('"/>
                    <xsl:call-template name="replaceVariablesInFormula">
                        <xsl:with-param name="formula" select="$readonly"/>
                        <xsl:with-param name="variables" as="node()">
                            <Variables>
                                <xsl:for-each select="tokenize(enofr:get-deactivatable-command-variables($source-context),' ')">
                                    <xsl:sort select="string-length(.)" order="descending"/>
                                    <Variable><xsl:value-of select="."/></Variable>
                                </xsl:for-each>
                            </Variables>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:value-of select="')'"/>
                </xsl:attribute>
            </xsl:if>
        </xf:bind>
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
        <xsl:param name="question-calculate-label" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="label" select="eno:serialize(enofr:get-label($source-context, $language))"/>
        <xsl:variable name="hint" select="eno:serialize(enofr:get-hint($source-context, $language))"/>
        <xsl:variable name="help" select="eno:serialize(enofr:get-help($source-context, $language))"/>
        <xsl:variable name="alert" select="eno:serialize(enofr:get-alert($source-context, $language))"/>

        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:if test="$label!='' or $question-label!=''">
                <label>
                    <xsl:choose>
                        <xsl:when test="$question-calculate-label != ''">
                            <xsl:value-of select="'custom label'"/>
                        </xsl:when>
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
                        <xsl:when test="enofr:get-calculate-text($source-context,$language,$instance-ancestor) != ''">
                            <xsl:value-of select="'custom label'"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$label"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </label>
            </xsl:if>
            <xsl:if test="$hint">
                <hint>
                    <xsl:value-of select="$hint"/>
                </hint>
            </xsl:if>
            <xsl:if test="$help">
                <help>
                    <xsl:value-of select="$help"/>
                </help>
            </xsl:if>
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
            <xd:p>Template for Resource for the drivers xf-select and xf-select1.</xd:p>
            <xd:p>It builds the resources by using different enofr functions then the process goes on within the created resource.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Resource//*[starts-with(name(), 'xf-select') and not(ancestor::ResourceItem)]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:param name="question-label" tunnel="yes"/>
        <xsl:param name="question-calculate-label" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="label" select="eno:serialize(enofr:get-label($source-context, $language))"/>
        <xsl:variable name="hint" select="eno:serialize(enofr:get-hint($source-context, $language))"/>
        <xsl:variable name="help" select="eno:serialize(enofr:get-help($source-context, $language))"/>
        <xsl:variable name="alert" select="eno:serialize(enofr:get-alert($source-context, $language))"/>

        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:if test="$label!='' or $question-label!=''">
                <label>
                    <xsl:choose>
                        <xsl:when test="$question-calculate-label != ''">
                            <xsl:value-of select="'custom label'"/>
                        </xsl:when>
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
                        <xsl:when test="enofr:get-calculate-text($source-context,$language,$instance-ancestor) != ''">
                            <xsl:value-of select="'custom label'"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$label"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </label>
            </xsl:if>
            <xsl:if test="$hint!=''">
                <hint>
                    <xsl:value-of select="$hint"/>
                </hint>
            </xsl:if>
            <xsl:if test="$help!=''">
                <help>
                    <xsl:value-of select="$help"/>
                </help>
            </xsl:if>
            <xsl:if test="$alert!=''">
                <alert>
                    <xsl:choose>
                        <xsl:when test="enofr:get-calculate-text($source-context,$language,$instance-ancestor) != ''">
                            <xsl:value-of select="'custom alert'"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$alert"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </alert>
            </xsl:if>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('ResourceItem', .)" tunnel="yes"/>
            </xsl:apply-templates>
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
                    <xsl:choose>
                        <xsl:when test="enofr:get-calculate-text($source-context,$language,$instance-ancestor) != ''">
                            <xsl:value-of select="'custom alert'"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$alert"/>
                        </xsl:otherwise>
                    </xsl:choose>
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
                        <xsl:value-of select="concat('&lt;img src=&quot;',$image,'&quot; title=&quot;',eno:serialize(enofr:get-label($source-context, $language)),'&quot; /&gt;')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat('&lt;img src=&quot;/',$properties//Images/Folder,'/',$image,'&quot; title=&quot;',eno:serialize(enofr:get-label($source-context, $language)),'&quot; /&gt;')"/>
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
            <xd:p>Template for Resource for DoubleDuration driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Resource//DoubleDuration[not(ancestor::ResourceItem)]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label" select="eno:serialize(enofr:get-label($source-context, $language))"/>
        <xsl:variable name="hint" select="eno:serialize(enofr:get-hint($source-context, $language))"/>

        <xsl:element name="{replace($name,'-','-A-')}">
            <xsl:if test="$label!=''">
                <label>
                    <xsl:value-of select="$label"/>
                </label>
            </xsl:if>
            <xsl:if test="$hint!=''">
                <hint>
                    <xsl:value-of select="$hint"/>
                </hint>
            </xsl:if>
            <xsl:for-each select="20 to 60">
                <item>
                    <label>
                        <xsl:value-of select="."/>
                    </label>
                    <value>
                        <xsl:value-of select="."/>
                    </value>
                </item>
            </xsl:for-each>
        </xsl:element>
        <xsl:element name="{replace($name,'-','-B-')}">
            <xsl:if test="$label!=''">
                <label>
                    <xsl:value-of select="$label"/>
                </label>
            </xsl:if>
            <xsl:if test="$hint!=''">
                <hint>
                    <xsl:value-of select="$hint"/>
                </hint>
            </xsl:if>
            <xsl:for-each select="0 to 99">
                <xsl:variable name="item-label">
                    <xsl:choose>
                        <xsl:when test="number(.) &lt; 10">
                            <xsl:value-of select="concat('0', string(.))"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="string(.)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <item>
                    <label>
                        <xsl:value-of select="$item-label"/>
                    </label>
                    <value>
                        <xsl:value-of select="$item-label"/>
                    </value>
                </item>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for ResourceBind for the drivers.</xd:p>
            <xd:p>If the label or the alert is dynamic, it creates a bind.</xd:p>
            <xd:p>The process goes on next to the created bind.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="ResourceBind//RowLoop | ResourceBind//QuestionLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="business-name" select="enofr:get-business-name($source-context)"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
            <!-- the absolute address of the element in enriched for RowLoop and QuestionLoop, for which several instances are possible -->
            <xsl:with-param name="instance-ancestor"
                select="concat($instance-ancestor,'*[name()=''',$business-name,''' and position()= $',$business-name,'-position ]//')"
                tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for ResourceBind for the drivers.</xd:p>
            <xd:p>If the label or the alert is dynamic, it creates a bind.</xd:p>
            <xd:p>The process goes on next to the created bind.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="ResourceBind//*" mode="model" priority="-1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:param name="question-calculate-label" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="calculate-label" select="enofr:get-calculate-text($source-context,$language,$instance-ancestor)"/>

        <xsl:if test="$calculate-label != '' or ($question-calculate-label != '' and (self::xf-input or self::xf-textarea or self::xf-select1 or self::xf-select)) ">
            <xsl:variable name="name" select="enofr:get-name($source-context)"/>
            <xf:bind id="{$name}-resource-{$language}-bind" name="{$name}-{$language}-resource"
                ref="{$name}">
                <xf:bind id="{$name}-resource-{$language}-bind-label"
                    name="{$name}-{$language}-resource-label" ref="label">
                    <xsl:choose>
                        <xsl:when test="$question-calculate-label">
                            <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
                            <xsl:choose>
                                <xsl:when test="$css-class != ''">
                                    <xsl:attribute name="calculate" select="replace($question-calculate-label,'block question',concat('block question ',$css-class))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="calculate" select="$question-calculate-label"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="calculate" select="$calculate-label"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xf:bind>
            </xf:bind>
        </xsl:if>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for ResourceBind for the driver ConsistencyCheck.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="ResourceBind//ConsistencyCheck" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>

        <xsl:variable name="calculate-alert" select="enofr:get-calculate-text($source-context,$language,$instance-ancestor)"/>

        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:if test="$calculate-alert != ''">
            <xf:bind id="{$name}-resource-{$language}-bind" name="{$name}-{$language}-resource" ref="{$name}">
                <xf:bind id="{$name}-resource-{$language}-bind-alert"
                    name="{$name}-{$language}-resource-alert" ref="alert"
                    calculate="{$calculate-alert}"/>
            </xf:bind>
        </xsl:if>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the Module driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//Module" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <fr:section id="{$name}-control" bind="{$name}-bind" name="{$name}">
            <xf:label ref="$form-resources/{$name}/label"/>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </fr:section>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the SubModule driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//SubModule" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label" select="enofr:get-label($source-context, $languages[1])"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>

        <xhtml:div>
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class" select="$css-class"/>
            </xsl:if>
            <xsl:if test="not($label = '')">
                <xhtml:h3>
                    <xf:output id="{$name}-control" bind="{$name}-bind">
                        <xf:label ref="$form-resources/{$name}/label">
                            <xsl:if test="$css-class != ''">
                                <xsl:attribute name="class" select="$css-class"/>
                            </xsl:if>
                            <xsl:if test="eno:is-rich-content($label)">
                                <xsl:attribute name="mediatype">text/html</xsl:attribute>
                            </xsl:if>
                        </xf:label>
                    </xf:output>
                </xhtml:h3>
            </xsl:if>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:div>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the Group driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//Group" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label" select="enofr:get-label($source-context, $languages[1])"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>

        <xhtml:div>
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class" select="$css-class"/>
            </xsl:if>
            <xsl:if test="not($label = '')">
                <xhtml:h4>
                    <xf:output id="{$name}-control" bind="{$name}-bind">
                        <xf:label ref="$form-resources/{$name}/label">
                            <xsl:if test="$css-class != ''">
                                <xsl:attribute name="class" select="$css-class"/>
                            </xsl:if>
                            <xsl:if test="eno:is-rich-content($label)">
                                <xsl:attribute name="mediatype">text/html</xsl:attribute>
                            </xsl:if>
                        </xf:label>
                    </xf:output>
                </xhtml:h4>
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
        <xsl:param name="rich-question-label" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="appearance" select="enofr:get-appearance($source-context)"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>
        <xsl:variable name="length" select="enofr:get-length($source-context)"/>
        <xsl:variable name="suffix" select="enofr:get-suffix($source-context, $languages[1])"/>
        <xsl:variable name="label" select="eno:serialize(enofr:get-label($source-context, $languages[1]))"/>
        <xsl:variable name="hint" select="eno:serialize(enofr:get-hint($source-context, $languages[1]))"/>
        <xsl:variable name="help" select="eno:serialize(enofr:get-help($source-context, $languages[1]))"/>
        <xsl:variable name="alert" select="eno:serialize(enofr:get-alert($source-context, $languages[1]))"/>

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
            <xsl:if test="not($length='')">
                <xsl:attribute name="xxf:maxlength" select="$length"/>
            </xsl:if>
            <xsl:if test="$label != '' or $question-label!= ''">
                <xf:label ref="$form-resources/{$name}/label">
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
                    <xf:label ref="label">
                        <xsl:apply-templates select="eno:child-fields($source-context)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="eno:append-empty-element('Rich-Body', .)" tunnel="yes"/>
                        </xsl:apply-templates>
                    </xf:label>
                    <xf:value ref="value"/>
                </xf:itemset>
            </xsl:if>
            <!-- In this select case, if there is still something after a space in the current value, that means that 2 boxes are checked.
            We replace the value with what was after the space and that corresponds to the value of the last checked box.
            This unchecks the first box that was checked -->
            <xsl:if test="self::xf-select">
                <xf:action ev:event="xforms-value-changed"
                    if="substring-after({$instance-ancestor}{$name},' ') ne ''">
                    <!-- if the collected variable is in a loop, instance-ancestor helps choosing the good collected variable -->
                    <xf:setvalue ref="{$instance-ancestor}{$name}"
                        value="substring-after({$instance-ancestor}{$name},' ')"/>
                </xf:action>
            </xsl:if>
            <!-- For each element which relevance depends on this field, we erase the data if it became unrelevant -->
            <xsl:for-each select="enofr:get-relevant-dependencies($source-context)">
                <!-- if the filter is in a loop, instance-ancestor helps choosing the good filter -->
                <xf:action ev:event="xforms-value-changed"
                    if="not(xxf:evaluate-bind-property('{.}-bind','relevant'))"
                    iterate="{$instance-ancestor}{.}//*[not(descendant::*)]">
                    <xf:setvalue ref="." value="''"/>
                </xf:action>
            </xsl:for-each>
            <!-- For each element which readonly status depends on this field, we erase the data if it became readonly -->
            <!-- change in the point of view : we keep then now -->
            <!--            <xsl:for-each select="enofr:get-readonly-dependencies($source-context)">
                <xf:action ev:event="xforms-value-changed"
                    if="{concat('xxf:evaluate-bind-property(''',.,'-bind'',''readonly'')')}"
                    iterate="{concat($instance-ancestor,.,'//*[not(descendant::*)]')}">
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
        <xsl:variable name="alert" select="eno:serialize(enofr:get-label($source-context, $languages[1]))"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>

        <xsl:element name="xf:output">
            <xsl:attribute name="id" select="concat($name, '-control')"/>
            <xsl:attribute name="name" select="$name"/>
            <xsl:attribute name="bind" select="concat($name, '-bind')"/>
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class" select="$css-class"/>
            </xsl:if>
            <xsl:attribute name="xxf:order" select="'label control hint help alert'"/>
            <xf:alert ref="$form-resources/{$name}/alert">
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
                        <xsl:apply-templates
                            select="enofr:get-header-line($source-context, position())"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="$ancestors//*[not(child::*) and not(name() = 'driver')]"
                                tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xsl:for-each>
            </xhtml:thead>
            <xhtml:tbody>
                <xsl:for-each select="enofr:get-body-lines($source-context)">
                    <xhtml:tr>
                        <xsl:apply-templates
                            select="enofr:get-body-line($source-context, position())" mode="source">
                            <xsl:with-param name="driver"
                                select="$ancestors//*[not(child::*) and not(name() = 'driver')]"
                                tunnel="yes"/>
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
                        <xsl:apply-templates
                            select="enofr:get-header-line($source-context, position())"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="$ancestors//*[not(child::*) and not(name() = 'driver')]"
                                tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xsl:for-each>
            </xhtml:thead>
            <xhtml:tbody>
                <!-- if the loop is in a loop, instance-ancestor helps choosing the good ancestor loop instance -->
                <xf:repeat id="{$loop-name}" nodeset="{$instance-ancestor}{$loop-name}">
                    <xf:var name="{$loop-name}-position" value="position()"/>
                    <!-- the table has a repeated zone that may have more than one line -->
                    <xsl:for-each select="enofr:get-body-lines($source-context)">
                        <xhtml:tr>
                            <xsl:apply-templates select="enofr:get-body-line($source-context, position())" mode="source">
                                <xsl:with-param name="driver"
                                    select="$ancestors//*[not(child::*) and not(name() = 'driver')]"
                                    tunnel="yes"/>
                                <!-- the absolute address of the element in enriched for TableLoop, for which several instances of RowLoop are possible -->
                                <xsl:with-param name="instance-ancestor"
                                    select="concat($instance-ancestor,'*[name()=''',$loop-name,''' and position()= $',$loop-name,'-position ]//')"
                                    tunnel="yes"/>
                            </xsl:apply-templates>
                        </xhtml:tr>
                    </xsl:for-each>
                </xf:repeat>
            </xhtml:tbody>
        </xhtml:table>

        <xsl:variable name="max-lines" select="enofr:get-maximum-lines($source-context)"/>

        <xsl:if test="not($max-lines != '') or $max-lines &gt; enofr:get-minimum-lines($source-context)">
            <xf:trigger>
                <xsl:if test="$max-lines != ''">
                    <xsl:attribute name="id" select="concat($loop-name,'-addline')"/>
                    <xsl:attribute name="bind" select="concat($loop-name,'-addline-bind')"/>
                </xsl:if>
                <xf:label ref="$form-resources/AddLine/label"/>
                <xf:insert ev:event="DOMActivate" context="{$instance-ancestor}{$loop-name}-Container"
                    nodeset="{$instance-ancestor}{$loop-name}"
                    position="after"
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

        <xhtml:th colspan="{enofr:get-colspan($source-context)}"
            rowspan="{enofr:get-rowspan($source-context)}">
            <xsl:if
                test="$depth != '1' and $depth != ''">
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
    <xsl:template match="*[name() = ('Instance', 'Bind', 'Resource')]//*[name() = ('Cell')]"
        mode="model">
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

        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label" select="enofr:get-label($source-context, $languages)"/>
        <xsl:variable name="css-class" select="enofr:get-css-class($source-context)"/>

        <xhtml:td colspan="{enofr:get-colspan($source-context)}" rowspan="{enofr:get-rowspan($source-context)}">
            <xf:output id="{$name}-control" bind="{$name}-bind">
                <xsl:if test="$css-class != ''">
                    <xsl:attribute name="class" select="$css-class"/>
                </xsl:if>
                <xf:label ref="$form-resources/{$name}/label">
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
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="business-name" select="enofr:get-business-name($source-context)"/>

        <xf:repeat id="{$name}" nodeset="{$instance-ancestor}{$name}">
            <xf:var name="{$name}-position" value="position()"/>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
                <!-- the absolute address of the element in enriched for Loops, for which several instances are possible -->
                <xsl:with-param name="instance-ancestor"
                    select="concat($instance-ancestor,'*[name()=''',$business-name,''' and position()= $',$business-name,'-position ]//')"
                    tunnel="yes"/>
            </xsl:apply-templates>
        </xf:repeat>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the DoubleDuration driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//DoubleDuration" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="appearance" select="enofr:get-appearance($source-context)"/>
        <xsl:variable name="length" select="enofr:get-length($source-context)"/>
        <xsl:variable name="name" select="replace(enofr:get-name($source-context), '-', '-A-')"/>

        <xsl:element name="xf:select1">
            <xsl:attribute name="id" select="concat($name, '-control')"/>
            <xsl:attribute name="bind" select="concat($name, '-bind')"/>
            <xsl:if test="$appearance != ''">
                <xsl:attribute name="appearance" select="$appearance"/>
            </xsl:if>
            <xsl:attribute name="class" select="'double-duration'"/>
            <xsl:attribute name="xxf:order" select="'label control hint help alert'"/>
            <xsl:if test="not($length = '')">
                <xsl:attribute name="xxf:maxlength" select="$length"/>
            </xsl:if>
            <xsl:if test="enofr:get-label($source-context, $languages[1])/node()">
                <xf:label ref="$form-resources/{$name}/label">
                    <xsl:if test="eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:label>
            </xsl:if>
            <xsl:if test="enofr:get-hint($source-context, $languages[1])/node()">
                <xf:hint ref="$form-resources/{$name}/hint">
                    <xsl:if test="eno:is-rich-content(enofr:get-hint($source-context, $languages[1]))">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:hint>
            </xsl:if>
            <xf:item>
                <xf:label/>
                <xf:value/>
            </xf:item>
            <xf:itemset ref="$form-resources/{$name}/item">
                <xf:label ref="label"/>
                <xf:value ref="value"/>
            </xf:itemset>
        </xsl:element>
        <xsl:element name="xhtml:span">
            <xsl:attribute name="class" select="'double-duration-suffix'"/>
            <xsl:text>heure(s)</xsl:text>
        </xsl:element>

        <xsl:variable name="name" select="replace(enofr:get-name($source-context), '-', '-B-')"/>
        <xsl:element name="xf:select1">
            <xsl:attribute name="id" select="concat($name, '-control')"/>
            <xsl:attribute name="bind" select="concat($name, '-bind')"/>
            <xsl:if test="$appearance != ''">
                <xsl:attribute name="appearance" select="$appearance"/>
            </xsl:if>
            <xsl:attribute name="class" select="'double-duration'"/>
            <xsl:attribute name="xxf:order" select="'label control hint help alert'"/>
            <xsl:if test="$length">
                <xsl:attribute name="xxf:maxlength" select="$length"/>
            </xsl:if>
            <xsl:if test="enofr:get-label($source-context, $languages[1])/node()">
                <xf:label ref="$form-resources/{$name}/label">
                    <xsl:if test="eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:label>
            </xsl:if>
            <xsl:if test="enofr:get-hint($source-context, $languages[1])/node()">
                <xf:hint ref="$form-resources/{$name}/hint">
                    <xsl:if test="eno:is-rich-content(enofr:get-hint($source-context, $languages[1]))">
                        <xsl:attribute name="mediatype">text/html</xsl:attribute>
                    </xsl:if>
                </xf:hint>
            </xsl:if>
            <xf:item>
                <xf:label/>
                <xf:value/>
            </xf:item>
            <xf:itemset ref="$form-resources/{$name}/item">
                <xf:label ref="label"/>
                <xf:value ref="value"/>
            </xf:itemset>
        </xsl:element>
        <xsl:element name="xhtml:span">
            <xsl:attribute name="class" select="'double-duration-suffix'"/>
            <xsl:text>centième(s)</xsl:text>
        </xsl:element>
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
        <xd:desc>
            <xd:p>Template named:replaceVariablesInFormula.</xd:p>
            <xd:p>It replaces variables in a all formula (Filter, ConsistencyCheck, CalculatedVariable).</xd:p>
            <xd:p>"variable" -> "variableBusinessName"</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="replaceVariablesInFormula">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="formula"/>
        <xsl:param name="variables" as="node()"/>
        
        <xsl:choose>
            <xsl:when test="$variables/Variable">
                <xsl:variable name="variable-business-name" select="enofr:get-variable-business-name($source-context,$variables/Variable[1])"/>
                <xsl:call-template name="replaceVariablesInFormula">
                    <xsl:with-param name="formula" select="replace($formula,$variables/Variable[1],$variable-business-name)"/>
                    <xsl:with-param name="variables" as="node()">
                        <Variables>
                            <xsl:copy-of select="$variables/Variable[position() != 1 ]"/>
                        </Variables>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="replaceGroupsInFormula">
                    <xsl:with-param name="formula" select="$formula"/>
                    <xsl:with-param name="position" select="1"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>

    <xsl:template name="replaceGroupsInFormula">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="formula"/>
        <xsl:param name="position" as="xs:integer"/>

        <xsl:choose>
            <xsl:when test="$list-of-groups/Group[$position]">
                <xsl:value-of select="'tttttttttttttX'"/>
                <xsl:call-template name="replaceGroupsInFormula">
                    <xsl:with-param name="formula" select="replace($formula,$list-of-groups/Group[$position]/@id,$list-of-groups/Group[$position]/@name)"/>
                    <xsl:with-param name="position" select="$position +1"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formula"/>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

</xsl:stylesheet>
