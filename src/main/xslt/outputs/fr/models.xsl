<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xi="http://www.w3.org/2001/XInclude"
    xmlns:xxi="http://orbeon.org/oxf/xml/xinclude" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:exf="http://www.exforms.org/exf/1-0" xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
    xmlns:saxon="http://saxon.sf.net/" xmlns:sql="http://orbeon.org/oxf/xml/sql"
    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner" xmlns:xbl="http://www.w3.org/ns/xbl"
    xmlns:dataModel="java:org.orbeon.oxf.fb.DataModel"
    xmlns:oxf="http://www.orbeon.com/oxf/processors" xmlns:p="http://www.orbeon.com/oxf/pipeline"
    exclude-result-prefixes="xd eno enofr" version="2.0">

    <!-- Orbeon-form-runner related file -->
    <!-- This file is imported in the ddi2fr.xsl file (already in ddi2fr-fixed.xsl) -->

    <!-- Parameters defined in build-non-regression.xml -->
    <xsl:param name="campaign" as="xs:string"/>
    <xsl:param name="model" as="xs:string"/>
    <xsl:param name="properties-file"/>
    <xsl:variable name="properties" select="doc($properties-file)"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Form generation</xd:p>
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
                    href="{concat('/',$properties//css/dossier,'/',$properties//css/principale)}"/>
                <xhtml:link rel="stylesheet">
                    <xsl:attribute name="href"
                        select="concat('/', $properties//css/dossier, '/', tokenize($campaign, '-')[1], '/', tokenize($campaign, '-')[1], '.css')"
                    />
                </xhtml:link>
                <xhtml:link rel="stylesheet">
                    <xsl:attribute name="href"
                        select="concat('/', $properties//css/dossier, '/', tokenize($campaign, '-')[1], '/', tokenize($campaign, '-')[2], '.css')"
                    />
                </xhtml:link>
                <xhtml:link rel="stylesheet">
                    <xsl:attribute name="href"
                        select="concat('/', $properties//css/dossier, '/', tokenize($campaign, '-')[1], '/', $model, '.css')"
                    />
                </xhtml:link>
                <xf:model id="fr-form-model" xxf:expose-xpath-types="true"
                    xxf:noscript-support="true">

                    <!-- Main instance -->
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
                                <resource xml:lang="{.}">
                                    <xsl:apply-templates select="eno:child-fields($source-context)"
                                        mode="source">
                                        <xsl:with-param name="driver"
                                            select="eno:append-empty-element('Resource', $driver)"
                                            tunnel="yes"/>
                                        <xsl:with-param name="language" select="." tunnel="yes"/>
                                    </xsl:apply-templates>
                                </resource>
                            </xsl:for-each>
                        </resources>
                    </xf:instance>

                    <!-- Bind of resources for the ones that are dynamic (text depends from the answer to another question) -->
                    <xf:bind id="fr-form-resources-bind" ref="instance('fr-form-resources')">
                        <xsl:variable name="driver" select="."/>
                        <xsl:variable name="apos">'</xsl:variable>
                        <xsl:for-each select="$languages">
                            <xf:bind id="{concat('bind-resource-',.)}"
                                name="{concat('resource-',.)}"
                                ref="{concat('resource[@xml:lang=',$apos,.,$apos,']')}">
                                <xsl:apply-templates select="eno:child-fields($source-context)"
                                    mode="source">
                                    <xsl:with-param name="driver"
                                        select="eno:append-empty-element('ResourceBind', $driver)"
                                        tunnel="yes"/>
                                    <xsl:with-param name="language" select="." tunnel="yes"/>
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
                    <fr:body>
                        <xsl:apply-templates select="eno:child-fields($source-context)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="eno:append-empty-element('Body', .)" tunnel="yes"/>
                            <xsl:with-param name="languages" select="$languages" tunnel="yes"/>
                        </xsl:apply-templates>
                    </fr:body>
                </fr:view>
            </xhtml:body>
        </xhtml:html>
    </xsl:template>

    <!-- Default template, creating the corresponding instance element, and going on on the children -->
    <xsl:template match="Instance//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}"/>        
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Instance//*[name() = ('xf-group', 'Module')]" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xsl:template match="Instance//xf-output" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}"/>
    </xsl:template>

    <xsl:template match="Instance//*[name() = ('SubModule', 'TextCell', 'MultipleQuestion')]"
        mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="*[name() = ('Resource', 'Body')]//*[name() = ('ResponseElement')]"
        mode="model" priority="1"/>

    <xsl:template match="ResourceBind//xf-output" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:if
            test="enofr:get-calculate-label($source-context) != '' or enofr:get-calculate-alert($source-context) != ''">
            <xsl:variable name="name" select="enofr:get-name($source-context)"/>
            <xf:bind id="{$name}-resource-{$language}-bind" name="{$name}-{$language}-resource"
                ref="{$name}">
                <xsl:if test="enofr:get-calculate-label($source-context) != ''">
                    <xf:bind id="{$name}-resource-{$language}-bind-label"
                        name="{$name}-{$language}-resource-label" ref="label">
                        <xsl:attribute name="calculate">
                            <xsl:value-of select="enofr:get-calculate-label($source-context)"/>
                        </xsl:attribute>
                    </xf:bind>
                </xsl:if>
                <xsl:if test="enofr:get-calculate-alert($source-context) != ''">
                    <xf:bind id="{$name}-resource-{$language}-bind-alert"
                        name="{$name}-{$language}-resource-alert" ref="alert">
                        <xsl:attribute name="calculate">
                            <xsl:value-of select="enofr:get-calculate-alert($source-context)"/>
                        </xsl:attribute>
                    </xf:bind>
                </xsl:if>
            </xf:bind>
        </xsl:if>
    </xsl:template>

    <xsl:template match="ResourceBind//*" mode="#all">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Instance//RowLoop" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
        <xsl:element name="{concat(enofr:get-name($source-context),'-Count')}">
            <xsl:value-of select="enofr:get-minimum-lines($source-context)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="Model//*[not(ancestor::Instance)]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Model//RowLoop | Model//QuestionLoop" priority="1" mode="model">
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

    <xsl:template match="Instance//QuestionLoop" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <!--<xsl:template match="Model//*[parent::Cell[ancestor::RowLoop]]" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}"/>
    </xsl:template>-->

    <xsl:template match="Instance//*[name() = ('xf-item', 'EmptyCell')]" priority="1" mode="model"/>

    <xsl:template match="Instance//Table | Instance//TableLoop" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}"/>        
        <!--<xsl:element name="{concat(enofr:get-name($source-context),'-group')}"/>-->
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Instance//DoubleDuration" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}"/>
        <xsl:element name="{replace(enofr:get-name($source-context),'-','-A-')}"/>
        <xsl:element name="{replace(enofr:get-name($source-context),'-','-B-')}"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Bind//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not(enofr:get-required($source-context) = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="enofr:get-required($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="enofr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="enofr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-type($source-context) = 'date'">
                <xsl:attribute name="type">
                    <xsl:value-of select="concat('xf:', enofr:get-type($source-context))"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(enofr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', enofr:get-readonly($source-context), ')')"
                    />
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-constraint($source-context) != ''">
                <xsl:element name="xf:constraint">
                    <xsl:if test="enofr:get-alert-level($source-context) != ''">
                        <xsl:attribute name="level">
                            <xsl:value-of select="enofr:get-alert-level($source-context)"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="value">
                        <xsl:value-of select="enofr:get-constraint($source-context)"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xf:bind>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Bind//ResponseElement" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="enofr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="enofr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
        </xf:bind>
    </xsl:template>

    <xsl:template match="Bind//Module" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="enofr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="enofr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(enofr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', enofr:get-readonly($source-context), ')')"
                    />
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:bind>
    </xsl:template>

    <xsl:template match="Bind//xf-group" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="enofr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="enofr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(enofr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', enofr:get-readonly($source-context), ')')"
                    />
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:bind>
    </xsl:template>

    <xsl:template match="Bind//RowLoop | Bind//QuestionLoop" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" nodeset="{$name}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:bind>
    </xsl:template>

    <xsl:template match="Bind//*[name() = ('xf-item', 'EmptyCell')]" priority="1" mode="model"/>

    <xsl:template match="Bind//Table | Bind//TableLoop" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not(enofr:get-required($source-context) = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="enofr:get-required($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="enofr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="enofr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-type($source-context) != ''">
                <xsl:attribute name="type">
                    <xsl:value-of select="enofr:get-type($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(enofr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', enofr:get-readonly($source-context), ')')"
                    />
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-constraint($source-context) != ''">
                <xsl:element name="xf:constraint">
                    <xsl:if test="enofr:get-alert-level($source-context) != ''">
                        <xsl:attribute name="level">
                            <xsl:value-of select="enofr:get-alert-level($source-context)"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="value">
                        <xsl:value-of select="enofr:get-constraint($source-context)"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xf:bind>
        <xsl:variable name="name-bis" select="concat(enofr:get-name($source-context), '-group')"/>
        <!--<xf:bind id="{$name-bis}-bind" name="{$name-bis}" ref="{$name-bis}">
            <xsl:if test="enofr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="enofr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
        </xf:bind>-->
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Bind//DoubleDuration" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <!-- Creating one element that correspond to the concatenation of the two ones -->
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:attribute name="calculate">
                <xsl:value-of
                    select="
                        concat('if (not(instance(&quot;fr-form-instance&quot;)//', replace($name, '-', '-A-'),
                        ' castable as xs:integer or instance(&quot;fr-form-instance&quot;)//', replace($name, '-', '-B-'),
                        ' castable as xs:integer)) then &quot;&quot; else (100*number(if (instance(&quot;fr-form-instance&quot;)//', replace($name, '-', '-A-'),
                        ' castable as xs:integer) then instance(&quot;fr-form-instance&quot;)//', replace($name, '-', '-A-'),
                        ' else 0)+number(if (instance(&quot;fr-form-instance&quot;)//', replace($name, '-', '-B-'),
                        ' castable as xs:integer) then instance(&quot;fr-form-instance&quot;)//', replace($name, '-', '-B-'),
                        ' else 0))')"
                />
            </xsl:attribute>
        </xf:bind>
        <xsl:variable name="name" select="replace(enofr:get-name($source-context), '-', '-A-')"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not(enofr:get-required($source-context) = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="enofr:get-required($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="enofr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="enofr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-type($source-context) != ''">
                <xsl:attribute name="type">
                    <xsl:value-of select="enofr:get-type($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(enofr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', enofr:get-readonly($source-context), ')')"
                    />
                </xsl:attribute>
            </xsl:if>
        </xf:bind>
        <xsl:variable name="name" select="replace(enofr:get-name($source-context), '-', '-B-')"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not(enofr:get-required($source-context) = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="enofr:get-required($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="enofr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="enofr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-type($source-context) != ''">
                <xsl:attribute name="type">
                    <xsl:value-of select="enofr:get-type($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(enofr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', enofr:get-readonly($source-context), ')')"
                    />
                </xsl:attribute>
            </xsl:if>
        </xf:bind>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Resource//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}">
            <label>
                <xsl:value-of select="eno:serialize(enofr:get-label($source-context, $language))"/>
            </label>
            <hint>
                <xsl:value-of select="eno:serialize(enofr:get-hint($source-context, $language))"/>
            </hint>
            <help>
                <xsl:value-of select="eno:serialize(enofr:get-help($source-context, $language))"/>
            </help>
            <alert>
                <xsl:value-of select="eno:serialize(enofr:get-alert($source-context, $language))"/>
            </alert>
        </xsl:element>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Resource//*[starts-with(name(), 'xf-select')]" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}">
            <label>
                <xsl:value-of select="eno:serialize(enofr:get-label($source-context, $language))"/>
            </label>
            <hint>
                <xsl:value-of select="eno:serialize(enofr:get-hint($source-context, $language))"/>
            </hint>
            <help>
                <xsl:value-of select="eno:serialize(enofr:get-help($source-context, $language))"/>
            </help>
            <alert>
                <xsl:value-of select="eno:serialize(enofr:get-alert($source-context, $language))"/>
            </alert>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xsl:template match="Resource//DoubleDuration" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:element name="{replace(enofr:get-name($source-context),'-','-A-')}">
            <label>
                <xsl:value-of select="eno:serialize(enofr:get-label($source-context, $language))"/>
            </label>
            <hint>
                <xsl:value-of select="eno:serialize(enofr:get-hint($source-context, $language))"/>
            </hint>
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
        <xsl:element name="{replace(enofr:get-name($source-context),'-','-B-')}">
            <label>
                <xsl:value-of select="eno:serialize(enofr:get-label($source-context, $language))"/>
            </label>
            <hint>
                <xsl:value-of select="eno:serialize(enofr:get-hint($source-context, $language))"/>
            </hint>
            <xsl:for-each select="0 to 99">
                <xsl:variable name="label">
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
                        <xsl:value-of select="$label"/>
                    </label>
                    <value>
                        <xsl:value-of select="$label"/>
                    </value>
                </item>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="Resource//EmptyCell" priority="1" mode="model"/>

    <xsl:template match="Resource//xf-item" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <item>
            <label>
                <xsl:value-of select="enofr:get-label($source-context, $language)"/>
            </label>
            <value>
                <xsl:value-of select="enofr:get-value($source-context)"/>
            </value>
        </item>
    </xsl:template>

    <xsl:template match="Body//Module" mode="model" priority="1">
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

    <xsl:template match="Body//SubModule" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label">
            <xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
        </xsl:variable>
        <xhtml:div>
            <xsl:if test="enofr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="enofr:get-css-class($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($label = '')">
                <xhtml:h3>
                    <xf:output id="{$name}-control" bind="{$name}-bind">
                        <xf:label ref="$form-resources/{$name}/label">
                            <xsl:if test="enofr:get-css-class($source-context) != ''">
                                <xsl:attribute name="class">
                                    <xsl:value-of select="enofr:get-css-class($source-context)"/>
                                </xsl:attribute>
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

    <xsl:template match="Body//Group" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label">
            <xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
        </xsl:variable>
        <xhtml:div>
            <xsl:if test="enofr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="enofr:get-css-class($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($label = '')">
                <xhtml:h4>
                    <xf:output id="{$name}-control" bind="{$name}-bind">
                        <xf:label ref="$form-resources/{$name}/label">
                            <xsl:if test="enofr:get-css-class($source-context) != ''">
                                <xsl:attribute name="class">
                                    <xsl:value-of select="enofr:get-css-class($source-context)"/>
                                </xsl:attribute>
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

    <xsl:template match="Body//xf-group" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xf:group id="{$name}-control" bind="{$name}-bind">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:group>
    </xsl:template>

    <xsl:template match="Body//MultipleQuestion" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label">
            <xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
        </xsl:variable>
        <xhtml:div class="question">
            <xsl:variable name="question-title">
                <Body>
                    <xf-output/>
                </Body>
            </xsl:variable>
            <xsl:apply-templates select="$question-title//xf-output" mode="model"/>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:div>
    </xsl:template>

    <xsl:template match="*[name() = ('Instance', 'Bind', 'Resource')]//*[name() = ('Cell')]"
        mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="Body//TextCell" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:th colspan="{enofr:get-colspan($source-context)}"
            rowspan="{enofr:get-rowspan($source-context)}">
            <xsl:variable name="cell-text">
                <Body>
                    <xf-output/>
                </Body>
            </xsl:variable>
            <xsl:apply-templates select="$cell-text//xf-output" mode="model"/>
            <!--            <xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>-->
        </xhtml:th>
    </xsl:template>

    <xsl:template match="Body//Cell" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:td align="center">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:td>
    </xsl:template>

    <xsl:template match="Body//EmptyCell" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:td colspan="{enofr:get-colspan($source-context)}"/>
    </xsl:template>

    <xsl:template match="Body//Table" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="table-title">
            <Body>
                <xf-output/>
            </Body>
        </xsl:variable>
        <xsl:apply-templates select="$table-title//xf-output" mode="model"/>

        <xsl:variable name="ancestors">
            <xsl:copy-of select="root(.)"/>
        </xsl:variable>

        <xhtml:table name="{enofr:get-name($source-context)}">
            <xsl:if test="enofr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="enofr:get-css-class($source-context)"/>
                </xsl:attribute>
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

    <xsl:template match="Body//QuestionLoop" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xf:repeat nodeset="{concat('//',enofr:get-name($source-context))}"
            id="{enofr:get-name($source-context)}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:repeat>
    </xsl:template>

    <xsl:template match="Body//TableLoop" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="table-title">
            <Body>
                <xf-output/>
            </Body>
        </xsl:variable>
        <xsl:apply-templates select="$table-title//xf-output" mode="model"/>

        <xsl:variable name="ancestors">
            <xsl:copy-of select="root(.)"/>
        </xsl:variable>

        <xhtml:table name="{enofr:get-name($source-context)}">
            <xsl:if test="enofr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="enofr:get-css-class($source-context)"/>
                </xsl:attribute>
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
                <xf:repeat nodeset="{concat('//',enofr:get-name($source-context),'-RowLoop')}"
                    id="{concat(enofr:get-name($source-context),'-RowLoop')}">
                    <xhtml:tr>
                        <xsl:apply-templates select="enofr:get-body-line($source-context, 1)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="$ancestors//*[not(child::*) and not(name() = 'driver')]"
                                tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xf:repeat>
            </xhtml:tbody>
        </xhtml:table>
        <xf:trigger>
            <xf:label>Ajouter</xf:label>
            <xf:insert ev:event="DOMActivate" context="."
                nodeset="{concat('//',enofr:get-name($source-context),'-RowLoop')}"
                origin="{concat('instance(&#34;fr-form-loop-model&#34;)/',enofr:get-name($source-context),'-RowLoop')}"
            />
        </xf:trigger>
    </xsl:template>

    <xsl:template match="Body//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:element name="{translate(name(), '-', ':')}">
            <xsl:attribute name="id">
                <xsl:value-of select="concat($name, '-control')"/>
            </xsl:attribute>
            <xsl:attribute name="name">
                <xsl:value-of select="$name"/>
            </xsl:attribute>
            <xsl:attribute name="bind">
                <xsl:value-of select="concat($name, '-bind')"/>
            </xsl:attribute>
            <xsl:if test="enofr:get-appearance($source-context) != ''">
                <xsl:attribute name="appearance">
                    <xsl:value-of select="enofr:get-appearance($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="enofr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="enofr:get-css-class($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="xxf:order">
                <xsl:value-of select="string('label control hint help alert')"/>
            </xsl:attribute>
            <xsl:if test="enofr:get-length($source-context)">
                <xsl:attribute name="xxf:maxlength">
                    <xsl:value-of select="enofr:get-length($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xf:label ref="$form-resources/{$name}/label">
                <xsl:if test="eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:label>
            <xf:hint ref="$form-resources/{$name}/hint">
                <xsl:if test="eno:is-rich-content(enofr:get-hint($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:hint>
            <xf:help ref="$form-resources/{$name}/help">
                <xsl:if test="eno:is-rich-content(enofr:get-help($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:help>
            <xf:alert ref="$form-resources/{$name}/alert">
                <xsl:if test="enofr:get-alert-level($source-context) != ''">
                    <xsl:attribute name="level">
                        <xsl:value-of select="enofr:get-alert-level($source-context)"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="eno:is-rich-content(enofr:get-alert($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:alert>
            <xsl:if test="self::xf-select1 or self::xf-select">
                <xsl:if test="enofr:get-appearance($source-context) = 'minimal'">
                    <xf:item>
                        <xf:label/>
                        <xf:value/>
                    </xf:item>
                </xsl:if>
                <xf:itemset ref="$form-resources/{$name}/item">
                    <xf:label ref="label"/>
                    <xf:value ref="value"/>
                </xf:itemset>
            </xsl:if>
            <!-- In this select case, if there is still someting after a space in the current value, that means that 2 boxes are checked.
            We replace the value with what was after the space and that correspond the value of the last checked box.
            This unchecks the first box that was checked -->
            <xsl:if test="self::xf-select">
                <xf:action ev:event="xforms-value-changed"
                    if="{concat('substring-after(instance(&quot;fr-form-instance&quot;)//',$name,',&quot; &quot;) ne &quot;&quot;')}">
                    <xf:setvalue ref="{concat('instance(&quot;fr-form-instance&quot;)//',$name)}"
                        value="{concat('substring-after(instance(&quot;fr-form-instance&quot;)//',$name,',&quot; &quot;)')}"
                    />
                </xf:action>
            </xsl:if>
            <!-- For each element which relevance depends on this field, we erase the data if it became unrelevant -->
            <xsl:for-each select="enofr:get-relevant-dependencies($source-context)">
                <xf:action ev:event="xforms-value-changed"
                    if="{concat('not(xxf:evaluate-bind-property(&quot;',.,'-bind&quot;,&quot;relevant&quot;))')}"
                    iterate="{concat('instance(&quot;fr-form-instance&quot;)//',.,'//*')}">
                    <xf:setvalue ref="." value="''"/>
                </xf:action>
            </xsl:for-each>
            <xsl:for-each select="enofr:get-constraint-dependencies($source-context)">
                <xsl:element name="xf:dispatch">
                    <xsl:attribute name="ev:event">
                        <xsl:text>DOMFocusOut xforms-value-changed</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="name">
                        <xsl:text>DOMFocusOut</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="target">
                        <xsl:value-of select="concat(., '-control')"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
        <xsl:if test="enofr:get-suffix($source-context, $languages[1])">
            <xsl:element name="xhtml:span">
                <xsl:attribute name="class" select="'suffixe'"/>
                <xsl:copy-of select="enofr:get-suffix($source-context, $languages[1])"
                    copy-namespaces="no"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="Body//DoubleDuration" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="replace(enofr:get-name($source-context), '-', '-A-')"/>
        <xsl:element name="xf:select1">
            <xsl:attribute name="id">
                <xsl:value-of select="concat($name, '-control')"/>
            </xsl:attribute>
            <xsl:attribute name="bind">
                <xsl:value-of select="concat($name, '-bind')"/>
            </xsl:attribute>
            <xsl:if test="enofr:get-appearance($source-context) != ''">
                <xsl:attribute name="appearance">
                    <xsl:value-of select="enofr:get-appearance($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="class">
                <xsl:value-of select="string('double-duration')"/>
            </xsl:attribute>
            <xsl:attribute name="xxf:order">
                <xsl:value-of select="string('label control hint help alert')"/>
            </xsl:attribute>
            <xsl:if test="enofr:get-length($source-context)">
                <xsl:attribute name="xxf:maxlength">
                    <xsl:value-of select="enofr:get-length($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xf:label ref="$form-resources/{$name}/label">
                <xsl:if test="eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:label>
            <xf:hint ref="$form-resources/{$name}/hint">
                <xsl:if test="eno:is-rich-content(enofr:get-hint($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:hint>
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
            <xsl:attribute name="id">
                <xsl:value-of select="concat($name, '-control')"/>
            </xsl:attribute>
            <xsl:attribute name="bind">
                <xsl:value-of select="concat($name, '-bind')"/>
            </xsl:attribute>
            <xsl:if test="enofr:get-appearance($source-context) != ''">
                <xsl:attribute name="appearance">
                    <xsl:value-of select="enofr:get-appearance($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="class">
                <xsl:value-of select="string('double-duration')"/>
            </xsl:attribute>
            <xsl:attribute name="xxf:order">
                <xsl:value-of select="string('label control hint help alert')"/>
            </xsl:attribute>
            <xsl:if test="enofr:get-length($source-context)">
                <xsl:attribute name="xxf:maxlength">
                    <xsl:value-of select="enofr:get-length($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xf:label ref="$form-resources/{$name}/label">
                <xsl:if test="eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:label>
            <xf:hint ref="$form-resources/{$name}/hint">
                <xsl:if test="eno:is-rich-content(enofr:get-hint($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:hint>
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

</xsl:stylesheet>
