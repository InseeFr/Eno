<?xml version="1.0" encoding="UTF-8"?>
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
        <xsl:apply-templates select="h:Questionnaire"/>
    </xsl:template>
    
    <xsl:template match="h:Questionnaire">
        <Questionnaire>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
            <xsl:apply-templates select="descendant::h:variables"/>
        </Questionnaire>
    </xsl:template>
    
    <xsl:template match="h:components[@xsi:type='Sequence' or @xsi:type='Subsequence']">
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:apply-templates select="h:components"/>
        </components>
    </xsl:template>
    
    <xsl:template match="h:components[@xsi:type='Table']">
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:choose>
                <xsl:when test="h:lines">
                    <xsl:variable name="nbLines" select="count(h:cells[h:cells/@type!='header'])"/>
                    <xsl:variable name="nbLinesExpected" select="h:lines/@max"/>
                    <max value="{$nbLinesExpected}"/>
                    <real-nb-lines value="{$nbLines}"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="*[not(self::h:variables)]"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
        </components>
    </xsl:template>
    
    <xsl:template match="h:components">
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
        </components>
    </xsl:template>
    
    <xsl:template match="h:unit">
        <unit><xsl:value-of select="."/></unit>
    </xsl:template>
    
    <xsl:template match="h:lines">
        <lines><xsl:copy-of select="@*"/></lines>
    </xsl:template>
    
    <xsl:template match="h:label">
        <label><xsl:value-of select="normalize-space(.)"/></label>
    </xsl:template>
    
    <xsl:template match="h:conditionFilter">
        <xsl:variable name="listVariable" select="//h:Questionnaire/descendant::h:variables[h:value!='']" as="node()*"/>
        <conditionFilter>
            <xsl:call-template name="enojs:replaceVariableValueInFormula">
                <xsl:with-param name="variables" select="$listVariable"/>
                <xsl:with-param name="formula" select="."/>
            </xsl:call-template>
        </conditionFilter>
    </xsl:template>
    
    <xsl:template match="h:declarations">
        <declarations>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="h:label"/>
        </declarations>
    </xsl:template>
    
    <xsl:template match="h:response">
        <xsl:param name="idLine" tunnel="yes"/>
        <xsl:param name="idColmun" tunnel="yes"/>
        <xsl:param name="ancestor" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="$ancestor='table'">
                <responses>
                    <xsl:choose>
                        <xsl:when test="string($idLine)!='' and string($idColmun)!=''">
                            <xsl:attribute name="name"><xsl:value-of select="concat(@name,'_',$idLine,'_',$idColmun)"/></xsl:attribute>
                            <xsl:copy-of select="@xsi:type"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="@*"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:apply-templates select="h:valueState"/>
                </responses>
            </xsl:when>
            <xsl:otherwise>
                <response>
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates select="h:valueState"/>
                </response>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    <xsl:template match="h:valueState">
        <valueState>
            <xsl:copy-of select="@*"/>
            <value><xsl:value-of select="h:value"/></value>
        </valueState>
    </xsl:template>
    
    <xsl:template match="h:codeLists">
        <codeLists>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </codeLists>
    </xsl:template>	
    
    
    
    <xsl:template match="h:value">
        <value><xsl:value-of select="."/></value>
    </xsl:template>
    
    <xsl:template match="h:variables">
        <xsl:variable name="value" select="h:value"/>
        <xsl:variable name="responseRef" select="h:responseRef"/>
        <variables>
            <name><xsl:value-of select="h:name"/></name>
            <xsl:choose>
                <xsl:when test="$value!=''">
                    <value><xsl:value-of select="normalize-space($value)"/></value>
                </xsl:when>
                <xsl:when test="$responseRef!=''">
                    <responseRef><xsl:value-of select="$responseRef"/></responseRef>
                </xsl:when>
            </xsl:choose>
            <xsl:apply-templates select="h:label"/>
        </variables>
    </xsl:template>
    
    <xsl:template match="h:dateFormat">
        <dateFormat><xsl:value-of select="."/></dateFormat>
    </xsl:template>
    
    <xsl:template match="h:options">
        <options>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </options>
    </xsl:template>
    
    <xsl:template match="h:codes">
        <codes>
            <xsl:apply-templates select="h:value"/>
            <xsl:apply-templates select="h:label"/>
        </codes>
    </xsl:template>
    
    
    <xsl:template match="h:header">
        <header>
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="."/>
        </header>
    </xsl:template>
    <xsl:template match="h:cells">
        <cells>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
        </cells>
    </xsl:template>
    
    
    <xd:doc>
        <xd:desc>
            <xd:p>Recursive template named "enojs:orderResponses"</xd:p>
            <xd:p>It orders responses in tables "responses" for each the tables's line</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="enojs:orderResponses">
        <xsl:param name="nbColumn"/>
        <xsl:param name="responses" as="node()*"/>
        <responses>
            <xsl:apply-templates select="$responses[position()&lt;=$nbColumn]">
                <xsl:with-param name="ancestor" select="'table'" tunnel="yes"/>
            </xsl:apply-templates>
        </responses>
        
        <xsl:if test="count($responses[position()&gt;$nbColumn])&gt;0"> 
            <xsl:call-template name="enojs:orderResponses">
                <xsl:with-param name="nbColumn" select="$nbColumn"/>
                <xsl:with-param name="responses" select="$responses[position()&gt;$nbColumn]" as="node()*"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    
    <xsl:template name="enojs:addResponsesForRoster">
        <xsl:param name="currentLigne"/>
        <xsl:param name="nbLigneMax"/>
        <xsl:param name="responses" as="node()*"/>
        <xsl:if test="$currentLigne&lt;=$nbLigneMax">
            <responses>
                <xsl:for-each select="$responses">
                    <xsl:apply-templates select=".">
                        <xsl:with-param name="idLine" select="$currentLigne" tunnel="yes"/>
                        <xsl:with-param name="idColmun" select="position()" tunnel="yes"/>
                        <xsl:with-param name="ancestor" select="'table'" tunnel="yes"/>
                    </xsl:apply-templates>
                </xsl:for-each>
                
            </responses>
            <xsl:call-template name="enojs:addResponsesForRoster">
                <xsl:with-param name="nbLigneMax" select="$nbLigneMax"/>
                <xsl:with-param name="currentLigne" select="$currentLigne +1"/>
                <xsl:with-param name="responses" select="$responses" as="node()*"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xd:doc>
        <xd:desc>
            <xd:p>Recursive template named "enojs:replaceVariableValueInFormula"</xd:p>
            <xd:p>It replaces all variables that depend on other variables in a formula (in a filter)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="enojs:replaceVariableValueInFormula">
        <xsl:param name="variables" as="node()*"/>
        <xsl:param name="formula"/>
        
        <xsl:choose>
            <xsl:when test="count($variables)=0">
                <xsl:value-of select="$formula"/>
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:variable name="var" select="$variables[1]/h:name" as="xs:string"/>
                <xsl:variable name="regex" select="concat('\$',$var)"/>
                <xsl:variable name="valueOfVariable" as="xs:string" select="$variables[1]/h:value"/>
                <xsl:variable name="newFormula">
                    <xsl:choose>
                        <xsl:when test="$valueOfVariable!=''">
                            <!-- Issue with '$', solved by replacing by \$ -->
                            <xsl:variable name="expressionToReplace" as="xs:string" select="replace($valueOfVariable,'\$','\\\$')"/>
                            <!-- Replace in formula "$var" by "(value of the var)" -->
                            <xsl:value-of select="replace($formula,$regex,concat('(',$expressionToReplace,')'))"/>
                        </xsl:when>
                        <xsl:otherwise><xsl:value-of select="$formula"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                
                <!-- Find if in the value of the current variable, there are variables which depends other variables --> 
                <xsl:variable name="variablesToAdd" as="node()*">
                    <xsl:call-template name="enojs:findVariableInFormula">
                        <xsl:with-param name="formula" select="$valueOfVariable"/>
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="variablesInFormula" as="node()*">
                    <xsl:call-template name="enojs:findVariableInFormula">
                        <xsl:with-param name="formula" select="$newFormula"/>
                    </xsl:call-template>
                </xsl:variable>
                
                <!-- Add variables found in the value of the current variable in the list of variables -->
                <xsl:variable name="variablesCalculatedInFormula" as="node()*">
                    <xsl:for-each select="$variables">
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                    <xsl:if test="count($variablesToAdd)!=0">
                        <xsl:for-each select="$variablesToAdd">
                            <xsl:copy-of select="."/>
                        </xsl:for-each>
                    </xsl:if>
                </xsl:variable>
                
                <xsl:call-template name="enojs:replaceVariableValueInFormula">
                    <xsl:with-param name="formula" select="$newFormula"/>
                    <xsl:with-param name="variables" select="$variablesCalculatedInFormula[position() &gt; 1]"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>	
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Recursive template named "enojs:findVariableInFormula"</xd:p>
            <xd:p>This template research in a formula (or in a value of variable in this case) dependencies with other variables</xd:p>
            <xd:p>It returns sequence of variables found (with the name and the value of these variables)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="enojs:findVariableInFormula">
        <xsl:param name="formula" as="xs:string"/>
        <xsl:if test="contains($formula,'$')">
            <xsl:variable name="nameOfVariable" select="substring-before(substring-after($formula,'$'),'$')" as="xs:string"/>
            <h:variables>
                <h:name><xsl:value-of select="$nameOfVariable"/></h:name>
                <h:value><xsl:value-of select="//h:Questionnaire/descendant::h:variables[h:name=$nameOfVariable]/h:value"/></h:value>
            </h:variables>
            
            <xsl:variable name="endOfFormula" select="substring-after(substring-after($formula,'$'),'$')"/>
            <xsl:call-template name="enojs:findVariableInFormula">
                <xsl:with-param name="formula" select="$endOfFormula"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>