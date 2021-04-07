<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This xslt stylesheet is used to add numbering to DDI elements, by identifying the
                different depths.</xd:p>
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

    <xd:doc>
        <xd:desc>
            <xd:p>Numbering style configuration (Eno configuration)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="style">
        <xsl:copy-of select="document(../../../config/style-numerotation.xml)/Numerotation"/>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>The involved parameters are charged as an xml tree.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="params">
        <xsl:choose>
            <xsl:when test="$parameters-node//Parameters/Numerotation">
                <xsl:copy-of select="$parameters-node//Parameters/Numerotation"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="document($parameters-file)//Parameters/Numerotation"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Where to restart counting questions :</xd:p>
            <xd:p>QuestNum : all, module, no-number</xd:p>
            <xd:p>no-number : no numbering</xd:p>
            <xd:p>all : continuous numbering throughout the questionnaire </xd:p>
            <xd:p>module : return to 1 for each new module</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="numbering-browser">
        <xsl:choose>
            <xsl:when test="$params/Numerotation/QuestNum">
                <xsl:if test="$params/Numerotation/QuestNum = 'questionnaire'">
                    <xsl:value-of select="'template'"/>
                </xsl:if>
                <xsl:if test="$params/Numerotation/QuestNum != 'questionnaire'">
                    <xsl:value-of select="$params/Numerotation/QuestNum"/>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'all'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

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
            <xd:p>Default template for every element and every attribute, getting to the
                child.</xd:p>
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
            <xd:p>For every 'module', 'submodule' or 'group' type d:Sequence, prefixing the
                title.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="d:Sequence[d:TypeOfSequence = 'module' or d:TypeOfSequence = 'submodule' or d:TypeOfSequence = 'group']/r:Label">
        <xsl:variable name="level" select="parent::d:Sequence/d:TypeOfSequence"/>
        <xsl:variable name="seq-style" select="$style/Numerotation/Sequence/Level[@name = $level]"/>
        <xsl:variable name="parent-level"
            select="$style/Numerotation/Sequence/Level[following-sibling::Level[1]/@name = $level]/@name"/>
        <xsl:variable name="gd-parent-level"
            select="$style/Numerotation/Sequence/Level[following-sibling::Level[2]/@name = $level]/@name"/>

        <xsl:variable name="number">
            <xsl:apply-templates select="parent::d:Sequence" mode="calculate-number"/>
        </xsl:variable>

        <xsl:variable name="prefix">
            <xsl:choose>
                <xsl:when test="$number = ''">
                    <xsl:value-of select="$seq-style/PreSeq"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($seq-style/PreSeq, $number, $seq-style/PostNumSeq)"
                    />
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
                <xsl:when test="ancestor::d:Sequence[d:TypeOfSequence = 'group']">group</xsl:when>
                <xsl:when test="ancestor::d:Sequence[d:TypeOfSequence = 'submodule']">submodule</xsl:when>
                <xsl:otherwise>module</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="parent-level"
            select="$style/Numerotation/Sequence/Level[following-sibling::Level[1]/@name = $question-seq-level]/@name"/>
        <xsl:variable name="styleQuest"
            select="$style/Numerotation/Question/Level[@name = $question-seq-level]"/>

        <xsl:variable name="parent-number">
            <xsl:if test="not(boolean($styleQuest/NumParent))">
                <xsl:apply-templates
                    select="
                        ancestor::d:Sequence[d:TypeOfSequence = 'module' or d:TypeOfSequence = 'submodule' or d:TypeOfSequence = 'group']
                        [1]"
                    mode="calculate-number"/>
            </xsl:if>
        </xsl:variable>

        <xsl:variable name="number">
            <xsl:choose>
                <xsl:when test="$numbering-browser = 'no-number'"/>
                <xsl:otherwise>
                    <xsl:variable name="levels">
                        <Levels>
                            <Level>template</Level>
                            <Level>module</Level>
                            <Level>submodule</Level>
                            <Level>group</Level>
                        </Levels>
                    </xsl:variable>
                    <xsl:variable name="numbering-start"
                        select="$levels//Level[text() = $numbering-browser or following-sibling::Level = $numbering-browser]/text()"/>
                    <xsl:number count="*[(name() = 'd:QuestionItem' or name() = 'd:QuestionGrid')]"
                        level="any" format="{$styleQuest/StyleNumQuest}"
                        from="d:ControlConstructReference[d:Sequence[d:TypeOfSequence = $numbering-start]]"
                    />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!--Question number by concatenation: PreQuest + (($parent-number + PostNumParentQuest) + $number + PostNumQuest)-->
        <xsl:variable name="prefix">

            <!-- Depending on a pre-question symbol is requested in the customer settings.  -->

            <xsl:if test="boolean($params/Numerotation/PreQuestSymbol)">
                <xsl:value-of select="$styleQuest/PreQuest"/>
            </xsl:if>

            <xsl:if test="$number != ''">
                <xsl:if test="$parent-number != ''">
                    <xsl:value-of select="concat($parent-number, $styleQuest/PostNumParentQuest)"/>
                </xsl:if>
                <xsl:value-of select="concat($number, $styleQuest/PostNumQuest)"/>
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
        <xsl:variable name="seq-style" select="$style/Numerotation/Sequence/Level[@name = $level]"/>
        <xsl:variable name="parent-level"
            select="$style/Numerotation/Sequence/Level[following-sibling::Level[1]/@name = $level]/@name"/>

        <xsl:variable name="parent-number">
            <xsl:if test="not(boolean($seq-style/NumParent))">
                <xsl:apply-templates
                    select="ancestor::d:Sequence[d:TypeOfSequence/text() = $parent-level]"
                    mode="calculate-number"/>
            </xsl:if>
        </xsl:variable>

        <xsl:if test="$parent-number != ''">
            <xsl:value-of select="concat($parent-number, $seq-style/PostNumParentSeq)"/>
        </xsl:if>

        <xsl:number count="d:Sequence[d:TypeOfSequence/text() = $level]" level="any"
            format="{$seq-style/StyleNumSeq}"
            from="d:Sequence[d:TypeOfSequence/text() = $parent-level]"/>

    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Only the first child of a xhtml:p must be titled</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:p" mode="modif-title" priority="2">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()[position() = 1]" mode="modif-title"/>
            <xsl:apply-templates select="node()[not(position() = 1)]"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>When we match a node starting by xhtml, we only process the first child node with
                modif-title mode.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[starts-with(name(), 'xhtml')]" mode="modif-title">
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
    <xsl:template match="xhtml:span[@class = 'block']" mode="modif-title" priority="2">
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
                <xsl:value-of select="concat($prefix, .)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
