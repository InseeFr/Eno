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
        <xsl:apply-templates select="Questionnaire"/>
    </xsl:template>
    
    <xsl:template match="Questionnaire">
        <xsl:variable name="idQuestionnaire" select="@id"/>
        <Questionnaire 
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns="http://xml.insee.fr/schema/applis/lunatic-h"
            xsi:schemaLocation="http://xml.insee.fr/schema/applis/lunatic-h file:/src/main/resources/xsd/QuestionnaireH.xsd"
            id="{$idQuestionnaire}">
            <xsl:apply-templates select="*[not(self::variables)]"/>
            <xsl:apply-templates select="descendant::variables"/>
        </Questionnaire>
    </xsl:template>
    
    <xsl:template match="components[@xsi:type='Sequence']">
        <components xsi:type="{@xsi:type}" id="{@id}">
            <xsl:apply-templates select="label"/>
            <xsl:apply-templates select="declarations"/>
            <xsl:apply-templates select="conditionFilter"/>
            <xsl:apply-templates select="components"/>
        </components>
    </xsl:template>
    
    <xsl:template match="components[@xsi:type='Subsequence']">
        <components xsi:type="{@xsi:type}" id="{@id}">
            <xsl:apply-templates select="label"/>
            <xsl:apply-templates select="declarations"/>
            <xsl:apply-templates select="conditionFilter"/>
            <xsl:apply-templates select="components"/>
        </components>
    </xsl:template>
    
    <xsl:template match="components">
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*[not(self::variables)]"/>
        </components>
    </xsl:template>
       
    <xsl:template match="unit">
        <unit><xsl:value-of select="."/></unit>
    </xsl:template>
    
    <xsl:template match="label">
        <label><xsl:value-of select="normalize-space(.)"/></label>
    </xsl:template>
    
    <xsl:template match="conditionFilter">
        <xsl:variable name="listVariable" select="//Questionnaire/descendant::variables[value!='']" as="node()*"/>
        <conditionFilter>
            <xsl:call-template name="enojs:replaceVariableValueInFormula">
                <xsl:with-param name="variables" select="$listVariable"/>
                <xsl:with-param name="formula" select="."/>
            </xsl:call-template>
        </conditionFilter>
    </xsl:template>
    
    <xsl:template match="declarations">
        <declarations declarationType="{@declarationType}" id="{@id}" position="{@position}">
            <xsl:apply-templates select="label"/>
        </declarations>
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
    
    <xsl:template match="items">
        <items>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </items>
    </xsl:template>
    
    <xsl:template match="variables">
        <xsl:variable name="value" select="value"/>
        <xsl:variable name="responseRef" select="responseRef"/>
        <variables>
            <name><xsl:value-of select="name"/></name>
            <xsl:choose>
                <xsl:when test="$value!=''">
                    <value><xsl:value-of select="normalize-space($value)"/></value>
                </xsl:when>
                <xsl:when test="$responseRef!=''">
                    <responseRef><xsl:value-of select="$responseRef"/></responseRef>
                </xsl:when>
            </xsl:choose>
            <xsl:apply-templates select="label"/>
        </variables>
    </xsl:template>
    
    <xsl:template match="dateFormat">
        <dateFormat><xsl:value-of select="."/></dateFormat>
    </xsl:template>
    
    <xsl:template match="codes">
        <codes>
            <parent><xsl:value-of select="parent"/></parent>
            <value><xsl:value-of select="value"/></value>
            <xsl:apply-templates select="label"/>
        </codes>
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
                <!-- on enleve les "$" qui restent -->
                <!--<xsl:value-of select="replace($formula,'\$','')"/>-->
                <xsl:value-of select="$formula"/>
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:variable name="var" select="$variables[1]/name" as="xs:string"/>
                <xsl:variable name="regex" select="concat('\$',$var)"/>
                <xsl:variable name="valueOfVariable" as="xs:string" select="$variables[1]/value"/>
                <xsl:variable name="newFormula">
                    <xsl:choose>
                        <xsl:when test="$valueOfVariable!=''">
                            <!-- Probleme avec les '$', résolue en les remplacant par \$ -->
                            <xsl:variable name="expressionToReplace" as="xs:string" select="replace($valueOfVariable,'\$','\\\$')"/>
                            <!-- Replace in formula "$var" by "(value of the var)" -->
                            <xsl:value-of select="replace($formula,$regex,concat('(',$expressionToReplace,')'))"/>
                        </xsl:when>
                        <!-- on laisse la formule tel quel si la variable n'a pas de valeur => on met "var" au lieu de "$var$" -->
                        <!-- A voir si on remet des "$" autour des variables dans les lambdas expression-->
                        <xsl:otherwise><xsl:value-of select="replace($formula,$regex,$var)"/></xsl:otherwise>
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
                
                <!--<xsl:variable name="oldArguments" select="substring-before(substring-after($newFormula,'('),')')"/>
                <xsl:variable name="newArguments" select="enojs:addArgumentsInFormula($variablesInFormula,$oldArguments,$variables[1])"/>
                
                <xsl:variable name="endFormula" select="replace(replace($newFormula,concat('\(',$oldArguments,'\)'),'()'),'\(\)',$newArguments)"/>-->
                
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
            <variables>
                <name><xsl:value-of select="$nameOfVariable"/></name>
                <value><xsl:value-of select="//Questionnaire/descendant::variables[name=$nameOfVariable]/value"/></value>
            </variables>
            
            <xsl:variable name="endOfFormula" select="substring-after(substring-after($formula,'$'),'$')"/>
            <xsl:call-template name="enojs:findVariableInFormula">
                <xsl:with-param name="formula" select="$endOfFormula"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <!--<xsl:function name="enojs:addArgumentsInFormula">
        <xsl:param name="newArguments" as="node()*"/>
        <xsl:param name="oldArguments" as="xs:string"/>
        <xsl:param name="currentVar" as="node()"/>
        <xsl:variable name="oldArguments2">
            <xsl:choose>
                <xsl:when test="$currentVar/value!=''">
                    <!-\- delete calculated variable and ',,'-\->
                    <xsl:value-of select="replace(replace($oldArguments,$currentVar/name,''),',,',',')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$oldArguments"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="argumentList" as="xs:string*">
            <xsl:for-each select="$newArguments">
                <xsl:value-of select="child::name"/>
            </xsl:for-each>
            <xsl:for-each select="distinct-values(tokenize($oldArguments2,','))">
                <xsl:value-of select="."/>
            </xsl:for-each>
        </xsl:variable>
        <!-\- delete ',' and concat all variable : ex (var1,var2) -\->
        <xsl:value-of select="replace(concat('(',string-join(distinct-values($argumentList),','),')'),',\s*\)',')')"/>
    </xsl:function>-->
    
    
</xsl:stylesheet>