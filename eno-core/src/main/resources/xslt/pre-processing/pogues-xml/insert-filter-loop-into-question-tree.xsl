<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:poguesFilterLoop="http://xml.insee.fr/schema/applis/poguesFilterLoop"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
        <xd:desc>The whole input file</xd:desc>
    </xd:doc>
    <xsl:variable name="root" select="." as="node()"/>

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
                            <xsl:choose>
                                <xsl:when test="@xsi:type='RoundaboutType'">
                                    <xsl:variable name="lastRoundaboutElement" select="pogues:Loop/pogues:MemberReference[2]"/>
                                <xsl:for-each select="following-sibling::pogues:Child[@id = $lastRoundaboutElement
                                                                                       or following-sibling::pogues:Child/@id = $lastRoundaboutElement]">
                                        <poguesFilterLoop:childId>
                                            <xsl:value-of select="./@id"/>
                                        </poguesFilterLoop:childId>
                                    </xsl:for-each>
                                </xsl:when>
                            <xsl:otherwise>
                                    <xsl:for-each select="pogues:Child">
                                        <xsl:variable name="childId" select="@id"/>
                                        <xsl:if test="not(preceding-sibling::pogues:Child
                                                          [@xsi:type='RoundaboutType'
                                                       and pogues:Loop[pogues:MemberReference = $childId 
                                                                    or pogues:MemberReference = $root//pogues:Child[preceding-sibling::pogues:Child/@id = $childId]/@id]
                                                          ])">
                                            <poguesFilterLoop:childId>
                                                <xsl:value-of select="./@id"/>
                                            </poguesFilterLoop:childId>
                                        </xsl:if>
                                    </xsl:for-each>
                                </xsl:otherwise>
                            </xsl:choose>
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
                <xsl:variable name="from-position" select="min($child-tree//poguesFilterLoop:idElement[@id = current()/pogues:MemberReference]/number(@position))"/>
                <xsl:variable name="to-position" select="max($child-tree//poguesFilterLoop:idElement[@id = current()/pogues:MemberReference]/number(@position))"/>
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
            <xsl:copy-of select="$list-filter//poguesFilterLoop:FilterLoop"/>
            <xsl:copy-of select="$list-loop//poguesFilterLoop:FilterLoop"/>
        </poguesFilterLoop:FilterLoopList>
    </xsl:variable>

    <xd:doc>
        <xd:desc>the root element</xd:desc>
    </xd:doc>
    <xsl:template match="/pogues:Questionnaire" priority="1">
        <!-- raise errors -->
        <xsl:for-each select="$list-loop-filter//poguesFilterLoop:FilterLoop">
            <xsl:variable name="from-position" select="poguesFilterLoop:From/number(@position)"/>
            <xsl:variable name="to-position" select="poguesFilterLoop:To/number(@position)"/>
            <xsl:variable name="main-id" select="@id"/>
            <xsl:for-each select="$list-loop-filter//poguesFilterLoop:FilterLoop">
                <xsl:if test="poguesFilterLoop:From/number(@position) &gt; $from-position
                          and poguesFilterLoop:From/number(@position) &lt; $to-position
                          and poguesFilterLoop:To/number(@position) &gt; $to-position">
                    <xsl:variable name="current-id" select="@id"/>
                    <xsl:message terminate="yes">
                        <xsl:value-of select="'Problème de chevauchement entre '"/>
                        <xsl:choose>
                            <xsl:when test="$list-loop-filter//poguesFilterLoop:FilterLoop[@id=$main-id]/@type='loop'">
                                <xsl:value-of select="concat('la boucle ',$main-id ,' &quot;',$root//pogues:Iteration[@id = $main-id]/pogues:Name,'&quot;')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('le filtre ',$main-id ,' &quot;',$root//*[(local-name()='FlowControl' or local-name()='Next') and @id = $main-id]/pogues:Description,'&quot;')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:value-of select="' qui va de '"/>
                        <xsl:value-of select="$root//pogues:Child[@id = $child-position-list//poguesFilterLoop:idElement[@position = $from-position]/@id]/pogues:Name"/>
                        <xsl:value-of select="' à '"/>
                        <xsl:value-of select="$root//pogues:Child[@id = $child-position-list//poguesFilterLoop:idElement[@position = $to-position]/@id]/pogues:Name"/>
                        <xsl:value-of select="' et '"/>
                        <xsl:choose>
                            <xsl:when test="$list-loop-filter//poguesFilterLoop:FilterLoop[@id=$current-id]/@type='loop'">
                                <xsl:value-of select="concat('la boucle ',$current-id ,' &quot;',$root//pogues:Iteration[@id = $current-id]/pogues:Name,'&quot;')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('le filtre ',$current-id ,' &quot;',$root//*[(local-name()='FlowControl' or local-name()='Next') and @id = $current-id]/pogues:Description,'&quot;')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:value-of select="' qui va de '"/>
                        <xsl:value-of select="$root//pogues:Child[@id = $child-position-list//poguesFilterLoop:idElement[@position = current()/poguesFilterLoop:From/@position]/@id]/pogues:Name"/>
                        <xsl:value-of select="' à '"/>
                        <xsl:value-of select="$root//pogues:Child[@id = $child-position-list//poguesFilterLoop:idElement[@position = current()/poguesFilterLoop:To/@position]/@id]/pogues:Name"/>
                    </xsl:message>
                </xsl:if>
            </xsl:for-each>
        </xsl:for-each>
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
        <xd:desc>Roundabout</xd:desc>
    </xd:doc>
    <xsl:template match="pogues:Child[@xsi:type='RoundaboutType']" priority="2" mode="first-child-next-brother">
        <xsl:param name="stop-position"/>
        
        <xsl:variable name="roundabout-last-member" select="pogues:Loop/pogues:MemberReference[2]"/>
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction() | node()[not(self::pogues:Loop)]"/>
            <xsl:element name="Loop" namespace="http://xml.insee.fr/schema/applis/pogues">
                <xsl:attribute name="id" select="concat(@id,'-',pogues:Loop/pogues:Name)"/>
                <xsl:copy-of select="pogues:Loop/*[not(self::pogues:MemberReference)]"/>
                <xsl:apply-templates select="following-sibling::node()[1]" mode="first-child-next-brother">
                    <xsl:with-param name="stop-position" select="$roundabout-last-member"/>
                </xsl:apply-templates>                
            </xsl:element>
        </xsl:copy>
        <xsl:apply-templates select="following-sibling::pogues:Child[@id = $roundabout-last-member]/following-sibling::*[1]" mode="first-child-next-brother">
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
        <xsl:variable name="chosen-next-filter" as="node()*">
            <poguesFilterLoop:FilterLoopList>
                <xsl:if test="$next-filter-position != ''">
                    <xsl:copy-of select="$possible-next-filters//poguesFilterLoop:FilterLoop[poguesFilterLoop:To/@position = string($next-filter-position)]"/>
                </xsl:if>
            </poguesFilterLoop:FilterLoopList>
        </xsl:variable>

        <xsl:choose>
            <!-- Neither filter nor loop -->
            <xsl:when test="not($chosen-next-filter//poguesFilterLoop:FilterLoop)">
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
            </xsl:when>
            <xsl:otherwise>
                <!-- Allow filter of filter of loop of loop -->
                <xsl:call-template name="include-IfThenElse">
                    <xsl:with-param name="filters" as="node()">
                        <poguesFilterLoop:FilterList>
                            <xsl:copy-of select="$chosen-next-filter//poguesFilterLoop:FilterLoop[@type='filter']"/>
                        </poguesFilterLoop:FilterList>
                    </xsl:with-param>
                    <xsl:with-param name="content" as="node()*">
                        <xsl:call-template name="include-Loop">
                            <xsl:with-param name="loops" as="node()">
                                <poguesFilterLoop:LoopList>
                                    <xsl:copy-of select="$chosen-next-filter//poguesFilterLoop:FilterLoop[@type='loop']"/>
                                </poguesFilterLoop:LoopList>                                
                            </xsl:with-param>
                            <xsl:with-param name="content" as="node()*">
                                <xsl:apply-templates select="." mode="first-child-next-brother">
                                    <xsl:with-param name="stop-position" select="$chosen-next-filter//poguesFilterLoop:To[1]/@id"/>
                                    <xsl:with-param name="current-filter" select="$chosen-next-filter//poguesFilterLoop:FilterLoop[1]/@id"/>
                                </xsl:apply-templates>                                
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:if test="$stop-position != $chosen-next-filter//poguesFilterLoop:To[1]/@id">
                    <xsl:choose>
                        <xsl:when test="$chosen-next-filter//poguesFilterLoop:To[1]/@id = $current-id">
                            <xsl:apply-templates select="following-sibling::*[1]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="current-filter" select="''"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="following-sibling::pogues:Child[@id = $chosen-next-filter//poguesFilterLoop:To[1]/@id]/following-sibling::*[1]" mode="first-child-next-brother">
                                <xsl:with-param name="stop-position" select="$stop-position"/>
                                <xsl:with-param name="current-filter" select="''"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="include-IfThenElse">
        <xsl:param name="content" as="node()*"/>
        <xsl:param name="filters" as="node()"/>
        
        <xsl:choose>
            <xsl:when test="$filters//poguesFilterLoop:FilterLoop">
                <xsl:call-template name="include-IfThenElse">
                    <xsl:with-param name="content" as="node()*">
                        <xsl:element name="IfThenElse" namespace="http://xml.insee.fr/schema/applis/pogues">
                            <xsl:attribute name="id" select="$filters//poguesFilterLoop:FilterLoop[1]/@id"/>
                            <xsl:copy-of select="/pogues:Questionnaire//*[@id = $filters//poguesFilterLoop:FilterLoop[1]/@id]/*[local-name()='Expression' or local-name()='Description']"/>
                            <xsl:element name="IfTrue" namespace="http://xml.insee.fr/schema/applis/pogues">
                                <xsl:copy-of select="$content"/>
                            </xsl:element>
                        </xsl:element>
                    </xsl:with-param>
                    <xsl:with-param name="filters" as="node()">
                        <poguesFilterLoop:FilterList>
                            <xsl:copy-of select="$filters//poguesFilterLoop:FilterLoop[position() != 1]"/>
                        </poguesFilterLoop:FilterList>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="$content"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="include-Loop">
        <xsl:param name="content" as="node()*"/>
        <xsl:param name="loops" as="node()"/>
        
        <xsl:choose>
            <xsl:when test="$loops//poguesFilterLoop:FilterLoop">
                <xsl:variable name="chosen-loop" select="$loops//poguesFilterLoop:FilterLoop[1]/@id"/>
                <xsl:call-template name="include-Loop">
                    <xsl:with-param name="content" as="node()*">
                        <xsl:element name="Loop" namespace="http://xml.insee.fr/schema/applis/pogues">
                            <xsl:attribute name="id" select="$chosen-loop"/>
                            <xsl:copy-of select="/pogues:Questionnaire/pogues:Iterations/pogues:Iteration[@id = $chosen-loop]/*[not(local-name()='MemberReference')]"/>
                            <xsl:copy-of select="$content"/>
                        </xsl:element>
                    </xsl:with-param>
                    <xsl:with-param name="loops" as="node()">
                        <poguesFilterLoop:LoopList>
                            <xsl:copy-of select="$loops//poguesFilterLoop:FilterLoop[@id != $chosen-loop]"/>
                        </poguesFilterLoop:LoopList>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="$content"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>
