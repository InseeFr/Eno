<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:variable name="lines-per-page" select="9" as="xs:integer"/>
    
    <xsl:variable name="dynamic_arrays" as="node()">
        <Arrays>
            <Array nbcol="4">vacT</Array>
            <Array nbcol="4">vacHT</Array>
            <Array nbcol="4">BTPIMO_vacB</Array>
            <Array nbcol="4">BTPIMO_vacM</Array>
            <Array nbcol="4">BTPIMO_vacS</Array>
            <Array nbcol="3">CA_ProdB1ST1</Array>
            <Array nbcol="3">CA_ProdM2ST1</Array>
            <Array nbcol="3">CA_ProdM2ST2</Array>
            <Array nbcol="3">CA_ProdM3ST1</Array>
            <Array nbcol="3">CA_ProdM3ST2</Array>
            <Array nbcol="3">CA_ProdM3ST3</Array>
            <Array nbcol="3">CA_ProdM4ST1</Array>
            <Array nbcol="3">CA_ProdM4ST2</Array>
            <Array nbcol="3">CA_ProdM4ST3</Array>
            <Array nbcol="3">CA_ProdM4ST4</Array>
            <Array nbcol="3">CA_ProdS1ST1</Array>
            <Array nbcol="3">DPE_SVC</Array>
            <Array nbcol="3">V_PROD_IMMO</Array>
            <Array nbcol="3">vacB</Array>
            <Array nbcol="3">vacM</Array>
            <Array nbcol="3">vacS</Array>
            <Array nbcol="3">vacU</Array>
            <Array nbcol="3">VFV1</Array>
            <Array nbcol="3">VFV1_MB</Array>
            <Array nbcol="3">VFV2</Array>
            <Array nbcol="3">VFV2_MB</Array>
        </Arrays>
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
    
    <xsl:template match="fo:external-graphic/@src[contains(.,'mask_number.png')]" mode="#all">
        <xsl:attribute name="src">
            <xsl:value-of select="replace(.,'mask_number.png','mask_number_esa.png')"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="fo:block[contains(@id,'-')]">
        <xsl:variable name="roster-name" select="substring-before(@id,'-')"/>
        <xsl:variable name="page-number" select="substring-after(@id,'-')"/>
        
        <xsl:choose>
            <xsl:when test="$dynamic_arrays//Array/text() = $roster-name">
                <xsl:if test="$page-number != '1'">
                    <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',(number($page-number) -1) * $lines-per-page -1,'-',$roster-name,'_LIB})')"/>
                </xsl:if>
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates select="node()" mode="roster">
                        <xsl:with-param name="roster-name" select="$roster-name" tunnel="yes"/>
                        <xsl:with-param name="page" select="$page-number" tunnel="yes"/>
                        <xsl:with-param name="nbcol" select="$dynamic_arrays//Array[text()=$roster-name]/@nbcol" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:copy>
                <xsl:if test="$page-number != '1'">
                    <xsl:value-of select="'#{end}'"/>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*" mode="#current"/>
                </xsl:copy>
                </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="fo:table-header/fo:table-row/fo:table-cell[1]" mode="roster">
        <xsl:param name="nbcol" tunnel="yes"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:choose>
                <xsl:when test="$nbcol = '4'">
                    <xsl:attribute name="width" select="'70mm'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="width" select="'150mm'"/>
                </xsl:otherwise>
            </xsl:choose>
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
            <xsl:attribute name="width" select="'85mm'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fo:table-header/fo:table-row/fo:table-cell[4]" mode="roster">
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
                    <xsl:value-of select="number($page) * $lines-per-page -1 - $lines-per-page + position()"/>
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
            <fo:block>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_LIB})')"/>
            <xsl:value-of select="'&lt;fo:block'"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''titre0'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''titre1'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''titre2'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''intertitre0'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''intertitre1'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''intertitre2'')
                ) font-weight=&quot;bold&quot;#{end}')"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''ventilation0'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''ventilation1'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''ventilation2'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''intertitre0'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''intertitre1'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''intertitre2'')
            ) font-style=&quot;italic&quot;#{end}')"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''code1'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''titre1'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''intertitre1'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''ventilation1'')
                ) text-indent=&quot;2em&quot;#{end}')"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''code2'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''titre2'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''intertitre2'') || 
                $!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq (''ventilation2'')
                ) text-indent=&quot;4em&quot;#{end}')"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_niveau} eq ''code3'') text-indent=&quot;6em&quot;#{end}')"/>
            <xsl:value-of select="'&gt;'"/>
            <xsl:value-of select="concat('$!{',$roster-name,'-',$line-number,'-',$roster-name,'_LIB}')"/>
            <xsl:value-of select="'&lt;/fo:block&gt;'"/>
            <xsl:value-of select="'#{else}'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
            <xsl:value-of select="'#{end}'"/>
            </fo:block>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[2]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>
        
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <fo:block>
                <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_LIB})')"/>
                <xsl:value-of select="concat('&lt;fo:block&gt;$!{',$roster-name,'-',$line-number,'-',$roster-name,'_CO}&lt;/fo:block&gt;')"/>
                <xsl:value-of select="'#{else}&lt;fo:block background-color=&quot;#CCCCCC&quot;/&gt;'"/>
                <xsl:value-of select="'#{end}'"/>
            </fo:block>
            </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[3]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>
        
        <xsl:copy>
            <fo:block>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_LIB} and !$!{',$roster-name,'-',$line-number,'-',$roster-name,'_MO})')"/>
            <xsl:value-of select="'&lt;fo:block/&gt;#{else}'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
            <xsl:value-of select="'#{end}'"/>
            </fo:block>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[4]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>

        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <fo:block>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_LIB} and !$!{',$roster-name,'-',$line-number,'-',$roster-name,'_MOST})')"/>
            <xsl:value-of select="'&lt;fo:block/&gt;#{else}'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
            <xsl:value-of select="'#{end}'"/>
            </fo:block>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:block[fo:external-graphic and contains(text(),'(JJMMAAAA)')]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <fo:external-graphic src="date.png"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>