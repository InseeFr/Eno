<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
    xmlns:enoddi2pdf="http://xml.insee.fr/apps/eno/ddi2pdf"
    xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:l="ddi:logicalproduct:3_2" version="2.0">

    <!-- Importing the different resources -->
    <xsl:import href="../../inputs/ddi/source.xsl"/>
    <xsl:import href="../../outputs/pdf/models.xsl"/>
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
        <xsl:sequence select="eno:build-labels-resource($labels-folder,enopdf:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"/>
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
            <xd:p>This xforms function is used to get the concatened string corresponding to a dynamic text.</xd:p>
            <xd:p>It is created by calling the static text and making it dynamic.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enopdf:get-calculate-text">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language" as="item()"/>
        <xsl:param name="text-type"/>

        <xsl:variable name="static-text-content">
            <xsl:choose>
                <xsl:when test="$text-type='label'">
                    <xsl:sequence select="enoddi:get-label($context,$language)"/>
                </xsl:when>
                <xsl:when test="$text-type='alert'">
                    <xsl:sequence select="enoddi:get-consistency-message($context,$language)"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="contains(substring-after($static-text-content,$conditioning-variable-begin),$conditioning-variable-end)">
            <xsl:variable name="condition-variables">
                <conditions>
                    <xsl:copy-of select="$context/descendant::d:ConditionalText"/>
                </conditions>
            </xsl:variable>

            <xsl:variable name="calculated-text">
                <xsl:call-template name="enoddi2pdf:calculate-text">
                    <xsl:with-param name="text-to-calculate" select="eno:serialize($static-text-content)"/>
                    <xsl:with-param name="condition-variables" select="$condition-variables"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:text>concat(</xsl:text>
            <xsl:value-of select="substring($calculated-text,2)"/>
            <xsl:text>)</xsl:text>
        </xsl:if>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>This recursive template returns the calculated conditional text from the static one.</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template name="enoddi2pdf:calculate-text">
        <xsl:param name="text-to-calculate"/>
        <xsl:param name="condition-variables"/>

        <xsl:text>,</xsl:text>
        <xsl:choose>
            <xsl:when test="contains(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)">
                <xsl:text>'</xsl:text>
                <!-- Replacing the single quote by 2 single quotes because a concatenation is made -->
                <!-- We actually need to double the quotes in order not to generate an error in the xforms concat.-->
                <xsl:value-of select="replace(substring-before($text-to-calculate,$conditioning-variable-begin),'''','''''')"/>
                <xsl:text>',</xsl:text>
                <xsl:choose>
                    <!-- conditionalText doesn't exist for the element in the DDI structure or it exists and references the variable -->
                    <xsl:when test="not($condition-variables//text())">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <!-- TODO : add the elements that will show which variable to use when it is in a loop -->
                        <xsl:value-of select="substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
                    </xsl:when>
                    <!-- conditionalText exists and references the variable -->
                    <xsl:when test="index-of($condition-variables//r:SourceParameterReference/r:OutParameter/r:ID,
                        substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)) >0">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <!-- TODO : add the elements that will show which variable to use when it is in a loop -->
                        <xsl:value-of select="substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
                    </xsl:when>
                    <!-- conditionalText contains the calculation of the variable -->
                    <xsl:when test="index-of($condition-variables//d:Expression/r:Command/r:OutParameter/r:ID,
                        substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)) >0">
                        <!-- TODO : perhaps to change so that the label includes the calculation, not a temporary variable -->
                        <xsl:value-of select="replace(replace(
                            $condition-variables//d:Expression/r:Command
                                                                        [r:OutParameter/r:ID=substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)]
                                                                        /r:CommandContent,
                                                      '//','instance(''fr-form-instance'')//'),
                                              '\]instance(''fr-form-instance'')',']')"/>
                        <!--                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
-->
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- conditionalText exists, but the variable is not in it -->
                        <xsl:text>'</xsl:text>
                        <xsl:value-of select="concat($conditioning-variable-begin,
                            replace(substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end),'''',''''''),
                            $conditioning-variable-end)"/>
                        <xsl:text>'</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="enoddi2pdf:calculate-text">
                    <xsl:with-param name="text-to-calculate" select="substring-after(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
                    <xsl:with-param name="condition-variables" select="$condition-variables"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>'</xsl:text>
                <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.-->
                <xsl:value-of select="replace($text-to-calculate,'''','''''')"/>
                <xsl:text>'</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>This function returns an xforms hint for the context on which it is applied.</xd:p>
            <xd:p>It uses different DDI functions to do this job.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enopdf:get-hint">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <!-- We look for an instruction of 'Format' type -->
        <xsl:variable name="format-instruction">
            <xsl:sequence select="enoddi:get-instructions-by-format($context,'format')"/>
        </xsl:variable>
        <xsl:choose>
            <!-- If there is no such instruction -->
            <xsl:when test="not($format-instruction/*)">
                <!-- We look for the container of the element -->
                <xsl:variable name="question-type">
                    <xsl:value-of select="enoddi:get-container($context)"/>
                </xsl:variable>
                <!-- If it is a grid we do not want the hint to be displayed for n fields. If it is a question, we can display this info -->
                <xsl:if test="$question-type='question'">
                    <xsl:variable name="type">
                        <xsl:value-of select="enoddi:get-type($context)"/>
                    </xsl:variable>
                    <!-- If it is number, we display this hint -->
                    <xsl:if test="$type='number'">
                        <xsl:value-of select="concat($labels-resource/Languages/Language[@xml:lang=$language]/Hint/Number,enoddi:get-maximum($context))"/>
                    </xsl:if>
                    <!-- If it is a date, we display this hint -->
                    <xsl:if test="$type='date'">
                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Hint/Date"/>
                    </xsl:if>
                </xsl:if>
            </xsl:when>
            <!-- If there is such an instruction, it is used for the hint xforms element -->
            <xsl:when test="$format-instruction/*">
                <xsl:sequence select="$format-instruction/*"/>
            </xsl:when>
        </xsl:choose>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>This function returns an xforms alert for the context on which it is applied.</xd:p>
            <xd:p>It uses different DDI functions to do this job.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enopdf:get-alert">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <!-- We look for a 'message' -->
        <!-- 02-21-2017 : this function is only called for an Instruction in a ComputationItem on the DDI side -->
        <xsl:variable name="message">
            <xsl:sequence select="enoddi:get-consistency-message($context,$language)"/>
        </xsl:variable>
        <xsl:choose>
            <!-- if there is no such message -->
            <xsl:when test="not($message/node())">
                <!-- We retrieve the question type -->
                <xsl:variable name="type">
                    <xsl:value-of select="enoddi:get-type($context)"/>
                </xsl:variable>
                <!-- We retrieve the format -->
                <xsl:variable name="format">
                    <xsl:value-of select="enoddi:get-format($context)"/>
                </xsl:variable>
                <!-- If it is a 'text' and a format is defined, we use a generic sentence as an alert -->
                <xsl:if test="$type='text'">
                    <xsl:if test="not($format='')">
                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Text"/>
                    </xsl:if>
                </xsl:if>
                <!-- If it is a number, we look for infos about the format and deduce a message for the alert element -->
                <xsl:if test="$type='number'">
                    <xsl:variable name="number-of-decimals">
                        <xsl:value-of select="enoddi:get-number-of-decimals($context)"/>
                    </xsl:variable>
                    <xsl:variable name="minimum">
                        <xsl:value-of select="enoddi:get-minimum($context)"/>
                    </xsl:variable>
                    <xsl:variable name="maximum">
                        <xsl:value-of select="enoddi:get-maximum($context)"/>
                    </xsl:variable>
                    <xsl:variable name="beginning">
                        <xsl:choose>
                            <xsl:when test="not($number-of-decimals='' or $number-of-decimals='0')">
                                <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/Beginning"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Integer"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:variable name="end">
                        <xsl:choose>
                            <xsl:when test="not($number-of-decimals='' or $number-of-decimals='0')">
                                <xsl:value-of select="' '
                                    ,concat($labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/DecimalCondition
                                    ,' '
                                    ,$number-of-decimals
                                    ,' '
                                    ,$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/Digit
                                    ,if (number($number-of-decimals)&gt;1) then $labels-resource/Languages/Language[@xml:lang=$language]/Plural else ''
                                    ,' '
                                    ,$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/End)"/>
                            </xsl:when>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:value-of select="concat($beginning,' ',$minimum, ' ',$labels-resource/Languages/Language[@xml:lang=$language]/And,' ',$maximum, $end)"/>
                </xsl:if>
                <!-- If it is a 'date', we use a generic sentence as an alert -->
                <xsl:if test="$type='date'">
                    <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Date"/>
                </xsl:if>
                <!-- In those cases, we use specific messages as alert messages -->
                <xsl:if test="$type='duration'">
                    <xsl:if test="$format='hh'">
                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Duration/Hours"/>
                    </xsl:if>
                    <xsl:if test="$format='mm'">
                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Duration/Minutes"/>
                    </xsl:if>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="$message/node()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>This function retrieves the languages to appear in the generated Xforms.</xd:p>
            <xd:p>Those languages can be specified in a parameters file on a questionnaire level.</xd:p>
            <xd:p>If not, it will get the languages defined in the DDI input.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enopdf:get-form-languages">
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

    <xsl:function name="enopdf:get-formatted-label">
        <xsl:param name="label" as="item()"/>
        <xsl:param name="language"/>
        <xsl:variable name="tempLabel">
            <xsl:apply-templates select="enoddi:get-label($label,$language)" mode="enopdf:format-label"/>
        </xsl:variable>
        <xsl:sequence select="$tempLabel"/>
    </xsl:function>
    
    
    <xsl:template match="*" mode="enopdf:format-label">
      <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
    </xsl:template>
    
    <xsl:template match="xhtml:p[.//xhtml:br]" mode="enopdf:format-label">
        <xsl:element name="fo:block">
            <xsl:attribute name="linefeed-treatment" select="'preserve'"/>
            <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="*[not(descendant-or-self::xhtml:*)]" mode="enopdf:format-label">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="text()" mode="enopdf:format-label">
        <xsl:copy-of select="normalize-space(.)"/>
    </xsl:template>
    
    <xsl:template match="xhtml:i" mode="enopdf:format-label">
        <xsl:element name="fo:inline">          
            <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="xhtml:b" mode="enopdf:format-label">
        <xsl:element name="fo:inline">
            <xsl:attribute name="font-weight" select="'bold'"/>
            <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
        </xsl:element>        
    </xsl:template>
    
    <xsl:template match="xhtml:span[@style='text-decoration:underline']" mode="enopdf:format-label">
        <xsl:element name="fo:wrapper">
            <xsl:attribute name="text-decoration" select="'underline'"/>
            <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="xhtml:br" mode="enopdf:format-label">
        <xsl:text xml:space="preserve">&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="xhtml:a[contains(@href,'#ftn')]" mode="enopdf:format-label">
        <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
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
    <xsl:function name="enopdf:get-ddi-element">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="local-name($context)"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enopdf:get-after-question-title-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'instruction,comment,help')"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enopdf:get-end-question-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'footnote') | enoddi:get-next-filter-description($context)"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving default line number for TableLoop</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enopdf:get-rooster-number-lines">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="if($context/self::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]) then(8) else()"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving style for QuestionTable (only 'no-border' or '' as values yet)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enopdf:get-style">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="if(enoddi:get-style($context) = 'question multiple-choice-question') then ('no-border') else()"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving an index for footnote instructions (based on their ordering in the questionnaire)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enopdf:get-end-question-instructions-index">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instruction-index($context,'footnote,tooltip')"/>
    </xsl:function>
    
    
</xsl:stylesheet>
