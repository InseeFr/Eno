<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <xsl:attribute-set name="Titre-sequence">
        <xsl:attribute name="background-color">#666666</xsl:attribute>
        <xsl:attribute name="color">White</xsl:attribute>
        <xsl:attribute name="font-weight">Bold</xsl:attribute>
        <xsl:attribute name="margin-bottom">9pt</xsl:attribute>
        <xsl:attribute name="margin-top">3pt</xsl:attribute>
        <xsl:attribute name="font-size">14pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="Titre-paragraphe">
        <xsl:attribute name="background-color">#CCCCCC</xsl:attribute>
        <xsl:attribute name="color">black</xsl:attribute>
        <xsl:attribute name="font-weight">Bold</xsl:attribute>
        <xsl:attribute name="margin-bottom">9pt</xsl:attribute>
        <xsl:attribute name="margin-top">3pt</xsl:attribute>
        <xsl:attribute name="font-size">12pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="general-style">
        <xsl:attribute name="color">black</xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="padding">1mm</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="label-question">
        <xsl:attribute name="color">black</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="margin-top">9pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="label-instruction">
        <xsl:attribute name="color">black</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">9pt</xsl:attribute>
        <xsl:attribute name="font-family">arial</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="entete-ligne">
        <xsl:attribute name="color">black</xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="colonne-tableau">
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="Line-drawing">
        <xsl:attribute name="width">100%</xsl:attribute>
        <xsl:attribute name="height">10mm</xsl:attribute>
        <xsl:attribute name="position">relative</xsl:attribute>
        <xsl:attribute name="border-bottom">1px dotted black</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="Line-drawing-Garde">
        <xsl:attribute name="position">relative</xsl:attribute>
        <xsl:attribute name="border-bottom">1px dotted black</xsl:attribute>
    </xsl:attribute-set>
</xsl:stylesheet>