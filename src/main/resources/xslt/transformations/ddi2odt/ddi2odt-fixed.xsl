<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enoodt="http://xml.insee.fr/apps/eno/out/odt"
    xmlns:enoddi2fr="http://xml.insee.fr/apps/eno/ddi2form-runner"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
    xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    version="2.0">

    <!-- Importing the different resources -->
    <xsl:import href="../../inputs/ddi/source.xsl"/>
    <xsl:import href="../../outputs/odt/models.xsl"/>
    <xsl:import href="../../lib.xsl"/>
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This stylesheet is used to transform a DDI input into an Xforms form (containing orbeon form runner adherences).</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:strip-space elements="*"/>
    
    <xsl:param name="properties-file"/>
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty/>
    </xsl:param>
    
    <xsl:variable name="properties" select="doc($properties-file)"/>
    
    
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
        <xsl:sequence select="eno:build-labels-resource($labels-folder,enoodt:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"/>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Characters used to surround variables in conditioned text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="conditioning-variable-begin" select="$properties//TextConditioningVariable/ddi/Before"/>
    <xsl:variable name="conditioning-variable-end" select="$properties//TextConditioningVariable/ddi/After"/>

	
	<xsl:function name="enoodt:get-variable-business-name">
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
        <xsl:apply-templates select="//d:Sequence[d:TypeOfSequence/text()='template']" mode="source"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>This function retrieves the languages to appear in the generated Xforms.</xd:p>
            <xd:p>Those languages can be specified in a parameters file on a questionnaire level.</xd:p>
            <xd:p>If not, it will get the languages defined in the DDI input.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoodt:get-form-languages">
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
    
    <xsl:function name="enoodt:get-formatted-label">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <xsl:variable name="tempLabel">
            <xsl:apply-templates select="enoddi:get-label($context,$language)" mode="enoodt:format-label">
                <xsl:with-param name="label-variables" select="enoddi:get-label-conditioning-variables($context,$language)" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:sequence select="$tempLabel"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoodt:get-after-question-title-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'instruction,comment,help')"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoodt:get-end-question-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'footnote') | enoddi:get-next-filter-description($context)"/>
    </xsl:function>
    
       
    
    <xsl:template match="*" mode="enoodt:format-label" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="enoodt:format-label"/>      
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:p | xhtml:span" mode="enoodt:format-label">
        <xsl:apply-templates select="node()" mode="enoodt:format-label"/>
    </xsl:template>
    
    <xsl:template match="xhtml:span[@class='block']" mode="enoodt:format-label">
            <xsl:apply-templates select="node()" mode="enoodt:format-label"/>        
    </xsl:template>
        
    <xsl:template match="text()" mode="enoodt:format-label">
        <xsl:param name="label-variables" tunnel="yes"/>
        
        <xsl:if test="substring(.,1,1)=' '">
            <xsl:text xml:space="preserve"> </xsl:text>
        </xsl:if>
        <xsl:call-template name="vtl-label">
            <xsl:with-param name="label" select="normalize-space(.)"/>
            <xsl:with-param name="variables" select="$label-variables"/>
        </xsl:call-template>
        <xsl:if test="substring(.,string-length(.),1)=' ' and string-length(.) &gt; 1">
            <xsl:text xml:space="preserve"> </xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="vtl-label">
        <xsl:param name="label"/>
        <xsl:param name="variables"/>
        
        <xsl:choose>
            <xsl:when test="contains($label,$conditioning-variable-begin) and contains(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)">
                <xsl:value-of select="substring-before($label,$conditioning-variable-begin)"/>
                <xsl:variable name="variable-type">
                    <xsl:call-template name="enoddi:get-variable-type">
                        <xsl:with-param name="variable" select="substring-before(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)"/>
                    </xsl:call-template>                    
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$variable-type = 'external'">
                        <xsl:value-of select="'${'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'$!{'"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="enoddi:get-business-name">
                    <xsl:with-param name="variable" select="substring-before(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)"/>
                </xsl:call-template>
                <xsl:value-of select="'}'"/>
                <xsl:call-template name="vtl-label">
                    <xsl:with-param name="label" select="substring-after(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)"/>
                    <xsl:with-param name="variables" select="$variables"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$label"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="xhtml:i" mode="enoodt:format-label">
        <text:span text:style-name="italic">
            <xsl:apply-templates select="node()" mode="enoodt:format-label"/>
        </text:span>
    </xsl:template>
    
    <xsl:template match="xhtml:b" mode="enoodt:format-label">
        <text:span text:style-name="bold">
            <xsl:apply-templates select="node()" mode="enoodt:format-label"/>
        </text:span>
    </xsl:template>
    
    <xsl:template match="xhtml:span[@style='text-decoration:underline']" mode="enoodt:format-label">
        <text:span text:style-name="underline">
            <xsl:apply-templates select="node()" mode="enoodt:format-label"/>
        </text:span>
    </xsl:template>
    
    <xsl:template match="xhtml:br" mode="enoodt:format-label">
        <xsl:text xml:space="preserve">&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="xhtml:a[contains(@href,'#ftn')]" mode="enoodt:format-label">
        <xsl:apply-templates select="node()" mode="enoodt:format-label"/>
        <xsl:variable name="relatedInstruction" select="enoddi:get-instruction-by-anchor-ref(.,@href)"/>
        <xsl:choose>
            <xsl:when test="$relatedInstruction/d:InstructionName/r:String = 'tooltip'">
                <xsl:text xml:space="preserve"> (*)</xsl:text>
            </xsl:when>
            <xsl:when test="$relatedInstruction/d:InstructionName/r:String = 'footnote'">
                <xsl:value-of select="enoddi:get-instruction-index($relatedInstruction,'footnote')"/>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
