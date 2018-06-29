<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
                xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:eno="http://xml.insee.fr/apps/eno"
                xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
                version="2.0"
                exclude-result-prefixes="#all">
   <xsl:param name="properties-file"/>
   <xsl:variable name="properties" select="doc($properties-file)"/>
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
      <getters-calls>
         <get-form-title>
            <xsl:value-of select="enopdf:get-form-title(.,'fr')"/>
         </get-form-title>

         <get-application-name>
            <xsl:value-of select="enopdf:get-application-name(.)"/>
         </get-application-name>

         <get-form-name>
            <xsl:value-of select="enopdf:get-form-name(.)"/>
         </get-form-name>

         <get-name>
            <xsl:value-of select="enopdf:get-name(.)"/>
         </get-name>

         <get-relevant>
            <xsl:value-of select="enopdf:get-relevant(.)"/>
         </get-relevant>

         <get-readonly>
            <xsl:value-of select="enopdf:get-readonly(.)"/>
         </get-readonly>

         <get-calculate>
            <xsl:value-of select="enopdf:get-calculate(.)"/>
         </get-calculate>

         <get-type>
            <xsl:value-of select="enopdf:get-type(.)"/>
         </get-type>

         <get-format>
            <xsl:value-of select="enopdf:get-format(.)"/>
         </get-format>

         <get-constraint>
            <xsl:value-of select="enopdf:get-constraint(.)"/>
         </get-constraint>

         <get-alert-level>
            <xsl:value-of select="enopdf:get-alert-level(.)"/>
         </get-alert-level>

         <get-help>
            <xsl:value-of select="enopdf:get-help(.,'fr')"/>
         </get-help>

         <get-label>
            <xsl:value-of select="enopdf:get-label(.,'fr')"/>
         </get-label>

         <get-value>
            <xsl:value-of select="enopdf:get-value(.)"/>
         </get-value>

         <get-appearance>
            <xsl:value-of select="enopdf:get-appearance(.)"/>
         </get-appearance>

         <get-css-class>
            <xsl:value-of select="enopdf:get-css-class(.)"/>
         </get-css-class>

         <get-length>
            <xsl:value-of select="enopdf:get-length(.)"/>
         </get-length>

         <get-suffix>
            <xsl:value-of select="enopdf:get-suffix(.,'fr')"/>
         </get-suffix>

         <get-header-columns>
            <xsl:value-of select="enopdf:get-header-columns(.)"/>
         </get-header-columns>

         <get-header-lines>
            <xsl:value-of select="enopdf:get-header-lines(.)"/>
         </get-header-lines>

         <get-body-lines>
            <xsl:value-of select="enopdf:get-body-lines(.)"/>
         </get-body-lines>

         <get-header-line>
            <xsl:value-of select="enopdf:get-header-line(.,0)"/>
         </get-header-line>

         <get-body-line>
            <xsl:value-of select="enopdf:get-body-line(.,0)"/>
         </get-body-line>

         <get-rowspan>
            <xsl:value-of select="enopdf:get-rowspan(.)"/>
         </get-rowspan>

         <get-colspan>
            <xsl:value-of select="enopdf:get-colspan(.)"/>
         </get-colspan>

         <get-minimum-lines>
            <xsl:value-of select="enopdf:get-minimum-lines(.)"/>
         </get-minimum-lines>

         <get-constraint-dependencies>
            <xsl:value-of select="enopdf:get-constraint-dependencies(.)"/>
         </get-constraint-dependencies>

         <get-relevant-dependencies>
            <xsl:value-of select="enopdf:get-relevant-dependencies(.)"/>
         </get-relevant-dependencies>

         <get-readonly-dependencies>
            <xsl:value-of select="enopdf:get-readonly-dependencies(.)"/>
         </get-readonly-dependencies>

         <get-code-depth>
            <xsl:value-of select="enopdf:get-code-depth(.)"/>
         </get-code-depth>

         <get-image>
            <xsl:value-of select="enopdf:get-image(.)"/>
         </get-image>

         <is-first>
            <xsl:value-of select="enopdf:is-first(.)"/>
         </is-first>

         <get-ddi-element>
            <xsl:value-of select="enopdf:get-ddi-element(.)"/>
         </get-ddi-element>

         <get-after-question-title-instructions>
            <xsl:value-of select="enopdf:get-after-question-title-instructions(.)"/>
         </get-after-question-title-instructions>

         <get-end-question-instructions>
            <xsl:value-of select="enopdf:get-end-question-instructions(.)"/>
         </get-end-question-instructions>

         <get-before-question-title-instructions>
            <xsl:value-of select="enopdf:get-before-question-title-instructions(.)"/>
         </get-before-question-title-instructions>

         <get-rooster-number-lines>
            <xsl:value-of select="enopdf:get-rooster-number-lines(.)"/>
         </get-rooster-number-lines>

         <get-style>
            <xsl:value-of select="enopdf:get-style(.)"/>
         </get-style>

         <get-end-question-instructions-index>
            <xsl:value-of select="enopdf:get-end-question-instructions-index(.)"/>
         </get-end-question-instructions-index>

         <get-number-of-decimals>
            <xsl:value-of select="enopdf:get-number-of-decimals(.)"/>
         </get-number-of-decimals>

         <debug-get-formatted-label>
            <xsl:value-of select="enopdf:debug-get-formatted-label(.,'fr')"/>
         </debug-get-formatted-label>
      </getters-calls>
   </xsl:template>
</xsl:stylesheet>
