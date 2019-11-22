<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
	xmlns:enoodt="http://xml.insee.fr/apps/eno/out/odt"
	xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
	xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
	xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
	xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
	xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
	xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
	exclude-result-prefixes="xs fn xd eno enoodt" version="2.0">
	
	<xsl:import href="../../../styles/style.xsl"/>
	<xsl:include href="../../../xslt/outputs/odt/office-styles.xsl"/>
	
	
	<xd:doc scope="stylesheet">
		<xd:desc>
			<xd:p>An xslt stylesheet who transforms an input into Odt through generic driver
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
		<xsl:variable name="languages" select="enoodt:get-form-languages($source-context)"
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
						<xsl:value-of select="enoodt:get-label($source-context, $languages[1])"/>
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
		<text:section text:name="Module-{enoodt:get-name($source-context)}">
			<text:p text:style-name="Module">
				<xsl:value-of select="enoodt:get-label($source-context, $languages[1])"/>
			</text:p>
			
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
		<text:section text:name="SubModule-{enoodt:get-name($source-context)}">
			<text:p text:style-name="SubModule">
				<xsl:value-of select="enoodt:get-label($source-context, $languages[1])"/>
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
			select="enoodt:get-question-name($source-context, $languages[1])"/>
		<text:section text:name="Question-{enoodt:get-name($source-context)}">
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
			
			<xsl:apply-templates select="enoodt:get-end-question-instructions($source-context)"
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
			select="enoodt:get-number-of-decimals($source-context)"/>
		<xsl:variable name="minimumResponse" select="enoodt:get-minimum($source-context)"/>
		<xsl:variable name="maximumResponse" select="enoodt:get-maximum($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enoodt:get-business-name($source-context)"/>
		
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
		<xsl:variable name="lengthResponse" select="enoodt:get-length($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enoodt:get-business-name($source-context)"/>
		
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
		<xsl:variable name="dateFormat" select="enoodt:get-format($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enoodt:get-business-name($source-context)"/>
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
		<xsl:variable name="lengthResponse" select="enoodt:get-length($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enoodt:get-business-name($source-context)"/>
		
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
		<xsl:variable name="nameOfVariable" select="enoodt:get-business-name($source-context)"/>
		
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
			select="enoodt:get-code-maximum-length($source-context)"/>
		<xsl:variable name="nameOfVariable" select="enoodt:get-business-name($source-context)"/>
		
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
			select="enoodt:get-question-name($source-context, $languages[1])"/>
		<xsl:variable name="maximumLengthCode"
			select="enoodt:get-code-maximum-length($source-context)"/>
		<xsl:variable name="headerCol" select="enoodt:get-body-line($source-context, position())"/>
		<xsl:variable name="type" select="enoodt:get-css-class($source-context)"/>
		<text:section text:name="Table-{enoodt:get-name($source-context)}">
			<xsl:if test="$questionName != ''">
				<text:p text:style-name="QuestionName">
					<xsl:value-of select="concat('[', $questionName, ']')"/>
				</text:p>
			</xsl:if>
			
			<!-- print the question label and its instructions -->
			<xsl:call-template name="eno:printQuestionTitleWithInstruction">
				<xsl:with-param name="driver" select="."/>
			</xsl:call-template>
			
			<table:table table:name="{enoodt:get-name($source-context)}" table:style-name="Table">
				<xsl:for-each select="$headerCol">
					<table:table-column table:style-name="Table.Column"/>
				</xsl:for-each>
				<!--    Header   -->
				<xsl:for-each select="enoodt:get-header-lines($source-context)">
					<table:table-row>
						<xsl:apply-templates
							select="enoodt:get-header-line($source-context, position())"
							mode="source">
							<xsl:with-param name="ancestorTable" select="'headerLine'" tunnel="yes"
							/>
						</xsl:apply-templates>
					</table:table-row>
				</xsl:for-each>
				<!--   Body    -->
				<xsl:for-each select="enoodt:get-body-lines($source-context)">
					<table:table-row>
						<xsl:apply-templates
							select="enoodt:get-body-line($source-context, position())" mode="source">
							<xsl:with-param name="ancestorTable" select="'line'" tunnel="yes"/>
							<xsl:with-param name="typeOfAncestor" select="$type" tunnel="yes"/>
						</xsl:apply-templates>
					</table:table-row>
				</xsl:for-each>
			</table:table>
			
			<xsl:variable name="nbMaximumLines" select="enoodt:get-maximum-lines($source-context)"/>
			<xsl:variable name="nbMinimumLines" select="enoodt:get-minimum-lines($source-context)"/>
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
		<xsl:variable name="col-span" select="number(enoodt:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enoodt:get-rowspan($source-context))"/>
		
		
		<xsl:if test="$ancestorTable != ''">
			<table:table-cell table:number-rows-spanned="{$row-span}"
				table:number-columns-spanned="{$col-span}" table:style-name="Table.Cell">
				<xsl:variable name="label" select="enoodt:get-label($source-context, $languages)"/>
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
		<xsl:variable name="col-span" select="number(enoodt:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enoodt:get-rowspan($source-context))"/>
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
		<xsl:variable name="col-span" select="number(enoodt:get-colspan($source-context))"/>
		<xsl:variable name="row-span" select="number(enoodt:get-rowspan($source-context))"/>
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
		<xsl:variable name="label" select="enoodt:get-label($source-context, $languages[1])"/>
		
		<!-- remove item in the cell for table when the response is boolean-->
		<xsl:if test="$label != '' and not(ancestor::BooleanDomain)">
			<text:p text:style-name="CodeItem">
				<xsl:value-of select="fn:concat(enoodt:get-value($source-context), ' - ', $label)"/>
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
		
		<xsl:variable name="instructionFormat" select="enoodt:get-format($source-context)"/>
		<xsl:variable name="instructionLabel"
			select="enoodt:get-label($source-context, $languages[1])"/>
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
		<xsl:variable name="filter" select="enoodt:get-relevant($source-context)"/>
		<xsl:variable name="idVariables"
			select="tokenize(enoodt:get-hideable-command-variables($source-context), '\s')"/>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>template for the GoTo</xd:desc>
	</xd:doc>
	<xsl:template match="GoTo" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="label" select="enoodt:get-label($source-context, $languages[1])"
			as="node()"/>
		<xsl:variable name="nameOfVariable" select="enoodt:get-flowcontrol-target($source-context)"/>
		
		<xsl:if test="$label != ''">
			<text:section text:name="GoTo-{enoodt:get-name($source-context)}">
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
						<xsl:value-of select="enoodt:get-flowcontrol-condition($source-context)"/>
					</text:span>
				</text:p>
				
				<text:p text:style-name="Format">
					<text:span text:style-name="GotoTitle">
						<xsl:value-of select="'Cible : '"/>
					</text:span>
					<text:span text:style-name="NameOfVariable">
						<xsl:value-of select="concat('[', $nameOfVariable, ']')"/>
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
			select="enoodt:get-variable-calculation($source-context)"/>
		
		<!--<xsl:variable name="outVariable" select="enoodt:get-name($source-context)"/>-->
		<xsl:variable name="nameOutVariable" select="enoodt:get-business-name($source-context)"/>
		<xsl:variable name="idVariables"
			select="tokenize(enoodt:get-variable-calculation-variables($source-context), '\s')"/>
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
		
		<!--<xsl:variable name="name" select="enoodt:get-label-conditioner($source-context,$languages[1])"/>-->
		<xsl:variable name="nameOfControl"
			select="enoodt:get-check-name($source-context, $languages)"/>
		<xsl:variable name="control" select="enoodt:get-constraint($source-context)"/>
		<xsl:variable name="instructionFormat" select="enoodt:get-css-class($source-context)"/>
		<xsl:variable name="vars"
			select="enoodt:get-label-conditioning-variables($source-context, $languages)"/>
		<xsl:variable name="instructionLabel">
			<xsl:call-template name="replaceVariablesInFormula">
				<xsl:with-param name="formula"
					select="enoodt:get-label($source-context, $languages)"/>
				<xsl:with-param name="variables" select="$vars"/>
			</xsl:call-template>
		</xsl:variable>
		<text:section text:name="ConsistencyCheck-{enoodt:get-name($source-context)}">
			<xsl:if test="$control != ''">
				<text:p text:style-name="Control">
					<xsl:value-of select="concat('Contrôle bloquant : ', $nameOfControl)"/>
				</text:p>
				<xsl:variable name="idVariables"
					select="tokenize(enoodt:get-control-variables($source-context), '\s')"/>
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
					select="concat('^', enoodt:get-variable-business-name($source-context, $variables[1]))"/>
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
		
		<xsl:apply-templates select="enoodt:get-before-question-title-instructions($source-context)"
			mode="source">
			<xsl:with-param name="driver" select="$driver"/>
		</xsl:apply-templates>
		<xsl:if test="enoodt:get-label($source-context, $languages[1]) != ''">
			<text:p text:style-name="Question">
				<xsl:value-of select="enoodt:get-label($source-context, $languages[1])"/>
			</text:p>
		</xsl:if>
		<!-- The enoddi:get-instructions-by-format getter produces in-language fragments, on which templates must be applied in "source" mode. -->
		<xsl:apply-templates select="enoodt:get-after-question-title-instructions($source-context)"
			mode="source">
			<xsl:with-param name="driver" select="$driver"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>
