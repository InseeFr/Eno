<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!--Variables become children of the instrument (that represent the start of the Xforms form)-->
   <xsl:template match="d:Sequence[d:TypeOfSequence/text()='template']"
                 mode="eno:child-fields"
                 as="node()*">
      <xsl:sequence select="* | ancestor::DDIInstance//l:VariableScheme"/>
   </xsl:template>
</xsl:stylesheet>
