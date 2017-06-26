<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoxsl="http://xml.insee.fr/apps/eno/xsl"
    version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>An xslt stylesheet who transforms an input into XSL through generic driver templates.</xd:p>
            <xd:p>The real input is mapped with the drivers.</xd:p>
            <xd:p>The generated XSL follows some Eno patterns.</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>The default element to match :</xd:p>
            <xd:p>it creates the root of an xslt stylesheet.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Sheet" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <!-- Creating the root element of an xsl sheet -->
        <xsl:element name='xsl:stylesheet'>
            <!-- This will call children elements that will create an xml structure -->
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A driver to generate an Eno xsl template.</xd:p>
            <xd:p>Calls a function to get the xpath to match.</xd:p>
            <xd:p>Calls a function to get the driver to launch, linked to the xpath.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Template" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <!-- Call of this template to write comments -->
        <xsl:call-template name="documentation">
            <xsl:with-param name="context" select="$source-context"/>
        </xsl:call-template>
        <xsl:text>&#xA;</xsl:text>
        <!-- The generated template follows some Eno pattern rules -->
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(enoxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="'source'"/>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'driver'"/>
                <xsl:attribute name="tunnel" select="'yes'"/>
                <xsl:element name="driver"/>
            </xsl:element>
            <xsl:element name="xsl:apply-templates">
                <xsl:attribute name="select"
                    select="concat('eno:append-empty-element(''',normalize-space(enoxsl:get-driver($source-context)),''',$driver)')"/>
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
            <xd:p>A driver to link a function (on the output side) to another one (on the input side).</xd:p>
            <xd:p>Calls a function that gets the name of the function.</xd:p>
            <xd:p>Calls a function to get the value of the linked function (input side).</xd:p>
            <xd:p>Calls a function to get the parameters of the function.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="TransitionFunction" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <!-- Call of this template to write comments -->
        <xsl:call-template name="documentation">
            <xsl:with-param name="context" select="$source-context"/>
        </xsl:call-template>
        <xsl:text>&#xA;</xsl:text>
        <!-- The generated function follows some Eno pattern rules -->
        <xsl:element name="xsl:function">
            <xsl:attribute name="name" select="normalize-space(enoxsl:get-output-function($source-context))"/>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'context'"/>
                <xsl:attribute name="as" select="'item()'"/>
            </xsl:element>
            <xsl:variable name="parameters" select="enoxsl:get-parameters($source-context)" as="xs:string +"/>
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
                    select="concat(normalize-space(enoxsl:get-input-function($source-context)),'(',$function-parameters/text(),')')"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A driver to generate something for the output function (returns empty text value) when there is no input function linked to it.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="NotSupportedFunction" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:text>&#xA;</xsl:text>
        <!-- The generated function follows some Eno pattern rules -->
        <xsl:element name="xsl:function">
            <xsl:attribute name="name" select="normalize-space(enoxsl:get-output-function($source-context))"/>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'context'"/>
                <xsl:attribute name="as" select="'item()'"/>
            </xsl:element>
            <xsl:variable name="parameters" select="enoxsl:get-parameters($source-context)" as="xs:string +"/>
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
            <xd:p>An implementation of a source function.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Implementation" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <!-- Call of this template to write comments -->
        <xsl:call-template name="documentation">
            <xsl:with-param name="context" select="$source-context"/>
        </xsl:call-template>
        <xsl:text>&#xA;</xsl:text>
        
        <!-- The generated template follows some Eno pattern rules -->
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(enoxsl:get-xpath($source-context))"/>
            <xsl:attribute name="mode" select="normalize-space(enoxsl:get-xpath-mode($source-context))"/>
            
            <xsl:variable name="select" select="normalize-space(enoxsl:get-match($source-context))"></xsl:variable>
            <xsl:variable name="mode" select="normalize-space(enoxsl:get-match-mode($source-context))" as="xs:string"/>
            
            <xsl:choose>
                <xsl:when test="$mode=''">
                    <xsl:element name="xsl:value-of">
                        <xsl:attribute name="select" select="$select"/>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:element name="xsl:apply-templates">
                        <xsl:attribute name="select" select="$select"/>
                        <xsl:attribute name="mode" select="$mode"/>
                    </xsl:element>
                </xsl:otherwise>
            </xsl:choose>
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
            <xsl:value-of select="normalize-space(enoxsl:get-function($source-context))"/>
        </xsl:variable>
        <!-- Call of this template to write comments -->
        <xsl:call-template name="documentation">
            <xsl:with-param name="context" select="$source-context"/>
        </xsl:call-template>
        <xsl:text>&#xA;</xsl:text>
        <!-- The generated function follows some Eno pattern rules -->
        <xsl:element name="xsl:function">
            <xsl:attribute name="name" select="$function-name"/>
            <xsl:variable name="type" select="enoxsl:get-as($source-context)"/>
            <xsl:if test="$type!=''">
                <xsl:attribute name="as" select="$type"/>
            </xsl:if>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name" select="'context'"/>
                <xsl:attribute name="as" select="'item()'"/>
            </xsl:element>
            <xsl:variable name="parameters" select="enoxsl:get-parameters($source-context)" as="xs:string +"/>
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
            <xd:p>Calls a function that gets the xpath to match (parent).</xd:p>
            <xd:p>Calls a function that gets the xpath returned (children).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="GetChildren" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <!-- Call of this template to write comments -->
        <xsl:call-template name="documentation">
            <xsl:with-param name="context" select="$source-context"/>
        </xsl:call-template>
        <xsl:text>&#xA;</xsl:text>
        <!-- The generated template follows some Eno pattern rules -->
        <xsl:element name="xsl:template">
            <xsl:attribute name="match" select="normalize-space(enoxsl:get-parent($source-context))"/>
            <xsl:attribute name="mode" select="'eno:child-fields'"/>
            <xsl:attribute name="as" select="'node()*'"/>
            <xsl:element name="xsl:sequence">
                <xsl:attribute name="select"
                    select="normalize-space(enoxsl:get-children($source-context))"/>
            </xsl:element>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A template to write oXygen documentation elements with their content.</xd:p>
            <xd:p>Calls a function to get the documentation content.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="documentation">
        <xsl:param name="context"/>
        <xsl:element name="xd:doc">
            <xsl:element name="xd:desc">
                <xsl:element name="xd:p">
                    <xsl:value-of select="enoxsl:get-documentation($context)"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
