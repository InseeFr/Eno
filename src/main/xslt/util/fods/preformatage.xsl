<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:im="http://www.insee.fr/schemas/ItemML"
    exclude-result-prefixes="#all" version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- Cette feuille de style pré-formate les xml ODS afin de supprimer les attributs number-columns-repeated 
        et d'ajouter un attribut "fictif" cell-position (ne figurant pas au format odt)
        afin de faciliter les transformations suivantes (permet de s'appuyer sur la position des cellules au sein d'une ligne).
        Les éléments sont recopiés à l'identique (à l'exception de @table:number-columns-repeated) 
        autant de fois que la valeur de l'attribut @table:number-columns-repeated, l'attribut en lui-même n'est pas conservé.-->
    
    <xsl:template match="/">
        <xsl:variable name="preformat1">
            <xsl:copy>          
                <!-- On applique les template de pré-formatage (répétition des cellules en prenant en compte @table:number-columns-repeated) -->
                <xsl:apply-templates select="node()"/>    
            </xsl:copy>            
        </xsl:variable>
        <!-- On applique ensuite les templates qui vont ajouter la position (sans doute faisable en une seule fois, mais plus complexe) -->
        <xsl:apply-templates select="$preformat1" mode="addPosition"/>
    </xsl:template>
    
    <xsl:template match="table:table" mode="addPosition">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="table:table-row" mode="addPosition"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="table:table-row" mode="addPosition">
        <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates select="*" mode="addPosition"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Nécessaire pour éviter de sortir des lignes vides. -->
    <xsl:template match="table:table-row[normalize-space(string(.))='']"/>
    
    <xsl:template match="table:table-row/*" mode="addPosition">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="table:cell-position" select="position()"/>
            <xsl:copy-of select="*"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Template générique de recopie. Le mode copy correspond à une copy forcée de tous les éléments. Le mode #default permet de ne recopier que les éléments ne contenant pas l'attribut @table:number-columns-repeated. -->
    <xsl:template match="node()" mode="#all">
        <xsl:copy>            
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Les éléments avec l'attribut @table:number-columns-repeated sont recopier autant de fois que la valeur de l'attribut.
         Ne s'applique que dans le mode par défaut. En mode copy, ces éléments sont recopiés (utilisé ci-dessous dans les recopies).-->
    <xsl:template match="*[@table:number-columns-repeated &gt; 1]">
        <xsl:call-template name="repetition">
            <xsl:with-param name="nbRepetition" select="@table:number-columns-repeated" tunnel="yes"/>
            <xsl:with-param name="element" select="." tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>
    
    <!-- Les attributs sont toujours recopiés. -->
    <xsl:template match="@*" mode="#all">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <!-- L'attribut @table:number-columns-repeated n'est jamais conservé. -->
    <xsl:template match="@table:number-columns-repeated" mode="#all"/>
    
    <!-- Template récursif de recopie d'un élément. -->
    <xsl:template name="repetition">
        <xsl:param name="nbRepetition" select="1" tunnel="yes"/>
        <xsl:param name="element" tunnel="yes"/>
        <xsl:param name="index" select="1"/>
        <!-- Recopie (on s'appuie sur le mode "copy" pour forcer la recopie). -->
        <xsl:apply-templates select="$element" mode="copy"/>
        <!-- Appel récursif s'il reste des recopies à faire. -->
        <xsl:if test="$nbRepetition - $index &gt; 0">
            <xsl:call-template name="repetition">
                <xsl:with-param name="index" select="$index + 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>