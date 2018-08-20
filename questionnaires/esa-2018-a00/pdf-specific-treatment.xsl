<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:variable name="lines-per-page" select="9" as="xs:integer"/>
    
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
    
    <xsl:template match="fo:block[contains(@id,'-')]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()" mode="roster">
                <xsl:with-param name="roster-name" select="substring-before(@id,'-')" tunnel="yes"/>
                <xsl:with-param name="page" select="substring-after(@id,'-')" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fo:table-header/fo:table-row/fo:table-cell[1]" mode="roster">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="width" select="'170mm'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="fo:table-header/fo:table-row/fo:table-cell[2]" mode="roster">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="width" select="'24mm'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="fo:table-header/fo:table-row/fo:table-cell[3]" mode="roster">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="width" select="'85mm'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fo:table-body/fo:table-row" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="page" tunnel="yes"/>
        
        <xsl:variable name="line-number" as="xs:integer">
            <xsl:choose>
                <xsl:when test="$page = '1'">
                    <xsl:value-of select="position()"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="number($page) * $lines-per-page -1 - number($page) + position()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()" mode="roster">
                <xsl:with-param name="line-number" select="$line-number" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    
    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[1]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>
        
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'_LIB-',$line-number,'})')"/>
            <xsl:value-of select="'&lt;fo:block'"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'_niveau-',$line-number,'} in (''Titre0'',''Titre1'',''Titre2'',''Intertitre0'',''Intertitre1'',''Intertitre2'')) font-weight=&quot;bold&quot;#{end}')"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'_niveau-',$line-number,'} in (''Ventilation0'',''Ventilation1'',''Ventilation2'',''Intertitre0'',''Intertitre1'',''Intertitre2'')) font-style=&quot;italic&quot;#{end}')"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'_niveau-',$line-number,'} in (''Code1'',''Titre1'',''Intertitre1'',''Ventilation1'')) text-indent=&quot;2em&quot;#{end}')"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'_niveau-',$line-number,'} in (''Code2'',''Titre2'',''Intertitre2'',''Ventilation2'')) text-indent=&quot;4em&quot;#{end}')"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'_niveau-',$line-number,'} eq ''Code3'') text-indent=&quot;6em&quot;#{end}')"/>
            <xsl:value-of select="'&gt;'"/>
            <xsl:value-of select="concat('$!{',$roster-name,'_LIB-',$line-number,'}')"/>
            <xsl:value-of select="'&lt;/fo:block&gt;'"/>
            <xsl:value-of select="'#{else}'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
            <xsl:value-of select="'#{end}'"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[2]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>
        
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'_LIB-',$line-number,'})')"/>
            <xsl:value-of select="concat('$!{',$roster-name,'_CO-',$line-number,'}')"/>
            <xsl:value-of select="'#{else}'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
            <xsl:value-of select="'#{end}'"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[3]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>
        
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'_LIB-',$line-number,'} || !$!{',$roster-name,'_MO-',$line-number,'})')"/>
            <xsl:value-of select="'#{else}'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
            <xsl:value-of select="'#{end}'"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>