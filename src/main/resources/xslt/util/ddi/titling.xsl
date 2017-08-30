<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:d="ddi:datacollection:3_2" xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2"
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
    <!--<xsl:param name="parameters-file"/>-->

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:strip-space elements="*"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The involved parameters are charged as an xml tree.</xd:p>
        </xd:desc>
    </xd:doc>
    <!--<xsl:variable name="style">
        <xsl:copy-of select="document($parameters-file)/Parameters/Title"/>
    </xsl:variable>-->
    <xsl:variable name="style">
        <xsl:copy-of select="document('title-parameters.xml')/Parameters/Title"/>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:param name="ObjectTitle"/>
        <xsl:param name="num"/>
        <xsl:param name="textConstruct"/>
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, getting to the
                child.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*" mode="#all">
        <xsl:param name="ObjectTitle"/>
        <xsl:param name="num"/>
        <xsl:param name="textConstruct"/>
        <xsl:copy>
            <xsl:copy-of select="./@*"/>
            <xsl:call-template name="CreatConstructName">
                <xsl:with-param name="textConstruct" select="$textConstruct"/>
            </xsl:call-template>
            <xsl:apply-templates select="node()" >
                <xsl:with-param name="ObjectTitle" select="$ObjectTitle"/>
                <xsl:with-param name="num" select="$num"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template used to add numbers to objects.</xd:p>
            <xd:p>For every 'module', 'submodule' or 'group' type d:Sequence, prefixing the
                title.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[d:ControlConstructReference]">
        <xsl:param name="ObjectTitle"/>
        <xsl:param name="num"/>
        <xsl:param name="textConstruct"/>
        <xsl:copy>
            <xsl:apply-templates select="./*[name(.) != 'd:ControlConstructReference']"
                >
                <xsl:with-param name="ObjectTitle" select="$ObjectTitle"/>
                <xsl:with-param name="num" select="$num"/>
            </xsl:apply-templates>
            <xsl:call-template name="CreatConstructName">
                <xsl:with-param name="textConstruct" select="$textConstruct"/>
            </xsl:call-template>

            <xsl:call-template name="CopyCCR">
                <xsl:with-param name="ObjectTitle" select="$ObjectTitle"/>
                <xsl:with-param name="num" select="1"/>
                <xsl:with-param name="num-max" select="count(d:ControlConstructReference)"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template used to add numbers to objects.</xd:p>
            <xd:p>For every 'module', 'submodule' or 'group' type d:Sequence, prefixing the
                title.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="TitleAttribution">
        <!--<xsl:template match="d:ControlConstructReference/*[r:ID]">-->
        <xsl:param name="ObjectTitle"/>
        <xsl:param name="num"/>
        <xsl:param name="textConstruct"/>
        <d:ControlConstructReference>
            <xsl:copy-of select="d:ControlConstructReference[$num]/*[not(.[r:ID])]"/>
            <xsl:choose>
                <xsl:when test="d:ControlConstructReference[$num]/r:TypeOfObject = 'Sequence' and d:ControlConstructReference[$num]/d:Sequence/d:TypeOfSequence = 'template'">
                    <xsl:apply-templates select="d:ControlConstructReference[$num]/*[r:ID]" >
                        <xsl:with-param name="ObjectTitle" select="$ObjectTitle"/>
                        <xsl:with-param name="num" select="$num"/>
                    </xsl:apply-templates>
                </xsl:when>
                
                <xsl:when test="d:ControlConstructReference[$num]/r:TypeOfObject = 'Sequence' and d:ControlConstructReference[$num]/d:Sequence/d:TypeOfSequence != 'template'">
                    <xsl:variable name="level">
                        <xsl:value-of select="d:ControlConstructReference[$num]/*[r:ID]/d:TypeOfSequence"/>
                    </xsl:variable>
                    <xsl:variable name="seq-style" select="$style/Title/Sequence/Level[@name = $level]"/>
                    <xsl:variable name="NumFormated"><xsl:number value="$num" format="{$seq-style/StyleNumSeq}"></xsl:number></xsl:variable>
                    <xsl:variable name="plop">
                        <xsl:value-of select="concat($seq-style/PreSeq, $ObjectTitle, $seq-style/PostNumParentSeq,  $NumFormated, $seq-style/PostNumSeq)"/>
                    </xsl:variable>
                    <xsl:apply-templates select="d:ControlConstructReference[$num]/*[r:ID]"
                        >
                        <xsl:with-param name="ObjectTitle" select="$plop"/>
                        <xsl:with-param name="num" select="$num"/>
                        <xsl:with-param name="textConstruct" select="$plop"/>
                    </xsl:apply-templates>
                </xsl:when>
                
                <xsl:when test="d:ControlConstructReference[$num]/r:TypeOfObject = 'QuestionConstruct'">
                    <xsl:variable name="questtype" select="module"/>
                    <xsl:variable name="Quest-style" select="$style/Title/Question/Level[1]"/>
                    <xsl:variable name="NumFormated"><xsl:number value="$num" format="{$Quest-style/StyleNumQuest}"></xsl:number></xsl:variable>
                    <xsl:variable name="plop">
                        <xsl:value-of select="concat($Quest-style/PreQuest, $ObjectTitle, $Quest-style/PostNumParentQuest, $NumFormated, $Quest-style/PostNumQuest)"/>
                    </xsl:variable>
                    <xsl:apply-templates select="d:ControlConstructReference[$num]/*[r:ID]"
                        >
                        <xsl:with-param name="ObjectTitle" select="$plop"/>
                        <xsl:with-param name="num" select="$num"/>
                        <xsl:with-param name="textConstruct" select="$plop"/>
                    </xsl:apply-templates>
                </xsl:when>
                
                <xsl:otherwise>
                    <xsl:variable name="seq-style" select="$style/Title/Sequence/Level[4]"/>
                    <xsl:variable name="NumFormated"><xsl:number value="$num" format="{$seq-style/StyleNumSeq}"></xsl:number></xsl:variable>
                    <xsl:variable name="plop">
                        <xsl:value-of select="concat($seq-style/PreSeq, $ObjectTitle, $seq-style/PostNumParentSeq, $NumFormated, $seq-style/PostNumSeq)"/>
                    </xsl:variable>
                    <xsl:apply-templates select="d:ControlConstructReference[$num]/*[r:ID]" >
                        <xsl:with-param name="ObjectTitle" select="$plop"/>
                        <xsl:with-param name="num" select="$num"/>
                        <xsl:with-param name="textConstruct" select="$plop"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </d:ControlConstructReference>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>plop.</xd:p>
            <xd:p>plop.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="CopyCCR">
        <xsl:param name="ObjectTitle"/>
        <xsl:param name="num" as="xs:integer"/>
        <xsl:param name="textConstruct"/>
        <xsl:param name="num-max"/>
        <xsl:if test="$num &lt;= $num-max">            
            <xsl:call-template name="TitleAttribution">
                <xsl:with-param name="ObjectTitle" select="$ObjectTitle"/>
                <xsl:with-param name="num" select="$num"/>
            </xsl:call-template>
            
            <xsl:call-template name="CopyCCR">
                <xsl:with-param name="ObjectTitle" select="$ObjectTitle"/>
                <xsl:with-param name="num" select="$num + 1"/>
                <xsl:with-param name="textConstruct" select="$textConstruct"/>
                <xsl:with-param name="num-max" select="$num-max"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>plop.</xd:p>
            <xd:p>plop.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="CreatConstructName">
        <xsl:param name="textConstruct"/>
        <xsl:if test="$textConstruct">
            <r:ConstructName><xsl:value-of select="$textConstruct"/></r:ConstructName>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
