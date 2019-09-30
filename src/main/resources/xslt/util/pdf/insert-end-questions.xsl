<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions" exclude-result-prefixes="xd eno enopdf" version="2.0">

    <xsl:output method="xml" indent="yes" encoding="UTF-8" />
    <xsl:strip-space elements="*" />

    <xd:doc>
        <xd:desc>
            <xd:p>This stylesheet inserts the end questions according to the parameters</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>The properties file used by the stylesheet.</xd:p>
            <xd:p>It's on a transformation level.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="properties-file" />
    <xsl:param name="parameters-file" />
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <xd:doc>
        <xd:desc>
            <xd:p>The properties and parameters files are charged as xml trees.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="properties" select="doc($properties-file)" />
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

    <xsl:variable name="studyUnit">
        <xsl:choose>
            <xsl:when test="$parameters//StudyUnit != ''">
                <xsl:value-of select="$parameters//StudyUnit" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//StudyUnit" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="response-time-question" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//EndQuestion/ResponseTimeQuestion != ''">
                <xsl:value-of select="$parameters//EndQuestion/ResponseTimeQuestion" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//EndQuestion/ResponseTimeQuestion" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="comment-question" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//EndQuestion/CommentQuestion != ''">
                <xsl:value-of select="$parameters//EndQuestion/CommentQuestion" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//EndQuestion/CommentQuestion" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="end-question-folder">
        <xsl:choose>
            <xsl:when test="$parameters//EndQuestion/Folder != ''">
                <xsl:value-of select="$parameters//EndQuestion/Folder" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//EndQuestion/Folder" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="end-question-file">
        <xsl:value-of select="concat('end-question-',$studyUnit,'.fo')" />
    </xsl:variable>
    <xsl:variable name="end-question-adress" select="concat('../../../',$end-question-folder,'/',$end-question-file)" />
    <xsl:variable name="end-question" select="doc($end-question-adress)" />
    <xsl:variable name="orientation">
        <xsl:choose>
            <xsl:when test="$parameters//Format/Orientation != ''">
                <xsl:value-of select="$parameters//Format/Orientation" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//Format/Orientation" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>


    <xd:doc>
        <xd:desc>
            <xd:p>Root template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="*" mode="#default" />
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply coying to the output file.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="fo:block[@id='TheVeryLastPage']" mode="#all">
        <xsl:choose>
            <xsl:when test="$studyUnit='business'">
                <xsl:choose>
                    <xsl:when test="$comment-question and $response-time-question">
                        <xsl:copy-of select="$end-question//fo:page-sequence/fo:flow/*[not(@id='COMMENT_QUESTION_TITLE' or @id='TIME_QUESTION_TITLE')]" />
                    </xsl:when>
                    <xsl:when test="$comment-question">
                        <xsl:copy-of select="$end-question//fo:page-sequence/fo:flow/*[@id='COMMENT_QUESTION_TITLE' or @id='COMMENT_QUESTION']" />
                    </xsl:when>
                    <xsl:when test="$response-time-question">
                        <xsl:copy-of select="$end-question//fo:page-sequence/fo:flow/*[@id='TIME_QUESTION_TITLE' or @id='TIME_QUESTION']" />
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="$comment-question">
                    <xsl:copy-of select="$end-question//fo:page-sequence/fo:flow/*" />
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:copy-of select="." />
    </xsl:template>

</xsl:stylesheet>