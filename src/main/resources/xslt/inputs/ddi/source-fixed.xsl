<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi" xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" version="2.0">

    <!-- Importing the different resources -->
<!--    <xsl:import href="../../lib.xsl"/>-->

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>A library of getter functions for fods with their implementations for different elements.</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:variable name="root" select="root(.)"/>

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
            <xd:p>Getter function of a table line depending on an index number.</xd:p>
            <xd:p>Too many parameters to be created in functions.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoddi:get-table-line">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="index"/>
        <xsl:param name="table-first-line"/>

        <xsl:apply-templates select="$context" mode="enoddi:get-table-line">
            <xsl:with-param name="index" select="$index" tunnel="yes"/>
            <xsl:with-param name="table-first-line" select="$table-first-line" tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Getter function of the row span of a grid element.</xd:p>
            <xd:p>Too many parameters to be created in functions.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoddi:get-rowspan" as="xs:string">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="table-first-line"/>
        <xsl:param name="table-last-line"/>
        <xsl:apply-templates select="$context" mode="enoddi:get-rowspan">
            <xsl:with-param name="table-first-line" select="$table-first-line" tunnel="yes"/>
            <xsl:with-param name="table-last-line" select="$table-last-line" tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:function>


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
    <xsl:template match="node()[parent::xhtml:p]" priority="1" mode="lang-choice">
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
            <xd:p>For a text() sibling of a xhtml:p (and generally empty), nothing is returned</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="text()[parent::*/xhtml:p]" mode="lang-choice" priority="1"/>

    <xd:doc>
        <xd:desc>
            <xd:p>For those nodes, the language is used to return the right text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node()[r:Content or r:String or xhtml:p]" mode="lang-choice">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="r:Content[@xml:lang=$language and xhtml:p] or r:String[@xml:lang=$language and xhtml:p]">
                <xsl:sequence select="child::node()[@xml:lang=$language]/xhtml:p"/>
            </xsl:when>
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
        <xsl:variable name="variable-measurement-unit" select="//l:VariableScheme//l:Variable[r:SourceParameterReference/r:ID = $ddi-variable]/l:VariableRepresentation//r:MeasurementUnit"/>
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
                select="d:StructuredMixedGridResponseDomain/*[name()='d:GridResponseDomainInMixed' or name()='d:NoDataByDefinition']//d:SelectDimension[@rank='1']"
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
        <xsl:variable name="label-or-no" select="count(d:GridDimension[@rank='2']/d:CodeDomain/r:CodeListReference/l:CodeList/r:Label)"/>

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

        <xsl:sequence select="d:GridDimension[@rank='2']//(l:Code[count(ancestor::l:CodeList[r:Label])+count(ancestor::l:Code)=$index+number($label-or-no)-1]
                                                         | l:CodeList/r:Label[count(ancestor::l:CodeList[r:Label])+count(ancestor::l:Code)=$index+number($label-or-no)])"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting a table line depending on an index number within a d:QuestionGrid.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionGrid" mode="enoddi:get-table-line">
        <xsl:param name="index" tunnel="yes"/>
        <xsl:param name="table-first-line" tunnel="yes" required="no"/>
        <xsl:variable name="codes">
            <xsl:apply-templates select="." mode="enoddi:get-codes-first-dimension"/>
        </xsl:variable>
        <xsl:variable name="id">
            <xsl:value-of select="$codes/l:Code[position()=$index]/r:ID"/>
        </xsl:variable>

        <xsl:apply-templates select="d:GridDimension[@rank='1']//l:Code[r:ID=$id]" mode="enoddi:get-table-line"/>
        <xsl:choose>
            <xsl:when test="string($table-first-line) = string($index)">
                <xsl:for-each select="d:StructuredMixedGridResponseDomain/(d:GridResponseDomainInMixed | d:NoDataByDefinition)
                    [.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1' and (@rangeMinimum=string($index) or @specificValue=string($index))]]
                    | d:StructuredMixedGridResponseDomain/(d:GridResponseDomainInMixed | d:NoDataByDefinition)
                    [.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1' and number(@rangeMinimum) &lt; $index and number(@rangeMaximum &gt;= $index)]]
                    ">
                    <xsl:sort select="number(.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum)"/>
                    <xsl:sequence select="."/>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:for-each select="d:StructuredMixedGridResponseDomain/(d:GridResponseDomainInMixed
                    | d:NoDataByDefinition)[.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1' and (@rangeMinimum=string($index) or @specificValue=string($index))]]">
                    <xsl:sort select="number(.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum)"/>
                    <xsl:sequence select="."/>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting a table line for an l:Code.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Code" mode="enoddi:get-table-line">
        <xsl:param name="index" tunnel="yes"/>
        <xsl:param name="table-first-line" tunnel="yes" required="no"/>
        <xsl:if test="parent::l:Code">
            <xsl:variable name="first-parent-code-id">
                <xsl:value-of select="parent::l:Code/l:Code[1]/r:ID"/>
            </xsl:variable>
            <xsl:if test="string($index) = string($table-first-line) or r:ID=$first-parent-code-id">
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
    <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='1'] and not(descendant::l:Code)]" mode="enoddi:get-colspan" priority="1">
        <!-- Getting the depth-level of parents codes -->
        <xsl:variable name="parents" select="if (string(count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) != 'NaN') then count(ancestor::l:CodeList[r:Label] | ancestor::l:Code) else 0"/>
        <!-- Getting the depth-level of children codes -->
        <xsl:variable name="children"
            select="if (string(max(.//l:Code[not(l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) - count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) !='')
                    then max(.//l:Code[not(l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) - count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)
                    else 0"/>
        <xsl:value-of select="max(ancestor::d:GridDimension[@rank='1']//l:Code[not(l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))
                            -number($parents) -number($children)+1"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>For the column headers (that are also part of the line.
            Those are the r:Label that are directly on top of the referenced codes list.
            We need to get the depth level from the 2nd dimension.
            So, we select all the l:Code that have no child (and therefore have the highest depth-level),
            for each one we get this depth-level
            and we keep the maximum, which will be the depth of the 2nd dimension.
        </xd:desc>
    </xd:doc>
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
    <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='1'] and l:Code]" mode="enoddi:get-rowspan" priority="1">
        <xsl:param name="table-first-line" tunnel="yes" required="no"/>
        <xsl:param name="table-last-line" tunnel="yes" required="no"/>

        <xsl:variable name="first-descendant-position" select="descendant::l:Code[not(l:Code)][1]/count(preceding::l:Code[ancestor::d:GridDimension=current()/ancestor::d:GridDimension and not(l:Code)])+1"/>
        <xsl:variable name="last-descendant-position" select="descendant::l:Code[not(l:Code)][last()]/count(preceding::l:Code[ancestor::d:GridDimension=current()/ancestor::d:GridDimension and not(l:Code)])+1"/>
        <xsl:variable name="first-line">
            <xsl:choose>
                <xsl:when test="string($table-first-line) != '' and number($table-first-line) &gt; $first-descendant-position">
                    <xsl:value-of select="$table-first-line"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$first-descendant-position"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="last-line">
            <xsl:choose>
                <xsl:when test="string($table-last-line) != '' and number($table-last-line) &lt; $last-descendant-position">
                    <xsl:value-of select="$table-last-line"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$last-descendant-position"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="1 + $last-line - $first-line"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting colspan for the line labels (2nd dimension). It depends on the depth level.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="r:Label[ancestor::d:GridDimension[@rank='2']]" mode="enoddi:get-colspan" priority="1">
        <xsl:value-of select="count(parent::l:CodeList//l:Code[not(descendant::l:Code)])"/>
    </xsl:template>
    <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='2'] and descendant::l:Code]" mode="enoddi:get-colspan" priority="1">
        <xsl:value-of select="count(descendant::l:Code[not(descendant::l:Code)])"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Getting rowspan for the line labels (2nd dimension). It depends on the depth level.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='2'] and not(descendant::l:Code)]" mode="enoddi:get-rowspan" priority="1">
        <xsl:value-of select="max(ancestor::d:GridDimension[@rank='2']//l:Code[not(l:Code)]/(count(ancestor::l:CodeList[r:Label])+count(ancestor::l:Code)+1))
                             -count(ancestor::l:CodeList[r:Label])
                             -count(ancestor::l:Code)"/>
    </xsl:template>
    <!--<xsl:template match="r:Label[ancestor::d:GridDimension[@rank='2']]" mode="enoddi:get-rowspan" priority="1">
        <xsl:value-of select="max(ancestor::d:GridDimension[@rank='2']//l:Code[not(l:Code)]/(count(ancestor::l:CodeList[r:Label])+count(ancestor::l:Code)+1))
            -count(parent::l:CodeList/ancestor::l:CodeList[r:Label])
            -count(ancestor::l:Code)"/>
    </xsl:template>-->
    <xsl:template match="d:GridDimension[@rank='1' and ../d:GridDimension/@rank='2' and d:CodeDomain and not(descendant::l:CodeList/r:Label)]" mode="enoddi:get-rowspan" priority="1">
        <xsl:value-of select="max(../d:GridDimension[@rank='2']//l:Code[not(l:Code)]/(count(ancestor::l:CodeList[r:Label])+count(ancestor::l:Code)))+1
            -(if (../d:GridDimension[@rank='2']//l:CodeList[1]/r:Label) then 1 else 0)"/>
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
        <xsl:param name="table-first-line" tunnel="yes" required="no"/>
        <xsl:param name="table-last-line" tunnel="yes" required="no"/>


        <xsl:variable name="first-line">
            <xsl:choose>
                <xsl:when test="$table-first-line != '' and number($table-first-line) &gt; number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1']/@rangeMinimum)">
                    <xsl:value-of select="$table-first-line"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1']/@rangeMinimum"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="last-line">
            <xsl:choose>
                <xsl:when test="$table-last-line != '' and number($table-last-line) &lt; number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1']/@rangeMaximum)">
                    <xsl:value-of select="$table-last-line"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1']/@rangeMaximum"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="string(1 + number($last-line) - number($first-line))"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>getting maximum from date / duration</xd:desc>
    </xd:doc>
    <xsl:template match="d:DateTimeDomain|d:DateTimeDomainReference" mode="enoddi:get-maximum">
        <xsl:variable name="standart-maximum" select="descendant::r:MaximumValue[not(ancestor::r:OutParameter)]"/>

        <xsl:choose>
            <xsl:when test="$standart-maximum = ''"/>
            <xsl:when test="$standart-maximum = 'format-date(current-date(),''[Y0001]-[M01]-[D01]'')'">
                <xsl:value-of select="'format-date(current-date(),''[Y0001]-[M01]-[D01]'')'"/>
            </xsl:when>
            <xsl:when test="$standart-maximum = 'format-date(current-date(),''[Y0001]-[M01]'')'">
                <xsl:value-of select="'format-date(current-date(),''[Y0001]-[M01]'')'"/>
            </xsl:when>
            <xsl:when test="$standart-maximum = 'year-from-date(current-date())'">
                <xsl:value-of select="'year-from-date(current-date())'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$standart-maximum"/>
            </xsl:otherwise>
        </xsl:choose>
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
            <xsl:value-of select="enoddi:get-id(.)"/>
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
      
    <xsl:template match="*" mode="enoddi:get-generation-instruction">
        <xsl:param name="variable" tunnel="yes"/>
        <xsl:variable
            name="is-calculated-variable"
            as="xs:boolean"
            select="$root//l:Variable[r:ID=$variable or l:VariableName/r:String=$variable]/l:VariableRepresentation/r:ProcessingInstructionReference/r:TypeOfObject='GenerationInstruction'"/>
        <xsl:if test="$is-calculated-variable">
            <xsl:variable name="idGenerationInstruction" select="$root//l:Variable[r:ID=$variable or l:VariableName/r:String=$variable]/l:VariableRepresentation/r:ProcessingInstructionReference/r:ID"/>
            <xsl:copy-of select="$root//d:GenerationInstruction[r:ID=$idGenerationInstruction]"/>
        </xsl:if>
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

        <xsl:for-each select="//d:IfThenElse[d:TypeOfIfThenElse='hideable' and (d:IfCondition/r:Command/r:Binding/r:SourceParameterReference/r:ID = $modified-variables//Variable)]">
            <xsl:choose>
                <xsl:when test="descendant::d:Sequence/d:TypeOfSequence[text()='module']">
                    <xsl:for-each select="descendant::d:Sequence[d:TypeOfSequence='module']">
                        <xsl:value-of select="enoddi:get-id(current())"/>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="enoddi:get-id(current()/d:ThenConstructReference)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:for-each select="ancestor::d:StructuredMixedResponseDomain/d:ResponseDomainInMixed[d:AttachmentLocation/d:DomainSpecificValue/@attachmentDomain=current()/parent::d:ResponseDomainInMixed/@attachmentBase]">
            <xsl:value-of select="enoddi:get-id(current())"/>
        </xsl:for-each>
        <xsl:for-each select="ancestor::d:StructuredMixedGridResponseDomain/d:GridResponseDomainInMixed[d:ResponseAttachmentLocation/d:DomainSpecificValue/@attachmentDomain=current()/parent::d:GridResponseDomainInMixed/@attachmentBase]">
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

        <xsl:for-each select="//d:IfThenElse[d:TypeOfIfThenElse='greyedout' and (d:IfCondition/r:Command/r:Binding/r:SourceParameterReference/r:ID = $modified-variables//Variable)]
                                            /*[name()='d:ThenConstructReference' or name()='d:ElseConstructReference']">
            <xsl:value-of select="enoddi:get-id(current())"/>
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
            <xsl:for-each select="ancestor::*[(name()='d:ThenConstructReference' or name()='d:ElseConstructReference') and parent::d:IfThenElse/d:TypeOfIfThenElse='hideable']">
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
            <xd:p>Get the formula to know when a response is hidden or not in a QuestionItem.</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template match="d:ResponseDomainInMixed[d:AttachmentLocation]" mode="enoddi:get-hideable-command">

        <xsl:variable name="attachment-domain" select="d:AttachmentLocation/d:DomainSpecificValue/@attachmentDomain"/>
        <xsl:variable name="source-response-out-parameter" select="../d:ResponseDomainInMixed[@attachmentBase=$attachment-domain]//r:OutParameter/r:ID"/>
        <xsl:variable name="source-response-id" select="../../r:Binding[r:SourceParameterReference/r:ID=$source-response-out-parameter]/r:TargetParameterReference/r:ID"/>

        <xsl:for-each select="d:AttachmentLocation/d:DomainSpecificValue/r:Value">
            <xsl:if test="position()!=1">
                <xsl:text> or </xsl:text>
            </xsl:if>
            <xsl:value-of select="concat($conditioning-variable-begin,$source-response-id,$conditioning-variable-end,'=''',.,'''')"/>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Get the formula to know when a response is hidden or not in a QuestionGrid.</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template match="d:GridResponseDomainInMixed[d:ResponseAttachmentLocation]" mode="enoddi:get-hideable-command">

        <xsl:variable name="attachment-domain" select="d:ResponseAttachmentLocation/d:DomainSpecificValue/@attachmentDomain"/>
        <xsl:variable name="source-response-out-parameter" select="../d:GridResponseDomainInMixed[@attachmentBase=$attachment-domain]//r:OutParameter/r:ID"/>
        <xsl:variable name="source-response-id" select="../../r:Binding[r:SourceParameterReference/r:ID=$source-response-out-parameter]/r:TargetParameterReference/r:ID"/>

        <xsl:variable name="result">
            <xsl:for-each select="d:ResponseAttachmentLocation/d:DomainSpecificValue/r:Value">
                <xsl:if test="position()!=1">
                    <xsl:text> or </xsl:text>
                </xsl:if>
                <xsl:value-of select="concat($conditioning-variable-begin,$source-response-id,$conditioning-variable-end,'=''',.,'''')"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="$result"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Returns the conditions of all its deactivatable ancestors.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-deactivatable-ancestors" as="xs:string *">
        <xsl:for-each select="ancestor::*[not(d:TypeOfSequence[text()='module'])]">
            <xsl:if test="enoddi:get-deactivatable-command(.) != ''">
                <xsl:value-of select="enoddi:get-deactivatable-command(.)"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <xd:doc>
        <xd:desc>
            <xd:p>Returns the variables of the conditions of all its deactivatable ancestors.</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template match="*" mode="enoddi:get-deactivatable-ancestors-variables" as="xs:string *">
        <xsl:for-each select="ancestor::*[not(d:TypeOfSequence[text()='module'])]">
            <xsl:if test="enoddi:get-deactivatable-command(.) != ''">
                <xsl:sequence select="enoddi:get-deactivatable-command-variables(.)"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Returns the conditions of all its hideable ancestors.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-hideable-ancestors" as="xs:string *">
        <xsl:for-each select="ancestor::*[not(d:TypeOfSequence[text()='module'])]">
            <xsl:if test="enoddi:get-hideable-command(.) != ''">
                <xsl:value-of select="enoddi:get-hideable-command(.)"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="*" mode="enoddi:get-hideable-ancestors-variables" as="xs:string *">
        <xsl:for-each select="ancestor::*[not(d:TypeOfSequence[text()='module'])]">
            <xsl:if test="enoddi:get-hideable-command(.) != ''">
                <xsl:sequence select="enoddi:get-hideable-command-variables(.)"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>get-instruction restricted to a format list (if not #all).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-instructions-by-format">
        <xsl:param name="format" select="'#all'" tunnel="yes" required="no"/>
        <xsl:sequence select="d:InterviewerInstructionReference/d:Instruction[if($format = '#all') then(true())
            else(contains(concat(',',$format,','),concat(',',d:InstructionName/r:String,',')))]"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>get-instruction for tooltips and codes.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Code" mode="enoddi:get-instructions-by-format">
        <xsl:param name="format" select="'#all'" tunnel="yes" required="no"/>
        <xsl:sequence select="r:CategoryReference/l:Category/d:InterviewerInstructionReference/d:Instruction[if($format = '#all') then(true())
            else(contains(concat(',',$format,','),concat(',',d:InstructionName/r:String,',')))]"/>
        <xsl:choose>
            <!-- MCQ -->
            <xsl:when test="parent::r:CodeReference/ancestor::d:NominalDomain[ancestor::d:QuestionGrid[not(d:GridDimension/@rank='2')
                and not(d:StructuredMixedGridResponseDomain/d:GridResponseDomainInMixed[not(d:NominalDomain) and not(d:ResponseAttachmentLocation)])]
                and parent::d:GridResponseDomainInMixed and following-sibling::d:GridAttachment//d:SelectDimension]">
                <xsl:variable name="codeCoordinates" select="ancestor::d:NominalDomain/following-sibling::d:GridAttachment//d:SelectDimension"/>
                <xsl:sequence select="ancestor::d:QuestionGrid/d:GridDimension[@rank=$codeCoordinates/@rank]//l:Code[position()=$codeCoordinates/@rangeMinimum]/
                    r:CategoryReference/l:Category/d:InterviewerInstructionReference/d:Instruction[if($format = '#all') then(true())
                    else(contains(concat(',',$format,','),concat(',',d:InstructionName/r:String,',')))]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="r:CategoryReference/l:Category/d:InterviewerInstructionReference/d:Instruction[if($format = '#all') then(true())
                    else(contains(concat(',',$format,','),concat(',',d:InstructionName/r:String,',')))]"/>
            </xsl:otherwise>
        </xsl:choose>

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
        and not(d:StructuredMixedGridResponseDomain/d:GridResponseDomainInMixed[not(d:NominalDomain) and not(d:ResponseAttachmentLocation)])]
        and parent::d:GridResponseDomainInMixed and following-sibling::d:GridAttachment//d:SelectDimension]]" mode="enoddi:get-label" priority="2">
        <xsl:variable name="codeCoordinates" select="ancestor::d:NominalDomain/following-sibling::d:GridAttachment//d:SelectDimension" as="node()"/>
        <xsl:apply-templates
            select="ancestor::d:QuestionGrid/d:GridDimension[@rank='1']//l:Code[position()=number($codeCoordinates/@rangeMinimum)]"
            mode="enoddi:get-label"/>
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
                <xsl:with-param name="label" select="eno:serialize(enoddi:get-label(.,$language))"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:if test="$variable-list != enoddi:get-label(.,$language)">
            <xsl:sequence select="$variable-list"/>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-cell-value-variables.</xd:p>
            <xd:p>Function that returns the list of the variables of the value of a fixed cell.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:NoDataByDefinition" mode="enoddi:get-cell-value-variables">
        <xsl:param name="language" tunnel="yes"/>
        <xsl:variable name="variable-list" as="xs:string *">
            <xsl:call-template name="enoddi:variables-from-label">
                <xsl:with-param name="label" select="eno:serialize(enoddi:get-cell-value(.))"/>
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
            <xd:p>Defining getter get-conditioning-variable-formula for StatementItem, Instruction, QuestionItem, QuestionGrid and ComputationItem.</xd:p>
            <xd:p>Function that returns the formula of a conditioning variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:StatementItem | d:Instruction | d:QuestionItem | d:QuestionGrid | d:ComputationItem
        | *[(ends-with(name(),'Domain') or ends-with(name(),'DomainReference')) and ancestor::d:QuestionItem]"
        mode="enoddi:get-conditioning-variable-formula" priority="2">
        <xsl:param name="variable" tunnel="yes"/>
        <!-- The markup containing the text has different names, but it is always a child of the element -->
        <xsl:variable name="conditional-text">
            <xsl:choose>
                <xsl:when test="ends-with(name(),'Domain') or ends-with(name(),'DomainReference')">
                    <xsl:copy-of select="ancestor::d:QuestionItem/descendant::d:ConditionalText"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="descendant::d:ConditionalText"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="possible-formulas" as="xs:string *">
            <xsl:for-each select="$conditional-text//d:Expression/r:Command">
                <xsl:if test="r:OutParameter/r:ID = $variable">
                    <xsl:sequence select="r:CommandContent"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="$possible-formulas[1]"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-conditioning-variable-formula-variables for StatementItem, Instruction, QuestionItem, QuestionGrid and ComputationItem.</xd:p>
            <xd:p>Function that returns the variables of the formula of a conditioning variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:StatementItem | d:Instruction | d:QuestionItem | d:QuestionGrid | d:ComputationItem
        | *[(ends-with(name(),'Domain') or ends-with(name(),'DomainReference')) and ancestor::d:QuestionItem]"
        mode="enoddi:get-conditioning-variable-formula-variables" priority="2">
        <xsl:param name="variable" tunnel="yes"/>
        <!-- The markup containing the text has different names, but it is always a child of the element -->
        <xsl:variable name="conditional-text">
            <xsl:choose>
                <xsl:when test="ends-with(name(),'Domain') or ends-with(name(),'DomainReference')">
                    <xsl:copy-of select="ancestor::d:QuestionItem/descendant::d:ConditionalText"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="descendant::d:ConditionalText"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="formula-variables" as="node()">
            <Variables>
                <xsl:for-each select="$conditional-text//d:Expression/r:Command">
                    <xsl:if test="r:OutParameter/r:ID = $variable">
                        <Variable>
                            <xsl:value-of select="r:Binding/r:SourceParameterReference/r:ID"/>
                        </Variable>
                    </xsl:if>
                </xsl:for-each>
            </Variables>
        </xsl:variable>
        <xsl:variable name="ordered-variables">
            <xsl:for-each select="$formula-variables//Variable">
                <xsl:sort select="string-length(.)" order="descending"/>
                <xsl:value-of select="."/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:sequence select="distinct-values($ordered-variables)"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getterget-item-label-conditioning-variables.</xd:p>
            <xd:p>Function that returns the variables of the labels of the items of CodeDomain and NominalDomain.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:CodeDomain | d:NominalDomain" mode="enoddi:get-item-label-conditioning-variables">

        <xsl:variable name="item-label-conditioning-variables-with-doubles" as="xs:string*">
            <xsl:for-each select="descendant::l:Category/r:Label/r:Content">
                <xsl:call-template name="enoddi:variables-from-label">
                    <xsl:with-param name="label" select="eno:serialize(.)"/>
                </xsl:call-template>
            </xsl:for-each>
        </xsl:variable>
        <xsl:sequence select="distinct-values($item-label-conditioning-variables-with-doubles)"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getterget-item-label-conditioning-variables.</xd:p>
            <xd:p>Function that returns the variables of the labels of the items of MCQ.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:NominalDomain[ancestor::d:QuestionGrid[not(d:GridDimension/@rank='2')
        and not(d:StructuredMixedGridResponseDomain/d:GridResponseDomainInMixed[not(d:NominalDomain) and not(d:ResponseAttachmentLocation)])]
        and parent::d:GridResponseDomainInMixed and following-sibling::d:GridAttachment//d:SelectDimension]"
        mode="enoddi:get-item-label-conditioning-variables" priority="2">

        <xsl:variable name="codeCoordinates" select="following-sibling::d:GridAttachment//d:SelectDimension"/>
        <xsl:variable name="item-label-conditioning-variables-with-doubles">
            <xsl:call-template name="enoddi:variables-from-label">
                <xsl:with-param name="label"
                    select="eno:serialize(ancestor::d:QuestionGrid/d:GridDimension[@rank=$codeCoordinates/@rank]
                                                                  //l:Code[position()=$codeCoordinates/@rangeMinimum]
                                                                  //l:Category/r:Label/r:Content)"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:sequence select="distinct-values($item-label-conditioning-variables-with-doubles)"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-variable-type.</xd:p>
            <xd:p>Function that returns the type of a variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-variable-type">

        <xsl:call-template name="enoddi:get-variable-type">
            <xsl:with-param name="variable" select="enoddi:get-id(.)"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="enoddi:get-variable-type">
        <xsl:param name="variable"/>

        <xsl:choose>
            <!-- collected variable -->
            <xsl:when test="$root//l:VariableScheme//l:Variable/r:SourceParameterReference/r:ID = $variable">
                <xsl:value-of select="'collected'"/>
            </xsl:when>
            <!-- calculated variable -->
            <xsl:when test="$root//l:VariableScheme//l:Variable//r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable">
                <xsl:value-of select="'calculated'"/>
            </xsl:when>
            <!-- external variable -->
            <xsl:when test="$root//l:VariableScheme//l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference)]/l:VariableName/r:String= $variable">
                <xsl:value-of select="'external'"/>
            </xsl:when>
            <!-- unknown -->
            <xsl:otherwise>
                <xsl:value-of select="concat('unknow type for : ',$variable)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-container-name.</xd:p>
            <xd:p>Function that returns the business name of the container of a loop or a dynamic array.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-container-name">
        <xsl:variable name="loop-id" select="enoddi:get-id(.)"/>
        <xsl:variable name="loop-name" select="$root//l:VariableScheme//l:VariableGroup[r:BasedOnObject/r:BasedOnReference/r:ID= $loop-id]/l:VariableGroupName/r:String"/>
        <xsl:variable name="loop-position" select="$root//l:VariableScheme//l:VariableGroup/r:BasedOnObject/r:BasedOnReference[r:ID= $loop-id]/count(preceding-sibling::r:BasedOnReference)+1"/>

        <xsl:choose>
            <xsl:when test="$loop-position = 1">
                <xsl:value-of select="concat($loop-name,'-Container')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat($loop-name,'_',$loop-position,'-Container')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-linked-containers.</xd:p>
            <xd:p>Function that returns the list of the business name of the different containers of an occurrence of the current loop or dynamic array.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-linked-containers">
        <xsl:variable name="loop-id" select="enoddi:get-id(.)"/>
        <xsl:variable name="loop-name" select="$root//l:VariableScheme//l:VariableGroup[r:BasedOnObject/r:BasedOnReference/r:ID= $loop-id]/l:VariableGroupName/r:String"/>
        <xsl:for-each select="$root//l:VariableScheme//l:VariableGroup/r:BasedOnObject[r:BasedOnReference/r:ID= $loop-id]/r:BasedOnReference">
            <xsl:variable name="loop-position" select="position()"/>
            <xsl:choose>
                <xsl:when test="$loop-position = 1">
                    <xsl:value-of select="concat($loop-name,'-Container')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($loop-name,'_',$loop-position,'-Container')"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-business-name.</xd:p>
            <xd:p>Function that returns the business variable from the DDI one.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-business-name">

        <xsl:call-template name="enoddi:get-business-name">
            <xsl:with-param name="variable" select="enoddi:get-id(.)"/>
        </xsl:call-template>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-variable-business-name.</xd:p>
            <xd:p>Function that returns the business name of a variable from its DDI ID.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-variable-business-name">
        <xsl:param name="variable" tunnel="yes"/>
        
        <xsl:call-template name="enoddi:get-business-name">
            <xsl:with-param name="variable" select="$variable"/>
        </xsl:call-template>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Name template get-business-name that returns the business name of a variable from its DDI ID.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template name="enoddi:get-business-name">
        <xsl:param name="variable"/>

        <xsl:choose>
            <!-- collected variable -->
            <xsl:when test="$root//l:VariableScheme//l:Variable/r:SourceParameterReference/r:ID = $variable">
                <xsl:value-of select="$root//l:VariableScheme//l:Variable[r:SourceParameterReference/r:ID = $variable]/l:VariableName/r:String"/>
            </xsl:when>
            <!-- calculated variable -->
            <xsl:when test="$root//l:VariableScheme//l:Variable//r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable">
                <xsl:value-of select="$root//l:VariableScheme//l:Variable[descendant::r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable]/l:VariableName/r:String"/>
            </xsl:when>
            <!-- external variable -->
            <xsl:when test="$root//l:VariableScheme//l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference)]/l:VariableName/r:String= $variable">
                <xsl:value-of select="$variable"/>
            </xsl:when>
            <xsl:when test="$root//l:VariableScheme//l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference)]/r:ID= $variable">
                <xsl:value-of select="$root//l:VariableScheme//l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference) and descendant::r:ID= $variable]/l:VariableName/r:String"/>
            </xsl:when>
            <!-- Loop -->
            <xsl:when test="$root//l:VariableScheme//l:VariableGroup/r:BasedOnObject/r:BasedOnReference/r:ID = $variable">
                <xsl:value-of select="$root//l:VariableScheme//l:VariableGroup[r:BasedOnObject/r:BasedOnReference/r:ID= $variable]/l:VariableGroupName/r:String"/>
            </xsl:when>
            <!-- Grid with roster, but without VariableGroup -->
            <xsl:otherwise>
                <xsl:value-of select="$variable"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-business-ancestors.</xd:p>
            <xd:p>Function that returns the business ascendants loop and rowloop business names from a DDI variable.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-business-ancestors">

        <xsl:call-template name="enoddi:get-business-ancestors">
            <xsl:with-param name="variable" select="enoddi:get-id(.)"/>
        </xsl:call-template>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Defining getter get-variable-business-ancestors.</xd:p>
            <xd:p>Function that returns the business ascendants loop and rowloop business names from a DDI variable ID.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-variable-business-ancestors">
        <xsl:param name="variable" tunnel="yes"/>
        <xsl:call-template name="enoddi:get-business-ancestors">
            <xsl:with-param name="variable" select="$variable"/>
        </xsl:call-template>
    </xsl:template>
    
    
    <xsl:template name="enoddi:get-business-ancestors">
        <xsl:param name="variable"/>

        <xsl:choose>
            <!-- collected variable -->
            <xsl:when test="$root//l:VariableScheme//l:Variable/r:SourceParameterReference/r:ID = $variable">
                <xsl:for-each select="$root//l:VariableScheme//l:VariableGroup[descendant::r:SourceParameterReference/r:ID = $variable and not(l:TypeOfVariableGroup='Questionnaire')]">
                    <xsl:sequence select="l:VariableGroupName/r:String"/>
                </xsl:for-each>
            </xsl:when>
            <!-- calculated variable -->
            <xsl:when test="$root//l:VariableScheme//l:Variable//r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable">
                <xsl:for-each select="$root//l:VariableScheme//l:VariableGroup[descendant::r:SourceParameterReference/r:ID = $variable and not(l:TypeOfVariableGroup='Questionnaire')]">
                    <xsl:sequence select="l:VariableGroupName/r:String"/>
                </xsl:for-each>
            </xsl:when>
            <!-- external variable -->
            <xsl:when test="$root//l:VariableScheme//l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference)]/l:VariableName/r:String= $variable">
                <xsl:for-each select="$root//l:VariableScheme//l:VariableGroup[descendant::l:VariableName/r:String= $variable and not(l:TypeOfVariableGroup='Questionnaire')]">
                    <xsl:sequence select="l:VariableGroupName/r:String"/>
                </xsl:for-each>
            </xsl:when>
            <!-- Loop -->
            <xsl:when test="$root//l:VariableScheme//l:VariableGroup/r:BasedOnObject/r:BasedOnReference/r:ID = $variable"/>
            <!-- Loop - position -->
            <xsl:when test="ends-with($variable,'-position') and $root//l:VariableScheme//l:VariableGroup/r:BasedOnObject/r:BasedOnReference/r:ID = substring-before($variable,'-position')"/>
            <!-- calculated variable not in VariableScheme -->
            <xsl:when test="$root//d:GenerationInstruction/r:CommandCode/r:Command/r:OutParameter/r:ID = $variable">
                <xsl:for-each select="$root//*[name()='d:Loop' or (name()='d:QuestionGrid' and d:GridDimension/d:Roster)]
                    [descendant::d:GenerationInstruction/r:CommandCode/r:Command/r:OutParameter/r:ID = $variable]">
                    <xsl:call-template name="enoddi:get-business-name">
                        <xsl:with-param name="variable" select="enoddi:get-id(.)"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:when>
            <!-- unknown -->
            <xsl:otherwise>
                <xsl:value-of select="concat($variable,'_is_not_a_variable_looking_for_its_ancestors')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc></xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="enoddi:get-variable-representation">
        <xsl:param name="variable" tunnel="yes"/>
        
        <xsl:variable name="variable-representation">
            <xsl:choose>
                <!-- collected variable -->
                <xsl:when test="$root//l:VariableScheme//l:Variable/r:SourceParameterReference/r:ID = $variable">
                    <xsl:if test="$root//l:VariableScheme//l:Variable[r:SourceParameterReference/r:ID = $variable]/l:VariableRepresentation/*">                        
                        <xsl:value-of select="enoddi:get-type($root//l:VariableScheme//l:Variable[r:SourceParameterReference/r:ID = $variable]/l:VariableRepresentation/*)"/>
                    </xsl:if>
                </xsl:when>
                <!-- calculated variable -->
                <xsl:when test="$root//l:VariableScheme//l:Variable//r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable">
                    <xsl:if test="$root//l:VariableScheme//l:Variable[descendant::r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable]/l:VariableRepresentation/*[not(self::r:ProcessingInstructionReference)]">
                        <xsl:value-of select="enoddi:get-type($root//l:VariableScheme//l:Variable[descendant::r:ProcessingInstructionReference/r:Binding/r:SourceParameterReference/r:ID = $variable]/l:VariableRepresentation/*[not(self::r:ProcessingInstructionReference)])"/>
                    </xsl:if>                    
                </xsl:when>
                <!-- external variable -->
                <xsl:when test="$root//l:VariableScheme//l:Variable[not(r:QuestionReference or r:SourceParameterReference or descendant::r:ProcessingInstructionReference)]/l:VariableName/r:String= $variable">
                    <!-- VariableRepresentation may be empty for external variables -->
                    <xsl:if test="$root//l:VariableScheme//l:Variable[l:VariableName/r:String= $variable]/l:VariableRepresentation/*">
                        <xsl:value-of select="enoddi:get-type($root//l:VariableScheme//l:Variable[l:VariableName/r:String= $variable]/l:VariableRepresentation/*)"/>    
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise/>
            </xsl:choose>    
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$variable-representation != ''">
                <xsl:value-of select="$variable-representation"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'UNKNOWN'"/>
            </xsl:otherwise>
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

    <xd:doc>
        <xd:desc>
            <xd:p>Get flowControl linked to a question</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionItem | d:QuestionGrid"
        mode="enoddi:get-next-filter-description">
        <xsl:sequence select="d:ExternalAid[r:OtherMaterial/r:Description/r:Content/xhtml:div/@class='FlowControl']"/>
    </xsl:template>
    
    <xsl:template match="d:ExternalAid" mode="enoddi:get-flowcontrol-target">
        <xsl:variable name="idTarget" select="r:OtherMaterial/r:Description/r:Content/xhtml:div[@class='FlowControl']/xhtml:div[@class='IfTrue']/text()"/>
        <xsl:variable name="target" select="$root//*[r:ID=$idTarget]"/>
        <xsl:choose>
            <xsl:when test="$target/d:ConstructName!=''">
                <xsl:copy-of select="$target/d:ConstructName"/>
            </xsl:when>
            <xsl:when test="$target/d:QuestionItemName/r:String!=''">
                <xsl:copy-of select="$target/d:QuestionItemName/r:String"/>
            </xsl:when>
            <xsl:when test="$target/d:QuestionGridName/r:String!=''">
                <xsl:copy-of select="$target/d:QuestionGridName/r:String"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Get Filter linked to a question</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionItem | d:QuestionGrid"
        mode="enoddi:get-previous-filter-description">
        <xsl:sequence select="ancestor::d:QuestionConstruct/parent::d:ControlConstructReference[not(preceding-sibling::d:ControlConstructReference)]/ancestor::d:IfThenElse[not(d:ExternalAid)]/r:Description"/>
    </xsl:template>
    <xd:doc>
        <xd:desc>
            <xd:p>The label of a Code in a CodeDomain with @displayCode = 'true' is the concatenation of its r:Value and its descendant r:Label.</xd:p>
            <xd:p>The same for a Code in a GridDimension with @displayCode = 'true', but only if it has no descendant.</xd:p>
            <xd:p>When both are true, only one Code is concatened</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Code[(ancestor::d:CodeDomain/@displayCode='true') or (ancestor::d:GridDimension[@displayCode='true'] and not(descendant::l:Code))]"
        mode="enoddi:get-label">
        <xsl:variable name="xhtml-label" as="node()*">
            <xsl:apply-templates select="r:CategoryReference/l:Category/r:Label" mode="lang-choice"/>
        </xsl:variable>
        <xsl:variable name="titled-label" as="node()">
            <xsl:apply-templates select="$xhtml-label" mode="modif-title">
                <xsl:with-param name="prefix" select="concat(r:Value,' - ')" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:sequence select="$titled-label"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Only the first child of a xhtml:p must be titled</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:p" mode="modif-title" priority="2">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()[position()=1]" mode="modif-title"/>
            <xsl:apply-templates select="node()[not(position()=1)]"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>When we match a node starting by xhtml, we only process the first child node with modif-title mode.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[starts-with(name(),'xhtml')]" mode="modif-title">
        <xsl:param name="prefix" tunnel="yes"/>
        <xsl:value-of select="$prefix"/>
        <xsl:apply-templates select="."/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Adding the prefix.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:span[@class='block']" mode="modif-title" priority="2">
        <xsl:param name="prefix" tunnel="yes"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="$prefix"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Adding the prefix.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="text()" mode="modif-title" priority="1">
        <xsl:param name="prefix" tunnel="yes"/>
        <xsl:choose>
            <xsl:when test="preceding-sibling::xhtml:p or following-sibling::xhtml:p"/>
            <xsl:otherwise>
                <xsl:value-of select="concat($prefix,.)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>default template</xd:desc>
    </xd:doc>
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions before the label of the question</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enoddi:get-previous-statement-item">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi:get-previous-statement-item"/>
    </xsl:function>

    <xsl:template match="*" mode="enoddi:get-previous-statement-item">
        <xsl:sequence select="ancestor::d:QuestionConstruct/parent::d:ControlConstructReference/preceding-sibling::d:ControlConstructReference/d:StatementItem[parent::d:ControlConstructReference/following-sibling::d:ControlConstructReference[descendant::d:QuestionConstruct][1]/descendant::d:QuestionConstruct/r:ID=current()/ancestor::d:QuestionConstruct/r:ID]"></xsl:sequence>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving the id (modele) of a questionnaire.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence/text()='template']" mode="enoddi:get-form-model">
        <xsl:choose>
            <xsl:when test="//d:Instrument/d:InstrumentName">
                <xsl:value-of select="//d:Instrument/d:InstrumentName/r:String"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="//d:Instrument/r:ID/text()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>