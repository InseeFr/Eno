<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>VariableScheme becomes child of the main sequence (which starts the creation of the Xforms) because some Variables are used to create elements into the generated Xforms.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='template']"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="* | ancestor::DDIInstance//l:VariableScheme"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>ResponseDomainInMixed with AttachmentLocation correspond to attached Responsedomain that should'nt be seen as a direct child of the ResponseDomainInMixed but as a direct child of the response field which it's attached to ('other:' usecase)</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:StructuredMixedResponseDomain"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="*[not(d:AttachmentLocation)]"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>For l:Code with attached ResponseDomain, the attached is a direct child of the l:Code. FIXME : Works only for l:Code, if other case are used, need an extension of the implementation.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:ResponseDomainInMixed[@attachmentBase]//l:Code"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="ancestor::d:StructuredMixedResponseDomain/d:ResponseDomainInMixed[d:AttachmentLocation/d:DomainSpecificValue[r:Value = current()/r:Value and @attachmentDomain = current()/ancestor::d:ResponseDomainInMixed/@attachmentBase]]/*[not(self::d:AttachmentLocation)]"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>As in r:OutParameter, all the CodeList could be referenced, it's needed to make the r:OutParameter not a direct child of the CodeDomain to avoid conflict for attached ResponseDomain.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:CodeDomain" mode="eno:child-fields" as="node()*">
      <xsl:sequence select="*[not(self::r:OutParameter)]"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p>It deactivate explicit driver flow for Instructions attached to a question. Explicit out-getters should be used instead to retrieve instructions.</xd:p>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:QuestionItem | d:QuestionGrid"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="*[not(self::d:InterviewerInstructionReference)]"/>
   </xsl:template>
   <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
      <xd:desc>
         <xd:p/>
      </xd:desc>
   </xd:doc>
   <xsl:template match="d:ComputationItem" mode="eno:child-fields" as="node()*">
      <xsl:sequence select="*[not(descendant-or-self::d:Instruction/d:InstructionName/r:String = 'warning')]"/>
   </xsl:template>
</xsl:stylesheet>
