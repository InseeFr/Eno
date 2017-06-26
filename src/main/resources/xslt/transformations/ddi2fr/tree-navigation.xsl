<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
</xsl:stylesheet>
