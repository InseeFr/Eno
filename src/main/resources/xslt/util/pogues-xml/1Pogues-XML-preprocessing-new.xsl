<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    exclude-result-prefixes="xs xd"
    version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Aug 22, 2017</xd:p>
            <xd:p><xd:b>Author:</xd:b> nirnfv</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    
    <xsl:output indent="yes" omit-xml-declaration="yes"/> 
    
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="./*"/>
    </xsl:template>
    
    <xd:doc>
        id attribute
        <xd:desc/>
    </xd:doc>
    <xsl:template match="pogues:Questionnaire | pogues:Declaration | pogues:CodeList | pogues:Code | pogues:Child | pogues:Response | pogues:IfThenElse">
        <!--xsl:message>HELLO : <xsl:value-of select="@id"/>::<xsl:value-of select="matches(@id,'^[A-Za-z0-9\*@$\-_]+$')"/></xsl:message-->
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="not(@id)">
                    <!-- It is an error-->
                    <!--xsl:message>Missing id in <xsl:value-of select="name()"/></xsl:message-->
                    <xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="matches(@id,'^[A-Za-z0-9\*@$\-_]+$')">
                    <!-- It is OK-->
                    <xsl:copy-of select="@id"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- It is an error-->
                    <xsl:message>Uncorrect id in <xsl:value-of select="name()"/> : <xsl:value-of select="@id"/></xsl:message>
                    <xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:copy-of select="@*[name() != 'id' and name() != 'agency']"/>
            <xsl:choose>
                <xsl:when test="not(@agency)">
                    <!-- It is OK-->
                </xsl:when>
                <xsl:when test="matches(@agency,'^[a-zA-Z0-9\-]{1,63}(\.[a-zA-Z0-9\-]{1,63})*$')">
                    <!-- It is OK-->
                    <xsl:copy-of select="@agency"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:message>Incorrect agency in <xsl:value-of select="name()"/> : <xsl:value-of select="@agency"/></xsl:message>
                    <!-- It is an error-->
                    <xsl:attribute name="agency"><xsl:value-of select="replace(@agency,'[^a-z^A-^Z^0-9^\-]','')"/></xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:copy-of select="text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="*"/>
        </xsl:copy>    
    </xsl:template>

    <xd:doc>
        id value
        <xd:desc/>
    </xd:doc>
    <xsl:template match="pogues:CodeListReference">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="not(text())">
                    <!-- It is an error-->
                    <xsl:message>Missing id in <xsl:value-of select="name()"/></xsl:message>
                    <xsl:value-of select="generate-id()"/>
                </xsl:when>
                <xsl:when test="matches(text(),'^[A-Za-z0-9\*@$\-_]+$')">
                    <!-- It is OK-->
                    <xsl:copy-of select="text()"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- It is an error-->
                    <xsl:message>Uncorrect id in <xsl:value-of select="name()"/> : <xsl:value-of select="text()"/></xsl:message>
                    <xsl:value-of select="generate-id()"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:copy-of select="@*  | comment() | processing-instruction()"/>
            <xsl:apply-templates select="*"/>
        </xsl:copy>    
    </xsl:template>
    
    <xd:doc>
        no id
        <xd:desc/>
    </xd:doc>
    <xsl:template match="pogues:GoTo | pogues:Survey | pogues:ComponentGroup | pogues:MemberReference | pogues:Datatype |  pogues:Label | pogues:Name | pogues:Text | pogues:Expression | pogues:IfTrue | pogues:Value | pogues:MaxLength | pogues:Pattern | pogues:Minimum | pogues:Maximum | pogues:Decimal | pogues:Format | pogues:ResponseStructure | pogues:TotalLabel | pogues:Control | pogues:FailMessage | pogues:CodeLists | pogues:Decimals">
        <xsl:copy><xsl:copy-of select="@* | text() | comment() | processing-instruction()"/><xsl:apply-templates select="*"/></xsl:copy>    
    </xsl:template>
    
    <xsl:template match="pogues:Dimension">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="not(@dynamic)"/>                                   
                <xsl:when test="@dynamic='0' or matches(@dynamic,'^[0-9]+-[0-9]+$')">
                    <xsl:copy-of select="@dynamic"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:message>Uncorrect dynamic in <xsl:value-of select="name()"/> : <xsl:value-of select="@dynamic"/></xsl:message>
                    <!--it is an error-->
                </xsl:otherwise>
            </xsl:choose>
            <xsl:copy-of select="@*[name() != 'dynamic'] | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="*"/>
        </xsl:copy> 
    </xsl:template>
    
    <xsl:template match="node()" >
        <xsl:message>Unaccounted node : <xsl:value-of select="name()"/></xsl:message>
        <xsl:copy>
            <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
            <xsl:apply-templates select="*"/>
        </xsl:copy>    
    </xsl:template>
    
</xsl:stylesheet>