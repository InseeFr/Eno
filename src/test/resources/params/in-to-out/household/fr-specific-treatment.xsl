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
    
    <!-- Autre : précisez dans les tableaux -->
    
    <xsl:variable name="table-give-details">
        <TableGiveDetailsList>
            <TableGiveDetail other="RAISPROMO_A" give-details="RAISONPROMO_A_LIB" QOPI="jjfqblut-Details-1" value="1"/>
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
    
    <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()=$table-give-details//TableGiveDetail/@QOPI]"/>
    
    <xsl:template match="xhtml:body//xhtml:td[*[ends-with(@id,'-control') and substring-before(@id,'-control')=$table-give-details//TableGiveDetail/@other]]">
        <xsl:variable name="give-details" select="$table-give-details//TableGiveDetail[@other=substring-before(current()/*/@id,'-control')]/@give-details"/>
        <xsl:variable name="QOPI-id" select="$table-give-details//TableGiveDetail[@other=substring-before(current()/*/@id,'-control')]/@QOPI"/>
        
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:if test="following-sibling::xhtml:td[*/@id=concat($give-details,'-control')]">
                <xf:group id="{$QOPI-id}-control" bind="{$QOPI-id}-bind">
                    <xsl:copy-of select="following-sibling::xhtml:td[xf:input/@id=concat($give-details,'-control')]/xf:input"/>
                </xf:group>
            </xsl:if>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:body//xhtml:td[*[ends-with(@id,'-control') and substring-before(@id,'-control')=$table-give-details//TableGiveDetail/@give-details]]"/>
    
    <!-- Modification des bornes des dates et durées -->
    <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()='DATEDEP-layout-Y' or name()='ANNEEFORM']/item[number(value) &lt; 1950]"/>

    <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()='ANNEEREM' or name()='ANNEEFP' or name()='ANNEE_FONC']/item[number(value) &lt; 1968]"/>

    <xsl:template match="xf:bind[@id='NBHEURPROF-layout-H-bind']/xf:constraint/@value">
        <xsl:attribute name="value">
            <xsl:value-of select="replace(.,'=99','=50')"/>
        </xsl:attribute>
    </xsl:template>
    <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()='NBHEURPROF-layout-H']/alert">
        <xsl:copy>
            <xsl:value-of select="replace(.,'99','50')"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xf:bind[@id='NBHEURPREP-layout-H-bind' or @id='NBHEURAUTRE-layout-H-bind' or @id='NBHEURHEBDO-layout-H-bind']/xf:constraint/@value">
        <xsl:attribute name="value">
            <xsl:value-of select="replace(.,'=99','=80')"/>
        </xsl:attribute>
    </xsl:template>
    <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()='NBHEURPREP-layout-H' or name()='NBHEURAUTRE-layout-H' or name()='NBHEURHEBDO-layout-H']/alert">
        <xsl:copy>
            <xsl:value-of select="replace(.,'99','80')"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xf:bind[@id='DUREEPRIV-layout-Y-bind']/xf:constraint/@value">
        <xsl:attribute name="value">
            <xsl:value-of select="replace(.,'=99','=60')"/>
        </xsl:attribute>
    </xsl:template>
    <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()='DUREEPRIV-layout-Y']/alert">
        <xsl:copy>
            <xsl:value-of select="replace(.,'99','60')"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xf:bind[@id='DUREETP-layout-Y-bind' or @id='DUREEINTERR_1-layout-Y-bind' or @id='DUREEINTERR_2-layout-Y-bind' or @id='DUREEINTERR_3-layout-Y-bind' or @id='DUREEFORM-layout-Y-bind']/xf:constraint/@value">
        <xsl:attribute name="value">
            <xsl:value-of select="replace(.,'=99','=45')"/>
        </xsl:attribute>
    </xsl:template>
    <xsl:template match="xf:instance[@id='fr-form-resources']//*[name()='DUREETP-layout-Y' or name()='DUREEINTERR_1-layout-Y' or name()='DUREEINTERR_2-layout-Y' or name()='DUREEINTERR_3-layout-Y' or name()='DUREEFORM-layout-Y']/alert">
        <xsl:copy>
            <xsl:value-of select="replace(.,'99','45')"/>
        </xsl:copy>
    </xsl:template>
    

</xsl:transform>
