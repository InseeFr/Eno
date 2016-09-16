<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!--Starting with d:Instrument-->
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
      <xsl:value-of select="concat(parent::d:QuestionGrid/r:ID,'-RowLoop')"/>
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
      <xsl:apply-templates select="r:Label" mode="lang-choice"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:StatementItem" mode="iatddi:get-label">
      <xsl:apply-templates select="d:DisplayText/d:LiteralText/d:Text" mode="lang-choice"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:Instruction[not(ancestor::d:ComputationItem)]"
                 mode="iatddi:get-label">
      <xsl:apply-templates select="d:InstructionText/d:LiteralText/d:Text" mode="lang-choice"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:ResponseDomainInMixed" mode="iatddi:get-label">
      <xsl:apply-templates select="child::node()/r:Label" mode="lang-choice"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[not(descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')])]"
                 mode="iatddi:get-label">
      <xsl:apply-templates select="d:QuestionText/d:LiteralText/d:Text" mode="lang-choice"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionGrid[not(descendant::d:Instruction[not(d:InstructionName/r:String/text()='Format')])]"
                 mode="iatddi:get-label">
      <xsl:apply-templates select="d:QuestionText/d:LiteralText/d:Text" mode="lang-choice"/>
   </xsl:template>
   <!---->
   <xsl:template match="l:Code" mode="iatddi:get-label">
      <xsl:apply-templates select="r:CategoryReference/l:Category/r:Label" mode="lang-choice"/>
   </xsl:template>
   <!---->
   <xsl:template match="*[ends-with(name(),'Domain') and parent::d:ResponseDomainInMixed]"
                 mode="iatddi:get-label">
      <xsl:apply-templates select="r:Label" mode="lang-choice"/>
   </xsl:template>
   <!---->
   <xsl:template match="l:Code" mode="iatddi:get-value">
      <xsl:value-of select="r:Value"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:GenericOutputFormat/text()='boutonradio']]"
                 mode="iatddi:get-output-format">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:GenericOutputFormat/text()='listederoulante']]"
                 mode="iatddi:get-output-format">
      <xsl:value-of select="string('minimal')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:GenericOutputFormat/text()='caseacocher']]"
                 mode="iatddi:get-output-format">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NominalDomain]" mode="iatddi:get-output-format">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:CodeDomain[(ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed) and r:GenericOutputFormat/text()='boutonradio']"
                 mode="iatddi:get-output-format">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:CodeDomain[(ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed) and r:GenericOutputFormat/text()='listederoulante']"
                 mode="iatddi:get-output-format">
      <xsl:value-of select="string('minimal')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:CodeDomain[(ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed) and r:GenericOutputFormat/text()='caseacocher']"
                 mode="iatddi:get-output-format">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NominalDomain[ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed]"
                 mode="iatddi:get-output-format">
      <xsl:value-of select="string('full')"/>
   </xsl:template>
   <!---->
   <xsl:template match="l:Code[.//r:Description]" mode="iatddi:get-help-instruction">
      <xsl:apply-templates select="r:CategoryReference/l:Category/r:Description"
                           mode="lang-choice"/>
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
      <xsl:value-of select="string('question simple-grid')"/>
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
                 mode="iatddi:get-number-of-decimals">
      <xsl:value-of select="@decimalPositions"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomain]"
                 mode="iatddi:get-number-of-decimals">
      <xsl:value-of select="d:NumericDomain/@decimalPositions"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:NumericDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and descendant::r:ManagedNumericRepresentation]"
                 mode="iatddi:get-number-of-decimals">
      <xsl:value-of select="r:ManagedNumericRepresentation/@decimalPositions"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:QuestionItem[d:NumericDomainReference and descendant::r:ManagedNumericRepresentation]"
                 mode="iatddi:get-number-of-decimals">
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
                 mode="iatddi:get-message-type">
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
      <xsl:apply-templates select="." mode="lang-choice"/>
   </xsl:template>
   <!---->
   <xsl:template match="d:StructuredMixedGridResponseDomain[parent::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]]"
                 mode="iatddi:get-minimum-required">
      <xsl:value-of select="../d:GridDimension/d:Roster/@minimumRequired"/>
   </xsl:template>
</xsl:stylesheet>
