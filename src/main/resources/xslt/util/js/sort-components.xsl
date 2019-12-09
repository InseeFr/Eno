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
            <xsl:apply-templates select="*[not(self::h:variables or self::h:cells[@type='line'])]"/>
            <xsl:choose>
                <xsl:when test="h:lines">
                    <xsl:variable name="nbLines" select="count(h:cells[@type='line'])"/>
                    <xsl:variable name="nbLinesExpected" select="h:lines/@max"/>
                    <xsl:choose>
                        <xsl:when test="$nbLines = 1">
                            <xsl:variable name="cell" select="enojs:prepareCellsForRoster(h:cells[@type='line'])"/>
                            <xsl:apply-templates mode="roster" select="$cell">
                                <xsl:with-param name="idLine" select="1" tunnel="yes"/>
                                <xsl:with-param name="ancestor" select="'table'" tunnel="yes"/>
                            </xsl:apply-templates>
                            <xsl:call-template name="enojs:addLinesForRoster">
                                <xsl:with-param name="currentLigne" select="2"/>
                                <xsl:with-param name="nbLigneMax" select="$nbLinesExpected"/>
                                <xsl:with-param name="lineToCopy" select="$cell"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="*[not(self::h:variables) and self::h:cells[@type='line']]"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="*[not(self::h:variables) and self::h:cells[@type='line']]"/>
                </xsl:otherwise>
            </xsl:choose>
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
        <xsl:param name="ancestor" tunnel="yes"/>
        <xsl:variable name="idColumn" select="ancestor::h:cells/@idColumn"/>
        <xsl:choose>
            <xsl:when test="$ancestor='table'">
                <response>
                    <xsl:choose>
                        <xsl:when test="string($idLine)!='' and string($idColumn)!=''">
                            <xsl:attribute name="name"><xsl:value-of select="concat(@name,'_',$idLine,'_',$idColumn)"/></xsl:attribute>
                            <xsl:copy-of select="@xsi:type"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="@*"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:apply-templates select="h:valueState"/>
                </response>
                <xsl:if test="string($idLine)!='' and string($idColumn)!=''">                    
                    <xsl:call-template name="enojs:addVariableCollected">
                        <xsl:with-param name="responseName" select="concat(@name,'_',$idLine,'_',$idColumn)"/>
                        <xsl:with-param name="responseRef" select="concat(@name,'_',$idLine,'_',$idColumn)"/>
                    </xsl:call-template>
                </xsl:if>
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
            <xsl:apply-templates select="node()"/>
        </valueState>
    </xsl:template>
    
    <xsl:template match="h:codeLists">
        <codeLists>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
        </codeLists>
    </xsl:template>	

    <xsl:template match="h:value">
        <value>
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="."/>
        </value>
    </xsl:template>
    
    <xsl:template match="h:variables">
        <xsl:variable name="value" select="h:value"/>
        <xsl:variable name="responseRef" select="h:responseRef"/>
        <xsl:variable name="expression" select="h:expression"/>
        <variables>
            <xsl:copy-of select="@*"/>
            <name><xsl:value-of select="h:name"/></name>
            <xsl:choose>
                <xsl:when test="$responseRef!=''">
                    <responseRef><xsl:value-of select="$responseRef"/></responseRef>
                </xsl:when>
                <xsl:when test="$expression!=''">
                    <expression><xsl:value-of select="$expression"/></expression>
                </xsl:when>
            </xsl:choose>
            <xsl:apply-templates select="h:value"/>
        </variables>
    </xsl:template>
    
    <xsl:template match="h:dateFormat">
        <dateFormat><xsl:value-of select="."/></dateFormat>
    </xsl:template>
    
    <xsl:template match="h:responses">
        <responses>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
        </responses>
    </xsl:template>
    
    <xsl:template match="h:options">
        <options>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
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
    
    <xsl:template match="h:cells" mode="roster">
        <xsl:param name="column" tunnel="yes"/>
        <cells>
            <xsl:copy-of select="@*"/>
            <xsl:if test="string($column)!=''"><xsl:attribute name="idColumn" select="$column"/></xsl:if>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
        </cells>
    </xsl:template>
    
    <xsl:template name="enojs:addVariableCollected">
        <xsl:param name="responseName"/>
        <xsl:param name="responseRef"/>
        <variables variableType="COLLECTED">
            <name><xsl:value-of select="$responseName"/></name>
            <responseRef><xsl:value-of select="$responseRef"/></responseRef>
        </variables>
    </xsl:template>
    
    <xsl:template name="enojs:addLinesForRoster">
        <xsl:param name="currentLigne"/>
        <xsl:param name="nbLigneMax"/>
        <xsl:param name="lineToCopy" as="node()"/>
        <xsl:if test="$currentLigne&lt;=$nbLigneMax">
            
            <xsl:apply-templates select="$lineToCopy" mode="roster">
                <xsl:with-param name="idLine" select="$currentLigne" tunnel="yes"/>
                <xsl:with-param name="ancestor" select="'table'" tunnel="yes"/>
            </xsl:apply-templates>
            
            <xsl:call-template name="enojs:addLinesForRoster">
                <xsl:with-param name="currentLigne" select="$currentLigne +1"/>
                <xsl:with-param name="nbLigneMax" select="$nbLigneMax"/>
                <xsl:with-param name="lineToCopy" select="$lineToCopy" as="node()"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    
    <xsl:function name="enojs:prepareCellsForRoster">
        <xsl:param name="cell" as="node()"/>        
        <cells>
            <xsl:copy-of select="$cell/@*"/>
            <xsl:for-each select="$cell/h:cells">
                <xsl:apply-templates select="." mode="roster">
                    <xsl:with-param name="column" select="position()" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:for-each>
        </cells>
    </xsl:function>
    
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