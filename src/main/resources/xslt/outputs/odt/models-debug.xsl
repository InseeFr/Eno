<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
                xmlns:eno="http://xml.insee.fr/apps/eno"
                xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
                xmlns:enoodt="http://xml.insee.fr/apps/eno/out/odt"
                xmlns:enoddi2fr="http://xml.insee.fr/apps/eno/ddi2form-runner"
                xmlns:d="ddi:datacollection:3_3"
                xmlns:r="ddi:reusable:3_3"
                xmlns:l="ddi:logicalproduct:3_3"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                version="2.0">
   <xsl:template match="*" mode="model">
      <xsl:param name="source-context" as="item()" tunnel="yes"/>
      <xsl:copy copy-namespaces="no">
         <xsl:if test="self::Form">
            <getters_for_which_context_is_not_enough>
               <xsl:element name="get-variable-business-name">
                  <xsl:value-of select="'needs variable param'"/>
               </xsl:element>
               <xsl:element name="get-conditioning-variable-formula">
                  <xsl:value-of select="'needs variable param'"/>
               </xsl:element>
               <xsl:element name="get-conditioning-variable-formula-variables">
                  <xsl:value-of select="'needs variable param'"/>
               </xsl:element>
            </getters_for_which_context_is_not_enough>
         </xsl:if>
         <xsl:choose>
            <xsl:when test="enoodt:get-form-languages($source-context) castable as xs:boolean">
               <xsl:element name="get-form-languages">
                  <xsl:value-of select="enoodt:get-form-languages($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-form-languages($source-context) != ''">
                  <xsl:element name="get-form-languages">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-form-languages($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-form-languages($source-context))">
                                 <xsl:element name="{name(enoodt:get-form-languages($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-form-languages($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-form-languages($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-form-languages($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-form-languages($source-context)"/>
                           <xsl:message select="'5enoodt:get-form-languages($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-after-question-title-instructions($source-context) castable as xs:boolean">
               <xsl:element name="get-after-question-title-instructions">
                  <xsl:value-of select="enoodt:get-after-question-title-instructions($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-after-question-title-instructions($source-context) != ''">
                  <xsl:element name="get-after-question-title-instructions">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-after-question-title-instructions($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-after-question-title-instructions($source-context))">
                                 <xsl:element name="{name(enoodt:get-after-question-title-instructions($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-after-question-title-instructions($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-after-question-title-instructions($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-after-question-title-instructions($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-after-question-title-instructions($source-context)"/>
                           <xsl:message select="'5enoodt:get-after-question-title-instructions($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-end-question-instructions($source-context) castable as xs:boolean">
               <xsl:element name="get-end-question-instructions">
                  <xsl:value-of select="enoodt:get-end-question-instructions($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-end-question-instructions($source-context) != ''">
                  <xsl:element name="get-end-question-instructions">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-end-question-instructions($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-end-question-instructions($source-context))">
                                 <xsl:element name="{name(enoodt:get-end-question-instructions($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-end-question-instructions($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-end-question-instructions($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-end-question-instructions($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-end-question-instructions($source-context)"/>
                           <xsl:message select="'5enoodt:get-end-question-instructions($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-form-title($source-context,'fr') castable as xs:boolean">
               <xsl:element name="get-form-title">
                  <xsl:value-of select="enoodt:get-form-title($source-context,'fr')"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-form-title($source-context,'fr') != ''">
                  <xsl:element name="get-form-title">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-form-title($source-context,'fr') castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-form-title($source-context,'fr'))">
                                 <xsl:element name="{name(enoodt:get-form-title($source-context,'fr'))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-form-title($source-context,'fr')"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-form-title($source-context,'fr')[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-form-title($source-context,'fr')">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-form-title($source-context,'fr')"/>
                           <xsl:message select="'5enoodt:get-form-title($source-context,''fr'')'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-application-name($source-context) castable as xs:boolean">
               <xsl:element name="get-application-name">
                  <xsl:value-of select="enoodt:get-application-name($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-application-name($source-context) != ''">
                  <xsl:element name="get-application-name">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-application-name($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-application-name($source-context))">
                                 <xsl:element name="{name(enoodt:get-application-name($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-application-name($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-application-name($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-application-name($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-application-name($source-context)"/>
                           <xsl:message select="'5enoodt:get-application-name($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-form-name($source-context) castable as xs:boolean">
               <xsl:element name="get-form-name">
                  <xsl:value-of select="enoodt:get-form-name($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-form-name($source-context) != ''">
                  <xsl:element name="get-form-name">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-form-name($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-form-name($source-context))">
                                 <xsl:element name="{name(enoodt:get-form-name($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-form-name($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-form-name($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-form-name($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-form-name($source-context)"/>
                           <xsl:message select="'5enoodt:get-form-name($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-name($source-context) castable as xs:boolean">
               <xsl:element name="get-name">
                  <xsl:value-of select="enoodt:get-name($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-name($source-context) != ''">
                  <xsl:element name="get-name">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-name($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-name($source-context))">
                                 <xsl:element name="{name(enoodt:get-name($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-name($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-name($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-name($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-name($source-context)"/>
                           <xsl:message select="'5enoodt:get-name($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-relevant($source-context) castable as xs:boolean">
               <xsl:element name="get-relevant">
                  <xsl:value-of select="enoodt:get-relevant($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-relevant($source-context) != ''">
                  <xsl:element name="get-relevant">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-relevant($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-relevant($source-context))">
                                 <xsl:element name="{name(enoodt:get-relevant($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-relevant($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-relevant($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-relevant($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-relevant($source-context)"/>
                           <xsl:message select="'5enoodt:get-relevant($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-readonly($source-context) castable as xs:boolean">
               <xsl:element name="get-readonly">
                  <xsl:value-of select="enoodt:get-readonly($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-readonly($source-context) != ''">
                  <xsl:element name="get-readonly">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-readonly($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-readonly($source-context))">
                                 <xsl:element name="{name(enoodt:get-readonly($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-readonly($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-readonly($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-readonly($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-readonly($source-context)"/>
                           <xsl:message select="'5enoodt:get-readonly($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:is-required($source-context) castable as xs:boolean">
               <xsl:element name="is-required">
                  <xsl:value-of select="enoodt:is-required($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:is-required($source-context) != ''">
                  <xsl:element name="is-required">
                     <xsl:choose>
                        <xsl:when test="enoodt:is-required($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:is-required($source-context))">
                                 <xsl:element name="{name(enoodt:is-required($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:is-required($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:is-required($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:is-required($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:is-required($source-context)"/>
                           <xsl:message select="'5enoodt:is-required($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-type($source-context) castable as xs:boolean">
               <xsl:element name="get-type">
                  <xsl:value-of select="enoodt:get-type($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-type($source-context) != ''">
                  <xsl:element name="get-type">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-type($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-type($source-context))">
                                 <xsl:element name="{name(enoodt:get-type($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-type($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-type($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-type($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-type($source-context)"/>
                           <xsl:message select="'5enoodt:get-type($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-format($source-context) castable as xs:boolean">
               <xsl:element name="get-format">
                  <xsl:value-of select="enoodt:get-format($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-format($source-context) != ''">
                  <xsl:element name="get-format">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-format($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-format($source-context))">
                                 <xsl:element name="{name(enoodt:get-format($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-format($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-format($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-format($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-format($source-context)"/>
                           <xsl:message select="'5enoodt:get-format($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-constraint($source-context) castable as xs:boolean">
               <xsl:element name="get-constraint">
                  <xsl:value-of select="enoodt:get-constraint($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-constraint($source-context) != ''">
                  <xsl:element name="get-constraint">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-constraint($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-constraint($source-context))">
                                 <xsl:element name="{name(enoodt:get-constraint($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-constraint($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-constraint($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-constraint($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-constraint($source-context)"/>
                           <xsl:message select="'5enoodt:get-constraint($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-format-constraint($source-context) castable as xs:boolean">
               <xsl:element name="get-format-constraint">
                  <xsl:value-of select="enoodt:get-format-constraint($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-format-constraint($source-context) != ''">
                  <xsl:element name="get-format-constraint">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-format-constraint($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-format-constraint($source-context))">
                                 <xsl:element name="{name(enoodt:get-format-constraint($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-format-constraint($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-format-constraint($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-format-constraint($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-format-constraint($source-context)"/>
                           <xsl:message select="'5enoodt:get-format-constraint($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-alert-level($source-context) castable as xs:boolean">
               <xsl:element name="get-alert-level">
                  <xsl:value-of select="enoodt:get-alert-level($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-alert-level($source-context) != ''">
                  <xsl:element name="get-alert-level">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-alert-level($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-alert-level($source-context))">
                                 <xsl:element name="{name(enoodt:get-alert-level($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-alert-level($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-alert-level($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-alert-level($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-alert-level($source-context)"/>
                           <xsl:message select="'5enoodt:get-alert-level($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-help($source-context,'fr') castable as xs:boolean">
               <xsl:element name="get-help">
                  <xsl:value-of select="enoodt:get-help($source-context,'fr')"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-help($source-context,'fr') != ''">
                  <xsl:element name="get-help">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-help($source-context,'fr') castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-help($source-context,'fr'))">
                                 <xsl:element name="{name(enoodt:get-help($source-context,'fr'))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-help($source-context,'fr')"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-help($source-context,'fr')[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-help($source-context,'fr')">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-help($source-context,'fr')"/>
                           <xsl:message select="'5enoodt:get-help($source-context,''fr'')'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-label($source-context,'fr') castable as xs:boolean">
               <xsl:element name="get-label">
                  <xsl:value-of select="enoodt:get-label($source-context,'fr')"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-label($source-context,'fr') != ''">
                  <xsl:element name="get-label">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-label($source-context,'fr') castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-label($source-context,'fr'))">
                                 <xsl:element name="{name(enoodt:get-label($source-context,'fr'))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-label($source-context,'fr')"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-label($source-context,'fr')[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-label($source-context,'fr')">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-label($source-context,'fr')"/>
                           <xsl:message select="'5enoodt:get-label($source-context,''fr'')'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-value($source-context) castable as xs:boolean">
               <xsl:element name="get-value">
                  <xsl:value-of select="enoodt:get-value($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-value($source-context) != ''">
                  <xsl:element name="get-value">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-value($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-value($source-context))">
                                 <xsl:element name="{name(enoodt:get-value($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-value($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-value($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-value($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-value($source-context)"/>
                           <xsl:message select="'5enoodt:get-value($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-appearance($source-context) castable as xs:boolean">
               <xsl:element name="get-appearance">
                  <xsl:value-of select="enoodt:get-appearance($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-appearance($source-context) != ''">
                  <xsl:element name="get-appearance">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-appearance($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-appearance($source-context))">
                                 <xsl:element name="{name(enoodt:get-appearance($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-appearance($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-appearance($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-appearance($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-appearance($source-context)"/>
                           <xsl:message select="'5enoodt:get-appearance($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-css-class($source-context) castable as xs:boolean">
               <xsl:element name="get-css-class">
                  <xsl:value-of select="enoodt:get-css-class($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-css-class($source-context) != ''">
                  <xsl:element name="get-css-class">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-css-class($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-css-class($source-context))">
                                 <xsl:element name="{name(enoodt:get-css-class($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-css-class($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-css-class($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-css-class($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-css-class($source-context)"/>
                           <xsl:message select="'5enoodt:get-css-class($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-length($source-context) castable as xs:boolean">
               <xsl:element name="get-length">
                  <xsl:value-of select="enoodt:get-length($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-length($source-context) != ''">
                  <xsl:element name="get-length">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-length($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-length($source-context))">
                                 <xsl:element name="{name(enoodt:get-length($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-length($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-length($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-length($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-length($source-context)"/>
                           <xsl:message select="'5enoodt:get-length($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-minimum($source-context) castable as xs:boolean">
               <xsl:element name="get-minimum">
                  <xsl:value-of select="enoodt:get-minimum($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-minimum($source-context) != ''">
                  <xsl:element name="get-minimum">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-minimum($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-minimum($source-context))">
                                 <xsl:element name="{name(enoodt:get-minimum($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-minimum($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-minimum($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-minimum($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-minimum($source-context)"/>
                           <xsl:message select="'5enoodt:get-minimum($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-maximum($source-context) castable as xs:boolean">
               <xsl:element name="get-maximum">
                  <xsl:value-of select="enoodt:get-maximum($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-maximum($source-context) != ''">
                  <xsl:element name="get-maximum">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-maximum($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-maximum($source-context))">
                                 <xsl:element name="{name(enoodt:get-maximum($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-maximum($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-maximum($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-maximum($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-maximum($source-context)"/>
                           <xsl:message select="'5enoodt:get-maximum($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-number-of-decimals($source-context) castable as xs:boolean">
               <xsl:element name="get-number-of-decimals">
                  <xsl:value-of select="enoodt:get-number-of-decimals($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-number-of-decimals($source-context) != ''">
                  <xsl:element name="get-number-of-decimals">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-number-of-decimals($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-number-of-decimals($source-context))">
                                 <xsl:element name="{name(enoodt:get-number-of-decimals($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-number-of-decimals($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-number-of-decimals($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-number-of-decimals($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-number-of-decimals($source-context)"/>
                           <xsl:message select="'5enoodt:get-number-of-decimals($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-suffix($source-context,'fr') castable as xs:boolean">
               <xsl:element name="get-suffix">
                  <xsl:value-of select="enoodt:get-suffix($source-context,'fr')"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-suffix($source-context,'fr') != ''">
                  <xsl:element name="get-suffix">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-suffix($source-context,'fr') castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-suffix($source-context,'fr'))">
                                 <xsl:element name="{name(enoodt:get-suffix($source-context,'fr'))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-suffix($source-context,'fr')"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-suffix($source-context,'fr')[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-suffix($source-context,'fr')">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-suffix($source-context,'fr')"/>
                           <xsl:message select="'5enoodt:get-suffix($source-context,''fr'')'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-header-columns($source-context) castable as xs:boolean">
               <xsl:element name="get-header-columns">
                  <xsl:value-of select="enoodt:get-header-columns($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-header-columns($source-context) != ''">
                  <xsl:element name="get-header-columns">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-header-columns($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-header-columns($source-context))">
                                 <xsl:element name="{name(enoodt:get-header-columns($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-header-columns($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-header-columns($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-header-columns($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-header-columns($source-context)"/>
                           <xsl:message select="'5enoodt:get-header-columns($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-header-lines($source-context) castable as xs:boolean">
               <xsl:element name="get-header-lines">
                  <xsl:value-of select="enoodt:get-header-lines($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-header-lines($source-context) != ''">
                  <xsl:element name="get-header-lines">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-header-lines($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-header-lines($source-context))">
                                 <xsl:element name="{name(enoodt:get-header-lines($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-header-lines($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-header-lines($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-header-lines($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-header-lines($source-context)"/>
                           <xsl:message select="'5enoodt:get-header-lines($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-body-lines($source-context) castable as xs:boolean">
               <xsl:element name="get-body-lines">
                  <xsl:value-of select="enoodt:get-body-lines($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-body-lines($source-context) != ''">
                  <xsl:element name="get-body-lines">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-body-lines($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-body-lines($source-context))">
                                 <xsl:element name="{name(enoodt:get-body-lines($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-body-lines($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-body-lines($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-body-lines($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-body-lines($source-context)"/>
                           <xsl:message select="'5enoodt:get-body-lines($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-header-line($source-context,1) castable as xs:boolean">
               <xsl:element name="get-header-line">
                  <xsl:value-of select="enoodt:get-header-line($source-context,1)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-header-line($source-context,1) != ''">
                  <xsl:element name="get-header-line">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-header-line($source-context,1) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-header-line($source-context,1))">
                                 <xsl:element name="{name(enoodt:get-header-line($source-context,1))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-header-line($source-context,1)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-header-line($source-context,1)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-header-line($source-context,1)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-header-line($source-context,1)"/>
                           <xsl:message select="'5enoodt:get-header-line($source-context,1)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-body-line($source-context,1) castable as xs:boolean">
               <xsl:element name="get-body-line">
                  <xsl:value-of select="enoodt:get-body-line($source-context,1)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-body-line($source-context,1) != ''">
                  <xsl:element name="get-body-line">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-body-line($source-context,1) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-body-line($source-context,1))">
                                 <xsl:element name="{name(enoodt:get-body-line($source-context,1))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-body-line($source-context,1)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-body-line($source-context,1)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-body-line($source-context,1)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-body-line($source-context,1)"/>
                           <xsl:message select="'5enoodt:get-body-line($source-context,1)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-rowspan($source-context) castable as xs:boolean">
               <xsl:element name="get-rowspan">
                  <xsl:value-of select="enoodt:get-rowspan($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-rowspan($source-context) != ''">
                  <xsl:element name="get-rowspan">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-rowspan($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-rowspan($source-context))">
                                 <xsl:element name="{name(enoodt:get-rowspan($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-rowspan($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-rowspan($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-rowspan($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-rowspan($source-context)"/>
                           <xsl:message select="'5enoodt:get-rowspan($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-colspan($source-context) castable as xs:boolean">
               <xsl:element name="get-colspan">
                  <xsl:value-of select="enoodt:get-colspan($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-colspan($source-context) != ''">
                  <xsl:element name="get-colspan">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-colspan($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-colspan($source-context))">
                                 <xsl:element name="{name(enoodt:get-colspan($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-colspan($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-colspan($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-colspan($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-colspan($source-context)"/>
                           <xsl:message select="'5enoodt:get-colspan($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-minimum-lines($source-context) castable as xs:boolean">
               <xsl:element name="get-minimum-lines">
                  <xsl:value-of select="enoodt:get-minimum-lines($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-minimum-lines($source-context) != ''">
                  <xsl:element name="get-minimum-lines">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-minimum-lines($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-minimum-lines($source-context))">
                                 <xsl:element name="{name(enoodt:get-minimum-lines($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-minimum-lines($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-minimum-lines($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-minimum-lines($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-minimum-lines($source-context)"/>
                           <xsl:message select="'5enoodt:get-minimum-lines($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-maximum-lines($source-context) castable as xs:boolean">
               <xsl:element name="get-maximum-lines">
                  <xsl:value-of select="enoodt:get-maximum-lines($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-maximum-lines($source-context) != ''">
                  <xsl:element name="get-maximum-lines">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-maximum-lines($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-maximum-lines($source-context))">
                                 <xsl:element name="{name(enoodt:get-maximum-lines($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-maximum-lines($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-maximum-lines($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-maximum-lines($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-maximum-lines($source-context)"/>
                           <xsl:message select="'5enoodt:get-maximum-lines($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-relevant-dependencies($source-context) castable as xs:boolean">
               <xsl:element name="get-relevant-dependencies">
                  <xsl:value-of select="enoodt:get-relevant-dependencies($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-relevant-dependencies($source-context) != ''">
                  <xsl:element name="get-relevant-dependencies">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-relevant-dependencies($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-relevant-dependencies($source-context))">
                                 <xsl:element name="{name(enoodt:get-relevant-dependencies($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-relevant-dependencies($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-relevant-dependencies($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-relevant-dependencies($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-relevant-dependencies($source-context)"/>
                           <xsl:message select="'5enoodt:get-relevant-dependencies($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-readonly-dependencies($source-context) castable as xs:boolean">
               <xsl:element name="get-readonly-dependencies">
                  <xsl:value-of select="enoodt:get-readonly-dependencies($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-readonly-dependencies($source-context) != ''">
                  <xsl:element name="get-readonly-dependencies">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-readonly-dependencies($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-readonly-dependencies($source-context))">
                                 <xsl:element name="{name(enoodt:get-readonly-dependencies($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-readonly-dependencies($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-readonly-dependencies($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-readonly-dependencies($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-readonly-dependencies($source-context)"/>
                           <xsl:message select="'5enoodt:get-readonly-dependencies($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-code-depth($source-context) castable as xs:boolean">
               <xsl:element name="get-code-depth">
                  <xsl:value-of select="enoodt:get-code-depth($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-code-depth($source-context) != ''">
                  <xsl:element name="get-code-depth">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-code-depth($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-code-depth($source-context))">
                                 <xsl:element name="{name(enoodt:get-code-depth($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-code-depth($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-code-depth($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-code-depth($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-code-depth($source-context)"/>
                           <xsl:message select="'5enoodt:get-code-depth($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-image($source-context) castable as xs:boolean">
               <xsl:element name="get-image">
                  <xsl:value-of select="enoodt:get-image($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-image($source-context) != ''">
                  <xsl:element name="get-image">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-image($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-image($source-context))">
                                 <xsl:element name="{name(enoodt:get-image($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-image($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-image($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-image($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-image($source-context)"/>
                           <xsl:message select="'5enoodt:get-image($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-readonly-ancestors($source-context) castable as xs:boolean">
               <xsl:element name="get-readonly-ancestors">
                  <xsl:value-of select="enoodt:get-readonly-ancestors($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-readonly-ancestors($source-context) != ''">
                  <xsl:element name="get-readonly-ancestors">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-readonly-ancestors($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-readonly-ancestors($source-context))">
                                 <xsl:element name="{name(enoodt:get-readonly-ancestors($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-readonly-ancestors($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-readonly-ancestors($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-readonly-ancestors($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-readonly-ancestors($source-context)"/>
                           <xsl:message select="'5enoodt:get-readonly-ancestors($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-readonly-ancestors-variables($source-context) castable as xs:boolean">
               <xsl:element name="get-readonly-ancestors-variables">
                  <xsl:value-of select="enoodt:get-readonly-ancestors-variables($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-readonly-ancestors-variables($source-context) != ''">
                  <xsl:element name="get-readonly-ancestors-variables">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-readonly-ancestors-variables($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-readonly-ancestors-variables($source-context))">
                                 <xsl:element name="{name(enoodt:get-readonly-ancestors-variables($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-readonly-ancestors-variables($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-readonly-ancestors-variables($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-readonly-ancestors-variables($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-readonly-ancestors-variables($source-context)"/>
                           <xsl:message select="'5enoodt:get-readonly-ancestors-variables($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-label-conditioning-variables($source-context,'fr') castable as xs:boolean">
               <xsl:element name="get-label-conditioning-variables">
                  <xsl:value-of select="enoodt:get-label-conditioning-variables($source-context,'fr')"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-label-conditioning-variables($source-context,'fr') != ''">
                  <xsl:element name="get-label-conditioning-variables">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-label-conditioning-variables($source-context,'fr') castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-label-conditioning-variables($source-context,'fr'))">
                                 <xsl:element name="{name(enoodt:get-label-conditioning-variables($source-context,'fr'))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-label-conditioning-variables($source-context,'fr')"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-label-conditioning-variables($source-context,'fr')[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-label-conditioning-variables($source-context,'fr')">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-label-conditioning-variables($source-context,'fr')"/>
                           <xsl:message select="'5enoodt:get-label-conditioning-variables($source-context,''fr'')'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-item-label-conditioning-variables($source-context) castable as xs:boolean">
               <xsl:element name="get-item-label-conditioning-variables">
                  <xsl:value-of select="enoodt:get-item-label-conditioning-variables($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-item-label-conditioning-variables($source-context) != ''">
                  <xsl:element name="get-item-label-conditioning-variables">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-item-label-conditioning-variables($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-item-label-conditioning-variables($source-context))">
                                 <xsl:element name="{name(enoodt:get-item-label-conditioning-variables($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-item-label-conditioning-variables($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-item-label-conditioning-variables($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-item-label-conditioning-variables($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-item-label-conditioning-variables($source-context)"/>
                           <xsl:message select="'5enoodt:get-item-label-conditioning-variables($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-business-name($source-context) castable as xs:boolean">
               <xsl:element name="get-business-name">
                  <xsl:value-of select="enoodt:get-business-name($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-business-name($source-context) != ''">
                  <xsl:element name="get-business-name">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-business-name($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-business-name($source-context))">
                                 <xsl:element name="{name(enoodt:get-business-name($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-business-name($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-business-name($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-business-name($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-business-name($source-context)"/>
                           <xsl:message select="'5enoodt:get-business-name($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-business-ancestors($source-context) castable as xs:boolean">
               <xsl:element name="get-business-ancestors">
                  <xsl:value-of select="enoodt:get-business-ancestors($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-business-ancestors($source-context) != ''">
                  <xsl:element name="get-business-ancestors">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-business-ancestors($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-business-ancestors($source-context))">
                                 <xsl:element name="{name(enoodt:get-business-ancestors($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-business-ancestors($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-business-ancestors($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-business-ancestors($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-business-ancestors($source-context)"/>
                           <xsl:message select="'5enoodt:get-business-ancestors($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-control-variables($source-context) castable as xs:boolean">
               <xsl:element name="get-control-variables">
                  <xsl:value-of select="enoodt:get-control-variables($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-control-variables($source-context) != ''">
                  <xsl:element name="get-control-variables">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-control-variables($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-control-variables($source-context))">
                                 <xsl:element name="{name(enoodt:get-control-variables($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-control-variables($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-control-variables($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-control-variables($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-control-variables($source-context)"/>
                           <xsl:message select="'5enoodt:get-control-variables($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-hideable-command-variables($source-context) castable as xs:boolean">
               <xsl:element name="get-hideable-command-variables">
                  <xsl:value-of select="enoodt:get-hideable-command-variables($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-hideable-command-variables($source-context) != ''">
                  <xsl:element name="get-hideable-command-variables">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-hideable-command-variables($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-hideable-command-variables($source-context))">
                                 <xsl:element name="{name(enoodt:get-hideable-command-variables($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-hideable-command-variables($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-hideable-command-variables($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-hideable-command-variables($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-hideable-command-variables($source-context)"/>
                           <xsl:message select="'5enoodt:get-hideable-command-variables($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-deactivatable-command-variables($source-context) castable as xs:boolean">
               <xsl:element name="get-deactivatable-command-variables">
                  <xsl:value-of select="enoodt:get-deactivatable-command-variables($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-deactivatable-command-variables($source-context) != ''">
                  <xsl:element name="get-deactivatable-command-variables">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-deactivatable-command-variables($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-deactivatable-command-variables($source-context))">
                                 <xsl:element name="{name(enoodt:get-deactivatable-command-variables($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-deactivatable-command-variables($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-deactivatable-command-variables($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-deactivatable-command-variables($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-deactivatable-command-variables($source-context)"/>
                           <xsl:message select="'5enoodt:get-deactivatable-command-variables($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-variable-calculation($source-context) castable as xs:boolean">
               <xsl:element name="get-variable-calculation">
                  <xsl:value-of select="enoodt:get-variable-calculation($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-variable-calculation($source-context) != ''">
                  <xsl:element name="get-variable-calculation">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-variable-calculation($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-variable-calculation($source-context))">
                                 <xsl:element name="{name(enoodt:get-variable-calculation($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-variable-calculation($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-variable-calculation($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-variable-calculation($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-variable-calculation($source-context)"/>
                           <xsl:message select="'5enoodt:get-variable-calculation($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-variable-calculation-variables($source-context) castable as xs:boolean">
               <xsl:element name="get-variable-calculation-variables">
                  <xsl:value-of select="enoodt:get-variable-calculation-variables($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-variable-calculation-variables($source-context) != ''">
                  <xsl:element name="get-variable-calculation-variables">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-variable-calculation-variables($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-variable-calculation-variables($source-context))">
                                 <xsl:element name="{name(enoodt:get-variable-calculation-variables($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-variable-calculation-variables($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-variable-calculation-variables($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-variable-calculation-variables($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-variable-calculation-variables($source-context)"/>
                           <xsl:message select="'5enoodt:get-variable-calculation-variables($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-before-question-title-instructions($source-context) castable as xs:boolean">
               <xsl:element name="get-before-question-title-instructions">
                  <xsl:value-of select="enoodt:get-before-question-title-instructions($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-before-question-title-instructions($source-context) != ''">
                  <xsl:element name="get-before-question-title-instructions">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-before-question-title-instructions($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-before-question-title-instructions($source-context))">
                                 <xsl:element name="{name(enoodt:get-before-question-title-instructions($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-before-question-title-instructions($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-before-question-title-instructions($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-before-question-title-instructions($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-before-question-title-instructions($source-context)"/>
                           <xsl:message select="'5enoodt:get-before-question-title-instructions($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-question-name($source-context,'fr') castable as xs:boolean">
               <xsl:element name="get-question-name">
                  <xsl:value-of select="enoodt:get-question-name($source-context,'fr')"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-question-name($source-context,'fr') != ''">
                  <xsl:element name="get-question-name">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-question-name($source-context,'fr') castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-question-name($source-context,'fr'))">
                                 <xsl:element name="{name(enoodt:get-question-name($source-context,'fr'))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-question-name($source-context,'fr')"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-question-name($source-context,'fr')[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-question-name($source-context,'fr')">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-question-name($source-context,'fr')"/>
                           <xsl:message select="'5enoodt:get-question-name($source-context,''fr'')'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-code-maximum-length($source-context) castable as xs:boolean">
               <xsl:element name="get-code-maximum-length">
                  <xsl:value-of select="enoodt:get-code-maximum-length($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-code-maximum-length($source-context) != ''">
                  <xsl:element name="get-code-maximum-length">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-code-maximum-length($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-code-maximum-length($source-context))">
                                 <xsl:element name="{name(enoodt:get-code-maximum-length($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-code-maximum-length($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-code-maximum-length($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-code-maximum-length($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-code-maximum-length($source-context)"/>
                           <xsl:message select="'5enoodt:get-code-maximum-length($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-container($source-context) castable as xs:boolean">
               <xsl:element name="get-container">
                  <xsl:value-of select="enoodt:get-container($source-context)"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-container($source-context) != ''">
                  <xsl:element name="get-container">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-container($source-context) castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-container($source-context))">
                                 <xsl:element name="{name(enoodt:get-container($source-context))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-container($source-context)"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-container($source-context)[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-container($source-context)">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no" select="enoodt:get-container($source-context)"/>
                           <xsl:message select="'5enoodt:get-container($source-context)'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-check-name($source-context,'fr') castable as xs:boolean">
               <xsl:element name="get-check-name">
                  <xsl:value-of select="enoodt:get-check-name($source-context,'fr')"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-check-name($source-context,'fr') != ''">
                  <xsl:element name="get-check-name">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-check-name($source-context,'fr') castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-check-name($source-context,'fr'))">
                                 <xsl:element name="{name(enoodt:get-check-name($source-context,'fr'))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-check-name($source-context,'fr')"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-check-name($source-context,'fr')[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-check-name($source-context,'fr')">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-check-name($source-context,'fr')"/>
                           <xsl:message select="'5enoodt:get-check-name($source-context,''fr'')'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:choose>
            <xsl:when test="enoodt:get-flowcontrol-label($source-context,'fr') castable as xs:boolean">
               <xsl:element name="get-flowcontrol-label">
                  <xsl:value-of select="enoodt:get-flowcontrol-label($source-context,'fr')"/>
               </xsl:element>
            </xsl:when>
            <xsl:otherwise>
               <xsl:if test="enoodt:get-flowcontrol-label($source-context,'fr') != ''">
                  <xsl:element name="get-flowcontrol-label">
                     <xsl:choose>
                        <xsl:when test="enoodt:get-flowcontrol-label($source-context,'fr') castable as xs:string">
                           <xsl:choose>
                              <xsl:when test="eno:is-rich-content(enoodt:get-flowcontrol-label($source-context,'fr'))">
                                 <xsl:element name="{name(enoodt:get-flowcontrol-label($source-context,'fr'))}">
                                    <xsl:comment select="'Input content'"/>
                                 </xsl:element>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:sequence select="enoodt:get-flowcontrol-label($source-context,'fr')"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:when>
                        <xsl:when test="enoodt:get-flowcontrol-label($source-context,'fr')[1] castable as xs:string">
                           <xsl:value-of select="'liste des éléments : '"/>
                           <xsl:for-each select="enoodt:get-flowcontrol-label($source-context,'fr')">
                              <xsl:choose>
                                 <xsl:when test="eno:is-rich-content(.)">
                                    <xsl:element name="{name(.)}">
                                       <xsl:comment select="'Input content'"/>
                                    </xsl:element>
                                 </xsl:when>
                                 <xsl:otherwise>
                                    <xsl:sequence select="."/>
                                 </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>
</xsl:text>
                           </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:copy-of copy-namespaces="no"
                                        select="enoodt:get-flowcontrol-label($source-context,'fr')"/>
                           <xsl:message select="'5enoodt:get-flowcontrol-label($source-context,''fr'')'"/>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
               </xsl:if>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
         </xsl:apply-templates>
      </xsl:copy>
   </xsl:template>
</xsl:stylesheet>
