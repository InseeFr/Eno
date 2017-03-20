<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:eno="http://xml.insee.fr/apps/eno"
                xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
                xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
                xmlns:d="ddi:datacollection:3_2"
                xmlns:r="ddi:reusable:3_2"
                xmlns:l="ddi:logicalproduct:3_2"
                version="2.0"><!-- Base file of the upcoming ddi2fr.xsl stylesheet (that will be used in the ddi2fr target to create basic-form.tmp) --><!-- Importing the different files used in the process --><!-- source.xsl : the xsl created by merging inputs/ddi/functions.xsl, source-fixed.xsl and templates.xsl --><!-- models.xsl : Orbeon related transformations --><!-- lib.xsl : used to parse a file with defined constraints -->
   <xsl:import href="../../inputs/ddi/source.xsl"/>
   <xsl:import href="../../outputs/fr/models.xsl"/>
   <xsl:import href="../../lib.xsl"/>
   <!-- The output file generated will be xml type -->
   <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
   <xsl:strip-space elements="*"/>
   <xd:doc scope="stylesheet">
      <xd:desc>
         <xd:p>Transforms DDI into Orbeon Form Runner!</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="/">
      <xsl:apply-templates select="/" mode="source"/>
   </xsl:template>
   <!-- Getting this here, actually dependent of the input and output language -->
   <!-- Getting conditionned text for d:Instruction elements having a r:SourceParameterReference descendant and no  -->
   <!-- d:ComputationItem ancestor  -->
   <xsl:template match="d:Instruction[descendant::d:ConditionalText[r:SourceParameterReference] and not(ancestor::d:ComputationItem)]"
                 mode="enoddi:get-conditionned-text"
                 priority="1">
      <xsl:variable name="condition">
         <xsl:copy-of select="descendant::d:ConditionalText"/>
      </xsl:variable>
      <xsl:variable name="text">
         <xsl:value-of select="eno:serialize(descendant::d:LiteralText/d:Text/node())"/>
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
                  <xsl:value-of select="replace(.,'''','''''')"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:value-of select="$result"/>
   </xsl:template>
   <!-- Getting this here, actually dependent of the input and ouput language -->
   <!-- Getting conditionned text for d:Instruction elements having a r:SourceParameterReference descendant and a  -->
   <!-- d:ComputationItem ancestor  -->
   <xsl:template match="d:Instruction[descendant::d:ConditionalText[r:SourceParameterReference] and ancestor::d:ComputationItem]"
                 mode="enoddi:get-conditionned-text-bis"
                 priority="1">
      <xsl:variable name="condition">
         <xsl:copy-of select="descendant::d:ConditionalText"/>
      </xsl:variable>
      <xsl:variable name="text">
         <xsl:value-of select="eno:serialize(descendant::d:LiteralText/d:Text/node())"/>
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
                  <xsl:value-of select="replace(.,'''','''''')"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:value-of select="$result"/>
   </xsl:template>
   <!-- Getting the conditionned text for d:Instruction elements having a d:ConditionalText/d:Expression descendant -->
   <!-- and no d:ComputationItem ancestor -->
   <xsl:template match="d:Instruction[descendant::d:ConditionalText[d:Expression] and not(ancestor::d:ComputationItem)]"
                 mode="enoddi:get-conditionned-text"
                 priority="1">
      <xsl:variable name="condition">
         <xsl:copy-of select="descendant::d:ConditionalText"/>
      </xsl:variable>
      <xsl:variable name="text">
         <xsl:value-of select="eno:serialize(descendant::d:LiteralText/d:Text/node())"/>
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
                  <xsl:value-of select="replace(.,'''','''''')"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:value-of select="$result"/>
   </xsl:template>
   <!-- Getting the conditionned text for d:Instruction elements having a d:ConditionalText/d:Expression descendant -->
   <!-- and a d:ComputationItem ancestor -->
   <xsl:template match="d:Instruction[descendant::d:ConditionalText[d:Expression] and ancestor::d:ComputationItem]"
                 mode="enoddi:get-conditionned-text-bis"
                 priority="1">
      <xsl:variable name="condition">
         <xsl:copy-of select="descendant::d:ConditionalText"/>
      </xsl:variable>
      <xsl:variable name="text">
         <xsl:value-of select="eno:serialize(descendant::d:LiteralText/d:Text/node())"/>
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
                  <xsl:value-of select="replace(.,'''','''''')"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:value-of select="$result"/>
   </xsl:template>
   <!--Linking the Xforms hint getter function to multiple DDI getter functions -->
   <xsl:function name="enofr:get-hint">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <!-- We look for an instruction of 'Format' type -->
      <xsl:variable name="format-instruction">
         <xsl:sequence select="enoddi:get-format-instruction($context,$language)"/>
      </xsl:variable>
      <xsl:choose><!-- If there is no such instruction -->
         <xsl:when test="not($format-instruction/*)"><!-- We look for the container of the element -->
            <xsl:variable name="question-type">
               <xsl:value-of select="enoddi:get-container($context)"/>
            </xsl:variable>
            <!-- If it is a grid we do not want the hint to be displayed for n fields. If it is a question, we can display this info -->
            <xsl:if test="$question-type='question'">
               <xsl:variable name="type">
                  <xsl:value-of select="enoddi:get-type($context)"/>
               </xsl:variable>
               <!-- If it is number, we display this hint -->
               <xsl:if test="$type='number'">
                  <xsl:value-of select="concat('Exemple : ',enoddi:get-maximum($context))"/>
               </xsl:if>
               <!-- If it is a date, we display this hint -->
               <xsl:if test="$type='date'">
                  <xsl:text>Date au format JJ/MM/AAAA</xsl:text>
               </xsl:if>
            </xsl:if>
         </xsl:when>
         <!-- If there is such an instruction, it is used for the hint xforms element -->
         <xsl:when test="$format-instruction/*">
            <xsl:sequence select="$format-instruction/*"/>
         </xsl:when>
      </xsl:choose>
   </xsl:function>
   <!--Linking the Xforms alert getter function to multiple DDI getter functions -->
   <xsl:function name="enofr:get-alert">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <!-- We look for a 'message' -->
      <!-- 02-21-2017 : this function is only called for an Instruction in a ComputationItem on the DDI side -->
      <xsl:variable name="message">
         <xsl:sequence select="enoddi:get-consistency-message($context,$language)"/>
      </xsl:variable>
      <xsl:choose><!-- if there is no such message -->
         <xsl:when test="not($message/node())"><!-- We retrieve the question type -->
            <xsl:variable name="type">
               <xsl:value-of select="enoddi:get-type($context)"/>
            </xsl:variable>
            <!-- We retrieve the format -->
            <xsl:variable name="format">
               <xsl:value-of select="enoddi:get-format($context)"/>
            </xsl:variable>
            <!-- If it is a 'text' and a format is defined, we use a generic sentence as an alert -->
            <xsl:if test="$type='text'">
               <xsl:if test="not($format='')">
                  <xsl:text>Vous devez saisir une valeur correcte</xsl:text>
               </xsl:if>
            </xsl:if>
            <!-- If it is a number, we look for infos about the format and deduce a message for the alert element -->
            <xsl:if test="$type='number'">
               <xsl:variable name="number-of-decimals">
                  <xsl:value-of select="enoddi:get-number-of-decimals($context)"/>
               </xsl:variable>
               <xsl:variable name="minimum">
                  <xsl:value-of select="enoddi:get-minimum($context)"/>
               </xsl:variable>
               <xsl:variable name="maximum">
                  <xsl:value-of select="enoddi:get-maximum($context)"/>
               </xsl:variable>
               <xsl:variable name="beginning">
                  <xsl:choose>
                     <xsl:when test="not($number-of-decimals='' or $number-of-decimals='0')">
                        <xsl:text>Vous devez utiliser le point comme séparateur de décimale, sans espace, et saisir un nombre compris entre</xsl:text>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:text>Vous devez saisir un nombre entier compris entre</xsl:text>
                     </xsl:otherwise>
                  </xsl:choose>
               </xsl:variable>
               <xsl:variable name="end">
                  <xsl:choose>
                     <xsl:when test="not($number-of-decimals='' or $number-of-decimals='0')">
                        <xsl:value-of select="concat('(avec au plus ',$number-of-decimals,' chiffre',if (number($number-of-decimals)&gt;1) then 's' else '',' derrière le séparateur &#34;.&#34;)')"/>
                     </xsl:when>
                  </xsl:choose>
               </xsl:variable>
               <xsl:value-of select="concat($beginning,' ',$minimum, ' et ',$maximum,' ', $end)"/>
            </xsl:if>
            <!-- If it is a 'date', we use a generic sentence as an alert -->
            <xsl:if test="$type='date'">
               <xsl:text>Entrez une date valide</xsl:text>
            </xsl:if>
            <!-- In those cases, we use specific messages as alert messages -->
            <xsl:if test="$type='duration'">
               <xsl:if test="$format='hh'">
                  <xsl:text>Le nombre d'heures doit être compris entre 0 et 99.</xsl:text>
               </xsl:if>
               <xsl:if test="$format='mm'">
                  <xsl:text>Le nombre de minutes doit être compris entre 0 et 59.</xsl:text>
               </xsl:if>
            </xsl:if>
         </xsl:when>
         <xsl:otherwise>
            <xsl:sequence select="$message/node()"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:function>
   <!--Linking the Form driver to the instrument-->
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
   <!--Linking the DDI element label sending function to the form title getter function-->
   <xsl:function name="enofr:get-form-title">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI languages getter function to the form languages getter function-->
   <xsl:function name="enofr:get-form-languages">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-languages($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the application name getter function-->
   <xsl:function name="enofr:get-application-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the form name getter function-->
   <xsl:function name="enofr:get-form-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <!--Linking the DDI element id sender function to the Xforms element name getter function-->
   <xsl:function name="enofr:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="enofr:get-relevant">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-command($context)"/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="enofr:get-readonly">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-command($context)"/>
   </xsl:function>
   <xsl:function name="enofr:get-required">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <!--Not linked yet-->
   <xsl:function name="enofr:get-calculate">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-link($context)"/>
   </xsl:function>
   <!--Linking the DDI element conditionned-text to the calculate label getter function-->
   <xsl:function name="enofr:get-calculate-label">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-conditionned-text($context)"/>
   </xsl:function>
   <!--Linking the DDI element conditionned-text-bis to the calculate alert getter function-->
   <xsl:function name="enofr:get-calculate-alert">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-conditionned-text-bis($context)"/>
   </xsl:function>
   <!--Used to specify the character string to identify as xf:date-->
   <xsl:function name="enofr:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-type($context)"/>
   </xsl:function>
   <!--Linking a Xforms function to a DDI function giving infos about format-->
   <xsl:function name="enofr:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-format($context)"/>
   </xsl:function>
   <!--Linking the DDI element control to the constraint getter function-->
   <xsl:function name="enofr:get-constraint">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-control($context)"/>
   </xsl:function>
   <!--Linking the DDI element message-type to the alert-level getter function-->
   <xsl:function name="enofr:get-alert-level">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-message-type($context)"/>
   </xsl:function>
   <!--Linking the DDI Instruction (Help Type) getter function to the Xforms help element getter function-->
   <xsl:function name="enofr:get-help">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-help-instruction($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI element label sending function to the Xforms elements label getter function-->
   <xsl:function name="enofr:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI code value sending function to the Xforms item value getter function-->
   <xsl:function name="enofr:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-value($context)"/>
   </xsl:function>
   <!--Linking the DDI code list representation format sending function to the Xforms list appearance getter function-->
   <xsl:function name="enofr:get-appearance">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-output-format($context)"/>
   </xsl:function>
   <!--Linking the DDI style sending function to the css class getter function-->
   <xsl:function name="enofr:get-css-class">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-style($context)"/>
   </xsl:function>
   <!--Linking both length getter functions-->
   <xsl:function name="enofr:get-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-length($context)"/>
   </xsl:function>
   <!--Linking both suffix getter functions-->
   <xsl:function name="enofr:get-suffix">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-suffix($context,$language)"/>
   </xsl:function>
   <!--Linking the DDI element levels-first-dimension to the header-columns getter function-->
   <xsl:function name="enofr:get-header-columns">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-first-dimension($context)"/>
   </xsl:function>
   <!--Linking the DDI element levels-second-dimension to the header-lines getter function-->
   <xsl:function name="enofr:get-header-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-second-dimension($context)"/>
   </xsl:function>
   <!--Linking the DDI element codes-first-dimension to the body-lines getter function-->
   <xsl:function name="enofr:get-body-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-codes-first-dimension($context)"/>
   </xsl:function>
   <!--Linking the DDI element title-line to the header-line getter function-->
   <xsl:function name="enofr:get-header-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-title-line($context,$index)"/>
   </xsl:function>
   <!--Linking the DDI element table-line to the body-line getter function-->
   <xsl:function name="enofr:get-body-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-table-line($context,$index)"/>
   </xsl:function>
   <!--Linking both rowspan element getter functions-->
   <xsl:function name="enofr:get-rowspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-rowspan($context)"/>
   </xsl:function>
   <!--Linking both colspan element getter functions-->
   <xsl:function name="enofr:get-colspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-colspan($context)"/>
   </xsl:function>
   <!--Linking the DDI element minimum-required to the minimum-lines getter function-->
   <xsl:function name="enofr:get-minimum-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-minimum-required($context)"/>
   </xsl:function>
   <!--Linking the DDI element computation-items to the constraint-dependencies getter function-->
   <xsl:function name="enofr:get-constraint-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-computation-items($context)"/>
   </xsl:function>
   <!--Linking the DDI element 'then' to the relevant-dependencies getter function-->
   <xsl:function name="enofr:get-relevant-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-then($context)"/>
   </xsl:function>
   <!--Linking the DDI element 'then' to the readonly-dependencies getter function-->
   <xsl:function name="enofr:get-readonly-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-then($context)"/>
   </xsl:function>
   <!--Linking the DDI attribute levelNumber to the code-depth function-->
   <xsl:function name="enofr:get-code-depth">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-level-number($context)"/>
   </xsl:function>
   <!--Variables become children of the instrument (that represent the start of the Xforms form)-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='template']"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="* | ancestor::DDIInstance//l:VariableScheme"/>
   </xsl:template>
</xsl:stylesheet>
