<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
    exclude-result-prefixes="xd eno enofr" version="2.0">
    
    <!--<xsl:import href="../../transformations/ddi2fr/ddi2fr.xsl"/>-->
    
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
                    href="{concat('/',$properties//Css/Folder,'/',$properties//Css/Common)}"/>
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
                            <xf:bind id="{concat('bind-resource-',.)}"
                                name="{concat('resource-',.)}"
                                ref="{concat('resource[@xml:lang=''',.,''']')}">
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
                    <!-- Writing the main body -->
                    <fr:body>
                        <xsl:apply-templates select="eno:child-fields($source-context)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="eno:append-empty-element('Body', .)" tunnel="yes"/>
                            <xsl:with-param name="languages" select="$languages" tunnel="yes"/>
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
    <xsl:template match="Instance//*[name() = ('xf-group', 'Module', 'QuestionLoop')]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Special template for Instance for the RowLoop driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Instance//RowLoop" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name">
            <xsl:value-of select="enofr:get-name($source-context)"/>
        </xsl:variable>
        <xsl:element name="{$name}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
        <xsl:element name="{concat($name,'-Count')}">
            <xsl:value-of select="enofr:get-minimum-lines($source-context)"/>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Special template for Instance for the DoubleDuration driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Instance//DoubleDuration" mode="model" >
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name">
            <xsl:value-of select="enofr:get-name($source-context)"/>
        </xsl:variable>
        <xsl:element name="{$name}"/>
        <xsl:element name="{replace($name,'-','-A-')}"/>
        <xsl:element name="{replace($name,'-','-B-')}"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <!--<xsl:template match="Model//*[parent::Cell[ancestor::RowLoop]]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}"/>
    </xsl:template>-->

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
        <xsl:variable name="name">
            <xsl:value-of select="enofr:get-name($source-context)"/>
        </xsl:variable>
        <xsl:variable name="required">
            <xsl:value-of select="enofr:get-required($source-context)"/>
        </xsl:variable>
        <xsl:variable name="relevant">
            <xsl:value-of select="enofr:get-relevant($source-context)"/>
        </xsl:variable>
        <xsl:variable name="calculate">
            <xsl:value-of select="enofr:get-calculate($source-context)"/>
        </xsl:variable>
        <xsl:variable name="type">
            <xsl:value-of select="enofr:get-type($source-context)"/>
        </xsl:variable>
        <xsl:variable name="readonly">
            <xsl:value-of select="enofr:get-readonly($source-context)"/>
        </xsl:variable>
        <xsl:variable name="constraint">
            <xsl:value-of select="enofr:get-constraint($source-context)"/>
        </xsl:variable>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not($required = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="$required"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="$relevant"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$calculate != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="$calculate"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$type = 'date'">
                <xsl:attribute name="type">
                    <xsl:value-of select="concat('xf:', $type)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', $readonly, ')')"
                    />
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$constraint != ''">
                <xsl:element name="xf:constraint">
                    <xsl:variable name="alert-level">
                        <xsl:value-of select="enofr:get-alert-level($source-context)"/>
                    </xsl:variable>
                    <xsl:if test="$alert-level != ''">
                        <xsl:attribute name="level">
                            <xsl:value-of select="$alert-level"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="value">
                        <xsl:value-of select="$constraint"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xf:bind>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
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
        <xsl:variable name="relevant">
            <xsl:value-of select="enofr:get-relevant($source-context)"/>
        </xsl:variable>
        <xsl:variable name="readonly">
            <xsl:value-of select="enofr:get-readonly($source-context)"/>
        </xsl:variable>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="$relevant"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', $readonly, ')')"
                    />
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
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xf:bind id="{$name}-bind" name="{$name}" nodeset="{$name}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
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
        <xsl:variable name="name">
            <xsl:value-of select="enofr:get-name($source-context)"/>
        </xsl:variable>
        <xsl:variable name="required">
            <xsl:value-of select="enofr:get-required($source-context)"/>
        </xsl:variable>
        <xsl:variable name="relevant">
            <xsl:value-of select="enofr:get-relevant($source-context)"/>
        </xsl:variable>
        <xsl:variable name="calculate">
            <xsl:value-of select="enofr:get-calculate($source-context)"/>
        </xsl:variable>
        <xsl:variable name="type">
            <xsl:value-of select="enofr:get-type($source-context)"/>
        </xsl:variable>
        <xsl:variable name="readonly">
            <xsl:value-of select="enofr:get-readonly($source-context)"/>
        </xsl:variable>
        <xsl:variable name="constraint">
            <xsl:value-of select="enofr:get-constraint($source-context)"/>
        </xsl:variable>
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:if test="not($required = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="$required"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="$relevant"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$calculate != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="$calculate"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$type = 'date'">
                <xsl:attribute name="type">
                    <xsl:value-of select="concat('xf:', $type)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', $readonly, ')')"
                    />
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$constraint != ''">
                <xsl:element name="xf:constraint">
                    <xsl:variable name="alert-level">
                        <xsl:value-of select="enofr:get-alert-level($source-context)"/>
                    </xsl:variable>
                    <xsl:if test="$alert-level != ''">
                        <xsl:attribute name="level">
                            <xsl:value-of select="$alert-level"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="value">
                        <xsl:value-of select="$constraint"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xf:bind>
        <xsl:variable name="name-bis" select="concat($name, '-group')"/>
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
   
    <xd:doc>
        <xd:desc>
            <xd:p>Template for Bind for the DoubleDuration driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Bind//DoubleDuration" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="required">
            <xsl:value-of select="enofr:get-required($source-context)"/>
        </xsl:variable>
        <xsl:variable name="relevant">
            <xsl:value-of select="enofr:get-relevant($source-context)"/>
        </xsl:variable>
        <xsl:variable name="calculate">
            <xsl:value-of select="enofr:get-calculate($source-context)"/>
        </xsl:variable>
        <xsl:variable name="readonly">
            <xsl:value-of select="enofr:get-readonly($source-context)"/>
        </xsl:variable>
        <!-- Creating one element that correspond to the concatenation of the two ones -->
        <xf:bind id="{$name}-bind" name="{$name}" ref="{$name}">
            <xsl:attribute name="calculate">
                <xsl:value-of
                    select="
                        concat('if (not(instance(''fr-form-instance'')//', replace($name, '-', '-A-'),
                        ' castable as xs:integer or instance(''fr-form-instance'')//', replace($name, '-', '-B-'),
                        ' castable as xs:integer)) then '''' else (100*number(if (instance(''fr-form-instance'')//', replace($name, '-', '-A-'),
                        ' castable as xs:integer) then instance(''fr-form-instance'')//', replace($name, '-', '-A-'),
                        ' else 0)+number(if (instance(''fr-form-instance'')//', replace($name, '-', '-B-'),
                        ' castable as xs:integer) then instance(''fr-form-instance'')//', replace($name, '-', '-B-'),
                        ' else 0))')"
                />
            </xsl:attribute>
        </xf:bind>
        <xsl:variable name="nameA" select="replace($name, '-', '-A-')"/>
        <xf:bind id="{$nameA}-bind" name="{$nameA}" ref="{$nameA}">
            <xsl:if test="not($required = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="$required"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="$relevant"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$calculate != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="$calculate"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', $readonly, ')')"
                    />
                </xsl:attribute>
            </xsl:if>
        </xf:bind>
        <xsl:variable name="nameB" select="replace($name, '-', '-B-')"/>
        <xf:bind id="{$nameB}-bind" name="{$nameB}" ref="{$nameB}">
            <xsl:if test="not($required = ('false()', ''))">
                <xsl:attribute name="required">
                    <xsl:value-of select="$required"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$relevant != ''">
                <xsl:attribute name="relevant">
                    <xsl:value-of select="$relevant"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$calculate != ''">
                <xsl:attribute name="calculate">
                    <xsl:value-of select="$calculate"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($readonly = ('false()', ''))">
                <xsl:attribute name="readonly">
                    <xsl:value-of select="concat('not(', $readonly, ')')"
                    />
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
    <xsl:template match="Resource//*" mode="model" priority="-1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}">
            <label>
                <xsl:choose>
                    <xsl:when test="enofr:get-calculate-text($source-context,$language,'label') != ''">
                        <xsl:value-of select="'custom label'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="eno:serialize(enofr:get-label($source-context, $language))"/>
                    </xsl:otherwise>
                </xsl:choose>
            </label>
            <hint>
                <xsl:value-of select="eno:serialize(enofr:get-hint($source-context, $language))"/>
            </hint>
            <help>
                <xsl:value-of select="eno:serialize(enofr:get-help($source-context, $language))"/>
            </help>
            <alert>
                <xsl:choose>
                    <xsl:when test="enofr:get-calculate-text($source-context,$language,'alert') != ''">
                        <xsl:value-of select="'custom alert'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="eno:serialize(enofr:get-alert($source-context, $language))"/>
                    </xsl:otherwise>                    
                </xsl:choose>
            </alert>
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
    <xsl:template match="Resource//*[starts-with(name(), 'xf-select')]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:element name="{enofr:get-name($source-context)}">
            <label>
                <xsl:choose>
                    <xsl:when test="enofr:get-calculate-text($source-context,$language,'label') != ''">
                        <xsl:value-of select="'custom label'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="eno:serialize(enofr:get-label($source-context, $language))"/>
                    </xsl:otherwise>
                </xsl:choose>
            </label>
            <hint>
                <xsl:value-of select="eno:serialize(enofr:get-hint($source-context, $language))"/>
            </hint>
            <help>
                <xsl:value-of select="eno:serialize(enofr:get-help($source-context, $language))"/>
            </help>
            <alert>
                <xsl:choose>
                    <xsl:when test="enofr:get-calculate-text($source-context,$language,'alert') != ''">
                        <xsl:value-of select="'custom alert'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="eno:serialize(enofr:get-alert($source-context, $language))"/>
                    </xsl:otherwise>                    
                </xsl:choose>
            </alert>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Template for Resource for xf-item driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Resource//xf-item" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        
        <xsl:variable name="image">
            <xsl:value-of select="enofr:get-image($source-context)"/>
        </xsl:variable>
        
        <item>
            <label>
                <xsl:choose>
                    <xsl:when test="$image=''">
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
            <xd:p>The xf-item driver produces something only in the Resource part.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[name() = ('Instance', 'Bind', 'Body')]//xf-item" mode="model"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Resource for DoubleDuration driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Resource//DoubleDuration" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:variable name="name">
            <xsl:value-of select="enofr:get-name($source-context)"/>
        </xsl:variable>
        <xsl:variable name="label">
            <xsl:value-of select="eno:serialize(enofr:get-label($source-context, $language))"/>
        </xsl:variable>
        <xsl:variable name="hint">
            <xsl:value-of select="eno:serialize(enofr:get-hint($source-context, $language))"/>
        </xsl:variable>
        <xsl:element name="{replace($name,'-','-A-')}">
            <label>
                <xsl:value-of select="$label"/>
            </label>
            <hint>
                <xsl:value-of select="$hint"/>
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
        <xsl:element name="{replace($name,'-','-B-')}">
            <label>
                <xsl:value-of select="$label"/>
            </label>
            <hint>
                <xsl:value-of select="$hint"/>
            </hint>
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
    <xsl:template match="ResourceBind//*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="language" tunnel="yes"/>
        <xsl:variable name="calculate-label">
            <xsl:value-of select="enofr:get-calculate-text($source-context,$language,'label')"/>
        </xsl:variable>
        <xsl:variable name="calculate-alert">
            <xsl:value-of select="enofr:get-calculate-text($source-context,$language,'alert')"/>
        </xsl:variable>
        <xsl:if test="$calculate-label != '' or $calculate-alert != ''">
            <xsl:variable name="name" select="enofr:get-name($source-context)"/>
            <xf:bind id="{$name}-resource-{$language}-bind" name="{$name}-{$language}-resource"
                ref="{$name}">
                <xsl:if test="$calculate-label != ''">
                    <xf:bind id="{$name}-resource-{$language}-bind-label"
                        name="{$name}-{$language}-resource-label" ref="label">
                        <xsl:attribute name="calculate">
                            <xsl:value-of select="$calculate-label"/>
                        </xsl:attribute>
                    </xf:bind>
                </xsl:if>
                <xsl:if test="$calculate-alert != ''">
                    <xf:bind id="{$name}-resource-{$language}-bind-alert"
                        name="{$name}-{$language}-resource-alert" ref="alert">
                        <xsl:attribute name="calculate">
                            <xsl:value-of select="$calculate-alert"/>
                        </xsl:attribute>
                    </xf:bind>
                </xsl:if>
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
        <xsl:variable name="label">
            <xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
        </xsl:variable>
        <xsl:variable name="css-class">
            <xsl:value-of select="enofr:get-css-class($source-context)"/>
        </xsl:variable>
        <xhtml:div>
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="$css-class"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($label = '')">
                <xhtml:h3>
                    <xf:output id="{$name}-control" bind="{$name}-bind">
                        <xf:label ref="$form-resources/{$name}/label">
                            <xsl:if test="$css-class != ''">
                                <xsl:attribute name="class">
                                    <xsl:value-of select="$css-class"/>
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

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the Group driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//Group" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="label">
            <xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
        </xsl:variable>
        <xsl:variable name="css-class">
            <xsl:value-of select="enofr:get-css-class($source-context)"/>
        </xsl:variable>
        <xhtml:div>
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="$css-class"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="not($label = '')">
                <xhtml:h4>
                    <xf:output id="{$name}-control" bind="{$name}-bind">
                        <xf:label ref="$form-resources/{$name}/label">
                            <xsl:if test="$css-class != ''">
                                <xsl:attribute name="class">
                                    <xsl:value-of select="$css-class"/>
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
            <xd:p>Template for Body for the MultipleQuestion driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//MultipleQuestion" mode="model">
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
            <xd:p>Default template for Body for the drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//*" mode="model" priority="-1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:param name="instance-ancestor" tunnel="yes"/>
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="appearance">
            <xsl:value-of select="enofr:get-appearance($source-context)"/>
        </xsl:variable>
        <xsl:variable name="css-class">
            <xsl:value-of select="enofr:get-css-class($source-context)"/>
        </xsl:variable>
        <xsl:variable name="length">
            <xsl:value-of select="enofr:get-length($source-context)"/>
        </xsl:variable>
        <xsl:variable name="suffix">
            <xsl:value-of select="enofr:get-suffix($source-context, $languages[1])"/>
        </xsl:variable>
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
            <xsl:if test="$appearance != ''">
                <xsl:attribute name="appearance">
                    <xsl:value-of select="$appearance"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="$css-class"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="xxf:order">
                <xsl:value-of select="'label control hint help alert'"/>
            </xsl:attribute>
            <xsl:if test="not($length='')">
                <xsl:attribute name="xxf:maxlength">
                    <xsl:value-of select="$length"/>
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
                    if="{concat('substring-after(',$instance-ancestor,$name,','' '') ne ''''')}">
                    <xf:setvalue ref="{concat($instance-ancestor,$name)}"
                        value="{concat('substring-after(',$instance-ancestor,$name,','' '')')}"/>
                </xf:action>
            </xsl:if>
            <!-- For each element which relevance depends on this field, we erase the data if it became unrelevant -->
            <xsl:for-each select="enofr:get-relevant-dependencies($source-context)">
                <xf:action ev:event="xforms-value-changed"
                    if="{concat('not(xxf:evaluate-bind-property(''',.,'-bind'',''relevant''))')}"
                    iterate="{concat($instance-ancestor,.,'//*[not(descendant::*)]')}">
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
        <xsl:if test="not($suffix='')">
            <xsl:element name="xhtml:span">
                <xsl:attribute name="class" select="'suffixe'"/>
                <xsl:copy-of select="$suffix"
                    copy-namespaces="no"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>Template to add mediatype html/css to rich text items</xd:desc>
    </xd:doc>
    
    <xsl:template match="Rich-Body//xf-item" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        
        <xsl:if test="enofr:get-image($source-context) !='' or eno:is-rich-content(enofr:get-label($source-context, $languages[1]))">
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
        <xsl:variable name="css-class">
            <xsl:value-of select="enofr:get-css-class($source-context)"/>
        </xsl:variable>
        <xhtml:table name="{enofr:get-name($source-context)}">
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="$css-class"/>
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
        <xsl:apply-templates select="$table-title//xf-output" mode="model"/>
        
        <xsl:variable name="ancestors">
            <xsl:copy-of select="root(.)"/>
        </xsl:variable>
        
        <xsl:variable name="name" select="enofr:get-name($source-context)"/>
        <xsl:variable name="css-class">
            <xsl:value-of select="enofr:get-css-class($source-context)"/>
        </xsl:variable>
        
        <xhtml:table name="{$name}">
            <xsl:if test="$css-class != ''">
                <xsl:attribute name="class">
                    <xsl:value-of select="$css-class"/>
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
                <xf:repeat
                    nodeset="{concat($instance-ancestor,$name,'-RowLoop')}"
                    id="{concat($name,'-RowLoop')}">
                    <xhtml:tr>
                        <xsl:apply-templates select="enofr:get-body-line($source-context, 1)"
                            mode="source">
                            <xsl:with-param name="driver"
                                select="$ancestors//*[not(child::*) and not(name() = 'driver')]"
                                tunnel="yes"/>
                            <xsl:with-param name="instance-ancestor"
                                select="concat($instance-ancestor,'*[name()=''',$name,
                                ''' and count(preceding-sibling::*)=count(current()/ancestor::*[name()=''',
                                $name,''']/preceding-sibling::*)]//')"
                                tunnel="yes"/>
                        </xsl:apply-templates>
                    </xhtml:tr>
                </xf:repeat>
            </xhtml:tbody>
        </xhtml:table>
        <xf:trigger>
            <xf:label ref="$form-resources/AddLine/label"/>
            <xf:insert ev:event="DOMActivate" context="."
                nodeset="{concat('//',$name,'-RowLoop')}"
                origin="{concat('instance(''fr-form-loop-model'')/',$name,'-RowLoop')}"
            />
        </xf:trigger>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the TextCell driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//TextCell" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xsl:variable name="depth">
            <xsl:value-of select="enofr:get-code-depth($source-context)"/>
        </xsl:variable>
        <xhtml:th colspan="{enofr:get-colspan($source-context)}"
            rowspan="{enofr:get-rowspan($source-context)}">
            <xsl:if
                test="$depth!='1' and $depth!=''">
                <xsl:attribute name="class"
                    select="concat('depth',$depth)"/>
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
            <xd:p>Template for Body for the EmptyCell driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Body//EmptyCell" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="languages" tunnel="yes"/>
        <xhtml:td colspan="{enofr:get-colspan($source-context)}"/>
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
        <xf:repeat nodeset="{concat($instance-ancestor,$loop-name)}"
            id="{$loop-name}">
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
                <xsl:with-param name="instance-ancestor"
                    select="concat($instance-ancestor,'*[name()=''',$loop-name,
                    ''' and count(preceding-sibling::*)=count(current()/ancestor::*[name()=''',
                    $loop-name,''']/preceding-sibling::*)]//')"
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
        <xsl:variable name="appearance">
            <xsl:value-of select="enofr:get-appearance($source-context)"/>
        </xsl:variable>
        <xsl:variable name="length">
            <xsl:value-of select="enofr:get-length($source-context)"/>
        </xsl:variable>
        <xsl:variable name="name" select="replace(enofr:get-name($source-context), '-', '-A-')"/>
        <xsl:element name="xf:select1">
            <xsl:attribute name="id">
                <xsl:value-of select="concat($name, '-control')"/>
            </xsl:attribute>
            <xsl:attribute name="bind">
                <xsl:value-of select="concat($name, '-bind')"/>
            </xsl:attribute>
            <xsl:if test="$appearance != ''">
                <xsl:attribute name="appearance">
                    <xsl:value-of select="$appearance"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="class">
                <xsl:value-of select="'double-duration'"/>
            </xsl:attribute>
            <xsl:attribute name="xxf:order">
                <xsl:value-of select="'label control hint help alert'"/>
            </xsl:attribute>
            <xsl:if test="not($length='')">
                <xsl:attribute name="xxf:maxlength">
                    <xsl:value-of select="$length"/>
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
            <xsl:if test="$appearance != ''">
                <xsl:attribute name="appearance">
                    <xsl:value-of select="$appearance"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="class">
                <xsl:value-of select="'double-duration'"/>
            </xsl:attribute>
            <xsl:attribute name="xxf:order">
                <xsl:value-of select="'label control hint help alert'"/>
            </xsl:attribute>
            <xsl:if test="$length">
                <xsl:attribute name="xxf:maxlength">
                    <xsl:value-of select="$length"/>
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

    <xd:doc>
        <xd:desc>
            <xd:p>Template for Body for the ResponseElement and CalculatedVariable drivers.</xd:p>
            <xd:p>It corresponds to elements which will be present in the Instance and Bind but not in the Resource and the Body.</xd:p>
            <xd:p>Their prefilled value can have an impact on other elements of the form.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[name() = ('Resource', 'Body')]//*[name() = ('ResponseElement','CalculatedVariable')]"
        mode="model"/>
    
</xsl:stylesheet>
