<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:fn="http://www.w3.org/2005/xpath-functions" 
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="xs fn xd xsi " version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8" />
    
    <xsl:variable name="default-parameters-adress" select="'../../../params/default/parameters.xml'"/>
    <xsl:variable name="default-parameters" select="document($default-parameters-adress)"/>
    
    <xsl:variable name="root" select="root(.)"/>
    
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="/">
        <xsl:apply-templates select="$default-parameters/*"/>
    </xsl:template>
    
    <xsl:template match="Pipeline">
        <xsl:copy-of select="$root//Pipeline"/>
    </xsl:template>
    
    <xsl:template match="StudyUnit">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$root//StudyUnit!=''">
                    <xsl:value-of select="$root//StudyUnit"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="Campagne">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$root//Campagne!=''">
                    <xsl:value-of select="$root//Campagne"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="Languages">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$root//Languages!=''">
                    <xsl:copy-of select="$root//Languages/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="BeginQuestion">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$root//BeginQuestion/Identification!=''">
                    <xsl:copy-of select="$root//BeginQuestion/Identification"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./Identification"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="EndQuestion">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$root//EndQuestion/ResponseTimeQuestion!=''">
                    <xsl:copy-of select="$root//EndQuestion/ResponseTimeQuestion"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./ResponseTimeQuestion"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//EndQuestion/CommentQuestion!=''">
                    <xsl:copy-of select="$root//EndQuestion/CommentQuestion"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./CommentQuestion"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="fr-parameters">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$root//fr-parameters/NumericExample!=''">
                    <xsl:copy-of select="$root//fr-parameters/NumericExample"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./NumericExample"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//fr-parameters/Deblocage!=''">
                    <xsl:copy-of select="$root//fr-parameters/Deblocage"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./Deblocage"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//fr-parameters/Satisfaction!=''">
                    <xsl:copy-of select="$root//fr-parameters/Satisfaction"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./Satisfaction"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//fr-parameters/LengthOfLongTable!=''">
                    <xsl:copy-of select="$root//fr-parameters/LengthOfLongTable"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./LengthOfLongTable"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//fr-parameters/DecimalSeparator!=''">
                    <xsl:copy-of select="$root//fr-parameters/DecimalSeparator"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./DecimalSeparator"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:copy-of select="$root//fr-parameters/Css"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="pdf-parameters">
        <xsl:copy>
            <xsl:element name="Format">
                <xsl:choose>
                    <xsl:when test="$root//pdf-parameters/Format/Orientation!=''">
                        <xsl:copy-of select="$root//pdf-parameters/Format/Orientation"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="./Format/Orientation"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:choose>
                    <xsl:when test="$root//pdf-parameters/Format/Columns!=''">
                        <xsl:copy-of select="$root//pdf-parameters/Format/Columns"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="./Format/Columns"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element>            
            <xsl:choose>
                <xsl:when test="$root//pdf-parameters/Roster!=''">
                    <xsl:copy-of select="$root//pdf-parameters/Roster"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./Roster"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//pdf-parameters/TextArea!=''">
                    <xsl:copy-of select="$root//pdf-parameters/TextArea"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./TextArea"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//pdf-parameters/Table!=''">
                    <xsl:copy-of select="$root//pdf-parameters/Table"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./Table"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//pdf-parameters/Capture!=''">
                    <xsl:copy-of select="$root//pdf-parameters/Capture"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./Capture"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//pdf-parameters/PageBreakBetween!=''">
                    <xsl:copy-of select="$root//pdf-parameters/PageBreakBetween"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./PageBreakBetween"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$root//pdf-parameters/AccompanyingMail!=''">
                    <xsl:copy-of select="$root//pdf-parameters/AccompanyingMail"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./AccompanyingMail"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="js-parameters">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$root//js-parameters/FilterDescription!=''">
                    <xsl:copy-of select="$root//js-parameters/FilterDescription"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./FilterDescription"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="odt-parameters">
        <xsl:copy/>
    </xsl:template>
    
    <xsl:template match="Title">
        <xsl:copy>
            <xsl:choose>
                <xsl:when test="$root//Title/Browsing!=''">
                    <xsl:copy-of select="$root//Title/Browsing"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./Browsing"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="./*[not(self::Browsing)]"></xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="Sequence">
        <xsl:copy>
            <xsl:for-each select="Level">
                <xsl:choose>
                    <xsl:when test="@name='module'">
                        <xsl:copy>
                            <xsl:copy-of select="@name"/>
                            <xsl:choose>
                                <xsl:when test="$root//Title/Sequence/Level[@name='module']/PreSeq">
                                    <xsl:copy-of select="$root//Title/Sequence/Level[@name='module']/PreSeq"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:copy-of select="./PreSeq"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$root//Title/Sequence/Level[@name='module']/NumParent">
                                    <xsl:copy-of select="$root//Title/Sequence/Level[@name='module']/NumParent"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:copy-of select="./NumParent"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$root//Title/Sequence/Level[@name='module']/PostNumParentSeq">
                                    <xsl:copy-of select="$root//Title/Sequence/Level[@name='module']/PostNumParentSeq"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:copy-of select="./PostNumParentSeq"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$root//Title/Sequence/Level[@name='module']/StyleNumSeq">
                                    <xsl:copy-of select="$root//Title/Sequence/Level[@name='module']/StyleNumSeq"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:copy-of select="./StyleNumSeq"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$root//Title/Sequence/Level[@name='module']/PostNumSeq">
                                    <xsl:copy-of select="$root//Title/Sequence/Level[@name='module']/PostNumSeq"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:copy-of select="./PostNumSeq"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:copy>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="levelName" select="@name"/>
                        <xsl:copy-of select="$root//Title/Sequence/Level[@name=$levelName]"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="Question">
        <xsl:copy>
            <xsl:for-each select="Level">
                <xsl:variable name="levelName" select="@name"/>
                <xsl:copy>
                    <xsl:copy-of select="@name"/>
                    <xsl:choose>
                        <xsl:when test="$root//Title/Question/Level[@name=$levelName]/PreQuest">
                            <xsl:copy-of select="$root//Title/Question/Level[@name=$levelName]/PreQuest"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="./PreQuest"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        <xsl:when test="$root//Title/Question/Level[@name=$levelName]/NumParent">
                            <xsl:copy-of select="$root//Title/Question/Level[@name=$levelName]/NumParent"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="./NumParent"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        <xsl:when test="$root//Title/Question/Level[@name=$levelName]/PostNumParentQuest">
                            <xsl:copy-of select="$root//Title/Question/Level[@name=$levelName]/PostNumParentQuest"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="./PostNumParentQuest"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        <xsl:when test="$root//Title/Question/Level[@name=$levelName]/StyleNumQuest">
                            <xsl:copy-of select="$root//Title/Question/Level[@name=$levelName]/StyleNumQuest"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="./StyleNumQuest"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        <xsl:when test="$root//Title/Question/Level[@name=$levelName]/PostNumQuest">
                            <xsl:copy-of select="$root//Title/Question/Level[@name=$levelName]/PostNumQuest"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="./PostNumQuest"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:copy>
                
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>