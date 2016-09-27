<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:il="http://xml/insee.fr/xslt/lib"
    xmlns:iatxml="http://xml/insee.fr/xslt/apply-templates/xml"
    xmlns:iatxsl="http://xml/insee.fr/xslt/apply-templates/xsl"
    exclude-result-prefixes="xs xd" version="2.0">

    <xsl:import href="../inputs/xml/source.xsl"/>
    <xsl:import href="../outputs/xsl/models.xsl"/>
    <xsl:import href="../lib.xsl"/>

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
        <xsl:apply-templates select="il:append-empty-element('Sheet',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the 'Driver' driver to a GenericElement element if the xpath is given and a driver is linked.</xd:p>
        <xd:p>This covers the case where we link an output driver to an input element</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@nom='Xpath']/text()!='' and DefinedElement[@nom='Driver']]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('Template',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the simple implementation driver to a GenericElement element when it has : an xpath, a match but no mode</xd:p>
        <xd:p>This covers the case where we implement a function for a given source element, and we only return this element's value</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@nom='Xpath']/text()!='' and DefinedElement[@nom='Match']/text()!='' and not(DefinedElement[@nom='Match_Mode']/text()!='')]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('SimpleImplementation',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the complex implementation driver to a GenericElement element when it has : an xpath, a match and a mode.
        </xd:p>
        <xd:p>This covers the case where we implement a function for a given source element, and we return something more xomplex (using a mode)</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@nom='Xpath']/text()!='' and DefinedElement[@nom='Match']/text()!='' and DefinedElement[@nom='Match_Mode']/text()!='']" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('ComplexImplementation',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the empty-implementation driver to a GenericElement element when it has : an xpath, no driver and no match.
        </xd:p>
        <xd:p>This covers the case where we implement a function for a given source element, and nothing is returned</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@nom='Xpath']/text()!='' and not(DefinedElement[@nom='Driver'] or DefinedElement[@nom='Match']/text()!='')]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('EmptyImplementation',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the function driver to GenericElement element where a function is provided</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@nom='Out_Function']/text()!='' and DefinedElement[@nom='In_Function']/text()]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('TransitionFunction',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the NotSupportedFunction driver to a GenericElement element where no function is provided</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@nom='Out_Function']/text()!='' and not(DefinedElement[@nom='In_Function']/text())]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:message>Je ne suis pas supporté</xsl:message>
        <xsl:apply-templates select="il:append-empty-element('NotSupportedFunction',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the function driver to a genricElement element where a function is provided</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@nom='Function']/text()!='']" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('SourceFunction',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking the GetChildren driver to a GenericElement element where a parent is provided</xd:p>
    </xd:desc>
    <xsl:template match="GenericElement[DefinedElement[@nom='Parent']/text()!='']" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('GetChildren',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>Linking a documentation sending function a the documentation getter function</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-documentation">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-documentation"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-documentation">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Documentation'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The xpath getter function returns the associated value from Xpath name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-xpath">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-xpath"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-xpath">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Xpath'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The xpath mode function returns the associated value from Xpath_Mode name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-mode-xpath">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-mode-xpath"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-mode-xpath">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Xpath_Mode'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The match getter function returns the associated value from Match name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-match">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-match"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-match">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Match'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The match mode getter function returns the associated value from Match_Mode name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-match-mode">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-match-mode"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-match-mode">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Match_Mode'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The function getter function returns the associated value from Function name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-function">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-function"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-function">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Function'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The out-function getter function returns the associated value from Out_Function name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-output-function">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-output-function"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-output-function">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Out_Function'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The in-function getter function returns the associated value from In_Function name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-input-function">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-input-function"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-input-function">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='In_Function'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The driver getter function returns the associated value from Driver name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-driver">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-driver"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-driver">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Driver'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The associatedFunction getter function returns the associated value from Parameters name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-parameters" as="xs:string *">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-parameters"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-parameters">
        <xsl:call-template name="split">
            <xsl:with-param name="chain" select="iatxml:get-value(./DefinedElement[@nom='Parameters'])"/>
        </xsl:call-template>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The parent getter function returns the associated value from Parent name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-parent">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-parent"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-parent">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Parent'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The return-type-getter function returns the associated value from As name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-as">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-as"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-as">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='As'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>The children-getter function returns the associated vluae from Children name element</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-children">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-children"/>
    </xsl:function>
    
    <xsl:template match="GenericElement" mode="iatxml:get-children">
        <xsl:value-of select="iatxml:get-value(./DefinedElement[@nom='Children'])"/>
    </xsl:template>
    
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
