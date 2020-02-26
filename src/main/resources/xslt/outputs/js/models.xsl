<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:eno="http://xml.insee.fr/apps/eno"
	xmlns:enojs="http://xml.insee.fr/apps/eno/out/js"
	xmlns="http://xml.insee.fr/schema/applis/lunatic-h"
	exclude-result-prefixes="xs fn xd eno enojs" version="2.0">
	
	<xd:doc>
		<xd:desc>
			<xd:p>The properties file used by the stylesheet.</xd:p>
			<xd:p>It's on a transformation level.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:param name="properties-file"/>
	<xsl:param name="parameters-file"/>
	<xsl:param name="parameters-node" as="node()" required="no">
		<empty/>
	</xsl:param>
	
	<xd:doc scope="stylesheet">
		<xd:desc>
			<xd:p>An xslt stylesheet who transforms an input into js through generic driver templates.</xd:p>
			<xd:p>The real input is mapped with the drivers.</xd:p>
		</xd:desc>
	</xd:doc>
	
	<xsl:variable name="varName" select="parent"/>
		
	<xsl:template match="ResponseElement" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enojs:get-form-languages($source-context)" as="xs:string +"/>
		<!-- display only external variable -->
		<variables variableType="EXTERNAL">
			<name><xsl:value-of select="enojs:get-name($source-context)"/></name>
			<value xsi:nil="true"/>
		</variables>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on Form driver.</xd:p>
			<xd:p>It writes the root of the document with the main title.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Form" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enojs:get-form-languages($source-context)" as="xs:string +"/>
		<xsl:variable name="id" select="replace(enojs:get-name($source-context),'Sequence-','')"/>
		<xsl:variable name="label" select="enojs:get-label($source-context, $languages[1])"/>
		<Questionnaire id="{$id}" modele="{enojs:get-form-model($source-context)}">
			<label><xsl:value-of select="$label"/></label>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="languages" select="$languages" tunnel="yes"/>
				<!--<xsl:with-param name="isInSurvey" select="'yes'" tunnel="yes"/>-->
			</xsl:apply-templates>
		</Questionnaire>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on Module and SubModule drivers.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Module | SubModule" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="id" select="enojs:get-name($source-context)"/>
		<xsl:variable name="componentType-Sequence">
			<xsl:choose>
				<xsl:when test="self::Module"><xsl:value-of select="'Sequence'"/></xsl:when>
				<xsl:when test="self::SubModule"><xsl:value-of select="'Subsequence'"/></xsl:when>
			</xsl:choose>
		</xsl:variable>
		
		<components xsi:type="{$componentType-Sequence}" componentType="{$componentType-Sequence}" id="{$id}">
			<label><xsl:value-of select="enojs:get-vtl-label($source-context, $languages[1])"/></label>
			<xsl:copy-of select="enojs:getInstructionForQuestion($source-context,.)"/>
			<conditionFilter><xsl:value-of select="enojs:get-global-filter($source-context)"/></conditionFilter>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</components>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>filters do not create a component because their condition is borne by each of their descendants</xd:desc>
	</xd:doc>
	<xsl:template match="xf-group" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>SingleResponseQuestion driver does not create a component : it is created by its response</xd:desc>
	</xd:doc>
	<xsl:template match="SingleResponseQuestion | MultipleQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
			<xsl:with-param name="idQuestion" select="enojs:get-name($source-context)" tunnel="yes"/>
			<xsl:with-param name="questionName" select="lower-case(enojs:get-question-name($source-context,$languages[1]))" tunnel="yes"/>
			<xsl:with-param name="labelQuestion" select="enojs:get-vtl-label($source-context, $languages[1])" tunnel="yes"/>
			<xsl:with-param name="typeOfQuestion" select="self::*/name()" tunnel="yes"/>
			<xsl:with-param name="declarations" select="enojs:getInstructionForQuestion($source-context,.)" as="node()*" tunnel="yes"/>
			<xsl:with-param name="filterCondition" select="enojs:get-global-filter($source-context)" tunnel="yes"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates select="enojs:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>MultipleChoiceQuestion driver creates a CheckboxGroup component</xd:desc>
	</xd:doc>
	<xsl:template match="MultipleChoiceQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="idQuestion" select="enojs:get-name($source-context)"/>
		
		<components xsi:type="CheckboxGroup" componentType="CheckboxGroup" id="{$idQuestion}">
			<label><xsl:value-of select="enojs:get-vtl-label($source-context, $languages[1])"/></label>
			<xsl:copy-of select="enojs:getInstructionForQuestion($source-context,.)"/>
			<conditionFilter><xsl:value-of select="enojs:get-global-filter($source-context)"/></conditionFilter>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="idQuestion" select="$idQuestion" tunnel="yes"/>
				<xsl:with-param name="questionName" select="lower-case(enojs:get-question-name($source-context,$languages[1]))" tunnel="yes"/>
				<xsl:with-param name="labelQuestion" select="enojs:get-vtl-label($source-context, $languages[1])" tunnel="yes"/>
				<xsl:with-param name="typeOfQuestion" select="self::*/name()" tunnel="yes"/>
				<xsl:with-param name="declarations" select="enojs:getInstructionForQuestion($source-context,.)" as="node()*" tunnel="yes"/>
				<xsl:with-param name="filterCondition" select="enojs:get-global-filter($source-context)" tunnel="yes"/>
			</xsl:apply-templates>
		</components>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>Table / TableLoop drivers create a Table component</xd:desc>
	</xd:doc>
	<xsl:template match="Table | TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="idQuestion" select="enojs:get-name($source-context)"/>
		<xsl:variable name="componentType" select="'Table'"/>
		<xsl:variable name="mandatory" select="enojs:is-required($source-context)" as="xs:boolean"/>
		<xsl:variable name="nbMinimumLines" select="enojs:get-minimum-lines($source-context)"/>
		<xsl:variable name="nbMaximumLines" select="enojs:get-maximum-lines($source-context)"/>
		
		<components xsi:type="{$componentType}" componentType="{$componentType}" id="{$idQuestion}" positioning="HORIZONTAL" mandatory="{$mandatory}">
			<label><xsl:value-of select="enojs:get-vtl-label($source-context, $languages[1])"/></label>
			<xsl:copy-of select="enojs:getInstructionForQuestion($source-context,.)"/>
			<conditionFilter><xsl:value-of select="enojs:get-global-filter($source-context)"/></conditionFilter>
			
			<xsl:for-each select="enojs:get-header-lines($source-context)">
				<cells type="header">
					<xsl:apply-templates select="enojs:get-header-line($source-context,position())" mode="source">
						<xsl:with-param name="ancestorTable" select="'headerLine'" tunnel="yes"/>
						<xsl:with-param name="idColumn" select="position()" tunnel="yes"/>
					</xsl:apply-templates>
				</cells>
			</xsl:for-each>
			
			<xsl:for-each select="enojs:get-body-lines($source-context)">
				<cells type="line">
					<xsl:apply-templates select="enojs:get-body-line($source-context,position())" mode="source">
						<xsl:with-param name="ancestorTable" select="'bodyLine'" tunnel="yes"/>
						<xsl:with-param name="position" select="position()" tunnel="yes"/>
						<xsl:with-param name="questionName" select="enojs:get-question-name($source-context,$languages[1])" tunnel="yes"/>
					</xsl:apply-templates>
				</cells>
			</xsl:for-each>
			
			<xsl:if test="$nbMinimumLines!='' and $nbMaximumLines!=''">
				<lines min="{$nbMinimumLines}" max="{$nbMaximumLines}"/>
			</xsl:if>
		</components>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>TextCell driver displays the header cells, for columns and lines.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="TextCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="idColumn" tunnel="yes"/>
		<xsl:param name="ancestorTable" tunnel="yes"/>
		
		<xsl:variable name="col-span" select="number(enojs:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enojs:get-rowspan($source-context))"/>
		<xsl:variable name="id" select="enojs:get-name($source-context)"/>
		<xsl:variable name="label" select="enojs:get-vtl-label($source-context,$languages[1])"/>
		
		<cells>
			<xsl:if test="$ancestorTable='headerLine'">
				<xsl:attribute name="headerCell" select="true()"/>
			</xsl:if>
			<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
			<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
			<xsl:if test="$label!='' and $ancestorTable='bodyLine'">
				<value><xsl:value-of select="enojs:get-value($source-context)"/></value>
			</xsl:if>
			<label><xsl:value-of select="$label"/></label>
		</cells>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The Cell driver gives the colspan and the rowspan to the Response, which creates the cell.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Cell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
			<xsl:with-param name="col-span" select="number(enojs:get-colspan($source-context))" tunnel="yes"/>
			<xsl:with-param name="row-span" select="number(enojs:get-rowspan($source-context))" tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The EmptyCell driver creates an empty cell.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="EmptyCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="ancestorTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="idColumn" tunnel="yes"/>
		
		<xsl:variable name="col-span" select="number(enojs:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enojs:get-rowspan($source-context))"/>
		
		<xsl:choose>
			<xsl:when test="$ancestorTable='headerLine'">
				<cells headerCell="true">
					<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
					<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
					<label/>
				</cells>
			</xsl:when>
			<xsl:otherwise>
				<cells headerCell="false">
					<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
					<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
				</cells>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The Response drivers in SingleResponseQuestion and MultipleQuestion create a component, which type depends on the Response driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="*[name(.) =('SingleResponseQuestion','MultipleQuestion')]//*[name(.) =('NumericDomain','TextDomain','TextareaDomain','DateTimeDomain','CodeDomain','BooleanDomain')]" mode="model" priority="1">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="questionName" tunnel="yes"/>
		<xsl:param name="labelQuestion" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="declarations" as="node()*" tunnel="yes"/>
		<xsl:param name="filterCondition" tunnel="yes"/>
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		<xsl:variable name="responseType">
			<xsl:choose>
				<xsl:when test="self::BooleanDomain"><xsl:value-of select="'Boolean'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'String'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="code-appearance" select="enojs:get-appearance($source-context)"/>
		<xsl:variable name="componentType">
			<xsl:choose>
				<xsl:when test="self::NumericDomain"><xsl:value-of select="'InputNumber'"/></xsl:when>
				<xsl:when test="self::TextDomain"><xsl:value-of select="'Input'"/></xsl:when>
				<xsl:when test="self::TextareaDomain"><xsl:value-of select="'Textarea'"/></xsl:when>
				<xsl:when test="self::DateTimeDomain"><xsl:value-of select="'Datepicker'"/></xsl:when>
				<xsl:when test="self::CodeDomain and $code-appearance='radio-button'"><xsl:value-of select="'Radio'"/></xsl:when>
				<xsl:when test="self::CodeDomain and $code-appearance='drop-down-list'"><xsl:value-of select="'Dropdown'"/></xsl:when>
				<xsl:when test="self::CodeDomain and $code-appearance='checkbox'"><xsl:value-of select="'CheckboxOne'"/></xsl:when>
				<xsl:when test="self::BooleanDomain"><xsl:value-of select="'CheckboxBoolean'"/></xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!-- NumericDomain getters -->
		<xsl:variable name="minimumResponse" select="enojs:get-minimum($source-context)"/>
		<xsl:variable name="maximumResponse" select="enojs:get-maximum($source-context)"/>
		<xsl:variable name="numberOfDecimals">
			<xsl:variable name="dec" select="enojs:get-number-of-decimals($source-context)"/>
			<xsl:choose>
				<xsl:when test="$dec!=''"><xsl:value-of select="$dec"/></xsl:when>
				<xsl:when test="$dec='' and $minimumResponse!='' and self::NumericDomain">0</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="unit" select="enojs:get-suffix($source-context,$languages[1])"/>
		<!-- TextDomain getters -->
		<xsl:variable name="lengthResponse" select="enojs:get-length($source-context)"/>
		<!-- DateTimeDomain getters -->
		<xsl:variable name="dateFormat" select="enojs:get-format($source-context)"/>
		
		<xsl:if test="$questionName!=''">
			<components xsi:type="{$componentType}" componentType="{$componentType}" id="{$idQuestion}">
				<xsl:if test="$lengthResponse!='' and (self::TextDomain or self::TextareaDomain)"><xsl:attribute name="maxLength" select="$lengthResponse"/></xsl:if>
				<xsl:attribute name="mandatory" select="enojs:is-required($source-context)"/>
				<xsl:if test="$minimumResponse!=''"><xsl:attribute name="min" select="$minimumResponse"/></xsl:if>
				<xsl:if test="$maximumResponse!=''"><xsl:attribute name="max" select="$maximumResponse"/></xsl:if>
				<xsl:if test="$numberOfDecimals!=''"><xsl:attribute name="decimals" select="$numberOfDecimals"/></xsl:if>
				<label><xsl:value-of select="$labelQuestion"/></label>
				<xsl:if test="$unit!=''">
					<unit><xsl:value-of select="$unit"/></unit>
				</xsl:if>
				<xsl:if test="$dateFormat != ''">
					<dateFormat><xsl:value-of select="$dateFormat"/></dateFormat>
				</xsl:if>
				<xsl:copy-of select="$declarations"/>
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
				<xsl:call-template name="enojs:add-response-to-components">
					<xsl:with-param name="responseName" select="$responseName"/>
					<xsl:with-param name="responseType" select="$responseType"/>
				</xsl:call-template>
				<conditionFilter><xsl:value-of select="$filterCondition"/></conditionFilter>
			</components>
		</xsl:if>
		<variables variableType="COLLECTED">
			<name><xsl:value-of select="$responseName"/></name>
			<responseRef><xsl:value-of select="$responseName"/></responseRef>
		</variables>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The Response drivers in the body lines of tables create a cell, which type depends on the Response driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="*[name(.) =('NumericDomain','TextDomain','TextareaDomain','DateTimeDomain','CodeDomain','BooleanDomain')]" mode="model" priority="0">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="col-span" tunnel="yes"/>
		<xsl:param name="row-span" tunnel="yes"/>
		<xsl:param name="ancestorTable" tunnel="yes"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		<xsl:variable name="responseType">
			<xsl:choose>
				<xsl:when test="self::BooleanDomain"><xsl:value-of select="'Boolean'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'String'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="code-appearance" select="enojs:get-appearance($source-context)"/>
		<xsl:variable name="componentType">
			<xsl:choose>
				<xsl:when test="self::NumericDomain"><xsl:value-of select="'InputNumber'"/></xsl:when>
				<xsl:when test="self::TextDomain"><xsl:value-of select="'Input'"/></xsl:when>
				<xsl:when test="self::TextareaDomain"><xsl:value-of select="'Textarea'"/></xsl:when>
				<xsl:when test="self::DateTimeDomain"><xsl:value-of select="'Datepicker'"/></xsl:when>
				<xsl:when test="self::CodeDomain and $code-appearance='radio-button'"><xsl:value-of select="'Radio'"/></xsl:when>
				<xsl:when test="self::CodeDomain and $code-appearance='drop-down-list'"><xsl:value-of select="'Dropdown'"/></xsl:when>
				<xsl:when test="self::CodeDomain and $code-appearance='checkbox'"><xsl:value-of select="'CheckboxOne'"/></xsl:when>
				<xsl:when test="self::BooleanDomain"><xsl:value-of select="'CheckboxBoolean'"/></xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!-- NumericDomain getters -->
		<xsl:variable name="minimumResponse" select="enojs:get-minimum($source-context)"/>
		<xsl:variable name="maximumResponse" select="enojs:get-maximum($source-context)"/>
		<xsl:variable name="numberOfDecimals">
			<xsl:variable name="dec" select="enojs:get-number-of-decimals($source-context)"/>
			<xsl:choose>
				<xsl:when test="$dec!=''"><xsl:value-of select="$dec"/></xsl:when>
				<xsl:when test="$dec='' and $minimumResponse!=''">0</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="unit" select="enojs:get-suffix($source-context,$languages[1])"/>
		<!-- TextDomain getters -->
		<xsl:variable name="lengthResponse" select="enojs:get-length($source-context)"/>
		<!-- DateTimeDomain getters -->
		<xsl:variable name="dateFormat" select="enojs:get-format($source-context)"/>
		
		<cells id="{enojs:get-name($source-context)}" componentType="{$componentType}">
			<xsl:if test="$lengthResponse!='' and (self::TextDomain or self::TextareaDomain)"><xsl:attribute name="maxLength" select="$lengthResponse"/></xsl:if>
			<xsl:if test="$col-span &gt; 1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
			<xsl:if test="$row-span &gt; 1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
			<xsl:if test="$minimumResponse!=''"><xsl:attribute name="min" select="$minimumResponse"/></xsl:if>
			<xsl:if test="$maximumResponse!=''"><xsl:attribute name="max" select="$maximumResponse"/></xsl:if>
			<xsl:if test="$numberOfDecimals!=''"><xsl:attribute name="decimals" select="$numberOfDecimals"/></xsl:if>
			<xsl:if test="$unit!=''">
				<unit><xsl:value-of select="$unit"/></unit>
			</xsl:if>
			<xsl:if test="$dateFormat != ''">
				<dateFormat><xsl:value-of select="$dateFormat"/></dateFormat>
			</xsl:if>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
			<xsl:call-template name="enojs:add-response-to-components">
				<xsl:with-param name="responseName" select="$responseName"/>
				<xsl:with-param name="responseType" select="$responseType"/>
			</xsl:call-template>
		</cells>
		<variables variableType="COLLECTED">
			<name><xsl:value-of select="$responseName"/></name>
			<responseRef><xsl:value-of select="$responseName"/></responseRef>
		</variables>
		
		
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The Response of MultipleChoiceQuestion creates a response element of the CheckboxGroup.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="MultipleChoiceQuestion//BooleanDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		
		<responses id="{enojs:get-name($source-context)}">
			<!-- call item driver for the label -->
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
			<xsl:call-template name="enojs:add-response-to-components">
				<xsl:with-param name="responseName" select="$responseName"/>
				<xsl:with-param name="responseType" select="'Boolean'"/>
			</xsl:call-template>
		</responses>
		<variables variableType="COLLECTED">
			<name><xsl:value-of select="$responseName"/></name>
			<responseRef><xsl:value-of select="$responseName"/></responseRef>
		</variables>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Each value of a CodeDomain response creates an option.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="CodeDomain//xf-item" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="label" select="enojs:get-vtl-label($source-context, $languages[1])"/>
		
		<xsl:if test="$label !=''">
			<options>
				<value><xsl:value-of select="enojs:get-value($source-context)"/></value>
				<label><xsl:value-of select="$label"/></label>
			</options>
		</xsl:if>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The value of a BooleanDomain gives its label.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="BooleanDomain//xf-item" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="label" select="enojs:get-vtl-label($source-context, $languages[1])"/>
		
		<xsl:if test="$label !=''">
			<label><xsl:value-of select="$label"/></label>
		</xsl:if>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The xf-output driver adds declaration elements.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="positionDeclaration" tunnel="yes"></xsl:param>
		
		<xsl:variable name="instructionFormat">
			<xsl:variable name="format" select="upper-case(enojs:get-format($source-context))"/>
			<xsl:choose>
				<xsl:when test="$format!=''"><xsl:value-of select="$format"/></xsl:when>
				<!-- Default value : COMMENT -->
				<xsl:otherwise><xsl:value-of select="'COMMENT'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="instructionLabel" select="enojs:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="instructionFormatMaj" select="concat(upper-case(substring($instructionFormat,1,1)),
			substring($instructionFormat,2))" as="xs:string"/>
		
		<xsl:if test="$positionDeclaration!=''">
			<declarations declarationType="{$instructionFormat}" id="{enojs:get-name($source-context)}" position="{$positionDeclaration}">
				<label><xsl:value-of select="$instructionLabel"/></label>
			</declarations>
		</xsl:if>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The CalculatedVariable driver displays the formula of the calculated variable on the elements variables.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="CalculatedVariable" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>		
		<xsl:variable name="nameOutVariable" select="enojs:get-business-name($source-context)"/>
		
		<variables variableType="CALCULATED">
			<name>
				<xsl:value-of select="$nameOutVariable"/>
			</name>
			<expression>
				<xsl:value-of select="normalize-space(enojs:replace-variable-with-collected-and-external-variables-formula($source-context,$nameOutVariable))"/>
			</expression>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</variables>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>template for the GoTo</xd:desc>
	</xd:doc>
	<xsl:template match="GoTo" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="componentType" select="'FilterDescription'"/>
		<xsl:variable name="idGoTo" select="enojs:get-name($source-context)"/>
		<xsl:variable name="label" select="enojs:get-vtl-label($source-context,$languages[1])"/>
		
		<components xsi:type="{$componentType}" componentType="{$componentType}" id="{$idGoTo}" filterDescription="{$filterDescription}">
			<label><xsl:value-of select="$label"/></label>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</components>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the ConsistencyCheck driver.</xd:p>
			<xd:p>It writes the formula of the check.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="ConsistencyCheck" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="nameOfControl" select="enojs:get-check-name($source-context,$languages[1])"/>
		<xsl:variable name="control" select="enojs:get-constraint($source-context)"/>
		<xsl:variable name="instructionFormat" select="enojs:get-css-class($source-context)"/>
		<xsl:variable name="instructionLabel" select="enojs:get-vtl-label($source-context, $languages[1])"/>
		<xsl:variable name="alertLevel" select="enojs:get-alert-level($source-context)"/>
		
		<control>
			<xsl:if test="$alertLevel != ''">
				<xsl:attribute name="level" select="$alertLevel"/>
			</xsl:if>
			<xsl:if test="$control!=''">
				<title><xsl:value-of select="concat(upper-case($alertLevel),' control : ',$nameOfControl)"/></title>
				<value>
					<xsl:variable name="final-control">
						<xsl:call-template name="enojs:replace-variables-in-formula">
							<xsl:with-param name="source-context" select="$source-context"/>
							<xsl:with-param name="formula" select="$control"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:value-of select="normalize-space($final-control)"/>
				</value>
			</xsl:if>
			
			<xsl:if test="$instructionLabel!=''">
				<instruction><xsl:value-of select="$instructionLabel"/>	</instruction>
			</xsl:if>		
			
			<!-- Go to the Calculated Variable -->
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</control>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Function named: enojs:printQuestionTitleWithInstruction.</xd:p>
			<xd:p>It prints the instructions of a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:function name="enojs:getInstructionForQuestion">
		<xsl:param name="context" as="item()"/>
		<xsl:param name="driver"/>
		<xsl:apply-templates select="enojs:get-before-question-title-instructions($context)" mode="source">
			<xsl:with-param name="driver" select="$driver"/>
			<xsl:with-param name="positionDeclaration" select="'BEFORE_QUESTION_TEXT'" tunnel="yes"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="enojs:get-after-question-title-instructions($context)" mode="source">
			<xsl:with-param name="driver" select="$driver"/>
			<xsl:with-param name="positionDeclaration" select="'AFTER_QUESTION_TEXT'" tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:function>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Named template: enojs:add-response-to-components.</xd:p>
			<xd:p>It creates the response with its different possible states.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template name="enojs:add-response-to-components">
		<xsl:param name="responseName"/>
		<xsl:param name="responseType"/>
		<xsl:variable name="ResponseTypeEnum" select="'PREVIOUS,COLLECTED,FORCED,EDITED,INPUTED'" as="xs:string"/>
		<!-- responseType="{$responseType}" -->
		<response name="{$responseName}" xsi:type="ResponseContainer{$responseType}" >
			<xsl:for-each select="tokenize($ResponseTypeEnum,',')">
				<valueState valueType="{.}">
					<value xsi:nil="true"/>
				</valueState>
			</xsl:for-each>
		</response>
	</xsl:template>
	
</xsl:stylesheet>