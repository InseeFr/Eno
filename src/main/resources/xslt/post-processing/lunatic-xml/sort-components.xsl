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

    <xsl:param name="properties-file"/>
    <xsl:param name="parameters-file"/>
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty/>
    </xsl:param>

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

    <xsl:variable name="unusedVars" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//UnusedVars != ''">
                <xsl:value-of select="$parameters//UnusedVars" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//UnusedVars" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>An xslt stylesheet who transforms an input into js through generic driver templates.</xd:p>
            <xd:p>The real input is mapped with the drivers.</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:variable name="root" select="root(.)"/>

    <!-- This variable retrieves all the dependencies (variables) used somewhere in components
    Useful later to check if calculated variables are used somewhere in the questionnaire-->
    <!-- There will be a final unwanted space with the way I concatenate, so I normalize-space in hte final variable -->
    <xsl:variable name="variablesUsedTemp">
        <xsl:for-each select="$root//h:components//h:dependencies">
            <xsl:value-of select="concat(.,' ')"/>
        </xsl:for-each>
        <xsl:for-each select="$root//h:components/h:lines">
            <xsl:value-of select="concat(h:min,' ')"/>
            <xsl:value-of select="concat(h:max,' ')"/>
        </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="variablesUsed" select="normalize-space($variablesUsedTemp)"/>

    <!-- This variable retrieves all the dependencies (variables) used in filters
    Useful later to check if calculated variables are used in filters-->
    <xsl:variable name="variablesInFilter">
        <xsl:for-each select="$root//h:conditionFilter/h:dependencies">
            <xsl:value-of select="concat(.,' ')"/>
        </xsl:for-each>
        <xsl:for-each select="$root//h:components/h:lines">
            <xsl:value-of select="concat(h:min,' ')"/>
            <xsl:value-of select="concat(h:max,' ')"/>
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
            <xsl:apply-templates select="descendant::h:suggesters[not(preceding::h:suggesters[h:name = current()/h:name])]"/>
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
        <!-- minimum is value of min element of lines (may not exist)-->
        <xsl:variable name="minimum">
            <xsl:value-of select="h:lines/h:min/h:value"/>
        </xsl:variable>
        <!-- maximum is value of iterations element of components if linked loop, else it is value of max element of lines -->
        <xsl:variable name="maximum">
            <xsl:choose>
                <xsl:when test="$idGenerator!=''"><xsl:value-of select="h:iterations/h:value"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="h:lines/h:max/h:value"/></xsl:otherwise>
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
            <!-- In case of linked loop, we put iterations element with specified value or count of the generator loop response-->
            <xsl:if test="$idGenerator!=''">
                <iterations>
                    <value>
                        <xsl:choose>
                            <xsl:when test="string-length(h:iterations/h:value) &gt; 0"><xsl:value-of select="h:iterations/h:value"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="concat('count(',$localLoopDependencies[1],')')"/></xsl:otherwise>
                        </xsl:choose>
                    </value>
                    <type>
                        <xsl:choose>
                            <xsl:when test="string-length(h:iterations/h:type) &gt; 0"><xsl:value-of select="h:iterations/h:type"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="'VTL'"/></xsl:otherwise>
                        </xsl:choose>
                    </type>
                </iterations>
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
            <xsl:variable name="componentId" select="@id"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:copy-of select="enolunatic:get-all-controls($componentId)"/>
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
            <xsl:apply-templates select="*[not(self::h:variables or self::h:body or self::h:hierarchy or self::h:label or self::h:declarations or self::h:conditionFilter or self::h:missingResponse)]"/>
            <xsl:apply-templates select="h:body"/>
        </components>
    </xsl:template>

    <xsl:template match="h:components">
        <xsl:param name="loopDependencies" as="xs:string*" tunnel="yes" />
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:variable name="componentId" select="@id"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:storeName"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:copy-of select="enolunatic:get-all-controls($componentId)"/>
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
            <xsl:apply-templates select="*[not(self::h:hierarchy or self::h:variables or self::h:label or self::h:declarations or self::h:conditionFilter or self::h:missingResponse or self::h:storeName)]"/>
        </components>
    </xsl:template>

    <xsl:template match="h:dependencies"/>
    <xsl:template match="h:responseDependencies"/>

    <xsl:template match="h:label">
        <label>
            <value><xsl:value-of select="normalize-space(./h:value)"/></value>
            <type><xsl:value-of select="h:type"/></type>
        </label>
    </xsl:template>

    <xsl:template match="h:body">
        <body>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
        </body>
    </xsl:template>

    <xsl:template match="h:bodyLine">
        <bodyLine>
            <xsl:copy-of select="@*"/>
            <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies)" as="xs:string*"/>
            <xsl:variable name="responseDependencies" select="distinct-values(descendant::h:responseDependencies)" as="xs:string*"/>
            <xsl:variable name="allDependencies" as="xs:string*">
                <xsl:copy-of select="$dependencies"/>
                <xsl:copy-of select="$responseDependencies"/>
            </xsl:variable>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
            <xsl:if test="string-length(@type)=0">
                <xsl:call-template name="enolunatic:add-all-dependencies">
                    <xsl:with-param name="dependencies" select="$allDependencies"/>
                </xsl:call-template>
            </xsl:if>
        </bodyLine>
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
        <xsl:if test="$unusedVars or contains($varName,'FILTER_RESULT') or contains($varName,'xAxis') or contains($varName,'yAxis') or enolunatic:is-var-used-in-list-of-dependencies($varName,'',$variablesUsed,$searchTerm)='true'">
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
            <!-- <componentRef><xsl:value-of select="$componentRef"/></componentRef> -->
            <values>
                <xsl:for-each select="tokenize($ResponseTypeEnum,',')">
                    <xsl:element name="{.}">
                        <xsl:attribute name="xsi:nil" select="true()"/>
                    </xsl:element>
                </xsl:for-each>
            </values>
        </variables>
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

    <!-- This function is used to retrieve all controls linked to a given component, which ID is given in parameter -->
    <xsl:function name="enolunatic:get-all-controls">
        <xsl:param name="componentId"/>
        <xsl:if test="$componentId != ''">
            <xsl:for-each select="$root//h:controls[matches(@id,$componentId)]">
                <controls>
                    <xsl:copy-of select="*[not(self::h:dependencies)] | @*"/>
                    <xsl:call-template name="enolunatic:add-all-dependencies">
                        <xsl:with-param name="dependencies" select="distinct-values(descendant::h:dependencies)"/>
                    </xsl:call-template>
                </controls>
            </xsl:for-each>            
        </xsl:if>
    </xsl:function>

    <!-- This function checks if the variable given in parameter is used as part of a given list of dependencies (by itself or by another calculated variable using it) -->
    <xsl:function name="enolunatic:is-var-used-in-list-of-dependencies">
        <xsl:param name="varName"/>
        <xsl:param name="varList"/>
        <xsl:param name="dependenciesToSearch"/>
        <xsl:param name="termToSearch"/>
        <xsl:choose>
            <!-- If I find the variable in the dependencies to search, we can stop here, it is true and the variable must be kept -->
            <!-- First matches is general case, the termToSearch surrounds the variable with [\W] to search among a space separated list of variables -->
            <!-- Second match treats the case of two variables in dependencies and the one to search is in first place -->
            <!-- Third match is same as above, but the var to search is in second place -->
            <!-- The last match is used when there is only one dependency to search -->
            <!-- I may have been able to get away with one match with searchTerm being : ^(.*[\W])?(VARIABLE)([\W].*)?$ -->
            <!-- But it has not been battle tested so I preferred to be as specific as possible to avoid side effects... -->
            <xsl:when test="matches($dependenciesToSearch, $termToSearch)
                            or matches($dependenciesToSearch,concat('^',$varName,' '))
                            or matches($dependenciesToSearch,concat(' ',$varName,'$'))
                            or matches($dependenciesToSearch,concat('^',$varName,'$'))
                            or contains($varName,'xAxis') or contains($varName,'yAxis')">
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