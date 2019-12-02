<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:d32="ddi:datacollection:3_2" xmlns:r32="ddi:reusable:3_2" xmlns:l32="ddi:logicalproduct:3_2" xmlns:g32="ddi:group:3_2" xmlns:s32="ddi:studyunit:3_2"
    xmlns:d="ddi:datacollection:3_3" xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3" xmlns:g="ddi:group:3_3" xmlns:s="ddi:studyunit:3_3"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs d32 r32 l32 g32 s32 xsl xd"
    version="2.0">

    <!-- useful only for surveys with more than 1 questionnaire and used with ant -->
    <xsl:variable name="ant-call" as="xs:boolean" select="false()"/>
    <!-- the first variable is the address of the temporary folder relative to the ddi32toddi33.xsl folder -->
    <!-- this program will seach for the file generated par dereferencing.xsl : ${TempFolder}/${survey}/ddi/${questionnaire}.tmp -->
    <xsl:variable name="dereferenced-temporary-files-folder" select="'../../../../../../../../../coltrane-dev/coltrane-eno/src/main/temp/'"/>
    <xsl:variable name="file-name" select="replace(substring-before(tokenize(base-uri(),'/')[last()],'.'),'_ddi32','')"/>

    <xsl:output indent="yes"/>
    <xd:doc>
        <xd:desc>root template : DDIInstance with DDI 3.3 namespaces</xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <DDIInstance xmlns="ddi:instance:3_3"
            xmlns:d="ddi:datacollection:3_3" xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3" xmlns:g="ddi:group:3_3" xmlns:s="ddi:studyunit:3_3"
            xmlns:a="ddi:archive:3_3" xmlns:pr="ddi:ddiprofile:3_3" xmlns:c="ddi:conceptualcomponent:3_3" xmlns:cm="ddi:comparative:3_3">
            <xsl:apply-templates select="*/*"/>
        </DDIInstance>
    </xsl:template>

    <xd:doc>
        <xd:desc>default template : keep the same</xd:desc>
    </xd:doc>
    <xsl:template match="@* | node()" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>DDI 3.2 namespaces to DDI 3.3 ones</xd:desc>
    </xd:doc>
    <xsl:template match="d32:*">
        <xsl:element name="d:{local-name()}">
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="r32:*">
        <xsl:element name="r:{local-name()}">
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="l32:*">
        <xsl:element name="l:{local-name()}">
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="g32:*">
        <xsl:element name="g:{local-name()}">
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="s32:*">
        <xsl:element name="s:{local-name()}">
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>xhtml namespace keeps the same</xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:*">
        <xsl:element name="xhtml:{local-name()}">
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>real evolutions</xd:desc>
    </xd:doc>
    <xsl:template match="@codeListID">
        <xsl:attribute name="controlledVocabularyID">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

    <xd:doc>
        <xd:desc>https://ddi-alliance.atlassian.net/projects/DDILIFE/issues/DDILIFE-3526</xd:desc>
    </xd:doc>
    <xsl:template match="d32:GridResponseDomain">
        <xsl:element name="d:GridResponseDomainInMixed">
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>https://ddi-alliance.atlassian.net/projects/DDILIFE/issues/DDILIFE-3523</xd:desc>
    </xd:doc>
    <xsl:template match="d32:ComputationItem/r32:CommandCode">
        <xsl:element name="d:TypeOfComputationItem">
            <xsl:attribute name="controlledVocabularyID" select="'INSEE-TOCI-CL-3'"/>
            <xsl:value-of select="'informational'"/>
        </xsl:element>
        <xsl:element name="r:CommandCode">
            <xsl:apply-templates select="node()"/>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>https://ddi-alliance.atlassian.net/projects/DDILIFE/issues/DDILIFE-3523</xd:desc>
    </xd:doc>
    <xsl:template match="d32:IfThenElse/d32:IfCondition">
        <xsl:element name="d:TypeOfIfThenElse">
            <xsl:choose>
                <xsl:when test="//d32:Sequence[r32:ID=current()/parent::d32:IfThenElse/d32:ThenConstructReference/r32:ID]/d32:TypeOfSequence='deactivatable'">
                    <xsl:value-of select="'greyedout'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'hideable'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
        <xsl:element name="d:IfCondition">
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="d32:CodeDomain">
        <xsl:element name="d:CodeDomain">
            <xsl:attribute name="displayCode" select="'false'"/>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>Add VariableGroup "questionnaire"</xd:desc>
    </xd:doc>
    <xsl:template match="l32:VariableScheme[1]">
        <xsl:element name="l:VariableScheme">
            <xsl:apply-templates select="@* | node()"/>
            <xsl:for-each select="//s32:StudyUnit//d32:Instrument">
                <xsl:variable name="questionnaire-name">
                    <xsl:choose>
                        <xsl:when test="d32:InstrumentName">
                            <xsl:value-of select="d32:InstrumentName/r32:String"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="replace(r32:ID/text(), concat(replace(//s32:StudyUnit/r32:ID/text(), '-SU', ''),'-In-'), '')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="derefenced-questionnaire-address" select="concat($dereferenced-temporary-files-folder,$file-name,'/ddi/',$questionnaire-name,'.tmp')"/>
                <xsl:variable name="dereferenced-questionnaire" as="node()">
                    <xsl:choose>
                        <xsl:when test="$ant-call and unparsed-text-available($derefenced-questionnaire-address)">
                            <xsl:copy-of select="doc($derefenced-questionnaire-address)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <Empty/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:element name="l:VariableGroup">
                    <xsl:element name="r:Agency"><xsl:value-of select="r32:Agency"/></xsl:element>
                    <xsl:element name="r:ID"><xsl:value-of select="concat(r32:ID,'-VG')"/></xsl:element>
                    <xsl:element name="r:Version"><xsl:value-of select="r32:Version"/></xsl:element>
                    <xsl:element name="r:BasedOnObject">
                        <xsl:element name="r:BasedOnReference">
                            <xsl:element name="r:Agency"><xsl:value-of select="r32:Agency"/></xsl:element>
                            <xsl:element name="r:ID"><xsl:value-of select="r32:ID"/></xsl:element>
                            <xsl:element name="r:Version"><xsl:value-of select="r32:Version"/></xsl:element>
                            <xsl:element name="r:TypeOfObject"><xsl:value-of select="'Instrument'"/></xsl:element>
                        </xsl:element>
                    </xsl:element>
                    <xsl:element name="l:TypeOfVariableGroup"><xsl:value-of select="'Questionnaire'"/></xsl:element>
                    <xsl:element name="l:VariableGroupName">
                        <xsl:element name="r:String"><xsl:value-of select="$questionnaire-name"/></xsl:element>
                    </xsl:element>
                    <xsl:for-each select="//l32:VariableScheme/l32:Variable">
                        <xsl:variable name="variable-id" select="r32:ID"/>
                        <xsl:variable name="is-variable-to-reference">
                            <xsl:choose>
                                <!-- variable already in a loop -->
                                <xsl:when test="//l32:VariableScheme/l32:VariableGroup[contains(l32:TypeOfVariableGroup,'Loop')]/r32:VariableReference/r32:ID = $variable-id"/>

                                <xsl:when test="$dereferenced-questionnaire/* and r32:SourceParameterReference">
                                    <!-- collected variable -->
                                    <xsl:value-of select="$dereferenced-questionnaire//*[local-name()='QuestionReference'][descendant::*[local-name()='ID' and not(ends-with(parent::*/local-name(),'Reference'))]/text() = current()/r32:QuestionReference/r32:ID/text()]/local-name()"/>
                                </xsl:when>
                                <xsl:when test="$dereferenced-questionnaire/* and l32:VariableRepresentation/r32:ProcessingInstructionReference">
                                    <!-- calculated variable -->
                                    <xsl:value-of select="$dereferenced-questionnaire//*[local-name()='GenerationInstruction'][*[local-name()='ID']/text()=current()/l32:VariableRepresentation/r32:ProcessingInstructionReference/r32:ID/text()]/local-name()"/>
                                </xsl:when>
                                <!-- external variable -->
                                <xsl:when test="$dereferenced-questionnaire/*">
                                    <xsl:variable name="variable-name" select="l32:VariableName/r32:String/text()"/>
                                    <xsl:choose>
                                        <xsl:when test="$dereferenced-questionnaire//text()[contains(.,concat('¤',$variable-name,'¤'))]">
                                            <xsl:value-of select="'¤'"/>
                                        </xsl:when>
                                        <xsl:when test="$dereferenced-questionnaire//text()[contains(.,concat('ø',$variable-name,'ø'))]">
                                            <xsl:value-of select="'ø'"/>
                                        </xsl:when>
                                        <xsl:when test="$dereferenced-questionnaire//*[local-name()='SourceParameterReference' and *[local-name()='TypeOfObject' and text()='InParameter']]/*[local-name()='ID'] = $variable-name">
                                            <xsl:value-of select="$variable-name"/>
                                        </xsl:when>
                                        <xsl:otherwise/>
                                    </xsl:choose>
                                </xsl:when>
                                <xsl:otherwise>
                                    <!-- no dereferenced questionnaire : all variables are taken -->
                                    <xsl:value-of select="'true'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:if test="$is-variable-to-reference != ''">
                            <xsl:element name="r:VariableReference">
                                <xsl:element name="r:Agency"><xsl:value-of select="r32:Agency"/></xsl:element>
                                <xsl:element name="r:ID"><xsl:value-of select="$variable-id"/></xsl:element>
                                <xsl:element name="r:Version"><xsl:value-of select="r32:Version"/></xsl:element>
                                <xsl:element name="r:TypeOfObject"><xsl:value-of select="'Variable'"/></xsl:element>
                            </xsl:element>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:for-each select="//l32:VariableScheme/l32:VariableGroup">
                        <xsl:variable name="variablegroup-id" select="r32:ID"/>
                        <xsl:variable name="is-variablegroup-to-reference">
                            <xsl:choose>
                                <!-- variable already in a loop -->
                                <xsl:when test="//l32:VariableScheme/l32:VariableGroup[contains(l32:TypeOfVariableGroup,'Loop')]/r32:VariableGroupReference/r32:ID = $variablegroup-id"/>

                                <xsl:when test="$dereferenced-questionnaire/*">
                                    <!-- existing dereferenced questionnaire -->
                                    <xsl:value-of select="$dereferenced-questionnaire//*[not(contains(name(),'Reference')) and *[local-name()='ID']/text() = current()/r32:BasedOnObject/r32:BasedOnReference/r32:ID/text()]/local-name()"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <!-- no dereferenced questionnaire : all variablegroups are taken -->
                                    <xsl:value-of select="'true'"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:if test="$is-variablegroup-to-reference != ''">
                            <xsl:element name="l:VariableGroupReference">
                                <xsl:element name="r:Agency"><xsl:value-of select="r32:Agency"/></xsl:element>
                                <xsl:element name="r:ID"><xsl:value-of select="$variablegroup-id"/></xsl:element>
                                <xsl:element name="r:Version"><xsl:value-of select="r32:Version"/></xsl:element>
                                <xsl:element name="r:TypeOfObject"><xsl:value-of select="'VariableGroup'"/></xsl:element>
                            </xsl:element>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>https://ddi-alliance.atlassian.net/browse/DDILIFE-3532</xd:desc>
    </xd:doc>
    <xsl:template match="l32:VariableRepresentation[r32:MeasurementUnit]">
        <xsl:variable name="QID" select="../r32:QuestionReference/r32:ID"/>
        <xsl:variable name="QOPID" select="../r32:SourceParameterReference/r32:ID"/>
        <xsl:variable name="RDOPID" select="//*[not(ends-with(name(),'Reference')) and r32:ID=$QID]/r32:Binding[r32:TargetParameterReference/r32:ID=$QOPID]/r32:SourceParameterReference/r32:ID"/>
        <xsl:variable name="domain" select="//*[not(ends-with(name(),'Reference')) and r32:ID=$QID]//*[(ends-with(name(),'Domain') or  ends-with(name(),'DomainReference')) and r32:OutParameter/r32:ID=$RDOPID]" as="node()"/>
        <xsl:variable name="domain-root">
            <xsl:choose>
                <xsl:when test="$domain/local-name()='NumericDomain'">
                    <xsl:value-of select="'r:NumericRepresentation'"/>
                </xsl:when>
                <xsl:when test="$domain/local-name()='NumericDomainReference'">
                    <xsl:value-of select="'r:NumericRepresentationReference'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'UNKNWON'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:element name="l:VariableRepresentation">
            <xsl:element name="{$domain-root}">
                <xsl:apply-templates select="$domain/@*"/>
                <xsl:element name="r:MeasurementUnit">
                    <xsl:value-of select="r32:MeasurementUnit"/>
                </xsl:element>
                <xsl:apply-templates select="$domain/*[not(self::r32:OutParameter) and not(self::r32:ResponseCardinality)]"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>Variables without representation : not new with DDI 3.3, but forgotten till now + evolution needed for MeasurementUnit</xd:desc>
        <xd:desc>Works only for collected variables</xd:desc>
    </xd:doc>
    <xsl:template match="l32:VariableRepresentation[not(*) and ../r32:SourceParameterReference]">
        <xsl:variable name="QID" select="../r32:QuestionReference/r32:ID"/>
        <xsl:variable name="QOPID" select="../r32:SourceParameterReference/r32:ID"/>
        <xsl:variable name="RDOPID" select="//*[not(ends-with(name(),'Reference')) and r32:ID=$QID]/r32:Binding[r32:TargetParameterReference/r32:ID=$QOPID]/r32:SourceParameterReference/r32:ID"/>
        <xsl:variable name="domain" select="//*[not(ends-with(name(),'Reference')) and r32:ID=$QID]//*[(ends-with(name(),'Domain') or  ends-with(name(),'DomainReference')) and r32:OutParameter/r32:ID=$RDOPID]" as="node()"/>
        <xsl:variable name="domain-root">
            <xsl:choose>
                <xsl:when test="ends-with($domain/local-name(),'Domain')">
                    <xsl:value-of select="concat('r:',replace($domain/local-name(),'Domain','Representation'))"/>
                </xsl:when>
                <xsl:when test="ends-with($domain/local-name(),'DomainReference')">
                    <xsl:value-of select="concat('r:',replace($domain/local-name(),'Domain','Representation'))"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'UNKNWON'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:element name="l:VariableRepresentation">
            <xsl:choose>
                <xsl:when test="$domain/local-name()='NominalDomain'">
                    <xsl:apply-templates select="$domain/descendant::r32:CodeRepresentation"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:element name="{$domain-root}">
                        <xsl:apply-templates select="$domain/@*"/>
                        <xsl:apply-templates select="$domain/*[not(self::r32:OutParameter) and not(self::r32:ResponseCardinality) and not(self::r32:Label)]"/>
                    </xsl:element>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>https://ddi-alliance.atlassian.net/projects/DDILIFE/issues/DDILIFE-3590</xd:desc>
    </xd:doc>
    <xsl:template match="d32:ExternalAid[r32:Description]">
        <xsl:element name="d:ExternalAid">
            <xsl:element name="r:OtherMaterial">
                <xsl:apply-templates select="node()"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>Simplify numeric formulas</xd:desc>
    </xd:doc>
    <xsl:template match="r32:CommandContent">
        <xsl:element name="r:CommandContent">
            <xsl:call-template name="simplify-numeric-formulas">
                <xsl:with-param name="formula" select="normalize-space(text())"/>
            </xsl:call-template>
        </xsl:element>
    </xsl:template>

    <xsl:template name="simplify-numeric-formulas">
        <xsl:param name="formula"/>

        <xsl:analyze-string select="$formula" regex="^(.*)number ?\( ?if \( ?(.+) ?= ?'' ?\) then '0' else \2 ?\)(.*)$">
            <xsl:matching-substring>
                <xsl:call-template name="simplify-numeric-formulas">
                    <xsl:with-param name="formula" select="regex-group(1)"/>
                </xsl:call-template>
                <xsl:call-template name="simplify-numeric-formulas">
                    <xsl:with-param name="formula" select="regex-group(2)"/>
                </xsl:call-template>
                <xsl:call-template name="simplify-numeric-formulas">
                    <xsl:with-param name="formula" select="regex-group(3)"/>
                </xsl:call-template>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:value-of select="$formula"/>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>
</xsl:stylesheet>