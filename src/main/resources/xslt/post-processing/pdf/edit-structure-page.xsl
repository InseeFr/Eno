<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
    xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
    exclude-result-prefixes="xd eno enopdf fox"
    version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>This stylesheet edits the structure of the page.</xd:p>
            <xd:p>Page-odd, page-even, the orientation (portrait:0 or landscape:90)</xd:p>
            <xd:p>This stylesheet edits the structure of the page.</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The properties file used by the stylesheet.</xd:p>
            <xd:p>It's on a transformation level.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="properties-file"/>
    <xsl:param name="parameters-file"/>
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty/>
    </xsl:param>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The properties and parameters files are charged as xml trees.</xd:p>
        </xd:desc>
    </xd:doc>
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
    
    <xd:doc>
        <xd:desc>Variables from propertiers and parameters</xd:desc>
    </xd:doc>
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
    <xsl:variable name="column-count">
        <xsl:choose>
            <xsl:when test="$parameters//Format/Columns != ''">
                <xsl:value-of select="$parameters//Format/Columns"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//Format/Columns"/>
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
    <xsl:variable name="page-model-folder">
        <xsl:value-of select="$properties//PageModel/Folder"/>
    </xsl:variable>
    <xsl:variable name="page-model-file-adress">
        <xsl:choose>
            <xsl:when test="$studyUnit='business' and $orientation='90'">
                <xsl:value-of select="concat('../../../',$page-model-folder,'/page-model-esa.fo')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat('../../../',$page-model-folder,'/page-model-',$studyUnit,'.fo')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="page-model" select="doc($page-model-file-adress)"/>

    <xsl:variable name="questionnaire-flow" select="root(.)//fo:flow"/>
    <xsl:variable name="questionnaire-title" select="root(.)//fo:title"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="$page-model//fo:root" mode="display"/>
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
    
    <!-- Surcharge referenceorientation and column-count by parameters -->
    <xsl:template match="@reference-orientation[parent::fo:simple-page-master]" mode="display">
        <xsl:attribute name="reference-orientation">
            <xsl:value-of select="$orientation"/>
        </xsl:attribute>
    </xsl:template>
    <xsl:template match="@column-count[parent::fo:region-body]" mode="display">
        <xsl:attribute name="column-count">
            <xsl:value-of select="$column-count"/>
        </xsl:attribute>
    </xsl:template>
    
    <xsl:template match="fo:flow" mode="display">
        <xsl:copy-of select="$questionnaire-flow"/>
    </xsl:template>
    
    <xsl:template match="fo:title" mode="display">
        <xsl:copy-of select="$questionnaire-title"/>
    </xsl:template>
    
</xsl:stylesheet>