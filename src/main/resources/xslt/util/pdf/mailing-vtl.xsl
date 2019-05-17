<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:enopdf="http://xml.insee.fr/apps/eno/out/form-runner"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="xd eno enopdf fox"
	xmlns:fox="http://xmlgraphics.apache.org/fop/extensions" version="2.0">

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
	<xsl:template match="text()[contains(.,'ø')]">
		<xsl:call-template name="replace-danish-character">
			<xsl:with-param name="label" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="replace-danish-character">
		<xsl:param name="label"/>
		
		<xsl:choose>
			<xsl:when test="contains(substring-after($label,'ø'),'ø')">
				<xsl:value-of select="concat(substring-before($label,'ø'),'${',substring-before(substring-after($label,'ø'),'ø'),'}')"/>
				<xsl:call-template name="replace-danish-character">
					<xsl:with-param name="label" select="substring-after(substring-after($label,'ø'),'ø')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$label"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
	