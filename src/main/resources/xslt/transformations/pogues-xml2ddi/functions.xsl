<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Function that returns the label of a pogues element.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-citation">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-label($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Name is the default element for names in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-name($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the agency that created the survey</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-agency">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-agency($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the ID attribute of the element</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-id($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the text of the Declaration</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-text">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-declaration-text($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Value is the default element for values in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-label($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return a lang for the survey. As this information in not available in PoguesXML, it is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-value($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return a version for the survey. As this information in not available in PoguesXML, it is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-lang">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-lang($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the type of visualisation of a response (checkbox, radio-button,..)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-version">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-version($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the type of a question or of the data of a response</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-generic-output-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-visualization-hint($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the name of the type of data of a response (TEXT, NUMERIC,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-type($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the maximum length of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-type-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-type-name($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the attribut coding if the anwser is mandatory. This part is not implemented</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-max-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-max-length($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the attribut coding the level of the sequence (QUESTIONNAIRE, MODULE, SUBMODULE,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-mandatory">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-mandatory($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return all Sequenses elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-sequence-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-generic-name($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return all Questions elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-sequences">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-sequences($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return all Declarations elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-questions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-questions($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the number of decimals of the data type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-instructions($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the minimal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-decimal-positions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-decimals($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the maximal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-low">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-minimum($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the string expected as value of the isDiscrete attribut of l:code in ddi3.2 . This value is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-high">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-maximum($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the dynamic attribute of the pogues:Dimension. This value is used to compute positions in grids</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:is-discrete">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:is-discrete($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>This is used to implement the code of booleans if this return something</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-dynamic">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-dynamic($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Expression is the default element for expressions in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:exist-boolean">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:exist-boolean($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the ID of the element that result a true condition.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-expression">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-expression($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the ID to which the GoTo aim</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-if-true">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-if-true($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return all IfThenElses elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-ifthenelses">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-ifthenelses($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-parent-id">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-parent-id($context)"/>
   </xsl:function>
</xsl:stylesheet>
