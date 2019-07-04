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
	
	<xsl:param name="properties-file"/>
	<xsl:param name="parameters-file"/>
	<xsl:param name="parameters-node" as="node()" required="no">
		<empty/>
	</xsl:param>
	<xsl:param name="labels-folder"/>
	
	<xsl:variable name="properties" select="doc($properties-file)"/>
	
	
	<xd:doc scope="stylesheet">
		<xd:desc>
			<xd:p>An xslt stylesheet who transforms an input into js through generic driver templates.</xd:p>
			<xd:p>The real input is mapped with the drivers.</xd:p>
		</xd:desc>
	</xd:doc>
	
	
	<xsl:variable name="varName" select="parent"/>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Forces the traversal of the whole driver tree. Must be present once in the transformation.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="*" mode="model" priority="-1">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	
	
	<xsl:template match="ResponseElement" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="finding"/>
		<xsl:variable name="languages" select="enojs:get-form-languages($source-context)" as="xs:string +"/>
		<!--<xsl:param name="isInSurvey" tunnel="yes"/>-->
		<!-- display only external variable -->
		<xsl:choose>
			<xsl:when test="$finding='yo'">
			</xsl:when>
			<xsl:otherwise>
				<variables>
					<name><xsl:value-of select="enojs:get-name($source-context)"/></name>
					<label><xsl:value-of select="enojs:get-label($source-context,$languages[1])"/></label>
				</variables>
			</xsl:otherwise>
		</xsl:choose>
		
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
		<Questionnaire id="{$id}">
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
			<xd:p>Match on Module driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Module" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="id" select="enojs:get-name($source-context)"/>
		<xsl:variable name="label" select="enojs:get-label($source-context, $languages[1])"/>
		
		<xsl:variable name="componentType-Sequence" select="'Sequence'"/>
		
		<xsl:variable name="formulaReadOnly" select="enojs:get-readonly-ancestors($source-context)" as="xs:string*"/>
		<xsl:variable name="formulaRelevant" select="enojs:get-relevant-ancestors($source-context)" as="xs:string*"/>		
		<xsl:variable name="variablesReadOnly" select="enojs:get-readonly-ancestors-variables($source-context)" as="xs:string*"/>
		<xsl:variable name="variablesRelevant" select="enojs:get-relevant-ancestors-variables($source-context)" as="xs:string*"/>
		
		<xsl:variable name="filterCondition" select="enojs:createLambdaExpression(
			.,
			$formulaReadOnly,
			$formulaRelevant,
			$variablesReadOnly,
			$variablesRelevant
			)"/>
		
		<components xsi:type="{$componentType-Sequence}" componentType="{$componentType-Sequence}" id="{$id}">
			<label><xsl:value-of select="$label"/></label>
			<xsl:call-template name="eno:printQuestionTitleWithInstruction">
				<xsl:with-param name="driver" select="."/>
			</xsl:call-template>
			<xsl:copy-of select="$filterCondition"/>
			
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</components>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on SubModule driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="SubModule" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="id" select="enojs:get-name($source-context)"/>
		<xsl:variable name="label" select="enojs:get-label($source-context, $languages[1])"/>
		
		<xsl:variable name="componentType-Subsequence" select="'Subsequence'"/>
		
		<xsl:variable name="formulaReadOnly" select="enojs:get-readonly-ancestors($source-context)" as="xs:string*"/>
		<xsl:variable name="formulaRelevant" select="enojs:get-relevant-ancestors($source-context)" as="xs:string*"/>		
		<xsl:variable name="variablesReadOnly" select="enojs:get-readonly-ancestors-variables($source-context)" as="xs:string*"/>
		<xsl:variable name="variablesRelevant" select="enojs:get-relevant-ancestors-variables($source-context)" as="xs:string*"/>
		
		<xsl:variable name="filterCondition" select="enojs:createLambdaExpression(
			.,
			$formulaReadOnly,
			$formulaRelevant,
			$variablesReadOnly,
			$variablesRelevant
			)"/>
		
		<components xsi:type="{$componentType-Subsequence}" componentType="{$componentType-Subsequence}" id="{$id}">
			<label><xsl:value-of select="$label"/></label>
			<xsl:call-template name="eno:printQuestionTitleWithInstruction">
				<xsl:with-param name="driver" select="."/>
			</xsl:call-template>
			<xsl:copy-of select="$filterCondition"/>			
			
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</components>
	</xsl:template>
	
	<xsl:template match="SingleResponseQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:call-template name="addQuestionArguments">
			<xsl:with-param name="source-context" select="$source-context"/>
			<xsl:with-param name="typeOfQuestion" select="'SingleResponseQuestion'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="MultipleQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:call-template name="addQuestionArguments">
			<xsl:with-param name="source-context" select="$source-context"/>
			<xsl:with-param name="typeOfQuestion" select="'MultipleQuestion'"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="MultipleChoiceQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		
		<xsl:call-template name="addQuestionArguments">
			<xsl:with-param name="source-context" select="$source-context"/>
			<xsl:with-param name="typeOfQuestion" select="'MultipleChoiceQuestion'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="Table | TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="questionName" select="enojs:get-question-name($source-context,$languages[1])"/>
		<xsl:variable name="idQuestion" select="enojs:get-name($source-context)"/>
		
		<xsl:variable name="componentType-Table" select="'Table'"/>
		
		<xsl:variable name="formulaReadOnly" select="enojs:get-readonly-ancestors($source-context)" as="xs:string*"/>
		<xsl:variable name="formulaRelevant" select="enojs:get-relevant-ancestors($source-context)" as="xs:string*"/>
		
		<xsl:variable name="variablesReadOnly" select="enojs:get-readonly-ancestors-variables($source-context)" as="xs:string*"/>
		<xsl:variable name="variablesRelevant" select="enojs:get-relevant-ancestors-variables($source-context)" as="xs:string*"/>
		
		<xsl:variable name="declarations" select="eno:getInstructionForQuestion($source-context,.)" as="node()*" />
		<xsl:variable name="labelQuestion" select="enojs:get-label($source-context, $languages[1])"/>
		
		<xsl:variable name="filterCondition" select="enojs:createLambdaExpression(
			.,
			$formulaReadOnly,
			$formulaRelevant,
			$variablesReadOnly,
			$variablesRelevant
			)"/>
		
		<xsl:variable name="nbMinimumLines" select="enojs:get-minimum-lines($source-context)"/>
		<xsl:variable name="nbMaximumLines" select="enojs:get-maximum-lines($source-context)"/>
		
		<components xsi:type="{$componentType-Table}" componentType="{$componentType-Table}" id="{$idQuestion}" positioning="HORIZONTAL">
			<label><xsl:value-of select="$labelQuestion"/></label>
			<xsl:copy-of select="$declarations"/>
			<xsl:copy-of select="$filterCondition"/>
			
			<xsl:for-each select="enojs:get-body-lines($source-context)">
				<xsl:apply-templates select="enojs:get-body-line($source-context,position())" mode="source">
					<xsl:with-param name="ancestorTable" select="'line'" tunnel="yes"/>
					<xsl:with-param name="typeOfAncestor" select="'table'" tunnel="yes"/>
					<xsl:with-param name="position" select="position()" tunnel="yes"/>
					<xsl:with-param name="questionName" select="$questionName" tunnel="yes"/>
				</xsl:apply-templates>
			</xsl:for-each>
			
			<xsl:for-each select="enojs:get-header-lines($source-context)">
				<xsl:apply-templates select="enojs:get-header-line($source-context,position())" mode="source">
					<xsl:with-param name="ancestorTable" select="'headerLine'" tunnel="yes"/>
					<xsl:with-param name="idColumn" select="position()" tunnel="yes"/>
					<xsl:with-param name="typeOfAncestor" select="'table'" tunnel="yes"/>
				</xsl:apply-templates>
			</xsl:for-each>
			<xsl:if test="$nbMinimumLines!='' and $nbMaximumLines!=''">
				<lines min="{$nbMinimumLines}" max="{$nbMaximumLines}"/>
			</xsl:if>
		</components>
		
	</xsl:template>

	<xd:doc>
		<xd:desc>
			<xd:p>Match on the TextCell driver.</xd:p>
			<xd:p>It displays the headers on the top and the left.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="TextCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="ancestorTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="idColumn" tunnel="yes"/>
		
		<xsl:variable name="col-span" select="number(enojs:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enojs:get-rowspan($source-context))"/>
		<xsl:variable name="id" select="enojs:get-name($source-context)"/>
		<xsl:variable name="depth" select="enojs:get-code-depth($source-context)"/>
		
		
		<xsl:if test="$ancestorTable!=''">
			<xsl:variable name="label" select="enojs:get-label($source-context,$languages)"/>
			<xsl:choose>
				<xsl:when test="$label!='' and $ancestorTable='line'">
					<codes id="{$id}" depth="{$depth}">
						<value><xsl:value-of select="enojs:get-value($source-context)"/></value>
						<label><xsl:value-of select="$label"/></label>
					</codes>
				</xsl:when>
				<xsl:when test="$label!='' and $ancestorTable='headerLine'">
					<header><xsl:value-of select="$label"/></header>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the Cell driver.</xd:p>
			<xd:p>Create a cell and call templates for children to fill the cell.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Cell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="ancestorTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="col-span" select="number(enojs:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enojs:get-rowspan($source-context))"/>
		<xsl:if test="$ancestorTable!=''">
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</xsl:if>	
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the EmptyCell driver.</xd:p>
			<xd:p>Create a cell and call templates for children to fill the cell (a priori nothing).</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="EmptyCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="ancestorTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="col-span" select="number(enojs:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enojs:get-rowspan($source-context))"/>
		<xsl:if test="$ancestorTable!=''">
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</xsl:if>
		
	</xsl:template>
	
	<xsl:template name="addQuestionArguments">
		<xsl:param name="source-context"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="typeOfQuestion"/>
		
		<xsl:variable name="questionName" select="enojs:get-question-name($source-context,$languages[1])"/>
		<xsl:variable name="idQuestion" select="enojs:get-name($source-context)"/>
		
		<xsl:variable name="formulaReadOnly" select="enojs:get-readonly-ancestors($source-context)" as="xs:string*"/>
		<xsl:variable name="formulaRelevant" select="enojs:get-relevant-ancestors($source-context)" as="xs:string*"/>
		
		<xsl:variable name="variablesReadOnly" select="enojs:get-readonly-ancestors-variables($source-context)" as="xs:string*"/>
		<xsl:variable name="variablesRelevant" select="enojs:get-relevant-ancestors-variables($source-context)" as="xs:string*"/>
		
		<xsl:variable name="declarations" select="eno:getInstructionForQuestion($source-context,.)" as="node()*" />
		<xsl:variable name="labelQuestion" select="enojs:get-label($source-context, $languages[1])"/>
		
		<xsl:variable name="filterCondition" select="enojs:createLambdaExpression(
			.,
			$formulaReadOnly,
			$formulaRelevant,
			$variablesReadOnly,
			$variablesRelevant
			)"/>
		<xsl:if test="$typeOfQuestion='MultipleChoiceQuestion'">
			<components xsi:type="Checkbox" componentType="Checkbox" id="{$idQuestion}">
				<label><xsl:value-of select="$labelQuestion"/></label>
				<xsl:copy-of select="$declarations"/>
				<xsl:copy-of select="$filterCondition"/>
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
					<xsl:with-param name="typeOfQuestion" select="$typeOfQuestion" tunnel="yes"/>
					<xsl:with-param name="idQuestion" select="$idQuestion" tunnel="yes"/>
					<xsl:with-param name="questionName" select="lower-case($questionName)" tunnel="yes"/>
					<xsl:with-param name="labelQuestion" select="$labelQuestion" tunnel="yes"/>
					<xsl:with-param name="declarations" select="$declarations" as="node()*" tunnel="yes"/>
					<xsl:with-param name="filterCondition" select="$filterCondition" tunnel="yes"/>
				</xsl:apply-templates>
			</components>
		</xsl:if>
		<xsl:if test="$idQuestion != '' and $typeOfQuestion!='MultipleChoiceQuestion'">
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="typeOfQuestion" select="$typeOfQuestion" tunnel="yes"/>
				<xsl:with-param name="idQuestion" select="$idQuestion" tunnel="yes"/>
				<xsl:with-param name="questionName" select="lower-case($questionName)" tunnel="yes"/>
				<xsl:with-param name="labelQuestion" select="$labelQuestion" tunnel="yes"/>
				<xsl:with-param name="declarations" select="$declarations" as="node()*" tunnel="yes"/>
				<xsl:with-param name="filterCondition" select="$filterCondition" tunnel="yes"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on xf-input driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-input" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="questionName" tunnel="yes"/>
		<xsl:param name="labelQuestion" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="declarations" as="node()*" tunnel="yes"/>
		<xsl:param name="filterCondition" tunnel="yes"/>
		
		<xsl:param name="typeOfAncestor" tunnel="yes"/>
		<xsl:param name="position" tunnel="yes"/>
		
		<xsl:variable name="componentType-Input" select="'Input'"/>
		<xsl:variable name="componentType-InputNumber" select="'InputNumber'"/>
		
		<xsl:variable name="typeResponse" select="enojs:get-type($source-context)"/>
		<xsl:variable name="lengthResponse" select="enojs:get-length($source-context)"/>
		
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
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		
		<xsl:if test="$typeResponse!=''">
			
			<xsl:choose>
				<xsl:when test="$typeResponse='text' and $questionName!='' and $typeOfAncestor!='table'">
					<components xsi:type="{$componentType-Input}" componentType="{$componentType-Input}" id="{$idQuestion}" maxLength="{$lengthResponse}">
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:copy-of select="$declarations"/>
						<xsl:call-template name="enojs:addResponeTocomponents">
							<xsl:with-param name="responseName" select="$responseName"/>
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
					</components>
				</xsl:when>
				
				<xsl:when test="$typeResponse='text' and $typeOfAncestor='table'">
					<columns componentType="{$componentType-Input}" id="{$position}" maxLength="{$lengthResponse}"/>
					<xsl:call-template name="enojs:addResponeTocomponents">
						<xsl:with-param name="responseName" select="$responseName"/>
					</xsl:call-template>
				</xsl:when>
				
				<xsl:when test="$typeResponse='number' and $questionName!='' and $typeOfAncestor!='table'">
					<components xsi:type="{$componentType-InputNumber}" componentType="{$componentType-InputNumber}" id="{$idQuestion}"> 
						<xsl:if test="$minimumResponse!=''">
							<xsl:attribute name="min"><xsl:value-of select="$minimumResponse"/></xsl:attribute>
						</xsl:if>
						<xsl:if test="$maximumResponse!=''">
							<xsl:attribute name="max"><xsl:value-of select="$maximumResponse"/></xsl:attribute>
						</xsl:if>
						<xsl:if test="$numberOfDecimals!=''">
							<xsl:attribute name="decimals"><xsl:value-of select="$numberOfDecimals"/></xsl:attribute>
						</xsl:if>
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:if test="$unit!=''">
							<unit><xsl:value-of select="$unit"/></unit>
						</xsl:if>
						<xsl:copy-of select="$declarations"></xsl:copy-of>
						<xsl:call-template name="enojs:addResponeTocomponents">
							<xsl:with-param name="responseName" select="$responseName"/>
							
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
					</components>
				</xsl:when>
				
				<xsl:when test="$typeResponse='number' and $typeOfAncestor='table'">
					<columns componentType="{$componentType-InputNumber}" id="{$position}">
						<xsl:if test="$minimumResponse!=''">
							<xsl:attribute name="min"><xsl:value-of select="$minimumResponse"/></xsl:attribute>
						</xsl:if>
						<xsl:if test="$maximumResponse!=''">
							<xsl:attribute name="max"><xsl:value-of select="$maximumResponse"/></xsl:attribute>
						</xsl:if>
						<xsl:if test="$numberOfDecimals!=''">
							<xsl:attribute name="decimals"><xsl:value-of select="$numberOfDecimals"/></xsl:attribute>
						</xsl:if>
						<xsl:if test="$unit!=''">
							<unit><xsl:value-of select="$unit"/></unit>
						</xsl:if>
					</columns>
					<xsl:call-template name="enojs:addResponeTocomponents">
						<xsl:with-param name="responseName" select="$responseName"/>
					</xsl:call-template>
				</xsl:when>
				
			</xsl:choose>
			
			<xsl:call-template name="enojs:addVariableCollected">
				<xsl:with-param name="responseName" select="$responseName"/>
				<xsl:with-param name="responseRef" select="$responseName"/>
			</xsl:call-template>
		</xsl:if>		
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
	</xsl:template>	
	
	<xsl:template match="DateTimeDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="questionName" tunnel="yes"/>
		<xsl:param name="labelQuestion" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="declarations" as="node()*" tunnel="yes"/>
		<xsl:param name="filterCondition" tunnel="yes"/>
		
		<xsl:param name="typeOfAncestor" tunnel="yes"/>
		<xsl:param name="position" tunnel="yes"/>
		
		<xsl:variable name="componentType-Datepicker" select="'Datepicker'"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		<xsl:variable name="dateFormat" select="enojs:get-format($source-context)"/>
		<xsl:choose>
			<xsl:when test="$typeOfAncestor='table'">
				<columns componentType="{$componentType-Datepicker}" id="{$position}">
					<dateFormat><xsl:value-of select="$dateFormat"/></dateFormat>
				</columns>
				<xsl:call-template name="enojs:addResponeTocomponents">
					<xsl:with-param name="responseName" select="$responseName"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<components xsi:type="{$componentType-Datepicker}" componentType="{$componentType-Datepicker}" id="{$idQuestion}">
					<label><xsl:value-of select="$labelQuestion"/></label>
					<xsl:copy-of select="$declarations"/>
					<xsl:call-template name="enojs:addResponeTocomponents">
						<xsl:with-param name="responseName" select="$responseName"/>				
						
					</xsl:call-template>
					<xsl:copy-of select="$filterCondition"/>
					<dateFormat><xsl:value-of select="$dateFormat"/></dateFormat>
				</components>
			</xsl:otherwise>
		</xsl:choose>
		
		
		<xsl:call-template name="enojs:addVariableCollected">
			<xsl:with-param name="responseName" select="$responseName"/>
			<xsl:with-param name="responseRef" select="$responseName"/>
		</xsl:call-template>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on xf-select driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-select" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="driver" tunnel="yes"/>
		<xsl:param name="typeOfAncestor" tunnel="yes"/>
		<xsl:param name="typeOfQuestion" tunnel="yes"/>	
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="filterCondition" tunnel="yes"/>
		
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="questionName" tunnel="yes"/>
		<xsl:param name="labelQuestion" tunnel="yes"/>
		<xsl:param name="declarations" as="node()*" tunnel="yes"/>
		
		<xsl:param name="position" tunnel="yes"/>
		
		<xsl:variable name="componentType-CheckboxOne" select="'CheckboxOne'"/>
		<xsl:variable name="componentType-CheckboxBoolean" select="'CheckboxBoolean'"/>
		
		<xsl:variable name="name" select="enojs:get-codelist-name($source-context)"/>
		<xsl:variable name="idCodeList" select="enojs:get-codelist-id($source-context)"/>
		<xsl:variable name="maximumLengthCode" select="enojs:get-code-maximum-length($source-context)"/>
		<xsl:variable name="typeResponse" select="enojs:get-type($source-context)"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		
		
		<xsl:choose>
			
			<xsl:when test="$maximumLengthCode != '' and $questionName!=''">
				<!-- remove Format in the cell for table 'question multiple-choice-question'-->
				<xsl:if test="$typeOfQuestion!='MultipleChoiceQuestion' and $typeOfAncestor!='table'">
					<components xsi:type="{$componentType-CheckboxOne}" componentType="{$componentType-CheckboxOne}" id="{$idQuestion}">
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:copy-of select="$declarations"></xsl:copy-of>
						
						<codeLists id="{$idCodeList}">
							<label><xsl:value-of select="$name"/></label>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
								<xsl:with-param name="typeResponse" select="$typeResponse" tunnel="yes"/>
								<xsl:with-param name="typeOfAncestor" select="'codeLists'" tunnel="yes"/>
							</xsl:apply-templates>
						</codeLists>
						
						<xsl:call-template name="enojs:addResponeTocomponents">
							<xsl:with-param name="responseName" select="$responseName"/>
							
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
					</components>
					
					<xsl:call-template name="enojs:addVariableCollected">
						<xsl:with-param name="responseName" select="$responseName"/>
						<xsl:with-param name="responseRef" select="$responseName"/>
					</xsl:call-template>
				</xsl:if>
				
				<xsl:if test="$typeOfQuestion!='MultipleChoiceQuestion' and $typeOfAncestor='table'">
					<columns id="{$position}" componentType="{$componentType-CheckboxOne}">
						<codeLists id="{$idCodeList}">
							<label><xsl:value-of select="$name"/></label>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
								<xsl:with-param name="typeOfAncestor" select="'codeLists'" tunnel="yes"/>
							</xsl:apply-templates>
						</codeLists>
					</columns>
					<xsl:call-template name="enojs:addResponeTocomponents">
						<xsl:with-param name="responseName" select="$responseName"/>
					</xsl:call-template>
					<xsl:call-template name="enojs:addVariableCollected">
						<xsl:with-param name="responseName" select="$responseName"/>
						<xsl:with-param name="responseRef" select="$responseName"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			
			<xsl:when test="$typeResponse='boolean' and $typeOfQuestion='MultipleChoiceQuestion' and $typeOfAncestor!='table'">
				<items id="{enojs:get-name($source-context)}">
					<!-- call item driver for the label -->
					<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
						<xsl:with-param name="driver" select="." tunnel="yes"/>
						<xsl:with-param name="typeOfAncestor" select="'Checkbox'" tunnel="yes"/>
					</xsl:apply-templates>
					<xsl:call-template name="enojs:addResponeTocomponents">
						<xsl:with-param name="responseName" select="$responseName"/>
						<xsl:with-param name="responseType" select="'Boolean'"/>
					</xsl:call-template>
				</items>
				<xsl:call-template name="enojs:addVariableCollected">
					<xsl:with-param name="responseName" select="$responseName"/>
					<xsl:with-param name="responseRef" select="$responseName"/>
				</xsl:call-template>
			</xsl:when>
			
			<xsl:when test="$typeResponse='boolean' and $typeOfQuestion='SingleResponseQuestion' and $idQuestion!='' and $typeOfAncestor!='table'">
				<components xsi:type="{$componentType-CheckboxBoolean}" componentType="{$componentType-CheckboxBoolean}" id="{$idQuestion}">
					<label><xsl:value-of select="$labelQuestion"/></label>
					<xsl:copy-of select="$declarations"/>
					
					<xsl:call-template name="enojs:addResponeTocomponents">
						<xsl:with-param name="responseName" select="$responseName"/>
						<xsl:with-param name="responseType" select="'Boolean'"/>
					</xsl:call-template>
					<xsl:copy-of select="$filterCondition"/>
				</components>
				
				<xsl:call-template name="enojs:addVariableCollected">
					<xsl:with-param name="responseName" select="$responseName"/>
					<xsl:with-param name="responseRef" select="$responseName"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$typeResponse='boolean' and $typeOfAncestor='table'">
				<columns id="{$position}" componentType="{$componentType-CheckboxBoolean}"/>
				<xsl:call-template name="enojs:addResponeTocomponents">
					<xsl:with-param name="responseName" select="$responseName"/>
					<xsl:with-param name="responseType" select="'Boolean'"/>
				</xsl:call-template>
				<xsl:call-template name="enojs:addVariableCollected">
					<xsl:with-param name="responseName" select="$responseName"/>
					<xsl:with-param name="responseRef" select="$responseName"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>	
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on xf-select1 driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-select1" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="driver" tunnel="yes"/>
		<xsl:param name="typeOfAncestor" tunnel="yes"/>		
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="filterCondition" tunnel="yes"/>
		
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="questionName" tunnel="yes"/>
		<xsl:param name="labelQuestion" tunnel="yes"/>
		<xsl:param name="declarations" as="node()*" tunnel="yes"/>
		
		<xsl:param name="position" tunnel="yes"/>
		
		<xsl:variable name="componentType-Radio" select="'Radio'"/>
		<xsl:variable name="componentType-Dropdown" select="'Dropdown'"/>
		
		<xsl:variable name="name" select="enojs:get-codelist-name($source-context)"/>
		<xsl:variable name="idCodeList" select="enojs:get-codelist-id($source-context)"/>
		<xsl:variable name="typeResponse" select="enojs:get-type($source-context)"/>
		<xsl:variable name="lengthResponse" select="enojs:get-length($source-context)"/>
		<xsl:variable name="maximumLengthCode" select="enojs:get-code-maximum-length($source-context)"/>
		<xsl:variable name="typeXf" select="enojs:get-appearance($source-context)"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		
		<xsl:if test="$maximumLengthCode != '' and $typeOfAncestor!='question multiple-choice-question' and $questionName!=''">
			<xsl:choose>
				<xsl:when test="$typeXf='full'">
					<components xsi:type="{$componentType-Radio}" componentType="{$componentType-Radio}" id="{$idQuestion}">
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:copy-of select="$declarations"></xsl:copy-of>
						
						
						<codeLists id="{$idCodeList}">
							<label><xsl:value-of select="$name"/></label>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
								<xsl:with-param name="typeOfAncestor" select="'codeLists'" tunnel="yes"/>
							</xsl:apply-templates>
						</codeLists>
						
						<xsl:call-template name="enojs:addResponeTocomponents">
							<xsl:with-param name="responseName" select="$responseName"/>
							
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
						
					</components>
				</xsl:when>
				<xsl:when test="$typeXf='minimal'">
					<components xsi:type="{$componentType-Dropdown}" componentType="{$componentType-Dropdown}" id="{$idQuestion}">
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:copy-of select="$declarations"></xsl:copy-of>
						<codeLists id="{$idCodeList}">
							<label><xsl:value-of select="$name"/></label>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
								<xsl:with-param name="typeOfAncestor" select="'codeLists'" tunnel="yes"/>
							</xsl:apply-templates>
						</codeLists>
						
						<xsl:call-template name="enojs:addResponeTocomponents">
							<xsl:with-param name="responseName" select="$responseName"/>
							
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
					</components>
				</xsl:when>
			</xsl:choose>
			
			<xsl:call-template name="enojs:addVariableCollected">
				<xsl:with-param name="responseName" select="$responseName"/>
				<xsl:with-param name="responseRef" select="$responseName"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:if test="$maximumLengthCode != '' and $typeOfAncestor='table'">
			<xsl:choose>
				<xsl:when test="$typeXf='full'">
					<columns id="{$position}" componentType="{$componentType-Radio}">
						<codeLists id="{$idCodeList}">
							<label><xsl:value-of select="$name"/></label>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
								<xsl:with-param name="typeOfAncestor" select="'codeLists'" tunnel="yes"/>
							</xsl:apply-templates>
						</codeLists>
					</columns>
					<xsl:call-template name="enojs:addResponeTocomponents">
						<xsl:with-param name="responseName" select="$responseName"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$typeXf='minimal'">
					<columns id="{$position}" componentType="{$componentType-Dropdown}">
						<codeLists id="{$idCodeList}">
							<label><xsl:value-of select="$name"/></label>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
								<xsl:with-param name="typeOfAncestor" select="'codeLists'" tunnel="yes"/>
							</xsl:apply-templates>
						</codeLists>
					</columns>
					<xsl:call-template name="enojs:addResponeTocomponents">
						<xsl:with-param name="responseName" select="$responseName"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			
			<xsl:call-template name="enojs:addVariableCollected">
				<xsl:with-param name="responseName" select="$responseName"/>
				<xsl:with-param name="responseRef" select="$responseName"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the xf-item driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-item" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="ancestorTable" tunnel="yes"/>
		<xsl:param name="typeResponse" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="typeOfAncestor" tunnel="yes"/>
		<xsl:variable name="label" select="enojs:get-label($source-context, $languages[1])"/>
		<xsl:variable name="depth" select="enojs:get-code-depth($source-context)"/>
		<!-- remove item in the cell for table when the response is boolean-->
		<xsl:choose>
			<xsl:when test="$label !='' and $typeResponse!='boolean' and $typeOfAncestor='codeLists'">
				<codes depth="{$depth}">
					<value><xsl:value-of select="enojs:get-value($source-context)"/></value>
					<label><xsl:value-of select="$label"/></label>
				</codes>
			</xsl:when>
			<xsl:when test="$label !='' and $typeOfAncestor='Checkbox'">
				<label><xsl:value-of select="$label"/></label>
			</xsl:when>
		</xsl:choose>
		
	</xsl:template>
	
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on xf-textarea driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-textarea" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="questionName" tunnel="yes"/>
		<xsl:param name="labelQuestion" tunnel="yes"/>
		<xsl:param name="declarations" as="node()*" tunnel="yes"/>
		<xsl:param name="filterCondition" tunnel="yes"/>
		
		<xsl:param name="typeOfAncestor" tunnel="yes"/>
		<xsl:param name="position" tunnel="yes"/>
		
		<xsl:variable name="componentType-Textarea" select="'Textarea'"/>
		
		<xsl:variable name="typeResponse" select="enojs:get-type($source-context)"/>
		<xsl:variable name="lengthResponse" select="enojs:get-length($source-context)"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		
		<xsl:if test="$typeResponse !='' and $questionName!=''">
			<components xsi:type="{$componentType-Textarea}" componentType="{$componentType-Textarea}" id="{$idQuestion}" maxLength="{$lengthResponse}">
				<label><xsl:value-of select="$labelQuestion"/></label>
				<xsl:copy-of select="$declarations"></xsl:copy-of>
				
				<xsl:call-template name="enojs:addResponeTocomponents">
					<xsl:with-param name="responseName" select="$responseName"/>					
				</xsl:call-template>
				<xsl:copy-of select="$filterCondition"/>
			</components>
			
			<xsl:call-template name="enojs:addVariableCollected">
				<xsl:with-param name="responseName" select="$responseName"/>
				<xsl:with-param name="responseRef" select="$responseName"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:if test="$typeResponse !='' and $typeOfAncestor='table'">
			<columns componentType="{$componentType-Textarea}" id="{$position}" maxLength="{$lengthResponse}">
				<label><xsl:value-of select="$labelQuestion"/></label>
				<xsl:copy-of select="$declarations"></xsl:copy-of>				
			</columns>
			<xsl:call-template name="enojs:addResponeTocomponents">
				<xsl:with-param name="responseName" select="$responseName"/>					
			</xsl:call-template>
			<xsl:copy-of select="$filterCondition"/>
			
			<xsl:call-template name="enojs:addVariableCollected">
				<xsl:with-param name="responseName" select="$responseName"/>
				<xsl:with-param name="responseRef" select="$responseName"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the xf-output driver.</xd:p>
			<xd:p>Adding declarations elements.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="positionDeclaration" tunnel="yes"></xsl:param>
		
		<xsl:variable name="instructionFormat" select="upper-case(enojs:get-format($source-context))"/>
		<xsl:variable name="instructionLabel" select="enojs:get-label($source-context, $languages[1])"/>
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
			<xd:p>Match on the CalculatedVariable driver.</xd:p>
			<xd:p>Its displays the formula of the calculated variable on the elements variables.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="CalculatedVariable" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="variableCalculation" select="enojs:get-variable-calculation($source-context)"/>
		
		<xsl:variable name="nameOutVariable" select="enojs:get-business-name($source-context)"/>
		<xsl:variable name="idVariables" select="tokenize(enojs:get-variable-calculation-variables($source-context),'\s')"/>
		<variables>
			<name>
				<xsl:value-of select="$nameOutVariable"/>
			</name>
			<value>
				<xsl:call-template name="replaceVariablesInFormula">
					<xsl:with-param name="formula" select="$variableCalculation"/>
					<xsl:with-param name="variables" select="$idVariables"/>
				</xsl:call-template>				
			</value>
			
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</variables>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Template named:eno:printQuestionTitleWithInstruction.</xd:p>
			<xd:p>It prints the question label and its instructions.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template name="eno:printQuestionTitleWithInstruction" >
		<xsl:param name="driver" tunnel="no"/>
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<!--
			<xsl:apply-templates select="enojs:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="$driver"/>
			<xsl:with-param name="positionDeclaration" select="'BEFORE'" tunnel="yes"/>
			</xsl:apply-templates>			
		-->
		<!-- The enoddi:get-instructions-by-format getter produces in-language fragments, on which templates must be applied in "source" mode. -->
		<xsl:apply-templates select="enojs:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="$driver"/>
			<xsl:with-param name="positionDeclaration" select="'AFTER_QUESTION_TEXT'" tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	
	<xsl:function name="eno:getInstructionForQuestion">
		<xsl:param name="context" as="item()"/>
		<xsl:param name="driver"/>		
		<xsl:apply-templates select="enojs:get-after-question-title-instructions($context)" mode="source">
			<xsl:with-param name="driver" select="$driver"/>
			<xsl:with-param name="positionDeclaration" select="'AFTER_QUESTION_TEXT'" tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:function>
	
	<xsl:template name="enojs:addResponeTocomponents">
		<xsl:param name="responseName"/>
		<xsl:param name="responseType"/>
		<xsl:variable name="ResponseTypeEnum" select="'PREVIOUS,COLLECTED,FORCED,EDITED,INPUTED'" as="xs:string"/>
		<xsl:variable name="responseContainer">
			<xsl:choose>
				<xsl:when test="$responseType!=''">
					<xsl:value-of select="$responseType"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'String'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- responseType="{$responseType}" -->
		<response name="{$responseName}" xsi:type="{concat('ResponseContainer',$responseContainer)}" >
			<xsl:for-each select="tokenize($ResponseTypeEnum,',')">
				<valueState valueType="{.}">
					<xsl:choose>
						<xsl:when test="$responseType='Int'">
							<value>0</value>
						</xsl:when>
						<xsl:when test="$responseType='Boolean'">
							<value><xsl:value-of select="false()"/></value>
						</xsl:when>
						<xsl:otherwise>
							<value/>
						</xsl:otherwise>
					</xsl:choose>
				</valueState>
			</xsl:for-each>
		</response>
	</xsl:template>
	
	<xsl:function name="enojs:createLambdaExpression">
		<xsl:param name="source-context" as="item()"/>
		<xsl:param name="formulaReadOnly" as="xs:string*"/>
		<xsl:param name="formulaRelevant" as="xs:string*"/>
		<xsl:param name="variablesReadOnly" as="xs:string*"/>
		<xsl:param name="variablesRelevant" as="xs:string*"/>
		<!--		
			<xsl:variable name="formulaReadOnly1" as="xs:string*" select="tokenize('$SUM_EXPENSES$!=2,$genoux$!=5',',')"/>		
			<xsl:variable name="variablesReadOnly1" select="'SUM_EXPENSES'" as="xs:string*"/>
		-->
		<xsl:variable name="variableFilterId" as="xs:string*">
			<xsl:for-each select="distinct-values($variablesRelevant)">
				<xsl:sequence select="."/>
			</xsl:for-each>
			<xsl:for-each select="distinct-values($variablesReadOnly)">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="variableFilterName" as="xs:string*">
			<xsl:for-each select="distinct-values($variablesRelevant)">
				<xsl:sequence select="enojs:get-variable-business-name($source-context,.)"/>
			</xsl:for-each>
			<xsl:for-each select="distinct-values($variablesReadOnly)">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="variablesId" as="xs:string*">
			<xsl:for-each select="distinct-values($variableFilterId)">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="variablesName">
			<xsl:for-each select="distinct-values($variableFilterName)">
				<xsl:if test="position()!=1">
					<xsl:value-of select="','"/>
				</xsl:if>
				<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		
		<conditionFilter>
			<!--Expression VTL : #if(condition)je suis true#{else}je suis false#end-->
			<!-- Caution for encodage : # -> &#x23;  { -> &#x7B; } -> &#x7D; -->
			<xsl:variable name="if" select="'&#x23;if'"/>
			<xsl:variable name="else" select="'&#x23;&#x7B;else&#x7D;'"/>
			<xsl:variable name="ifEnd" select="'&#x23;end'"/>
			<xsl:choose>
				<!--<xsl:when test="$variablesName=''">
					<xsl:value-of select="'() => true ? ''normal'' : '''''"/> guillemet autour de normal ? 
					</xsl:when>-->
				<xsl:when test="$formulaRelevant!='' and $formulaReadOnly!=''">
					<xsl:variable name="initial-relevant-ancestors">
						<xsl:for-each select="$formulaRelevant">
							<xsl:value-of select="concat('(',.,')')"/>
							<xsl:if test="position()!=last()">
								<xsl:value-of select="' || '"/><!-- "||" = "or"-->
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>
					<xsl:variable name="relevant-condition">
						<xsl:call-template name="replaceVariablesInFormula">
							<xsl:with-param name="source-context" select="$source-context" as="item()" tunnel="yes"/>
							<xsl:with-param name="formula" select="$initial-relevant-ancestors"/>
							<xsl:with-param name="variables" select="$variablesId"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="initial-readonly-ancestors">
						<xsl:for-each select="$formulaReadOnly">
							<xsl:value-of select="concat('(',.,')')"/>
							<xsl:if test="position()!=last()">
								<xsl:value-of select="' || '"/>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>
					<xsl:variable name="readonly-condition">
						<xsl:call-template name="replaceVariablesInFormula">
							<xsl:with-param name="source-context" select="$source-context" as="item()" tunnel="yes"/>
							<xsl:with-param name="formula" select="$initial-readonly-ancestors"/>
							<xsl:with-param name="variables" select="$variablesId"/>
						</xsl:call-template>
					</xsl:variable>
					<!-- replace "not -> "!", " and " -> "&&", " or " -> "||", "=" -> "==" -->
					<xsl:variable name="returned-relevant-condition" select="replace(replace(replace(replace($relevant-condition,'not','!'),'\sand\s','&amp;&amp;'),'\sor\s',' || '),'\s=\s',' == ')"/>
					<xsl:variable name="returned-readonly-condition" select="replace(replace(replace(replace($readonly-condition,'not','!'),'\sand\s','&amp;&amp;'),'\sor\s',' || '),'\s=\s',' == ')"/>
					
					<!--<xsl:value-of select="concat('(',$variablesName,') =>', $readonly-condition,'toto',$relevant-condition,' ? ''normal'' : ''''')"/>-->
					<!-- les trois possibles : caché (hidden) , gris (readOnly), affiché (normal) -->
					<!--	si relevant
						alors 
						si readonly,
						alors normal
						sinon readonly
						sinon hidden-->
					<xsl:value-of select="concat(
						$if,'(',$returned-relevant-condition,')',
						$if,'(',$returned-readonly-condition,')normal',
						$else,'readonly',$ifEnd,')',
						$else,'hidden',$ifEnd
						)"/>
				</xsl:when>
				<xsl:when test="$formulaRelevant!=''">
					<xsl:variable name="initial-relevant-ancestors">
						<xsl:for-each select="$formulaRelevant">
							<xsl:value-of select="concat('(',.,')')"/>
							<xsl:if test="position()!=last()">
								<xsl:value-of select="' || '"/>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>
					<xsl:variable name="relevant-condition">
						<xsl:call-template name="replaceVariablesInFormula">
							<xsl:with-param name="source-context" select="$source-context" as="item()" tunnel="yes"/>
							<xsl:with-param name="formula" select="$initial-relevant-ancestors"/>
							<xsl:with-param name="variables" select="$variablesId"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="returned-relevant-condition" select="replace(replace(replace(replace($relevant-condition,'not','!'),'\sand\s',' &amp;&amp; '),'\sor\s',' || '),'\s=\s',' == ')"/>
					
					<xsl:value-of select="concat($if,'(', $returned-relevant-condition,')normal',$else,'hidden',$ifEnd)"/>
					
					<!-- pas de gris, on affiche (normal) ou pas (hidden) -->
				</xsl:when>
				<xsl:when test="$formulaReadOnly!=''">
					<xsl:variable name="initial-readonly-ancestors">
						<xsl:for-each select="$formulaReadOnly">
							<xsl:value-of select="concat('(',.,')')"/>
							<xsl:if test="position()!=last()">
								<xsl:value-of select="' || '"/>
							</xsl:if>
						</xsl:for-each>
					</xsl:variable>
					<xsl:variable name="readonly-condition">
						<xsl:call-template name="replaceVariablesInFormula">
							<xsl:with-param name="source-context" select="$source-context" as="item()" tunnel="yes"/>
							<xsl:with-param name="formula" select="$initial-readonly-ancestors"/>
							<xsl:with-param name="variables" select="$variablesId"/>
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="returned-readonly-condition" select="replace(replace(replace(replace($readonly-condition,'not','!'),'\sand\s',' &amp;&amp; '),'\sor\s',' || '),'\s=\s',' == ')"/>
					<xsl:value-of select="concat($if,'(',$returned-readonly-condition,')normal',$else,'readonly',$ifEnd)"/>
					<!-- on ne cache pas , gris (readOnly) ou affiché (normal)-->
				</xsl:when>
				
				<xsl:otherwise>
					<xsl:value-of select="'normal'"/>
				</xsl:otherwise>
			</xsl:choose>
			
		</conditionFilter>
	</xsl:function>
	
	
	<xd:doc>
		<xd:desc>
			<xd:p>Template named:replaceVariablesInFormula.</xd:p>
			<xd:p>It replaces variables in a all formula (control, instruction, filter).</xd:p>
			<xd:p>"number(if (¤idVariable¤='') then '0' else ¤idVariable¤)" -> "variableName"</xd:p>
			<xd:p>"¤idVariable¤" -> "variableName"</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template name="replaceVariablesInFormula">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="formula"/>
		<xsl:param name="variables" as="xs:string*"/>
		
		<xsl:choose>
			<xsl:when test="count($variables)=0">
				<xsl:value-of select="$formula"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="regexA" select="concat('number\(if\s+\(',$conditioning-variable-begin,$variables[1],$conditioning-variable-end,'=''''\)\sthen\s+''0''\s+else\s+',$conditioning-variable-begin,$variables[1],$conditioning-variable-end,'\)')"/>
				<xsl:variable name="regexB" select="concat($conditioning-variable-begin,$variables[1],$conditioning-variable-end)"/>				
				<xsl:variable name="expressionToReplace" select="concat('\$',enojs:get-variable-business-name($source-context,$variables[1]))"/>				
				<xsl:variable name="newFormula" select="replace(replace($formula,
					$regexA,$expressionToReplace),
					$regexB,$expressionToReplace)"/>
				
				<xsl:call-template name="replaceVariablesInFormula">
					<xsl:with-param name="formula" select="$newFormula"/>
					<xsl:with-param name="variables" select="$variables[position() &gt; 1]"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	
	
	
	<xsl:template name="enojs:addVariableCollected">
		<xsl:param name="responseName"/>
		<xsl:param name="responseRef"/>
		<variables>
			<name><xsl:value-of select="$responseName"/></name>
			<responseRef><xsl:value-of select="$responseRef"/></responseRef>
		</variables>
	</xsl:template>
	
	
</xsl:stylesheet>