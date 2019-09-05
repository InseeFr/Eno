<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" encoding="utf-8"/>
    <!-- Add generic comment question at the end of questionnaire -->

    <xsl:template match="/">
        <xsl:apply-templates select="xhtml:html"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Generic template for all elements : copy all the elements or attributes, one by one</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add elements to the main instance</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xf:instance[@id='fr-form-instance']/form">
        <xsl:copy>
            <xsl:apply-templates select="node()"/>
            <INSEE-HOUSEHOLD-COMMENT>
                <COMMENT_QE/>
            </INSEE-HOUSEHOLD-COMMENT>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add elements to the corresponding bind</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xf:bind[@id='fr-form-instance-binds']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()"/>
            <xf:bind id="INSEE-HOUSEHOLD-COMMENT-bind" name="INSEE-HOUSEHOLD-COMMENT" ref="INSEE-HOUSEHOLD-COMMENT">
                <xf:bind id="COMMENT_QE-bind" name="COMMENT_QE" ref="COMMENT_QE"/>
            </xf:bind>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add elements to french resources</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xf:instance[@id='fr-form-resources']/resources/resource[@xml:lang='fr']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()"/>
            <INSEE-HOUSEHOLD-COMMENT>
                <label>Commentaires</label>
            </INSEE-HOUSEHOLD-COMMENT>
            <COMMENT_QE>
                <label>➡ Commentaires et remarques éventuelles concernant l’enquête :</label>
            </COMMENT_QE>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add the new page</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xf:instance[@id='fr-form-util']/Util/Pages/End">
        <INSEE-HOUSEHOLD-COMMENT/>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add elements into the body</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="fr:view/fr:body">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()"/>
            <fr:section id="INSEE-HOUSEHOLD-COMMENT-control" bind="INSEE-HOUSEHOLD-COMMENT-bind" name="INSEE-HOUSEHOLD-COMMENT">
                <xf:label ref="$form-resources/INSEE-HOUSEHOLD-COMMENT/label"/>
                <xf:textarea id="COMMENT_QE-control" name="COMMENT_QE" bind="COMMENT_QE-bind"
                    class="question text text2000" xxf:order="label control" xxf:maxlength="2000">
                    <xf:label ref="$form-resources/COMMENT_QE/label"/>
                </xf:textarea>
            </fr:section>
        </xsl:copy>
    </xsl:template>

</xsl:transform>
