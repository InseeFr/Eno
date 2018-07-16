<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
	xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="xd eno enopdf"
	xmlns:fox="http://xmlgraphics.apache.org/fop/extensions" version="2.0"
	xmlns:regexp="http://exslt.org/regular-expressions">


	<!-- TODO : extract from parameters -->
	<xsl:variable name="SpecialCharacterSymbol1" select="'ø'" />
	<xsl:variable name="SpecialCharacterSymbol2" select="'¤'" />
	<xsl:variable name="pattern"
		select="concat('.*',$SpecialCharacterSymbol1,'.*',$SpecialCharacterSymbol1,'.*','|', '.*',$SpecialCharacterSymbol2,'.*',$SpecialCharacterSymbol2,'.*')" />
	<xsl:variable name="valueofBlockPattern"
		select="concat($SpecialCharacterSymbol1,'.*',$SpecialCharacterSymbol1,'|', $SpecialCharacterSymbol2,'.*',$SpecialCharacterSymbol2)" />
	<xsl:variable name="valueOfStringCleanedPattern"
		select="concat($SpecialCharacterSymbol1,'|',$SpecialCharacterSymbol2)" />

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="fo:block[text()[matches(.,$pattern)]]">
	
		<xsl:variable name='valueOfBlock'>
			<xsl:copy-of select="." />
		</xsl:variable>
		<xsl:call-template name="blockBegin" />
	
		<xsl:analyze-string select="$valueOfBlock" regex="ø.*ø|¤.*¤">
			<xsl:matching-substring>
			<xsl:variable name="valueOfStringCleaned"
					select="translate(.,$valueOfStringCleanedPattern,'')" />
				<xsl:call-template name="velocityCondition">
					<xsl:with-param name="var" select="$valueOfStringCleaned" />
				</xsl:call-template>
			</xsl:matching-substring>
			<xsl:non-matching-substring>
				<xsl:copy-of select="." />
			</xsl:non-matching-substring>
		</xsl:analyze-string>
		<xsl:call-template name="blockEnd" />
	</xsl:template>

	<!-- TODO : must retrieve the source block attributes -->
	<xsl:template name="blockBegin">
		<xsl:text disable-output-escaping="yes">
				   &lt;fo:block       
				   color="black"
                   font-weight="bold"
                   margin-bottom="3pt"
                   margin-top="3pt"
                   font-size="9pt"
                   keep-with-next="always" &gt;
	   </xsl:text>
	</xsl:template>

	<xsl:template name="blockEnd">
		<xsl:text disable-output-escaping="yes">&lt;/fo:block&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="velocityCondition">
		<xsl:param name="var" />
		<xsl:text disable-output-escaping="yes"> if(${</xsl:text>
		<xsl:value-of select="$var" />
		<xsl:text disable-output-escaping="yes">}) ${</xsl:text>
		<xsl:value-of select="$var" />
		<xsl:text disable-output-escaping="yes">} #else &lt;fo:block border-bottom=&quot;1px dotted black&quot; &gt; &amp;#160; &lt;/fo:block&gt; #end 
		</xsl:text>
	</xsl:template>
</xsl:stylesheet>
	