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
                xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
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
            <xsl:value-of select="enofr:get-form-title(.,'fr')"/>
         </get-form-title>

         <get-application-name>
            <xsl:value-of select="enofr:get-application-name(.)"/>
         </get-application-name>

         <get-form-name>
            <xsl:value-of select="enofr:get-form-name(.)"/>
         </get-form-name>

         <get-name>
            <xsl:value-of select="enofr:get-name(.)"/>
         </get-name>

         <get-relevant>
            <xsl:value-of select="enofr:get-relevant(.)"/>
         </get-relevant>

         <get-readonly>
            <xsl:value-of select="enofr:get-readonly(.)"/>
         </get-readonly>

         <get-calculate>
            <xsl:value-of select="enofr:get-calculate(.)"/>
         </get-calculate>

         <get-type>
            <xsl:value-of select="enofr:get-type(.)"/>
         </get-type>

         <get-format>
            <xsl:value-of select="enofr:get-format(.)"/>
         </get-format>

         <get-constraint>
            <xsl:value-of select="enofr:get-constraint(.)"/>
         </get-constraint>

         <get-alert-level>
            <xsl:value-of select="enofr:get-alert-level(.)"/>
         </get-alert-level>

         <get-help>
            <xsl:value-of select="enofr:get-help(.,'fr')"/>
         </get-help>

         <get-label>
            <xsl:value-of select="enofr:get-label(.,'fr')"/>
         </get-label>

         <get-value>
            <xsl:value-of select="enofr:get-value(.)"/>
         </get-value>

         <get-appearance>
            <xsl:value-of select="enofr:get-appearance(.)"/>
         </get-appearance>

         <get-css-class>
            <xsl:value-of select="enofr:get-css-class(.)"/>
         </get-css-class>

         <get-length>
            <xsl:value-of select="enofr:get-length(.)"/>
         </get-length>

         <get-suffix>
            <xsl:value-of select="enofr:get-suffix(.,'fr')"/>
         </get-suffix>

         <get-header-columns>
            <xsl:value-of select="enofr:get-header-columns(.)"/>
         </get-header-columns>

         <get-header-lines>
            <xsl:value-of select="enofr:get-header-lines(.)"/>
         </get-header-lines>

         <get-body-lines>
            <xsl:value-of select="enofr:get-body-lines(.)"/>
         </get-body-lines>

         <get-header-line>
            <xsl:value-of select="enofr:get-header-line(.,0)"/>
         </get-header-line>

         <get-body-line>
            <xsl:value-of select="enofr:get-body-line(.,0)"/>
         </get-body-line>

         <get-rowspan>
            <xsl:value-of select="enofr:get-rowspan(.)"/>
         </get-rowspan>

         <get-colspan>
            <xsl:value-of select="enofr:get-colspan(.)"/>
         </get-colspan>

         <get-minimum-lines>
            <xsl:value-of select="enofr:get-minimum-lines(.)"/>
         </get-minimum-lines>

         <get-constraint-dependencies>
            <xsl:value-of select="enofr:get-constraint-dependencies(.)"/>
         </get-constraint-dependencies>

         <get-relevant-dependencies>
            <xsl:value-of select="enofr:get-relevant-dependencies(.)"/>
         </get-relevant-dependencies>

         <get-readonly-dependencies>
            <xsl:value-of select="enofr:get-readonly-dependencies(.)"/>
         </get-readonly-dependencies>

         <get-code-depth>
            <xsl:value-of select="enofr:get-code-depth(.)"/>
         </get-code-depth>

         <get-image>
            <xsl:value-of select="enofr:get-image(.)"/>
         </get-image>

         <get-readonly-ancestors>
            <xsl:value-of select="enofr:get-readonly-ancestors(.)"/>
         </get-readonly-ancestors>

         <is-first>
            <xsl:value-of select="enofr:is-first(.)"/>
         </is-first>

         <get-ddi-element>
            <xsl:value-of select="enofr:get-ddi-element(.)"/>
         </get-ddi-element>

         <get-after-question-title-instructions>
            <xsl:value-of select="enofr:get-after-question-title-instructions(.)"/>
         </get-after-question-title-instructions>

         <get-end-question-instructions>
            <xsl:value-of select="enofr:get-end-question-instructions(.)"/>
         </get-end-question-instructions>

         <get-before-question-title-instructions>
            <xsl:value-of select="enofr:get-before-question-title-instructions(.)"/>
         </get-before-question-title-instructions>

         <get-rooster-number-lines>
            <xsl:value-of select="enofr:get-rooster-number-lines(.)"/>
         </get-rooster-number-lines>

         <get-style>
            <xsl:value-of select="enofr:get-style(.)"/>
         </get-style>

         <get-end-question-instructions-index>
            <xsl:value-of select="enofr:get-end-question-instructions-index(.)"/>
         </get-end-question-instructions-index>

         <get-number-of-decimals>
            <xsl:value-of select="enofr:get-number-of-decimals(.)"/>
         </get-number-of-decimals>
      </getters-calls>
   </xsl:template>
</xsl:stylesheet>
