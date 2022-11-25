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
		<xsl:variable name="labelType" select="enolunatic:get-label-type('label')"/>
		<Questionnaire id="{$id}" modele="{enolunatic:get-form-model($source-context)}" enoCoreVersion="{$enoVersion}" missing="{$missingVar}">
			<label>
				<value><xsl:value-of select="$label"/></value>
				<type><xsl:value-of select="$labelType"/></type>
			</label>
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
		<xsl:param name="shouldHaveMissingVars" tunnel="yes"/>
		<xsl:param name="sequenceParent" tunnel="yes"/>
		<xsl:variable name="componentType" select="'Loop'"/>
		<xsl:variable name="isGeneratedLoop" select="enolunatic:is-linked-loop($source-context)" as="xs:boolean"/>
		<xsl:variable name="isGeneratingLoop" select="enolunatic:is-generating-loop($source-context)" as="xs:boolean"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="labelType" select="enolunatic:get-label-type('label')"/>
		<xsl:variable name="firstDescendantResponse" select="enolunatic:get-loop-first-descendant-question($source-context,$languages[1])"/>
		
		<xsl:variable name="id" select="enolunatic:get-name($source-context)"/>
		<!-- keep idLoop of the parent Loop if exists -->
		<xsl:variable name="newIdLoop" select="if($idLoop!='') then $idLoop else $id"/>
		<xsl:variable name="newLoopDepth" select="$loopDepth + 1"/>		
		<xsl:variable name="newShouldHaveMissingVars" select="if(string($shouldHaveMissingVars)!='') then $shouldHaveMissingVars else not($isGeneratingLoop)"/>
		
		<xsl:variable name="filter" select="enolunatic:get-global-filter($source-context)"/>
		<xsl:variable name="filterDependencies" select="enolunatic:find-variables-in-formula($filter)"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context,$filter)"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="minimumOccurrences" select="enolunatic:get-minimum-occurrences($source-context)"/>
		<xsl:variable name="maximumOccurrences" select="enolunatic:get-maximum-occurrences($source-context)"/>
		<xsl:variable name="minDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($minimumOccurrences)"/>
		<xsl:variable name="maxDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($maximumOccurrences)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
			<xsl:for-each select="$minDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
			<xsl:for-each select="$maxDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
			<xsl:if test="not($newShouldHaveMissingVars) and $missingVar">
				<xsl:value-of select="concat($firstDescendantResponse,'_MISSING')"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>
		
		
		<components xsi:type="{$componentType}" componentType="{$componentType}" id="{$id}">
			<xsl:attribute name="depth" select="$newLoopDepth"/>
			<xsl:choose>
				<xsl:when test="$isGeneratedLoop">
					<xsl:if test="$maximumOccurrences!=''">
						<iterations>
							<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$maximumOccurrences)"/></value>
							<type><xsl:value-of select="enolunatic:get-label-type('iterations')"/></type>
						</iterations>
					</xsl:if>
					<idGenerator><xsl:value-of select="enolunatic:get-loop-generator-id($source-context)"/></idGenerator>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="$minimumOccurrences!=$maximumOccurrences and $label!=''">
						<label>
							<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></value>
							<type><xsl:value-of select="$labelType"/></type>
						</label>
					</xsl:if>
					<lines>
						<min>
							<value><xsl:value-of select="if ($minimumOccurrences!='') then enolunatic:replace-all-variables-with-business-name($source-context,$minimumOccurrences) else 0"  /></value>
							<type><xsl:value-of select="enolunatic:get-label-type('lines.min')"/></type>
						</min>
						<max>
							<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$maximumOccurrences)"/></value>
							<type><xsl:value-of select="enolunatic:get-label-type('lines.max')"/></type>
						</max>
					</lines>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="not($newShouldHaveMissingVars) and $missingVar">
				<missingResponse>
					<xsl:attribute name="name" select="concat($firstDescendantResponse,'_MISSING')"/>
				</missingResponse>
			</xsl:if>
			<xsl:if test="$minimumOccurrences!=$maximumOccurrences and $label!=''">
				<label>
					<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></value>
					<type><xsl:value-of select="$labelType"/></type>
				</label>
			</xsl:if>
			<xsl:copy-of select="enolunatic:add-condition-filter($filterCondition,$filterDependencies)"/>
			<xsl:if test="$sequenceParent">
				<hierarchy>
					<xsl:copy-of select="$sequenceParent"/>
				</hierarchy>
			</xsl:if>			
			<xsl:copy-of select="$dependencies"/>
			
			<!-- In the case of not shouldHaveMissingVars, it means it is a generating loop
				Thus I want to generate a simple missing collected variable, based on the name of the first descendant response of the loop
				which should be the variable used as the iterator for linked loops
				loopDepth is not passed on, as it should be 0 to instantiate a single null instead of an array with null-->
			<xsl:if test="not($newShouldHaveMissingVars) and $missingVar">
				<xsl:call-template name="enolunatic:add-collected-variable-to-components">
					<xsl:with-param name="responseName" select="concat($firstDescendantResponse,'_MISSING')"/>
					<xsl:with-param name="idLoop" select="$newIdLoop"/>
				</xsl:call-template>
			</xsl:if>
			
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="loopDepth" select="$newLoopDepth" tunnel="yes"/>
				<xsl:with-param name="idLoop" select="$newIdLoop" tunnel="yes"/>
				<xsl:with-param name="shouldHaveMissingVars" select="$newShouldHaveMissingVars" as="xs:boolean" tunnel="yes"/>
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
		<xsl:variable name="labelType" select="enolunatic:get-label-type('label')"/>
		<xsl:variable name="filter" select="enolunatic:get-global-filter($source-context)"/>
		<xsl:variable name="filterDependencies" select="enolunatic:find-variables-in-formula($filter)"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context, $filter)"/>
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
						<label>
							<value><xsl:value-of select="$finalLabel"/></value>
							<type><xsl:value-of select="enolunatic:get-label-type('hierarchy.sequence.label')"/></type>
						</label>
					</sequence>
				</xsl:when>
				<xsl:when test="self::SubModule"><xsl:copy-of select="$sequenceParent"/></xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="subSequence">
			<xsl:choose>
				<xsl:when test="self::SubModule">
					<subSequence id="{$id}">
						<label>
							<value><xsl:value-of select="$finalLabel"/></value>
							<type><xsl:value-of select="enolunatic:get-label-type('hierarchy.subSequence.label')"/></type>
						</label>
					</subSequence>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<components xsi:type="{$componentType-Sequence}" componentType="{$componentType-Sequence}" id="{$id}">
			<label>
				<value><xsl:value-of select="$finalLabel"/></value>
				<type><xsl:value-of select="$labelType"/></type>
			</label>
			<xsl:copy-of select="enolunatic:getInstructionForQuestion($source-context,.)"/>
			<xsl:copy-of select="enolunatic:add-condition-filter($filterCondition,$filterDependencies)"/>
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
		<xd:desc>SingleResponseQuestion and MultipleQuestion drivers do not create a component : it is created by its response</xd:desc>
	</xd:doc>
	<xsl:template match="SingleResponseQuestion | MultipleQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="shouldHaveMissingVars" select="true()" tunnel="yes"/>

		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="filter" select="enolunatic:get-global-filter($source-context)"/>
		<xsl:variable name="filterDependencies" select="enolunatic:find-variables-in-formula($filter)"/>
		<xsl:variable name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])"/>
		<xsl:variable name="missingResponseName" select="concat($questionName,'_MISSING')"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context, $filter)"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
			<xsl:if test="$shouldHaveMissingVars and $missingVar">	
				<xsl:value-of select="$missingResponseName"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>

		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
			<xsl:with-param name="idQuestion" select="enolunatic:get-name($source-context)" tunnel="yes"/>
			<xsl:with-param name="questionName" select="$questionName" tunnel="yes"/>
			<xsl:with-param name="missingResponseName" select="$missingResponseName" tunnel="yes"/>
			<xsl:with-param name="labelQuestion" select="enolunatic:replace-all-variables-with-business-name($source-context, $label)" tunnel="yes"/>
			<xsl:with-param name="typeOfQuestion" select="self::*/name()" tunnel="yes"/>
			<xsl:with-param name="declarations" select="enolunatic:getInstructionForQuestion($source-context,.)" as="node()*" tunnel="yes"/>
			<xsl:with-param name="filterCondition" select="$filterCondition" tunnel="yes"/>
			<xsl:with-param name="filterConditionDependencies" select="$filterDependencies" as="xs:string*" tunnel="yes"/>
			<xsl:with-param name="dependencies" select="$dependencies" tunnel="yes"/>
		</xsl:apply-templates>

		<xsl:apply-templates select="enolunatic:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>PairwiseQuestion driver creates a component which is likely a loop of loop, with the same axises : for example, the link between people of the same household</xd:desc>
	</xd:doc>
	<xsl:template match="PairwiseQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="sequenceParent" tunnel="yes"/>
		<xsl:param name="subSequenceParent" tunnel="yes"/>
		<xsl:param name="loopDepth" select="0" tunnel="yes"/>
		<xsl:param name="idLoop" select="''" tunnel="yes"/>
		
		<xsl:variable name="mandatory" select="enolunatic:is-required($source-context)" as="xs:boolean"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="filter" select="enolunatic:get-global-filter($source-context)"/>
		<xsl:variable name="filterDependencies" select="enolunatic:find-variables-in-formula($filter)"/>
		<xsl:variable name="idQuestion" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])"/>
		<xsl:variable name="missingResponseName" select="concat($questionName,'_MISSING')"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context, $filter)"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="pairwiseScope" select="enolunatic:get-variable-business-name(enolunatic:get-pairwise-scope($source-context))"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>
		
		<components xsi:type="PairwiseLinks" componentType="PairwiseLinks" id="{$idQuestion}" mandatory="{$mandatory}">
			<xsl:copy-of select="enolunatic:add-condition-filter($filterCondition,$filterDependencies)"/>
			<hierarchy>
				<xsl:copy-of select="$sequenceParent"/>
				<xsl:copy-of select="$subSequenceParent"/>
			</hierarchy>
			<xAxisIterations>
				<value>count(<xsl:value-of select="$pairwiseScope"/>)</value>
				<type>VTL|MD</type>
			</xAxisIterations>
			<yAxisIterations>
				<value>count(<xsl:value-of select="$pairwiseScope"/>)</value>
				<type>VTL|MD</type>
			</yAxisIterations>
			<symLinks>
				<LINKS>
					<LINK>
						<source>1</source>
						<target>1</target>
					</LINK>
					<LINK>
						<source>2</source>
						<target>3</target>
					</LINK>
					<LINK>
						<source>3</source>
						<target>2</target>
					</LINK>
					<LINK>
						<source>4</source>
						<target>4</target>
					</LINK>
					<LINK>
						<source>5</source>
						<target>6</target>
					</LINK>
					<LINK>
						<source>6</source>
						<target>5</target>
					</LINK>
					<LINK>
						<source>7</source>
						<target>8</target>
					</LINK>
					<LINK>
						<source>8</source>
						<target>7</target>
					</LINK>
					<LINK>
						<source>9</source>
						<target>9</target>
					</LINK>
					<LINK>
						<source>10</source>
						<target>12</target>
					</LINK>
					<LINK>
						<source>11</source>
						<target>13</target>
					</LINK>
					<LINK>
						<source>12</source>
						<target>10</target>
					</LINK>
					<LINK>
						<source>13</source>
						<target>11</target>
					</LINK>
					<LINK>
						<source>14</source>
						<target>null</target>
					</LINK>
					<LINK>
						<source>15</source>
						<target>null</target>
					</LINK>
					<LINK>
						<source>16</source>
						<target>16</target>
					</LINK>
					<LINK>
						<source>17</source>
						<target>null</target>
					</LINK>
				</LINKS>
			</symLinks>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="idQuestion" select="concat($idQuestion,'-pairwise-dropdown')" tunnel="yes"/>
				<xsl:with-param name="questionName" select="$questionName" tunnel="yes"/>
				<xsl:with-param name="missingResponseName" select="$missingResponseName" tunnel="yes"/>
				<xsl:with-param name="labelQuestion" select="enolunatic:replace-all-variables-with-business-name($source-context, $label)" tunnel="yes"/>
				<xsl:with-param name="typeOfQuestion" select="'Dropdown'" tunnel="yes"/>
				<xsl:with-param name="declarations" select="enolunatic:getInstructionForQuestion($source-context,.)" as="node()*" tunnel="yes"/>
				<xsl:with-param name="filterCondition" select="$filterCondition" tunnel="yes"/>
				<xsl:with-param name="filterConditionDependencies" select="$filterDependencies" as="xs:string*" tunnel="yes"/>
				<xsl:with-param name="dependencies" select="$dependencies" tunnel="yes"/>
			</xsl:apply-templates>
		</components>
		<xsl:for-each select="('xAxis','yAxis')">
			<variables variableType="CALCULATED" xsi:type="VariableType">
				<name><xsl:value-of select="."/></name>
				<expression>
					<value><xsl:value-of select="$pairwiseScope"/></value>
					<type>VTL</type>
				</expression>
				<bindingDependencies><xsl:value-of select="$pairwiseScope"/></bindingDependencies>
				<shapeFrom><xsl:value-of select="$pairwiseScope"/></shapeFrom>
				<inFilter>true</inFilter>
			</variables>
		</xsl:for-each>
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
		<xsl:param name="loopDepth" select="0" tunnel="yes"/>
		<xsl:param name="idLoop" select="''" tunnel="yes"/>
		<xsl:param name="shouldHaveMissingVars" select="true()" tunnel="yes"/>
		
		<xsl:variable name="filter" select="enolunatic:get-global-filter($source-context)"/>
		<xsl:variable name="filterDependencies" select="enolunatic:find-variables-in-formula($filter)"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context,$filter)"/>

		<xsl:variable name="idQuestion" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])"/>
		<xsl:variable name="missingResponseName" select="concat($questionName,'_MISSING')"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="labelType" select="enolunatic:get-label-type('label')"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
			<xsl:if test="$shouldHaveMissingVars and $missingVar">
				<xsl:value-of select="$missingResponseName"/>
			</xsl:if>
		</xsl:variable>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>
		

		<components xsi:type="CheckboxGroup" componentType="CheckboxGroup" id="{$idQuestion}">
			<label>
				<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context, $label)"/></value>
				<type><xsl:value-of select="$labelType"/></type>
			</label>
			<xsl:copy-of select="enolunatic:getInstructionForQuestion($source-context,.)"/>
			<xsl:copy-of select="enolunatic:add-condition-filter($filterCondition,$filterDependencies)"/>
			<hierarchy>
				<xsl:copy-of select="$sequenceParent"/>
				<xsl:copy-of select="$subSequenceParent"/>
			</hierarchy>
			<xsl:if test="$shouldHaveMissingVars and $missingVar">
				<missingResponse>
					<xsl:attribute name="name" select="$missingResponseName"/>
				</missingResponse>	
			</xsl:if>
			<xsl:copy-of select="$dependencies"/>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="idQuestion" select="$idQuestion" tunnel="yes"/>
				<xsl:with-param name="questionName" select="$questionName" tunnel="yes"/>
				<xsl:with-param name="labelQuestion" select="enolunatic:replace-all-variables-with-business-name($source-context, $label)" tunnel="yes"/>
				<xsl:with-param name="typeOfQuestion" select="self::*/name()" tunnel="yes"/>
				<xsl:with-param name="declarations" select="enolunatic:getInstructionForQuestion($source-context,.)" as="node()*" tunnel="yes"/>
				<xsl:with-param name="filterCondition" select="$filterCondition" tunnel="yes"/>
			</xsl:apply-templates>
		</components>
		
		<xsl:if test="$addFilterResult">
			<xsl:call-template name="enolunatic:add-calculated-variable-filter-result">
				<xsl:with-param name="name" select="$questionName"/>
				<xsl:with-param name="expression" select="$filterCondition"/>
				<xsl:with-param name="dependencies" select="$filterDependencies" as="xs:string*"/>
				<xsl:with-param name="idLoop" select="$idLoop"/>
				<xsl:with-param name="languages" select="$languages"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$shouldHaveMissingVars and $missingVar">
			<xsl:call-template name="enolunatic:add-collected-variable-to-components">
				<xsl:with-param name="responseName" select="concat($questionName,'_MISSING')"/>
				<xsl:with-param name="componentRef" select="$idQuestion"/>
				<xsl:with-param name="loopDepth" select="$loopDepth"/>
				<xsl:with-param name="idLoop" select="$idLoop"/>
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
		<xsl:param name="idLoop" select="''" tunnel="yes"/>
		<xsl:param name="shouldHaveMissingVars" select="true()" tunnel="yes"/>
		<xsl:param name="sequenceParent" tunnel="yes"/>
		<xsl:param name="subSequenceParent" tunnel="yes"/>

		<xsl:variable name="idQuestion" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])"/>
		<xsl:variable name="missingResponseName" select="concat($questionName,'_MISSING')"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="labelType" select="enolunatic:get-label-type('label')"/>
		<xsl:variable name="filter" select="enolunatic:get-global-filter($source-context)"/>
		<xsl:variable name="filterDependencies" select="enolunatic:find-variables-in-formula($filter)"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context,$filter)"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependenciesVariables" as="xs:string*">
			<xsl:for-each select="$labelDependencies">
				<xsl:sequence select="."/>
			</xsl:for-each>
			<xsl:if test="$shouldHaveMissingVars and $missingVar">
				<xsl:value-of select="$missingResponseName"/>				
			</xsl:if>
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
			<label>
				<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context, $label)"/></value>
				<type><xsl:value-of select="$labelType"/></type>
			</label>
			<xsl:copy-of select="enolunatic:getInstructionForQuestion($source-context,.)"/>
			<xsl:copy-of select="enolunatic:add-condition-filter($filterCondition,$filterDependencies)"/>
			<hierarchy>
				<xsl:copy-of select="$sequenceParent"/>
				<xsl:copy-of select="$subSequenceParent"/>
			</hierarchy>
			<xsl:if test="$shouldHaveMissingVars and $missingVar">
				<missingResponse>
					<xsl:attribute name="name" select="$missingResponseName"/>
				</missingResponse>
			</xsl:if>
			<xsl:copy-of select="$dependencies"/>
			<xsl:if test="$nbMinimumLines!='' and $nbMaximumLines!=''">
				<lines>
					<min>
						<value><xsl:value-of select="$nbMinimumLines"/></value>
						<type><xsl:value-of select="enolunatic:get-label-type('lines.min')"/></type>
					</min>
					<max>
						<value><xsl:value-of select="$nbMaximumLines"/></value>
						<type><xsl:value-of select="enolunatic:get-label-type('lines.max')"/></type>
					</max>
				</lines>
			</xsl:if>
			<xsl:if test="$shouldHaveMissingVars and $missingVar">
				<xsl:call-template name="enolunatic:add-collected-variable-to-components">
					<xsl:with-param name="responseName" select="concat($questionName,'_MISSING')"/>
					<xsl:with-param name="componentRef" select="$idQuestion"/>
					<xsl:with-param name="loopDepth" select="$loopDepth"/>
					<xsl:with-param name="idLoop" select="$idLoop"/>
				</xsl:call-template>
			</xsl:if>

			<xsl:for-each select="enolunatic:get-header-lines($source-context)">
				<xsl:choose>
					<xsl:when test="$componentType = 'Table'">
						<xsl:apply-templates select="enolunatic:get-header-line($source-context,position())" mode="source">
							<xsl:with-param name="elementName" select="'header'" tunnel="yes"/>
							<xsl:with-param name="idColumn" select="position()" tunnel="yes"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:when test="$componentType = 'RosterForLoop'">
						<xsl:apply-templates select="enolunatic:get-header-line($source-context,position())" mode="source">
							<xsl:with-param name="elementName" select="'header'" tunnel="yes"/>
							<xsl:with-param name="idColumn" select="position()" tunnel="yes"/>
							<xsl:with-param name="loopDepth" select="$loopDepth + 1" tunnel="yes"/>
						</xsl:apply-templates>						
					</xsl:when>
				</xsl:choose>				
			</xsl:for-each>

			<xsl:for-each select="enolunatic:get-body-lines($source-context)">
				<xsl:choose>
					<xsl:when test="$componentType = 'Table'">
						<body>
							<xsl:apply-templates select="enolunatic:get-body-line($source-context,position())" mode="source">
								<xsl:with-param name="elementName" select="'bodyLine'" tunnel="yes"/>
								<xsl:with-param name="position" select="position()" tunnel="yes"/>
								<xsl:with-param name="questionName" select="enolunatic:get-question-name($source-context,$languages[1])" tunnel="yes"/>
								<xsl:with-param name="idQuestion" select="$idQuestion" tunnel="yes"/>
							</xsl:apply-templates>
						</body>
					</xsl:when>
					<xsl:when test="$componentType = 'RosterForLoop'">
						<xsl:apply-templates select="enolunatic:get-body-line($source-context,position())" mode="source">
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
				<xsl:with-param name="dependencies" select="$filterDependencies" as="xs:string*"/>
				<xsl:with-param name="idLoop" select="$idLoop"/>
				<xsl:with-param name="languages" select="$languages"/>
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
		<xsl:param name="elementName" tunnel="yes"/>

		<xsl:variable name="col-span" select="number(enolunatic:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enolunatic:get-rowspan($source-context))"/>
		<xsl:variable name="id" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="labelType" select="enolunatic:get-label-type('label')"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>
		<xsl:element name="{$elementName}">
			<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
			<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
			<xsl:if test="$label!='' and $elementName!='header'">
				<value><xsl:value-of select="enolunatic:get-value($source-context)"/></value>
			</xsl:if>
			<label>
				<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></value>
				<type><xsl:value-of select="$labelType"/></type>
			</label>
		</xsl:element>
		<xsl:copy-of select="$dependencies"/>
	</xsl:template>


	<xsl:template match="FixedCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="idColumn" tunnel="yes"/>
		<xsl:param name="elementName" tunnel="yes"/>
		
		<xsl:variable name="col-span" select="number(enolunatic:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enolunatic:get-rowspan($source-context))"/>
		<xsl:variable name="id" select="enolunatic:get-name($source-context)"/>
		<xsl:variable name="label" select="enolunatic:get-vtl-label($source-context,$languages[1])"/>
		<xsl:variable name="labelType" select="enolunatic:get-label-type('label')"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="value" select="enolunatic:get-cell-value($source-context)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>
		<xsl:element name="{$elementName}">
			<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
			<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
			<label>
				<xsl:choose>
					<xsl:when test="$label != '' and $value !=''">
						<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,concat($label,' || &quot; &quot; || &quot;',$value,'&quot;'))"/></value>
						<type><xsl:value-of select="$labelType"/></type>
					</xsl:when>
					<xsl:when test="$label != '' and $value = ''">
						<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></value>
						<type><xsl:value-of select="$labelType"/></type>
					</xsl:when>
					<xsl:otherwise>
						<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,concat('&quot;',$value,'&quot;'))"/></value>
						<type><xsl:value-of select="$labelType"/></type>
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
		<xsl:param name="elementName" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="idColumn" tunnel="yes"/>

		<xsl:variable name="col-span" select="number(enolunatic:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enolunatic:get-rowspan($source-context))"/>

		<xsl:choose>
			<xsl:when test="$elementName='header'">
				<xsl:element name="{$elementName}">
					<xsl:if test="$col-span&gt;1"><xsl:attribute name="colspan" select="$col-span"/></xsl:if>
					<xsl:if test="$row-span&gt;1"><xsl:attribute name="rowspan" select="$row-span"/></xsl:if>
					<label/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="{$elementName}">
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
	<xsl:template match="*[name(.) =('SingleResponseQuestion','MultipleQuestion','PairwiseQuestion')]//*[name(.) =('NumericDomain','TextDomain','TextareaDomain','DateTimeDomain','CodeDomain','BooleanDomain')]" mode="model" priority="1">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="idQuestion" tunnel="yes"/>
		<xsl:param name="questionName" tunnel="yes"/>
		<xsl:param name="missingResponseName" tunnel="yes"/>
		<xsl:param name="labelQuestion" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="declarations" as="node()*" tunnel="yes"/>
		<xsl:param name="filterCondition" tunnel="yes"/>
		<xsl:param name="filterConditionDependencies" as="xs:string*" tunnel="yes"/>
		<xsl:param name="dependencies" tunnel="yes"/>
		<xsl:param name="loopDepth" select="0" tunnel="yes"/>
		<xsl:param name="idLoop" select="''" tunnel="yes"/>
		<xsl:param name="shouldHaveMissingVars" select="true()" tunnel="yes"/>
		<xsl:param name="sequenceParent" tunnel="yes"/>
		<xsl:param name="subSequenceParent" tunnel="yes"/>

		<xsl:variable name="labelType" select="enolunatic:get-label-type('label')"/>
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
			<xsl:variable name="dec" select="string(enolunatic:get-number-of-decimals($source-context))"/>
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
				<label>
					<value><xsl:value-of select="$labelQuestion"/></value>
					<type><xsl:value-of select="$labelType"/></type>
				</label>

				<xsl:copy-of select="$declarations"/>
				<xsl:copy-of select="enolunatic:add-condition-filter($filterCondition,$filterConditionDependencies)"/>
				<xsl:if test="not(ancestor::PairwiseQuestion)">
					<hierarchy>
						<xsl:copy-of select="$sequenceParent"/>
						<xsl:copy-of select="$subSequenceParent"/>
					</hierarchy>
				</xsl:if>
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
				<xsl:if test="$shouldHaveMissingVars and $missingVar">
					<missingResponse>
						<xsl:attribute name="name" select="$missingResponseName"/>
					</missingResponse>
				</xsl:if>


			</components>
			
			<xsl:if test="$addFilterResult">
				<xsl:call-template name="enolunatic:add-calculated-variable-filter-result">
					<xsl:with-param name="name" select="$questionName"/>
					<xsl:with-param name="expression" select="$filterCondition"/>
					<xsl:with-param name="dependencies" select="$filterConditionDependencies" as="xs:string*"/>
					<xsl:with-param name="idLoop" select="$idLoop"/>
					<xsl:with-param name="languages" select="$languages"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
		<xsl:call-template name="enolunatic:add-collected-variable-to-components">
			<xsl:with-param name="responseName" select="$responseName"/>
			<xsl:with-param name="componentRef" select="$idQuestion"/>
			<xsl:with-param name="loopDepth" select="$loopDepth"/>
			<xsl:with-param name="idLoop" select="$idLoop"/>
		</xsl:call-template>
		<xsl:if test="$shouldHaveMissingVars and $missingVar">
			<xsl:call-template name="enolunatic:add-collected-variable-to-components">
				<xsl:with-param name="responseName" select="concat($questionName,'_MISSING')"/>
				<xsl:with-param name="componentRef" select="$idQuestion"/>
				<xsl:with-param name="loopDepth" select="$loopDepth"/>
				<xsl:with-param name="idLoop" select="$idLoop"/>
			</xsl:call-template>
		</xsl:if>
		

		<xsl:call-template name="enolunatic:add-format-controls">
			<xsl:with-param name="idQuestion" select="$idQuestion"/>
			<xsl:with-param name="responseName" select="$responseName"/>
			<xsl:with-param name="componentType" select="$componentType"/>
			<xsl:with-param name="minimumResponse" select="$minimumResponse"/>
			<xsl:with-param name="numberOfDecimals" select="$numberOfDecimals"/>
			<xsl:with-param name="maximumResponse" select="$maximumResponse"/>
			<xsl:with-param name="format" select="$format"/>
			<xsl:with-param name="lengthResponse" select="$lengthResponse"/>
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
			<xsl:variable name="dec" select="string(enolunatic:get-number-of-decimals($source-context))"/>
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
		<xsl:call-template name="enolunatic:add-format-controls">
			<xsl:with-param name="idQuestion" select="$idQuestion"/>
			<xsl:with-param name="responseName" select="$responseName"/>
			<xsl:with-param name="componentType" select="$componentType"/>
			<xsl:with-param name="minimumResponse" select="$minimumResponse"/>
			<xsl:with-param name="numberOfDecimals" select="$numberOfDecimals"/>
			<xsl:with-param name="maximumResponse" select="$maximumResponse"/>
			<xsl:with-param name="format" select="$dateFormat"/>
			<xsl:with-param name="lengthResponse" select="$lengthResponse"/>
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
		<xsl:variable name="labelType" select="enolunatic:get-label-type('options.label')"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>
		<xsl:if test="$label !=''">
			<options>
				<value><xsl:value-of select="enolunatic:get-value($source-context)"/></value>
				<label>
					<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></value>
					<type><xsl:value-of select="$labelType"/></type>
				</label>
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
		<xsl:variable name="labelType" select="enolunatic:get-label-type('responses.label')"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>

		<xsl:if test="$label !=''">
			<label>
				<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></value>
				<type><xsl:value-of select="$labelType"/></type>
			</label>
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
		<xsl:variable name="labelType" select="enolunatic:get-label-type('declarations.label')"/>
		<xsl:variable name="instructionFormatMaj" select="concat(upper-case(substring($instructionFormat,1,1)),
			substring($instructionFormat,2))" as="xs:string"/>
		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($instructionLabel)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>

		<xsl:if test="$positionDeclaration!=''">
			<declarations declarationType="{$instructionFormat}" id="{enolunatic:get-name($source-context)}" position="{$positionDeclaration}">
				<label>
					<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$instructionLabel)"/></value>
					<type><xsl:value-of select="$labelType"/></type>
				</label>
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
		<xsl:variable name="expression" select="enolunatic:replace-variable-with-collected-and-external-variables-formula($source-context,$nameOutVariable)"/>

		<xsl:variable name="expressionDependencies" as="xs:string*">
			<xsl:copy-of select="enolunatic:find-variables-in-formula($expression)"/>
				<xsl:call-template name="enolunatic:resolve-variables-to-collected-and-external-variables">
					<xsl:with-param name="source-context" select="$source-context"/>
					<xsl:with-param name="listVar" select="enolunatic:find-variables-in-formula($expression)"/>
				</xsl:call-template>
		</xsl:variable>

		<variables variableType="CALCULATED" xsi:type="VariableType">
			<name><xsl:value-of select="$nameOutVariable"/></name>
			<expression>				
				<value><xsl:value-of select="normalize-space(enolunatic:replace-all-variables-with-business-name($source-context,$expression))"/></value>
				<type><xsl:value-of select="enolunatic:get-label-type('expression')"/></type>
			</expression>
			<xsl:for-each select="distinct-values($expressionDependencies)">
				<bindingDependencies><xsl:value-of select="enolunatic:get-variable-business-name(.)"/></bindingDependencies>
			</xsl:for-each>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
			<xsl:if test="enolunatic:is-scope-questionconstruct($source-context)">
			<shapeFrom>
				<xsl:value-of select="enolunatic:get-shapeFrom-name(enolunatic:get-scope-id($source-context),'QuestionConstruct',$languages[1])"/>
			</shapeFrom>
			</xsl:if>
			<xsl:if test="enolunatic:is-scope-loop($source-context)">
				<shapeFrom>
					<xsl:value-of select="enolunatic:get-shapeFrom-name(enolunatic:get-scope-id($source-context),'Loop',$languages[1])"/>
				</shapeFrom>
			</xsl:if>
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
		<xsl:variable name="labelType" select="enolunatic:get-label-type('label')"/>
		<xsl:variable name="filter" select="enolunatic:get-global-filter($source-context)"/>
		<xsl:variable name="filterDependencies" select="enolunatic:find-variables-in-formula($filter)"/>
		<xsl:variable name="filterCondition" select="enolunatic:replace-all-variables-with-business-name($source-context,$filter)"/>

		<xsl:variable name="labelDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($label)"/>
		<xsl:variable name="dependencies" select="enolunatic:add-dependencies($labelDependencies)"/>

		<components xsi:type="{$componentType}" componentType="{$componentType}" id="{$idGoTo}" filterDescription="{$filterDescription}">
			<label>
				<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$label)"/></value>
				<type><xsl:value-of select="$labelType"/></type>
			</label>
			<xsl:copy-of select="enolunatic:add-condition-filter($filterCondition,$filterDependencies)"/>
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

		<xsl:if test="$controlParam">
			<xsl:variable name="id" select="enolunatic:get-name($source-context)"/>
			<xsl:variable name="control" select="enolunatic:get-constraint($source-context)"/>
			<xsl:variable name="errorMessage" select="enolunatic:get-vtl-label($source-context, $languages[1])"/>
			<xsl:variable name="criticality" select="enolunatic:get-alert-level($source-context)"/>
			<xsl:variable name="typeOfControl"><xsl:value-of select="'CONSISTENCY'"/></xsl:variable>
			
			<xsl:variable name="controlDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($control)"/>
			<xsl:variable name="instructionDependencies" as="xs:string*" select="enolunatic:find-variables-in-formula($errorMessage)"/>
			<xsl:variable name="dependenciesVariables" as="xs:string*">
				<xsl:for-each select="$controlDependencies">
					<xsl:sequence select="."/>
				</xsl:for-each>
				<xsl:for-each select="$instructionDependencies">
					<xsl:sequence select="."/>
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="dependencies" select="enolunatic:add-dependencies($dependenciesVariables)"/>
	
			<controls>
				
				<xsl:if test="$id != ''">
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
				</xsl:if>
				<xsl:if test="$criticality != ''">
					<xsl:choose>
						<xsl:when test="$criticality='warning'"><xsl:attribute name="criticality"><xsl:value-of select="'WARN'"/></xsl:attribute></xsl:when>
						<xsl:when test="$criticality='stumblingblock'"><xsl:attribute name="criticality"><xsl:value-of select="'ERROR'"/></xsl:attribute></xsl:when>
						<xsl:otherwise><xsl:attribute name="criticality"><xsl:value-of select="'INFO'"/></xsl:attribute></xsl:otherwise>
					</xsl:choose>
				</xsl:if>
				<xsl:attribute name="typeOfControl"><xsl:value-of select="$typeOfControl"/></xsl:attribute>
				<xsl:if test="$control!=''">
					<control>
						<value><xsl:value-of select="normalize-space(enolunatic:replace-all-variables-with-business-name($source-context,$control))"/></value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.control')"/></type>
					</control>
				</xsl:if>
				<xsl:if test="$errorMessage!=''">
					<errorMessage>
						<value><xsl:value-of select="enolunatic:replace-all-variables-with-business-name($source-context,$errorMessage)"/></value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.errorMessage')"/></type>
					</errorMessage>
				</xsl:if>
				<xsl:copy-of select="$dependencies"/>
	
				<!-- Go to the Calculated Variable -->
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
			</controls>
		</xsl:if>
		
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
			<!-- <componentRef><xsl:value-of select="$newComponentRef"/></componentRef>  -->
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
			<xd:p>Named template: enolunatic:add-format-controls.</xd:p>
			<xd:p>It creates the format controls.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template name="enolunatic:add-format-controls">
		<xsl:param name="idQuestion"/>
		<xsl:param name="responseName"/>
		<xsl:param name="componentType"/>
		<xsl:param name="minimumResponse"/>
		<xsl:param name="maximumResponse"/>
		<xsl:param name="format"/>
		<xsl:param name="numberOfDecimals"/>
		<xsl:param name="lengthResponse"/>
		<xsl:if test="$componentType='InputNumber'">
			<xsl:if test="$minimumResponse!='' and $maximumResponse!=''">	
				<controls>		
					<xsl:attribute name="id"><xsl:value-of select="concat($idQuestion,'-format-borne-inf-sup')"/></xsl:attribute>
					<xsl:attribute name="criticality"><xsl:value-of select="'ERROR'"/></xsl:attribute>
					<xsl:attribute name="typeOfControl"><xsl:value-of select="'FORMAT'"/></xsl:attribute>
					<control>       
						<value>
							<xsl:value-of select="'not(not(isnull('||$responseName||')) and ('||$minimumResponse|| '&gt;'||$responseName||' or '||$maximumResponse||'&lt;'||$responseName||'))'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.control')"/></type>
					</control>
					<errorMessage>
						<value>
							<xsl:value-of select="'&quot; La valeur doit être comprise entre ' ||$minimumResponse || ' et ' ||$maximumResponse ||'.&quot;'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.errorMessage')"/></type>
					</errorMessage>
				</controls>
			</xsl:if>
			<xsl:if test="$minimumResponse='' and $maximumResponse!=''">	
				<controls>		
					<xsl:attribute name="id"><xsl:value-of select="concat($idQuestion,'-format-borne-sup')"/></xsl:attribute>
					<xsl:attribute name="criticality"><xsl:value-of select="'ERROR'"/></xsl:attribute>   
					<xsl:attribute name="typeOfControl"><xsl:value-of select="'FORMAT'"/></xsl:attribute>
					<control>       
						<value>
							<xsl:value-of select="'not(not(isnull('||$responseName||')) and '||$maximumResponse||'&lt;'||$responseName||')'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.control')"/></type>
					</control>
					<errorMessage>
						<value>
							<xsl:value-of select="'&quot;La valeur doit être inférieure à ' ||$maximumResponse ||'.&quot;'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.errorMessage')"/></type>
					</errorMessage>
				</controls>
			</xsl:if>
			<xsl:if test="$minimumResponse!='' and $maximumResponse=''">	
				<controls>		
					<xsl:attribute name="id"><xsl:value-of select="concat($idQuestion,'-format-borne-inf')"/></xsl:attribute>
					<xsl:attribute name="criticality"><xsl:value-of select="'ERROR'"/></xsl:attribute>   
					<xsl:attribute name="typeOfControl"><xsl:value-of select="'FORMAT'"/></xsl:attribute>
					<control>       
						<value>
							<xsl:value-of select="'not(not(isnull('||$responseName||')) and '||$minimumResponse|| '&gt;'||$responseName||')'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.control')"/></type>
					</control>
					<errorMessage>
						<value>
							<xsl:value-of select="'&quot;La valeur doit être supérieure à ' ||$minimumResponse ||'.&quot;'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.errorMessage')"/></type>
					</errorMessage>
				</controls>
			</xsl:if>
			<controls>		
				<xsl:attribute name="id"><xsl:value-of select="concat($idQuestion,'-format-decimal')"/></xsl:attribute>
				<xsl:attribute name="criticality"><xsl:value-of select="'ERROR'"/></xsl:attribute>  
				<xsl:attribute name="typeOfControl"><xsl:value-of select="'FORMAT'"/></xsl:attribute>
				<control>       
					<value>
						<xsl:value-of select="'not(not(isnull('||$responseName||'))  and round('||$responseName||','||$numberOfDecimals ||')&lt;&gt;'||$responseName||')'"/>				
					</value>
					<type><xsl:value-of select="enolunatic:get-label-type('controls.control')"/></type>
				</control>
				<errorMessage>
					<value>
						<xsl:value-of select="'&quot;Le nombre doit comporter au maximum ' ||$numberOfDecimals|| ' chiffre(s) après la virgule.&quot;'"/>				
					</value>
					<type><xsl:value-of select="enolunatic:get-label-type('controls.errorMessage')"/></type>
				</errorMessage>
			</controls>
		</xsl:if>
		
		<xsl:if test="$componentType='Datepicker'">
			<xsl:if test="$minimumResponse!='' and $maximumResponse=''">
				<controls>		
					<xsl:attribute name="id"><xsl:value-of select="concat($idQuestion,'-format-date-borne-sup')"/></xsl:attribute>
					<xsl:attribute name="criticality"><xsl:value-of select="'ERROR'"/></xsl:attribute>   
					<xsl:attribute name="typeOfControl"><xsl:value-of select="'FORMAT'"/></xsl:attribute>
					<control>       
						<value>
							<xsl:value-of select="'not(not(isnull('||$responseName||')) and cast('||$responseName||', date, &quot;'||$format||'&quot;)&lt;cast(&quot;'||$minimumResponse||'&quot;, date, &quot;'||$format||'&quot;))'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.control')"/></type>
					</control>
					<errorMessage>
						<value>
							<xsl:value-of select="'&quot;La date saisie doit être postérieure à '|| $minimumResponse ||'.&quot;'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.errorMessage')"/></type>
					</errorMessage>
				</controls>
			</xsl:if>
			<xsl:if test="$minimumResponse='' and $maximumResponse!=''">
				<controls>		
					<xsl:attribute name="id"><xsl:value-of select="concat($idQuestion,'-format-date-borne-inf')"/></xsl:attribute>
					<xsl:attribute name="criticality"><xsl:value-of select="'ERROR'"/></xsl:attribute>   
					<xsl:attribute name="typeOfControl"><xsl:value-of select="'FORMAT'"/></xsl:attribute>
					<control>       
						<value>
							<xsl:value-of select="'not(not(isnull('||$responseName||')) and cast('||$responseName||', date, &quot;'||$format||')&gt;cast(&quot;'||$maximumResponse||'&quot;, date, &quot;'||$format||'&quot;))'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.control')"/></type>
					</control>
					<errorMessage>
						<value>
							<xsl:value-of select="'&quot;La date saisie doit être antérieure à '|| $maximumResponse|| '.&quot;'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.errorMessage')"/></type>
					</errorMessage>
				</controls>
			</xsl:if>
			<xsl:if test="$minimumResponse!='' and $maximumResponse!=''">
				<controls>		
					<xsl:attribute name="id"><xsl:value-of select="concat($idQuestion,'-format-borne-inf-sup')"/></xsl:attribute>
					<xsl:attribute name="criticality"><xsl:value-of select="'ERROR'"/></xsl:attribute>   
					<xsl:attribute name="typeOfControl"><xsl:value-of select="'FORMAT'"/></xsl:attribute>
					<control>       
						<value>
							<xsl:value-of select="'not(not(isnull('||$responseName||')) and (cast('||$responseName||', date, &quot;'||$format||'&quot;)&gt;cast(&quot;'||$maximumResponse||'&quot;, date, &quot;'||$format||'&quot;) or cast('||$responseName||', date, &quot;'||$format||'&quot;)&lt;cast(&quot;'||$minimumResponse||'&quot;, date, &quot;'||$format||'&quot;)))'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.control')"/></type>
					</control>
					<errorMessage>
						<value>
							<xsl:value-of select="'&quot;La date saisie doit être comprise entre '|| $minimumResponse || ' et '|| $maximumResponse || '.&quot;'"/>				
						</value>
						<type><xsl:value-of select="enolunatic:get-label-type('controls.errorMessage')"/></type>
					</errorMessage>
				</controls>
			</xsl:if>
		</xsl:if>
		
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
	
	<xd:doc>
		<xd:desc>
			<xd:p>Named function: enolunatic:add-conditionFilter</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:function name="enolunatic:add-condition-filter">
		<xsl:param name="value" as="xs:string"/>
		<xsl:param name="dependencies" as="xs:string*"/>
		<conditionFilter>
			<value><xsl:value-of select="$value"/></value>
			<type><xsl:value-of select="enolunatic:get-label-type('conditionFilter')"/></type>
			<xsl:copy-of select="enolunatic:add-dependencies($dependencies)"/>
		</conditionFilter>
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
		<xsl:param name="dependencies" as="xs:string*"/>
		<xsl:param name="idLoop"/>
		<xsl:param name="languages"/>
		<variables variableType="CALCULATED" xsi:type="VariableType">
			<name><xsl:value-of select="concat('FILTER_RESULT_',$name)"/></name>
			<expression>
				<value><xsl:value-of select="$expression"/></value>
				<type><xsl:value-of select="enolunatic:get-label-type('expression')"/></type>
			</expression>
			<xsl:for-each select="distinct-values($dependencies)">
				<bindingDependencies><xsl:value-of select="enolunatic:get-variable-business-name(.)"/></bindingDependencies>
			</xsl:for-each>
			<xsl:if test="$idLoop!=''">
				<shapeFrom><xsl:value-of select="enolunatic:get-shapeFrom-name($idLoop,'Loop',$languages[1])"/></shapeFrom>
			</xsl:if>
		</variables>
	</xsl:template>
	
</xsl:stylesheet>