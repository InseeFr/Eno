<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:poguesGoto="http://xml.insee.fr/schema/applis/poguesGoto"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="pogues poguesGoto xd" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="debug" select="false()"/>
    <!-- xsi:schemaLocation="Pogues.xsd"-->
    <!--xmlns:xs="http://www.w3.org/2001/XMLSchema"-->
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jun 15, 2017</xd:p>
            <xd:p><xd:b>Modified on:</xd:b> Feb 07, 2018</xd:p>
            <xd:p><xd:b>Authors:</xd:b>Antoine Dreyer + François Bulot</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>List of the Child (Sequence ; Question ; Loop ; IfThenElse ?), with position</xd:desc>
    </xd:doc>
    <xsl:variable name="child-position-list" as="node()">
        <poguesGoto:IdList>
            <xsl:for-each select="//*[@id]">
                <xsl:sort select="position()"/>
                <!-- TODO : IfThenElse : add IfThenElse with their Expression -->
                <xsl:if test="local-name(.)='Child'">
                    <poguesGoto:idElement>
                        <xsl:attribute name="id" select="./@id"/>
                        <xsl:attribute name="position" select="position()"/>
                        <poguesGoto:childrenId>
                            <xsl:for-each select="pogues:Child">
                                <poguesGoto:childId>
                                    <xsl:value-of select="./@id"/>
                                </poguesGoto:childId>
                            </xsl:for-each>
                        </poguesGoto:childrenId>
                        <xsl:if test="not(ancestor::pogues:Child)">
                            <poguesGoto:hasNoAncestor/>
                        </xsl:if>
                    </poguesGoto:idElement>
                </xsl:if>
            </xsl:for-each>
        </poguesGoto:IdList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>The same list where parents contain their descendants</xd:desc>
    </xd:doc>
    <xsl:variable name="child-tree">
        <poguesGoto:IdList>
            <xsl:apply-templates select="$child-position-list/poguesGoto:idElement[poguesGoto:hasNoAncestor]" mode="children-in-tree"/>
        </poguesGoto:IdList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>The only template necessary to create the variable $child-tree </xd:desc>
    </xd:doc>
    <xsl:template match="poguesGoto:idElement" mode="children-in-tree">
        <xsl:copy>
            <xsl:copy-of select="@id"/>
            <xsl:copy-of select="@position"/>
            <xsl:apply-templates select="$child-position-list/poguesGoto:idElement[@id=current()/poguesGoto:childrenId/poguesGoto:childId]" mode="children-in-tree"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>The list of Gotos</xd:desc>
    </xd:doc>

    <xsl:variable name="list_goto" as="node()">
        <poguesGoto:GotoList>
            <xsl:for-each select="//pogues:FlowControl">
                <poguesGoto:gotoValue start="after">
                    <poguesGoto:Expression>
                        <xsl:value-of select="pogues:Expression"/>
                    </poguesGoto:Expression>
                    <poguesGoto:From>
                        <xsl:attribute name="id" select="../@id"/>
                        <xsl:attribute name="position"
                            select="$child-tree//poguesGoto:idElement[@id = current()/parent::pogues:Child/@id]/@position"/>
                    </poguesGoto:From>
                    <poguesGoto:To>
                        <xsl:variable name="official-To" select="pogues:IfTrue"/>
                        <!-- TODO : IfThenElse : be sure the position is the good one -->
                        <xsl:attribute name="id" select="$child-tree//poguesGoto:idElement[@id = $official-To]
                            /ancestor-or-self::poguesGoto:idElement[preceding-sibling::poguesGoto:idElement][1]/@id"/>
                        <xsl:attribute name="position" select="$child-tree//poguesGoto:idElement[@id = $official-To]
                            /ancestor-or-self::poguesGoto:idElement[preceding-sibling::poguesGoto:idElement][1]/@position"/>
                    </poguesGoto:To>
                </poguesGoto:gotoValue>
            </xsl:for-each>
        </poguesGoto:GotoList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>list of the gotos :
            - without the Gotos goning backward (it would be loops)
            - with distinct From and To : the first one takes the conditions of its following-siblings
            So 2 Gotos going forward with the same From and the same To become 1 with 'or' between the 2 conditions
        </xd:desc>
    </xd:doc>
    <xsl:variable name="list_distinct_goto" as="node()">
        <poguesGoto:GotoList>
            <xsl:for-each select="$list_goto/poguesGoto:gotoValue">
                <xsl:if test="(poguesGoto:To/number(@position) - poguesGoto:From/number(@position) &gt; 0) and
                    not(preceding-sibling::poguesGoto:gotoValue[poguesGoto:From/@id = current()/poguesGoto:From/@id
                                                            and poguesGoto:To/@id = current()/poguesGoto:To/@id])">
                    <xsl:copy>
                        <xsl:copy-of select="@start"/>
                        <poguesGoto:Expression>
                            <xsl:choose>
                                <xsl:when test="following-sibling::poguesGoto:gotoValue[poguesGoto:From/@id = current()/poguesGoto:From/@id
                                                                                    and poguesGoto:To/@id = current()/poguesGoto:To/@id
                                                                                    and poguesGoto:Expression != current()/poguesGoto:Expression]">
                                    <xsl:value-of select="concat('((',poguesGoto:Expression,')')"/>
                                    <xsl:for-each
                                        select="following-sibling::poguesGoto:gotoValue[poguesGoto:From/@id = current()/poguesGoto:From/@id
                                                                                    and poguesGoto:To/@id = current()/poguesGoto:To/@id]">
                                        <xsl:if test="not(preceding-sibling::poguesGoto:gotoValue[poguesGoto:From/@id = current()/poguesGoto:From/@id
                                                                                              and poguesGoto:To/@id = current()/poguesGoto:To/@id
                                                                                              and poguesGoto:Expression = current()/poguesGoto:Expression])">
                                            <xsl:value-of select="concat(' or (',poguesGoto:Expression,')')"/>
                                        </xsl:if>
                                    </xsl:for-each>
                                    <xsl:value-of select="')'"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="poguesGoto:Expression"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </poguesGoto:Expression>
                        <xsl:copy-of select="poguesGoto:From"/>
                        <xsl:copy-of select="poguesGoto:To"/>
                    </xsl:copy>
                </xsl:if>
            </xsl:for-each>
        </poguesGoto:GotoList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>This variable deals with lapping gotos:
            When there are :
                Goto1 : Expression : condition1 ; From : 1 ; To : 4
                Goto2 : Expression : condition2 ; From : 2 ; To : 5
            They become 2 Gotos and a gotoLap :
            Goto1 : Expression : condition1 ; From : 1 ; To : 4
            Goto2 : Expression : condition2 ; From : 2 ; To : 4 (the same as the previous Goto)
            gotoLap : Expression : ((condition2) or not(condition1)) ; lap From : 4 ; To : 5

            When Goto1.To (4 here) is a child inside a sequence and Goto2.To (5 here) is out of the sequence
            then From is not the child, but the sequence (and new-Goto2.To is the same value), because Goto2 leaps over the whole sequence
        </xd:desc>
    </xd:doc>
    <xsl:variable name="list_nolap_goto" as="node()">
        <poguesGoto:GotoList>
            <xsl:for-each select="$list_distinct_goto//poguesGoto:gotoValue">
                <xsl:choose>
                    <xsl:when test="not($list_distinct_goto//poguesGoto:gotoValue[number(poguesGoto:From/@position) &lt; number(current()/poguesGoto:From/@position)
                                                                              and number(poguesGoto:To/@position) &gt; number(current()/poguesGoto:From/@position)
                                                                              and number(poguesGoto:To/@position) &lt; number(current()/poguesGoto:To/@position)])">
                        <xsl:copy-of select="."/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="initial-condition" select="poguesGoto:Expression"/>
                        <xsl:variable name="initial-from" select="poguesGoto:From"/>
                        <xsl:variable name="initial-to" select="poguesGoto:To"/>

                        <xsl:variable name="lapping-goto" as="node()">
                            <poguesGoto:gotoValues>
                                <xsl:for-each select="$list_distinct_goto//poguesGoto:gotoValue[number(poguesGoto:From/@position) &lt; number($initial-from/@position)
                                                                                            and number(poguesGoto:To/@position) &gt; number($initial-from/@position)
                                                                                            and number(poguesGoto:To/@position) &lt; number($initial-to/@position)]">
                                    <xsl:sort select="poguesGoto:To/@position" order="ascending"/>
                                    <xsl:copy-of select="."/>
                                </xsl:for-each>
                            </poguesGoto:gotoValues>
                        </xsl:variable>

                        <xsl:variable name="non-lapping-goto" as="node()">
                            <poguesGoto:gotoValues>
                                <xsl:for-each select="$lapping-goto/poguesGoto:gotoValue">
                                    <poguesGoto:gotoValue>
                                        <poguesGoto:Expression>
                                            <xsl:value-of select="concat('(',$initial-condition,') and not (')"/>
                                            <xsl:for-each select="preceding::poguesGoto:gotoValue">
                                                <xsl:value-of select="concat('(',poguesGoto:Expression,') or ')"/>
                                            </xsl:for-each>
                                            <xsl:value-of select="concat('(',poguesGoto:Expression,'))')"/>
                                        </poguesGoto:Expression>
                                        <poguesGoto:From>
                                            <xsl:attribute name="id" select="$child-tree//poguesGoto:idElement[@id = current()/poguesGoto:To/@id]
                                                /ancestor-or-self::poguesGoto:idElement[parent::*[descendant::poguesGoto:idElement/@id=$initial-from/@id
                                                                                               and descendant::poguesGoto:idElement/@id=$initial-to/@id]]
                                                                                       [1]/@id"/>
                                            <xsl:attribute name="position" select="$child-tree//poguesGoto:idElement[@id = current()/poguesGoto:To/@id]
                                                /ancestor-or-self::poguesGoto:idElement[parent::*[descendant::poguesGoto:idElement/@id=$initial-from/@id
                                                                                               and descendant::poguesGoto:idElement/@id=$initial-to/@id]]
                                                                                       [1]/@position"/>
                                        </poguesGoto:From>
                                        </poguesGoto:gotoValue>
                                </xsl:for-each>
                            </poguesGoto:gotoValues>
                        </xsl:variable>

                        <xsl:copy>
                            <xsl:copy-of select="@start"/>
                            <xsl:copy-of select="poguesGoto:Expression"/>
                            <xsl:copy-of select="poguesGoto:From"/>
                            <poguesGoto:To>
                                <xsl:attribute name="id" select="$non-lapping-goto//poguesGoto:gotoValue[1]/poguesGoto:From/@id"/>
                                <xsl:attribute name="position" select="$non-lapping-goto//poguesGoto:gotoValue[1]/poguesGoto:From/@position"/>
                            </poguesGoto:To>
                        </xsl:copy>
                        <!-- A tester : cibles : S2Q2 ; S2Q3 ; S3Q2 ; sources dans S1 : M23 le teste -->
                        <!-- En cas d'erreur : preceding au lieu de following -->
                        <xsl:for-each select="$non-lapping-goto//poguesGoto:gotoValue">
                            <!--<xsl:for-each select="$non-lapping-goto//poguesGoto:gotoValue[not(poguesGoto:From/@position = following-sibling::poguesGoto:gotoValue[1]/poguesGoto:From/@position)]">-->
                            <poguesGoto:gotoValue>
                                <xsl:attribute name="start" select="'before'"/>
                                <xsl:copy-of select="poguesGoto:Expression"/>
                                <xsl:copy-of select="poguesGoto:From"/>
                                <xsl:choose>
                                    <xsl:when test="position()=last()">
                                        <xsl:copy-of select="$initial-to"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <poguesGoto:To>
                                            <xsl:attribute name="id" select="following-sibling::poguesGoto:gotoValue[1]/poguesGoto:From/@id"/>
                                            <xsl:attribute name="position" select="following-sibling::poguesGoto:gotoValue[1]/poguesGoto:From/@position"/>
                                        </poguesGoto:To>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </poguesGoto:gotoValue>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </poguesGoto:GotoList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>Splits Gotos which go outside a sequence or inside a sequence
        Each split goto goes from an element to one of its following-sibling</xd:desc>
    </xd:doc>
    <xsl:variable name="split_goto">
        <poguesGoto:GotoList>
            <xsl:for-each select="$list_nolap_goto//poguesGoto:gotoValue">
                <xsl:variable name="condition" select="poguesGoto:Expression"/>
                <!-- TODO : Correct Expression when going outside an IfThenElse -->
                <xsl:variable name="initial-from" select="poguesGoto:From"/>
                <xsl:variable name="initial-to" select="poguesGoto:To"/>

                <xsl:choose>
                    <xsl:when test="$child-tree//poguesGoto:idElement[@id=$initial-from/@id]
                                                                     /following-sibling::poguesGoto:idElement[@id=$initial-to/@id]">
                        <xsl:copy-of select="."/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="start" select="@start"/>
                        <!-- goto going outside a sequence ; nothing to create if @start='before', because there is an ITE starting before an ancestor -->
                        <xsl:if test="$start='after'">
                            <xsl:for-each select="$child-tree//poguesGoto:idElement[descendant::poguesGoto:idElement/@id=$initial-from/@id
                                                                                and not(descendant::poguesGoto:idElement/@id=$initial-to/@id)]">
                                <xsl:variable name="from-child" select="child::poguesGoto:idElement[descendant-or-self::poguesGoto:idElement/@id=$initial-from/@id]"/>
                                <xsl:if test="poguesGoto:idElement[@id=$from-child/@id]/following-sibling::poguesGoto:idElement">
                                    <poguesGoto:gotoValue>
                                        <xsl:attribute name="start" select="'after'"/>
                                        <xsl:copy-of select="$condition"/>
                                        <poguesGoto:From>
                                            <xsl:attribute name="id" select="poguesGoto:idElement[@id=$from-child/@id]/@id"/>
                                            <xsl:attribute name="position" select="poguesGoto:idElement[@id=$from-child/@id]/@position"/>
                                        </poguesGoto:From>
                                        <poguesGoto:To id="last" position="last"/>
                                    </poguesGoto:gotoValue>
                                </xsl:if>
                            </xsl:for-each>                            
                        </xsl:if>
                        <!-- goto going outside a child or inside a following one -->
                        <xsl:for-each select="$child-tree//*[descendant::poguesGoto:idElement/@id=$initial-from/@id
                                                         and descendant::poguesGoto:idElement/@id=$initial-to/@id]">
                            <xsl:variable name="from-child" select="child::poguesGoto:idElement[descendant-or-self::poguesGoto:idElement/@id=$initial-from/@id]"/>
                            <xsl:variable name="to-child" select="child::poguesGoto:idElement[descendant-or-self::poguesGoto:idElement/@id=$initial-to/@id]"/>
                            <xsl:if test="$from-child/@id != $to-child/@id
                                and not($start='after' and poguesGoto:idElement[@id=$from-child/@id]/following-sibling::poguesGoto:idElement[1]/@id=$to-child/@id)">
                                <poguesGoto:gotoValue>
                                    <xsl:attribute name="start" select="$start"/>
                                    <xsl:copy-of select="$condition"/>
                                    <poguesGoto:From>
                                        <xsl:attribute name="id" select="poguesGoto:idElement[@id=$from-child/@id]/@id"/>
                                        <xsl:attribute name="position" select="poguesGoto:idElement[@id=$from-child/@id]/@position"/>
                                    </poguesGoto:From>
                                    <poguesGoto:To>
                                        <xsl:attribute name="id" select="poguesGoto:idElement[@id=$to-child/@id]/@id"/>
                                        <xsl:attribute name="position" select="poguesGoto:idElement[@id=$to-child/@id]/@position"/>
                                    </poguesGoto:To>
                                </poguesGoto:gotoValue>
                            </xsl:if>
                        </xsl:for-each>
                        <!-- goto going inside a sequence = going from its first child to the good one -->
                        <xsl:for-each select="$child-tree//poguesGoto:idElement[not(descendant::poguesGoto:idElement/@id=$initial-from/@id)
                                                                            and descendant::poguesGoto:idElement/@id=$initial-to/@id]">
                            <xsl:variable name="from-child" select="poguesGoto:idElement[1]"/>
                            <xsl:variable name="to-child" select="child::poguesGoto:idElement[descendant-or-self::poguesGoto:idElement/@id=$initial-to/@id]"/>
                            <xsl:if test="$child-tree//poguesGoto:idElement[@id=$to-child/@id]/preceding-sibling::poguesGoto:idElement">
                                <poguesGoto:gotoValue>
                                    <xsl:attribute name="start" select="'before'"/>
                                    <xsl:copy-of select="$condition"/>
                                    <poguesGoto:From>
                                        <xsl:attribute name="id" select="$from-child/@id"/>
                                        <xsl:attribute name="position" select="$from-child/@position"/>
                                    </poguesGoto:From>
                                    <poguesGoto:To>
                                        <xsl:attribute name="id" select="$to-child/@id"/>
                                        <xsl:attribute name="position" select="$to-child/@position"/>
                                    </poguesGoto:To>
                                </poguesGoto:gotoValue>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </poguesGoto:GotoList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>the root element</xd:desc>
    </xd:doc>
    <xsl:template match="/pogues:Questionnaire" priority="1">
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:if test="$debug">
                <xsl:copy-of select="$child-tree"/>
                <xsl:copy-of select="$list_goto"/>
                <xsl:copy-of select="$list_distinct_goto"/>
                <xsl:copy-of select="$list_nolap_goto"/>
                <xsl:copy-of select="$split_goto"/>
            </xsl:if>
            <xsl:apply-templates select="node()[1]" mode="first-child-next-brother">
                <xsl:with-param name="goto-style" select="'none'"/>
                <xsl:with-param name="stop-position" select="'end'"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>every tag not containing Child can be directly copied and calls its first following-sibling</xd:desc>
    </xd:doc>
    <xsl:template match="*[not(descendant::pogues:Child)]" mode="first-child-next-brother">
        <xsl:param name="goto-style"/>
        <xsl:param name="stop-position"/>

        <xsl:if test="$stop-position != 'last' or following-sibling::pogues:Child or following-sibling::*[descendant::pogues:Child]">
            <xsl:copy-of select="."/>
            <xsl:apply-templates select="following-sibling::node()[1]" mode="first-child-next-brother">
                <xsl:with-param name="goto-style" select="$goto-style"/>
                <xsl:with-param name="stop-position" select="$stop-position"/>
            </xsl:apply-templates>            
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>Tags containing Child call their first child and their first following-sibling</xd:desc>
    </xd:doc>
    <xsl:template match="*[descendant::pogues:Child]" mode="first-child-next-brother">
        <xsl:param name="goto-style"/>
        <xsl:param name="stop-position"/>

        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="child::node()[1]" mode="first-child-next-brother">
                <xsl:with-param name="goto-style" select="$goto-style"/>
                <xsl:with-param name="stop-position" select="$stop-position"/>
            </xsl:apply-templates>
        </xsl:copy>
        <xsl:apply-templates select="following-sibling::node()[1]" mode="first-child-next-brother">
            <xsl:with-param name="goto-style" select="$goto-style"/>
            <xsl:with-param name="stop-position" select="$stop-position"/>
        </xsl:apply-templates>
    </xsl:template>

    <xd:doc>
        Child : Sequence or Question
        parameters : - stop-position : first Child not to take
                     - goto-style : none, before, after : says which kind of goto has started to be done
        <xd:desc/>
    </xd:doc>
    <!-- TODO : pogues:Child | pogues:IfThenElse -->
    <xsl:template match="pogues:Child" priority="1" mode="first-child-next-brother">
        <xsl:param name="goto-style"/>
        <xsl:param name="stop-position"/>
        
        <xsl:variable name="current-id" select="@id"/>
        <xsl:variable name="current-position" select="$child-tree//poguesGoto:idElement[@id=$current-id]/@position"/>
        <xsl:variable name="next-sibling-position" select="$child-tree//poguesGoto:idElement[@id=$current-id]/following-sibling::poguesGoto:idElement[1]/@position"/>

        <xsl:variable name="current-goto-list">
            <xsl:copy-of select="$split_goto//poguesGoto:gotoValue[poguesGoto:From/@id = $current-id]"/>
        </xsl:variable>
        
        <xsl:variable name="choosen-goto">
            <poguesGoto:gotoValue>
                <xsl:choose>
                    <!-- before : not if To/@position = $current-position -->
                    <xsl:when test="$goto-style='none' and $current-goto-list//poguesGoto:gotoValue[@start='before' and poguesGoto:To/@position != $current-position]">
                        <xsl:attribute name="start" select="'before'"/>
                        <poguesGoto:To>
                            <xsl:attribute name="position" select="max($current-goto-list//poguesGoto:gotoValue[@start='before']/poguesGoto:To/number(@position))"/>    
                        </poguesGoto:To>
                    </xsl:when>
                    <xsl:when test="$goto-style='before' and $current-goto-list//poguesGoto:gotoValue[@start='before']/poguesGoto:To[@position != $current-position
                                                                                                                                 and number(@position) &lt; $stop-position]">
                        <xsl:attribute name="start" select="'before'"/>
                        <poguesGoto:To>
                            <xsl:attribute name="position"
                                select="max($current-goto-list//poguesGoto:gotoValue[@start='before']/poguesGoto:To[number(@position) &lt; $stop-position]/number(@position))"/>    
                        </poguesGoto:To>
                    </xsl:when>
                    <!-- after : not if To/@position = $next-sibling-position -->
                    <xsl:when test="$goto-style != 'after' and $current-goto-list//poguesGoto:gotoValue[@start='after' and poguesGoto:To/@id='last']">
                        <xsl:attribute name="start" select="'after'"/>
                        <poguesGoto:To position="last"/>
                    </xsl:when>
                    <xsl:when test="$goto-style != 'after' and $current-goto-list//poguesGoto:gotoValue[@start='after' and poguesGoto:To/@position != $next-sibling-position]">
                        <xsl:attribute name="start" select="'after'"/>
                        <poguesGoto:To>
                            <xsl:attribute name="position" select="max($current-goto-list//poguesGoto:gotoValue[@start='after']/poguesGoto:To/number(@position))"/>
                        </poguesGoto:To>
                    </xsl:when>
                    <xsl:when test="$goto-style='after' and $stop-position='last'
                        and $current-goto-list//poguesGoto:gotoValue[@start='after']/poguesGoto:To[@position!='last' and @position != $next-sibling-position]">
                        <xsl:attribute name="start" select="'after'"/>
                        <poguesGoto:To>
                            <xsl:attribute name="position" select="max($current-goto-list//poguesGoto:gotoValue[@start='after']/poguesGoto:To[@position!='last']/number(@position))"/>
                        </poguesGoto:To>
                    </xsl:when>
                    <xsl:when test="$goto-style='after' and $stop-position!='last'
                        and $current-goto-list//poguesGoto:gotoValue[@start='after']/poguesGoto:To[@position!='last' and @position != $next-sibling-position
                                                                                               and number(@position) &lt; $stop-position]">
                        <xsl:attribute name="start" select="'after'"/>
                        <poguesGoto:To>
                            <xsl:attribute name="position"
                                select="max($current-goto-list//poguesGoto:gotoValue[@start='after']/poguesGoto:To[@position!='last' and number(@position) &lt; $stop-position]/number(@position))"/>
                        </poguesGoto:To>
                    </xsl:when>
                    <!-- none -->
                    <xsl:otherwise>
                        <xsl:attribute name="start" select="'none'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </poguesGoto:gotoValue>
        </xsl:variable>

        <xsl:variable name="choosen-goto-to-id" select="$child-tree//poguesGoto:idElement[@position=$choosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position]/@id"/>

        <xsl:variable name="choosen-goto-condition">
            <xsl:if test="$choosen-goto/poguesGoto:gotoValue/@start != 'none'">
                <xsl:choose>
                    <xsl:when test="count($current-goto-list//poguesGoto:gotoValue[poguesGoto:From/@id=$current-id
                                                                               and @start=$choosen-goto/poguesGoto:gotoValue/@start
                                                                               and poguesGoto:To/@position=$choosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position])
                                          = 1">
                        <xsl:value-of select="$current-goto-list//poguesGoto:gotoValue[poguesGoto:From/@id=$current-id
                                                                                   and @start=$choosen-goto/poguesGoto:gotoValue/@start
                                                                                   and poguesGoto:To/@position=$choosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position]
                                                                                      /poguesGoto:Expression"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- The expression is the combination of at least 2 expressions-->
                        <xsl:for-each select="$current-goto-list//poguesGoto:gotoValue[poguesGoto:From/@id=$current-id
                                                                                   and @start=$choosen-goto/poguesGoto:gotoValue/@start
                                                                                   and poguesGoto:To/@position=$choosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position]">
                            <xsl:choose>
                                <xsl:when test="position()=1">
                                    <xsl:value-of select="concat('(',poguesGoto:Expression,')')"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:if test="not(preceding-sibling::poguesGoto:gotoValue[poguesGoto:Expression = current()/poguesGoto:Expression])">
                                        <xsl:value-of select="concat(' and (',poguesGoto:Expression,')')"/>    
                                    </xsl:if>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:variable>

        <xsl:if test="$stop-position = 'end' or $stop-position = 'last' or number($current-position) &lt; number($stop-position)">
            <xsl:choose>
                <xsl:when test="$choosen-goto/poguesGoto:gotoValue/@start='before'">
                    <pogues:IfThenElse id="{generate-id()}">
                        <pogues:Expression>
                            <xsl:value-of select="concat('not(',$choosen-goto-condition,')')"/>
                        </pogues:Expression>
                        <pogues:IfTrue>
                            <xsl:apply-templates select="." mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$choosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position"/>
                                <xsl:with-param name="goto-style" select="'before'"/>
                            </xsl:apply-templates>
                        </pogues:IfTrue>
                    </pogues:IfThenElse>
                    <xsl:choose>
                        <xsl:when test="$choosen-goto/poguesGoto:To/@position = 'last'">
                            <xsl:apply-templates select="following-sibling::*[not(name()=pogues:Child) and not(following-sibling::pogues:Child)][1]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="goto-style" select="'none'"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="following-sibling::pogues:Child[@id=$choosen-goto-to-id]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="goto-style" select="'none'"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <!-- what to write into the Child -->
                    <xsl:if test="$goto-style != 'after'">
                        <xsl:choose>
                            <xsl:when test="descendant::pogues:Child">
                                <xsl:copy>
                                    <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
                                    <xsl:apply-templates select="node()[1]" mode="first-child-next-brother">
                                        <xsl:with-param name="goto-style" select="'none'"/>
                                        <xsl:with-param name="stop-position" select="'end'"/>
                                    </xsl:apply-templates>
                                </xsl:copy>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:copy-of select="."/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                    <!-- what to write after the Child -->
                    <xsl:choose>
                        <xsl:when test="$choosen-goto/poguesGoto:gotoValue/@start='none'">
                            <xsl:apply-templates select="following-sibling::node()[1]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="goto-style" select="'none'"/>                                
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <pogues:IfThenElse id="{generate-id()}">
                                <pogues:Expression>
                                    <xsl:value-of select="concat('not(',$choosen-goto-condition,')')"/>
                                </pogues:Expression>
                                <pogues:IfTrue>
                                    <xsl:apply-templates select="." mode="first-child-next-brother">
                                        <xsl:with-param name="stop-position" select="$choosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position"/>
                                        <xsl:with-param name="goto-style" select="'after'"/>
                                    </xsl:apply-templates>
                                </pogues:IfTrue>
                            </pogues:IfThenElse>
                            <xsl:choose>
                                <xsl:when test="$choosen-goto/poguesGoto:To/@position = 'last'">
                                    <xsl:apply-templates select="following-sibling::*[not(name()=pogues:Child) and not(following-sibling::pogues:Child)][1]" mode="first-child-next-brother">
                                        <xsl:with-param name="stop-position" select="$stop-position"/>
                                        <xsl:with-param name="goto-style" select="'none'"/>
                                    </xsl:apply-templates>                                    
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:apply-templates select="following-sibling::pogues:Child[@id=$choosen-goto-to-id]" mode="first-child-next-brother">
                                        <xsl:with-param name="stop-position" select="$stop-position"/>
                                        <xsl:with-param name="goto-style" select="'none'"/>
                                    </xsl:apply-templates>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>

    </xsl:template>

</xsl:stylesheet>