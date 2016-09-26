<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
    xmlns:iatxsl="http://xml/insee.fr/xslt/apply-templates/xsl"
    exclude-result-prefixes="xd" version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 6, 2013</xd:p>
            <xd:p>Generation of XSL!</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:output name="concise-xml" method="xml" indent="no" omit-xml-declaration="yes"
        exclude-result-prefixes="#all"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The default element to match</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="sheet" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <!-- Creating the root element of an xsl sheet -->
        <xsl:element name='xsl:stylesheet'>
            <!-- This will call children elements that will create an xml structure -->
            <xsl:apply-templates select="iat:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A template</xd:p>
            <xd:p>Calls a function to get the element in charge of the documentation creation.</xd:p>
            <xd:p>Calls a function to get the xpath to match.</xd:p>
            <xd:p>Calls a function to get the driver to launch linked to the xpath.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="template" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="'source'"/>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'driver'"/>
                <xsl:attribute name="tunnel" select="'yes'"/>
                <xsl:element name="driver"/>
            </xsl:element>
            <xsl:element name="xsl:apply-templates">
                <xsl:attribute name="select"
                    select="concat('il:append-empty-element(''',normalize-space(iatxsl:get-driver($source-context)),''',$driver)')"/>
                <xsl:attribute name="mode" select="'model'"/>
                <xsl:element name="xsl:with-param">
                    <xsl:attribute name="name" select="'source-context'"/>
                    <xsl:attribute name="select" select="'.'"/>
                    <xsl:attribute name="tunnel" select="'yes'"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A function</xd:p>
            <xd:p>Calls a function to get element in charge of the documentation creation.</xd:p>
            <xd:p>Calls a function that gets the name of the function.</xd:p>
            <xd:p>Calls a function to get the value of the linked function (source side).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="TransitionFunction" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:function">
            <xsl:attribute name="name" select="normalize-space(iatxsl:get-output-function($source-context))"/>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'context'"/>
                <xsl:attribute name="as" select="'item()'"/>
            </xsl:element>
            <xsl:variable name="parameters" select="iatxsl:get-parameters($source-context)" as="xs:string +"/>
            <xsl:if test="$parameters!=''">
                <xsl:for-each select="$parameters">
                    <xsl:element name="xsl:param">
                        <xsl:attribute name="name" select="current()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:if>
            <xsl:variable name="function-parameters">
                <xsl:text>$context</xsl:text>
                <xsl:if test="$parameters!=''">
                    <xsl:for-each select="$parameters">
                        <xsl:text>,$</xsl:text>
                        <xsl:value-of select="."/>
                    </xsl:for-each>
                </xsl:if>
            </xsl:variable>
            <xsl:element name="xsl:sequence">
                <xsl:attribute name="select"
                    select="concat(normalize-space(iatxsl:get-input-function($source-context)),'(',$function-parameters/text(),')')"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Not supported function</xd:p>
            <xd:p>A not yet supported function.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="NotSupportedFunction" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:function">
            <xsl:attribute name="name" select="normalize-space(iatxsl:get-output-function($source-context))"/>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'context'"/>
                <xsl:attribute name="as" select="'item()'"/>
            </xsl:element>
            <xsl:variable name="parameters" select="iatxsl:get-parameters($source-context)" as="xs:string +"/>
            <xsl:if test="$parameters!=''">
                <xsl:for-each select="$parameters">
                    <xsl:element name="xsl:param">
                        <xsl:attribute name="name" select="current()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:if>
            <xsl:element name="xsl:text"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A simple implementation of a source function</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="SimpleImplementation" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="normalize-space(iatxsl:get-mode-xpath($source-context))"/>
            <xsl:element name="xsl:value-of">
                <xsl:attribute name="select" select="normalize-space(iatxsl:get-match($source-context))"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A complex implementation of a source function</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="ComplexImplementation" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="normalize-space(iatxsl:get-mode-xpath($source-context))"/>
            <xsl:element name="xsl:apply-templates">
                <xsl:attribute name="select" select="normalize-space(iatxsl:get-match($source-context))"/>
                <xsl:attribute name="mode" select="normalize-space(iatxsl:get-match-mode($source-context))"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A source function implementation that won't return anything</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="EmptyImplementation" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="normalize-space(iatxsl:get-mode-xpath($source-context))"/>
            <xsl:element name="xsl:text"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A function defined for the source</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="SourceFunction" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="function-name">
            <xsl:value-of select="normalize-space(iatxsl:get-function($source-context))"/>
        </xsl:variable>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:function">
            <xsl:attribute name="name" select="$function-name"/>
            <xsl:variable name="type" select="iatxsl:get-as($source-context)"/>
            <xsl:if test="$type!=''">
                <xsl:attribute name="as" select="$type"/>
            </xsl:if>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'context'"/>
                <xsl:attribute name="as" select="'item()'"/>
            </xsl:element>
            <xsl:variable name="parameters" select="iatxsl:get-parameters($source-context)" as="xs:string +"/>
            <xsl:if test="$parameters!=''">
                <xsl:for-each select="$parameters">
                    <xsl:element name="xsl:param">
                        <xsl:attribute name="name" select="current()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:if>
            <xsl:element name="xsl:apply-templates">
                <xsl:attribute name="select" select="'$context'"/>
                <xsl:attribute name="mode" select="$function-name"/>
                <xsl:if test="$parameters!=''">
                    <xsl:for-each select="$parameters">
                        <xsl:element name="xsl:with-param">
                            <xsl:attribute name="name" select="current()"/>
                            <xsl:attribute name="select" select="concat('$',current())"/>
                            <xsl:attribute name="tunnel" select="'yes'"/>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:if>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A template used to get children</xd:p>
            <xd:p>Calls a function that gets the element in charge of the documentation creation.</xd:p>
            <xd:p>Calls a function that gets the xpath to match (parent).</xd:p>
            <xd:p>Calls a function that gets the xpath returned (children).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="GetChildren" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:comment select="iatxsl:get-documentation($source-context)"/>
        <xsl:text>&#xA;</xsl:text>
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(iatxsl:get-parent($source-context))"/>
            <xsl:attribute name="mode" select="'iat:child-fields'"/>
            <xsl:attribute name="as" select="'node()*'"/>
            <xsl:element name="xsl:sequence">
                <xsl:attribute name="select"
                    select="normalize-space(iatxsl:get-children($source-context))"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>

</xsl:stylesheet>
