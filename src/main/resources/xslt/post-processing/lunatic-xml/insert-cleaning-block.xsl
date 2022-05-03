<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
  xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enolunatic="http://xml.insee.fr/apps/eno/out/js"
  xmlns:h="http://xml.insee.fr/schema/applis/lunatic-h"
  xmlns="http://xml.insee.fr/schema/applis/lunatic-h"
  exclude-result-prefixes="xs fn xd eno enolunatic h" version="2.0">

  <xsl:output indent="yes"/>

  <xsl:variable name="root" select="root(.)"/>

  <xd:doc scope="stylesheet">
    <xd:desc>
      <xd:p>An xslt stylesheet aimed at adding the blocks "cleaning", "missingBlock" and "resizing" to the Lunatic-XML output.</xd:p>
    </xd:desc>
  </xd:doc>

  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="h:Questionnaire">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- When encountering the last variable, we copy the variable and add the cleaning and missing block -->
  <xsl:template match="h:variables[last()]">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
    <!-- Adding the cleaning block -->
    <cleaning>
      <xsl:copy-of select="enolunatic:construct-cleaning-list()"/>
    </cleaning>
    <!-- Adding the missing block only if missing option is true, under Questionnaire -->
    <xsl:if test="$root//@missing = 'true'">
      <missingBlock>
        <xsl:copy-of select="enolunatic:construct-missing-list()"/>
      </missingBlock>
    </xsl:if>
    <resizing>
      <xsl:copy-of select="enolunatic:construct-resizing-list()"/>
    </resizing>
  </xsl:template>

  <!-- Function constructing a list containing cleaning relations, with structure : -->
  <!-- <VAR_LAUNCHING_CLEANING><VAR_NEEDING_CLEANING>expression</VAR_NEEDING_CLEANING></VAR_LAUNCHING_CLEANING>-->
  <xsl:function name="enolunatic:construct-cleaning-list">
    <!-- We search in every component which has a collected response associated -->
    <!-- (We don't care about components without responses because they don't need cleaning) -->
    <xsl:variable name="untidiedList">
      <!-- 1) First we take care of the basic components which have one response (e.g. RadioButton, Input etc.) -->
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
      <!-- 2) Then we take care of the components containg cells with multiple responses (e.g. Tables) -->
      <xsl:for-each select="$root//h:components[h:body]">
        <!-- We need to go through each bindingDependencies to add it as a variable lauching cleaning for our response -->
        <xsl:for-each select="h:conditionFilter/h:bindingDependencies">
          <!-- We get the expression of the filter that activates cleaning in a variable for later use -->
          <xsl:variable name="filterValue" select="../h:value"/>
          <xsl:element name="{.}">
            <!-- We iterate through the cells with responses that need cleaning to create elements -->
            <xsl:for-each select="../../h:body//h:response">
              <xsl:element name="{@name}">
                <!-- We retrieve the expression -->
                <xsl:value-of select="$filterValue"/>
              </xsl:element>
            </xsl:for-each>
          </xsl:element>
        </xsl:for-each>
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
    <xsl:copy-of select="$tidiedList"/>
  </xsl:function>

  <!-- Function constructing a list containing missing relations, with structure : -->
  <!--  <VAR_MISSING>VAR_1</VAR_MISSING>
        <VAR_MISSING>VAR_2</VAR_MISSING>
        ...
        <VAR_MISSING>VAR_N</VAR_MISSING> -->
  <!-- But also the other way around : -->
  <!--  <VAR_1>VAR_MISSING</VAR_1>
        <VAR_2>VAR_MISSING</VAR_2>
        ...
        <VAR_N>VAR_MISSING</VAR_N>-->
  <xsl:function name="enolunatic:construct-missing-list">
    <xsl:variable name="missingList">

      <!-- We iterate on all the missingResponse we find in any component -->
      <xsl:for-each select="$root//h:components[h:missingResponse]">
        <xsl:variable name="missingName" select="h:missingResponse/@name"/>
        <!-- We iterate on all the responses linked to that missing response -->
        <xsl:for-each select=".//h:response">
          <!-- We put the name of the missing response as the element name -->
          <xsl:element name="{$missingName}">
            <!-- We get the name of the response that needs cleaning -->
            <xsl:value-of select="@name"/>
          </xsl:element>
        </xsl:for-each>

        <!-- We iterate a second time to reverse element name and content -->
        <!-- I do it on a separate loop because I want all elements of a same missing variable
      to be adjacent : that way when transformed to JSON it should become an array -->
        <xsl:for-each select=".//h:response">
          <!-- We put the name of the missing response as the element name -->
          <xsl:element name="{@name}">
            <!-- We get the name of the response that needs cleaning -->
            <xsl:value-of select="$missingName"/>
          </xsl:element>
        </xsl:for-each>
        </xsl:for-each>
    
    </xsl:variable>

    <xsl:copy-of select="$missingList"/>
  </xsl:function>


  <!-- Function constructing a list containing resizing relations, with structure : -->
  <!--  <VAR_RESIZING>
          <size>expression(VAR_RESIZING)</size>
          <variables>VAR_1</variables>
          <variables>VAR_2</variables>
          ...
          <variables>VAR_N</variables> -->
  <!-- Where size is how VAR_RESIZING is used to generate a number of iterations 
  (e.g. count(PRENOM), cast(NHAB,integer) or simply the variable itself -->
  <!-- Where the N variables which should be resized are in their own variables element (so it becomes an array later in JSON) -->
  <xsl:function name="enolunatic:construct-resizing-list">
    <xsl:variable name="resizingList">
      
      <!-- We iterate on all the loop components we find -->
      <xsl:for-each select="$root//h:components[@componentType='Loop']">
        <!-- We store the name of the resizing variable (which should be in the loopDependencies) -->
        <xsl:variable name="resizingName" select="h:loopDependencies"/>
        <!-- We store the expression of the resizing variable (which should be iterations OR lines-min=lines-max OR defaulting to the name of the variable...) -->
        <xsl:variable name="resizingExpr">
          <xsl:choose>
            <xsl:when test="h:iterations/h:value">
              <xsl:value-of select="h:iterations/h:value"/>
            </xsl:when>
            <xsl:when test="h:lines/h:min/h:value=h:lines/h:max/h:value">
              <xsl:value-of select="h:lines/h:max/h:value"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$resizingName"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <!-- We put the name of the resizing variable as the element name -->
        <xsl:element name="{$resizingName}">
          <!-- We put the resizing expression as the content of size element -->
          <size><xsl:value-of select="$resizingExpr"/></size>
          <!-- We iterate on all the responses linked to that loop -->
          <xsl:for-each select=".//h:response">
              <!-- We get the name of the response that needs resizing inside a variables element -->
            <variables><xsl:value-of select="@name"/></variables>
          </xsl:for-each>
        </xsl:element>
      </xsl:for-each>
      
    </xsl:variable>
    
    <xsl:copy-of select="$resizingList"/>
  </xsl:function>

</xsl:stylesheet>
