<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:im="http://www.insee.fr/schemas/ItemML"
    exclude-result-prefixes="#all" version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- This stylesheet preformates the ODS xml files in order to delete the number-columns-repeated attributes while 
        adding a fictional attribute cell-position (that doesn't exist in odt format).
        This attribute will help doing the next transformations (using the cells position in a line).
        Elements are literally copied (except attributes @table:number-columns-repeated) x times
        x being the value of the @table:number-columns-repeated attribute. The attribute itself isn't kept. -->
    
    <xsl:template match="/">
        <xsl:variable name="preformat-1">
            <xsl:copy>          
                <!-- Applying the preformatting templates (repeating the cells by taking into account @table:number-columns-repeated ) -->
                <xsl:apply-templates select="node()"/>    
            </xsl:copy>            
        </xsl:variable>
        <!-- Then applying the templates that will add the position -->
        <xsl:apply-templates select="$preformat-1" mode="add-position"/>
    </xsl:template>
    
    <xsl:template match="table:table" mode="add-position">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="table:table-row" mode="add-position"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="table:table-row" mode="add-position">
        <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates select="*" mode="add-position"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Necessary to avoid getting empty lines. -->
    <xsl:template match="table:table-row[normalize-space(string(.))='']"/>
    
    <xsl:template match="table:table-row/*" mode="add-position">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="table:cell-position" select="position()"/>
            <xsl:copy-of select="*"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Generic recopy template. The copy mode makes a forced copy of all elements. The #default mode is used to only recopy the elements that don't have the @table:number-columns-repeated attribute. -->
    <xsl:template match="node()" mode="#all">
        <xsl:copy>            
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Elements with @table:number-columns-repeated are copied as many times as the attribute's value.
        This applies only in the default mode. In copy mode, those elements are recopied (used below). -->
    <xsl:template match="*[@table:number-columns-repeated &gt; 1]">
        <xsl:call-template name="repetition">
            <xsl:with-param name="nb-repetition" select="@table:number-columns-repeated" tunnel="yes"/>
            <xsl:with-param name="element" select="." tunnel="yes"/>
        </xsl:call-template>
    </xsl:template>
    
    <!-- Attributes are always recopied -->
    <xsl:template match="@*" mode="#all">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <!-- The @table:number-columns-repeated is never kept. -->
    <xsl:template match="@table:number-columns-repeated" mode="#all"/>
    
    <!-- Recursive template of element recopy. -->
    <xsl:template name="repetition">
        <xsl:param name="nb-repetition" select="1" tunnel="yes"/>
        <xsl:param name="element" tunnel="yes"/>
        <xsl:param name="index" select="1"/>
        <!-- Recopy (using the copy mode to force the recopy). -->
        <xsl:apply-templates select="$element" mode="copy"/>
        <!-- Recursive call if there are still copies to do. -->
        <xsl:if test="$nb-repetition - $index &gt; 0">
            <xsl:call-template name="repetition">
                <xsl:with-param name="index" select="$index + 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>