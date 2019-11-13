<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs eno"
    xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" 
    xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
    version="2.0">
        
    <xsl:function name="eno:Office-styles">
        <xsl:param name="source-context"/>
        <style:style style:name="Standard" style:family="paragraph" style:class="text"/>
        <style:style style:name="Title" style:family="paragraph" style:class="chapter">
            <style:paragraph-properties fo:text-align="center" fo:margin-top="3cm"
                style:justify-single-word="false"/>
            <style:text-properties fo:font-size="36pt" fo:font-weight="bold" fo:color="#7b7c7c"/>
        </style:style>
        <style:style style:name="TitleComment" style:family="paragraph">
            <style:paragraph-properties fo:text-align="center" fo:margin-top="3cm"
                style:justify-single-word="false"/>
            <style:text-properties fo:font-size="22pt" fo:font-weight="bold"/>
        </style:style>
        <style:style style:name="Module" style:family="paragraph" style:default-outline-level="1"
            style:class="text">
            <style:paragraph-properties fo:text-align="center" fo:break-before="page"/>
            <style:text-properties fo:font-size="14pt" fo:font-weight="bold"/>
        </style:style>
        <style:style style:name="SubModule" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:margin-top="0.6cm" fo:text-align="left"/>
            <style:text-properties fo:font-size="14pt" fo:font-weight="bold"/>
        </style:style>
        <style:style style:name="QuestionName" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:margin-top="0.4cm" fo:text-align="left"/>
            <style:text-properties fo:font-size="10pt" fo:font-style="italic" fo:font-weight="bold"
                fo:color="#a5106c"/>
        </style:style>
        <style:style style:name="Question" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:margin-top="0.1cm" fo:text-align="left"/>
            <style:text-properties fo:font-size="10pt"/>
        </style:style>
        <style:style style:name="Format" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:text-properties fo:font-size="10pt"/>
        </style:style>
        <style:style style:name="NameOfVariable" style:family="text">
            <style:text-properties fo:color="#0000ff" fo:font-style="italic" fo:font-weight="bold"/>
        </style:style>
        <style:style style:name="GotoBlock" style:family="paragraph"
            style:class="text">
            <style:text-properties fo:color="#0000ff" fo:font-weight="bold"/>
            <style:paragraph-properties fo:text-align="left" fo:margin-top="0.7cm"
                fo:border="0.05pt solid #000000"/>
        </style:style>
        <style:style style:name="GotoTitle" style:family="text">
            <style:text-properties fo:color="#0000ff" fo:font-weight="bold"/>
        </style:style>
        <style:style style:name="GotoText" style:family="text">
            <style:text-properties fo:color="#0000ff"/>
        </style:style>
        
        <!-- <style:style style:name="QuestionSelect" style:family="paragraph"
					style:default-outline-level="2" style:class="text">
					<style:paragraph-properties fo:text-align="left"/>
					<style:text-properties fo:font-size="10pt" fo:font-weight="bold"/>
					</style:style> -->
        <style:style style:name="Comment" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:margin-top="0.1cm" fo:text-align="left"/>
            <style:text-properties fo:font-size="10pt" fo:color="#006600"/>
        </style:style>
        <style:style style:name="Help" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:margin-top="0.1cm" fo:text-align="left"/>
            <style:text-properties fo:font-size="10pt" fo:color="#0000ff" style:text-underline-style="solid"
            />
        </style:style>
        <style:style style:name="Instruction" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:margin-top="0.1cm" fo:text-align="left"/>
            <style:text-properties fo:font-size="10pt" fo:color="#0000ff"/>
        </style:style>
        <style:style style:name="Warning" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:text-align="left" fo:border="0.05pt solid #000000"
                fo:margin-top="0.1cm"/>
            <style:text-properties fo:font-size="10pt" fo:font-weight="bold" fo:color="#ff3333"/>
        </style:style>
        <style:style style:name="CodeItem" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:text-align="left" fo:margin-left="1cm" fo:margin-top="0.1cm"/>
            <style:text-properties fo:font-size="10pt"/>
        </style:style>
        <style:style style:name="CalculatedVariableTitle" style:family="paragraph"
            style:default-outline-level="2" style:class="text">
            <style:paragraph-properties fo:text-align="left" fo:margin-top="0.7cm"/>
            <style:text-properties fo:font-size="10pt" fo:font-weight="bold"/>
        </style:style>
        <style:style style:name="CalculatedVariableContent" style:family="paragraph"
            style:default-outline-level="2" style:class="text">
            <style:paragraph-properties fo:text-align="left" fo:margin-top="0.3cm"/>
            <style:text-properties fo:font-size="10pt"/>
        </style:style>
        <style:style style:name="Control" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:text-align="left" fo:margin-top="0.7cm"
                fo:border="0.05pt solid #000000"/>
            <style:text-properties fo:font-size="10pt" fo:font-weight="bold" fo:color="#0000ff"/>
        </style:style>
        <style:style style:name="MultipleChoice" style:family="table">
            <style:table-properties fo:break-before="page"/>
        </style:style>
        <style:style style:name="MultipleChoice.Column" style:family="table-column"/> 
        <style:style style:name="MultipleChoice.Cell" style:family="table-cell"/> 
        <style:style style:name="Table" style:family="table">
            <style:table-properties fo:margin-top="1cm" style:shadow="none"/>
        </style:style>
        <style:style style:name="Table.Column" style:family="table-column"/> 
        <style:style style:name="Table.Cell" style:family="table-cell">
            <style:table-cell-properties fo:padding="0cm" fo:border="0.05pt solid #000000"/>
        </style:style>
        <style:style style:name="ColumnHeader" style:family="paragraph" style:default-outline-level="2"
            style:class="text">
            <style:paragraph-properties fo:text-align="center"/>
            <style:text-properties fo:font-size="10pt" fo:font-weight="bold"/>
        </style:style>
        <style:style style:name="Table.Cell.deleted" style:family="table-cell">
            <style:table-cell-properties  fo:background-color="#f79e9e" fo:padding="0cm" fo:border="0.05pt solid #000000"/>
        </style:style>
        <style:style style:name="Table.Cell.added" style:family="table-cell">
            <style:table-cell-properties  fo:background-color="#9ef7ac" fo:padding="0cm" fo:border="0.05pt solid #000000"/>
        </style:style>
        <style:style style:name="deleted" style:family="text" style:default-outline-level="2">
            <style:text-properties fo:background-color="#f79e9e" style:text-line-through-style="solid" style:text-line-through-type="single"/>
        </style:style>
        <style:style style:name="added" style:family="text" style:default-outline-level="2">
            <style:text-properties fo:background-color="#9ef7ac" />
        </style:style>
        <style:style style:name="sectionDeleted" style:family="section">
            <style:section-properties fo:background-color="#f79e9e" style:editable="false">
                <style:columns fo:column-count="1" fo:column-gap="0cm"/>
                <style:background-image/>
            </style:section-properties>
        </style:style>
        <style:style style:name="sectionAdded" style:family="section">
            <style:section-properties fo:background-color="#9ef7ac" style:editable="false">
                <style:columns fo:column-count="1" fo:column-gap="0cm"/>
                <style:background-image/>
            </style:section-properties>
        </style:style>
        <!-- styles from input file -->
        <style:style style:name="bold" style:family="text">
            <style:text-properties fo:font-weight="bold" style:font-weight-asian="bold" style:font-weight-complex="bold"/>
        </style:style>
        <style:style style:name="italic" style:family="text">
            <style:text-properties fo:font-style="italic" style:font-style-asian="italic" style:font-style-complex="italic"/>
        </style:style>
        <style:style style:name="underline" style:family="text">
            <style:text-properties style:text-underline-style="solid" style:text-underline-width="auto" style:text-underline-color="font-color"/>
        </style:style>
    </xsl:function>


</xsl:stylesheet>

