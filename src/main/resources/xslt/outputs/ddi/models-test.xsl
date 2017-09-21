<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:g="ddi:group:3_2"
    xmlns:d="ddi:datacollection:3_2" xmlns:s="ddi:studyunit:3_2" xmlns:r="ddi:reusable:3_2"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:a="ddi:archive:3_2"
    xmlns:l="ddi:logicalproduct:3_2" xmlns:enoddi32="http://xml.insee.fr/apps/eno/out/ddi32"
    exclude-result-prefixes="#all" version="2.0">
    
    <xsl:template match="*" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:copy>
            <xsl:apply-templates select="$source-context" mode="test-getter"/>
            
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*" mode="test-getter">
        <get-citation><xsl:value-of select="enoddi32:get-citation(.)"/></get-citation>
        <get-agency><xsl:value-of select="enoddi32:get-agency(.)"/></get-agency>
        <get-id><xsl:value-of select="concat('INSEE-', enoddi32:get-id(.))"/></get-id>
        <get-parent-id><xsl:value-of select="enoddi32:get-parent-id(.)"/></get-parent-id>
        <get-lang><xsl:value-of select="enoddi32:get-lang(.)"/></get-lang>
        <get-name><xsl:value-of select="enoddi32:get-name(.)"/></get-name>
        <get-text><xsl:value-of select="enoddi32:get-text(.)"/></get-text>
        <get-label><xsl:value-of select="enoddi32:get-label(.)"/></get-label>
        <get-value><xsl:value-of select="enoddi32:get-value(.)"/></get-value>
        <get-lang><xsl:value-of select="enoddi32:get-lang(.)"/></get-lang>
        <get-version><xsl:value-of select="enoddi32:get-version(.)"/></get-version>
        <get-generic-output-format><xsl:value-of select="enoddi32:get-generic-output-format(.)"/></get-generic-output-format>
        <get-type><xsl:value-of select="enoddi32:get-type(.)"/></get-type>
        <get-type-name><xsl:value-of select="enoddi32:get-type-name(.)"/></get-type-name>
        <get-max-length><xsl:value-of select="enoddi32:get-max-length(.)"/></get-max-length>
        <get-mandatory><xsl:value-of select="enoddi32:get-mandatory(.)"/></get-mandatory>
        <get-sequence-type><xsl:value-of select="enoddi32:get-sequence-type(.)"/></get-sequence-type>
        <!--<get-sequences><xsl:sequence select="enoddi32:get-sequences(.)"/></get-sequences>
        <get-questions><xsl:sequence select="enoddi32:get-questions(.)"/></get-questions>--> 
        <get-instructions><xsl:copy-of select="enoddi32:get-instructions(.)"/></get-instructions> 
        <get-decimal-positions><xsl:copy-of select="enoddi32:get-decimal-positions(.)"/></get-decimal-positions>
        <get-dynamic><xsl:value-of select="enoddi32:get-dynamic(.)"/></get-dynamic>
    </xsl:template>
    
    
</xsl:stylesheet>
