<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3" xmlns:g="ddi:group:3_3"
    xmlns:s="ddi:studyunit:3_3" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This stylesheet is used to dereference DDI.</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>The output folder in which the dereferenced files (one for each main sequence) are generated.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="output-folder"/>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="no" encoding="UTF-8"/>

    <!--<xsl:strip-space elements="*"/>-->

    <xd:doc>
        <xd:desc>
            <xd:p>The external-variables are not collected nor calculated.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="external-variables">
        <ExternalVariables>
            <xsl:for-each select="//l:VariableScheme/l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference)]">
                <Variable>
                    <xsl:attribute name="id" select="r:ID"/>
                    <xsl:attribute name="variable-name" select="l:VariableName/r:String"/>
                </Variable>
            </xsl:for-each>
        </ExternalVariables>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>The calculated variables refer to control construct, not the contrary.</xd:p>
            <xd:p>templates sequences, loops and dynamic tables always verify if there are calculated variables</xd:p>
            <xd:p>this variable lists the other elements linked to calculated variables</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="calculated-variables-sequences" as="node()">
        <Variables>
            <xsl:for-each select="//d:GenerationInstruction/d:ControlConstructReference">
                <xsl:if test="not(current()/r:ID = //d:Sequence[d:TypeOfSequence = 'template']/r:ID)
                        and not(current()/r:ID = //d:Loop/r:ID)
                        and not(current()/r:ID = //d:QuestionGrid[d:GridDimension/d:Roster]/r:ID)">
                    <Variable>
                        <xsl:value-of select="r:ID"/>
                    </Variable>
                </xsl:if>
            </xsl:for-each>
        </Variables>
    </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template :</xd:p>
            <xd:p>Successively, some group of elements is used to dereference some other group of elements.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <!-- The references used to dereference at the beginning -->
        <xsl:variable name="references-for-codelists">
            <xsl:element name="g:ResourcePackage">
                <xsl:copy-of select="//l:CodeListScheme"/>
                <xsl:copy-of select="//l:CategoryScheme"/>
                <xsl:copy-of select="//d:InterviewerInstructionScheme"/>
            </xsl:element>
        </xsl:variable>
        <!-- The l:CodeListScheme are dereferenced -->
        <xsl:variable name="dereferenced-codelists">
            <xsl:element name="g:ResourcePackage">
                <xsl:apply-templates select="//l:CodeListScheme">
                    <xsl:with-param name="references" select="$references-for-codelists" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:variable>

        <!-- The dereferenced l:CodeListScheme, the d:InterviewerInstructionScheme, the r:ManagedRepresentationScheme and the d:QuestionScheme are used as new references -->
        <xsl:variable name="references-for-questions">
            <xsl:copy-of select="//d:QuestionScheme"/>
            <xsl:copy-of select="//d:InterviewerInstructionScheme"/>
            <xsl:copy-of select="//r:ManagedRepresentationScheme"/>
            <xsl:copy-of select="//d:ProcessingInstructionScheme"/>
            <xsl:copy-of select="$dereferenced-codelists//l:CodeListScheme"/>
        </xsl:variable>

        <!-- The d:QuestionScheme are dereferenced -->
        <xsl:variable name="dereferenced-questions">
            <xsl:element name="g:ResourcePackage">
                <xsl:apply-templates select="//d:QuestionScheme">
                    <xsl:with-param name="references" select="$references-for-questions" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:variable>

        <!-- The dereferenced d:QuestionScheme, the d:InterviewerInstructionScheme, and the ControlConstructScheme are used as new references -->
        <xsl:variable name="references-for-template-sequence">
            <xsl:copy-of select="//l:VariableScheme"/>
            <xsl:copy-of select="//d:ControlConstructScheme"/>
            <xsl:copy-of select="//d:InterviewerInstructionScheme"/>
            <xsl:copy-of select="//d:ProcessingInstructionScheme"/>
            <xsl:copy-of select="$dereferenced-questions//d:QuestionScheme"/>
        </xsl:variable>

        <!-- The main sequences of the DDI are dereferenced -->
        <xsl:variable name="dereferenced-template-sequence">
            <xsl:element name="g:ResourcePackage">
                <xsl:apply-templates
                    select="//d:ControlConstructScheme/d:Sequence[d:TypeOfSequence/text() = 'template']">
                    <xsl:with-param name="references" select="$references-for-template-sequence" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:variable>

        <!-- The root of all identifiers in the survey -->
        <xsl:variable name="root">
            <xsl:value-of select="replace(//s:StudyUnit/r:ID/text(), '-SU', '')"/>
        </xsl:variable>

        <!-- Then each d:Instrument is dereferenced with the previous dereferenced tree used as references -->
        <xsl:for-each select="//d:Instrument">
            <xsl:variable name="template-id" select="d:ControlConstructReference/r:ID"/>
            <xsl:variable name="variable-groups" select="$dereferenced-template-sequence//*[r:ID=$template-id]//*[name()='d:Loop' or (name()='d:QuestionGrid' and d:GridDimension/d:Roster)]/r:ID"/>
            <xsl:variable name="form-name">
                <xsl:choose>
                    <xsl:when test="d:InstrumentName">
                        <xsl:value-of select="d:InstrumentName/r:String"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="replace(r:ID/text(), concat($root/text(),'-In-'), '')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:result-document href="{lower-case(concat('file:///',replace($output-folder, '\\' , '/'),'/',$form-name,'.tmp'))}">
                <DDIInstance xmlns="ddi:instance:3_3"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns:a="ddi:archive:3_3"
                    xmlns:eno="http://xml.insee.fr/apps/eno"
                    xmlns:enoddi33="http://xml.insee.fr/apps/eno/out/ddi33"
                    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
                    xmlns:pr="ddi:ddiprofile:3_3"
                    xmlns:c="ddi:conceptualcomponent:3_3"
                    xmlns:cm="ddi:comparative:3_3">
                    <s:StudyUnit>
                        <xsl:apply-templates select=".">
                            <xsl:with-param name="references" select="$dereferenced-template-sequence" tunnel="yes"/>
                        </xsl:apply-templates>
                    </s:StudyUnit>
                    <g:ResourcePackage>
                        <l:VariableScheme>
                            <xsl:apply-templates select="//l:VariableScheme/l:Variable[not(r:ID=//l:VariableScheme//r:VariableReference/r:ID)]
                                |//l:VariableScheme/l:VariableGroup[not(r:ID=//l:VariableScheme//r:VariableGroupReference/r:ID) 
                                and r:BasedOnObject/r:BasedOnReference/r:ID = $variable-groups]">
                                <xsl:with-param name="references" select="//l:VariableScheme" tunnel="yes"/>
                            </xsl:apply-templates>
                        </l:VariableScheme>
                    </g:ResourcePackage>
                </DDIInstance>
            </xsl:result-document>
        </xsl:for-each>

    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply copying to the
                output result.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Insert External Variable and Calculated Variable to the root template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence/text() = 'template']">
        <xsl:param name="references" tunnel="yes"/>
        <xsl:variable name="ID" select="r:ID"/>

        <xsl:variable name="multiple-variables" as="node()">
            <Variables>
                <xsl:for-each select="$references//l:VariableGroup/r:VariableReference">
                    <Variable>
                        <xsl:value-of select="r:ID"/>
                    </Variable>
                </xsl:for-each>
            </Variables>
        </xsl:variable>

        <xsl:copy>
            <xsl:apply-templates select="$references//l:VariableScheme
                                                        /l:Variable[r:ID=$external-variables//Variable/@id and not(r:ID=$multiple-variables/Variable)]"/>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:apply-templates select="$references//d:GenerationInstruction[d:ControlConstructReference/r:ID=$ID]"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Insert External Variable and Calculated Variable to loops.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Loop">
        <xsl:param name="references" tunnel="yes"/>
        <xsl:variable name="ID" select="r:ID"/>

        <xsl:copy>
            <xsl:apply-templates select="$references//l:VariableGroup[r:BasedOnObject/r:BasedOnReference/r:ID=$ID]
                /r:VariableReference[r:ID=$external-variables//Variable/@id]"/>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:apply-templates select="$references//d:GenerationInstruction[d:ControlConstructReference/r:ID=$ID]"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Insert External Variable and Calculated Variable to dynamical tables.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster]/d:StructuredMixedGridResponseDomain">
        <xsl:param name="references" tunnel="yes"/>
        <xsl:variable name="ID" select="../r:ID"/>

        <xsl:copy>
              <xsl:apply-templates select="$references//l:VariableGroup[r:BasedOnObject/r:BasedOnReference/r:ID=$ID]
                  /r:VariableReference[r:ID=$external-variables//Variable/@id]"/>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:apply-templates select="$references//d:GenerationInstruction[d:ControlConstructReference/r:ID=$ID]"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Template to insert GenerationInstruction, which reference the element they have to be included in.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node()[r:ID=$calculated-variables-sequences/Variable and not(ends-with(name(), 'Reference'))]">
        <xsl:param name="references" tunnel="yes"/>
        
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:apply-templates select="$references//d:GenerationInstruction[d:ControlConstructReference/r:ID=current()/r:ID]"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Calculated variables refer the ControlContructs they belong to, not the contrary.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:GenerationInstruction/d:ControlConstructReference"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element that corresponds to a reference.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node()[ends-with(name(), 'Reference') and not(parent::r:Binding) and not(name()='r:BasedOnReference')]/r:ID">
        <xsl:param name="references" tunnel="yes"/>
        <xsl:variable name="ID" select="."/>
        <!-- Copying the element -->
        <!-- Making sure we're not copying an element that isn't itself inside another reference (and that would actually not the base element but an already indexed reference) -->
        <xsl:apply-templates
            select="$references//*[r:ID = $ID and not(ancestor-or-self::node()[ends-with(name(), 'Reference') or starts-with(name(), 'd:Source')])]"
        />
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Specific template for external variables, for which there is no OutParameter to refer.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:ConditionalText/r:SourceParameterReference[r:TypeOfObject='InParameter']/r:ID" priority="2">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xd:doc>
        <xd:desc>In VariableScheme, Variable are used only for getters, not drivers and are simply copied</xd:desc>
    </xd:doc>
    <xsl:template match="l:Variable">
        <xsl:copy-of select="."/>
    </xsl:template>
    <!--
    <xd:doc>
        <xd:desc>
            <xd:p>Instruction are not allowed in Category for DDI 3.3. This template allows to insert tooltips into arrays' labels</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:a">
        <xsl:variable name="ref" select="replace(@href,'#','')"/>
        <xsl:variable name="language" select="ancestor::*[@xml:lang][1]/@xml:lang"/>

        <xsl:choose>
            <xsl:when test="//*[@id=$ref
                                and ancestor-or-self::*[@xml:lang][1]/@xml:lang=$language
                                and ancestor::d:Instruction/d:InstructionName/r:String[@xml:lang=$language]='tooltip']">
                <xsl:element name="xhtml:span">
                    <xsl:attribute name="title">
                        <xsl:value-of select="normalize-space(//*[@id=$ref
                                                                    and ancestor-or-self::*[@xml:lang][1]/@xml:lang=$language
                                                                    and ancestor::d:Instruction/d:InstructionName/r:String[@xml:lang=$language]='tooltip'])"/>
                    </xsl:attribute>
                    <xsl:text>&#160;</xsl:text>
                    <xsl:element name="img">
                        <xsl:attribute name="src" select="'/img/Help-browser.svg.png'"/>
                    </xsl:element>
                    <xsl:text>&#160;</xsl:text>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
-->
    <xsl:template match="*[name()='r:Label' or name()='d:DisplayText' or name()='d:InstructionText' or name()='d:QuestionText'][descendant::xhtml:a]">
        <xsl:variable name="ref" as="xs:string *">
            <xsl:for-each select="descendant::xhtml:a">
                <xsl:value-of select="replace(@href,'#','')"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="existing-tooltip" select="following-sibling::d:InterviewerInstructionReference/r:ID | following-sibling::d:InterviewerInstructionReference/d:Instruction/r:ID"/>
        <xsl:copy-of select="."/>
        
        <xsl:for-each select="//d:InterviewerInstructionScheme/d:Instruction[d:InstructionName/r:String='tooltip' and descendant::*/@id=$ref]">
            <xsl:if test="not(r:ID = $existing-tooltip)">
                <d:InterviewerInstructionReference>
                    <xsl:copy-of select="."/>
                </d:InterviewerInstructionReference>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
