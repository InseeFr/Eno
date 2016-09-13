<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!--Linking the DDI element label sending function to the form title getter function-->
   <xsl:function name="iatfr:get-form-title">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-label($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI language getter function to the form languages getter function-->
   <xsl:function name="iatfr:get-form-languages">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-languages($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the application name getter function-->
   <xsl:function name="iatfr:get-application-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-id($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the form name getter function-->
   <xsl:function name="iatfr:get-form-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-id($context)"/>
   </xsl:function>

   <xsl:function name="iatfr:get-form-description">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:text/>
   </xsl:function>

   <xsl:function name="iatfr:get-default-value">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the Xforms element name getter function-->
   <xsl:function name="iatfr:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-id($context)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="iatfr:get-relevant">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-cachable($context)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="iatfr:get-readonly">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-grisable($context)"/>
   </xsl:function>

   <xsl:function name="iatfr:get-required">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="iatfr:get-calculate">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-link($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-calculate-label">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-conditionned-text($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-calculate-alert">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-conditionned-text-bis($context)"/>
   </xsl:function>
   <!--Used to specify the character string to identify as xf:date-->
   <xsl:function name="iatfr:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-type($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-constraint">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-control($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-nombre-decimales">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-number-decimal($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-alert-level">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-type-message($context)"/>
   </xsl:function>
   <!--Linking the DDI Instruction (Help Type) getter function to the Xforms help element getter function-->
   <xsl:function name="iatfr:get-help">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-help-instruction($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI Instruction (Hint Type) getter function to the Xforms hint element getter function-->
   <xsl:function name="iatfr:get-hint">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-hint-instruction($context,$language)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="iatfr:get-alert">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-message($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI element label sending function to the Xforms elements label getter function-->
   <xsl:function name="iatfr:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-label($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI code value sending function to the Xforms item value getter function-->
   <xsl:function name="iatfr:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-value($context)"/>
   </xsl:function>
   <!--Linking the DDI code list representation format sending function to the Xforms list appearance getter function-->
   <xsl:function name="iatfr:get-appearance">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-outputformat($context)"/>
   </xsl:function>
   <!--Linking the DDI style sending function to the css class getter function-->
   <xsl:function name="iatfr:get-css-class">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-style($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-format($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-length($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-suffix">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-suffix($context,$language)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-header-columns">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-levels-first-dimension($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-header-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-levels-second-dimension($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-body-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-codes-first-dimension($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-header-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="iatddi:get-title-line($context,$index)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-body-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="iatddi:get-table-line($context,$index)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-rowspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-rowspan($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-colspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-colspan($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-minimum-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-minimumRequired($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-dependants-constraint">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-computation-items($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-dependants-relevant">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-then($context)"/>
   </xsl:function>
</xsl:stylesheet>
