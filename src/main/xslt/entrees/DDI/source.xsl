<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
                xmlns:iatddi="http://xml/insee.fr/xslt/apply-templates/ddi"
                xmlns:d="ddi:datacollection:3_2"
                xmlns:r="ddi:reusable:3_2"
                xmlns:l="ddi:logicalproduct:3_2"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:il="http://xml/insee.fr/xslt/lib"
                exclude-result-prefixes="#all"
                version="2.0">
   <xd:doc>
      <xd:desc>
         <xd:p>Pour tous les éléments, le comportement par défaut est de renvoyer du texte
                vide</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="#all" priority="-1">
      <xsl:text/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>On récupère la liste des langues utilisées dans le ddi.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Modele']"
                 mode="iatddi:get-languages"
                 as="xs:string *">
      <xsl:for-each-group select="//@xml:lang" group-by=".">
         <xsl:value-of select="current-grouping-key()"/>
      </xsl:for-each-group>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>On récupère le nombre de modules dans le ddi.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*" mode="iatddi:get-nb-modules" as="xs:integer">
      <xsl:value-of select="count(//d:Sequence[d:TypeOfSequence/text()='Module'])"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>On récupère l'id pour un d:ResponseDomainInMixed.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:ResponseDomainInMixed" mode="iatddi:get-id">
      <xsl:variable name="parentId">
         <xsl:apply-templates select="parent::d:StructuredMixedResponseDomain/parent::d:QuestionItem"
                              mode="iatddi:get-id"/>
      </xsl:variable>
      <xsl:variable name="sousId">
         <xsl:value-of select="count(preceding-sibling::d:ResponseDomainInMixed)+1"/>
      </xsl:variable>
      <xsl:value-of select="concat($parentId,'-',$sousId)"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>On concatène les libellés des instructions pour former un libellé de
                question.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionGrid[not(descendant::d:Instruction[d:InstructionName/r:String/text()='Format'])         and descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')]]         | d:QuestionItem[not(descendant::d:Instruction[d:InstructionName/r:String/text()='Format'])         and descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')]]"
                 mode="iatddi:get-label">
      <xsl:element name="xhtml:p">
         <xsl:for-each select="d:QuestionText/d:LiteralText/d:Text | d:InterviewerInstructionReference/d:Instruction/d:InstructionText/d:LiteralText/d:Text">
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
   <xsl:template match="d:QuestionGrid[descendant::d:Instruction[d:InstructionName/r:String/text()='Format']         and descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')]]         | d:QuestionItem[descendant::d:Instruction[d:InstructionName/r:String/text()='Format']         and descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')]]"
                 mode="iatddi:get-label">
      <xsl:apply-templates select="d:QuestionText/d:LiteralText/d:Text" mode="choixLangue"/>
   </xsl:template>
   <xsl:template match="d:QuestionGrid[descendant::d:Instruction[d:InstructionName/r:String/text()='Format']] | d:QuestionItem[descendant::d:Instruction[d:InstructionName/r:String/text()='Format']]"
                 mode="iatddi:get-hint-instruction"
                 priority="2">
      <xsl:apply-templates select="descendant::d:Instruction[d:InstructionName/r:String/text()='Format']"
                           mode="iatddi:get-label"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Les libellés qu'on renvoie selon que l'attribut xml:lang soit présent ou
                non</xd:p>
         <xd:p>Dépend de la langue</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="*[not(r:String) and not(r:Content) and not(xhtml:p)]"
                 mode="choixLangue">
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
   <xd:doc>
      <xd:desc>
         <xd:p>On récupère le suffixe correspondant à certains domaines de réponse.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:DateTimeDomain[r:DateFieldFormat/text()='HH']"
                 mode="iatddi:get-suffix">
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
   <xsl:template match="d:DateTimeDomain[r:DateFieldFormat/text()='mm']"
                 mode="iatddi:get-suffix">
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
   <xsl:template match="d:QuestionItem[d:NumericDomainReference]"
                 mode="iatddi:get-suffix">
      <xsl:apply-templates select="d:NumericDomainReference" mode="iatddi:get-suffix"/>
   </xsl:template>
   <xsl:template match="d:NumericDomainReference[r:ManagedNumericRepresentation]"
                 mode="iatddi:get-suffix">
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
         <xsl:for-each-group select="d:StructuredMixedGridResponseDomain/d:GridResponseDomain//d:SelectDimension[@rank='1']"
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
         <xsl:value-of select="count(d:GridDimension[@rank='2']/d:CodeDomain/r:CodeListReference/l:CodeList/r:Label)"/>
      </xsl:variable>
      <!-- S'il s'agit de la première ligne -->
      <xsl:if test="$index=1">
         <xsl:sequence select="d:GridDimension[@rank='1']//l:CodeList/r:Label"/>
      </xsl:if>
      <!-- On récupère:
        1) les codes pour lesquels leur niveau d'imbrication dans listes de codes avec label est égal à l'index +1
        2) les labels de l:CodeList-->
      <xsl:sequence select="d:GridDimension[@rank='2']//(l:Code[count(ancestor::l:CodeList[r:Label])=$index+number($labelOuNon)-1] | l:CodeList/r:Label[count(ancestor::l:CodeList[r:Label])=$index+number($labelOuNon)])"/>
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
      <xsl:for-each select="d:StructuredMixedGridResponseDomain/(d:GridResponseDomain | d:NoDataByDefinition)[.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='1' and (@rangeMinimum=string($index) or @specificValue=string($index))]]">
         <xsl:sort select="number(.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum)"/>
         <xsl:sequence select="."/>
      </xsl:for-each>
   </xsl:template>
   <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]"
                 mode="iatddi:get-table-line">
      <xsl:param name="index" tunnel="yes"/>
      <xsl:for-each select="d:StructuredMixedGridResponseDomain/(d:GridResponseDomain | d:NoDataByDefinition)">
         <xsl:sort select="number(.//d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum)"/>
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
   <xsl:template match="l:Code[max(ancestor::d:GridDimension[@rank='1']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList | ancestor::l:Code))&gt;1]"
                 mode="iatddi:get-colspan"
                 priority="1"><!-- On récupère  le niveau de profondeur des codes parents -->
      <xsl:variable name="parents">
         <xsl:value-of select="if (string(count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) != 'NaN') then count(ancestor::l:CodeList[r:Label] | ancestor::l:Code) else 0"/>
      </xsl:variable>
      <!-- On récupère le niveau de profondeur des codes enfants -->
      <xsl:variable name="enfants">
         <xsl:value-of select="if (string(max(.//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))-count(ancestor::l:CodeList[r:Label] | ancestor::l:Code)) !='') then max(.//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label] | ancestor::l:Code))-count(ancestor::l:CodeList[r:Label] | ancestor::l:Code) else 0"/>
      </xsl:variable>
      <xsl:value-of select="max(ancestor::d:GridDimension[@rank='1']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList | ancestor::l:Code))-number($parents)-number($enfants)+1"/>
   </xsl:template>
   <!-- Pour les chapeaux de la colonne (qui font donc partie aussi de la ligne) -->
   <!-- Il s'agit des r:Label qui sont directement en haut de la liste de codes référencées -->
   <!-- Il faut ramener la profondeur de niveau de la seconde dimension -->
   <!-- Donc on sélectionne tous les l:Code qui n'ont pas d'enfant et qui sont donc le plus en profondeur,
    On compte pour chacun d'eux quel est ce niveau de profondeur
    Et on prend le max qui correspond la profondeur de la seconde dimension-->
   <xsl:template match="r:Label[parent::l:CodeList/parent::r:CodeListReference/parent::d:CodeDomain/parent::d:GridDimension[@rank='1']]"
                 mode="iatddi:get-rowspan"
                 priority="1">
      <xsl:variable name="labelOuNon">
         <xsl:value-of select="count(ancestor::d:GridDimension[@rank='1']/following-sibling::d:GridDimension[@rank='2']/d:CodeDomain/r:CodeListReference/l:CodeList/r:Label)"/>
      </xsl:variable>
      <xsl:value-of select="max(ancestor::d:GridDimension[@rank='1']/following-sibling::d:GridDimension[@rank='2']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label]))+1-number($labelOuNon)"/>
   </xsl:template>
   <!--Pour les colonnes, pour les l:Code qui ont un l:Code, c'est à dire une case éclatée en sous-cases, on ramène le nombre de l:Code enfant -->
   <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='1'] and child::l:Code]"
                 mode="iatddi:get-rowspan"
                 priority="1">
      <xsl:value-of select="count(descendant::l:Code[not(child::l:Code)])"/>
   </xsl:template>
   <!-- ATTENTION -->
   <!-- Pour l'instant, c'est égal au nombre de l:Code qu'on trouve + bas. Cele ne fonctionne que s'il y a deux niveaux -->
   <!-- A faire évoluer pour un tableau dont le chapeau du haut aurait 3 niveaux -->
   <xsl:template match="r:Label[ancestor::d:GridDimension[@rank='2']]"
                 mode="iatddi:get-colspan"
                 priority="1">
      <xsl:value-of select="count(parent::l:CodeList//l:Code)"/>
   </xsl:template>
   <!-- Pour les étiquettes de la ligne (seconde dimension), comme plus haut, on calcule le niveau de profondeur -->
   <xsl:template match="l:Code[ancestor::d:GridDimension[@rank='2']]"
                 mode="iatddi:get-rowspan"
                 priority="1">
      <xsl:value-of select="max(ancestor::d:GridDimension[@rank='2']//l:Code[not(child::l:Code)]/count(ancestor::l:CodeList[r:Label]))+1-count(ancestor::l:CodeList[r:Label])"/>
   </xsl:template>
   <xsl:template match="d:NoDataByDefinition" mode="iatddi:get-colspan" priority="1">
      <xsl:value-of select="string(1+number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMaximum)-number(d:CellCoordinatesAsDefined/d:SelectDimension[@rank='2']/@rangeMinimum))"/>
   </xsl:template>
   <xsl:template match="*" mode="iatddi:get-computation-items" as="xs:string *">
      <xsl:variable name="id">
         <xsl:value-of select="iatddi:get-id(.)"/>
      </xsl:variable>
      <xsl:for-each select="//d:ComputationItem[contains(r:CommandCode/r:Command/r:CommandContent/text(), $id)]"><!-- Le computationItem contient la chaîne correspondant à la variable mais peut-être que cela ne correspond pas exactement à la variable --><!-- On modifie la condition en supprimant les éventuels faux positifs (la même valeur suivie directement d'un tiret ou d'un chiffre) -->
         <xsl:variable name="condition">
            <xsl:value-of select="replace(r:CommandCode/r:Command/r:CommandContent/text(),concat($id,'(\-|[0-9])'),'')"/>
         </xsl:variable>
         <!-- Si la condition modifiée contient toujours la valeur, c'est ok -->
         <xsl:if test="contains($condition,$id)">
            <xsl:value-of select="iatddi:get-id(current()/d:InterviewerInstructionReference/d:Instruction)"/>
         </xsl:if>
      </xsl:for-each>
   </xsl:template>
   <xsl:template match="*" mode="iatddi:get-then" as="xs:string *">
      <xsl:variable name="id">
         <xsl:value-of select="iatddi:get-id(.)"/>
      </xsl:variable>
      <xsl:for-each select="//d:IfThenElse[d:ThenConstructReference/d:Sequence/d:TypeOfSequence[text()='Cachable'] and contains(d:IfCondition/r:Command/r:CommandContent/text(),$id)]">
         <xsl:value-of select="iatddi:get-id(current()/d:ThenConstructReference/d:Sequence)"/>
      </xsl:for-each>
   </xsl:template>
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Module']"
                 mode="iatddi:get-control">
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
      <xsl:value-of select="substring-after(//d:Expression/r:Command/r:CommandContent[contains(text(),$id)]/text(),'=')"/>
   </xsl:template>
   <xsl:template match="d:DateTimeDomain[r:DateFieldFormat/text()='HH' and @regExp and (parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed)]"
                 mode="iatddi:get-message"
                 priority="2">
      <xsl:variable name="apos">'</xsl:variable>
      <xsl:value-of select="concat('Le nombre d',$apos,'heures doit être compris entre 0 et 99.')"/>
   </xsl:template>
   <xsl:template match="d:DateTimeDomain[r:DateFieldFormat/text()='mm' and @regExp and (parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed)]"
                 mode="iatddi:get-message"
                 priority="2">
      <xsl:value-of select="string('Le nombre de minutes doit être compris entre 0 et 59.')"/>
   </xsl:template>
   <!--Getter function of the DDI element identifier-->
   <xsl:function name="iatddi:get-id">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-id"/>
   </xsl:function>
   <!--Getter function of the DDI element label-->
   <xsl:function name="iatddi:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-label">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!--Getter function of the DDI document languages -->
   <xsl:function name="iatddi:get-languages" as="xs:string*">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-languages"/>
   </xsl:function>
   <!--Getter function of the DDI element value-->
   <xsl:function name="iatddi:get-value" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-value"/>
   </xsl:function>
   <!--Getter function of some DDI element display format-->
   <xsl:function name="iatddi:get-outputformat" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-outputformat"/>
   </xsl:function>
   <!--Getter function of the DDI element index-->
   <xsl:function name="iatddi:get-index" as="xs:integer">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-index"/>
   </xsl:function>
   <!--Getter function of a help-type instruction-->
   <xsl:function name="iatddi:get-help-instruction">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-help-instruction">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!--Getter function of a hint-type instruction-->
   <xsl:function name="iatddi:get-hint-instruction">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-hint-instruction">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-style" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-style"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-type" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-type"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-cachable">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-cachable"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-grisable" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-grisable"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-control" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-control"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-number-decimal" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-number-decimal"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-message" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-message">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-type-message" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-type-message"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-link" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-link"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-format"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-length" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-length"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-conditionned-text" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-conditionned-text"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-conditionned-text-bis" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-conditionned-text-bis"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-nb-modules" as="xs:integer">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-nb-modules"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-suffix" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-suffix">
         <xsl:with-param name="language" select="$language" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-levels-first-dimension">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-levels-first-dimension"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-levels-second-dimension">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-levels-second-dimension"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-codes-first-dimension">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-codes-first-dimension"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-title-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-title-line">
         <xsl:with-param name="index" select="$index" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-table-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-table-line">
         <xsl:with-param name="index" select="$index" tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-rowspan" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-rowspan"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-colspan" as="xs:string">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-colspan"/>
   </xsl:function>
   <!--Getter function of the minimum number of lines to display in a dynamic table-->
   <xsl:function name="iatddi:get-minimumRequired">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-minimumRequired"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-computation-items" as="xs:string*">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-computation-items"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatddi:get-then" as="xs:string*">
      <xsl:param name="context" as="item()"/>
      <xsl:apply-templates select="$context" mode="iatddi:get-then"/>
   </xsl:function>
   <!--Starting with d:Instrument-->
   <xsl:template match="/" mode="source">
      <xsl:apply-templates select="//d:Sequence[d:TypeOfSequence/text()='Modele']" mode="source"/>
   </xsl:template>
   <!--r:ID is the element identifier in DDI-->
   <xsl:template match="*" mode="iatddi:get-id">
      <xsl:value-of select="r:ID"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:TextDomain or d:NumericDomain or d:NumericDomainReference or d:DateTimeDomain or d:DateTimeDomainReference or d:CodeDomain or d:NominalDomain]"
                 mode="iatddi:get-id">
      <xsl:value-of select="r:OutParameter/r:ID"/>
   </xsl:template>
   <!---->
   <xsl:template match="*[ends-with(name(),'Domain') and (parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed)]"
                 mode="iatddi:get-id">
      <xsl:value-of select="ancestor::*[name()=('d:QuestionGrid','d:QuestionItem')]/r:Binding[r:SourceParameterReference/r:ID=current()/r:OutParameter/r:ID]/r:TargetParameterReference/r:ID"/>
   </xsl:template>
   <!---->
   <xsl:template match="*[ends-with(name(),'DomainReference') and (parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed)]"
                 mode="iatddi:get-id">
      <xsl:value-of select="ancestor::*[name()=('d:QuestionGrid','d:QuestionItem')]/r:Binding[r:SourceParameterReference/r:ID=current()/r:OutParameter/r:ID]/r:TargetParameterReference/r:ID"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Instruction" mode="iatddi:get-id">
      <xsl:value-of select="concat(parent::d:InterviewerInstructionReference/parent::*/r:ID,'-',r:ID)"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:StructuredMixedGridResponseDomain[parent::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]]"
                 mode="iatddi:get-id">
      <xsl:value-of select="concat(parent::d:QuestionGrid/r:ID,'-rowloop')"/>
   </xsl:template>
   <!--Identifiers from rows and columns (table name is included)-->
   <xsl:template match="l:Code[ancestor::d:GridDimension]" mode="iatddi:get-id">
      <xsl:value-of select="concat(ancestor::d:GridDimension/parent::d:QuestionGrid/r:ID,'-',r:ID)"/>
   </xsl:template>
   <!--Identifiers from dimensions (table name is included)-->
   <xsl:template match="r:Label[parent::l:CodeList[ancestor::d:GridDimension]]"
                 mode="iatddi:get-id">
      <xsl:value-of select="concat(ancestor::d:QuestionGrid/r:ID,'-',parent::l:CodeList/r:ID,'-Header-',count(preceding-sibling::r:Label)+1)"/>
   </xsl:template>
   <!---->
   <xsl:template match="l:Variable" mode="iatddi:get-id">
      <xsl:value-of select="l:VariableName/r:String"/>
   </xsl:template>
   <!--Index from a Module type sequence-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Module']"
                 mode="iatddi:get-index">
      <xsl:value-of select="count(ancestor::d:ControlConstructReference/preceding-sibling::d:ControlConstructReference/descendant-or-self::d:Sequence[d:TypeOfSequence/text()='Module'])+1"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Paragraphe']"
                 mode="iatddi:get-index">
      <xsl:value-of select="count(parent::d:ControlConstructReference/preceding-sibling::d:ControlConstructReference/d:Sequence[d:TypeOfSequence/text()='Paragraphe'])+count(ancestor::d:Sequence[d:TypeOfSequence/text()='Module']/parent::d:ControlConstructReference/preceding-sibling::d:ControlConstructReference/d:Sequence[d:TypeOfSequence/text()='Module']//d:Sequence[d:TypeOfSequence/text()='Paragraphe'])+1"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Sequence" mode="iatddi:get-label">
      <xsl:apply-templates select="r:Label" mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:StatementItem" mode="iatddi:get-label">
      <xsl:apply-templates select="d:DisplayText/d:LiteralText/d:Text" mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Instruction[not(ancestor::d:ComputationItem)]"
                 mode="iatddi:get-label">
      <xsl:apply-templates select="d:InstructionText/d:LiteralText/d:Text" mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:ResponseDomainInMixed" mode="iatddi:get-label">
      <xsl:apply-templates select="child::node()/r:Label" mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[not(descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')])]"
                 mode="iatddi:get-label">
      <xsl:apply-templates select="d:QuestionText/d:LiteralText/d:Text" mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionGrid[not(descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')])]"
                 mode="iatddi:get-label">
      <xsl:apply-templates select="d:QuestionText/d:LiteralText/d:Text" mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="l:Code" mode="iatddi:get-label">
      <xsl:apply-templates select="r:CategoryReference/l:Category/r:Label" mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="*[ends-with(name(),'Domain') and parent::d:ResponseDomainInMixed]"
                 mode="iatddi:get-label">
      <xsl:apply-templates select="r:Label" mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="l:Code" mode="iatddi:get-value">
      <xsl:value-of select="r:Value"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:GenericOutputFormat/text()='boutonradio']]"
                 mode="iatddi:get-outputformat">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:GenericOutputFormat/text()='listederoulante']]"
                 mode="iatddi:get-outputformat">
      <xsl:value-of select="string('minimal')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:GenericOutputFormat/text()='caseacocher']]"
                 mode="iatddi:get-outputformat">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NominalDomain]" mode="iatddi:get-outputformat">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:CodeDomain[(ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed) and r:GenericOutputFormat/text()='boutonradio']"
                 mode="iatddi:get-outputformat">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:CodeDomain[(ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed) and r:GenericOutputFormat/text()='listederoulante']"
                 mode="iatddi:get-outputformat">
      <xsl:value-of select="string('minimal')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:CodeDomain[(ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed) and r:GenericOutputFormat/text()='caseacocher']"
                 mode="iatddi:get-outputformat">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NominalDomain[ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed]"
                 mode="iatddi:get-outputformat">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="l:Code[.//r:Description]" mode="iatddi:get-help-instruction">
      <xsl:apply-templates select="r:CategoryReference/l:Category/r:Description"
                           mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain]"
                 mode="iatddi:get-hint-instruction">
      <xsl:value-of select="concat('Exemple : ',d:NumericDomain/r:NumberRange/r:High/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[parent::d:ResponseDomainInMixed]"
                 mode="iatddi:get-hint-instruction">
      <xsl:value-of select="concat('Exemple : ',r:NumberRange/r:High/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference]"
                 mode="iatddi:get-hint-instruction">
      <xsl:value-of select="concat('Exemple : ',descendant::r:High[not(ancestor::r:OutParameter)]/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[parent::d:ResponseDomainInMixed]"
                 mode="iatddi:get-hint-instruction">
      <xsl:value-of select="concat('Exemple : ',descendant::r:High[not(ancestor::r:OutParameter)]/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomainReference[descendant::r:DateTypeCode/text()='date']"
                 mode="iatddi:get-hint-instruction">
      <xsl:value-of select="string('Date au format : JJ/MM/AAAA')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:DateTimeDomainReference/descendant::r:DateTypeCode/text()='date']"
                 mode="iatddi:get-hint-instruction">
      <xsl:value-of select="string('Date au format JJ/MM/AAAA')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem" mode="iatddi:get-style">
      <xsl:value-of select="string('question')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:TextDomain[not(@maxLength)]]"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('question text')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:TextDomain[@maxLength]]"
                 mode="iatddi:get-style">
      <xsl:value-of select="concat('question text text',d:TextDomain/@maxLength)"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:TextDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(@maxLength)]"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('text')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:TextDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and @maxLength]"
                 mode="iatddi:get-style">
      <xsl:value-of select="concat('text text',@maxLength)"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain or d:NumericDomainReference]"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('question number')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('number')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('number')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Paragraphe']"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('submodule')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Groupe']"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('group')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Instruction[d:InstructionName/r:String='Aide']"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('help')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Instruction[d:InstructionName/r:String='Consigne']"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('hint')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionGrid[count(d:GridDimension)=2 and not(d:GridDimension/d:Roster/@maximumAllowed)]"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('question complex-grid')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionGrid[count(d:GridDimension)=1]"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('question multiple-choice-question')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionGrid[count(d:GridDimension)=2 and d:GridDimension/d:Roster/@maximumAllowed]"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('question simpleTable')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomain[r:DateTypeCode/text()='duration']"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('duration')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomain[r:DateTypeCode/text()='date']"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('date')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomainReference[descendant::r:DateTypeCode/text()='duration']"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('duration')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomainReference[descendant::r:DateTypeCode/text()='date']"
                 mode="iatddi:get-style">
      <xsl:value-of select="string('date')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Sequence[child::d:TypeOfSequence/text()='Module' and (ancestor::d:ThenConstructReference or ancestor::d:ElseConstructReference) and ancestor::d:Sequence[child::d:TypeOfSequence/text()='Cachable']]"
                 mode="iatddi:get-cachable">
      <xsl:value-of select="ancestor::d:IfThenElse[1]/d:IfCondition/r:Command/r:CommandContent"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Sequence[(parent::d:ThenConstructReference or parent::d:ElseConstructReference) and child::d:TypeOfSequence/text()='Cachable' and not(child::d:TypeOfSequence/text()='Module')]"
                 mode="iatddi:get-cachable">
      <xsl:value-of select="ancestor::d:IfThenElse[1]/d:IfCondition/r:Command/r:CommandContent"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Sequence[(parent::d:ThenConstructReference or parent::d:ElseConstructReference) and child::d:TypeOfSequence/text()='Grisable']"
                 mode="iatddi:get-grisable">
      <xsl:value-of select="ancestor::d:IfThenElse[1]/d:IfCondition/r:Command/r:CommandContent"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Instruction[ancestor::d:ComputationItem]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('not(',normalize-space(ancestor::d:ComputationItem/r:CommandCode/r:Command/r:CommandContent/text()),')')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and not(@decimalPositions)]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:integer) then (xs:integer(.)&lt;=',number(r:NumberRange/r:High),' and xs:integer(.)&gt;',number(r:NumberRange/r:Low),') else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[descendant::r:Low[@isInclusive='false'] and not(@decimalPositions)]]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:integer) then (xs:integer(.)&lt;=',number(d:NumericDomain/r:NumberRange/r:High),' and xs:integer(.)&gt;',number(d:NumericDomain/r:NumberRange/r:Low),') else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and not(@decimalPositions)]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:integer) then (xs:integer(.)&lt;=',number(r:NumberRange/r:High),' and xs:integer(.)&gt;=',number(r:NumberRange/r:Low),') else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[not(descendant::r:Low[@isInclusive='false']) and not(@decimalPositions)]]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:integer) then (xs:integer(.)&lt;=',number(d:NumericDomain/r:NumberRange/r:High),' and xs:integer(.)&gt;=',number(d:NumericDomain/r:NumberRange/r:Low),') else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and not(r:ManagedNumericRepresentation/@decimalPositions)]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:integer) then (xs:integer(.)&lt;=',number(descendant::r:High[not(ancestor::r:OutParameter)]),' and xs:integer(.)&gt;',number(descendant::r:Low[not(ancestor::r:OutParameter)]),') else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[descendant::r:Low[@isInclusive='false'] and not(r:ManagedNumericRepresentation/@decimalPositions)]]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:integer) then (xs:integer(.)&lt;=',number(descendant::r:High[not(ancestor::r:OutParameter)]),' and xs:integer(.)&gt;',number(descendant::r:Low[not(ancestor::r:OutParameter)]),') else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and not(r:ManagedNumericRepresentation/@decimalPositions)]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:integer) then (xs:integer(.)&lt;=',number(descendant::r:High[not(ancestor::r:OutParameter)]),' and xs:integer(.)&gt;=',number(descendant::r:Low[not(ancestor::r:OutParameter)]),') else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[not(descendant::r:Low[@isInclusive='false']) and not(r:ManagedNumericRepresentation/@decimalPositions)]]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:integer) then (xs:integer(.)&lt;=',number(descendant::r:High[not(ancestor::r:OutParameter)]),' and xs:integer(.)&gt;=',number(descendant::r:Low[not(ancestor::r:OutParameter)]),') else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and @decimalPositions]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:float) then (xs:float(.)&lt;=',number(r:NumberRange/r:High),' and xs:float(.)&gt;',number(r:NumberRange/r:Low),' and matches(.,&#34;^(0|[1-9][0-9]*)(\.[0-9]{1,',@decimalPositions,'})?$&#34;)) else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[descendant::r:Low[@isInclusive='false'] and @decimalPositions]]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:float) then (xs:float(.)&lt;=',number(d:NumericDomain/r:NumberRange/r:High),' and xs:float(.)&gt;',number(d:NumericDomain/r:NumberRange/r:Low),' and matches(.,&#34;^(0|[1-9][0-9]*)(\.[0-9]{1,',d:NumericDomain/@decimalPositions,'})?$&#34;)) else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and @decimalPositions]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:float) then (xs:float(.)&lt;=',number(r:NumberRange/r:High),' and xs:float(.)&gt;=',number(r:NumberRange/r:Low),' and matches(.,&#34;^(0|[1-9][0-9]*)(\.[0-9]{1,',@decimalPositions,'})?$&#34;)) else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[not(descendant::r:Low[@isInclusive='false']) and @decimalPositions]]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:float) then (xs:float(.)&lt;=',number(d:NumericDomain/r:NumberRange/r:High),' and xs:float(.)&gt;=',number(d:NumericDomain/r:NumberRange/r:Low),' and matches(.,&#34;^(0|[1-9][0-9]*)(\.[0-9]{1,',d:NumericDomain/@decimalPositions,'})?$&#34;)) else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and r:ManagedNumericRepresentation/@decimalPositions]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:float) then (xs:float(.)&lt;=',number(descendant::r:High[not(ancestor::r:OutParameter)]),' and xs:float(.)&gt;',number(descendant::r:Low[not(ancestor::r:OutParameter)]),' and matches(.,&#34;^(0|[1-9][0-9]*)(\.[0-9]{1,',r:ManagedNumericRepresentation/@decimalPositions,'})?$&#34;)) else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[descendant::r:Low[@isInclusive='false'] and r:ManagedNumericRepresentation/@decimalPositions]]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:float) then (xs:float(.)&lt;=',number(descendant::r:High[not(ancestor::r:OutParameter)]),' and xs:float(.)&gt;',number(descendant::r:Low[not(ancestor::r:OutParameter)]),' and matches(.,&#34;^(0|[1-9][0-9]*)(\.[0-9]{1,',descendant::r:ManagedNumericRepresentation[not(ancestor::r:OutParameter)]/@decimalPositions,'})?$&#34;)) else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and r:ManagedNumericRepresentation/@decimalPositions]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:float) then (xs:float(.)&lt;=',number(descendant::r:High[not(ancestor::r:OutParameter)]),' and xs:float(.)&gt;=',number(descendant::r:Low[not(ancestor::r:OutParameter)]),' and matches(.,&#34;^(0|[1-9][0-9]*)(\.[0-9]{1,',r:ManagedNumericRepresentation/@decimalPositions,'})?$&#34;)) else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[not(descendant::r:Low[@isInclusive='false']) and r:ManagedNumericRepresentation/@decimalPositions]]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('if(. castable as xs:float) then (xs:float(.)&lt;=',number(descendant::r:High[not(ancestor::r:OutParameter)]),' and xs:float(.)&gt;=',number(descendant::r:Low[not(ancestor::r:OutParameter)]),' and matches(.,&#34;^(0|[1-9][0-9]*)(\.[0-9]{1,',descendant::r:ManagedNumericRepresentation[not(ancestor::r:OutParameter)]/@decimalPositions,'})?$&#34;)) else (.=&#34;&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[*[ends-with(name(),'Domain') and @regExp]]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('matches(.,&#34;',*[ends-with(name(),'Domain')]/@regExp,'&#34;) or .=&#34;&#34;')"/>
   </xsl:template>
   <!---->
   <xsl:template match="*[ends-with(name(),'Domain') and @regExp and (parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed)]"
                 mode="iatddi:get-control">
      <xsl:value-of select="concat('matches(.,&#34;',@regExp,'&#34;) or .=&#34;&#34;')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]"
                 mode="iatddi:get-number-decimal">
      <xsl:value-of select="@decimalPositions"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain]"
                 mode="iatddi:get-number-decimal">
      <xsl:value-of select="d:NumericDomain/@decimalPositions"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:ManagedNumericRepresentation]"
                 mode="iatddi:get-number-decimal">
      <xsl:value-of select="r:ManagedNumericRepresentation/@decimalPositions"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference and descendant::r:ManagedNumericRepresentation]"
                 mode="iatddi:get-number-decimal">
      <xsl:value-of select="d:NumericDomainReference/r:ManagedNumericRepresentation/@decimalPositions"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Instruction[ancestor::d:ComputationItem]"
                 mode="iatddi:get-message">
      <xsl:value-of select="d:InstructionText/d:LiteralText/d:Text"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and not(@decimalPositions)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez saisir un nombre entier compris entre ', string(number(r:NumberRange/r:Low/text())+1), ' et ', r:NumberRange/r:High/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[descendant::r:Low[@isInclusive='false'] and not(@decimalPositions)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez saisir un nombre entier compris entre ', string(number(d:NumericDomain/r:NumberRange/r:Low/text())+1), ' et ', d:NumericDomain/r:NumberRange/r:High/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and not(@decimalPositions)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez saisir un nombre entier compris entre ', r:NumberRange/r:Low/text(), ' et ', r:NumberRange/r:High/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[not(descendant::r:Low[@isInclusive='false']) and not(@decimalPositions)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez saisir un nombre entier compris entre ', d:NumericDomain/r:NumberRange/r:Low/text(), ' et ', d:NumericDomain/r:NumberRange/r:High/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and not(r:ManagedNumericRepresentation/@decimalPositions)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez saisir un nombre entier compris entre ', string(number(descendant::r:Low[not(ancestor::r:OutParameter)]/text())+1), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[descendant::r:Low[@isInclusive='false'] and not(r:ManagedNumericRepresentation/@decimalPositions)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez saisir un nombre entier compris entre ', string(number(descendant::r:Low[not(ancestor::r:OutParameter)]/text())+1), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and not(r:ManagedNumericRepresentation/@decimalPositions)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez saisir un nombre entier compris entre ', descendant::r:Low[not(ancestor::r:OutParameter)]/text(), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[not(descendant::r:Low[@isInclusive='false']) and not(r:ManagedNumericRepresentation/@decimalPositions)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez saisir un nombre entier compris entre ', descendant::r:Low[not(ancestor::r:OutParameter)]/text(), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text())"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and (@decimalPositions &gt; 1)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', string(number(r:NumberRange/r:Low/text())+1), ' et ', r:NumberRange/r:High/text(),' (avec au plus ',@decimalPositions,' chiffres derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[descendant::r:Low[@isInclusive='false'] and (@decimalPositions &gt; 1)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', string(number(d:NumericDomain/r:NumberRange/r:Low/text())+1), ' et ', d:NumericDomain/r:NumberRange/r:High/text(),' (avec au plus ',d:NumericDomain/@decimalPositions,' chiffres derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and (@decimalPositions &gt; 1)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', r:NumberRange/r:Low/text(), ' et ', r:NumberRange/r:High/text(),' (avec au plus ',@decimalPositions,' chiffres derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[not(descendant::r:Low[@isInclusive='false']) and (@decimalPositions &gt; 1)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', d:NumericDomain/r:NumberRange/r:Low/text(), ' et ', d:NumericDomain/r:NumberRange/r:High/text(),' (avec au plus ',d:NumericDomain/@decimalPositions,' chiffres derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and (r:ManagedNumericRepresentation/@decimalPositions &gt; 1)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', string(number(descendant::r:Low[not(ancestor::r:OutParameter)]/text())+1), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text(),' (avec au plus ',r:ManagedNumericRepresentation/@decimalPositions,' chiffres derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[descendant::r:Low[@isInclusive='false'] and (r:ManagedNumericRepresentation/@decimalPositions &gt; 1)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', string(number(descendant::r:Low[not(ancestor::r:OutParameter)]/text())+1), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text(),' (avec au plus ',descendant::r:ManagedNumericRepresentation[not(ancestor::r:OutParameter)]/@decimalPositions,' chiffres derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and (r:ManagedNumericRepresentation/@decimalPositions &gt; 1)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', descendant::r:Low[not(ancestor::r:OutParameter)]/text(), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text(),' (avec au plus ',r:ManagedNumericRepresentation/@decimalPositions,' chiffres derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[not(descendant::r:Low[@isInclusive='false']) and (r:ManagedNumericRepresentation/@decimalPositions &gt; 1)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', descendant::r:Low[not(ancestor::r:OutParameter)]/text(), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text(),' (avec au plus ',descendant::r:ManagedNumericRepresentation[not(ancestor::r:OutParameter)]/@decimalPositions,' chiffres derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and (@decimalPositions =1)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', string(number(r:NumberRange/r:Low/text())+1), ' et ', r:NumberRange/r:High/text(),' (avec au plus ',@decimalPositions,' chiffre derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[descendant::r:Low[@isInclusive='false'] and (@decimalPositions =1)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', string(number(d:NumericDomain/r:NumberRange/r:Low/text())+1), ' et ', d:NumericDomain/r:NumberRange/r:High/text(),' (avec au plus ',d:NumericDomain/@decimalPositions,' chiffre derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and (@decimalPositions =1)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', r:NumberRange/r:Low/text(), ' et ', r:NumberRange/r:High/text(),' (avec au plus ',@decimalPositions,' chiffre derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[not(descendant::r:Low[@isInclusive='false']) and (@decimalPositions =1)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', d:NumericDomain/r:NumberRange/r:Low/text(), ' et ', d:NumericDomain/r:NumberRange/r:High/text(),' (avec au plus ',d:NumericDomain/@decimalPositions,' chiffre derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:Low[@isInclusive='false'] and (r:ManagedNumericRepresentation/@decimalPositions =1)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', string(number(descendant::r:Low[not(ancestor::r:OutParameter)]/text())+1), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text(),' (avec au plus ',r:ManagedNumericRepresentation/@decimalPositions,' chiffre derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[descendant::r:Low[@isInclusive='false'] and (r:ManagedNumericRepresentation/@decimalPositions =1)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', string(number(descendant::r:Low[not(ancestor::r:OutParameter)]/text())+1), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text(),' (avec au plus ',descendant::r:ManagedNumericRepresentation[not(ancestor::r:OutParameter)]/@decimalPositions,' chiffre derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(descendant::r:Low[@isInclusive='false']) and (r:ManagedNumericRepresentation/@decimalPositions =1)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', descendant::r:Low[not(ancestor::r:OutParameter)]/text(), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text(),' (avec au plus ',r:ManagedNumericRepresentation/@decimalPositions,' chiffre derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[not(descendant::r:Low[@isInclusive='false']) and (r:ManagedNumericRepresentation/@decimalPositions =1)]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="concat('Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre ', descendant::r:Low[not(ancestor::r:OutParameter)]/text(), ' et ', descendant::r:High[not(ancestor::r:OutParameter)]/text(),' (avec au plus ',descendant::r:ManagedNumericRepresentation[not(ancestor::r:OutParameter)]/@decimalPositions,' chiffre derrière le séparateur &#34;.&#34;)')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[*[ends-with(name(),'Domain') and @regExp]]"
                 mode="iatddi:get-message">
      <xsl:value-of select="string('Vous devez saisir une valeur correcte')"/>
   </xsl:template>
   <!---->
   <xsl:template match="*[ends-with(name(),'Domain') and @regExp and (parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed)]"
                 mode="iatddi:get-message">
      <xsl:value-of select="string('Vous devez saisir une valeur correcte')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomainReference[descendant::r:DateTypeCode/text()='date']"
                 mode="iatddi:get-message">
      <xsl:value-of select="string('Entrez une date valide')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:DateTimeDomainReference/descendant::r:DateTypeCode/text()='date']"
                 mode="iatddi:get-message">
      <xsl:value-of select="string('Entrez une date valide')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Instruction[ancestor::d:ComputationItem]"
                 mode="iatddi:get-type-message">
      <xsl:value-of select="string('warning')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain]" mode="iatddi:get-format">
      <xsl:value-of select="&#34;if (. castable as xs:integer) then replace(format-number(xs:integer(.),'###,###,###,###,###,###,###,###,###,##0'),',',' ') else ''&#34;"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[parent::d:GridResponseDomain]"
                 mode="iatddi:get-format">
      <xsl:value-of select="&#34;if (. castable as xs:integer) then replace(format-number(xs:integer(.),'###,###,###,###,###,###,###,###,###,##0'),',',' ') else ''&#34;"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain[r:NumberRange]]"
                 mode="iatddi:get-length">
      <xsl:value-of select="max((string-length(d:NumericDomain/r:NumberRange/r:Low),string-length(d:NumericDomain/r:NumberRange/r:High)))"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and r:NumberRange]"
                 mode="iatddi:get-length">
      <xsl:value-of select="max((string-length(r:NumberRange/r:Low),string-length(r:NumberRange/r:High)))"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference[descendant::r:NumberRange]]"
                 mode="iatddi:get-length">
      <xsl:value-of select="max((string-length(descendant::r:Low[not(ancestor::r:OutParameter)]),string-length(descendant::r:High[not(ancestor::r:OutParameter)])))"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:NumberRange]"
                 mode="iatddi:get-length">
      <xsl:value-of select="max((string-length(descendant::r:Low[not(ancestor::r:OutParameter)]),string-length(descendant::r:High[not(ancestor::r:OutParameter)])))"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:TextDomain[@maxLength]]"
                 mode="iatddi:get-length">
      <xsl:value-of select="number(d:TextDomain/@maxLength)"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:TextDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and @maxLength]"
                 mode="iatddi:get-length">
      <xsl:value-of select="number(@maxLength)"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomain[r:DateTypeCode/text()='duration']"
                 mode="iatddi:get-length">
      <xsl:value-of select="string-length(r:DateFieldFormat)"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomainReference[descendant::r:DateTypeCode/text()='duration']"
                 mode="iatddi:get-length">
      <xsl:value-of select="string-length(descendant::r:DateFieldFormat)"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomainReference[descendant::r:DateTypeCode/text()='date']"
                 mode="iatddi:get-type">
      <xsl:value-of select="string('date')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:DateTimeDomainReference/descendant::r:DateTypeCode/text()='date']"
                 mode="iatddi:get-type">
      <xsl:value-of select="string('date')"/>
   </xsl:template>
   <!---->
   <xsl:template match="*" mode="iatddi:get-rowspan">
      <xsl:value-of select="1"/>
   </xsl:template>
   <!---->
   <xsl:template match="*" mode="iatddi:get-colspan">
      <xsl:value-of select="1"/>
   </xsl:template>
   <!---->
   <xsl:template match="r:Label" mode="iatddi:get-label">
      <xsl:apply-templates select="." mode="choixLangue"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:StructuredMixedGridResponseDomain[parent::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]]"
                 mode="iatddi:get-minimumRequired">
      <xsl:value-of select="../d:GridDimension/d:Roster/@minimumRequired"/>
   </xsl:template>
</xsl:stylesheet>
