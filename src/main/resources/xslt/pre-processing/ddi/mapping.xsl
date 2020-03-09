<?xml version="1.0" encoding="utf-8" ?>
<xsl:transform version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xhtml="http://www.w3.org/1999/xhtml" 
    xmlns:d="ddi:datacollection:3_3" xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3" xmlns:g="ddi:group:3_3" xmlns:s="ddi:studyunit:3_3" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" />
    <xsl:strip-space elements="*" />

    <!-- As a parameter the parameter file which contains the requirement information of the standard Coltrane variables. -->
    <xsl:param name="parameters-file" />
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <xsl:variable name="parameters">
        <xsl:choose>
            <xsl:when test="$parameters-node/*">
                <xsl:copy-of select="$parameters-node" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="doc($parameters-file)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="studyUnit" select="$parameters//StudyUnit" />

    <xsl:variable name="begin-questions-identification" as="xs:boolean" select="$parameters//BeginQuestion/Identification" />
    <xsl:variable name="end-response-time-question" as="xs:boolean" select="$parameters//EndQuestion/ResponseTimeQuestion" />
    <xsl:variable name="end-comment-question" as="xs:boolean" select="$parameters//EndQuestion/CommentQuestion" />

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Root Template, we apply the templates of all children</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <mapping>
            <xsl:if test="$begin-questions-identification">
                <Variable name="COMMENT_UE" type="Collected" />
            </xsl:if>
            <xsl:apply-templates select="//l:VariableScheme" />
            <xsl:if test="$end-response-time-question">
                <Variable name="HEURE_REMPL" type="Collected" />
                <Variable name="MIN_REMPL" type="Collected" />
            </xsl:if>
            <xsl:if test="$end-comment-question">
                <Variable name="COMMENT_QE" type="Collected" />
            </xsl:if>
        </mapping>
    </xsl:template>

    <xsl:template match="*">
        <xsl:apply-templates select="node()" />
    </xsl:template>

    <xsl:template match="text()" />

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>We do the mapping VariableScheme identifier / QuestionScheme identifier</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Variable">
        <Variable name="{l:VariableName/r:String/text()}">
            <xsl:attribute name="type">
                <xsl:choose>
                    <xsl:when test="r:SourceParameterReference and r:QuestionReference">
                        <xsl:value-of select="'Collected'" />
                    </xsl:when>
                    <xsl:when test="descendant::r:ProcessingInstructionReference">
                        <xsl:value-of select="'Calculated'" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'External'" />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </Variable>
    </xsl:template>

    <xsl:template match="l:VariableGroup">
        <Group name="{l:VariableGroupName/r:String/text()}" type="l:TypeOfVariableGroup">
            <xsl:apply-templates select="node()" />
        </Group>
    </xsl:template>

</xsl:transform>