<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="enoddi xd"
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

    <xsl:variable name="style" select="doc('../../../config/style-numerotation.xml')/Numerotation"/>

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
                <xsl:if test="$params/Numerotation/QuestNum = 'all'">
                    <xsl:copy-of select="'template'"/>
                </xsl:if>
                <xsl:if test="$params/Numerotation/QuestNum != 'all'">
                    <xsl:value-of select="$params/Numerotation/QuestNum"/>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'template'"/>
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
            <xd:p>For every 'module' type d:Sequence, prefixing the title.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence = 'module']/r:Label">

        <xsl:variable name="number">
            <!-- If the SeqNum into parameters is true --> 
            <xsl:if test="xs:boolean($params//SeqNum/text()='true')">
                <xsl:apply-templates select="parent::d:Sequence" mode="calculate-number"/>
            </xsl:if>
        </xsl:variable>

        <xsl:variable name="prefix">
                <xsl:if test="$number != ''">
                    <xsl:value-of
                        select="concat($number, $style//Sequence/PostNumSeq)"
                    />
                </xsl:if>
        </xsl:variable>

        <xsl:variable name="languages" select="r:Content/@xml:lang"/>
        <xsl:variable name="prefixAdded" select="not(descendant::xhtml:p) and not(descendant::xhtml:span)" as="xs:boolean"/>
        <xsl:copy>
            <xsl:if test="$prefixAdded">
                <xsl:for-each select="$languages">
                    <d:Text xml:lang="{.}"><xsl:value-of select="$prefix"/></d:Text>     
                </xsl:for-each>
            </xsl:if>
            <xsl:apply-templates select="node() | @*" mode="modif-title">
                <xsl:with-param name="prefix" select="if($prefixAdded) then '' else $prefix" tunnel="yes"/>
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

        <xsl:variable name="number">
            <xsl:choose>
                <xsl:when test="$numbering-browser = 'no-number'"/>
                <xsl:otherwise>
                    <xsl:number count="*[(name() = 'd:QuestionItem' or name() = 'd:QuestionGrid')]"
                        level="any" format="{$style/Question/StyleNumQuest}"
                        from="d:ControlConstructReference[d:Sequence[d:TypeOfSequence = $numbering-browser/text()]]"
                    />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!--Question number by concatenation: PreQuest + (($sequence-number + PostNumParentQuest) + $number + PostNumQuest)-->
        <xsl:variable name="prefix">

            <!-- Depending on a pre-question symbol is requested in the customer settings.  -->

            <xsl:if test="xs:boolean($params/Numerotation/PreQuestSymbol/text())">
                <xsl:value-of select="$style/Question/PreQuest"/>
            </xsl:if>

            <xsl:if test="$number != ''">
                <xsl:value-of select="concat($number, $style/Question/PostNumQuest)"/>
            </xsl:if>
        </xsl:variable>
        <xsl:variable name="languages" select="d:Text/@xml:lang"/>
        <xsl:variable name="prefixAdded" select="not(descendant::xhtml:p) and not(descendant::xhtml:span)" as="xs:boolean"/>
        <xsl:copy>
            <xsl:if test="$prefixAdded">
                <xsl:for-each select="$languages">
                    <d:Text xml:lang="{.}"><xsl:value-of select="$prefix"/></d:Text>     
                </xsl:for-each>
            </xsl:if>
            <xsl:apply-templates select="node() | @*" mode="modif-title">
                <xsl:with-param name="prefix" select="if($prefixAdded) then '' else $prefix" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template used to get recursively the sequence's number.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence" mode="calculate-number">

        <xsl:number count="d:Sequence[d:TypeOfSequence/text() = 'module']" level="any"
            format="{$style/Sequence/StyleNumSeq}"
            from="d:Sequence[d:TypeOfSequence/text() = 'template']"/>

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
