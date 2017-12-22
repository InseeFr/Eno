<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:enopogues="http://xml.insee.fr/apps/eno/in/pogues-xml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xd:doc>
        <xd:desc>
            <xd:p>For each element, the default behaviour is to return empty text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="#all" priority="-1">
        <xsl:text/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>This mode is used to return nodes instead of strings.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="with-tag">
        <xsl:sequence select="."/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>This mode is used when a conversion table is needed between Pogues and DDI.</xd:p>
            <xd:p>TODO : Move this out of the input interface and put it in the transformation one (depends both on input and output).</xd:p>
        </xd:desc>
    </xd:doc>   
    <xsl:template match="*" mode="conversion-table">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="* | @*" mode="conversion-table-error-message">
        <xsl:message select="concat('The value ',.,' for ',name(),' are not supported')"/>
    </xsl:template>

    <xsl:template match="pogues:Declaration/@declarationType" mode="conversion-table">        
        <xsl:choose>
            <xsl:when test=". ='COMMENT'">
                <xsl:value-of select="'comment'"/>
            </xsl:when>
            <xsl:when test=". ='INSTRUCTION'">
                <xsl:value-of select="'instruction'"/>
            </xsl:when>
            <xsl:when test=". ='HELP'">
                <xsl:value-of select="'help'"/>
            </xsl:when>
            <xsl:when test=". ='WARNING'">
                <xsl:value-of select="'warning'"/>
            </xsl:when>            
            <xsl:otherwise>
                <xsl:apply-templates select="." mode="conversion-table-error-message"/>
            </xsl:otherwise>
        </xsl:choose>    
    </xsl:template>
    
    <xsl:template match="pogues:Datatype/@visualizationHint" mode="conversion-table">
        <xsl:choose>
            <xsl:when test=". ='RADIO'">
                <xsl:value-of select="'radio-button'"/>
            </xsl:when>
            <xsl:when test=". ='CHECKDOWN'">
                <xsl:value-of select="'checkbox'"/>
            </xsl:when>
            <xsl:when test=". ='DROPDOWN'">
                <xsl:value-of select="'drop-down-list'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="." mode="conversion-table-error-message"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
   <!-- <xsl:template match="pogues:Variable" mode="enopogues:get-related-response">
        <xsl:sequence select="//pogues:Response[pogues:CollectedVariableReference = current/@id]"/>
    </xsl:template>-->
    
    <xsl:template match="pogues:Control" mode="enopogues:get-ip-id">
        <xsl:param name="index" tunnel="yes"/>        
        <xsl:value-of select="concat(enopogues:get-id(.),'-IP-',$index)"/>
    </xsl:template>
    
    
    <xsl:template match="pogues:Response" mode="enopogues:get-related-variable">
        <xsl:variable name="idVariable" select="pogues:CollectedVariableReference"/>
        <xsl:sequence select="//pogues:Variable[@id = $idVariable]"/>
    </xsl:template>
    
    <xsl:template match="pogues:Control" mode="enopogues:get-related-variable">
        <xsl:variable name="expressionVariable" select="tokenize(pogues:Expression,'\$')"/>
        <xsl:variable name="variables" select="//pogues:Variables"/>        
        <xsl:sequence select="$variables/pogues:Variable[some $x in $expressionVariable satisfies substring-before($x,' ')=pogues:Name/text()]"></xsl:sequence>
        <!-- 
        <xsl:sequence select="//pogues:Variables/pogues:Variable[some $x in tokenize(pogues:Expression,'\$') satisfies substring-before($x,' ')=pogues:Name/text()]"></xsl:sequence>        
        -->
    </xsl:template>
    
    
</xsl:stylesheet>