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
            <xd:p>Pour tous les éléments, le comportement par défaut est de renvoyer du texte
                vide</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="#all" priority="-1">
        <xsl:text/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>On récupère la liste des langues utilisées dans le ddi.</xd:p>
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
            <xd:p>On récupère le nombre de modules dans le ddi.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="iatddi:get-nb-modules" as="xs:integer">
        <xsl:value-of select="count(//d:Sequence[d:TypeOfSequence/text()='Module'])"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>On récupère l'id pour un d:ResponseDomainInMixed.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:ResponseDomainInMixed" mode="iatddi:get-id">
        <xsl:variable name="parentId">
            <xsl:apply-templates
                select="parent::d:StructuredMixedResponseDomain/parent::d:QuestionItem"
                mode="iatddi:get-id"/>
        </xsl:variable>
        <xsl:variable name="sousId">
            <xsl:value-of select="count(preceding-sibling::d:ResponseDomainInMixed)+1"/>
        </xsl:variable>
        <xsl:value-of select="concat($parentId,'-',$sousId)"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>On concatène les libellés des instructions pour former un libellé de
                question.</xd:p>
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
                        mode="choixLangue"/>
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
        <xsl:apply-templates select="d:QuestionText/d:LiteralText/d:Text" mode="choixLangue"/>
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
            <xd:p>Les libellés qu'on renvoie selon que l'attribut xml:lang soit présent ou
                non</xd:p>
            <xd:p>Dépend de la langue</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[not(r:String) and not(r:Content) and not(xhtml:p)]" mode="choixLangue">
        <xsl:sequence select="child::node()"/>
    </xsl:template>
    <xsl:template match="*[parent::xhtml:p]" priority="1" mode="choixLangue">
        <xsl:sequence select="."/>
    </xsl:template>
    <xsl:template match="text()" mode="choixLangue">
        <xsl:value-of select="."/>
    </xsl:template>
    <xsl:template match="node()[r:Content or r:String or xhtml:p]" mode="choixLangue">
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
            <xd:p>On récupère le suffixe correspondant à certains domaines de réponse.</xd:p>
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
        <xsl:apply-templates select="d:GridDimension[@rank='1']" mode="iatddi:get-niveaux"/>
    </xsl:template>

    <xsl:template match="d:QuestionGrid" mode="iatddi:get-levels-second-dimension">
        <xsl:apply-templates select="d:GridDimension[@rank='2']" mode="iatddi:get-niveaux"/>
    </xsl:template>

    <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster]"
        mode="iatddi:get-codes-first-dimension">
        <xsl:variable name="niveaux">
            <xsl:for-each-group
                select="d:StructuredMixedGridResponseDomain/d:GridResponseDomain//d:SelectDimension[@rank='1']"
                group-by="@rangeMinimum">
                <dummy/>
            </xsl:for-each-group>
        </xsl:variable>
        <xsl:sequence select="$niveaux/*"/>
    </xsl:template>

    <xsl:template match="d:QuestionGrid[not(d:GridDimension/d:Roster)]"
        mode="iatddi:get-codes-first-dimension">
        <xsl:sequence select="d:GridDimension[@rank='1']//l:Code[not(descendant::l:Code)]"/>
    </xsl:template>

    <xsl:template match="d:GridDimension" mode="iatddi:get-niveaux">
        <xsl:variable name="niveaux">
            <xsl:for-each select="d:CodeDomain/r:CodeListReference/l:CodeList//l:CodeList[r:Label]">
                <dummy/>
            </xsl:for-each>
            <xsl:for-each-group select="d:CodeDomain/r:CodeListReference/l:CodeList//l:Code"
                group-by="@levelNumber">
                <dummy/>
            </xsl:for-each-group>
        </xsl:variable>
        <xsl:variable name="nbNiveaux">
            <xsl:value-of select="count($niveaux//dummy)"/>
        </xsl:variable>
        <xsl:sequence select="$niveaux/*"/>
    </xsl:template>

    <!-- On récupère une ligne de titre selon un numéro d'index -->
    <xsl:template match="d:QuestionGrid" mode="iatddi:get-title-line">
        <xsl:param name="index" tunnel="yes" as="xs:integer"/>
        <!-- On compte les labels qui sont en haut de la liste référencée (car s'il y en a, ils ne sont pas censés être pris en compte) -->
        <xsl:variable name="labelOuNon">
            <xsl:value-of
                select="count(d:GridDimension[@rank='2']/d:CodeDomain/r:CodeListReference/l:CodeList/r:Label)"
            />
        </xsl:variable>

        <!-- S'il s'agit de la première ligne -->
        <xsl:if test="$index=1">
            <xsl:sequence select="d:GridDimension[@rank='1']//l:CodeList/r:Label"/>
        </xsl:if>
        <!-- On récupère:
        1) les codes pour lesquels leur niveau d'imbrication dans listes de codes avec label est égal à l'index +1
        2) les labels de l:CodeList-->

        <xsl:sequence
            select="d:GridDimension[@rank='2']//(l:Code[count(ancestor::l:CodeList[r:Label])=$index+number($labelOuNon)-1] | l:CodeList/r:Label[count(ancestor::l:CodeList[r:Label])=$index+number($labelOuNon)])"
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
            <xsl:variable name="idPremierCodeParent">
                <xsl:value-of select="parent::l:Code/l:Code[1]/r:ID"/>
            </xsl:variable>
            <xsl:if test="r:ID=$idPremierCodeParent">
                <xsl:apply-templates select="parent::l:Code" mode="iatddi:get-table-line"/>
            </xsl:if>
        </xsl:if>
        <xsl:sequence select="."/>
    </xsl:template>

    <!-- Pour les codes appartenant à une dimension 1 de plusieurs niveaux -->
    <xsl:template
        match="l:Code[max(ancestor::d:GridDimension[@rank='1']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList | ancestor::l:Code))>1]"
        mode="iatddi:get-colspan" priority="1">
        <!-- On récupère  le niveau de profondeur des codes parents -->
        <xsl:variable name="parents">
            <xsl:value-of
                select="if (string(count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) != 'NaN') then count(ancestor::l:CodeList[r:Label] | ancestor::l:Code) else 0"
            />
        </xsl:variable>
        <!-- On récupère le niveau de profondeur des codes enfants -->
        <xsl:variable name="enfants">
            <xsl:value-of
                select="if (string(max(.//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))-count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) !='') then max(.//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))-count(ancestor::l:CodeList[r:Label] | ancestor::l:Code) else 0"
            />
        </xsl:variable>
        <xsl:value-of
            select="max(ancestor::d:GridDimension[@rank='1']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList | ancestor::l:Code))-number($parents)-number($enfants)+1"
        />
    </xsl:template>

    <!-- Pour les chapeaux de la colonne (qui font donc partie aussi de la ligne) -->
    <!-- Il s'agit des r:Label qui sont directement en haut de la liste de codes référencées -->
    <!-- Il faut ramener la profondeur de niveau de la seconde dimension -->
    <!-- Donc on sélectionne tous les l:Code qui n'ont pas d'enfant et qui sont donc le plus en profondeur,
    On compte pour chacun d'eux quel est ce niveau de profondeur
    Et on prend le max qui correspond la profondeur de la seconde dimension-->
    <xsl:template
        match="r:Label[parent::l:CodeList/parent::r:CodeListReference/parent::d:CodeDomain/parent::d:GridDimension[@rank='1']]"
        mode="iatddi:get-rowspan" priority="1">
        <xsl:variable name="labelOuNon">
            <xsl:value-of
                select="count(ancestor::d:GridDimension[@rank='1']/following-sibling::d:GridDimension[@rank='2']/d:CodeDomain/r:CodeListReference/l:CodeList/r:Label)"
            />
        </xsl:variable>
        <xsl:value-of
            select="max(ancestor::d:GridDimension[@rank='1']/following-sibling::d:GridDimension[@rank='2']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label]))+1-number($labelOuNon)"
        />
    </xsl:template>

    <!--Pour les colonnes, pour les l:Code qui ont un l:Code, c'est à dire une case éclatée en sous-cases, on ramène le nombre de l:Code enfant -->
    <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='1'] and child::l:Code]"
        mode="iatddi:get-rowspan" priority="1">
        <xsl:value-of select="count(descendant::l:Code[not(child::l:Code)])"/>
    </xsl:template>

    <!-- ATTENTION -->
    <!-- Pour l'instant, c'est égal au nombre de l:Code qu'on trouve + bas. Cele ne fonctionne que s'il y a deux niveaux -->
    <!-- A faire évoluer pour un tableau dont le chapeau du haut aurait 3 niveaux -->
    <xsl:template match="r:Label[ancestor::d:GridDimension[@rank='2']]" mode="iatddi:get-colspan"
        priority="1">
        <xsl:value-of select="count(parent::l:CodeList//l:Code)"/>
    </xsl:template>

    <!-- Pour les étiquettes de la ligne (seconde dimension), comme plus haut, on calcule le niveau de profondeur -->
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
            <!-- Le computationItem contient la chaîne correspondant à la variable mais peut-être que cela ne correspond pas exactement à la variable -->
            <!-- On modifie la condition en supprimant les éventuels faux positifs (la même valeur suivie directement d'un tiret ou d'un chiffre) -->
            <xsl:variable name="condition">
                <xsl:value-of select="replace(r:CommandCode/r:Command/r:CommandContent/text(),concat($id,'(\-|[0-9])'),'')"/>
            </xsl:variable>
            <!-- Si la condition modifiée contient toujours la valeur, c'est ok -->
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
        <xsl:variable name="controles">
            <xsl:for-each select=".//d:Instruction[ancestor::d:ComputationItem]">
                <xsl:text> and </xsl:text>
                <xsl:apply-templates select="current()" mode="iatddi:get-control"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:variable name="resultat">
            <xsl:choose>
                <xsl:when test="contains($controles,'and ')">
                    <xsl:value-of select="substring($controles,6)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$controles"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$resultat"/>
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
