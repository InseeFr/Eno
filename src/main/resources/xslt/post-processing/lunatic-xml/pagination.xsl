<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enolunatic="http://xml.insee.fr/apps/eno/out/js"
    xmlns:h="http://xml.insee.fr/schema/applis/lunatic-h"
    xmlns="http://xml.insee.fr/schema/applis/lunatic-h"
    exclude-result-prefixes="xs fn xd eno enolunatic h" version="2.0">
    
    <xsl:output indent="yes"/>
    
    <xsl:param name="properties-file"/>
    <xsl:param name="parameters-file"/>
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty/>
    </xsl:param>
    
    <xsl:variable name="properties" select="doc($properties-file)"/>
    <xsl:variable name="parameters">
        <xsl:choose>
            <xsl:when test="$parameters-node/*">
                <xsl:copy-of select="$parameters-node"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="doc($parameters-file)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="NONE" select="'none'"/>
    <xsl:variable name="SEQUENCE" select="'sequence'"/>
    <xsl:variable name="SUBSEQUENCE" select="'subsequence'"/>
    <xsl:variable name="QUESTION" select="'question'"/>
    
    <xsl:variable name="pagination">
        <xsl:choose>
            <xsl:when test="$parameters//Pagination != ''">
                <xsl:value-of select="$parameters//Pagination" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//Pagination" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>An xslt stylesheet who transforms an input into js through generic driver templates.</xd:p>
            <xd:p>The real input is mapped with the drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:variable name="root" select="root(.)"/>
    
    <xsl:template match="@*|node()" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="#current"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:Questionnaire">
        <xsl:choose>
            <xsl:when test="$pagination = $SEQUENCE">
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:attribute name="pagination" select="$pagination"/>
                    <xsl:attribute name="maxPage" select="count(child::h:components[@componentType='Sequence' or @componentType='Loop'])"/>
                    <xsl:apply-templates mode="sequence"/>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="$pagination = $QUESTION">
                <!-- Remove subsequence with no declaration -->
                <xsl:variable name="step1">
                    <xsl:copy>
                        <xsl:copy-of select="@*"/>
                        <xsl:apply-templates mode="clean-subsequence"/>
                    </xsl:copy>
                </xsl:variable>
                <!-- Numbering pages -->
                <xsl:variable name="step2">
                    <xsl:apply-templates mode="question" select="$step1"/>
                </xsl:variable>
                <!-- Replacing subsequence pages for "lost" components -->
                <xsl:apply-templates mode="replacing-subseq-page" select="$step2"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="$pagination = $SUBSEQUENCE">
                    <xsl:message><xsl:value-of select="concat('Pagination : ',$pagination,' is not yet implemented.')"/></xsl:message>
                </xsl:if>
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:attribute name="pagination" select="$pagination"/>
                    <xsl:apply-templates/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
    
    
    <!-- sequence pagination -->
    <xsl:template match="h:components[@componentType='Loop']" mode="sequence">
        <xsl:param name="loopPage" tunnel="yes"/>
        <xsl:param name="sequencePage" tunnel="yes"/>
        <xsl:variable name="parent" select=".."/>
        <xsl:variable name="firstComponentId" select="$parent/h:components[1]/@id"/>
        <xsl:variable name="page">
            <xsl:variable name="newPage">
                <xsl:choose>
                    <xsl:when test="$parent/@componentType='Sequence'">
                        <xsl:value-of select="$sequencePage"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:number count="h:components[@componentType='Sequence' or @componentType='Loop'][parent::h:*[@id = $parent/@id]]"
                            level="any" format="1"
                            from="h:components[@id = $firstComponentId]"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="$loopPage != ''"><xsl:value-of select="concat($loopPage,'.',$newPage)"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$newPage"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="page" select="$page"/>            
            <xsl:if test="not($parent/@componentType='Sequence')">                
                <xsl:attribute name="maxPage" select="count(child::h:components[@componentType='Sequence' or @componentType='Loop'])"/>
            </xsl:if>
            <xsl:attribute name="paginatedLoop" select="not($parent/@componentType='Sequence')"/>
            <xsl:apply-templates mode="#current">
                <xsl:with-param name="loopPage" select="$page" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:components[@componentType='Sequence']" mode="sequence">
        <xsl:param name="loopPage" tunnel="yes"/>
        <xsl:variable name="parent" select=".."/>
        <xsl:variable name="firstComponentId" select="$parent/h:components[1]/@id"/>
        <xsl:variable name="page">
            <xsl:variable name="newPage">
                <xsl:number count="h:components[@componentType='Sequence' or @componentType='Loop'][parent::h:*[@id = $parent/@id]]" 
                    level="any" format="1"
                    from="h:components[@id = $firstComponentId]"/>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="$loopPage != ''"><xsl:value-of select="concat($loopPage,'.',$newPage)"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$newPage"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="page" select="$page"/>
            <xsl:apply-templates mode="#current">
                <xsl:with-param name="sequencePage" select="$page" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:components[not(@componentType=('Sequence','Loop'))]" mode="sequence">
        <xsl:param name="sequencePage" tunnel="yes"/>
        <xsl:variable name="page" select="$sequencePage"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="page" select="$page"/>
            <xsl:choose>
                <xsl:when test="@componentType='Subsequence'">
                    <xsl:apply-templates mode="#current">
                        <xsl:with-param name="subSequencePage" select="$sequencePage" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise><xsl:apply-templates mode="#current"/></xsl:otherwise>
            </xsl:choose>            
        </xsl:copy>
    </xsl:template>   
    
    
    <xsl:template match="h:components[@componentType='Loop']" mode="clean-subsequence">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="#current">
                <xsl:with-param name="isFromLinkedLoop" select="boolean(@iterations != '')" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <!-- This template "delete" subsequence without declaration -->
    <xsl:template match="h:components[@componentType='Subsequence']" mode="clean-subsequence">
        <xsl:param name="isFromLinkedLoop" select="true()" as="xs:boolean" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="h:declarations or not($isFromLinkedLoop)">
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates mode="#current"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates mode="#current" select="h:components"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- question pagination -->
    
    <xsl:template match="h:Questionnaire" mode="question">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="pagination" select="$pagination"/>
            <xsl:attribute name="maxPage" select="count(descendant::h:components[not(ancestor::h:components[@componentType='Loop'])][@componentType!='FilterDescription'])"/>
            <xsl:apply-templates mode="question"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:components[@componentType='Loop']" mode="question">
        <xsl:param name="loopPage" tunnel="yes"/>
        <xsl:variable name="container" select="ancestor::h:*[@componentType='Loop' or local-name(.)='Questionnaire'][1]"/>
        <xsl:variable name="firstComponentId" select="$container/h:components[1]/@id"/>
        <xsl:variable name="page">
            <xsl:variable name="newPage">
                <xsl:number count="h:components[@componentType!='FilterDescription'][ancestor::h:*[@componentType='Loop' or local-name(.)='Questionnaire'][1]/@id = $container/@id]"
                    level="any" format="1"
                    from="h:components[@id = $firstComponentId]"/>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="$loopPage != ''"><xsl:value-of select="concat($loopPage,'.',$newPage)"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$newPage"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="isLinkedLoop" select="boolean(@iterations != '')"/>
        
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="page" select="$page"/>            
            <xsl:if test="$isLinkedLoop">                
                <xsl:attribute name="maxPage" select="count(descendant::h:components[@componentType!='FilterDescription'])"/>
            </xsl:if>
            <xsl:attribute name="paginatedLoop" select="$isLinkedLoop"/>
            <xsl:apply-templates mode="#current">
                <xsl:with-param name="container" select="." tunnel="yes"/>
                <xsl:with-param name="loopPage" select="$page" tunnel="yes"/>
                <xsl:with-param name="isFromLinkedLoop" select="$isLinkedLoop" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:components[not(@componentType='Loop')]" mode="question">
        <xsl:param name="loopPage" tunnel="yes"/>
        <xsl:param name="isFromLinkedLoop" as="xs:boolean" select="true()" tunnel="yes"/>
        <xsl:variable name="container" select="ancestor::h:*[@componentType='Loop' or local-name(.)='Questionnaire'][1]"/>
        <xsl:variable name="firstComponentId" select="$container/h:components[1]/@id"/>
        <xsl:variable name="page">
            <xsl:variable name="newPage">
                <xsl:number count="h:components[@componentType!='FilterDescription'][ancestor::h:*[@componentType='Loop' or local-name(.)='Questionnaire'][1]/@id = $container/@id]"
                    level="any" format="1"
                    from="h:components[@id = $firstComponentId]"/>
            </xsl:variable>            
            <xsl:choose>
                <xsl:when test="$loopPage != '' and $isFromLinkedLoop"><xsl:value-of select="concat($loopPage,'.',$newPage)"/></xsl:when>
                <xsl:when test="$loopPage != '' and not($isFromLinkedLoop)"><xsl:value-of select="$loopPage"/></xsl:when>                
                <xsl:otherwise><xsl:value-of select="$newPage"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="isLostComponentOfSubSeq" select="not(@componentType=('Sequence','Subsequence')) and h:hierarchy/h:subSequence and not(../@componentType='Subsequence')" as="xs:boolean" />
                
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="page" select="$page"/>
            <xsl:choose>
                <xsl:when test="@componentType='Sequence'">
                    <xsl:apply-templates mode="#current">
                        <xsl:with-param name="sequencePage" select="$page" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:when test="@componentType='Subsequence'">
                    <xsl:apply-templates mode="#current">
                        <xsl:with-param name="subSequencePage" select="$page" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:when test="$isLostComponentOfSubSeq">
                    <xsl:variable name="subSequencePage">
                        <xsl:variable name="subsequenceId" select="h:hierarchy/h:subSequence/@id"/>
                        <xsl:variable name="firstComponentOfSubsequence" select="../h:components[h:hierarchy/h:subSequence/@id = $subsequenceId][1]"/>
                        <xsl:value-of select="concat('¤',$firstComponentOfSubsequence/@id,'¤')"/>
                    </xsl:variable>
                    <xsl:apply-templates mode="#current">
                        <xsl:with-param name="subSequencePage" select="$subSequencePage" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates mode="#current"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
        
    <xsl:template match="h:sequence | h:subSequence" mode="question sequence">
        <xsl:param name="sequencePage" tunnel="yes"/>
        <xsl:param name="subSequencePage" tunnel="yes"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="page" select="if(self::h:sequence) then $sequencePage else $subSequencePage"/>
            <xsl:apply-templates mode="#current"/>
        </xsl:copy>
    </xsl:template>
        
    <xsl:template match="h:subSequence" mode="replacing-subseq-page">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="page">
                <xsl:choose>
                    <xsl:when test="starts-with(@page,'¤') and ends-with(@page,'¤')">
                        <xsl:variable name="id" select="substring(substring-after(@page,'¤'),0,string-length(@page)-1)"/>
                        <xsl:value-of select="../../../h:components[@id=$id]/@page"/>
                    </xsl:when>
                    <xsl:otherwise><xsl:value-of select="@page"/></xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates mode="#current"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>