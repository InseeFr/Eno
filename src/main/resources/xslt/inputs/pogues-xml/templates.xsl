<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>Label is the default element for labels in Pogues.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-label">
      <xsl:value-of select="pogues:Label"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-name">
      <xsl:value-of select="pogues:Name"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Declaration" mode="enopogues:get-name">
      <xsl:value-of select="@declarationType"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-agency">
      <xsl:value-of select="ancestor-or-self::pogues:Questionnaire/@agency"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-id">
      <xsl:value-of select="@id"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="pogues:Declaration" mode="enopogues:get-declaration-text">
      <xsl:value-of select="pogues:Text"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="enopogues:get-value">
      <xsl:value-of select="pogues:Value"/>
   </xsl:template>
</xsl:stylesheet>
