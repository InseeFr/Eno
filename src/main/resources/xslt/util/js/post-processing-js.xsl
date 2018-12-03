<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:fn="http://www.w3.org/2005/xpath-functions" 
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno" 
    xmlns:enojs="http://xml.insee.fr/apps/eno/out/js"
    
    exclude-result-prefixes="xs fn xd eno enojs " version="2.0">	
    
    <xsl:output indent="yes"/>
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>An xslt stylesheet who transforms an input into js through generic driver templates.</xd:p>
            <xd:p>The real input is mapped with the drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Match on Form driver.</xd:p>
            <xd:p>It writes the root of the document with the main title.</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:template match="main">
        <xsl:variable name="idQuestionnaire" select="Questionnaire/@id"/>
        <Questionnaire 
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            id="{$idQuestionnaire}">
            <xsl:apply-templates select="Questionnaire"/>
            <xsl:apply-templates select="descendant::variable"/>
        </Questionnaire>
    </xsl:template>
    
    
    <xsl:template match="Questionnaire">
        <xsl:apply-templates select="*[not(self::variable)]"/>
    </xsl:template>
    
    <xsl:template match="component[@xsi:type='Sequence']">
        <xsl:variable name="page" select="count(preceding-sibling::component[@xsi:type='Sequence'])+1"/>
        <component xsi:type="{@xsi:type}" id="{@id}" page="{$page}">
            <xsl:apply-templates select="label"/>
            <xsl:apply-templates select="declaration"/>
            <xsl:apply-templates select="conditionFilter"/>
            <xsl:apply-templates select="component">
                <xsl:with-param name="page" select="$page" tunnel="yes"/>
            </xsl:apply-templates>
        </component>
    </xsl:template>
    
    <xsl:template match="component[@xsi:type='Subsequence']">
        <xsl:param name="page" tunnel="yes"/>
        <component xsi:type="{@xsi:type}" id="{@id}" page="{$page}">
            <xsl:apply-templates select="label"/>
            <xsl:apply-templates select="declaration"/>
            <xsl:apply-templates select="conditionFilter"/>
            <xsl:apply-templates select="component"/>
        </component>
        
    </xsl:template>
    
    <xsl:template match="component">
        <xsl:param name="page" tunnel="yes"/>
        <component>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="page" select="$page"/>
            <xsl:apply-templates/>
        </component>
    </xsl:template>
    
    <xsl:template match="label">
        <label><xsl:value-of select="."/></label>
    </xsl:template>
    
    <xsl:template match="conditionFilter">
        <xsl:variable name="listVariable" select="//Questionnaire/descendant::variable[value!='']" as="node()*"/>
        <conditionFilter>
            <xsl:call-template name="enojs:replaceVariableValueInFormula">
                <xsl:with-param name="variables" select="$listVariable"/>
                <xsl:with-param name="formula" select="."/>
            </xsl:call-template>
        </conditionFilter>
    </xsl:template>
    
    
    <xsl:template match="declaration">
        <declaration declarationType="{@declarationType}" id="{@id}" position="{@position}">
            <xsl:apply-templates select="label"/>
        </declaration>
    </xsl:template>
    
    <xsl:template match="component[@xsi:type='Radio'] | component[@xsi:type='Dropdown'] | component[@xsi:type='CheckboxOne']">
        <xsl:param name="page" tunnel="yes"/>
        <component xsi:type="{@xsi:type}" id="{@id}" toValidate="{@toValidate}" page="{$page}">
            <xsl:apply-templates select="label"/>
            <xsl:apply-templates select="declaration"/>
            
            <xsl:copy-of select="codeLists"/>
            
            <xsl:apply-templates select="response"/>
            <xsl:apply-templates select="conditionFilter"/>
        </component>
    </xsl:template>
    
    <xsl:template match="component[@xsi:type='InputNumber']">
        <xsl:param name="page" tunnel="yes"/>
        <component>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="page" select="$page"/>
            <xsl:apply-templates select="label"/>
            <xsl:apply-templates select="declaration"/>
            <unit><xsl:value-of select="unit"/></unit>
            <xsl:apply-templates select="response"/>
            <xsl:apply-templates select="conditionFilter"/>
        </component>
    </xsl:template>
    
    <xsl:template match="response">
        <response name="{@name}">
            <xsl:apply-templates  select="valueState"/>
        </response>
    </xsl:template>
    
    <xsl:template match="valueState">
        <valueState type="{@type}">
            <value><xsl:value-of select="value"/></value>
        </valueState>
    </xsl:template>
    
    <xsl:template match="codeLists">
        <codeLists>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </codeLists>
    </xsl:template>	
    
    <xsl:template match="variable">
        <xsl:variable name="value" select="value"/>
        <xsl:variable name="responseRef" select="responseRef"/>
        <variable>
            <name><xsl:value-of select="name"/></name>
            <xsl:choose>
                <xsl:when test="$value!=''">
                    <value><xsl:value-of select="$value"/></value>
                </xsl:when>
                <xsl:when test="$responseRef!=''">
                    <responseRef><xsl:value-of select="$responseRef"/></responseRef>
                </xsl:when>
            </xsl:choose>
        </variable>
    </xsl:template>
</xsl:stylesheet>