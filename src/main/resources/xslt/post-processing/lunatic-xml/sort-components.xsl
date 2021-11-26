<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:eno="http://xml.insee.fr/apps/eno"
                xmlns:enolunatic="http://xml.insee.fr/apps/eno/out/js"
                xmlns:h="http://xml.insee.fr/schema/applis/lunatic-h"
                xmlns="http://xml.insee.fr/schema/applis/lunatic-h"
                exclude-result-prefixes="xs fn xd eno enolunatic h" version="2.0">

    <xsl:output indent="yes"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>An xslt stylesheet who transforms an input into js through generic driver templates.</xd:p>
            <xd:p>The real input is mapped with the drivers.</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:variable name="root" select="root(.)"/>
    
    
    <!-- This variable retrieves all the dependencies (variables) used somewhere in components
    Useful later to check if calculated variables are used somewhere in the questionnaire-->
    <xsl:variable name="variablesUsed">
        <xsl:for-each select="$root//h:components//h:dependencies">
            <xsl:value-of select="concat(.,' ')"/>
        </xsl:for-each>
        <xsl:for-each select="$root//h:components/h:lines">
            <xsl:value-of select="concat(@min,' ')"/>
            <xsl:value-of select="concat(@max,' ')"/>
        </xsl:for-each>
    </xsl:variable>
    
    <!-- This variable retrieves all the dependencies (variables) used in filters
    Useful later to check if calculated variables are used in filters-->
    <xsl:variable name="variablesInFilter">
        <xsl:for-each select="$root//h:conditionFilter/h:dependencies">
            <xsl:value-of select="concat(.,' ')"/>
        </xsl:for-each>
        <xsl:for-each select="$root//h:components/h:lines">
            <xsl:value-of select="concat(@min,' ')"/>
            <xsl:value-of select="concat(@max,' ')"/>
        </xsl:for-each>
    </xsl:variable>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

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
            <xsl:apply-templates select="descendant::h:variables[@variableType='EXTERNAL']"/>
            <xsl:apply-templates select="descendant::h:variables[@variableType='COLLECTED']"/>
            <xsl:apply-templates select="descendant::h:variables[@variableType='CALCULATED']"/>
        </Questionnaire>
    </xsl:template>

    <xsl:template match="h:components[@xsi:type='Sequence' or @xsi:type='Subsequence']">
        <xsl:param name="loopDependencies" as="xs:string*" tunnel="yes"/>
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:apply-templates select="h:hierarchy"/>
            <xsl:variable name="dependencies" select="distinct-values(h:dependencies)" as="xs:string*"/>
            <xsl:variable name="allDependencies" as="xs:string*">
                <xsl:copy-of select="$dependencies"/>
                <xsl:copy-of select="$loopDependencies"/>
            </xsl:variable>
            <xsl:call-template name="enolunatic:add-all-dependencies">
                <xsl:with-param name="dependencies" select="$allDependencies"/>
            </xsl:call-template>
            <xsl:apply-templates select="h:components"/>
        </components>
    </xsl:template>
    
    <xsl:template match="h:components[@xsi:type='Loop']">
        <xsl:param name="loopDependencies" as="xs:string*" tunnel="yes"/>
        <!-- Value of idGenerator, may be empty -->
        <xsl:variable name="idGenerator" select="h:idGenerator"/>
        <!-- minimum is @min attribute of components if linked loop, else it is @min attributes of lines -->
        <xsl:variable name="minimum">
            <xsl:choose>
                <xsl:when test="$idGenerator!=''"><xsl:value-of select="@min"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="h:lines/@min"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <!-- maximum is @iterations attribute of components if linked loop, else it is @max attributes of lines -->
        <xsl:variable name="maximum">
            <xsl:choose>
                <xsl:when test="$idGenerator!=''"><xsl:value-of select="@iterations"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="h:lines/@max"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies[not(parent::h:conditionFilter)])" as="xs:string*"/>
        <xsl:variable name="responseDependencies" select="distinct-values(descendant::h:responseDependencies)" as="xs:string*"/>
        <!-- The loopDependencies consist of the reponseDependencies of the generating loop if linked loop, and the variables used in formula of minimum and maximum -->
        <xsl:variable name="localLoopDependencies" as="xs:string*">
            <xsl:copy-of select="distinct-values($root//h:components[@id=$idGenerator]//h:responseDependencies)"/>
            <xsl:for-each select="$dependencies">
                <xsl:if test="contains($minimum,.) or contains($maximum,.)">
                    <xsl:copy-of select="."/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable> 
        <xsl:variable name="allDependencies" as="xs:string*">
            <xsl:copy-of select="$dependencies"/>
            <xsl:copy-of select="$responseDependencies"/>
            <xsl:copy-of select="$loopDependencies"/>
        </xsl:variable>
        <components>
            <xsl:copy-of select="@*"/>
            <!-- In case of linked loop, we put @iterations attribute with specified value or count of the generator loop response-->
            <xsl:if test="$idGenerator!=''">
                <xsl:attribute name="iterations">
                    <xsl:choose>
                        <xsl:when test="string-length(@iterations) &gt; 0"><xsl:value-of select="@iterations"/></xsl:when>
                        <xsl:otherwise><xsl:value-of select="concat('count(',$localLoopDependencies[1],')')"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
            
            <xsl:apply-templates select="h:label"/>

            <!-- If not linked loop, we have to copy the lines and the missingResponse node -->
            <xsl:if test="$idGenerator='' or not(exists($idGenerator))">
                <xsl:apply-templates select="h:lines"/>
                <xsl:apply-templates select="h:missingResponse"/>
            </xsl:if>
            
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:apply-templates select="h:hierarchy"/>
            <xsl:call-template name="enolunatic:add-all-dependencies">
                <xsl:with-param name="dependencies" select="$allDependencies"/>
            </xsl:call-template>
            <xsl:for-each select="distinct-values($localLoopDependencies)">
                <loopDependencies><xsl:value-of select="."/></loopDependencies>
            </xsl:for-each>
            
            <xsl:variable name="allLoopDependencies" as="xs:string*">
                <xsl:copy-of select="$loopDependencies"/>
                <xsl:copy-of select="$localLoopDependencies"/>
            </xsl:variable>

            <xsl:apply-templates select="h:components">
                <xsl:with-param name="loopDependencies" select="$allLoopDependencies" as="xs:string*" tunnel="yes"/>
            </xsl:apply-templates>
        </components>
    </xsl:template>

    <xsl:template match="h:components[@xsi:type='RosterForLoop']">
        <xsl:param name="loopDependencies" as="xs:string*" tunnel="yes" />
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:apply-templates select="h:hierarchy"/>
            <xsl:apply-templates select="h:missingResponse"/>
            <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies[not(parent::h:conditionFilter)])" as="xs:string*"/>
            <xsl:variable name="responseDependencies" select="distinct-values(descendant::h:responseDependencies)" as="xs:string*"/>
            <xsl:variable name="allDependencies" as="xs:string*">
                <xsl:copy-of select="$dependencies"/>
                <xsl:copy-of select="$responseDependencies"/>
                <xsl:copy-of select="$loopDependencies"/>
            </xsl:variable>
            <xsl:call-template name="enolunatic:add-all-dependencies">
                <xsl:with-param name="dependencies" select="$allDependencies"/>
            </xsl:call-template>
            <xsl:apply-templates select="*[not(self::h:hierarchy or self::h:variables or self::h:label or self::h:declarations or self::h:conditionFilter or self::h:missingResponse)]"/>
        </components>
    </xsl:template>
    <xsl:template match="h:components[@xsi:type='Table']">
        <xsl:param name="loopDependencies" as="xs:string*" tunnel="yes" />
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:apply-templates select="h:hierarchy"/>
            <xsl:apply-templates select="h:missingResponse"/>
            <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies[not(parent::h:conditionFilter)])" as="xs:string*"/>
            <xsl:variable name="responseDependencies" select="distinct-values(descendant::h:responseDependencies)" as="xs:string*"/>
            <xsl:variable name="allDependencies" as="xs:string*">
                <xsl:copy-of select="$dependencies"/>
                <xsl:copy-of select="$responseDependencies"/>
                <xsl:copy-of select="$loopDependencies"/>
            </xsl:variable>
            <xsl:call-template name="enolunatic:add-all-dependencies">
                <xsl:with-param name="dependencies" select="$allDependencies"/>
            </xsl:call-template>
            <xsl:apply-templates select="*[not(self::h:variables or self::h:cells[@type='line'] or self::h:hierarchy or self::h:label or self::h:declarations or self::h:conditionFilter or self::h:missingResponse)]"/>
            <xsl:choose>
                <xsl:when test="h:lines">
                    <xsl:variable name="nbLines" select="count(h:cells[@type='line'])"/>
                    <xsl:variable name="nbLinesExpected" select="h:lines/@max"/>
                    <xsl:choose>
                        <xsl:when test="$nbLines = 1">
                            <xsl:call-template name="enolunatic:addLinesForRoster">
                                <xsl:with-param name="currentLigne" select="1"/>
                                <xsl:with-param name="nbLigneMax" select="$nbLinesExpected"/>
                                <xsl:with-param name="lineToCopy" select="h:cells[@type='line']"/>
                                <xsl:with-param name="tableId" select="@id"/>
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
        <xsl:param name="loopDependencies" as="xs:string*" tunnel="yes" />
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:variable name="component-id" select="@id"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:for-each select="$root//h:controls[matches(@id,$component-id)]">
                <controls>
                    <xsl:copy-of select="*[not(self::h:dependencies)] | @*"/>
                    <xsl:call-template name="enolunatic:add-all-dependencies">
                        <xsl:with-param name="dependencies" select="distinct-values(descendant::h:dependencies)"/>
                    </xsl:call-template>
                </controls>
            </xsl:for-each>
            <xsl:apply-templates select="h:hierarchy"/>
            <xsl:apply-templates select="h:missingResponse"/>
            <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies[not(parent::h:conditionFilter)])" as="xs:string*"/>
            <xsl:variable name="responseDependencies" select="distinct-values(descendant::h:responseDependencies)" as="xs:string*"/>
            <xsl:variable name="allDependencies" as="xs:string*">
                <xsl:copy-of select="$dependencies"/>
                <xsl:copy-of select="$responseDependencies"/>
                <xsl:copy-of select="$loopDependencies"/>
            </xsl:variable>
            <xsl:call-template name="enolunatic:add-all-dependencies">
                <xsl:with-param name="dependencies" select="$allDependencies"/>
            </xsl:call-template>
            <xsl:apply-templates select="*[not(self::h:hierarchy or self::h:variables or self::h:label or self::h:declarations or self::h:conditionFilter or self::h:missingResponse)]"/>
        </components>
    </xsl:template>

    <xsl:template match="h:response">
        <xsl:param name="idLine" tunnel="yes"/>
        <xsl:param name="ancestor" tunnel="yes"/>
        <xsl:param name="tableId" tunnel="yes"/>
        <xsl:param name="idColumn" tunnel="yes"/>
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
                </response>
                <xsl:if test="string($idLine)!='' and string($idColumn)!=''">
                    <xsl:call-template name="enolunatic:addVariableCollected">
                        <xsl:with-param name="responseName" select="concat(@name,'_',$idLine,'_',$idColumn)"/>
                        <xsl:with-param name="componentRef" select="$tableId"/>
                    </xsl:call-template>
                    <xsl:call-template name="enolunatic:add-dependencies">
                        <xsl:with-param name="name" select="concat(@name,'_',$idLine,'_',$idColumn)"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <response>
                    <xsl:copy-of select="@*"/>
                </response>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="h:dependencies"/>
    <xsl:template match="h:responseDependencies"/>

    <xsl:template match="h:label">
        <label><xsl:value-of select="normalize-space(.)"/></label>
    </xsl:template>

    <xsl:template match="h:cells">
        <xsl:param name="idLine" tunnel="yes"/>        
        <xsl:param name="idColumn" tunnel="yes"/>
        <cells>
            <xsl:if test="@id">
                <xsl:attribute name="id">
                    <xsl:choose>
                        <xsl:when test="$idColumn">
                            <xsl:value-of select="concat(@id,'_',$idLine,'_',$idColumn)"></xsl:value-of>        
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="@id"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
            <xsl:copy-of select="@*[not(name(.)='id')]"/>            
            <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies)" as="xs:string*"/>
            <xsl:variable name="responseDependencies" select="distinct-values(descendant::h:responseDependencies)" as="xs:string*"/>
            <xsl:variable name="allDependencies" as="xs:string*">
                <xsl:copy-of select="$dependencies"/>
                <xsl:if test="not($idColumn)"><xsl:copy-of select="$responseDependencies"/></xsl:if>
            </xsl:variable>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
            <xsl:if test="string-length(@type)=0">
                <xsl:call-template name="enolunatic:add-all-dependencies">
                    <xsl:with-param name="dependencies" select="$allDependencies"/>
                </xsl:call-template>
            </xsl:if>
        </cells>
    </xsl:template>

    <xsl:template match="h:cells" mode="roster">
        <xsl:param name="tableId" tunnel="yes"/>
        <cells>
            <xsl:copy-of select="@*"/>
            <xsl:for-each select="h:cells">
                <xsl:apply-templates select=".">
                    <xsl:with-param name="tableId" select="$tableId" tunnel="yes"/>
                    <xsl:with-param name="idColumn" select="position()" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:for-each>
        </cells>
    </xsl:template>

    <xsl:template match="h:conditionFilter">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
            <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies)" as="xs:string*"/>
            <!-- for filter dependencies, we need all dependencies (dependencies of calculated variables) -->
            <!-- for each calculated variables, we retrieve its dependencies -->
            <xsl:call-template name="enolunatic:add-all-recursive-dependencies">
                <xsl:with-param name="dependencies" select="$dependencies"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="h:variables[@variableType='CALCULATED']">
        <xsl:variable name="varName" select="h:name"/>
        <xsl:variable name="searchTerm" select="concat('[\W]', $varName, '[\W]')"/>
        <xsl:if test="enolunatic:is-var-used-in-list-of-dependencies($varName,'',$variablesUsed,$searchTerm)='true' or contains($varName,'FILTER_RESULT')">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
                <inFilter><xsl:value-of select="enolunatic:is-var-used-in-list-of-dependencies($varName,'',$variablesInFilter,$searchTerm)"/></inFilter>
            </xsl:copy>
        </xsl:if>
    </xsl:template>

    <xsl:template name="enolunatic:addVariableCollected">
        <xsl:param name="responseName"/>
        <xsl:param name="componentRef"/>
        <xsl:variable name="ResponseTypeEnum" select="'PREVIOUS,COLLECTED,FORCED,EDITED,INPUTED'" as="xs:string"/>
        <!-- responseType="{$responseType}" -->
        <variables variableType="COLLECTED" xsi:type="VariableType">
            <name><xsl:value-of select="$responseName"/></name>
            <componentRef><xsl:value-of select="$componentRef"/></componentRef>
            <values>
                <xsl:for-each select="tokenize($ResponseTypeEnum,',')">
                    <xsl:element name="{.}">
                        <xsl:attribute name="xsi:nil" select="true()"/>
                    </xsl:element>
                </xsl:for-each>
            </values>
        </variables>
    </xsl:template>

    <xsl:template name="enolunatic:addLinesForRoster">
        <xsl:param name="currentLigne"/>
        <xsl:param name="nbLigneMax"/>
        <xsl:param name="lineToCopy" as="node()"/>
        <xsl:param name="tableId"/>
        <xsl:if test="$currentLigne&lt;=$nbLigneMax">

            <xsl:apply-templates select="$lineToCopy" mode="roster">
                <xsl:with-param name="idLine" select="$currentLigne" tunnel="yes"/>
                <xsl:with-param name="ancestor" select="'table'" tunnel="yes"/>
                <xsl:with-param name="tableId" select="$tableId" tunnel="yes"/>
            </xsl:apply-templates>

            <xsl:call-template name="enolunatic:addLinesForRoster">
                <xsl:with-param name="currentLigne" select="$currentLigne +1"/>
                <xsl:with-param name="nbLigneMax" select="$nbLigneMax"/>
                <xsl:with-param name="lineToCopy" select="$lineToCopy" as="node()"/>
                <xsl:with-param name="tableId" select="$tableId"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="enolunatic:add-all-recursive-dependencies">
        <xsl:param name="dependencies" as="xs:string*"/>
        <xsl:variable name="results" as="xs:string*">
            <xsl:for-each select="$dependencies">
                <xsl:call-template name="enolunatic:add-dependencies">
                    <xsl:with-param name="name" select="."/>
                </xsl:call-template>
                <xsl:copy-of select="enolunatic:get-all-dependencies(.)"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:call-template name="enolunatic:add-all-dependencies">
            <xsl:with-param name="dependencies" select="$results"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template name="enolunatic:add-all-dependencies">
        <xsl:param name="dependencies" as="xs:string*"/>
        <xsl:for-each select="distinct-values($dependencies)">                
            <xsl:call-template name="enolunatic:add-dependencies">
                <xsl:with-param name="name" select="."/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:function name="enolunatic:get-all-dependencies">
        <xsl:param name="variableName"/>
        <xsl:choose>
            <xsl:when test="$root//h:variables[@variableType='CALCULATED' and h:name=$variableName]">
                <xsl:copy-of select="$root//h:variables[@variableType='CALCULATED' and h:name=$variableName]/h:bindingDependencies"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="enolunatic:add-dependencies">
                    <xsl:with-param name="name" select="$variableName"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:template name="enolunatic:add-dependencies">
        <xsl:param name="name"/>
        <bindingDependencies><xsl:value-of select="$name"/></bindingDependencies>
    </xsl:template>
    
    
    <!-- This function checks if the variable given in parameter is used as part of a given list of dependencies (by itself or by another calculated variable using it) -->
    <xsl:function name="enolunatic:is-var-used-in-list-of-dependencies">
        <xsl:param name="varName"/>
        <xsl:param name="varList"/>
        <xsl:param name="dependenciesToSearch"/>
        <xsl:param name="termToSearch"/>
        <xsl:choose>
            <xsl:when test="matches($dependenciesToSearch, $termToSearch)">
                <xsl:value-of select="'true'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="callingVars">
                    <xsl:value-of select="enolunatic:get-vars-using-var($varName)"/>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$callingVars != '' or $varList != ''">
                        <xsl:variable name="listToFeed" select="normalize-space(concat($varList,' ',$callingVars))"/>
                        <xsl:variable name="newVar">
                            <xsl:choose>
                                <xsl:when test="contains($listToFeed,' ')">
                                    <xsl:value-of select="substring-before($listToFeed,' ')"/> 
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$listToFeed"/> 
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="newSearch" select="concat('[\W]', $newVar, '[\W]')"/>
                        <xsl:value-of select="enolunatic:is-var-used-in-list-of-dependencies($newVar,normalize-space(replace($listToFeed,$newVar,'')),$dependenciesToSearch,$newSearch)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'false'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    
    
<!--    <!-\- This function checks if the variable given in parameter is used as part of a filter (by itself or by another calculated variable using it) -\->
    <xsl:function name="enolunatic:is-var-used-in-filter">
        <xsl:param name="varName"/>
        <xsl:param name="varList"/>
        <xsl:param name="toSearchFilter"/>
        <xsl:param name="toSearch"/>
        <xsl:choose>
            <xsl:when test="matches($toSearchFilter, $toSearch)">
                <xsl:value-of select="'true'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="callingVars">
                    <xsl:value-of select="enolunatic:get-vars-using-var($varName)"/>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$callingVars != '' or $varList != ''">
                        <xsl:variable name="listToFeed" select="normalize-space(concat($varList,' ',$callingVars))"/>
                        <xsl:variable name="newVar">
                            <xsl:choose>
                                <xsl:when test="contains($listToFeed,' ')">
                                    <xsl:value-of select="substring-before($listToFeed,' ')"/> 
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$listToFeed"/> 
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="newSearch" select="concat('[\W]', $newVar, '[\W]')"/>
                        <xsl:value-of select="enolunatic:is-var-used-in-filter($newVar,normalize-space(replace($listToFeed,$newVar,'')),$toSearchFilter,$newSearch)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'false'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>-->
    
    <!-- This function returns for the (calculated) variable given in parameter all the names of other calculated variables using it -->
    <xsl:function name="enolunatic:get-vars-using-var">
        <xsl:param name="varName"/>
        <xsl:for-each select="$root//h:variables[@variableType='CALCULATED']">
            <xsl:variable name="node" select="."/>
            <xsl:if test="$node/h:bindingDependencies=$varName">
                <xsl:sequence select="$node/h:name"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:function>
    
</xsl:stylesheet>