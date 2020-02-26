<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
	exclude-result-prefixes="xd xs eno enopdf fox"
	version="2.0">

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
		
	<xsl:variable name="page-model-default" select="doc('../../../xslt/post-processing/pdf/page-model/page-model-default.fo')"/>
	
	<xsl:include href="../../../styles/style.xsl"/>
	
	<!-- Remove all the ConsistencyCheck messages from the pdf -->
	<xsl:template match="main//ConsistencyCheck" mode="model"/>
	
	
	<xd:doc>
		<xd:desc>root template : main sequence = the questionnaire</xd:desc>
	</xd:doc>
	<xsl:template match="Form" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)" as="xs:string +"/>
		<xsl:variable name="survey-name" select="enopdf:get-label($source-context, $languages[1])"/>
		
		<fo:root>
			<xsl:copy-of select="$page-model-default//fo:layout-master-set"/>
			<fo:page-sequence master-reference="A4" initial-page-number="2" force-page-count="odd">
				<fo:title><xsl:value-of select="$survey-name"/></fo:title>
				<xsl:copy-of select="$page-model-default//fo:static-content"/>
				<fo:flow flow-name="xsl-region-body" border-collapse="collapse" font-size="10pt">
					<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
						<xsl:with-param name="driver" select="eno:append-empty-element('main', .)" tunnel="yes"/>
						<xsl:with-param name="languages" select="$languages" tunnel="yes"/>
					</xsl:apply-templates>
					<fo:block id="TheVeryLastPage"/>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xd:doc>
		<xd:desc>default template : do nothing ; call children</xd:desc>
	</xd:doc>
	<xsl:template match="*" mode="model" priority="-1">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:desc>Module sequence template</xd:desc>
	</xd:doc>
	<xsl:template match="main//Module" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<fo:block xsl:use-attribute-sets="Titre-sequence" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
			<xsl:if test="lower-case($page-break-between) = 'module' or lower-case($page-break-between) = 'submodule'">
				<xsl:attribute name="page-break-before" select="'always'"/>
			</xsl:if>
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:desc>SubModule sequence template</xd:desc>
	</xd:doc>
	<xsl:template match="main//SubModule" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<fo:block xsl:use-attribute-sets="Titre-paragraphe" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always"> <!-- linefeed-treatment="preserve" -->
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		<xsl:if test="lower-case($page-break-between) = 'submodule'">
			<fo:block page-break-after="always"> </fo:block>
		</xsl:if>
	</xsl:template>

	<xd:doc>
		<xd:desc>template for the filters</xd:desc>
	</xd:doc>
	<xsl:template match="main//xf-group" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>

		<xsl:variable name="label" select="enopdf:get-flowcontrol-label($source-context,$languages[1])"/>
		<xsl:if test="$label != ''">
			<fo:block page-break-inside="avoid" keep-with-previous="always" xsl:use-attribute-sets="filter-block">
				<fo:inline-container start-indent="0%" end-indent="0%" width="9%" vertical-align="middle">
					<fo:block margin="2pt">
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'filter_arrow.png'"/>
						</xsl:call-template>
					</fo:block>
				</fo:inline-container>
				<fo:inline-container xsl:use-attribute-sets="filter-inline-container">
					<fo:block xsl:use-attribute-sets="filter-alternative">
						<xsl:copy-of select="$label"/>
					</fo:block>
				</fo:inline-container>
			</fo:block>
		</xsl:if>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>template for the Clarification of a response</xd:desc>
	</xd:doc>
	<xsl:template match="main//Clarification" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
			<xsl:with-param name="other-give-details" select="true()" tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:desc>template for the instructions</xd:desc>
	</xd:doc>
	<xsl:template match="main//xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>

		<xsl:variable name="format" select="normalize-space(enopdf:get-format($source-context))"/>
		<xsl:variable name="label" select="enopdf:get-label($source-context, $languages[1])" as="node()"/>
		<xsl:choose>
			<xsl:when test="$format = 'footnote'">
				<fo:block>
					<fo:footnote>
						<fo:inline></fo:inline>
						<fo:footnote-body xsl:use-attribute-sets="footnote">
							<fo:block>
								<fo:inline font-size="75%" baseline-shift="super">
									<xsl:copy-of select="enopdf:get-end-question-instructions-index($source-context)"/>
								</fo:inline>
								<xsl:copy-of select="$label"/>
							</fo:block>
						</fo:footnote-body>
					</fo:footnote>
				</fo:block>
			</xsl:when>
			<xsl:when test="$format = 'tooltip'">
			</xsl:when>
			<xsl:when test="$format = 'comment' or $format = 'help' or $format = 'instruction'">
				<fo:block xsl:use-attribute-sets="instruction" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
					<xsl:copy-of select="$label"/>
				</fo:block>
			</xsl:when>
			<xsl:when test="$format = 'statement'">
				<fo:block xsl:use-attribute-sets="statement" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
					<xsl:copy-of select="$label"/>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message select="concat('unknown xf-output : ',enopdf:get-name($source-context),$label)"/>
				<fo:block xsl:use-attribute-sets="general-style" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
					<xsl:if test="$isTable = 'YES'">
						<xsl:attribute name="margin-left">1mm</xsl:attribute>
					</xsl:if>
					<xsl:copy-of select="$label"/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:desc>template for the GoTo</xd:desc>
	</xd:doc>
	<xsl:template match="main//GoTo" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="label" select="enopdf:get-label($source-context, $languages[1])" as="node()"/>

		<xsl:if test="$label != ''">
			<fo:block page-break-inside="avoid" keep-with-previous="always" xsl:use-attribute-sets="filter-block">
				<fo:inline-container start-indent="0%" end-indent="0%" width="9%" vertical-align="middle">
					<fo:block margin="2pt">
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'filter_arrow.png'"/>
						</xsl:call-template>
					</fo:block>
				</fo:inline-container>
				<fo:inline-container xsl:use-attribute-sets="filter-inline-container">
					<fo:block xsl:use-attribute-sets="filter-alternative">
						<xsl:copy-of select="$label"/>
					</fo:block>
				</fo:inline-container>
			</fo:block>
		</xsl:if>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- QUESTIONS -->
	<xd:doc>
		<xd:desc>Questions with responses which are not in a table</xd:desc>
	</xd:doc>
	<xsl:template match="main//SingleResponseQuestion | main//MultipleQuestion | main//MultipleChoiceQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="other-give-details" tunnel="yes" select="false()"/>

		<!--<xsl:apply-templates select="enopdf:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>-->
		<xsl:choose>
			<xsl:when test="$other-give-details">
				<fo:block xsl:use-attribute-sets="details" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
					<fo:inline>
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'arrow_details.png'"/>
						</xsl:call-template>
						<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
					</fo:inline>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
					<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="enopdf:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>
		<fo:block id="{enopdf:get-question-name($source-context,$languages[1])}" page-break-inside="avoid">
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="typeOfAncestor" select="'question'" tunnel="yes"/>
			</xsl:apply-templates>
		</fo:block>
		<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Déclenche tous les Table de l'arbre des drivers -->
	<xsl:template match="main//Table | main//TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="current-match" select="."/>
		<xsl:variable name="no-border" select="enopdf:get-style($source-context)"/>
		<xsl:variable name="table-type" select="local-name()"/>
		<xsl:variable name="total-lines" as="xs:integer">
			<xsl:choose>
				<xsl:when test="$table-type = 'Table'">
					<xsl:value-of select="count(enopdf:get-body-lines($source-context))"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="number($roster-defaultsize)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="maxlines-by-page" as="xs:integer">
			<xsl:value-of select="number($table-defaultsize)"/>
		</xsl:variable>
		<!-- The table in the first page contains 1 line less than next ones -->
		<xsl:variable name="table-pages" select="xs:integer(1+(($total-lines -1+1) div $maxlines-by-page))" as="xs:integer"/>
		
		<!--<xsl:apply-templates select="enopdf:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>-->
		<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<xsl:apply-templates select="enopdf:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>

		<!-- long tables are split : $maxlines-by-page lines maximum, except the first one which has 1 less -->
		<xsl:for-each select="1 to $table-pages">
			<xsl:variable name="page-position" select="position()"/>
			<fo:block page-break-inside="avoid">
				<xsl:attribute name="id">
					<xsl:choose>
						<xsl:when test="$table-type = 'Table'">
							<xsl:value-of select="enopdf:get-question-name($source-context,$languages[1])"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="enopdf:get-business-name($source-context)"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="$total-lines &gt; $maxlines-by-page -1">
						<xsl:choose>
							<!-- For TableLoop, "-" character will be used to identify pages which will have the same input mask -->
							<!-- For Table, input masks of page 2 and page 3 will be different -->
							<xsl:when test="$table-type = 'Table'">
								<xsl:value-of select="'0'"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'-'"/>	
							</xsl:otherwise>
						</xsl:choose>
						<xsl:value-of select="$page-position"/>
					</xsl:if>
				</xsl:attribute>
				<xsl:if test="$current-match/name()='TableLoop' and $total-lines &gt; $maxlines-by-page -1">
					<xsl:attribute name="page-break-after" select="'always'"/>
				</xsl:if>
				<fo:table inline-progression-dimension="auto" table-layout="fixed" width="100%" font-size="10pt" border-width="0.35mm"
					text-align="center" margin-top="1mm" display-align="center" space-after="5mm">
					<xsl:if test="count(enopdf:get-header-lines($source-context)) != 0">
						<fo:table-header>
							<xsl:for-each select="enopdf:get-header-lines($source-context)">
								<fo:table-row xsl:use-attribute-sets="entete-ligne" text-align="center">
									<xsl:apply-templates select="enopdf:get-header-line($source-context, position())" mode="source">
										<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
										<xsl:with-param name="header" select="'YES'" tunnel="yes"/>
										<xsl:with-param name="no-border" select="$no-border" tunnel="yes"/>
									</xsl:apply-templates>
								</fo:table-row>
							</xsl:for-each>
						</fo:table-header>
					</xsl:if>
					<fo:table-body>
						<xsl:choose>
							<xsl:when test="$current-match/name()='Table'">
								<xsl:variable name="first-line" select="$maxlines-by-page*($page-position -1)"/>
								<xsl:variable name="last-line" select="$maxlines-by-page*($page-position) -1"/>
								<xsl:for-each select="enopdf:get-body-lines($source-context)">
									<xsl:variable name="position" select="position()"/>
									<!-- page 1 starts at line 0, so contains 1 line less than next ones -->
									<xsl:if test="($position &gt;= $first-line) and ($position &lt;= $last-line)">
										<fo:table-row border-color="black">
											<xsl:apply-templates select="enopdf:get-body-line($source-context, position(),$first-line)" mode="source">
												<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
												<xsl:with-param name="table-first-line" select="$first-line" tunnel="yes"/>
												<xsl:with-param name="table-last-line" select="$last-line" tunnel="yes"/>
												<xsl:with-param name="isTable" select="'YES'" tunnel="yes"/>
												<xsl:with-param name="row-number" select="position()" tunnel="yes"/>
												<xsl:with-param name="no-border" select="$no-border" tunnel="yes"/>
											</xsl:apply-templates>
										</fo:table-row>
									</xsl:if>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<xsl:for-each select="1 to $maxlines-by-page">
									<!-- if the dynamic table is on several pages, each page contains maxlines-by-page, except the first one, which has maxlines-by-page -1 -->
									<xsl:if test="$page-position &gt; 1 or (. &lt;= $total-lines and . &lt; $maxlines-by-page)">
										<!-- in a dynamic table, a repeated "line" may be on several get-body-lines -->
										<xsl:for-each select="enopdf:get-body-lines($source-context)">
											<xsl:variable name="position" select="position()"/>
											<fo:table-row border-color="black">
												<xsl:apply-templates select="enopdf:get-body-line($source-context, $position)" mode="source">
													<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
													<xsl:with-param name="no-border" select="$no-border" tunnel="yes"/>
												</xsl:apply-templates>
											</fo:table-row>
										</xsl:for-each>
									</xsl:if>
								</xsl:for-each>
							</xsl:otherwise>
						</xsl:choose>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</xsl:for-each>
		<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Déclenche tous les TextCell de l'arbre des drivers -->
	<xsl:template match="main//TextCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="header" tunnel="yes"/>
		<xsl:param name="row-number" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="table-first-line" tunnel="yes"/>
		<xsl:param name="table-last-line" tunnel="yes"/>

		<fo:table-cell xsl:use-attribute-sets="colonne-tableau"
			number-rows-spanned="{enopdf:get-rowspan($source-context,$table-first-line,$table-last-line)}"
			number-columns-spanned="{enopdf:get-colspan($source-context)}">
			<xsl:if test="$header">
				<xsl:attribute name="text-align">center</xsl:attribute>
			</xsl:if>
			<xsl:if test="$no-border = 'no-border'">
				<xsl:attribute name="border" select="'0mm'"/>
				<xsl:attribute name="padding" select="'0mm'"/>
			</xsl:if>
			<fo:block xsl:use-attribute-sets="general-style">
				<xsl:if test="not($header)">
					<xsl:attribute name="margin-left" select="'1mm'"/>
				</xsl:if>
				<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<!-- Déclenche tous les Cell de l'arbre des drivers -->
	<xsl:template match="main//Cell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>

		<fo:table-cell xsl:use-attribute-sets="data-cell">
			<xsl:if test="$no-border = 'no-border'">
				<xsl:attribute name="border">0mm</xsl:attribute>
				<xsl:attribute name="padding-top">0mm</xsl:attribute>
				<xsl:attribute name="padding-bottom">0mm</xsl:attribute>
			</xsl:if>
			<fo:block>
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
					<xsl:with-param name="isTable" select="'YES'" tunnel="yes"/>
					<xsl:with-param name="no-border" select="$no-border" tunnel="yes"/>
				</xsl:apply-templates>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xd:doc>
		<xd:desc>No other - give details out of cells</xd:desc>
	</xd:doc>
	<xsl:template match="xf-group[(ancestor::Table or ancestor::TableLoop) and not(ancestor::Cell)]" mode="model" priority="2"/>

	<!-- Déclenche tous les EmptyCell de l'arbre des drivers -->
	<xsl:template match="main//EmptyCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<fo:table-cell background-color="#CCCCCC" border-color="black" border-style="solid"
			number-columns-spanned="{enopdf:get-colspan($source-context)}"
			number-rows-spanned="{enopdf:get-rowspan($source-context)}">
			<fo:block/>
		</fo:table-cell>
	</xsl:template>
	
	<!-- Déclenche tous les FixedCell de l'arbre des drivers -->
	<xsl:template match="main//FixedCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<fo:table-cell background-color="#CCCCCC" border-color="black" border-style="solid"
			number-columns-spanned="{enopdf:get-colspan($source-context)}"
			number-rows-spanned="{enopdf:get-rowspan($source-context)}">
			<fo:block>
				<xsl:sequence select="enopdf:get-label($source-context, $languages[1])"/>
				<xsl:sequence select="enopdf:get-fixed-value($source-context, $languages[1])"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<!-- 	REPONSES -->

	<!-- external variables : do nothing -->
	<xsl:template match="main//ResponseElement" mode="model"/>

	<!-- Déclenche tous les TextareaDomain de l'arbre des drivers -->
	<xsl:template match="main//TextareaDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		
		<xsl:variable name="height" select="8*number($textarea-defaultsize)"/>
		<xsl:choose>
			<xsl:when test="$isTable = 'YES'">
				<fo:block-container height="{$height}mm">
					<fo:block>&#160;</fo:block>	
				</fo:block-container>
			</xsl:when>
			<xsl:otherwise>
				<fo:block-container height="{$height}mm" border-color="black" border-style="solid">
					<fo:block>&#160;</fo:block>	
				</fo:block-container>
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Déclenche tous les TextDomain : REPONSES QUI DOIVENT ETRE RENSEIGNEES DANS LE QUESTIONNAIRE-->
	<xsl:template match="main//TextDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="other-give-details" tunnel="yes" select="false()"/>
		
		<xsl:variable name="length" select="enopdf:get-length($source-context)"/>
		
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			<xsl:choose>
				<xsl:when test="$other-give-details">
					<fo:block xsl:use-attribute-sets="details" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<fo:inline>
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'arrow_details.png'"/>
							</xsl:call-template>
							<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
						</fo:inline>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<fo:block>
			<xsl:choose>
				<xsl:when test="enopdf:get-format($source-context) or ($length !='' and number($length) &lt;= 20)">
					<fo:block xsl:use-attribute-sets="general-style">
						<xsl:for-each select="1 to xs:integer(number($length))">
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'mask_number.png'"/>
							</xsl:call-template>
						</xsl:for-each>
					</fo:block>
				</xsl:when>
				<xsl:when test="$no-border = 'no-border'">
					<fo:block-container height="8mm" width="50mm">
						<fo:block border-color="black" border-style="solid" width="50mm">&#160;</fo:block>
					</fo:block-container>
				</xsl:when>
				<xsl:when test="$isTable = 'YES'">
					<fo:block-container height="8mm" width="50mm">
						<fo:block>&#160;</fo:block>	
					</fo:block-container>
				</xsl:when>
				<xsl:otherwise>
					<fo:block-container height="8mm" border-color="black" border-style="solid" width="100%">
						<fo:block>&#160;</fo:block>
					</fo:block-container>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Déclenche tous les NumericDomain : REPONSES QUI DOIVENT ETRE RENSEIGNEES DANS LE QUESTIONNAIRE-->
	<xsl:template match="main//NumericDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="other-give-details" tunnel="yes" select="false()"/>
		
		<xsl:variable name="length" select="number(enopdf:get-length($source-context))"/>
		
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			<xsl:choose>
				<xsl:when test="$other-give-details">
					<fo:block xsl:use-attribute-sets="details" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<fo:inline>
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'arrow_details.png'"/>
							</xsl:call-template>
							<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
						</fo:inline>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<fo:block>
			<xsl:if test="$isTable = 'YES'">
				<xsl:attribute name="text-align">right</xsl:attribute>
				<xsl:attribute name="padding-top">0mm</xsl:attribute>
				<xsl:attribute name="padding-bottom">0mm</xsl:attribute>
			</xsl:if>
			<fo:block xsl:use-attribute-sets="general-style" padding-bottom="0mm" padding-top="0mm">
				<xsl:choose>
					<xsl:when test="$numeric-capture = 'optical'">
						<xsl:variable name="separator-position">
							<xsl:choose>
								<xsl:when test="enopdf:get-number-of-decimals($source-context) != '0'">
									<xsl:value-of select="string($length - number(enopdf:get-number-of-decimals($source-context)))"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="'0'"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:for-each select="1 to xs:integer($length)">
							<xsl:choose>
								<xsl:when test="$separator-position = .">
									<fo:inline> , </fo:inline>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="insert-image">
										<xsl:with-param name="image-name" select="'mask_number.png'"/>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="width-coefficient" as="xs:integer">
							<xsl:choose>
								<xsl:when test="not($isTable = 'YES') or ($no-border = 'no-border')">
									<xsl:value-of select="4"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="3"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<fo:inline-container>
							<xsl:attribute name="width" select="concat(string($length*$width-coefficient),'mm')"/>
							<fo:block-container height="8mm">
								<xsl:attribute name="width" select="concat(string($length*$width-coefficient),'mm')"/>
								<xsl:if test="not($isTable = 'YES') or ($no-border = 'no-border')">
									<xsl:attribute name="border-color" select="'black'"/>
									<xsl:attribute name="border-style" select="'solid'"/>
								</xsl:if>
								<fo:block>
									&#160;
								</fo:block>										
							</fo:block-container>
						</fo:inline-container>
					</xsl:otherwise>
				</xsl:choose>
				<fo:inline><xsl:value-of select="enopdf:get-suffix($source-context, $languages[1])"/></fo:inline>
			</fo:block>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//DateTimeDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="other-give-details" tunnel="yes" select="false()"/>
		
		<xsl:variable name="numeric-capture-character" select="substring($numeric-capture,1,1)"/>
		<xsl:variable name="field" select="upper-case(enopdf:get-format($source-context))"/>
		<xsl:variable name="field-image-name">
			<xsl:if test="contains($field,'YYYY') or contains($field,'AAAA')">
				<xsl:value-of select="'YYYY'"/>
			</xsl:if>
			<xsl:if test="contains($field,'MM')">
				<xsl:value-of select="'MM'"/>
			</xsl:if>
			<xsl:if test="contains($field,'DD') or contains($field,'JJ')">
				<xsl:value-of select="'DD'"/>
			</xsl:if>
		</xsl:variable>

		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			<xsl:choose>
				<xsl:when test="$other-give-details">
					<fo:block xsl:use-attribute-sets="details" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<fo:inline>
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'arrow_details.png'"/>
							</xsl:call-template>
							<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
						</fo:inline>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<fo:block xsl:use-attribute-sets="general-style">
			<xsl:if test="$isTable = 'YES'">
				<xsl:attribute name="text-align">right</xsl:attribute>
				<xsl:attribute name="padding-top">0mm</xsl:attribute>
				<xsl:attribute name="padding-bottom">0mm</xsl:attribute>
			</xsl:if>
			<xsl:call-template name="insert-image">
				<xsl:with-param name="image-name" select="concat('date-',$numeric-capture-character,'-',$languages[1],'-',$field-image-name,'.png')"/>
			</xsl:call-template>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//DurationDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="field" select="upper-case(enopdf:get-format($source-context))"/>
		<fo:inline>
			<fo:block xsl:use-attribute-sets="general-style">
				<xsl:choose>
					<xsl:when test="$field='HH:CH'">
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'mask_number.png'"/>
						</xsl:call-template>
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'mask_number.png'"/>
						</xsl:call-template>
						<fo:inline padding-start="1mm" padding-end="2mm">heures</fo:inline>
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'mask_number.png'"/>
						</xsl:call-template>
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'mask_number.png'"/>
						</xsl:call-template>
						<fo:inline padding-start="1mm" padding-end="2mm">centièmes</fo:inline>
					</xsl:when>
					<xsl:otherwise>
						<xsl:variable name="first-number-position" select="string-length(substring-before($field,'N'))+1"/>
						<xsl:variable name="maximum-duration" select="enopdf:get-maximum($source-context)"/>
						<xsl:for-each select="1 to string-length($field)">
							<xsl:variable name="current-position" select="position()"/>
							<xsl:variable name="current-character" select="substring($field,$current-position,1)"/>
							<xsl:choose>
								<xsl:when test="$current-character = 'P'"/>
								<xsl:when test="$current-character = 'T'"/>
								<xsl:when test="$current-character = 'N'">
									<xsl:variable name="number-of-characters">
										<xsl:choose>
											<xsl:when test="$current-position = $first-number-position and $maximum-duration != ''">
												<xsl:variable name="duration-regex" select="concat('^PT?([0-9]+)',substring($field,$current-position+1,1),'.*$')"/>
												<xsl:analyze-string select="$maximum-duration" regex="{$duration-regex}">
													<xsl:matching-substring>
														<xsl:value-of select="string-length(regex-group(1))"/>
													</xsl:matching-substring>
													<xsl:non-matching-substring>
														<xsl:value-of select="'2'"/>
													</xsl:non-matching-substring>
												</xsl:analyze-string>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="'2'"/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:variable>
									<xsl:for-each select="1 to xs:integer(number($number-of-characters))">
										<xsl:call-template name="insert-image">
											<xsl:with-param name="image-name" select="'mask_number.png'"/>
										</xsl:call-template>
									</xsl:for-each>
								</xsl:when>
								<xsl:when test="$current-character = 'Y' or $current-character = 'A'">
									<fo:inline padding-start="1mm" padding-end="3mm">
										<xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$languages[1]]/Duration/Year"/>
									</fo:inline>
								</xsl:when>
								<xsl:when test="$current-character = 'D' or $current-character = 'J'">
									<fo:inline padding-start="1mm" padding-end="3mm">
										<xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$languages[1]]/Duration/Day"/>
									</fo:inline>
								</xsl:when>
								<xsl:when test="$current-character = 'H'">
									<fo:inline padding-start="1mm" padding-end="3mm">
										<xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$languages[1]]/Duration/Hour"/>
									</fo:inline>
								</xsl:when>
								<xsl:when test="$current-character = 'S'">
									<fo:inline padding-start="1mm" padding-end="3mm">
										<xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$languages[1]]/Duration/Second"/>
									</fo:inline>
								</xsl:when>
								<xsl:when test="$current-character = 'M' and not(contains(substring($field,1,position()),'T'))">
									<fo:inline padding-start="1mm" padding-end="3mm">
										<xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$languages[1]]/Duration/Month"/>
									</fo:inline>
								</xsl:when>
								<xsl:when test="$current-character = 'M' and contains(substring($field,1,position()),'T')">
									<fo:inline padding-start="1mm" padding-end="3mm">
										<xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$languages[1]]/Duration/Minute"/>
									</fo:inline>
								</xsl:when>
								<xsl:otherwise>
									<fo:inline padding-start="1mm" padding-end="3mm">unité de temps inconnue</fo:inline>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</fo:block>
		</fo:inline>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//CodeDomain | main//BooleanDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		
		<xsl:choose>
			<xsl:when test="enopdf:get-appearance($source-context) = 'drop-down-list'">
				<xsl:choose>
					<xsl:when test="$no-border = 'no-border'">
						<fo:block-container height="8mm" width="50mm">
							<fo:block border-color="black" border-style="solid" width="50mm">&#160;</fo:block>
						</fo:block-container>
					</xsl:when>
					<xsl:when test="$isTable = 'YES'">
						<fo:block-container height="8mm" width="50mm">
							<fo:block>&#160;</fo:block>	
						</fo:block-container>
					</xsl:when>
					<xsl:otherwise>
						<fo:block-container height="8mm" border-color="black" border-style="solid" width="100%">
							<fo:block>&#160;</fo:block>
						</fo:block-container>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$no-border = 'no-border'">
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<fo:list-block>
					<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
						<xsl:with-param name="driver" select="." tunnel="yes"/>
					</xsl:apply-templates>
				</fo:list-block>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Déclenche tous les xf-item de l'arbre des drivers -->
	<xsl:template match="main//xf-item" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="image">
			<xsl:value-of select="enopdf:get-image($source-context)"/>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$no-border = 'no-border'">
				<fo:inline>
					<fo:inline>
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'check_case.png'"/>
						</xsl:call-template>
					</fo:inline>
					<xsl:choose>
						<xsl:when test="$image != ''">
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="$image"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<fo:inline xsl:use-attribute-sets="general-style">
								<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
							</fo:inline>
						</xsl:otherwise>
					</xsl:choose>
				</fo:inline>
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<fo:list-item>
					<fo:list-item-label end-indent="label-end()">
						<fo:block text-align="right">
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'check_case.png'"/>
							</xsl:call-template>
						</fo:block>
					</fo:list-item-label>
					<fo:list-item-body start-indent="body-start()">
						<fo:block xsl:use-attribute-sets="answer-item">
							<xsl:choose>
								<xsl:when test="$image != ''">
									<xsl:call-template name="insert-image">
										<xsl:with-param name="image-name" select="$image"/>
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
						<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
							<xsl:with-param name="driver" select="." tunnel="yes"/>
						</xsl:apply-templates>
					</fo:list-item-body>
				</fo:list-item>
			</xsl:otherwise>
		</xsl:choose>
		</xsl:template>

	<xsl:template name="insert-image">
		<xsl:param name="image-name"/>
		<fo:external-graphic>
			<xsl:attribute name="src">
				<xsl:choose>
					<xsl:when test="$images-folder != ''">
						<xsl:value-of select="concat('url(''file:',$images-folder,$image-name,''')')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$image-name"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</fo:external-graphic>
	</xsl:template>

</xsl:stylesheet>
