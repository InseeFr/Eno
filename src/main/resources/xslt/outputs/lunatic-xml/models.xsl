<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn="http://www.w3.org/2005/xpath-functions"
				xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
				xmlns:eno="http://xml.insee.fr/apps/eno"
				xmlns:enolunatic="http://xml.insee.fr/apps/eno/out/js"
				xmlns="http://xml.insee.fr/schema/applis/lunatic-h"
				exclude-result-prefixes="xs fn xd eno enolunatic" version="2.0">

	<xd:doc scope="stylesheet">
		<xd:desc>
			<xd:p>An xslt stylesheet who transforms an input into js through generic driver templates.</xd:p>
			<xd:p>The real input is mapped with the drivers.</xd:p>
		</xd:desc>
	</xd:doc>

	<xsl:variable name="varName" select="parent"/>

	<xd:doc>
		<xd:desc>VariableGroup only leads to its children</xd:desc>
	</xd:doc>
	<xsl:template match="VariableGroup" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>template Variable is used only for external variables : TODO : refactor for other ones</xd:desc>
	</xd:doc>
	<xsl:template match="Variable" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<!-- display only external variable -->
		<xsl:if test="enolunatic:get-variable-type($source-context) = 'external'">
			<variables variableType="EXTERNAL" xsi:type="VariableType">
				<name><xsl:value-of select="enolunatic:get-name($source-context)"/></name>
				<value xsi:nil="true"/>
			</variables>
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
		<xsl:variable name="languages" select="enolunatic:get-form-languages($source-context)" as="xs:string +"/>
		<xsl:variable name="id" select="replace(enolunatic:get-name($source-context),'Sequence-','')"/>
		<xsl:variable name="label" select="enolunatic:get-label($source-context, $languages[1])"/>
		<Questionnaire id="{$id}" modele="{enolunatic:get-form-model($source-context)}" enoCoreVersion="{$enoVersion}">
			<label><xsl:value-of select="$label"/></label>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="languages" select="$languages" tunnel="yes"/>
			</xsl:apply-templates>
		</Questionnaire>
	</xsl:template>


	<xd:doc>
		<xs:desc>
			<xd:p>Match on QuestionLoop driver</xd:p>
		</xs:desc>
	</xd:doc>
	<xsl:template match="QuestionLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loopDepth" select="0" tunnel="yes"/>
		<xsl:param name="idLoop" select="''" tunnel="yes"/>
		<xsl:param name="sequenceParent" tunnel="yes"/>
		<xsl:variable name="componentType" select="'Loop'"/>
		<xsl:variable name="isGeneratingLoop" select="enolunatic:is-generating-loop($source-context)" as="xs:boolean"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context,enolunatic:get-global-filter($source-context))"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>
		<xsl:variable name="id" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="minimumOccurrences">
			<xsl:call-template name="enolunatic:replace-variables-in-formula">
				<xsl:with-param name="source-context" select="$source-context"/>
				<xsl:with-param name="formula" select="enolunatic:get-minimum-occurrences($source-context)"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="maximumOccurrences">
			<xsl:call-template name="enolunatic:replace-variables-in-formula">
				<xsl:with-param name="source-context" select="$source-context"/>
				<xsl:with-param name="formula" select="enolunatic:get-maximum-occurrences($source-context)"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- keep idLoop of the parent Loop if exists -->
		<xsl:variable name="newIdLoop" select="if($idLoop!='') then $idLoop else $id"/>
		<xsl:variable name="newLoopDepth" select="$loopDepth + 1"/>
		
		<components xsi:type="{$componentType}" componentType="{$componentType}" id="{$id}">
			<xsl:if test="not($isGeneratingLoop)">
				<xsl:attribute name="depth" select="$newLoopDepth"/>
				<xsl:attribute name="min" select="if ($minimumOccurrences!='') then $minimumOccurrences else 0"  />
				<xsl:if test="$maximumOccurrences!=''">
					<xsl:attribute name="iterations" select="enolunatic:replace-all-variables-with-business-name($source-context,$maximumOccurrences)"/>
				</xsl:if>
				<idGenerator><xsl:value-of select="enolunatic:get-loop-generator-id($source-context)"/></idGenerator>
			</xsl:if>
			<xsl:if test="$label!=''">
				<label><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></label>
			</xsl:if>
			<conditionFilter><xsl:value-of select="$filterCondition"/></conditionFilter>
			<xsl:if test="$sequenceParent">
				<hierarchy>
					<xsl:copy-of select="$sequenceParent"/>
				</hierarchy>
			</xsl:if>			
			<xsl:copy-of select="$dependencies"/>
			
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="loopDepth" select="$newLoopDepth" tunnel="yes"/>
				<xsl:with-param name="idLoop" select="$newIdLoop" tunnel="yes"/>
			</xsl:apply-templates>
		</components>

	</xsl:template>

	<xd:doc>
		<xd:desc>
			<xd:p>Match on Module and SubModule drivers.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Module | SubModule" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="sequenceParent" tunnel="yes"/>

		<xsl:variable name="id" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="componentType-Sequence">
			<xsl:choose>
				<xsl:when test="self::Module"><xsl:value-of select="'Sequence'"/></xsl:when>
				<xsl:when test="self::SubModule"><xsl:value-of select="'Subsequence'"/></xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="finalLabel" select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/>
		<xsl:variable name="filterCondition" select="enolunatic:get-global-filter($source-context)"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>
		
		<xsl:variable name="sequence">
			<xsl:choose>
				<xsl:when test="self::Module">
					<sequence id="{$id}">
						<label><xsl:value-of select="$finalLabel"/></label>
					</sequence>
				</xsl:when>
				<xsl:when test="self::SubModule"><xsl:copy-of select="$sequenceParent"/></xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="subSequence">
			<xsl:choose>
				<xsl:when test="self::SubModule">
					<subSequence id="{$id}">
						<label><xsl:value-of select="$finalLabel"/></label>
					</subSequence>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<components xsi:type="{$componentType-Sequence}" componentType="{$componentType-Sequence}" id="{$id}">
			<label><xsl:value-of select="$finalLabel"/></label>
			<xsl:copy-of select="enolunatic:getInstructionForQuestion($source-context,.)"/>
			<conditionFilter><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$filterCondition)"/></conditionFilter>
			<hierarchy>				
				<xsl:copy-of select="$sequence"/>
				<xsl:copy-of select="$subSequence"/>
			</hierarchy>
			<xsl:copy-of select="$dependencies"/>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="sequenceParent" select="$sequence" tunnel="yes"/>
				<xsl:with-param name="subSequenceParent" select="$subSequence" tunnel="yes"/>
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

		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context, enolunatic:get-global-filter($source-context))"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>

		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
			<xsl:with-param name="idQuestion" select="enolunatic:get-name($source-context)" tunnel="yes"/>
			<xsl:with-param name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])" tunnel="yes"/>
			<xsl:with-param name="labelQuestion" select="enolunatic:replace-all-variables-with-business-name($source-context, $label)" tunnel="yes"/>
			<xsl:with-param name="typeOfQuestion" select="self::*/name()" tunnel="yes"/>
			<xsl:with-param name="declarations" select="enolunatic:getInstructionForQuestion($source-context,.)" as="node()*" tunnel="yes"/>
			<xsl:with-param name="filterCondition" select="$filterCondition" tunnel="yes"/>
			<xsl:with-param name="dependencies" select="$dependencies" tunnel="yes"/>
		</xsl:apply-templates>

		<xsl:apply-templates select="enolunatic:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:desc>MultipleChoiceQuestion driver creates a CheckboxGroup component</xd:desc>
	</xd:doc>
	<xsl:template match="MultipleChoiceQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="sequenceParent" tunnel="yes"/>
		<xsl:param name="subSequenceParent" tunnel="yes"/>

		<xsl:variable name="idQuestion" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context, enolunatic:get-global-filter($source-context))"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>

		<components xsi:type="CheckboxGroup" componentType="CheckboxGroup" id="{$idQuestion}">
			<label><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context, $label)"/></label>
			<xsl:copy-of select="enolunatic:getInstructionForQuestion($source-context,.)"/>
			<conditionFilter><xsl:value-of select="$filterCondition"/></conditionFilter>
			<hierarchy>
				<xsl:copy-of select="$sequenceParent"/>
				<xsl:copy-of select="$subSequenceParent"/>
			</hierarchy>
			<xsl:copy-of select="$dependencies"/>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="idQuestion" select="$idQuestion" tunnel="yes"/>
				<xsl:with-param name="questionName" select="$questionName" tunnel="yes"/>
				<xsl:with-param name="labelQuestion" select="enolunatic:replace-all-variables-with-business-name($source-context, $label)" tunnel="yes"/>
				<xsl:with-param name="typeOfQuestion" select="self::*/name()" tunnel="yes"/>
				<xsl:with-param name="declarations" select="enolunatic:getInstructionForQuestion($source-context,.)" as="node()*" tunnel="yes"/>
				<xsl:with-param name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context, $filterCondition)" tunnel="yes"/>
			</xsl:apply-templates>
		</components>
		
		<xsl:if test="$addFilterResult">
			<xsl:call-template name="enolunatic:add-calculated-variable-filter-result">
				<xsl:with-param name="name" select="$questionName"/>
				<xsl:with-param name="expression" select="$filterCondition"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:apply-templates select="enolunatic:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:desc>Table / TableLoop drivers create a Table component</xd:desc>
	</xd:doc>
	<xsl:template match="Table | TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loopDepth" select="0" tunnel="yes"/>
		<xsl:param name="sequenceParent" tunnel="yes"/>
		<xsl:param name="subSequenceParent" tunnel="yes"/>

		<xsl:variable name="idQuestion" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context, enolunatic:get-global-filter($source-context))"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>
		<xsl:variable name="componentType">
			<xsl:choose>
				<xsl:when test="name(.) = 'TableLoop' and enolunatic:is-generating-loop($source-context)"><xsl:value-of select="'RosterForLoop'"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="'Table'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="mandatory" select="enolunatic:is-required($source-context)" as="xs:boolean"/>
		<xsl:variable name="nbMinimumLines" select="enolunatic:get-minimum-lines($source-context)"/>
		<xsl:variable name="nbMaximumLines" select="enolunatic:get-maximum-lines($source-context)"/>

		<components xsi:type="{$componentType}" componentType="{$componentType}" id="{$idQuestion}" positioning="HORIZONTAL" mandatory="{$mandatory}">
			<label><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context, $label)"/></label>
			<xsl:copy-of select="enolunatic:getInstructionForQuestion($source-context,.)"/>
			<conditionFilter><xsl:value-of select="$filterCondition"/></conditionFilter>
			<hierarchy>
				<xsl:copy-of select="$sequenceParent"/>
				<xsl:copy-of select="$subSequenceParent"/>
			</hierarchy>
			<xsl:copy-of select="$dependencies"/>
			<xsl:if test="$nbMinimumLines!='' and $nbMaximumLines!=''">
				<lines min="{$nbMinimumLines}" max="{$nbMaximumLines}"/>
			</xsl:if>

			<xsl:for-each select="enolunatic:get-header-lines($source-context)">
				<xsl:choose>
					<xsl:when test="$componentType = 'Table'">
						<cells type="header">
							<xsl:apply-templates select="enolunatic:get-header-line($source-context,position())" mode="source">
								<xsl:with-param name="lineType" select="'headerLine'" tunnel="yes"/>
								<xsl:with-param name="elementName" select="'cells'" tunnel="yes"/>
								<xsl:with-param name="idColumn" select="position()" tunnel="yes"/>
							</xsl:apply-templates>
						</cells>
					</xsl:when>
					<xsl:when test="$componentType = 'RosterForLoop'">
						<xsl:apply-templates select="enolunatic:get-header-line($source-context,position())" mode="source">
							<xsl:with-param name="lineType" select="'headerLine'" tunnel="yes"/>
							<xsl:with-param name="elementName" select="'headers'" tunnel="yes"/>
							<xsl:with-param name="idColumn" select="position()" tunnel="yes"/>
							<xsl:with-param name="loopDepth" select="$loopDepth + 1" tunnel="yes"/>
						</xsl:apply-templates>						
					</xsl:when>
				</xsl:choose>				
			</xsl:for-each>

			<xsl:for-each select="enolunatic:get-body-lines($source-context)">
				<xsl:choose>
					<xsl:when test="$componentType = 'Table'">
						<cells type="line">
							<xsl:apply-templates select="enolunatic:get-body-line($source-context,position())" mode="source">
								<xsl:with-param name="lineType" select="'bodyLine'" tunnel="yes"/>
								<xsl:with-param name="elementName" select="'cells'" tunnel="yes"/>
								<xsl:with-param name="position" select="position()" tunnel="yes"/>
								<xsl:with-param name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])" tunnel="yes"/>
								<xsl:with-param name="idQuestion" select="$idQuestion" tunnel="yes"/>
							</xsl:apply-templates>
						</cells>
					</xsl:when>
					<xsl:when test="$componentType = 'RosterForLoop'">
						<xsl:apply-templates select="enolunatic:get-body-line($source-context,position())" mode="source">
							<xsl:with-param name="lineType" select="'bodyLine'" tunnel="yes"/>
							<xsl:with-param name="elementName" select="'components'" tunnel="yes"/>
							<xsl:with-param name="position" select="position()" tunnel="yes"/>
							<xsl:with-param name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])" tunnel="yes"/>
							<xsl:with-param name="idQuestion" select="$idQuestion" tunnel="yes"/>
							<xsl:with-param name="loopDepth" select="$loopDepth + 1" tunnel="yes"/>
						</xsl:apply-templates>						
					</xsl:when>
				</xsl:choose>				
			</xsl:for-each>
		</components>
		
		<xsl:if test="$addFilterResult">
			<xsl:call-template name="enolunatic:add-calculated-variable-filter-result">
				<xsl:with-param name="name" select="$questionName"/>
				<xsl:with-param name="expression" select="$filterCondition"/>
			</xsl:call-template>
		</xsl:if>
		
		<xsl:apply-templates select="enolunatic:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
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
		<xsl:param name="lineType" tunnel="yes"/>
		<xsl:param name="elementName" tunnel="yes"/>

		<xsl:variable name="col-span" select="number(enolunatic:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enolunatic:get-rowspan($source-context))"/>
		<xsl:variable name="id" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>
		<xsl:element name="{$elementName}">
			<xsl:if test="$lineType='headerLine'">
				<xsl:attribute name="headerCell" select="true()"/>
			</xsl:if>
			<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
			<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
			<xsl:if test="$label!='' and $lineType='bodyLine'">
				<value><xsl:value-of select="enolunatic:get-value($source-context)"/></value>
			</xsl:if>
			<label><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></label>
		</xsl:element>
		<xsl:copy-of select="$dependencies"/>
	</xsl:template>


	<xsl:template match="FixedCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="idColumn" tunnel="yes"/>
		<xsl:param name="lineType" tunnel="yes"/>
		<xsl:param name="elementName" tunnel="yes"/>
		
		<xsl:variable name="col-span" select="number(enolunatic:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enolunatic:get-rowspan($source-context))"/>
		<xsl:variable name="id" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="value" select="enolunatic:get-cell-value($source-context)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>
		<xsl:element name="{$elementName}">
			<xsl:if test="$lineType='headerLine'">
				<xsl:attribute name="headerCell" select="true()"/>
			</xsl:if>
			<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
			<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
			<label>
				<xsl:choose>
					<xsl:when test="$label != '' and $value !=''">
						<xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,concat($label,' || &quot; &quot; || &quot;',$value,'&quot;'))"/>
					</xsl:when>
					<xsl:when test="$label != '' and $value = ''">
						<xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,concat('&quot;',$value,'&quot;'))"/>
					</xsl:otherwise>
				</xsl:choose>
			</label>
		</xsl:element>
		<xsl:copy-of select="$dependencies"/>
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
			<xsl:with-param name="col-span" select="number(enolunatic:get-colspan($source-context))" tunnel="yes"/>
			<xsl:with-param name="row-span" select="number(enolunatic:get-rowspan($source-context))" tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:desc>
			<xd:p>The EmptyCell driver creates an empty cell.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="EmptyCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="lineType" tunnel="yes"/>
		<xsl:param name="elementName" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="idColumn" tunnel="yes"/>

		<xsl:variable name="col-span" select="number(enolunatic:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enolunatic:get-rowspan($source-context))"/>

		<xsl:choose>
			<xsl:when test="$lineType='headerLine'">
				<xsl:element name="{$elementName}">
					<xsl:attribute name="headerCell" select="true()"/>
					<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
					<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
					<label/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="{$elementName}">
					<xsl:attribute name="headerCell" select="false()"/>
					<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
					<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>				
				</xsl:element>
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
		<xsl:param name="dependencies" tunnel="yes"/>
		<xsl:param name="loopDepth" select="0" tunnel="yes"/>
		<xsl:param name="idLoop" select="''" tunnel="yes"/>
		<xsl:param name="sequenceParent" tunnel="yes"/>
		<xsl:param name="subSequenceParent" tunnel="yes"/>

		<xsl:variable name="responseName" select="enolunatic:get-business-name($source-context)"/>
		<xsl:variable name="code-appearance" select="enolunatic:get-appearance($source-context)"/>
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
		<xsl:variable name="minimumResponse" select="enolunatic:get-minimum($source-context)"/>
		<xsl:variable name="maximumResponse" select="enolunatic:get-maximum($source-context)"/>
		<xsl:variable name="numberOfDecimals">
			<xsl:variable name="dec" select="enolunatic:get-number-of-decimals($source-context)"/>
			<xsl:choose>
				<xsl:when test="$dec!=''"><xsl:value-of select="$dec"/></xsl:when>
				<xsl:when test="$dec='' and $minimumResponse!='' and self::NumericDomain">0</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="unit" select="enolunatic:get-suffix($source-context,$languages[1])"/>
		<!-- TextDomain getters -->
		<xsl:variable name="lengthResponse" select="enolunatic:get-length($source-context)"/>
		<!-- DateTimeDomain getters -->
		<xsl:variable name="format" select="enolunatic:get-format($source-context)"/>

		<xsl:if test="$questionName!=''">
			<components xsi:type="{$componentType}" componentType="{$componentType}" id="{$idQuestion}">
				<xsl:if test="$lengthResponse!='' and (self::TextDomain or self::TextareaDomain)"><xsl:attribute name="maxLength" select="$lengthResponse"/></xsl:if>
				<xsl:attribute name="mandatory" select="enolunatic:is-required($source-context)"/>
				<xsl:if test="$minimumResponse!=''"><xsl:attribute name="min" select="$minimumResponse"/></xsl:if>
				<xsl:if test="$maximumResponse!=''"><xsl:attribute name="max" select="$maximumResponse"/></xsl:if>
				<xsl:if test="$numberOfDecimals!=''"><xsl:attribute name="decimals" select="$numberOfDecimals"/></xsl:if>
				<label><xsl:value-of select="$labelQuestion"/></label>

				<xsl:copy-of select="$declarations"/>
				<conditionFilter><xsl:value-of select="$filterCondition"/></conditionFilter>
				<hierarchy>
					<xsl:copy-of select="$sequenceParent"/>
					<xsl:copy-of select="$subSequenceParent"/>
				</hierarchy>
				
				<xsl:copy-of select="$dependencies"/>
				<xsl:call-template name="enolunatic:add-response-dependencies">
					<xsl:with-param name="responseName" select="$responseName"/>
				</xsl:call-template>
				
				<xsl:if test="$unit!=''">
					<unit><xsl:value-of select="$unit"/></unit>
				</xsl:if>
				<xsl:if test="$format != ''">
					<xsl:choose>
						<xsl:when test="self::DateTimeDomain">
							<dateFormat><xsl:value-of select="$format"/></dateFormat>
						</xsl:when>
						<xsl:otherwise>
							<format><xsl:value-of select="$format"/></format>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
					<xsl:with-param name="idQuestion" select="$idQuestion" tunnel="yes"/>
				</xsl:apply-templates>				
				<xsl:call-template name="enolunatic:add-response-to-components">
					<xsl:with-param name="responseName" select="$responseName"/>
				</xsl:call-template>
			</components>
			
			<xsl:if test="$addFilterResult">
				<xsl:call-template name="enolunatic:add-calculated-variable-filter-result">
					<xsl:with-param name="name" select="$questionName"/>
					<xsl:with-param name="expression" select="$filterCondition"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
		<xsl:call-template name="enolunatic:add-collected-variable-to-components">
			<xsl:with-param name="responseName" select="$responseName"/>
			<xsl:with-param name="componentRef" select="$idQuestion"/>
			<xsl:with-param name="loopDepth" select="$loopDepth"/>
			<xsl:with-param name="idLoop" select="$idLoop"/>
		</xsl:call-template>
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
		<xsl:param name="lineType" tunnel="yes"/>
		<xsl:param name="elementName" tunnel="yes"/>
		<xsl:param name="loopDepth" select="0" tunnel="yes"/>
		<xsl:param name="idLoop" select="''" tunnel="yes"/>

		<xsl:variable name="responseName" select="enolunatic:get-business-name($source-context)"/>
		<xsl:variable name="code-appearance" select="enolunatic:get-appearance($source-context)"/>
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
		<xsl:variable name="minimumResponse" select="enolunatic:get-minimum($source-context)"/>
		<xsl:variable name="maximumResponse" select="enolunatic:get-maximum($source-context)"/>
		<xsl:variable name="numberOfDecimals">
			<xsl:variable name="dec" select="enolunatic:get-number-of-decimals($source-context)"/>
			<xsl:choose>
				<xsl:when test="$dec!=''"><xsl:value-of select="$dec"/></xsl:when>
				<xsl:when test="$dec='' and $minimumResponse!=''">0</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="unit" select="enolunatic:get-suffix($source-context,$languages[1])"/>
		<!-- TextDomain getters -->
		<xsl:variable name="lengthResponse" select="enolunatic:get-length($source-context)"/>
		<!-- DateTimeDomain getters -->
		<xsl:variable name="dateFormat" select="enolunatic:get-format($source-context)"/>
		
		<xsl:element name="{$elementName}">
			<xsl:attribute name="id" select="enolunatic:get-name($source-context)"/>
			<xsl:attribute name="componentType" select="$componentType"/>
			<xsl:if test="$lengthResponse!='' and (self::TextDomain or self::TextareaDomain)"><xsl:attribute name="maxLength" select="$lengthResponse"/></xsl:if>
			<xsl:if test="$col-span &gt; 1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
			<xsl:if test="$row-span &gt; 1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
			<xsl:if test="$minimumResponse!=''"><xsl:attribute name="min" select="$minimumResponse"/></xsl:if>
			<xsl:if test="$maximumResponse!=''"><xsl:attribute name="max" select="$maximumResponse"/></xsl:if>
			<xsl:if test="$numberOfDecimals!=''"><xsl:attribute name="decimals" select="$numberOfDecimals"/></xsl:if>
			<xsl:call-template name="enolunatic:add-response-dependencies">
				<xsl:with-param name="responseName" select="$responseName"/>
			</xsl:call-template>
			
			<xsl:if test="$unit!=''">
				<unit><xsl:value-of select="$unit"/></unit>
			</xsl:if>
			<xsl:if test="$dateFormat != ''">
				<dateFormat><xsl:value-of select="$dateFormat"/></dateFormat>
			</xsl:if>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
			<xsl:call-template name="enolunatic:add-response-to-components">
				<xsl:with-param name="responseName" select="$responseName"/>
			</xsl:call-template>
		</xsl:element>
		<xsl:call-template name="enolunatic:add-collected-variable-to-components">
			<xsl:with-param name="responseName" select="$responseName"/>
			<xsl:with-param name="componentRef" select="$idQuestion"/>
			<xsl:with-param name="loopDepth" select="$loopDepth"/>
			<xsl:with-param name="idLoop" select="$idLoop"/>
		</xsl:call-template>
	</xsl:template>

	<xd:doc>
		<xd:desc>
			<xd:p>The Response of MultipleChoiceQuestion creates a response element of the CheckboxGroup.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="MultipleChoiceQuestion//BooleanDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="loopDepth" select="0" tunnel="yes"/>
		<xsl:param name="idLoop" select="''" tunnel="yes"/>

		<xsl:variable name="responseName" select="enolunatic:get-business-name($source-context)"/>

		<responses id="{enolunatic:get-name($source-context)}">
			<!-- call item driver for the label -->
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
			<xsl:call-template name="enolunatic:add-response-to-components">
				<xsl:with-param name="responseName" select="$responseName"/>
			</xsl:call-template>
		</responses>
		<xsl:call-template name="enolunatic:add-response-dependencies">
			<xsl:with-param name="responseName" select="$responseName"/>
		</xsl:call-template>
		
		<xsl:call-template name="enolunatic:add-collected-variable-to-components">
			<xsl:with-param name="responseName" select="$responseName"/>
			<xsl:with-param name="componentRef" select="$idQuestion"/>
			<xsl:with-param name="loopDepth" select="$loopDepth"/>
			<xsl:with-param name="idLoop" select="$idLoop"/>
		</xsl:call-template>
	</xsl:template>

	<xd:doc>
		<xd:desc>
			<xd:p>Each value of a CodeDomain response creates an option.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="CodeDomain//xf-item" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>

		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context, $languages[1])"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>
		<xsl:if test="$label !=''">
			<options>
				<value><xsl:value-of select="enolunatic:get-value($source-context)"/></value>
				<label><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></label>
			</options>
			<xsl:copy-of select="$dependencies"/>
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

		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context, $languages[1])"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>

		<xsl:if test="$label !=''">
			<label><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></label>
			<xsl:copy-of select="$dependencies"/>
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
			<xsl:variable name="format" select="upper-case(enolunatic:get-format($source-context))"/>
			<xsl:choose>
				<xsl:when test="$format!=''"><xsl:value-of select="normalize-space($format)"/></xsl:when>
				<!-- Default value : COMMENT -->
				<xsl:otherwise><xsl:value-of select="'COMMENT'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="instructionLabel" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="instructionFormatMaj" select="concat(upper-case(substring($instructionFormat,1,1)),
			substring($instructionFormat,2))" as="xs:string"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($instructionLabel)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>

		<xsl:if test="$positionDeclaration!=''">
			<declarations declarationType="{$instructionFormat}" id="{enolunatic:get-name($source-context)}" position="{$positionDeclaration}">
				<label><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$instructionLabel)"/></label>
			</declarations>
			<xsl:copy-of select="$dependencies"/>
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
		<xsl:variable name="nameOutVariable" select="enolunatic:get-business-name($source-context)"/>

		<variables variableType="CALCULATED" xsi:type="VariableType">
			<name>
				<xsl:value-of select="$nameOutVariable"/>
			</name>
			<expression>
				<xsl:value-of select="normalize-space(
					enolunatic:replace-all-variables-with-business-name($source-context,
					enolunatic:replace-variable-with-collected-and-external-variables-formula($source-context,$nameOutVariable)))"/>
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
		<xsl:param name="sequenceParent" tunnel="yes"/>
		<xsl:param name="subSequenceParent" tunnel="yes"/>

		<xsl:variable name="componentType" select="'FilterDescription'"/>
		<xsl:variable name="idGoTo" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="filterCondition" select="enolunatic:get-global-filter($source-context)"/>

		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>

		<components xsi:type="{$componentType}" componentType="{$componentType}" id="{$idGoTo}" filterDescription="{$filterDescription}">
			<label><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></label>
			<conditionFilter><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$filterCondition)"/></conditionFilter>
			<hierarchy>
				<xsl:copy-of select="$sequenceParent"/>
				<xsl:copy-of select="$subSequenceParent"/>
			</hierarchy>
			<xsl:copy-of select="$dependencies"/>
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

		<xsl:variable name="nameOfControl" select="enolunatic:get-check-name($source-context,$languages[1])"/>
		<xsl:variable name="control" select="enolunatic:get-constraint($source-context)"/>
		<xsl:variable name="final-control">
			<xsl:call-template name="enolunatic:replace-variables-in-formula">
				<xsl:with-param name="source-context" select="$source-context"/>
				<xsl:with-param name="formula" select="$control"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="instructionFormat" select="enolunatic:get-css-class($source-context)"/>
		<xsl:variable name="instructionLabel" select="enolunatic:get-vtl-label($source-context, $languages[1])"/>
		<xsl:variable name="alertLevel" select="enolunatic:get-alert-level($source-context)"/>

		<xsl:variable name="controlDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($final-control)"/>
		<xsl:variable name="instructionDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($instructionLabel)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$controlDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
			<xsl:for-each select="$instructionDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>

		<control>
			<xsl:if test="$alertLevel != ''">
				<xsl:attribute name="level" select="$alertLevel"/>
			</xsl:if>
			<xsl:if test="$control!=''">
				<title><xsl:value-of select="concat(upper-case($alertLevel),' control : ',$nameOfControl)"/></title>
				<value>
					<xsl:value-of select="normalize-space(enolunatic:replace-all-variables-with-business-name($source-context,$final-control))"/>
				</value>
			</xsl:if>

			<xsl:if test="$instructionLabel!=''">
				<instruction><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$instructionLabel)"/></instruction>
			</xsl:if>
			<xsl:copy-of select="$dependencies"/>

			<!-- Go to the Calculated Variable -->
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</control>
	</xsl:template>

	<xd:doc>
		<xd:desc>
			<xd:p>Function named: enolunatic:printQuestionTitleWithInstruction.</xd:p>
			<xd:p>It prints the instructions of a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:function name="enolunatic:getInstructionForQuestion">
		<xsl:param name="context" as="item()"/>
		<xsl:param name="driver"/>
		<xsl:apply-templates select="enolunatic:get-before-question-title-instructions($context)" mode="source">
			<xsl:with-param name="driver" select="$driver"/>
			<xsl:with-param name="positionDeclaration" select="'BEFORE_QUESTION_TEXT'" tunnel="yes"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="enolunatic:get-after-question-title-instructions($context)" mode="source">
			<xsl:with-param name="driver" select="$driver"/>
			<xsl:with-param name="positionDeclaration" select="'AFTER_QUESTION_TEXT'" tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:function>

	<xd:doc>
		<xd:desc>
			<xd:p>Named template: enolunatic:add-response-to-components.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template name="enolunatic:add-response-to-components">
		<xsl:param name="responseName"/>
		<response name="{$responseName}"/>
	</xsl:template>
	
	<xsl:template name="enolunatic:add-response-dependencies">
		<xsl:param name="responseName"/>
		<responseDependencies><xsl:value-of select="$responseName"/></responseDependencies>
	</xsl:template>

	<xd:doc>
		<xd:desc>
			<xd:p>Named template: enolunatic:add-collected-variable-to-components.</xd:p>
			<xd:p>It creates the variables with its different possible states.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template name="enolunatic:add-collected-variable-to-components">
		<xsl:param name="responseName"/>
		<xsl:param name="componentRef"/>
		<xsl:param name="loopDepth" select="0"/>
		<xsl:param name="idLoop" select="''"/>
		<xsl:variable name="ResponseTypeEnum" select="'PREVIOUS,COLLECTED,FORCED,EDITED,INPUTED'" as="xs:string"/>
		<xsl:variable name="variableType">
			<xsl:choose>
				<xsl:when test="$loopDepth &gt; 0">
					<xsl:value-of select="'VariableTypeArray'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'VariableType'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="newComponentRef">
			<xsl:choose>
				<xsl:when test="$idLoop!=''"><xsl:value-of select="$idLoop"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$componentRef"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<variables variableType="COLLECTED" xsi:type="{$variableType}">
			<name><xsl:value-of select="$responseName"/></name>
			<componentRef><xsl:value-of select="$newComponentRef"/></componentRef>
			<values>
				<xsl:for-each select="tokenize($ResponseTypeEnum,',')">
					<xsl:call-template name="enolunatic:add-collected-value">
						<xsl:with-param name="valueType" select="."/>
						<xsl:with-param name="depth" select="$loopDepth"/>
					</xsl:call-template>
				</xsl:for-each>
			</values>
		</variables>
	</xsl:template>

	<xd:doc>
		<xd:desc>
			<xd:p>Named function: enolunatic:add-dependencies.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:function name="enolunatic:add-dependencies">
		<xsl:param name="dependencies" as="xs:string*"/>
		<xsl:for-each select="distinct-values($dependencies)">
			<dependencies><xsl:value-of select="enolunatic:get-variable-business-name(.)"/></dependencies>
		</xsl:for-each>
	</xsl:function>

	<xsl:template name="enolunatic:add-collected-value">
		<xsl:param name="valueType"/>
		<xsl:param name="depth"/>
		
		<xsl:choose>
			<xsl:when test="$depth&gt;0">
				<xsl:element name="{$valueType}">
					<xsl:attribute name="xsi:type" select="concat($valueType,'Array')"/>
					<xsl:call-template name="enolunatic:add-collected-value">
						<xsl:with-param name="valueType" select="$valueType"/>
						<xsl:with-param name="depth" select="$depth - 1"/>
					</xsl:call-template>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="{$valueType}">
					<xsl:attribute name="xsi:nil" select="true()"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="enolunatic:add-calculated-variable-filter-result">
		<xsl:param name="name"/>
		<xsl:param name="expression"/>
		<variables variableType="CALCULATED" xsi:type="VariableType">
			<name><xsl:value-of select="concat('FILTER_RESULT_',$name)"/></name>
			<expression><xsl:value-of select="$expression"/></expression>
		</variables>
	</xsl:template>
	
</xsl:stylesheet>