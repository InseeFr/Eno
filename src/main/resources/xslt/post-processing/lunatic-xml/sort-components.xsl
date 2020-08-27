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
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:variable name="dependencies" select="distinct-values(h:dependencies)" as="xs:string*"/>
            <xsl:for-each select="$dependencies">                
                <bindingDependencies><xsl:value-of select="."/></bindingDependencies>
            </xsl:for-each>
            <xsl:apply-templates select="h:components"/>
        </components>
    </xsl:template>
    
    <xsl:template match="h:components[@xsi:type='Loop']">
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies)" as="xs:string*"/>
            <xsl:for-each select="$dependencies">                
                <bindingDependencies><xsl:value-of select="."/></bindingDependencies>
            </xsl:for-each>
            <xsl:apply-templates select="h:components"/>
        </components>
    </xsl:template>

    <xsl:template match="h:components[@xsi:type='Table']">
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies)" as="xs:string*"/>
            <xsl:for-each select="$dependencies">                
                <bindingDependencies><xsl:value-of select="."/></bindingDependencies>
            </xsl:for-each>
            <xsl:apply-templates select="*[not(self::h:variables or self::h:cells[@type='line'] or self::h:label or self::h:declarations or self::h:conditionFilter)]"/>
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
                                <xsl:with-param name="tableId" select="@id" tunnel="yes"/>
                            </xsl:apply-templates>
                            <xsl:call-template name="enojs:addLinesForRoster">
                                <xsl:with-param name="currentLigne" select="2"/>
                                <xsl:with-param name="nbLigneMax" select="$nbLinesExpected"/>
                                <xsl:with-param name="lineToCopy" select="$cell"/>
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
        <components>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="h:label"/>
            <xsl:apply-templates select="h:declarations"/>
            <xsl:apply-templates select="h:conditionFilter"/>
            <xsl:variable name="dependencies" select="distinct-values(descendant::h:dependencies)" as="xs:string*"/>
            <xsl:for-each select="$dependencies">
                <bindingDependencies><xsl:value-of select="."/></bindingDependencies>
            </xsl:for-each>
            <xsl:apply-templates select="*[not(self::h:variables or self::h:label or self::h:declarations or self::h:conditionFilter)]"/>
        </components>
    </xsl:template>

    <xsl:template match="h:response">
        <xsl:param name="idLine" tunnel="yes"/>
        <xsl:param name="ancestor" tunnel="yes"/>
        <xsl:param name="tableId" tunnel="yes"/>
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
                </response>
                <xsl:if test="string($idLine)!='' and string($idColumn)!=''">
                    <xsl:call-template name="enojs:addVariableCollected">
                        <xsl:with-param name="responseName" select="concat(@name,'_',$idLine,'_',$idColumn)"/>
                        <xsl:with-param name="componentRef" select="$tableId"/>
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

    <xsl:template match="h:label">
        <label><xsl:value-of select="normalize-space(.)"/></label>
    </xsl:template>

    <xsl:template match="h:cells">
        <cells>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*[not(self::h:variables)]"/>
        </cells>
    </xsl:template>

    <xsl:template match="h:cells" mode="roster">
        <xsl:param name="column" tunnel="yes"/>
        <xsl:param name="tableId" tunnel="yes"/>
        <cells>
            <xsl:copy-of select="@*"/>
            <xsl:if test="string($column)!=''"><xsl:attribute name="idColumn" select="$column"/></xsl:if>
            <xsl:apply-templates select="*[not(self::h:variables)]">
                <xsl:with-param name="tableId" select="$tableId" tunnel="yes"/>
            </xsl:apply-templates>
        </cells>
    </xsl:template>

    <xsl:template name="enojs:addVariableCollected">
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

    <xsl:template name="enojs:addLinesForRoster">
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

            <xsl:call-template name="enojs:addLinesForRoster">
                <xsl:with-param name="currentLigne" select="$currentLigne +1"/>
                <xsl:with-param name="nbLigneMax" select="$nbLigneMax"/>
                <xsl:with-param name="lineToCopy" select="$lineToCopy" as="node()"/>
                <xsl:with-param name="tableId" select="$tableId"/>
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
</xsl:stylesheet>