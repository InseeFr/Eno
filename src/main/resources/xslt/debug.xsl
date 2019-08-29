<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    version="2.0">
    
    <!-- Apply transformation on generated xslt\transformations/in2out/in2out.xsl file and create xslt\transformations/in2out/in2out-debug.xsl file -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    <xsl:template match="node() | @*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xsl:import[contains(@href,'models')]">
        <xsl:variable name="models-debug" select="replace(@href,'models','models-debug')"/>
        <xsl:copy>
            <xsl:attribute name="href" select="$models-debug"/>
        </xsl:copy>
        <xsl:result-document href="{$models-debug}">
            <xsl:apply-templates select="//xsl:stylesheet" mode="debug"/>
        </xsl:result-document>
    </xsl:template>
    
    <xsl:template match="xsl:stylesheet" mode="debug">
        <xsl:copy copy-namespaces="yes">
            <xsl:namespace name="xs" select="'http://www.w3.org/2001/XMLSchema'"/>
            <xsl:copy-of select="@*"/>
            <xsl:element name="xsl:template">
                <xsl:attribute name="match" select="'*'"/>
                <xsl:attribute name="mode" select="'model'"/>
                <xsl:element name="xsl:param">
                    <xsl:attribute name="name" select="'source-context'"/>
                    <xsl:attribute name="as" select="'item()'"/>
                    <xsl:attribute name="tunnel" select="'yes'"/>
                </xsl:element>
                <xsl:element name="xsl:copy">
                    <xsl:attribute name="copy-namespaces" select="'no'"/>
                    <xsl:apply-templates select="xsl:function" mode="debug"/>
                    <xsl:element name="xsl:apply-templates">
                        <xsl:attribute name="select" select="'eno:child-fields($source-context)'"/>
                        <xsl:attribute name="mode" select="'source'"/>
                        <xsl:element name="xsl:with-param">
                            <xsl:attribute name="name" select="'driver'"/>
                            <xsl:attribute name="select" select="'.'"/>
                            <xsl:attribute name="tunnel" select="'yes'"/>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xsl:function[not(xsl:param/@name='variable') and not(xsl:param/@name='ip-id')]" mode="debug">
        <xsl:variable name="function-call">
            <xsl:value-of select="@name"/>
            <xsl:value-of select="'('"/>
            <xsl:for-each select="xsl:param">
                <xsl:if test="preceding-sibling::xsl:param">
                    <xsl:value-of select="','"/>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="@name='context'">
                        <xsl:value-of select="'$source-context'"/>
                    </xsl:when>
                    <!-- Always french -->
                    <xsl:when test="@name='language'">
                        <xsl:value-of select="'''fr'''"/>
                    </xsl:when>
                    <!-- Always the first line or column -->
                    <xsl:when test="@name='index'">
                        <xsl:value-of select="'1'"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:for-each>
            <xsl:value-of select="')'"/>
        </xsl:variable>
        
        <xsl:element name="xsl:choose">
            <xsl:element name="xsl:when">
                <xsl:attribute name="test" select="concat($function-call,' castable as xs:boolean')"/>
                <xsl:element name="xsl:element">
                    <xsl:attribute name="name" select="substring-after(@name,':')"/>
                    <xsl:element name="xsl:value-of">
                        <xsl:attribute name="select" select="'name()'"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="xsl:otherwise">
                <xsl:element name="xsl:if">
                    <xsl:attribute name="test" select="concat($function-call,' != ''''')"/>
                    <xsl:element name="xsl:element">
                        <xsl:attribute name="name" select="substring-after(@name,':')"/>
                        <xsl:element name="xsl:choose">
                            <xsl:element name="xsl:when">
                                <xsl:attribute name="test" select="concat($function-call,' castable as xs:string')"/>
                                <xsl:element name="xsl:copy-of">
                                    <xsl:attribute name="copy-namespaces" select="'no'"/>
                                    <xsl:attribute name="select" select="$function-call"/>
                                </xsl:element>
                            </xsl:element>
                            <xsl:element name="xsl:when">
                                <xsl:attribute name="test" select="concat($function-call,'[1] castable as xs:string')"/>
                                <xsl:element name="xsl:for-each">
                                    <xsl:attribute name="select" select="$function-call"/>
                                    <xsl:element name="xsl:value-of">
                                        <xsl:attribute name="select" select="'.'"/>
                                    </xsl:element>
                                    <xsl:element name="xsl:text">
                                        <xsl:text>&#xA;</xsl:text>
                                    </xsl:element>
                                </xsl:element>
                            </xsl:element>
                            <xsl:element name="xsl:when">
                                <!--<xsl:attribute name="test" select="concat($function-call,'//node()')"/>-->
                                <xsl:attribute name="test" select="concat($function-call,'[1] is node()')"/>
                                <xsl:element name="xsl:value-of">
                                    <xsl:attribute name="select" select="'''liste des éléments : '''"/>
                                </xsl:element>
                                <xsl:element name="xsl:for-each">
                                    <xsl:attribute name="select" select="$function-call"/>
                                    <xsl:element name="xsl:value-of">
                                        <xsl:attribute name="select" select="'name()'"/>
                                    </xsl:element>
                                    <xsl:element name="xsl:text">
                                        <xsl:text>&#xA;</xsl:text>
                                    </xsl:element>
                                </xsl:element>
                            </xsl:element>
                            <xsl:element name="xsl:otherwise">
                                <xsl:element name="xsl:copy-of">
                                    <xsl:attribute name="copy-namespaces" select="'no'"/>
                                    <xsl:attribute name="select" select="$function-call"/>
                                </xsl:element>
                            </xsl:element>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="xsl:function[xsl:param/@name='variable' or xsl:param/@name='ip-id']" mode="debug">
        <xsl:element name="xsl:element">
            <xsl:attribute name="name" select="substring-after(@name,':')"/>
            <xsl:element name="xsl:value-of">
                <xsl:attribute name="select" select="'''needs variable param'''"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>