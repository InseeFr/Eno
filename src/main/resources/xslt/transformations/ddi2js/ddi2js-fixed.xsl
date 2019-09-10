<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enoodt="http://xml.insee.fr/apps/eno/out/odt"
    xmlns:enojs="http://xml.insee.fr/apps/eno/out/js"
    xmlns:enoddi2fr="http://xml.insee.fr/apps/eno/ddi2form-runner"
    xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    version="2.0">

    <!-- Importing the different resources -->
    <xsl:import href="../../inputs/ddi/source.xsl"/>
    <xsl:import href="../../outputs/js/models.xsl"/>
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
            <xd:p>The parameter file used by the stylesheet.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="parameters-file"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The parameters are charged as an xml tree.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="parameters" select="doc($parameters-file)"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The folder containing label resources in different languages.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="labels-folder"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A variable is created to build a set of label resources in different languages.</xd:p>
            <xd:p>Only the resources in languages already present in the DDI input are charged.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="labels-resource">
        <xsl:sequence select="eno:build-labels-resource($labels-folder,enojs:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"/>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Characters used to surround variables in conditioned text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="conditioning-variable-begin" select="$properties//TextConditioningVariable/ddi/Before"/>
    <xsl:variable name="conditioning-variable-end" select="$properties//TextConditioningVariable/ddi/After"/>

	
	<xsl:function name="enojs:get-variable-business-name">
        <xsl:param name="context" as="item()"/>
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
        <main>
           <!-- <xsl:apply-templates select="//d:Sequence[d:TypeOfSequence/text()='template']" mode="source"/>-->
            <xsl:apply-templates mode="source"/>
        </main>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>This function retrieves the languages to appear in the generated Xforms.</xd:p>
            <xd:p>Those languages can be specified in a parameters file on a questionnaire level.</xd:p>
            <xd:p>If not, it will get the languages defined in the DDI input.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enojs:get-form-languages">
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
    <xsl:function name="enojs:get-after-question-title-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'instruction,comment,help')"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enojs:get-end-question-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'footnote') | enoddi:get-next-filter-description($context)"/>
    </xsl:function>
    
    
    <xsl:function name="enojs:get-global-filter">
        <xsl:param name="context" as="item()"/>
        
        <xsl:variable name="formulaReadOnly" select="enoddi:get-deactivatable-ancestors($context)" as="xs:string*"/>
        <xsl:variable name="formulaRelevant" select="enoddi:get-hideable-ancestors($context)" as="xs:string*"/>		
        <xsl:variable name="variablesReadOnly" select="enoddi:get-deactivatable-ancestors-variables($context)" as="xs:string*"/>
        <xsl:variable name="variablesRelevant" select="enoddi:get-hideable-ancestors-variables($context)" as="xs:string*"/>

        <xsl:variable name="variableFilterId" as="xs:string*">
            <xsl:for-each select="distinct-values($variablesRelevant)">
                <xsl:sequence select="."/>
            </xsl:for-each>
            <xsl:for-each select="distinct-values($variablesReadOnly)">
                <xsl:sequence select="."/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="variableFilterName" as="xs:string*">
            <xsl:for-each select="distinct-values($variablesRelevant)">
                <xsl:sequence select="enojs:get-variable-business-name($context,.)"/>
            </xsl:for-each>
            <xsl:for-each select="distinct-values($variablesReadOnly)">
                <xsl:sequence select="."/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="variablesId" as="xs:string*">
            <xsl:for-each select="distinct-values($variableFilterId)">
                <xsl:sequence select="."/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="variablesName">
            <xsl:for-each select="distinct-values($variableFilterName)">
                <xsl:if test="position()!=1">
                    <xsl:value-of select="','"/>
                </xsl:if>
                <xsl:value-of select="."/>
            </xsl:for-each>
        </xsl:variable>
        
        <!--Expression VTL : #if(condition)je suis true#{else}je suis false#end-->
        <!-- Caution for encodage : # -> &#x23;  { -> &#x7B; } -> &#x7D; -->
        <xsl:variable name="if" select="'&#x23;if'"/>
        <xsl:variable name="else" select="'&#x23;&#x7B;else&#x7D;'"/>
        <xsl:variable name="ifEnd" select="'&#x23;end'"/>
        <xsl:choose>
            <!--<xsl:when test="$variablesName=''">
					<xsl:value-of select="'() => true ? ''normal'' : '''''"/> guillemet autour de normal ? 
					</xsl:when>-->
            <xsl:when test="$formulaRelevant!='' and $formulaReadOnly!=''">
                <xsl:variable name="initial-relevant-ancestors">
                    <xsl:for-each select="$formulaRelevant">
                        <xsl:value-of select="concat('(',.,')')"/>
                        <xsl:if test="position()!=last()">
                            <xsl:value-of select="' &amp;&amp; '"/><!-- "||" = "or"-->
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:variable name="relevant-condition">
                    <xsl:call-template name="enojs:replaceVariablesInFormula">
                        <xsl:with-param name="source-context" select="$context" as="item()" tunnel="yes"/>
                        <xsl:with-param name="formula" select="$initial-relevant-ancestors"/>
                        <xsl:with-param name="variables" select="$variablesId"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="initial-readonly-ancestors">
                    <xsl:for-each select="$formulaReadOnly">
                        <xsl:value-of select="concat('(',.,')')"/>
                        <xsl:if test="position()!=last()">
                            <xsl:value-of select="' || '"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:variable name="readonly-condition">
                    <xsl:call-template name="enojs:replaceVariablesInFormula">
                        <xsl:with-param name="source-context" select="$context" as="item()" tunnel="yes"/>
                        <xsl:with-param name="formula" select="$initial-readonly-ancestors"/>
                        <xsl:with-param name="variables" select="$variablesId"/>
                    </xsl:call-template>
                </xsl:variable>
                <!-- replace "not -> "!", " and " -> "&&", " or " -> "||", "=" -> "==" -->
                <xsl:variable name="returned-relevant-condition" select="replace(replace(replace(replace($relevant-condition,'not','!'),'\sand\s','&amp;&amp;'),'\sor\s',' || '),'\s=\s',' == ')"/>
                <xsl:variable name="returned-readonly-condition" select="replace(replace(replace(replace($readonly-condition,'not','!'),'\sand\s','&amp;&amp;'),'\sor\s',' || '),'\s=\s',' == ')"/>
                
                <!--<xsl:value-of select="concat('(',$variablesName,') =>', $readonly-condition,'toto',$relevant-condition,' ? ''normal'' : ''''')"/>-->
                <!-- les trois possibles : caché (hidden) , gris (readOnly), affiché (normal) -->
                <!--	si relevant
						alors 
						si readonly,
						alors readonly
						sinon normal
						sinon hidden-->
                <xsl:value-of select="concat(
                    $if,'(',$returned-relevant-condition,')',
                    $if,'(',$returned-readonly-condition,')readonly',
                    $else,'normal',$ifEnd,')',
                    $else,'hidden',$ifEnd
                    )"/>
            </xsl:when>
            <xsl:when test="$formulaRelevant!=''">
                <xsl:variable name="initial-relevant-ancestors">
                    <xsl:for-each select="$formulaRelevant">
                        <xsl:value-of select="concat('(',.,')')"/>
                        <xsl:if test="position()!=last()">
                            <xsl:value-of select="' &amp;&amp; '"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:variable name="relevant-condition">
                    <xsl:call-template name="enojs:replaceVariablesInFormula">
                        <xsl:with-param name="source-context" select="$context" as="item()" tunnel="yes"/>
                        <xsl:with-param name="formula" select="$initial-relevant-ancestors"/>
                        <xsl:with-param name="variables" select="$variablesId"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="returned-relevant-condition" select="replace(replace(replace(replace($relevant-condition,'not','!'),'\sand\s',' &amp;&amp; '),'\sor\s',' || '),'\s=\s',' == ')"/>
                
                <xsl:value-of select="concat($if,'(', $returned-relevant-condition,')normal',$else,'hidden',$ifEnd)"/>
                
                <!-- pas de gris, on affiche (normal) ou pas (hidden) -->
            </xsl:when>
            <xsl:when test="$formulaReadOnly!=''">
                <xsl:variable name="initial-readonly-ancestors">
                    <xsl:for-each select="$formulaReadOnly">
                        <xsl:value-of select="concat('(',.,')')"/>
                        <xsl:if test="position()!=last()">
                            <xsl:value-of select="' || '"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:variable name="readonly-condition">
                    <xsl:call-template name="enojs:replaceVariablesInFormula">
                        <xsl:with-param name="source-context" select="$context" as="item()" tunnel="yes"/>
                        <xsl:with-param name="formula" select="$initial-readonly-ancestors"/>
                        <xsl:with-param name="variables" select="$variablesId"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="returned-readonly-condition" select="replace(replace(replace(replace($readonly-condition,'not','!'),'\sand\s',' &amp;&amp; '),'\sor\s',' || '),'\s=\s',' == ')"/>
                <xsl:value-of select="concat($if,'(',$returned-readonly-condition,')readonly',$else,'normal',$ifEnd)"/>
                <!-- on ne cache pas , gris (readOnly) ou affiché (normal)-->
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:value-of select="'normal'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

</xsl:stylesheet>
