<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-citation">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-label($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-name($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-agency">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-agency($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-id($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-text">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-declaration-text($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-label($context)"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enoddi32:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enopogues:get-value($context)"/>
   </xsl:function>
</xsl:stylesheet>
