<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoxml="http://xml.insee.fr/apps/eno/xml"
    xmlns:enoxsl="http://xml.insee.fr/apps/eno/xsl"
    exclude-result-prefixes="xs xd" version="2.0">

    <!-- xsl stylesheet applied to xml.tmp in the temporary process of xsl files creation (fods2xml then xml2xsl) -->
    <!-- This stylesheet will read the xml.tmp, get the different informations required (with source.xsl) -->
    <!-- models.xml will then use the different retrieved information to create the desired .xsl file -->
    <!-- which can be drivers.xsl, templates.xsl... given the state of the build process -->
    <!-- The content of this file (xml2xsl.xsl) will help linking the different elements with each other -->
    <!-- Particularly by linking drivers to the different elements so that xsl/models.xsl can read it and -->
    <!-- create to desired output file. -->
    <!-- lib.xsl : used to parse a file with defined constraints -->

    <!-- Importing the different resources -->
    <xsl:import href="../inputs/xml/source.xsl"/>
    <xsl:import href="../outputs/xsl/models.xsl"/>
    <xsl:import href="../lib.xsl"/>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 7, 2013</xd:p>
            <xd:p>Transforms XML into XSL!</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template match="/">
        <xsl:apply-templates select="/" mode="source"/>
    </xsl:template>

    <xd:desc>
        <xd:p>Linking an xsl sheet to the root element of the xml file</xd:p>
    </xd:desc>
    <xsl:template match="Root" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('Sheet',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the 'Driver' driver to a GenericElement element if the xpath is given and a driver is linked.</xd:p>
        <xd:p>This covers the case where we link an output driver to an input element</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@name='Xpath']/text()!='' and DefinedElement[@name='Driver']]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('Template',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the simple implementation driver to a GenericElement element when it has : an xpath, a match but no mode</xd:p>
        <xd:p>This covers the case where we implement a function for a given source element, and we only return this element's value</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@name='Xpath']/text()!='' and DefinedElement[@name='Match']/text()!='' and not(DefinedElement[@name='Match_Mode']/text()!='')]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('SimpleImplementation',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the complex implementation driver to a GenericElement element when it has : an xpath, a match and a mode.
        </xd:p>
        <xd:p>This covers the case where we implement a function for a given source element, and we return something more xomplex (using a mode)</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@name='Xpath']/text()!='' and DefinedElement[@name='Match']/text()!='' and DefinedElement[@name='Match_Mode']/text()!='']" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('ComplexImplementation',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the empty-implementation driver to a GenericElement element when it has : an xpath, no driver and no match.
        </xd:p>
        <xd:p>This covers the case where we implement a function for a given source element, and nothing is returned</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@name='Xpath']/text()!='' and not(DefinedElement[@name='Driver'] or DefinedElement[@name='Match']/text()!='')]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('EmptyImplementation',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the function driver to GenericElement element where an input function is provided</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@name='Out_Function']/text()!='' and DefinedElement[@name='In_Function']/text()]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('TransitionFunction',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the NotSupportedFunction driver to a GenericElement element where no input function is provided</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@name='Out_Function']/text()!='' and not(DefinedElement[@name='In_Function']/text())]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:message>Je ne suis pas supporté</xsl:message>
        <xsl:apply-templates select="eno:append-empty-element('NotSupportedFunction',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the function driver to a genericElement element where a function is provided</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@name='Function']/text()!='']" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('SourceFunction',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the GetChildren driver to a GenericElement element where a parent is provided</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@name='Parent']/text()!='']" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="eno:append-empty-element('GetChildren',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking a documentation sending function a the documentation getter function</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-documentation">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-documentation"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-documentation">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Documentation'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The xpath getter function returns the associated value from Xpath name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-xpath">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-xpath"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-xpath">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Xpath'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The xpath mode function returns the associated value from Xpath_Mode name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-mode-xpath">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-mode-xpath"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-mode-xpath">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Xpath_Mode'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The match getter function returns the associated value from Match name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-match">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-match"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-match">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Match'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The match mode getter function returns the associated value from Match_Mode name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-match-mode">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-match-mode"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-match-mode">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Match_Mode'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The function getter function returns the associated value from Function name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-function">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-function"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-function">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Function'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The out-function getter function returns the associated value from Out_Function name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-output-function">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-output-function"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-output-function">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Out_Function'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The in-function getter function returns the associated value from In_Function name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-input-function">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-input-function"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-input-function">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='In_Function'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The driver getter function returns the associated value from Driver name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-driver">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-driver"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-driver">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Driver'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The associatedFunction getter function returns the associated value from Parameters name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-parameters" as="xs:string *">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-parameters"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-parameters">
        <xsl:call-template name="split">
            <xsl:with-param name="chain" select="enoxml:get-value(./DefinedElement[@name='Parameters'])"/>
        </xsl:call-template>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The parent getter function returns the associated value from Parent name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-parent">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-parent"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-parent">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Parent'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The return-type-getter function returns the associated value from As name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-as">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-as"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="enoxml:get-as">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='As'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The children-getter function returns the associated vluae from Children name element</xd:p>
    </xd:desc>
    <xsl:function name="enoxsl:get-children">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoxml:get-children"/>
    </xsl:function>

    <xsl:template match="GenericElement" mode="enoxml:get-children">
        <xsl:value-of select="enoxml:get-value(./DefinedElement[@name='Children'])"/>
    </xsl:template>
    

    <!-- Template called when matching with the Parameters name element -->
    <!-- Recursively splits a string chain on the "," character -->
    <xsl:template name="split">
        <xsl:param name="chain"/>
        <xsl:choose>
            <xsl:when test="contains($chain,',')">
                <xsl:value-of select="substring-before($chain,',')"/>
                <xsl:call-template name="split">
                    <xsl:with-param name="chain" select="substring-after($chain,',')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$chain"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>
