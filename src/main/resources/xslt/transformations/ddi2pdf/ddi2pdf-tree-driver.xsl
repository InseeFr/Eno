<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:eno="http://xml.insee.fr/apps/eno"
                xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
                xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
                xmlns:enoddi2fr="http://xml.insee.fr/apps/eno/ddi2form-runner"
                xmlns:d="ddi:datacollection:3_2"
                xmlns:r="ddi:reusable:3_2"
                xmlns:l="ddi:logicalproduct:3_2"
                version="2.0"><!-- Importing the different resources --><xsl:import href="../../inputs/ddi/source.xsl"/>
   <xsl:import href="../../outputs/fr/modelsListeDriver.xsl"/>
   <xsl:import href="../../lib.xsl"/>
   <xd:doc scope="stylesheet">
      <xd:desc>
         <xd:p>This stylesheet is used to transform a DDI input into an Xforms form (containing orbeon form runner adherences).</xd:p>
      </xd:desc>
   </xd:doc>
   <!-- The output file generated will be xml type --><xsl:output method="xml" indent="yes" encoding="UTF-8"/>
   <xsl:strip-space elements="*"/>
   <xd:doc>
      <xd:desc>
         <xd:p>The parameter file used by the stylesheet.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:param name="parameters-file"/>
   <xd:doc>
      <xd:desc>
         <xd:p>The parameters are charged as an xml tree.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:variable name="parameters" select="doc($parameters-file)"/>
   <xd:doc>
      <xd:desc>
         <xd:p>The folder containing label resources in different languages.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:param name="labels-folder"/>
   <xd:doc>
      <xd:desc>
         <xd:p>A variable is created to build a set of label resources in different languages.</xd:p>
         <xd:p>Only the resources in languages already present in the DDI input are charged.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:variable name="labels-resource">
      <xsl:sequence select="eno:build-labels-resource($labels-folder,enofr:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"/>
   </xsl:variable>
   <xd:doc>
      <xd:desc>
         <xd:p>Root template :</xd:p>
         <xd:p>The transformation starts with the main Sequence.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="/">
      <xsl:apply-templates select="//d:Sequence[d:TypeOfSequence/text()='template']" mode="source"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>This xforms function is used to get an xpath corresponding to a dynamic text.</xd:p>
         <xd:p>It can be associated with different modes depending of the text type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-calculate-text">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="text-type"/>
      <xsl:choose>
         <xsl:when test="$text-type='label'">
            <xsl:apply-templates select="$context" mode="enoddi2fr:get-calculate-label"/>
         </xsl:when>
         <xsl:when test="$text-type='alert'">
            <xsl:apply-templates select="$context" mode="enoddi2fr:get-calculate-alert"/>
         </xsl:when>
      </xsl:choose>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Instructions having a ConditionalText are matched for this mode only if there aren't inside a ComputationItem.</xd:p>
         <xd:p>But the treatment (using the enoddi2fr:get-calculate-text mode) is the same whether they are or are not inside a ComputationItem.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Instruction[descendant::d:ConditionalText and not(ancestor::d:ComputationItem)]"
                 mode="enoddi2fr:get-calculate-label">
      <xsl:apply-templates select="." mode="enoddi2fr:get-calculate-text"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Instruction having a ConditionalText are matched for this mode only if there are inside a ComputationItem.</xd:p>
         <xd:p>But the treatment (using the enoddi2fr:get-calculate-text mode) is the same whether they are or are not inside a ComputationItem.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Instruction[descendant::d:ConditionalText and ancestor::d:ComputationItem]"
                 mode="enoddi2fr:get-calculate-alert">
      <xsl:apply-templates select="." mode="enoddi2fr:get-calculate-text"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>This mode is only used on Instructions having a ConditionalText.</xd:p>
         <xd:p>Each dynamic string of the conditional text is surrounded by 'ø' characters.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Instruction[descendant::d:ConditionalText]"
                 mode="enoddi2fr:get-calculate-text"
                 priority="1">
      <xsl:variable name="condition">
         <xsl:copy-of select="descendant::d:ConditionalText"/>
      </xsl:variable>
      <xsl:variable name="text">
         <xsl:value-of select="eno:serialize(descendant::d:LiteralText/d:Text/node())"/>
      </xsl:variable>
      <!-- The result is an xpath concat of different values --><xsl:variable name="result">
         <xsl:text>concat(''</xsl:text>
         <!-- The conditional text is split with the character 'ø' --><xsl:for-each select="tokenize($text,'ø')[not(.='')]">
            <xsl:text>,</xsl:text>
            <xsl:choose><!-- If the split string has a match in the following elements, it means it is a dynamic part of the text, it will be in the instance of the generated xforms --><xsl:when test=".=$condition/d:ConditionalText/r:SourceParameterReference/r:OutParameter/r:ID/text()                         or contains($condition/d:ConditionalText/d:Expression/r:Command/r:CommandContent/text(),.)">
                  <xsl:text>instance('fr-form-instance')//</xsl:text>
                  <xsl:value-of select="."/>
               </xsl:when>
               <!-- if not, it's a static text which is returned --><xsl:otherwise>
                  <xsl:text>'</xsl:text>
                  <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.--><xsl:value-of select="replace(.,'''','''''')"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:text>)</xsl:text>
      </xsl:variable>
      <xsl:value-of select="$result"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>This function returns an xforms hint for the context on which it is applied.</xd:p>
         <xd:p>It uses different DDI functions to do this job.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-hint">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <!-- We look for an instruction of 'Format' type --><xsl:variable name="format-instruction">
         <xsl:sequence select="enoddi:get-format-instruction($context,$language)"/>
      </xsl:variable>
      <xsl:choose><!-- If there is no such instruction --><xsl:when test="not($format-instruction/*)"><!-- We look for the container of the element --><xsl:variable name="question-type">
               <xsl:value-of select="enoddi:get-container($context)"/>
            </xsl:variable>
            <!-- If it is a grid we do not want the hint to be displayed for n fields. If it is a question, we can display this info --><xsl:if test="$question-type='question'">
               <xsl:variable name="type">
                  <xsl:value-of select="enoddi:get-type($context)"/>
               </xsl:variable>
               <!-- If it is number, we display this hint --><xsl:if test="$type='number'">
                  <xsl:value-of select="concat($labels-resource/Languages/Language[@xml:lang=$language]/Hint/Number,enoddi:get-maximum($context))"/>
               </xsl:if>
               <!-- If it is a date, we display this hint --><xsl:if test="$type='date'">
                  <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Hint/Date"/>
               </xsl:if>
            </xsl:if>
         </xsl:when>
         <!-- If there is such an instruction, it is used for the hint xforms element --><xsl:when test="$format-instruction/*">
            <xsl:sequence select="$format-instruction/*"/>
         </xsl:when>
      </xsl:choose>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>This function returns an xforms alert for the context on which it is applied.</xd:p>
         <xd:p>It uses different DDI functions to do this job.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-alert">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <!-- We look for a 'message' --><!-- 02-21-2017 : this function is only called for an Instruction in a ComputationItem on the DDI side --><xsl:variable name="message">
         <xsl:sequence select="enoddi:get-consistency-message($context,$language)"/>
      </xsl:variable>
      <xsl:choose><!-- if there is no such message --><xsl:when test="not($message/node())"><!-- We retrieve the question type --><xsl:variable name="type">
               <xsl:value-of select="enoddi:get-type($context)"/>
            </xsl:variable>
            <!-- We retrieve the format --><xsl:variable name="format">
               <xsl:value-of select="enoddi:get-format($context)"/>
            </xsl:variable>
            <!-- If it is a 'text' and a format is defined, we use a generic sentence as an alert --><xsl:if test="$type='text'">
               <xsl:if test="not($format='')">
                  <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Text"/>
               </xsl:if>
            </xsl:if>
            <!-- If it is a number, we look for infos about the format and deduce a message for the alert element --><xsl:if test="$type='number'">
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
                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/Beginning"/>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Integer"/>
                     </xsl:otherwise>
                  </xsl:choose>
               </xsl:variable>
               <xsl:variable name="end">
                  <xsl:choose>
                     <xsl:when test="not($number-of-decimals='' or $number-of-decimals='0')">
                        <xsl:value-of select="' '                                     ,concat($labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/DecimalCondition                                     ,' '                                     ,$number-of-decimals                                     ,' '                                     ,$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/Digit                                     ,if (number($number-of-decimals)&gt;1) then $labels-resource/Languages/Language[@xml:lang=$language]/Plural else ''                                     ,' '                                     ,$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Number/Decimal/End)"/>
                     </xsl:when>
                  </xsl:choose>
               </xsl:variable>
               <xsl:value-of select="concat($beginning,' ',$minimum, ' ',$labels-resource/Languages/Language[@xml:lang=$language]/And,' ',$maximum, $end)"/>
            </xsl:if>
            <!-- If it is a 'date', we use a generic sentence as an alert --><xsl:if test="$type='date'">
               <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Date"/>
            </xsl:if>
            <!-- In those cases, we use specific messages as alert messages --><xsl:if test="$type='duration'">
               <xsl:if test="$format='hh'">
                  <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Duration/Hours"/>
               </xsl:if>
               <xsl:if test="$format='mm'">
                  <xsl:value-of select="$labels-resource/Languages/Language[@xml:lang=$language]/Alert/Duration/Minutes"/>
               </xsl:if>
            </xsl:if>
         </xsl:when>
         <xsl:otherwise>
            <xsl:sequence select="$message/node()"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>This function retrieves the languages to appear in the generated Xforms.</xd:p>
         <xd:p>Those languages can be specified in a parameters file on a questionnaire level.</xd:p>
         <xd:p>If not, it will get the languages defined in the DDI input.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-form-languages">
      <xsl:param name="context" as="item()"/>
      <xsl:choose>
         <xsl:when test="$parameters/Parameters/Languages">
            <xsl:for-each select="$parameters/Parameters/Languages/Language">
               <xsl:value-of select="."/>
            </xsl:for-each>
         </xsl:when>
         <xsl:otherwise>
            <xsl:sequence select="enoddi:get-languages($context)"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>The main Sequence activates the higher driver 'Form'.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='template']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Form',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The 'module' Sequence activates the 'Module' driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='module']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Module',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The 'submodule' Sequence activates the 'SubModule' driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='submodule']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('SubModule',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The 'group' Sequence activates the 'Group' driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='group']" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Group',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those Sequence elements activate the xf-group driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Sequence[(parent::d:ThenConstructReference or parent::d:ElseConstructReference) and not(descendant::d:TypeOfSequence[text()='module'])]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-group',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>A StatementItem activates the xf-output driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:StatementItem" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-output',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Most Instruction elements activates the xf-output driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Instruction[not(ancestor::r:QuestionReference) and not(d:InstructionName/r:String/text()='tooltip')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-output',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those DateTimeDomain elements (not of the HH:CH type) activate the xf-input driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:DateTimeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(r:DateFieldFormat/text()='HH:CH')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those DateTimeDomainReference elements (not of the HH:CH type) activate the xf-input driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:DateTimeDomainReference[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(r:DateFieldFormat/text()='HH:CH')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The NumericDomain element activates the xf-input driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:NumericDomain[parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The NumericDomainReference element activates the xf-input driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:NumericDomainReference[parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those QuestionItem elements activate the xf-input driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem[d:TextDomain[not(@maxLength) or number(@maxLength)&lt;250] or d:DateTimeDomain or d:DateTimeDomainReference or d:NumericDomain or d:NumericDomainReference]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those TextDomain (250 chars max excluded) activate the xf-input driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:TextDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and (not(@maxLength) or number(@maxLength)&lt;250)]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-input',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those QuestionItem elements (with a TextDomain of 250 chars min) activate the xf-textarea driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem[d:TextDomain[@maxLength and not(number(@maxLength)&lt;250)]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-textarea',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those TextDomain elements (250 chars min) activate the xf-textarea driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:TextDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and (@maxLength and not(number(@maxLength)&lt;250))]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-textarea',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those CodeDomain elements (only one response and not of checkbox type) activate the xf-select1 driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:CodeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and r:ResponseCardinality[@maximumResponses='1'] and not(r:GenericOutputFormat/text()='checkbox')]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select1',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those QuestionItem elements (with a CodeDomain with only one response and not of checkbox type) activate the xf-select1 driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:ResponseCardinality[@maximumResponses='1'] and not(r:GenericOutputFormat/text()='checkbox')]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select1',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those CodeDomain elements (only one response and of checkbox type) activate the xf-select driver. It does seem odd but there is some dynamic behaviour added to prevent checking more than one box.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:CodeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and r:ResponseCardinality[@maximumResponses='1'] and r:GenericOutputFormat/text()='checkbox']"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those QuestionItem elements (with CodeDomain with only one response and of checkbox type) activate the xf-select driver. It does seem odd but there is some dynamic behaviour added to prevent checking more than one box.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:ResponseCardinality[@maximumResponses='1'] and r:GenericOutputFormat/text()='checkbox']]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those CodeDomain elements (no ResponseCardinality) activate the xf-select driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:CodeDomain[(parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed) and not(r:ResponseCardinality)]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those QuestionItem elements (with CodeDomain with no ResponseCardinality) activate the xf-select driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem[d:CodeDomain[not(r:ResponseCardinality)]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The NominalDomain element activates the xf-select driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:NominalDomain[ancestor::d:GridResponseDomain or ancestor::d:ResponseDomainInMixed]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The QuestionItem element activates the xf-select driver when it has a NominalDomain response.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem[d:NominalDomain]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those QuestionItem activate the xf-select driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem[d:CodeDomain[r:ResponseCardinality[not(@maximumResponses='1')]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-select',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those Code elements activate the xf-item driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="l:Code[ancestor::r:CodeListReference[parent::d:CodeDomain[parent::d:QuestionItem or parent::d:GridResponseDomain or parent::d:ResponseDomainInMixed]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-item',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those Code elements activate the xf-item driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="l:Code[parent::r:CodeReference and ancestor::d:NominalDomain]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-item',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those Code elements activate the TextCell driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="l:Code[ancestor::d:GridDimension]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('TextCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those Label elements activate the TextCell driver. It is placed at the top left of the Grid.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="r:Label[parent::l:CodeList[not(ancestor::l:CodeList) and ancestor::d:GridDimension[@rank='1' and ../d:GridDimension[@rank='2']]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('TextCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those Label elements activate the TextCell driver. It is used as a header for the l:Code of the CodeList.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="r:Label[parent::l:CodeList/ancestor::l:CodeList/ancestor::d:GridDimension]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('TextCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those GridResponseDomain elements activate the Cell driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:GridResponseDomain[ancestor::d:QuestionGrid]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Cell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those NoDataByDefinition elements activate the EmptyCell driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:NoDataByDefinition[ancestor::d:QuestionGrid]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('EmptyCell',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The QuestionItem elements with a StructuredMixedResponseDomain activates the MultipleQuestion driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem[d:StructuredMixedResponseDomain]" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('MultipleQuestion',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those DateTimeDomain elements (of the HH:CH type) activate the DoubleDuration driver. This driver is to be rethought (see issue 49 on github).</xd:p>
      </xd:desc>
   </xd:doc>
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
   <xd:doc>
      <xd:desc>
         <xd:p>Those d:QuestionGrid aren't fixed, it is possible to add rows, they activate the 'TableLoop' driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('TableLoop',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those d:QuestionGrid aren't fixed, it is possible to add rows. The d:StructuredMixedGridResponseDomain activate a RowLoop driver to do so.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:StructuredMixedGridResponseDomain[parent::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('RowLoop',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those d:QuestionGrid are 'fixed', they activate the Table driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionGrid[not(d:GridDimension/d:Roster[not(@maximumAllowed)])]"
                 mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('Table',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The d:Loop element activates the QuestionLoop driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Loop" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('QuestionLoop',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Those Variables (which aren't linked to a question) activate the ResponseElement driver. They are part of the Xforms instance but with no associated field.</xd:p>
      </xd:desc>
   </xd:doc>
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
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-form-title to input function enoddi:get-label.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-form-title">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-application-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-application-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-form-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-form-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-relevant to input function enoddi:get-hideable-command.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-relevant">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-command($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-readonly to input function enoddi:get-deactivatable-command.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-readonly">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-command($context)"/>
   </xsl:function>
   <xsl:function name="enofr:get-required">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-calculate to input function enoddi:get-variable-calculation.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-calculate">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-variable-calculation($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-type to input function enoddi:get-type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-type($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-format to input function enoddi:get-format.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-format($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-constraint to input function enoddi:get-control.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-constraint">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-control($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-alert-level to input function enoddi:get-message-type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-alert-level">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-message-type($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-help to input function enoddi:get-help-instruction.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-help">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-help-instruction($context,$language)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-label to input function enoddi:get-label.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-value to input function enoddi:get-value.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-value($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-appearance to input function enoddi:get-output-format.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-appearance">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-output-format($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-css-class to input function enoddi:get-style.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-css-class">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-style($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-length to input function enoddi:get-length.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-length($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-suffix to input function enoddi:get-suffix.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-suffix">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-suffix($context,$language)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-header-columns to input function enoddi:get-levels-first-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-header-columns">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-first-dimension($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-header-lines to input function enoddi:get-levels-second-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-header-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-second-dimension($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-body-lines to input function enoddi:get-codes-first-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-body-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-codes-first-dimension($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-header-line to input function enoddi:get-title-line.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-header-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-title-line($context,$index)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-body-line to input function enoddi:get-table-line.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-body-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-table-line($context,$index)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-rowspan to input function enoddi:get-rowspan.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-rowspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-rowspan($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-colspan to input function enoddi:get-colspan.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-colspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-colspan($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-minimum-lines to input function enoddi:get-minimum-required.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-minimum-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-minimum-required($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-constraint-dependencies to input function enoddi:get-computation-items.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-constraint-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-computation-items($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-relevant-dependencies to input function enoddi:get-hideable-then.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-relevant-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-then($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-readonly-dependencies to input function enoddi:get-deactivatable-then.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-readonly-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-then($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enofr:get-code-depth to input function enoddi:get-level-number.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enofr:get-code-depth">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-level-number($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>VariableScheme becomes child of the main sequence (which starts the creation of the Xforms) because some Variables are used to create elements into the generated Xforms.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='template']"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="* | ancestor::DDIInstance//l:VariableScheme"/>
   </xsl:template>
</xsl:stylesheet>
