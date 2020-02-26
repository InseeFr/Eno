<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
    xmlns:enoddi2fr="http://xml.insee.fr/apps/eno/ddi2form-runner" xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3" version="2.0">

    <!-- Importing the different resources -->
    <xsl:import href="../../inputs/ddi/source.xsl" />
    <xsl:import href="../../outputs/fr/models.xsl" />
    <xsl:import href="../../lib.xsl" />

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This stylesheet is used to transform a DDI input into an Xforms form (containing orbeon form runner adherences).</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8" />

    <!--<xsl:strip-space elements="*"/>-->

    <xd:doc>
        <xd:desc>
            <xd:p>The properties file used by the stylesheet.</xd:p>
            <xd:p>It's on a transformation level.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="properties-file"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The properties file is charged as an xml tree.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="properties" select="doc($properties-file)"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The parameter file used by the stylesheet.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="parameters-file" />
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <xd:doc>
        <xd:desc>
            <xd:p>The parameters are charged as an xml tree.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="parameters">
        <xsl:choose>
            <xsl:when test="$parameters-node/*">
                <xsl:copy-of select="$parameters-node" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="doc($parameters-file)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>The folder containing label resources in different languages.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="labels-folder" />

    <xd:doc>
        <xd:desc>
            <xd:p>A variable is created to build a set of label resources in different languages.</xd:p>
            <xd:p>Only the resources in languages already present in the DDI input are charged.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="labels-resource">
        <xsl:sequence select="eno:build-labels-resource($labels-folder,enofr:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))" />
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Characters used to surround variables in conditioned text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="conditioning-variable-begin" select="$properties//TextConditioningVariable/ddi/Before" />
    <xsl:variable name="conditioning-variable-end" select="$properties//TextConditioningVariable/ddi/After" />

    <xd:doc>
        <xd:desc>
            <xd:p>Boolean to hide numeric example.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="numeric-example" select="$parameters//NumericExample" as="xs:boolean" />

    <xd:doc>
        <xd:desc>
            <xd:p>Character for the decimal separator.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="decimal-separator">
        <xsl:choose>
            <xsl:when test="$parameters//DecimalSeparator != ''">
                <xsl:value-of select="$parameters//DecimalSeparator"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//DecimalSeparator"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xd:doc>
        <xd:desc>Loops and dynamic array's ids may be called in many calculs : filters, consistency checks, calculated variables</xd:desc>
        <xd:dec>To change their in-language-ID into business-name, everywhere it is necessary, it is simple to try everywhere it could be necessary</xd:dec>
    </xd:doc>
    <xsl:variable name="list-of-groups">
        <Groups>
            <xsl:for-each select="//l:VariableGroup">
                <xsl:sort select="string-length(r:ID)" order="descending" />
                <Group>
                    <xsl:attribute name="id" select="r:ID" />
                    <xsl:attribute name="name" select="l:VariableGroupName/r:String" />
                </Group>
            </xsl:for-each>
        </Groups>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template :</xd:p>
            <xd:p>The transformation starts with the main Sequence.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="//d:Sequence[d:TypeOfSequence/text()='template']" mode="source" />
    </xsl:template>

    <xd:doc>
        <xd:desc>For business variables, their name is their business name ; for other objects, their name is their id</xd:desc>
    </xd:doc>
    <xsl:function name="enofr:get-name">
        <xsl:param name="context" as="item()" />
        <xsl:variable name="ddi-markup" select="name($context)" />

        <xsl:choose>
            <xsl:when test="($ddi-markup = ('l:Variable','d:GenerationInstruction','d:Loop')) or ends-with($ddi-markup,'Domain') or ends-with($ddi-markup,'DomainReference')">
                <xsl:sequence select="enoddi:get-business-name($context)" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="enoddi:get-id($context)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>
            <xd:p>This function returns an xforms label for the context on which it is applied.</xd:p>
            <xd:p>It concats different labels to do this job.</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:function name="enofr:get-label">
        <xsl:param name="context" as="item()" />
        <xsl:param name="language" />

        <xsl:variable name="ddi-label" select="enoddi:get-label($context,$language)" />
        <xsl:variable name="tooltip" select="enoddi:get-instructions-by-format($context,'tooltip')" as="node()*" />
        <xsl:variable name="tooltips-with-id" select="$tooltip[descendant-or-self::*/@id]" as="node()*" />
        <xsl:variable name="other-instructions" select="enoddi:get-instructions-by-format($context,'instruction,comment,help,warning')" as="node()*" />

        <xsl:variable name="original-label">
            <xsl:choose>
                <xsl:when test="$ddi-label/name()='xhtml:p'">
                    <xsl:copy-of select="$ddi-label/* | $ddi-label/text()" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$ddi-label" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="label-without-tooltips-with-id">
            <xsl:choose>
                <xsl:when test="(name($context)='d:QuestionItem' or name($context)='d:QuestionGrid') and ($other-instructions/* or $tooltip/*)">
                    <xsl:element name="xhtml:p">
                        <xsl:element name="xhtml:span">
                            <xsl:attribute name="class">
                                <xsl:value-of select="'block '" />
                                <xsl:value-of select="enoddi:get-style($context)" />
                            </xsl:attribute>
                            <xsl:if test="$ddi-label/@id">
                                <xsl:attribute name="id" select="$ddi-label/@id" />
                            </xsl:if>
                            <xsl:copy-of select="$original-label" />
                            <xsl:for-each select="$tooltip[not(descendant-or-self::*/@id) or not(concat('#',descendant-or-self::*/@id) = $ddi-label//xhtml:a/@href)]">
                                <xsl:call-template name="tooltip-xforms">
                                    <xsl:with-param name="ddi-tooltip" select="." />
                                    <xsl:with-param name="language" select="$language" />
                                </xsl:call-template>
                            </xsl:for-each>
                        </xsl:element>
                        <xsl:for-each select="$other-instructions">
                            <xsl:variable name="instruction-label" select="enoddi:get-label(.,$language)" />
                            <xsl:variable name="instruction-tooltip" select="enoddi:get-instructions-by-format(.,'tooltip')" as="node()*" />
                            <xsl:variable name="tooltips-with-id" select="$instruction-tooltip[descendant-or-self::*/@id]" as="node()*" />
                            <xsl:variable name="instruction-label-without-id-tooltips">
                                <xsl:element name="xhtml:span">
                                    <xsl:attribute name="class">
                                        <xsl:value-of select="'block '" />
                                        <xsl:value-of select="enoddi:get-style(.)" />
                                    </xsl:attribute>
                                    <xsl:if test="$instruction-label/@id">
                                        <xsl:attribute name="id" select="$instruction-label/@id" />
                                    </xsl:if>
                                    <xsl:choose>
                                        <xsl:when test="$instruction-label/name()='xhtml:p'">
                                            <xsl:copy-of select="$instruction-label/* | $instruction-label/text()" />
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:copy-of select="$instruction-label" />
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:for-each select="$instruction-tooltip[not(descendant-or-self::*/@id) or not(concat('#',descendant-or-self::*/@id) = $instruction-label//xhtml:a/@href)]">
                                        <xsl:call-template name="tooltip-xforms">
                                            <xsl:with-param name="ddi-tooltip" select="." />
                                            <xsl:with-param name="language" select="$language" />
                                        </xsl:call-template>
                                    </xsl:for-each>
                                </xsl:element>
                            </xsl:variable>
                            <xsl:call-template name="tooltip-in-label">
                                <xsl:with-param name="label" select="$instruction-label-without-id-tooltips" />
                                <xsl:with-param name="language" select="$language" />
                                <xsl:with-param name="tooltip" select="$instruction-tooltip" />
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:when>
                <xsl:when test="$tooltip/*">
                    <xsl:element name="xhtml:p">
                        <xsl:copy-of select="$original-label" />
                        <xsl:for-each select="$tooltip[not(descendant-or-self::*/@id) or not(concat('#',descendant-or-self::*/@id) = $ddi-label//xhtml:a/@href)]">
                            <xsl:call-template name="tooltip-xforms">
                                <xsl:with-param name="ddi-tooltip" select="." />
                                <xsl:with-param name="language" select="$language" />
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:when>
                <xsl:when test="$ddi-label//xhtml:span/@class='block' ">
                    <xsl:element name="xhtml:p">
                        <xsl:for-each select="$ddi-label//xhtml:span[@class='block']">
                            <xsl:element name="xhtml:span">
                                <xsl:attribute name="class">
                                    <xsl:value-of select="'block'"/>
                                    <xsl:if test="enoddi:get-style($context) != ''">
                                        <xsl:value-of select="concat(' ',enoddi:get-style($context))"/>    
                                    </xsl:if>
                                </xsl:attribute>
                                <xsl:if test="$ddi-label/@id and not(preceding-sibling::xhtml:span)">
                                    <xsl:attribute name="id" select="$ddi-label/@id"/>
                                </xsl:if>
                                <xsl:copy-of select="node()|text()"/>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:sequence select="$ddi-label" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="tooltip-in-label">
            <xsl:with-param name="label" select="$label-without-tooltips-with-id" />
            <xsl:with-param name="language" select="$language" />
            <xsl:with-param name="tooltip" select="$tooltips-with-id" />
        </xsl:call-template>
    </xsl:function>

    <xsl:template name="tooltip-xforms">
        <xsl:param name="ddi-tooltip" />
        <xsl:param name="language" />

        <xsl:variable name="tooltip-label" select="enoddi:get-label($ddi-tooltip,$language)" />
        <xsl:variable name="title">
            <xsl:choose>
                <xsl:when test="$tooltip-label/name()='xhtml:p'">
                    <xsl:copy-of select="$tooltip-label/* | $tooltip-label/text()" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$tooltip-label" />
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:element name="xhtml:span">
            <xsl:attribute name="title" select="normalize-space($title)" />
            <xsl:text>&#160;</xsl:text>
            <img src="/img/Help-browser.svg.png" />
            <xsl:text>&#160;</xsl:text>
        </xsl:element>
    </xsl:template>

    <xsl:template name="tooltip-in-label">
        <xsl:param name="label" />
        <xsl:param name="language" />
        <xsl:param name="tooltip" as="node()*" />

        <xsl:choose>
            <xsl:when test="$tooltip[1]">
                <xsl:variable name="href" select="concat('#',$tooltip[1]//*/@id)" />
                <xsl:variable name="tooltip-label">
                    <xsl:call-template name="tooltip-xforms">
                        <xsl:with-param name="ddi-tooltip" select="$tooltip[1]" />
                        <xsl:with-param name="language" select="$language" />
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="new-label">
                    <xsl:apply-templates select="$label" mode="replace-tooltip">
                        <xsl:with-param name="href" select="$href" tunnel="yes" />
                        <xsl:with-param name="tooltip-label" select="$tooltip-label" tunnel="yes" />
                    </xsl:apply-templates>
                </xsl:variable>
                <xsl:call-template name="tooltip-in-label">
                    <xsl:with-param name="label" select="$new-label" />
                    <xsl:with-param name="language" select="$language" />
                    <xsl:with-param name="tooltip" select="$tooltip[position() &gt; 1]" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$label/*">
                        <xsl:copy-of select="$label" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$label" />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="node() | @*" mode="replace-tooltip">
        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates select="@*|node()" mode="replace-tooltip" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xhtml:a" mode="replace-tooltip">
        <xsl:param name="href" tunnel="yes" />
        <xsl:param name="tooltip-label" tunnel="yes" />
        <xsl:choose>
            <xsl:when test="@href = $href">
                <xsl:copy-of select="$tooltip-label" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()" mode="replace-tooltip" />
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>enofr:get-label-conditioning-variables</xd:desc>
    </xd:doc>
    <xsl:function name="enofr:get-label-conditioning-variables">
        <xsl:param name="context" as="item()" />
        <xsl:param name="language" />

        <xsl:variable name="conditioning-variables-with-doubles" as="xs:string*">
            <xsl:sequence select="enoddi:get-label-conditioning-variables($context,$language)" />
            <xsl:choose>
                <xsl:when test="name($context)='d:QuestionItem' or name($context)='d:QuestionGrid'">
                    <xsl:for-each select="enoddi:get-instructions-by-format($context)">
                        <xsl:sequence select="enoddi:get-label-conditioning-variables(.,$language)" />
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="enoddi:get-instructions-by-format($context,'tooltips')">
                        <xsl:sequence select="enoddi:get-label-conditioning-variables(.,$language)" />
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:sequence select="distinct-values($conditioning-variables-with-doubles)" />
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>This function returns an xforms hint for the context on which it is applied.</xd:p>
            <xd:p>It uses different DDI functions to do this job.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofr:get-hint">
        <xsl:param name="context" as="item()" />
        <xsl:param name="language" />
        <!-- We look for an instruction of 'Format' type -->
        <xsl:variable name="format-instruction">
            <xsl:sequence select="enoddi:get-instructions-by-format($context,'format')" />
        </xsl:variable>
        <xsl:choose>
            <!-- If there is no such instruction -->
            <xsl:when test="not($format-instruction/*)">
                <!-- We look for the container of the element -->
                <xsl:variable name="question-type">
                    <xsl:value-of select="enoddi:get-container($context)" />
                </xsl:variable>
                <!-- If it is a grid we do not want the hint to be displayed for n fields. If it is a question, we can display this info -->
                <xsl:if test="$question-type='question'">
                    <xsl:variable name="type">
                        <xsl:value-of select="enoddi:get-type($context)" />
                    </xsl:variable>
                    <!-- If it is number, we display this hint -->
                    <xsl:if test="$type='number' and boolean($numeric-example)">
                        <xsl:variable name="number-of-decimals" select="enoddi:get-number-of-decimals($context)" />
                        <xsl:variable name="number-format">
                            <xsl:value-of select="'#'" />
                            <xsl:if test="$number-of-decimals!='' and $number-of-decimals!='0'">
                                <xsl:value-of select="'.'" />
                                <xsl:for-each select="1 to $number-of-decimals">
                                    <xsl:value-of select="'#'" />
                                </xsl:for-each>
                            </xsl:if>
                        </xsl:variable>
                        <xsl:value-of select="concat($labels-resource/Languages/Language[@xml:lang=$language]/Hint/Number,
                                                     format-number((number(enoddi:get-minimum($context))*3+number(enoddi:get-maximum($context))) div 4,
                                                     $number-format))" />
                    </xsl:if>
                    <!-- If it is a date, we display this hint -->
                    <xsl:if test="$type='date'">
                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Hint/Date" />
                    </xsl:if>
                    <xsl:if test="$type='gYearMonth'">
                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Hint/gYearMonth" />
                    </xsl:if>
                </xsl:if>
            </xsl:when>
            <!-- If there is such an instruction, it is used for the hint xforms element -->
            <xsl:when test="$format-instruction/*">
                <xsl:sequence select="enoddi:get-label($format-instruction,$language)" />
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
        <xsl:param name="context" as="item()" />
        <xsl:param name="language" />

        <xsl:variable name="type">
            <xsl:value-of select="enoddi:get-type($context)" />
        </xsl:variable>
        <xsl:variable name="format">
            <xsl:value-of select="enoddi:get-format($context)" />
        </xsl:variable>
        <xsl:variable name="minimum">
            <xsl:value-of select="replace(enoddi:get-minimum($context),'\.',$decimal-separator)"/>
        </xsl:variable>
        <xsl:variable name="maximum">
            <xsl:value-of select="replace(enoddi:get-maximum($context),'\.',$decimal-separator)"/>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$type='text'">
                <xsl:if test="not($format='')">
                    <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Text"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$type='number'">
                <xsl:variable name="number-of-decimals">
                    <xsl:value-of select="enoddi:get-number-of-decimals($context)"/>
                </xsl:variable>
                <xsl:variable name="beginning">
                    <xsl:choose>
                        <xsl:when test="not($number-of-decimals='' or $number-of-decimals='0')">
                            <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/Beginning"/>
                            <xsl:value-of select="$decimal-separator"/>
                            <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/Beginning2"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Integer"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/Between)"/>
                </xsl:variable>
                <xsl:variable name="end">
                    <xsl:choose>
                        <xsl:when test="not($number-of-decimals='' or $number-of-decimals='0')">
                            <xsl:value-of
                                select="concat(' ',
                                $labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/DecimalCondition,
                                ' ',
                                $number-of-decimals,
                                ' ',
                                $labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/Digit,
                                if (number($number-of-decimals)&gt;1) then $labels-resource/Languages/Language[@xml:lang=$language]/Plural else '',
                                ' ',
                                $labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/End,
                                $decimal-separator,
                                $labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/End2)"
                            />
                        </xsl:when>
                    </xsl:choose>
                </xsl:variable>
                <xsl:value-of select="concat($beginning,' ',$minimum, ' ',$labels-resource/Languages/Language[@xml:lang=$language]/And,' ',$maximum, $end)"/>
            </xsl:when>
            <xsl:when test="$type='date'">
                <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Date"/>
                <xsl:if test="$minimum != ''">
                    <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/After/Date,' ')"/>
                    <xsl:value-of select="concat(number(substring($minimum,9,2)),' ')"/>
                    <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/DateTime/Months/*[position()=number(substring($minimum,6,2))]"/>
                    <xsl:value-of select="concat(' ',substring($minimum,1,4))"/>
                </xsl:if>
                <xsl:if test="$minimum != '' and $maximum != ''">
                    <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/And)"/>
                </xsl:if>
                <xsl:if test="$maximum != ''">
                    <xsl:choose>
                        <xsl:when test="contains($maximum,'-date()')">
                            <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/Before/Date-YYYY,' ')"/>
                            <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Today"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/Before/Date,' ')"/>
                            <xsl:value-of select="concat(number(substring($maximum,9,2)),' ')"/>
                            <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/DateTime/Months/*[position()=number(substring($maximum,6,2))]"/>
                            <xsl:value-of select="concat(' ',substring($maximum,1,4))"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$type='gYearMonth'">
                <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Date-YYYYMM"/>
                <xsl:if test="$minimum != ''">
                    <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/After/Date-YYYYMM,' ')"/>
                    <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/DateTime/Months/*[position()=number(substring($minimum,6,2))]"/>
                    <xsl:value-of select="concat(' ',substring($minimum,1,4))"/>
                </xsl:if>
                <xsl:if test="$minimum != '' and $maximum != ''">
                    <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/And)"/>
                </xsl:if>
                <xsl:if test="$maximum != ''">
                    <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/Before/Date-YYYYMM,' ')"/>
                    <xsl:choose>
                        <xsl:when test="contains($maximum,'-date()')">
                            <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Today"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/DateTime/Months/*[position()=number(substring($maximum,6,2))]"/>
                            <xsl:value-of select="concat(' ',substring($maximum,1,4))"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$type='gYear'">
                <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Date-YYYY"/>
                <xsl:if test="$minimum != ''">
                    <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/After/Date-YYYY,' ',$minimum)"/>
                </xsl:if>
                <xsl:if test="$minimum != '' and $maximum != ''">
                    <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/And)"/>
                </xsl:if>
                <xsl:if test="$maximum != ''">
                    <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/Before/Date-YYYY,' ')"/>
                    <xsl:choose>
                        <xsl:when test="contains($maximum,'-date()')">
                            <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Today"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$maximum"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$type='duration'">
                <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Duration"/>
                <xsl:if test="$minimum!=''">
                    <xsl:choose>
                        <xsl:when test="$maximum!=''">
                            <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/Between,' ')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/GreaterThan,' ')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:call-template name="text-from-duration">
                        <xsl:with-param name="duration" select="$minimum"/>
                        <xsl:with-param name="language" select="$language"/>
                    </xsl:call-template>
                </xsl:if>
                <xsl:if test="$maximum!=''">
                    <xsl:choose>
                        <xsl:when test="$minimum!=''">
                            <xsl:value-of select="concat($labels-resource/Languages/Language[@xml:lang=$language]/And,' ')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat(' ',$labels-resource/Languages/Language[@xml:lang=$language]/LessThan,' ')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:call-template name="text-from-duration">
                        <xsl:with-param name="duration" select="$maximum"/>
                        <xsl:with-param name="language" select="$language"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:when>
            <xsl:when test="$type='boolean'"/>
            <xsl:when test="$type!=''">
                <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Text"/>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:function>

    <xd:doc>
        <xd:desc>Calculates the text from a duration to show it in an alert message</xd:desc>
    </xd:doc>
    <xsl:template name="text-from-duration">
        <xsl:param name="duration"/>
        <xsl:param name="language"/>

        <xsl:variable name="zero-duration">
            <xsl:analyze-string select="$duration" regex="^P0(.)[^1-9]*$">
                <xsl:matching-substring>
                    <xsl:value-of select="regex-group(1)"/>
                </xsl:matching-substring>
                <xsl:non-matching-substring>
                    <xsl:analyze-string select="$duration" regex="^PT0(.)[^1-9]*$">
                        <xsl:matching-substring>
                            <xsl:value-of select="lower-case(regex-group(1))"/>
                        </xsl:matching-substring>
                    </xsl:analyze-string>
                </xsl:non-matching-substring>
            </xsl:analyze-string>
        </xsl:variable>

        <xsl:analyze-string select="$duration" regex="^P(([0-9]+)Y)?(([0-9]+)M)?(([0-9]+)D)?(T(([0-9]+)H)?(([0-9]+)M)?(([0-9]+)S)?)?$">
            <xsl:matching-substring>
                <xsl:if test="regex-group(2) != '' and (regex-group(2) != '0' or $zero-duration='Y')">
                    <xsl:value-of select="concat(regex-group(2),' ',$labels-resource/Languages/Language[@xml:lang=$language]/Duration/Year,' ')"/>
                </xsl:if>
                <xsl:if test="regex-group(4) != '' and (regex-group(4) != '0' or $zero-duration='M')">
                    <xsl:value-of select="concat(regex-group(4),' ',$labels-resource/Languages/Language[@xml:lang=$language]/Duration/Month,' ')"/>
                </xsl:if>
                <xsl:if test="regex-group(6) != '' and (regex-group(6) != '0' or $zero-duration='D')">
                    <xsl:value-of select="concat(regex-group(6),' ',$labels-resource/Languages/Language[@xml:lang=$language]/Duration/Day,' ')"/>
                </xsl:if>
                <xsl:if test="regex-group(9) != '' and (regex-group(9) != '0' or $zero-duration='h')">
                    <xsl:value-of select="concat(regex-group(9),' ',$labels-resource/Languages/Language[@xml:lang=$language]/Duration/Hour,' ')"/>
                </xsl:if>
                <xsl:if test="regex-group(11) != '' and (regex-group(11) != '0' or $zero-duration='m')">
                    <xsl:value-of select="concat(regex-group(11),' ',$labels-resource/Languages/Language[@xml:lang=$language]/Duration/Minute,' ')"/>
                </xsl:if>
                <xsl:if test="regex-group(13) != '' and (regex-group(13) != '0' or $zero-duration='s')">
                    <xsl:value-of select="concat(regex-group(13),' ',$labels-resource/Languages/Language[@xml:lang=$language]/Duration/Second,' ')"/>
                </xsl:if>
            </xsl:matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>This function retrieves the languages to appear in the generated Xforms.</xd:p>
            <xd:p>Those languages can be specified in a parameters file on a questionnaire
                level.</xd:p>
            <xd:p>If not, it will get the languages defined in the DDI input.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofr:get-form-languages">
        <xsl:param name="context" as="item()" />
        <xsl:choose>
            <xsl:when test="$parameters//Parameters/Languages">
                <xsl:for-each select="$parameters//Parameters/Languages/Language">
                    <xsl:value-of select="." />
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="enoddi:get-languages($context)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Length that defines the size of a long table</xd:p>
            <xd:p>It is used for defining a css class</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="lengthOfLongTable" as="xs:integer">
        <xsl:variable name="lengthOfLongTableParameters" select="$parameters//LengthOfLongTable" />
        <xsl:choose>
            <xsl:when test="$lengthOfLongTableParameters!=''">
                <xsl:value-of select="$lengthOfLongTableParameters" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//LengthOfLongTable" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xd:doc>
        <xd:desc>no use of input enoddi:is-required : always false for Web collect</xd:desc>
    </xd:doc>
    <xsl:function name="enofr:is-required">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="false()"/>
    </xsl:function>
    
    <xd:doc>
        <xd:desc>ddi error message keeps warning for Web collect</xd:desc>
    </xd:doc>
    <xsl:function name="enofr:get-message-type">
        <xsl:param name="context" as="item()"/>
        <xsl:variable name="ddi-message" select="enoddi:get-message-type($context)"/>
        
        <xsl:choose>
            <xsl:when test="$ddi-message = 'error'">
                <xsl:value-of select="'warning'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$ddi-message"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
</xsl:stylesheet>
