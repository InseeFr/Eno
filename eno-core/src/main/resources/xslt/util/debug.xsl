<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  version="2.0">

  <!-- Apply transformation on generated xslt\transformations/in2out/in2out.xsl file and create xslt\transformations/in2out/in2out-debug.xsl file -->
  <xsl:param name="source-file"/>
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
  <xsl:strip-space elements="*"/>

  <xsl:variable name="source" select="doc($source-file)"/>
  <xsl:variable name="excluded-variables">
    <Variables>
      <Variable>variable</Variable>
      <Variable>ip-id</Variable>
      <Variable>label</Variable>
      <Variable>formulaReadOnly</Variable>
      <Variable>formulaRelevant</Variable>
      <Variable>variablesId</Variable>
    </Variables>
  </xsl:variable>

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
          <xsl:element name="xsl:if">
            <xsl:attribute name="test" select="'self::Form'"/>
            <xsl:element name="getters_for_which_context_is_not_enough">
              <xsl:apply-templates select="xsl:function[xsl:param/@name=$excluded-variables//Variable]" mode="debug"/>
            </xsl:element>
          </xsl:element>
          <xsl:apply-templates select="xsl:function[not(xsl:param/@name=$excluded-variables//Variable)]" mode="debug"/>
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

  <xsl:template match="xsl:function[not(xsl:param/@name=$excluded-variables//Variable)]" mode="debug">
    <xsl:variable name="function-type">
      <xsl:choose>
        <xsl:when test="@as != ''">
          <xsl:value-of select="@as"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$source//xsl:function[@name=substring-before(current()/xsl:sequence[1]/@select,'(')]/@as"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
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
          <!-- the tables are always limited to their 20 first lines in fo  -->
          <xsl:when test="@name='table-first-line'">
            <xsl:value-of select="'1'"/>
          </xsl:when>
          <xsl:when test="@name='table-last-line'">
            <xsl:value-of select="'20'"/>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
      <xsl:value-of select="')'"/>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$function-type = 'xs:boolean'">
        <xsl:element name="xsl:if">
          <xsl:attribute name="test" select="$function-call"/>
          <xsl:element name="xsl:element">
            <xsl:attribute name="name" select="substring-after(@name,':')"/>
            <xsl:element name="xsl:value-of">
              <xsl:attribute name="select" select="$function-call"/>
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:when>
      <xsl:when test="$function-type = 'xs:string'">
        <xsl:element name="xsl:if">
          <xsl:attribute name="test" select="concat($function-call,' != ''''')"/>
          <xsl:element name="xsl:element">
            <xsl:attribute name="name" select="substring-after(@name,':')"/>
            <xsl:element name="xsl:sequence">
              <xsl:attribute name="select" select="$function-call"/>
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:when>
      <xsl:when test="$function-type = 'xs:string*' or $function-type = 'xs:string *'">
        <xsl:element name="xsl:if">
          <xsl:attribute name="test" select="concat($function-call,' != ''''')"/>
          <xsl:element name="xsl:element">
            <xsl:attribute name="name" select="substring-after(@name,':')"/>
            <xsl:element name="xsl:value-of">
              <xsl:attribute name="select" select="'''liste des éléments : '''"/>
            </xsl:element>
            <xsl:element name="xsl:for-each">
              <xsl:attribute name="select" select="$function-call"/>
              <xsl:element name="xsl:text">
                <xsl:value-of select="'&#xA;'"/>
              </xsl:element>
              <xsl:element name="xsl:sequence">
                <xsl:attribute name="select" select="'.'"/>
              </xsl:element>
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="xsl:if">
          <xsl:attribute name="test" select="concat($function-call,' != ''''')"/>
          <xsl:element name="xsl:element">
            <xsl:attribute name="name" select="substring-after(@name,':')"/>
            <xsl:element name="xsl:choose">
              <xsl:element name="xsl:when">
                <xsl:attribute name="test" select="concat($function-call,' instance of xs:string')"/>
                <xsl:element name="xsl:choose">
                  <xsl:element name="xsl:when">
                    <xsl:attribute name="test" select="concat('eno:is-rich-content(',$function-call,')')"/>
                    <xsl:element name="xsl:element">
                      <xsl:attribute name="name" select="concat('{name(',$function-call,')}')"/>
                      <xsl:element name="xsl:comment">
                        <xsl:attribute name="select" select="'''Input content'''"/>
                      </xsl:element>
                    </xsl:element>
                  </xsl:element>
                  <xsl:element name="xsl:otherwise">
                    <xsl:element name="xsl:sequence">
                      <xsl:attribute name="select" select="$function-call"/>
                    </xsl:element>
                  </xsl:element>
                </xsl:element>
              </xsl:element>
              <xsl:element name="xsl:when">
                <xsl:attribute name="test" select="concat($function-call,'[1] instance of xs:string')"/>
                <xsl:element name="xsl:value-of">
                  <xsl:attribute name="select" select="'''liste des éléments : '''"/>
                </xsl:element>
                <xsl:element name="xsl:for-each">
                  <xsl:attribute name="select" select="$function-call"/>
                  <xsl:element name="xsl:text">
                    <xsl:value-of select="'&#xA;'"/>
                  </xsl:element>
                  <xsl:element name="xsl:choose">
                    <xsl:element name="xsl:when">
                      <xsl:attribute name="test" select="'eno:is-rich-content(.)'"/>
                      <xsl:element name="xsl:element">
                        <xsl:attribute name="name" select="'{name(.)}'"/>
                        <xsl:element name="xsl:comment">
                          <xsl:attribute name="select" select="'''Input content'''"/>
                        </xsl:element>
                      </xsl:element>
                    </xsl:element>
                    <xsl:element name="xsl:otherwise">
                      <xsl:element name="xsl:sequence">
                        <xsl:attribute name="select" select="'.'"/>
                      </xsl:element>
                    </xsl:element>
                  </xsl:element>
                </xsl:element>
              </xsl:element>
              <xsl:element name="xsl:otherwise">
                <xsl:element name="xsl:if">
                  <xsl:attribute name="test" select="concat($function-call,'[2] != ''''')"/>
                  <xsl:element name="xsl:value-of">
                    <xsl:attribute name="select" select="'''liste des éléments : '''"/>
                  </xsl:element>
                </xsl:element>
                <xsl:element name="xsl:for-each">
                  <xsl:attribute name="select" select="$function-call"/>
                  <xsl:element name="xsl:if">
                    <xsl:attribute name="test" select="concat($function-call,'[2] = ''''')"/>
                    <xsl:element name="xsl:text">
                      <xsl:value-of select="'&#xA;'"/>
                    </xsl:element>
                  </xsl:element>
                  <xsl:element name="xsl:choose">
                    <xsl:element name="xsl:when">
                      <xsl:attribute name="test" select="'eno:is-rich-content(.)'"/>
                      <xsl:element name="xsl:element">
                        <xsl:attribute name="name" select="'{name(.)}'"/>
                        <xsl:element name="xsl:comment">
                          <xsl:attribute name="select" select="'''Input content'''"/>
                        </xsl:element>
                      </xsl:element>
                    </xsl:element>
                    <xsl:element name="xsl:otherwise">
                      <xsl:element name="xsl:sequence">
                        <xsl:attribute name="select" select="'.'"/>
                      </xsl:element>
                    </xsl:element>
                  </xsl:element>
                </xsl:element>
              </xsl:element>
            </xsl:element>
          </xsl:element>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="xsl:function[xsl:param/@name=$excluded-variables//Variable]" mode="debug">
    <xsl:element name="xsl:element">
      <xsl:attribute name="name" select="substring-after(@name,':')"/>
      <xsl:element name="xsl:value-of">
        <xsl:attribute name="select" select="'''needs variable param'''"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>