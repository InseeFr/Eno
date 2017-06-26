<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!--Linking the Form driver to the instrument-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='template']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Form',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the Module driver to d:Sequence elements with module type-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='module']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Module',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the Submodule driver to d:Sequence elements with submodule type-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='submodule']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('SubModule',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the Group driver to d:Sequence elements with group type-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='group']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Group',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-output driver to d:Instruction elements that are comments-->
   <xsl:template match="d:Instruction[not(ancestor::r:QuestionReference) and not(d:InstructionName/r:String/text()='tooltip')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-output',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-output driver to d :StatementItem elements-->
   <xsl:template match="d:StatementItem" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-output',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-input driver to d:QuestionItem elements with numeric or text answer-->
   <xsl:template match="d:QuestionItem[d:TextDomain[not(@maxLength) or number(@maxLength)&lt;250] or d:DateTimeDomain or d:DateTimeDomainReference or d:NumericDomain or d:NumericDomainReference]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-input driver to d:TextDomain elements having a d:GridResponseDomain or d:ResponseDomainInMixed parent and either no @maxLength attribute, or a @maxLength attribute < 250-->
   <xsl:template match="d:TextDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and (not(@maxLength) or number(@maxLength)&lt;250)]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-select1 driver to d:CodeDomain elements having a r:ResponseCardinality/@maximumResponses=1 and not a 'checkbox' type r:GenericOutputFormat-->
   <xsl:template match="d:CodeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and r:ResponseCardinality[@maximumResponses='1'] and not(r:GenericOutputFormat/text()='checkbox')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select1',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-select driver to d:CodeDomain elements having a r:ResponseCardinality/@maximumResponses=1 and a 'checkbox' type r:GenericOutputFormat-->
   <xsl:template match="d:CodeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and r:ResponseCardinality[@maximumResponses='1'] and r:GenericOutputFormat/text()='checkbox']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-select driver to d:CodeDomain elements not having a r:ResponseCardinality child-->
   <xsl:template match="d:CodeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(r:ResponseCardinality)]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-select driver to d:QuestionItem having a d:NominalDomain child-->
   <xsl:template match="d:QuestionItem[d:NominalDomain]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-input driver to d:NumericDomain elements having a d:GridResponseDomain or d:ResponseDomainInMixed parent-->
   <xsl:template match="d:NumericDomain[parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-input driver to d:NumericDomainReference elements having a d:GridResponseDomain or a d:ResponseDomainInMixed parent-->
   <xsl:template match="d:NumericDomainReference[parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-textarea driver to d:QuestionItem elements having a d:TextDomain child with a @maxLength attribute > 250-->
   <xsl:template match="d:QuestionItem[d:TextDomain[@maxLength and not(number(@maxLength)&lt;250)]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-textarea',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-textarea driver to d:TextDomain elements with a @maxLength attribute > 250 -->
   <xsl:template match="d:TextDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and (@maxLength and not(number(@maxLength)&lt;250))]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-textarea',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking xf-select1 driver to d:QuestionItem elements when there is only one possible answer with d :CodeDomain type-->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:ResponseCardinality[@maximumResponses='1'] and not(r:GenericOutputFormat/text()='checkbox')]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select1',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-select driver to d:QuestionItem when there is only one possible answer and the r:GenericOutPutFormat is a 'checkbox' type-->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:ResponseCardinality[@maximumResponses='1'] and r:GenericOutputFormat/text()='checkbox']]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking xf-select driver to d:QuestionItem elements when there are multiple possible answers with d :CodeDomain type-->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:ResponseCardinality[not(@maximumResponses='1')]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking xf-select driver to d:QuestionItem elements when there is no information about the number of possible answer with d :CodeDomain type-->
   <xsl:template match="d:QuestionItem[d:CodeDomain[not(r:ResponseCardinality)]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-select driver to d:NominalDomain which are descendants of d:GridResponseDomain or d:ResponseDomainInMixed-->
   <xsl:template match="d:NominalDomain[ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-item driver to the l:Code elements that will correspond to the answer's modality (and only those)-->
   <xsl:template match="l:Code[ancestor::r:CodeListReference[parent::d:CodeDomain[parent::d:QuestionItem or parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-item',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-item driver to the l:Code elements that have a r:CodeReference parent and a d:NominalDomain ancestor-->
   <xsl:template match="l:Code[parent::r:CodeReference and ancestor::d:NominalDomain]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-item',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking ResponseElement to a variable-->
   <xsl:template match="l:Variable[not(r:QuestionReference or r:SourceParameterReference)]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('ResponseElement',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--The TextCell is at the top-left in 2 dimensions array-->
   <xsl:template match="r:Label[parent::l:CodeList[not(ancestor::l:CodeList) and ancestor::d:GridDimension[@rank='1' and ../d:GridDimension[@rank='2']]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('TextCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--The codeList is referenced by another one, which is a GridDimension-->
   <xsl:template match="r:Label[parent::l:CodeList/ancestor::l:CodeList/ancestor::d:GridDimension]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('TextCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking TextCell to a code-->
   <xsl:template match="l:Code[ancestor::d:GridDimension]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('TextCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking Cell to a d:GridResponseDomain-->
   <xsl:template match="d:GridResponseDomain[ancestor::d:QuestionGrid]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Cell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking EmptyCell to a d:NoDataByDefinition-->
   <xsl:template match="d:NoDataByDefinition[ancestor::d:QuestionGrid]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('EmptyCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-group driver to d:Sequence elements that are not 'module' type Sequence-->
   <xsl:template match="d:Sequence[(parent::d:ThenConstructReference or parent::d:ElseConstructReference) and not(descendant::d:TypeOfSequence[text()='module'])]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-group',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking MultipleQuestion to d:QuestionItem elements having a d:StructuredMixedResponseDomain child-->
   <xsl:template match="d:QuestionItem[d:StructuredMixedResponseDomain]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('MultipleQuestion',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking DoubleDuration to d:DateTimeDomain elements having the 'HH:CH' format-->
   <xsl:template match="d:DateTimeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and r:DateFieldFormat/text()='HH:CH']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('DoubleDuration',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-input driver to d:DateTimeDomain elements not having the 'HH:CH' format-->
   <xsl:template match="d:DateTimeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(r:DateFieldFormat/text()='HH:CH')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking the xf-input driver to d:DateTimeDomainReference not having the 'HH:CH' format-->
   <xsl:template match="d:DateTimeDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(r:DateFieldFormat/text()='HH:CH')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking RowLoop to d:StructuredMixedGridResponseDomain not having a @maximumAllowed attribute in its d:Roster descendant-->
   <xsl:template match="d:StructuredMixedGridResponseDomain[parent::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('RowLoop',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking TableLoop to d:QuestionGrid elements not having a @maximumAllowed attribute in its d :Roster descendant-->
   <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('TableLoop',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!--Linking Table to d:QuestionGrid elements not having a @maximumAllowed attribute in its d :Roster descendant-->
   <xsl:template match="d:QuestionGrid[not(d:GridDimension/d:Roster[not(@maximumAllowed)])]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Table',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <!---->
   <xsl:template match="d:Loop" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('QuestionLoop',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
</xsl:stylesheet>
