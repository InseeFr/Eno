<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2" xmlns:g="ddi:group:3_2"
    xmlns:s="ddi:studyunit:3_2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="main-file"/>
    <xsl:param name="secondary-file"/>
    <xsl:param name="variables-file"/>
    <xsl:param name="output-folder"/>
    <xsl:output method="xml" indent="no" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Variable that concatenates the DDI</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="total-ddi">
        <conteneur>
            <xsl:copy-of select="document($main-file)"/>
            <xsl:copy-of select="document($secondary-file)"/>
        </conteneur>
    </xsl:variable>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Root template, applying every template of every child</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <!-- Several cases -->
        <xsl:choose>
            <!-- studyUnit case, dereferencing every instrument -->
            <xsl:when test="document($main-file)//d:Instrument">
                <xsl:variable name="root">
                    <xsl:value-of
                        select="replace(document($main-file)//s:StudyUnit/r:ID/text(), '-SU', '')"
                    />
                </xsl:variable>
                <xsl:for-each select="document($main-file)//d:Instrument">
                    <xsl:result-document
                        href="{lower-case(concat('file:///',replace($output-folder, '\\' , '/'),'/',replace(r:ID/text(), concat($root/text(),'-In-'), ''),'.tmp'))}"
                        method="xml">
                        <DDIInstance xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                            <s:StudyUnit>
                                <xsl:apply-templates select="."/>
                            </s:StudyUnit>
							<!-- Only copying the variables that don't correspond to a question -->
                            <xsl:apply-templates
                                select="document($variables-file)//g:ResourcePackage"/>
                        </DDIInstance>
                    </xsl:result-document>
                </xsl:for-each>
            </xsl:when>

            <!-- ControlConstructScheme pool case, dereferencing the ControlConstructScheme -->
            <xsl:when test="document($main-file)//d:ControlConstructScheme">
                <d:ControlConstructScheme>
                    <xsl:apply-templates
                        select="document($main-file)//d:Sequence[d:TypeOfSequence/text()='Modele']"
                    />
                </d:ControlConstructScheme>
            </xsl:when>

            <!-- Questions/interviews/categories/codes pool case. dereferencing the principal elements : questions and interviews
             -->
            <xsl:otherwise>
                <g:ResourcePackage>
                    <xsl:apply-templates
                        select="document($main-file)//d:InterviewerInstructionScheme"/>
                    <xsl:apply-templates select="document($main-file)//d:QuestionScheme"/>
                </g:ResourcePackage>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply copying to the output file</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Not retrieving the variables that correspond to a question</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="l:Variable[r:QuestionReference or r:SourceParameterReference]"
        priority="1"/>
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Only retrieving the variables not corresponding to a question</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template
        match="l:Variable[not(r:QuestionReference or r:SourceParameterReference)]"
        priority="1">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Default template for every element that corresponds to a reference</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node()[ends-with(name(),'Reference') and not(parent::r:Binding)]/r:ID">
        <xsl:variable name="ID" select="."/>
        <!-- Copying the element -->
        <!-- Making sure we're not copying an element that isn't itself inside another reference (and that would actually not the base element but an already indexed reference) -->
        <xsl:apply-templates
            select="$total-ddi//*[r:ID=$ID and not(ancestor-or-self::node()[ends-with(name(),'Reference')])]"
        />
    </xsl:template>

</xsl:transform>
