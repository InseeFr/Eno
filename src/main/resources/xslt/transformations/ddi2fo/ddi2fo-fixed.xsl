<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enofo="http://xml.insee.fr/apps/eno/out/fo"
    xmlns:enoddi2fo="http://xml.insee.fr/apps/eno/ddi2fo"
    xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:l="ddi:logicalproduct:3_3" version="2.0">

    <!-- Importing the different resources -->
    <xsl:import href="../../inputs/ddi/source.xsl"/>
    <xsl:import href="../../outputs/fo/models.xsl"/>
    <xsl:import href="../../lib.xsl"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This stylesheet is used to transform a DDI input into an Xforms form (containing orbeon form runner adherences).</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <!--<xsl:strip-space elements="*"/>-->

    <xd:doc>
        <xd:desc>
            <xd:p>The folder containing label resources in different languages.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="labels-folder"/>

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
    
    <xsl:variable name="page-model-default" select="doc('../../../xslt/post-processing/fo/page-model/page-model-default.fo')"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A variable is created to build a set of label resources in different languages.</xd:p>
            <xd:p>Only the resources in languages already present in the DDI input are charged.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="labels-resource">
        <xsl:sequence select="eno:build-labels-resource($labels-folder,enofo:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"/>
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
    <xsl:variable name="orientation">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Format/Orientation != ''">
                <xsl:value-of select="$parameters//fo-parameters/Format/Orientation"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Format/Orientation"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="column-count">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Format/Columns != ''">
                <xsl:value-of select="$parameters//fo-parameters/Format/Columns"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Format/Columns"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="roster-minimum-empty-row" as="xs:integer">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Roster/Row/MinimumEmpty != ''">
                <xsl:value-of select="$parameters//fo-parameters/Roster/Row/MinimumEmpty"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Roster/Row/MinimumEmpty"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="roster-defaultsize" as="xs:integer">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Roster/Row/DefaultSize != ''">
                <xsl:value-of select="$parameters//fo-parameters/Roster/Row/DefaultSize"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Roster/Row/DefaultSize"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="loop-default-occurrence" as="xs:integer">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Loop/DefaultOccurrence != ''">
                <xsl:value-of select="$parameters//fo-parameters/Loop/DefaultOccurrence"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Loop/DefaultOccurrence"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="loop-minimum-empty-occurrence" as="xs:integer">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Loop/MinimumEmptyOccurrence != ''">
                <xsl:value-of select="$parameters//fo-parameters/Loop/MinimumEmptyOccurrence"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Loop/MinimumEmptyOccurrence"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="table-defaultsize">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Table/Row/DefaultSize != ''">
                <xsl:value-of select="$parameters//fo-parameters/Table/Row/DefaultSize"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Table/Row/DefaultSize"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="textarea-defaultsize">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/TextArea/Row/DefaultSize != ''">
                <xsl:value-of select="$parameters//fo-parameters/TextArea/Row/DefaultSize"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/TextArea/Row/DefaultSize"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="images-folder">
        <xsl:choose>
            <xsl:when test="$parameters//Images/Folder != ''">
                <xsl:value-of select="$parameters//Images/Folder"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//Images/Folder"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="numeric-capture">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Capture/Numeric != ''">
                <xsl:value-of select="$parameters//fo-parameters/Capture/Numeric"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Capture/Numeric"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="page-break-between">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/PageBreakBetween/pdf != ''">
                <xsl:value-of select="$parameters//fo-parameters/PageBreakBetween/pdf"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/PageBreakBetween/pdf"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="initialize-all-variables">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/InitializeAllVariables  != ''">
                <xsl:value-of select="$parameters//fo-parameters/InitializeAllVariables "/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/InitializeAllVariables "/>
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
            <xd:p>Linking output function enofo:get-body-line to input function enoddi:get-table-line.</xd:p>
            <xd:p>This function has too many parameters to stay in the functions.fods file</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-body-line">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="index"/>
        <xsl:param name="table-first-line"/>
        <xsl:sequence select="enoddi:get-table-line($context,$index,$table-first-line)"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Linking output function enofo:get-rowspan to input function enoddi:get-rowspan.</xd:p>
            <xd:p>This function has too many parameters to stay in the functions.fods file</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-rowspan">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="table-first-line"/>
        <xsl:param name="table-last-line"/>
        <xsl:sequence select="enoddi:get-rowspan($context,$table-first-line,$table-last-line)"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>This function retrieves the languages to appear in the generated Xforms.</xd:p>
            <xd:p>Those languages can be specified in a parameters file on a questionnaire level.</xd:p>
            <xd:p>If not, it will get the languages defined in the DDI input.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-form-languages">
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

    <xsl:function name="enofo:get-label">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <xsl:param name="loop-navigation" as="node()"/>
        <xsl:variable name="tempLabel">
            <xsl:apply-templates select="enoddi:get-label($context,$language)" mode="enofo:format-label">
                <xsl:with-param name="label-variables" select="enoddi:get-label-conditioning-variables($context,$language)" tunnel="yes"/>
                <xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:sequence select="$tempLabel"/>
    </xsl:function>
    
    <xsl:function name="enofo:get-fixed-value">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <xsl:param name="loop-navigation" as="node()"/>
        <xsl:variable name="tempLabel">
            <xsl:apply-templates select="enoddi:get-cell-value($context)" mode="enofo:format-label">
                <xsl:with-param name="label-variables" select="enoddi:get-cell-value-variables($context)" tunnel="yes"/>
                <xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:sequence select="$tempLabel"/>
    </xsl:function>

    <xsl:template match="*" mode="enofo:format-label" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="enofo:format-label"/>
        </xsl:copy>
    </xsl:template>
     
    <xsl:template match="xhtml:p | xhtml:span" mode="enofo:format-label">
        <xsl:apply-templates select="node()" mode="enofo:format-label"/>
    </xsl:template>
    
    <xsl:template match="xhtml:span[@class='block']" mode="enofo:format-label">
        <xsl:element name="fo:block">
            <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        </xsl:element>
    </xsl:template>
    
<!--
    <xsl:template match="*[not(descendant-or-self::xhtml:*)]" mode="enofo:format-label">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="enofo:format-label"/>
        </xsl:copy>
    </xsl:template>-->

    <xsl:template match="text()" mode="enofo:format-label">
        <xsl:param name="label-variables" tunnel="yes"/>
        <xsl:param name="loop-navigation" tunnel="yes" as="node()"/>
        
        <xsl:if test="substring(.,1,1)=' '">
            <xsl:text xml:space="preserve"> </xsl:text>
        </xsl:if>
        <xsl:call-template name="velocity-label">
            <xsl:with-param name="label" select="normalize-space(.)"/>
            <xsl:with-param name="variables" select="$label-variables"/>
            <xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()"/>
        </xsl:call-template>
        <xsl:if test="substring(.,string-length(.),1)=' ' and string-length(.) &gt; 1">
            <xsl:text xml:space="preserve"> </xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template name="velocity-label">
        <xsl:param name="label"/>
        <xsl:param name="variables"/>
        <xsl:param name="loop-navigation" as="node()"/>
        
        <xsl:choose>
            <xsl:when test="contains($label,$conditioning-variable-begin) and contains(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)">
                <xsl:value-of select="substring-before($label,$conditioning-variable-begin)"/>
                <xsl:variable name="variable-name" select="substring-before(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)"/>
                <xsl:variable name="variable-type">
                    <xsl:call-template name="enoddi:get-variable-type">
                        <xsl:with-param name="variable" select="$variable-name"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="variable-ancestors" as="xs:string *">
                    <xsl:call-template name="enoddi:get-business-ancestors">
                        <xsl:with-param name="variable" select="$variable-name"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$variable-ancestors != ''">
                        <xsl:variable name="current-ancestor" select="$variable-ancestors[last()]"/>
                        <xsl:choose>
                            <xsl:when test="$loop-navigation//Loop[@name=$current-ancestor]/text() != ''">
                                <xsl:value-of select="concat('$!{',$current-ancestor,'-0-')"/>
                            </xsl:when>
                            <xsl:when test="$variable-type = 'external'">
                                <xsl:value-of select="concat('${',$current-ancestor,'.')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('$!{',$current-ancestor,'.')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$variable-type = 'external'">
                        <xsl:value-of select="'${'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'$!{'"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="enoddi:get-business-name">
                    <xsl:with-param name="variable" select="$variable-name"/>
                </xsl:call-template>
                <xsl:value-of select="'}'"/>
                <xsl:call-template name="velocity-label">
                    <xsl:with-param name="label" select="substring-after(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)"/>
                    <xsl:with-param name="variables" select="$variables"/>
                    <xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$label"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="xhtml:i" mode="enofo:format-label">
        <xsl:element name="fo:inline">
            <xsl:attribute name="font-style" select="'italic'"/>
            <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xhtml:b" mode="enofo:format-label">
        <xsl:element name="fo:inline">
            <xsl:attribute name="font-weight" select="'bold'"/>
            <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xhtml:span[@style='text-decoration:underline']" mode="enofo:format-label">
        <xsl:element name="fo:wrapper">
            <xsl:attribute name="text-decoration" select="'underline'"/>
            <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xhtml:br" mode="enofo:format-label">
        <xsl:text xml:space="preserve">&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="xhtml:a[contains(@href,'#ftn')]" mode="enofo:format-label">
        <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        <xsl:variable name="relatedInstruction" select="enoddi:get-instruction-by-anchor-ref(.,@href)"/>
        <xsl:choose>
            <xsl:when test="$relatedInstruction/d:InstructionName/r:String = 'tooltip'">
                <xsl:text>*</xsl:text>
            </xsl:when>
            <xsl:when test="$relatedInstruction/d:InstructionName/r:String = 'footnote'">
                <xsl:value-of select="enoddi:get-instruction-index($relatedInstruction,'footnote')"/>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for debugging, it outputs the input name of the element related to the driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-ddi-element">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="local-name($context)"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-after-question-title-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'instruction,comment,help')"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-end-question-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'footnote') | enoddi:get-next-filter-description($context)"/>
    </xsl:function>


    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving style for QuestionTable (only 'no-border' or '' as values yet)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-style">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="if(enoddi:get-style($context) = 'question multiple-choice-question') then ('no-border') else(if(enoddi:get-style($context) = 'image') then ('image') else())"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving an index for footnote instructions (based on their ordering in the questionnaire)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-end-question-instructions-index">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instruction-index($context,'footnote,tooltip')"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that returns if a variable is initializable or not</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:is-initializable-variable" as="xs:boolean">
        <xsl:param name="context" as="item()"/>
        <xsl:choose>
            <xsl:when test="lower-case($initialize-all-variables) = 'true'">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- TODO : improve DDI content -->
                <xsl:value-of select="enoddi:get-variable-type($context,enoddi:get-id($context)) = 'external'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
</xsl:stylesheet>
