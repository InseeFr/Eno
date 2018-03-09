<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-form-title to input function enoddi:get-label.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-form-title">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-application-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-application-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-form-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-form-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-relevant to input function enoddi:get-hideable-command.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-relevant">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-command($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-readonly to input function enoddi:get-deactivatable-command.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-readonly">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-command($context)"/>
   </xsl:function>

   <xsl:function name="enofr:get-required">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-calculate to input function enoddi:get-variable-calculation.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-calculate">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-variable-calculation($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-type to input function enoddi:get-type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-type($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-format to input function enoddi:get-format.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-format($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-constraint to input function enoddi:get-control.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-constraint">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-control($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-alert-level to input function enoddi:get-message-type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-alert-level">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-message-type($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-help to input function enoddi:get-help-instruction.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-help">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-help-instruction($context,$language)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-label to input function enoddi:get-label.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-value to input function enoddi:get-value.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-value($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-appearance to input function enoddi:get-output-format.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-appearance">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-output-format($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-css-class to input function enoddi:get-style.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-css-class">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-style($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-length to input function enoddi:get-length.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-length($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-suffix to input function enoddi:get-suffix.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-suffix">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-suffix($context,$language)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-header-columns to input function enoddi:get-levels-first-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-header-columns">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-first-dimension($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-header-lines to input function enoddi:get-levels-second-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-header-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-second-dimension($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-body-lines to input function enoddi:get-codes-first-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-body-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-codes-first-dimension($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-header-line to input function enoddi:get-title-line.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-header-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-title-line($context,$index)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-body-line to input function enoddi:get-table-line.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-body-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-table-line($context,$index)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-rowspan to input function enoddi:get-rowspan.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-rowspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-rowspan($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-colspan to input function enoddi:get-colspan.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-colspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-colspan($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-minimum-lines to input function enoddi:get-minimum-required.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-minimum-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-minimum-required($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-constraint-dependencies to input function enoddi:get-computation-items.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-constraint-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-computation-items($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-relevant-dependencies to input function enoddi:get-hideable-then.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-relevant-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-then($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-readonly-dependencies to input function enoddi:get-deactivatable-then.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-readonly-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-then($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-code-depth to input function enoddi:get-level-number.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-code-depth">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-level-number($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enofr:get-image to input function enoddi:get-image.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-image">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-image($context)"/>
   </xsl:function>
</xsl:stylesheet>
