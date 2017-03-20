<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!--Linking the DDI element label sending function to the form title getter function-->
   <xsl:function name="enofr:get-form-title">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI languages getter function to the form languages getter function-->
   <xsl:function name="enofr:get-form-languages">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-languages($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the application name getter function-->
   <xsl:function name="enofr:get-application-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the form name getter function-->
   <xsl:function name="enofr:get-form-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the Xforms element name getter function-->
   <xsl:function name="enofr:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="enofr:get-relevant">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-command($context)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="enofr:get-readonly">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-command($context)"/>
   </xsl:function>

   <xsl:function name="enofr:get-required">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="enofr:get-calculate">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-link($context)"/>
   </xsl:function>
   <!--Linking the DDI element conditionned-text to the calculate label getter function-->
   <xsl:function name="enofr:get-calculate-label">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-conditionned-text($context)"/>
   </xsl:function>
   <!--Linking the DDI element conditionned-text-bis to the calculate alert getter function-->
   <xsl:function name="enofr:get-calculate-alert">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-conditionned-text-bis($context)"/>
   </xsl:function>
   <!--Used to specify the character string to identify as xf:date-->
   <xsl:function name="enofr:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-type($context)"/>
   </xsl:function>
   <!--Linking a Xforms function to a DDI function giving infos about format-->
   <xsl:function name="enofr:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-format($context)"/>
   </xsl:function>
   <!--Linking the DDI element control to the constraint getter function-->
   <xsl:function name="enofr:get-constraint">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-control($context)"/>
   </xsl:function>
   <!--Linking the DDI element message-type to the alert-level getter function-->
   <xsl:function name="enofr:get-alert-level">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-message-type($context)"/>
   </xsl:function>
   <!--Linking the DDI Instruction (Help Type) getter function to the Xforms help element getter function-->
   <xsl:function name="enofr:get-help">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-help-instruction($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI element label sending function to the Xforms elements label getter function-->
   <xsl:function name="enofr:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI code value sending function to the Xforms item value getter function-->
   <xsl:function name="enofr:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-value($context)"/>
   </xsl:function>
   <!--Linking the DDI code list representation format sending function to the Xforms list appearance getter function-->
   <xsl:function name="enofr:get-appearance">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-output-format($context)"/>
   </xsl:function>
   <!--Linking the DDI style sending function to the css class getter function-->
   <xsl:function name="enofr:get-css-class">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-style($context)"/>
   </xsl:function>
   <!--Linking both length getter functions-->
   <xsl:function name="enofr:get-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-length($context)"/>
   </xsl:function>
   <!--Linking both suffix getter functions-->
   <xsl:function name="enofr:get-suffix">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-suffix($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI element levels-first-dimension to the header-columns getter function-->
   <xsl:function name="enofr:get-header-columns">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-first-dimension($context)"/>
   </xsl:function>
   <!--Linking the DDI element levels-second-dimension to the header-lines getter function-->
   <xsl:function name="enofr:get-header-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-second-dimension($context)"/>
   </xsl:function>
   <!--Linking the DDI element codes-first-dimension to the body-lines getter function-->
   <xsl:function name="enofr:get-body-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-codes-first-dimension($context)"/>
   </xsl:function>
   <!--Linking the DDI element title-line to the header-line getter function-->
   <xsl:function name="enofr:get-header-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-title-line($context,$index)"/>
   </xsl:function>
   <!--Linking the DDI element table-line to the body-line getter function-->
   <xsl:function name="enofr:get-body-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-table-line($context,$index)"/>
   </xsl:function>
   <!--Linking both rowspan element getter functions-->
   <xsl:function name="enofr:get-rowspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-rowspan($context)"/>
   </xsl:function>
   <!--Linking both colspan element getter functions-->
   <xsl:function name="enofr:get-colspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-colspan($context)"/>
   </xsl:function>
   <!--Linking the DDI element minimum-required to the minimum-lines getter function-->
   <xsl:function name="enofr:get-minimum-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-minimum-required($context)"/>
   </xsl:function>
   <!--Linking the DDI element computation-items to the constraint-dependencies getter function-->
   <xsl:function name="enofr:get-constraint-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-computation-items($context)"/>
   </xsl:function>
   <!--Linking the DDI element 'then' to the relevant-dependencies getter function-->
   <xsl:function name="enofr:get-relevant-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-then($context)"/>
   </xsl:function>
   <!--Linking the DDI element 'then' to the readonly-dependencies getter function-->
   <xsl:function name="enofr:get-readonly-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-then($context)"/>
   </xsl:function>
   <!--Linking the DDI attribute levelNumber to the code-depth function-->
   <xsl:function name="enofr:get-code-depth">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-level-number($context)"/>
   </xsl:function>
</xsl:stylesheet>
