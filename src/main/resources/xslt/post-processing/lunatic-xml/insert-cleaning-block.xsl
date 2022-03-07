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
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- When encountering the last variable, we copy the variable and add the cleaning block -->
  <xsl:template match="h:variables[last()]">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
    <cleaning>
      <xsl:copy-of select="enolunatic:construct-cleaning-list()"/>
    </cleaning>
  </xsl:template>
  
  <!-- Function constructing a list containing cleaning relations, with structure : -->
  <!-- <VAR_LAUNCHING_CLEANING><VAR_NEEDING_CLEANING>expression</VAR_NEEDING_CLEANING></VAR_LAUNCHING_CLEANING>-->
  <xsl:function name="enolunatic:construct-cleaning-list">
    <!-- We search in every component which has a collected response associated -->
    <!-- (We don't care about components without responses because they don't need cleaning) -->
    <xsl:variable name="untidiedList">
      <xsl:for-each select="$root//h:components[h:response]">
        <!-- If there are no bindingDependencies in the conditionFilter, we don't care -->
        <xsl:if test="h:conditionFilter/h:bindingDependencies">
          <!-- We need to go through each bindingDependencies to add it as a variable lauching cleaning for our response -->
          <xsl:for-each select="h:conditionFilter/h:bindingDependencies">
            <xsl:element name="{.}">
              <!-- We get the name of the response that needs cleaning -->
              <xsl:element name="{../../h:response/@name}">
                <!-- We get the expression of the filter, so we can know when to activate cleaning -->
                <xsl:value-of select="../h:value"/>
              </xsl:element>
            </xsl:element>
          </xsl:for-each>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy-of select="enolunatic:tidying-cleaning-list($untidiedList)"/>
  </xsl:function>
  
  <!-- Function tidying the list produced by first step of enolunatic:construct-cleaning-list -->
  <!-- The idea is simply to regroup under a unique variable launching cleaning -->
  <!-- This is a naive approach using only XSL 1.0 functionality -->
  <!-- It seems a more direct approach could be used in XSL 2.0 with for-each-group -->
  <!-- But I couldn't manage to make it work -->
  <xsl:function name="enolunatic:tidying-cleaning-list">
    <xsl:param name="untidiedList"/>
    <xsl:variable name="tidiedList">
      <xsl:for-each select="$untidiedList/*">
        <!-- For each name that is encountered -->
        <xsl:variable name="name" select="local-name()"/>
        <!-- If that name does not already exist before (so the first time we encounter it) -->
        <xsl:if test="not(preceding-sibling::*[local-name() = $name])">
          <!-- We copy that node -->
          <xsl:copy>
            <xsl:copy-of select="node()"/>
            <!-- And we go fetch every sibling node with the same name to put its content inside -->
            <xsl:for-each select="following-sibling::*[local-name() = $name]">
              <xsl:copy-of select="node()"/>
            </xsl:for-each>
          </xsl:copy>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    <xsl:copy-of select="$tidiedList"></xsl:copy-of>
  </xsl:function>
  
</xsl:stylesheet>