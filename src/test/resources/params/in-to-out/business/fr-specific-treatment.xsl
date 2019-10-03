<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="2.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
    xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ev="http://www.w3.org/2001/xml-events">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de racine, on applique les templates de tous les enfants</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de base pour tous les éléments et tous les attributs, on recopie
                simplement en sortie</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    
       
    <!-- Autre : précisez dans les QCM -->
    <!-- Remplacer -QOPI- par -Details- pour le contenu de @QOPI -->
    
    <xsl:variable name="table-give-details">
        <TableGiveDetailsList>
            <TableGiveDetail other="AUT_PERS_AUTRES" give-details="AUT_PERS_AUTRES_P" QOPI="js0dvdaw-Details-1" value="1"/>
            <TableGiveDetail other="MAD_LOCAUX_PAR_AUTRES" give-details="MAD_LOCAUX_PAR_AUTRES_P" QOPI="js1wmyn8-Details-1" value="1"/>
            <TableGiveDetail other="MAD_TERRAINS_PAR_AUTRES" give-details="MAD_TERRAINS_PAR_AUTRES_P" QOPI="js1wqahn-Details-1" value="1"/>
        </TableGiveDetailsList>
    </xsl:variable>
    
    <xsl:template match="xf:instance[@id='fr-form-instance']//Variable[@idVariable=$table-give-details//TableGiveDetail/@give-details
        and preceding-sibling::Variable[@idVariable=$table-give-details//TableGiveDetail/@other]]">
        
        <xsl:element name="{$table-give-details//TableGiveDetail[@give-details=current()/@idVariable]/@QOPI}">
            <xsl:copy-of select="."/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="xf:bind[ends-with(@id,'-bind') and substring-before(@id,'-bind')=$table-give-details//TableGiveDetail/@give-details 
        and preceding-sibling::xf:bind[substring-before(@id,'-bind')=$table-give-details//TableGiveDetail/@other]]">
        
        <xsl:variable name="QOPI-id" select="$table-give-details//TableGiveDetail[@give-details=substring-before(current()/@id,'-bind')]/@QOPI"/>
        
        <xf:bind id="{$QOPI-id}-bind" name="{$QOPI-id}" ref="{$QOPI-id}"
            relevant="../Variable[@idVariable='{$table-give-details//TableGiveDetail[@give-details=substring-before(current()/@id,'-bind')]/@other}'] ='{$table-give-details//TableGiveDetail[@give-details=substring-before(current()/@id,'-bind')]/@value}'">
            <xsl:copy-of select="."/>
        </xf:bind>
    </xsl:template>
    
    <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()=$table-give-details//TableGiveDetail/@give-details]">
        <xsl:element name="{$table-give-details//TableGiveDetail[@give-details=current()/name()]/@QOPI}"/>
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <!--a eclaircir-->
    <!--    vu sur INVT : ne change rien-->
    <!--    vu sur ASSOT : change qqch !-->
     <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()=$table-give-details//TableGiveDetail/@QOPI]"/>
    
    <xsl:template match="xhtml:body//xhtml:td[*[ends-with(@id,'-control') and substring-before(@id,'-control')=$table-give-details//TableGiveDetail/@other]]">  
        <xsl:variable name="give-details" select="$table-give-details//TableGiveDetail[@other=substring-before(current()/*/@id,'-control')]/@give-details"/>
        <xsl:variable name="QOPI-id" select="$table-give-details//TableGiveDetail[@other=substring-before(current()/*/@id,'-control')]/@QOPI"/>
        
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:if test="following-sibling::xhtml:td[*/@id=concat($give-details,'-control')]">
                <xf:group id="{$QOPI-id}-control" bind="{$QOPI-id}-bind">
                    <!--<xsl:copy-of select="following-sibling::xhtml:td[xf:input/@id=concat($give-details,'-control')]/xf:input"/>-->
                    <xsl:copy-of select="following-sibling::xhtml:td/xf:input[@id=concat($give-details,'-control')]"/>
                    <xsl:copy-of select="following-sibling::xhtml:td/xf:textarea[@id=concat($give-details,'-control')]"/>
                </xf:group>
            </xsl:if>
        </xsl:copy>
    </xsl:template>
    
    <!--a eclaircir-->
    <!--    vu sur INVT : ne change rien-->
    <!--    vu sur ASSOT : change qqch !-->
        <xsl:template match="xhtml:body//xhtml:td[*[ends-with(@id,'-control') and substring-before(@id,'-control')=$table-give-details//TableGiveDetail/@give-details]]"/>
    
    <xsl:variable name="tablehead-variable">
        <Variables>
            <Variable name="RESS_AUTRES_P"/>
            </Variables>
    </xsl:variable>
    
    <xsl:template match="xhtml:td[*/@name = $tablehead-variable//Variable/@name]"/>
    
    <xsl:template match="xhtml:th[following-sibling::xhtml:td/*/@name = $tablehead-variable//Variable/@name]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:apply-templates select="following-sibling::xhtml:td/*[@name = $tablehead-variable//Variable/@name]"/>
        </xsl:copy>
    </xsl:template>
    
    </xsl:transform>
