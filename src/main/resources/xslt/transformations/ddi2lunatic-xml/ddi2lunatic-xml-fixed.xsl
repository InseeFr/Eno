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
    <xsl:variable name="addFilterResult" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//AddFilterResult != ''">
                <xsl:value-of select="$parameters//AddFilterResult"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//AddFilterResult"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="missingVar" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//MissingVar != ''">
                <xsl:value-of select="$parameters//MissingVar"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//MissingVar"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="controlParam" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//Control != ''">
                <xsl:value-of select="$parameters//Control"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//Control"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="enoVersion" select="$properties//EnoVersion"/>
    
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
    
    <xsl:variable name="is-xpath" select="enoddi:is-programlanguage-xpath(/)"/>
    
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
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'instruction,comment,help,codecard')"/>
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
            <xsl:choose>
                <xsl:when test="$is-xpath">
                    <xsl:sequence select="enoddi:get-label($context,$language)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="enolunatic:tidy-label(enoddi:get-label($context,$language))"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:if test="$label!=''">
            <!-- If the programming language is xpath : we call surround-label-with-quote -->
            <xsl:choose>
                <xsl:when test="$is-xpath">
                    <xsl:value-of select="enolunatic:surround-label-with-quote(enolunatic:encode-special-char-in-js($label))"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="enolunatic:encode-special-char-in-js($label)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:function>
    
    <xsl:function name="enolunatic:tidy-label">
        <xsl:param name="label"/>
        <xsl:variable name="final">
            <xsl:choose>
                <xsl:when test="count($label) &gt; 1">
                    <xsl:variable name="number" select="$label[1]"/>
                    <xsl:variable name="other" select="$label[position() &gt; 1]"/>
                    <xsl:value-of select="concat('&quot;',$number,'&quot; || ')"/>
                    <xsl:for-each select="$other">
                        <xsl:choose>
                            <xsl:when test="matches($other,'^&quot;.*&quot;$|.*\|\|.*|cast\(.*,string\)')">
                                <xsl:value-of select="."/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('&quot;',.,'&quot;')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                </xsl:when>
                <xsl:when test="$label != ''">
                    <xsl:choose>
                        <xsl:when test="matches($label,'^[0-9\-]')">
                            <xsl:value-of select="concat('&quot;',$label,'&quot; || &quot;&quot;')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$label"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$final"/>
    </xsl:function>
    
    <!-- This function is used for labels when programming language is xpath -->
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
    
    <xsl:function name="enolunatic:encode-special-char-in-js">
        <xsl:param name="label"/>
        <xsl:value-of select="enolunatic:special-treatment-for-tooltip($label)"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>This function replace [Data](. "example of tooltip") by [Data](. 'example of tooltip')</xd:p>
            <xd:p>The goal is to prevent quotes from interfering with VTL</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enolunatic:special-treatment-for-tooltip">
        <xsl:param name="label"/>        
        <xsl:variable name="listChar" select="$properties//JSEncoding/char"/>
        <xsl:analyze-string select="$label" regex="(\[([^\]]+)\])\(\. &quot;([^&quot;]+)&quot;\)">
            <xsl:matching-substring>
                <xsl:value-of select="concat(regex-group(1),'(. ''',enolunatic:recursive-replace(regex-group(3),$listChar),''')')"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <!-- replace special JS character by their encoded value-->
                <xsl:value-of select="enolunatic:recursive-replace(.,$listChar)"/>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:function>
    
    <xsl:function name="enolunatic:recursive-replace">
        <xsl:param name="label"/>
        <xsl:param name="listChar"/>
        <xsl:choose>
            <xsl:when test="count($listChar) &gt; 0">
                <xsl:variable name="newLabel">
                    <xsl:variable name="in" select="$listChar[1]/in"/>           
                    <xsl:variable name="out" select="$listChar[1]/out"/>
                    <xsl:value-of select="replace($label,$in,$out)"/>
                </xsl:variable>
                <xsl:value-of select="enolunatic:recursive-replace($newLabel,$listChar[position() &gt; 1])"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$label"/>
            </xsl:otherwise>
        </xsl:choose>
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
        <xsl:variable name="true" select="'true'"/>
        
        <conditionFilter>
            <xsl:choose>
                <xsl:when test="$formulaRelevant!=''">
                    <xsl:variable name="initial-relevant-ancestors">
                        <xsl:for-each select="$formulaRelevant">
                            <xsl:value-of select="concat('(',.,')')"/>
                            <xsl:if test="position()!=last()">
                                <xsl:value-of select="$and-logic"/>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:variable name="relevant-condition" select="$initial-relevant-ancestors"/>
                    <xsl:variable name="returned-relevant-condition" select="$relevant-condition"/>                    
                    <xsl:value-of select="$returned-relevant-condition"/>                    
                </xsl:when>                
                <xsl:otherwise>
                    <xsl:value-of select="$true"/>
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
            <xsl:when test="$is-xpath">
                <xsl:choose>
                    <xsl:when test="$type='text'">
                        <xsl:value-of select="concat('cast(',$variable,',','string)')"/>
                    </xsl:when>
                    <xsl:when test="$type='integer'">
                        <xsl:value-of select="concat('cast(',$variable,',','integer)')"/>
                    </xsl:when>
                    <xsl:when test="$type='decimal'">
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
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$variable"/>
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
            <xd:p>"number(if (¤idVariable¤='') then '0' else ¤idVariable¤)" -> ¤idVariable¤</xd:p>
            <xd:p>"¤idVariableCalculatedVar¤" -> formula</xd:p>
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
            <xsl:value-of select="count($conditions[.=true()]) &gt;0 and count($conditions)!=0"/>
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
    
    
    <xd:doc>
        <xd:desc>
            <xd:p>Recursive named template: enolunatic:resolve-variables-to-collected-and-external-variables.</xd:p>
            <xd:p>It searches variables until collected and external in all formula for caculated variables.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="enolunatic:resolve-variables-to-collected-and-external-variables">
        <xsl:param name="source-context" as="item()"/>
        
        <!-- We track 3 lists : 
            - listvar : complete list of variables encountered that we want to resolve 
            - listVarResolved : list of variables which have already been resolved
            - listVarFinal : list of variables which are collected or external (so no need to go further, those will be the variables we will return in the end)
        -->
        <xsl:param name="listVar" as="xs:string*"/>
        <xsl:param name="listVarResolved" select="''" as="xs:string*"/>
        <xsl:param name="listVarFinal" select="''" as="xs:string*"/>
        
        <!-- I create a flat list from listVar, that way I can easily tokenize elements later : needed because it seems the format returned by function find-variables is inconsistent -->
        <xsl:variable name="listVarFlat">
            <xsl:value-of select="$listVar"/>
        </xsl:variable>
        
        <!-- We check if there are still variables which need to be resolved, e.g. that are not in the listVarResolved -->
        <xsl:variable name="unresolvedVars" as="xs:string*">
            <xsl:for-each select="tokenize($listVarFlat,' ')">
                <xsl:variable name="currentVar" select="."/>
                <xsl:if test="not(contains($listVarResolved,$currentVar))">
                    <xsl:value-of select="$currentVar"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:variable name="existsUnresolvedVars" as="xs:boolean">
            <xsl:value-of select="$unresolvedVars != ''"/>
        </xsl:variable>

        <!-- We check the value of existsUnresolvedVars, which is true if there are still variables to resolve. If there are not, it is our stop condition and we will return the listVarFinal -->
        <xsl:choose>
            <xsl:when test="$existsUnresolvedVars">
                <!-- If there are still unresolved variables, we will resolve the first one (it could be any, really, as it will become resolved in the next step) -->
                <xsl:variable name="var" select="$unresolvedVars[1]"/>
                <xsl:variable name="typeVariable" select="enoddi:get-variable-representation($source-context,$var)"/>
                <xsl:variable name="value-var" select="enolunatic:replace-variable-with-collected-and-external-variables-formula(
                    $source-context,
                    $var)"/>
                
                <!-- There are 2 cases :
                    
                    - var!=value-var means that the variable is calculated and we need further resolution. Thus :
                        - We add the variables encountered in the current calculated variable to the complete list of variables (listToResolve)
                        - We add the current calculated variable in the list of resolved variables (listResolved)
                        - The list of final variables remains unchanged, as our current variable was not collected or external (listFinal)
                        
                    - var=value-var means that the variable is collected or external, so the resolution stops here. Thus :
                        - The complete list of variables remains unchanged : no new variables (listToResolve)
                        - We add the current variable to the list of resolved variables (listResolved)
                        - We add the current variable to the list of final variables (listFinal)
                        
                   I create the variable newListSet containing the new lists to use according to each case
                -->
                <xsl:variable name="newListSet">
                    <xsl:choose>
                        <xsl:when test="$var!=$value-var">
                            <xsl:variable name="variablesFound" as="xs:string*" select="enolunatic:find-variables-in-formula($value-var)"/>
                            <!-- Since I have a hard time knowing which type of value I have, the simplest thing is a dirty concatenate using xsl:value -->
                            <listToResolve>
                                <xsl:value-of select="$listVar"/>
                                <xsl:value-of select="' '"/>
                                <xsl:value-of select="$variablesFound"/>
                            </listToResolve>
                            <listResolved>
                                <xsl:value-of select="$listVarResolved"/>
                                <xsl:value-of select="' '"/>
                                <xsl:value-of select="$var"/>
                            </listResolved>
                            <listFinal>
                                <xsl:value-of select="$listVarFinal"/>
                            </listFinal>
                        </xsl:when>
                        <xsl:otherwise>
                            <listToResolve>
                                <xsl:value-of select="$listVar"/>
                            </listToResolve>
                            <listResolved>
                                <xsl:value-of select="$listVarResolved"/>
                                <xsl:value-of select="' '"/>
                                <xsl:value-of select="$var"/>
                            </listResolved>
                            <listFinal>
                                <xsl:value-of select="$listVarFinal"/>
                                <xsl:value-of select="' '"/>
                                <xsl:value-of select="$var"/>
                            </listFinal>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <!-- Based on the newListSet containing the different new lists (complete, resolved and final) we recursively call the function with those new lists -->
                <!-- I normalize-space since I fear unexpected behaviour with the dirty concatenate -->
                <xsl:call-template name="enolunatic:resolve-variables-to-collected-and-external-variables">
                    <xsl:with-param name="source-context" select="$source-context"/>
                    <xsl:with-param name="listVar" select="normalize-space($newListSet//listToResolve)"/>
                    <xsl:with-param name="listVarResolved" select="normalize-space($newListSet//listResolved)"/>
                    <xsl:with-param name="listVarFinal" select="normalize-space($newListSet//listFinal)"/>
                </xsl:call-template>
                
            </xsl:when>
            
            <!-- If there are no more unresolved variables, we can return the list of final variables, which are all collected and external -->
            <xsl:otherwise>
                <!-- To have the expected format for the expressionDependencies, we need to return a sequence of variables, similarly to find-variables-in-formula -->
                <xsl:for-each select="distinct-values(tokenize($listVarFinal,' '))">
                    <xsl:sequence select="."/>
                </xsl:for-each>
            </xsl:otherwise>
            
        </xsl:choose>
        
    </xsl:template>
        
    <xsl:function name="enolunatic:is-generating-loop" as="xs:boolean">
        <xsl:param name="context" as="item()"/>
        <xsl:variable name="isLinkedLoop" select="enoddi:is-linked-loop($context)" as="xs:boolean"/>
        <xsl:variable name="linkedContainers" select="enoddi:get-linked-containers($context)"/>
        <xsl:value-of select="not($isLinkedLoop) and count($linkedContainers) &gt; 1 and enoddi:get-id($context)=enoddi:get-id($linkedContainers[1])"/>
    </xsl:function>
    
    <xsl:function name="enolunatic:get-loop-generator-id">
        <xsl:param name="context" as="item()"/>
        <xsl:variable name="linkedContainers" select="enoddi:get-linked-containers($context)" as="item()*"/>
        <xsl:value-of select="enoddi:get-id($linkedContainers[1])"/>
    </xsl:function>
    
    <xsl:function name="enolunatic:get-shapeFrom-name">
        <xsl:param name="id"/>
        <xsl:param name="type"/>
        <xsl:param name="language"/>
        <xsl:call-template name="enoddi:get-controlconstructreference-name">
            <xsl:with-param name="id" select="$id"/>
            <xsl:with-param name="type" select="$type"/>
            <xsl:with-param name="language" select="$language"/>
        </xsl:call-template>
    </xsl:function>
    
    <!-- This function will return for a given label its type : 
    STRING : if it is a plain text string needing no treatment 
    VTL : if it contains a VTL expression that needs to be evaluated
    MD : if it contains markdown that needs to be evaluated
    VTL|MD : if it contains both a VTL expression and markdown that need to be evaluated-->
    <!-- Right now, it is simply a mapping between the location of the label in the Lunatic questionnaire and STRING, VTL and/or MD -->
    <!-- It is possibly meant to be an information item to retrieve from the DDI, in which case it should become a proper Eno getter -->
    <xsl:function name="enolunatic:get-label-type">
        <xsl:param name="locationOfLabel"/>
        <xsl:choose>
            <xsl:when test="$locationOfLabel='label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
            <xsl:when test="$locationOfLabel='responses.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
            <xsl:when test="$locationOfLabel='hierarchy.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
            <xsl:when test="$locationOfLabel='hierarchy.subSequence.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
            <xsl:when test="$locationOfLabel='hierarchy.sequence.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
            <xsl:when test="$locationOfLabel='declarations.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
            <xsl:when test="$locationOfLabel='controls.control'"><xsl:value-of select="'VTL'"/></xsl:when>
            <xsl:when test="$locationOfLabel='controls.errorMessage'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
            <xsl:when test="$locationOfLabel='options.label'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
            <xsl:when test="$locationOfLabel='lines.min'"><xsl:value-of select="'VTL'"/></xsl:when>
            <xsl:when test="$locationOfLabel='lines.max'"><xsl:value-of select="'VTL'"/></xsl:when>
            <xsl:when test="$locationOfLabel='iterations'"><xsl:value-of select="'VTL|MD'"/></xsl:when>
            <xsl:when test="$locationOfLabel='conditionFilter'"><xsl:value-of select="'VTL'"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="'VTL|MD'"/></xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
</xsl:stylesheet>