<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Function that returns the label of a pogues element.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-label"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Name is the default element for names in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-name"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the agency that created the survey</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-agency">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-agency"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the ID attribute of the element</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-id"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the text of the Declaration</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-declaration-text">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-declaration-text"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Value is the default element for values in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-value"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return a lang for the survey. As this information in not available in PoguesXML, it is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-lang">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-lang"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return a version for the survey. As this information in not available in PoguesXML, it is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-version">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-version"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the type of visualisation of a response (checkbox, radio-button,..)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-visualization-hint">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-visualization-hint"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the type of a question or of the data of a response</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-type"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the name of the type of data of a response (TEXT, NUMERIC,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-type-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-type-name"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the maximum length of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-max-length">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-max-length"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the attribut coding if the anwser is mandatory. This part is not implemented</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-mandatory">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-mandatory"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the attribut coding the level of the sequence (QUESTIONNAIRE, MODULE, SUBMODULE,...)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-generic-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-generic-name"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return all Sequenses elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-sequences">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-sequences"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return all Questions elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-questions">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-questions"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return all Declarations elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-instructions"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the number of decimals of the data type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-decimals">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-decimals"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the minimal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-minimum">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-minimum"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the maximal value of the data type</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-maximum">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-maximum"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the string expected as value of the isDiscrete attribut of l:code in ddi3.2 . This value is hard-coded</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:is-discrete">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:is-discrete"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the dynamic attribute of the pogues:Dimension. This value is used to compute positions in grids</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-dynamic">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-dynamic"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>This is used to implement the code of booleans if this return something</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:exist-boolean">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:exist-boolean"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Expression is the default element for expressions in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-expression">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-expression"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the ID of the element that result a true condition.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-then-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-then-id"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return the ID to which the GoTo aim</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-if-true">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-if-true"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Return all IfThenElses elements of the survey.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-ifthenelses">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-ifthenelses"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-parent-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-parent-id"/>
   </xsl:function>
</xsl:stylesheet>
