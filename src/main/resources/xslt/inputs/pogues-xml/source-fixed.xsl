<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:enopogues="http://xml.insee.fr/apps/eno/in/pogues-xml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xs" version="2.0">

    <xd:doc>
        <xd:desc>
            <xd:p>For each element, the default behaviour is to return empty text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="#all" priority="-1">
        <xsl:text/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>This mode is used to return nodes instead of strings.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="with-tag">
        <xsl:sequence select="."/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>This mode is used when a conversion table is needed between Pogues and DDI.</xd:p>
            <xd:p>TODO : Move this out of the input interface and put it in the transformation one
                (depends both on input and output).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="conversion-table">
        <xsl:value-of select="."/>
    </xsl:template>

    <xsl:template match="* | @*" mode="conversion-table-error-message">
        <xsl:message select="concat('The value ', ., ' for ', name(), ' are not supported')"/>
    </xsl:template>

    <xsl:template match="pogues:Declaration/@declarationType" mode="conversion-table">
        <xsl:choose>
            <xsl:when test=". = 'COMMENT'">
                <xsl:value-of select="'comment'"/>
            </xsl:when>
            <xsl:when test=". = 'INSTRUCTION'">
                <xsl:value-of select="'instruction'"/>
            </xsl:when>
            <xsl:when test=". = 'HELP'">
                <xsl:value-of select="'help'"/>
            </xsl:when>
            <xsl:when test=". = 'WARNING'">
                <xsl:value-of select="'warning'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="." mode="conversion-table-error-message"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="pogues:Datatype/@visualizationHint" mode="conversion-table">
        <xsl:choose>
            <xsl:when test=". = 'RADIO'">
                <xsl:value-of select="'radio-button'"/>
            </xsl:when>
            <xsl:when test=". = 'CHECKBOX'">
                <xsl:value-of select="'checkbox'"/>
            </xsl:when>
            <xsl:when test=". = 'DROPDOWN'">
                <xsl:value-of select="'drop-down-list'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="." mode="conversion-table-error-message"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="pogues:Child/@genericName" mode="conversion-table">
        <xsl:choose>
            <xsl:when test=". = 'COMMENT'">
                <xsl:value-of select="'template'"/>
            </xsl:when>
            <xsl:when test=". = 'MODULE'">
                <xsl:value-of select="'module'"/>
            </xsl:when>
            <xsl:when test=". = 'SUBMODULE'">
                <xsl:value-of select="'submodule'"/>
            </xsl:when>
            <xsl:when test=". = 'HIDEABLE'">
                <xsl:value-of select="'hideable'"/>
            </xsl:when>
            <xsl:when test=". = 'DEACTIVATABLE'">
                <xsl:value-of select="'deactivatable'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="." mode="conversion-table-error-message"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="pogues:Control/@criticity" mode="conversion-table">
        <xsl:choose>
            <xsl:when test=". = 'INFO'">
                <xsl:value-of select="'info'"/>
            </xsl:when>
            <xsl:when test=". = 'WARN'">
                <xsl:value-of select="'warn'"/>
            </xsl:when>
            <xsl:when test=". = 'ERROR'">
                <xsl:value-of select="'error'"/>
            </xsl:when>
            <xsl:when test=". = 'MANDATORY'">
                <xsl:value-of select="'mandatory'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="." mode="conversion-table-error-message"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="pogues:Unit" mode="conversion-table">
        <xsl:choose>
            <xsl:when test=". = 'http://id.insee.fr/unit/euro'">
                <xsl:value-of select="'€'"/>
            </xsl:when>
            <xsl:when test=". = 'http://id.insee.fr/unit/keuro'">
                <xsl:value-of select="'k€'"/>
            </xsl:when>
            <xsl:when test=". = 'http://id.insee.fr/unit/percent'">
                <xsl:value-of select="'%'"/>
            </xsl:when>
            <xsl:when test=". = 'http://id.insee.fr/unit/heure'">
                <xsl:value-of select="'heures'"/>
            </xsl:when>
            <xsl:when test=". = 'http://id.insee.fr/unit/jour'">
                <xsl:value-of select="'jours'"/>
            </xsl:when>
            <xsl:when test=". = 'http://id.insee.fr/unit/mois'">
                <xsl:value-of select="'mois'"/>
            </xsl:when>
            <xsl:when test=". = 'http://id.insee.fr/unit/annee'">
                <xsl:value-of select="'années'"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="pogues:Expression | pogues:Formula | pogues:Response" mode="enopogues:get-ip-id">
        <xsl:param name="index" tunnel="yes"/>
        <xsl:value-of select="concat(enopogues:get-id(.), '-IP-', $index)"/>
    </xsl:template>


    <xsl:template match="pogues:Response" mode="enopogues:get-related-variable">
        <xsl:variable name="idVariable" select="pogues:CollectedVariableReference"/>
        <xsl:sequence select="//pogues:Variable[@id = $idVariable]"/>
    </xsl:template>

    <xsl:template match="pogues:ClarificationQuestion" mode="enopogues:get-related-variable">
        <xsl:variable name="idVariable" select="pogues:Response/pogues:CollectedVariableReference"/>
        <xsl:sequence select="//pogues:Variable[@id = $idVariable]"/>
    </xsl:template>

    <xsl:template match="pogues:Expression | pogues:Formula | pogues:Text | pogues:Control/pogues:FailMessage | pogues:Label" mode="enopogues:get-related-variable">
        <xsl:variable name="expressionVariable" select="tokenize(., '\$')"/>
        <xsl:variable name="variables" select="//pogues:Variables"/>

        <!-- 2 ways to describe a variable : $Variable$ or $Variable with a space after -->
        <!-- TODO : Use regexp matches instead to handle several ' ' and linebreak cases. -->

        <xsl:sequence select="$variables/pogues:Variable[some $x in $expressionVariable satisfies (if (contains($x, ' '))
                                                                                                    then (substring-before($x, ' '))
                                                                                                    else ($x))
                                                                 = pogues:Name/text()]"/>
    </xsl:template>

    <xsl:template match="pogues:Variable[pogues:Formula]" mode="enopogues:get-related-variable">
        <xsl:sequence select="enopogues:get-related-variable(pogues:Formula)"/>
    </xsl:template>

    <xsl:template match="pogues:Response" mode="enopogues:get-cell-coordinates">
        <xsl:variable name="correspondingMapping"
            select="following-sibling::pogues:ResponseStructure/pogues:Mapping[pogues:MappingSource = current()/@id]"/>
        <xsl:sequence select="tokenize($correspondingMapping/pogues:MappingTarget, ' ')"/>
    </xsl:template>

    <xsl:template
        match="pogues:Child[@xsi:type = 'QuestionType' and @questionType = 'MULTIPLE_CHOICE']"
        mode="enopogues:get-grid-dimensions">
        <xsl:sequence select="pogues:Dimension[@dimensionType = ('PRIMARY', 'SECONDARY')]"/>
    </xsl:template>

    <xsl:template
        match="pogues:Child[@questionType = 'TABLE']/pogues:ResponseStructure[not(pogues:Dimension/@dimensionType = 'SECONDARY')]"
        mode="enopogues:get-fake-dimension">
        <xsl:element name="pogues:Dimension">
            <xsl:attribute name="dimensionType" select="'SECONDARY'"/>
            <xsl:attribute name="dynamic" select="0"/>
            <xsl:element name="pogues:CodeListReference">
                <xsl:value-of select="enopogues:get-fake-code-list-id(.)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="pogues:CodeLists | pogues:Questionnaire" mode="enopogues:get-fake-code-lists">
        <xsl:apply-templates select="ancestor-or-self::pogues:Questionnaire//pogues:ResponseStructure" mode="enopogues:get-fake-code-lists"/>
    </xsl:template>

    <xsl:template match="pogues:Child[@questionType = 'TABLE']/pogues:ResponseStructure[not(pogues:Dimension/@dimensionType = 'SECONDARY')]"
        mode="enopogues:get-fake-code-lists">
        <xsl:variable name="fake-code-list">
            <pogues:CodeList id="{enopogues:get-fake-code-list-id(.)}">
                <pogues:Name/>
                <pogues:Label><xsl:value-of select="concat('FAKE-CODELIST-',enopogues:get-fake-code-list-id(.))"/></pogues:Label>
                <xsl:for-each select="pogues:Dimension[@dimensionType='MEASURE' and @dynamic='0']">
                    <pogues:Code>
                        <pogues:Value><xsl:value-of select="position()"/></pogues:Value>
                        <pogues:Label><xsl:value-of select="enopogues:get-label(.)"/></pogues:Label>
                    </pogues:Code>
                </xsl:for-each>
            </pogues:CodeList>
        </xsl:variable>
        <xsl:sequence select="$fake-code-list"/>
    </xsl:template>

    <xsl:function name="enopogues:get-fake-code-list-id">
        <xsl:param name="context" as="node()"/>
        <xsl:value-of
            select="concat($context/parent::pogues:Child/@id, '-secondDimension-fakeCL-1')"/>
    </xsl:function>

    <xsl:template match="pogues:Declaration/pogues:Text | pogues:Control/pogues:FailMessage | pogues:Label" mode="id-variable">
        <xsl:variable name="variables" select="enopogues:get-related-variable(.)"/>
        <xsl:choose>
            <xsl:when test="$variables">
                <xsl:call-template name="enopogues:id-variable-to-ddi">
                     <xsl:with-param name="variables" select="$variables" as="item()*"/>
                     <xsl:with-param name="expression" select="concat(./text(),' ')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="."/>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template name="enopogues:id-variable-to-ddi">
        <xsl:param name="variables" as="item()*"/>
        <xsl:param name="index" select="1"/>
        <xsl:param name="expression"/>
        <xsl:choose>
            <xsl:when test="$index &gt; count($variables)">
                <xsl:value-of select="$expression"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="currentVariable" select="$variables[$index]"/>
                <!-- TO DO, variable-name is only for external variables, others should refer to outParam. -->
                <!-- TO DO Remove variable without $ end separator -->
                <xsl:variable name="variable-name" select="enopogues:get-name($currentVariable)"/>
                <xsl:variable name="variable-ref-name-with-final-dollars" select="concat('\$',$variable-name,'\$')"/>
                <xsl:variable name="variable-ref-name-without-final-dollars" select="concat('\$',$variable-name,' ')"/>
                <xsl:variable name="variable-type" select="enopogues:get-type($currentVariable)"/>
                <xsl:choose>
                    <!-- In this case the variable id separator is '¤' and variable id is the outparam related to the variable (QOP for collected, GOP for calculated).  -->
                    <xsl:when test="$variable-type = ('CollectedVariableType','CalculatedVariableType')">
                        <xsl:variable name="variable-ref" select="enopogues:get-qop-id($currentVariable)"/>
                        <xsl:call-template name="enopogues:id-variable-to-ddi">
                            <xsl:with-param name="variables" select="$variables"/>
                            <xsl:with-param name="index" select="$index + 1"/>
                            <xsl:with-param name="expression" select="replace(replace($expression, $variable-ref-name-with-final-dollars, concat('¤', $variable-ref, '¤')),
                                                                                                   $variable-ref-name-without-final-dollars, concat('¤', $variable-ref, '¤'))"
                            />
                        </xsl:call-template>
                    </xsl:when>
                    <!-- In this case the variable id separator is 'ø' and variable id is the variable name.  -->
                    <xsl:when test="$variable-type = 'ExternalVariableType'">
                            <xsl:variable name="variable-ref"
                            select="enopogues:get-name($currentVariable)"/>
                        <xsl:call-template name="enopogues:id-variable-to-ddi">
                                <xsl:with-param name="variables" select="$variables"/>
                                <xsl:with-param name="index" select="$index + 1"/>
                                <xsl:with-param name="expression" select="replace(replace($expression, $variable-ref-name-with-final-dollars, concat('¤', $variable-ref, '¤')),
                                                                                                       $variable-ref-name-without-final-dollars, concat('¤', $variable-ref, '¤'))"
                            />
                            </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="enopogues:id-variable-to-ddi">
                            <xsl:with-param name="variables" select="$variables"/>
                            <xsl:with-param name="index" select="$index + 1"/>
                            <xsl:with-param name="expression" select="$expression"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template match="pogues:Variable" mode="enopogues:get-command-id">
        <xsl:param name="ip-id" tunnel="yes"/>

        <xsl:value-of select="$ip-id"/>
    </xsl:template>

    <!--TODO Better handling of multi variable references  -->
    <xsl:template match="pogues:FailMessage | pogues:Text" mode="enopogues:get-qop-id">
        <xsl:variable name="related-variables" select="enopogues:get-related-variable(.)"/>
        <xsl:if test="count($related-variables) &gt; 1">
            <xsl:message>Multi Variables references in ConditionnalText (coming from FailMessage) are not supported.</xsl:message>
        </xsl:if>
        <xsl:value-of select="enopogues:get-qop-id($related-variables[1])"/>
    </xsl:template>

    <!-- id generated from idCodeList with id of Other Choice question -->
    <xsl:template match="*" mode="enopogues:get-clarified-code">
        <xsl:param name="idList" as="xs:string" tunnel="yes"/>
        <xsl:param name="otherValue" as="xs:string" tunnel="yes"/>
        <xsl:value-of select="enopogues:get-id(//pogues:CodeList[@id=$idList]/pogues:Code[pogues:Value=$otherValue])"/>
    </xsl:template>

</xsl:stylesheet>
