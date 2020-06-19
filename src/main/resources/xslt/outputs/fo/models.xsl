<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enofo="http://xml.insee.fr/apps/eno/out/fo"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
	exclude-result-prefixes="xd xs eno enofo fox"
	version="2.0">

	<xsl:include href="../../../styles/style.xsl"/>

	<xd:doc>
		<xd:desc>Remove all the ConsistencyCheck messages from the pdf</xd:desc>
	</xd:doc>
	<xsl:template match="main//ConsistencyCheck" mode="model"/>

	<xd:doc>
		<xd:desc>root template : main sequence = the questionnaire</xd:desc>
	</xd:doc>
	<xsl:template match="Form" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofo:get-form-languages($source-context)" as="xs:string +"/>
		<xsl:variable name="loop-navigation" as="node()">
			<Loops/>
		</xsl:variable>
		<xsl:variable name="survey-name" select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>

		<fo:root>
			<xsl:copy-of select="$page-model-default//fo:layout-master-set"/>
			<fo:page-sequence master-reference="A4" initial-page-number="2" force-page-count="odd">
				<fo:title><xsl:value-of select="$survey-name"/></fo:title>
				<xsl:copy-of select="$page-model-default//fo:static-content"/>
				<fo:flow flow-name="xsl-region-body" border-collapse="collapse" font-size="10pt">
					<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
						<xsl:with-param name="driver" select="eno:append-empty-element('main', .)" tunnel="yes"/>
						<xsl:with-param name="languages" select="$languages" tunnel="yes"/>
						<xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()" tunnel="yes"/>
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
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>

		<fo:block xsl:use-attribute-sets="Titre-sequence" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
			<xsl:if test="lower-case($page-break-between) = 'module' or lower-case($page-break-between) = 'submodule'">
				<xsl:attribute name="page-break-before" select="'always'"/>
			</xsl:if>
			<xsl:copy-of select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
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
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>

		<fo:block xsl:use-attribute-sets="Titre-paragraphe" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
			<xsl:copy-of select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
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

		<xsl:variable name="label" select="enofo:get-flowcontrol-label($source-context,$languages[1])"/>
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
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>

		<xsl:variable name="format" select="normalize-space(enofo:get-format($source-context))"/>
		<xsl:variable name="label" select="enofo:get-label($source-context, $languages[1],$loop-navigation)" as="node()"/>
		<xsl:choose>
			<xsl:when test="$format = 'footnote'">
				<fo:block>
					<fo:footnote>
						<fo:inline></fo:inline>
						<fo:footnote-body xsl:use-attribute-sets="footnote">
							<fo:block>
								<fo:inline font-size="75%" baseline-shift="super">
									<xsl:copy-of select="enofo:get-end-question-instructions-index($source-context)"/>
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
				<xsl:message select="concat('unknown xf-output : ',enofo:get-name($source-context),$label)"/>
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
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>

		<xsl:variable name="label" select="enofo:get-label($source-context, $languages[1],$loop-navigation)" as="node()"/>

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
		<xd:desc>template for loops</xd:desc>
	</xd:doc>
	<xsl:template match="main//QuestionLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>
		<xsl:param name="loop-position" tunnel="yes" select="''"/>
		<xsl:param name="empty-occurrence" tunnel="yes" as="xs:boolean" select="false()"/>

		<xsl:variable name="loop-name" select="enofo:get-business-name($source-context)"/>
		<xsl:variable name="current-match" select="."/>

		<xsl:variable name="loop-minimum-occurrence">
			<xsl:choose>
				<xsl:when test="enofo:get-maximum-occurrences-variables($source-context) != ''">
					<xsl:value-of select="$loop-default-occurrence"/>
				</xsl:when>
				<xsl:when test="enofo:get-minimum-occurrences-variables($source-context) != ''">
					<xsl:value-of select="$loop-default-occurrence"/>
				</xsl:when>
				<xsl:when test="enofo:get-maximum-occurrences($source-context) = ''">
					<xsl:value-of select="$loop-default-occurrence"/>
				</xsl:when>
				<xsl:when test="number(enofo:get-maximum-occurrences($source-context)) &lt; $loop-default-occurrence">
					<xsl:value-of select="enofo:get-maximum-occurrences($source-context)"/>
				</xsl:when>
				<xsl:when test="number(enofo:get-minimum-occurrences($source-context)) &gt; $loop-default-occurrence">
					<xsl:value-of select="enofo:get-minimum-occurrences($source-context)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$loop-default-occurrence"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- initialized occurrences -->
		<xsl:if test="not($empty-occurrence)">
			<xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="concat('#foreach( ${',$loop-name,'} in ${',$loop-name,'-Container} ) ')"/>
			<xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="concat('#set( $',$loop-name,'.LoopPosition = $velocityCount)')"/>
			<xsl:text>&#xa;</xsl:text>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="loop-position" select="concat($loop-position,'-$',$loop-name,'.LoopPosition')" tunnel="yes"/>
				<xsl:with-param name="loop-navigation" as="node()" tunnel="yes">
					<Loops>
						<xsl:copy-of select="$loop-navigation//Loop"/>
						<Loop name="{$loop-name}"/>
					</Loops>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="'#end '"/>
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>
		<!-- empty occurrences -->
		<xsl:if test="$loop-minimum-empty-occurrence != 0 or $loop-minimum-occurrence != 0">
			<xsl:text>&#xa;#set( $initializeInt = 0)&#xa;</xsl:text>
			<xsl:value-of select="concat('#set( $',$loop-name,'-TotalOccurrenceInt = $initializeInt.parseInt(${',$loop-name,'-TotalOccurrenceCount}))')"/>
			<xsl:text>&#xa;</xsl:text>
			<xsl:for-each select="1 to (if ($loop-minimum-empty-occurrence &gt; $loop-minimum-occurrence) then $loop-minimum-empty-occurrence else $loop-minimum-occurrence)">
				<xsl:variable name="empty-position" select="position()"/>
				<xsl:if test="$empty-position &gt; $loop-minimum-empty-occurrence">
					<xsl:text>&#xa;</xsl:text>
					<xsl:value-of select="concat('#if ($',$loop-name,'-TotalOccurrenceInt le ',$loop-minimum-occurrence - $empty-position,') ')"/>
					<xsl:text>&#xa;</xsl:text>
				</xsl:if>
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
					<xsl:with-param name="loop-position" select="concat($loop-position,'-0',$empty-position)" tunnel="yes"/>
					<xsl:with-param name="empty-occurrence" as="xs:boolean" select="true()" tunnel="yes"/>
					<xsl:with-param name="loop-navigation" as="node()" tunnel="yes">
						<Loops>
							<xsl:copy-of select="$loop-navigation//Loop"/>
							<Loop name="{$loop-name}"><xsl:value-of select="$empty-position"/></Loop>
						</Loops>
					</xsl:with-param>
				</xsl:apply-templates>
				<xsl:if test="$empty-position &gt; $loop-minimum-empty-occurrence">
					<xsl:text>&#xa;</xsl:text>
					<xsl:value-of select="'#end '"/>
					<xsl:text>&#xa;</xsl:text>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

	<!-- QUESTIONS -->
	<xd:doc>
		<xd:desc>Questions with responses which are not in a table</xd:desc>
	</xd:doc>
	<xsl:template match="main//SingleResponseQuestion | main//MultipleQuestion | main//MultipleChoiceQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>
		<xsl:param name="other-give-details" tunnel="yes" select="false()"/>
		<xsl:param name="loop-position" tunnel="yes" select="''"/>

		<!--<xsl:apply-templates select="enofo:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>-->
		<xsl:choose>
			<xsl:when test="$other-give-details">
				<fo:block xsl:use-attribute-sets="details" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
					<fo:inline>
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'arrow_details.png'"/>
						</xsl:call-template>
						<xsl:copy-of select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
					</fo:inline>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
					<xsl:copy-of select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="enofo:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>
		<fo:block id="{enofo:get-question-name($source-context,$languages[1])}{$loop-position}" page-break-inside="avoid">
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="typeOfAncestor" select="'question'" tunnel="yes"/>
			</xsl:apply-templates>
		</fo:block>
		<xsl:apply-templates select="enofo:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//Table" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>
		<xsl:param name="loop-position" tunnel="yes" select="''"/>

		<xsl:variable name="current-match" select="."/>
		<xsl:variable name="total-lines" as="xs:integer" select="count(enofo:get-body-lines($source-context))"/>
		<xsl:variable name="maxlines-by-page" as="xs:integer" select="xs:integer($table-defaultsize)"/>
		<!-- The table in the first page contains 1 line less than next ones -->
		<xsl:variable name="table-pages" select="xs:integer(1+(($total-lines -1+1) div $maxlines-by-page))" as="xs:integer"/>

		<!--<xsl:apply-templates select="enofo:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>-->
		<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
			<xsl:copy-of select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
		</fo:block>
		<xsl:apply-templates select="enofo:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>

		<!-- long tables are split : $maxlines-by-page lines maximum, except the first one which has 1 less -->
		<xsl:for-each select="1 to $table-pages">
			<xsl:variable name="page-position" select="position()"/>
			<fo:block page-break-inside="avoid">
				<xsl:attribute name="id">
					<xsl:value-of select="concat(enofo:get-question-name($source-context,$languages[1]),$loop-position)"/>
					<xsl:if test="$total-lines &gt; $maxlines-by-page -1">
						<xsl:value-of select="'0'"/>
						<xsl:value-of select="$page-position"/>
					</xsl:if>
				</xsl:attribute>
				<fo:table inline-progression-dimension="auto" table-layout="fixed" width="100%" font-size="10pt" border-width="0.35mm"
					text-align="center" margin-top="1mm" display-align="center" space-after="5mm">
					<xsl:if test="count(enofo:get-header-lines($source-context)) != 0">
						<fo:table-header>
							<xsl:for-each select="enofo:get-header-lines($source-context)">
								<fo:table-row xsl:use-attribute-sets="entete-ligne" text-align="center">
									<xsl:apply-templates select="enofo:get-header-line($source-context, position())" mode="source">
										<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
										<xsl:with-param name="header" select="'YES'" tunnel="yes"/>
										<xsl:with-param name="no-border" select="enofo:get-style($source-context)" tunnel="yes"/>
									</xsl:apply-templates>
								</fo:table-row>
							</xsl:for-each>
						</fo:table-header>
					</xsl:if>
					<fo:table-body>
						<xsl:variable name="first-line" select="$maxlines-by-page*($page-position -1)"/>
						<xsl:variable name="last-line" select="$maxlines-by-page*($page-position) -1"/>
						<xsl:for-each select="enofo:get-body-lines($source-context)">
							<xsl:variable name="position" select="position()"/>
							<!-- page 1 starts at line 0, so contains 1 line less than next ones -->
							<xsl:if test="($position &gt;= $first-line) and ($position &lt;= $last-line)">
								<fo:table-row border-color="black">
									<xsl:apply-templates select="enofo:get-body-line($source-context, position(),$first-line)" mode="source">
										<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
										<xsl:with-param name="table-first-line" select="$first-line" tunnel="yes"/>
										<xsl:with-param name="table-last-line" select="$last-line" tunnel="yes"/>
										<xsl:with-param name="isTable" select="'YES'" tunnel="yes"/>
										<xsl:with-param name="row-number" select="position()" tunnel="yes"/>
										<xsl:with-param name="no-border" select="enofo:get-style($source-context)" tunnel="yes"/>
									</xsl:apply-templates>
								</fo:table-row>
							</xsl:if>
						</xsl:for-each>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</xsl:for-each>
		<xsl:apply-templates select="enofo:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>
		<xsl:param name="loop-position" tunnel="yes" select="''"/>
		<xsl:param name="empty-occurrence" tunnel="yes" as="xs:boolean" select="false()"/>

		<xsl:variable name="loop-name" select="enofo:get-business-name($source-context)"/>
		<xsl:variable name="current-match" select="."/>
		<xsl:variable name="no-border" select="enofo:get-style($source-context)"/>
		<xsl:variable name="total-max-lines" select="enofo:get-maximum-lines($source-context)"/>
		<xsl:variable name="maxlines-by-page" as="xs:integer" select="xs:integer($table-defaultsize)"/>
		<xsl:variable name="roster-minimum-lines" as="xs:integer">
			<xsl:choose>
				<xsl:when test="$total-max-lines != '' and number($total-max-lines) &lt; $roster-defaultsize">
					<xsl:value-of select="$total-max-lines"/>
				</xsl:when>
				<xsl:when test="number(enofo:get-minimum-lines($source-context)) &gt; $roster-defaultsize">
					<xsl:value-of select="enofo:get-minimum-lines($source-context)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$roster-defaultsize"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="table-header" as="node()*">
			<xsl:for-each select="enofo:get-header-lines($source-context)">
				<fo:table-row xsl:use-attribute-sets="entete-ligne" text-align="center">
					<xsl:apply-templates select="enofo:get-header-line($source-context, position())" mode="source">
						<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
						<xsl:with-param name="header" select="'YES'" tunnel="yes"/>
						<xsl:with-param name="no-border" select="$no-border" tunnel="yes"/>
					</xsl:apply-templates>
				</fo:table-row>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="table-split-content">
			<xsl:value-of select="concat('#if (PositionInTheLoop % ',$maxlines-by-page,' eq 0) ')"/>
			<xsl:text>&#xd;</xsl:text>
			<xsl:value-of select="'&lt;/fo:table-body&gt; '"/>
			<xsl:value-of select="'&lt;/fo:table&gt;'"/>
			<xsl:value-of select="'&lt;/fo:block&gt;'"/>
			<xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="concat('#set ($DynamicArrayPage = PositionInTheLoop / ',$maxlines-by-page,')')"/>
			<xsl:text>&#xa;</xsl:text>
			<xsl:variable name="table-begin" as="node()">
				<fo:block page-break-inside="avoid">
					<xsl:attribute name="id" select="concat($loop-name,$loop-position,'-$DynamicArrayPage')"/>
					<xsl:attribute name="page-break-after" select="'always'"/>
					<fo:table inline-progression-dimension="auto" table-layout="fixed" width="100%" font-size="10pt" border-width="0.35mm"
						text-align="center" margin-top="1mm" display-align="center" space-after="5mm"/>
				</fo:block>
			</xsl:variable>
			<xsl:value-of select="replace(concat(substring-before(eno:serialize($table-begin),'/&gt;'),'&gt;'),'&lt;','&lt;fo:')"/>
			<xsl:if test="count(enofo:get-header-lines($source-context)) != 0">
				<xsl:value-of select="concat('&lt;fo:table-header&gt;',
					                  replace(replace(eno:serialize($table-header),'&lt;','&lt;fo:'),'&lt;fo:/','&lt;/fo:'),
					                  '&lt;/fo:table-header&gt;')"/>
			</xsl:if>
			<xsl:value-of select="'&lt;fo:table-body&gt;'"/>
			<xsl:text>&#xa;</xsl:text>
			<xsl:value-of select="'#end '"/>
			<xsl:text>&#xa;</xsl:text>
		</xsl:variable>
		<!--<xsl:apply-templates select="enofo:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>-->
		<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
			<xsl:copy-of select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
		</fo:block>
		<xsl:apply-templates select="enofo:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>

		<fo:block page-break-inside="avoid">
			<xsl:attribute name="id" select="concat($loop-name,$loop-position)"/>
			<xsl:if test="$total-max-lines = '' or number($total-max-lines) &gt;= $maxlines-by-page">
				<xsl:attribute name="page-break-after" select="'always'"/>
			</xsl:if>
			<fo:table inline-progression-dimension="auto" table-layout="fixed" width="100%" font-size="10pt" border-width="0.35mm"
				text-align="center" margin-top="1mm" display-align="center" space-after="5mm">
				<xsl:if test="count(enofo:get-header-lines($source-context)) != 0">
					<fo:table-header>
						<xsl:copy-of select="$table-header"/>
					</fo:table-header>
				</xsl:if>
				<fo:table-body>
					<!-- initialized rows -->
					<xsl:if test="not($empty-occurrence)">
						<xsl:text>&#xd;</xsl:text>
						<xsl:value-of select="concat('#foreach( ${',$loop-name,'} in ${',$loop-name,'-Container} ) ')"/>
						<xsl:text>&#xd;</xsl:text>
						<xsl:value-of select="concat('#set( $',$loop-name,'.LoopPosition = $velocityCount)')"/>
						<xsl:text>&#xd;</xsl:text>
						<xsl:value-of select="replace($table-split-content,'PositionInTheLoop',concat('\$',$loop-name,'.LoopPosition'))"/>
						<!-- the line to loop on -->
						<xsl:for-each select="enofo:get-body-lines($source-context)">
							<xsl:variable name="position" select="position()"/>
							<fo:table-row border-color="black">
								<xsl:apply-templates select="enofo:get-body-line($source-context, $position)" mode="source">
									<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
									<xsl:with-param name="no-border" select="$no-border" tunnel="yes"/>
									<xsl:with-param name="loop-position" select="concat($loop-position,'-$',$loop-name,'.LoopPosition')" tunnel="yes"/>
									<xsl:with-param name="loop-navigation" as="node()" tunnel="yes">
										<Loops>
											<xsl:copy-of select="$loop-navigation//Loop"/>
											<Loop name="{$loop-name}"/>
										</Loops>
									</xsl:with-param>
								</xsl:apply-templates>
							</fo:table-row>
						</xsl:for-each>
						<xsl:text>&#xd;</xsl:text>
						<xsl:value-of select="'#end '"/>
						<xsl:text>&#xd;</xsl:text>
					</xsl:if>
					<!-- empty rows -->
					<xsl:if test="$roster-minimum-empty-row != 0 or $roster-minimum-lines != 0">
						<xsl:text>&#xa;#set( $initializeInt = 0)&#xa;</xsl:text>
						<xsl:value-of select="concat('#set( $',$loop-name,'-TotalOccurrenceInt = $initializeInt.parseInt(${',$loop-name,'-TotalOccurrenceCount}))')"/>
						<xsl:text>&#xa;</xsl:text>
						<xsl:for-each select="1 to (if ($roster-minimum-empty-row &gt; $roster-minimum-lines) then $roster-minimum-empty-row else $roster-minimum-lines)">
							<xsl:variable name="empty-position" select="position()"/>
							<xsl:if test="$empty-position &gt; $roster-minimum-empty-row">
								<xsl:text>&#xa;</xsl:text>
								<xsl:value-of select="concat('#if ($',$loop-name,'-TotalOccurrenceInt le ',$roster-minimum-lines - $empty-position,') ')"/>
								<xsl:text>&#xa;</xsl:text>
							</xsl:if>
							<xsl:value-of select="replace($table-split-content,'PositionInTheLoop',concat('(\$',$loop-name,'-TotalOccurrenceInt + ',$empty-position,')'))"/>
							<!-- the line to fake-loop on -->
							<xsl:for-each select="enofo:get-body-lines($source-context)">
								<xsl:variable name="position" select="position()"/>
								<fo:table-row border-color="black">
									<xsl:apply-templates select="enofo:get-body-line($source-context, $position)" mode="source">
										<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
										<xsl:with-param name="no-border" select="$no-border" tunnel="yes"/>
										<xsl:with-param name="loop-position" select="concat($loop-position,'-0',$empty-position)" tunnel="yes"/>
										<xsl:with-param name="empty-occurrence" as="xs:boolean" select="true()" tunnel="yes"/>
										<xsl:with-param name="loop-navigation" as="node()" tunnel="yes">
											<Loops>
												<xsl:copy-of select="$loop-navigation//Loop"/>
												<Loop name="{$loop-name}"><xsl:value-of select="$empty-position"/></Loop>
											</Loops>
										</xsl:with-param>
									</xsl:apply-templates>
								</fo:table-row>
							</xsl:for-each>
							<xsl:if test="$empty-position &gt; $roster-minimum-empty-row">
								<xsl:text>&#xa;</xsl:text>
								<xsl:value-of select="'#end '"/>
								<xsl:text>&#xa;</xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<xsl:apply-templates select="enofo:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//TextCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>
		<xsl:param name="header" tunnel="yes"/>
		<xsl:param name="row-number" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="table-first-line" tunnel="yes"/>
		<xsl:param name="table-last-line" tunnel="yes"/>

		<fo:table-cell xsl:use-attribute-sets="colonne-tableau"
			number-rows-spanned="{enofo:get-rowspan($source-context,$table-first-line,$table-last-line)}"
			number-columns-spanned="{enofo:get-colspan($source-context)}">
			<xsl:if test="$header">
				<xsl:attribute name="text-align">center</xsl:attribute>
			</xsl:if>
			<xsl:if test="$no-border = 'no-border'">
				<xsl:attribute name="border" select="'0mm'"/>
				<xsl:attribute name="padding" select="'0mm'"/>
			</xsl:if>
			<fo:block xsl:use-attribute-sets="label-cell">
				<xsl:if test="not($header)">
					<xsl:attribute name="margin-left" select="'1mm'"/>
				</xsl:if>
				<xsl:copy-of select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

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

	<xsl:template match="main//EmptyCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<fo:table-cell background-color="#CCCCCC" border-color="black" border-style="solid"
			number-columns-spanned="{enofo:get-colspan($source-context)}"
			number-rows-spanned="{enofo:get-rowspan($source-context)}">
			<fo:block/>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="main//FixedCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>

		<fo:table-cell background-color="#CCCCCC" border-color="black" border-style="solid"
			number-columns-spanned="{enofo:get-colspan($source-context)}"
			number-rows-spanned="{enofo:get-rowspan($source-context)}">
			<fo:block>
				<xsl:sequence select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
				<xsl:sequence select="enofo:get-fixed-value($source-context, $languages[1],$loop-navigation)"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<!-- 	RESPONSES -->

	<xd:doc>
		<xd:desc>variables and variable groups : do nothing</xd:desc>
	</xd:doc>
	<xsl:template match="main//VariableGroup" mode="model"/>
	<xsl:template match="main//Variable" mode="model"/>

	<xsl:template match="main//TextareaDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>

		<xsl:variable name="height" select="8*number($textarea-defaultsize)"/>
		<xsl:variable name="variable-name">
			<xsl:call-template name="variable-velocity-name">
				<xsl:with-param name="variable" select="enofo:get-business-name($source-context)"/>
				<xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()"/>
			</xsl:call-template>
		</xsl:variable>

		<fo:block-container height="{$height}mm">
			<xsl:if test="not($isTable = 'YES')">
				<xsl:attribute name="border-color" select="'black'"/>
				<xsl:attribute name="border-style" select="'solid'"/>
			</xsl:if>
			<fo:block>
				<xsl:choose>
					<xsl:when test="enofo:is-initializable-variable($source-context)">
						<xsl:value-of select="concat('#{if}(',$variable-name,')',$variable-name,'#{else}&#160;#{end}')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'&#160;'"/>
					</xsl:otherwise>
				</xsl:choose>
			</fo:block>
		</fo:block-container>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//TextDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="other-give-details" tunnel="yes" select="false()"/>

		<xsl:variable name="length" select="enofo:get-length($source-context)"/>
		<xsl:variable name="label" select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
		<xsl:variable name="variable-name">
			<xsl:call-template name="variable-velocity-name">
				<xsl:with-param name="variable" select="enofo:get-business-name($source-context)"/>
				<xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="variable-personalization-begin" select="concat('#{if}(',$variable-name,')',$variable-name,'#{else}')"/>

		<xsl:if test="$label != ''">
			<xsl:choose>
				<xsl:when test="$other-give-details">
					<fo:block xsl:use-attribute-sets="details" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<fo:inline>
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'arrow_details.png'"/>
							</xsl:call-template>
							<xsl:copy-of select="$label"/>
						</fo:inline>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<xsl:copy-of select="$label"/>
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<fo:block>
			<xsl:choose>
				<xsl:when test="(enofo:get-format($source-context) or ($length !='' and number($length) &lt;= 20)) and ancestor::Cell">
					<fo:block xsl:use-attribute-sets="label-cell">
						<xsl:if test="enofo:is-initializable-variable($source-context)">
							<xsl:value-of select="$variable-personalization-begin"/>
						</xsl:if>
						<xsl:for-each select="1 to xs:integer(number($length))">
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'mask_number.png'"/>
							</xsl:call-template>
						</xsl:for-each>
						<xsl:if test="enofo:is-initializable-variable($source-context)">
							<xsl:value-of select="'#{end}'"/>
						</xsl:if>
					</fo:block>
				</xsl:when>
				<xsl:when test="enofo:get-format($source-context) or ($length !='' and number($length) &lt;= 20)">
					<fo:block xsl:use-attribute-sets="general-style">
						<xsl:if test="enofo:is-initializable-variable($source-context)">
							<xsl:value-of select="$variable-personalization-begin"/>
						</xsl:if>
						<xsl:for-each select="1 to xs:integer(number($length))">
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'mask_number.png'"/>
							</xsl:call-template>
						</xsl:for-each>
						<xsl:if test="enofo:is-initializable-variable($source-context)">
							<xsl:value-of select="'#{end}'"/>
						</xsl:if>
					</fo:block>
				</xsl:when>
				<xsl:when test="$no-border = 'no-border'">
					<fo:block-container height="8mm" width="50mm">
						<fo:block border-color="black" border-style="solid" width="50mm">
							<xsl:choose>
								<xsl:when test="enofo:is-initializable-variable($source-context)">
									<xsl:value-of select="concat('#{if}(',$variable-name,')',$variable-name,'#{else}&#160;#{end}')"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="'&#160;'"/>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:block-container>
				</xsl:when>
				<xsl:when test="$isTable = 'YES'">
					<fo:block-container height="8mm" width="50mm">
						<fo:block>
							<xsl:choose>
								<xsl:when test="enofo:is-initializable-variable($source-context)">
									<xsl:value-of select="concat('#{if}(',$variable-name,')',$variable-name,'#{else}&#160;#{end}')"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="'&#160;'"/>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:block-container>
				</xsl:when>
				<xsl:otherwise>
					<fo:block-container height="8mm" border-color="black" border-style="solid" width="100%">
						<fo:block>
							<xsl:choose>
								<xsl:when test="enofo:is-initializable-variable($source-context)">
									<xsl:value-of select="concat('#{if}(',$variable-name,')',$variable-name,'#{else}&#160;#{end}')"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="'&#160;'"/>
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:block-container>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//NumericDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="other-give-details" tunnel="yes" select="false()"/>

		<xsl:variable name="length" select="number(enofo:get-length($source-context))"/>
		<xsl:variable name="label" select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
		<xsl:variable name="variable-name">
			<xsl:call-template name="variable-velocity-name">
				<xsl:with-param name="variable" select="enofo:get-business-name($source-context)"/>
				<xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="variable-personalization-begin" select="concat('#{if}(',$variable-name,')',$variable-name,'#{else}')"/>

		<xsl:if test="$label != ''">
			<xsl:choose>
				<xsl:when test="$other-give-details">
					<fo:block xsl:use-attribute-sets="details" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<fo:inline>
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'arrow_details.png'"/>
							</xsl:call-template>
							<xsl:copy-of select="$label"/>
						</fo:inline>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<xsl:copy-of select="$label"/>
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
			<xsl:choose>
				<xsl:when test="$numeric-capture = 'optical'">
					<xsl:variable name="separator-position">
						<xsl:choose>
							<xsl:when test="enofo:get-number-of-decimals($source-context) != '0'">
								<xsl:value-of select="string($length - number(enofo:get-number-of-decimals($source-context)))"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="'0'"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="optical-content" as="node() *">
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
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="ancestor::Cell">
							<fo:block xsl:use-attribute-sets="label-cell" padding-bottom="0mm" padding-top="0mm">
								<xsl:choose>
									<xsl:when test="enofo:is-initializable-variable($source-context)">
										<xsl:copy-of select="concat($variable-personalization-begin,$optical-content,'#{end})"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:copy-of select="$optical-content"/>
									</xsl:otherwise>
								</xsl:choose>
								<fo:inline><xsl:value-of select="enofo:get-suffix($source-context, $languages[1])"/></fo:inline>
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block xsl:use-attribute-sets="general-style" padding-bottom="0mm" padding-top="0mm">
								<xsl:choose>
									<xsl:when test="enofo:is-initializable-variable($source-context)">
										<xsl:copy-of select="concat($variable-personalization-begin,$optical-content,'#{end})"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:copy-of select="$optical-content"/>
									</xsl:otherwise>
								</xsl:choose>
								<fo:inline><xsl:value-of select="enofo:get-suffix($source-context, $languages[1])"/></fo:inline>
							</fo:block>
						</xsl:otherwise>
					</xsl:choose>
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
					<xsl:variable name="manual-content" as="node()">
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
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="ancestor::Cell">
							<fo:block xsl:use-attribute-sets="label-cell" padding-bottom="0mm" padding-top="0mm">
								<xsl:choose>
									<xsl:when test="enofo:is-initializable-variable($source-context)">
										<xsl:copy-of select="concat($variable-personalization-begin,$manual-content,'#{end})"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:copy-of select="$manual-content"/>
									</xsl:otherwise>
								</xsl:choose>
								<fo:inline><xsl:value-of select="enofo:get-suffix($source-context, $languages[1])"/></fo:inline>
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block xsl:use-attribute-sets="general-style" padding-bottom="0mm" padding-top="0mm">
								<xsl:choose>
									<xsl:when test="enofo:is-initializable-variable($source-context)">
										<xsl:copy-of select="concat($variable-personalization-begin,$manual-content,'#{end})"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:copy-of select="$manual-content"/>
									</xsl:otherwise>
								</xsl:choose>
								<fo:inline><xsl:value-of select="enofo:get-suffix($source-context, $languages[1])"/></fo:inline>
							</fo:block>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//DateTimeDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="other-give-details" tunnel="yes" select="false()"/>

		<xsl:variable name="label" select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
		<xsl:variable name="numeric-capture-character" select="substring($numeric-capture,1,1)"/>
		<xsl:variable name="field" select="upper-case(enofo:get-format($source-context))"/>
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

		<xsl:if test="$label != ''">
			<xsl:choose>
				<xsl:when test="$other-give-details">
					<fo:block xsl:use-attribute-sets="details" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<fo:inline>
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'arrow_details.png'"/>
							</xsl:call-template>
							<xsl:copy-of select="$label"/>
						</fo:inline>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always" keep-together.within-column="always">
						<xsl:copy-of select="$label"/>
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="$isTable = 'YES'">
				<fo:block xsl:use-attribute-sets="label-cell">
					<xsl:attribute name="text-align">right</xsl:attribute>
					<xsl:attribute name="padding-top">0mm</xsl:attribute>
					<xsl:attribute name="padding-bottom">0mm</xsl:attribute>
					<xsl:call-template name="insert-image">
						<xsl:with-param name="image-name" select="concat('date-',$numeric-capture-character,'-',$languages[1],'-',$field-image-name,'.png')"/>
					</xsl:call-template>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:call-template name="insert-image">
						<xsl:with-param name="image-name" select="concat('date-',$numeric-capture-character,'-',$languages[1],'-',$field-image-name,'.png')"/>
					</xsl:call-template>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//DurationDomain" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>

		<xsl:variable name="field" select="upper-case(enofo:get-format($source-context))"/>
		<fo:inline>
			<xsl:variable name="duration-content" as="node() *">
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
						<xsl:variable name="maximum-duration" select="enofo:get-maximum($source-context)"/>
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
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="ancestor::Cell">
					<fo:block xsl:use-attribute-sets="label-cell">
						<xsl:copy-of select="$duration-content"/>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block xsl:use-attribute-sets="general-style">
						<xsl:copy-of select="$duration-content"/>
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
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
			<xsl:when test="enofo:get-appearance($source-context) = 'drop-down-list'">
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

	<xsl:template match="main//xf-item" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="loop-navigation" as="node()" tunnel="yes"/>

		<xsl:variable name="image">
			<xsl:value-of select="enofo:get-image($source-context)"/>
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
							<fo:inline xsl:use-attribute-sets="label-cell">
								<xsl:copy-of select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
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
									<xsl:copy-of select="enofo:get-label($source-context, $languages[1],$loop-navigation)"/>
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

	<xd:doc>
		<xd:desc>the name of a variable</xd:desc>
	</xd:doc>
	<xsl:template name="variable-velocity-name">
		<xsl:param name="variable"/>
		<xsl:param name="loop-navigation" as="node()"/>

		<xsl:variable name="variable-name">
			<xsl:value-of select="'$!{'"/>
			<xsl:value-of select="$loop-navigation//Loop[last()]/name()"/>
			<xsl:choose>
				<!-- variable in empty occurrence after loop -->
				<xsl:when test="$loop-navigation//Loop[last()]/text() != ''">
					<xsl:value-of select="'-0-'"/>
				</xsl:when>
				<!-- variable in loop occurrence -->
				<xsl:when test="$loop-navigation//Loop != ''">
					<xsl:value-of select="'.'"/>
				</xsl:when>
				<!-- variable out of loops -->
				<xsl:otherwise/>
			</xsl:choose>
			<xsl:value-of select="$variable"/>
			<xsl:value-of select="'}'"/>
		</xsl:variable>
	</xsl:template>

</xsl:stylesheet>
