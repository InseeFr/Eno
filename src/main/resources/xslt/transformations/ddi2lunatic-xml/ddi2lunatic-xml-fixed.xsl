<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enolunatic="http://xml.insee.fr/apps/eno/out/js"
    xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    version="2.0">
    
    <!-- Importing the different resources -->
    <xsl:import href="../../inputs/ddi/source.xsl"/>
    <xsl:import href="../../outputs/lunatic-xml/models.xsl"/>
    <xsl:import href="../../lib.xsl"/>
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This stylesheet is used to transform a DDI input into an Xforms form (containing orbeon form runner adherences).</xd:p>
        </xd:desc>
    </xd:doc>
    
    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:strip-space elements="*"/>
    
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
    <xsl:param name="labels-folder"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A variable is created to build a set of label resources in different languages.</xd:p>
            <xd:p>Only the resources in languages already present in the DDI input are charged.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="labels-resource">
        <xsl:sequence select="eno:build-labels-resource($labels-folder,enolunatic:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"/>
    </xsl:variable>
    
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
    <xsl:variable name="filterDescription">
        <xsl:choose>
            <xsl:when test="$parameters//FilterDescription != ''">
                <xsl:value-of select="$parameters//FilterDescription"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//FilterDescription"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Characters used to surround variables in conditioned text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="conditioning-variable-begin" select="$properties//TextConditioningVariable/ddi/Before"/>
    <xsl:variable name="conditioning-variable-end" select="$properties//TextConditioningVariable/ddi/After"/>
    
    <xsl:variable name="filter-type" select="$properties//Filter"/>

    <xsl:variable name="regex-var" select="'[a-zA-Z0-9\-_]*'"/>
    <xsl:variable name="regex-var-surrounded" select="concat($conditioning-variable-begin,$regex-var,$conditioning-variable-end)"/>
    <xsl:variable name="regex-var-large-surrounded" select="concat('number\(if\s+\(',$regex-var-surrounded,'=''''\)\sthen\s+''0''\s+else\s+',$regex-var-surrounded,'\)')"/>
    
    <xsl:function name="enolunatic:get-variable-business-name">
        <xsl:param name="variable"/>        
        <xsl:call-template name="enoddi:get-business-name">
            <xsl:with-param name="variable" select="$variable"/>
        </xsl:call-template>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Root template :</xd:p>
            <xd:p>The transformation starts with the main Sequence.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="//d:Sequence[d:TypeOfSequence/text()='template']" mode="source"/>
        <!--<xsl:apply-templates mode="source"/>-->        
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>This function retrieves the languages to appear in the generated Xforms.</xd:p>
            <xd:p>Those languages can be specified in a parameters file on a questionnaire level.</xd:p>
            <xd:p>If not, it will get the languages defined in the DDI input.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enolunatic:get-form-languages">
        <xsl:param name="context" as="item()"/>
        <xsl:choose>
            <xsl:when test="$parameters/Parameters/Languages">
                <xsl:for-each select="$parameters/Parameters/Languages/Language">
                    <xsl:value-of select="."/>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="enoddi:get-languages($context)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enolunatic:get-after-question-title-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'instruction,comment,help')"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enolunatic:get-end-question-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'footnote') | enoddi:get-next-filter-description($context)"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enolunatic:get-variable-type">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-variable-type($context,enoddi:get-id($context))"/>
    </xsl:function>
    
    <xsl:function name="enolunatic:get-global-filter">
        <xsl:param name="context" as="item()"/>
        
        <xsl:variable name="formulaReadOnly" select="enoddi:get-deactivatable-ancestors($context)" as="xs:string*"/>
        <xsl:variable name="formulaRelevant" select="enoddi:get-hideable-ancestors($context)" as="xs:string*"/>		

        <xsl:choose>
            <xsl:when test="$filter-type='sdmx'">
                <xsl:copy-of select="normalize-space(enolunatic:get-vtl-sdmx-filter($context,$formulaReadOnly,$formulaRelevant))"/>
            </xsl:when>
        </xsl:choose>
        
    </xsl:function>
        
    <xsl:function name="enolunatic:get-vtl-label">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <xsl:variable name="label">
            <xsl:sequence select="enoddi:get-label($context,$language)"/>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$label!=''">
                <xsl:call-template name="enolunatic:replace-variables-in-formula">
                    <xsl:with-param name="source-context" select="$context"/>
                    <xsl:with-param name="formula" select="enolunatic:surround-label-with-quote(enolunatic:replace-double-quote-by-simple-quote($label))"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="enolunatic:surround-label-with-quote">
        <xsl:param name="label"/>
        <xsl:variable name="labelSimple" select="concat('&quot;',$label,'&quot;')"/>
        <xsl:variable name="final-label">
            <xsl:analyze-string select="$labelSimple" regex="{$regex-var-surrounded}">
                <xsl:matching-substring>
                    <xsl:value-of select="concat('&quot; || ','cast(',.,',string)',' || &quot;')"/>
                </xsl:matching-substring>
                <xsl:non-matching-substring>
                    <xsl:value-of select="."/>
                </xsl:non-matching-substring>
            </xsl:analyze-string>
        </xsl:variable>
        <xsl:value-of select="$final-label"/>
    </xsl:function>
    
    <xsl:function name="enolunatic:replace-double-quote-by-simple-quote">        
        <xsl:param name="label"/>
        <xsl:value-of select="replace($label,'&quot;','''')"/>
    </xsl:function>
    
    <xsl:function name="enolunatic:get-vtl-sdmx-filter">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="formulaReadOnly" as="xs:string*"/>
        <xsl:param name="formulaRelevant" as="xs:string*"/>
        
        <!--Expression VTL : if(condition) then "i'm true" else "i'm false"-->
        <xsl:variable name="if" select="' if '"/>
        <xsl:variable name="then" select="' then '"/>
        <xsl:variable name="else" select="' else '"/>
        <xsl:variable name="ifEnd" select="''"/>
        
        <xsl:variable name="and-logic" select="' and '"/>
        <xsl:variable name="or-logic" select="' or '"/>
        <xsl:variable name="not-logic" select="' not '"/>            
        <xsl:variable name="not-equal" select="' &lt;&gt; '"/>
        
        <xsl:variable name="readonly" select="'&quot;readonly&quot;'"/>
        <xsl:variable name="normal" select="'&quot;normal&quot;'"/>
        <xsl:variable name="hidden" select="'&quot;hidden&quot;'"/>
        
        <conditionFilter>
            <xsl:choose>
                <xsl:when test="$formulaRelevant!='' and $formulaReadOnly!=''">
                    <xsl:variable name="initial-relevant-ancestors">
                        <xsl:for-each select="$formulaRelevant">
                            <xsl:value-of select="concat('(',.,')')"/>
                            <xsl:if test="position()!=last()">
                                <xsl:value-of select="$and-logic"/>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:variable name="relevant-condition">
                        <xsl:call-template name="enolunatic:replace-variables-in-formula">
                            <xsl:with-param name="source-context" select="$context"/>
                            <xsl:with-param name="formula" select="$initial-relevant-ancestors"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="initial-readonly-ancestors">
                        <xsl:for-each select="$formulaReadOnly">
                            <xsl:value-of select="concat('(',.,')')"/>
                            <xsl:if test="position()!=last()">
                                <xsl:value-of select="$or-logic"/>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:variable name="readonly-condition">
                        <xsl:call-template name="enolunatic:replace-variables-in-formula">
                            <xsl:with-param name="source-context" select="$context"/>
                            <xsl:with-param name="formula" select="$initial-readonly-ancestors"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="returned-relevant-condition" select="$relevant-condition"/>
                    <xsl:variable name="returned-readonly-condition" select="$readonly-condition"/>                    
                    
                    <!-- three cases : caché (hidden) , gris (readOnly), affiché (normal) -->
                    <!--	
                        if relevant
                        then 
                        if readonly,
                        then readonly
                        else normal
                        else hidden-->
                    <xsl:value-of select="concat(
                        $if,'(',$returned-relevant-condition,')',
                        $then,
                        '(',$if,'(',$returned-readonly-condition,')',
                        $then,$readonly,
                        $else,$normal,
                        $ifEnd,')',
                        $else,$hidden,
                        $ifEnd
                        )"/>
                </xsl:when>
                <xsl:when test="$formulaRelevant!=''">
                    <xsl:variable name="initial-relevant-ancestors">
                        <xsl:for-each select="$formulaRelevant">
                            <xsl:value-of select="concat('(',.,')')"/>
                            <xsl:if test="position()!=last()">
                                <xsl:value-of select="$and-logic"/>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:variable name="relevant-condition">
                        <xsl:call-template name="enolunatic:replace-variables-in-formula">
                            <xsl:with-param name="source-context" select="$context"/>
                            <xsl:with-param name="formula" select="$initial-relevant-ancestors"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="returned-relevant-condition" select="$relevant-condition"/>                    
                    <xsl:value-of select="concat($if,'(', $returned-relevant-condition,')',$then,$normal,$else,$hidden,$ifEnd)"/>                    
                </xsl:when>
                <xsl:when test="$formulaReadOnly!=''">
                    <xsl:variable name="initial-readonly-ancestors">
                        <xsl:for-each select="$formulaReadOnly">
                            <xsl:value-of select="concat('(',.,')')"/>
                            <xsl:if test="position()!=last()">
                                <xsl:value-of select="$or-logic"/>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:variable name="readonly-condition">
                        <xsl:call-template name="enolunatic:replace-variables-in-formula">
                            <xsl:with-param name="source-context" select="$context"/>
                            <xsl:with-param name="formula" select="$initial-readonly-ancestors"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="returned-readonly-condition" select="$readonly-condition"/>
                    <xsl:value-of select="concat($if,'(',$returned-readonly-condition,')',$then,$readonly,$else,$normal,$ifEnd)"/>
                </xsl:when>
                
                <xsl:otherwise>
                    <xsl:value-of select="$normal"/>
                </xsl:otherwise>
            </xsl:choose>
        </conditionFilter>
    </xsl:function>
    
    
    <xsl:function name="enolunatic:replace-variable-with-collected-and-external-variables-formula">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="variable"/>

        <xsl:variable name="temp" select="enoddi:get-generation-instruction($context,enolunatic:get-variable-business-name($variable))"/>
        <xsl:choose>
            <xsl:when test="$temp!=''">
                <xsl:value-of select="enoddi:get-variable-calculation($temp)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$variable"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="enolunatic:replace-all-variables-with-business-name">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="formula"/>

        <xsl:variable name="temp-formula">
            <xsl:choose>
                <xsl:when test="$formula">
                    <xsl:analyze-string select="$formula" regex="{$regex-var-large-surrounded}">
                        <xsl:matching-substring>
                            <xsl:variable name="temp" select="replace(replace(.,
                                concat('number\(if\s+\(',$regex-var-surrounded,'=''''\)\sthen\s+''0''\s+else\s+',$conditioning-variable-begin),''),
                                concat($conditioning-variable-end,'\)'),'')"/>
                            <xsl:variable name="var" select="replace(replace($temp,$conditioning-variable-begin,''),$conditioning-variable-end,'')"/>
                            <xsl:variable name="typeVariable" select="enoddi:get-variable-representation($context,$var)"/>
                            <xsl:value-of select="enolunatic:get-cast-variable($typeVariable,enolunatic:get-variable-business-name($var))"/>
                        </xsl:matching-substring>
                        <xsl:non-matching-substring>
                            <xsl:value-of select="."/>
                        </xsl:non-matching-substring>
                    </xsl:analyze-string>
                </xsl:when>
                <xsl:otherwise><xsl:value-of select="$formula"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="final-formula">
            <xsl:choose>
                <xsl:when test="$temp-formula">
                    <xsl:analyze-string select="$temp-formula" regex="{$regex-var-surrounded}">
                        <xsl:matching-substring>
                            <xsl:variable name="var" select="replace(replace(.,$conditioning-variable-begin,''),$conditioning-variable-end,'')"/>
                            <xsl:variable name="typeVariable" select="enoddi:get-variable-representation($context,$var)"/>
                            <xsl:value-of select="enolunatic:get-cast-variable($typeVariable,enolunatic:get-variable-business-name($var))"/>
                        </xsl:matching-substring>
                        <xsl:non-matching-substring>
                            <xsl:value-of select="."/>
                        </xsl:non-matching-substring>
                    </xsl:analyze-string>
                </xsl:when>
                <xsl:otherwise><xsl:value-of select="$temp-formula"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$final-formula"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function: enolunatic:get-cast-variable.</xd:p>
            <xd:p>It returns the type of variable, string, number, integer, boolean</xd:p>
            <xd:p>variableName -> cast(variableName,type)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enolunatic:get-cast-variable">
        <xsl:param name="type"/>
        <xsl:param name="variable"/>
        <xsl:choose>
            <xsl:when test="$type='text'">
                <xsl:value-of select="concat('cast(',$variable,',','string)')"/>
            </xsl:when>
            <xsl:when test="$type='integer' or $type='decimal'">
                <xsl:value-of select="concat('cast(',$variable,',','number)')"/>
            </xsl:when>
            <xsl:when test="$type='boolean'">
                <xsl:value-of select="concat('cast(',$variable,',','integer)')"/>
            </xsl:when>
            <xsl:when test="$type='date'"></xsl:when>
            <xsl:when test="$type='duration'"></xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat('cast(',$variable,',','string)')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="enolunatic:find-variables-in-formula">
        <xsl:param name="formula"/>
        <xsl:if test="$formula">
            <xsl:variable name="variables" as="xs:string*">
                <xsl:analyze-string select="$formula" regex="{$regex-var-large-surrounded}">
                    <xsl:matching-substring>
                        <xsl:variable name="temp" select="replace(replace(.,
                                concat('number\(if\s+\(',$regex-var-surrounded,'=''''\)\sthen\s+''0''\s+else\s+',$conditioning-variable-begin),''),
                                concat($conditioning-variable-end,'\)'),'')"/>
                        <xsl:variable name="var" select="replace(replace($temp,$conditioning-variable-begin,''),$conditioning-variable-end,'')"/>
                        <xsl:copy-of select="$var"/>
                    </xsl:matching-substring>
                </xsl:analyze-string>
                <xsl:analyze-string select="$formula" regex="{$regex-var-surrounded}">
                    <xsl:matching-substring>
                        <xsl:variable name="var" select="replace(replace(.,$conditioning-variable-begin,''),$conditioning-variable-end,'')"/>
                        <xsl:copy-of select="$var"/>
                    </xsl:matching-substring>
                </xsl:analyze-string>
            </xsl:variable>

            <xsl:for-each select="distinct-values($variables)">
                <xsl:sequence select="."/>
            </xsl:for-each>
        </xsl:if>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Recursive named template: enolunatic:replace-variables-in-formula.</xd:p>
            <xd:p>It replaces variables in a all formula (filter, control, personalized text, calculated variable).</xd:p>
            <xd:p>"number(if (¤idVariable¤='') then '0' else ¤idVariable¤)" -> "variableName"</xd:p>
            <xd:p>"¤idVariable¤" -> "variableName"</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="enolunatic:replace-variables-in-formula">
        <xsl:param name="source-context" as="item()"/>
        <xsl:param name="formula"/>

        <xsl:variable name="variablesFound" as="xs:string*" select="enolunatic:find-variables-in-formula($formula)"/>

        <xsl:variable name="conditions" as="xs:boolean*">
            <xsl:for-each select="$variablesFound">
                <xsl:value-of select=".!=enolunatic:replace-variable-with-collected-and-external-variables-formula($source-context,.)"/>
            </xsl:for-each>
        </xsl:variable>

        <xsl:variable name="conditionToContinue" as="xs:boolean">
            <xsl:value-of select="count($conditions[.=true()])=count($conditions) and count($conditions)!=0"/>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="$conditionToContinue">
                <xsl:variable name="temp-formula">
                    <xsl:analyze-string select="$formula" regex="{$regex-var-large-surrounded}">
                        <xsl:matching-substring>
                            <xsl:variable name="temp" select="replace(replace(.,
                                concat('number\(if\s+\(',$regex-var-surrounded,'=''''\)\sthen\s+''0''\s+else\s+',$conditioning-variable-begin),''),
                                concat($conditioning-variable-end,'\)'),'')"/>
                            <xsl:variable name="var" select="replace(replace($temp,$conditioning-variable-begin,''),$conditioning-variable-end,'')"/>
                            <xsl:variable name="typeVariable" select="enoddi:get-variable-representation($source-context,$var)"/>
                            <xsl:variable name="value-var" select="enolunatic:replace-variable-with-collected-and-external-variables-formula(
                                $source-context,
                                $var)"/>
                            <xsl:choose>
                                <xsl:when test="$var!=$value-var">
                                    <xsl:value-of select="enolunatic:get-cast-variable($typeVariable,$value-var)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="concat($conditioning-variable-begin,$var,$conditioning-variable-end)"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:matching-substring>
                        <xsl:non-matching-substring>
                            <xsl:value-of select="."/>
                        </xsl:non-matching-substring>
                    </xsl:analyze-string>
                </xsl:variable>
                <xsl:variable name="new-formula">
                    <xsl:analyze-string select="$temp-formula" regex="{$regex-var-surrounded}">
                        <xsl:matching-substring>
                            <xsl:variable name="var" select="replace(replace(.,$conditioning-variable-begin,''),$conditioning-variable-end,'')"/>
                            <xsl:variable name="typeVariable" select="enoddi:get-variable-representation($source-context,$var)"/>
                            <xsl:variable name="value-var" select="enolunatic:replace-variable-with-collected-and-external-variables-formula(
                                $source-context,
                                $var)"/>
                            <xsl:choose>
                                <xsl:when test="$var!=$value-var">
                                    <xsl:value-of select="enolunatic:get-cast-variable($typeVariable,$value-var)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="concat($conditioning-variable-begin,$var,$conditioning-variable-end)"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:matching-substring>
                        <xsl:non-matching-substring>
                            <xsl:value-of select="."/>
                        </xsl:non-matching-substring>
                    </xsl:analyze-string>
                </xsl:variable>
                <xsl:call-template name="enolunatic:replace-variables-in-formula">
                    <xsl:with-param name="source-context" select="$source-context"/>
                    <xsl:with-param name="formula" select="$new-formula"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$formula"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>