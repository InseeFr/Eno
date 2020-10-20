<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:poguesFilter="http://xml.insee.fr/schema/applis/poguesFilter"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="xs" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>

    <!-- xsi:schemaLocation="Pogues.xsd"-->
    <!--xmlns:xs="http://www.w3.org/2001/XMLSchema"-->
    <xd:doc scope="stylesheet">
        <xd:desc>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>List of the Child (Sequence ; Question), with position</xd:desc>
    </xd:doc>
    <xsl:variable name="child-position-list" as="node()">
        <poguesFilter:IdList>
            <xsl:for-each select="//*[@id]">
                <xsl:sort select="position()"/>
                <!-- TODO : IfThenElse : add IfThenElse with their Expression -->
                <xsl:if test="local-name(.)='Child' or local-name(.)='Questionnaire'">
                    <poguesFilter:idElement id="{./@id}" position="{position()}">
                        <poguesFilter:childrenId>
                            <xsl:for-each select="pogues:Child">
                                <poguesFilter:childId>
                                    <xsl:value-of select="./@id"/>
                                </poguesFilter:childId>
                            </xsl:for-each>
                        </poguesFilter:childrenId>
                    </poguesFilter:idElement>
                </xsl:if>
            </xsl:for-each>
        </poguesFilter:IdList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>The same list where parents contain their descendants</xd:desc>
    </xd:doc>
    <xsl:variable name="child-tree">
        <poguesFilter:IdList>
            <xsl:apply-templates select="$child-position-list/poguesFilter:idElement[1]" mode="children-in-tree"/>
        </poguesFilter:IdList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>The template necessary to browse child-position-list and create the variable $child-tree </xd:desc>
    </xd:doc>
    <xsl:template match="poguesFilter:idElement" mode="children-in-tree">
        <xsl:copy>
            <xsl:copy-of select="@id"/>
            <xsl:copy-of select="@position"/>
            <xsl:apply-templates select="$child-position-list/poguesFilter:idElement[@id=current()/poguesFilter:childrenId/poguesFilter:childId]" mode="children-in-tree"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>The list of filters and loops directly from the source file</xd:desc>
    </xd:doc>
    <xsl:variable name="list-filter" as="node()">
        <poguesFilter:FilterList>
            <xsl:for-each select="/pogues:Questionnaire/pogues:FlowControl | //pogues:Next">
                <xsl:variable name="from" select="substring-before(pogues:IfTrue,'-')"/>
                <xsl:variable name="to" select="substring-after(pogues:IfTrue,'-')"/>

                <poguesFilter:Filter id="{@id}">
                    <poguesFilter:From id="{$from}" position="{$child-tree//poguesFilter:idElement[@id = $from]/@position}"/>
                    <poguesFilter:To id="{$to}" position="{$child-tree//poguesFilter:idElement[@id = $to]/@position}"/>
                </poguesFilter:Filter>
            </xsl:for-each>
        </poguesFilter:FilterList>
    </xsl:variable>

    <xsl:variable name="list-loop-filter" as="node()">
        <xsl:copy-of select="$list-filter"/>
    </xsl:variable>

    <xd:doc>
        <xd:desc>the root element</xd:desc>
    </xd:doc>
    <xsl:template match="/pogues:Questionnaire" priority="1">
        <xsl:copy>
            <xsl:copy-of select="@* | text() | processing-instruction()"/>
            <xsl:apply-templates select="node()[1]" mode="first-child-next-brother">
                <xsl:with-param name="stop-position" select="''"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>every tag not containing Child can be directly copied and calls its first following-sibling</xd:desc>
    </xd:doc>
    <xsl:template match="*[not(descendant::pogues:Child)] | comment()" mode="first-child-next-brother">
        <xsl:param name="stop-position"/>

        <xsl:copy-of select="."/>
        <xsl:if test="self::comment()">
            <xsl:text>&#13;</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="following-sibling::node()[1]" mode="first-child-next-brother">
            <xsl:with-param name="stop-position" select="$stop-position"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        <xd:desc>Tags containing Child call their first child and their first following-sibling</xd:desc>
    </xd:doc>
    <xsl:template match="*[descendant::pogues:Child]" mode="first-child-next-brother">
        <xsl:param name="stop-position"/>

        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="child::node()[1]" mode="first-child-next-brother">
                <xsl:with-param name="stop-position" select="$stop-position"/>
            </xsl:apply-templates>
        </xsl:copy>
        <xsl:apply-templates select="following-sibling::node()[1]" mode="first-child-next-brother">
            <xsl:with-param name="stop-position" select="$stop-position"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        Child : Sequence or Question
        parameters :
        - stop-position : first Child not to take
        - goto-style (none, before, after) : with stop-position, identifies the last Goto already inserted
        <xd:desc/>
    </xd:doc>
    <xsl:template match="pogues:Child" priority="1" mode="first-child-next-brother">
        <xsl:param name="current-filter" select="''"/>
        <xsl:param name="stop-position"/>

        <xsl:variable name="current-id" select="@id"/>
        <xsl:variable name="possible-next-filters" as="node()">
            <poguesFilter:FilterList>
                <xsl:choose>
                    <xsl:when test="$current-filter = ''">
                        <xsl:copy-of select="$list-loop-filter//poguesFilter:Filter[poguesFilter:From/@id = $current-id]"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="current-filter-to-position" as="xs:double"
                            select="number($list-loop-filter//poguesFilter:Filter[@id = $current-filter]/poguesFilter:To/@position)"/>
                        <xsl:copy-of select="$list-loop-filter//poguesFilter:Filter[poguesFilter:From/@id = $current-id and number(poguesFilter:To/@position) &lt; $current-filter-to-position]"/>
                    </xsl:otherwise>
                </xsl:choose>
            </poguesFilter:FilterList>
        </xsl:variable>
        <xsl:variable name="next-filter-position">
            <xsl:choose>
                <xsl:when test="$possible-next-filters//poguesFilter:Filter">
                    <xsl:value-of select="max($possible-next-filters//poguesFilter:Filter/poguesFilter:To/number(@position))"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="''"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="chosen-next-filter" as="node()">
            <poguesFilter:FilterList>
                <xsl:if test="$next-filter-position != ''">
                    <xsl:copy-of select="$possible-next-filters//poguesFilter:Filter[poguesFilter:To/@position = string($next-filter-position)]"/>
                </xsl:if>
            </poguesFilter:FilterList>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="$chosen-next-filter//poguesFilter:Filter">
                <xsl:element name="IfThenElse" namespace="http://xml.insee.fr/schema/applis/pogues">
                    <xsl:attribute name="id" select="$chosen-next-filter//poguesFilter:Filter/@id"/>
                    <xsl:copy-of select="/pogues:Questionnaire//*[@id = $chosen-next-filter//poguesFilter:Filter/@id]/*[local-name()='Expression' or local-name()='Description']"/>
                    <xsl:element name="IfTrue" namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:apply-templates select="." mode="first-child-next-brother">
                            <xsl:with-param name="stop-position" select="$chosen-next-filter//poguesFilter:To/@id"/>
                            <xsl:with-param name="current-filter" select="$chosen-next-filter//poguesFilter:Filter/@id"/>
                        </xsl:apply-templates>
                    </xsl:element>
                </xsl:element>
                <xsl:if test="$stop-position != $chosen-next-filter//poguesFilter:To/@id">
                    <xsl:apply-templates select="following-sibling::pogues:Child[@id = $chosen-next-filter//poguesFilter:To/@id]/following-sibling::*[1]" mode="first-child-next-brother">
                        <xsl:with-param name="stop-position" select="$stop-position"/>
                        <xsl:with-param name="current-filter" select="''"/>
                    </xsl:apply-templates>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="descendant::pogues:Child">
                        <xsl:copy>
                            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
                            <xsl:apply-templates select="node()[1]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="''"/>
                                <xsl:with-param name="current-filter" select="''"/>
                            </xsl:apply-templates>
                        </xsl:copy>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="."/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$stop-position != $current-id">
                    <xsl:apply-templates select="following-sibling::*[1]" mode="first-child-next-brother">
                        <xsl:with-param name="stop-position" select="$stop-position"/>
                        <xsl:with-param name="current-filter" select="''"/>
                    </xsl:apply-templates>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
