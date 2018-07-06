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
	
	 
	<xsl:include href="../../../xslt/outputs/pdf/home-page.xsl"/>
	
	<!-- Remove all the ConsistencyCheck messages from the pdf -->
	<xsl:template match="ConsistencyCheck" mode="model"/>
	
	
	<!--Afficher le titre dans le driver FORM . 
			Définit la structure du questionnaire
			Permet aussi l'encapsulation <fo:root> -->
	<xsl:template match="Form" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="Cover-A4" page-height="297mm"
					page-width="210mm" font-family="arial" font-size="10pt" 
					font-weight="normal" margin-bottom="5mm">
					<fo:region-body margin="13mm" column-count="1"/>
					<fo:region-before region-name="xsl-region-before-cover" extent="25mm" 
						display-align="before" precedence="true"/>
					<fo:region-after extent="25mm" region-name="xsl-region-after-cover"
						display-align="before" precedence="true"/>
				</fo:simple-page-master>
				<!-- reference-orientation="90" column-count="2" -->
				<fo:simple-page-master master-name="A4-portrait" page-height="297mm" 
					page-width="210mm" font-family="arial" font-size="10pt" reference-orientation="{$properties//Format/Orientation}"
					font-weight="normal" margin-bottom="5mm">
					<fo:region-body margin="13mm" column-count="1"/>
					<fo:region-before region-name="xsl-region-before" extent="25mm"
						display-align="before" precedence="true"/>
					<fo:region-after extent="25mm" region-name="xsl-region-after"
						display-align="before" precedence="true"/>
				</fo:simple-page-master>
				
			</fo:layout-master-set>
			
			
			<!-- DEFINITION DELA PAGE DE GARDE -->
			<fo:page-sequence master-reference="Cover-A4" font-family="arial" font-size="10pt">
				<fo:flow flow-name="xsl-region-body">
					<xsl:copy-of select="eno:Home-Page($source-context)"/>
				</fo:flow>
			</fo:page-sequence>
			
			<fo:page-sequence master-reference="A4-portrait">
				<fo:static-content flow-name="xsl-region-before">
					<fo:block position="absolute" margin="10mm" text-align="right">
						<fo:external-graphic>
							<xsl:attribute name="src">
								<xsl:choose>
									<xsl:when test="$properties//Images/Folder != ''">
										<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'encoche-top-right.png'')')"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="'encoche-top-right.png'"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</fo:external-graphic>
					</fo:block>
					<fo:block position="absolute" margin-top="80%" text-align="right">
						<fo:instream-foreign-object>
							<barcode:barcode
								xmlns:barcode="http://barcode4j.krysalis.org/ns"
								message="Code Bar" orientation="90">
								<barcode:code128>
									<barcode:height>8mm</barcode:height>
								</barcode:code128>
							</barcode:barcode>
						</fo:instream-foreign-object>
					</fo:block>
				</fo:static-content>
				<fo:static-content flow-name="xsl-region-after">
					<fo:block position="absolute" margin-left="10mm" margin-top="10mm" bottom="0px" text-align="left">
						<fo:external-graphic>
							<xsl:attribute name="src">
								<xsl:choose>
									<xsl:when test="$properties//Images/Folder != ''">
										<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'encoche-bottom-left.png'')')"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="'encoche-bottom-left.png'"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
						</fo:external-graphic>
					</fo:block>
					<fo:block text-align="center">
						<fo:page-number/> / <fo:page-number-citation ref-id="TheVeryLastPage"/>
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" border-collapse="collapse"
					font-size="10pt">
					
					<!-- Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
					<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
						<xsl:with-param name="driver" select="." tunnel="yes"/>
					</xsl:apply-templates>
					
					<fo:block id="TheVeryLastPage"> </fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<!--Permet de parcourir tout l'arbre des drivers A RAJOUTER UNE FOIS -->
	<xsl:template match="*" mode="model" priority="-1">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--Afficher le titre dans le driver Module -->
	<xsl:template match="Module" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
				
		
		<fo:block xsl:use-attribute-sets="Titre-sequence" border-color="black" border-style="solid">
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!--Afficher le titre dans le driver SubModule -->
	<xsl:template match="SubModule" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
	
		<fo:block xsl:use-attribute-sets="Titre-paragraphe" keep-with-next="always"> <!-- linefeed-treatment="preserve" -->
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
	</xsl:template>

	<!-- Déclenche tous les xf-output de l'arbre des drivers 
			Definit les instructions liees au questions-->
	<!--<xsl:template match="*//Form//Module//xf-output"-->
	<xsl:template match="xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="noInstructions" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<xsl:choose>
			<xsl:when test="$noInstructions != 'YES'">
				<xsl:choose>
					<xsl:when test="enopdf:get-format($source-context) = 'footnote'">
						<fo:block><Flag178>footnote</Flag178>
							<fo:footnote>
								<fo:inline></fo:inline>
								<fo:footnote-body  xsl:use-attribute-sets="footnote">
									<fo:block>
										<fo:inline font-size="75%" 
											baseline-shift="super">
											<xsl:value-of select="enopdf:get-end-question-instructions-index($source-context)"/>
										</fo:inline>
										<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
									</fo:block>
								</fo:footnote-body>
							</fo:footnote>
						</fo:block>
					</xsl:when>
					<xsl:when test="enopdf:get-format($source-context) = 'tooltip'">
						<Flag194>tooltip</Flag194>
						<!-- Do nothing -->
					</xsl:when>
					<xsl:when test="enopdf:get-format($source-context) = 'comment' or enopdf:get-format($source-context) = 'help' or enopdf:get-format($source-context) = 'instruction'">
						<fo:block xsl:use-attribute-sets="instruction">
							<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
						</fo:block>
					</xsl:when>
					<xsl:when test="enopdf:get-format($source-context) = 'filter-alternative-text'">
						<fo:block width="100%">
							<fo:inline-container width="10%">
								<fo:block-container>
									<fo:block>
										<fo:external-graphic>
											<xsl:attribute name="src">
												<xsl:choose>
													<xsl:when test="$properties//Images/Folder != ''">
														<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'filter_arrow_25.png'')')"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="'filter_arrow_25.png'"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:attribute>
										</fo:external-graphic>
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
			</xsl:when>
		</xsl:choose>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>
	
	<!--Correctif sur l'imbrication fo:table-cell-->
	<xsl:template match="Body//xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
	
		<fo:block xsl:use-attribute-sets="general-style">
			<xsl:if test="$isTable = 'YES'">
				<xsl:attribute name="margin-left">1mm</xsl:attribute>
			</xsl:if>
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		
	</xsl:template>
	
	
	
	<!-- QUESTIONS     -->
	
	<!-- Appel driver SingleResponseQuestion : 
	   xf-input, xf-select, xf-select1 et xf-textaera ne contiennent que les réponses -->
	   
	<xsl:template match="SingleResponseQuestion" mode="model">

		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="driver" select="." tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<fo:block font-size="10pt" font-weight="bold" color="black" keep-with-next="always"> <!--linefeed-treatment="preserve"-->

		<!-- print the question label and its instructions -->
			<xsl:call-template name="eno:printQuestionTitleWithInstruction">
				<xsl:with-param name="driver" select="."/>
			</xsl:call-template>
					
		</fo:block>
		
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
 			<xsl:with-param name="typeOfAncestor" select="'question'" tunnel="yes"/> 
		</xsl:apply-templates>
		
	</xsl:template>
		


	<!-- Déclenche tous les Questions à choix multiple de l'arbre des drivers -->
	<xsl:template match="MultipleQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

			<fo:block xsl:use-attribute-sets="label-question" keep-with-next="always" > <!--linefeed-treatment="preserve"-->
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
					<xsl:with-param name="noInstructions" select="'YES'" tunnel="yes"></xsl:with-param>
				</xsl:apply-templates>
			</fo:block>
			
			<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
	</xsl:template>
	
		<!-- Déclenche tous les question multiples de l'arbre des drivers -->
	<xsl:template match="MultipleChoiceQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)" as="xs:string +"/>
			
			<fo:block xsl:use-attribute-sets="label-question" keep-with-next="always">
			
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>

				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="noInstructions" select="'YES'" tunnel="yes"/>
				</xsl:apply-templates>
			
				<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
			
			</fo:block>
		
	</xsl:template>
	
	
	
<!-- 	REPONSES -->
	
	<!-- Déclenche tous les xf-input  :  REPONSES QUI DOIVENT ETRE RENSEIGNEES DANS LE QUESTIONNAIRE-->
	<xsl:template match="xf-input" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="position" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:param name="autreHandle" tunnel="yes"/>
		
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<Label><xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/></Label>
		<Type><xsl:value-of select="enopdf:get-type($source-context)"/></Type>
		<Format><xsl:value-of select="enopdf:get-format($source-context)"/></Format>
		<Length><xsl:value-of select="enopdf:get-length($source-context)"/></Length>
		
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			
			<xsl:if test="enopdf:get-type($source-context) = 'text'">
					<xsl:choose>
						<xsl:when test="enopdf:get-format($source-context)">
							<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
								<xsl:variable name="curVal" select="."/>
								<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
									<xsl:for-each select="1 to $curVal">
										<fo:inline-container width="4mm" background-color="#CCCCCC" color="#CCCCCC" border="solid white">
											<fo:block>A</fo:block>
										</fo:inline-container>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<fo:block xsl:use-attribute-sets="Line-drawing">
								&#160;
							</fo:block>
						</xsl:otherwise>
					</xsl:choose>
					
					<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
						<xsl:with-param name="driver" select="." tunnel="yes"/>
					</xsl:apply-templates>

			</xsl:if>

			<xsl:if test="enopdf:get-type($source-context) = 'date'">
				<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
					<fo:block xsl:use-attribute-sets="general-style">
							<xsl:for-each select="1 to xs:integer(number(string-length(replace($field,'/',''))))">
								<xsl:variable name="curVal" select="."/>
								<xsl:if test="number(string-length(replace($field,'/',''))) = $curVal">
									<xsl:for-each select="1 to $curVal">
										<xsl:variable name="curVal2" select="."/>
										<fo:external-graphic>
											<xsl:attribute name="src">
												<xsl:choose>
													<xsl:when test="$properties//Images/Folder != ''">
														<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="'mask_number.png'"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:attribute>
										</fo:external-graphic>
										<xsl:if test="$curVal2 = number(string-length(replace($field,'/','')))"> (<xsl:value-of select="$field"/>) </xsl:if>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each>
					</fo:block>
					
					<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
						<xsl:with-param name="driver" select="." tunnel="yes"/>
					</xsl:apply-templates>

			</xsl:if>
			
			<xsl:if test="enopdf:get-type($source-context) = 'duration'">
			<fo:block xsl:use-attribute-sets="general-style">	 
				<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
				<fo:inline xsl:use-attribute-sets="general-style">
					<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
						<xsl:variable name="curVal" select="."/>
						<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
							<xsl:for-each select="1 to $curVal">
								<xsl:variable name="curVal2" select="."/>
								<fo:external-graphic>
									<xsl:attribute name="src">
										<xsl:choose>
											<xsl:when test="$properties//Images/Folder != ''">
												<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="'mask_number.png'"/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:attribute>
								</fo:external-graphic>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
					<fo:inline xsl:use-attribute-sets="general-style">
						<xsl:choose>
							<xsl:when test="$field = 'hh'">heures</xsl:when>
							<xsl:when test="$field = 'mm'">minutes</xsl:when>
							<xsl:otherwise><xsl:value-of select="$field"/></xsl:otherwise>
						</xsl:choose>
					</fo:inline>
				</fo:inline>
			</fo:block>
			</xsl:if>
			
			<xsl:if test="enopdf:get-type($source-context) = 'number'">
					<fo:block xsl:use-attribute-sets="general-style">
						<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
							<xsl:variable name="curVal" select="."/>
							<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
								<xsl:for-each select="1 to $curVal">
									<fo:external-graphic>
										<xsl:attribute name="src">
											<xsl:choose>
												<xsl:when test="$properties//Images/Folder != ''">
													<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
												</xsl:when>
												<xsl:otherwise>
													<xsl:value-of select="'mask_number.png'"/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:attribute>
									</fo:external-graphic>
								</xsl:for-each>
							</xsl:if>
						</xsl:for-each>
						<fo:inline><xsl:value-of select="enopdf:get-suffix($source-context, $languages[1])"/></fo:inline>
					</fo:block>
					<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
						<xsl:with-param name="driver" select="." tunnel="yes"/>
					</xsl:apply-templates>

			</xsl:if>
				
		</xsl:if>

		<xsl:if test="enopdf:get-label($source-context, $languages[1]) = ''">
			<xsl:if test="enopdf:get-type($source-context) = 'text'">
				<fo:block>
					<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
						<fo:block xsl:use-attribute-sets="label-question">
							<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>						
						</fo:block>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="enopdf:get-format($source-context)">
							<fo:block xsl:use-attribute-sets="general-style">
								<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
									<xsl:variable name="curVal" select="."/>
									<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
										<xsl:for-each select="1 to $curVal">
											<fo:external-graphic>
												<xsl:attribute name="src">
													<xsl:choose>
														<xsl:when test="$properties//Images/Folder != ''">
															<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
														</xsl:when>
														<xsl:otherwise>
															<xsl:value-of select="'mask_number.png'"/>
														</xsl:otherwise>
													</xsl:choose>
												</xsl:attribute>
											</fo:external-graphic>
										</xsl:for-each>
									</xsl:if>
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
			</xsl:if>

			<xsl:if test="enopdf:get-type($source-context) = 'date'">
				<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
				<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
					<fo:block xsl:use-attribute-sets="label-question">
						<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>						
					</fo:block>
				</xsl:if>
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:if test="$isTable = 'YES'">
						<xsl:attribute name="text-align">right</xsl:attribute>
						<xsl:attribute name="padding-top">0px</xsl:attribute>
						<xsl:attribute name="padding-bottom">0px</xsl:attribute>
					</xsl:if>
					<Flag508>Date+Label_vide</Flag508>
					
					<xsl:for-each select="1 to xs:integer(number(string-length(replace($field,'/',''))))">
								<xsl:variable name="curVal" select="."/>
								<xsl:if test="number(string-length(replace($field,'/',''))) = $curVal">
									<xsl:for-each select="1 to $curVal">
										<xsl:variable name="curVal2" select="."/>
										<fo:external-graphic>
											<xsl:attribute name="src">
												<xsl:choose>
													<xsl:when test="$properties//Images/Folder != ''">
														<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="'mask_number.png'"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:attribute>
										</fo:external-graphic>
										<xsl:if test="$curVal2 = number(string-length(replace($field,'/','')))"> (<xsl:value-of select="$field"/>) </xsl:if>
									</xsl:for-each>
								</xsl:if>
					</xsl:for-each>
					
					
				</fo:block>
			</xsl:if>
			

<!-- LES 26062018 : Suppression du code pour gérer erreur : "fo:inline" is not a valid child of "fo:flow"!  -->
<!-- 			<xsl:if test="enopdf:get-type($source-context) = 'duration'">  -->
<!-- 				<xsl:variable name="field" select="enopdf:get-format($source-context)"/> -->
				
<!-- 					<fo:inline xsl:use-attribute-sets="general-style"> -->
<!-- 						<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))"> -->
<!-- 							<xsl:variable name="curVal" select="."/> -->
<!-- 							<xsl:if test="number(enopdf:get-length($source-context)) = $curVal"> -->
<!-- 								<xsl:for-each select="1 to $curVal"> -->
<!-- 									<xsl:variable name="curVal2" select="."/> -->
 									<!--<fo:external-graphic>
 										<xsl:attribute name="src">
 											<xsl:choose>
 												<xsl:when test="$properties//Images/Folder != ''">
 													<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
 												</xsl:when>
 												<xsl:otherwise>
 													<xsl:value-of select="'mask_number.png'"/>
 												</xsl:otherwise>
 											</xsl:choose>
 										</xsl:attribute>
 									</fo:external-graphic> 
-->

<!-- 										<xsl:attribute name="src"> -->
<!-- 											<xsl:value-of select="concat($properties//Images/Folder,'mask_number.png')"/> -->
<!-- 										</xsl:attribute> -->
<!-- 									</fo:external-graphic> -->
								
								
<!-- 								</xsl:for-each> -->
<!-- 							</xsl:if> -->
<!-- 						</xsl:for-each> -->
<!-- 						<fo:inline xsl:use-attribute-sets="general-style"> -->
<!-- 							<xsl:choose> -->
<!-- 								<xsl:when test="$field = 'hh'">heures</xsl:when> -->
<!-- 								<xsl:when test="$field = 'mm'">minutes</xsl:when> -->
<!-- 								<xsl:otherwise><xsl:value-of select="$field"/></xsl:otherwise> -->
<!-- 							</xsl:choose> -->
<!-- 						</fo:inline> -->
<!-- 					</fo:inline> -->
<!-- 			</xsl:if> -->


			<xsl:if test="enopdf:get-type($source-context) = 'number'">
				<xsl:choose>
					<xsl:when test="$autreHandle">
						<fo:inline xsl:use-attribute-sets="general-style">
							<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
								<xsl:variable name="curVal" select="."/>
								<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
									<xsl:for-each select="1 to $curVal">
										<fo:external-graphic>
											<xsl:attribute name="src">
												<xsl:choose>
													<xsl:when test="$properties//Images/Folder != ''">
														<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="'mask_number.png'"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:attribute>
										</fo:external-graphic>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each>
							<fo:inline><xsl:value-of select="enopdf:get-suffix($source-context, $languages[1])"/></fo:inline>
						</fo:inline>
					</xsl:when>
					<xsl:otherwise>
						<fo:block>
							<xsl:if test="$isTable = 'YES'">
								<xsl:attribute name="text-align">right</xsl:attribute>
								<xsl:attribute name="padding-top">0px</xsl:attribute>
								<xsl:attribute name="padding-bottom">0px</xsl:attribute>
							</xsl:if>
							<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
								<fo:block xsl:use-attribute-sets="label-question">
									<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
								</fo:block>
							</xsl:if>
							<fo:block xsl:use-attribute-sets="general-style" padding-bottom="0mm" padding-top="0mm">
								<!-- if decimals in mask -->
								<xsl:choose>
									<xsl:when test="enopdf:get-number-of-decimals($source-context) != ''">
										<xsl:variable name="comaPos" select="(number(enopdf:get-length($source-context)) - number(enopdf:get-number-of-decimals($source-context)))"/>
										<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
											<xsl:variable name="curVal" select="."/>
											<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
												<xsl:for-each select="1 to $curVal">
													<xsl:variable name="curVal2" select="."/>
													<xsl:choose>
														<xsl:when test="$curVal2 = $comaPos">
															<xsl:text>,</xsl:text>
														</xsl:when>
														<xsl:otherwise>
															<fo:external-graphic>
																<xsl:attribute name="src">
																	<xsl:choose>
																		<xsl:when test="$properties//Images/Folder != ''">
																			<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
																		</xsl:when>
																		<xsl:otherwise>
																			<xsl:value-of select="'mask_number.png'"/>
																		</xsl:otherwise>
																	</xsl:choose>
																</xsl:attribute>
															</fo:external-graphic>
														</xsl:otherwise>
													</xsl:choose>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:when>
									<!-- if not decimals -->
									<xsl:otherwise>
										<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
											<xsl:variable name="curVal" select="."/>
											<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
												<xsl:for-each select="1 to $curVal">
													<fo:external-graphic>
														<xsl:attribute name="src">
															<xsl:choose>
																<xsl:when test="$properties//Images/Folder != ''">
																	<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
																</xsl:when>
																<xsl:otherwise>
																	<xsl:value-of select="'mask_number.png'"/>
																</xsl:otherwise>
															</xsl:choose>
														</xsl:attribute>
													</fo:external-graphic>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:otherwise>
								</xsl:choose>
								<fo:inline><xsl:value-of select="enopdf:get-suffix($source-context, $languages[1])"/></fo:inline>
							</fo:block>
						</fo:block>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			
		</xsl:if>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
			<xsl:with-param name="noInstructions" select="'YES'" tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>


	<!-- Déclenche tous les xf-item de l'arbre des drivers -->
	<xsl:template match="xf-item" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<xsl:variable name="image">
			<xsl:value-of select="enopdf:get-image($source-context)"/>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="enopdf:get-image($source-context) != ''" >
				<xsl:choose>
					<xsl:when test="starts-with($image,'http')">
<!-- 						<xsl:value-of select="concat('<fo:external-graphic src=\"',$image,'\" title=\"',eno:serialize(enopdf:get-label($source-context, $languages[1])),'\" /&gt;')"/> -->
							<fo:inline font-family="ZapfDingbats" font-size="10pt" padding="5mm" >&#x274F;</fo:inline>
							<fo:external-graphic padding-right="3mm">
								<xsl:attribute name="src">
									<xsl:value-of select="$image"/>
								</xsl:attribute>
							</fo:external-graphic>
					</xsl:when>
					<xsl:otherwise>
<!-- 						 <xsl:value-of select="concat('&lt;fo:external-graphic src=&quot;/',$properties//Images/Folder,'/',$image,'&quot; title=&quot;',eno:serialize(enopdf:get-label($source-context, $languages[1])),'&quot; /&gt;')"/> -->
<!-- 							<fo:inline font-family="ZapfDingbats" font-size="10pt" -->
<!-- 								margin-top="3mm">&#x274F;fo:inline> -->
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
								<Flag700>No border_label_vide</Flag700>
								<fo:inline font-family="ZapfDingbats" font-size="10pt"
									margin-top="3mm">
									&#x274F;
								</fo:inline>
								<fo:inline>
									<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
								</fo:inline>
								<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
									<xsl:with-param name="driver" select="." tunnel="yes"/>
								</xsl:apply-templates>
							</xsl:when>
							<xsl:otherwise>
							<Flag712>No border_label_plein</Flag712>
								<fo:block font-family="ZapfDingbats" text-align="center" font-size="10pt" padding-right="4mm" padding-left="6mm"
									margin-top="3mm">
									&#x274F;
									<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
										<xsl:with-param name="driver" select="." tunnel="yes"/>
									</xsl:apply-templates>
								</fo:block>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<Flag725>Avec Border</Flag725>
						<fo:block>
							<fo:inline font-family="ZapfDingbats" font-size="10pt" padding-right="5mm"
								margin-top="3mm">&#x274F;</fo:inline>
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
	<xsl:template match="xf-select" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		

			<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE  -->
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
				<xsl:with-param name="noInstructions" select="'YES'" tunnel="yes"/>
			</xsl:apply-templates>
			
			<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
			
			<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			
			</xsl:if>

	</xsl:template>
	

	<!-- Déclenche tous les xf-select de l'arbre des drivers -->
	<xsl:template match="xf-select1" mode="model">
		
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="position" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		<xsl:variable name="format" select="enopdf:get-appearance($source-context)"/>
					
			<xsl:choose>
				<xsl:when test="$format = 'minimal'">
					<fo:block xsl:use-attribute-sets="Line-drawing">
						&#160;
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE  -->
					<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
						<xsl:with-param name="driver" select="." tunnel="yes"/>
						<xsl:with-param name="noInstructions" select="'YES'" tunnel="yes"/>
						<xsl:with-param name="autreHandle" select="'YES'" tunnel="yes"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>

		
		<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
	</xsl:template>
	
	<!-- Déclenche tous les Table de l'arbre des drivers -->
	<xsl:template match="Table" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
			
		<fo:block xsl:use-attribute-sets="label-question" keep-with-next="always">
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>	
					
			<!-- FLAG Recupérer les caractéristiques du tableau pour le construire dynamiquement -->
	
			<!--<NBHeaderCols><xsl:value-of select="count(enopdf:get-header-columns($source-context))"/></NBHeaderCols>
				<NBHeaderLines><xsl:value-of select="count(enopdf:get-header-lines($source-context))"/></NBHeaderLines>	
				<NBHeaderLine><xsl:value-of select="count(enopdf:get-header-line($source-context, position()))"/></NBHeaderLine>
				<NBBodyLines><xsl:value-of select="count(enopdf:get-body-lines($source-context))"/></NBBodyLines>
				<NBBodyLine><xsl:value-of select="count(enopdf:get-body-line($source-context, position()))"/></NBBodyLine>-->
	
	<!-- On fixe le nombre max de ligne dans un tableau (DefaultSize) pour pagination -->
		<xsl:variable name="line-number" select="count(enopdf:get-body-lines($source-context))"/>
		<xsl:variable name="maxlines-by-table" select="$properties//Table/Row/DefaultSize"/>
		<xsl:variable name="table-pages" select="xs:integer(1+(($line-number -1) div $maxlines-by-table))" as="xs:integer"/>
		
		<xsl:for-each select="1 to $table-pages">
			<xsl:variable name="page-position" select="position()"/>
	
			<fo:block page-break-inside="avoid">
				<xsl:if test="$line-number &gt;= $maxlines-by-table">
					<xsl:attribute name="page-break-after" select="'always'"/>
				</xsl:if>
				<fo:table inline-progression-dimension="auto" table-layout="fixed" width="100%" font-size="10pt" border-width="0.35mm"
					text-align="center" margin-top="1mm" display-align="center" space-after="5mm">
	
				<!--Gestion du header-->
					<xsl:if test="count(enopdf:get-header-lines($source-context)) != 0">
						<fo:table-header>
						<!-- Récupére le nombre de header-lines = Nombre de lignes dans le tableau -->
							<xsl:for-each select="enopdf:get-header-lines($source-context)">
								<fo:table-row xsl:use-attribute-sets="entete-ligne" text-align="center">
								<!--<NBHeaderCols><xsl:value-of select="count(enopdf:get-header-columns($source-context))"/></NBHeaderCols>
										<NBHeaderLines><xsl:value-of select="count(enopdf:get-header-lines($source-context))"/></NBHeaderLines>
										<NBHeaderLine><xsl:value-of select="count(enopdf:get-header-line($source-context, position()))"/></NBHeaderLine>
										<NBBodyLines><xsl:value-of select="count(enopdf:get-body-lines($source-context))"/></NBBodyLines>
										<NBBodyLine><xsl:value-of select="count(enopdf:get-body-line($source-context, position()))"/></NBBodyLine>
										<NBRowspan><xsl:value-of select="enopdf:get-rowspan($source-context)"/></NBRowspan>
										<NBColspan><xsl:value-of select="enopdf:get-colspan($source-context)"/></NBColspan>-->
	
								<!-- Dans un for-each, la fonction position() renvoie la position de l'élément dans l'arbre temporaire créé dans le select du for-each -->
									<xsl:apply-templates
										select="enopdf:get-header-line($source-context, position())"
										mode="source">
										<xsl:with-param name="driver" select="." tunnel="yes"/>
										<xsl:with-param name="header"  select="'YES'" tunnel="yes"/>
										<xsl:with-param name="no-border" select="enopdf:get-style($source-context)" tunnel="yes"/>
									</xsl:apply-templates>
								<!-- Pour chaque boucle , on récupére les infos du header -->
								</fo:table-row>
							</xsl:for-each>
						</fo:table-header>
					</xsl:if>
	
				<!--Gestion du body-->
					<fo:table-body>
						<xsl:for-each select="enopdf:get-body-lines($source-context)">
							<xsl:variable name="position" select="position()"/>
							<xsl:if test="($position &gt; $maxlines-by-table*($page-position -1)) and ($position &lt;= $maxlines-by-table*$page-position)" >
								<fo:table-row border-color="black" >
									<!--<NBHeaderCols><xsl:value-of select="count(enopdf:get-header-columns($source-context))"/></NBHeaderCols>
								<NBHeaderLines><xsl:value-of select="count(enopdf:get-header-lines($source-context))"/></NBHeaderLines>
								<NBHeaderLine><xsl:value-of select="count(enopdf:get-header-line($source-context, position()))"/></NBHeaderLine>
								<NBBodyLines><xsl:value-of select="count(enopdf:get-body-lines($source-context))"/></NBBodyLines>
								<NBBodyLine><xsl:value-of select="count(enopdf:get-body-line($source-context, position()))"/></NBBodyLine>
								<NBRowspan><xsl:value-of select="enopdf:get-rowspan($source-context)"/></NBRowspan>
								<NBColspan><xsl:value-of select="enopdf:get-colspan($source-context)"/></NBColspan>-->

									<xsl:apply-templates select="enopdf:get-body-line($source-context, position())" mode="source">
										<xsl:with-param name="driver" select="." tunnel="yes"/>
										<xsl:with-param name="isTable" select="'YES'" tunnel="yes"/>
										<xsl:with-param name="row-number"  select="position()" tunnel="yes"/>
										<xsl:with-param name="no-border" select="enopdf:get-style($source-context)" tunnel="yes"/>
									</xsl:apply-templates>
							
							<!-- Pour chaque boucle , on récupére les infos des lignes du tableau -->
								</fo:table-row>
							</xsl:if>
						</xsl:for-each>
					</fo:table-body>
				</fo:table>
			</fo:block>
		</xsl:for-each>
		
		<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
				
	</xsl:template>

	<!--TableLoop renvoie seulment des headers avec des lignes vierges en des- (rowloop est dupliqué) -->
	<xsl:template match="TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="instance-ancestor" tunnel="yes"/>
				
		<fo:block xsl:use-attribute-sets="label-question" keep-with-next="always">
					<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
				</fo:block>		
				
				<!-- FLAG Recupérer les caractéristiques du tableau pour le construire dynamiquement -->
				
				<!--<NBHeaderCols><xsl:value-of select="count(enopdf:get-header-columns($source-context))"/></NBHeaderCols>
				<NBHeaderLines><xsl:value-of select="count(enopdf:get-header-lines($source-context))"/></NBHeaderLines>	
				<NBHeaderLine><xsl:value-of select="count(enopdf:get-header-line($source-context, position()))"/></NBHeaderLine>
				<NBBodyLines><xsl:value-of select="count(enopdf:get-body-lines($source-context))"/></NBBodyLines>
				<NBBodyLine><xsl:value-of select="count(enopdf:get-body-line($source-context, position()))"/></NBBodyLine>-->
				
				<!-- On fixe le nombre max de ligne dans un tableau (DefaultSize) pour pagination -->
				<xsl:variable name="line-number" select="count(enopdf:get-body-lines($source-context))"/>
				<xsl:variable name="table-pages" select="xs:integer(1+(($line-number -1) div $properties//Table/Row/DefaultSize))" as="xs:integer"/>
				<xsl:for-each select="1 to $table-pages">
				<xsl:variable name="page-position" select="position()"/>
					<fo:block  page-break-inside="avoid">
				
				<xsl:if test="$line-number &gt;= $properties//Table/Row/DefaultSize">
					<xsl:attribute name="page-break-after" select="'always'"/>
				</xsl:if>
				
					<fo:table inline-progression-dimension="auto" font-size="10pt" table-layout="fixed" width="100%" border-width="0.35mm"
					text-align="center" display-align="center" space-after="5mm">
					
					<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
<!-- 					<xsl:variable name="ancestors"> -->
<!-- 						<xsl:copy-of select="xs:integer(root(.))"/> -->
<!-- 					</xsl:variable> -->
					
		<!-- Debut boucle page-break after -->								
					<!--Gestion du header-->
					<xsl:if test="count(enopdf:get-header-lines($source-context)) != 0">
						
						<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
<!-- 						<xsl:variable name="ancestors"> -->
<!-- 							<xsl:copy-of select="root(.)"/> -->
<!-- 						</xsl:variable> -->
						
						<fo:table-header>
							<!-- Récupére le nombre de header-lines = Nombre de lignes dans le tableau -->
							<xsl:for-each select="enopdf:get-header-lines($source-context)">
								<fo:table-row xsl:use-attribute-sets="entete-ligne" text-align="center">
									
									<!-- Dans un for-each, la fonction position() renvoie la position de l'élément dans l'arbre temporaire créé dans le select du for-each -->
									<xsl:apply-templates
										select="enopdf:get-header-line($source-context, position())"
										mode="source">
										<xsl:with-param name="driver" select="." tunnel="yes"/>
										<xsl:with-param name="header"  select="'YES'" tunnel="yes"/>
										<xsl:with-param name="no-border" select="enopdf:get-style($source-context)" tunnel="yes"/>
									</xsl:apply-templates>
									<!-- Pour chaque boucle , on récupére les infos du header -->
								</fo:table-row>
							</xsl:for-each>
						</fo:table-header>
					</xsl:if>
					
					<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
<!-- 					<xsl:variable name="ancestors"> -->
<!-- 						<xsl:copy-of select="root(.)"/> -->
<!-- 					</xsl:variable> -->
					
					<!--Gestion du body-->
					<fo:table-body>
						<xsl:for-each select="1 to xs:integer(enopdf:get-rooster-number-lines($source-context))">
<!-- 						<xsl:for-each select="1 to xs:integer(enopdf:get-maximum-lines($source-context))"> -->
							<xsl:for-each select="enopdf:get-body-lines($source-context)">
							
							<xsl:variable name="position" select="position()"/>
							<xsl:if test="($position &gt; $properties//Table/Row/DefaultSize*($page-position -1)) and ($position &lt;= $properties//Table/Row/DefaultSize*$page-position)" >
							
								<fo:table-row border-color="black" >
									<!--<NBHeaderCols><xsl:value-of select="count(enopdf:get-header-columns($source-context))"/></NBHeaderCols>
									<NBHeaderLines><xsl:value-of select="count(enopdf:get-header-lines($source-context))"/></NBHeaderLines>
									<NBHeaderLine><xsl:value-of select="count(enopdf:get-header-line($source-context, position()))"/></NBHeaderLine>
									<NBBodyLines><xsl:value-of select="count(enopdf:get-body-lines($source-context))"/></NBBodyLines>
									<NBBodyLine><xsl:value-of select="count(enopdf:get-body-line($source-context, position()))"/></NBBodyLine>
									<NBRowspan><xsl:value-of select="enopdf:get-rowspan($source-context)"/></NBRowspan>
									<NBColspan><xsl:value-of select="enopdf:get-colspan($source-context)"/></NBColspan>-->
									<xsl:apply-templates
										select="enopdf:get-body-line($source-context, position())" mode="source">
										<xsl:with-param name="driver" select="." tunnel="yes"/>
										<xsl:with-param name="no-border" select="enopdf:get-style($source-context)" tunnel="yes"/>
									</xsl:apply-templates>
									<!-- Pour chaque boucle , on récupére les infos des lignes du tableau -->
								</fo:table-row>
								
								</xsl:if>
								
							</xsl:for-each>
						</xsl:for-each>
					</fo:table-body>
				</fo:table>
				
				</fo:block>
				</xsl:for-each>
				
				<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>

	</xsl:template>

	<!-- Déclenche tous les TextCell de l'arbre des drivers -->
	<xsl:template match="TextCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="header" tunnel="yes"/>
		<xsl:param name="row-number" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		
		<!--FLAG-->
		<!--<TextCell/>
		<NBRowspan><xsl:value-of select="enopdf:get-rowspan($source-context)"/></NBRowspan>
		<NBColspan><xsl:value-of select="enopdf:get-colspan($source-context)"/></NBColspan>-->
		<!--<CodeDepth><xsl:value-of select="enopdf:get-code-depth($source-context)"/></CodeDepth>-\->-->
		
		<fo:table-cell xsl:use-attribute-sets="colonne-tableau"
			number-rows-spanned="{enopdf:get-rowspan($source-context)}"
			number-columns-spanned="{enopdf:get-colspan($source-context)}">
			
			<xsl:if test="$header">
				<xsl:attribute name="text-align">center</xsl:attribute>
			</xsl:if>
			<xsl:if test="$no-border = 'no-border'">
				<xsl:attribute name="border">0mm </xsl:attribute>
				<xsl:attribute name="padding">0mm </xsl:attribute>
			</xsl:if>
			
			<!-- A new virtual tree is created as driver -->
			<xsl:variable name="new-driver">
				<Body>
					<xf-output/>
				</Body>
			</xsl:variable>
			<!-- This new driver is applied on the same source-context -->
			<xsl:apply-templates select="$new-driver//xf-output" mode="model"/>
			
		</fo:table-cell>
		<!--<FinTextCell/>-->

	</xsl:template>


	<!-- Déclenche tous les Cell de l'arbre des drivers -->
	<xsl:template match="Cell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="no-border" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
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
	<xsl:template match="EmptyCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		<!--FLAG-->
		<!--<EmptyCell/>-->
		<fo:table-cell background-color="#CCCCCC" border-color="black" border-style="solid">
			<!--<xsl:if test="enopdf:get-label($source-context,$languages[1]) !=''">-->
			<fo:block>
				<!--<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>-->
				 </fo:block>
		</fo:table-cell>
		<!--</xsl:if>-->

		<xsl:if test="enopdf:get-colspan($source-context) = '2'">
			<fo:table-cell background-color="#CCCCCC" border-color="black" border-style="solid">
				<fo:block>  </fo:block>
			</fo:table-cell>
		</xsl:if>


		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!-- Déclenche tous les xf-group de l'arbre des drivers -->
	<xsl:template match="xf-group" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!-- Déclenche tous les xf-textarea de l'arbre des drivers -->
	<xsl:template match="xf-textarea" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
					
			<fo:block xsl:use-attribute-sets="Line-drawing">
				&#160;
			</fo:block>
			<fo:block xsl:use-attribute-sets="Line-drawing">
				&#160;
			</fo:block>
			<fo:block xsl:use-attribute-sets="Line-drawing">
				&#160;
			</fo:block>
			<fo:block xsl:use-attribute-sets="Line-drawing">
				&#160;
			</fo:block>
			<fo:block xsl:use-attribute-sets="Line-drawing">
				&#160;
			</fo:block>
			<xsl:apply-templates select="enopdf:get-end-question-instructions($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
				
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!-- Déclenche tous les ResponseElement de l'arbre des drivers -->
	<xsl:template match="ResponseElement" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>
				

	<xsl:template match="DoubleDuration" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<xsl:if test="enopdf:get-type($source-context) = 'duration'"> 
			<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
			<fo:inline>
				<xsl:for-each select="1 to string-length($field)">
					<xsl:variable name="curVal" select="."/>
					<xsl:if test="string-length($field) = $curVal">
						<fo:block xsl:use-attribute-sets="general-style">
							<xsl:for-each select="1 to $curVal">
								<xsl:variable name="curVal2" select="."/>
								<xsl:choose>
									<xsl:when test="':' = substring($field,$curVal2,1)">
										<fo:inline>:</fo:inline>
									</xsl:when>
									<xsl:otherwise>
										<fo:external-graphic>
											<xsl:attribute name="src">
												<xsl:choose>
													<xsl:when test="$properties//Images/Folder != ''">
														<xsl:value-of select="concat('url(''file:',$properties//Images/Folder,'mask_number.png'')')"/>
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="'mask_number.png'"/>
													</xsl:otherwise>
												</xsl:choose>
											</xsl:attribute>
										</fo:external-graphic>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:for-each>
						</fo:block>
					</xsl:if>
				</xsl:for-each>
			</fo:inline>
		</xsl:if>
		
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
	</xsl:template>
		
	<xsl:function name="eno:printQuestionTitleWithInstruction" >
		<xsl:param name="source" tunnel="no"/>
		<xsl:param name="language" tunnel="no"/>
		<xsl:param name="driver" tunnel="no"/>

		<xsl:if test="enopdf:get-label($source, $language) != ''">
			<xsl:if test="enopdf:get-before-question-title-instructions($source) != ''">
				<fo:block>
					<xsl:apply-templates select="enopdf:get-before-question-title-instructions($source)" mode="source">
						<xsl:with-param name="driver" select="$driver" tunnel="yes"/>
					</xsl:apply-templates>
				</fo:block>				
			</xsl:if>
			<fo:block xsl:use-attribute-sets="label-question">
				<xsl:copy-of select="enopdf:get-label($source, $language)"/>
			</fo:block>
			<fo:block>
				<xsl:apply-templates select="enopdf:get-after-question-title-instructions($source)" mode="source">
					<xsl:with-param name="driver" select="$driver" tunnel="yes"/>
				</xsl:apply-templates>
			</fo:block>
		</xsl:if>
	</xsl:function>

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
	
		<xsl:apply-templates select="enopdf:get-before-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="$driver"/>
		</xsl:apply-templates>	
		
				
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
				<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
		</xsl:if>
		
		<!-- The enoddi:get-instructions-by-format getter produces in-language fragments, on which templates must be applied in "source" mode. -->
		
		<xsl:apply-templates select="enopdf:get-after-question-title-instructions($source-context)" mode="source">
			<xsl:with-param name="driver" select="$driver"/>
		</xsl:apply-templates>
		
	</xsl:template>
	
</xsl:stylesheet>
