<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xd xsl"
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
    <xsl:param name="survey-name"/>
    <xsl:param name="form-name"/>
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
    
    
    <xsl:variable name="accompanying-mails-folder">
        <xsl:value-of select="$properties//AccompanyingMails/Folder"/>
    </xsl:variable>
    
    
    <xd:doc>
        <xd:desc>
            <xd:p>Root template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:variable name="AccompanyingMail" select="$parameters//AccompanyingMail"/>
        <xsl:variable name="accompanying-mails-adress" select="concat('../../../',$accompanying-mails-folder,'/',$AccompanyingMail,'.fo')"/>
        <xsl:variable name="accompanying-mails-page">
            <xsl:choose>
                <xsl:when test="$AccompanyingMail!=''">
                    <xsl:copy-of select="doc($accompanying-mails-adress)"/>
                </xsl:when>
                <xsl:otherwise>
                    <Empty/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:apply-templates select="./*" mode="keep-cdata">
            <xsl:with-param name="accompanying-mails-page" select="$accompanying-mails-page" as="node()" tunnel="yes"/>
            <xsl:with-param name="accompanying-mail" select="$AccompanyingMail" tunnel="yes"/>
        </xsl:apply-templates>
        
        
        
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>add accompanying mail and cover page.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="fo:root/fo:layout-master-set" mode="#all">
        <xsl:param name="accompanying-mails-page" as="node()" tunnel="yes">
            <Empty/>
        </xsl:param>
        <xsl:param name="accompanying-mail" tunnel="yes" required="no"/>
        <xsl:copy>
            <xsl:copy-of select="$accompanying-mails-page//fo:page-sequence-master[@master-name=$accompanying-mail]"/>
            <xsl:copy-of select="$accompanying-mails-page//fo:simple-page-master[@master-name=concat($accompanying-mail,'-recto')]"/>
            <xsl:copy-of select="$accompanying-mails-page//fo:simple-page-master[@master-name=concat($accompanying-mail,'-verso')]"/>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
        <xsl:apply-templates select="$accompanying-mails-page//fo:page-sequence[@master-reference=$accompanying-mail]" mode="keep-cdata"/>
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
    
    <xsl:template match="text()" mode="keep-cdata" priority="2">
        <xsl:value-of select="replace(.,'&amp;','&amp;amp;')" disable-output-escaping="yes"/>
    </xsl:template>
</xsl:stylesheet>