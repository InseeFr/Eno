<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform Pogues.xsd"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:sparql="http://www.w3.org/2005/sparql-results#"
    exclude-result-prefixes="sparql xs xd xsi pogues" version="2.0">
    <xsl:output indent="yes"/>
    <!--exclude-result-prefixes="pogues sparql xs xd" -->
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jun 15, 2017</xd:p>
            <xd:p><xd:b>Author:</xd:b>Antoine Dreyer</xd:p>
            <xd:p>Missing : sparql and CodeLists</xd:p>
        </xd:desc>
    </xd:doc>
  
    <xd:doc>
        add Value element to CodeList/Code when it is missing
        
        <xd:desc/>
    </xd:doc> 
    <xsl:template match="pogues:CodeList/pogues:Code[not(pogues:Value)]">
        <xsl:copy>
            <Value><xsl:value-of select="count(preceding-sibling::pogues:Code)+1"/></Value>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="./*"/>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc>
        gestion des Code avec Value à blanc
     <xd:desc/>
    </xd:doc> 
    <xsl:template match="pogues:CodeList/pogues:Code/pogues:Value[not(text()) or text()='']">
        <xsl:copy><xsl:value-of select="count(parent::pogues:Code/preceding-sibling::pogues:Code)+1"/></xsl:copy>
    </xsl:template>
   
<!--    <xd:doc>
    rajout du nombre d'éléments dans les Dimension PRIMARY pour calculer plus facilement les coordonnées des tableaux à 2 dimensions
        <xd:desc/>
    </xd:doc> 
    <xsl:template match="pogues:Dimension[@dimensionType='PRIMARY']">
        <xsl:copy>
            <xsl:attribute name="dimension1Length">
                <xsl:choose>
                    <xsl:when test="@dynamic = '0'"><!-\- La première dimension est une Liste de codes -\->
                        <xsl:value-of select="//pogues:Questionnaire/pogues:CodeLists/pogues:CodeList[@id=current()/pogues:CodeListReference]/count(pogues:Code)"/>
                    </xsl:when>
                    <xsl:otherwise><!-\- La première dimension est un roster -\->
                        <xsl:variable name="maximumAllowed" select="substring-after(@dynamic,'-')"/>
                        <xsl:choose><!-\- C'est un tableau dynamique -\->
                            <xsl:when test="$maximumAllowed=''">
                                <xsl:value-of select="'1'"/>
                            </xsl:when>
                            <xsl:otherwise><!-\- C'est un tableau standard avec roster -\->
                                <xsl:value-of select="$maximumAllowed"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>-->
   
    <xd:doc>
        création de la Dimension SECONDARY pour les tableaux à 2 dimensions définis sans
        <xd:desc/>
    </xd:doc> 
    <xsl:template match="pogues:ResponseStructure[not(pogues:Dimension[@dimensionType='SECONDARY'])
        and parent::pogues:Child/@questionType='TABLE']">
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="./*"/>
            <Dimension dimensionType="SECONDARY" dynamic="0">
                <CodeListReference><xsl:value-of select="concat(../pogues:Name,'-dim2')"/></CodeListReference>
            </Dimension>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc>
        création de la CodeList de la Dimension SECONDARY créée ci-dessus
        <xd:desc/>
    </xd:doc>     
    <xsl:template match="pogues:CodeLists">
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="./*"/>
            <xsl:apply-templates select="//pogues:ResponseStructure[not(pogues:Dimension[@dimensionType='SECONDARY'])
                and parent::pogues:Child/@questionType='TABLE']" mode="codeList"/>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc>
        creation of missing codeList from ResponseStructure
        <xd:desc/>
    </xd:doc> 
    
    <xsl:template match="pogues:ResponseStructure" mode="codeList">
        <CodeList id="{concat(replace(../pogues:Name,':',''),'-dim2')}">
            <Name><xsl:value-of select="concat('Deuxieme_dimension_du_tableau_',replace(../pogues:Name,':',''))"/></Name>
            <!--<xsl:value-of select="concat('Deuxieme dimension du tableau ',replace(../pogues:Label,'^.*\} ',''))"/>-->
            <Label><xsl:value-of select="concat('Dim2 ',replace(../pogues:Label,'^.*\} ',''))"/></Label>
            <xsl:apply-templates select="pogues:Dimension[@dimensionType='MEASURE']" mode="codeList"/>
        </CodeList>
    </xsl:template>
    
    <xd:doc>
        creation of missing code from Dimension
        <xd:desc/>
    </xd:doc> 
    <xsl:template match="pogues:Dimension" mode="codeList">
        <Code>
            <Value><xsl:value-of select="count(preceding-sibling::pogues:Dimension[@dimensionType='MEASURE'])+1"/></Value>
            <Label><xsl:value-of select="pogues:Label"/></Label>
        </Code>
    </xsl:template><!-- verrue :  -->
   
<!--    <xd:doc>
        transformation des CodeListSpecification en éléments de CodeLists
        <xd:desc/>
    </xd:doc> 
    <xsl:template match="pogues:CodeListSpecification">
        <xsl:variable name="listID" select="@id"/>
        <xsl:element name="CodeList" namespace="http://xml.insee.fr/schema/applis/pogues">
            <xsl:attribute name="id" select="@id"/>
            <xsl:choose>
                <xsl:when test="pogues:Name">
                    <xsl:copy-of select="pogues:Name"/>
                </xsl:when>
                <xsl:otherwise>
                    <Name>
                        <xsl:value-of select="@id"/>
                    </Name>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="pogues:Label">
                    <xsl:copy-of select="pogues:Label"/>
                </xsl:when>
                <xsl:when test="pogues:Name">
                    <xsl:value-of select="concat('Liste des codes de la nomenclature ',pogues:Name)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat('Liste des codes de la nomenclature ',@id)"/>
                </xsl:otherwise>
            </xsl:choose>
            <!-\-TODO : find file-\->
            <!-\-xsl:variable name="RMESList" select="document('temp/tempRmes.xml')"/>
            <xsl:apply-templates select="$RMESList/sparql:ids/sparql:sparql[@id=$listID]/sparql:results/sparql:result[descendant::sparql:literal/@xml:lang='fr']"/-\->
            <!-\-TODO : find file-\->
        </xsl:element>
    </xsl:template>-->
  
<!--    <xd:doc>
        transformation of sparql result into Code
        <xd:desc/>
    </xd:doc>  
    <xsl:template match="sparql:result">
        <xsl:element name="Code" namespace="http://xml.insee.fr/schema/applis/pogues">
            <xsl:element name="Value" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:value-of select="sparql:binding[@name='code']/sparql:literal"/>
            </xsl:element>
            <xsl:element name="Label" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:value-of select="sparql:binding[@name='intitule']/sparql:literal[@xml:lang='fr']"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>-->
    
 <!--   <xd:doc>
        suppression of "^##{" and "}$" in Labels
        <xd:desc/>
    </xd:doc>  
    <xsl:template match="pogues:Label">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="starts-with(text(),'##{')">
                    <xsl:value-of select="replace(text(),'^.*\} ','')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="text()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template><!-\- La non-réponse, ça casse tout... -\->-->
   
<!--    <xd:doc>
        suppress nonResponseModality
        <xd:desc/>
    </xd:doc> 
    <xsl:template match="pogues:NonResponseModality"/>-->
    
    <xd:doc>
        cas général : on recopie la structure initiale sans y retoucher
        <xd:desc/>
    </xd:doc> 
    
    <xsl:template match="node()">
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="./*"/>
        </xsl:copy>
    </xsl:template>
	
	
</xsl:stylesheet>
	