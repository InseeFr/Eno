<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:barcode="http://barcode4j.krysalis.org/ns"
    exclude-result-prefixes="xs"
    version="2.0">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xsl:variable name="lines-per-page" select="12" as="xs:integer"/>
    <xsl:variable name="lines-less-page1" select="2" as="xs:integer"/>

    <xsl:variable name="dynamic_arrays" as="node()">
        <Arrays>
            <Array nbcol="3">REPARTITION_CA</Array>
        </Arrays>
    </xsl:variable>

    <xsl:variable name="table" as="node()">
        <Tables>
            <!--Indiquer tableau par tableau les largeurs de ses colonnes
            Les fusions d'en-têtes de colonnes sont prises en compte
            <Table id="NomMetierDuTableau">
                <col>largeurDeLaPremiereColonneEnmm</col>
                <col>largeurDeLaDeuxiemeColonneEnmm</col>
                ...
                <col>largeurDeLaDerniereColonneEnmm</col>
            </Table>-->
        </Tables>
    </xsl:variable>

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
    <xsl:template match="node() | @*" mode="#all" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:block[@id]">
        <xsl:variable name="table-name">
            <xsl:choose>
                <xsl:when test="contains(@id,'-')">
                    <xsl:value-of select="substring-before(@id,'-')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@id"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="$dynamic_arrays//Array/text() = $table-name">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates select="node()" mode="roster">
                        <xsl:with-param name="roster-name" select="$table-name" tunnel="yes"/>
                        <xsl:with-param name="nbcol" select="$dynamic_arrays//Array[text()=$table-name]/@nbcol" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="$table//Table/@id = $table-name">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates select="node()" mode="table">
                        <xsl:with-param name="table-name" select="$table-name" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="substring($table-name,string-length($table-name)-1,1)='0'
                and $table//Table/@id = substring($table-name,1,string-length($table-name)-2)">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates select="node()" mode="table">
                        <xsl:with-param name="table-name" select="substring($table-name,1,string-length($table-name)-2)" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*" mode="#current"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="fo:table-cell" mode="table">
        <xsl:param name="table-name" tunnel="yes"/>
        
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="width">
                <xsl:variable name="count-following" as="xs:integer"
                    select="count(following-sibling::fo:table-cell)
                    + sum(following-sibling::fo:table-cell[@number-columns-spanned]/(xs:integer(@number-columns-spanned)-1))"/>
                <xsl:choose>
                    <xsl:when test="@number-columns-spanned != '1'">
                        <xsl:variable name="count-following-first" as="xs:integer" select="$count-following + xs:integer(@number-columns-spanned) -1"/>
                        <xsl:value-of select="sum($table//Table[@id=$table-name]/col[(count(following-sibling::col) &gt;= $count-following) 
                            and (count(following-sibling::col) &lt;= $count-following-first)]/xs:integer(text()))"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$table//Table[@id=$table-name]/col[count(following-sibling::col) = $count-following]"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:value-of select="'mm'"/>
            </xsl:attribute>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-header/fo:table-row/fo:table-cell[1]" mode="roster">
        <xsl:param name="nbcol" tunnel="yes"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="width" select="'120mm'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="fo:table-header/fo:table-row/fo:table-cell[2]" mode="roster">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="width" select="'20mm'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="fo:table-header/fo:table-row/fo:table-cell[3]" mode="roster">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="width" select="'40mm'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-header/fo:table-row/fo:table-cell[4]" mode="roster">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="width" select="'40mm'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>

        <xsl:choose>
            <xsl:when test="preceding::fo:table-row[parent::fo:table-body and starts-with(ancestor::fo:table/parent::fo:block/@id,$roster-name)]">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:attribute name="height" select="'11mm'"/>
                    <xsl:apply-templates select="node()" mode="static-roster"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:attribute name="height" select="'11mm'"/>
                    <xsl:apply-templates select="node()" mode="personalized-roster"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[1]" mode="personalized-roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:element name="fo:block">
                <xsl:value-of select="concat('${',$roster-name,'.',$roster-name,'1}')"/>
            </xsl:element>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[2]" mode="personalized-roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:element name="fo:block">
                <xsl:value-of select="concat('$!{',$roster-name,'.',$roster-name,'2}')"/>
            </xsl:element>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[2]" mode="static-roster">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="background-color" select="'#CCCCCC'"/>
            <xsl:element name="fo:block"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:block-container[@height='24mm']" mode="personalized-roster">
        <xsl:copy>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:block-container[@height='24mm']" mode="static-roster">
        <xsl:copy>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>