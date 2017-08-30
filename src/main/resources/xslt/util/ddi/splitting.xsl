<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:s="ddi:studyunit:3_2"
    xmlns:d="ddi:datacollection:3_2"
    xmlns:ddi-instance="ddi:instance:3_2"
    xmlns:dereferencing="dereferencing"
    exclude-result-prefixes="xs xd ddi-instance"
    version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Aug 17, 2017</xd:p>
            <xd:p><xd:b>Author:</xd:b> nirnfv</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="output-folder"/>
    <!--xsl:param name="output-folder" select="'./'"/-->
    
    <xsl:template match="/">
        <xsl:for-each select="dereferencing:dereferencing-results/dereferencing:dereferencing-result-DDI">
            <xsl:result-document href="{concat('file:///',replace($output-folder, '\\' , '/'),'/',lower-case(replace(./@instrument-name, '.*-', '')),'-dereferenced.tmp')}">
                <xsl:copy-of select="."/>
             </xsl:result-document>
        </xsl:for-each>
      </xsl:template>
</xsl:stylesheet>