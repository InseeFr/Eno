<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
	xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="xd eno enofr"
	xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
	version="2.0">
	
	<xsl:import href="../../util/pdf/style.xsl"/>

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
	
	<!--Afficher le titre dans le drvier FORM . Permet aussi l'encapsulation <fo:root> -->
	<xsl:template match="Form" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:root>
			
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4-portrail" page-height="297mm"
					page-width="210mm" margin-top="5mm" margin-bottom="5mm" margin-left="5mm"
					margin-right="5mm" font-family="arial" font-size="10pt" font-weight="normal">
					<fo:region-body margin-top="10mm" margin-bottom="20mm"/>
					<fo:region-before region-name="xsl-region-before" extent="25mm"
						display-align="before" precedence="true"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="A4-portrail">
				<fo:flow flow-name="xsl-region-body">
					<fo:block width="100%">
						<fo:table>
							<fo:table-body>
								<fo:table-cell>
									<fo:block height="15mm" text-align="center" padding="2mm">
										<fo:external-graphic  height="10mm" src="./Images/logo-insee-header.png"></fo:external-graphic>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>
								<fo:table-cell border-color="black" border-style="solid" padding="2mm" text-align="center">
									<fo:block  font-weight="bold" font-size="14pt" ><xsl:value-of select="enofr:get-label($source-context, $languages[1])"/></fo:block>
								</fo:table-cell>
							</fo:table-body>
						</fo:table>
					</fo:block>
					
					<fo:block width="100%" margin-top="1%" >
						<fo:table>
							<fo:table-body>
								<fo:table-cell number-columns-spanned="2">
									<fo:block-container border-color="black" border-style="solid" padding="2mm 2mm 2mm 2mm" fox:border-radius="5mm">
										<fo:block font-weight="bold">Unité enquêtée</fo:block>
										<fo:block>Identifiant : $identifiant</fo:block>
										<fo:block>Raison sociale : $RS</fo:block>
										<fo:block>Adresse :</fo:block>
										<fo:block>$champ_adresse</fo:block>
									</fo:block-container>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1">
									<fo:block></fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="3" >
									<fo:block-container border-color="black" border-style="solid" padding="2mm 2mm 2mm 2mm" fox:border-radius="5mm">
										<fo:block font-weight="bold">Contacter l'assistance</fo:block>
										<fo:block>Par téléphone</fo:block>
										<fo:block>- $nom gestionnaire1  au $telephone1</fo:block>
										<fo:block>- $nom gestionnaire2  au $telephone2</fo:block>
										<fo:block>Par Mail :</fo:block>
										<fo:block>$mail_gestionnaire</fo:block>
									</fo:block-container>
								</fo:table-cell>
							</fo:table-body>
						</fo:table>
					</fo:block>
					
					<fo:block-container margin-top="1%" width="40%" border-color="black" border-style="solid" padding="2mm 2mm 2mm 2mm" fox:border-radius="5mm">
						<fo:block font-weight="bold">Coordonnées de la personne ayant</fo:block>
						<fo:block font-weight="bold">répondu à ce questionnaire :</fo:block>
						<fo:block>Nom : $nom_corresp</fo:block>
						<fo:block>Prénom : $prenom_corresp</fo:block>
						<fo:block>Téléphone : $tel_corresp</fo:block>
						<fo:block>Mel : $mel_corresp</fo:block>
						<fo:block>(est-ce pré rémpli et modifiable ?</fo:block>
						<fo:block>Ou à renseigner)</fo:block>
					</fo:block-container>
					
					<fo:block font-weight="bold" font-size="12pt" border="dashed" width="75%" margin-top="1%" padding="2mm">Merci de nous retourner ce questionnaire au plus tard le : $Date</fo:block>
					
					<fo:block-container width="100%" border="solid" margin-top="1%" padding="2mm">
						<fo:block font-weight="bold" font-size="10pt">
							Commentaires et remarques :
						</fo:block>
						<fo:block>
							.........................................................................................................................................................................
						</fo:block>
						<fo:block>
							.........................................................................................................................................................................
						</fo:block>
						<fo:block>
							.........................................................................................................................................................................
						</fo:block>
						<fo:block>
							.........................................................................................................................................................................
						</fo:block>
						<fo:block>
							.........................................................................................................................................................................
						</fo:block>
						<fo:block>
							.........................................................................................................................................................................
						</fo:block>
					</fo:block-container>
					
					<fo:block-container margin-top="1%" font-size="7pt" border="solid" padding="2mm">
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
						
					</fo:block-container>			
					<fo:block background-color="#666666" border="solid" padding="2mm" margin-left="80%" margin-top="3%">
						$CodeBar
					</fo:block>
					<fo:block width="100%" margin-top="1%">
						<fo:table>
							<fo:table-body>
								<fo:table-cell>
									<fo:block>Ce questionnaire est à retourner à :</fo:block>
								</fo:table-cell>
								<fo:table-cell border="solid" padding="2mm">
									<fo:block>Adresse pouvant utiliser jusqu'à 7 lignes</fo:block>
									<fo:block>(dépend du questionnaire)</fo:block>
									<fo:block>Emplacement précis à revoir avec Anne</fo:block>
									<fo:block>L4</fo:block>
									<fo:block>L5</fo:block>
									<fo:block>L6</fo:block>
									<fo:block>L7</fo:block>
										
								</fo:table-cell>
							</fo:table-body>
						</fo:table>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>

			<!-- Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>

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
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:page-sequence master-reference="A4-portrail">
			<fo:flow flow-name="xsl-region-body" border-collapse="collapse"
				reference-orientation="0">
				<fo:block xsl:use-attribute-sets="Titre-sequence">
					<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
				</fo:block>

				<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>

			</fo:flow>
		</fo:page-sequence>

	</xsl:template>

	<!--Afficher le titre dans le driver SubModule -->
	<xsl:template match="SubModule" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:block xsl:use-attribute-sets="Titre-paragraphe">
			<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
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
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<!--FLAG -->
		<!--<xf-output/>-->
		<!--<NBRowspan><xsl:value-of select="enofr:get-rowspan($source-context)"/></NBRowspan>
		<NBColspan><xsl:value-of select="enofr:get-colspan($source-context)"/></NBColspan>-->

		<fo:block xsl:use-attribute-sets="general-style">
			<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
		</fo:block>
		<!--<Finxf-output/>-->

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="//Form/Module/xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<xsl:if test="enofr:get-label($source-context, $languages[1]) != ''">
			<fo:block xsl:use-attribute-sets="general-style">
				<!--<fo:inline font-family="ZapfDingbats" font-size="10pt">&#x274F;</fo:inline>-->
				<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
			</fo:block>
		</xsl:if>


		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!--<xsl:template match="//TextCell/xf-output" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<xsl:if test="enofr:get-label($source-context, $languages[1]) !='' ">
			<fo:block font-size="10pt" font-weight="normal" color="black">
				<!-\-<fo:inline font-family="ZapfDingbats" font-size="10pt">&#x274F;</fo:inline>-\->
				<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
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
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<!-- FLAG Recupérer les formats des réponses pour anticiper le cadre -->
		<!-- <xf-input/>
			<Type><xsl:value-of select="enofr:get-type($source-context)"/></Type>
			<Format><xsl:value-of select="enofr:get-format($source-context)"/></Format>
			<Longueur><xsl:value-of select="enofr:get-length($source-context)"/></Longueur>
			<Decimal><xsl:value-of select="enofr:get-number-of-decimals($source-context)"/></Decimal>
			<Unite><xsl:value-of select="enofr:get-suffix($source-context, $languages[1])"/></Unite>	-->
		<xsl:if test="enofr:get-label($source-context, $languages[1]) != ''">
			<xsl:if test="enofr:get-type($source-context) = 'text'">
				<fo:block xsl:use-attribute-sets="general-style">
				<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
					<fo:block>............................................................................................................................................................................................................</fo:block>
				</fo:block>
			</xsl:if>

			<xsl:if test="enofr:get-type($source-context) = 'date'">
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
					<fo:block>..../..../....</fo:block>
				</fo:block>
			</xsl:if>
			
			<xsl:if test="enofr:get-type($source-context) = 'duration'">
				<xsl:variable name="field" select="enofr:get-label($source-context, $languages[1])"/>
				<fo:inline border-color="black" border-style="solid" width="15%"></fo:inline>
				<fo:inline><xsl:value-of select="$field"/></fo:inline>					
			</xsl:if>
			
			<!-- get-numberof decimal -->
			<xsl:if test="enofr:get-type($source-context) = 'number'">
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
					<xsl:for-each select="1 to 10">
						<xsl:variable name="curVal" select="."/>
						<xsl:if test="number(enofr:get-length($source-context)) = $curVal">
							<fo:block>
								<xsl:for-each select="1 to $curVal">
									<xsl:variable name="curVal2" select="."/>
									<xsl:choose>
										<xsl:when test="$curVal2 = $curVal">|__|</xsl:when>
										<xsl:otherwise>|__</xsl:otherwise>
									</xsl:choose>
								</xsl:for-each>
							</fo:block>
						</xsl:if>
					</xsl:for-each>
					
				</fo:block>
			</xsl:if>
		</xsl:if>

		<xsl:if test="enofr:get-label($source-context, $languages[1]) = ''">
			<xsl:if test="enofr:get-type($source-context) = 'text'">
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
					<fo:block>|.............|</fo:block>
				</fo:block>
			</xsl:if>

			<xsl:if test="enofr:get-type($source-context) = 'date'">
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
					<fo:block>..../..../....</fo:block>
				</fo:block>
			</xsl:if>
			
			<xsl:if test="enofr:get-type($source-context) = 'duration'">
				<xsl:variable name="field" select="enofr:get-format($source-context)"/>
				<fo:inline xsl:use-attribute-sets="general-style">|___|___|</fo:inline>
				<fo:inline xsl:use-attribute-sets="general-style"><xsl:value-of select="$field"/></fo:inline>					
			</xsl:if>


			<xsl:if test="enofr:get-type($source-context) = 'number'">
				<fo:block xsl:use-attribute-sets="general-style">
					<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
					<xsl:for-each select="1 to 10">
						<xsl:variable name="curVal" select="."/>
						<xsl:if test="number(enofr:get-length($source-context)) = $curVal">
							<fo:block>
								<xsl:for-each select="1 to $curVal">
									<xsl:variable name="curVal2" select="."/>
									<xsl:choose>
										<xsl:when test="$curVal2 = $curVal">|__|</xsl:when>
										<xsl:otherwise>|__</xsl:otherwise>
									</xsl:choose>
								</xsl:for-each>
							</fo:block>
						</xsl:if>
					</xsl:for-each>
				</fo:block>
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
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<xsl:variable name="image">
			<xsl:value-of select="enofr:get-image($source-context)"/>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="enofr:get-image($source-context) != ''" >
				<xsl:choose>
					<xsl:when test="starts-with($image,'http')">
						<!-- <xsl:value-of select="concat('<fo:external-graphic src=\"',$image,'\" title=\"',eno:serialize(enofr:get-label($source-context, $languages[1])),'\" /&gt;')"/> -->
							<fo:inline font-family="ZapfDingbats" font-size="10pt" >&#x274F;</fo:inline>
							<fo:external-graphic padding-right="3mm">
								<xsl:attribute name="src">
									<xsl:value-of select="$image"/>
								</xsl:attribute>
							</fo:external-graphic>
					</xsl:when>
					<xsl:otherwise>
						<!--  <xsl:value-of select="concat('&lt;fo:external-graphic src=&quot;/',$properties//Images/Folder,'/',$image,'&quot; title=&quot;',eno:serialize(enofr:get-label($source-context, $languages[1])),'&quot; /&gt;')"/>-->
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
			<xsl:when test="enofr:get-label($source-context, $languages[1]) != ''">
					<!-- label output not in table -->
					<fo:block xsl:use-attribute-sets="general-style">
						<fo:inline font-family="ZapfDingbats" font-size="10pt" padding-right="7mm"
							margin-top="3mm">&#x274F;</fo:inline>
						<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
					</fo:block>
			</xsl:when>
		</xsl:choose>
		<!--<xsl:if test="enofr:get-label($source-context, $languages[1]) != ''">
			<fo:table-cell>
						<fo:block>
							<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
						</fo:block>
					</fo:table-cell>

			<fo:table-cell>
			<fo:block font-size="10pt" font-weight="normal" color="black">
				<fo:inline font-family="ZapfDingbats" font-size="10pt" padding-right="7mm"
					margin-top="3mm">&#x274F;</fo:inline>
				<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
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
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<xsl:if test="enofr:get-label($source-context, $languages[1]) != ''">
			<fo:block xsl:use-attribute-sets="label-question">
				<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
			</fo:block>
		</xsl:if>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE  -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!-- Déclenche tous les xf-select de l'arbre des divers -->
	<xsl:template match="xf-select1" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>
		<xsl:variable name="format" select="enofr:get-appearance($source-context)"/>
		
		<xsl:if test="enofr:get-label($source-context, $languages[1]) != ''">
			<fo:block xsl:use-attribute-sets="label-question">
				<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
			</fo:block>
		</xsl:if>
		
		<xsl:choose>
			<xsl:when test="$format = 'minimal'">
				<fo:block>............................................................................................................................................................................................................</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE  -->
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- Déclenche tous les Table de l'arbre des drivers -->
	<xsl:template match="Table" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>


		<!-- On récupére la question associée à la table -->
		<fo:block xsl:use-attribute-sets="label-question">
			<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!-- FLAG Recupérer les caractéristiques du tableau pour le construire dynamiquement -->

		<!--<NBHeaderCols><xsl:value-of select="count(enofr:get-header-columns($source-context))"/></NBHeaderCols>
			<NBHeaderLines><xsl:value-of select="count(enofr:get-header-lines($source-context))"/></NBHeaderLines>	
			<NBHeaderLine><xsl:value-of select="count(enofr:get-header-line($source-context, position()))"/></NBHeaderLine>
			<NBBodyLines><xsl:value-of select="count(enofr:get-body-lines($source-context))"/></NBBodyLines>
			<NBBodyLine><xsl:value-of select="count(enofr:get-body-line($source-context, position()))"/></NBBodyLine>-->


		<fo:table table-layout="fixed" width="100%" font-size="10pt" border-width="0.35mm"
			text-align="center" display-align="center" space-after="5mm">

			<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
			<xsl:variable name="ancestors">
				<xsl:copy-of select="root(.)"/>
			</xsl:variable>

			<!--Gestion du nombre de colonnes pour la construction du tableau-->
			<!--<xsl:for-each select="enofr:get-header-line($source-context, position())">
				<fo:table-column column-width="proportional-column-width(1)"/>
			</xsl:for-each>-->

			<!--Gestion du header-->
			<xsl:if test="count(enofr:get-header-lines($source-context)) != 0">

				<!-- Avant d'entrer dans un for-each, sauvegarde obligatoire de l'arbre des driver -->
				<xsl:variable name="ancestors">
					<xsl:copy-of select="root(.)"/>
				</xsl:variable>

				<fo:table-header>
					<!-- Récupére le nombre de header-lines = Nombre de lignes dans le tableau -->
					<xsl:for-each select="enofr:get-header-lines($source-context)">
						<fo:table-row xsl:use-attribute-sets="entete-ligne">
							<!--<NBHeaderCols><xsl:value-of select="count(enofr:get-header-columns($source-context))"/></NBHeaderCols>
									<NBHeaderLines><xsl:value-of select="count(enofr:get-header-lines($source-context))"/></NBHeaderLines>
									<NBHeaderLine><xsl:value-of select="count(enofr:get-header-line($source-context, position()))"/></NBHeaderLine>
									<NBBodyLines><xsl:value-of select="count(enofr:get-body-lines($source-context))"/></NBBodyLines>
									<NBBodyLine><xsl:value-of select="count(enofr:get-body-line($source-context, position()))"/></NBBodyLine>
									<NBRowspan><xsl:value-of select="enofr:get-rowspan($source-context)"/></NBRowspan>
									<NBColspan><xsl:value-of select="enofr:get-colspan($source-context)"/></NBColspan>-->

							<!-- Dans un for-each, la fonction position() renvoie la position de l'élément dans l'arbre temporaire créé dans le select du for-each -->
							<xsl:apply-templates
								select="enofr:get-header-line($source-context, position())"
								mode="source">
								<xsl:with-param name="driver" select="." tunnel="yes"/>
								<xsl:with-param name="header"  select='"Yes"'/>
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
				<xsl:for-each select="enofr:get-body-lines($source-context)">
					<fo:table-row border-color="black" >
						<!--<NBHeaderCols><xsl:value-of select="count(enofr:get-header-columns($source-context))"/></NBHeaderCols>
							<NBHeaderLines><xsl:value-of select="count(enofr:get-header-lines($source-context))"/></NBHeaderLines>
							<NBHeaderLine><xsl:value-of select="count(enofr:get-header-line($source-context, position()))"/></NBHeaderLine>
							<NBBodyLines><xsl:value-of select="count(enofr:get-body-lines($source-context))"/></NBBodyLines>
							<NBBodyLine><xsl:value-of select="count(enofr:get-body-line($source-context, position()))"/></NBBodyLine>
							<NBRowspan><xsl:value-of select="enofr:get-rowspan($source-context)"/></NBRowspan>
							<NBColspan><xsl:value-of select="enofr:get-colspan($source-context)"/></NBColspan>-->
						<xsl:apply-templates
							select="enofr:get-body-line($source-context, position())" mode="source">
							<xsl:with-param name="driver" select="." tunnel="yes"/>
						</xsl:apply-templates>
						<!-- Pour chaque boucle , on récupére les infos des lignes du tableau -->
					</fo:table-row>
				</xsl:for-each>
			</fo:table-body>
		</fo:table>

	</xsl:template>

	<!--TableLoop renvoie seulment des headers avec des lignes vierges en dessous (rowloop est dupliqué) -->
	<xsl:template match="TableLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		<xsl:param name="instance-ancestor" tunnel="yes"/>
		<xsl:variable name="table-title">
			<!-- FLAG Recupérer les caractéristiques du tableau pour le construire dynamiquement -->
			<!--<NBHeaderCols><xsl:value-of select="count(enofr:get-header-columns($source-context))"/></NBHeaderCols>
			<NBHeaderLines><xsl:value-of select="count(enofr:get-header-lines($source-context))"/></NBHeaderLines>
			<NBBodyLines><xsl:value-of select="count(enofr:get-body-lines($source-context))"/></NBBodyLines>-->
			<fo:table-body font-size="95%">
				<fo:table-row border-color="black">
					<Body>
						<xf-output/>
					</Body>
				</fo:table-row>
			</fo:table-body>
		</xsl:variable>
		<xsl:apply-templates select="$table-title//xf-output" mode="model">
		</xsl:apply-templates>

		<xsl:variable name="ancestors">
			<xsl:copy-of select="root(.)"/>
		</xsl:variable>

		<xsl:variable name="name" select="enofr:get-name($source-context)"/>

		<!--<tableloop name="{$name}">
            <tableloop-header>
                <xsl:for-each select="enofr:get-header-lines($source-context)">
                        <xsl:apply-templates select="enofr:get-header-line($source-context, position())" mode="source">
                            <xsl:with-param name="driver" select="." tunnel="yes"/>
                        </xsl:apply-templates>
                </xsl:for-each>
            </tableloop-header>
			
            <tableloop-body>
                        <xsl:apply-templates select="enofr:get-body-line($source-context, 1)" mode="source">
                            <xsl:with-param name="driver" select="." tunnel="yes"/>
                            <xsl:with-param name="instance-ancestor" select="concat($instance-ancestor,'*[name()=''',$name, ''' and count(preceding-sibling::*)=count(current()/ancestor::*[name()=''', $name,''']/preceding-sibling::*)]//')" tunnel="yes"/>
                        </xsl:apply-templates>      
            </tableloop-body>
		</tableloop>-->

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>



	<!-- Déclenche tous les TextCell de l'arbre des divers CODE TARIK-->
	<xsl:template match="TextCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:param name="languages" tunnel="yes"/>
		
		<!--FLAG-->
		<!--<TextCell/>
		<NBRowspan><xsl:value-of select="enofr:get-rowspan($source-context)"/></NBRowspan>
		<NBColspan><xsl:value-of select="enofr:get-colspan($source-context)"/></NBColspan>-->
		<!--<CodeDepth><xsl:value-of select="enofr:get-code-depth($source-context)"/></CodeDepth>-\->-->
		
		<fo:table-cell xsl:use-attribute-sets="colonne-tableau"
			number-rows-spanned="{enofr:get-rowspan($source-context)}"
			number-columns-spanned="{enofr:get-colspan($source-context)}">
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
		<!--<NBRowspan><xsl:value-of select="enofr:get-rowspan($source-context)"/></NBRowspan>
			<NBColspan><xsl:value-of select="enofr:get-colspan($source-context)"/></NBColspan>-->
		<!--<CodeDepth><xsl:value-of select="enofr:get-code-depth($source-context)"/></CodeDepth>-->

		<xsl:variable name="depth">
			<xsl:value-of select="enofr:get-code-depth($source-context)"/>
		</xsl:variable>

		<!--<fo:table-cell border-color="black" border-style="solid" text-align="left" number-rows-spanned="{enofr:get-rowspan($source-context)}" number-columns-spanned="{enofr:get-colspan($source-context)}">-->


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
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		
		
		<fo:table-cell text-align="center" border-color="black" border-style="solid" padding="2mm">
			<fo:block>
				<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
					<xsl:with-param name="driver" select="." tunnel="yes"/>
				</xsl:apply-templates>
			</fo:block>
		</fo:table-cell>

		
	</xsl:template>

	<!-- Déclenche tous les EmptyCell de l'arbre des divers -->
	<xsl:template match="EmptyCell" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>
		<!--FLAG-->
		<!--<EmptyCell/>-->
		<fo:table-cell background-color="#CCCCCC" border-color="black" border-style="solid">
			<!--<xsl:if test="enofr:get-label($source-context,$languages[1]) !=''">-->
			<fo:block>
				<!--<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>-->
				 </fo:block>
		</fo:table-cell>
		<!--</xsl:if>-->

		<xsl:if test="enofr:get-colspan($source-context) = '2'">
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
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!-- Déclenche tous les xf-textarea de l'arbre des divers -->
	<xsl:template match="xf-textarea" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:block xsl:use-attribute-sets="label-question">
			<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
		</fo:block>
		
		<fo:block>
			.......................................................................................................................................................
		</fo:block>
		<fo:block>
			.......................................................................................................................................................
		</fo:block>
		<fo:block>
			.......................................................................................................................................................
		</fo:block>
		<fo:block>
			.......................................................................................................................................................
		</fo:block>
		<fo:block>
			.......................................................................................................................................................
		</fo:block>
		
		<!-- Temporaire -->
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<!--<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>-->

	</xsl:template>

	<!-- Déclenche tous les ResponseElement de l'arbre des divers -->
	<xsl:template match="ResponseElement" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>

	<!-- Déclenche tous les ResponseElement de l'arbre des divers -->
	<xsl:template match="MultipleQuestion" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>
		<fo:block xsl:use-attribute-sets="label-question">
			<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
		</fo:block>
		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<fo:block>
			<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
				<xsl:with-param name="driver" select="." tunnel="yes"/>
			</xsl:apply-templates>
		</fo:block>

	</xsl:template>

	<!-- Déclenche tous les Rowloop de l'arbre des divers -->
	<xsl:template match="RowLoop" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>

		<fo:block font-size="10pt" font-weight="bold" color="black">
			<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
			<xsl:value-of select="enofr:get-minimum-lines($source-context)"/>
		</fo:block>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>


	<!-- Déclenche tous les ResponseElement de l'arbre des divers -->
	<xsl:template match="DoubleDuration" mode="model">
		<xsl:param name="source-context" as="item()" tunnel="yes"/>
		<xsl:variable name="languages" select="enofr:get-form-languages($source-context)"
			as="xs:string +"/>
		
		<fo:block xsl:use-attribute-sets="label-question">
			<xsl:value-of select="enofr:get-label($source-context, $languages[1])"/>
		</fo:block>

		<!--Revient au parent A RAJOUTER DANS CHAQUE TEMPLATE -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
			<xsl:with-param name="driver" select="." tunnel="yes"/>
		</xsl:apply-templates>

	</xsl:template>
	
</xsl:stylesheet>
