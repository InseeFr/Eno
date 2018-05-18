<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" 
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
    xmlns:enoddi2fr="http://xml.insee.fr/apps/eno/ddi2form-runner" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2" version="2.0">

    <!-- Importing the different resources -->
    <xsl:import href="../../inputs/ddi/source.xsl"/>
    <xsl:import href="../../outputs/fr/models.xsl"/>
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
        <xsl:sequence
            select="eno:build-labels-resource($labels-folder,enofr:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"
        />
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
        <xsl:apply-templates select="//d:Sequence[d:TypeOfSequence/text()='template']" mode="source"
        />
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>This xforms function is used to get the concatened string corresponding to a dynamic text.</xd:p>
            <xd:p>It is created by calling the static text and making it dynamic.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofr:get-calculate-text">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language" as="item()"/>
        <xsl:param name="instance-ancestor"/>

        <xsl:variable name="static-text-content">
            <xsl:sequence select="enofr:get-label($context,$language)"/>
        </xsl:variable>

        <xsl:if
            test="contains(substring-after($static-text-content,$conditioning-variable-begin),$conditioning-variable-end)">
            <!-- doesn't work : takes all the ConditionalText in the questionnaire... -->
            <xsl:variable name="condition-variables">
                <xsl:sequence select="enoddi:get-label-conditioner($context,$language)"/>
            </xsl:variable>

            <xsl:variable name="calculated-text">
                <xsl:call-template name="enoddi2fr:calculate-text">
                    <xsl:with-param name="text-to-calculate"
                        select="eno:serialize($static-text-content)"/>
                    <xsl:with-param name="condition-variables" select="$condition-variables"/>
                    <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
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

    <xsl:template name="enoddi2fr:calculate-text">
        <xsl:param name="text-to-calculate"/>
        <xsl:param name="condition-variables"/>
        <xsl:param name="instance-ancestor"/>

        <xsl:text>,</xsl:text>
        <xsl:choose>
            <xsl:when
                test="contains(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)">
                <xsl:text>'</xsl:text>
                <!-- Replacing the single quote by 2 single quotes because a concatenation is made -->
                <!-- We actually need to double the quotes in order not to generate an error in the xforms concat.-->
                <xsl:value-of
                    select="replace(substring-before($text-to-calculate,$conditioning-variable-begin),'''','''''')"/>
                <xsl:text>',</xsl:text>
                <xsl:choose>
                    <!-- conditionalText doesn't exist for the element in the DDI structure or it exists and references the variable -->
                    <xsl:when test="not($condition-variables//text())">
                        <xsl:value-of select="$instance-ancestor"/>
                        <xsl:value-of
                            select="substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"
                        />
                    </xsl:when>
                    <!-- conditionalText exists and references the variable -->
                    <xsl:when
                        test="index-of($condition-variables//r:SourceParameterReference//r:ID,
                        substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)) >0">
                        <xsl:value-of select="$instance-ancestor"/>
                        <xsl:value-of select="substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
                    </xsl:when>
                    <!-- conditionalText contains the calculation of the variable -->
                    <xsl:when
                        test="index-of($condition-variables//d:Expression/r:Command/r:OutParameter/r:ID,
                        substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)) >0">
                        <xsl:value-of
                            select="replace(replace(
                            $condition-variables//d:Expression/r:Command
                                                                        [r:OutParameter/r:ID=substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)]
                                                                        /r:CommandContent,
                                                                        '//',$instance-ancestor),
                                                                        concat('\]',$instance-ancestor),']//')"
                        />
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- conditionalText exists, but the variable is not in it -->
                        <xsl:text>'</xsl:text>
                        <xsl:value-of
                            select="concat($conditioning-variable-begin,
                            replace(substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end),'''',''''''),
                            $conditioning-variable-end)"/>
                        <xsl:text>'</xsl:text>
                        <xsl:value-of select="eno:serialize($condition-variables)"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="enoddi2fr:calculate-text">
                    <xsl:with-param name="text-to-calculate"
                        select="substring-after(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
                    <xsl:with-param name="condition-variables" select="$condition-variables"/>
                    <xsl:with-param name="instance-ancestor" select="$instance-ancestor"/>
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
            <xd:p>This function returns an xforms label for the context on which it is applied.</xd:p>
            <xd:p>It concats different labels to do this job.</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:function name="enofr:get-label">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        
        <xsl:variable name="ddi-label" select="enoddi:get-label($context,$language)"/>
        <xsl:variable name="tooltip" select="enoddi:get-instructions-by-format($context,'tooltip')" as="node() *"/>
        <xsl:variable name="other-instructions" select="enoddi:get-instructions-by-format($context,'instruction,comment,help')" as="node()*"/>
        
        <xsl:choose>
            <xsl:when test="(name($context)='d:QuestionItem' or name($context)='d:QuestionGrid') and ($other-instructions/* or $tooltip/*)">
                <xsl:element name="xhtml:p">
                    <xsl:element name="xhtml:span">
                        <xsl:attribute name="class">
                            <xsl:value-of select="'block '"/>
                            <xsl:value-of select="enoddi:get-style($context)"/>
                        </xsl:attribute>
                        <xsl:if test="$ddi-label/@id">
                            <xsl:attribute name="id" select="$ddi-label/@id"/>
                        </xsl:if>
                        <xsl:variable name="original-label">
                            <xsl:choose>
                                <xsl:when test="$ddi-label/name()='xhtml:p'">
                                    <xsl:apply-templates select="$ddi-label/* | $ddi-label/text()" mode="lang-choice">
                                        <xsl:with-param name="id-tooltips" select="$tooltip[descendant-or-self::*/@id]" as="node() *" tunnel="yes"/>
                                    </xsl:apply-templates>
                                </xsl:when>
                                <xsl:otherwise>
                                       <xsl:apply-templates select="$ddi-label" mode="lang-choice">
                                           <xsl:with-param name="id-tooltips" select="$tooltip[descendant-or-self::*/@id]" as="node() *" tunnel="yes"/>
                                       </xsl:apply-templates>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:copy-of select="$original-label"/>
                        <xsl:for-each select="$tooltip[not(@id) or not(concat('#',descendant-or-self::*/@id) = $ddi-label//xhtml:a/@href)]">
                            <xsl:variable name="tooltip-label" select="enoddi:get-label(.,$language)"/>
                            <xsl:variable name="title">
                                <xsl:choose>
                                    <xsl:when test="$tooltip-label/name()='xhtml:p'">
                                        <xsl:apply-templates select="$tooltip-label/* | $tooltip-label/text()" mode="lang-choice">
                                            <xsl:with-param name="id-tooltips" select="$tooltip[descendant-or-self::*/@id]" as="node() *" tunnel="yes"/>
                                        </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:apply-templates select="$tooltip-label" mode="lang-choice">
                                            <xsl:with-param name="id-tooltips" select="$tooltip[descendant-or-self::*/@id]" as="node() *" tunnel="yes"/>
                                        </xsl:apply-templates>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <xsl:element name="xhtml:span">
                                <xsl:attribute name="title" select="normalize-space($title)"/>
                                <xsl:text>&#160;</xsl:text>
                                <img src="/img/Help-browser.svg.png"/>
                                <!--<xsl:element name="img">
                                    <xsl:attribute name="src" select="'/img/Help-browser.svg.png'"/>
                                </xsl:element>-->
                                <xsl:text>&#160;</xsl:text>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>
                    <xsl:for-each select="$other-instructions">
                        <xsl:variable name="instruction-label" select="enoddi:get-label(.,$language)"/>
                        <xsl:element name="xhtml:span">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'block '"/>
                                <xsl:value-of select="enoddi:get-style(.)"/>
                            </xsl:attribute>
                            <xsl:if test="$instruction-label/@id">
                                <xsl:attribute name="id" select="$instruction-label/@id"/>
                            </xsl:if>
                            <xsl:choose>
                                <xsl:when test="$instruction-label/name()='xhtml:p'">
                                    <xsl:apply-templates select="$instruction-label/* | $instruction-label/text()" mode="lang-choice">
                                        <xsl:with-param name="id-tooltips" select="$tooltip[descendant-or-self::*/@id]" as="node() *" tunnel="yes"/>
                                    </xsl:apply-templates>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:apply-templates select="$instruction-label" mode="lang-choice">
                                        <xsl:with-param name="id-tooltips" select="$tooltip[descendant-or-self::*/@id]" as="node() *" tunnel="yes"/>
                                    </xsl:apply-templates>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:when>
            <xsl:when test="$tooltip/*">
                <xsl:element name="xhtml:p">
                    <!--<xsl:if test="$tooltip[descendant-or-self::*/@id][1]">
                        <xsl:copy-of select="enoddi:get-label($tooltip[descendant-or-self::*/@id][1],$language)"/>    
                    </xsl:if>-->
                    <xsl:apply-templates select="$ddi-label" mode="lang-choice">
                        <xsl:with-param name="id-tooltips" select="$tooltip[descendant-or-self::*/@id]" as="node() *" tunnel="yes"/>
                    </xsl:apply-templates>
                    
                    <xsl:for-each select="$tooltip[not(descendant-or-self::*/@id) or not(concat('#',descendant-or-self::*/@id) = $ddi-label//xhtml:a/@href)]">
                        <xsl:variable name="tooltip-label" select="enoddi:get-label(.,$language)"/>
                        <xsl:variable name="title">
                            <xsl:choose>
                                <xsl:when test="$tooltip-label/name()='xhtml:p'">
                                    <xsl:apply-templates select="$tooltip-label/* | $tooltip-label/text()" mode="lang-choice">
                                        <xsl:with-param name="id-tooltips" select="$tooltip[descendant-or-self::*/@id]" as="node() *" tunnel="yes"/>
                                    </xsl:apply-templates>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:apply-templates select="$tooltip-label" mode="lang-choice">
                                        <xsl:with-param name="id-tooltips" select="$tooltip[descendant-or-self::*/@id]" as="node() *" tunnel="yes"/>
                                    </xsl:apply-templates>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:element name="xhtml:span">
                            <xsl:attribute name="title" select="normalize-space($title)"/>
                            <xsl:text>&#160;</xsl:text>
                            <img src="/img/Help-browser.svg.png"/>
                            <xsl:text>&#160;</xsl:text>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="$ddi-label"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:template match="*[(ancestor::xhtml:p or ancestor::r:Content or ancestor::r:String)] 
        | @*[(ancestor::xhtml:p or ancestor::r:Content or ancestor::r:String)]" priority="2" mode="lang-choice">
        <xsl:copy>
            <xsl:apply-templates select="@*|*|text()" mode="lang-choice"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="node()[r:Content or r:String or xhtml:p][descendant::xhtml:a]" priority="2" mode="lang-choice">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="r:Content[@xml:lang=$language] or r:String[@xml:lang=$language]">
                <xsl:apply-templates select="child::node()[@xml:lang=$language]/child::node()" mode="lang-choice"/>
            </xsl:when>
            <xsl:when test="xhtml:p[@xml:lang=$language]">
                <xsl:apply-templates select="xhtml:p[@xml:lang=$language]" mode="lang-choice"/>
            </xsl:when>
            <xsl:when test="xhtml:p[not(@xml:lang)]">
                <xsl:apply-templates select="xhtml:p" mode="lang-choice"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="child::node()/child::node()" mode="lang-choice"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    

    <xsl:template match="xhtml:a" priority="5" mode="lang-choice">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:param name="id-tooltips" as="node() *" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="@href=concat('#',$id-tooltips/descendant-or-self::*/@id)">
                <xsl:variable name="href" select="substring-after(@href,'#')"/>
                <xsl:variable name="tooltip-label" select="enoddi:get-label($id-tooltips[descendant-or-self::*/@id=$href],$language)"/>
                <xsl:variable name="title">
                    <xsl:choose>
                        <xsl:when test="$tooltip-label/name()='xhtml:p'">
                            <xsl:copy-of select="$tooltip-label/* | $tooltip-label/text()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="$tooltip-label"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:element name="xhtml:span">
                    <xsl:attribute name="title" select="normalize-space($title)"/>
                    <xsl:text>&#160;</xsl:text>
                    <img src="/img/Help-browser.svg.png"/>
                    <xsl:text>&#160;</xsl:text>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*|*|text()" mode="lang-choice"/>
                    <xsl:value-of select="'toto'"/>
                    <xsl:value-of select="$id-tooltips"/>
                </xsl:copy>
                <!--<xsl:copy-of select="."/>-->
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    

    <xd:doc>
        <xd:desc>
            <xd:p>This function returns an xforms hint for the context on which it is applied.</xd:p>
            <xd:p>It uses different DDI functions to do this job.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofr:get-hint">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <!-- We look for an instruction of 'Format' type -->
        <xsl:variable name="format-instruction">
            <!--<xsl:sequence select="enoddi:get-label(enoddi:get-instructions-by-format($context,'format'),$language)"/>-->
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
                        <xsl:variable name="number-of-decimals" select="enoddi:get-number-of-decimals($context)"/>
                        <xsl:variable name="number-format">
                            <xsl:value-of select="'#'"/>
                            <xsl:if test="$number-of-decimals!='' and $number-of-decimals!='0'">
                                <xsl:value-of select="'.'"/>
                                <xsl:for-each select="1 to $number-of-decimals">
                                    <xsl:value-of select="'#'"/>
                                </xsl:for-each>
                            </xsl:if>
                        </xsl:variable>
                        <xsl:value-of select="concat($labels-resource/Languages/Language[@xml:lang=$language]/Hint/Number,
                                                     format-number((number(enoddi:get-minimum($context))*3+number(enoddi:get-maximum($context))) div 4,
                                                     $number-format))"
                        />
                    </xsl:if>
                    <!-- If it is a date, we display this hint -->
                    <xsl:if test="$type='date'">
                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Hint/Date"/>
                    </xsl:if>
                </xsl:if>
            </xsl:when>
            <!-- If there is such an instruction, it is used for the hint xforms element -->
            <xsl:when test="$format-instruction/*">
                <xsl:sequence select="enoddi:get-label($format-instruction,$language)"/>
            </xsl:when>
        </xsl:choose>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>This function returns an xforms alert for the context on which it is applied.</xd:p>
            <xd:p>It uses different DDI functions to do this job.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofr:get-alert">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>

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
                        <xsl:value-of
                            select="' ',
                                    concat($labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/DecimalCondition,
                                    ' ',
                                    $number-of-decimals,
                                    ' ',
                                    $labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/Digit,
                                    if (number($number-of-decimals)&gt;1) then $labels-resource/Languages/Language[@xml:lang=$language]/Plural else '',
                                    ' ',
                                    $labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/End)"
                        />
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
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>This function retrieves the languages to appear in the generated Xforms.</xd:p>
            <xd:p>Those languages can be specified in a parameters file on a questionnaire
                level.</xd:p>
            <xd:p>If not, it will get the languages defined in the DDI input.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofr:get-form-languages">
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

</xsl:stylesheet>
