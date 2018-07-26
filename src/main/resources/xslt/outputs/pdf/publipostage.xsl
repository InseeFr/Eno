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

	<xd:doc>
		<xd:desc>
			<xd:p>The FO/PDF properties file path.</xd:p>
			<xd:p>Injected by the xsl transformation.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:param name="properties-file"/>

	<xd:doc>
		<xd:desc>
			<xd:p>The properties file of FO/PDF generation.</xd:p>
		</xd:desc>
	</xd:doc>	
	<xsl:variable name="properties" select="doc($properties-file)"/>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Get the conditioning characters from properties file</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:variable name="SpecialCharacterSymbol1" select="'ø'" />
	<xsl:variable name="SpecialCharacterSymbol2_before" select="$properties//TextConditioningVariable/ddi/Before" />
	<xsl:variable name="SpecialCharacterSymbol2_after" select="$properties//TextConditioningVariable/ddi/After" />
	
	<xd:doc>
		<xd:desc>
			<xd:p>Pattern to match nodes containing the conditioning characters</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:variable name="pattern"
		select="concat('.*',$SpecialCharacterSymbol1,'.*',$SpecialCharacterSymbol1,'.*','|', '.*',$SpecialCharacterSymbol2_before,'.*',$SpecialCharacterSymbol2_after,'.*')" />
	
	<xsl:variable name="valueofBlockPattern"
		select="concat($SpecialCharacterSymbol1,'.*',$SpecialCharacterSymbol1,'|', $SpecialCharacterSymbol2_before,'.*',$SpecialCharacterSymbol2_after)" />
	<xsl:variable name="valueOfStringCleanedPattern"
		select="concat($SpecialCharacterSymbol1,'|',$SpecialCharacterSymbol2_before,'|',$SpecialCharacterSymbol2_after)" />

	<xd:doc>
		<xd:desc>
			<xd:p>Copy all nodes with their attributes.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

	<xd:doc>
		<xd:desc>
			<xd:p>Apply conversion of DDI variables to simple velocity variable syntax. Apply to all nodes but the response items (nodes having the element "ResponseBlock")</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="text()[matches(.,$pattern)]">
		<xsl:variable name='valueOfBlock'>
			<xsl:copy-of select="." />
		</xsl:variable>
		<xsl:analyze-string select="$valueOfBlock" regex="ø.*ø|¤.*¤">
			<xsl:matching-substring>
			<xsl:variable name="valueOfStringCleaned"
					select="translate(.,$valueOfStringCleanedPattern,'')" />
				<xsl:call-template name="simpleVelocityCondition">
					<xsl:with-param name="var" select="$valueOfStringCleaned" />
				</xsl:call-template>
			</xsl:matching-substring>
			<xsl:non-matching-substring>
				<xsl:copy-of select="." />
			</xsl:non-matching-substring>
		</xsl:analyze-string>
	</xsl:template>
	
	
	<xd:doc>
		<xd:desc>
			<xd:p>Apply conversion of variables to velocity conditions syntax. Apply to response items (nodes having the element "ResponseBlock")</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template match="//*[child::ResponseBlock and text()[matches(.,$pattern)]]">
		<xsl:variable name='valueOfBlock'>
			<xsl:copy-of select="." />
		</xsl:variable>
		<xsl:copy><xsl:apply-templates select="@*"/></xsl:copy>
		<xsl:analyze-string select="$valueOfBlock" regex="ø.*ø|¤.*¤">
			<xsl:matching-substring>
			<xsl:variable name="valueOfStringCleaned"
					select="translate(.,$valueOfStringCleanedPattern,'')" />
				<xsl:call-template name="velocityConditionForResponseItems">
					<xsl:with-param name="var" select="$valueOfStringCleaned" />
				</xsl:call-template>
			</xsl:matching-substring>
			<xsl:non-matching-substring>
				<xsl:copy-of select="." />
			</xsl:non-matching-substring>
		</xsl:analyze-string>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Velocity condition for replacement of ddi response items variables.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template name="velocityConditionForResponseItems">
		<xsl:param name="var" />
		<xsl:text disable-output-escaping="yes"> if(${</xsl:text>
		<xsl:value-of select="$var" />
		<xsl:text disable-output-escaping="yes">}) ${</xsl:text>
		<xsl:value-of select="$var" />
		<xsl:text disable-output-escaping="yes">} #else &lt;fo:block border-bottom=&quot;1px dotted black&quot; &gt; &amp;#160; &lt;/fo:block&gt; #end 
		</xsl:text>
	</xsl:template>
	
	<xd:doc>
		<xd:desc>
			<xd:p>Velocity variable for replacement of all non ddi response items variables.</xd:p>
		</xd:desc>
	</xd:doc>
	<xsl:template name="simpleVelocityCondition">
		<xsl:param name="var" />
		<xsl:text disable-output-escaping="yes"> ${</xsl:text> <xsl:value-of select="$var" /> <xsl:text disable-output-escaping="yes">} </xsl:text>
	</xsl:template>
</xsl:stylesheet>
	