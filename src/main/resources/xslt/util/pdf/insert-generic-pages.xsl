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
    <xsl:variable name="parameters" select="document($parameters-file)"/>
    <xsl:variable name="properties" select="document($properties-file)"/>
   
    <!--<xsl:variable name="static-pages" select="document('static-pages.fo')"/>-->
    
    <xd:doc>
        <xd:desc>
            <xd:p>Root template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:variable name="root" select="."/>
        <xsl:apply-templates select="*" mode="#default"/>
        <xsl:for-each select="$parameters//AccompanyingMail">
            <xsl:result-document href="../../courrier_type_{replace($survey-name,'-','')}{$form-name}{.}.fo">
                <xsl:apply-templates select="$root/*">
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
    <xsl:template match="fo:root/fo:layout-master-set">
        <xsl:param name="accompanying-mail" tunnel="yes"/>
        
        <xsl:variable name="static-pages-adress" select="concat('../../../',$properties//HomePage/Folder,'/',$parameters//HomePage/File)"/>
        <xsl:variable name="static-pages" select="doc($static-pages-adress)"/>
        
        <xsl:copy>
            <xsl:copy-of select="$static-pages//fo:page-sequence-master[@master-name=$accompanying-mail]"/>
            <xsl:copy-of select="$static-pages//fo:simple-page-master[@master-name=concat($accompanying-mail,'-recto')]"/>
            <xsl:copy-of select="$static-pages//fo:simple-page-master[@master-name=concat($accompanying-mail,'-verso')]"/>
            <xsl:copy-of select="$static-pages//fo:simple-page-master[@master-name='Cover-A4']"/>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
        
        <xsl:apply-templates select="$static-pages//fo:page-sequence[@master-reference=$accompanying-mail]" mode="keep-cdata"/>
        <xsl:apply-templates select="$static-pages//fo:page-sequence[@master-reference='Cover-A4']" mode="keep-cdata"/>
    </xsl:template>

    <xsl:template match="text()" mode="keep-cdata" priority="2">
        <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    
</xsl:stylesheet>