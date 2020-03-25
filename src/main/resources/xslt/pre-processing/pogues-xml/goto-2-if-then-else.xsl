<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:poguesGoto="http://xml.insee.fr/schema/applis/poguesGoto"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="xs" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="debug" select="false()"/>
    <!-- xsi:schemaLocation="Pogues.xsd"-->
    <!--xmlns:xs="http://www.w3.org/2001/XMLSchema"-->
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jun 15, 2017</xd:p>
            <xd:p><xd:b>Modified on:</xd:b> Feb, 2018</xd:p>
            <xd:p><xd:b>Authors:</xd:b>Antoine Dreyer + François Bulot</xd:p>
            <xd:p>This program works with 2 steps :</xd:p>
            <xd:p>- the first step creates this list ($split_goto) of the Gotos to insert</xd:p>
            <xd:p>- the second one inserts the IfThenElse in the existing questionnaire</xd:p>
            <xd:p/>
            <xd:p>The idea is that before inserting an ITE, one can define these 4 siblings :</xd:p>
            <xd:p>(1) the last sibling before the ITE</xd:p>
            <xd:p>(2) the first sibling inside the ITE</xd:p>
            <xd:p>(3) the last sibling inside the ITE</xd:p>
            <xd:p>(4) the first sibling after the ITE</xd:p>
            <xd:p>Theses sibling follow these rules :</xd:p>
            <xd:p>(2) and (3) must exist, but can be the same child</xd:p>
            <xd:p>(1) is (2)'s first preceding sibling ; (4) is (3)'s first following sibling</xd:p>
            <xd:p>(1) or (4) must exist, otherwise the parent is filtered</xd:p>
            <xd:p/>
            <xd:p>What defines the ITE to insert :</xd:p>
            <xd:p>- its condition is the opposite of the Goto's</xd:p>
            <xd:p>- it starts after (1) or before (2)</xd:p>
            <xd:p>- it ends before (4) or after (3) when (3) is the last child</xd:p>
            <xd:p/>
            <xd:p>The list defined by the first step contains Goto with these characteristics :</xd:p>
            <xd:p>- an Expression / condition</xd:p>
            <xd:p>- 2 elements : "From" and "To" that designate siblings (real id or 'last' for the sibling "To")</xd:p>
            <xd:p>- an attribute "start" that worth 'before' or 'after'</xd:p>
            <xd:p/>
            <xd:p>The second step uses a special browsing :</xd:p>
            <xd:p>By default, each element calls its first child and its first following sibling</xd:p>
            <xd:p>When an ITE is inserted, another following sibling (4) is called as ITE's first following sibling</xd:p>
            <xd:p>and, inside the ITE, the elements are called with a stop-position = (4)</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>List of the Child (Sequence ; Question ; Loop), with position</xd:desc>
    </xd:doc>
    <xsl:variable name="child-position-list" as="node()">
        <poguesGoto:IdList>
            <xsl:for-each select="//*[@id]">
                <xsl:sort select="position()"/>
                <!-- TODO : IfThenElse : add IfThenElse with their Expression -->
                <xsl:if test="local-name(.)='Child' or local-name(.)='Questionnaire'">
                    <poguesGoto:idElement id="{./@id}" position="{position()}">
                        <poguesGoto:childrenId>
                            <xsl:for-each select="pogues:Child">
                                <poguesGoto:childId>
                                    <xsl:value-of select="./@id"/>
                                </poguesGoto:childId>
                            </xsl:for-each>
                        </poguesGoto:childrenId>
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
            <xsl:apply-templates select="$child-position-list/poguesGoto:idElement[1]" mode="children-in-tree"/>
        </poguesGoto:IdList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>The template necessary to browse child-position-list and create the variable $child-tree </xd:desc>
    </xd:doc>
    <xsl:template match="poguesGoto:idElement" mode="children-in-tree">
        <xsl:copy>
            <xsl:copy-of select="@id"/>
            <xsl:copy-of select="@position"/>
            <xsl:apply-templates select="$child-position-list/poguesGoto:idElement[@id=current()/poguesGoto:childrenId/poguesGoto:childId]" mode="children-in-tree"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>A value to identify the last element of any Child ; not used and greater than any existing id</xd:desc>
    </xd:doc>
    <xsl:variable name="last" select="count(//*[@id])+1"/>
    <xd:doc>
        <xd:desc>A value to identify the end of the questionnaire ; not used and greater than any existing id</xd:desc>
    </xd:doc>
    <xsl:variable name="end" select="count(//*[@id])+2"/>
    
    <xd:doc>
        <xd:desc>The list of Gotos directly from the source file</xd:desc>
        <xd:desc>Goto's targets that are first child are replace by their parent (or first ancestor that has a preeding-sibling</xd:desc>
    </xd:doc>
    <xsl:variable name="list_goto" as="node()">
        <poguesGoto:GotoList>
            <xsl:for-each select="//pogues:FlowControl[not(@flowControlType)]">
                <xsl:variable name="official-To" select="pogues:IfTrue"/>
                
                <poguesGoto:gotoValue start="after" flowid="{@id}">
                    <poguesGoto:Expression>
                        <xsl:value-of select="pogues:Expression"/>
                    </poguesGoto:Expression>
                    <poguesGoto:From id="{../@id}"
                        position="{$child-tree//poguesGoto:idElement[@id = current()/parent::pogues:Child/@id]/@position}"/>
                    <!-- TODO : IfThenElse : be sure the position is still the good one -->
                    <poguesGoto:To id="{$child-tree//poguesGoto:idElement[@id = $official-To]
                                                                         /ancestor-or-self::poguesGoto:idElement[preceding-sibling::poguesGoto:idElement][1]/@id}"
                             position="{$child-tree//poguesGoto:idElement[@id = $official-To]
                                                                         /ancestor-or-self::poguesGoto:idElement[preceding-sibling::poguesGoto:idElement][1]/@position}"/>
                </poguesGoto:gotoValue>
            </xsl:for-each>
        </poguesGoto:GotoList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>list of the gotos :
            - without the Gotos goning backward (it would be loops)
            - with distinct From and To : the first one takes the conditions of its following-siblings
            So 2 Gotos going forward with the same From and the same To become 1 with 'or' between the 2 Expressions
            If 2 Gotos have the same From, To and Expression, the 2nd one is removed
        </xd:desc>
    </xd:doc>
    <xsl:variable name="list_distinct_goto" as="node()">
        <poguesGoto:GotoList>
            <xsl:for-each select="$list_goto/poguesGoto:gotoValue">
                <!-- A Goto is removed when it doesn't go forward -->
                <!-- Gotos with same From and To are merged into the first one -->
                <xsl:if test="(poguesGoto:To/number(@position) - poguesGoto:From/number(@position) &gt; 0)
                           and not(preceding-sibling::poguesGoto:gotoValue[poguesGoto:From/@id = current()/poguesGoto:From/@id
                                                                       and poguesGoto:To/@id = current()/poguesGoto:To/@id])">
                    <xsl:copy>
                        <xsl:copy-of select="@start"/>
                        <xsl:attribute name="flowid">
                            <xsl:value-of select="@flowid"/>
                            <xsl:for-each select="following-sibling::poguesGoto:gotoValue[poguesGoto:From/@id = current()/poguesGoto:From/@id
                                                                                      and poguesGoto:To/@id = current()/poguesGoto:To/@id]">
                                <xsl:value-of select="@flowid"/>
                            </xsl:for-each>
                        </xsl:attribute>
                        <poguesGoto:Expression>
                            <xsl:choose>
                                <xsl:when test="not(following-sibling::poguesGoto:gotoValue[poguesGoto:From/@id = current()/poguesGoto:From/@id
                                                                                        and poguesGoto:To/@id = current()/poguesGoto:To/@id
                                                                                        and poguesGoto:Expression != current()/poguesGoto:Expression])">
                                    <xsl:value-of select="poguesGoto:Expression"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <!-- when merge is needed : concatenation of the Expressions with 'or' between them -->
                                    <xsl:value-of select="concat('((',poguesGoto:Expression,')')"/>
                                    <xsl:for-each select="following-sibling::poguesGoto:gotoValue[poguesGoto:From/@id = current()/poguesGoto:From/@id
                                                                                              and poguesGoto:To/@id = current()/poguesGoto:To/@id]">
                                        <xsl:if test="not(preceding-sibling::poguesGoto:gotoValue[poguesGoto:From/@id = current()/poguesGoto:From/@id
                                                                                              and poguesGoto:To/@id = current()/poguesGoto:To/@id
                                                                                              and poguesGoto:Expression = current()/poguesGoto:Expression])">
                                            <xsl:value-of select="concat(' or (',poguesGoto:Expression,')')"/>
                                        </xsl:if>
                                    </xsl:for-each>
                                    <xsl:value-of select="')'"/>
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
                Goto1 : Expression : condition1 ; From : 1 ; To : 6
                Goto2 : Expression : condition2 ; From : 2 ; To : 9
            They become 2 Gotos and a gotoLap :
            Goto1 : Expression : condition1 ; From : 1 ; To : 6
            Goto2 : Expression : condition2 ; From : 2 ; To : 6 (the same as the previous Goto)
            gotoLap : Expression : ((condition2) or not(condition1)) ; lap From : 6 ; To : 9
        </xd:desc>
        <xd:desc>
            When Goto1.To (6 here) is a child inside a sequence (sequence 4 includes questions 5 to 7) and Goto2.From and Goto2.To (2 and 9 here) are out of the sequence
            then the gotoLap.From is not the child (6), but the sequence (4), because Goto2 leaps over the whole sequence.
            new-Goto2.To is the same value (4 instead of 6)
            Goto1 : Expression : condition1 ; From : 1 ; To : 6
            Goto2 : Expression : condition2 ; From : 2 ; To : 6 (no longer the same as the previous Goto : the split point has changed)
            gotoLap : Expression : ((condition2) or not(condition1)) ; lap From : 4 ; To : 9            
        </xd:desc>
    </xd:doc>
    <xsl:variable name="list_no_overlap_goto" as="node()">
        <poguesGoto:GotoList>
            <xsl:for-each select="$list_distinct_goto//poguesGoto:gotoValue">
                <xsl:choose>
                    <!-- gotoValue not overlapped in list_distinct_goto -->
                    <xsl:when test="not($list_distinct_goto//poguesGoto:gotoValue[number(poguesGoto:From/@position) &lt; number(current()/poguesGoto:From/@position)
                                                                              and number(poguesGoto:To/@position) &gt; number(current()/poguesGoto:From/@position)
                                                                              and number(poguesGoto:To/@position) &lt; number(current()/poguesGoto:To/@position)])">
                        <xsl:copy-of select="."/>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- Goto2 of the example -->
                        <xsl:variable name="initial-flowid" select="@flowid"/>
                        <xsl:variable name="initial-condition" select="poguesGoto:Expression"/>
                        <xsl:variable name="initial-from" select="poguesGoto:From"/>
                        <xsl:variable name="initial-to" select="poguesGoto:To"/>

                        <!-- List of the Gotos that overlap the main one : start before and end during this Goto : Goto1 of the example -->
                        <xsl:variable name="overlapping-goto" as="node()">
                            <poguesGoto:gotoValues>
                                <xsl:for-each select="$list_distinct_goto//poguesGoto:gotoValue[number(poguesGoto:From/@position) &lt; number($initial-from/@position)
                                                                                            and number(poguesGoto:To/@position) &gt; number($initial-from/@position)
                                                                                            and number(poguesGoto:To/@position) &lt; number($initial-to/@position)]">
                                    <xsl:sort select="number(poguesGoto:To/@position)" order="ascending"/>
                                    <xsl:copy-of select="."/>
                                </xsl:for-each>
                            </poguesGoto:gotoValues>
                        </xsl:variable>

                        <!-- List of the points where to split Goto2 and Expression after each of them -->
                        <xsl:variable name="non-overlapping-goto" as="node()">
                            <poguesGoto:gotoValues>
                                <xsl:for-each select="$overlapping-goto/poguesGoto:gotoValue">
                                    <poguesGoto:gotoValue flowid="{@flowid}">
                                        <!-- New expression : Goto2's and not one of the Goto1's -->
                                        <poguesGoto:Expression>
                                            <xsl:value-of select="concat('(',$initial-condition,') and not (')"/>
                                            <xsl:for-each select="preceding::poguesGoto:gotoValue">
                                                <xsl:value-of select="concat('(',poguesGoto:Expression,') or ')"/>
                                            </xsl:for-each>
                                            <xsl:value-of select="concat('(',poguesGoto:Expression,'))')"/>
                                        </poguesGoto:Expression>
                                        <!-- When a Goto stops inside a Child that is overlapped by another, the overlap must include the Child's label -->
                                        <!-- The point where to start the split is replaced by 
                                             the first ancestor of Goto1.To that doesn't include nor $initial-to nor $initial-from
                                                                        and that parent's Child includes $initial-to or $initial-from -->
                                        <poguesGoto:From id="{$child-tree//poguesGoto:idElement[@id = current()/poguesGoto:To/@id]
                                                                                               /ancestor-or-self::poguesGoto:idElement[parent::*[descendant::poguesGoto:idElement/@id=$initial-from/@id
                                                                                                                                              or descendant::poguesGoto:idElement/@id=$initial-to/@id]]
                                                                                                                                      [1]/@id}"
                                                   position="{$child-tree//poguesGoto:idElement[@id = current()/poguesGoto:To/@id]
                                                                                               /ancestor-or-self::poguesGoto:idElement[parent::*[descendant::poguesGoto:idElement/@id=$initial-from/@id
                                                                                                                                              or descendant::poguesGoto:idElement/@id=$initial-to/@id]]
                                                                                                                                      [1]/@position}"/>
                                    </poguesGoto:gotoValue>
                                </xsl:for-each>
                            </poguesGoto:gotoValues>
                        </xsl:variable>

                        <xsl:copy>
                            <xsl:copy-of select="@start"/>
                            <xsl:attribute name="flowid" select="$initial-flowid"/>
                            <xsl:copy-of select="poguesGoto:Expression"/>
                            <xsl:copy-of select="poguesGoto:From"/>
                            <!-- The original Goto stops at the first split point -->
                            <poguesGoto:To id="{$non-overlapping-goto//poguesGoto:gotoValue[1]/poguesGoto:From/@id}"
                                     position="{$non-overlapping-goto//poguesGoto:gotoValue[1]/poguesGoto:From/@position}"/>
                        </xsl:copy>
                        <!-- A Goto starts 'before' each split point -->
                        <xsl:for-each select="$non-overlapping-goto//poguesGoto:gotoValue">
                            <poguesGoto:gotoValue start="before" flowid="{$initial-flowid}-{@flowid}">
                                <xsl:copy-of select="poguesGoto:Expression"/>
                                <xsl:copy-of select="poguesGoto:From"/>
                                <xsl:choose>
                                    <xsl:when test="position()=last()">
                                        <xsl:copy-of select="$initial-to"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <poguesGoto:To id="{following-sibling::poguesGoto:gotoValue[1]/poguesGoto:From/@id}"
                                                 position="{following-sibling::poguesGoto:gotoValue[1]/poguesGoto:From/@position}"/>
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
        <xd:desc>TODO : go outside an existing IfThenElse ; forbid go inside an existing IfThenElse (nonsense)</xd:desc>
    </xd:doc>
    <xsl:variable name="split_goto">
        <poguesGoto:GotoList>
            <xsl:for-each select="$list_no_overlap_goto//poguesGoto:gotoValue">
                <xsl:variable name="initial-from" select="poguesGoto:From"/>
                <xsl:variable name="initial-to" select="poguesGoto:To"/>
                <xsl:variable name="initial-start" select="@start"/>
                <xsl:variable name="initial-condition" select="poguesGoto:Expression"/>
                <xsl:variable name="initial-flowid" select="@flowid"/>

                <xsl:for-each select="$child-tree//poguesGoto:idElement">
                    <xsl:choose>
                        <xsl:when test="$initial-start='before' and child::poguesGoto:idElement/@id = $initial-from/@id and descendant::poguesGoto:idElement/@id = $initial-to/@id">
                            <xsl:variable name="to-child" select="child::poguesGoto:idElement[descendant-or-self::poguesGoto:idElement/@id = $initial-to/@id]"/>
                            <xsl:if test="$initial-from/@id != $to-child/@id">
                                <poguesGoto:gotoValue start="before" flowid="{$initial-flowid}">
                                    <xsl:copy-of select="$initial-condition"/>
                                    <poguesGoto:From id="{$initial-from/@id}" position="{$initial-from/@position}"/>
                                    <poguesGoto:To id="{$to-child/@id}" position="{$to-child/@position}"/>
                                </poguesGoto:gotoValue>
                            </xsl:if>
                        </xsl:when>
                        <xsl:when test="descendant::poguesGoto:idElement/@id = $initial-from/@id and descendant::poguesGoto:idElement/@id = $initial-to/@id">
                            <xsl:variable name="from-child" select="child::poguesGoto:idElement[descendant-or-self::poguesGoto:idElement/@id = $initial-from/@id]"/>
                            <xsl:variable name="to-child" select="child::poguesGoto:idElement[descendant-or-self::poguesGoto:idElement/@id = $initial-to/@id]"/>
                            <xsl:if test="$from-child/@id != $to-child/@id
                                      and child::poguesGoto:idElement[@id = $from-child/@id]/following-sibling::poguesGoto:idElement[1]/@id != $to-child/@id">
                                <!-- there is at least a Child between from-child and to-child -->
                                <poguesGoto:gotoValue start="after" flowid="{$initial-flowid}">
                                    <!-- if $from-child != $initial-from, the split goto starts after $from-child, even if $initial-start = 'before' -->
                                    <xsl:copy-of select="$initial-condition"/>
                                    <poguesGoto:From id="{$from-child/@id}" position="{$from-child/@position}"/>
                                    <poguesGoto:To id="{$to-child/@id}" position="{$to-child/@position}"/>
                                </poguesGoto:gotoValue>
                            </xsl:if>
                        </xsl:when>
                        <xsl:when test="$initial-start='before' and child::poguesGoto:idElement/@id = $initial-from/@id and not(descendant::poguesGoto:idElement/@id = $initial-to/@id)">
                            <poguesGoto:gotoValue start="before" flowid="{$initial-flowid}">
                                <xsl:copy-of select="$initial-condition"/>
                                <poguesGoto:From id="{$initial-from/@id}" position="{$initial-from/@position}"/>
                                <poguesGoto:To id="last" position="{$last}"/>
                            </poguesGoto:gotoValue>
                        </xsl:when>
                        <xsl:when test="descendant::poguesGoto:idElement/@id = $initial-from/@id and not(descendant::poguesGoto:idElement/@id = $initial-to/@id)">
                            <xsl:variable name="from-child" select="child::poguesGoto:idElement[descendant-or-self::poguesGoto:idElement/@id = $initial-from/@id]"/>
                            <xsl:if test="child::poguesGoto:idElement[@id = $from-child/@id]/following-sibling::poguesGoto:idElement">
                                <!-- $from-child is not the last Child -->
                                <poguesGoto:gotoValue start="after" flowid="{$initial-flowid}">
                                    <!-- if $from-child != $initial-from, the split goto starts after $from-child, even if $initial-start = 'before' -->
                                    <xsl:copy-of select="$initial-condition"/>
                                    <poguesGoto:From id="{$from-child/@id}" position="{$from-child/@position}"/>
                                    <poguesGoto:To id="last" position="{$last}"/>
                                </poguesGoto:gotoValue>
                            </xsl:if>
                        </xsl:when>
                        <xsl:when test="not(descendant::poguesGoto:idElement/@id = $initial-from/@id) and descendant::poguesGoto:idElement/@id = $initial-to/@id">
                            <xsl:variable name="first-child" select="child::poguesGoto:idElement[1]"/>
                            <xsl:variable name="to-child" select="child::poguesGoto:idElement[descendant-or-self::poguesGoto:idElement/@id = $initial-to/@id]"/>
                            <xsl:if test="$first-child/@id != $to-child/@id">
                                <!-- $to-child is not the first Child -->
                                <poguesGoto:gotoValue start="before" flowid="{$initial-flowid}">
                                    <xsl:copy-of select="$initial-condition"/>
                                    <poguesGoto:From id="{$first-child/@id}" position="{$first-child/@position}"/>
                                    <poguesGoto:To id="{$to-child/@id}" position="{$to-child/@position}"/>
                                </poguesGoto:gotoValue>
                            </xsl:if>
                        </xsl:when>
                        <!-- not(descendant::poguesGoto:idElement/@id = $initial-from/@id) and not(descendant::poguesGoto:idElement/@id = $initial-to/@id) -->
                        <xsl:otherwise/>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:for-each>
        </poguesGoto:GotoList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>the root element</xd:desc>
    </xd:doc>
    <xsl:template match="/pogues:Questionnaire" priority="1">
        <xsl:copy>
            <xsl:copy-of select="@* | text() | processing-instruction()"/>
            <xsl:if test="$debug">
                <xsl:copy-of select="$child-tree"/>
                <xsl:copy-of select="$list_goto"/>
                <xsl:copy-of select="$list_distinct_goto"/>
                <xsl:copy-of select="$list_no_overlap_goto"/>
                <xsl:copy-of select="$split_goto"/>
            </xsl:if>
            <xsl:apply-templates select="node()[1]" mode="first-child-next-brother">
                <xsl:with-param name="goto-style" select="'none'"/>
                <xsl:with-param name="stop-position" select="$end"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>every tag not containing Child can be directly copied and calls its first following-sibling, including case stop-position = $end</xd:desc>
        <xd:desc>exception : stop-position = $last and the element is after the last child : it is the only stop case not managed by the pogues:Child template</xd:desc>
    </xd:doc>
    <xsl:template match="*[not(descendant::pogues:Child)] | comment()" mode="first-child-next-brother">
        <xsl:param name="goto-style"/>
        <xsl:param name="stop-position"/>

        <xsl:if test="$stop-position != $last or following-sibling::pogues:Child or following-sibling::*[descendant::pogues:Child]">
            <xsl:copy-of select="."/>
            <xsl:if test="self::comment()">
                <xsl:text>
                </xsl:text>
            </xsl:if>
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
        parameters :
        - stop-position : first Child not to take
        - goto-style (none, before, after) : with stop-position, identifies the last Goto already inserted
        <xd:desc/>
    </xd:doc>
    <!-- TODO : pogues:Child | pogues:IfThenElse -->
    <xsl:template match="pogues:Child" priority="1" mode="first-child-next-brother">
        <xsl:param name="goto-style"/>
        <xsl:param name="stop-position"/>

        <xsl:variable name="current-id" select="@id"/>
        <xsl:variable name="current-position" select="$child-tree//poguesGoto:idElement[@id=$current-id]/@position"/>
        <xsl:variable name="next-sibling-position" select="$child-tree//poguesGoto:idElement[@id=$current-id]/following-sibling::poguesGoto:idElement[1]/@position"/>
        <!-- Tests the stop condition -->
        <xsl:if test="number($current-position) &lt; number($stop-position)">
            <xsl:variable name="current-goto-list">
                <xsl:copy-of select="$split_goto//poguesGoto:gotoValue[poguesGoto:From/@id = $current-id]"/>
            </xsl:variable>
            <!-- idenfifies the next Goto to apply -->
            <!-- order : -->
            <!-- - @start = 'before' first ; then @start='after' -->
            <!-- - by descendant To/@position -->
            <xsl:variable name="chosen-goto">
                <xsl:choose>
                    <!-- old $goto-style='none' and new $goto-style='before' -->
                    <xsl:when test="$goto-style='none' and $current-goto-list//poguesGoto:gotoValue[@start='before' and poguesGoto:To/@position != $current-position]">
                        <poguesGoto:gotoValue start="before">
                            <poguesGoto:To position="{max($current-goto-list//poguesGoto:gotoValue[@start='before']/poguesGoto:To/number(@position))}"/>
                        </poguesGoto:gotoValue>
                    </xsl:when>
                    <!-- old $goto-style='before' and new $goto-style='before' -->
                    <xsl:when test="$goto-style='before' and $current-goto-list//poguesGoto:gotoValue[@start='before']/poguesGoto:To[@position != $current-position 
                                                                                                                                 and number(@position) &lt; number($stop-position)]">
                        <poguesGoto:gotoValue start="before">
                            <poguesGoto:To position="{max($current-goto-list//poguesGoto:gotoValue[@start='before']/poguesGoto:To[number(@position) &lt; number($stop-position)]/@position)}"/>
                        </poguesGoto:gotoValue>
                    </xsl:when>
                    <!-- old $goto-style='before' or 'none' and new $goto-style='after' -->
                    <xsl:when test="$goto-style != 'after' and $current-goto-list//poguesGoto:gotoValue[@start='after' and poguesGoto:To/@position != $next-sibling-position]">
                        <poguesGoto:gotoValue start="after">
                            <poguesGoto:To position="{max($current-goto-list//poguesGoto:gotoValue[@start='after']/poguesGoto:To/number(@position))}"/>
                        </poguesGoto:gotoValue>
                    </xsl:when>
                    <!-- old $goto-style='after' and new $goto-style='after' -->
                    <xsl:when test="$goto-style='after' and $current-goto-list//poguesGoto:gotoValue[@start='after']/poguesGoto:To[@position != $next-sibling-position
                                                                                                                               and number(@position) &lt; number($stop-position)]">
                        <poguesGoto:gotoValue start="after">
                            <poguesGoto:To position="{max($current-goto-list//poguesGoto:gotoValue[@start='after']/poguesGoto:To[number(@position) &lt; number($stop-position)]/number(@position))}"/>
                        </poguesGoto:gotoValue>
                    </xsl:when>
                    <!-- new $goto-style='none' -->
                    <xsl:otherwise>
                        <poguesGoto:gotoValue start="none"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            
            <xsl:variable name="chosen-goto-to-id">
                <xsl:choose>
                    <xsl:when test="$chosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position = $last">
                        <xsl:value-of select="'last'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$child-tree//poguesGoto:idElement[@position=$chosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position]/@id"/>        
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            
            <xsl:variable name="chosen-goto-condition">
                <xsl:if test="$chosen-goto/poguesGoto:gotoValue/@start != 'none'">
                    <xsl:choose>
                        <xsl:when test="count($current-goto-list//poguesGoto:gotoValue[poguesGoto:From/@id=$current-id
                                                                                   and @start=$chosen-goto/poguesGoto:gotoValue/@start
                                                                                   and poguesGoto:To/@position=$chosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position])
                                        = 1">
                            <xsl:value-of select="$current-goto-list//poguesGoto:gotoValue[poguesGoto:From/@id=$current-id
                                                                                       and @start=$chosen-goto/poguesGoto:gotoValue/@start
                                                                                       and poguesGoto:To/@position=$chosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position]
                                                                                          /poguesGoto:Expression"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- The final expression is the combination of several expressions : with the word "or" between two of them-->
                            <xsl:for-each select="$current-goto-list//poguesGoto:gotoValue[poguesGoto:From/@id=$current-id
                                                                                       and @start=$chosen-goto/poguesGoto:gotoValue/@start
                                                                                       and poguesGoto:To/@position=$chosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position]">
                                <xsl:choose>
                                    <xsl:when test="position()=1">
                                        <xsl:value-of select="concat('(',poguesGoto:Expression,')')"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:if test="not(preceding-sibling::poguesGoto:gotoValue[poguesGoto:Expression = current()/poguesGoto:Expression])">
                                            <xsl:value-of select="concat(' or (',poguesGoto:Expression,')')"/>
                                        </xsl:if>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:variable>
            
            <xsl:variable name="chosen-goto-flowid">
                <xsl:for-each select="$current-goto-list//poguesGoto:gotoValue[poguesGoto:From/@id=$current-id
                                                                          and @start=$chosen-goto/poguesGoto:gotoValue/@start
                                                                          and poguesGoto:To/@position=$chosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position]">
                    <xsl:if test="position() != 1">
                        <xsl:value-of select="'-'"/>
                    </xsl:if>
                    <xsl:value-of select="@flowid"/>
                </xsl:for-each>
            </xsl:variable>
            
            <xsl:choose>
                <xsl:when test="$chosen-goto/poguesGoto:gotoValue/@start='before'">
                    <xsl:element name="IfThenElse" namespace="http://xml.insee.fr/schema/applis/pogues">
                        <xsl:attribute name="id" select="concat($chosen-goto-flowid,'-b-',$current-id)"/>
                        <xsl:element name="Expression" namespace="http://xml.insee.fr/schema/applis/pogues">
                            <xsl:value-of select="concat('not(',$chosen-goto-condition,')')"/>
                        </xsl:element>
                        <xsl:element name="IfTrue" namespace="http://xml.insee.fr/schema/applis/pogues">
                            <xsl:apply-templates select="." mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$chosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position"/>
                                <xsl:with-param name="goto-style" select="'before'"/>
                            </xsl:apply-templates>
                        </xsl:element>
                    </xsl:element>
                    <xsl:choose>
                        <xsl:when test="$chosen-goto/poguesGoto:To/@position = $last">
                            <xsl:apply-templates select="following-sibling::*[not(name()=pogues:Child) and not(following-sibling::pogues:Child)][1]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="goto-style" select="'none'"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="following-sibling::pogues:Child[@id=$chosen-goto-to-id]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="goto-style" select="'none'"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <!-- what to write into the Child -->
                    <!-- If the $goto-style = after, then the xsl:copy has already been done -->
                    <xsl:if test="$goto-style != 'after'">
                        <xsl:choose>
                            <xsl:when test="descendant::pogues:Child">
                                <xsl:copy>
                                    <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
                                    <xsl:apply-templates select="node()[1]" mode="first-child-next-brother">
                                        <xsl:with-param name="goto-style" select="'none'"/>
                                        <xsl:with-param name="stop-position" select="$end"/>
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
                        <xsl:when test="$chosen-goto/poguesGoto:gotoValue/@start='none'">
                            <xsl:apply-templates select="following-sibling::node()[1]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="goto-style" select="'none'"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:element name="IfThenElse" namespace="http://xml.insee.fr/schema/applis/pogues">
                                <xsl:attribute name="id" select="concat($chosen-goto-flowid,'-a-',$current-id)"/>
                                <xsl:element name="Expression" namespace="http://xml.insee.fr/schema/applis/pogues">
                                    <xsl:value-of select="concat('not(',$chosen-goto-condition,')')"/>
                                </xsl:element>
                                <xsl:element name="IfTrue" namespace="http://xml.insee.fr/schema/applis/pogues">
                                    <xsl:apply-templates select="." mode="first-child-next-brother">
                                        <xsl:with-param name="stop-position" select="$chosen-goto/poguesGoto:gotoValue/poguesGoto:To/@position"/>
                                        <xsl:with-param name="goto-style" select="'after'"/>
                                    </xsl:apply-templates>
                                </xsl:element>
                            </xsl:element>
                            <xsl:choose>
                                <xsl:when test="$chosen-goto/poguesGoto:To/@position = $last">
                                    <xsl:apply-templates select="following-sibling::*[not(name()=pogues:Child) and not(following-sibling::pogues:Child)][1]" mode="first-child-next-brother">
                                        <xsl:with-param name="stop-position" select="$stop-position"/>
                                        <xsl:with-param name="goto-style" select="'none'"/>
                                    </xsl:apply-templates>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:apply-templates select="following-sibling::pogues:Child[@id=$chosen-goto-to-id]" mode="first-child-next-brother">
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
