<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
    xmlns:eno="http://xml.insee.fr/apps/eno" version="2.0">

    <xsl:import href="../../lib.xsl"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This xslt stylesheet is used on a dereferenced ddi to make some adaptations.</xd:p>
            <xd:p>The purpose is to simplify the main ddi2fr transformation and make the result
                compatible with Orbeon form.</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="no" encoding="UTF-8"/>

    <!--<xsl:strip-space elements="*"/>-->

    <xd:doc>
        <xd:desc>
            <xd:p>The properties file used by the stylesheet.</xd:p>
            <xd:p>It's on a transformation level.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="properties-file"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The properties file is charged as an xml tree.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="properties" select="doc($properties-file)"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply copying to the
                output.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>This template compensates a Bootstrap/Orbeon bug.</xd:p>
            <xd:p>br tags create errors when the label, where they are contained, has a dynamic
                behaviour (if there is a relevant attribute on the xforms element for
                example).</xd:p>
            <xd:p>They are replaced by a span element here with a specific css class.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:p[xhtml:br]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:for-each-group select="node()" group-ending-with="xhtml:br">
                <xhtml:span class="block">
                    <xsl:for-each select="current-group()[not(name()='xhtml:br')]">
                        <xsl:apply-templates select="."/>
                    </xsl:for-each>
                </xhtml:span>
            </xsl:for-each-group>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>This template modifies the identifiers used in the r:CommandContent.</xd:p>
            <xd:p>The identifiers used are local. They are replaced by identifiers that are linked
                to OutParameters of questions.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="r:CommandContent[parent::r:Command/r:InParameter]">
        <xsl:variable name="command">
            <xsl:value-of select="."/>
        </xsl:variable>
        <xsl:copy>
            <!-- Creating a list of old identifiers -->
            <xsl:variable name="old-identifiers" as="xs:string*">
                <xsl:for-each select="parent::r:Command/r:InParameter">
                    <!-- Sorting them to get the longer ones first
                    This resolve a problem appearing when the chain refering to an identifier is contained into another one.
                    By sorting like this, this problem is solved.-->
                    <xsl:sort select="string-length(r:ID)" order="descending"/>
                    <xsl:value-of select="r:ID"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:variable name="new-identifiers" as="xs:string*">
                <!-- Creating a list of new identifiers -->
                <xsl:for-each select="parent::r:Command/r:InParameter">
                    <xsl:sort select="string-length(r:ID)" order="descending"/>
                    <xsl:value-of select="concat($properties//TextConditioningVariable/ddi/Before,
                        parent::r:Command/r:Binding[r:TargetParameterReference/r:ID=current()/r:ID]/r:SourceParameterReference/r:ID,
                        $properties//TextConditioningVariable/ddi/After)"/>
                </xsl:for-each>
            </xsl:variable>

            <xsl:variable name="modified-text">
                <!-- The modifications are made through this function --> 
                <xsl:value-of select="eno:text-modification($old-identifiers,$new-identifiers,$command,1)"/>
            </xsl:variable>
            
            <!-- In the end, some other strings are replaced to have a generic way of describing the empty/null value -->
            <xsl:value-of select="replace(replace($modified-text,'&quot;',''''),'∅','''''')"/>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Modifying format of the xml:lang attribute from xx-XX to xx (localization is not used in Eno for the moment).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="@xml:lang[contains(.,'-')]">
        <xsl:attribute name="xml:lang" select="substring-before(.,'-')"/>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>For each xhtml:a element not having an internal @href attribute, an attribute 'target' is created with the '_blank' value.</xd:p>
            <xd:p>Links are consequently opened in new tabs of the browser.</xd:p>
            <xd:p>It is not directly written in the DDI because even if the attribute target is interpreted by most browsers, it is not strictly valid in XHTML.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:a[not(contains(@href,'#'))]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="target">
                <xsl:value-of select="'_blank'"/>
            </xsl:attribute>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Adding identifier to footnote type d:Instruction in xhtml:p elements.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="xhtml:p[ancestor::d:Instruction[d:InstructionName/r:String/text()='footnote']]">
        <xsl:copy>
            <xsl:variable name="identifier">
                <xsl:value-of select="concat('#',@id)"/>
            </xsl:variable>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="concat(//xhtml:a[@href=$identifier]/text(),' ')"/>
            <xsl:copy-of select="node()"/>
        </xsl:copy>
    </xsl:template>


    <xd:doc>
        <xd:desc>
            <xd:p>Correct r:Low when @isInclusive='false' or number of digits after the dot is not good</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="r:Low|r:High">
        <xsl:variable name="initial-extremum" select="text()"/>
        <xsl:variable name="evolution">
            <xsl:choose>
                <xsl:when test="@isInclusive='false' and name()='r:Low'">
                    <xsl:value-of select="'1'"/>
                </xsl:when>
                <xsl:when test="@isInclusive='false' and name()='r:High'">
                    <xsl:value-of select="'-1'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'0'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="decimal-position">
            <xsl:choose>
                <xsl:when test="../../@decimalPositions">
                    <xsl:value-of select="../../@decimalPositions"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'0'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$evolution='0' and $decimal-position='0' and not(contains($initial-extremum,'.'))">
                    <xsl:value-of select="$initial-extremum"/>
                </xsl:when>
                <xsl:when test="$evolution='0' and contains($initial-extremum,'.') and string-length(substring-after($initial-extremum,'.')) = number($decimal-position)">
                    <xsl:value-of select="$initial-extremum"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="power">
                        <xsl:value-of select="'1'"/>
                        <xsl:for-each select="1 to $decimal-position">
                            <xsl:value-of select="'0'"/>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:variable name="format">
                        <xsl:value-of select="'0.'"/>
                        <xsl:for-each select="1 to $decimal-position">
                            <xsl:value-of select="'0'"/>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:value-of select="format-number(($initial-extremum * $power + $evolution) div $power,$format)"/>
                    <!--<xsl:value-of select="substring-before(substring-after(format-number(($initial-extremum * $power + $evolution) div $power,$format),''''),'''')"/>-->
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
