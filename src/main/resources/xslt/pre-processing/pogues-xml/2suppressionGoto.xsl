<?xml version="1.0" encoding="UTF-8"?>
<!-- As pogues document use default namespacing, to simplify handling, output element should use default namespacing. -->
<xsl:stylesheet
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns="http://xml.insee.fr/schema/applis/pogues"
    xmlns:poguesGoto="http://xml.insee.fr/schema/applis/poguesGoto"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:param name="debug" select="false()"/>
    <!-- xsi:schemaLocation="Pogues.xsd"-->
    <!--xmlns:xs="http://www.w3.org/2001/XMLSchema"-->
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jun 15, 2017</xd:p>
            <xd:p><xd:b>Author:</xd:b>Antoine Dreyer</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    <!--"-->

    <xsl:template name="build-goto">
        <!-- list of the id-->
        <xsl:variable name="list_id">
            <!--TODO : be more precise on the list-->
            <xsl:for-each select="//*[@id]">
                <xsl:sort select="position()"/>
                <xsl:if test="local-name(.)='Child'">
                <poguesGoto:idElement>
                    <xsl:attribute name="id"><xsl:value-of select="./@id"/></xsl:attribute>
                    <xsl:attribute name="position"><xsl:value-of select="position()"/></xsl:attribute>
                    <poguesGoto:childrenId>
                        <xsl:for-each select="./*[self::pogues:Child]">
                            <poguesGoto:childId>
                                <xsl:value-of select="./@id"/>
                            </poguesGoto:childId>
                        </xsl:for-each>
                    </poguesGoto:childrenId>
                    <xsl:if test="count(./ancestor::pogues:Child)=0">
                        <poguesGoto:hasNoAncestor/>
                    </xsl:if>
                    <!--xsl:if test="self::pogues:GoTo">
                        <poguesGoto:isGoTo/>
                    </xsl:if-->

                </poguesGoto:idElement>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>

        <!-- list of the gotos-->
        <xsl:variable name="list_goto">
            <xsl:for-each select="//pogues:FlowControl[not(@flowControlType)]">
                <poguesGoto:gotoValue>
                    <poguesGoto:Expression>
                        <xsl:value-of select="./pogues:Expression"/>
                    </poguesGoto:Expression>
                    <!--<poguesGoto:Description>
                        <xsl:value-of select="./pogues:Description"/>
                    </poguesGoto:Description>-->
                    <poguesGoto:IfTrue>
                        <xsl:call-template name="find_id_position">
                            <xsl:with-param name="value" select="./pogues:IfTrue"/>
                            <xsl:with-param name="list_id" select="$list_id"/>
                        </xsl:call-template>
                    </poguesGoto:IfTrue>

                    <poguesGoto:FromId>
                        <!-- We compute where the goto comes from-->
                        <xsl:call-template name="find_id_position">
                            <!--id of the pogues:Child containing the pogues:GoTo-->
                            <xsl:with-param name="value" select="../@id"/>
                            <xsl:with-param name="list_id" select="$list_id"/>
                        </xsl:call-template>
                    </poguesGoto:FromId>
                </poguesGoto:gotoValue>
            </xsl:for-each>
        </xsl:variable>

        <!-- list of the forward gotos-->
        <xsl:variable name="list_forward_goto">
            <xsl:copy-of select="$list_goto/poguesGoto:gotoValue[ - poguesGoto:FromId/@position + poguesGoto:IfTrue/@position > 0]"></xsl:copy-of>
        </xsl:variable>

        <!--list of the ids in tree form with childless nodes with goto expressions-->
        <xsl:variable name="list_id_with_forward_goto_tree">
            <!--xsl:for-each select="$list_id/*">
                <xsl:if test="count(./poguesGoto:hasNoAncestor) gt 0 and count(./poguesGoto:isGoTo) = 0"-->
            <xsl:for-each select="$list_id/*[poguesGoto:hasNoAncestor]">
            <!--find the children-->
                <xsl:call-template name="find_children_id">
                    <xsl:with-param name="list_id" select="$list_id"/>
                    <xsl:with-param name="list_forward_goto" select="$list_forward_goto"/>
                </xsl:call-template>
                <!--/xsl:if-->
            </xsl:for-each>
        </xsl:variable>

        <poguesGoto:GotoParameters>
            <poguesGoto:IdList>
                <xsl:copy-of select="$list_id_with_forward_goto_tree"/>
            </poguesGoto:IdList>
            <poguesGoto:GotoList>
                <xsl:copy-of select="$list_forward_goto"/>
            </poguesGoto:GotoList>
        </poguesGoto:GotoParameters>

    </xsl:template>

    <xd:doc>
        <xd:desc/>
    </xd:doc>

    <xsl:template match="comment() | processing-instruction()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="pogues:Questionnaire">
        <xsl:variable name="gotoParameters">
            <xsl:call-template name="build-goto"/>
        </xsl:variable>

        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:if test="$debug">
                <xsl:copy-of select="$gotoParameters"/>
            </xsl:if>
            <xsl:apply-templates select="./*">
                <xsl:with-param name="list_id_with_forward_goto_tree">
                    <xsl:copy-of select="$gotoParameters/poguesGoto:GotoParameters/poguesGoto:IdList/*"/>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:copy>

    </xsl:template>

    <xd:doc>
        Tags not having "id" but containing tags having "id"
        parameter : global parameter describing forward goto
        <xd:desc/>
        <xd:param name="list_id_with_forward_goto_tree"/>
    </xd:doc>
    <xsl:template match="*[not(self::pogues:Child or self::pogues:Questionnaire) and descendant::pogues:Child]">
        <xsl:param name="list_id_with_forward_goto_tree"/>
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="./*">
                <xsl:with-param name="list_id_with_forward_goto_tree" select="$list_id_with_forward_goto_tree"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        Tags having "id" except GoTo
        parameter : global parameter describing forward goto
        <xd:desc/>
        <xd:param name="list_id_with_forward_goto_tree"/>
    </xd:doc>
    <xsl:template match="pogues:Child">
        <!--*[@id and not(self::pogues:GoTo)  and not(self::pogues:Questionnaire)]-->
        <xsl:param name="list_id_with_forward_goto_tree"/>

        <xsl:variable name="id_with_goto"><xsl:copy-of select="$list_id_with_forward_goto_tree//*[@id=current()/@id]"/></xsl:variable>

        <!--xsl:message>*<xsl:copy-of select="$id_with_goto/*/poguesGoto:GoToExpressions"></xsl:copy-of></xsl:message-->
        <xsl:choose>
            <xsl:when test="count($id_with_goto/*/poguesGoto:GoToExpressions) ne 0">
                <IfThenElse id="{generate-id()}" xsi:type="SequenceType">
                    <!--TODO : Put a template here to transform expressions-->
                    <xsl:call-template name="transform_goto_expressions">
                        <xsl:with-param name="goto_expressions" select="$id_with_goto/*/poguesGoto:GoToExpressions/poguesGoto:Expression"/>
                    </xsl:call-template>
                    <!--<Description>
                        <xsl:value-of select="$id_with_goto//poguesGoto:Description"/>
                    </Description>-->
                    <IfTrue>
                        <xsl:copy>
                            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
                            <!--xsl:message><xsl:copy-of select="./*"/></xsl:message-->
                            <xsl:apply-templates select="./*">
                                <xsl:with-param name="list_id_with_forward_goto_tree" select="$list_id_with_forward_goto_tree"/>
                            </xsl:apply-templates>
                        </xsl:copy>
                    </IfTrue>
                </IfThenElse>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
                    <xsl:apply-templates select="./*">
                        <xsl:with-param name="list_id_with_forward_goto_tree" select="$list_id_with_forward_goto_tree"/>
                    </xsl:apply-templates>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xd:doc>
        every attributes and tags not containing "id" (including pogues:FlowControl)
        <xd:desc/>
    </xd:doc>
    <xsl:template match="*[not(self::pogues:Child or self::pogues:Questionnaire) and not(descendant::pogues:Child)]">
        <!--p:Name | p:Label | p:Survey | p:CodeLists | p:Response | p:ResponseStructure | p:ComponentGroup"-->
        <xsl:copy-of select="."/>
    </xsl:template>

    <!--xd:doc>
        goto tag : do nothing
        <xd:desc/>
    </xd:doc>
    <xsl:template match="pogues:FlowControl">
        <xsl:copy-of select="."/>
    </xsl:template-->

    <xd:doc>
        find id position in id list to know from where and to where a goto goes.
        <xd:desc/>
        <xd:param name="value"/>
        <xd:param name="list_id"/>
    </xd:doc>
    <xsl:template name = "find_id_position" >
        <xsl:param name="value"/>
        <xsl:param name="list_id"/>
        <xsl:attribute name="id">
            <xsl:value-of select="$value"/>
        </xsl:attribute>
        <xsl:attribute name="position">
            <xsl:value-of select="$list_id/poguesGoto:idElement/@position[../@id = $value]"/>
        </xsl:attribute>
    </xsl:template>

    <xd:doc>
        find if the id is sandwiched between a goto and its label
        <xd:desc/>
        <xd:param name="pos"/>
        <xd:param name="list_forward_goto"/>
    </xd:doc>
    <xsl:template name="find_goto">
        <xsl:param name="pos"/>
        <xsl:param name="list_forward_goto"/>

        <xsl:for-each select="$list_forward_goto/*">
            <xsl:if test="-$pos+./poguesGoto:IfTrue/@position gt 0 and $pos - ./poguesGoto:FromId/@position gt 0" >
                <xsl:copy-of select="./poguesGoto:Expression"/>
                <!--<xsl:copy-of select="./poguesGoto:Description"/>-->
            </xsl:if>
        </xsl:for-each>


    </xsl:template>

    <xd:doc>
        find if the condition on the execution of the id consequence of goto
        <xd:desc/>
        <xd:param name="list_id_with_forward_goto"/>
        <xd:param name="node_id">the node to be documented</xd:param>
        <xd:param name="id_child"/>
        <xd:param name="id_child2"/>
    </xd:doc>
    <xsl:template name="find_id_goto_expressions">
        <xsl:param name="list_id_with_forward_goto"/>
        <xsl:param name="node_id" />
        <xsl:param name="id_child" /><!--xsl:value-of select="$node_id/@id"/></xsl:variable-->
        <xsl:param name="id_child2" /><!--xsl:value-of select="$node_id/*/@id"/></xsl:variable-->
        <xsl:variable name ="list_expression"><xsl:copy-of  select="$list_id_with_forward_goto/*/poguesGoto:Expression[../@id=$id_child]"/></xsl:variable>
        <xsl:variable name ="list_expression_child"><xsl:copy-of  select="$list_id_with_forward_goto/*[contains($id_child2, @id)]"/></xsl:variable>

        <xsl:choose>
            <xsl:when test="count($list_expression/*)=0">
                <!--there is no goto : do nothing -->

            </xsl:when>
            <xsl:when test="count($id_child2)=0">
                <!-- there is no child -->
                <poguesGoto:GoToExpressions>
                    <xsl:copy-of select="$list_expression"/>
                </poguesGoto:GoToExpressions>
            </xsl:when>
            <xsl:otherwise>

                <poguesGoto:GoToExpressions>
                    <xsl:for-each select="$list_expression/*">
                        <xsl:variable name="e"><xsl:value-of select="."></xsl:value-of></xsl:variable>

                        <xsl:if test="count($list_expression_child/*[pogues:Expression = $e])!=count($list_expression_child/*)">
                            <xsl:copy-of select="."/>
                        </xsl:if>
                    </xsl:for-each>
                </poguesGoto:GoToExpressions>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        construct the id tree with goto expression
        <xd:desc/>
        <xd:param name="list_id"/>
        <xd:param name="list_forward_goto"/>
    </xd:doc>
    <xsl:template name="find_children_id">
        <xsl:param name="list_id"/>
        <xsl:param name="list_forward_goto"/>
        <xsl:variable name="children" select="./poguesGoto:childrenId"/>
        <xsl:choose>
            <xsl:when test="count($children/*)=0">
                <!-- if there is no child we copy the node as is, adding gotoExpressions found with forward goto-->
                <xsl:call-template name="find_id_node_without_child">
                    <xsl:with-param name="list_forward_goto" select="$list_forward_goto"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <!-- if there is a child we copy the node as is, and use the children-->
                <xsl:copy>
                    <xsl:attribute name="id" select= "./@id"/>
                    <xsl:attribute name="position" select= "./@position"/>

                    <!--for each child-->
                    <xsl:variable name="children_result">
                        <xsl:for-each select="$list_id/*[@id = $children/poguesGoto:childId]">
                            <xsl:call-template name="find_children_id">
                                <xsl:with-param name="list_id" select="$list_id"/>
                                <!--<xsl:with-param name="list_id" select="."/>-->
                                <xsl:with-param name="list_forward_goto" select="$list_forward_goto"/>
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:variable>

                    <xsl:variable name="goto_expressions">
                        <xsl:if test="count($children_result/poguesGoto:idElement/poguesGoto:GoToExpressions) = count($children_result/poguesGoto:idElement)">
                            <xsl:call-template name="find_intersection">
                                <xsl:with-param name="prior_value">
                                    <xsl:copy-of select="$children_result/*[1]/poguesGoto:GoToExpressions/*"/>
                                </xsl:with-param>
                                <xsl:with-param name="remaining_list">
                                    <xsl:copy-of select="$children_result/*[position()>1]/poguesGoto:GoToExpressions"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:if>
                    </xsl:variable>
                    <xsl:if test="count($goto_expressions/*) gt 0">
                        <poguesGoto:GoToExpressions>
                            <xsl:copy-of select="$goto_expressions"/>
                        </poguesGoto:GoToExpressions>
                    </xsl:if>
                    <xsl:for-each select="$children_result/poguesGoto:idElement">
                        <xsl:call-template name="build_child">
                            <xsl:with-param name="goto_expressions" select="$goto_expressions"/>
                        </xsl:call-template>
                    </xsl:for-each>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc/>
        <xd:param name="list_forward_goto"/>
    </xd:doc>
    <xsl:template name="find_id_node_without_child">
        <xsl:param name="list_forward_goto"/>
        <xsl:copy>
            <xsl:attribute name="id" select= "./@id"/>
            <xsl:attribute name="position" select= "./@position"/>
            <xsl:variable name="temp0">
                <xsl:call-template name="find_goto">
                    <xsl:with-param name="pos" select="./@position"/>
                    <xsl:with-param name="list_forward_goto" select="$list_forward_goto"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:if test="count($temp0/*) ne 0">
                <poguesGoto:GoToExpressions>
                    <xsl:copy-of select="$temp0"/>
                </poguesGoto:GoToExpressions>
            </xsl:if>

        </xsl:copy>

    </xsl:template>

    <xd:doc>
        <xd:desc/>
        <xd:param name="goto_expressions"/>
    </xd:doc>
    <xsl:template name="build_child">
        <xsl:param name="goto_expressions"/>
        <xsl:variable name="gotoChild">
            <xsl:choose>
                <xsl:when test="count($goto_expressions/poguesGoto:Expression)=0">
                    <xsl:copy-of select="./poguesGoto:GoToExpressions/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <!--xsl:message><xsl:value-of select="./@id"/></xsl:message-->
                    <xsl:for-each select="./poguesGoto:GoToExpressions">
                        <xsl:variable name="expr_current" select="poguesGoto:Expression"/>
                        <xsl:if test="count($goto_expressions/poguesGoto:Expression=$expr_current)=0">
                            <xsl:copy-of select="*"/>
                        </xsl:if>
                     </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:copy>
            <xsl:attribute name="id" select= "./@id"/>
            <xsl:attribute name="position" select= "./@position"/>
            <xsl:if test="count($gotoChild/*) gt 0">
                <poguesGoto:GoToExpressions>
                    <xsl:copy-of select="$gotoChild"/>
                </poguesGoto:GoToExpressions>
            </xsl:if>
            <xsl:copy-of select="./*[not(self::poguesGoto:GoToExpressions)]"/>
        </xsl:copy>

    </xsl:template>

    <xd:doc>
        <xd:desc/>
        <xd:param name="prior_value"/>
        <xd:param name="remaining_list"/>
    </xd:doc>
    <xsl:template name="find_intersection">
        <xsl:param name="prior_value"/>
        <xsl:param name="remaining_list"/>
        <xsl:choose>
            <xsl:when test="count($prior_value/*)=0">
            </xsl:when>
            <xsl:when test="count($remaining_list/*)=0">
                <xsl:copy-of select="$prior_value"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="find_intersection">
                    <xsl:with-param name="prior_value">
                        <xsl:for-each select="$prior_value">
                            <xsl:if test="count($remaining_list/*[1]/poguesGoto:Expression= current())>0">
                                <xsl:copy-of select="current()"/>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:with-param>
                    <xsl:with-param name="remaining_list">
                        <xsl:copy-of select="$remaining_list/*[position()>1]"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc/>
        <xd:param name="goto_expressions"/>
    </xd:doc>
    <xsl:template name="transform_goto_expressions">
        <xsl:param name="goto_expressions"/>
        <Expression>
            <xsl:value-of select="concat('not( ',string-join(($goto_expressions),' ) and not( '),')')"/>
        </Expression>
    </xsl:template>



</xsl:stylesheet>