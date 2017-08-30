<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" 
    xmlns:d="ddi:datacollection:3_2"
    xmlns:r="ddi:reusable:3_2" 
    xmlns:l="ddi:logicalproduct:3_2" 
    xmlns:g="ddi:group:3_2"
    xmlns:s="ddi:studyunit:3_2"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:pr="ddi:ddiprofile:3_2"
    xmlns:c="ddi:conceptualcomponent:3_2"
    xmlns:cm="ddi:comparative:3_2"
    xmlns:ddi-instance="ddi:instance:3_2"
    xmlns:dereferencing="dereferencing"
    exclude-result-prefixes="xs xd ddi-instance"
    version="2.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:param name="do-not-use-key" select="false()"/><!--set to false() if you don't know-->
    <xsl:param name="fast-and-dangerous-mode" select="false()"/><!--set to false() if you don't know-->
    <xsl:param name="build-messages" select="true()"/><!--set to true() if you don't know-->
    <xsl:param name="build-DDI" select="true()"/><!--set to true() if you don't know-->
    <xsl:strip-space elements="*"/>
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Aug 8, 2017</xd:p>
            <xd:p><xd:b>Author:</xd:b> nirnfv</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="follow-references">
        <dereferencing:reference-name name="d:ControlConstructReference"/>
        <dereferencing:reference-name name="d:InterviewerInstructionReference"/>
        <dereferencing:reference-name name="r:QuestionReference"/>
        <dereferencing:reference-name name="d:ThenConstructReference"/>
        <dereferencing:reference-name name="r:CodeListReference"/>
        <dereferencing:reference-name name="r:CategoryReference"/>
        <dereferencing:reference-name name="d:QuestionItemReference"/>
        <dereferencing:reference-name name="d:DateTimeDomainReference"/>
    </xsl:variable>
    
    <xsl:variable name="ignore-references">
        <dereferencing:reference-name name="r:QuestionSchemeReference"/>
        <dereferencing:reference-name name="r:ControlConstructSchemeReference"/>
        <dereferencing:reference-name name="r:InterviewerInstructionSchemeReference"/>
        <dereferencing:reference-name name="r:SourceParameterReference"/>
        <dereferencing:reference-name name="r:TargetParameterReference"/>
        <dereferencing:reference-name name="r:CodeReference"/>
    </xsl:variable>  
    
    <xsl:variable name="key-names">
        <xsl:choose>
            <xsl:when test="$do-not-use-key">
                <dereferencing:key-name name='_'  name1='_' name2='_'/>        
            </xsl:when>
            <xsl:otherwise>        
                <dereferencing:key-name name='r:CategoryReference-_-Category' name1='r:CategoryReference' name2='Category'/>
                <dereferencing:key-name name='r:CodeListReference-_-CodeList'  name1='r:CodeListReference' name2='CodeList'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-ComputationItem' name1='d:ControlConstructReference' name2='ComputationItem'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-IfThenElse' name1='d:ControlConstructReference' name2='IfThenElse'/>
                <dereferencing:key-name name='d:InterviewerInstructionReference-_-Instruction' name1='d:InterviewerInstructionReference' name2='Instruction'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-QuestionConstruct' name1='d:ControlConstructReference' name2='QuestionConstruct'/>
                <dereferencing:key-name name='d:QuestionItemReference-_-QuestionItem' name1='d:QuestionItemReference' name2='QuestionItem'/>
                <dereferencing:key-name name='r:QuestionReference-_-QuestionBlock' name1='r:QuestionReference' name2='QuestionBlock'/>
                <dereferencing:key-name name='r:QuestionReference-_-QuestionGrid' name1='r:QuestionReference' name2='QuestionGrid'/>
                <dereferencing:key-name name='r:QuestionReference-_-QuestionItem' name1='r:QuestionReference' name2='QuestionItem'/>
                <dereferencing:key-name name='d:ControlConstructReference-_-Sequence' name1='d:ControlConstructReference' name2='Sequence'/>
                <dereferencing:key-name name='d:ThenConstructReference-_-Sequence' name1='d:ThenConstructReference' name2='Sequence'/>
                <dereferencing:key-name name='d:DateTimeDomainReference-_-ManagedDateTimeRepresentation' name1='d:DateTimeDomainReference' name2='ManagedDateTimeRepresentation'/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:key name='r:CategoryReference-_-Category'                              match='/ddi-instance:DDIInstance/g:ResourcePackage/l:CategoryScheme/l:Category'                                     use='r:ID'/>
    <xsl:key name='r:CodeListReference-_-CodeList'                              match='/ddi-instance:DDIInstance/g:ResourcePackage/l:CodeListScheme/l:CodeList'                                     use='r:ID'/>
    <xsl:key name='d:ControlConstructReference-_-ComputationItem'               match='/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:ComputationItem'                      use='r:ID'/>
    <xsl:key name='d:ControlConstructReference-_-IfThenElse'                    match='/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:IfThenElse'                           use='r:ID'/>
    <xsl:key name='d:InterviewerInstructionReference-_-Instruction'             match='/ddi-instance:DDIInstance/g:ResourcePackage/d:InterviewerInstructionScheme/d:Instruction'                    use='r:ID'/>
    <xsl:key name='d:ControlConstructReference-_-QuestionConstruct'             match='/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:QuestionConstruct'                    use='r:ID'/>
    <xsl:key name='d:QuestionItemReference-_-QuestionItem'                      match='/ddi-instance:DDIInstance/g:ResourcePackage/d:QuestionScheme/d:QuestionItem'                                 use='r:ID'/>
    <xsl:key name='r:QuestionReference-_-QuestionBlock'                         match='/ddi-instance:DDIInstance/g:ResourcePackage/d:QuestionScheme/d:QuestionBlock'                                use='r:ID'/>
    <xsl:key name='r:QuestionReference-_-QuestionGrid'                          match='/ddi-instance:DDIInstance/g:ResourcePackage/d:QuestionScheme/d:QuestionGrid'                                 use='r:ID'/>
    <xsl:key name='r:QuestionReference-_-QuestionItem'                          match='/ddi-instance:DDIInstance/g:ResourcePackage/d:QuestionScheme/d:QuestionItem'                                 use='r:ID'/>
    <xsl:key name='d:ControlConstructReference-_-Sequence'                      match='/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:Sequence'                             use='r:ID'/>
    <xsl:key name='d:ThenConstructReference-_-Sequence'                         match='/ddi-instance:DDIInstance/g:ResourcePackage/d:ControlConstructScheme/d:Sequence'                             use='r:ID'/>
    <xsl:key name="d:DateTimeDomainReference-_-ManagedDateTimeRepresentation"   match="/ddi-instance:DDIInstance/g:ResourcePackage/r:ManagedRepresentationScheme/r:ManagedDateTimeRepresentation"   use="r:ID"/>
    <!--when modifying keys : modify key-names variable ; if you do not the program will not work-->
    
    <xsl:variable name="message-label">
        <dereferencing:message type-number="1" message-order="1">
            <dereferencing:label>Unknown Reference tag</dereferencing:label>
            <dereferencing:modifyXSLcode>Modify "follow-references" or "ignore references" variable, by adding the unknown reference tag. One or more key is also needed, see other messages.</dereferencing:modifyXSLcode>
        </dereferencing:message>
        <dereferencing:message type-number="2" message-order="2">
            <dereferencing:label>Missing ID node</dereferencing:label>
            <dereferencing:modifyDDIcode>Add non empty ID tag to the tag described in the where section</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="3" message-order="3">
            <dereferencing:label>Non-unique ID found with xsl:key</dereferencing:label>
            <dereferencing:modifyDDIcode>Find the incorrect ID and modify it in the DDI code.</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="4" message-order="4">
            <dereferencing:label>ID not found with xsl:key but found with global search</dereferencing:label>
            <dereferencing:modifyDDIcode>There is either a problem in the reference node or in the target node (or in both) : the type of object or the reference tag is incorrect in one of the node</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="5" message-order="5">
            <dereferencing:label>xsl:key creation requested</dereferencing:label>
            <dereferencing:modifyXSLcode>There is no key for this combination of reference and type of object. 
                It is therefor necessay to create a xsl:key node with the proposed name, match and use (in value node).
                Do not forget to update the key-names variable using name, name1 and name2 in value node : it will not work if you do not do it.</dereferencing:modifyXSLcode>
        </dereferencing:message>
        <dereferencing:message type-number="6" message-order="6">
            <dereferencing:label>Type mismatch</dereferencing:label>
            <dereferencing:modifyDDIcode>The type of object in the reference node and the tag of the target node does node match.</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="7" message-order="7">
            <dereferencing:label>Non-unique ID found with global search</dereferencing:label>
            <dereferencing:modifyDDIcode>A key search of the ID was not possible, and a global search of the ID in the document brought more than one match back.</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="8" message-order="8">
            <dereferencing:label>ID not found with xsl:key and global search</dereferencing:label>
            <dereferencing:modifyDDIcode>A key search then a global search in the document brought no match back.</dereferencing:modifyDDIcode>
        </dereferencing:message>
        <dereferencing:message type-number="9" message-order="9">
            <dereferencing:label>ID not found with global search</dereferencing:label>
            <dereferencing:modifyDDIcode>A key search of the ID was not possible, and a global search in the document brought no match back.</dereferencing:modifyDDIcode>
        </dereferencing:message>
    </xsl:variable>
    
    
    <xd:doc>
        <xd:desc>
            Root template
        </xd:desc>
    </xd:doc>
    
    <xsl:template match="/">
        <dereferencing:dereferencing-results>
            <xsl:if test="$build-messages">
                <xsl:call-template name="messages-template"/>
            </xsl:if>
            <xsl:if test="$build-DDI">
                <xsl:call-template name="DDI-template"/>
            </xsl:if>
        </dereferencing:dereferencing-results>
    </xsl:template>
    
    
    <xd:doc>
        Part of root template dealing with messages
        <xd:desc/>
    </xd:doc>
    <xsl:template name="messages-template">
        <dereferencing:dereferencing-result-messages>
            <xsl:variable name="messages-all">
                <xsl:for-each select="ddi-instance:DDIInstance/s:StudyUnit/d:DataCollection/d:InstrumentScheme/d:Instrument">
                    <xsl:apply-templates select=".">
                        <xsl:with-param name="output-DDI" select="false()"/>
                        <xsl:with-param name="output-message" select="true()"/>
                    </xsl:apply-templates>
                </xsl:for-each>
            </xsl:variable>
            <xsl:for-each select="$message-label/*" >
                <xsl:sort select="dereferencing:message-order"/>
                <xsl:if test="$messages-all/*[dereferencing:type-number=current()/@type-number]">
                    <xsl:copy>
                        <xsl:copy-of select="./@* | ./node() |text()"/>
                        <xsl:choose>
                            <xsl:when test="dereferencing:modifyXSLcode">
                                <xsl:for-each-group select="$messages-all/*[./dereferencing:type-number=current()/@type-number]" group-by="./dereferencing:value">
                                    <xsl:sort select="./dereferencing:value"/>
                                       <dereferencing:value>
                                       <xsl:copy-of select="./dereferencing:value/*"/>
                                           <dereferencing:where-list>
                                                <xsl:for-each-group select="current-group()" group-by="dereferencing:where">
                                                    <xsl:sort select="dereferencing:where"/>
                                                    <xsl:copy-of select="./dereferencing:where"/>
                                                </xsl:for-each-group>
                                           </dereferencing:where-list>
                                        </dereferencing:value>
                                    </xsl:for-each-group>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:for-each-group select="$messages-all/*[./dereferencing:type-number=current()/@type-number]" group-by="./dereferencing:where">
                                    <xsl:sort select="./dereferencing:where"/>
                                    <xsl:copy-of select="./*[name() != 'dereferencing:type-number']"/>                             
                                </xsl:for-each-group>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:copy>
                </xsl:if>
            </xsl:for-each>
        </dereferencing:dereferencing-result-messages>
    </xsl:template>
    
    <xd:doc>
        Part of root template dealing with DDI : only Instruments are found in the output
        <xd:desc/>
    </xd:doc>
    <xsl:template name="DDI-template">
        <xsl:for-each select="ddi-instance:DDIInstance/s:StudyUnit/d:DataCollection/d:InstrumentScheme/d:Instrument">
            <dereferencing:dereferencing-result-DDI instrument-name="{./r:ID}" studyUnit-name="{../../../r:ID}">  
                <ddi-instance:DDIInstance>
                    <r:ID><xsl:value-of select="./r:ID"/></r:ID>
                    <s:StudyUnit>
                        <r:ID><xsl:value-of select="../../../r:ID"/></r:ID>
                        <xsl:apply-templates select=".">
                            <xsl:with-param name="output-DDI" select="true()"/>
                            <xsl:with-param name="output-message" select="false()"/>
                        </xsl:apply-templates>
                    </s:StudyUnit>
                </ddi-instance:DDIInstance>
            </dereferencing:dereferencing-result-DDI>
        </xsl:for-each>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Template for Reference tags</xd:p>
        </xd:desc>
        <xd:param name="output-DDI"/>
        <xd:param name="output-message"/>
    </xd:doc>
    <xsl:template match="node()[ends-with(name(.), 'Reference')]">
        <xsl:param name="output-DDI"/>
        <xsl:param name="output-message"/>
        
        <xsl:variable name="copy-node-name"><xsl:value-of select="name(.)"/></xsl:variable>
        <xsl:choose>
            <xsl:when test="count($follow-references/dereferencing:reference-name[@name=$copy-node-name])!=0">
                <xsl:call-template name="xxx-Reference">
                    <xsl:with-param name="output-DDI" select="$output-DDI"/>
                    <xsl:with-param name="output-message" select="$output-message"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="count($ignore-references/dereferencing:reference-name[@name=$copy-node-name])!=0">
                <xsl:call-template name="simple-copy">
                    <xsl:with-param name="output-DDI" select="$output-DDI"/>
                    <xsl:with-param name="output-message" select="$output-message"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="$output-message">
                    <dereferencing:warning>
                        <dereferencing:type-number>1</dereferencing:type-number>
                        <dereferencing:value><xsl:value-of select="$copy-node-name"/></dereferencing:value>
                        <xsl:call-template name="where-message"/>
                    </dereferencing:warning>
                </xsl:if>
                <xsl:call-template name="xxx-Reference">
                    <xsl:with-param name="output-DDI" select="$output-DDI"/>
                    <xsl:with-param name="output-message" select="$output-message"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xd:doc>
        Default template : identity template.
        <xd:desc/>
        <xd:param name="output-DDI"/>
        <xd:param name="output-message"/>
    </xd:doc>
    <xsl:template match="*">
        <xsl:param name="output-DDI"/>
        <xsl:param name="output-message"/>
        <xsl:call-template name="simple-copy">
            <xsl:with-param name="output-DDI" select="$output-DDI"/>
            <xsl:with-param name="output-message" select="$output-message"/>
        </xsl:call-template>
    </xsl:template>
    
    
    
    <xd:doc>
        Identity template
        <xd:desc/>
        <xd:param name="output-DDI"/>
        <xd:param name="output-message"/>
    </xd:doc>
    <xsl:template name="simple-copy">
        <xsl:param name="output-DDI"/>
        <xsl:param name="output-message"/>
        <xsl:choose>
            <xsl:when test="$output-DDI">
                <xsl:copy>
                    <xsl:copy-of select="@* | text() | comment() | processing-instruction()"/>
                    <xsl:apply-templates select="*" >
                        <xsl:with-param name="output-DDI" select="$output-DDI"/>
                        <xsl:with-param name="output-message" select="$output-message"/>
                    </xsl:apply-templates>    
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="*" >
                    <xsl:with-param name="output-DDI" select="$output-DDI"/>
                    <xsl:with-param name="output-message" select="$output-message"/>
                </xsl:apply-templates> 
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xd:doc>
        Copy the ID node
        <xd:desc/>
        <xd:param name="output-DDI"/>
        <xd:param name="output-message"/>
    </xd:doc>
    <xsl:template name="simple-copy-ID">
        <xsl:param name="output-DDI"/>
        <xsl:param name="output-message"/>
        <xsl:if test="$output-DDI">
            <xsl:copy-of select="./r:ID"/>
        </xsl:if>
    </xsl:template>
    
    <xd:doc>
        <xd:desc>
            Template for ***Reference, which are searched and copied from index defined by xsl:key
        </xd:desc>
        <xd:param name="output-DDI"/>
        <xd:param name="output-message"/>
    </xd:doc>
    <xsl:template name="xxx-Reference">
        <xsl:param name="output-DDI"/>
        <xsl:param name="output-message"/>
        <xsl:choose>
            <xsl:when test="$output-DDI">
                <xsl:copy>
                    <xsl:copy-of select="@* | text() | comment() | processing-instruction() "/>
                    <xsl:copy-of select="./r:Agency"/>
                    <xsl:call-template name="xxx-Reference-call-templates">
                        <xsl:with-param name="output-DDI" select="$output-DDI"/>
                        <xsl:with-param name="output-message" select="$output-message"/>
                    </xsl:call-template>
                </xsl:copy>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="xxx-Reference-call-templates">
                    <xsl:with-param name="output-DDI" select="$output-DDI"/>
                    <xsl:with-param name="output-message" select="$output-message"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xd:doc>
        Wrapper for base template for searched References, to respect tag order.
        <xd:desc/>
        <xd:param name="output-DDI"/>
        <xd:param name="output-message"/>
    </xd:doc>
    <xsl:template name="xxx-Reference-call-templates">
        <xsl:param name="output-DDI"/>
        <xsl:param name="output-message"/>
        <xsl:call-template name="xxx-Reference-ID">
            <xsl:with-param name="output-DDI" select="$output-DDI"/>
            <xsl:with-param name="output-message" select="$output-message"/>
        </xsl:call-template>
        <xsl:apply-templates select="./*[name() != 'r:ID' and name() != 'r:Agency']">
            <xsl:with-param name="output-DDI" select="$output-DDI"/>
            <xsl:with-param name="output-message" select="$output-message"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:doc>
        Base template for searched References
        <xd:desc/>
        <xd:param name="output-DDI"/>
        <xd:param name="output-message"/>
    </xd:doc>
    <xsl:template name="xxx-Reference-ID">
        <xsl:param name="output-DDI"/>
        <xsl:param name="output-message"/>
        
        <xsl:choose>
            <xsl:when test="$fast-and-dangerous-mode">
                <xsl:apply-templates select="key(concat(name(.),'-_-',./r:TypeOfObject),./r:ID)">
                    <xsl:with-param name="output-DDI" select="$output-DDI"/>
                    <xsl:with-param name="output-message" select="$output-message"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="count(./r:ID)=0">
                        <xsl:if test="$output-message">
                            <dereferencing:warning>
                                <dereferencing:type-number>2</dereferencing:type-number>
                                <xsl:call-template name="where-message"/>
                            </dereferencing:warning>
                        </xsl:if>
                        <xsl:call-template name="simple-copy-ID">
                            <xsl:with-param name="output-DDI" select="$output-DDI"/>
                            <xsl:with-param name="output-message" select="$output-message"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="$key-names/dereferencing:key-name[@name1=name(current()) and @name2=current()/r:TypeOfObject]">
                                <xsl:variable name="result-key">
                                    <xsl:copy-of select="key(concat(name(.),'-_-',./r:TypeOfObject),./r:ID)"/>
                                </xsl:variable>
                                <xsl:choose>
                                    <xsl:when test="count($result-key/*)=1">
                                        <xsl:apply-templates select="key(concat(name(.),'-_-',./r:TypeOfObject),./r:ID)">
                                            <xsl:with-param name="output-DDI" select="$output-DDI"/>
                                            <xsl:with-param name="output-message" select="$output-message"/>
                                        </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:when test="count($result-key/*)>1">
                                        <xsl:if test="$output-message">
                                            <dereferencing:warning>
                                                <dereferencing:type-number>3</dereferencing:type-number>
                                                <xsl:call-template name="where-message"/>
                                            </dereferencing:warning>
                                        </xsl:if>
                                        <xsl:call-template name="simple-copy-ID">
                                            <xsl:with-param name="output-DDI" select="$output-DDI"/>
                                            <xsl:with-param name="output-message" select="$output-message"/>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:call-template name="find-id-without-key">
                                            <xsl:with-param name="output-DDI" select="$output-DDI"/>
                                            <xsl:with-param name="output-message" select="$output-message"/>
                                            <xsl:with-param name="key-existence" select="true()"/>
                                        </xsl:call-template>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:call-template name="find-id-without-key">
                                    <xsl:with-param name="output-DDI" select="$output-DDI"/>
                                    <xsl:with-param name="output-message" select="$output-message"/>
                                    <xsl:with-param name="key-existence" select="false()"/>
                                </xsl:call-template>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xd:doc>
        Template to find an idea with a global search.
        <xd:desc/>
        <xd:param name="output-DDI"/>
        <xd:param name="output-message"/>
        <xd:param name="key-existence"/>
    </xd:doc>
    <xsl:template name="find-id-without-key">
        <xsl:param name="output-DDI"/>
        <xsl:param name="output-message"/>
        <xsl:param name="key-existence"/>
        
        <xsl:variable name="current-id"><xsl:value-of select="./r:ID"></xsl:value-of></xsl:variable>
        <xsl:variable name="current-name"><xsl:value-of select="name(.)"></xsl:value-of></xsl:variable>
        <xsl:variable name="current-type"><xsl:value-of select="./r:TypeOfObject"></xsl:value-of></xsl:variable>
        <xsl:variable name="reference-targets"><xsl:copy-of select="//*[r:ID=$current-id and name(.) != $current-name]"></xsl:copy-of></xsl:variable>
        
        <xsl:choose>
            <xsl:when test="count($reference-targets/*) = 1">
                <xsl:variable name="reference-targets-parent-name"><xsl:value-of select="name((//*[r:ID=$current-id and name(.) != $current-name])[1]/..)"></xsl:value-of></xsl:variable>
                <xsl:variable name="reference-targets-name"><xsl:value-of select="name((//*[r:ID=$current-id and name(.) != $current-name])[1])"/></xsl:variable>
                <xsl:variable name="reference-targets-local-name"><xsl:value-of select="local-name((//*[r:ID=$current-id and name(.) != $current-name])[1])"/></xsl:variable>
                <xsl:if test="$output-message">
                    <dereferencing:warning>
                        <xsl:choose>
                            <xsl:when test="$key-existence=true()">
                                <dereferencing:type-number>4</dereferencing:type-number>
                                <dereferencing:reference-target-comparison>
                                    <dereferencing:reference><xsl:value-of select="$current-name"/></dereferencing:reference>
                                    <dereferencing:target-scheme><xsl:value-of select="$reference-targets-parent-name"/></dereferencing:target-scheme>
                                    <dereferencing:type-of-object><xsl:value-of select="$current-type"/></dereferencing:type-of-object>
                                    <dereferencing:target-type><xsl:value-of select="$reference-targets-name"/></dereferencing:target-type>
                                    <dereferencing:ID><xsl:value-of select="$current-id"/></dereferencing:ID>
                                </dereferencing:reference-target-comparison>
                                <xsl:call-template name="where-message"/>
                            </xsl:when>
                            <xsl:when test="$key-existence=false()">
                                <xsl:choose>
                                    <xsl:when test="$reference-targets-local-name = $current-type">
                                        <dereferencing:type-number>5</dereferencing:type-number>
                                        <dereferencing:value>
                                            <dereferencing:name><xsl:value-of select="concat($current-name,'-_-',$current-type)"/></dereferencing:name>
                                            <dereferencing:match><xsl:value-of select="concat('/ddi-instance:DDIInstance/g:ResourcePackage/',$reference-targets-parent-name,'/',$reference-targets-name)"/></dereferencing:match>
                                            <dereferencing:use><xsl:value-of select="'r:ID'"/></dereferencing:use>
                                            <dereferencing:name1><xsl:value-of select="$current-name"/></dereferencing:name1>
                                            <dereferencing:name2><xsl:value-of select="$current-type"/></dereferencing:name2>
                                        </dereferencing:value>
                                        <xsl:call-template name="where-message"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <dereferencing:type-number>6</dereferencing:type-number>
                                        <dereferencing:reference-target-comparison>
                                            <dereferencing:reference><xsl:value-of select="$current-name"/></dereferencing:reference>
                                            <dereferencing:target-scheme><xsl:value-of select="$reference-targets-parent-name"/></dereferencing:target-scheme>
                                            <dereferencing:type-of-object><xsl:value-of select="$current-type"/></dereferencing:type-of-object>
                                            <dereferencing:target-type><xsl:value-of select="$reference-targets-name"/></dereferencing:target-type>
                                            <dereferencing:ID><xsl:value-of select="$current-id"/></dereferencing:ID>
                                        </dereferencing:reference-target-comparison>
                                        <xsl:call-template name="where-message"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:when> 
                            <xsl:otherwise/>     
                        </xsl:choose>
                    </dereferencing:warning>
                </xsl:if>
                <xsl:apply-templates select="//*[r:ID=$current-id and name(.) != $current-name]">
                    <xsl:with-param name="output-DDI" select="$output-DDI"/>
                    <xsl:with-param name="output-message" select="$output-message"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="$output-message">
                    <dereferencing:warning>
                        <xsl:choose>
                            <xsl:when test="count($reference-targets/*) > 1">
                                <dereferencing:type-number>7</dereferencing:type-number>
                                <xsl:call-template name="where-message"/>
                            </xsl:when>
                            <xsl:when test="$key-existence=true()">
                                <dereferencing:type-number>8</dereferencing:type-number>
                                <xsl:call-template name="where-message"/>
                            </xsl:when>
                            <xsl:when test="$key-existence=false()">
                                <dereferencing:type-number>9</dereferencing:type-number>
                                <xsl:call-template name="where-message"/>
                            </xsl:when> 
                        </xsl:choose>
                    </dereferencing:warning>
                </xsl:if>
                <xsl:call-template name="simple-copy-ID">
                    <xsl:with-param name="output-DDI" select="$output-DDI"/>
                    <xsl:with-param name="output-message" select="$output-message"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xd:doc>
        Writing where the message comes from.
        <xd:desc/>
    </xd:doc>
    <xsl:template name="where-message">
        <dereferencing:where>
            <dereferencing:current-id><xsl:value-of select="./r:ID"/></dereferencing:current-id>
            <dereferencing:current-name><xsl:value-of select="name(.)"/></dereferencing:current-name>
            <dereferencing:current-type><xsl:value-of select="./r:TypeOfObject"/></dereferencing:current-type>
            <dereferencing:parent-id><xsl:value-of select="../r:ID"/></dereferencing:parent-id>
            <dereferencing:parent-name><xsl:value-of select="name(..)"/></dereferencing:parent-name>
            <dereferencing:parent-type><xsl:value-of select="../r:TypeOfObject"/></dereferencing:parent-type>
        </dereferencing:where>
    </xsl:template>
</xsl:stylesheet>

