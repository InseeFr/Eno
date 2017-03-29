<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2" version="2.0">

    <!-- Base file of the upcoming ddi2fr.xsl stylesheet (that will be used in the ddi2fr target to create basic-form.tmp) -->

    <!-- Importing the different files used in the process -->
    <!-- source.xsl : the xsl created by merging inputs/ddi/functions.xsl, source-fixed.xsl and templates.xsl -->
    <!-- models.xsl : Orbeon related transformations -->
    <!-- lib.xsl : used to parse a file with defined constraints -->
    <xsl:import href="../../inputs/ddi/source.xsl"/>
    <xsl:import href="../../outputs/fr/models.xsl"/>
    <xsl:import href="../../lib.xsl"/>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:strip-space elements="*"/>
    
    <!-- Parameters defined in build-non-regression.xml -->
    <xsl:param name="parameters-file"/>
    <xsl:variable name="parameters" select="doc($parameters-file)"/>
    
    <xsl:param name="labels-folder"/>
    
    <xsl:variable name="labels-resource">
        <xsl:sequence select="eno:build-labels-resource($labels-folder,enofr:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"/>
    </xsl:variable>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>Transforms DDI into Orbeon Form Runner!</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template match="/">
        <xsl:apply-templates select="/" mode="source"/>
    </xsl:template>

    <!-- Getting this here, actually dependent of the input and output language -->
    <!-- Getting conditional text for d:Instruction elements having a r:SourceParameterReference descendant and no  -->
    <!-- d:ComputationItem ancestor  -->
    <xsl:template
        match="d:Instruction[descendant::d:ConditionalText[r:SourceParameterReference] and not(ancestor::d:ComputationItem)]"
        mode="enoddi:get-conditional-text" priority="1">
        <xsl:variable name="condition">
            <xsl:copy-of select="descendant::d:ConditionalText"/>
        </xsl:variable>
        <xsl:variable name="text">
            <xsl:value-of select="eno:serialize(descendant::d:LiteralText/d:Text/node())"/>
        </xsl:variable>
        <xsl:variable name="result">
            <xsl:text>concat(''</xsl:text>
            <xsl:for-each select="tokenize($text,'ø')">
                <xsl:text>,</xsl:text>
                <xsl:choose>
                    <xsl:when
                        test=".=$condition/d:ConditionalText/r:SourceParameterReference/r:OutParameter/r:ID/text()">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="."/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>'</xsl:text>
                        <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.-->
                        <xsl:value-of select="replace(.,'''','''''')"/>
                        <xsl:text>'</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:text>)</xsl:text>
        </xsl:variable>
        <xsl:value-of select="$result"/>
    </xsl:template>

    <!-- Getting this here, actually dependent of the input and ouput language -->
    <!-- Getting conditional text for d:Instruction elements having a r:SourceParameterReference descendant and a  -->
    <!-- d:ComputationItem ancestor  -->
    <xsl:template
        match="d:Instruction[descendant::d:ConditionalText[r:SourceParameterReference] and ancestor::d:ComputationItem]"
        mode="enoddi:get-conditional-text-bis" priority="1">
        <xsl:variable name="condition">
            <xsl:copy-of select="descendant::d:ConditionalText"/>
        </xsl:variable>
        <xsl:variable name="text">
            <xsl:value-of select="eno:serialize(descendant::d:LiteralText/d:Text/node())"/>
        </xsl:variable>
        <xsl:variable name="result">
            <xsl:text>concat(''</xsl:text>
            <xsl:for-each select="tokenize($text,'ø')">
                <xsl:text>,</xsl:text>
                <xsl:choose>
                    <xsl:when
                        test=".=$condition/d:ConditionalText/r:SourceParameterReference/r:OutParameter/r:ID/text()">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="."/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>'</xsl:text>
                        <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.-->
                        <xsl:value-of select="replace(.,'''','''''')"/>
                        <xsl:text>'</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:text>)</xsl:text>
        </xsl:variable>
        <xsl:value-of select="$result"/>
    </xsl:template>

    <!-- Getting the conditional text for d:Instruction elements having a d:ConditionalText/d:Expression descendant -->
    <!-- and no d:ComputationItem ancestor -->
    <xsl:template
        match="d:Instruction[descendant::d:ConditionalText[d:Expression] and not(ancestor::d:ComputationItem)]"
        mode="enoddi:get-conditional-text" priority="1">
        <xsl:variable name="condition">
            <xsl:copy-of select="descendant::d:ConditionalText"/>
        </xsl:variable>
        <xsl:variable name="text">
            <xsl:value-of select="eno:serialize(descendant::d:LiteralText/d:Text/node())"/>
        </xsl:variable>
        <xsl:variable name="result">
            <xsl:text>concat(''</xsl:text>
            <xsl:for-each select="tokenize($text,'ø')[not(.='')]">
                <xsl:text>,</xsl:text>
                <xsl:choose>
                    <xsl:when
                        test="contains($condition/d:ConditionalText/d:Expression/r:Command/r:CommandContent/text(),.)">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="."/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>'</xsl:text>
                        <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.-->
                        <xsl:value-of select="replace(.,'''','''''')"/>
                        <xsl:text>'</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:text>)</xsl:text>
        </xsl:variable>
        <xsl:value-of select="$result"/>
    </xsl:template>

    <!-- Getting the conditional text for d:Instruction elements having a d:ConditionalText/d:Expression descendant -->
    <!-- and a d:ComputationItem ancestor -->
    <xsl:template
        match="d:Instruction[descendant::d:ConditionalText[d:Expression] and ancestor::d:ComputationItem]"
        mode="enoddi:get-conditional-text-bis" priority="1">
        <xsl:variable name="condition">
            <xsl:copy-of select="descendant::d:ConditionalText"/>
        </xsl:variable>
        <xsl:variable name="text">
            <xsl:value-of select="eno:serialize(descendant::d:LiteralText/d:Text/node())"/>
        </xsl:variable>
        <xsl:variable name="result">
            <xsl:text>concat(''</xsl:text>
            <xsl:for-each select="tokenize($text,'ø')[not(.='')]">
                <xsl:text>,</xsl:text>
                <xsl:choose>
                    <xsl:when
                        test="contains($condition/d:ConditionalText/d:Expression/r:Command/r:CommandContent/text(),.)">
                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="."/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>'</xsl:text>
                        <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.-->
                        <xsl:value-of select="replace(.,'''','''''')"/>
                        <xsl:text>'</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            <xsl:text>)</xsl:text>
        </xsl:variable>
        <xsl:value-of select="$result"/>
    </xsl:template>

    <!--Linking the Xforms hint getter function to multiple DDI getter functions -->
    <xsl:function name="enofr:get-hint">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <!-- We look for an instruction of 'Format' type -->
        <xsl:variable name="format-instruction">
            <xsl:sequence select="enoddi:get-format-instruction($context,$language)"/>
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
    
    <!--Linking the Xforms alert getter function to multiple DDI getter functions -->
    <xsl:function name="enofr:get-alert">
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
    
    <!--Linking the DDI languages getter function to the form languages getter function-->
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
