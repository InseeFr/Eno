<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" version="2.0">

    <!-- Importing the different resources -->
<!--    <xsl:import href="../../lib.xsl"/>-->
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>A library of getter functions for fods with their implementations for different elements.</xd:p>
        </xd:desc>
    </xd:doc>

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
            <xd:p>Getting the languages list used in the DDI input.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence/text()='template']" mode="enoddi:get-languages"
        as="xs:string *">
        <xsl:for-each-group select="//@xml:lang" group-by=".">
            <xsl:value-of select="current-grouping-key()"/>
        </xsl:for-each-group>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>For a node() which isn't one of those three, there can't be a defined language.</xd:p>
            <xd:p>It's child is returned.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[not(r:String) and not(r:Content) and not(xhtml:p)]" mode="lang-choice">
        <xsl:sequence select="child::node()"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>For a child of an xhtml:p element, it is directly returned.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[parent::xhtml:p]" priority="1" mode="lang-choice">
        <xsl:sequence select="."/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>For a text(). The value is simply returned</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="text()" mode="lang-choice">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>For those nodes, the language is used to return the right text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node()[r:Content or r:String or xhtml:p]" mode="lang-choice">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="r:Content[@xml:lang=$language] or r:String[@xml:lang=$language]">
                <xsl:sequence select="child::node()[@xml:lang=$language]/child::node()"/>
            </xsl:when>
            <xsl:when test="xhtml:p[@xml:lang=$language]">
                <xsl:sequence select="xhtml:p[@xml:lang=$language]"/>
            </xsl:when>
            <xsl:when test="xhtml:p[not(@xml:lang)]">
                <xsl:sequence select="xhtml:p"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="child::node()/child::node()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting the suffix for different response domains.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[ends-with(name(),'DomainReference') or ends-with(name(),'Domain')]"
        mode="enoddi:get-suffix">
        <xsl:param name="language" tunnel="yes"/>
        
        <xsl:variable name="ddi-variable" select="enoddi:get-id(.)"/>
        <xsl:variable name="variable-measurement-unit" select="//l:VariableScheme//l:Variable[r:SourceParameterReference/r:ID = $ddi-variable]/l:VariableRepresentation/r:MeasurementUnit"/>
        <xsl:choose>
            <xsl:when test="$variable-measurement-unit != ''">
                <xsl:value-of select="$variable-measurement-unit"/>
            </xsl:when>
            <xsl:when test="*[ends-with(name(),'Representation')]/r:Label/r:Content/@xml:lang">
                <xsl:value-of
                    select="*[ends-with(name(),'Representation')]/r:Label/r:Content[@xml:lang=$language]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="*[ends-with(name(),'Representation')]/r:Label/r:Content"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting levels of first dimension in d:QuestionGrid elements.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid" mode="enoddi:get-levels-first-dimension">
        <xsl:apply-templates select="d:GridDimension[@rank='1']" mode="enoddi:get-levels"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting levels of second dimension in d:QuestionGrid elements.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid" mode="enoddi:get-levels-second-dimension">
        <xsl:apply-templates select="d:GridDimension[@rank='2']" mode="enoddi:get-levels"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting codes of first dimension in d:QuestionGrid elements having d:GridDimension/d:Roster child.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster]"
        mode="enoddi:get-codes-first-dimension">
        <xsl:variable name="levels">
            <!-- Only one attribute amongst rangeMinimum and specificValue is present -->
            <xsl:for-each-group
                select="d:StructuredMixedGridResponseDomain/*[name()='d:GridResponseDomain' or name()='d:NoDataByDefinition']//d:SelectDimension[@rank='1']"
                group-by="concat(@rangeMinimum,@specificValue)">
                <dummy/>
            </xsl:for-each-group>
        </xsl:variable>
        <xsl:sequence select="$levels/*"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting codes of first dimension in d:QuestionGrid elements not having d:GridDimension/d:Roster child.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid[not(d:GridDimension/d:Roster)]"
        mode="enoddi:get-codes-first-dimension">
        <xsl:sequence select="d:GridDimension[@rank='1']//l:Code[not(descendant::l:Code)]"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting the virtual 'levels' in a d:GridDimension.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:GridDimension" mode="enoddi:get-levels">
        <xsl:variable name="levels">
            <xsl:for-each select="d:CodeDomain/r:CodeListReference/l:CodeList//l:CodeList[r:Label]">
                <dummy/>
            </xsl:for-each>
            <xsl:for-each-group select="d:CodeDomain/r:CodeListReference/l:CodeList//l:Code"
                group-by="@levelNumber">
                <dummy/>
            </xsl:for-each-group>
        </xsl:variable>
        <xsl:sequence select="$levels/*"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting a title line depending on an index number within a d:QuestionGrid.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid" mode="enoddi:get-title-line">
        <xsl:param name="index" tunnel="yes" as="xs:integer"/>
        <!-- Counting the labels located at the top of the referenced list (if they exist, they should not be taken into account)-->
        <xsl:variable name="label-or-no">
            <xsl:value-of
                select="count(d:GridDimension[@rank='2']/d:CodeDomain/r:CodeListReference/l:CodeList/r:Label)"
            />
        </xsl:variable>

        <!-- If it is the first line -->
        <xsl:if test="$index=1">
            <xsl:choose>
                <xsl:when test="d:GridDimension[@rank='1']//l:CodeList/r:Label">
                    <xsl:sequence select="d:GridDimension[@rank='1']//l:CodeList/r:Label"/>        
                </xsl:when>
                <xsl:otherwise>
                    <xsl:sequence select="d:GridDimension[@rank='1']"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <!-- Getting :
        1) codes which nesting level in codes lists with labels is equal to index+1
        2) labels from l:CodeList-->

        <xsl:sequence
            select="d:GridDimension[@rank='2']//(l:Code[count(ancestor::l:CodeList[r:Label])=$index+number($label-or-no)-1] | l:CodeList/r:Label[count(ancestor::l:CodeList[r:Label])=$index+number($label-or-no)])"
        />
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting a table line depending on an index number within a d:QuestionGrid.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid" mode="enoddi:get-table-line">
        <xsl:param name="index" tunnel="yes"/>
        <xsl:variable name="codes">
            <xsl:apply-templates select="." mode="enoddi:get-codes-first-dimension"/>
        </xsl:variable>
        <xsl:variable name="id">
            <xsl:value-of select="$codes/l:Code[position()=$index]/r:ID"/>
        </xsl:variable>

        <xsl:apply-templates select="d:GridDimension[@rank='1']//l:Code[r:ID=$id]"
            mode="enoddi:get-table-line"/>
        <xsl:for-each
            select="d:StructuredMixedGridResponseDomain/(d:GridResponseDomain | d:NoDataByDefinition)[.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1' and (@rangeMinimum=string($index) or @specificValue=string($index))]]">
            <xsl:sort
                select="number(.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum)"/>
            <xsl:sequence select="."/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting a table line for an l:Code.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Code" mode="enoddi:get-table-line">
        <xsl:if test="parent::l:Code">
            <xsl:variable name="first-parent-code-id">
                <xsl:value-of select="parent::l:Code/l:Code[1]/r:ID"/>
            </xsl:variable>
            <xsl:if test="r:ID=$first-parent-code-id">
                <xsl:apply-templates select="parent::l:Code" mode="enoddi:get-table-line"/>
            </xsl:if>
        </xsl:if>
        <xsl:sequence select="."/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting the colspan for l:Code belonging to a 1-dimension of several levels.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="l:Code[max(ancestor::d:GridDimension[@rank='1']//l:Code[not(l:Code)]/count(ancestor::l:CodeList | ancestor::l:Code))>1]"
        mode="enoddi:get-colspan" priority="1">
        <!-- Getting the depth-level of parents codes -->
        <xsl:variable name="parents">
            <xsl:value-of
                select="if (string(count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) != 'NaN') then count(ancestor::l:CodeList[r:Label] | ancestor::l:Code) else 0"
            />
        </xsl:variable>
        <!-- Getting the depth-level of children codes -->
        <xsl:variable name="children">
            <xsl:value-of
                select="if (string(max(.//l:Code[not(l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))-count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) !='') then max(.//l:Code[not(l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))-count(ancestor::l:CodeList[r:Label] | ancestor::l:Code) else 0"
            />
        </xsl:variable>
        <xsl:value-of
            select="max(ancestor::d:GridDimension[@rank='1']//l:Code[not(l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))-number($parents)-number($children)+1"
        />
    </xsl:template>

    <!-- For the column headers (that are also part of the line) -->
    <!-- Those are the r:Label that are directly on top of the referenced codes list -->
    <!-- We need to get the depth level from the 2nd dimension -->
    <!-- So, we select all the l:Code that have no child (and therefore have the highest depth-level),
    for each one we get this depth-level
    and we keep the maximum, which will be the depth of the 2nd dimension-->
    <xsl:template
        match="r:Label[parent::l:CodeList/parent::r:CodeListReference/parent::d:CodeDomain/parent::d:GridDimension[@rank='1']]"
        mode="enoddi:get-rowspan" priority="1">
        <xsl:variable name="label-or-no">
            <xsl:value-of
                select="count(ancestor::d:GridDimension[@rank='1']/following-sibling::d:GridDimension[@rank='2']/d:CodeDomain/r:CodeListReference/l:CodeList/r:Label)"
            />
        </xsl:variable>
        <xsl:value-of
            select="max(ancestor::d:GridDimension[@rank='1']/following-sibling::d:GridDimension[@rank='2']//l:Code[not(l:Code)]/count(ancestor::l:CodeList[r:Label]))+1-number($label-or-no)"
        />
    </xsl:template>

    <xsl:template
        match="r:Label[parent::l:CodeList/parent::r:CodeListReference/parent::d:CodeDomain/parent::d:GridDimension[@rank='1' and ../d:GridDimension[@rank='2']]]"
        mode="enoddi:get-colspan" priority="1">
        <xsl:variable name="label-or-no">
            <xsl:value-of
                select="count(../r:Label)-1"
            />
        </xsl:variable>
        
        <xsl:value-of select="max(parent::l:CodeList//l:Code[not(l:Code)]/count(ancestor::l:Code))+1-$label-or-no"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Getting rowspan for the line labels (1st dimension). It depends on the depth level.</xd:p>
            <xd:p>When l:Code has a l:Code (representing a box dispatched in sub-boxes), we get the number of children l:Code</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='1'] and l:Code]"
        mode="enoddi:get-rowspan" priority="1">
        <xsl:value-of select="count(descendant::l:Code[not(l:Code)])"/>
    </xsl:template>

    <!-- WARNING -->
    <!-- At the moment, this is equal to the number of l:Code that we find lower. This will only work with 2 levels. -->
    <!-- Consider an evolution where the table header would have 3 levels -->
    <xsl:template match="r:Label[ancestor::d:GridDimension[@rank='2']]" mode="enoddi:get-colspan"
        priority="1">
        <xsl:value-of select="count(parent::l:CodeList//l:Code)"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Getting rowspan for the line labels (2nd dimension). It depends on the depth level.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='2']]" mode="enoddi:get-rowspan"
        priority="1">
        <xsl:value-of
            select="max(ancestor::d:GridDimension[@rank='2']//l:Code[not(l:Code)]/count(ancestor::l:CodeList[r:Label]))+1-count(ancestor::l:CodeList[r:Label])"
        />
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting colspan for d:NoDataByDefinition elements for which colspan can be different from 1.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:NoDataByDefinition[d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMaximum]" mode="enoddi:get-colspan" priority="1">
        <xsl:value-of select="string(1
                                    +number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMaximum)
                                    -number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum))"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting rowspan for d:NoDataByDefinition elements for which rowspan can be different from 1.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:NoDataByDefinition[d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1']/@rangeMaximum]" mode="enoddi:get-rowspan" priority="1">
        <xsl:value-of select="string(1
            +number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1']/@rangeMaximum)
            -number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1']/@rangeMinimum))"/>
    </xsl:template>
    
    
    <xd:doc>
        <xd:desc>
            <xd:p>For a given element, return a set of the Instruction ids which are dependent of the said.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-computation-items" as="xs:string *">
        <xsl:variable name="modified-variables" as="node()">
            <xsl:call-template name="enoddi:modified-variables">
                <xsl:with-param name="position" select="1"/>
                <xsl:with-param name="list-of-variables">
                    <Variables>
                        <Variable><xsl:value-of select="enoddi:get-id(.)"/></Variable>
                    </Variables>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>
        
        <xsl:for-each select="//d:ComputationItem[r:CommandCode/r:Command/r:Binding/r:SourceParameterReference/r:ID = $modified-variables//Variable]">
            <xsl:value-of select="enoddi:get-id(current()/d:InterviewerInstructionReference/d:Instruction)"/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Recursive template that returns the list of variables depending on the first one (the other ones are all calculated variables).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="enoddi:modified-variables">
        <xsl:param name="position" as="xs:integer"/>
        <xsl:param name="list-of-variables"/>
        
        <xsl:choose>
            <xsl:when test="count($list-of-variables//Variable)&lt;$position">
                <xsl:copy-of select="$list-of-variables"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="variable-id" select="$list-of-variables//Variable[position()=$position]"/>
                <xsl:variable name="calculated-variables" as="xs:string *"
                    select="//d:GenerationInstruction[r:CommandCode/r:Command/r:Binding/r:SourceParameterReference/r:ID = $variable-id]"/>
                <xsl:variable name="calculated-variables">
                    <Variables>
                        <!-- variables that are not already in the list and calculated with the $position variable -->
                        <xsl:for-each select="//d:GenerationInstruction/r:CommandCode/r:Command
                                                 [not($list-of-variables//Variable = r:OutParameter/r:ID)
                                                  and r:Binding/r:SourceParameterReference/r:ID = $variable-id]">
                            <Variable>
                                <xsl:value-of select="r:OutParameter/r:ID"/>
                            </Variable>
                        </xsl:for-each>                        
                    </Variables>
                </xsl:variable>
                <xsl:call-template name="enoddi:modified-variables">
                    <xsl:with-param name="position" select="$position +1"/>
                    <xsl:with-param name="list-of-variables">
                        <Variables>
                            <xsl:copy-of select="$list-of-variables//Variable"/>
                            <xsl:copy-of select="$calculated-variables//Variable"/>
                        </Variables>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>For a given element, return a set of the Sequence ids which are dependent of the said element regarding their hideable property.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-hideable-then" as="xs:string *">
        <xsl:variable name="modified-variables" as="node()">
            <xsl:call-template name="enoddi:modified-variables">
                <xsl:with-param name="position" select="1"/>
                <xsl:with-param name="list-of-variables">
                    <Variables>
                        <Variable><xsl:value-of select="enoddi:get-id(.)"/></Variable>
                    </Variables>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>
        
        <xsl:for-each
            select="//d:IfThenElse[d:ThenConstructReference/d:Sequence/d:TypeOfSequence[text()='hideable'] and (d:IfCondition/r:Command/r:Binding/r:SourceParameterReference/r:ID = $modified-variables//Variable)]">
            <xsl:choose>
                <xsl:when test="descendant::d:Sequence/d:TypeOfSequence[text()='module']">
                    <xsl:for-each select="descendant::d:Sequence[d:TypeOfSequence='module']">
                        <xsl:value-of select="enoddi:get-id(current())"/>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="enoddi:get-id(current()/d:ThenConstructReference/d:Sequence)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:for-each select="ancestor::d:StructuredMixedResponseDomain/d:ResponseDomainInMixed[d:AttachmentLocation/d:DomainSpecificValue/@attachmentDomain=current()/parent::d:ResponseDomainInMixed/@attachmentBase]">
            <xsl:value-of select="enoddi:get-id(current())"/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>For a given element, return a set of the Sequence ids which are dependent of the said element regarding their deactivatable property.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-deactivatable-then" as="xs:string *">
        <xsl:variable name="modified-variables" as="node()">
            <xsl:call-template name="enoddi:modified-variables">
                <xsl:with-param name="position" select="1"/>
                <xsl:with-param name="list-of-variables">
                    <Variables>
                        <Variable><xsl:value-of select="enoddi:get-id(.)"/></Variable>
                    </Variables>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>

        <xsl:for-each
            select="//d:IfThenElse[d:ThenConstructReference/d:Sequence/d:TypeOfSequence[text()='deactivatable'] and d:IfCondition/r:Command/r:CommandContent/text() = $modified-variables//Variable]">
            <xsl:value-of select="enoddi:get-id(current()/d:ThenConstructReference/d:Sequence)"/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Get the formula to know when a module is hidden or not.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence/text()='module']"
        mode="enoddi:get-hideable-command">
        <xsl:variable name="filters">
            <xsl:for-each select="ancestor::d:Sequence[d:TypeOfSequence/text()='hideable']">
                <xsl:text> and </xsl:text>
                <xsl:apply-templates select="current()" mode="enoddi:get-hideable-command"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="result">
            <xsl:choose>
                <xsl:when test="contains($filters,'and ')">
                    <xsl:value-of select="substring($filters,6)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$filters"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$result"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Get the formula to know when a response is hidden or not.</xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:template match="d:ResponseDomainInMixed[d:AttachmentLocation]" mode="enoddi:get-hideable-command">
        
        <xsl:variable name="attachment-domain" select="d:AttachmentLocation/d:DomainSpecificValue/@attachmentDomain"/>
        <xsl:variable name="source-response-out-parameter" select="../d:ResponseDomainInMixed[@attachmentBase=$attachment-domain]//r:OutParameter/r:ID"/>
        
        <!-- relative-path code comes from cleaning.xsl -->
        <xsl:variable name="source-response-id">
            <xsl:variable name="relative-path">
                <xsl:value-of>//</xsl:value-of>
                <xsl:for-each select="ancestor::d:Loop | ancestor::d:QuestionGrid[d:GridDimension/d:Roster]">
                    <xsl:variable name="id">
                        <xsl:choose>
                            <xsl:when test="name()='d:Loop'">
                                <xsl:value-of select="concat(r:ID,'-Loop')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(r:ID,'-RowLoop')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:value-of
                        select="concat('*[name()=''',$id,
                        ''' and count(preceding-sibling::*)=count(current()/ancestor::*[name()=''',
                        $id,''']/preceding-sibling::*)]//')"
                    />
                </xsl:for-each>
            </xsl:variable>
            <xsl:value-of select="concat($relative-path,../../r:Binding[r:SourceParameterReference/r:ID=$source-response-out-parameter]/r:TargetParameterReference/r:ID)"/>
        </xsl:variable>

        <xsl:for-each select="d:AttachmentLocation/d:DomainSpecificValue/r:Value">
            <xsl:if test="position()!=1">
                <xsl:text> or </xsl:text>
            </xsl:if>
            <xsl:value-of select="concat($source-response-id,'=''',.,'''')"/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>For the Instruction of a ComputationItem, returns the conditions of all its deactivatable ancestors.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:ComputationItem" mode="enoddi:get-deactivatable-ancestors" as="xs:string *">
        <xsl:for-each select="ancestor::d:Sequence[d:TypeOfSequence/text()='deactivatable']">
            <xsl:value-of select="enoddi:get-deactivatable-command(.)"/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>get-instruction restricted to a format list (if not #all).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-instructions-by-format">
        <xsl:param name="format" select="'#all'" tunnel="yes"/>
        <xsl:sequence select="d:InterviewerInstructionReference/d:Instruction[if($format = '#all') then(true()) else(contains(concat(',',$format,','),concat(',',d:InstructionName/r:String,',')))]"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining specific getter is-first for multiple questions.</xd:p>
            <xd:p>Testing if the parent Question is-first, then testing if it's the first ResponseDomainInMixed</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:ResponseDomainInMixed//*" mode="enoddi:is-first" priority="2">
        <xsl:variable name="isFirstQuestion" select="enoddi:is-first(ancestor::*[local-name() = ('QuestionItem','QuestionGrid','QuestionBlock')])"/>
        <xsl:value-of select="if($isFirstQuestion) then(count(ancestor::d:ResponseDomainInMixed/preceding-sibling::d:ResponseDomainInMixed) = 0) else(false())"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining specific getter is-first for questions in questionBlock.</xd:p>
            <xd:p>Testing if the parent QuestionBlock is-first, then testing if it's the first in the QuestionBlock</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionBlock//*[local-name()=('QuestionItem','QuestionGrid')]" mode="enoddi:is-first" priority="2">
        <xsl:variable name="isFirstQuestionBlock" select="enoddi:is-first(ancestor::d:QuestionBlock)"/>
        <xsl:value-of select="if($isFirstQuestionBlock) then(count(ancestor::*[local-name()=('QuestionItemReference','QuestionGridReference')]/preceding-sibling::*[local-name()=('QuestionItemReference','QuestionGridReference')]) = 0) else(false())"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-instruction-index for Instruction.</xd:p>
            <xd:p>Retrieving the index of an instruction based of previous Instructions matching the $formats param (several formats whith ',' separator accepted).</xd:p>
            <xd:p>For consistency purpose the getter won't return anything if the self::Instruction format doesn't match the $formats param.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Instruction" mode="enoddi:get-instruction-index" priority="2">
        <xsl:param name="formats" select="'#none'" tunnel="yes"/>
        <!-- Formatting the param value for use in Xpath expression. -->
        <xsl:variable name="formatsForXpath" select="tokenize($formats,',')"/>       
        <xsl:value-of select="if($formats = '#none' or not(d:InstructionName = $formatsForXpath)) then() else(count(preceding::d:Instruction[d:InstructionName/r:String=$formatsForXpath])+1)"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-code-maximum-length for several questions.</xd:p>
            <xd:p>Return the maximum length of a code domain, if is blank then it is a boolean.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionItem[descendant::d:CodeDomain] |d:QuestionGrid[descendant::d:CodeDomain[not(ancestor::d:GridDimension)]] | d:CodeDomain[not(ancestor::d:GridDimension)]" 
        mode="enoddi:get-code-maximum-length" priority="2">
        <xsl:variable name="listLengthCode" as="xs:double*">
            <xsl:for-each select="descendant::r:Value[not(ancestor::d:GridDimension)]">
                <xsl:value-of select="string-length(.)"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="max($listLengthCode)"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-label for MultipleChoiceQuestion.</xd:p>
            <xd:p>In MCQ, the label of ResponseDomain is retrieve through the corresponding l:Code in the Dimension (only rank="1" in MCQ).</xd:p>            
        </xd:desc>
    </xd:doc>
    <!-- TODO : Simplify the Xpath match ? Only "MCQ" needed ? -->
    <xsl:template match="l:Code[parent::r:CodeReference/ancestor::d:NominalDomain[ancestor::d:QuestionGrid[not(d:GridDimension/@rank='2') 
        and not(d:StructuredMixedGridResponseDomain/d:GridResponseDomain[not(d:NominalDomain) and not(d:AttachmentLocation)])] 
        and parent::d:GridResponseDomain and following-sibling::d:GridAttachment//d:SelectDimension]]" mode="enoddi:get-label" priority="2">        
        <xsl:variable name="codeCoordinates" select="ancestor::d:NominalDomain/following-sibling::d:GridAttachment//d:SelectDimension"/>
        <xsl:variable name="correspondingCode" select="ancestor::d:QuestionGrid/d:GridDimension[@rank=$codeCoordinates/@rank]//l:Code[position()=$codeCoordinates/@rangeMinimum]"/>
        <xsl:apply-templates select="$correspondingCode" mode="enoddi:get-label"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-label-conditioning-variables.</xd:p>
            <xd:p>Function that returns the list of the variables conditioning a label.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-label-conditioning-variables">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:variable name="variable-list" as="xs:string *">
            <xsl:call-template name="enoddi:variables-from-label">
                <xsl:with-param name="label" select="enoddi:get-label(.,$language)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:sequence select="$variable-list"/>
    </xsl:template>

    <xsl:template name="enoddi:variables-from-label">
        <xsl:param name="label"/>
        
        <xsl:if test="contains(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)">
            <xsl:sequence select="substring-before(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)"/>
            <xsl:call-template name="enoddi:variables-from-label">
                <xsl:with-param name="label" select="substring-after(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)"/>
            </xsl:call-template>
        </xsl:if> 
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-conditioning-variable-formula for StatementItem, Instruction, QuestionItem and QuestionGrid.</xd:p>
            <xd:p>Function that returns the formula of a conditioning variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:StatementItem | d:Instruction | d:QuestionItem | d:QuestionGrid" mode="enoddi:get-conditioning-variable-formula" priority="2">
        <xsl:param name="variable" tunnel="yes"/>
        <!-- The markup containing the text has different names, but it is always a child of the element -->
        <xsl:variable name="conditional-text" select="descendant::d:ConditionalText"/>
        <xsl:variable name="possible-formulas" as="xs:string *">
            <xsl:for-each select="$conditional-text//d:Expression/r:Command">
                <xsl:if test="r:OutParameter/r:ID = $variable">
                    <xsl:sequence select="r:CommandContent"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="$possible-formulas != ''">
                <xsl:value-of select="$possible-formulas[1]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$variable"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-conditioning-variable-formula for StatementItem, Instruction, QuestionItem and QuestionGrid.</xd:p>
            <xd:p>Function that returns the variables of the formula of a conditioning variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:StatementItem | d:Instruction | d:QuestionItem | d:QuestionGrid" mode="enoddi:get-conditioning-variable-formula-variables" priority="2">
        <xsl:param name="variable" tunnel="yes"/>
        <!-- The markup containing the text has different names, but it is always a child of the element -->
        <xsl:variable name="conditional-text" select="descendant::d:ConditionalText"/>
        <xsl:variable name="formula-variables" as="node()">
            <Variables>
                <xsl:for-each select="$conditional-text//d:Expression/r:Command">
                    <xsl:if test="r:OutParameter/r:ID = $variable">
                        <Variable>
                            <xsl:sequence select="r:Binding/r:SourceParameterReference/r:ID"/>    
                        </Variable>
                    </xsl:if>
                </xsl:for-each>                
            </Variables>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="$formula-variables/*">
                <xsl:variable name="ordered-variables">
                    <xsl:for-each select="$formula-variables//Variable">
                        <xsl:sort select="string-length(.)" order="descending"/>
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:sequence select="distinct-values($ordered-variables)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$variable"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-conditioning-variable-formula for the others</xd:p>
            <xd:p>Function that returns the formula of a conditioning variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-conditioning-variable-formula">
        <xsl:param name="variable" tunnel="yes"/>
        
        <xsl:value-of select="$variable"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-conditioning-variable-formula for the others.</xd:p>
            <xd:p>Function that returns the variables of the formula of a conditioning variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-conditioning-variable-formula-variables">
        <xsl:param name="variable" tunnel="yes"/>
        
        <xsl:value-of select="$variable"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-business-name.</xd:p>
            <xd:p>Function that returns the business variable from the DDI one.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-business-name">
        <xsl:param name="variable" tunnel="yes"/>
        <xsl:choose>
            <!-- collected variable -->
            <xsl:when test="//l:VariableScheme//l:Variable/r:SourceParameterReference/r:ID = $variable">
                <xsl:value-of select="//l:VariableScheme//l:Variable[r:SourceParameterReference/r:ID = $variable]/l:VariableName/r:String"/>
            </xsl:when>
            <!-- calculated variable -->
            <xsl:when test="//l:VariableScheme//l:Variable//r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable">
                <xsl:value-of select="//l:VariableScheme//l:Variable[descendant::r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable]/l:VariableName/r:String"/>
            </xsl:when>
            <!-- external variable -->
            <xsl:when test="//l:VariableScheme//l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference)]/l:VariableName/r:String= $variable">
                <xsl:value-of select="$variable"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-business-ascendants.</xd:p>
            <xd:p>Function that returns the business ascendants loop and rowloop business names from a DDI variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-business-ascendants">
        <xsl:param name="variable" tunnel="yes"/>
        <xsl:choose>
            <!-- collected or calculated variable -->
            <xsl:when test="(//l:VariableScheme//l:Variable/r:SourceParameterReference/r:ID = $variable)
                or (//l:VariableScheme//l:Variable//r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable)">
                <xsl:for-each select="//l:VariableScheme//l:VariableGroup[descendant::r:SourceParameterReference/r:ID = $variable]">
                    <xsl:sequence select="l:VariableGroupName/r:String"/>
                </xsl:for-each>
                <!--<xsl:value-of select="//l:VariableScheme//l:Variable[r:SourceParameterReference/r:ID = $variable]/l:VariableName/r:String"/>-->
            </xsl:when>
            <!-- external variable -->
            <xsl:when test="//l:VariableScheme//l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference)]/l:VariableName/r:String= $variable">
                <xsl:for-each select="//l:VariableScheme//l:VariableGroup[descendant::l:VariableName/r:String= $variable]">
                    <xsl:sequence select="l:VariableGroupName/r:String"/>
                </xsl:for-each>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
           
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-instruction-by-anchor-ref.</xd:p>
            <xd:p>Retrieving an instruction based on the value of @href attribute.</xd:p>
            <xd:p>If the href param value contains a '#' as first character it will be ignored for the match criteria.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-instruction-by-anchor-ref" priority="2">
        <xsl:param name="href" select="''" tunnel="yes"/>
        <!-- Checking if '#' first character should be ommitted. -->
        <xsl:variable name="href-formatted" select="if(starts-with($href,'#')) then(substring-after($href,'#')) else($href)"/>
        <xsl:sequence select="//d:Instruction[.//xhtml:p/@id = $href-formatted]"/>            
    </xsl:template>
    <xd:doc>
        <xd:desc>
            <xd:p>Function that retruns the instruction related to an href value. The current implementation retrieve the instruction anywhere in the input, so href should be unique inside the input context. If the href param starts with the '#' charac, it will be omitted in the matching criteria.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoddi:get-instruction-by-anchor-ref">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="href"/>
        <xsl:apply-templates select="$context" mode="enoddi:get-instruction-by-anchor-ref">
            <xsl:with-param name="href" select="$href" tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:function>

</xsl:stylesheet>