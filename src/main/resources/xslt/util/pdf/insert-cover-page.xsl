<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" 
    xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
    xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
    exclude-result-prefixes="xd eno enopdf"
    version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>This stylesheet inserts the accompanying mails and the first page according to the parameters</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The properties file used by the stylesheet.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="form-name" select="//fo:title/text()"/>
    <xsl:param name="parameters-file"/>
    <xsl:param name="properties-file"/>
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty/>
    </xsl:param>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The properties and parameters files are charged as xml trees.</xd:p>
        </xd:desc>
    </xd:doc>   
    <xsl:variable name="properties" select="document($properties-file)"/>
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
    
    <xsl:variable name="studyUnit">
        <xsl:choose>
            <xsl:when test="$parameters//StudyUnit != ''">
                <xsl:value-of select="$parameters//StudyUnit"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//StudyUnit"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="firstPage-folder">
        <xsl:choose>
            <xsl:when test="$parameters//FirstPage/Folder != ''">
                <xsl:value-of select="$parameters//FirstPage/Folder"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//FirstPage/Folder"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="firstPage-file">
        <xsl:value-of select="concat('page-first-',$studyUnit,'.fo')"/>
    </xsl:variable>
    
    <xsl:variable name="firstPage-adress" select="concat('../../../',$firstPage-folder,'/',$firstPage-file)"/>
    <xsl:variable name="first-page" select="doc($firstPage-adress)"/>
    
    <xsl:variable name="orientation">
        <xsl:choose>
            <xsl:when test="$parameters//Format/Orientation != ''">
                <xsl:value-of select="$parameters//Format/Orientation"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//Format/Orientation"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Root template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:variable name="root" select="."/>
        <xsl:apply-templates select="*" mode="#default"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply coying to the output file.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fo:block[@id='survey-name' and ancestor::fo:page-sequence[@master-reference='page-first-default']]" mode="keep-cdata">
        <xsl:if test="$studyUnit='default'">
            <xsl:copy>
                <xsl:copy-of select="@*"/>
                <xsl:value-of select="$form-name"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>
    
    
    <xd:doc>
        <xd:desc>
            <xd:p>add accompanying mail and cover page.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="fo:root/fo:layout-master-set" mode="#all">
        
        <xsl:variable name="page-first-name">
            <xsl:choose>
                <xsl:when test="$orientation = '0'">
                    <xsl:value-of select="replace($firstPage-file,'.fo','')"/>        
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat(replace($firstPage-file,'.fo',''),'-',$orientation)"/>
                </xsl:otherwise>
            </xsl:choose>            
        </xsl:variable>
        
        <xsl:copy>
            <xsl:copy-of select="$first-page//fo:simple-page-master[@master-name=$page-first-name]"/>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
        <xsl:apply-templates select="$first-page//fo:page-sequence[@master-reference=$page-first-name]" mode="keep-cdata"/>
        
    </xsl:template>
    
    <xsl:template match="text()" mode="keep-cdata" priority="2">
        <xsl:value-of select="replace(.,'&amp;','&amp;amp;')" disable-output-escaping="yes"/>
    </xsl:template>
</xsl:stylesheet>