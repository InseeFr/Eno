<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:poguesFilterLoop="http://xml.insee.fr/schema/applis/poguesFilterLoop"
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
        <poguesFilterLoop:IdList>
            <xsl:for-each select="//*[@id]">
                <xsl:sort select="position()"/>
                <xsl:if test="local-name(.)='Child' or local-name(.)='Questionnaire'">
                    <poguesFilterLoop:idElement id="{./@id}" position="{position()}">
                        <poguesFilterLoop:childrenId>
                            <xsl:for-each select="pogues:Child">
                                <poguesFilterLoop:childId>
                                    <xsl:value-of select="./@id"/>
                                </poguesFilterLoop:childId>
                            </xsl:for-each>
                        </poguesFilterLoop:childrenId>
                    </poguesFilterLoop:idElement>
                </xsl:if>
            </xsl:for-each>
        </poguesFilterLoop:IdList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>The same list where parents contain their descendants</xd:desc>
    </xd:doc>
    <xsl:variable name="child-tree">
        <poguesFilterLoop:IdList>
            <xsl:apply-templates select="$child-position-list/poguesFilterLoop:idElement[1]" mode="children-in-tree"/>
        </poguesFilterLoop:IdList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>The template necessary to browse child-position-list and create the variable $child-tree </xd:desc>
    </xd:doc>
    <xsl:template match="poguesFilterLoop:idElement" mode="children-in-tree">
        <xsl:copy>
            <xsl:copy-of select="@id"/>
            <xsl:copy-of select="@position"/>
            <xsl:apply-templates select="$child-position-list/poguesFilterLoop:idElement[@id=current()/poguesFilterLoop:childrenId/poguesFilterLoop:childId]" mode="children-in-tree"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>The list of filters directly from the source file</xd:desc>
    </xd:doc>
    <xsl:variable name="list-filter" as="node()">
        <poguesFilterLoop:FilterList>
            <xsl:for-each select="/pogues:Questionnaire/pogues:FlowControl | //pogues:Next">
                <xsl:variable name="from" select="substring-before(pogues:IfTrue,'-')"/>
                <xsl:variable name="to" select="substring-after(pogues:IfTrue,'-')"/>

                <poguesFilterLoop:FilterLoop id="{@id}" type="filter">
                    <poguesFilterLoop:From id="{$from}" position="{$child-tree//poguesFilterLoop:idElement[@id = $from]/@position}"/>
                    <poguesFilterLoop:To id="{$to}" position="{$child-tree//poguesFilterLoop:idElement[@id = $to]/@position}"/>
                </poguesFilterLoop:FilterLoop>
            </xsl:for-each>
        </poguesFilterLoop:FilterList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>The list of loops directly from the source file</xd:desc>
    </xd:doc>
    <xsl:variable name="list-loop" as="node()">
        <poguesFilterLoop:LoopList>
            <xsl:for-each select="/pogues:Questionnaire/pogues:Iterations/pogues:Iteration">
                <xsl:variable name="from-position" select="min($child-tree//poguesFilterLoop:idElement[@id = pogues:MemberReference]/number(@position))"/>
                <xsl:variable name="to-position" select="max($child-tree//poguesFilterLoop:idElement[@id = pogues:MemberReference]/number(@position))"/>
                
                <poguesFilterLoop:FilterLoop id="{@id}" type="loop">
                    <poguesFilterLoop:From id="{$child-tree//poguesFilterLoop:idElement[@position = string($from-position)]/@id}" position="{$from-position}"/>
                    <poguesFilterLoop:To id="{$child-tree//poguesFilterLoop:idElement[@position = string($to-position)]/@id}" position="{$to-position}"/>
                </poguesFilterLoop:FilterLoop>
            </xsl:for-each>
        </poguesFilterLoop:LoopList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>The list of filters and loops together</xd:desc>
    </xd:doc>
    <xsl:variable name="list-loop-filter" as="node()">
        <poguesFilterLoop:FilterLoopList>
            <xsl:copy-of select="$list-filter//FilterLoop"/>
            <xsl:copy-of select="$list-loop//FilterLoop"/>
        </poguesFilterLoop:FilterLoopList>
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
            <poguesFilterLoop:FilterLoopList>
                <xsl:choose>
                    <xsl:when test="$current-filter = ''">
                        <xsl:copy-of select="$list-loop-filter//poguesFilterLoop:FilterLoop[poguesFilterLoop:From/@id = $current-id]"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="current-filter-to-position" as="xs:double"
                            select="number($list-loop-filter//poguesFilterLoop:FilterLoop[@id = $current-filter]/poguesFilterLoop:To/@position)"/>
                        <xsl:copy-of select="$list-loop-filter//poguesFilterLoop:FilterLoop[poguesFilterLoop:From/@id = $current-id and number(poguesFilterLoop:To/@position) &lt; $current-filter-to-position]"/>
                    </xsl:otherwise>
                </xsl:choose>
            </poguesFilterLoop:FilterLoopList>
        </xsl:variable>
        <xsl:variable name="next-filter-position">
            <xsl:choose>
                <xsl:when test="$possible-next-filters//poguesFilterLoop:FilterLoop">
                    <xsl:value-of select="max($possible-next-filters//poguesFilterLoop:FilterLoop/poguesFilterLoop:To/number(@position))"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="''"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="chosen-next-filter" as="node()">
            <poguesFilterLoop:FilterLoopList>
                <xsl:if test="$next-filter-position != ''">
                    <xsl:copy-of select="$possible-next-filters//poguesFilterLoop:FilterLoop[poguesFilterLoop:To/@position = string($next-filter-position)]"/>
                </xsl:if>
            </poguesFilterLoop:FilterLoopList>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="$chosen-next-filter//poguesFilterLoop:FilterLoop">
                <xsl:element name="IfThenElse" namespace="http://xml.insee.fr/schema/applis/pogues">
                    <xsl:attribute name="id" select="$chosen-next-filter//poguesFilterLoop:FilterLoop/@id"/>
                    <xsl:copy-of select="/pogues:Questionnaire//*[@id = $chosen-next-filter//poguesFilterLoop:FilterLoop/@id]/*[local-name()='Expression' or local-name()='Description']"/>
                    <xsl:element name="IfTrue" namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:apply-templates select="." mode="first-child-next-brother">
                            <xsl:with-param name="stop-position" select="$chosen-next-filter//poguesFilterLoop:To/@id"/>
                            <xsl:with-param name="current-filter" select="$chosen-next-filter//poguesFilterLoop:FilterLoop/@id"/>
                        </xsl:apply-templates>
                    </xsl:element>
                </xsl:element>
                <xsl:if test="$stop-position != $chosen-next-filter//poguesFilterLoop:To/@id">
                    <xsl:choose>
                        <xsl:when test="$chosen-next-filter//poguesFilterLoop:To/@id = $current-id">
                            <xsl:apply-templates select="following-sibling::*[1]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="current-filter" select="''"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="following-sibling::pogues:Child[@id = $chosen-next-filter//poguesFilterLoop:To/@id]/following-sibling::*[1]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="current-filter" select="''"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
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
