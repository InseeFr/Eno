<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="xs"
    version="2.0">

    <!-- This stylesheet is a quick&dirty implementation of markdown2xhtml, but not all the markdown syntax is supported
        * : for italic
        ** : for bold
        *** : for both bold&italic
        \n : for line break

        It works by parsing first the "tags" (bold and italic which need creation of xhtml element), then elements (line break and other "element" which doesn't need to deal with embrace tags)
        It implements a black list (black-list param) for element that shouldn't be checked for markdown (as '*' is used in formula for example).
    -->
    <!-- black list param, it's a string list of the local name of excluded elements, ';' used as a separator -->
    <xsl:param name="black-list" select="'CommandContent;ExternalAid;'" as="xs:string"/>
    <!-- Char used for italic -->
    <xsl:variable name="italic-mw" select="'*'"/>
    <!-- Char used for bold -->
    <xsl:variable name="bold-mw" select="'**'"/>
    <!-- tags for italic open -->
    <xsl:variable name="i-open" select="'&lt;xhtml:i&gt;'"/>
    <!-- tags for italic close -->
    <xsl:variable name="i-close" select="'&lt;/xhtml:i&gt;'"/>
    <!-- tags for bold open -->
    <xsl:variable name="b-open" select="'&lt;xhtml:b&gt;'"/>
    <!-- tags for bold close -->
    <xsl:variable name="b-close" select="'&lt;/xhtml:b&gt;'"/>
    <!-- tags for p open -->
    <xsl:variable name="p-open" select="'&lt;xhtml:p&gt;'"/>
    <!-- tags for p close -->
    <xsl:variable name="p-close" select="'&lt;/xhtml:p&gt;'"/>
    <!-- Tokenized version of the black-list -->
    <xsl:variable name="_black-list" select="tokenize($black-list,';')"/>

    <!-- Standard identity pattern. -->
    <xsl:template match="*">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:choose>
                <xsl:when test="local-name(.) = $_black-list">
                    <!-- No MW parsing only a flat copy. -->
                    <xsl:copy-of select="node()"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- Default mode. -->
                    <xsl:apply-templates select="node()"/>
                </xsl:otherwise>
            </xsl:choose>

        </xsl:copy>
    </xsl:template>

    <!-- Keep comment and pi as well. -->
    <xsl:template match="comment() | processing-instruction()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <!-- Parsing is done on the text nodes for the default mode only -->
    <xsl:template match="text()[matches(normalize-space(replace(.,'&amp;#xd;', 'xhtml:br')),'[*]|xhtml:br|\[.*\]\(\. &quot;.*&quot;\)')]">
        <xhtml:p>
            <xsl:call-template name="parse-tags">
              <xsl:with-param name="expression" select="normalize-space(replace(.,'&amp;#xd;', 'xhtml:br'))"/>
            </xsl:call-template>
        </xhtml:p>
    </xsl:template>


    <!-- Parsing is done on the text nodes. -->
    <xsl:template match="text()">
        <xsl:copy-of select="."/>
        <!--  <xsl:call-template name="parse-tags">
              <xsl:with-param name="expression" select="normalize-space(replace(.,'&amp;#xd;', 'xhtml:br'))"/>
            </xsl:call-template>
            -->
    </xsl:template>

   <!-- <xsl:template match="text()">
            <xsl:call-template name="test-regex">
                <xsl:with-param name="expression" select="."/>
            </xsl:call-template>
    </xsl:template>-->

    <!--
        Parsing tags
        It's done by outputting directly the opening and closing tags (no guarantee of xml conformance, need to be very cautious)
    -->
    <xsl:template name="parse-tags">
        <!-- The expression to be parsed -->
        <xsl:param name="expression"/>
        <!-- If it's the first iteration of parsing-tags (for <p> handling) -->
        <xsl:param name="first" select="true()"/>
        <!-- If italic is open (needed to deal with embrace tags) -->
        <xsl:param name="italic" select="false()" as="xs:boolean"/>
        <!-- If bold is open (needed to deal with embrace tags) -->
        <xsl:param name="bold" select="false()" as="xs:boolean"/>
        <!-- To store which is the parent tags (to deal with order of close tags) -->
        <xsl:param name="parent" select="'none'"/>
        <!--
            Defining a regexp where :
            - regexp(1) is what is before the markdown tag, free from other tag.
            - regexp(2) is the first markdown tag encountered.
            - regexp(3) is what is after the markdown tag, could contains other tags.
            - non-matching is what doesn't have any markdown tags.
        -->
        <xsl:analyze-string select="$expression" regex="(^[^*]*)([*]{{1,3}})(([^*]{{1}}.*$)|$)">
            <xsl:matching-substring>
                <!-- Part in front of markdown tag (free from markdown tags) -->
                <xsl:call-template name="parse-elements">
                    <xsl:with-param name="expression" select="regex-group(1)"/>
                </xsl:call-template>
                <!-- Handling the html mark-up based on the markdown marker encountered. -->
                <xsl:choose>
                     <!-- Italic handling only. -->
                     <xsl:when test="regex-group(2)=$italic-mw">
                         <xsl:choose>
                             <!-- Italic not open => open italic. -->
                             <xsl:when test="not($italic)">
                                 <xsl:value-of select="$i-open" disable-output-escaping="yes"/>
                             </xsl:when>
                             <!-- Only italic open => close italic-->
                             <xsl:when test="$italic and not($bold)">
                                 <xsl:value-of select="$i-close" disable-output-escaping="yes"/>
                             </xsl:when>
                             <!-- Remaining case : italic&bold open and italic is parent => close b, close i, reopen b -->
                             <xsl:when test="$parent = 'italic'">
                                 <xsl:value-of select="concat($b-close,$i-close,$b-open)" disable-output-escaping="yes"/>
                             </xsl:when>
                             <!-- Last cases : italic&bold open and bold is parent => close i -->
                             <xsl:otherwise>
                                 <xsl:value-of select="$i-close" disable-output-escaping="yes"/>
                             </xsl:otherwise>
                         </xsl:choose>
                     </xsl:when>
                    <!-- Bold handling only.-->
                     <xsl:when test="regex-group(2)=$bold-mw">
                         <xsl:choose>
                             <xsl:when test="not($bold)">
                                 <xsl:value-of select="$b-open" disable-output-escaping="yes"/>
                             </xsl:when>
                             <xsl:when test="$bold and not($italic)">
                                 <xsl:value-of select="$b-close" disable-output-escaping="yes"/>
                             </xsl:when>
                             <xsl:when test="$parent = 'bold'">
                                 <xsl:value-of select="concat($i-close,$b-close,$i-open)" disable-output-escaping="yes"/>
                             </xsl:when>
                             <xsl:otherwise>
                                 <xsl:value-of select="$b-close" disable-output-escaping="yes"/>
                             </xsl:otherwise>
                         </xsl:choose>
                     </xsl:when>
                    <!-- Both italic & bold handling -->
                    <xsl:when test="regex-group(2)=concat($italic-mw,$bold-mw)">
                         <xsl:choose>
                             <!-- only italic open => close i, open b. -->
                             <xsl:when test="not($bold) and $italic">
                                 <xsl:value-of select="concat($i-close,$b-open)" disable-output-escaping="yes"/>
                             </xsl:when>
                             <!-- None open => open both, b has precedence. -->
                             <xsl:when test="not($bold) and not($italic)">
                                 <xsl:value-of select="concat($b-open,$i-open)" disable-output-escaping="yes"/>
                             </xsl:when>
                             <!-- both open => if italic is parent close b first, nor close i first. -->
                             <xsl:when test="$bold and $italic">
                                 <xsl:value-of select="if($parent = 'italic') then(concat($b-close,$i-close)) else(concat($i-close,$b-close))" disable-output-escaping="yes"/>
                             </xsl:when>
                             <!-- only bold open => close b, open i. -->
                             <xsl:when test="$bold and not($italic)">
                                 <xsl:value-of select="concat($b-close,$i-open)" disable-output-escaping="yes"/>
                             </xsl:when>
                         </xsl:choose>
                     </xsl:when>
                 </xsl:choose>
                <xsl:variable name="new-italic" select="if(regex-group(2)= $bold-mw) then($italic) else(not($italic))"/>
                <xsl:variable name="new-bold" select="if(regex-group(2)= $italic-mw) then($bold) else(not($bold))"/>
                <!-- Calculating the new parent considering each case. This is a quick&dirty implementation and doesn't support more syntaxes. -->
                <xsl:variable name="new-parent">
                    <xsl:choose>
                        <!-- Italic was parent and close during the step above. -->
                        <xsl:when test="$parent = 'italic' and regex-group(2)!=$bold-mw">
                            <!-- If bold is open, bold is the new parent. -->
                            <xsl:value-of select="if($bold) then('bold') else ('none')"/>
                        </xsl:when>
                        <!-- Bold was parent and close during the step above. -->
                        <xsl:when test="$parent = 'bold' and regex-group(2)!=$italic-mw">
                            <!-- If italic is open, italic is the new parent. -->
                            <xsl:value-of select="if($italic) then('italic') else ('none')"/>
                        </xsl:when>
                        <!-- Cover the both remaining case where bold or italic was parent and not closed. -->
                        <xsl:when test="$parent = 'bold' or $parent= 'italic'">
                            <xsl:value-of select="$parent"/>
                        </xsl:when>
                        <!-- None were parents. -->
                        <xsl:when test="$parent ='none'">
                            <!-- The new parent is the one opened, if both are, bold has precedence. -->
                            <xsl:value-of select="if(regex-group(2)!=$italic-mw) then ('bold') else('italic')"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:variable>
                <!-- Part after the markdown tag, could contains other tags. -->
                <xsl:call-template name="parse-tags">
                    <xsl:with-param name="first" select="false()"/>
                    <xsl:with-param name="expression" select="regex-group(3)"/>
                    <xsl:with-param name="italic" select="$new-italic"/>
                    <xsl:with-param name="bold" select="$new-bold"/>
                    <xsl:with-param name="parent" select="$new-parent"/>
                 </xsl:call-template>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <!-- Part without markdown tag. -->
                <xsl:call-template name="parse-elements">
                    <xsl:with-param name="expression" select="."/>
                </xsl:call-template>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xsl:template name="test-regex">
        <xsl:param name="expression"/>
        <xsl:param name="next" select="false()"/>
        <xsl:param name="italic" select="false()" as="xs:boolean"/>
        <xsl:param name="bold" select="false()" as="xs:boolean"/>
        <xsl:variable name="italic-mw" select="'*'"/>
        <xsl:variable name="bold-mw" select="'**'"/>
        <xsl:analyze-string select="$expression" regex="(\\n)|(\[(.*)\]\((.*)\)){{1}}">
            <xsl:matching-substring>
                regexp-group1 :
                <xsl:copy-of select="regex-group(1)"/>
                regexp-group2 :
                <xsl:copy-of select="regex-group(2)"/>
                regexp-group3 :
                <xsl:copy-of select="regex-group(3)"/>
                regexp-group4 :
                <xsl:copy-of select="regex-group(4)"/>
                regexp-group5 :
                <xsl:copy-of select="regex-group(5)"/>
                regexp-group6 :
                <xsl:copy-of select="regex-group(6)"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                Non-matching :
                <xsl:copy-of select="."/>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xsl:template name="parse-elements">
        <xsl:param name="expression"/>
        <xsl:param name="first" select="true()"/>
        <!--
            Defining a regexp where :
            - regexp(1) is the sequence \n if encountered, for breaklines,
            - regexp(2) is the sequence [.*](\. ".*") if encountered, for links.
            - regexp(3) is the sub-sequence ([^\)]+ if regexp(2) has matched, for the text associated to the link,
            - regexp(4) is the sub-sequence ([^&quot;]+) if regexp(2) has matched, for the url associated to the link.
        -->
        <xsl:analyze-string select="$expression" regex="(xhtml:br)|(\[([^\]]+)\]\(\. &quot;([^&quot;]+)&quot;\)){{1}}">
             <xsl:matching-substring>
                 <xsl:choose>
                     <!-- Breakline case -->
                     <xsl:when test="regex-group(1)">
                         <xsl:element name="xhtml:br"/>
                     </xsl:when>
                     <!-- Link case -->
                     <xsl:when test="regex-group(2)">
                         <xsl:element name="xhtml:a">
                             <xsl:attribute name="href" select="concat('. &quot;',regex-group(4),'&quot;')"/>
                             <xsl:copy-of select="regex-group(3)"/>
                         </xsl:element>
                     </xsl:when>
                 </xsl:choose>
                 <!--<xsl:call-template name="parse-elements">
                     <xsl:with-param name="expression" select="regex-group(4)"/>
                     <xsl:with-param name="first" select="$first"/>
                 </xsl:call-template>-->
             </xsl:matching-substring>
             <xsl:non-matching-substring>
                 <xsl:copy-of select="."/>
             </xsl:non-matching-substring>
         </xsl:analyze-string>
    </xsl:template>

</xsl:stylesheet>