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

    <xsl:template match="fo:block[@id and not(contains(@id,'-'))]">
        <xsl:choose>
            <xsl:when test="$table//Table/@id = current()/@id">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates select="node()" mode="table">
                        <xsl:with-param name="table-name" select="@id" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="substring(current()/@id,string-length(current()/@id)-1,1)='0'
                and $table//Table/@id = substring(current()/@id,1,string-length(current()/@id)-2)">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates select="node()" mode="table">
                        <xsl:with-param name="table-name" select="substring(current()/@id,1,string-length(current()/@id)-2)" tunnel="yes"/>
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

        <xsl:variable name="table-carac" as="node()">
            <xsl:copy-of select="$table//Table[@id=$table-name]"/>
        </xsl:variable>

        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="width">
                <xsl:choose>
                    <xsl:when test="$table-carac/@col='1+1'">
                        <xsl:choose>
                            <xsl:when test="not(preceding-sibling::fo:table-cell)">
                                <xsl:value-of select="$table-carac/un"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$table-carac/deux"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$table-carac/@col='2+1'">
                        <xsl:choose>
                            <xsl:when test="not(following-sibling::fo:table-cell)">
                                <xsl:value-of select="$table-carac/trois"/>
                            </xsl:when>
                            <xsl:when test="@number-columns-spanned='2'">
                                <xsl:value-of select="number($table-carac/un)+number($table-carac/deux)"/>
                            </xsl:when>
                            <xsl:when test="not(preceding-sibling::fo:table-cell)">
                                <xsl:value-of select="$table-carac/un"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$table-carac/deux"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$table-carac/@col='3+1'">
                        <xsl:choose>
                            <xsl:when test="not(following-sibling::fo:table-cell)">
                                <xsl:value-of select="$table-carac/quatre"/>
                            </xsl:when>
                            <xsl:when test="@number-columns-spanned='3'">
                                <xsl:value-of select="number($table-carac/un)+number($table-carac/deux)+number($table-carac/trois)"/>
                            </xsl:when>
                            <xsl:when test="count(following-sibling::fo:table-cell) = 1">
                                <xsl:value-of select="$table-carac/trois"/>
                            </xsl:when>
                            <xsl:when test="count(following-sibling::fo:table-cell) = 2">
                                <xsl:value-of select="$table-carac/deux"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$table-carac/un"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$table-carac/@col='2+2'">
                        <xsl:choose>
                            <xsl:when test="not(following-sibling::fo:table-cell)">
                                <xsl:value-of select="$table-carac/quatre"/>
                            </xsl:when>
                            <xsl:when test="@number-columns-spanned='2'">
                                <xsl:value-of select="number($table-carac/un)+number($table-carac/deux)"/>
                            </xsl:when>
                            <xsl:when test="count(following-sibling::fo:table-cell) = 1">
                                <xsl:value-of select="$table-carac/trois"/>
                            </xsl:when>
                            <xsl:when test="count(following-sibling::fo:table-cell) = 2">
                                <xsl:value-of select="$table-carac/deux"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$table-carac/un"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$table-carac/@col='5'">
                        <xsl:choose>
                            <xsl:when test="not(following-sibling::fo:table-cell)">
                                <xsl:value-of select="$table-carac/cinq"/>
                            </xsl:when>
                            <xsl:when test="count(following-sibling::fo:table-cell) = 1">
                                <xsl:value-of select="$table-carac/quatre"/>
                            </xsl:when>
                            <xsl:when test="count(following-sibling::fo:table-cell) = 2">
                                <xsl:value-of select="$table-carac/trois"/>
                            </xsl:when>
                            <xsl:when test="count(following-sibling::fo:table-cell) = 3">
                                <xsl:value-of select="$table-carac/deux"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$table-carac/un"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'150'"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:value-of select="'mm'"/>
            </xsl:attribute>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:block[contains(@id,'-')]">
        
        <xsl:variable name="roster-name" select="substring-before(@id,'-')"/>
        <xsl:variable name="page-number" select="substring-after(@id,'-')"/>

        <xsl:choose>
            <xsl:when test="$dynamic_arrays//Array/text() = $roster-name">
                <xsl:if test="$page-number != '1'">
                    <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',(number($page-number) -1) * $lines-per-page - $lines-less-page1 ,'-',$roster-name,'1})')"/>
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
            <xsl:when test="$table//Table/@id = $roster-name">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates select="node()" mode="table">
                        <xsl:with-param name="table-name" select="$roster-name" tunnel="yes"/>
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
        <xsl:param name="page" tunnel="yes"/>

        <xsl:variable name="line-number" as="xs:integer">
            <xsl:choose>
                <xsl:when test="$page = '1'">
                    <xsl:value-of select="position()"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="(number($page) -1) * $lines-per-page - $lines-less-page1 + position()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="$page != '1' or position() &lt;= $lines-per-page - $lines-less-page1">
            <xsl:copy>
                <xsl:apply-templates select="@*"/>
                <xsl:attribute name="height" select="'11mm'"/>
                <xsl:apply-templates select="node()" mode="roster">
                    <xsl:with-param name="line-number" select="$line-number" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:copy>            
        </xsl:if>
    </xsl:template>


    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[1]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>

        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'1})')"/>
            <xsl:element name="fo:block">
                <xsl:value-of select="concat('$!{',$roster-name,'-',$line-number,'-',$roster-name,'1}')"/>
            </xsl:element>
            <xsl:value-of select="'#{else}'"/>
            <xsl:apply-templates select="node()" mode="roster"/>
            <xsl:value-of select="'#{end}'"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[2]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>

        <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'2})')"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:element name="fo:block">
                <xsl:value-of select="concat('$!{',$roster-name,'-',$line-number,'-',$roster-name,'2}')"/>
            </xsl:element>
        </xsl:copy>
        <xsl:value-of select="'#{else}'"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="background-color" select="'#CCCCCC'"/>
            <xsl:element name="fo:block"/>
        </xsl:copy>
        <xsl:value-of select="'#{end}'"/>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[3]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>

        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:block-container[@height='24mm']" mode="roster">
        <xsl:copy>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>