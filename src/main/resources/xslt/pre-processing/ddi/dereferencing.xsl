<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:a="ddi:archive:3_3"
    xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3"
    xmlns:l="ddi:logicalproduct:3_3"
    xmlns:g="ddi:group:3_3"
    xmlns:s="ddi:studyunit:3_3"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:pr="ddi:ddiprofile:3_3"
    xmlns:c="ddi:conceptualcomponent:3_3"
    xmlns:cm="ddi:comparative:3_3"
    xmlns:ddi-instance="ddi:instance:3_3"
    xmlns:dereferencing="dereferencing"
    xmlns="ddi:instance:3_3"
    exclude-result-prefixes="xd"
    version="2.0">

    <xsl:output method="xml" indent="no" encoding="UTF-8"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The output folder in which the dereferenced files (one for each main sequence) are generated.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="output-folder"/>

    <xsl:param name="do-not-use-key" select="false()"/><!--set to false() if you don't want to use keys and test how much time keys make you save -->
    <xsl:param name="fast-and-dangerous-mode" select="false()"/><!--set to false() if you don't know-->
    <xsl:param name="build-messages" select="true()"/><!--set to true() if you doubt of the result and false() if you are sure it will succeed -->
    <xsl:param name="build-DDI" select="true()"/><!--set to true() if you don't know-->

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Aug 8, 2017</xd:p>
            <xd:p><xd:b>Author:</xd:b> nirnfv</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="follow-references">
        <dereferencing:reference-name name="r:CategoryReference"/>
        <dereferencing:reference-name name="r:CodeListReference"/>
        <dereferencing:reference-name name="r:CodeReference"/>
        <dereferencing:reference-name name="d:ControlConstructReference"/>
        <dereferencing:reference-name name="d:ThenConstructReference"/>
        <dereferencing:reference-name name="d:ElseConstructReference"/>
        <dereferencing:reference-name name="d:InterviewerInstructionReference"/>
        <dereferencing:reference-name name="r:QuestionReference"/>
        <dereferencing:reference-name name="d:QuestionItemReference"/>
        <dereferencing:reference-name name="d:QuestionGridReference"/>
        <dereferencing:reference-name name="d:DateTimeDomainReference"/>
        <dereferencing:reference-name name="r:DateTimeRepresentationReference"/>
        <dereferencing:reference-name name="d:NumericDomainReference"/>
        <dereferencing:reference-name name="r:NumericRepresentationReference"/>
        <dereferencing:reference-name name="r:VariableReference"/>
    </xsl:variable>

    <xsl:variable name="ignore-references">
        <dereferencing:reference-name name="r:QuestionSchemeReference"/>
        <dereferencing:reference-name name="r:ControlConstructSchemeReference"/>
        <dereferencing:reference-name name="r:InterviewerInstructionSchemeReference"/>
        <dereferencing:reference-name name="r:SourceParameterReference"/>
        <dereferencing:reference-name name="r:TargetParameterReference"/>
        <dereferencing:reference-name name="r:BasedOnReference"/>
        <dereferencing:reference-name name="r:ExternalURLReference"/>
        <!-- Celle-ci, je doute -->
        <dereferencing:reference-name name="r:ProcessingInstructionReference"/>
    </xsl:variable>

    <xsl:variable name="key-names">
        <xsl:choose>
            <xsl:when test="$do-not-use-key">
                <dereferencing:key-name name='_'  name1='_' name2='_'/>
            </xsl:when>
            <xsl:otherwise>
                <dereferencing:key-name name='r:CategoryReference-_-Category' name1='r:CategoryReference' name2='Category'/>
                <dereferencing:key-name name='r:CodeListReference-_-CodeList'  name1='r:CodeListReference' name2='CodeList'/>
                <dereferencing:key-name name='r:CodeReference-_-Code'  name1='r:CodeReference' name2='Code'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-ComputationItem' name1='d:ControlConstructReference' name2='ComputationItem'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-IfThenElse' name1='d:ControlConstructReference' name2='IfThenElse'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-QuestionConstruct' name1='d:ControlConstructReference' name2='QuestionConstruct'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-StatementItem' name1='d:ControlConstructReference' name2='StatementItem'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-Sequence' name1='d:ControlConstructReference' name2='Sequence'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-Loop' name1='d:ControlConstructReference' name2='Loop'/>
                <dereferencing:key-name name='d:ThenConstructReference-_-Sequence' name1='d:ThenConstructReference' name2='Sequence'/>
                <dereferencing:key-name name='d:ElseConstructReference-_-Sequence' name1='d:ElseConstructReference' name2='Sequence'/>
                <dereferencing:key-name name='d:InterviewerInstructionReference-_-Instruction' name1='d:InterviewerInstructionReference' name2='Instruction'/>
                <dereferencing:key-name name='r:QuestionReference-_-QuestionBlock' name1='r:QuestionReference' name2='QuestionBlock'/>
                <dereferencing:key-name name='r:QuestionReference-_-QuestionGrid' name1='r:QuestionReference' name2='QuestionGrid'/>
                <dereferencing:key-name name='r:QuestionReference-_-QuestionItem' name1='r:QuestionReference' name2='QuestionItem'/>
                <dereferencing:key-name name='d:QuestionGridReference-_-QuestionGrid' name1='d:QuestionGridReference' name2='QuestionGrid'/>
                <dereferencing:key-name name='d:QuestionItemReference-_-QuestionItem' name1='d:QuestionItemReference' name2='QuestionItem'/>
                <dereferencing:key-name name='d:DateTimeDomainReference-_-ManagedDateTimeRepresentation' name1='d:DateTimeDomainReference' name2='ManagedDateTimeRepresentation'/>
                <dereferencing:key-name name='r:DateTimeRepresentationReference-_-ManagedDateTimeRepresentation' name1='r:DateTimeRepresentationReference' name2='ManagedDateTimeRepresentation'/>
                <dereferencing:key-name name='d:NumericDomainReference-_-ManagedNumericRepresentation' name1='d:NumericDomainReference' name2='ManagedNumericRepresentation'/>
                <dereferencing:key-name name='r:NumericRepresentationReference-_-ManagedNumericRepresentation' name1='r:NumericRepresentationReference' name2='ManagedNumericRepresentation'/>
                <dereferencing:key-name name='r:VariableReference-_-Variable' name1='r:VariableReference' name2='Variable'/>
                <dereferencing:key-name name='l:VariableGroupReference-_-VariableGroup' name1='l:VariableGroupReference' name2='VariableGroup'/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:key name="r:CategoryReference-_-Category"                                    match="/ddi-instance:DDIInstance/g:ResourcePackage/l:CategoryScheme/l:Category"                                     use="r:ID"/>
    <xsl:key name="r:CodeListReference-_-CodeList"                                    match="/ddi-instance:DDIInstance/g:ResourcePackage/l:CodeListScheme/l:CodeList"                                   use="r:ID"/>
    <xsl:key name="r:CodeReference-_-Code"                                            match="/ddi-instance:DDIInstance/g:ResourcePackage/l:CodeListScheme/l:CodeList/l:Code"                            use="r:ID"/>
    <xsl:key name="d:ControlConstructReference-_-ComputationItem"                     match="/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:ComputationItem"                    use="r:ID"/>
    <xsl:key name="d:ControlConstructReference-_-IfThenElse"                          match="/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:IfThenElse"                         use="r:ID"/>
    <xsl:key name="d:ControlConstructReference-_-QuestionConstruct"                   match="/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:QuestionConstruct"                  use="r:ID"/>
    <xsl:key name="d:ControlConstructReference-_-StatementItem"                       match="/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:StatementItem"                      use="r:ID"/>
    <xsl:key name="d:ControlConstructReference-_-Sequence"                            match="/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:Sequence"                           use="r:ID"/>
    <xsl:key name="d:ControlConstructReference-_-Loop"                                match="/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:Loop"                               use="r:ID"/>
    <xsl:key name="d:ThenConstructReference-_-Sequence"                               match="/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:Sequence"                           use="r:ID"/>
    <xsl:key name="d:ElseConstructReference-_-Sequence"                               match="/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:Sequence"                           use="r:ID"/>
    <xsl:key name="d:InterviewerInstructionReference-_-Instruction"                   match="/ddi-instance:DDIInstance/g:ResourcePackage/d:InterviewerInstructionScheme/d:Instruction"                  use="r:ID"/>
    <xsl:key name="r:QuestionReference-_-QuestionBlock"                               match="/ddi-instance:DDIInstance/g:ResourcePackage/d:QuestionScheme/d:QuestionBlock"                              use="r:ID"/>
    <xsl:key name="r:QuestionReference-_-QuestionGrid"                                match="/ddi-instance:DDIInstance/g:ResourcePackage/d:QuestionScheme/d:QuestionGrid"                               use="r:ID"/>
    <xsl:key name="r:QuestionReference-_-QuestionItem"                                match="/ddi-instance:DDIInstance/g:ResourcePackage/d:QuestionScheme/d:QuestionItem"                               use="r:ID"/>
    <xsl:key name="d:QuestionGridReference-_-QuestionGrid"                            match="/ddi-instance:DDIInstance/g:ResourcePackage/d:QuestionScheme/d:QuestionGrid"                               use="r:ID"/>
    <xsl:key name="d:QuestionItemReference-_-QuestionItem"                            match="/ddi-instance:DDIInstance/g:ResourcePackage/d:QuestionScheme/d:QuestionItem"                               use="r:ID"/>
    <xsl:key name="d:DateTimeDomainReference-_-ManagedDateTimeRepresentation"         match="/ddi-instance:DDIInstance/g:ResourcePackage/r:ManagedRepresentationScheme/r:ManagedDateTimeRepresentation" use="r:ID"/>
    <xsl:key name="r:DateTimeRepresentationReference-_-ManagedDateTimeRepresentation" match="/ddi-instance:DDIInstance/g:ResourcePackage/r:ManagedRepresentationScheme/r:ManagedDateTimeRepresentation" use="r:ID"/>
    <xsl:key name="d:NumericDomainReference-_-ManagedNumericRepresentation"           match="/ddi-instance:DDIInstance/g:ResourcePackage/r:ManagedRepresentationScheme/r:ManagedNumericRepresentation"  use="r:ID"/>
    <xsl:key name="r:NumericRepresentationReference-_-ManagedNumericRepresentation"   match="/ddi-instance:DDIInstance/g:ResourcePackage/r:ManagedRepresentationScheme/r:ManagedNumericRepresentation"  use="r:ID"/>
    <xsl:key name="r:VariableReference-_-Variable"                                    match="/ddi-instance:DDIInstance/g:ResourcePackage/l:VariableScheme/l:Variable"                                   use="r:ID"/>
    <xsl:key name="l:VariableGroupReference-_-VariableGroup"                          match="/ddi-instance:DDIInstance/g:ResourcePackage/l:VariableScheme/l:VariableGroup"                              use="r:ID"/>
    <!--when modifying keys : modify key-names variable ; if you do not the program will not work-->
    <xsl:key name="external-variable" match="/ddi-instance:DDIInstance/g:ResourcePackage/l:VariableScheme/l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference)]" use="r:ID"/>
    <xsl:key name="referenced-variable" match="/ddi-instance:DDIInstance/g:ResourcePackage/l:VariableScheme/l:VariableGroup/r:VariableReference" use="r:ID"/>
    <xsl:key name="calculated-variable" match="/ddi-instance:DDIInstance/g:ResourcePackage/d:ProcessingInstructionScheme/d:GenerationInstruction" use="d:ControlConstructReference/r:ID"/>
    <xsl:key name="variablegroup" match="/ddi-instance:DDIInstance/g:ResourcePackage/l:VariableScheme/l:VariableGroup" use="r:BasedOnObject/r:BasedOnReference[1]/r:ID"/>
    <xsl:key name="tooltip-with-id" match="/ddi-instance:DDIInstance/g:ResourcePackage/d:InterviewerInstructionScheme/d:Instruction[d:InstructionName/r:String='tooltip' and descendant::*/@id]" use="descendant::*/@id"/>

    <xsl:variable name="message-label">
        <dereferencing:message type-number="1" message-order="1">
            <dereferencing:label>Unknown Reference tag</dereferencing:label>
            <dereferencing:modifyXSLcode>Modify "follow-references" or "ignore references" variable, by adding the unknown reference tag. One or more key is also needed, see other messages.</dereferencing:modifyXSLcode>
        </dereferencing:message>
        <dereferencing:message type-number="2" message-order="2">
            <dereferencing:label>Missing ID node</dereferencing:label>
            <dereferencing:modifyDDIcode>Add non empty ID tag to the tag described in the where section</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="3" message-order="3">
            <dereferencing:label>Non-unique ID found with xsl:key</dereferencing:label>
            <dereferencing:modifyDDIcode>Find the incorrect ID and modify it in the DDI code.</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="4" message-order="4">
            <dereferencing:label>ID not found with xsl:key but found with global search</dereferencing:label>
            <dereferencing:modifyDDIcode>There is either a problem in the reference node or in the target node (or in both) : the type of object or the reference tag is incorrect in one of the node</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="5" message-order="5">
            <dereferencing:label>xsl:key creation requested</dereferencing:label>
            <dereferencing:modifyXSLcode>There is no key for this combination of reference and type of object.
                It is therefore necessary to create a xsl:key node with the proposed name, match and use (in value node).
                Do not forget to update the key-names variable using name, name1 and name2 in value node : it will not work if you do not do it.</dereferencing:modifyXSLcode>
        </dereferencing:message>
        <dereferencing:message type-number="6" message-order="6">
            <dereferencing:label>Type mismatch</dereferencing:label>
            <dereferencing:modifyDDIcode>The type of object in the reference node and the tag of the target node does node match.</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="7" message-order="7">
            <dereferencing:label>Non-unique ID found with global search</dereferencing:label>
            <dereferencing:modifyDDIcode>A key search of the ID was not possible, and a global search of the ID in the document brought more than one match back.</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="8" message-order="8">
            <dereferencing:label>ID not found with xsl:key and global search</dereferencing:label>
            <dereferencing:modifyDDIcode>A key search then a global search in the document brought no match back.</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="9" message-order="9">
            <dereferencing:label>ID not found with global search</dereferencing:label>
            <dereferencing:modifyDDIcode>A key search of the ID was not possible, and a global search in the document brought no match back.</dereferencing:modifyDDIcode>
        </dereferencing:message>
    </xsl:variable>


    <xd:doc>
        <xd:desc>Root template</xd:desc>
    </xd:doc>

    <xsl:template match="/">
        <xsl:if test="$build-messages">
            <dereferencing:dereferencing-result-messages>
                <xsl:variable name="messages-all">
                    <xsl:for-each select="ddi-instance:DDIInstance/s:StudyUnit/d:DataCollection/d:InstrumentScheme/d:Instrument">
                        <xsl:apply-templates select="." mode="output-message"/>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:for-each select="$message-label/*" >
                    <xsl:sort select="number(dereferencing:message-order)"/>
                    <xsl:if test="$messages-all/*[dereferencing:type-number=current()/@type-number]">
                        <xsl:copy>
                            <xsl:copy-of select="./@* | ./node() |text()"/>
                            <xsl:choose>
                                <xsl:when test="dereferencing:modifyXSLcode">
                                    <xsl:for-each-group select="$messages-all/*[./dereferencing:type-number=current()/@type-number]" group-by="./dereferencing:value">
                                        <xsl:sort select="./dereferencing:value"/>
                                        <dereferencing:value>
                                            <xsl:copy-of select="./dereferencing:value/*"/>
                                            <dereferencing:where-list>
                                                <xsl:for-each-group select="current-group()" group-by="dereferencing:where">
                                                    <xsl:sort select="dereferencing:where"/>
                                                    <xsl:copy-of select="./dereferencing:where"/>
                                                </xsl:for-each-group>
                                            </dereferencing:where-list>
                                        </dereferencing:value>
                                    </xsl:for-each-group>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:for-each-group select="$messages-all/*[./dereferencing:type-number=current()/@type-number]" group-by="./dereferencing:where">
                                        <xsl:sort select="./dereferencing:where"/>
                                        <xsl:copy-of select="./*[name() != 'dereferencing:type-number']"/>
                                    </xsl:for-each-group>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:copy>
                    </xsl:if>
                </xsl:for-each>
            </dereferencing:dereferencing-result-messages>
        </xsl:if>
        <xsl:if test="$build-DDI">
            <xsl:for-each select="ddi-instance:DDIInstance/s:StudyUnit/d:DataCollection/d:InstrumentScheme/d:Instrument">
                <xsl:variable name="form-name">
                    <xsl:choose>
                        <xsl:when test="d:InstrumentName">
                            <xsl:value-of select="d:InstrumentName/r:String"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="replace(r:ID/text(), concat(replace(//s:StudyUnit/r:ID/text(), '-SU', ''),'-In-'), '')"/>
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
                            <xsl:apply-templates select="." mode="output-DDI">
                                <xsl:with-param name="form-id" select="r:ID" tunnel="yes"/>
                            </xsl:apply-templates>
                        </s:StudyUnit>
                        <g:ResourcePackage>
                            <l:VariableScheme>
                                <xsl:choose>
                                    <xsl:when test="key('variablegroup',r:ID)">
                                        <xsl:apply-templates select="key('variablegroup',r:ID)" mode="output-DDI"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:apply-templates select="//l:VariableScheme/l:Variable[not(r:ID=//l:VariableScheme//r:VariableReference/r:ID)]
                                            |//l:VariableScheme/l:VariableGroup[not(r:ID=//l:VariableScheme//l:VariableGroupReference/r:ID)]" mode="output-DDI">
                                        </xsl:apply-templates>                                        
                                    </xsl:otherwise>
                                </xsl:choose>
                            </l:VariableScheme>
                        </g:ResourcePackage>
                    </DDIInstance>
                </xsl:result-document>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>Default template : identity template.</xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="output-DDI">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="* | text() | comment() | processing-instruction()" mode="output-DDI"/>
            <xsl:apply-templates select="key('calculated-variable',r:ID)" mode="output-DDI"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Not to dereference templates.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:GenerationInstruction/d:ControlConstructReference" mode="output-DDI" priority="1"/>

    <xsl:template match="l:Variable" mode="output-DDI" priority="1">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xd:doc>
        <xd:desc>Template for Reference tags</xd:desc>
    </xd:doc>
    <xsl:template match="node()[ends-with(name(.), 'Reference')]" mode="output-DDI">

        <xsl:variable name="copy-node-name" select="name(.)"/>
        <xsl:choose>
            <xsl:when test="count($ignore-references/dereferencing:reference-name[@name=$copy-node-name])!=0
                    and not(count($follow-references/dereferencing:reference-name[@name=$copy-node-name])!=0)">
                <xsl:copy>
                    <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
                    <xsl:apply-templates select="*" mode="output-DDI"/>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
                    <xsl:copy-of select="./r:Agency"/>
                    <xsl:choose>
                        <xsl:when test="$fast-and-dangerous-mode">
                            <xsl:apply-templates select="key(concat(name(.),'-_-',./r:TypeOfObject),./r:ID)" mode="output-DDI"/>
                        </xsl:when>
                        <xsl:when test="count(./r:ID)=0"/>
                        <xsl:when test="$key-names/dereferencing:key-name[@name1=name(current()) and @name2=current()/r:TypeOfObject]">
                            <xsl:variable name="result-key">
                                <xsl:copy-of select="key(concat(name(.),'-_-',./r:TypeOfObject),./r:ID)"/>
                            </xsl:variable>
                            <xsl:choose>
                                <xsl:when test="count($result-key/*)=1">
                                    <xsl:apply-templates select="key(concat(name(.),'-_-',./r:TypeOfObject),./r:ID)" mode="output-DDI"/>
                                </xsl:when>
                                <xsl:when test="count($result-key/*)>1">
                                    <xsl:copy-of select="./r:ID"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:variable name="current-id" select="./r:ID"/>
                                    <xsl:variable name="current-name" select="name(.)"/>
                                    <xsl:variable name="reference-targets"><xsl:copy-of select="//*[r:ID=$current-id and name(.) != $current-name]"/></xsl:variable>

                                    <xsl:choose>
                                        <xsl:when test="count($reference-targets/*) = 1">
                                            <xsl:apply-templates select="//*[r:ID=$current-id and name(.) != $current-name]" mode="output-DDI"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:copy-of select="$current-id"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:variable name="current-id" select="./r:ID"/>
                            <xsl:variable name="current-name" select="name(.)"/>
                            <xsl:variable name="reference-targets"><xsl:copy-of select="//*[r:ID=$current-id and name(.) != $current-name]"/></xsl:variable>

                            <xsl:choose>
                                <xsl:when test="count($reference-targets/*) = 1">
                                    <xsl:apply-templates select="//*[r:ID=$current-id and name(.) != $current-name]" mode="output-DDI"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:copy-of select="./r:ID"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:apply-templates select="./*[name() != 'r:ID' and name() != 'r:Agency']" mode="output-DDI"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>Template for template Sequence</xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence/text() = 'template']" mode="output-DDI">
        <xsl:param name="form-id" tunnel="yes"/>
        <xsl:variable name="current-ID" select="r:ID"/>

        <xsl:copy>
            <xsl:choose>
                <xsl:when test="key('variablegroup',$form-id)">
                    <xsl:apply-templates select="key('variablegroup',$form-id)/r:VariableReference/key('external-variable',r:ID)" mode="output-DDI"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="//l:VariableScheme/l:Variable">
                        <xsl:if test="count(key('external-variable',r:ID))=1 and not(count(key('referenced-variable',r:ID))=1)">
                            <xsl:apply-templates select="key('external-variable',r:ID)" mode="output-DDI"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="*" mode="output-DDI"/>
            <xsl:apply-templates select="key('calculated-variable',$current-ID)" mode="output-DDI"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>Template for Loop</xd:desc>
    </xd:doc>
    <xsl:template match="d:Loop" mode="output-DDI">
        <xsl:variable name="current-ID" select="r:ID"/>

        <xsl:copy>
            <xsl:choose>
                <xsl:when test="key('variablegroup',$current-ID)">
                    <xsl:apply-templates select="key('variablegroup',$current-ID)/r:VariableReference/key('external-variable',r:ID)" mode="output-DDI"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="/ddi-instance:DDIInstance/g:ResourcePackage/l:VariableScheme/l:VariableGroup[r:BasedOnObject/r:BasedOnReference/r:ID = $current-ID]
                                                                                                                             /r:VariableReference/key('external-variable',r:ID)" mode="output-DDI"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="*" mode="output-DDI"/>
            <xsl:apply-templates select="key('calculated-variable',$current-ID)" mode="output-DDI"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>labels with reference to a tooltip</xd:desc>
    </xd:doc>
    <xsl:template match="*[name()='r:Label' or name()='d:DisplayText' or name()='d:InstructionText' or name()='d:QuestionText'][descendant::xhtml:a]" mode="output-DDI">
        <xsl:variable name="ref" as="xs:string *">
            <xsl:for-each select="descendant::xhtml:a">
                <xsl:value-of select="replace(@href,'#','')"/>
            </xsl:for-each>
        </xsl:variable>

        <xsl:copy-of select="."/>
        <xsl:for-each select="key('tooltip-with-id',$ref)">
            <d:InterviewerInstructionReference>
                <xsl:apply-templates select="." mode="output-DDI"/>
            </d:InterviewerInstructionReference>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>Template for dynamic array</xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster]/d:StructuredMixedGridResponseDomain" mode="output-DDI">
        <xsl:variable name="loop-ID" select="../r:ID"/>

        <xsl:copy>
            <xsl:apply-templates select="key('variablegroup',$loop-ID)/r:VariableReference/key('external-variable',r:ID)" mode="output-DDI"/>
            <xsl:apply-templates select="*" mode="output-DDI"/>
            <xsl:apply-templates select="key('calculated-variable',$loop-ID)" mode="output-DDI"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>Default template : identity template.</xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="output-message">
        <xsl:apply-templates select="*" mode="output-message"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>Template for Reference tags</xd:desc>
    </xd:doc>
    <xsl:template match="node()[ends-with(name(.), 'Reference')]" mode="output-message">

        <xsl:variable name="copy-node-name" select="name(.)"/>
        <xsl:choose>
            <xsl:when test="count($follow-references/dereferencing:reference-name[@name=$copy-node-name])!=0">
                <xsl:call-template name="xxx-Reference-ID"/>
                <xsl:apply-templates select="./*[name() != 'r:ID' and name() != 'r:Agency']" mode="output-message"/>
            </xsl:when>
            <xsl:when test="count($ignore-references/dereferencing:reference-name[@name=$copy-node-name])!=0">
                <xsl:apply-templates select="*" mode="output-message"/>
            </xsl:when>
            <xsl:otherwise>
                <dereferencing:warning>
                    <dereferencing:type-number>1</dereferencing:type-number>
                    <dereferencing:value><xsl:value-of select="$copy-node-name"/></dereferencing:value>
                    <xsl:call-template name="where-message"/>
                </dereferencing:warning>
                <xsl:call-template name="xxx-Reference-ID"/>
                <xsl:apply-templates select="./*[name() != 'r:ID' and name() != 'r:Agency']" mode="output-message"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>Base template for searched References</xd:desc>
    </xd:doc>
    <xsl:template name="xxx-Reference-ID">

        <xsl:choose>
            <xsl:when test="$fast-and-dangerous-mode">
                <xsl:apply-templates select="key(concat(name(.),'-_-',./r:TypeOfObject),./r:ID)" mode="output-message"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="count(./r:ID)=0">
                        <dereferencing:warning>
                            <dereferencing:type-number>2</dereferencing:type-number>
                            <xsl:call-template name="where-message"/>
                        </dereferencing:warning>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="$key-names/dereferencing:key-name[@name1=name(current()) and @name2=current()/r:TypeOfObject]">
                                <xsl:variable name="result-key">
                                    <xsl:copy-of select="key(concat(name(.),'-_-',./r:TypeOfObject),./r:ID)"/>
                                </xsl:variable>
                                <xsl:choose>
                                    <xsl:when test="count($result-key/*)=1">
                                        <xsl:apply-templates select="key(concat(name(.),'-_-',./r:TypeOfObject),./r:ID)" mode="output-message"/>
                                    </xsl:when>
                                    <xsl:when test="count($result-key/*)>1">
                                        <dereferencing:warning>
                                            <dereferencing:type-number>3</dereferencing:type-number>
                                            <xsl:call-template name="where-message"/>
                                        </dereferencing:warning>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:call-template name="find-id-without-key">
                                            <xsl:with-param name="key-existence" select="true()"/>
                                        </xsl:call-template>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:call-template name="find-id-without-key">
                                    <xsl:with-param name="key-existence" select="false()"/>
                                </xsl:call-template>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>Template to find an id with a global search.</xd:desc>
        <xd:param name="key-existence"/>
    </xd:doc>
    <xsl:template name="find-id-without-key">
        <xsl:param name="key-existence"/>

        <xsl:variable name="current-id" select="./r:ID"/>
        <xsl:variable name="current-name" select="name(.)"/>
        <xsl:variable name="current-type" select="./r:TypeOfObject"/>
        <xsl:variable name="reference-targets"><xsl:copy-of select="//*[r:ID=$current-id and name(.) != $current-name]"/></xsl:variable>

        <xsl:choose>
            <xsl:when test="count($reference-targets/*) = 1">
                <xsl:variable name="reference-targets-parent-name" select="name((//*[r:ID=$current-id and name(.) != $current-name])[1]/..)"/>
                <xsl:variable name="reference-targets-name" select="name((//*[r:ID=$current-id and name(.) != $current-name])[1])"/>
                <xsl:variable name="reference-targets-local-name" select="local-name((//*[r:ID=$current-id and name(.) != $current-name])[1])"/>
                <dereferencing:warning>
                    <xsl:choose>
                        <xsl:when test="$key-existence=true()">
                            <dereferencing:type-number>4</dereferencing:type-number>
                            <dereferencing:reference-target-comparison>
                                <dereferencing:reference><xsl:value-of select="$current-name"/></dereferencing:reference>
                                <dereferencing:target-scheme><xsl:value-of select="$reference-targets-parent-name"/></dereferencing:target-scheme>
                                <dereferencing:type-of-object><xsl:value-of select="$current-type"/></dereferencing:type-of-object>
                                <dereferencing:target-type><xsl:value-of select="$reference-targets-name"/></dereferencing:target-type>
                                <dereferencing:ID><xsl:value-of select="$current-id"/></dereferencing:ID>
                            </dereferencing:reference-target-comparison>
                            <xsl:call-template name="where-message"/>
                        </xsl:when>
                        <xsl:when test="$key-existence=false()">
                            <xsl:choose>
                                <xsl:when test="$reference-targets-local-name = $current-type">
                                    <dereferencing:type-number>5</dereferencing:type-number>
                                    <dereferencing:value>
                                        <dereferencing:name><xsl:value-of select="concat($current-name,'-_-',$current-type)"/></dereferencing:name>
                                        <dereferencing:match><xsl:value-of select="concat('/ddi-instance:DDIInstance/g:ResourcePackage/',$reference-targets-parent-name,'/',$reference-targets-name)"/></dereferencing:match>
                                        <dereferencing:use><xsl:value-of select="'r:ID'"/></dereferencing:use>
                                        <dereferencing:name1><xsl:value-of select="$current-name"/></dereferencing:name1>
                                        <dereferencing:name2><xsl:value-of select="$current-type"/></dereferencing:name2>
                                    </dereferencing:value>
                                    <xsl:call-template name="where-message"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <dereferencing:type-number>6</dereferencing:type-number>
                                    <dereferencing:reference-target-comparison>
                                        <dereferencing:reference><xsl:value-of select="$current-name"/></dereferencing:reference>
                                        <dereferencing:target-scheme><xsl:value-of select="$reference-targets-parent-name"/></dereferencing:target-scheme>
                                        <dereferencing:type-of-object><xsl:value-of select="$current-type"/></dereferencing:type-of-object>
                                        <dereferencing:target-type><xsl:value-of select="$reference-targets-name"/></dereferencing:target-type>
                                        <dereferencing:ID><xsl:value-of select="$current-id"/></dereferencing:ID>
                                    </dereferencing:reference-target-comparison>
                                    <xsl:call-template name="where-message"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise/>
                    </xsl:choose>
                </dereferencing:warning>
                <xsl:apply-templates select="//*[r:ID=$current-id and name(.) != $current-name]" mode="output-message"/>
            </xsl:when>
            <xsl:otherwise>
                <dereferencing:warning>
                    <xsl:choose>
                        <xsl:when test="count($reference-targets/*) > 1">
                            <dereferencing:type-number>7</dereferencing:type-number>
                            <xsl:call-template name="where-message"/>
                        </xsl:when>
                        <xsl:when test="$key-existence=true()">
                            <dereferencing:type-number>8</dereferencing:type-number>
                            <xsl:call-template name="where-message"/>
                        </xsl:when>
                        <xsl:when test="$key-existence=false()">
                            <dereferencing:type-number>9</dereferencing:type-number>
                            <xsl:call-template name="where-message"/>
                        </xsl:when>
                    </xsl:choose>
                </dereferencing:warning>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>Writing where the message comes from.</xd:desc>
    </xd:doc>
    <xsl:template name="where-message">
        <dereferencing:where>
            <dereferencing:current-id><xsl:value-of select="./r:ID"/></dereferencing:current-id>
            <dereferencing:current-name><xsl:value-of select="name(.)"/></dereferencing:current-name>
            <dereferencing:current-type><xsl:value-of select="./r:TypeOfObject"/></dereferencing:current-type>
            <dereferencing:parent-id><xsl:value-of select="../r:ID"/></dereferencing:parent-id>
            <dereferencing:parent-name><xsl:value-of select="name(..)"/></dereferencing:parent-name>
            <dereferencing:parent-type><xsl:value-of select="../r:TypeOfObject"/></dereferencing:parent-type>
        </dereferencing:where>
    </xsl:template>
</xsl:stylesheet>

