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
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-name"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-agency">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-agency"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-id"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-declaration-text">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-declaration-text"/>
   </xsl:function>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopogues:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="enopogues:get-value"/>
   </xsl:function>
</xsl:stylesheet>
