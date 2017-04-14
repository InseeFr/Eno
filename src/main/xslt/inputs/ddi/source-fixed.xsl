<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" version="2.0">

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
            <xd:p>Concatenation of the instruction labels in order to create a question
                label.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="d:QuestionGrid[not(descendant::d:Instruction[d:InstructionName/r:String/text()='format'])
        and descendant::d:Instruction[not(d:InstructionName/r:String/text()='format')]]
        | d:QuestionItem[not(descendant::d:Instruction[d:InstructionName/r:String/text()='format'])
        and descendant::d:Instruction[not(d:InstructionName/r:String/text()='format')]]"
        mode="enoddi:get-label">
        <xsl:element name="xhtml:p">
            <xsl:element name="xhtml:span">
                <xsl:attribute name="class">
                    <xsl:value-of select="'block'"/>
                </xsl:attribute>
                <xsl:if test="d:QuestionText/d:LiteralText/d:Text/xhtml:p/@id">
                    <xsl:attribute name="id" select="d:QuestionText/d:LiteralText/d:Text/xhtml:p/@id"/>
                </xsl:if>
                <xsl:apply-templates select="d:QuestionText/d:LiteralText/d:Text/node()[not(name()='xhtml:p')] | d:QuestionText/d:LiteralText/d:Text/xhtml:p/node()"
                    mode="lang-choice"/>
                <xsl:for-each select="d:InterviewerInstructionReference/d:Instruction[d:InstructionName/r:String/text()='tooltip']
                                                                                      /d:InstructionText/d:LiteralText/d:Text">
                    <xsl:element name="xhtml:span">
                        <xsl:attribute name="title">
                            <xsl:variable name="title">
                                <xsl:apply-templates select="node()[not(name()='xhtml:p')] | xhtml:p/node()"
                                    mode="lang-choice"/>                                
                            </xsl:variable>
                            <xsl:value-of select="normalize-space($title)"/>
                        </xsl:attribute>
                        <xsl:text>&#160;</xsl:text>
                        <xsl:element name="img">
                            <xsl:attribute name="src" select="'/img/Help-browser.svg.png'"/>
                        </xsl:element>
                        <xsl:text>&#160;</xsl:text>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
            <xsl:for-each select="d:InterviewerInstructionReference/d:Instruction[not(d:InstructionName/r:String[text()='tooltip'])]/d:InstructionText/d:LiteralText/d:Text">
                <xsl:element name="xhtml:span">
                    <xsl:attribute name="class">
                        <xsl:value-of select="'block'"/>
                    </xsl:attribute>
                    <xsl:if test="xhtml:p/@id">
                        <xsl:attribute name="id" select="xhtml:p/@id"/>
                    </xsl:if>
                    <xsl:apply-templates select="node()[not(name()='xhtml:p')] | xhtml:p/node()"
                        mode="lang-choice"/>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Inserting the Tooltip label into the Sequence label.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:InterviewerInstructionReference/d:Instruction/d:InstructionName/r:String[text()='tooltip']]" mode="enoddi:get-label" priority="2">
        <xsl:apply-templates select="r:Label" mode="lang-choice"/>
        <xsl:for-each select="d:InterviewerInstructionReference/d:Instruction[d:InstructionName/r:String='tooltip']">
            <xsl:element name="xhtml:span">
                <xsl:attribute name="title">
                    <xsl:variable name="title">
                        <xsl:apply-templates select="d:InstructionText/d:LiteralText/d:Text" mode="lang-choice"/>    
                    </xsl:variable>
                    <xsl:value-of select="normalize-space($title)"/>
                </xsl:attribute>
                <xsl:text>&#160;</xsl:text>
                <xsl:element name="img">
                    <xsl:attribute name="src" select="'/img/Help-browser.svg.png'"/>
                </xsl:element>
                <xsl:text>&#160;</xsl:text>
            </xsl:element>            
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>For questions which have both a 'format' Instruction and an other Instruction which is not of 'format' type.</xd:p>
            <xd:p>The instruction is not used for the label. It will be used in the enoddi:get-format-instruction instead.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="d:QuestionGrid[descendant::d:Instruction[d:InstructionName/r:String/text()='format']
        and descendant::d:Instruction[not(d:InstructionName/r:String/text()='format')]]
        | d:QuestionItem[descendant::d:Instruction[d:InstructionName/r:String/text()='format']
        and descendant::d:Instruction[not(d:InstructionName/r:String/text()='format')]]"
        mode="enoddi:get-label">
        <!-- We get the text of the QuestionText -->
        <xsl:apply-templates select="d:QuestionText/d:LiteralText/d:Text" mode="lang-choice"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>For questions which have a 'format' Instruction.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="d:QuestionGrid[descendant::d:Instruction[d:InstructionName/r:String/text()='format']] | d:QuestionItem[descendant::d:Instruction[d:InstructionName/r:String/text()='format']]"
        mode="enoddi:get-format-instruction">
        <!-- We get the label of the 'format' Instruction -->
        <xsl:apply-templates
            select="descendant::d:Instruction[d:InstructionName/r:String/text()='format']"
            mode="enoddi:get-label"/>
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
            <xd:p>Getting the suffix for a QuestionItem for different response domains.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionItem[*[ends-with(name(),'DomainReference')]]" mode="enoddi:get-suffix">
        <xsl:apply-templates select="*[ends-with(name(),'DomainReference')]" mode="enoddi:get-suffix"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Getting the suffix for different response domains.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[ends-with(name(),'DomainReference')]"
        mode="enoddi:get-suffix">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:choose>
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
            <xsl:for-each-group
                select="d:StructuredMixedGridResponseDomain/d:GridResponseDomain//d:SelectDimension[@rank='1']"
                group-by="@rangeMinimum">
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
            <xsl:sequence select="d:GridDimension[@rank='1']//l:CodeList/r:Label"/>
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
        <!--        <xsl:sequence
            select="d:StructuredMixedGridResponseDomain/(d:GridResponseDomain | d:NoDataByDefinition)[.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1' and (@rangeMinimum=string($index) or @specificValue=string($index))]]"
        />-->
        <xsl:for-each
            select="d:StructuredMixedGridResponseDomain/(d:GridResponseDomain | d:NoDataByDefinition)[.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1' and (@rangeMinimum=string($index) or @specificValue=string($index))]]">
            <xsl:sort
                select="number(.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum)"/>
            <xsl:sequence select="."/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting a table line depending on an index number within a d:QuestionGrid elements not having a @maximumAllowed attribute in their d:Roster.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]"
        mode="enoddi:get-table-line">
        <xsl:param name="index" tunnel="yes"/>
        <xsl:for-each
            select="d:StructuredMixedGridResponseDomain/(d:GridResponseDomain | d:NoDataByDefinition)">
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
            select="max(ancestor::d:GridDimension[@rank='1']//l:Code[not(l:Code)]/count(ancestor::l:CodeList | ancestor::l:Code))-number($parents)-number($children)+1"
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
            <xd:p>Getting colspan for d:NoDataByDefinition elements.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:NoDataByDefinition" mode="enoddi:get-colspan" priority="1">
        <xsl:value-of
            select="string(1+number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMaximum)-number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum))"
        />
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>For a given element, return a set of the Instruction ids which are dependent of the said.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-computation-items" as="xs:string *">
        <xsl:variable name="id">
            <xsl:value-of select="enoddi:get-id(.)"/>
        </xsl:variable>
        <xsl:for-each
            select="//d:ComputationItem[contains(r:CommandCode/r:Command/r:CommandContent/text(), $id)]">
            <!-- The computation Item contains a chain corresponding to the variable. But it might not correspond exactly to this variable. -->
            <!-- Therefore, we modify the condition by deleting the potential false positive (same value followed by a dash or a number) -->
            <xsl:variable name="condition">
                <xsl:value-of
                    select="replace(r:CommandCode/r:Command/r:CommandContent/text(),concat($id,'(\-|[0-9])'),'')"
                />
            </xsl:variable>
            <!-- If the modified condition still contains the value, then it's ok -->
            <xsl:if test="contains($condition,$id)">
                <xsl:value-of
                    select="enoddi:get-id(current()/d:InterviewerInstructionReference/d:Instruction)"
                />
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>For a given element, return a set of the Sequence ids which are dependent of the said element regarding their hideable property.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-hideable-then" as="xs:string *">
        <xsl:variable name="id">
            <xsl:value-of select="enoddi:get-id(.)"/>
        </xsl:variable>
        <xsl:for-each
            select="//d:IfThenElse[d:ThenConstructReference/d:Sequence/d:TypeOfSequence[text()='hideable'] and contains(d:IfCondition/r:Command/r:CommandContent/text(),$id)]">
            <xsl:value-of select="enoddi:get-id(current()/d:ThenConstructReference/d:Sequence)"/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>For a given element, return a set of the Sequence ids which are dependent of the said element regarding their deactivatable property.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-deactivatable-then" as="xs:string *">
        <xsl:variable name="id">
            <xsl:value-of select="enoddi:get-id(.)"/>
        </xsl:variable>
        <xsl:for-each
            select="//d:IfThenElse[d:ThenConstructReference/d:Sequence/d:TypeOfSequence[text()='deactivatable'] and contains(d:IfCondition/r:Command/r:CommandContent/text(),$id)]">
            <xsl:value-of select="enoddi:get-id(current()/d:ThenConstructReference/d:Sequence)"/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Get the concatenate formula of all ComputationItem controls for a given module.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence/text()='module']" mode="enoddi:get-control">
        <xsl:variable name="controls">
            <xsl:for-each select=".//d:Instruction[ancestor::d:ComputationItem]">
                <xsl:text> and </xsl:text>
                <xsl:apply-templates select="current()" mode="enoddi:get-control"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="result">
            <xsl:choose>
                <xsl:when test="contains($controls,'and ')">
                    <xsl:value-of select="substring($controls,6)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$controls"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$result"/>
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
            <xd:p>Get the formula to calculate a Variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Variable" mode="enoddi:get-variable-calculation">
        <xsl:variable name="id">
            <xsl:apply-templates select="." mode="enoddi:get-id"/>
        </xsl:variable>
        <xsl:value-of
            select="substring-after(//d:Expression/r:Command/r:CommandContent[contains(text(),$id)]/text(),'=')"
        />
    </xsl:template>

</xsl:stylesheet>
