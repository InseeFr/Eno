<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" 
  xmlns:fn="http://www.w3.org/2005/xpath-functions" 
  xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
  xmlns:eno="http://xml.insee.fr/apps/eno" 
  xmlns:enolunatic="http://xml.insee.fr/apps/eno/out/js"
  xmlns:h="http://xml.insee.fr/schema/applis/lunatic-h"
  xmlns="http://xml.insee.fr/schema/applis/lunatic-h"
  exclude-result-prefixes="xs fn xd eno enolunatic h" version="2.0">	
  
  <xsl:output indent="yes"/>
  
  <xsl:variable name="root" select="root(.)"/>
  
  <xd:doc scope="stylesheet">
    <xd:desc>
      <xd:p>An xslt stylesheet aimed at adding the block "cleaning" to the Lunatic-XML output.</xd:p>
    </xd:desc>
  </xd:doc>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="h:Questionnaire">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()/>
    </xsl:copy>
  </xsl:template>
  
  <!-- When encountering the last variable, we copy the variable and add the cleaning block -->
  <xsl:template match="h:variables[last()]">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()/>
    </xsl:copy>
    <cleaning>
    </cleaning>
  </xsl:template>
  
</xsl:stylesheet>