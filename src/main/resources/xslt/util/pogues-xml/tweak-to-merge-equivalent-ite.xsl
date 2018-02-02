<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <!-- 
        This stylesheet is a tweak to correct the transformation between 'Goto' to 'IfThenElse'
        The transformation produces a separate IfThenElse for each module.
        The expected output is the minimum of IfThenElse.
        So this stylesheet merges consecutive pogues:IfThenElse whith the exact same pogues:Expression.    
    -->
    
    <!-- Copying. -->
    <xsl:template match="/ | *" mode="#all">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="text() | comment() | processing-instruction()">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <!-- ITE with an equivalent ITE (aka same Expression) as the first preceding-sibling => Skipped. It's handled by the first ITE of the "equivalent class". -->
    <xsl:template match="pogues:IfThenElse[preceding-sibling::*[1][self::pogues:IfThenElse/pogues:Expression = current()/pogues:Expression]]"/>
    
    <xsl:template match="pogues:IfThenElse/pogues:IfTrue">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
            <xsl:call-template name="mergeConsecutiveITE">
                <xsl:with-param name="mergedITE" select="parent::pogues:IfThenElse"/>
                <xsl:with-param name="ITEToMerge" select="parent::pogues:IfThenElse/following-sibling::pogues:*[1]"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template name="mergeConsecutiveITE">
        <xsl:param name="mergedITE"/>
        <xsl:param name="ITEToMerge"/>
        <xsl:if test="$mergedITE/pogues:Expression = $ITEToMerge/self::pogues:IfThenElse/pogues:Expression">
            <xsl:apply-templates select="$ITEToMerge/pogues:IfTrue/node()"/>
            <xsl:call-template name="mergeConsecutiveITE">
                <xsl:with-param name="mergedITE" select="$mergedITE"/>
                <xsl:with-param name="ITEToMerge" select="$ITEToMerge/following-sibling::pogues:*[1]"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>