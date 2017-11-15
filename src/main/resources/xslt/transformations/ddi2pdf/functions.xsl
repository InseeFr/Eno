<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-form-title to input function enoddi:get-label.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-form-title">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-application-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-application-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-form-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-form-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-relevant to input function enoddi:get-hideable-command.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-relevant">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-command($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-readonly to input function enoddi:get-deactivatable-command.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-readonly">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-command($context)"/>
   </xsl:function>

   <xsl:function name="enopdf:get-required">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-calculate to input function enoddi:get-variable-calculation.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-calculate">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-variable-calculation($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-type to input function enoddi:get-type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-type($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-format to input function enoddi:get-format.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-format($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-constraint to input function enoddi:get-control.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-constraint">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-control($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-alert-level to input function enoddi:get-message-type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-alert-level">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-message-type($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-help to input function enoddi:get-help-instruction.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-help">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-help-instruction($context,$language)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-label to input function enoddi:get-label.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enopdf:get-formatted-label($context,$language)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-value to input function enoddi:get-value.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-value($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-appearance to input function enoddi:get-output-format.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-appearance">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-output-format($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-css-class to input function enoddi:get-style.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-css-class">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-style($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-length to input function enoddi:get-length.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-length($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-suffix to input function enoddi:get-suffix.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-suffix">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-suffix($context,$language)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-header-columns to input function enoddi:get-levels-first-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-header-columns">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-first-dimension($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-header-lines to input function enoddi:get-levels-second-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-header-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-second-dimension($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-body-lines to input function enoddi:get-codes-first-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-body-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-codes-first-dimension($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-header-line to input function enoddi:get-title-line.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-header-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-title-line($context,$index)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-body-line to input function enoddi:get-table-line.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-body-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-table-line($context,$index)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-rowspan to input function enoddi:get-rowspan.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-rowspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-rowspan($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-colspan to input function enoddi:get-colspan.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-colspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-colspan($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-minimum-lines to input function enoddi:get-minimum-required.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-minimum-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-minimum-required($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-constraint-dependencies to input function enoddi:get-computation-items.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-constraint-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-computation-items($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-relevant-dependencies to input function enoddi:get-hideable-then.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-relevant-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-then($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-readonly-dependencies to input function enoddi:get-deactivatable-then.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-readonly-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-then($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-code-depth to input function enoddi:get-level-number.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-code-depth">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-level-number($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:get-image to input function enoddi:get-image.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-image">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-image($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Linking output function enopdf:is-first to input function enoddi:is-first.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:is-first">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:is-first($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Function for debugging, it outputs the input name of the element related to the driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-ddi-element">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="local-name($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-after-question-title-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-instructions-by-format($context,'instruction,comment,help')"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-end-question-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-instructions-by-format($context,'footnote') | enoddi:get-next-filter-description($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-before-question-title-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-previous-filter-description($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Function for retrieving default line number for TableLoop</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-rooster-number-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="if($context/self::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]) then(8) else()"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Function for retrieving style for QuestionTable (only 'no-border' or '' as values yet)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-style">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="if(enoddi:get-style($context) = 'question multiple-choice-question') then ('no-border') else()"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Function for retrieving an index for footnote instructions (based on their ordering in the questionnaire)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-end-question-instructions-index">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-instruction-index($context,'footnote,tooltip')"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Function for retrieving the number of decimals accepted by a response field.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-number-of-decimals">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-number-of-decimals($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:debug-get-formatted-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enopdf:get-formatted-label($context,$language)"/>
   </xsl:function>
</xsl:stylesheet>
