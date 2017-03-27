<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno" version="2.0">

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This stylesheet contains functions and templates common to all xslt
                transformations of Eno.</xd:p>
        </xd:desc>
    </xd:doc>

    <xd:doc>
        <xd:desc>
            <xd:p>This function appends a new empty element into an other existing element.</xd:p>
            <xd:p>This function is written in a way that the context is not lost.</xd:p>
            <xd:p>This function is used to build the virtual trees that are used in templates of the
                output stylesheets in Eno.</xd:p>
        </xd:desc>
        <xd:param name="source">The name of the new empty element to be created.</xd:param>
        <xd:param name="target">The existing element in which the new empty element is
            created.</xd:param>
        <xd:return>It returns a sequence whose context is on the new element (the last
            child).</xd:return>
    </xd:doc>
    <xsl:function name="eno:append-empty-element" as="element()">
        <xsl:param name="source" as="xs:string"/>
        <xsl:param name="target" as="item()"/>
        <xsl:variable name="new-element" as="element()">
            <!-- The new element is created -->
            <xsl:element name="{$source}">
                <!-- His rank is calculated in the whole tree and placed in an attribute -->
                <xsl:attribute name="id">
                    <xsl:value-of select="concat('_', count(root($target)//*))"/>
                </xsl:attribute>
            </xsl:element>
        </xsl:variable>
        <!-- The whole tree is built again with the new element added -->
        <xsl:variable name="new-fragment">
            <xsl:apply-templates select="$target/root()" mode="eno:append">
                <xsl:with-param name="source" select="$new-element" tunnel="yes"/>
                <xsl:with-param name="target" select="$target" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:variable>
        <!-- The context is placed on this new element -->
        <xsl:sequence select="$new-fragment//*[@id=$new-element/@id]"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>This mode is used to rebuild a tree, and create the source element into the target
                element of the tree.</xd:p>
        </xd:desc>
        <xd:param name="source">The new element to be created.</xd:param>
        <xd:param name="target">The existing element in which the new element is created.</xd:param>
    </xd:doc>
    <xsl:template match="/|*" mode="eno:append">
        <xsl:param name="source" as="item()" tunnel="yes"/>
        <xsl:param name="target" as="item()" tunnel="yes"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="node()" mode="eno:append"/>
            <!-- When the target element is reached, the source element is created -->
            <xsl:if test=". is $target">
                <xsl:copy-of select="$source"/>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function to get the children of an element, common to every transformation in
                Eno.</xd:p>
            <xd:p>Indeed, every output will need the follow-up of the input tree, and every input
                must have a function describing the tree's parsing.</xd:p>
        </xd:desc>
        <xd:param name="context">The current context.</xd:param>
        <xd:return>The next element which shall get context.</xd:return>
    </xd:doc>
    <xsl:function name="eno:child-fields" as="node()*">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="eno:child-fields"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Default template to parse the xml tree while getting the children of an
                element.</xd:p>
            <xd:p>But the function can be overloaded if we need to parse the tree in a non-classic
                way.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="@*|node()" mode="eno:child-fields" as="node()*">
        <xsl:sequence select="./(@*|node())"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>By default, if an input xml node is not linked to a driver in the output side, we
                continue with it's children elements.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*" mode="source">
        <xsl:apply-templates select="eno:child-fields(.)" mode="source"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Elements that aren't nodes won't trigger anything from the source side (they
                cannot be linked to a driver).</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="@*|text()|processing-instruction()|comment()" mode="source"/>

    <xd:doc>
        <xd:desc>
            <xd:p>This function determines if a set of node is a simple text or a true xml
                tree.</xd:p>
        </xd:desc>
        <xd:param name="node-set">The set of nodes which is checked.</xd:param>
        <xd:return>false() if it is a text, true() if it is an xml tree.</xd:return>
    </xd:doc>
    <xsl:function name="eno:is-rich-content" as="xs:boolean">
        <xsl:param name="node-set"/>
        <xsl:sequence
            select="if ($node-set instance of xs:string) then false() else boolean($node-set//node())"
        />
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Escape an xml tree to get the corresponding text chain.</xd:p>
        </xd:desc>
        <xd:param name="node-set">The xml tree to serialize.</xd:param>
        <xd:return>The text chain corresponding to the xml tree.</xd:return>
    </xd:doc>
    <xsl:function name="eno:serialize" as="xs:string">
        <xsl:param name="node-set"/>
        <xsl:variable name="result">
            <xsl:apply-templates select="$node-set" mode="eno:serialize"/>
        </xsl:variable>
        <xsl:value-of select="$result"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Serialization of an empty node into text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[not(child::node())]" mode="eno:serialize">
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:apply-templates select="@*" mode="eno:serialize"/>
        <xsl:text>/&gt;</xsl:text>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Serialization of a node which is not empty into text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="*[child::node()]" mode="eno:serialize">
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:apply-templates select="@*" mode="eno:serialize"/>
        <xsl:text>&gt;</xsl:text>
        <xsl:apply-templates select="node()" mode="eno:serialize"/>
        <xsl:text>&lt;/</xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:text>&gt;</xsl:text>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Serialization of an attribute into text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="@*" mode="eno:serialize">
        <xsl:value-of select="' '"/>
        <xsl:value-of select="name()"/>
        <xsl:text>="</xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>"</xsl:text>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Serialization of a text node into text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="text()" mode="eno:serialize">
        <xsl:value-of select="."/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>When a tree is serialized into text, processing-instructions and comments are not relevant.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="processing-instruction()|comment()" mode="eno:serialize"/>

    <xd:doc>
        <xd:desc>
            <xd:p>This function builds a set of label resources in different languages.</xd:p>
        </xd:desc>
        <xd:param name="folder">The folder containing the label resources for a given output.</xd:param>
        <xd:param name="languages">The list of languages that are wanted among those available in the folder.</xd:param>
    </xd:doc>
    <xsl:function name="eno:build-labels-resource">
        <xsl:param name="folder"/>
        <xsl:param name="languages" as="xs:string*"/>
        <Languages>
            <xsl:for-each
                select="collection(concat('file:///', replace($folder, '\\' , '/'), '?select=*.xml'))/Language">
                <xsl:if test="@xml:lang=$languages">
                    <xsl:copy-of select="."/>
                </xsl:if>
            </xsl:for-each>
        </Languages>
    </xsl:function>

</xsl:stylesheet>
