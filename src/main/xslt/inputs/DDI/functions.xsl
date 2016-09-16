<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!--Getter function of the DDI element identifier-->
   <xsl:function name="iatddi:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-id"/>
   </xsl:function>
   <!--Getter function of the DDI element label-->
   <xsl:function name="iatddi:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-label">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!--Getter function of the DDI document languages -->
   <xsl:function name="iatddi:get-languages" as="xs:string*">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-languages"/>
   </xsl:function>
   <!--Getter function of the DDI element value-->
   <xsl:function name="iatddi:get-value" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-value"/>
   </xsl:function>
   <!--Getter function of some DDI element display format-->
   <xsl:function name="iatddi:get-output-format" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-output-format"/>
   </xsl:function>
   <!--Getter function of the DDI element index-->
   <xsl:function name="iatddi:get-index" as="xs:integer">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-index"/>
   </xsl:function>
   <!--Getter function of a help-type instruction-->
   <xsl:function name="iatddi:get-help-instruction">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-help-instruction">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!--Getter function of a hint-type instruction-->
   <xsl:function name="iatddi:get-hint-instruction">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-hint-instruction">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-style" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-style"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-type" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-type"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-cachable">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-cachable"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-grisable" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-grisable"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-control" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-control"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-number-of-decimals" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-number-of-decimals"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-message" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-message">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-message-type" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-message-type"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-link" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-link"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-format"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-length" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-length"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-conditionned-text" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-conditionned-text"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-conditionned-text-bis" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-conditionned-text-bis"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-nb-of-modules" as="xs:integer">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-nb-of-modules"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-suffix" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-suffix">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-levels-first-dimension">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-levels-first-dimension"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-levels-second-dimension">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-levels-second-dimension"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-codes-first-dimension">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-codes-first-dimension"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-title-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-title-line">
         <xsl:with-param name="index" select="$index" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-table-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-table-line">
         <xsl:with-param name="index" select="$index" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-rowspan" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-rowspan"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-colspan" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-colspan"/>
   </xsl:function>
   <!--Getter function of the minimum number of lines to display in a dynamic table-->
   <xsl:function name="iatddi:get-minimum-required">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-minimum-required"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-computation-items" as="xs:string*">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-computation-items"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-then" as="xs:string*">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-then"/>
   </xsl:function>
</xsl:stylesheet>
