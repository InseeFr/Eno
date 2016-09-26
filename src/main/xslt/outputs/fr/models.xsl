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
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
    xmlns:iatfr="http://xml/insee.fr/xslt/apply-templates/form-runner"
    xmlns:il="http://xml/insee.fr/xslt/lib" exclude-result-prefixes="xd iat il" version="2.0">

    <xsl:param name="campaign" as="xs:string"/>
    <xsl:param name="model" as="xs:string"/>
    <xsl:param name="properties-file"/>
    <xsl:variable name="properties" select="doc($properties-file)"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Apr 9, 2013</xd:p>
            <xd:p><xd:b>Author:</xd:b> vdv</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>

    <xsl:output name="concise-xml" method="xml" indent="no" omit-xml-declaration="yes"
        exclude-result-prefixes="#all"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Form generation</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Form" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="languages" select="iatfr:get-form-languages($source-context)"
            as="xs:string +"/>
        <xhtml:html>
            <xhtml:head>
                <xhtml:title>
                    <xsl:value-of select="iatfr:get-form-title($source-context, $languages[1])"/>
                </xhtml:title>
                <xhtml:link rel="stylesheet"
                    href="{concat('/',$properties//css/dossier,'/',$properties//css/principale)}"/>
                <xhtml:link rel="stylesheet">
                    <xsl:attribute name="href"
                        select="concat('/',$properties//css/dossier,'/',tokenize($campaign,'-')[1],'/',tokenize($campaign,'-')[1],'.css')"
                    />
                </xhtml:link>
                <xhtml:link rel="stylesheet">
                    <xsl:attribute name="href"
                        select="concat('/',$properties//css/dossier,'/',tokenize($campaign,'-')[1],'/',tokenize($campaign,'-')[2],'.css')"
                    />
                </xhtml:link>
                <xhtml:link rel="stylesheet">
                    <xsl:attribute name="href"
                        select="concat('/',$properties//css/dossier,'/',tokenize($campaign,'-')[1],'/',$model,'.css')"
                    />
                </xhtml:link>
                <xf:model id="fr-form-model" xxf:expose-xpath-types="true"
                    xxf:noscript-support="true">

                    <!-- Main instance -->
                    <xf:instance id="fr-form-instance">
                        <form>
                            <xsl:apply-templates select="iat:child-fields($source-context)"
                                mode="source">
                                <xsl:with-param name="driver"
                                    select="il:append-empty-element('instance', .)" tunnel="yes"/>
                            </xsl:apply-templates>
                        </form>
                    </xf:instance>

                    <xf:instance id="fr-form-loop-model">
                        <LoopModels>
                            <xsl:apply-templates select="iat:child-fields($source-context)"
                                mode="source">
                                <xsl:with-param name="driver"
                                    select="il:append-empty-element('model', .)" tunnel="yes"/>
                            </xsl:apply-templates>
                        </LoopModels>
                    </xf:instance>

                    <!-- Bindings -->
                    <xf:bind xmlns:dataModel="java:org.orbeon.oxf.fb.DataModel"
                        id="fr-form-instance-binds" ref="instance('fr-form-instance')">
                        <xsl:apply-templates select="iat:child-fields($source-context)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="il:append-empty-element('bind', .)" tunnel="yes"/>
                        </xsl:apply-templates>
                    </xf:bind>

                    <!-- Metadata -->
                    <xf:instance id="fr-form-metadata" xxf:readonly="true">
                        <metadata>
                            <application-name>
                                <xsl:value-of select="iatfr:get-application-name($source-context)"/>
                            </application-name>
                            <form-name>
                                <xsl:value-of select="iatfr:get-form-name($source-context)"/>
                            </form-name>
                            <xsl:for-each select="$languages">
                                <title xml:lang="{.}">
                                    <xsl:value-of select="iatfr:get-form-title($source-context, .)"
                                    />
                                </title>
                                <description xml:lang="{.}">
                                    <xsl:value-of
                                        select="iatfr:get-form-description($source-context, .)"/>
                                </description>
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
                                    <xsl:apply-templates select="iat:child-fields($source-context)"
                                        mode="source">
                                        <xsl:with-param name="driver"
                                            select="il:append-empty-element('resource', $driver)"
                                            tunnel="yes"/>
                                        <xsl:with-param name="language" select="." tunnel="yes"/>
                                    </xsl:apply-templates>
                                </resource>
                            </xsl:for-each>
                        </resources>
                    </xf:instance>

                    <!-- Bind of resources for the ones that are dynamic (text depends from the answer to another question) -->
                    <xf:bind xmlns:dataModel="java:org.orbeon.oxf.fb.DataModel"
                        id="fr-form-resources-bind" ref="instance('fr-form-resources')">
                        <xsl:variable name="driver" select="."/>
                        <xsl:variable name="apos">'</xsl:variable>
                        <xsl:for-each select="$languages">
                            <xf:bind id="{concat('bind-resource-',.)}"
                                name="{concat('resource-',.)}"
                                ref="{concat('resource[@xml:lang=',$apos,.,$apos,']')}">
                                <xsl:apply-templates select="iat:child-fields($source-context)"
                                    mode="source">
                                    <xsl:with-param name="driver"
                                        select="il:append-empty-element('bind-resource', $driver)"
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
                    <fr:body xmlns:xbl="http://www.w3.org/ns/xbl"
                        xmlns:dataModel="java:org.orbeon.oxf.fb.DataModel"
                        xmlns:oxf="http://www.orbeon.com/oxf/processors"
                        xmlns:p="http://www.orbeon.com/oxf/pipeline">
                        <xsl:apply-templates select="iat:child-fields($source-context)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="il:append-empty-element('body', .)" tunnel="yes"/>
                            <xsl:with-param name="languages" select="$languages" tunnel="yes"/>
                        </xsl:apply-templates>
                    </fr:body>
                </fr:view>
            </xhtml:body>
        </xhtml:html>
    </xsl:template>

    <!-- Default template, creating the corresponding instance element, and going on on the children -->
    <xsl:template match="instance//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}">
            <xsl:value-of select="iatfr:get-default-value($source-context)"/>
        </xsl:element>
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="instance//*[name()=('xf-group','Module')]" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}">
            <xsl:value-of select="iatfr:get-default-value($source-context)"/>
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xsl:template match="instance//xf-output" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}"/>
    </xsl:template>

    <xsl:template match="instance//*[name()=('SubModule','TextCell','MultipleQuestion')]"
        mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}"/>
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="*[name()=('resource','body')]//*[name()=('ResponseElement')]" mode="model"
        priority="1"/>

    <xsl:template match="bind-resource//xf-output" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:if
            test="iatfr:get-calculate-label($source-context) != '' or iatfr:get-calculate-alert($source-context) != ''">
            <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
            <xf:bind id="{$name}-resource-{$language}-bind" name="{$name}-{$language}-resource"
                ref="{$name}">
                <xsl:if test="iatfr:get-calculate-label($source-context) != ''">
                    <xf:bind id="{$name}-resource-{$language}-bind-label"
                        name="{$name}-{$language}-resource-label" ref="label">
                        <xsl:attribute name="calculate">
                            <xsl:value-of select="iatfr:get-calculate-label($source-context)"/>
                        </xsl:attribute>
                    </xf:bind>
                </xsl:if>
                <xsl:if test="iatfr:get-calculate-alert($source-context) != ''">
                    <xf:bind id="{$name}-resource-{$language}-bind-alert"
                        name="{$name}-{$language}-resource-alert" ref="alert">
                        <xsl:attribute name="calculate">
                            <xsl:value-of select="iatfr:get-calculate-alert($source-context)"/>
                        </xsl:attribute>
                    </xf:bind>
                </xsl:if>
            </xf:bind>
        </xsl:if>
    </xsl:template>

    <xsl:template match="bind-resource//*" mode="#all">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="instance//RowLoop" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}">
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
        <xsl:element name="{concat(iatfr:get-name($source-context),'-Count')}">
            <xsl:value-of select="iatfr:get-minimum-lines($source-context)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="model//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="model//RowLoop" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}">
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xsl:template match="model//*[parent::Cell[ancestor::RowLoop]]" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}">
            <xsl:value-of select="iatfr:get-default-value($source-context)"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="instance//*[name()=('xf-item','EmptyCell')]" priority="1" mode="model"/>

    <xsl:template match="instance//table | instance//TableLoop" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}">
            <xsl:value-of select="iatfr:get-default-value($source-context)"/>
        </xsl:element>
        <!--<xsl:element name="{concat(iatfr:get-name($source-context),'-group')}"/>-->
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="instance//DoubleDuration" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}"/>
        <xsl:element name="{replace(iatfr:get-name($source-context),'-','-A-')}"/>
        <xsl:element name="{replace(iatfr:get-name($source-context),'-','-B-')}"/>
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="bind//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not(iatfr:get-required($source-context) = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="iatfr:get-required($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="iatfr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="iatfr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-type($source-context) = 'date'">
                <xsl:attribute name="type">
                    <xsl:value-of select="concat('xf:',iatfr:get-type($source-context))"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(iatfr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(',iatfr:get-readonly($source-context),')')"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-constraint($source-context) != ''">
                <xsl:element name="xf:constraint">
                    <xsl:if test="iatfr:get-alert-level($source-context) != ''">
                        <xsl:attribute name="level">
                            <xsl:value-of select="iatfr:get-alert-level($source-context)"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="value">
                        <xsl:value-of select="iatfr:get-constraint($source-context)"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xf:bind>
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="bind//ResponseElement" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="iatfr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="iatfr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
        </xf:bind>
    </xsl:template>

    <xsl:template match="bind//Module" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="iatfr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="iatfr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(iatfr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(',iatfr:get-readonly($source-context),')')"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:bind>
    </xsl:template>

    <xsl:template match="bind//xf-group" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="iatfr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="iatfr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(iatfr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(',iatfr:get-readonly($source-context),')')"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:bind>
    </xsl:template>

    <xsl:template match="bind//RowLoop" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" nodeset="{$name}">
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:bind>
    </xsl:template>

    <xsl:template match="bind//*[name()=('xf-item','EmptyCell')]" priority="1" mode="model"/>

    <xsl:template match="bind//Table | bind//TableLoop" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not(iatfr:get-required($source-context) = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="iatfr:get-required($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="iatfr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="iatfr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-type($source-context) != ''">
                <xsl:attribute name="type">
                    <xsl:value-of select="iatfr:get-type($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(iatfr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(',iatfr:get-readonly($source-context),')')"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-constraint($source-context) != ''">
                <xsl:element name="xf:constraint">
                    <xsl:if test="iatfr:get-alert-level($source-context) != ''">
                        <xsl:attribute name="level">
                            <xsl:value-of select="iatfr:get-alert-level($source-context)"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="value">
                        <xsl:value-of select="iatfr:get-constraint($source-context)"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xf:bind>
        <xsl:variable name="name-bis" select="concat(iatfr:get-name($source-context),'-group')"/>
        <!--<xf:bind id="{$name-bis}-bind" name="{$name-bis}" ref="{$name-bis}">
            <xsl:if test="iatfr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="iatfr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
        </xf:bind>-->
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="bind//DoubleDuration" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <!-- Creating one element that correspond to the concatenation of the two ones -->
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:attribute name="calculate">
                <xsl:value-of
                    select="concat('if (not(instance(&quot;fr-form-instance&quot;)//', replace($name,'-','-A-'),
                    ' castable as xs:integer or instance(&quot;fr-form-instance&quot;)//', replace($name,'-','-B-'),
                    ' castable as xs:integer)) then &quot;&quot; else (100*number(if (instance(&quot;fr-form-instance&quot;)//', replace($name,'-','-A-'),
                    ' castable as xs:integer) then instance(&quot;fr-form-instance&quot;)//', replace($name,'-','-A-'),
                    ' else 0)+number(if (instance(&quot;fr-form-instance&quot;)//', replace($name,'-','-B-'),
                    ' castable as xs:integer) then instance(&quot;fr-form-instance&quot;)//', replace($name,'-','-B-'),
                    ' else 0))')"
                />
            </xsl:attribute>
        </xf:bind>
        <xsl:variable name="name" select="replace(iatfr:get-name($source-context),'-','-A-')"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not(iatfr:get-required($source-context) = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="iatfr:get-required($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="iatfr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="iatfr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-type($source-context) != ''">
                <xsl:attribute name="type">
                    <xsl:value-of select="iatfr:get-type($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(iatfr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(',iatfr:get-readonly($source-context),')')"/>
                </xsl:attribute>
            </xsl:if>
        </xf:bind>
        <xsl:variable name="name" select="replace(iatfr:get-name($source-context),'-','-B-')"/>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not(iatfr:get-required($source-context) = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="iatfr:get-required($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-relevant($source-context) != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="iatfr:get-relevant($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-calculate($source-context) != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="iatfr:get-calculate($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-type($source-context) != ''">
                <xsl:attribute name="type">
                    <xsl:value-of select="iatfr:get-type($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not(iatfr:get-readonly($source-context) = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(',iatfr:get-readonly($source-context),')')"/>
                </xsl:attribute>
            </xsl:if>
        </xf:bind>
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="resource//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}">
            <label>
                <xsl:value-of select="il:serialize(iatfr:get-label($source-context, $language))"/>
            </label>
            <hint>
                <xsl:value-of select="il:serialize(iatfr:get-hint($source-context, $language))"/>
            </hint>
            <help>
                <xsl:value-of select="il:serialize(iatfr:get-help($source-context, $language))"/>
            </help>
            <alert>
                <xsl:value-of select="il:serialize(iatfr:get-alert($source-context, $language))"/>
            </alert>
        </xsl:element>
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="resource//*[starts-with(name(),'xf-select')]" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:element name="{iatfr:get-name($source-context)}">
            <label>
                <xsl:value-of select="il:serialize(iatfr:get-label($source-context, $language))"/>
            </label>
            <hint>
                <xsl:value-of select="il:serialize(iatfr:get-hint($source-context, $language))"/>
            </hint>
            <help>
                <xsl:value-of select="il:serialize(iatfr:get-help($source-context, $language))"/>
            </help>
            <alert>
                <xsl:value-of select="il:serialize(iatfr:get-alert($source-context, $language))"/>
            </alert>
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xsl:template match="resource//DoubleDuration" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:element name="{replace(iatfr:get-name($source-context),'-','-A-')}">
            <label>
                <xsl:value-of select="il:serialize(iatfr:get-label($source-context, $language))"/>
            </label>
            <hint>
                <xsl:value-of select="il:serialize(iatfr:get-hint($source-context, $language))"/>
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
        <xsl:element name="{replace(iatfr:get-name($source-context),'-','-B-')}">
            <label>
                <xsl:value-of select="il:serialize(iatfr:get-label($source-context, $language))"/>
            </label>
            <hint>
                <xsl:value-of select="il:serialize(iatfr:get-hint($source-context, $language))"/>
            </hint>
            <xsl:for-each select="0 to 99">
                <xsl:variable name="caption">
                    <xsl:choose>
                        <xsl:when test="number(.)&lt;10">
                            <xsl:value-of select="concat('0',string(.))"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="string(.)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <item>
                    <label>
                        <xsl:value-of select="$caption"/>
                    </label>
                    <value>
                        <xsl:value-of select="$caption"/>
                    </value>
                </item>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="resource//EmptyCell" priority="1" mode="model"/>

    <xsl:template match="resource//xf-item" priority="1" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <item>
            <label>
                <xsl:value-of select="iatfr:get-label($source-context, $language)"/>
            </label>
            <value>
                <xsl:value-of select="iatfr:get-value($source-context)"/>
            </value>
        </item>
    </xsl:template>

    <xsl:template match="body/Module" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <fr:section id="{$name}-control" bind="{$name}-bind" name="{$name}">
            <xf:label ref="$form-resources/{$name}/label"/>
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </fr:section>
    </xsl:template>

    <xsl:template match="body//SubModule" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xsl:variable name="label">
            <xsl:value-of select="iatfr:get-label($source-context, $languages[1])"/>
        </xsl:variable>
        <xhtml:div>
            <xsl:if test="iatfr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="iatfr:get-css-class($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($label='')">
                <xhtml:h3>
                    <xf:output id="{$name}-control" bind="{$name}-bind">
                        <xf:label ref="$form-resources/{$name}/label">
                            <xsl:if test="iatfr:get-css-class($source-context) != ''">
                                <xsl:attribute name="class">
                                    <xsl:value-of select="iatfr:get-css-class($source-context)"/>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:if test="il:is-rich-content($label)">
                                <xsl:attribute name="mediatype">text/html</xsl:attribute>
                            </xsl:if>
                        </xf:label>
                    </xf:output>
                </xhtml:h3>
            </xsl:if>
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:div>
    </xsl:template>

    <xsl:template match="body//Group" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xsl:variable name="label">
            <xsl:value-of select="iatfr:get-label($source-context, $languages[1])"/>
        </xsl:variable>
        <xhtml:div>
            <xsl:if test="iatfr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="iatfr:get-css-class($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($label='')">
                <xhtml:h4>
                    <xf:output id="{$name}-control" bind="{$name}-bind">
                        <xf:label ref="$form-resources/{$name}/label">
                            <xsl:if test="iatfr:get-css-class($source-context) != ''">
                                <xsl:attribute name="class">
                                    <xsl:value-of select="iatfr:get-css-class($source-context)"/>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:if test="il:is-rich-content($label)">
                                <xsl:attribute name="mediatype">text/html</xsl:attribute>
                            </xsl:if>
                        </xf:label>
                    </xf:output>
                </xhtml:h4>
            </xsl:if>
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:div>
    </xsl:template>

    <xsl:template match="body//xf-group" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xf:group id="{$name}-control" bind="{$name}-bind">
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xf:group>
    </xsl:template>

    <xsl:template match="body//MultipleQuestion" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
        <xsl:variable name="label">
            <xsl:value-of select="iatfr:get-label($source-context, $languages[1])"/>
        </xsl:variable>
        <xhtml:div class="question">
            <xsl:variable name="question-title">
                <body>
                    <xf-output/>
                </body>
            </xsl:variable>
            <xsl:apply-templates select="$question-title//xf-output" mode="model"/>
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:div>
    </xsl:template>

    <xsl:template match="*[name()=('instance','bind','resource')]//*[name()=('Cell')]"
        mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="body//TextCell" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:th colspan="{iatfr:get-colspan($source-context)}"
            rowspan="{iatfr:get-rowspan($source-context)}">
            <xsl:variable name="cell-text">
                <body>
                    <xf-output/>
                </body>
            </xsl:variable>
            <xsl:apply-templates select="$cell-text//xf-output" mode="model"/>
            <!--            <xsl:value-of select="iatfr:get-label($source-context, $languages[1])"/>-->
        </xhtml:th>
    </xsl:template>

    <xsl:template match="body//Cell" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:td align="center">
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xhtml:td>
    </xsl:template>

    <xsl:template match="body//EmptyCell" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:td colspan="{iatfr:get-colspan($source-context)}"/>
    </xsl:template>

    <xsl:template match="body//Table" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="table-title">
            <body>
                <xf-output/>
            </body>
        </xsl:variable>
        <xsl:apply-templates select="$table-title//xf-output" mode="model"/>

        <xsl:variable name="ancestors">
            <xsl:copy-of select="root(.)"/>
        </xsl:variable>

        <!--        <xf:group id="{concat(iatfr:get-name($source-context),'-group')}" bind="{concat(iatfr:get-name($source-context),'-group-bind')}">-->
        <xhtml:table name="{iatfr:get-name($source-context)}">
            <xsl:if test="iatfr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="iatfr:get-css-class($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xhtml:colgroup>
                <xhtml:col span="{count(iatfr:get-header-columns($source-context))}"/>
            </xhtml:colgroup>
            <xhtml:thead>
                <xsl:for-each select="iatfr:get-header-lines($source-context)">
                    <xhtml:tr>
                        <xsl:apply-templates
                            select="iatfr:get-header-line($source-context, position())"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="$ancestors//*[not(child::*) and not(name()='driver')]"
                                tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xsl:for-each>
            </xhtml:thead>
            <xhtml:tbody>
                <xsl:for-each select="iatfr:get-body-lines($source-context)">
                    <xhtml:tr>
                        <xsl:apply-templates
                            select="iatfr:get-body-line($source-context, position())" mode="source">
                            <xsl:with-param name="driver"
                                select="$ancestors//*[not(child::*) and not(name()='driver')]"
                                tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xsl:for-each>
            </xhtml:tbody>
        </xhtml:table>
        <!--</xf:group>-->
    </xsl:template>

    <xsl:template match="body//TableLoop" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="table-title">
            <body>
                <xf-output/>
            </body>
        </xsl:variable>
        <xsl:apply-templates select="$table-title//xf-output" mode="model"/>

        <xsl:variable name="ancestors">
            <xsl:copy-of select="root(.)"/>
        </xsl:variable>

        <!--        <xf:group id="{concat(iatfr:get-name($source-context),'-group')}" bind="{concat(iatfr:get-name($source-context),'-group-bind')}">-->
        <xhtml:table name="{iatfr:get-name($source-context)}">
            <xsl:if test="iatfr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="iatfr:get-css-class($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xhtml:colgroup>
                <xhtml:col span="{count(iatfr:get-header-columns($source-context))}"/>
            </xhtml:colgroup>
            <xhtml:thead>
                <xsl:for-each select="iatfr:get-header-lines($source-context)">
                    <xhtml:tr>
                        <xsl:apply-templates
                            select="iatfr:get-header-line($source-context, position())"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="$ancestors//*[not(child::*) and not(name()='driver')]"
                                tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xsl:for-each>
            </xhtml:thead>
            <xhtml:tbody>
                <xf:repeat nodeset="{concat('//',iatfr:get-name($source-context),'-RowLoop')}"
                    id="{concat(iatfr:get-name($source-context),'-RowLoop')}">
                    <xhtml:tr>
                        <xsl:apply-templates select="iatfr:get-body-line($source-context, 1)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="$ancestors//*[not(child::*) and not(name()='driver')]"
                                tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xf:repeat>
            </xhtml:tbody>
        </xhtml:table>
        <xf:trigger>
            <xf:label>Ajouter</xf:label>
            <xf:insert ev:event="DOMActivate" context="."
                nodeset="{concat('//',iatfr:get-name($source-context),'-RowLoop')}"
                origin="{concat('instance(&#34;fr-form-loop-model&#34;)/',iatfr:get-name($source-context),'-RowLoop')}"
            />
        </xf:trigger>

        <!--</xf:group>-->
    </xsl:template>

    <!--
    <xsl:template match="thead/header-line"/>

    <!-\- The header-line driver doesn't return anything for the tbody parent.
    Indeed, the header-line templates aren't applied inside the tbody of the table -\->
    <xsl:template match="tbody/header-line"/>

    <!-\- The body-line driver doesn't return anything for the thead parent.
    Indeed, the body-line templates aren't applied inside the tbody of the thead -\->
    <xsl:template match="thead/body-line"/>

    <xsl:template match="tbody/body-line"/>-->

    <xsl:template match="body//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="iatfr:get-name($source-context)"/>
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
            <xsl:if test="iatfr:get-appearance($source-context) != ''">
                <xsl:attribute name="appearance">
                    <xsl:value-of select="iatfr:get-appearance($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-css-class($source-context) != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="iatfr:get-css-class($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="xxf:order">
                <xsl:value-of select="string('label control hint help alert')"/>
            </xsl:attribute>
            <!--<xsl:if test="iatfr:get-format($source-context) != ''">
                <xsl:attribute name="xxf:format">
                    <xsl:value-of select="iatfr:get-format($source-context)"/>
                </xsl:attribute>
            </xsl:if>-->
            <xsl:if test="iatfr:get-length($source-context)">
                <xsl:attribute name="xxf:maxlength">
                    <xsl:value-of select="iatfr:get-length($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xf:label ref="$form-resources/{$name}/label">
                <xsl:if test="il:is-rich-content(iatfr:get-label($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:label>
            <xf:hint ref="$form-resources/{$name}/hint">
                <xsl:if test="il:is-rich-content(iatfr:get-hint($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:hint>
            <xf:help ref="$form-resources/{$name}/help">
                <xsl:if test="il:is-rich-content(iatfr:get-help($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:help>
            <xf:alert ref="$form-resources/{$name}/alert">
                <xsl:if test="iatfr:get-alert-level($source-context) != ''">
                    <xsl:attribute name="level">
                        <xsl:value-of select="iatfr:get-alert-level($source-context)"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="il:is-rich-content(iatfr:get-alert($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:alert>
            <xsl:if test="self::xf-select1 or self::xf-select">
                <xsl:if test="iatfr:get-appearance($source-context) = 'minimal'">
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
            <xsl:for-each select="iatfr:get-relevant-dependencies($source-context)">
                <xf:action ev:event="xforms-value-changed"
                    if="{concat('not(xxf:evaluate-bind-property(&quot;',.,'-bind&quot;,&quot;relevant&quot;))')}"
                    iterate="{concat('instance(&quot;fr-form-instance&quot;)//',.,'//*')}">
                    <xf:setvalue ref="." value="''"/>
                </xf:action>
            </xsl:for-each>
<!--            <!-\- If constraints depend on this field's value -\->
            <xsl:if test="count(iatfr:get-constraint-dependencies($source-context))>0">
                <xf:setvalue ev:event="xforms-value-changed"
                    ref="{concat('instance(&quot;fr-form-instance&quot;)//',$name)}"
                    value="{concat('replace(instance(&quot;fr-form-instance&quot;)//',$name,',&quot; &quot;,&quot;&quot;)')}"/>-->
                <xsl:for-each select="iatfr:get-constraint-dependencies($source-context)">
                    <xsl:element name="xf:dispatch">
                        <xsl:attribute name="ev:event">
                            <xsl:text>DOMFocusOut xforms-value-changed</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="name">
                            <xsl:text>DOMFocusOut</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="target">
                            <xsl:value-of select="concat(.,'-control')"/>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:for-each>
            <!--</xsl:if>-->
        </xsl:element>
        <xsl:if test="iatfr:get-suffix($source-context, $languages[1])">
            <xsl:element name="xhtml:span">
                <xsl:attribute name="class" select="'suffixe'"/>
                <xsl:copy-of select="iatfr:get-suffix($source-context, $languages[1])"
                    copy-namespaces="no"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="body//DoubleDuration" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="replace(iatfr:get-name($source-context),'-','-A-')"/>
        <xsl:element name="xf:select1">
            <xsl:attribute name="id">
                <xsl:value-of select="concat($name, '-control')"/>
            </xsl:attribute>
            <xsl:attribute name="bind">
                <xsl:value-of select="concat($name, '-bind')"/>
            </xsl:attribute>
            <xsl:if test="iatfr:get-appearance($source-context) != ''">
                <xsl:attribute name="appearance">
                    <xsl:value-of select="iatfr:get-appearance($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="class">
                <xsl:value-of select="string('double-duration')"/>
            </xsl:attribute>

            <xsl:attribute name="xxf:order">
                <xsl:value-of select="string('label control hint help alert')"/>
            </xsl:attribute>
            <xsl:if test="iatfr:get-format($source-context) != ''">
                <xsl:attribute name="xxf:format">
                    <xsl:value-of select="iatfr:get-format($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-length($source-context)">
                <xsl:attribute name="xxf:maxlength">
                    <xsl:value-of select="iatfr:get-length($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xf:label ref="$form-resources/{$name}/label">
                <xsl:if test="il:is-rich-content(iatfr:get-label($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:label>
            <xf:hint ref="$form-resources/{$name}/hint">
                <xsl:if test="il:is-rich-content(iatfr:get-hint($source-context, $languages[1]))">
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
            <xsl:attribute name="class" select="'suffixe-double-duration'"/>
            <xsl:text>heure(s)</xsl:text>
        </xsl:element>

        <xsl:variable name="name" select="replace(iatfr:get-name($source-context),'-','-B-')"/>
        <xsl:element name="xf:select1">
            <xsl:attribute name="id">
                <xsl:value-of select="concat($name, '-control')"/>
            </xsl:attribute>
            <xsl:attribute name="bind">
                <xsl:value-of select="concat($name, '-bind')"/>
            </xsl:attribute>
            <xsl:if test="iatfr:get-appearance($source-context) != ''">
                <xsl:attribute name="appearance">
                    <xsl:value-of select="iatfr:get-appearance($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="class">
                <xsl:value-of select="string('double-duration')"/>
            </xsl:attribute>
            <xsl:attribute name="xxf:order">
                <xsl:value-of select="string('label control hint help alert')"/>
            </xsl:attribute>
            <xsl:if test="iatfr:get-format($source-context) != ''">
                <xsl:attribute name="xxf:format">
                    <xsl:value-of select="iatfr:get-format($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="iatfr:get-length($source-context)">
                <xsl:attribute name="xxf:maxlength">
                    <xsl:value-of select="iatfr:get-length($source-context)"/>
                </xsl:attribute>
            </xsl:if>
            <xf:label ref="$form-resources/{$name}/label">
                <xsl:if test="il:is-rich-content(iatfr:get-label($source-context, $languages[1]))">
                    <xsl:attribute name="mediatype">text/html</xsl:attribute>
                </xsl:if>
            </xf:label>
            <xf:hint ref="$form-resources/{$name}/hint">
                <xsl:if test="il:is-rich-content(iatfr:get-hint($source-context, $languages[1]))">
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
            <xsl:attribute name="class" select="'suffixe-double-duration'"/>
            <xsl:text>centième(s)</xsl:text>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
