<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2"
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
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:strip-space elements="*"/>

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
                    <xsl:variable name="old-identifier">
                        <xsl:value-of select="r:ID"/>
                    </xsl:variable>
                    <!-- Getting the parameter id from the source question -->
                    <xsl:variable name="new-identifier">
                        <xsl:value-of
                            select="parent::r:Command/r:Binding[r:TargetParameterReference/r:ID=$old-identifier]/r:SourceParameterReference/r:ID"
                        />
                    </xsl:variable>
                    <!-- Added to be selected with Xpath on the Xforms side-->
                    <xsl:variable name="relative-path">
                        <xsl:value-of>//</xsl:value-of>
                        <xsl:for-each
                            select="ancestor::d:Loop | ancestor::d:QuestionGrid[d:GridDimension/d:Roster]">
                            <xsl:variable name="id">
                                <xsl:choose>
                                    <xsl:when test="name()='d:Loop'">
                                        <xsl:value-of select="concat(r:ID,'-Loop')"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="concat(r:ID,'-RowLoop')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <xsl:value-of
                                select="concat('*[name()=''',$id,
                                ''' and count(preceding-sibling::*)=count(current()/ancestor::*[name()=''',
                                $id,''']/preceding-sibling::*)]//')"
                            />
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:value-of select="concat($relative-path,$new-identifier)"/>
                    <!--                        <xsl:choose>
                            <!-\- for filters and controls in loops, fetching the nearest variable in the tree -\->
                            <xsl:when test="ancestor::d:Loop | ancestor::d:QuestionGrid[d:GridDimension/d:Roster]">
                                <xsl:value-of select="concat('ancestor::*[descendant::',$new-identifier,'][1]//',$new-identifier)"/>                           
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('//',$new-identifier)"/>
                            </xsl:otherwise>
                        </xsl:choose>-->
                </xsl:for-each>
            </xsl:variable>

            <xsl:variable name="modified-text">
                <!-- The modifications are made through this function --> 
                <xsl:value-of
                    select="eno:text-modification($old-identifiers,$new-identifiers,$command,1)"/>
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

</xsl:stylesheet>
