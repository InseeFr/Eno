<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
	xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="xd eno enopdf"
	xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
	version="2.0">

	<xd:doc>
		<xd:desc>
			<xd:p>The properties file used by the stylesheet.</xd:p>
			<xd:p>It's on a transformation level.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:param name="properties-file"/>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The properties file is charged as an xml tree.</xd:p>
		</xd:desc>
	</xd:doc>
	
	<xsl:variable name="properties" select="doc($properties-file)"/>
	

	<xsl:include href="../../../styles/style.xsl"/>
	
	<!-- Remove all the ConsistencyCheck messages from the pdf -->
	<xsl:template match="main//ConsistencyCheck" mode="model"/>
	
	
	<xd:doc>
		<xd:desc>root template : main sequence = the questionnaire</xd:desc>
	</xd:doc>
	<xsl:template match="Form" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)" as="xs:string +"/>
		
		<fo:root>
			<fo:layout-master-set>
				<!-- reference-orientation="90" column-count="2" -->
				<fo:simple-page-master master-name="A4-portrait" page-height="297mm"
					page-width="210mm" font-family="arial" font-size="10pt" reference-orientation="{$properties//Format/Orientation}"
					font-weight="normal" margin-bottom="5mm">
					<fo:region-body margin="13mm" column-count="1"/>
					<fo:region-before region-name="xsl-region-before" extent="25mm" display-align="before" precedence="true"/>
					<fo:region-after region-name="xsl-region-after" extent="25mm" display-align="before" precedence="true"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="A4-portrait" initial-page-number="2">
				<fo:static-content flow-name="xsl-region-before">
					<fo:block position="absolute" margin="10mm" text-align="right">
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'encoche-top-right.png'"/>
						</xsl:call-template>
					</fo:block>
					<fo:block position="absolute" margin-top="80%" text-align="right">
						<fo:instream-foreign-object>
							<barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
								message="Code Bar - #page-number#" orientation="90">
								<barcode:code128>
									<barcode:height>8mm</barcode:height>
								</barcode:code128>
							</barcode:barcode>
						</fo:instream-foreign-object>
					</fo:block>
					<!-- Je n'ai pas trouvé quel contenu mettre... -->
					<!--<fo:block>
						<xsl:value-of select="'#if '"/>
						<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
							<xsl:with-param name="driver" select="eno:append-empty-element('barcode', .)" tunnel="yes"/>
						</xsl:apply-templates>
						<xsl:value-of select="'(&lt;fo:page-number/&gt; == &lt;fo:page-number-citation ref-id=&quot;TheVeryLastPage&quot;/&gt;) TheVeryLastPage '"/>
						<xsl:value-of select="'#else unknown Page #end'"/>
						<fo:page-number/> / <fo:page-number-citation ref-id="TheVeryLastPage"/>
					</fo:block>-->
				</fo:static-content>
				<fo:static-content flow-name="xsl-region-after">
					<fo:block position="absolute" margin-left="10mm" margin-top="10mm" bottom="0px" text-align="left">
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'encoche-bottom-left.png'"/>
						</xsl:call-template>
					</fo:block>
					<fo:block text-align="center">
						<fo:page-number/> / <fo:page-number-citation ref-id="TheVeryLastPage"/>
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" border-collapse="collapse" font-size="10pt">
					<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
						<xsl:with-param name="driver" select="eno:append-empty-element('main', .)" tunnel="yes"/>
						<xsl:with-param name="languages" select="$languages" tunnel="yes"/>
					</xsl:apply-templates>
					<fo:block id="TheVeryLastPage"> </fo:block>
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
		
		<fo:block xsl:use-attribute-sets="Titre-sequence" border-color="black" border-style="solid" keep-with-next="always">
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
		
		<fo:block xsl:use-attribute-sets="Titre-paragraphe" keep-with-next="always"> <!-- linefeed-treatment="preserve" -->
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:desc>template for the instructions</xd:desc>
	</xd:doc>
	<xsl:template match="main//xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>

		<xsl:choose>
			<xsl:when test="enopdf:get-format($source-context) = 'footnote'">
				<fo:block>
					<fo:footnote>
						<fo:inline></fo:inline>
						<fo:footnote-body xsl:use-attribute-sets="footnote">
							<fo:block>
								<fo:inline font-size="75%" baseline-shift="super">
									<xsl:copy-of select="enopdf:get-end-question-instructions-index($source-context)"/>
								</fo:inline>
								<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
							</fo:block>
						</fo:footnote-body>
					</fo:footnote>
				</fo:block>
			</xsl:when>
			<xsl:when test="enopdf:get-format($source-context) = 'tooltip'">
			</xsl:when>
			<xsl:when test="enopdf:get-format($source-context) = 'comment' or enopdf:get-format($source-context) = 'help' or enopdf:get-format($source-context) = 'instruction'">
				<fo:block xsl:use-attribute-sets="instruction" keep-with-next="always">
					<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
				</fo:block>
			</xsl:when>
			<xsl:when test="enopdf:get-format($source-context) = 'filter-alternative-text'">
				<fo:block width="100%" keep-with-previous="always">
					<fo:inline-container width="10%">
						<fo:block-container>
							<fo:block>
								<xsl:call-template name="insert-image">
									<xsl:with-param name="image-name" select="'filter_arrow_25.png'"/>
								</xsl:call-template>
							</fo:block>
						</fo:block-container>
					</fo:inline-container>
					<fo:inline-container width="87%">
						<fo:block xsl:use-attribute-sets="filter-alternative" width="100%">
							<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
						</fo:block>
					</fo:inline-container>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<fo:block xsl:use-attribute-sets="general-style" keep-with-next="always">
					<xsl:if test="$isTable = 'YES'">
						<xsl:attribute name="margin-left">1mm</xsl:attribute>
					</xsl:if>
					<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
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

		<xsl:apply-templates select="enopdf:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>
		<fo:block xsl:use-attribute-sets="label-question" keep-with-next="always"> <!--linefeed-treatment="preserve"-->
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<xsl:apply-templates select="enopdf:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>
		<fo:block id="{enopdf:get-name($source-context)}" page-break-inside="avoid">
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="typeOfAncestor" select="'question'" tunnel="yes"/>
			</xsl:apply-templates>
		</fo:block>
		<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

<!-- 	REPONSES -->
	
	<!-- Déclenche tous les xf-input : REPONSES QUI DOIVENT ETRE RENSEIGNEES DANS LE QUESTIONNAIRE-->
	<xsl:template match="main//xf-input" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="position" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="autreHandle" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="input-type" select="enopdf:get-type($source-context)"/>
		
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			<fo:block font-size="10pt" font-weight="bold" color="black"> <!--linefeed-treatment="preserve"-->
				<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
			</fo:block>
		</xsl:if>
		
		<xsl:choose>
			<xsl:when test="$input-type = 'text'">
				<fo:block>
					<xsl:choose>
						<xsl:when test="enopdf:get-format($source-context)">
							<fo:block xsl:use-attribute-sets="general-style">
								<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
									<xsl:call-template name="insert-image">
										<xsl:with-param name="image-name" select="'mask_number.png'"/>
									</xsl:call-template>
								</xsl:for-each>
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block xsl:use-attribute-sets="Line-drawing">
								<xsl:if test="enopdf:get-length($source-context)">
									<xsl:attribute name="min-width"><xsl:value-of select="(number(enopdf:get-length($source-context)))"/>mm</xsl:attribute>
								</xsl:if>
								&#160;
							</fo:block>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
			</xsl:when>
			<xsl:when test="$input-type = 'number'">
				<xsl:variable name="length" select="enopdf:get-length($source-context)"/>
				<fo:block>
					<xsl:if test="$isTable = 'YES'">
						<xsl:attribute name="text-align">right</xsl:attribute>
						<xsl:attribute name="padding-top">0px</xsl:attribute>
						<xsl:attribute name="padding-bottom">0px</xsl:attribute>
					</xsl:if>
					<fo:block xsl:use-attribute-sets="general-style" padding-bottom="0mm" padding-top="0mm">
						<xsl:variable name="separator-position">
							<xsl:choose>
								<xsl:when test="enopdf:get-number-of-decimals($source-context) != '0'">
									<xsl:value-of select="string(number($length) - number(enopdf:get-number-of-decimals($source-context)))"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="'0'"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
							<xsl:choose>
								<xsl:when test="$separator-position = .">
									<fo:inline>,</fo:inline>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="insert-image">
										<xsl:with-param name="image-name" select="'mask_number.png'"/>
									</xsl:call-template>									
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
						<fo:inline><xsl:value-of select="enopdf:get-suffix($source-context, $languages[1])"/></fo:inline>
					</fo:block>
				</fo:block>
			</xsl:when>
			<xsl:when test="$input-type = 'date'">
				<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:if test="$isTable = 'YES'">
						<xsl:attribute name="text-align">right</xsl:attribute>
						<xsl:attribute name="padding-top">0px</xsl:attribute>
						<xsl:attribute name="padding-bottom">0px</xsl:attribute>
					</xsl:if>
					<xsl:for-each select="1 to xs:integer(number(string-length(replace($field,'/',''))))">
						<xsl:call-template name="insert-image">
							<xsl:with-param name="image-name" select="'mask_number.png'"/>
						</xsl:call-template>
					</xsl:for-each>
					(<xsl:value-of select="$field"/>)
				</fo:block>
			</xsl:when>
		</xsl:choose>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
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
			<xsl:when test="enopdf:get-image($source-context) != ''">
				<xsl:choose>
					<xsl:when test="starts-with($image,'http')">
							<fo:inline font-family="ZapfDingbats" font-size="10pt" padding="5mm">&#x274F;</fo:inline>
							<fo:external-graphic padding-right="3mm">
								<xsl:attribute name="src">
									<xsl:value-of select="$image"/>
								</xsl:attribute>
							</fo:external-graphic>
					</xsl:when>
					<xsl:otherwise>
							<fo:external-graphic padding-right="3mm">
								<xsl:attribute name="src">
									<xsl:value-of select="concat($properties//Images/Folder,$image)"/>
								</xsl:attribute>
							</fo:external-graphic>										
							<fo:inline>
								<xsl:value-of select="$image"/>
							</fo:inline>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$no-border = 'no-border'">
						<xsl:choose>
							<xsl:when test="enopdf:get-label($source-context, $languages[1]) != ''">
								<fo:inline font-family="ZapfDingbats" font-size="10pt" margin-top="3mm">&#x274F;</fo:inline>
								<fo:inline><xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/></fo:inline>
								<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
									<xsl:with-param name="driver" select="." tunnel="yes"/>
								</xsl:apply-templates>
							</xsl:when>
							<xsl:otherwise>
								<fo:block font-family="ZapfDingbats" text-align="center" font-size="10pt" padding-right="4mm" padding-left="6mm" margin-top="3mm">
									&#x274F;
									<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
										<xsl:with-param name="driver" select="." tunnel="yes"/>
									</xsl:apply-templates>
								</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<fo:block>
							<fo:inline font-family="ZapfDingbats" font-size="10pt" padding-right="5mm" margin-top="3mm">&#x274F;</fo:inline>
							<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
								<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
							</xsl:if>
							<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
							</xsl:apply-templates>
						</fo:block>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Déclenche tous les xf-select de l'arbre des drivers -->
	<xsl:template match="main//xf-select" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	

	<!-- Déclenche tous les xf-select de l'arbre des drivers -->
	<xsl:template match="main//xf-select1" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>

		<xsl:variable name="format" select="enopdf:get-appearance($source-context)"/>
		
		<xsl:choose>
			<xsl:when test="$format = 'minimal'">
				<fo:block xsl:use-attribute-sets="Line-drawing">&#160;</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Déclenche tous les Table de l'arbre des drivers -->
	<xsl:template match="main//Table | main//TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="current-match" select="."/>
		<xsl:variable name="total-lines" as="xs:integer">
			<xsl:choose>
				<xsl:when test="self::Table">
					<xsl:value-of select="count(enopdf:get-body-lines($source-context))"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="number(enopdf:get-maximum-lines($source-context))"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="maxlines-by-table" as="xs:integer">
			<xsl:choose>
				<xsl:when test="self::Table">
					<xsl:value-of select="number($properties//Table/Row/DefaultSize)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="number($properties//Roster/Row/DefaultSize)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="table-pages" select="xs:integer(1+(($total-lines -1) div $maxlines-by-table))" as="xs:integer"/>
		
		<xsl:apply-templates select="enopdf:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>
		<fo:block xsl:use-attribute-sets="label-question" keep-with-next="always">
			<!--<xsl:attribute name="id" select="enopdf:get-name($source-context)"/>-->
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<xsl:apply-templates select="enopdf:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>

		<!-- long tables are split : $maxlines-by-table lines maximum, except the first one which has 1 less -->
		<xsl:for-each select="1 to $table-pages">
			<xsl:variable name="page-position" select="position()"/>
			<fo:block page-break-inside="avoid">
				<xsl:attribute name="id" select="concat(enopdf:get-name($source-context),'-',$page-position)"/>
				<xsl:if test="$current-match/name()='TableLoop' and $total-lines &gt;= $maxlines-by-table">
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
										<xsl:with-param name="no-border" select="enopdf:get-style($source-context)" tunnel="yes"/>
									</xsl:apply-templates>
								</fo:table-row>
							</xsl:for-each>
						</fo:table-header>
					</xsl:if>
					<fo:table-body>
						<xsl:choose>
							<xsl:when test="$current-match/name()='Table'">
								<xsl:for-each select="enopdf:get-body-lines($source-context)">
									<xsl:variable name="position" select="position()"/>
									<xsl:if test="($position &gt; $maxlines-by-table*($page-position -1)) and ($position &lt;= $maxlines-by-table*$page-position)">
										<fo:table-row border-color="black">
											<xsl:apply-templates select="enopdf:get-body-line($source-context, position(),$maxlines-by-table*($page-position -1) +1)" mode="source">
												<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
												<xsl:with-param name="table-first-line" select="$maxlines-by-table*($page-position -1) +1" tunnel="yes"/>
												<xsl:with-param name="table-last-line" select="$maxlines-by-table*$page-position" tunnel="yes"/>
												<xsl:with-param name="isTable" select="'YES'" tunnel="yes"/>
												<xsl:with-param name="row-number" select="position()" tunnel="yes"/>
												<xsl:with-param name="no-border" select="enopdf:get-style($source-context)" tunnel="yes"/>
											</xsl:apply-templates>
										</fo:table-row>
									</xsl:if>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<xsl:for-each select="1 to $maxlines-by-table">
									<!-- if the dynamic table is on several pages, each page contains maxlines-by-table -->
									<xsl:if test=". &lt;= $total-lines or $total-lines &gt; $maxlines-by-table">
										<!-- in a dynamic table, a repeated "line" may be on several get-body-lines -->
										<xsl:for-each select="enopdf:get-body-lines($source-context)">
											<xsl:variable name="position" select="position()"/>
											<fo:table-row border-color="black">
												<xsl:apply-templates select="enopdf:get-body-line($source-context, $position)" mode="source">
													<xsl:with-param name="driver" select="$current-match" tunnel="yes"/>
													<xsl:with-param name="no-border" select="enopdf:get-style($source-context)" tunnel="yes"/>
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
				<xsl:attribute name="border">0mm </xsl:attribute>
				<xsl:attribute name="padding">0mm </xsl:attribute>
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
		
		<fo:table-cell text-align="left" border-color="black" border-style="solid" padding="1mm">
			<xsl:if test="$no-border = 'no-border'">
				<xsl:attribute name="border">0mm</xsl:attribute>
				<xsl:attribute name="padding">0mm</xsl:attribute>
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
				<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
			</fo:block>
		</fo:table-cell>
	</xsl:template>
	

	<!-- Déclenche tous les xf-group de l'arbre des drivers -->
	<xsl:template match="main//xf-group" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Déclenche tous les xf-textarea de l'arbre des drivers -->
	<xsl:template match="main//xf-textarea" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:for-each select="1 to $properties//TextArea/Row/DefaultSize">
			<fo:block xsl:use-attribute-sets="Line-drawing">&#160;</fo:block>
		</xsl:for-each>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Déclenche tous les ResponseElement de l'arbre des drivers -->
	<xsl:template match="main//ResponseElement" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="main//DoubleDuration" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:if test="enopdf:get-type($source-context) = 'duration'">
			<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
			<fo:inline>
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:for-each select="1 to string-length($field)">
						<xsl:choose>
							<xsl:when test="':' = substring($field,.,1)">
								<fo:inline>:</fo:inline>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="insert-image">
									<xsl:with-param name="image-name" select="'mask_number.png'"/>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</fo:block>
			</fo:inline>
		</xsl:if>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template name="insert-image">
		<xsl:param name="image-name"/>
		<fo:external-graphic>
			<xsl:attribute name="src">
				<xsl:choose>
					<xsl:when test="$properties//Images/Folder != ''">
						<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,$image-name,''')')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$image-name"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</fo:external-graphic>
	</xsl:template>

</xsl:stylesheet>
