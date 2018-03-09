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
	
	<xsl:import href="../../../styles/style.xsl"/>

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
	<xsl:variable name="footnote-count" select="0"/>
	
	<!--Afficher le titre dans le drvier FORM . Permet aussi l'encapsulation <fo:root> -->
	<xsl:template match="Form" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:root>
			
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4-portrail" page-height="297mm"
					page-width="210mm" font-family="arial" font-size="10pt" 
					font-weight="normal" margin-bottom="5mm">
					<fo:region-body margin="13mm"/>
					<fo:region-before region-name="xsl-region-before" extent="25mm" margin="10mm"
						display-align="before" precedence="true"/>
					<fo:region-after extent="25mm" region-name="xsl-region-after"
						display-align="before" precedence="true"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="A4-portrail" font-family="arial" font-size="10pt">
				<fo:flow flow-name="xsl-region-body">
					<fo:block page-break-inside="avoid">
						<fo:block width="100%" >
							<fo:inline-container width="72mm">
								<fo:block-container height="20mm" max-height="20mm" overflow="hidden">
									<fo:block>
										<fo:external-graphic height="5mm" src="./Images/logo-insee-header.png"></fo:external-graphic>
									</fo:block>
								</fo:block-container>
							</fo:inline-container>
							<fo:inline-container width="13mm">
								<fo:block height="20mm">
									&#160;
								</fo:block>
							</fo:inline-container>
							<fo:inline-container width="97mm">
								<fo:block-container height="20mm" overflow="hidden">
									<fo:block font-weight="bold" font-size="18pt" font-family="arial" margin="3mm">
										<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
									</fo:block>
								</fo:block-container>
							</fo:inline-container>
						</fo:block>
						
						<fo:block width="100%" margin-top="7mm" font-family="arial" font-size="10pt">
							<fo:inline-container width="72mm">
								<fo:block-container height="35mm" overflow="hidden">
									<fo:block margin-left="3mm" margin-right="3mm" margin-top="1mm" margin-bottom="1mm">
										<fo:block font-weight="bold">Unité enquêtée</fo:block>
										<fo:block>Identifiant : $identifiant</fo:block>
										<fo:block>Raison sociale : $RS</fo:block>
										<fo:block>Adresse :</fo:block>
										<fo:block>$adresse_rep_L1</fo:block>
										<fo:block>$adresse_rep_L2</fo:block>
										<fo:block>$adresse_rep_L3</fo:block>
										<fo:block>$adresse_rep_L4</fo:block>
									</fo:block>
								</fo:block-container>
							</fo:inline-container>
							<fo:inline-container width="13mm">
								<fo:block height="20mm">
									&#160;
								</fo:block>
							</fo:inline-container>
							<fo:inline-container width="97mm" height="35mm">
								<fo:block-container height="35mm" overflow="hidden">
									<fo:block margin-left="3mm" margin-right="3mm" margin-top="1mm" margin-bottom="1mm">
										<fo:block font-weight="bold">Contacter l'assistance</fo:block>
										<fo:block>Par téléphone</fo:block>
										<fo:block>$telephone1</fo:block>
										<fo:block>$telephone_SVI_1</fo:block>
										<fo:block>Par Mail :</fo:block>
										<fo:block>$mail_gestionnaire</fo:block>								
									</fo:block>
								</fo:block-container>
							</fo:inline-container>
						</fo:block>
						
						<fo:block width="100%" margin-top="5mm" font-family="arial" font-size="10pt">
							<fo:inline-container width="72mm">
								<fo:block-container height="35mm" >
									<fo:block margin-left="3mm" margin-right="3mm" margin-top="1mm" margin-bottom="1mm">
										<fo:block font-weight="bold">Coordonnées de la personne ayant</fo:block>
										<fo:block font-weight="bold">répondu à ce questionnaire :</fo:block>
										<fo:block>Nom : $nom_corresp</fo:block>
										<fo:block>Prénom : $prenom_corresp</fo:block>
										<fo:block>Téléphone : $tel_corresp</fo:block>
										<fo:block>Mel : $mel_corresp</fo:block>
									</fo:block>
								</fo:block-container>
							</fo:inline-container>
						</fo:block>
						
						<fo:block font-weight="bold" margin-top="5mm" font-family="arial" font-size="12pt" width="100%">
							<fo:inline-container width="194mm">
								<fo:block height="10mm">
									Merci de nous retourner ce questionnaire au plus tard le : $Date
								</fo:block>
							</fo:inline-container>
						</fo:block>
						
						<!-- TODO I am here -->
						<fo:block width="100%" margin-top="5mm" font-family="arial" font-weight="bold" font-size="10pt" border="solid black 1pt">
							<fo:inline-container width="194mm">
								<fo:block-container height="35mm" >
									<fo:block margin="3mm">
										<fo:block>
											Commentaires et remarques :
										</fo:block>
										<fo:inline-container width="182mm" height="5mm">
											<fo:block xsl:use-attribute-sets="Line-drawing-Garde">
												&#160;
											</fo:block>
										</fo:inline-container>
										<fo:inline-container width="182mm" height="5mm">
											<fo:block xsl:use-attribute-sets="Line-drawing-Garde">
												&#160;
											</fo:block>
										</fo:inline-container>
										<fo:inline-container width="182mm" height="5mm">
											<fo:block xsl:use-attribute-sets="Line-drawing-Garde">
												&#160;
											</fo:block>
										</fo:inline-container>
										<fo:inline-container width="182mm" height="5mm">
											<fo:block xsl:use-attribute-sets="Line-drawing-Garde">
												&#160;
											</fo:block>
										</fo:inline-container>
									</fo:block>
								</fo:block-container>
							</fo:inline-container>
						</fo:block>
						
						<fo:block width="100%" height="35mm" font-family="arial" font-size="7pt" margin-top="5mm" border="solid black 0.5pt">
							<fo:inline-container width="194mm">
								<fo:block margin="3mm">
									<fo:block>Vu l'avis favorable du Conseil national de l'information statistique, cette enquête, reconnue d$utilite_publique, est $obligatoire.</fo:block>
									<fo:block>Visa n°$visa du Ministre du travail, de l'emploi, de la formation professionnelle et du dialogue social, valable pour l'année $annee.</fo:block>
									<fo:block>Aux termes de l'article 6 de la loi n° 51-711 du 7 juin 1951 modifiée sur l'obligation, la coordination et le secret en matière de statistiques, les renseignements transmis</fo:block>
									<fo:block>en réponse au présent questionnaire ne sauraient en aucun cas être utilisés à des fins de contrôle fiscal ou de répression économique.</fo:block>
									<fo:block>L'article 7 de la loi précitée stipule d'autre part que tout défaut de réponse ou une réponse sciemment inexacte peut entraîner l'application d'une amende administrative.</fo:block>
									
									<fo:block>Questionnaire confidentiel destiné à  $jenesaispasqui.</fo:block>
									<fo:block>La loi n°78-17 du 6 janvier 1978 modifiée relative à l'informatique, aux fichiers et aux libertés, s'applique aux réponses faites à la présente enquête par les entreprises</fo:block>
									<fo:block>individuelles.</fo:block>
									<fo:block>Elle leur garantit un droit d'accès et de rectification pour les données les concernant.</fo:block>
									<fo:block>Ce droit peut être exercé auprès de $MOA.</fo:block>
								</fo:block>
							</fo:inline-container>
						</fo:block>
						
						
						<fo:block margin-top="5mm">
							<fo:inline-container width="85mm">
								<fo:block>
									&#160;
								</fo:block>
							</fo:inline-container>
							<fo:inline-container width="97mm">
								<fo:block-container height="10mm" border="solid black" background-color="#EFEFFB">
									<fo:block text-align="left">
										<fo:block>
											<fo:instream-foreign-object>
												<barcode:barcode
													xmlns:barcode="http://barcode4j.krysalis.org/ns"
													message="Code Bar" orientation="0">
													<barcode:code128>
														<barcode:height>10mm</barcode:height>
													</barcode:code128>
												</barcode:barcode>
											</fo:instream-foreign-object>
										</fo:block>
									</fo:block>
								</fo:block-container>
							</fo:inline-container>
						</fo:block>
						<fo:block width="100%" margin-top="5mm" position="fixed">
							<fo:inline-container width="72mm" height="10mm" margin-top="5mm">
								<fo:block-container position="absolute" top="222mm" left="0mm">
									<fo:block>
										Ce questionnaire est à retourner à :
									</fo:block>
								</fo:block-container>
							</fo:inline-container>
							<fo:inline-container width="17mm">
								<fo:block>
									&#160;
								</fo:block>
							</fo:inline-container>
							<fo:inline-container width="92mm" height="40mm">
								<fo:block-container position="absolute" top="217mm" left="92mm">
									<fo:block padding="3mm">
										<fo:block>$Adresse_retour_L1</fo:block>
										<fo:block>$Adresse_retour_L2</fo:block>
										<fo:block>$Adresse_retour_L3</fo:block>
										<fo:block>$Adresse_retour_L4</fo:block>
										<fo:block>$Adresse_retour_L5</fo:block>
										<fo:block>$Adresse_retour_L6</fo:block>
										<fo:block>$Adresse_retour_L7</fo:block>
									</fo:block>
								</fo:block-container>
							</fo:inline-container>
						</fo:block>
					</fo:block>
					</fo:flow>
			</fo:page-sequence>
			
			<fo:page-sequence master-reference="A4-portrail">
				
				<fo:static-content flow-name="xsl-region-before">
					<fo:block position="absolute" margin="10mm" text-align="right">
						<fo:external-graphic>
							<xsl:attribute name="src">
								<xsl:value-of select="concat($properties//Images/Folder,'encoche-top-right.png')"/>
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
					<fo:block position="absolute" margin-left="10mm" margin-top="15mm" bottom="0px" text-align="left">
						<fo:external-graphic>
							<xsl:attribute name="src">
								<xsl:value-of select="concat($properties//Images/Folder,'encoche-bottom-left.png')"/>
							</xsl:attribute>
						</fo:external-graphic>
					</fo:block>
					<fo:block text-align="center">
						<fo:page-number/>
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" border-collapse="collapse"
					reference-orientation="0" font-family="arial" font-size="10pt">
					
					<!-- Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
					<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
						<xsl:with-param name="driver" select="." tunnel="yes"/>
					</xsl:apply-templates>
			
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
				
		<xsl:text disable-output-escaping="yes">
			&lt;fo:block page-break-inside="avoid" &gt;  
		</xsl:text>
		
		<fo:block xsl:use-attribute-sets="Titre-sequence" border-color="black" border-style="solid">
			<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
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
		
		<xsl:if test="enopdf:is-first($source-context) != 'true'">
			<xsl:text disable-output-escaping="yes">
				&lt;fo:block page-break-inside="avoid" &gt;
			</xsl:text>
		</xsl:if>

		<fo:block xsl:use-attribute-sets="Titre-paragraphe">
			<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
	</xsl:template>


	<!-- Déclenche tous les xf-output de l'arbre des divers -->
	<!--<xsl:template match="*//Form//Module//xf-output"-->
	<xsl:template match="xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
<<<<<<< HEAD
=======
		<xsl:param name="noInstructions" tunnel="yes"/>
>>>>>>> parent of 9adc753... TableColumnSizeProcessor plugin and title position
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

		<!--FLAG -->
		<!--<xf-output/>-->
		<!--<NBRowspan><xsl:value-of select="enopdf:get-rowspan($source-context)"/></NBRowspan>
		<NBColspan><xsl:value-of select="enopdf:get-colspan($source-context)"/></NBColspan>-->
		<xsl:choose>
<<<<<<< HEAD
			<xsl:when test="enopdf:get-format($source-context) = 'instruction'">
				<fo:block>
					<fo:footnote>
						<fo:inline font-size="75%" 
							baseline-shift="super" alignment-baseline="hanging">
							<!--<xsl:value-of select="$footnote-count"/>-->
						</fo:inline>
						<fo:footnote-body font-family="serif,Symbol,ZapfDingbats" 
							font-size="8pt" font-weight="normal" font-style="normal" 
							text-align="justify" margin-left="0pc">
							<fo:block>
								<!--<fo:inline font-size="75%" 
									baseline-shift="super">
									<xsl:value-of select="$footnote-count"/>
								</fo:inline>-->
								<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
							</fo:block>
						</fo:footnote-body>
					</fo:footnote>
				</fo:block>
=======
			<xsl:when test="$noInstructions != 'YES'">
				<xsl:choose>
					<xsl:when test="enopdf:get-format($source-context) = 'footnote'">
						<fo:block>
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
						<!-- Do nothing -->
					</xsl:when>
					<xsl:when test="enopdf:get-format($source-context) = 'comment' or enopdf:get-format($source-context) = 'help' or enopdf:get-format($source-context) = 'instruction'">
						<fo:block xsl:use-attribute-sets="instruction">
							<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
						</fo:block>
					</xsl:when>
					<xsl:otherwise>
						<fo:block xsl:use-attribute-sets="general-style">
							<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
						</fo:block>
					</xsl:otherwise>
				</xsl:choose>
>>>>>>> parent of 9adc753... TableColumnSizeProcessor plugin and title position
			</xsl:when>
			<xsl:otherwise>
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
		
		<!--<Finxf-output/>-->

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="//Form/Module/xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			<xsl:choose>
				<xsl:when test="enopdf:get-format($source-context) = 'instruction'">
					<xsl:variable name="footnote-count" select="($footnote-count + 1)"/>
					<fo:block>
						<fo:footnote>
							<fo:inline font-size="75%" 
								baseline-shift="super" alignment-baseline="hanging">
								<!--<xsl:value-of select="$footnote-count"/>-->
							</fo:inline>
							<fo:footnote-body font-family="serif,Symbol,ZapfDingbats" 
								font-size="8pt" font-weight="normal" font-style="normal" 
								text-align="justify" margin-left="0pc">
								<fo:block>
									<!--<fo:inline font-size="75%" 
										baseline-shift="super">
										<xsl:value-of select="$footnote-count"/>
									</fo:inline>-->
									<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
								</fo:block>
							</fo:footnote-body>
						</fo:footnote>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block xsl:use-attribute-sets="general-style">
						<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>


		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>
	
	<xsl:template match="//Form/Module/SubModule/xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
<<<<<<< HEAD
			<xsl:choose>
				<xsl:when test="enopdf:get-format($source-context) = 'instruction'">
					<xsl:variable name="footnote-count" select="($footnote-count + 1)"/>
					<fo:block>
						<fo:footnote>
							<fo:inline font-size="75%" 
								baseline-shift="super">
								<!--<xsl:value-of select="$footnote-count"/>-->
							</fo:inline>
							<fo:footnote-body font-family="serif,Symbol,ZapfDingbats" 
								font-size="8pt" font-weight="normal" font-style="normal" 
								text-align="justify" margin-left="0pc">
								<fo:block>
									<!--<fo:inline font-size="75%" 
										baseline-shift="super">
										<xsl:value-of select="$footnote-count"/>
									</fo:inline>-->
									<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
								</fo:block>
							</fo:footnote-body>
						</fo:footnote>
					</fo:block>
				</xsl:when>
				<xsl:otherwise>
					<fo:block xsl:use-attribute-sets="general-style">
						<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
					</fo:block>
				</xsl:otherwise>
			</xsl:choose>
=======
			<xsl:if test="enopdf:get-format($source-context) = 'filter-alternative-text'">
				<fo:block xsl:use-attribute-sets="filter-alternative">
					<xsl:copy-of select="enopdf:get-label($source-context, $languages[1])"/>
				</fo:block>
			</xsl:if>
>>>>>>> parent of 9adc753... TableColumnSizeProcessor plugin and title position
		</xsl:if>
		
		
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<fo:block>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</fo:block>
		
	</xsl:template>

	<!--<xsl:template match="//TextCell/xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) !='' ">
			<fo:block font-size="10pt" font-weight="normal" color="black">
				<!-\-<fo:inline font-family="ZapfDingbats" font-size="10pt">&#x274F;</fo:inline>-\->
				<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
			</fo:block>
		</xsl:if>
		
		
		<!-\-Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -\->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
	</xsl:template>-->
	
	<!-- Déclenche tous les xf-input de l'arbre des divers s'il est précédé su driver Module :  DONNEES QUI DOIVENT ETRE RENSEIGNEES DANS LE QUESTIONNAIRE-->
	<xsl:template match="xf-input" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="position" tunnel="yes"/>
		<xsl:param name="isTable" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		
		<!-- FLAG Recupérer les formats des réponses pour anticiper le cadre -->
		<!-- <xf-input/> -->
		<!--<Type><xsl:value-of select="enopdf:get-type($source-context)"/></Type>-->
		<!--<Format><xsl:value-of select="enopdf:get-format($source-context)"/></Format>-->
		<!--<Longueur><xsl:value-of select="enopdf:get-length($source-context)"/></Longueur>-->
		<!--<Decimal><xsl:value-of select="enopdf:get-number-of-decimals($source-context)"/></Decimal>-->
		<!--<Unite><xsl:value-of select="enopdf:get-suffix($source-context, $languages[1])"/></Unite>-->
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			
		
			<xsl:if test="enopdf:get-type($source-context) = 'text'">
				<fo:block page-break-inside="avoid">
					<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
						<fo:block xsl:use-attribute-sets="label-question">
							<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>						
						</fo:block>
					</xsl:if>
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
				</fo:block>
			</xsl:if>

			<xsl:if test="enopdf:get-type($source-context) = 'date'">
				<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
				<fo:block page-break-inside="avoid">
					<fo:block xsl:use-attribute-sets="label-question">
						<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>						
					</fo:block>
					<fo:block xsl:use-attribute-sets="general-style">
						<fo:table width="50%">
							<fo:table-body>
								<fo:table-cell>
									<fo:block>
										<fo:table>
											<xsl:attribute name="width">
												<xsl:value-of select="5 * number(string-length(replace($field,'/','')))"/>mm
											</xsl:attribute>
											<fo:table-body>
												<xsl:for-each select="1 to xs:integer(number(string-length(replace($field,'/',''))))">
													<xsl:variable name="curVal" select="."/>
													<xsl:if test="number(string-length(replace($field,'/',''))) = $curVal">
														<xsl:for-each select="1 to $curVal">
															<xsl:variable name="curVal2" select="."/>
															<fo:table-cell background-color="#CCCCCC" border="solid white"><fo:block color="#CCCCCC">a</fo:block></fo:table-cell>
														</xsl:for-each>
													</xsl:if>
												</xsl:for-each>
											</fo:table-body>
										</fo:table>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block> ( <xsl:value-of select="$field"/> ) </fo:block>
								</fo:table-cell>
							</fo:table-body>
						</fo:table>
						
						
					</fo:block>
				</fo:block>
			</xsl:if>
			
			<xsl:if test="enopdf:get-type($source-context) = 'duration'"> 
				<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
				
				<fo:inline xsl:use-attribute-sets="general-style">
					<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
						<xsl:variable name="curVal" select="."/>
						<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
							<xsl:for-each select="1 to $curVal">
								<xsl:variable name="curVal2" select="."/>
								<fo:inline width="5mm" min-height="10mm" background-color="#CCCCCC" border="solid white" color="#CCCCCC">HH</fo:inline>
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
				
				
				<xsl:if test="enopdf:is-first($source-context) = 'true' and $field = 'mm'">
					<xsl:text disable-output-escaping="yes">
						&lt;/fo:block&gt;
					</xsl:text>
				</xsl:if>
			</xsl:if>
			
			<!-- get-numberof decimal -->
			<xsl:if test="enopdf:get-type($source-context) = 'number'">
				<fo:block page-break-inside="avoid">
					<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
						<fo:block xsl:use-attribute-sets="label-question">
							<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>						
						</fo:block>
					</xsl:if>
					<fo:block xsl:use-attribute-sets="general-style">
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
						<fo:inline><xsl:value-of select="enopdf:get-suffix($source-context, $languages[1])"/></fo:inline>
					</fo:block>
				</fo:block>
			</xsl:if>
			
			<fo:block>
				<xsl:if test="enopdf:is-first($source-context) = 'true'">
					<xsl:text disable-output-escaping="yes">
						&lt;/fo:block&gt;						
					</xsl:text>
				</xsl:if>
			</fo:block>
			
		</xsl:if>

		<xsl:if test="enopdf:get-label($source-context, $languages[1]) = ''">
			<xsl:if test="enopdf:get-type($source-context) = 'text'">
				<fo:block>
					<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
						<fo:block xsl:use-attribute-sets="label-question">
							<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>						
						</fo:block>
					</xsl:if>
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
				</fo:block>
			</xsl:if>

			<xsl:if test="enopdf:get-type($source-context) = 'date'">
				<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
				<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
					<fo:block xsl:use-attribute-sets="label-question">
						<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>						
					</fo:block>
				</xsl:if>
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:if test="$isTable = 'YES'">
						<xsl:attribute name="text-align">right</xsl:attribute>
					</xsl:if>
					<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
						<xsl:variable name="curVal" select="."/>
						<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
							<xsl:for-each select="1 to $curVal">
								<xsl:variable name="curVal2" select="."/>
								<fo:inline-container width="4mm" background-color="#CCCCCC" color="#CCCCCC" border="solid white">
									<fo:block>A</fo:block>
								</fo:inline-container>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
				</fo:block>
			</xsl:if>
			
			<xsl:if test="enopdf:get-type($source-context) = 'duration'"> 
				<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
				
					<fo:inline xsl:use-attribute-sets="general-style">
						<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
							<xsl:variable name="curVal" select="."/>
							<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
								<xsl:for-each select="1 to $curVal">
									<xsl:variable name="curVal2" select="."/>
									<fo:inline width="5mm" min-height="10mm" background-color="#CCCCCC" border="solid white" color="#CCCCCC">HH</fo:inline>
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
			</xsl:if>


			<xsl:if test="enopdf:get-type($source-context) = 'number'">
<<<<<<< HEAD
				<fo:block>
					<xsl:if test="$isTable = 'YES'">
						<xsl:attribute name="text-align">right</xsl:attribute>
					</xsl:if>
					<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
						<fo:block xsl:use-attribute-sets="label-question">
							<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
						</fo:block>
					</xsl:if>
					<fo:block xsl:use-attribute-sets="general-style">
						<xsl:attribute name="width">
							<xsl:value-of select="5 * number(enopdf:get-length($source-context))"/>mm
						</xsl:attribute>
						<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
							<xsl:variable name="curVal" select="."/>
							<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
								<xsl:for-each select="1 to $curVal">
									<fo:inline-container width="4mm" background-color="#CCCCCC" color="#CCCCCC" border="solid white">
										<fo:block>A</fo:block>
									</fo:inline-container>
								</xsl:for-each>
=======
				<xsl:choose>
					<xsl:when test="$autreHandle">
						<fo:inline xsl:use-attribute-sets="general-style">
							<xsl:for-each select="1 to xs:integer(number(enopdf:get-length($source-context)))">
								<xsl:variable name="curVal" select="."/>
								<xsl:if test="number(enopdf:get-length($source-context)) = $curVal">
									<xsl:for-each select="1 to $curVal">
										<fo:external-graphic>
											<xsl:attribute name="src">
												<xsl:value-of select="concat($properties//Images/Folder,'mask_number.png')"/>
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
>>>>>>> parent of 9adc753... TableColumnSizeProcessor plugin and title position
							</xsl:if>
						</xsl:for-each>
						<!--<fo:inline> , </fo:inline>
						<xsl:for-each select="1 to xs:integer(number(enopdf:get-number-of-decimals($source-context)))">
							<xsl:variable name="curVal" select="."/>
							<xsl:if test="number(enopdf:get-number-of-decimals($source-context)) = $curVal">
								<xsl:for-each select="1 to $curVal">
									<fo:inline-container width="4mm" background-color="#CCCCCC" color="#CCCCCC" border="solid white">
										<fo:block>A</fo:block>
									</fo:inline-container>
								</xsl:for-each>
							</xsl:if>
<<<<<<< HEAD
						</xsl:for-each>-->
						<fo:inline><xsl:value-of select="enopdf:get-suffix($source-context, $languages[1])"/></fo:inline>
					</fo:block>
				</fo:block>
=======
							<fo:block xsl:use-attribute-sets="general-style">
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
																	<xsl:value-of select="concat($properties//Images/Folder,'mask_number.png')"/>
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
															<xsl:value-of select="concat($properties//Images/Folder,'mask_number.png')"/>
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
>>>>>>> parent of 9adc753... TableColumnSizeProcessor plugin and title position
			</xsl:if>
			
		</xsl:if>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>


	<!-- Déclenche tous les xf-item de l'arbre des divers -->
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
						<!-- <xsl:value-of select="concat('<fo:external-graphic src=\"',$image,'\" title=\"',eno:serialize(enopdf:get-label($source-context, $languages[1])),'\" /&gt;')"/> -->
							<fo:inline font-family="ZapfDingbats" font-size="10pt" padding="5mm" >&#x274F;</fo:inline>
							<fo:external-graphic padding-right="3mm">
								<xsl:attribute name="src">
									<xsl:value-of select="$image"/>
								</xsl:attribute>
							</fo:external-graphic>
					</xsl:when>
					<xsl:otherwise>
						<!--  <xsl:value-of select="concat('&lt;fo:external-graphic src=&quot;/',$properties//Images/Folder,'/',$image,'&quot; title=&quot;',eno:serialize(enopdf:get-label($source-context, $languages[1])),'&quot; /&gt;')"/>-->
							<fo:inline font-family="ZapfDingbats" font-size="10pt"
								margin-top="3mm">&#x274F;</fo:inline>
							<fo:external-graphic padding-right="3mm">
								<xsl:attribute name="src">
									<xsl:value-of select="concat($properties//Images/Folder,$image)"/>
								</xsl:attribute>
							</fo:external-graphic>										
							<!--<fo:inline>
								<xsl:value-of select="$image"/>
							</fo:inline>-->
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$no-border = 'no-border'">
						<fo:inline font-family="ZapfDingbats" font-size="10pt" padding-right="4mm" padding-left="6mm"
							margin-top="3mm">&#x274F;</fo:inline>
						<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
							<fo:inline>
								<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
							</fo:inline>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<fo:block>
							<fo:inline font-family="ZapfDingbats" font-size="10pt" padding-right="5mm"
								margin-top="3mm">&#x274F;</fo:inline>
							<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
								<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
							</xsl:if>
						</fo:block>
					</xsl:otherwise>
				</xsl:choose>
				
			</xsl:otherwise>
		</xsl:choose>
		<!--<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			<fo:table-cell>
						<fo:block>
							<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
						</fo:block>
					</fo:table-cell>

			<fo:table-cell>
			<fo:block font-size="10pt" font-weight="normal" color="black">
				<fo:inline font-family="ZapfDingbats" font-size="10pt" padding-right="7mm"
					margin-top="3mm">&#x274F;</fo:inline>
				<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
			</fo:block>
			</fo:table-cell>
		</xsl:if>-->
		

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>



	<!-- Déclenche tous les xf-select de l'arbre des divers -->
	<xsl:template match="xf-select" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		<fo:block page-break-inside="avoid">	
<<<<<<< HEAD
			<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
				<fo:block xsl:use-attribute-sets="label-question">
					<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
				</fo:block>
			</xsl:if>
=======
			<xsl:value-of select="eno:printQuestionTitleWithInstruction($source-context,$languages[1],.)" disable-output-escaping="yes"/>
>>>>>>> parent of 9adc753... TableColumnSizeProcessor plugin and title position
	
			<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE  -->
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
			
			<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
				<fo:block>
					<xsl:if test="enopdf:is-first($source-context) = 'true'">
						<xsl:text disable-output-escaping="yes">
							&lt;/fo:block&gt;						
						</xsl:text>
					</xsl:if>
				</fo:block>
			</xsl:if>
		</fo:block>

	</xsl:template>

	<!-- Déclenche tous les xf-select de l'arbre des divers -->
	<xsl:template match="xf-select1" mode="model">
		
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="position" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		<xsl:variable name="format" select="enopdf:get-appearance($source-context)"/>
		
		
		<fo:block page-break-inside="avoid">
			<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
				<fo:block xsl:use-attribute-sets="label-question">
					<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
				</fo:block>
			</xsl:if>
			
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
						</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block><!-- Cant work veriry dom of simpson.fo -->
		
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
				<xsl:if test="enopdf:is-first($source-context) = 'true'">
					<xsl:text disable-output-escaping="yes">
						&lt;/fo:block&gt;					
					</xsl:text>
				</xsl:if>
		</xsl:if>

	</xsl:template>

	<!-- Déclenche tous les Table de l'arbre des drivers -->
	<xsl:template match="Table" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

		
		<!-- On récupére la question associée à la table -->
		<fo:block xsl:use-attribute-sets="label-question" margin-bottom="4mm">
			<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!-- FLAG Recupérer les caractéristiques du tableau pour le construire dynamiquement -->

		<!--<NBHeaderCols><xsl:value-of select="count(enopdf:get-header-columns($source-context))"/></NBHeaderCols>
			<NBHeaderLines><xsl:value-of select="count(enopdf:get-header-lines($source-context))"/></NBHeaderLines>	
			<NBHeaderLine><xsl:value-of select="count(enopdf:get-header-line($source-context, position()))"/></NBHeaderLine>
			<NBBodyLines><xsl:value-of select="count(enopdf:get-body-lines($source-context))"/></NBBodyLines>
			<NBBodyLine><xsl:value-of select="count(enopdf:get-body-line($source-context, position()))"/></NBBodyLine>-->


		<fo:table inline-progression-dimension="auto" table-layout="fixed" width="100%" font-size="10pt" border-width="0.35mm"
			text-align="center" display-align="center" space-after="5mm">
			
			<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
			<xsl:variable name="ancestors">
				<xsl:copy-of select="root(.)"/>
			</xsl:variable>

			<!--Gestion du nombre de colonnes pour la construction du tableau-->
			<!--<xsl:for-each select="enopdf:get-header-line($source-context, position())">
				<fo:table-column column-width="proportional-column-width(1)"/>
			</xsl:for-each>-->

			<!--Gestion du header-->
			<xsl:if test="count(enopdf:get-header-lines($source-context)) != 0">

				<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
				<xsl:variable name="ancestors">
					<xsl:copy-of select="root(.)"/>
				</xsl:variable>

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

			<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
			<xsl:variable name="ancestors">
				<xsl:copy-of select="root(.)"/>
			</xsl:variable>

			<!--Gestion du body-->
			<fo:table-body>
				<xsl:for-each select="enopdf:get-body-lines($source-context)">
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
				</xsl:for-each>
			</fo:table-body>
		</fo:table>
			
		
		
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			<xsl:if test="enopdf:is-first($source-context) = 'true'">
				<xsl:text disable-output-escaping="yes">
						&lt;/fo:block&gt;					
					</xsl:text>
			</xsl:if>
		</xsl:if>
		
	</xsl:template>

	<!--TableLoop renvoie seulment des headers avec des lignes vierges en des- (rowloop est dupliqué) -->
	<xsl:template match="TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="instance-ancestor" tunnel="yes"/>
		
		<xsl:if test="enopdf:get-rooster-number-lines($source-context) = 8">
			<fo:block xsl:use-attribute-sets="label-question" margin-bottom="4mm">
				<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
			</fo:block>
			
			<!-- FLAG Recupérer les caractéristiques du tableau pour le construire dynamiquement -->
			
			<!--<NBHeaderCols><xsl:value-of select="count(enopdf:get-header-columns($source-context))"/></NBHeaderCols>
			<NBHeaderLines><xsl:value-of select="count(enopdf:get-header-lines($source-context))"/></NBHeaderLines>	
			<NBHeaderLine><xsl:value-of select="count(enopdf:get-header-line($source-context, position()))"/></NBHeaderLine>
			<NBBodyLines><xsl:value-of select="count(enopdf:get-body-lines($source-context))"/></NBBodyLines>
			<NBBodyLine><xsl:value-of select="count(enopdf:get-body-line($source-context, position()))"/></NBBodyLine>-->
			
			
			<fo:table inline-progression-dimension="auto" font-size="10pt" table-layout="fixed" width="100%" border-width="0.35mm"
				text-align="center" display-align="center" space-after="5mm">
				
				<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
				<xsl:variable name="ancestors">
					<xsl:copy-of select="root(.)"/>
				</xsl:variable>
				
				<!--Gestion du nombre de colonnes pour la construction du tableau-->
				<!--<xsl:for-each select="enopdf:get-header-line($source-context, position())">
				<fo:table-column column-width="proportional-column-width(1)"/>
			</xsl:for-each>-->
				
				<!--Gestion du header-->
				<xsl:if test="count(enopdf:get-header-lines($source-context)) != 0">
					
					<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
					<xsl:variable name="ancestors">
						<xsl:copy-of select="root(.)"/>
					</xsl:variable>
					
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
				<xsl:variable name="ancestors">
					<xsl:copy-of select="root(.)"/>
				</xsl:variable>
				
				<!--Gestion du body-->
				<fo:table-body>
					<xsl:for-each select="enopdf:get-body-lines($source-context)">
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
<<<<<<< HEAD
=======
								<xsl:with-param name="row-number"  select="position()" tunnel="yes"/>
>>>>>>> parent of 9adc753... TableColumnSizeProcessor plugin and title position
								<xsl:with-param name="no-border" select="enopdf:get-style($source-context)" tunnel="yes"/>
							</xsl:apply-templates>
							<!-- Pour chaque boucle , on récupére les infos des lignes du tableau -->
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>

			<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
				<xsl:if test="enopdf:is-first($source-context) = 'true'">
					<xsl:text disable-output-escaping="yes">
						&lt;/fo:block&gt;					
					</xsl:text>
				</xsl:if>
			</xsl:if>
		</xsl:if>

	</xsl:template>



	<!-- Déclenche tous les TextCell de l'arbre des divers CODE TARIK-->
	<xsl:template match="TextCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="header" tunnel="yes"/>
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
				<xsl:attribute name="border">0mm</xsl:attribute>
				<xsl:attribute name="padding">0mm</xsl:attribute>
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

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		

	</xsl:template>

	<!-- Déclenche tous les TextCell de l'arbre des divers s'il est précéde de TextCell-->
	<xsl:template match="Body//TextCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>

		<!--FLAG-->
		<!--<BodyTextCell/>-->
		<!--<NBRowspan><xsl:value-of select="enopdf:get-rowspan($source-context)"/></NBRowspan>
			<NBColspan><xsl:value-of select="enopdf:get-colspan($source-context)"/></NBColspan>-->
		<!--<CodeDepth><xsl:value-of select="enopdf:get-code-depth($source-context)"/></CodeDepth>-->

		<xsl:variable name="depth">
			<xsl:value-of select="enopdf:get-code-depth($source-context)"/>
		</xsl:variable>

		<!--<fo:table-cell border-color="black" border-style="solid" text-align="left" number-rows-spanned="{enopdf:get-rowspan($source-context)}" number-columns-spanned="{enopdf:get-colspan($source-context)}">-->


		<xsl:if test="$depth != '1' and $depth != ''">
			<xsl:attribute name="class" select="concat('depth', $depth)"/>
		</xsl:if>
		<!-- A new virtual tree is created as driver -->
		<xsl:variable name="new-driver">
			<Body>
				<xf-output/>
			</Body>
		</xsl:variable>
		<!-- This new driver is applied on the same source-context -->
		<xsl:apply-templates select="$new-driver//xf-output" mode="model"/>

		<!--</fo:table-cell>-->
		<!--<FinBodyTextCell/>-->
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>
	<!-- Déclenche tous les Cell de l'arbre des divers -->
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

	<!-- Déclenche tous les EmptyCell de l'arbre des divers -->
	<xsl:template match="EmptyCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		<!--FLAG-->
		<!--<EmptyCell/>-->
		<fo:table-cell background-color="#CCCCCC" border-color="black" border-style="solid">
			<!--<xsl:if test="enopdf:get-label($source-context,$languages[1]) !=''">-->
			<fo:block>
				<!--<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>-->
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

	<!-- Déclenche tous les xf-group de l'arbre des divers -->
	<xsl:template match="xf-group" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!-- Déclenche tous les xf-textarea de l'arbre des divers -->
	<xsl:template match="xf-textarea" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<fo:block page-break-inside="avoid">
			<fo:block xsl:use-attribute-sets="label-question">
				<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
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
			<fo:block xsl:use-attribute-sets="Line-drawing">
				&#160;
			</fo:block>
		</fo:block>
		
		<xsl:if test="enopdf:get-label($source-context, $languages[1]) != ''">
			<fo:block>
				<xsl:if test="enopdf:is-first($source-context) = 'true'">
					<xsl:text disable-output-escaping="yes">
						&lt;/fo:block&gt;						
					</xsl:text>
				</xsl:if>
			</fo:block>
		</xsl:if>
		
		<!-- Temporaire -->
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<!--<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>-->

	</xsl:template>

	<!-- Déclenche tous les ResponseElement de l'arbre des divers -->
	<xsl:template match="ResponseElement" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!-- Déclenche tous les ResponseElement de l'arbre des divers -->
	<xsl:template match="MultipleQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		<fo:block xsl:use-attribute-sets="label-question">
			<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
		</fo:block>
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<fo:block>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</fo:block>
		
		<xsl:if test="enopdf:is-first($source-context) = 'true'">
			<xsl:text disable-output-escaping="yes">
				&lt;/fo:block&gt;
			</xsl:text>
		</xsl:if>
		
	</xsl:template>

	<!-- Same behaviour of tableLoop -->
	<!-- Déclenche tous les Rowloop de l'arbre des divers -->
	<!--<xsl:template match="RowLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>Check-OK-2

		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
			<xsl:value-of select="enopdf:get-minimum-lines($source-context)"/>
		</fo:block>

		
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
		<fo:block>
			<xsl:if test="enopdf:is-first($source-context) = 'true'">
				<xsl:text disable-output-escaping="yes">
						&lt;/fo:block&gt;						
					</xsl:text>
			</xsl:if>
		</fo:block>

	</xsl:template>-->

	<xsl:template match="DoubleDuration" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<xsl:if test="enopdf:get-type($source-context) = 'duration'"> 
			<xsl:variable name="field" select="enopdf:get-format($source-context)"/>
			<fo:inline xsl:use-attribute-sets="general-style">
				<xsl:for-each select="1 to string-length($field)">
					<xsl:variable name="curVal" select="."/>
					<xsl:if test="string-length($field) = $curVal">
						<xsl:for-each select="1 to $curVal">
							<xsl:variable name="curVal2" select="."/>
							<xsl:choose>
								<xsl:when test="':' = substring($field,$curVal2,1)">
									<fo:inline>:</fo:inline>
								</xsl:when>
								<xsl:otherwise>
									<fo:inline-container width="4mm" background-color="#CCCCCC" color="#CCCCCC" border="solid white">
										<fo:block>A</fo:block>
									</fo:inline-container>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:if>
				</xsl:for-each>
			</fo:inline>
		</xsl:if>
		
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>
		
	</xsl:template>
	
</xsl:stylesheet>
