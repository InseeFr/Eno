<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
    xmlns:iatddi="http://xml/insee.fr/xslt/apply-templates/ddi" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:il="http://xml/insee.fr/xslt/lib"
    exclude-result-prefixes="#all" version="2.0">

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>For each element, the default behaviour is to return empty text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="#all" priority="-1">
        <xsl:text/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Getting the languages list used in the ddi.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Modele']" mode="iatddi:get-languages"
        as="xs:string *">
        <xsl:for-each-group select="//@xml:lang" group-by=".">
            <xsl:value-of select="current-grouping-key()"/>
        </xsl:for-each-group>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Getting the number of modules in the ddi.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="iatddi:get-nb-of-modules" as="xs:integer">
        <xsl:value-of select="count(//d:Sequence[d:TypeOfSequence/text()='Module'])"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Getting the id for d:ResponseDomainInMixed.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:ResponseDomainInMixed" mode="iatddi:get-id">
        <xsl:variable name="parent-id">
            <xsl:apply-templates
                select="parent::d:StructuredMixedResponseDomain/parent::d:QuestionItem"
                mode="iatddi:get-id"/>
        </xsl:variable>
        <xsl:variable name="sub-id">
            <xsl:value-of select="count(preceding-sibling::d:ResponseDomainInMixed)+1"/>
        </xsl:variable>
        <xsl:value-of select="concat($parent-id,'-',$sub-id)"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Concatenation of the instruction labels in order to create a question label.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="d:QuestionGrid[not(descendant::d:Instruction[d:InstructionName/r:String/text()='Format'])
        and descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')]]
        | d:QuestionItem[not(descendant::d:Instruction[d:InstructionName/r:String/text()='Format'])
        and descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')]]"
        mode="iatddi:get-label">
        <xsl:element name="xhtml:p">
            <xsl:for-each
                select="d:QuestionText/d:LiteralText/d:Text | d:InterviewerInstructionReference/d:Instruction/d:InstructionText/d:LiteralText/d:Text">
                <xsl:element name="xhtml:span">
                    <xsl:attribute name="class">
                        <xsl:value-of select="string('block')"/>
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

    <xsl:template
        match="d:QuestionGrid[descendant::d:Instruction[d:InstructionName/r:String/text()='Format']
        and descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')]]
        | d:QuestionItem[descendant::d:Instruction[d:InstructionName/r:String/text()='Format']
        and descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')]]"
        mode="iatddi:get-label">
        <xsl:apply-templates select="d:QuestionText/d:LiteralText/d:Text" mode="lang-choice"/>
    </xsl:template>

    <xsl:template
        match="d:QuestionGrid[descendant::d:Instruction[d:InstructionName/r:String/text()='Format']] | d:QuestionItem[descendant::d:Instruction[d:InstructionName/r:String/text()='Format']]"
        mode="iatddi:get-hint-instruction" priority="2">
        <xsl:apply-templates
            select="descendant::d:Instruction[d:InstructionName/r:String/text()='Format']"
            mode="iatddi:get-label"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>The different labels that we return depending on the existence of the xml:lang attribute
                non</xd:p>
            <xd:p>Depends on the language</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[not(r:String) and not(r:Content) and not(xhtml:p)]" mode="lang-choice">
        <xsl:sequence select="child::node()"/>
    </xsl:template>
    <xsl:template match="*[parent::xhtml:p]" priority="1" mode="lang-choice">
        <xsl:sequence select="."/>
    </xsl:template>
    <xsl:template match="text()" mode="lang-choice">
        <xsl:value-of select="."/>
    </xsl:template>
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

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Getting the corresponding suffix from different reponse domains.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:DateTimeDomain[r:DateFieldFormat/text()='HH']" mode="iatddi:get-suffix">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="$language='fr'">
                <xsl:text>heures</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="d:DateTimeDomain[r:DateFieldFormat/text()='mm']" mode="iatddi:get-suffix">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="$language='fr'">
                <xsl:text>minutes</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template
        match="d:QuestionItem[d:NumericDomainReference]" mode="iatddi:get-suffix">
        <xsl:apply-templates select="d:NumericDomainReference" mode="iatddi:get-suffix"/>
    </xsl:template>
    
    <xsl:template match="d:NumericDomainReference[r:ManagedNumericRepresentation]" mode="iatddi:get-suffix">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="r:ManagedNumericRepresentation//r:Content/@xml:lang">
                <xsl:value-of select="r:ManagedNumericRepresentation//r:Content[@xml:lang=$language]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="r:ManagedNumericRepresentation//r:Content"/>        
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    

    <xsl:template match="d:QuestionGrid" mode="iatddi:get-levels-first-dimension">
        <xsl:apply-templates select="d:GridDimension[@rank='1']" mode="iatddi:get-levels"/>
    </xsl:template>

    <xsl:template match="d:QuestionGrid" mode="iatddi:get-levels-second-dimension">
        <xsl:apply-templates select="d:GridDimension[@rank='2']" mode="iatddi:get-levels"/>
    </xsl:template>

    <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster]"
        mode="iatddi:get-codes-first-dimension">
        <xsl:variable name="levels">
            <xsl:for-each-group
                select="d:StructuredMixedGridResponseDomain/d:GridResponseDomain//d:SelectDimension[@rank='1']"
                group-by="@rangeMinimum">
                <dummy/>
            </xsl:for-each-group>
        </xsl:variable>
        <xsl:sequence select="$levels/*"/>
    </xsl:template>

    <xsl:template match="d:QuestionGrid[not(d:GridDimension/d:Roster)]"
        mode="iatddi:get-codes-first-dimension">
        <xsl:sequence select="d:GridDimension[@rank='1']//l:Code[not(descendant::l:Code)]"/>
    </xsl:template>

    <xsl:template match="d:GridDimension" mode="iatddi:get-levels">
        <xsl:variable name="levels">
            <xsl:for-each select="d:CodeDomain/r:CodeListReference/l:CodeList//l:CodeList[r:Label]">
                <dummy/>
            </xsl:for-each>
            <xsl:for-each-group select="d:CodeDomain/r:CodeListReference/l:CodeList//l:Code"
                group-by="@levelNumber">
                <dummy/>
            </xsl:for-each-group>
        </xsl:variable>
        <xsl:variable name="nb-of-levels">
            <xsl:value-of select="count($levels//dummy)"/>
        </xsl:variable>
        <xsl:sequence select="$levels/*"/>
    </xsl:template>

    <!-- Getting the title line depending on an index number -->
    <xsl:template match="d:QuestionGrid" mode="iatddi:get-title-line">
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

    <xsl:template match="d:QuestionGrid" mode="iatddi:get-table-line">
        <xsl:param name="index" tunnel="yes"/>
        <xsl:variable name="codes">
            <xsl:apply-templates select="." mode="iatddi:get-codes-first-dimension"/>
        </xsl:variable>
        <xsl:variable name="id">
            <xsl:value-of select="$codes/l:Code[position()=$index]/r:ID"/>
        </xsl:variable>

        <xsl:apply-templates select="d:GridDimension[@rank='1']//l:Code[r:ID=$id]"
            mode="iatddi:get-table-line"/>
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

    <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]" mode="iatddi:get-table-line">
        <xsl:param name="index" tunnel="yes"/>
        
        <xsl:for-each
            select="d:StructuredMixedGridResponseDomain/(d:GridResponseDomain | d:NoDataByDefinition)">
            <xsl:sort
                select="number(.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum)"/>
            <xsl:sequence select="."/>
        </xsl:for-each>
        
    </xsl:template>
    

    <xsl:template match="l:Code" mode="iatddi:get-table-line">
        <xsl:if test="parent::l:Code">
            <xsl:variable name="first-parent-code-id">
                <xsl:value-of select="parent::l:Code/l:Code[1]/r:ID"/>
            </xsl:variable>
            <xsl:if test="r:ID=$first-parent-code-id">
                <xsl:apply-templates select="parent::l:Code" mode="iatddi:get-table-line"/>
            </xsl:if>
        </xsl:if>
        <xsl:sequence select="."/>
    </xsl:template>

    <!-- For codes belonging to a 1-dimension of several levels -->
    <xsl:template
        match="l:Code[max(ancestor::d:GridDimension[@rank='1']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList | ancestor::l:Code))>1]"
        mode="iatddi:get-colspan" priority="1">
        <!-- Getting the depth-level of parents codes -->
        <xsl:variable name="parents">
            <xsl:value-of
                select="if (string(count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) != 'NaN') then count(ancestor::l:CodeList[r:Label] | ancestor::l:Code) else 0"
            />
        </xsl:variable>
        <!-- Getting the depth-level of children codes -->
        <xsl:variable name="children">
            <xsl:value-of
                select="if (string(max(.//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))-count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) !='') then max(.//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))-count(ancestor::l:CodeList[r:Label] | ancestor::l:Code) else 0"
            />
        </xsl:variable>
        <xsl:value-of
            select="max(ancestor::d:GridDimension[@rank='1']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList | ancestor::l:Code))-number($parents)-number($children)+1"
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
        mode="iatddi:get-rowspan" priority="1">
        <xsl:variable name="label-or-no">
            <xsl:value-of
                select="count(ancestor::d:GridDimension[@rank='1']/following-sibling::d:GridDimension[@rank='2']/d:CodeDomain/r:CodeListReference/l:CodeList/r:Label)"
            />
        </xsl:variable>
        <xsl:value-of
            select="max(ancestor::d:GridDimension[@rank='1']/following-sibling::d:GridDimension[@rank='2']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label]))+1-number($label-or-no)"
        />
    </xsl:template>

    <!--Concerning the columns, when l:Code has a l:Code (representing a box dispatched in sub-boxes), we get the number of children l:Code -->
    <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='1'] and child::l:Code]"
        mode="iatddi:get-rowspan" priority="1">
        <xsl:value-of select="count(descendant::l:Code[not(child::l:Code)])"/>
    </xsl:template>

    <!-- WARNING -->
    <!-- At the moment, this is equal to the number of l:Code that we find lower. This will only work with 2 levels. -->
    <!-- Consider an evolution where the table header would have 3 levels -->
    <xsl:template match="r:Label[ancestor::d:GridDimension[@rank='2']]" mode="iatddi:get-colspan"
        priority="1">
        <xsl:value-of select="count(parent::l:CodeList//l:Code)"/>
    </xsl:template>

    <!-- For the line labels (2nd dimension), as we did previously, we calculate the depth level -->
    <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='2']]" mode="iatddi:get-rowspan"
        priority="1">
        <xsl:value-of
            select="max(ancestor::d:GridDimension[@rank='2']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label]))+1-count(ancestor::l:CodeList[r:Label])"
        />
    </xsl:template>

    <xsl:template match="d:NoDataByDefinition" mode="iatddi:get-colspan" priority="1">
        <xsl:value-of
            select="string(1+number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMaximum)-number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum))"
        />
    </xsl:template>

    <xsl:template match="*" mode="iatddi:get-computation-items" as="xs:string *">
        <xsl:variable name="id">
            <xsl:value-of select="iatddi:get-id(.)"/>
        </xsl:variable>
        <xsl:for-each
            select="//d:ComputationItem[contains(r:CommandCode/r:Command/r:CommandContent/text(), $id)]">
            <!-- The computation Item contains a chain corresponding to the variable. But it might not correspond exactly to this variable. -->
            <!-- Therefore, we modify the condition by deleting the potential false positive (same value followed by a dash or a number) -->
            <xsl:variable name="condition">
                <xsl:value-of select="replace(r:CommandCode/r:Command/r:CommandContent/text(),concat($id,'(\-|[0-9])'),'')"/>
            </xsl:variable>
            <!-- If the modified condition still contains the value, then it's ok -->
            <xsl:if test="contains($condition,$id)">
                <xsl:value-of
                    select="iatddi:get-id(current()/d:InterviewerInstructionReference/d:Instruction)"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="*" mode="iatddi:get-then" as="xs:string *">
        <xsl:variable name="id">
            <xsl:value-of select="iatddi:get-id(.)"/>
        </xsl:variable>
        <xsl:for-each
            select="//d:IfThenElse[d:ThenConstructReference/d:Sequence/d:TypeOfSequence[text()='Cachable'] and contains(d:IfCondition/r:Command/r:CommandContent/text(),$id)]">
            <xsl:value-of select="iatddi:get-id(current()/d:ThenConstructReference/d:Sequence)"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Module']" mode="iatddi:get-control">
        <xsl:variable name="controls">
            <xsl:for-each select=".//d:Instruction[ancestor::d:ComputationItem]">
                <xsl:text> and </xsl:text>
                <xsl:apply-templates select="current()" mode="iatddi:get-control"/>
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


    <xsl:template match="l:Variable" mode="iatddi:get-link">
        <xsl:variable name="id">
            <xsl:apply-templates select="." mode="iatddi:get-id"/>
        </xsl:variable>
        <xsl:value-of select="substring-after(//d:Expression/r:Command/r:CommandContent[contains(text(),$id)]/text(),'=')"
        />
    </xsl:template>
    
    <xsl:template match="d:DateTimeDomain[r:DateFieldFormat/text()='HH' and @regExp and (parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed)]"
        mode="iatddi:get-message" priority="2">
        <xsl:variable name="apos">'</xsl:variable>
        <xsl:value-of select="concat('Le nombre d',$apos,'heures doit être compris entre 0 et 99.')"/>
    </xsl:template>
    
    <xsl:template match="d:DateTimeDomain[r:DateFieldFormat/text()='mm' and @regExp and (parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed)]"
        mode="iatddi:get-message" priority="2">
        <xsl:value-of select="string('Le nombre de minutes doit être compris entre 0 et 59.')"/>
    </xsl:template>

</xsl:stylesheet>
