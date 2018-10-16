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
	<xsl:param name="parameters-file"/>
	<xsl:param name="parameters-node" as="node()" required="no">
		<empty/>
	</xsl:param>
	
	<xd:doc>
		<xd:desc>
			<xd:p>The properties and parameters files are charged as xml trees.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:variable name="properties" select="doc($properties-file)"/>
	<xsl:variable name="parameters">
		<xsl:choose>
			<xsl:when test="$parameters-node/*">
				<xsl:copy-of select="$parameters-node"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="doc($parameters-file)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<xd:doc>
		<xd:desc>Variables from propertiers and parameters</xd:desc>
	</xd:doc>
	<xsl:variable name="orientation">
		<xsl:choose>
			<xsl:when test="$parameters//Format/Orientation != ''">
				<xsl:value-of select="$parameters//Format/Orientation"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$properties//Format/Orientation"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="column-count">
		<xsl:choose>
			<xsl:when test="$parameters//Format/Columns != ''">
				<xsl:value-of select="$parameters//Format/Columns"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$properties//Format/Columns"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="roster-defaultsize">
		<xsl:choose>
			<xsl:when test="$parameters//Roster/Row/DefaultSize != ''">
				<xsl:value-of select="$parameters//Roster/Row/DefaultSize"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$properties//Roster/Row/DefaultSize"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="table-defaultsize">
		<xsl:choose>
			<xsl:when test="$parameters//Table/Row/DefaultSize != ''">
				<xsl:value-of select="$parameters//Table/Row/DefaultSize"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$properties//Table/Row/DefaultSize"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="textarea-defaultsize">
		<xsl:choose>
			<xsl:when test="$parameters//TextArea/Row/DefaultSize != ''">
				<xsl:value-of select="$parameters//TextArea/Row/DefaultSize"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$properties//TextArea/Row/DefaultSize"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="images-folder">
		<xsl:choose>
			<xsl:when test="$parameters//Images/Folder != ''">
				<xsl:value-of select="$parameters//Images/Folder"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$properties//Images/Folder"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="numeric-capture">
		<xsl:choose>
			<xsl:when test="$parameters//Capture/Numeric != ''">
				<xsl:value-of select="$parameters//Capture/Numeric"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$properties//Capture/Numeric"/>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:variable>
	
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
				<fo:simple-page-master master-name="A4-portrait" page-height="297mm"
					page-width="210mm" font-family="arial" font-size="10pt" reference-orientation="{$orientation}"
					font-weight="normal" margin-bottom="5mm">
					<fo:region-body margin="13mm" column-count="{$column-count}"/>
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
					<xsl:if test="$orientation='0'">
						<fo:block position="absolute" margin-top="65%" text-align="right" margin-right="4mm">
							<fo:instream-foreign-object>
								<barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
									orientation="90">
									<xsl:attribute name="message" select="'${idQuestionnaire} - #page-number#'"/>
									<barcode:code128>
										<barcode:height>8mm</barcode:height>
										<barcode:human-readable>
											<barcode:placement>none</barcode:placement>
										</barcode:human-readable>
									</barcode:code128>
								</barcode:barcode>
							</fo:instream-foreign-object>
							<fo:block-container reference-orientation="90" margin-left="5mm">
								<fo:block text-align="left" font-size="8pt">${idQuestionnaire} - <fo:page-number/></fo:block>
							</fo:block-container>
						</fo:block>
					</xsl:if>
					
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
					<xsl:if test="$orientation='90'">
						<fo:block-container text-align="left" absolute-position="absolute" left="10mm" top="20mm">
							<fo:block>
								<fo:instream-foreign-object>
									<barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns">
										<xsl:attribute name="message" select="'${idQuestionnaire} - #page-number#'"/>
										<barcode:code128>
											<barcode:height>8mm</barcode:height>
											<barcode:human-readable>
												<barcode:placement>none</barcode:placement>
											</barcode:human-readable>
										</barcode:code128>
									</barcode:barcode>
								</fo:instream-foreign-object>
							</fo:block>
						</fo:block-container>
						<fo:block-container absolute-position="absolute" right="20mm" top="20mm">
							<fo:block-container>
								<fo:block text-align="right" font-size="8pt">${idQuestionnaire} - <fo:page-number/>
								</fo:block>
							</fo:block-container>
						</fo:block-container>
					</xsl:if>
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
		
		<fo:block xsl:use-attribute-sets="Titre-sequence" border-color="black" border-style="solid" page-break-inside="avoid" keep-with-next="always">
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
		
		<fo:block xsl:use-attribute-sets="Titre-paragraphe" page-break-inside="avoid" keep-with-next="always"> <!-- linefeed-treatment="preserve" -->
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
				<fo:block xsl:use-attribute-sets="instruction" page-break-inside="avoid" keep-with-next="always">
					<xsl:copy-of select="$label"/>
				</fo:block>
			</xsl:when>
			<xsl:when test="$format = ('filter-alternative-text','flowcontrol-text')">
				<xsl:if test="$label != ''">
					<fo:block width="100%" page-break-inside="avoid" keep-with-previous="always">
						<fo:inline-container width="10%" vertical-align="bottom" text-align="right">
							<fo:block-container>
								<fo:block>
									<xsl:call-template name="insert-image">
										<xsl:with-param name="image-name" select="'filter_arrow.png'"/>
									</xsl:call-template>
								</fo:block>
							</fo:block-container>
						</fo:inline-container>
						<fo:inline-container width="87%">
							<fo:block xsl:use-attribute-sets="filter-alternative" width="100%">
								<xsl:copy-of select="$label"/>
							</fo:block>
						</fo:inline-container>
					</fo:block>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message select="concat('unknown xf-output : ',enopdf:get-name($source-context),$label)"/>
				<fo:block xsl:use-attribute-sets="general-style" page-break-inside="avoid" keep-with-next="always">
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

	<!-- QUESTIONS -->
	<xd:doc>
		<xd:desc>Questions with responses which are not in a table</xd:desc>
	</xd:doc>
	<xsl:template match="main//SingleResponseQuestion | main//MultipleQuestion | main//MultipleChoiceQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>

		<!--<xsl:apply-templates select="enopdf:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>-->
		<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always"> <!--linefeed-treatment="preserve"-->
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
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		
		<xsl:variable name="input-type" select="enopdf:get-type($source-context)"/>
		<xsl:variable name="length" select="enopdf:get-length($source-context)"/>
		
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			<fo:block font-size="10pt" font-weight="bold" color="black"> <!--linefeed-treatment="preserve"-->
				<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
			</fo:block>
		</xsl:if>
		
		<xsl:choose>
			<xsl:when test="$input-type = 'text'">
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
			</xsl:when>
			<xsl:when test="$input-type = 'number'">
				<xsl:variable name="length" select="number(enopdf:get-length($source-context))"/>
				<fo:block>
					<xsl:if test="$isTable = 'YES'">
						<xsl:attribute name="text-align">right</xsl:attribute>
						<xsl:attribute name="padding-top">0px</xsl:attribute>
						<xsl:attribute name="padding-bottom">0px</xsl:attribute>
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
								<fo:inline-container>
									<xsl:attribute name="width" select="concat(string($length*3),'mm')"/>
									<fo:block-container height="8mm">
										<xsl:attribute name="width" select="concat(string($length*3),'mm')"/>
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
			</xsl:when>
			<xsl:when test="$input-type = 'date'">
				<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:if test="$isTable = 'YES'">
						<xsl:attribute name="text-align">right</xsl:attribute>
						<xsl:attribute name="padding-top">0px</xsl:attribute>
						<xsl:attribute name="padding-bottom">0px</xsl:attribute>
					</xsl:if>
					<xsl:call-template name="insert-image">
						<xsl:with-param name="image-name" select="'date.png'"/>
					</xsl:call-template>
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
			<xsl:when test="$no-border = 'no-border'">
				<fo:inline>
					<!--<fo:inline font-family="ZapfDingbats" font-size="10pt" padding-before="5mm" padding-after="1mm" wrap-option="inherit">&#x274F;</fo:inline>-->
					<!--<fo:inline font-family="Arial" font-size="15pt" margin-left="5mm" margin-right="1mm" wrap-option="inherit" margin-bottom="10mm">&#9633;</fo:inline>-->
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
							<fo:inline padding="1mm" text-align="left">
								<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
							</fo:inline>
						</xsl:otherwise>
					</xsl:choose>
				</fo:inline>
			</xsl:when>
			<xsl:otherwise>
				<fo:list-item>
					<fo:list-item-label end-indent="label-end()">
						<fo:block text-align="right">
							<!--<fo:inline font-family="ZapfDingbats" font-size="10pt" padding="5mm">&#x274F;</fo:inline>-->
							<!--<fo:inline font-family="Arial" font-size="15pt" padding="4mm" baseline-shift="super">&#9633;</fo:inline>-->
							<xsl:call-template name="insert-image">
								<xsl:with-param name="image-name" select="'check_case.png'"/>
							</xsl:call-template>
						</fo:block>
					</fo:list-item-label>
					<fo:list-item-body start-indent="body-start()">
						<xsl:choose>
							<xsl:when test="$image != ''">
								<xsl:call-template name="insert-image">
									<xsl:with-param name="image-name" select="$image"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<fo:block>
									<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
								</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</fo:list-item-body>
				</fo:list-item>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Déclenche tous les xf-select de l'arbre des drivers -->
	<xsl:template match="main//xf-select1 | main//xf-select" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		
		<xsl:choose>
			<xsl:when test="enopdf:get-appearance($source-context) = 'minimal'">
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
	
	<!-- Déclenche tous les Table de l'arbre des drivers -->
	<xsl:template match="main//Table | main//TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<xsl:variable name="current-match" select="."/>
		<xsl:variable name="no-border" select="enopdf:get-style($source-context)"/>
		<xsl:variable name="total-lines" as="xs:integer">
			<xsl:choose>
				<xsl:when test="self::Table">
					<xsl:value-of select="count(enopdf:get-body-lines($source-context))"/>
				</xsl:when>
				<xsl:when test="enopdf:get-maximum-lines($source-context)">
					<xsl:value-of select="number(enopdf:get-maximum-lines($source-context))"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="number($roster-defaultsize) -1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="maxlines-by-table" as="xs:integer">
			<xsl:choose>
				<xsl:when test="self::Table">
					<xsl:value-of select="number($table-defaultsize)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="number($roster-defaultsize)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- The table in the first page contains 1 line less than next ones -->
		<xsl:variable name="table-pages" select="xs:integer(1+(($total-lines -1+1) div $maxlines-by-table))" as="xs:integer"/>
		
		<!--<xsl:apply-templates select="enopdf:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>-->
		<fo:block xsl:use-attribute-sets="label-question" page-break-inside="avoid" keep-with-next="always">
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<xsl:apply-templates select="enopdf:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="."/>
		</xsl:apply-templates>

		<!-- long tables are split : $maxlines-by-table lines maximum, except the first one which has 1 less -->
		<xsl:for-each select="1 to $table-pages">
			<xsl:variable name="page-position" select="position()"/>
			<fo:block page-break-inside="avoid">
				<xsl:choose>
					<xsl:when test="$total-lines &gt; $maxlines-by-table -1">
						<xsl:attribute name="id" select="concat(enopdf:get-business-name($source-context),'-',$page-position)"/>		
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="id" select="enopdf:get-business-name($source-context)"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="$current-match/name()='TableLoop' and $total-lines &gt; $maxlines-by-table -1">
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
								<xsl:variable name="first-line" select="$maxlines-by-table*($page-position -1)"/>
								<xsl:variable name="last-line" select="$maxlines-by-table*($page-position) -1"/>
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
								<xsl:for-each select="1 to $maxlines-by-table">
									<!-- if the dynamic table is on several pages, each page contains maxlines-by-table, except the first one, which has maxlines-by-table -1 -->
									<xsl:if test="$page-position &gt; 1 or (. &lt;= $total-lines and . &lt; $maxlines-by-table)">
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

		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Déclenche tous les xf-textarea de l'arbre des drivers -->
	<xsl:template match="main//xf-textarea" mode="model">
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

	<!-- external variables : do nothing -->
	<xsl:template match="main//ResponseElement" mode="model"/>
	

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
