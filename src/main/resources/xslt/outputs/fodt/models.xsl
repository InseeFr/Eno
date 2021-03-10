<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
	xmlns:enofodt="http://xml.insee.fr/apps/eno/out/fodt"
	xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
	xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
	xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
	xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
	xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
	xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="xs fn xd eno enofodt" version="2.0">
	
	<xsl:import href="../../../styles/style.xsl"/>
	<xsl:include href="../../../xslt/outputs/fodt/office-styles.xsl"/>
	
	
	<xd:doc scope="stylesheet">
		<xd:desc>
			<xd:p>An xslt stylesheet that transforms an input into Odt through generic driver
				templates.</xd:p>
			<xd:p>The real input is mapped with the drivers.</xd:p>
		</xd:desc>
	</xd:doc>
	
	
	<xsl:variable name="varName" select="parent"/>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Forces the traversal of the whole driver tree. Must be present once in the
				transformation.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="*" mode="model" priority="-1">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on Form driver.</xd:p>
			<xd:p>It writes the root of the document with the main title.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Form" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofodt:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<office:document office:version="1.2"
			office:mimetype="application/vnd.oasis.opendocument.text"
			xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
			xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
			xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
			xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0">
			<office:font-face-decls>
				<style:font-face style:name="Arial" svg:font-family="Arial"
					style:font-family-generic="system" style:font-pitch="variable"/>
			</office:font-face-decls>
			
			<office:automatic-styles>
				<xsl:copy-of select="eno:Office-styles($source-context)"/>
			</office:automatic-styles>
			
			
			<!--
				<xsl:copy-of select="$header-content/office:document/@*"/>
				<xsl:copy-of select="$header-content/office:document/node()[not(name()='office:body')]"/>
			-->
			<office:body>
				<office:text>
					<text:p text:style-name="Title">
						<xsl:value-of select="enofodt:get-label($source-context, $languages[1])"/>
					</text:p>
					<text:p text:style-name="TitleComment">
						<!--  <xsl:value-of select="concat('Specification generated on: ',format-dateTime(current-dateTime(), '[D01]/[M01]/[Y0001] - [H1]:[m01]:[s01]'))"/>-->
						Specification generated from Eno </text:p>
					<!-- Go to the children -->
					<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
						<xsl:with-param name="driver" select="." tunnel="yes"/>
						<xsl:with-param name="languages" select="$languages" tunnel="yes"/>
					</xsl:apply-templates>
				</office:text>
			</office:body>
		</office:document>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on Module driver.</xd:p>
			<xd:p>It writes module label.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Module" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="moduleName"
			select="enofodt:get-module-name($source-context, $languages[1])"/>
		
		<text:section text:name="Module-{enofodt:get-name($source-context)}">
			<xsl:choose>
				<xsl:when test="enofodt:is-first-module-in-loop($source-context)">
					<text:p text:style-name="FirstModuleInLoop">
						<xsl:if test="$moduleName != ''">
							<text:span text:style-name="ModuleName">
								<xsl:value-of select="concat('[', $moduleName, '] ')"/>
							</text:span>
						</xsl:if>
						<xsl:value-of select="enofodt:get-label($source-context, $languages[1])"/>
					</text:p>
				</xsl:when>
				<xsl:otherwise>
					<text:p text:style-name="Module">
						<xsl:if test="$moduleName != ''">
							<text:span text:style-name="ModuleName">
								<xsl:value-of select="concat('[', $moduleName, '] ')"/>
							</text:span>
						</xsl:if>
						<xsl:value-of select="enofodt:get-label($source-context, $languages[1])"/>
					</text:p>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</text:section>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on SubModule driver.</xd:p>
			<xd:p>It writes sub-module label.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="SubModule" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="submoduleName"
			select="enofodt:get-submodule-name($source-context, $languages[1])"/>
		<text:section text:name="SubModule-{enofodt:get-name($source-context)}">
			<text:p text:style-name="SubModule">
				<xsl:if test="$submoduleName != ''">
					<text:span text:style-name="ModuleName">
						<xsl:value-of select="concat('[', $submoduleName, '] ')"/>
					</text:span>
				</xsl:if>
				<xsl:value-of select="enofodt:get-label($source-context, $languages[1])"/>
			</text:p>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</text:section>
	</xsl:template>
	
	<xsl:template match="SingleResponseQuestion | MultipleQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="questionName"
			select="enofodt:get-question-name($source-context, $languages[1])"/>
		<text:section text:name="Question-{enofodt:get-name($source-context)}">
			<xsl:if test="$questionName != ''">
				<text:p text:style-name="QuestionName">
					<xsl:value-of select="concat('[', $questionName, ']')"/>
				</text:p>
			</xsl:if>
			
			<!-- print the question label and its instructions -->
			
			<xsl:call-template name="eno:printQuestionTitleWithInstruction">
				<xsl:with-param name="driver" select="."/>
			</xsl:call-template>
			
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="typeOfAncestor" select="'question'" tunnel="yes"/>
			</xsl:apply-templates>
			
			<xsl:if test="enofodt:get-question-tooltip($source-context,$languages[1]) != ''">
				<text:p text:style-name="Tooltip">
					<xsl:value-of select="'(*) Infobulle au niveau de la question : ['"/>
					<xsl:value-of select="enofodt:get-question-tooltip($source-context,$languages[1])"/>
					<xsl:value-of select="']'"/>
				</text:p>
			</xsl:if>
			
			<xsl:if test="enofodt:get-question-response-tooltip($source-context,$languages[1]) != ''">
				<xsl:for-each select="enofodt:get-question-response-tooltip($source-context,$languages[1])">
					<text:p text:style-name="Tooltip">
						<xsl:value-of select="'(*) Infobulle au niveau des modalités de réponse : ['"/>
						<xsl:value-of select="."/>
						<xsl:value-of select="']'"/>
					</text:p>
				</xsl:for-each>
			</xsl:if>
			
			<xsl:if test="enofodt:get-question-instruction-tooltip($source-context,$languages[1]) != ''">
				<text:p text:style-name="Tooltip">
					<xsl:value-of select="'(*) Infobulle au niveau d''une consigne : ['"/>
					<xsl:value-of select="enofodt:get-question-instruction-tooltip($source-context,$languages[1])"/>
					<xsl:value-of select="']'"/>
				</text:p>
			</xsl:if>
			
			<xsl:apply-templates select="enofodt:get-end-question-instructions($source-context)"
				mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</text:section>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on NumericDomain driver.</xd:p>
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="NumericDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="numberOfDecimals"
			select="enofodt:get-number-of-decimals($source-context)"/>
		<xsl:variable name="minimumResponse" select="enofodt:get-minimum($source-context)"/>
		<xsl:variable name="maximumResponse" select="enofodt:get-maximum($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enofodt:get-business-name($source-context)"/>
		
		<text:p text:style-name="Format">
			<text:span text:style-name="NameOfVariable">
				<xsl:value-of select="concat('[', $nameOfVariable, '] - ')"/>
			</text:span>
			<xsl:choose>
				<xsl:when test="fn:string-length($numberOfDecimals) > 0">
					<xsl:value-of
						select="concat('num ', fn:substring-before($minimumResponse, '.'), '..', fn:substring-before($maximumResponse, '.'), ' - ', $numberOfDecimals, ' chiffre(s) après la virgule')"
					/>
				</xsl:when>
				<xsl:when test="fn:string-length($numberOfDecimals) = 0">
					<xsl:value-of select="concat('num ', $minimumResponse, '..', $maximumResponse)"
					/>
				</xsl:when>
			</xsl:choose>
		</text:p>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on TextDomain driver.</xd:p>
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="TextDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="lengthResponse" select="enofodt:get-length($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enofodt:get-business-name($source-context)"/>
		
		<text:p text:style-name="Format">
			<text:span text:style-name="NameOfVariable">
				<xsl:value-of select="concat('[', $nameOfVariable, '] - ')"/>
			</text:span>
			<xsl:value-of select="concat('Car ', $lengthResponse)"/>
		</text:p>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on DateTimeDomain driver.</xd:p>
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="DateTimeDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="dateFormat" select="enofodt:get-format($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enofodt:get-business-name($source-context)"/>
		<text:p text:style-name="Format">
			<text:span text:style-name="NameOfVariable">
				<xsl:value-of select="concat('[', $nameOfVariable, '] - ')"/>
			</text:span>
			<xsl:value-of select="concat('date ( ', $dateFormat, ' )')"/>
		</text:p>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on TextareaDomain driver.</xd:p>
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="TextareaDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="lengthResponse" select="enofodt:get-length($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enofodt:get-business-name($source-context)"/>
		
		<text:p text:style-name="Format">
			<text:span text:style-name="NameOfVariable">
				<xsl:value-of select="concat('[', $nameOfVariable, '] - ')"/>
			</text:span>
			<xsl:value-of select="concat('Car ', $lengthResponse)"/>
		</text:p>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on BooleanDomain driver.</xd:p>
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="BooleanDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="typeOfAncestor" tunnel="yes"/>
		<xsl:variable name="nameOfVariable" select="enofodt:get-business-name($source-context)"/>
		
		<xsl:if test="$typeOfAncestor != ''">
			<text:p text:style-name="Format">
				<text:span text:style-name="NameOfVariable">
					<xsl:value-of select="concat('[', $nameOfVariable, '] - ')"/>
				</text:span>
				<xsl:value-of select="'Booléen'"/>
			</text:p>
		</xsl:if>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on CodeDomain driver.</xd:p>
			<xd:p>It writes the short name, the label and its response format of a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="CodeDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="typeOfAncestor" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="maximumLengthCode"
			select="enofodt:get-code-maximum-length($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enofodt:get-business-name($source-context)"/>
		
		<xsl:if test="$maximumLengthCode != ''">
			<text:p text:style-name="Format">
				<text:span text:style-name="NameOfVariable">
					<xsl:value-of select="concat('[', $nameOfVariable, '] - ')"/>
				</text:span>
				<xsl:value-of
					select="concat('Car ', $maximumLengthCode, ' - ', 'liste de modalités')"/>
			</text:p>
		</xsl:if>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the Table driver and TableLoop driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Table | TableLoop | MultipleChoiceQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="questionName"
			select="enofodt:get-question-name($source-context, $languages[1])"/>
		<xsl:variable name="maximumLengthCode"
			select="enofodt:get-code-maximum-length($source-context)"/>
		<xsl:variable name="headerCol" select="enofodt:get-body-line($source-context, position())"/>
		<xsl:variable name="type" select="enofodt:get-css-class($source-context)"/>
		<text:section text:name="Table-{enofodt:get-name($source-context)}">
			<xsl:if test="$questionName != ''">
				<text:p text:style-name="QuestionName">
					<xsl:value-of select="concat('[', $questionName, ']')"/>
				</text:p>
			</xsl:if>
			
			<!-- print the question label and its instructions -->
			<xsl:call-template name="eno:printQuestionTitleWithInstruction">
				<xsl:with-param name="driver" select="."/>
			</xsl:call-template>
			
			<table:table table:name="{enofodt:get-name($source-context)}" table:style-name="Table">
				<xsl:for-each select="$headerCol">
					<table:table-column table:style-name="Table.Column"/>
				</xsl:for-each>
				<!--    Header   -->
				<xsl:for-each select="enofodt:get-header-lines($source-context)">
					<table:table-row>
						<xsl:apply-templates
							select="enofodt:get-header-line($source-context, position())"
							mode="source">
							<xsl:with-param name="ancestorTable" select="'headerLine'" tunnel="yes"
							/>
						</xsl:apply-templates>
					</table:table-row>
				</xsl:for-each>
				<!--   Body    -->
				<xsl:for-each select="enofodt:get-body-lines($source-context)">
					<table:table-row>
						<xsl:apply-templates
							select="enofodt:get-body-line($source-context, position())" mode="source">
							<xsl:with-param name="ancestorTable" select="'line'" tunnel="yes"/>
							<xsl:with-param name="typeOfAncestor" select="$type" tunnel="yes"/>
						</xsl:apply-templates>
					</table:table-row>
				</xsl:for-each>
			</table:table>
			
			<xsl:variable name="nbMaximumLines" select="enofodt:get-maximum-lines($source-context)"/>
			<xsl:variable name="nbMinimumLines" select="enofodt:get-minimum-lines($source-context)"/>
			<xsl:if test="$nbMinimumLines != ''">
				<text:p>
					<xsl:value-of select="concat('Nb line(s) minimum required : ', $nbMinimumLines)"
					/>
				</text:p>
			</xsl:if>
			<xsl:if test="$nbMaximumLines != ''">
				<text:p>
					<xsl:value-of select="concat('Nb line(s) maximum allowed : ', $nbMaximumLines)"
					/>
				</text:p>
			</xsl:if>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</text:section>
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
		<xsl:variable name="col-span" select="number(enofodt:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enofodt:get-rowspan($source-context))"/>
		
		
		<xsl:if test="$ancestorTable != ''">
			<table:table-cell table:number-rows-spanned="{$row-span}"
				table:number-columns-spanned="{$col-span}" table:style-name="Table.Cell">
				<xsl:variable name="label" select="enofodt:get-label($source-context, $languages)"/>
				<xsl:choose>
					<xsl:when test="$label != '' and $ancestorTable = 'line'">
						<text:p text:style-name="Question">
							<xsl:value-of select="$label"/>
						</text:p>
					</xsl:when>
					<xsl:when test="$label != '' and $ancestorTable = 'headerLine'">
						<text:p text:style-name="ColumnHeader">
							<xsl:value-of select="$label"/>
						</text:p>
					</xsl:when>
				</xsl:choose>
			</table:table-cell>
			
			<!-- To add spanned rows / columns -->
			<xsl:if test="$row-span &gt; 1">
				<xsl:for-each select="2 to xs:integer(floor($row-span))">
					<table:covered-table-cell table:style-name="Table.Cell"/>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="$col-span &gt; 1">
				<xsl:for-each select="2 to xs:integer(floor($col-span))">
					<table:covered-table-cell table:style-name="Table.Cell"/>
				</xsl:for-each>
			</xsl:if>
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
		<xsl:variable name="col-span" select="number(enofodt:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enofodt:get-rowspan($source-context))"/>
		<xsl:if test="$ancestorTable != ''">
			<table:table-cell table:number-rows-spanned="{$row-span}"
				table:number-columns-spanned="{$col-span}" table:style-name="Table.Cell">
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
			</table:table-cell>
			
			<!-- To add spanned rows / columns -->
			<xsl:if test="$row-span &gt; 1">
				<xsl:for-each select="2 to xs:integer(floor($row-span))">
					<table:covered-table-cell table:style-name="Table.Cell"/>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="$col-span &gt; 1">
				<xsl:for-each select="2 to xs:integer(floor($col-span))">
					<table:covered-table-cell table:style-name="Table.Cell"/>
				</xsl:for-each>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the EmptyCell driver.</xd:p>
			<xd:p>Create a cell and call templates for children to fill the cell (a priori
				nothing).</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="EmptyCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="ancestorTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="col-span" select="number(enofodt:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enofodt:get-rowspan($source-context))"/>
		<xsl:if test="$ancestorTable != ''">
			<table:table-cell table:number-rows-spanned="{$row-span}"
				table:number-columns-spanned="{$col-span}" table:style-name="Table.Cell">
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
			</table:table-cell>
			
			<!-- To add spanned rows / columns -->
			<xsl:if test="$row-span &gt; 1">
				<xsl:for-each select="2 to xs:integer(floor($row-span))">
					<table:covered-table-cell table:style-name="Table.Cell"/>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="$col-span &gt; 1">
				<xsl:for-each select="2 to xs:integer(floor($col-span))">
					<table:covered-table-cell table:style-name="Table.Cell"/>
				</xsl:for-each>
			</xsl:if>
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
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="label" select="enofodt:get-label($source-context, $languages[1])"/>
		
		<!-- remove item in the cell for table when the response is boolean-->
		<xsl:if test="$label != '' and not(ancestor::BooleanDomain)">
			<text:p text:style-name="CodeItem">
				<xsl:value-of select="fn:concat(enofodt:get-value($source-context), ' - ', $label)"/>
			</text:p>
		</xsl:if>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the xf-output driver.</xd:p>
			<xd:p>It writes the instruction text, with a different styles for comments,
				instructions, warning and help.</xd:p>
			<xd:p>It works for all drivers except for drivers whose contain a question.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="instructionFormat" select="enofodt:get-format($source-context)"/>
		<xsl:variable name="instructionLabel"
			select="enofodt:get-label($source-context, $languages[1])"/>
		<xsl:variable name="instructionFormatMaj"
			select="
			concat(upper-case(substring($instructionFormat, 1, 1)),
			substring($instructionFormat, 2))"
			as="xs:string"/>
		
		<xsl:choose>
			<xsl:when test="$instructionFormat = 'comment'">
				<text:p text:style-name="Comment">
					<xsl:value-of select="$instructionLabel"/>
				</text:p>
			</xsl:when>
			<xsl:when test="$instructionFormat = 'instruction'">
				<text:p text:style-name="Instruction">
					<xsl:value-of select="$instructionLabel"/>
				</text:p>
			</xsl:when>
			<xsl:when test="$instructionFormat = 'warning'">
				<text:p text:style-name="Warning">
					<xsl:value-of select="$instructionLabel"/>
				</text:p>
			</xsl:when>
			<xsl:when test="$instructionFormat = 'help'">
				<text:p text:style-name="Help">
					<xsl:value-of select="$instructionLabel"/>
				</text:p>
			</xsl:when>
			<xsl:otherwise>
				<text:p text:style-name="Instruction">
					<xsl:value-of select="$instructionLabel"/>
				</text:p>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the xf-group driver.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="xf-group" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="filter" select="enofodt:get-relevant($source-context)"/>
		<xsl:variable name="idVariables"
			select="tokenize(enofodt:get-hideable-command-variables($source-context), '\s')"/>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	
	<xd:doc>
		<xd:desc>
			<xd:p>Match on the Filter driver : a better option than using xf-group to deal with IfThenElse and filters for fodt spec</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="Filter" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:variable name="filter" select="enofodt:get-relevant($source-context)"/>
		<xsl:variable name="idVariables"
			select="tokenize(enofodt:get-hideable-command-variables($source-context), '\s')"/>
		<xsl:variable name="filterType">
			<xsl:choose>
				<xsl:when test="enofodt:is-module-filter($source-context)">
					<xsl:value-of select="'ModuleFilter'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'Filter'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<text:section text:name="Filter-{enofodt:get-name($source-context)}">
			<text:p text:style-name="{$filterType}">
				<xsl:value-of select="'Filtre'"/>
			</text:p>
			
			<text:p text:style-name="FilterInfo">
				<xsl:value-of select="'Description du filtre : '"/>
				<xsl:value-of select="enofodt:get-filter-description($source-context, $languages[1])"/>
			</text:p>
			
			<text:p text:style-name="FilterInfo">
				<xsl:value-of select="'Condition du filtre : '"/>
				<xsl:call-template name="replaceVariablesInFormula">
					<xsl:with-param name="formula" select="$filter"/>
					<xsl:with-param name="variables" select="$idVariables"/>
				</xsl:call-template>
			</text:p>
			
			
		</text:section>
		
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
		<text:section text:name="FilterEnd-{enofodt:get-name($source-context)}">
			<text:p text:style-name="Filter">
				<xsl:value-of select="'Fin du filtre'"/>
			</text:p>
			<text:p text:style-name="FilterInfo">
				<xsl:value-of select="'Description du filtre : '"/>
				<xsl:value-of select="enofodt:get-filter-description($source-context, $languages[1])"/>
			</text:p>
		</text:section>
		
	</xsl:template>
	
	
	<xd:doc>
		<xd:desc>template for the GoTo</xd:desc>
	</xd:doc>
	<xsl:template match="GoTo" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="label" select="enofodt:get-label($source-context, $languages[1])"
			as="node()"/>
		<xsl:variable name="nameOfVariable" select="enofodt:get-flowcontrol-target($source-context)"/>
		
		<xsl:if test="$label != ''">
			<text:section text:name="GoTo-{enofodt:get-name($source-context)}">
				<text:p text:style-name="Format">
					<text:span text:style-name="GotoTitle">
						<xsl:value-of select="'Redirection : '"/>
					</text:span>
				</text:p>
				<text:p text:style-name="Format">
					<text:span text:style-name="GotoText">
						<xsl:copy-of select="$label"/>
					</text:span>
				</text:p>
				<text:p text:style-name="Format">
					<text:span text:style-name="GotoTitle">
						<xsl:value-of select="'Condition : '"/>
					</text:span>
					<text:span text:style-name="GotoText">
						<xsl:value-of select="enofodt:get-flowcontrol-condition($source-context)"/>
					</text:span>
				</text:p>
				
				<text:p text:style-name="Format">
					<text:span text:style-name="GotoTitle">
						<xsl:value-of select="'Cible : '"/>
					</text:span>
					<text:span text:style-name="NameOfVariable">
						<xsl:choose>
							<xsl:when test="$nameOfVariable='End of questionnaire'">
								<xsl:value-of select="'[Fin du questionnaire]'"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat('[', $nameOfVariable, ']')"/>
							</xsl:otherwise>
						</xsl:choose>
					</text:span>
				</text:p>
				
			</text:section>
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
		
		<xsl:variable name="variableCalculation"
			select="enofodt:get-variable-calculation($source-context)"/>
		
		<xsl:variable name="isFirst" select="enofodt:is-first-calculated-variable($source-context)"/>
		
		<!--<xsl:variable name="outVariable" select="enofodt:get-name($source-context)"/>-->
		<xsl:variable name="nameOutVariable" select="enofodt:get-business-name($source-context)"/>
		<xsl:variable name="idVariables"
			select="tokenize(enofodt:get-variable-calculation-variables($source-context), '\s')"/>
		
		<xsl:if test="$isFirst">
			<text:p text:style-name="OpeningCalculatedVariableSection">
				<xsl:value-of select="'Liste des variables calculées'"/>
			</text:p>
		</xsl:if>
		
		<text:section text:name="CalculatedVariable-{$nameOutVariable}">
			<text:p text:style-name="CalculatedVariableTitle">
				<xsl:value-of
					select="concat('Calcul de la variable ', $nameOutVariable, ' Label : [', $nameOutVariable, ']')"
				/>
			</text:p>
			<text:p text:style-name="CalculatedVariableContent">
				<xsl:value-of select="concat('Formule de calcul : ', $nameOutVariable, ' = ')"/>
				<xsl:call-template name="replaceVariablesInFormula">
					<xsl:with-param name="formula" select="$variableCalculation"/>
					<xsl:with-param name="variables" select="$idVariables"/>
				</xsl:call-template>
			</text:p>
			
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</text:section>
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
		
		<!--<xsl:variable name="name" select="enofodt:get-label-conditioner($source-context,$languages[1])"/>-->
		<xsl:variable name="nameOfControl"
			select="enofodt:get-check-name($source-context, $languages)"/>
		<xsl:variable name="control" select="enofodt:get-constraint($source-context)"/>
		<xsl:variable name="instructionFormat" select="enofodt:get-css-class($source-context)"/>
		<xsl:variable name="vars"
			select="enofodt:get-label-conditioning-variables($source-context, $languages)"/>
		<xsl:variable name="instructionLabel">
			<xsl:call-template name="replaceVariablesInFormula">
				<xsl:with-param name="formula"
					select="enofodt:get-label($source-context, $languages)"/>
				<xsl:with-param name="variables" select="$vars"/>
			</xsl:call-template>
		</xsl:variable>
		<text:section text:name="ConsistencyCheck-{enofodt:get-name($source-context)}">
			<xsl:if test="$control != ''">
				<text:p text:style-name="Control">
					<xsl:value-of select="concat('Contrôle bloquant : ', $nameOfControl)"/>
				</text:p>
				<xsl:variable name="idVariables"
					select="tokenize(enofodt:get-control-variables($source-context), '\s')"/>
				<text:p text:style-name="Control">
					<xsl:value-of select="'Expression du contrôle : '"/>
					<xsl:call-template name="replaceVariablesInFormula">
						<xsl:with-param name="formula" select="$control"/>
						<xsl:with-param name="variables" select="$idVariables"/>
					</xsl:call-template>
				</text:p>
			</xsl:if>
			
			<xsl:choose>
				<xsl:when test="$instructionFormat = ''">
					<text:p text:style-name="Warning">
						<xsl:value-of
							select="concat('Message d', '''', 'erreur : ', $instructionLabel)"/>
					</text:p>
				</xsl:when>
				<xsl:when test="$instructionFormat = 'hint'">
					<text:p text:style-name="Instruction">
						<xsl:value-of select="$instructionLabel"/>
					</text:p>
				</xsl:when>
				<xsl:when test="$instructionFormat = 'help'">
					<text:p text:style-name="Help">
						<xsl:value-of select="$instructionLabel"/>
					</text:p>
				</xsl:when>
			</xsl:choose>
			
			<!-- Go to the Calculated Variable -->
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</text:section>
	</xsl:template>
	
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
			<xsl:when test="count($variables) = 0">
				<xsl:value-of select="$formula"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="regexA"
					select="concat('number\(if\s+\(', $conditioning-variable-begin, $variables[1], $conditioning-variable-end, '=''''\)\sthen\s+''0''\s+else\s+', $conditioning-variable-begin, $variables[1], $conditioning-variable-end, '\)')"/>
				<xsl:variable name="regexB"
					select="concat($conditioning-variable-begin, $variables[1], $conditioning-variable-end)"/>
				<xsl:variable name="expressionToReplace"
					select="concat('^', enofodt:get-variable-business-name($source-context, $variables[1]))"/>
				<xsl:variable name="newFormula"
					select="
					replace(replace($formula,
					$regexA, $expressionToReplace),
					$regexB, $expressionToReplace)"/>
				
				<xsl:call-template name="replaceVariablesInFormula">
					<xsl:with-param name="formula" select="$newFormula"/>
					<xsl:with-param name="variables" select="$variables[position() &gt; 1]"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Template named:eno:printQuestionTitleWithInstruction.</xd:p>
			<xd:p>It prints the question label and its instructions.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template name="eno:printQuestionTitleWithInstruction">
		<xsl:param name="driver" tunnel="no"/>
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:apply-templates select="enofodt:get-before-question-title-instructions($source-context)"
			mode="source">
			<xsl:with-param name="driver" select="$driver"/>
		</xsl:apply-templates>
		<xsl:variable name="questionContent" select="enofodt:get-question-label($source-context, $languages[1])"/>
		<xsl:if test="$questionContent != ''">
			<xsl:choose>
				<xsl:when test="exists($questionContent//xhtml:span[@class='block'])">
					<xsl:for-each select="$questionContent//xhtml:span[@class='block']">
						<text:p text:style-name="Question">
							<xsl:apply-templates select="."
								mode="enofodt:format-label">
								<xsl:with-param name="label-variables"
									select="enofodt:get-label-conditioning-variables($source-context, $languages[1])"
									tunnel="yes"/>
							</xsl:apply-templates>
						</text:p>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<text:p text:style-name="Question">
						<xsl:value-of select="enofodt:get-label($source-context, $languages[1])"/>
					</text:p>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<!-- The enoddi:get-instructions-by-format getter produces in-language fragments, on which templates must be applied in "source" mode. -->
		<xsl:apply-templates select="enofodt:get-after-question-title-instructions($source-context)"
			mode="source">
			<xsl:with-param name="driver" select="$driver"/>
		</xsl:apply-templates>
	</xsl:template>
	

	
	<xd:doc>
		<xd:desc>Template for (Question) Loops</xd:desc>
	</xd:doc>
	<xsl:template match="QuestionLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="label" select="enofodt:get-label($source-context, $languages[1])"/>
		<xsl:variable name="descendantLoop" select="enofodt:get-descendant-loop-ids($source-context)"/>
		<xsl:variable name="descendantModules" select="enofodt:get-descendant-module-names($source-context,$languages[1])"/>
		<!--	The typeOfLoop variable is useful to differentiate the behaviour whether the loop contains multiple modules (in which case we want to skip pages) or not
		The test in xsl:when is meant to check if there are multiple modules in the loop by counting the number of spaces in the variable descendantModules -->
		<xsl:variable name="typeOfLoop">	
			<xsl:choose>
				<xsl:when test="count($descendantModules) > 1">
					<xsl:value-of select="'MultiModuleLoop'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="string-length($descendantModules) > 1"><xsl:value-of select="'OnlyModuleLoop'"/></xsl:when>
						<xsl:otherwise><xsl:value-of select="'InsideModuleLoop'"/></xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
			<text:section text:name="Loop-{enofodt:get-name($source-context)}">

				<text:p text:style-name="{concat($typeOfLoop,'Start')}">
					<text:span text:style-name="LoopIdentifier">
						<xsl:value-of select="'Début de la boucle'"/>
<!--						<xsl:copy-of select="enofodt:get-name($source-context)"/>-->
<!--						<xsl:value-of select="']'"/>-->
					</text:span>
				</text:p>
				
				<xsl:if test="$label!=''">
					<text:p text:style-name="LoopStandard">
						<text:span text:style-name="LoopInfo">
							<xsl:value-of select="'Nom du bouton d''ajout : '"/>
							<xsl:copy-of select="$label"/>
						</text:span>
					</text:p>
				</xsl:if>
				
				<xsl:if test="enofodt:get-minimum-occurrences($source-context)!=''">
					<text:p text:style-name="LoopStandard">
						<text:span text:style-name="LoopInfo">
							<xsl:value-of select="'Nombre d''occurrences minimum : '"/>
						</text:span>
						<xsl:call-template name="replaceVariablesInFormula">
							<xsl:with-param name="formula" select="enofodt:get-minimum-occurrences($source-context)"/>
							<xsl:with-param name="variables" select="enofodt:get-minimum-occurrences-variables($source-context)"/>
						</xsl:call-template>
					</text:p>
				</xsl:if>
				
				<xsl:if test="enofodt:get-maximum-occurrences($source-context)!=''">
					<text:p text:style-name="LoopStandard">
						<text:span text:style-name="LoopInfo">
							<xsl:value-of select="'Nombre d''occurrences maximum : '"/>
						</text:span>
						<xsl:call-template name="replaceVariablesInFormula">
							<xsl:with-param name="formula" select="enofodt:get-maximum-occurrences($source-context)"/>
							<xsl:with-param name="variables" select="enofodt:get-maximum-occurrences-variables($source-context)"/>
						</xsl:call-template>
					</text:p>
				</xsl:if>
				
				<xsl:if test="enofodt:get-loop-filter($source-context)!=''">
					<text:p text:style-name="LoopStandard">
						<text:span text:style-name="LoopInfo">
							<xsl:value-of select="'Condition de la boucle: '"/>
						</text:span>
						<xsl:call-template name="replaceVariablesInFormula">
							<xsl:with-param name="formula" select="enofodt:get-loop-filter($source-context)"/>
							<xsl:with-param name="variables" select="enofodt:get-loop-filter-variables($source-context)"/>
						</xsl:call-template>
					</text:p>
				</xsl:if>
				
				<xsl:if test="$descendantModules != ''">
					<text:p text:style-name="LoopStandard">
						<text:span text:style-name="LoopInfo">
							<xsl:value-of select="'Séquences à l''intérieur de la boucle : '"/>
						</text:span>
					</text:p>
					
					<xsl:for-each select="$descendantModules">
						<text:p text:style-name="LoopStandard">
							<text:span text:style-name="LoopInfo">
								<xsl:copy-of select="."/>
							</text:span>
						</text:p>
					</xsl:for-each>
				</xsl:if>
				
				<xsl:if test="count(tokenize($descendantLoop,' ')) >= 1">
					<text:p text:style-name="LoopStandard">
						<text:span text:style-name="LoopInfo">
							<xsl:value-of select="'Boucles à l''intérieur de la boucle : '"/>
						</text:span>
					</text:p>
					
					<xsl:for-each select="tokenize($descendantLoop,' ')">
						<text:p text:style-name="LoopStandard">
							<text:span text:style-name="LoopInfo">
								<xsl:copy-of select="."/>
							</text:span>
						</text:p>
					</xsl:for-each>
				</xsl:if>
				
				<text:p text:style-name="{concat($typeOfLoop,'Type')}">
					<text:span text:style-name="Standard">
						<xsl:choose>
							<xsl:when test="$typeOfLoop='MultiModuleLoop'">
								<xsl:value-of select="'Type de boucle : multi-module avec '"/>
								<xsl:value-of select="count($descendantModules)"/>
								<xsl:value-of select="' modules'"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="$typeOfLoop='OnlyModuleLoop'"><xsl:value-of select="'Type de boucle : un module'"/></xsl:when>
									<xsl:otherwise><xsl:value-of select="'Type de boucle : à l''intérieur d''un module'"/></xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</text:span>
				</text:p>
				
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
					
				<text:p text:style-name="{concat($typeOfLoop,'End')}">
					<text:span text:style-name="LoopIdentifier">
						<xsl:value-of select="'Fin de la boucle'"/>
<!--						<xsl:copy-of select="enofodt:get-name($source-context)"/>-->
<!--						<xsl:value-of select="']'"/>-->
					</text:span>
				</text:p>
				
			</text:section>
		
		

	</xsl:template>
	
	
	
	
</xsl:stylesheet>
