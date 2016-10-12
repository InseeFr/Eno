<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:il="http://xml/insee.fr/xslt/lib" xmlns:iat="http://xml/insee.fr/xslt/apply-templates" exclude-result-prefixes="#all" version="2.0">

    <!-- This stylesheet is imported in several files : ddi2fr.xsl, fods2xml.xsl, xml2xsl.xsl -->
    <!-- lib.xsl defines functions used in the previous files -->
    <!-- Also clearing the xml file on which its applied by deleting some elements (comments, instructions) -->

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Apr 9, 2013</xd:p>
            <xd:p><xd:b>Author:</xd:b> vdv</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>Appends a new empty element in a fragment as a last child of $target</xd:desc>
    </xd:doc>
    <xsl:function name="il:append-empty-element" as="element()">
        <xsl:param name="source" as="xs:string"/>
        <xsl:param name="target" as="item()"/>
        <xsl:variable name="new-element" as="element()">
            <xsl:element name="{$source}">
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('_', count(root($target)//*))"/>
                </xsl:attribute>
            </xsl:element>
        </xsl:variable>
        <xsl:variable name="new-fragment">
            <xsl:apply-templates select="$target/root()" mode="il:append">
                <xsl:with-param name="source" select="$new-element" tunnel="yes"/>
                <xsl:with-param name="target" select="$target" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:sequence select="$new-fragment//*[@id=$new-element/@id]"/>
    </xsl:function>

    <xsl:template match="/|*" mode="il:append">
        <xsl:param name="source" as="item()" tunnel="yes"/>
        <xsl:param name="target" as="item()" tunnel="yes"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()" mode="il:append"/>
            <xsl:if test=". is $target">
                <xsl:copy-of select="$source"/>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:function name="il:is-rich-content" as="xs:boolean">
        <xsl:param name="node-set"/>
        <xsl:sequence select="if ($node-set instance of xs:string) then false() else boolean($node-set//node())"/>
    </xsl:function>

    <!-- To display an xml tree like a text chain -->
    <xsl:function name="il:serialize" as="xs:string">
        <!-- Input : a node -->
        <xsl:param name="node-set"/>
        <!-- Creating a variable -->
        <xsl:variable name="rooted">
            <root>
                <xsl:choose>
                    <!-- If the node is actually only text : recopying -->
                    <xsl:when test="$node-set instance of xs:string">
                        <xsl:copy-of select="$node-set"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- If the node is an xml tree, we get rid of the tags and we transform then into text -->
                        <xsl:apply-templates select="$node-set" mode="il:remove-xml"/>
                    </xsl:otherwise>
                </xsl:choose>
            </root>
        </xsl:variable>
        <!-- Then we return the text -->
        <xsl:sequence select="$rooted/root"/>
    </xsl:function>

    <!-- Used to transform the xml into text, concerning the attributes -->
    <xsl:template match="@*" mode="il:remove-xml">
        <xsl:text> </xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text>="</xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>"</xsl:text>
    </xsl:template>

    <!-- If it is already text-typed, we return it, nothing more to do -->
    <xsl:template match="text()" mode="il:remove-xml">
        <xsl:value-of select="."/>
    </xsl:template>

    <!-- Transforming the children free node into text -->
    <xsl:template match="*[not(child::node())]" mode="il:remove-xml">
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:apply-templates select="@*" mode="il:remove-xml"/>
        <xsl:text>/&gt;</xsl:text>
    </xsl:template>

    <!-- Transforming the node that has children into text -->
    <xsl:template match="*[child::node()]" mode="il:remove-xml">
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:apply-templates select="@*" mode="il:remove-xml"/>
        <xsl:text>&gt;</xsl:text>
        <xsl:apply-templates select="node()" mode="il:remove-xml"/>
        <xsl:text>&lt;/</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:text>&gt;</xsl:text>
    </xsl:template>

    <!-- Getting rid of the instructions and comments -->
    <xsl:template match="processing-instruction()|comment()" mode="il:remove-xml"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Function to get the children of an element, common to every input and output.</xd:p>
            <xd:p>Indeed, every output will need the follow-up of the input tree, and every input must have a function describing the tree's parsing.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iat:child-fields" as="node()*">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iat:child-fields"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template to parse the xml tree while getting the children of an element.</xd:p>
            <xd:p>But the function can be overloaded if we need to parse the tree in a non-classic way.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="@*|node()" mode="iat:child-fields" as="node()*">
        <xsl:sequence select="./(@*|node())"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>If a node doesn't return anything, we continue with it's children elements.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="source">
        <xsl:apply-templates select="iat:child-fields(.)" mode="source"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Elements that aren't nodes won't trigger anything from the source side.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="@*|text()|processing-instruction()|comment()" mode="source"/>

</xsl:stylesheet>
