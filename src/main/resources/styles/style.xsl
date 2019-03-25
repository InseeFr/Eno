<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:attribute-set name="Titre-sequence">
        <xsl:attribute name="background-color">#666666</xsl:attribute>
        <xsl:attribute name="color">white</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="margin-bottom">9pt</xsl:attribute>
        <xsl:attribute name="margin-top">3pt</xsl:attribute>
        <xsl:attribute name="font-size">14pt</xsl:attribute>
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="space-before">10mm</xsl:attribute>
        <xsl:attribute name="space-before.conditionality">discard</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="Titre-paragraphe">
        <xsl:attribute name="background-color">#CCCCCC</xsl:attribute>
        <xsl:attribute name="color">black</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
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
        <xsl:attribute name="margin-bottom">3pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="label-instruction">
        <xsl:attribute name="color">black</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">9pt</xsl:attribute>
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
        <xsl:attribute name="border-bottom">1px dashed black</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="Line-drawing-Garde">
        <xsl:attribute name="position">relative</xsl:attribute>
        <xsl:attribute name="border-bottom">1px dotted black</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="filter-block">
        <xsl:attribute name="space-before">2pt</xsl:attribute>
        <xsl:attribute name="space-after">2pt</xsl:attribute>
        <xsl:attribute name="start-indent">5%</xsl:attribute>
        <xsl:attribute name="end-indent">0%</xsl:attribute>
        <xsl:attribute name="background-color">#f0f0f0</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="filter-inline-container">
        <xsl:attribute name="width">85%</xsl:attribute>
        <xsl:attribute name="vertical-align">middle</xsl:attribute>
        <xsl:attribute name="padding-top">0pt</xsl:attribute>
        <xsl:attribute name="start-indent">0%</xsl:attribute>
        <xsl:attribute name="end-indent">0%</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="filter-alternative">
        <xsl:attribute name="width">100%</xsl:attribute>
        <xsl:attribute name="margin">2pt</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="footnote">
        <xsl:attribute name="font-size">9pt</xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="margin-bottom">3pt</xsl:attribute>
        <xsl:attribute name="margin-left">3pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="instruction">
        <xsl:attribute name="font-size">9pt</xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="margin-bottom">3pt</xsl:attribute>
        <xsl:attribute name="margin-left">3pt</xsl:attribute>
    </xsl:attribute-set>
    
    
</xsl:stylesheet>