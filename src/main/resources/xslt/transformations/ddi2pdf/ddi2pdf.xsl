<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:eno="http://xml.insee.fr/apps/eno"
                xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
                xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
                xmlns:enoddi2pdf="http://xml.insee.fr/apps/eno/ddi2pdf"
                xmlns:d="ddi:datacollection:3_2"
                xmlns:r="ddi:reusable:3_2"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:l="ddi:logicalproduct:3_2"
                version="2.0"><!-- Importing the different resources --><xsl:import href="../../inputs/ddi/source.xsl"/>
   <xsl:import href="../../outputs/pdf/models.xsl"/>
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
      <xsl:sequence select="eno:build-labels-resource($labels-folder,enopdf:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"/>
   </xsl:variable>
   <xd:doc>
      <xd:desc>
         <xd:p>Characters used to surround variables in conditioned text.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:variable name="conditioning-variable-begin"
                 select="$properties//TextConditioningVariable/ddi/Before"/>
   <xsl:variable name="conditioning-variable-end"
                 select="$properties//TextConditioningVariable/ddi/After"/>
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
         <xd:p>This xforms function is used to get the concatened string corresponding to a dynamic text.</xd:p>
         <xd:p>It is created by calling the static text and making it dynamic.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-calculate-text">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language" as="item()"/>
      <xsl:param name="text-type"/>
      <xsl:variable name="static-text-content">
         <xsl:choose>
            <xsl:when test="$text-type='label'">
               <xsl:sequence select="enoddi:get-label($context,$language)"/>
            </xsl:when>
            <xsl:when test="$text-type='alert'">
               <xsl:sequence select="enoddi:get-consistency-message($context,$language)"/>
            </xsl:when>
         </xsl:choose>
      </xsl:variable>
      <xsl:if test="contains(substring-after($static-text-content,$conditioning-variable-begin),$conditioning-variable-end)">
         <xsl:variable name="condition-variables">
            <conditions>
               <xsl:copy-of select="$context/descendant::d:ConditionalText"/>
            </conditions>
         </xsl:variable>
         <xsl:variable name="calculated-text">
            <xsl:call-template name="enoddi2pdf:calculate-text">
               <xsl:with-param name="text-to-calculate" select="eno:serialize($static-text-content)"/>
               <xsl:with-param name="condition-variables" select="$condition-variables"/>
            </xsl:call-template>
         </xsl:variable>
         <xsl:text>concat(</xsl:text>
         <xsl:value-of select="substring($calculated-text,2)"/>
         <xsl:text>)</xsl:text>
      </xsl:if>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>This recursive template returns the calculated conditional text from the static one.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template name="enoddi2pdf:calculate-text">
      <xsl:param name="text-to-calculate"/>
      <xsl:param name="condition-variables"/>
      <xsl:text>,</xsl:text>
      <xsl:choose>
         <xsl:when test="contains(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)">
            <xsl:text>'</xsl:text>
            <!-- Replacing the single quote by 2 single quotes because a concatenation is made --><!-- We actually need to double the quotes in order not to generate an error in the xforms concat.--><xsl:value-of select="replace(substring-before($text-to-calculate,$conditioning-variable-begin),'''','''''')"/>
            <xsl:text>',</xsl:text>
            <xsl:choose><!-- conditionalText doesn't exist for the element in the DDI structure or it exists and references the variable --><xsl:when test="not($condition-variables//text())">
                  <xsl:text>instance('fr-form-instance')//</xsl:text>
                  <!-- TODO : add the elements that will show which variable to use when it is in a loop --><xsl:value-of select="substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
               </xsl:when>
               <!-- conditionalText exists and references the variable --><xsl:when test="index-of($condition-variables//r:SourceParameterReference/r:OutParameter/r:ID,                         substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)) &gt;0">
                  <xsl:text>instance('fr-form-instance')//</xsl:text>
                  <!-- TODO : add the elements that will show which variable to use when it is in a loop --><xsl:value-of select="substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
               </xsl:when>
               <!-- conditionalText contains the calculation of the variable --><xsl:when test="index-of($condition-variables//d:Expression/r:Command/r:OutParameter/r:ID,                         substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)) &gt;0"><!-- TODO : perhaps to change so that the label includes the calculation, not a temporary variable --><xsl:value-of select="replace(replace(                             $condition-variables//d:Expression/r:Command                                                                         [r:OutParameter/r:ID=substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)]                                                                         /r:CommandContent,                                                       '//','instance(''fr-form-instance'')//'),                                               '\]instance(''fr-form-instance'')',']')"/>
                  <!--                        <xsl:text>instance('fr-form-instance')//</xsl:text>
                        <xsl:value-of select="substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
--></xsl:when>
               <xsl:otherwise><!-- conditionalText exists, but the variable is not in it --><xsl:text>'</xsl:text>
                  <xsl:value-of select="concat($conditioning-variable-begin,                             replace(substring-before(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end),'''',''''''),                             $conditioning-variable-end)"/>
                  <xsl:text>'</xsl:text>
               </xsl:otherwise>
            </xsl:choose>
            <xsl:call-template name="enoddi2pdf:calculate-text">
               <xsl:with-param name="text-to-calculate"
                               select="substring-after(substring-after($text-to-calculate,$conditioning-variable-begin),$conditioning-variable-end)"/>
               <xsl:with-param name="condition-variables" select="$condition-variables"/>
            </xsl:call-template>
         </xsl:when>
         <xsl:otherwise>
            <xsl:text>'</xsl:text>
            <!-- Replacing the single quote by 2 single quotes because a concatenation is made, we actually need to double the quotes in order not to generate an error in the xforms concat.--><xsl:value-of select="replace($text-to-calculate,'''','''''')"/>
            <xsl:text>'</xsl:text>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>This function returns an xforms hint for the context on which it is applied.</xd:p>
         <xd:p>It uses different DDI functions to do this job.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-hint">
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
   <xsl:function name="enopdf:get-alert">
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
   <xsl:function name="enopdf:get-form-languages">
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
   <xsl:function name="enopdf:get-formatted-label">
      <xsl:param name="label" as="item()"/>
      <xsl:param name="language"/>
      <xsl:variable name="tempLabel">
         <xsl:apply-templates select="enoddi:get-label($label,$language)" mode="enopdf:format-label"/>
      </xsl:variable>
      <xsl:sequence select="$tempLabel"/>
   </xsl:function>
   <xsl:template match="*" mode="enopdf:format-label">
      <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
   </xsl:template>
   <xsl:template match="xhtml:p[.//xhtml:br]" mode="enopdf:format-label">
      <xsl:element name="fo:block">
         <xsl:attribute name="linefeed-treatment" select="'preserve'"/>
         <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
      </xsl:element>
   </xsl:template>
   <xsl:template match="*[not(descendant-or-self::xhtml:*)]" mode="enopdf:format-label">
      <xsl:copy-of select="."/>
   </xsl:template>
   <xsl:template match="text()" mode="enopdf:format-label">
      <xsl:copy-of select="normalize-space(.)"/>
   </xsl:template>
   <xsl:template match="xhtml:i" mode="enopdf:format-label">
      <xsl:element name="fo:inline">
         <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
      </xsl:element>
   </xsl:template>
   <xsl:template match="xhtml:b" mode="enopdf:format-label">
      <xsl:element name="fo:inline">
         <xsl:attribute name="font-weight" select="'bold'"/>
         <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
      </xsl:element>
   </xsl:template>
   <xsl:template match="xhtml:span[@style='text-decoration:underline']"
                 mode="enopdf:format-label">
      <xsl:element name="fo:wrapper">
         <xsl:attribute name="text-decoration" select="'underline'"/>
         <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
      </xsl:element>
   </xsl:template>
   <xsl:template match="xhtml:br" mode="enopdf:format-label">
      <xsl:text xml:space="preserve">
</xsl:text>
   </xsl:template>
   <xsl:template match="xhtml:a[contains(@href,'#ftn')]" mode="enopdf:format-label">
      <xsl:apply-templates select="node()" mode="enopdf:format-label"/>
      <xsl:variable name="relatedInstruction"
                    select="enoddi:get-instruction-by-anchor-ref(.,@href)"/>
      <xsl:choose>
         <xsl:when test="$relatedInstruction/d:InstructionName/r:String = 'tooltip'">
            <xsl:text>*</xsl:text>
         </xsl:when>
         <xsl:when test="$relatedInstruction/d:InstructionName/r:String = 'footnote'">
            <xsl:value-of select="enoddi:get-instruction-index($relatedInstruction,'footnote')"/>
         </xsl:when>
         <xsl:otherwise/>
      </xsl:choose>
   </xsl:template>
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
         <xd:p>"Other – give details" activate the xf-group driver for "give details"</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:ResponseDomainInMixed[d:AttachmentLocation]" mode="source">
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
   <xsl:template match="d:Instruction" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('xf-output',$driver)" mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>The Description of a IfThenElse is an alternative text for non dynamic output format, so it activates the xf-output drivers as other Instruction-like.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="r:Description[parent::d:IfThenElse]" mode="source">
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
         <xd:p>A GenerationInstruction activates the CalculatedVariable driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:GenerationInstruction" mode="source">
      <xsl:param name="driver" tunnel="yes">
         <driver/>
      </xsl:param>
      <xsl:apply-templates select="eno:append-empty-element('CalculatedVariable',$driver)"
                           mode="model">
         <xsl:with-param name="source-context" select="." tunnel="yes"/>
      </xsl:apply-templates>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-form-title to input function enoddi:get-label.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-form-title">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-label($context,$language)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-application-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-application-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-form-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-form-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-name to input function enoddi:get-id.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-name">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-id($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-relevant to input function enoddi:get-hideable-command.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-relevant">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-command($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-readonly to input function enoddi:get-deactivatable-command.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-readonly">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-command($context)"/>
   </xsl:function>
   <xsl:function name="enopdf:get-required">
      <xsl:param name="context" as="item()"/>
      <xsl:text/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-calculate to input function enoddi:get-variable-calculation.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-calculate">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-variable-calculation($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-type to input function enoddi:get-type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-type">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-type($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-format to input function enoddi:get-format.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-format">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-format($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-constraint to input function enoddi:get-control.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-constraint">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-control($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-alert-level to input function enoddi:get-message-type.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-alert-level">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-message-type($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-help to input function enoddi:get-help-instruction.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-help">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-help-instruction($context,$language)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-label to input function enoddi:get-label.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enopdf:get-formatted-label($context,$language)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-value to input function enoddi:get-value.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-value">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-value($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-appearance to input function enoddi:get-output-format.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-appearance">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-output-format($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-css-class to input function enoddi:get-style.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-css-class">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-style($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-length to input function enoddi:get-length.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-length">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-length($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-suffix to input function enoddi:get-suffix.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-suffix">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enoddi:get-suffix($context,$language)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-header-columns to input function enoddi:get-levels-first-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-header-columns">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-first-dimension($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-header-lines to input function enoddi:get-levels-second-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-header-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-levels-second-dimension($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-body-lines to input function enoddi:get-codes-first-dimension.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-body-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-codes-first-dimension($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-header-line to input function enoddi:get-title-line.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-header-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-title-line($context,$index)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-body-line to input function enoddi:get-table-line.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-body-line">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="index"/>
      <xsl:sequence select="enoddi:get-table-line($context,$index)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-rowspan to input function enoddi:get-rowspan.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-rowspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-rowspan($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-colspan to input function enoddi:get-colspan.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-colspan">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-colspan($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-minimum-lines to input function enoddi:get-minimum-required.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-minimum-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-minimum-required($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-constraint-dependencies to input function enoddi:get-computation-items.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-constraint-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-computation-items($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-relevant-dependencies to input function enoddi:get-hideable-then.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-relevant-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-hideable-then($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-readonly-dependencies to input function enoddi:get-deactivatable-then.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-readonly-dependencies">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-deactivatable-then($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-code-depth to input function enoddi:get-level-number.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-code-depth">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-level-number($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:get-image to input function enoddi:get-image.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-image">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-image($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Linking output function enopdf:is-first to input function enoddi:is-first.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:is-first">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:is-first($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Function for debugging, it outputs the input name of the element related to the driver.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-ddi-element">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="local-name($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-after-question-title-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-instructions-by-format($context,'instruction,comment,help')"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-end-question-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-instructions-by-format($context,'footnote') | enoddi:get-next-filter-description($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-before-question-title-instructions">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-previous-filter-description($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Function for retrieving default line number for TableLoop</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-rooster-number-lines">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="if($context/self::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]) then(8) else()"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Function for retrieving style for QuestionTable (only 'no-border' or '' as values yet)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-style">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="if(enoddi:get-style($context) = 'question multiple-choice-question') then ('no-border') else()"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Function for retrieving an index for footnote instructions (based on their ordering in the questionnaire)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-end-question-instructions-index">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-instruction-index($context,'footnote,tooltip')"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p>Function for retrieving the number of decimals accepted by a response field.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:get-number-of-decimals">
      <xsl:param name="context" as="item()"/>
      <xsl:sequence select="enoddi:get-number-of-decimals($context)"/>
   </xsl:function>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:function name="enopdf:debug-get-formatted-label">
      <xsl:param name="context" as="item()"/>
      <xsl:param name="language"/>
      <xsl:sequence select="enopdf:get-formatted-label($context,$language)"/>
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
   <xd:doc>
      <xd:desc>
         <xd:p>ResponseDomainInMixed with AttachmentLocation correspond to attached Responsedomain that should'nt be seen as a direct child of the ResponseDomainInMixed but as a direct child of the response field which it's attached to ('other:' usecase)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:StructuredMixedResponseDomain"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="*[not(d:AttachmentLocation)]"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>For l:Code with attached ResponseDomain, the attached is a direct child of the l:Code. FIXME : Works only for l:Code, if other case are used, need an extension of the implementation.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:ResponseDomainInMixed[@attachmentBase]//l:Code"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="ancestor::d:StructuredMixedResponseDomain/d:ResponseDomainInMixed[d:AttachmentLocation/d:DomainSpecificValue[r:Value = current()/r:Value and @attachmentDomain = current()/ancestor::d:ResponseDomainInMixed/@attachmentBase]]/*[not(self::d:AttachmentLocation)]"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>As in r:OutParameter, all the CodeList could be referenced, it's needed to make the r:OutParameter not a direct child of the CodeDomain to avoid conflict for attached ResponseDomain.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:CodeDomain" mode="eno:child-fields" as="node()*">
      <xsl:sequence select="*[not(self::r:OutParameter)]"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p>It deactivate explicit driver flow for Instructions attached to a question. Explicit out-getters should be used instead to retrieve instructions.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem | d:QuestionGrid"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="*[not(self::d:InterviewerInstructionReference)]"/>
   </xsl:template>
   <xd:doc>
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:ComputationItem" mode="eno:child-fields" as="node()*">
      <xsl:sequence select="*[not(descendant-or-self::d:Instruction/d:InstructionName/r:String = 'warning')]"/>
   </xsl:template>
</xsl:stylesheet>
