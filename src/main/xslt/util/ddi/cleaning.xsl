<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" xmlns:l="ddi:logicalproduct:3_2" version="2.0">

    <!-- This xsl stylesheet is used in the 'cleaning' target of the ant build -->
    <!-- Therefore, it will be applied on the input ddi file (ccs, qb etc.) -->
    <!-- Basically, this stylesheet does a bit of cleaning in order to change some attribute names and delete undesired links -->

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:strip-space elements="*"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template, applying the templates of all children</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template for every element and every attribute, simply copying to the output</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <!-- To delete someday, this compensate a Bootstrap/Orbeon bug -->
    <!-- br tags report an error when the label where they're contained has a dynamic behaviour (relevant label for example)-->
    <!-- Replacing them by span tags (a span tag with a css line break) : dirty work here-->
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

    <xsl:template match="r:CommandContent[parent::r:Command/r:InParameter]">
        <xsl:variable name="command">
            <xsl:value-of select="."/>
        </xsl:variable>
        <xsl:copy>
            <!-- Creating a list of old identifiers -->
            <xsl:variable name="old-identifiers">
                <xsl:for-each select="parent::r:Command/r:InParameter">
                    <!-- Sorting them to get the longer ones first
                    This resolve a problem appearing when the chain refering to an identifier is contained into another one.
                    By sorting like this, this problem is solved.-->
                    <xsl:sort select="string-length(r:ID)" order="descending"/>
                    <xsl:copy-of select="r:ID"/>
                </xsl:for-each>
            </xsl:variable>
            <xsl:variable name="new-identifiers">
                <!-- Creating a list of new identifiers -->
                <xsl:for-each select="parent::r:Command/r:InParameter">
                    <xsl:sort select="string-length(r:ID)" order="descending"/>
                    <xsl:variable name="old-identifier">
                        <xsl:value-of select="r:ID"/>
                    </xsl:variable>
                    <!-- Getting the parameter id from the source question -->
                    <xsl:variable name="new-identifier">
                        <xsl:value-of select="parent::r:Command/r:Binding[r:TargetParameterReference/r:ID=$old-identifier]/r:SourceParameterReference/r:ID"/>
                    </xsl:variable>
                <r:ID>
                        <xsl:choose>
                            <!-- for filters and controls in loops, fetching the nearest variable in the tree -->
                            <xsl:when test="ancestor::d:Loop | ancestor::d:QuestionGrid[d:GridDimension/d:Roster[not(@maximumAllowed)]]">
                                <xsl:value-of select="concat('ancestor::*[descendant::',$new-identifier,'][1]//',$new-identifier)"/>                           
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('//',$new-identifier)"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </r:ID>
                </xsl:for-each>
            </xsl:variable>
            <xsl:call-template name="command-modification">
                <xsl:with-param name="old-identifiers" select="$old-identifiers"/>
                <xsl:with-param name="new-identifiers" select="$new-identifiers"/>
                <xsl:with-param name="command" select="$command"/>
                <xsl:with-param name="min" select="number(1)"/>
                <xsl:with-param name="max" select="count($old-identifiers/r:ID)"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="command-modification">
        <xsl:param name="old-identifiers"/>
        <xsl:param name="new-identifiers"/>
        <xsl:param name="command"/>
        <xsl:param name="min"/>
        <xsl:param name="max"/>
        <xsl:variable name="new-identifier">
            <xsl:value-of select="$new-identifiers/r:ID[$min]"/>
        </xsl:variable>
        <xsl:variable name="modified-command">
            <xsl:value-of select="replace($command,$old-identifiers/r:ID[$min],$new-identifier)"/>
        </xsl:variable>
        <xsl:if test="number($min) &lt; number($max)">
            <xsl:call-template name="command-modification">
                <xsl:with-param name="old-identifiers" select="$old-identifiers"/>
                <xsl:with-param name="new-identifiers" select="$new-identifiers"/>
                <xsl:with-param name="command" select="$modified-command"/>
                <xsl:with-param name="min" select="$min + 1"/>
                <xsl:with-param name="max" select="$max"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="number($min) = number($max)">
            <xsl:value-of select="replace($modified-command,'&#8709;','&#39;&#39;&#39;&#39;')"/>
        </xsl:if>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Deleting the Internet link (replacing it by r:Descritpion/r:Content, see below)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xhtml:sup"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Copying everything</xd:p>
            <xd:p>Applying the xhtml:sup template won't do anything (see above)</xd:p>
            <xd:p>Creating a r:Description/r:Content for each element met</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Category[r:Label//xhtml:sup]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xsl:element name="r:Description">
                <xsl:for-each select="r:Label//xhtml:sup">
                    <xsl:variable name="id" select="substring(xhtml:a/@href,2)"/>
                    <xsl:variable name="lang">
                        <xsl:value-of select="ancestor::r:Content/@xml:lang"/>
                    </xsl:variable>
                    <!-- Creating a r:Content element -->
                    <xsl:element name="r:Content">
                        <xsl:attribute name="xml:lang" select="$lang"/>
                        <xsl:element name="xhtml:p">
                            <xsl:attribute name="class" select="'help'"/>
                            <!-- Applying the template of the corresponding link -->
                            <xsl:apply-templates
                                select="//xhtml:p[@id=$id and @xml:lang=$lang]/node()"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
        </xsl:copy>
    </xsl:template>

    <!-- Changing format of the displayed language from xx-XX to xx -->
    <xsl:template match="@xml:lang[.='fr-FR']">
        <xsl:attribute name="xml:lang" select="'fr'"/>
    </xsl:template>

    <xsl:template match="@xml:lang[.='en-IE']">
        <xsl:attribute name="xml:lang" select="'en'"/>
    </xsl:template>

    <!-- For each xhtml:a element not having a @href attribute : replacing it with an empty text -->
    <xsl:template match="xhtml:a[not(contains(@href,'#'))]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:attribute name="target">
                <xsl:value-of select="string('_blank')"/>
            </xsl:attribute>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Adding identifier to footnote type d:Instruction in xhtml:p elements -->
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
