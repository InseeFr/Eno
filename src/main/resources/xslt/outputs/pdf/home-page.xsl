<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
    version="2.0">
    
    <xsl:function name="eno:Home-Page">
        <xsl:param name="source-context"/>
        <xsl:variable name="languages" select="enopdf:get-form-languages($source-context)"
            as="xs:string +"/>
        
        <fo:block page-break-inside="avoid">
            <fo:block width="100%" >
                <fo:inline-container width="72mm">
                    <fo:block-container height="20mm" max-height="20mm" overflow="hidden">
                        <fo:block>
                            <fo:external-graphic height="5mm">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="concat($properties//Images/Folder,'logo-insee-header.png')"/>
                                </xsl:attribute>
                            </fo:external-graphic>
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
                        <fo:block font-weight="bold" font-size="18pt" font-family="sans-serif" margin="3mm">
                            <xsl:value-of select="enopdf:get-label($source-context, $languages[1])"/>
                        </fo:block>
                    </fo:block-container>
                </fo:inline-container>
            </fo:block>
            
            <fo:block width="100%" margin-top="7mm" font-family="sans-serif" font-size="10pt">
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
            
            <fo:block width="100%" margin-top="5mm" font-family="sans-serif" font-size="10pt">
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
            
            <fo:block font-weight="bold" margin-top="5mm" font-family="sans-serif" font-size="12pt" width="100%">
                <fo:inline-container width="194mm">
                    <fo:block height="10mm">
                        Merci de nous retourner ce questionnaire au plus tard le : $Date
                    </fo:block>
                </fo:inline-container>
            </fo:block>
            
            <!-- TODO I am here -->
            <fo:block width="100%" margin-top="5mm" font-family="sans-serif" font-weight="bold" font-size="10pt" border="solid black 1pt">
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
            
            <fo:block width="100%" height="35mm" font-family="sans-serif" font-size="7pt" margin-top="5mm" border="solid black 0.5pt">
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
            <fo:block width="100%" margin-top="5mm" position="fixed" font-family="sans-serif">
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
    </xsl:function>

</xsl:stylesheet>