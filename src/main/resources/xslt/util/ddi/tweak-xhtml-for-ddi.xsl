<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:d="ddi:datacollection:3_3" xmlns:r="ddi:reusable:3_3" exclude-result-prefixes="xs"
    version="2.0">
    
    <!-- 
        *************************** Tweak-xhtml-for-ddi for ENO **************************************
        This stylesheet is used as a tweak to fit a very specific DDI pattern representing footnote for ENO.
        The syntax expected as input is : <xhtml:a href=". 'Some Footnote Text' ">Some other Text<xhtml:a>.
        The syntax expected as output is : Some Other Text<xhtml:a href="#ftn{index}"/>, whith an d:Instruction generated containing <xhtml:p id="ftn{index}">Some Footnote Text</xhtml:p> as InstructionText.
        All other elements not matching this syntax is simply copied untouched.               
    -->
    
    
    <!-- Defining what is an xhtml:a 'footnote'. -->
   <!-- <xsl:key name="is-footnote" match="xhtml:a" use="if(@href) then(matches(@href,'^\.\s\\&quot;.+\\&quot;')) else(false())"/>-->
    <xsl:key name="is-footnote" match="xhtml:a" use="if(@href) then(matches(@href,'^\.\s&quot;.+&quot;')) else(false())"/>
    
    <!-- Standard identity pattern. -->
    <xsl:template match="*">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text()">
        <xsl:value-of select="."/>
    </xsl:template>

    <!-- Keep comment and pi as well. -->
    <xsl:template match="comment() | processing-instruction()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <!-- xhtml:a 'footnote' will have a generic href and its content outputted just before. -->
    <xsl:template match="xhtml:a[key('is-footnote',true())]">
        <!-- Output its content -->
        <xsl:value-of select="."/>
        <xsl:element name="xhtml:a">
            <!-- Building an href=#ftn{index}, index is based on the number of xhtml:a footnote -->
            <xsl:attribute name="href"
                select="concat('#ftn', 1 + count(preceding::xhtml:a[key('is-footnote',true())]))"/>
        </xsl:element>
    </xsl:template>

    <!-- For each xhtml:a 'footnote', an Instruction needs to be generated. -->
    <xsl:template match="d:InterviewerInstructionScheme">
        <!-- First all the Scheme is processed. -->
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()"/>
            <!-- Then an instruction is generated for each xhmtl:a "footnote". -->
            <xsl:for-each select="//xhtml:a[key('is-footnote',true())]">
                <!-- Calculating the variables needed. -->
                <!-- For r:Agency, r:Version and xml:lang, the closest ancestor whith equivalent is used -->
                <xsl:variable name="agency" select="ancestor-or-self::*[r:Agency][1]/r:Agency"/>
                <xsl:variable name="version" select="ancestor-or-self::*[r:Version][1]/r:Version"/>
                <xsl:variable name="lang" select="ancestor-or-self::*[@xml:lang][1]/@xml:lang"/>
                <!-- The id is build with the pattern ftn{index}, index is based on the number of xhtml:a 'footnote'. -->
                <xsl:variable name="id"
                    select="concat('ftn', 1 + count(preceding::xhtml:a[key('is-footnote',true())]))"/>
                <d:Instruction>
                    <r:Agency>
                        <xsl:value-of select="$agency"/>
                    </r:Agency>
                    <!-- An Id is created (but its never called) -->
                    <r:ID>
                        <xsl:value-of select="concat('FTN-INSTRUCTION-', position())"/>
                    </r:ID>
                    <r:Version>
                        <xsl:value-of select="$version"/>
                    </r:Version>
                    <d:InstructionName>
                        <r:String xml:lang="{$lang}">tooltip</r:String>
                    </d:InstructionName>
                    <d:InstructionText>
                        <d:LiteralText>
                            <d:Text xml:lang="{$lang}">
                                <!-- The id referenced by the xhtml:a is the id of the xhtml:p. -->
                                <xhtml:p id="{$id}">
                                    <!-- Keeping in @href only the sequence between quotes (") as InstructionText-->
                                    <xsl:analyze-string select="@href" regex="&quot;(.+)&quot;">
                                        <xsl:matching-substring>
                                            <xsl:copy-of select="regex-group(1)"/>
                                        </xsl:matching-substring>
                                    </xsl:analyze-string>
                                </xhtml:p>
                            </d:Text>
                        </d:LiteralText>
                    </d:InstructionText>
                </d:Instruction>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
