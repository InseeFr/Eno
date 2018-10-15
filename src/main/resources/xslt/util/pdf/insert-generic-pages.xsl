<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:strip-space elements="*"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The properties file used by the stylesheet.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="survey-name"/>
    <xsl:param name="form-name"/>
    <xsl:param name="parameters-file"/>
    <xsl:param name="properties-file"/>
    <xsl:param name="parameters-node" as="node()" select="node()"/>

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
    <xsl:variable name="homepage-folder">
        <xsl:choose>
            <xsl:when test="$parameters//HomePage/Folder != ''">
                <xsl:value-of select="$parameters//HomePage/Folder"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//HomePage/Folder"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="homepage-file">
        <xsl:choose>
            <xsl:when test="$parameters//HomePage/File != ''">
                <xsl:value-of select="$parameters//HomePage/File"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//HomePage/File"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="static-pages-adress" select="concat('../../../',$homepage-folder,'/',$homepage-file)"/>
    <xsl:variable name="static-pages" select="doc($static-pages-adress)"/>
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
        <xsl:for-each select="$parameters//AccompanyingMail">
            <xsl:result-document href="../../courrier_type_{replace(replace(concat($survey-name,$form-name),'-',''),'_','')}{.}.fo">
                <xsl:apply-templates select="$root/*" mode="keep-cdata">
                    <xsl:with-param name="accompanying-mail" select="." tunnel="yes"/>
                </xsl:apply-templates>                
            </xsl:result-document>
        </xsl:for-each>
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
    
    <xd:doc>
        <xd:desc>
            <xd:p>add accompanying mail and cover page.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="fo:root/fo:layout-master-set" mode="#all">
        <xsl:param name="accompanying-mail" tunnel="yes"/>
        
        <xsl:variable name="cover-name">
            <xsl:choose>
                <xsl:when test="$orientation = '0'">
                    <xsl:value-of select="'Cover-A4'"/>        
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat('Cover-A4-',$orientation)"/>
                </xsl:otherwise>
            </xsl:choose>            
        </xsl:variable>
        
        <xsl:copy>
            <xsl:copy-of select="$static-pages//fo:page-sequence-master[@master-name=$accompanying-mail]"/>
            <xsl:copy-of select="$static-pages//fo:simple-page-master[@master-name=concat($accompanying-mail,'-recto')]"/>
            <xsl:copy-of select="$static-pages//fo:simple-page-master[@master-name=concat($accompanying-mail,'-verso')]"/>
            <xsl:copy-of select="$static-pages//fo:simple-page-master[@master-name=$cover-name]"/>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
        <xsl:apply-templates select="$static-pages//fo:page-sequence[@master-reference=$accompanying-mail]" mode="keep-cdata"/>
        <xsl:apply-templates select="$static-pages//fo:page-sequence[@master-reference=$cover-name]" mode="keep-cdata"/>
    </xsl:template>

    <xsl:template match="text()" mode="keep-cdata" priority="2">
        <xsl:value-of select="replace(.,'&amp;','&amp;amp;')" disable-output-escaping="yes"/>
    </xsl:template>
    
    <xsl:template match="fo:block[@id='TheVeryLastPage']" mode="#all">
        <xsl:if test="$parameters//ColtraneQuestions/*[name()='TypeRepondantLabel' or name()='Fin']">
            <xsl:copy-of select="$static-pages//fo:page-sequence[@master-reference='questions-fin']/fo:flow[@flow-name='xsl-region-body']/*"/>
        </xsl:if>
        <xsl:copy-of select="."/>
    </xsl:template>
    
</xsl:stylesheet>