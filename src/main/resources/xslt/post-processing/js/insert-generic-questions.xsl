<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:fn="http://www.w3.org/2005/xpath-functions" 
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno" 
    xmlns:enojs="http://xml.insee.fr/apps/eno/out/js"
    xmlns:h="http://xml.insee.fr/schema/applis/lunatic-h"
    xmlns="http://xml.insee.fr/schema/applis/lunatic-h"
    exclude-result-prefixes="xs fn xd eno enojs h" version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8" />
    
    <xsl:param name="properties-file" />
    <xsl:param name="parameters-file" />
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty />
    </xsl:param>
    
    <xsl:variable name="business" select="'business'" />
    <xsl:variable name="household" select="'household'" />
    <xsl:variable name="default" select="'default'" />
    
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
    
    <xsl:variable name="begin-questions-identification" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//BeginQuestion/Identification != ''">
                <xsl:value-of select="$parameters//BeginQuestion/Identification" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//BeginQuestion/Identification" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable> 
    <xsl:variable name="end-response-time-question" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//EndQuestion/ResponseTimeQuestion != ''">
                <xsl:value-of select="$parameters//EndQuestion/ResponseTimeQuestion" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//EndQuestion/ResponseTimeQuestion" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="end-comment-question" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//EndQuestion/CommentQuestion != ''">
                <xsl:value-of select="$parameters//EndQuestion/CommentQuestion" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//EndQuestion/CommentQuestion" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="begin-question-folder" select="$properties//BeginQuestion/Folder" />
    <xsl:variable name="begin-question-adress" select="concat('../../../',$begin-question-folder,'/begin-question.xml')" />
    <xsl:variable name="begin-question" select="doc($begin-question-adress)" />
    
    <xsl:variable name="end-question-folder" select="$properties//EndQuestion/Folder" />
    <xsl:variable name="end-question-file">
        <xsl:value-of select="concat('end-question-',$studyUnit,'.xml')" />
    </xsl:variable>
    <xsl:variable name="end-question-adress" select="concat('../../../',$end-question-folder,'/',$end-question-file)" />
    <xsl:variable name="end-question" select="doc($end-question-adress)" />
    
    <xsl:template match="/">
        <xsl:apply-templates select="*" />
    </xsl:template>
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de base pour tous les éléments et tous les attributs, on recopie
                simplement en sortie</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>
    
    
    
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add qeneric questions to the end</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="h:components[@componentType='Sequence'][1]">
        
        <xsl:choose>
            <xsl:when test="$studyUnit=$business and $begin-questions-identification">
                <xsl:copy-of select="$begin-question//*[@id='BEGIN-QUESTION-SEQ']"/>
            </xsl:when>
            <xsl:when test="$studyUnit=$household"/>
            <xsl:when test="$studyUnit=$default"/>
        </xsl:choose>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>
    
    
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add qeneric questions to the end</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="h:components[@componentType='Sequence'][last()]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
        <xsl:choose>
            <xsl:when test="$studyUnit=$business">
                <xsl:choose>
                    <xsl:when test="$end-response-time-question and $end-comment-question">
                        <xsl:copy-of select="$end-question//*[@id='TIME-COMMENT-SEQ']"/>
                    </xsl:when>
                    <xsl:when test="$end-comment-question">
                        <xsl:copy-of select="$end-question//*[@id='COMMENT-SEQ']"/>
                    </xsl:when>
                    <xsl:when test="$end-response-time-question">
                        <xsl:copy-of select="$end-question//*[@id='TIME-SEQ']"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$studyUnit=$household">
                <xsl:choose>
                    <xsl:when test="$end-response-time-question and $end-comment-question">
                        <xsl:copy-of select="$end-question//*[@id='TIME-COMMENT-SEQ']"/>
                    </xsl:when>
                    <xsl:when test="$end-comment-question">
                        <xsl:copy-of select="$end-question//*[@id='COMMENT-SEQ']"/>
                    </xsl:when>
                    <xsl:when test="$end-response-time-question">
                        <xsl:copy-of select="$end-question//*[@id='TIME-SEQ']"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$studyUnit=$default">
                <xsl:choose>
                    <xsl:when test="$end-response-time-question and $end-comment-question">
                        <xsl:copy-of select="$end-question//*[@id='TIME-COMMENT-SEQ']"/>
                    </xsl:when>
                    <xsl:when test="$end-comment-question">
                        <xsl:copy-of select="$end-question//*[@id='COMMENT-SEQ']"/>
                    </xsl:when>
                    <xsl:when test="$end-response-time-question">
                        <xsl:copy-of select="$end-question//*[@id='TIME-SEQ']"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    
    
</xsl:stylesheet>