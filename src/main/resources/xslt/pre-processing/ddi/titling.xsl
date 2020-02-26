<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This xslt stylesheet is used to add numbering to DDI elements, by identifying the different depths.</xd:p>
            <xd:p>It uses a parameter file on a questionnaire level to do this job.</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>The parameter file used by the stylesheet.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="parameters-file"/>
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty/>
    </xsl:param>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="no" encoding="UTF-8"/>

    <!--<xsl:strip-space elements="*"/>-->

    <xd:doc>
        <xd:desc>
            <xd:p>The involved parameters are charged as an xml tree.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="style">
        <xsl:choose>
            <xsl:when test="$parameters-node//Parameters/Title">
                <xsl:copy-of select="$parameters-node//Parameters/Title"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="document($parameters-file)//Parameters/Title"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Where to restart counting questions :</xd:p>
            <xd:p>sequence : template, module, submodule, group</xd:p>
            <xd:p>filter : numbered-filter, unnumbered-filter : questions inside them don't count for following-sibling, but can be numbered or not</xd:p>
            <xd:p>no-number : no number for questions</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="numbering-browser">
        <xsl:choose>
            <xsl:when test="$style/Title/Browsing">
                <xsl:value-of select="$style/Title/Browsing"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'unnumbered-filter'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>The list of sequences not to title with a number</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="number-free-seq" select="$style/Title/Sequence/NumberFreeSeq"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The list of filters which are at the end of a sequence, </xd:p>
            <xd:p>but for which questions must not be titled with a number</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="number-free-filter" select="$style/Title/Question/NotNumberedLastFilter"/>
<!--
    <xd:doc>
        <xd:desc>
            <xd:p>A virtual tree to easy question numbering for numbered filter</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="virtual-tree">
        <xsl:if test="$numbering-browser='numbered-filter'">
            <xsl:apply-templates select="node()[1]" mode="virtual-tree"/>
        </xsl:if>
    </xsl:variable>
    
    <xsl:template match="node() | @*" mode="virtual-tree">
        <xsl:apply-templates select="node()[1]" mode="virtual-tree"/>
        <xsl:apply-templates select="following-sibling::node()[1]" mode="virtual-tree"/>
    </xsl:template>
    <xsl:template match="d:ControlConstructReference[d:Sequence]" mode="virtual-tree">
        <Sequence>
            <xsl:attribute name="id" select="d:Sequence/r:ID"/>
            <xsl:apply-templates select="node()[1]" mode="virtual-tree"/>
        </Sequence>
        <xsl:apply-templates select="following-sibling::node()[1]" mode="virtual-tree"/>
    </xsl:template>
    
    <!-\- Following-sibling IfThenElse is inside the Question if necessary -\->
    <xsl:template match="d:ControlConstructReference[d:QuestionConstruct]" mode="virtual-tree">
        <Question>
            <xsl:attribute name="id" select="d:QuestionConstruct/r:ID"/>
            <xsl:apply-templates select="node()[1]" mode="virtual-tree"/>
            <xsl:if test="following-sibling::d:ControlConstructReference[not(d:ComputationItem)][1][d:IfThenElse]
                                                                        /following-sibling::d:ControlConstructReference[not(d:ComputationItem)]">
                <xsl:apply-templates select="following-sibling::d:ControlConstructReference[not(d:ComputationItem)][1]" mode="virtual-tree"/>        
            </xsl:if>
        </Question>
        <xsl:choose>
            <xsl:when test="following-sibling::d:ControlConstructReference[not(d:ComputationItem)][1][d:IfThenElse]
                /following-sibling::d:ControlConstructReference[not(d:ComputationItem)]">
                <xsl:apply-templates select="following-sibling::d:ControlConstructReference[not(d:ComputationItem)
                    and (not(d:IfThenElse) or not(following-sibling::d:ControlConstructReference[not(d:ComputationItem)]))][1]" mode="virtual-tree"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="following-sibling::node()[1]" mode="virtual-tree"/>    
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="d:ControlConstructReference[d:IfThenElse]" mode="virtual-tree">
        <xsl:apply-templates select="node()[1]" mode="virtual-tree"/>
        <xsl:if test="following-sibling::d:ControlConstructReference[not(d:ComputationItem)][1][d:IfThenElse and following-sibling::d:ControlConstructReference[not(d:ComputationItem)]]">
            <xsl:apply-templates select="following-sibling::d:ControlConstructReference[not(d:ComputationItem)][1]" mode="virtual-tree"/>
        </xsl:if>
    </xsl:template>
   --> 
    

    <xd:doc>
        <xd:desc>
            <xd:p>Root template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, getting to the child.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template used to add numbers to sequences.</xd:p>
            <xd:p>For every 'module', 'submodule' or 'group' type d:Sequence, prefixing the title.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="d:Sequence[d:TypeOfSequence='module' or d:TypeOfSequence='submodule' or d:TypeOfSequence='group']/r:Label">
        <xsl:variable name="level" select="parent::d:Sequence/d:TypeOfSequence"/>
        <xsl:variable name="seq-style" select="$style/Title/Sequence/Level[@name=$level]"/>
        <xsl:variable name="parent-level" select="$style/Title/Sequence/Level[following-sibling::Level[1]/@name=$level]/@name"/>
        <xsl:variable name="gd-parent-level" select="$style/Title/Sequence/Level[following-sibling::Level[2]/@name=$level]/@name"/>

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

    <xd:doc>
        <xd:desc>
            <xd:p>Template used to add numbers to questions.</xd:p>
            <xd:p>For every d:LiteralText wrapped in a d:QuestionText.</xd:p>
        </xd:desc>
    </xd:doc>
    <!--  -->
    <xsl:template match="d:QuestionText/d:LiteralText">
        <!-- The goal is to calculate the prefix, concatenation of element's numbers -->
        <xsl:variable name="question-seq-level">
            <xsl:choose>
                <xsl:when test="ancestor::d:Sequence[d:TypeOfSequence='group']">group</xsl:when>
                <xsl:when test="ancestor::d:Sequence[d:TypeOfSequence='submodule']">submodule</xsl:when>
                <xsl:otherwise>module</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="parent-level"
            select="$style/Title/Sequence/Level[following-sibling::Level[1]/@name=$question-seq-level]/@name"/>
        <xsl:variable name="styleQuest"
            select="$style/Title/Question/Level[@name=$question-seq-level]"/>

        <xsl:variable name="parent-number">
            <xsl:if test="not(boolean($styleQuest/NumParent))">
                <xsl:apply-templates
                    select="ancestor::d:Sequence[d:TypeOfSequence='module' or d:TypeOfSequence='submodule' or d:TypeOfSequence='group']
                    [1]"
                    mode="calculate-number"/>
            </xsl:if>
        </xsl:variable>

        <xsl:variable name="number">
            <xsl:choose>
                <xsl:when test="$numbering-browser='no-number'"/>
                <xsl:when test="$numbering-browser='unnumbered-filter'">
                    <xsl:if
                        test="enoddi:is-subquestion(ancestor::d:QuestionConstruct,$question-seq-level)=0">
                        <!-- Counting the questions that aren't subQuestions -->
                        <xsl:number
                            count="*[(name()='d:QuestionItem' or name()='d:QuestionGrid') and (enoddi:is-subquestion(ancestor::d:QuestionConstruct,$question-seq-level))=0]"
                            level="any" format="{$styleQuest/StyleNumQuest}"
                            from="d:ControlConstructReference[d:Sequence[d:TypeOfSequence=$question-seq-level]]"
                        />
                    </xsl:if>
                </xsl:when>
                <xsl:when test="$numbering-browser='numbered-filter' and enoddi:is-subquestion(ancestor::d:QuestionConstruct,$question-seq-level)=0">
                    <!-- Counting the questions that aren't subQuestions -->
                    <xsl:number
                        count="*[(name()='d:QuestionItem' or name()='d:QuestionGrid') and (enoddi:is-subquestion(ancestor::d:QuestionConstruct,$question-seq-level))=0]"
                        level="any" format="{$styleQuest/StyleNumQuest}"
                        from="d:ControlConstructReference[d:Sequence[d:TypeOfSequence=$question-seq-level]]"
                    />
                </xsl:when>
                <xsl:when test="$numbering-browser='numbered-filter' and enoddi:is-subquestion(ancestor::d:QuestionConstruct,$question-seq-level)&gt;0">
                    <!-- Counting the subQuestions -->
                    <!-- Doesn't work : trying to use a virtual tree ; started, not finished, and not prioritary -->
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="levels">
                        <Levels>
                            <Level>template</Level>
                            <Level>module</Level>
                            <Level>submodule</Level>
                            <Level>group</Level>
                        </Levels>
                    </xsl:variable>
                    <xsl:variable name="numbering-start" select="$levels//Level[text()=$numbering-browser or following-sibling::Level=$numbering-browser]/text()"/>
                    <xsl:number
                        count="*[(name()='d:QuestionItem' or name()='d:QuestionGrid')]"
                        level="any" format="{$styleQuest/StyleNumQuest}"
                        from="d:ControlConstructReference[d:Sequence[d:TypeOfSequence=$numbering-start]]"
                    />
                </xsl:otherwise>
            </xsl:choose>
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

    <xd:doc>
        <xd:desc>
            <xd:p>Template used to get recursively the sequence's number.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence" mode="calculate-number">
        <xsl:variable name="level" select="d:TypeOfSequence"/>
        <xsl:variable name="seq-style" select="$style/Title/Sequence/Level[@name=$level]"/>
        <xsl:variable name="parent-level"
            select="$style/Title/Sequence/Level[following-sibling::Level[1]/@name=$level]/@name"/>

        <xsl:variable name="number">
            <xsl:if test="$seq-style/StyleNumSeq">
                <xsl:if test="not(index-of($number-free-seq,r:ID)>0)">
                    <xsl:number
                        count="d:Sequence[d:TypeOfSequence/text()=$level and not(index-of($number-free-seq,r:ID)>0)]"
                        level="any" format="{$seq-style/StyleNumSeq}"
                        from="d:Sequence[d:TypeOfSequence/text()=$parent-level]"/>
                </xsl:if>                
            </xsl:if>
        </xsl:variable>
        <xsl:variable name="parent-number">
            <xsl:if test="not(boolean($seq-style/NumParent))">
                <xsl:apply-templates
                    select="ancestor::d:Sequence[d:TypeOfSequence/text()=$parent-level]"
                    mode="calculate-number"/>
            </xsl:if>
        </xsl:variable>

        <xsl:if test="$number!=''">
            <xsl:if test="$parent-number!=''">
                <xsl:value-of select="concat($parent-number,$seq-style/PostNumParentSeq)"/>
            </xsl:if>
            <xsl:value-of select="$number"/>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function used to identify if 2 lists have common elements.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoddi:is-common">
        <xsl:param name="list1"/>
        <xsl:param name="list2"/>
        <xsl:variable name="isCommon">
            <xsl:choose>
                <xsl:when test="empty($list2[1])">false</xsl:when>
                <xsl:when test="index-of($list1,$list2[1])>0">true</xsl:when>
                <xsl:when test="empty($list2[2])">false</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="enoddi:is-common($list1,$list2[position()>1])"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$isCommon"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function used to identify if a question is a sub-question.</xd:p>
            <xd:p>It depends of a filter having the following features :</xd:p>
            <xd:p>- is based on the directly previous question</xd:p>
            <xd:p>- isn't the last element of it's actual sequence</xd:p>
            <xd:p>This sub-question can depend on one or more of these filters, we just verify that there are more than 0.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoddi:is-subquestion">
        <xsl:param name="context"/>
        <xsl:param name="seq-level"/>

        <!-- Gets the module, submodule or group (depending on the $seq-level) of which the question belongs in order to only get the useful filters -->
        <xsl:variable name="ancestors">
            <xsl:copy-of
                select="root($context)//d:Sequence[d:TypeOfSequence=$seq-level and descendant::d:QuestionConstruct=$context]"
            />
        </xsl:variable>
        <xsl:value-of select="count($ancestors//d:ControlConstructReference
                            [d:IfThenElse//d:TypeOfSequence[text()='hideable']
                             and descendant::d:QuestionConstruct[r:ID=$context/r:ID]
                             and (following-sibling::d:ControlConstructReference[d:IfThenElse or d:QuestionConstruct]
                                  or index-of($number-free-filter,d:IfThenElse/r:ID)>0)
                             and preceding-sibling::d:ControlConstructReference[d:QuestionConstruct]]
                            [enoddi:is-common(preceding-sibling::d:ControlConstructReference[d:QuestionConstruct][1]//r:TargetParameterReference/r:ID,
                                              d:IfThenElse/d:IfCondition//r:SourceParameterReference/r:ID)]
                                   )"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Only the first child of a xhtml:p must be titled</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:p" mode="modif-title" priority="2">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()[position()=1]" mode="modif-title"/>
            <xsl:apply-templates select="node()[not(position()=1)]"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>When we match a node starting by xhtml, we only process the first child node with modif-title mode.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[starts-with(name(),'xhtml')]" mode="modif-title">
        <xsl:param name="prefix" tunnel="yes"/>
        <xsl:value-of select="$prefix"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Adding the prefix.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:span[@class='block']" mode="modif-title" priority="2">
        <xsl:param name="prefix" tunnel="yes"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="$prefix"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Adding the prefix.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="text()" mode="modif-title" priority="1">
        <xsl:param name="prefix" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="preceding-sibling::xhtml:p or following-sibling::xhtml:p">
                <xsl:value-of select="."/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat($prefix,.)"/>        
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
