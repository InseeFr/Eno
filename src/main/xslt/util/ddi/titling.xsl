<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:i="ddi:instance:3_2" xmlns:g="ddi:group:3_2"
    xmlns:d="ddi:datacollection:3_2" xmlns:r="ddi:reusable:3_2" xmlns:a="ddi:archive:3_2"
    xmlns:l="ddi:logicalproduct:3_2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:iatddi="http://xml/insee.fr/xslt/apply-templates/ddi"
    xmlns:xhtml="http://www.w3.org/1999/xhtml">
    <xsl:param name="parameters-file"/>
    <xsl:output method="xml" indent="no" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xsl:variable name="style">
        <xsl:copy-of select="document($parameters-file)/Parameters/Title"/>
    </xsl:variable>
    <xsl:variable name="number-free-seq" select="$style/Title/Sequence/NumberFreeSeq"/>
    <xsl:variable name="number-free-filter" select="$style/Title/Question/NotNumberedLastFilter"/>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Root template, applying all the children templates</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Default template for every element and every attribute, getting to the child</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template used to add numbers to sequences</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence='module' or d:TypeOfSequence='submodule' or d:TypeOfSequence='group']/r:Label">
        <xsl:variable name="level" select="parent::d:Sequence/d:TypeOfSequence"/>
        <xsl:variable name="seq-style" select="$style/Title/Sequence/Level[@nom=$level]"/>
        <xsl:variable name="parent-level" select="$style/Title/Sequence/Level[following-sibling::Level[1]/@nom=$level]/@nom"/>
        <xsl:variable name="gd-parent-level" select="$style/Title/Sequence/Level[following-sibling::Level[2]/@nom=$level]/@nom"/>

        <xsl:variable name="number">
            <xsl:apply-templates select="parent::d:Sequence" mode="calculate-number"/>
        </xsl:variable>
        
        <xsl:variable name="prefix">
            <xsl:choose>
                <xsl:when test="$number=''">
                    <xsl:value-of select="$seq-style/PreSeq"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($seq-style/PreSeq,$number,$seq-style/PostNumSeq)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="modif-title">
                <xsl:with-param name="prefix" select="$prefix" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template used to add numbers to questions</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionText/d:LiteralText">
        <!-- The goal is to calculate the prefix, concatenation of element's numbers -->
        
        <xsl:variable name="question-seq-level">
            <xsl:choose>
                <xsl:when test="ancestor::d:Sequence[d:TypeOfSequence='group']">group</xsl:when>
                <xsl:when test="ancestor::d:Sequence[d:TypeOfSequence='submodule']">submodule</xsl:when>
                <xsl:otherwise>module</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="parent-level" select="$style/Title/Sequence/Level[following-sibling::Level[1]/@nom=$question-seq-level]/@nom"/>
        <xsl:variable name="styleQuest" select="$style/Title/Question/Level[@nom=$question-seq-level]"/>
        
        <xsl:variable name="parent-number">
            <xsl:if test="$styleQuest/NumParent !='N'">
                <xsl:apply-templates select="ancestor::d:Sequence[d:TypeOfSequence='module' or d:TypeOfSequence='submodule' or d:TypeOfSequence='group']
                    [1]" mode="calculate-number"/>
            </xsl:if>
        </xsl:variable>

        <xsl:variable name="number">
            <xsl:if test="iatddi:is-subquestion(ancestor::d:QuestionConstruct,$question-seq-level)=0">
                <!-- Counting the questions that aren't subQuestions -->
<!--                <xsl:number count="d:ControlConstructReference[d:QuestionConstruct and (iatddi:is-subquestion(d:QuestionConstruct,$niveauSeqQuest))]" 
                    level="any" format="{$styleQuest/StyleNumQuest}" from="d:ControlConstructReference[d:Sequence[d:TypeOfSequence=$niveauSeqQuest]]"/>
-->                <xsl:number count="*[(name()='d:QuestionItem' or name()='d:QuestionGrid') and (iatddi:is-subquestion(ancestor::d:QuestionConstruct,$question-seq-level))=0]" 
                    level="any" format="{$styleQuest/StyleNumQuest}" from="d:ControlConstructReference[d:Sequence[d:TypeOfSequence=$question-seq-level]]"/>
            </xsl:if>
        </xsl:variable>

        <!--Question number by concatenation: PreQuest + (($parent-number + PostNumParentQuest) + $number + PostNumQuest)-->
        <xsl:variable name="prefix">
            <xsl:value-of select="$styleQuest/PreQuest"/>
            <xsl:if test="$number!=''">
                <xsl:if test="$parent-number!=''">
                    <xsl:value-of select="concat($parent-number,$styleQuest/PostNumParentQuest)"/>
                </xsl:if>
                <xsl:value-of select="concat($number,$styleQuest/PostNumQuest)"/>
            </xsl:if>
        </xsl:variable> 
        
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="modif-title">
                <xsl:with-param name="prefix" select="$prefix" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template used to get recursively the sequence's number</xd:p>
        </xd:desc>
    </xd:doc>    
    <xsl:template match="d:Sequence" mode="calculate-number">
        <xsl:variable name="level" select="d:TypeOfSequence"/>
        <xsl:variable name="seq-style" select="$style/Title/Sequence/Level[@nom=$level]"/>
        <xsl:variable name="parent-level" select="$style/Title/Sequence/Level[following-sibling::Level[1]/@nom=$level]/@nom"/>
        
        <xsl:variable name="number">
            <xsl:if test="not(index-of($number-free-seq,r:ID)>0)">
                <xsl:number count="d:Sequence[d:TypeOfSequence/text()=$level and not(index-of($number-free-seq,r:ID)>0)]" 
                    level="any" format="{$seq-style/StyleNumSeq}" from="d:Sequence[d:TypeOfSequence/text()=$parent-level]"/>
            </xsl:if>
        </xsl:variable>
        <xsl:variable name="parent-number">
            <xsl:if test="$seq-style/NumParent !='N'">
                <xsl:apply-templates select="ancestor::d:Sequence[d:TypeOfSequence/text()=$parent-level]" mode="calculate-number"/>
            </xsl:if>
        </xsl:variable>
        
        <xsl:if test="$number!=''">
            <xsl:if test="$parent-number!=''">
                <xsl:value-of select="concat($parent-number,$seq-style/PostNumParentSeq)"/>
            </xsl:if>
            <xsl:value-of select="$number"/>
        </xsl:if>
    </xsl:template>    
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Function used to identify if 2 lists have common elements</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iatddi:is-common">
        <xsl:param name="list1"/>
        <xsl:param name="list2"/>
        <xsl:variable name="isCommon">
            <xsl:choose>
                <xsl:when test="empty($list2[1])">false</xsl:when>
                <!--<xsl:when test="contains($list1,$list2[1])">true</xsl:when>-->
                <xsl:when test="index-of($list1,$list2[1])>0">true</xsl:when>
                <xsl:when test="empty($list2[2])">false</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="iatddi:is-common($list1,$list2[position()>1])"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$isCommon"/>
    </xsl:function>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Function used to identify if a question is a subQuestion</xd:p>
        </xd:desc>
    </xd:doc>
    <!-- Used to determine if a question is a sub-question, meaning that it depends of a filter having the following features :
            - Is based on the directly previous question
            - Isn't the last element of it's actual sequence
    This sub-question can depend on one or more of these filters, we just verify that there are more than 0 -->

    <xsl:function name="iatddi:is-subquestion">
        <xsl:param name="context"/>
        <xsl:param name="seq-level"/>
        
        <!-- Gets the module, submodule or group (depending on the $seq-level) of which the question belongs in order to only get the useful filters -->
        <xsl:variable name="ancestors">
            <xsl:copy-of select="root($context)//d:Sequence[d:TypeOfSequence=$seq-level and descendant::d:QuestionConstruct=$context]"/>
        </xsl:variable>
        <xsl:value-of select="count($ancestors//d:ControlConstructReference
            [d:IfThenElse//d:TypeOfSequence[text()='potentially-hidden']
            and descendant::d:QuestionConstruct[r:ID=$context/r:ID]
            and (following-sibling::d:ControlConstructReference[d:IfThenElse or d:QuestionConstruct]
            or index-of($number-free-filter,d:IfThenElse/r:ID)>0)
            and preceding-sibling::d:ControlConstructReference[d:QuestionConstruct]]
            [iatddi:is-common(preceding-sibling::d:ControlConstructReference[d:QuestionConstruct][1]//r:TargetParameterReference/r:ID,
            d:IfThenElse/d:IfCondition//r:SourceParameterReference/r:ID)])"/>
    </xsl:function>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template used to add code before labels from dropdown lists and tables headers</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Code[ancestor::d:GridDimension[@displayCode='true' and @displayLabel='true']]
        /r:CategoryReference/l:Category/r:Label/r:Content">
        <xsl:variable name="prefix">
            <xsl:value-of select="concat(../../../../r:Value,' - ')"/>
        </xsl:variable>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="modif-title">
                <xsl:with-param name="prefix" select="$prefix" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xhtml:p" mode="modif-title" priority="2">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()[position()=1]" mode="modif-title"/>
            <xsl:apply-templates select="node()[not(position()=1)]"/>
        </xsl:copy>
    </xsl:template>

    <!-- When we match a node starting by xhtml, we only process the first child node with modif-title mode -->
    <xsl:template match="*[starts-with(name(),'xhtml')]" mode="modif-title">
        <xsl:param name="prefix" tunnel="yes"/>
        <xsl:value-of select="$prefix"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:span[@class='block']" mode="modif-title" priority="2">
        <xsl:param name="prefix" tunnel="yes"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="$prefix"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Adding the prefix -->
    <xsl:template match="text()" mode="modif-title" priority="1">
        <xsl:param name="prefix" tunnel="yes"/>
        <xsl:value-of select="concat($prefix,.)"/>
    </xsl:template>

</xsl:transform>
