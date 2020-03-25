<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:a="ddi:archive:3_3"
    xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3"
    xmlns:l="ddi:logicalproduct:3_3"
    xmlns:g="ddi:group:3_3"
    xmlns:s="ddi:studyunit:3_3"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:pr="ddi:ddiprofile:3_3"
    xmlns:c="ddi:conceptualcomponent:3_3"
    xmlns:cm="ddi:comparative:3_3"
    xmlns:ddi-instance="ddi:instance:3_3"
    xmlns:dereferencing="dereferencing"
    xmlns="ddi:instance:3_3"
    exclude-result-prefixes="xd"
    version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The output folder in which the dereferenced files (one for each main sequence) are generated.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="output-folder"/>
    
    <xsl:variable name="root" select="."/>
        
    <xd:doc>
        <xd:desc>Root template</xd:desc>
    </xd:doc>
    
    <xsl:template match="/">
        <xsl:for-each select="ddi-instance:DDIInstance/s:StudyUnit/d:DataCollection/d:InstrumentScheme/d:Instrument">
            <xsl:variable name="form-name">
                <xsl:choose>
                    <xsl:when test="d:InstrumentName">
                        <xsl:value-of select="d:InstrumentName/r:String"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="replace(r:ID/text(), concat(replace(//s:StudyUnit/r:ID/text(), '-SU', ''),'-In-'), '')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:result-document href="{lower-case(concat('file:///',replace($output-folder, '\\' , '/'),'/',$form-name,'.xml'))}">
                <DDIInstance xmlns="ddi:instance:3_3"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns:a="ddi:archive:3_3"
                    xmlns:eno="http://xml.insee.fr/apps/eno"
                    xmlns:enoddi33="http://xml.insee.fr/apps/eno/out/ddi33"
                    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
                    xmlns:pr="ddi:ddiprofile:3_3"
                    xmlns:c="ddi:conceptualcomponent:3_3"
                    xmlns:cm="ddi:comparative:3_3">                    
                    <g:ResourcePackage>
                        <xsl:apply-templates select="$root//g:ResourcePackage/*" mode="output-DDI"/>
                    </g:ResourcePackage>
                    <s:StudyUnit>
                        <xsl:apply-templates select="$root//s:StudyUnit/*" mode="output-DDI">
                            <xsl:with-param name="form-id" select="r:ID" tunnel="yes"/>
                        </xsl:apply-templates>
                    </s:StudyUnit>
                </DDIInstance>
            </xsl:result-document>
        </xsl:for-each>
        
    </xsl:template>
    
    <xd:doc>
        <xd:desc>Default template : identity template.</xd:desc>
    </xd:doc>
    <xsl:template match="@* | node()" mode="output-DDI">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()" mode="output-DDI"/>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Not to dereference templates.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Instrument"  mode="output-DDI" priority="1">
        <xsl:param name="form-id" tunnel="yes"/>
        
        <xsl:if test="r:ID=$form-id">
            <xsl:copy>
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates select="*" mode="output-DDI"/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>

