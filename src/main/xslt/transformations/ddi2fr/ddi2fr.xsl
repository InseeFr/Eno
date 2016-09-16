<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:il="http://xml/insee.fr/xslt/lib"
                xmlns:iat="http://xml/insee.fr/xslt/apply-templates"
                xmlns:iatddi="http://xml/insee.fr/xslt/apply-templates/ddi"
                xmlns:iatfr="http://xml/insee.fr/xslt/apply-templates/form-runner"
                xmlns:d="ddi:datacollection:3_2"
                xmlns:r="ddi:reusable:3_2"
                xmlns:l="ddi:logicalproduct:3_2"
                exclude-result-prefixes="xd"
                version="2.0">
   <xsl:import href="../../inputs/ddi/source.xsl"/>
   <xsl:import href="../../outputs/orbeon-form-runner/models.xsl"/>
   <xsl:import href="../../lib.xsl"/>
   <xsl:output method="xml" indent="yes"/>
   <xd:doc scope="stylesheet">
      <xd:desc>
         <xd:p>
            <xd:b>Created on:</xd:b> Apr 9, 2013</xd:p>
         <xd:p>
            <xd:b>Author:</xd:b> vdv</xd:p>
         <xd:p>Transforms DDI into Orbeon Form Builder!</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="/">
      <xsl:apply-templates select="/" mode="source"/>
   </xsl:template>
   <!-- Getting this here, actually dependent of the input and ouput language -->
   <xsl:template match="d:Instruction[descendant::d:ConditionalText[r:SourceParameterReference] and not(ancestor::d:ComputationItem)]"
                 mode="iatddi:get-conditionned-text"
                 priority="1">
      <xsl:variable name="condition">
         <xsl:copy-of select="descendant::d:ConditionalText"/>
      </xsl:variable>
      <xsl:variable name="text">
         <xsl:value-of select="il:serialize(descendant::d:LiteralText/d:Text/node())"/>
      </xsl:variable>
      <xsl:variable name="result">
         <xsl:text>concat(''</xsl:text>
         <xsl:for-each select="tokenize($text,'ø')">
            <xsl:text>,</xsl:text>
            <xsl:choose>
               <xsl:when test=".=$condition/d:ConditionalText/r:SourceParameterReference/r:OutParameter/r:ID/text()">
                  <xsl:text>instance('fr-form-instance')//</xsl:text>
                  <xsl:value-of select="."/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:text>'</xsl:text>
                  <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.-->
                  <xsl:value-of select="replace(.,&#34;'&#34;,&#34;''&#34;)"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:value-of select="$result"/>
   </xsl:template>
   <!-- Getting this here, actually dependent of the input and ouput language -->
   <xsl:template match="d:Instruction[descendant::d:ConditionalText[r:SourceParameterReference] and ancestor::d:ComputationItem]"
                 mode="iatddi:get-conditionned-text-bis"
                 priority="1">
      <xsl:variable name="condition">
         <xsl:copy-of select="descendant::d:ConditionalText"/>
      </xsl:variable>
      <xsl:variable name="text">
         <xsl:value-of select="il:serialize(descendant::d:LiteralText/d:Text/node())"/>
      </xsl:variable>
      <xsl:variable name="result">
         <xsl:text>concat(''</xsl:text>
         <xsl:for-each select="tokenize($text,'ø')">
            <xsl:text>,</xsl:text>
            <xsl:choose>
               <xsl:when test=".=$condition/d:ConditionalText/r:SourceParameterReference/r:OutParameter/r:ID/text()">
                  <xsl:text>instance('fr-form-instance')//</xsl:text>
                  <xsl:value-of select="."/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:text>'</xsl:text>
                  <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.-->
                  <xsl:value-of select="replace(.,&#34;'&#34;,&#34;''&#34;)"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:value-of select="$result"/>
   </xsl:template>
   <xsl:template match="d:Instruction[descendant::d:ConditionalText[d:Expression] and not(ancestor::d:ComputationItem)]"
                 mode="iatddi:get-conditionned-text"
                 priority="1">
      <xsl:variable name="condition">
         <xsl:copy-of select="descendant::d:ConditionalText"/>
      </xsl:variable>
      <xsl:variable name="text">
         <xsl:value-of select="il:serialize(descendant::d:LiteralText/d:Text/node())"/>
      </xsl:variable>
      <xsl:variable name="result">
         <xsl:text>concat(''</xsl:text>
         <xsl:for-each select="tokenize($text,'ø')[not(.='')]">
            <xsl:text>,</xsl:text>
            <xsl:choose>
               <xsl:when test="contains($condition/d:ConditionalText/d:Expression/r:Command/r:CommandContent/text(),.)">
                  <xsl:text>instance('fr-form-instance')//</xsl:text>
                  <xsl:value-of select="."/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:text>'</xsl:text>
                  <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.-->
                  <xsl:value-of select="replace(.,&#34;'&#34;,&#34;''&#34;)"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:value-of select="$result"/>
   </xsl:template>
   <xsl:template match="d:Instruction[descendant::d:ConditionalText[d:Expression] and ancestor::d:ComputationItem]"
                 mode="iatddi:get-conditionned-text-bis"
                 priority="1">
      <xsl:variable name="condition">
         <xsl:copy-of select="descendant::d:ConditionalText"/>
      </xsl:variable>
      <xsl:variable name="text">
         <xsl:value-of select="il:serialize(descendant::d:LiteralText/d:Text/node())"/>
      </xsl:variable>
      <xsl:variable name="result">
         <xsl:text>concat(''</xsl:text>
         <xsl:for-each select="tokenize($text,'ø')[not(.='')]">
            <xsl:text>,</xsl:text>
            <xsl:choose>
               <xsl:when test="contains($condition/d:ConditionalText/d:Expression/r:Command/r:CommandContent/text(),.)">
                  <xsl:text>instance('fr-form-instance')//</xsl:text>
                  <xsl:value-of select="."/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:text>'</xsl:text>
                  <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.-->
                  <xsl:value-of select="replace(.,&#34;'&#34;,&#34;''&#34;)"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:value-of select="$result"/>
   </xsl:template>
   <!--Linking the Form driver to the instrument-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Modele']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('Form',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the Module driver to d:Sequence elements with Module type-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Module']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('Module',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the Submodule driver to d:Sequence elements with Paragraph type-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Paragraphe']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('SubModule',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Groupe']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('Group',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-output to d:Instruction elements that are comments-->
   <xsl:template match="d:Instruction[not(ancestor::r:QuestionReference)]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-output',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:StatementItem" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-output',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking xf-input driver to d:QuestionItem elements with numeric or text answer-->
   <xsl:template match="d:QuestionItem[d:TextDomain[not(@maxLength) or number(@maxLength)&lt;250] or d:DateTimeDomain or d:DateTimeDomainReference or d:NumericDomain or d:NumericDomainReference]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:TextDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and (not(@maxLength) or number(@maxLength)&lt;250)]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:CodeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and r:ResponseCardinality[@maximumResponses='1'] and not(r:GenericOutputFormat/text()='caseacocher')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-select1',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:CodeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and r:ResponseCardinality[@maximumResponses='1'] and r:GenericOutputFormat/text()='caseacocher']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:CodeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(r:ResponseCardinality)]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NominalDomain]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomain[parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:TextDomain[@maxLength and not(number(@maxLength)&lt;250)]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-textarea',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:TextDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and (@maxLength and not(number(@maxLength)&lt;250))]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-textarea',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking xf-select1 driver to d:QuestionItem elements when there is only one possible answer with d :CodeDomain type-->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:ResponseCardinality[@maximumResponses='1'] and not(r:GenericOutputFormat/text()='caseacocher')]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-select1',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:ResponseCardinality[@maximumResponses='1'] and r:GenericOutputFormat/text()='caseacocher']]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking xf-select driver to d:QuestionItem elements when there are multiple possible answers with d :CodeDomain type-->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:ResponseCardinality[not(@maximumResponses='1')]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking xf-select driver to d:QuestionItem elements when there is no information about the number of possible answer with d :CodeDomain type-->
   <xsl:template match="d:QuestionItem[d:CodeDomain[not(r:ResponseCardinality)]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:NominalDomain[ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-item driver to the l:Code elements that will correspond to the answer's modality (and only those)-->
   <xsl:template match="l:Code[ancestor::r:CodeListReference[parent::d:CodeDomain[parent::d:QuestionItem or parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-item',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="l:Code[parent::r:CodeReference and ancestor::d:NominalDomain]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-item',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking ResponseElement to a variable-->
   <xsl:template match="l:Variable[not(r:QuestionReference or r:SourceParameterReference)]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('ResponseElement',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="r:Label[parent::l:CodeList/ancestor::d:GridDimension]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('TextCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="l:Code[ancestor::d:GridDimension]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('TextCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:GridResponseDomain[ancestor::d:QuestionGrid]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('Cell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:NoDataByDefinition[ancestor::d:QuestionGrid]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('EmptyCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:Sequence[(parent::d:ThenConstructReference or parent::d:ElseConstructReference) and not(descendant::d:TypeOfSequence[text()='Module'])]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-group',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:StructuredMixedResponseDomain]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('MultipleQuestion',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and r:DateFieldFormat/text()='HH:CH']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('DoubleDuration',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(r:DateFieldFormat/text()='HH:CH')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:DateTimeDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(r:DateFieldFormat/text()='HH:CH')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:StructuredMixedGridResponseDomain[parent::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('RowLoop',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('TableLoop',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionGrid[not(d:GridDimension/d:Roster[not(@maximumAllowed)])]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="il:append-empty-element('Table',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the DDI element label sending function to the form title getter function-->
   <xsl:function name="iatfr:get-form-title">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-label($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI languages getter function to the form languages getter function-->
   <xsl:function name="iatfr:get-form-languages">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-languages($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the application name getter function-->
   <xsl:function name="iatfr:get-application-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-id($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the form name getter function-->
   <xsl:function name="iatfr:get-form-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-id($context)"/>
   </xsl:function>
   <xsl:function name="iatfr:get-form-description">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:text/>
   </xsl:function>
   <xsl:function name="iatfr:get-default-value">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the Xforms element name getter function-->
   <xsl:function name="iatfr:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-id($context)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="iatfr:get-relevant">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-cachable($context)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="iatfr:get-readonly">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-grisable($context)"/>
   </xsl:function>
   <xsl:function name="iatfr:get-required">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="iatfr:get-calculate">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-link($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-calculate-label">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-conditionned-text($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-calculate-alert">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-conditionned-text-bis($context)"/>
   </xsl:function>
   <!--Used to specify the character string to identify as xf:date-->
   <xsl:function name="iatfr:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-type($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-constraint">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-control($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-number-of-decimals">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-number-of-decimals($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-alert-level">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-message-type($context)"/>
   </xsl:function>
   <!--Linking the DDI Instruction (Help Type) getter function to the Xforms help element getter function-->
   <xsl:function name="iatfr:get-help">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-help-instruction($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI Instruction (Hint Type) getter function to the Xforms hint element getter function-->
   <xsl:function name="iatfr:get-hint">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-hint-instruction($context,$language)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="iatfr:get-alert">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-message($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI element label sending function to the Xforms elements label getter function-->
   <xsl:function name="iatfr:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-label($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI code value sending function to the Xforms item value getter function-->
   <xsl:function name="iatfr:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-value($context)"/>
   </xsl:function>
   <!--Linking the DDI code list representation format sending function to the Xforms list appearance getter function-->
   <xsl:function name="iatfr:get-appearance">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-output-format($context)"/>
   </xsl:function>
   <!--Linking the DDI style sending function to the css class getter function-->
   <xsl:function name="iatfr:get-css-class">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-style($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-format($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-length($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-suffix">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="iatddi:get-suffix($context,$language)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-header-columns">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-levels-first-dimension($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-header-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-levels-second-dimension($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-body-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-codes-first-dimension($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-header-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="iatddi:get-title-line($context,$index)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-body-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="iatddi:get-table-line($context,$index)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-rowspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-rowspan($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-colspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-colspan($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-minimum-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-minimum-required($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-constraint-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-computation-items($context)"/>
   </xsl:function>
   <!---->
   <xsl:function name="iatfr:get-relevant-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="iatddi:get-then($context)"/>
   </xsl:function>
   <!--Variables become children of the instrument (that represent the start of the Xforms form)-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='Modele']"
                 mode="iat:child-fields"
                 as="node()*">
      <xsl:sequence select="* | ancestor::DDIInstance//l:VariableScheme"/>
   </xsl:template>
</xsl:stylesheet>
