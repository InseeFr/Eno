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

    <xsl:variable name="table" as="node()">
        <Tables>
            <Table id="CLIENTELE" col="3+1">
                <un>25</un>
                <deux>45</deux>
                <trois>90</trois>
                <quatre>30</quatre>
            </Table>

            <Table id="BTPNAT" col="2+2">
                <un>30</un>
                <deux>100</deux>
                <trois>45</trois>
                <quatre>45</quatre>
            </Table>
            <Table id="CPTIMMO" col="2+2">
                <un>30</un>
                <deux>90</deux>
                <trois>45</trois>
                <quatre>45</quatre>
            </Table>

            <Table id="NAT_FINANC" col="2+1">
                <un>50</un>
                <deux>120</deux>
                <trois>45</trois>
            </Table>
            <Table id="CLIS_CLIENT" col="2+1">
                <un>50</un>
                <deux>120</deux>
                <trois>50</trois>
            </Table>
            <Table id="BTP_CL" col="2+1">
                <un>140</un>
                <deux>45</deux>
                <trois>45</trois>
            </Table>
            <Table id="FOURNISSEURS" col="2+1">
                <un>50</un>
                <deux>90</deux>
                <trois>45</trois>
            </Table>
            <Table id="COOP_RESULT" col="2+1">
                <un>30</un>
                <deux>160</deux>
                <trois>45</trois>
            </Table>
            <Table id="CLIS_CLIENT_HBG" col="2+1">
                <un>30</un>
                <deux>100</deux>
                <trois>65</trois>
            </Table>
            <Table id="IMONAT" col="2+1">
                <un>35</un>
                <deux>120</deux>
                <trois>45</trois>
            </Table>

            <Table id="VENTIL_RCH" col="1+1">
                <un>100</un>
                <deux>45</deux>
            </Table>
            <Table id="VENTIL_ORIG" col="1+1">
                <un>100</un>
                <deux>25</deux>
            </Table>
            <Table id="SURF_GROS" col="1+1">
                <un>90</un>
                <deux>60</deux>
            </Table>
            <Table id="SURF_DETAIL" col="1+1">
                <un>80</un>
                <deux>45</deux>
            </Table>
            <Table id="C_AGR_LOC_EFS" col="1+1">
                <un>90</un>
                <deux>30</deux>
            </Table>
            <Table id="V_RCH" col="1+1">
                <un>50</un>
                <deux>25</deux>
            </Table>
            <Table id="CLIS_PAYS_HBG" col="1+1">
                <un>90</un>
                <deux>65</deux>
            </Table>
            <Table id="C_AGRI" col="1+1">
                <un>60</un>
                <deux>35</deux>
            </Table>
            <Table id="C_AGR_LOCAL" col="1+1">
                <un>90</un>
                <deux>35</deux>
            </Table>
            <Table id="IMO_CL" col="1+1">
                <un>150</un>
                <deux>45</deux>
            </Table>
            <Table id="CLIS_ETAB" col="1+1">
                <un>105</un>
                <deux>65</deux>
            </Table>
            <Table id="CLIS_PAYS" col="1+1">
                <un>90</un>
                <deux>65</deux>
            </Table>

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
            <xsl:choose>
                <xsl:when test="$nbcol = '4'">
                    <xsl:attribute name="width" select="'165mm'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="width" select="'205mm'"/>
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
                    <xsl:value-of select="number($page) * $lines-per-page -1 - $lines-per-page + position()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="height" select="'11mm'"/>
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
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[2]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>

        <xsl:value-of select="concat('#{if}($!{',$roster-name,'-',$line-number,'-',$roster-name,'_CO})')"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:element name="fo:block">
                <xsl:value-of select="concat('$!{',$roster-name,'-',$line-number,'-',$roster-name,'_CO}')"/>
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

        <xsl:value-of select="concat('#{if}(!$!{',$roster-name,'-',$line-number,'-',$roster-name,'_LIB} or $!{',$roster-name,'-',$line-number,'-',$roster-name,'_MO})')"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
        <xsl:value-of select="'#{else}'"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="background-color" select="'#CCCCCC'"/>
            <xsl:element name="fo:block"/>
        </xsl:copy>
        <xsl:value-of select="'#{end}'"/>
    </xsl:template>

    <xsl:template match="fo:table-body/fo:table-row/fo:table-cell[4]" mode="roster">
        <xsl:param name="roster-name" tunnel="yes"/>
        <xsl:param name="line-number" as="xs:integer" tunnel="yes"/>

        <xsl:value-of select="concat('#{if}(!$!{',$roster-name,'-',$line-number,'-',$roster-name,'_LIB} or $!{',$roster-name,'-',$line-number,'-',$roster-name,'_MOST})')"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()" mode="roster"/>
        </xsl:copy>
        <xsl:value-of select="'#{else}'"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="background-color" select="'#CCCCCC'"/>
            <xsl:element name="fo:block"/>
        </xsl:copy>
        <xsl:value-of select="'#{end}'"/>
    </xsl:template>

    <xsl:template match="fo:block-container[@height='24mm']" mode="roster">
        <xsl:copy>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="barcode:barcode/@message">
        <xsl:attribute name="message">
            <xsl:choose>
                <xsl:when test="contains(.,'idQuestionnaire')">
                    <xsl:value-of select="replace(.,'\{idQuestionnaire\}','{idQuestionnaire}-#page-id#')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

</xsl:stylesheet>