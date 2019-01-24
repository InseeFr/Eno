<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:fn="http://www.w3.org/2005/xpath-functions" 
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" 
	xmlns:eno="http://xml.insee.fr/apps/eno" 
	xmlns:enojs="http://xml.insee.fr/apps/eno/out/js"
	exclude-result-prefixes="xs fn xd eno enojs" version="2.0">
	
	<xsl:import href="../../../styles/style.xsl"/>
	
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
		<xsl:param name="isInSurvey" tunnel="yes"/>
		<xsl:variable name="type" select="enojs:getTypeOfVariable($source-context)"/>
		<!-- display only external variable -->
		<xsl:if test="$type='External' and $isInSurvey!='yes'">
			<variable>
				<name><xsl:value-of select="enojs:get-name($source-context)"/></name>
			</variable>	
		</xsl:if>
			
		
			
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
				<xsl:with-param name="isInSurvey" select="'yes'" tunnel="yes"/>
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
		
		<component xsi:type="Sequence" id="{$id}">
			<label><xsl:value-of select="$label"/></label>
			<xsl:call-template name="eno:printQuestionTitleWithInstruction">
				<xsl:with-param name="driver" select="."/>
			</xsl:call-template>
			<xsl:copy-of select="$filterCondition"/>
			
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</component>
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
		
		<component xsi:type="Subsequence" id="{$id}">
			<label><xsl:value-of select="$label"/></label>
			<xsl:call-template name="eno:printQuestionTitleWithInstruction">
				<xsl:with-param name="driver" select="."/>
			</xsl:call-template>
			<xsl:copy-of select="$filterCondition"/>			
			
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</component>
	</xsl:template>
	
	<xsl:template match="SingleResponseQuestion | MultipleQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="questionName" select="enojs:get-question-name($source-context,$languages[1])"/>
		
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
		
		<xsl:if test="$questionName != ''">
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="typeOfAncestor" select="'question'" tunnel="yes"/>
				<xsl:with-param name="idQuestion" select="enojs:get-name($source-context)" tunnel="yes"/>
				<xsl:with-param name="questionName" select="lower-case($questionName)" tunnel="yes"/>
				<xsl:with-param name="labelQuestion" select="enojs:get-label($source-context, $languages[1])" tunnel="yes"/>
				<xsl:with-param name="declarations" select="eno:getInstructionForQuestion($source-context,.)" as="node()*" tunnel="yes"/>
				<xsl:with-param name="filterCondition" select="$filterCondition" tunnel="yes"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on xf-input driver.</xd:p>
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
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
		
		<xsl:variable name="typeResponse" select="enojs:get-type($source-context)"/>
		<xsl:variable name="lengthResponse" select="enojs:get-length($source-context)"/>
		<xsl:variable name="numberOfDecimals">
			<xsl:choose>
				<xsl:when test="enojs:get-number-of-decimals($source-context) !=''">
					<xsl:value-of select="enojs:get-number-of-decimals($source-context)"/>
				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="minimumResponse" select="enojs:get-minimum($source-context)"/>
		<xsl:variable name="maximumResponse" select="enojs:get-maximum($source-context)"/>
		<xsl:variable name="unit" select="enojs:get-suffix($source-context,$languages[1])"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		
		<xsl:if test="$typeResponse!='' and $questionName!=''">
			
			<xsl:choose>
				<xsl:when test="$typeResponse='text'">
					<component xsi:type="Input" id="{$idQuestion}" maxLength="{$lengthResponse}">
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:copy-of select="$declarations"/>
						<xsl:call-template name="enojs:addResponeToComponent">
							<xsl:with-param name="responseName" select="$responseName"/>
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
					</component>
				</xsl:when>
				
				<xsl:when test="$typeResponse='number'">
					<component xsi:type="InputNumber" id="{$idQuestion}" min="{$minimumResponse}" max="{$maximumResponse}" decimals="{$numberOfDecimals}">
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:if test="$unit!=''">
							<unit><xsl:value-of select="$unit"/></unit>
						</xsl:if>
						<xsl:copy-of select="$declarations"></xsl:copy-of>
						<xsl:call-template name="enojs:addResponeToComponent">
							<xsl:with-param name="responseName" select="$responseName"/>
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
					</component>
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
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		<xsl:variable name="dateFormat" select="enojs:get-format($source-context)"/>
		
		<component xsi:type="Datepicker" id="{$idQuestion}">
			<label><xsl:value-of select="$labelQuestion"/></label>
			<dateFormat><xsl:value-of select="$dateFormat"/></dateFormat>
			<declarations>
				<xsl:copy-of select="$declarations"/>
			</declarations>
			<xsl:call-template name="enojs:addResponeToComponent">
				<xsl:with-param name="responseName" select="$responseName"/>
			</xsl:call-template>
			<xsl:copy-of select="$filterCondition"/>
		</component>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on xf-select driver.</xd:p>
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-select" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="driver" tunnel="yes"/>
		<xsl:param name="typeOfAncestor" tunnel="yes"/>		
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="filterCondition" tunnel="yes"/>
		
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="questionName" tunnel="yes"/>
		<xsl:param name="labelQuestion" tunnel="yes"/>
		<xsl:param name="declarations" as="node()*" tunnel="yes"/>
		
		<xsl:variable name="name" select="enojs:get-name-Codelist($source-context)"/>
		<xsl:variable name="idCodeList" select="enojs:get-id-Codelist($source-context)"/>
		<xsl:variable name="maximumLengthCode" select="enojs:get-code-maximum-length($source-context)"/>
		<xsl:variable name="typeResponse" select="enojs:get-type($source-context)"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		
		<xsl:choose>
			<xsl:when test="$maximumLengthCode != '' and $questionName!=''">
				<!-- remove Format in the cell for table 'question multiple-choice-question'-->
				<xsl:if test="$typeOfAncestor!='question multiple-choice-question'">
					<component xsi:type="CheckboxOne" id="{$idQuestion}">
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:copy-of select="$declarations"></xsl:copy-of>
						
						<codeLists id="{$idCodeList}">
							<label><xsl:value-of select="$name"/></label>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
								<xsl:with-param name="typeResponse" select="$typeResponse" tunnel="yes"/>
							</xsl:apply-templates>
						</codeLists>
						
						<xsl:call-template name="enojs:addResponeToComponent">
							<xsl:with-param name="responseName" select="$responseName"/>
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
					</component>
					
					<xsl:call-template name="enojs:addVariableCollected">
						<xsl:with-param name="responseName" select="$responseName"/>
						<xsl:with-param name="responseRef" select="$responseName"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			
			<xsl:when test="$typeResponse='boolean' and $typeOfAncestor!=''">
				<component xsi:type="CheckboxBoolean" id="{$idQuestion}">
					<label><xsl:value-of select="$labelQuestion"/></label>
					<xsl:copy-of select="$declarations"></xsl:copy-of>
					
					<xsl:call-template name="enojs:addResponeToComponent">
						<xsl:with-param name="responseName" select="$responseName"/>
					</xsl:call-template>
					<xsl:copy-of select="$filterCondition"/>
				</component>
				
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
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
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
		
		<xsl:variable name="name" select="enojs:get-name-Codelist($source-context)"/>
		<xsl:variable name="idCodeList" select="enojs:get-id-Codelist($source-context)"/>
		<xsl:variable name="typeResponse" select="enojs:get-type($source-context)"/>
		<xsl:variable name="lengthResponse" select="enojs:get-length($source-context)"/>
		<xsl:variable name="maximumLengthCode" select="enojs:get-code-maximum-length($source-context)"/>
		<xsl:variable name="typeXf" select="enojs:get-appearance($source-context)"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		
		<xsl:if test="$maximumLengthCode != '' and $typeOfAncestor!='question multiple-choice-question' and $questionName!=''">
			<xsl:choose>
				<xsl:when test="$typeXf='full'">
					<component xsi:type="Radio" id="{$idQuestion}">
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:copy-of select="$declarations"></xsl:copy-of>
						
						
						<codeLists id="{$idCodeList}">
							<label><xsl:value-of select="$name"/></label>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
							</xsl:apply-templates>
						</codeLists>
												
						<xsl:call-template name="enojs:addResponeToComponent">
							<xsl:with-param name="responseName" select="$responseName"/>
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
						
					</component>
				</xsl:when>
				<xsl:when test="$typeXf='minimal'">
					<component xsi:type="Dropdown" id="{$idQuestion}">
						<label><xsl:value-of select="$labelQuestion"/></label>
						<xsl:copy-of select="$declarations"></xsl:copy-of>
						
						<codeLists id="{$idCodeList}">
							<label><xsl:value-of select="$name"/></label>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
							</xsl:apply-templates>
						</codeLists>
						
						<xsl:call-template name="enojs:addResponeToComponent">
							<xsl:with-param name="responseName" select="$responseName"/>
						</xsl:call-template>
						<xsl:copy-of select="$filterCondition"/>
					</component>
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
			<xd:p>It writes the code value and the label of the item.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-item" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="ancestorTable" tunnel="yes"/>
		<xsl:param name="typeResponse" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="typeOfAncestor" tunnel="yes"/>
		<xsl:variable name="label" select="enojs:get-label($source-context, $languages[1])"/>
		<xsl:variable name="name" select="enojs:get-question-name($source-context, $languages[1])"/>
		<!-- remove item in the cell for table when the response is boolean-->
		<xsl:if test="$label !='' and $typeResponse!='boolean' and $typeOfAncestor!=''">
			<code>
				<parent><xsl:value-of select="$name"/></parent>
				<value><xsl:value-of select="enojs:get-value($source-context)"/></value>
				<label><xsl:value-of select="$label"/></label>
			</code>
		</xsl:if>
	</xsl:template>
	
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on xf-textarea driver.</xd:p>
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
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
		
		<xsl:variable name="typeResponse" select="enojs:get-type($source-context)"/>
		<xsl:variable name="lengthResponse" select="enojs:get-length($source-context)"/>
		
		<xsl:variable name="responseName" select="enojs:get-business-name($source-context)"/>
		
		<xsl:if test="$typeResponse !='' and $questionName!=''">
			<component xsi:type="Textarea" id="{$idQuestion}" maxLength="{$lengthResponse}">
				<label><xsl:value-of select="$labelQuestion"/></label>
				<xsl:copy-of select="$declarations"></xsl:copy-of>
								
				<xsl:call-template name="enojs:addResponeToComponent">
					<xsl:with-param name="responseName" select="$responseName"/>
				</xsl:call-template>
				<xsl:copy-of select="$filterCondition"/>
			</component>
			
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
			<xd:p>It writes the instruction text, with a different styles for comments, instructions, warning and help.</xd:p>
			<xd:p>It works for all drivers except for drivers whose contain a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="positionDeclaration" tunnel="yes"></xsl:param>
		
		<xsl:variable name="instructionFormat" select="enojs:get-format($source-context)"/>
		<xsl:variable name="instructionLabel" select="enojs:get-label($source-context, $languages[1])"/>
		<xsl:variable name="instructionFormatMaj" select="concat(upper-case(substring($instructionFormat,1,1)),
			substring($instructionFormat,2))" as="xs:string"/>
		
		<xsl:if test="$positionDeclaration!=''">
			<declaration declarationType="{$instructionFormat}" id="{enojs:get-name($source-context)}" position="{$positionDeclaration}">
				<label><xsl:value-of select="$instructionLabel"/></label>
			</declaration>				
		</xsl:if>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
		
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the CalculatedVariable driver.</xd:p>
			<xd:p>Its displays the formula of the calculated variable.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="CalculatedVariable" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="variableCalculation" select="enojs:get-variable-calculation($source-context)"/>
		
		<xsl:variable name="nameOutVariable" select="enojs:get-business-name($source-context)"/>
		<xsl:variable name="idVariables" select="tokenize(enojs:get-variable-calculation-variables($source-context),'\s')"/>
		<variable>
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
		</variable>
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
	
	<xsl:template name="enojs:addResponeToComponent">
		<xsl:param name="responseName"/>
		<xsl:variable name="ResponseTypeEnum" select="'PREVIOUS,COLLECTED,FORCED,EDITED,INPUTED'" as="xs:string"/>
		<response name="{$responseName}">
			<xsl:for-each select="tokenize($ResponseTypeEnum,',')">
				<valueState type="{.}">
					<value/>
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
					<!-- replace "!=" -> "!==" , "not -> "!", " and " -> "&&", " or " -> "||"-->
					<xsl:variable name="returned-relevant-condition" select="replace(replace(replace(replace($relevant-condition,'!=','!=='),'not','!'),'\sand\s','&amp;&amp;'),'\sor\s',' || ')"/>
					<xsl:variable name="returned-readonly-condition" select="replace(replace(replace(replace($readonly-condition,'!=','!=='),'not','!'),'\sand\s','&amp;&amp;'),'\sor\s',' || ')"/>
					
					<!--<xsl:value-of select="concat('(',$variablesName,') =>', $readonly-condition,'toto',$relevant-condition,' ? ''normal'' : ''''')"/>-->
					<!-- les trois possibles : caché (hidden) , gris (readOnly), affiché (normal) -->
					<!--	si relevant
						alors 
							si readonly,
								alors normal
								sinon readonly
					sinon hidden-->
					<xsl:value-of select="concat('(',$variablesName,') =>', 
						$returned-relevant-condition,' ? ',
						'(',$returned-readonly-condition,' ? ''normal'' : ''readonly'')',
						': ''hidden''')"/>
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
					<xsl:variable name="returned-relevant-condition" select="replace(replace(replace(replace($relevant-condition,'!=','!=='),'not','!'),'\sand\s','&amp;&amp;'),'\sor\s',' || ')"/>
					
					<xsl:value-of select="concat('(',$variablesName,') =>', $returned-relevant-condition,' ? ''normal'' : ''hidden''')"/>
					
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
					<xsl:variable name="returned-readonly-condition" select="replace(replace(replace(replace($readonly-condition,'!=','!=='),'not','!'),'\sand\s','&amp;&amp;'),'\sor\s',' || ')"/>
					<xsl:value-of select="'(',$variablesName,') => ',$returned-readonly-condition,' ? ''normal'' : ''readonly'''"/>
					<!-- on ne cache pas , gris (readOnly) ou affiché (normal)-->
				</xsl:when>
				
				<xsl:otherwise>
					<xsl:value-of select="'() => true ? ''normal'' : '''''"/>
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
				<xsl:variable name="expressionToReplace" select="concat('\$',enojs:get-variable-business-name($source-context,$variables[1]),'\$')"/>				
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
		<variable>
			<name><xsl:value-of select="$responseName"/></name>
			<responseRef><xsl:value-of select="$responseRef"/></responseRef>
		</variable>
	</xsl:template>
	
	
</xsl:stylesheet>