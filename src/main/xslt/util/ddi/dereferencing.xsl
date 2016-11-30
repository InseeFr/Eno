<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2" xmlns:g="ddi:group:3_2"
    xmlns:s="ddi:studyunit:3_2" version="2.0">

    <!-- This xsl stylesheet will be applied to ddi input files (part of the dereferencing target) -->
    <!-- Clearing all the pointers reference in those input files -->

    <!-- Parameters given in the build-non-regression.xml -->
    <xsl:param name="output-folder"/>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:strip-space elements="*"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template, applying every template of every child</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <!-- The references used to dereference -->
        <xsl:variable name="references">
            <xsl:element name="g:ResourcePackage">
                <xsl:copy-of select="//l:CodeListScheme"/>
                <xsl:copy-of select="//l:CategoryScheme"/>
                <xsl:copy-of select="//d:InterviewerInstructionScheme"/>
            </xsl:element>
        </xsl:variable>
        <!-- The l:CodeListScheme are dereferenced -->
        <xsl:variable name="dereferenced">
            <xsl:element name="g:ResourcePackage">
                <xsl:apply-templates select="//l:CodeListScheme">
                    <xsl:with-param name="references" select="$references" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:variable>

        <!-- The dereferenced l:CodeListScheme, the d:InterviewerInstructionScheme, the r:ManagedRepresentationScheme and the d:QuestionScheme are used as new references -->
        <xsl:variable name="references">
            <xsl:copy-of select="//d:QuestionScheme"/>
            <xsl:copy-of select="//d:InterviewerInstructionScheme"/>
            <xsl:copy-of select="//r:ManagedRepresentationScheme"/>
            <xsl:copy-of select="$dereferenced//l:CodeListScheme"/>
        </xsl:variable>

        <!-- The d:QuestionScheme are dereferenced -->
        <xsl:variable name="dereferenced">
            <xsl:element name="g:ResourcePackage">
                <xsl:apply-templates select="//d:QuestionScheme">
                    <xsl:with-param name="references" select="$references" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:variable>

        <!-- The dereferenced d:QuestionScheme, the d:InterviewerInstructionScheme, and the ControlConstructScheme are used as new references -->
        <xsl:variable name="references">
            <xsl:copy-of select="//d:ControlConstructScheme"/>
            <xsl:copy-of select="//d:InterviewerInstructionScheme"/>
            <xsl:copy-of select="$dereferenced//d:QuestionScheme"/>
        </xsl:variable>

        <!-- The d:ControlConstructScheme are dereferenced -->
        <xsl:variable name="dereferenced">
            <xsl:element name="g:ResourcePackage">
                <xsl:apply-templates
                    select="//d:ControlConstructScheme/d:Sequence[d:TypeOfSequence/text() = 'template']">
                    <xsl:with-param name="references" select="$references" tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:element>
        </xsl:variable>

        <!-- The l:VariableScheme are used as new references -->
        <xsl:variable name="references">
            <xsl:copy-of select="//l:VariableScheme"/>
        </xsl:variable>

        <!-- The root of all identifiers in the survey -->
        <xsl:variable name="root">
            <xsl:value-of select="replace(//s:StudyUnit/r:ID/text(), '-SU', '')"/>
        </xsl:variable>

        <!-- Then each d:Instrument is dereferenced with the previous dereferenced tree used as references -->
        <xsl:for-each select="//d:Instrument">
            <xsl:result-document
                href="{lower-case(concat('file:///',replace($output-folder, '\\' , '/'),'/',replace(r:ID/text(), concat($root/text(),'-In-'), ''),'.tmp'))}">
                <DDIInstance>
                    <s:StudyUnit>
                        <xsl:apply-templates select=".">
                            <xsl:with-param name="references" select="$dereferenced" tunnel="yes"/>
                        </xsl:apply-templates>
                    </s:StudyUnit>
                    <!-- And the VariableScheme is dereferenced with itself as references -->
                    <!-- Only copying the variables that don't correspond to a question -->
                    <xsl:element name="g:ResourcePackage">
                        <xsl:apply-templates select="//l:VariableScheme">
                            <xsl:with-param name="references" select="$references" tunnel="yes"/>
                        </xsl:apply-templates>
                    </xsl:element>
                </DDIInstance>
            </xsl:result-document>
        </xsl:for-each>

    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply copying to the
                output file</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Not retrieving the variables that correspond to a question</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Variable[r:QuestionReference or r:SourceParameterReference]" priority="1"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Only retrieving the variables not corresponding to a question</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Variable[not(r:QuestionReference or r:SourceParameterReference)]"
        priority="1">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element that corresponds to a reference</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node()[ends-with(name(), 'Reference') and not(parent::r:Binding)]/r:ID">
        <xsl:param name="references" tunnel="yes"/>
        <xsl:variable name="ID" select="."/>
        <!-- Copying the element -->
        <!-- Making sure we're not copying an element that isn't itself inside another reference (and that would actually not the base element but an already indexed reference) -->
        <xsl:apply-templates
            select="$references//*[r:ID = $ID and not(ancestor-or-self::node()[ends-with(name(), 'Reference')])]"
        />
    </xsl:template>

</xsl:stylesheet>
