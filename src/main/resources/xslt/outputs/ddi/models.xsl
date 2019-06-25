<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:eno="http://xml.insee.fr/apps/eno" xmlns:g="ddi:group:3_2"
    xmlns:d="ddi:datacollection:3_2" xmlns:s="ddi:studyunit:3_2" xmlns:r="ddi:reusable:3_2"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:a="ddi:archive:3_2"
    xmlns:l="ddi:logicalproduct:3_2" xmlns:enoddi32="http://xml.insee.fr/apps/eno/out/ddi32"
    exclude-result-prefixes="xs" version="2.0">


    <xd:doc>
        <xd:desc>
            <xd:p>The highest driver, which starts the generation of the xforms.</xd:p>
            <xd:p>It writes codes on different levels for a same driver by adding an element to the
                virtuel tree :</xd:p>
            <xd:p>- Instance : to write the main instance</xd:p>
            <xd:p>- Bind : to writes the binds associated to the elements of the instance</xd:p>
            <xd:p>- Resource : an instance which stores the externalized texts used in the body part
                (xforms labels, hints, helps, alerts)</xd:p>
            <xd:p>- ResourceBind : to write the few binds of the elements of the resource instance
                which are calculated</xd:p>
            <xd:p>- Body : to write the fields</xd:p>
            <xd:p>- Model : to write model elements of the instance which could be potentially added
                by the user in the instance</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="Form" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="citation" select="enoddi32:get-citation($source-context)" as="xs:string"/>
        <xsl:variable name="agency" select="enoddi32:get-agency($source-context)" as="xs:string"/>        
        <DDIInstance xmlns="ddi:instance:3_2"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="ddi:instance:3_2 ../../../src/main/resources/schema/instance.xsd"
            xmlns:a="ddi:archive:3_2" xmlns:r="ddi:reusable:3_2" xmlns:s="ddi:studyunit:3_2"
            xmlns:d="ddi:datacollection:3_2" xmlns:g="ddi:group:3_2" xmlns:eno="http://xml.insee.fr/apps/eno"
            xmlns:l="ddi:logicalproduct:3_2" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
            xmlns:enoddi32="http://xml.insee.fr/apps/eno/out/ddi32" xmlns:xhtml="http://www.w3.org/1999/xhtml"
            isMaintainable="true">
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="concat('INSEE-', enoddi32:get-id($source-context))"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:Citation>
                <r:Title>
                    <r:String>
                        <xsl:value-of select="$citation"/>
                    </r:String>
                </r:Title>
            </r:Citation>
            <g:ResourcePackage isMaintainable="true" versionDate="2018-01-25+01:00">
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="concat('RessourcePackage-', enoddi32:get-id($source-context))"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <d:InterviewerInstructionScheme>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="concat('InterviewerInstructionScheme-', enoddi32:get-id($source-context))"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:Label><r:Content xml:lang="{enoddi32:get-lang($source-context)}">A définir</r:Content></r:Label>
                    <xsl:apply-templates select="enoddi32:get-instructions($source-context)[not(enoddi32:get-position(.) = 'BEFORE_QUESTION_TEXT')] | enoddi32:get-controls($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-InterviewerInstructionScheme', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                </d:InterviewerInstructionScheme>
                <d:ControlConstructScheme>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="concat('ControlConstructScheme-', enoddi32:get-id($source-context))"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <d:Sequence>
                        <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                        <r:ID><xsl:value-of select="enoddi32:get-main-sequence-id($source-context)"/></r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <r:Label>
                            <r:Content xml:lang="{enoddi32:get-lang($source-context)}">
                                <xsl:value-of select="enoddi32:get-label($source-context)"/>
                            </r:Content>
                        </r:Label>
                        <d:TypeOfSequence codeListID="INSEE-TOS-CL-1">template</d:TypeOfSequence>
                        <!--creation of references of direct children-->
                        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                            <xsl:with-param name="driver" select="eno:append-empty-element('Sequence', .)" tunnel="yes"/>
                            <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                        </xsl:apply-templates>
                    </d:Sequence>
                    <!--creation of control construct from children (everything since we are at the root node), whose reference were created sooner-->
                    <xsl:apply-templates select="enoddi32:get-sequences($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-ControlConstructScheme', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="enoddi32:get-ifthenelses($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-ControlConstructScheme', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="enoddi32:get-questions($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-ControlConstructScheme', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="enoddi32:get-controls($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-ControlConstructScheme', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="enoddi32:get-statement-item($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-StatementItem', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>                    
                </d:ControlConstructScheme>
                <d:QuestionScheme>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="concat('QuestionScheme-',enoddi32:get-id($source-context))"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:Label><r:Content xml:lang="{enoddi32:get-lang($source-context)}">A définir</r:Content></r:Label>                  
                    <!-- This 'hack' is needed to output the question in correct order (QuestionItem first, then QuestionGrid). -->
                    <xsl:apply-templates select="enoddi32:get-questions-simple($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-QuestionScheme', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="enoddi32:get-questions-table($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-QuestionScheme', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                </d:QuestionScheme>
                
                <!-- CategoryScheme part -->
                <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                    <xsl:with-param name="driver" select="eno:append-empty-element('driver-CategoryScheme', .)" tunnel="yes"/>
                    <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                </xsl:apply-templates>
                <!-- If needed, add of a codeList for boolean representation. -->
                <xsl:if test="enoddi32:exist-boolean($source-context)">                    
                    <l:CategoryScheme>
                        <r:Agency><xsl:value-of select="enoddi32:get-agency($source-context)"/></r:Agency>
                        <r:ID><xsl:value-of select="concat('CategoryScheme-',enoddi32:get-id($source-context))"/></r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <r:Label><r:Content xml:lang="{enoddi32:get-lang($source-context)}">A définir</r:Content></r:Label>                        
                        <l:Category>		
                            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                            <r:ID>INSEE-COMMUN-CA-Booleen-1</r:ID>	
                            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                            <r:Label><r:Content xml:lang="{enoddi32:get-lang($source-context)}"/></r:Label>	
                        </l:Category>		
                    </l:CategoryScheme>
                </xsl:if>
                <!-- CodeListScheme Part -->
               <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                    <xsl:with-param name="driver" select="eno:append-empty-element('driver-CodeListScheme', .)" tunnel="yes"/>
                    <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
               </xsl:apply-templates>
                <!-- VariableScheme -->
                <l:VariableScheme>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="concat('VariableScheme-',enoddi32:get-id($source-context))"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:Label><r:Content xml:lang="{enoddi32:get-lang($source-context)}">Variable Scheme for the survey</r:Content></r:Label>
                    <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-VariableScheme', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="enoddi32:get-questions-table($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-VariableGroup', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                </l:VariableScheme>
                <!-- ProcessingInstructionScheme -->
                <d:ProcessingInstructionScheme>
                    <r:Agency>fr.insee</r:Agency>
                    <r:ID>INSEE-SIMPSONS-PIS-1</r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <d:ProcessingInstructionSchemeName>
                        <r:String xml:lang="en-IE">SIMPSONS</r:String>
                    </d:ProcessingInstructionSchemeName>
                    <r:Label>
                        <r:Content xml:lang="en-IE">Processing instructions of the Simpsons questionnaire</r:Content>
                    </r:Label>
                    <xsl:apply-templates select="enoddi32:get-generation-instructions($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-ProcessingInstructionScheme', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" tunnel="yes"/>                        
                    </xsl:apply-templates>
                </d:ProcessingInstructionScheme>
                <!-- ManagedRepresenationScheme part. Full hard-coded at the moment. -->
                <r:ManagedRepresentationScheme>
                    <r:Agency>fr.insee</r:Agency>
                    <r:ID>INSEE-SIMPSONS-MRS</r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:Label>
                        <r:Content xml:lang="en-IE">Liste de formats numériques et dates de
                            l'enquête</r:Content>
                        <r:Content xml:lang="en-IE">Numeric and DateTime list for the survey</r:Content>
                    </r:Label>
                    <r:ManagedDateTimeRepresentation>
                        <r:Agency>fr.insee</r:Agency>
                        <r:ID>INSEE-COMMUN-MNR-DateTimedate</r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <r:DateFieldFormat>jj/mm/aaaa</r:DateFieldFormat>
                        <r:DateTypeCode codeListID="INSEE-DTC-CV">date</r:DateTypeCode>
                    </r:ManagedDateTimeRepresentation>
                    <r:ManagedDateTimeRepresentation>
			            <r:Agency>fr.insee</r:Agency>
			            <r:ID>INSEE-COMMUN-MNR-DateTimedate-YYYY-MM</r:ID>
			            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
			            <r:DateFieldFormat>YYYY-MM</r:DateFieldFormat>
			            <r:DateTypeCode codeListID="INSEE-DTC-CV">gYearMonth</r:DateTypeCode>
			        </r:ManagedDateTimeRepresentation>
			        <r:ManagedDateTimeRepresentation>
			            <r:Agency>fr.insee</r:Agency>
			            <r:ID>INSEE-COMMUN-MNR-DateTimedate-YYYY</r:ID>
			            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
			            <r:DateFieldFormat>YYYY</r:DateFieldFormat>
			            <r:DateTypeCode codeListID="INSEE-DTC-CV">gYear</r:DateTypeCode>
			        </r:ManagedDateTimeRepresentation>
			        <r:ManagedDateTimeRepresentation>
			            <r:Agency>fr.insee</r:Agency>
			            <r:ID>INSEE-COMMUN-MNR-DateTimedate-PnYnM</r:ID>
			            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
			            <r:DateFieldFormat>PnYnM</r:DateFieldFormat>
			            <r:DateTypeCode codeListID="INSEE-DTC-CV">duration</r:DateTypeCode>
			        </r:ManagedDateTimeRepresentation>
			        <r:ManagedDateTimeRepresentation>
			            <r:Agency>fr.insee</r:Agency>
			            <r:ID>INSEE-COMMUN-MNR-DateTimedate-PTnHnM</r:ID>
			            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
			            <r:DateFieldFormat>PTnHnM</r:DateFieldFormat>
			            <r:DateTypeCode codeListID="INSEE-DTC-CV">duration</r:DateTypeCode>
			        </r:ManagedDateTimeRepresentation>
                </r:ManagedRepresentationScheme>
            </g:ResourcePackage>
            <s:StudyUnit xmlns="ddi:studyunit:3_2">
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="concat('StudyUnit-',enoddi32:get-id($source-context))"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:ExPostEvaluation/>
                <d:DataCollection>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="concat('DataCollection-',enoddi32:get-id($source-context))"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:QuestionSchemeReference>
                        <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                        <r:ID><xsl:value-of select="concat('QuestionScheme-',enoddi32:get-id($source-context))"/></r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <r:TypeOfObject>QuestionScheme</r:TypeOfObject>
                    </r:QuestionSchemeReference>
                    <r:ControlConstructSchemeReference>
                        <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                        <r:ID><xsl:value-of select="concat('ControlConstructScheme-',enoddi32:get-id($source-context))"/></r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <r:TypeOfObject>ControlConstructScheme</r:TypeOfObject>
                    </r:ControlConstructSchemeReference>
                    <r:InterviewerInstructionSchemeReference>
                        <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                        <r:ID><xsl:value-of select="concat('InterviewerInstructionScheme-',enoddi32:get-id($source-context))"/></r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <r:TypeOfObject>InterviewerInstructionScheme</r:TypeOfObject>
                    </r:InterviewerInstructionSchemeReference>
                    <d:InstrumentScheme xml:lang="{enoddi32:get-lang($source-context)}">
                        <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                        <r:ID><xsl:value-of select="concat('InstrumentScheme-',enoddi32:get-id($source-context))"/></r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <d:Instrument xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
                            xmlns:pr="ddi:ddiprofile:3_2" xmlns:c="ddi:conceptualcomponent:3_2"
                            xmlns:cm="ddi:comparative:3_2">
                            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                            <r:ID><xsl:value-of select="concat('Instrument-',enoddi32:get-id($source-context))"/></r:ID>
                            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                            <d:InstrumentName>
                                <r:String><xsl:value-of select="enoddi32:get-name($source-context)"/></r:String>
                            </d:InstrumentName>
                            <r:Label>
                                <r:Content xml:lang="{enoddi32:get-lang($source-context)}"><xsl:value-of select="enoddi32:get-label($source-context)"/> questionnaire</r:Content>
                            </r:Label>
                            <d:TypeOfInstrument>A définir</d:TypeOfInstrument>
                            <d:ControlConstructReference>
                                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                                <r:ID><xsl:value-of select="enoddi32:get-main-sequence-id($source-context)"/></r:ID>
                                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                                <r:TypeOfObject>Sequence</r:TypeOfObject>
                            </d:ControlConstructReference>
                        </d:Instrument>
                    </d:InstrumentScheme>
                </d:DataCollection>
            </s:StudyUnit>
        </DDIInstance>
    </xsl:template>


    <xsl:template match="driver-VariableScheme//Variable" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:variable name="id" select="enoddi32:get-id($source-context)"/>
        <xsl:variable name="driver" select="."/>
        <l:Variable>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="$id"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <l:VariableName>
                <r:String xml:lang="fr-FR"><xsl:value-of select="enoddi32:get-name($source-context)"/></r:String>
            </l:VariableName>
            <r:Label>
                <r:Content xml:lang="fr-FR"><xsl:value-of select="enoddi32:get-label($source-context)"/></r:Content>
            </r:Label>
            <!-- It's a hack to test if Variable got a related-response (aka = CollectedVariable), only 0 or 1 are expected, could be done better way (@att on driver ?). -->
            <xsl:for-each select="enoddi32:get-related-response($source-context)">
                <xsl:variable name="idQuestion" select="enoddi32:get-parent-id(current())"/>
                <xsl:variable name="idResponse" select="enoddi32:get-id(current())"/>
                <r:SourceParameterReference>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-qop-id(current())"/></r:ID>                    
                    <r:Version><xsl:value-of select="enoddi32:get-version(current())"/></r:Version>
                    <r:TypeOfObject>OutParameter</r:TypeOfObject>
                </r:SourceParameterReference>
                <xsl:apply-templates select="." mode="enoddi32:question-reference"/>
                <l:VariableRepresentation>
                	<!-- Representation of variable: text, numeric, date ... -->
                    <xsl:apply-templates select="eno:child-fields(.)" mode="source">
                        <xsl:with-param name="driver" select="$driver" tunnel="yes"/>
                    </xsl:apply-templates>   
                </l:VariableRepresentation>
            </xsl:for-each>
            <!-- It's a dirty (large part is static) hack to test if Variable got formula (aka = CalcultatedVariable), only 0 or 1 are expected, could be done better way (@att on driver ?) -->
            <xsl:for-each select="enoddi32:get-processing-instruction($source-context)">
                <r:OutParameter>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-vrop-id(current())"/></r:ID>                    
                    <r:Version><xsl:value-of select="enoddi32:get-version(current())"/></r:Version>
                </r:OutParameter>
                <l:VariableRepresentation>
                    <r:ProcessingInstructionReference>                        
                        <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                        <r:ID><xsl:value-of select="enoddi32:get-gi-id(current())"/></r:ID>                    
                        <r:Version><xsl:value-of select="enoddi32:get-version(current())"/></r:Version>
                        <r:TypeOfObject>GenerationInstruction</r:TypeOfObject>
                        <r:Binding>
                            <r:SourceParameterReference>
                                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                                <r:ID><xsl:value-of select="enoddi32:get-qop-id(current())"/></r:ID>
                                <r:Version><xsl:value-of select="enoddi32:get-version(current())"/></r:Version>
                                <r:TypeOfObject>OutParameter</r:TypeOfObject>
                            </r:SourceParameterReference>
                            <r:TargetParameterReference>
                                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                                <r:ID><xsl:value-of select="enoddi32:get-vrop-id(current())"/></r:ID>
                                <r:Version><xsl:value-of select="enoddi32:get-version(current())"/></r:Version>
                                <r:TypeOfObject>OutParameter</r:TypeOfObject>
                            </r:TargetParameterReference>
                        </r:Binding>
                    </r:ProcessingInstructionReference>
                    <r:NumericRepresentation/>
                </l:VariableRepresentation>
            </xsl:for-each>
        </l:Variable>
    </xsl:template>
    
    <xsl:template match="driver-VariableScheme//CodeListReference" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:if test="enoddi32:get-code-list-id($source-context) != '' ">
			<r:CodeRepresentation>
		        <r:CodeListReference>	
		            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
		            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
		            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
		            <r:TypeOfObject>CodeList</r:TypeOfObject>
		        </r:CodeListReference>
		    </r:CodeRepresentation>
    	</xsl:if>
    </xsl:template>
    
    <xsl:template match="driver-VariableScheme//Unit" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:variable name="unit" select="enoddi32:get-unit($source-context)"/>
        <xsl:if test="not(normalize-space($unit) = ('',' '))">
            <r:MeasurementUnit><xsl:value-of select="$unit"/></r:MeasurementUnit>
        </xsl:if>
    </xsl:template>

    <xsl:template match="driver-VariableGroup//QuestionDynamicTable" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:variable name="id" select="enoddi32:get-id($source-context)"/>
        
        <l:VariableGroup>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="concat($id,'-gp')"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:BasedOnObject>
                <r:BasedOnReference>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="$id"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:TypeOfObject>QuestionGrid</r:TypeOfObject>
                </r:BasedOnReference>
            </r:BasedOnObject>
            <l:TypeOfVariableGroup>TableLoop</l:TypeOfVariableGroup>
            <l:VariableGroupName>
                <r:String><xsl:value-of select="enoddi32:get-name($source-context)"/></r:String>
            </l:VariableGroupName>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </l:VariableGroup>
    </xsl:template>
    
    <xsl:template match="driver-VariableGroup//QuestionDynamicTable//ResponseDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>

        <xsl:variable name="relatedVariable" select="enoddi32:get-related-variable($source-context)"/>

        <r:VariableReference>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($relatedVariable)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:TypeOfObject>Variable</r:TypeOfObject>
        </r:VariableReference>
    </xsl:template>

    <xsl:template match="driver-InterviewerInstructionScheme//*[name() = ('Instruction','Control')]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:Instruction>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <!-- Use of a specific getter for instruction to cover both Instruction&Control in one case, regular getter are called in Instruction context, specific in Control context -->
            <r:ID><xsl:value-of select="enoddi32:get-instruction-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <d:InstructionName>
                <r:String xml:lang="{enoddi32:get-lang($source-context)}">
                    <!-- Use of a specific getter for instruction to cover both Instruction&Control in one case, regular getter are called in Instruction context, specific in Control context -->                    
                    <xsl:value-of select="enoddi32:get-instruction-name($source-context)"/>
                </r:String>
            </d:InstructionName>
            <d:InstructionText>
                <d:LiteralText>
                    <d:Text xml:lang="{enoddi32:get-lang($source-context)}">
                        <!-- Use of a specific getter for instruction to cover both Instruction&Control in one case, regular getter are called in Instruction context, specific in Control context -->                        
                        <xsl:value-of select="enoddi32:get-instruction-text($source-context)"/>
                    </d:Text>
                </d:LiteralText>
                <xsl:if test="enoddi32:is-with-conditionnal-text($source-context) = true()">
                    <xsl:for-each
                        select="enoddi32:get-related-variable($source-context)[enoddi32:get-type(.) = ('CollectedVariableType', 'CalculatedVariableType')]">
                        <d:ConditionalText>
                        <r:SourceParameterReference>
                            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                            <r:ID><xsl:value-of select="enoddi32:get-qop-id(.)"/></r:ID>
                            <r:Version><xsl:value-of select="enoddi32:get-version(.)"/></r:Version>                            
                            <r:TypeOfObject>OutParameter</r:TypeOfObject>
                        </r:SourceParameterReference>
                        </d:ConditionalText>
                    </xsl:for-each>
                <xsl:for-each
                        select="enoddi32:get-related-variable($source-context)[enoddi32:get-type(.) = ('ExternalVariableType')]">
                        <d:ConditionalText>
                            <r:SourceParameterReference>
                                <r:Agency>
                                    <xsl:value-of select="$agency"/>
                                </r:Agency>
                                <r:ID>
                                    <xsl:value-of select="enoddi32:get-name(.)"/>
                                </r:ID>
                                <r:Version>
                                    <xsl:value-of select="enoddi32:get-version(.)"/>
                                </r:Version>
                                <r:TypeOfObject>InParameter</r:TypeOfObject>
                            </r:SourceParameterReference>
                        </d:ConditionalText>
                    </xsl:for-each>

                </xsl:if>
            </d:InstructionText>
        </d:Instruction>
    </xsl:template>

    <!--this part is disigned in this complicated way to maintain the order of the ddi 3.2 xsd schema-->
    <xsl:template match="driver-InterviewerInstructionReference//*" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
    </xsl:template>
    
    <xsl:template match="driver-InterviewerInstructionReference//Instruction" mode="model" priority="3">           
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:InterviewerInstructionReference>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:TypeOfObject>Instruction</r:TypeOfObject>
        </d:InterviewerInstructionReference>
    </xsl:template>
    
    
    <xsl:template match="driver-ExternalAid//*" mode="model" priority="3">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
    </xsl:template>
    
    <xsl:template match="driver-ExternalAid//FlowControl" mode="model" priority="4">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:ExternalAid>
            <xsl:variable name="ID" select="enoddi32:get-id($source-context)"/>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="$ID"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:Description>
                <r:Content>
                    <xhtml:div class="FlowControl" id="{$ID}">
                        <xhtml:div class="Description"><xsl:value-of select="enoddi32:get-description($source-context)"/></xhtml:div>                        
                        <xhtml:div class="Expression"><xsl:value-of select="enoddi32:get-expression($source-context)"/></xhtml:div>
                        <xhtml:div class="IfTrue"><xsl:value-of select="enoddi32:get-if-true($source-context)"/></xhtml:div>
                    </xhtml:div>
                </r:Content>
            </r:Description>
        </d:ExternalAid>
    </xsl:template>

    <xsl:template match="driver-CodeListScheme//CodeLists" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" tunnel="yes"/>
        <!-- All CodeLists (regular, fakes and boolean) are stored in a same CodeListScheme. -->
        <l:CodeListScheme xmlns="ddi:instance:3_2">
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="concat(enoddi32:get-survey-name($source-context),'-CLS')"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <l:CodeListSchemeName>
                <r:String xml:lang="en-IE"><xsl:value-of select="enoddi32:get-survey-name($source-context)"/></r:String>
            </l:CodeListSchemeName>
            <!-- Output the regular codeLists -->
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
            <!-- If needed, create the fake codeLists for TABLE question with implicit second dimension. -->
            <xsl:variable name="fake-code-lists" select="enoddi32:get-fake-code-lists($source-context)"/>
            <xsl:if test="$fake-code-lists">
                <xsl:apply-templates select="$fake-code-lists" mode="source">
                    <xsl:with-param name="driver" select="." tunnel="yes"/>
                </xsl:apply-templates>               
            </xsl:if>
            <!-- If needed, create the boolean codeList. -->
            <xsl:if test="enoddi32:exist-boolean($source-context)">
                <l:CodeList>
                    <r:Agency>fr.insee</r:Agency>
                    <r:ID>INSEE-COMMUN-CL-Booleen</r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <l:CodeListName>
                        <r:String xml:lang="fr-FR">Booleen</r:String>
                    </l:CodeListName>
                    <l:HierarchyType>Regular</l:HierarchyType>
                    <l:Level levelNumber="1">
                        <l:CategoryRelationship>Ordinal</l:CategoryRelationship>
                    </l:Level>
                    <l:Code levelNumber="1" isDiscrete="true">
                        <r:Agency>fr.insee</r:Agency>
                        <r:ID>INSEE-COMMUN-CL-Booleen-1</r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <r:CategoryReference>
                            <r:Agency>fr.insee</r:Agency>
                            <r:ID>INSEE-COMMUN-CA-Booleen-1</r:ID>
                            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                            <r:TypeOfObject>Category</r:TypeOfObject>
                        </r:CategoryReference>
                        <r:Value>1</r:Value>
                    </l:Code>
                </l:CodeList>                                
            </xsl:if>                                
        </l:CodeListScheme>
    </xsl:template>
    
    <xsl:template match="driver-CodeListScheme//CodeList" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <l:CodeList>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID>                
                <xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:Label>
                <r:Content xml:lang="{enoddi32:get-lang($source-context)}">
                    <xsl:value-of select="enoddi32:get-label($source-context)"/>
                </r:Content>
            </r:Label>
            <!--TODO define HierarchyType-->
            <!-- Enumeration:            
            "Regular" A hierarchy where each section has the same number of nested levels, i.e., the lowest level represents the most discrete values.
            "Irregular" A hierarchy where each section can vary in the number of nested levels it contains. The most discrete objects in an irregular hierarchy must be individually identified.
            -->
            <l:HierarchyType>Regular</l:HierarchyType>
            <!--TODO : define levelNumber-->            
            <l:Level levelNumber="1">    
            <!--TODO : Enumeration:	
"Nominal" A relationship of less than, or greater than, cannot be established among the included categories. This type of relationship is also called categorical or discrete.
"Ordinal" The categories in the domain have a rank order.
"Interval" The categories in the domain are in rank order and have a consistent interval between each category so that differences between arbitrary pairs of measurements can be meaningfully compared.
"Ratio" The categories have all the features of interval measurement and also have meaningful ratios between arbitrary pairs of numbers.
"Continuous" May be used to identify both interval and ratio classification levels, when more precise information is not available.-->                
                <l:CategoryRelationship>Ordinal</l:CategoryRelationship>
            </l:Level>                
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </l:CodeList>
    </xsl:template>

    <xsl:template match="driver-CodeListScheme//Code" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:param name="levelNumber" as="xs:integer" tunnel="yes" select="1"/>        
        <l:Code levelNumber="{$levelNumber}" isDiscrete="{enoddi32:is-discrete($source-context)}">
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID>
                <xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:CategoryReference>
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID>CA-<xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:TypeOfObject>Category</r:TypeOfObject>
            </r:CategoryReference>
            <r:Value>
                <xsl:value-of select="enoddi32:get-value($source-context)"/>
            </r:Value>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
                <xsl:with-param name="levelNumber" select="$levelNumber + 1" tunnel="yes"/>
            </xsl:apply-templates>
        </l:Code>
    </xsl:template>

    <xsl:template match="driver-CategoryScheme//CodeLists" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
        <!-- Adding fake categorySchemes -->
        <xsl:variable name="fake-code-lists" select="enoddi32:get-fake-code-lists($source-context)"/>
        <xsl:if test="$fake-code-lists">
            <xsl:apply-templates select="$fake-code-lists" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="driver-CategoryScheme//CodeList" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" tunnel="yes"/>
        <l:CategoryScheme>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="concat('CategoryScheme-',enoddi32:get-id($source-context))"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:Label><r:Content xml:lang="{enoddi32:get-lang($source-context)}"><xsl:value-of select="enoddi32:get-label($source-context)"/></r:Content></r:Label>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </l:CategoryScheme>
    </xsl:template>

    <xsl:template match="driver-CategoryScheme//Code" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <l:Category>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID>CA-<xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:Label>
                <r:Content xml:lang="{enoddi32:get-lang($source-context)}">
                    <xsl:value-of select="enoddi32:get-label($source-context)"/>
                </r:Content>
            </r:Label>
        </l:Category>
        <!-- Needed because Category should be flat when Codes are hierarchical. -->
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="driver-ControlConstructScheme//Sequence" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:Sequence>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <d:ConstructName>
                <r:String xml:lang="{enoddi32:get-lang($source-context)}"><xsl:value-of select="enoddi32:get-name($source-context)"></xsl:value-of></r:String>
            </d:ConstructName>                       
            <r:Label>
                <r:Content xml:lang="{enoddi32:get-lang($source-context)}">
                    <xsl:value-of select="enoddi32:get-label($source-context)"/>
                </r:Content>
            </r:Label>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('driver-InterviewerInstructionReference', .)" tunnel="yes"/>
            </xsl:apply-templates>
            <d:TypeOfSequence codeListID="INSEE-TOS-CL-1">
                <xsl:value-of select="enoddi32:get-sequence-type($source-context)"/>
            </d:TypeOfSequence>
            <xsl:apply-templates select="enoddi32:get-related-controls($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </d:Sequence>
    </xsl:template>
    
    <xsl:template name="StatementItem" match="driver-StatementItem//Instruction[not(ancestor::driver-InterviewerInstructionReference)]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>        
        <d:StatementItem>
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-si-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <!--<xsl:apply-templates select="$source-context" mode="source">
                    <xsl:with-param name="driver" select="eno:append-empty-element('driver-InterviewerInstructionReference',.)" tunnel="yes"/>
                </xsl:apply-templates>-->
            <d:DisplayText>
                <d:LiteralText>
                    <d:Text xml:lang="{enoddi32:get-lang($source-context)}">
                        <xsl:value-of select="enoddi32:get-instruction-text($source-context)"/>
                    </d:Text>
                </d:LiteralText>
            </d:DisplayText>
        </d:StatementItem>
    </xsl:template>
    
    <!-- ComputationItem only for Control -->
    <!-- <xsl:template name="ComputationItem" match="driver-ControlConstructScheme/Control | driver-ControlConstructScheme/ResponseDomain" mode="model"> -->
    <xsl:template name="ComputationItem" match="driver-ControlConstructScheme/Control" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:ComputationItem>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-ci-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <d:ConstructName>
                <r:String xml:lang="{enoddi32:get-lang($source-context)}"><xsl:value-of select="enoddi32:get-ci-name($source-context)"/></r:String>
            </d:ConstructName>
            <r:Description>
                <r:Content xml:lang="{enoddi32:get-lang($source-context)}"><xsl:value-of select="enoddi32:get-ci-name($source-context)"/></r:Content>
            </r:Description>
            <!-- If it's a control, an instruction would be generated, if it's a mandatory response, the instruction is not generated, it's handled by the output format. -->
            <xsl:if test="self::Control">
                <d:InterviewerInstructionReference>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-generated-instruction-id($source-context)"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:TypeOfObject>Instruction</r:TypeOfObject>
                </d:InterviewerInstructionReference>
            </xsl:if>
            <!-- Have a simpler way to deal with regular controls & mandatory response. -->
            <!-- An apply-templates on Expression will Output CommandeCode for Regular Control. -->
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
            <!-- But in case of Mandatory Response, no Regular Control but an induced one, so an explicit call is required. -->
            <xsl:if test="self::ResponseDomain">
                <xsl:call-template name="CommandCode">
                    <!-- In this explicit call, the ResponseDomain itself is used to generated the CommandCode -->    
                    <xsl:with-param name="source-context" select="$source-context" tunnel="yes"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:variable name="type" select="enoddi32:get-ci-type($source-context)"/>
            <xsl:if test="not(normalize-space($type)=('',' '))">
                <xsl:comment>
                    <xsl:text disable-output-escaping="yes">&lt;r:TypeOfComputationItem&gt;</xsl:text>
                        <xsl:value-of select="$type"/>
                    <xsl:text disable-output-escaping="yes">&lt;/r:TypeOfComputationItem&gt;</xsl:text>                    
                </xsl:comment>
            </xsl:if>
        </d:ComputationItem>
    </xsl:template>
    
    <xsl:template match="driver-ProcessingInstructionScheme//*" mode="model">
        <xsl:param name="source-context" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="."/>
        </xsl:apply-templates>
    </xsl:template>
    
    <!-- This is a quick&dirty IfThenElse support. Needs a refactor priority is a mess to maintain with several levels. -->    
    <xsl:template match="driver-ThenSequence//Command" mode="model" priority="3"/>
    
    <xsl:template name="CommandCode" match="*[name(.) =('driver-ControlConstructScheme','driver-ProcessingInstructionScheme')]//Command" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" tunnel="yes"/>
        <!-- Use of a call-template because this part is common whit IfCondition/Command but latter doesn't support parent CommandeCode -->
        <r:CommandCode>
            <xsl:call-template name="Command">
                <xsl:with-param name="source-context" select="$source-context" tunnel="yes"/>
            </xsl:call-template>
        </r:CommandCode>
        <!-- Define the scope of the Variable, because of loop. Outside loops, it's the main sequence which is referenced. -->
        <xsl:if test="ancestor::driver-ProcessingInstructionScheme">
            <d:ControlConstructReference>
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-referenced-sequence-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:TypeOfObject>Sequence</r:TypeOfObject>
            </d:ControlConstructReference>
        </xsl:if>
    </xsl:template>
    
    <!-- This specific template is needed because for IfInstruction Command no CommandCode is required (driver missing ?) -->
    <xsl:template name="Command">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" tunnel="yes"/>
        <!-- Getting all related variables from the command expression to build parameters and bindings -->
        <!-- Could be done if needed in the parent (external variable case) so it's a param not a variable -->
        <xsl:param name="related-variables" select="enoddi32:get-related-variable($source-context)" tunnel="yes"/>
        <!-- Calculating ids needed from the related-variable -->
        <xsl:variable name="related-variables-with-id">
            <xsl:for-each select="$related-variables">
                <xsl:variable name="ip-id" select="enoddi32:get-ip-id($source-context,position())"/>
                <xsl:variable name="related-response" select="enoddi32:get-related-response(.)"/>
                <!-- A container is created to save all the pre-calculted data to output the command. -->
                <Container xmlns="" xsl:exclude-result-prefixes="#all">
                    <!-- Corresponding to the inputParameter id. -->
                    <ip-id><xsl:value-of select="$ip-id"/></ip-id>
                    <!-- Corresponding to the id used in the final command code for the current variable. -->
                    <command-id><xsl:value-of select="enoddi32:get-command-id(.,$ip-id)"/></command-id>
                    <!-- Corresponding to the Out Parameter.
                        TODO : Rename correctly the getter, to get-op-id ?-->
                    <qop-id>
                        <xsl:value-of select="enoddi32:get-qop-id(.)"/>
                    </qop-id>
                    <name><xsl:value-of select="enoddi32:get-name(.)"/></name>
                    <type>
                        <xsl:value-of select="enoddi32:get-type(.)"/>
                    </type>
                </Container>
            </xsl:for-each>
        </xsl:variable>                
        <r:Command>
            <r:ProgramLanguage>xpath</r:ProgramLanguage>
            <xsl:for-each select="$related-variables-with-id/*">                        
                <r:InParameter isArray="false">
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>                        
                    <r:ID><xsl:value-of select="ip-id"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:ParameterName>
                        <r:String xml:lang="{enoddi32:get-lang($source-context)}"><xsl:value-of select="name"/></r:String>
                    </r:ParameterName>
                </r:InParameter>
            </xsl:for-each>
            <!-- Adding outParameter based on the context (for calculated Variable, but not needed for Filtrer&Control Command) -->
            <xsl:if test="ancestor::driver-ProcessingInstructionScheme">
                <r:OutParameter>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-qop-id($source-context)"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                </r:OutParameter>
            </xsl:if>
            <xsl:for-each select="$related-variables-with-id/*">
                <r:Binding>
                    <r:SourceParameterReference>
                        <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                        <xsl:choose>
                            <!-- Use Name for ExternalVariableType -->
                            <xsl:when test="./type = 'ExternalVariableType'">
                                <r:ID>
                                    <xsl:value-of select="./name"/>
                                </r:ID>
                                <r:Version>
                                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                                </r:Version>
                                <r:TypeOfObject>InParameter</r:TypeOfObject>
                            </xsl:when>
                            <xsl:otherwise>
                                <r:ID>
                                    <xsl:value-of select="./qop-id"/>
                                </r:ID>
                                <r:Version>
                                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                                </r:Version>
                                <r:TypeOfObject>OutParameter</r:TypeOfObject>
                            </xsl:otherwise>
                        </xsl:choose>
                        </r:SourceParameterReference>
                    <r:TargetParameterReference>
                        <r:Agency><xsl:value-of select="$agency"/></r:Agency>                        
                        <r:ID><xsl:value-of select="ip-id"/></r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <r:TypeOfObject>InParameter</r:TypeOfObject>
                    </r:TargetParameterReference>
                </r:Binding>                    
            </xsl:for-each>
            <r:CommandContent>
                <xsl:choose>
                    <!-- Calling the specifc template to handle CommandContent when in regular Command case. -->
                    <xsl:when test="self::Command or ancestor-or-self::IfThenElse">
                        <xsl:call-template name="replace-pogues-name-variable-by-ddi-id-ip">
                            <xsl:with-param name="expression" select="concat($source-context,' ')"/>
                            <xsl:with-param name="current-variable-with-id" select="$related-variables-with-id/*[1]"/>
                        </xsl:call-template>        
                    </xsl:when>
                    <!-- When no driver is associated (= no regular Control case), it's a mandatory response Case, for this case only single answer is implemented. -->
                    <xsl:when test="count($related-variables-with-id/*)=1">
                        <!-- The command value associated with the error message Instruction is VAR1 = '' -->
                        <xsl:value-of select="concat('if ',$related-variables-with-id/*[1]/ip-id,'=&apos;'''')"/>                            
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:message>Only madatory Response with unique answer is supported.</xsl:message>    
                    </xsl:otherwise>
                </xsl:choose>                    
            </r:CommandContent>
        </r:Command>        
    </xsl:template>
    
    <!-- This utility template is used to recursively replace names of pogues:Variable in an expression by their correspondant id of ddi:InParameter -->
    <xsl:template name="replace-pogues-name-variable-by-ddi-id-ip">
        <xsl:param name="expression"/>
        <xsl:param name="current-variable-with-id"/>
        <xsl:variable name="next-variable-with-id" select="$current-variable-with-id/following-sibling::*[1]"/>
        <!--        <xsl:variable name="new-expression" select="replace($expression,concat('\$',$current-variable-with-id/name),$current-variable-with-id/command-id)"/>-->
        <xsl:variable name="current-variable-name" select="$current-variable-with-id/name"/>
        <xsl:variable name="new-expression">
            <xsl:analyze-string select="$expression" regex="(\${$current-variable-name}( |\$))">
                <xsl:matching-substring>
                    <xsl:value-of select="$current-variable-with-id/command-id"/>
                </xsl:matching-substring>
                <xsl:non-matching-substring>
                    <xsl:copy-of select="."/>
                </xsl:non-matching-substring>
            </xsl:analyze-string>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$next-variable-with-id">
                <xsl:call-template name="replace-pogues-name-variable-by-ddi-id-ip">
                    <xsl:with-param name="expression" select="$new-expression"/>
                    <xsl:with-param name="current-variable-with-id" select="$next-variable-with-id"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$new-expression"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template match="driver-ProcessingInstructionScheme//Variable" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:GenerationInstruction>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-gi-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <xsl:variable name="related-variables" select="enoddi32:get-related-variable($source-context)"/>
            <xsl:comment><![CDATA[<d:SourceQuestion>Not implemented.</d:SourceQuestion>]]></xsl:comment>
            <xsl:for-each select="$related-variables">            
            <d:SourceVariable>
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id(.)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:TypeOfObject>Variable</r:TypeOfObject>
            </d:SourceVariable>
            </xsl:for-each>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
                <xsl:with-param name="related-variables" select="$related-variables" tunnel="yes"/>
            </xsl:apply-templates>
        </d:GenerationInstruction>
    </xsl:template>

    <!-- that does'nt work (I hate you!!!!!!!!!!!!!!!!!) -->
    <xsl:template match="driver-ControlConstructScheme//IfThenElse" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:IfThenElse>					
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>				
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:Label>
                <r:Content xml:lang="{enoddi32:get-lang($source-context)}">A définir</r:Content>
            </r:Label>
            <r:Description>
                <r:Content xml:lang="{enoddi32:get-lang($source-context)}">
                    <xsl:value-of select="enoddi32:get-description($source-context)"/>
                </r:Content>                
            </r:Description>
            <d:IfCondition>
                <xsl:call-template name="Command">
                    <xsl:with-param name="source-context" select="enoddi32:get-command($source-context)" tunnel="yes"/>
                </xsl:call-template>
           </d:IfCondition>				
            <d:ThenConstructReference>				
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>		
                <r:ID><xsl:value-of select="enoddi32:get-then-sequence-id($source-context)"/></r:ID>			
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:TypeOfObject>Sequence</r:TypeOfObject>			
            </d:ThenConstructReference>				
        </d:IfThenElse>					
        <d:Sequence>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-then-sequence-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:Label>
                <r:Content xml:lang="{enoddi32:get-lang($source-context)}">
                    <xsl:value-of select="enoddi32:get-label($source-context)"/>
                </r:Content>
            </r:Label>
            <d:TypeOfSequence codeListID="INSEE-TOS-CL-1">hideable</d:TypeOfSequence>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('driver-ThenSequence',.)" tunnel="yes"/>
            </xsl:apply-templates>
        </d:Sequence>
    </xsl:template>
    
    
    <xsl:template name="ControlConstructReference" match="*[name()=('Sequence','IfThenElse')]//*[name() =('Sequence','IfThenElse','QuestionMultipleChoice','QuestionSingleChoice','QuestionTable','QuestionDynamicTable','QuestionSimple','Control','ResponseDomain')]" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:variable name="driver" select="."/>
        <xsl:for-each select="enoddi32:get-instructions($source-context)[enoddi32:get-position(.) = 'BEFORE_QUESTION_TEXT']">                        
            <d:ControlConstructReference>
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-si-id(.)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version(.)"/></r:Version>
                <r:TypeOfObject>StatementItem</r:TypeOfObject>
            </d:ControlConstructReference>
        </xsl:for-each>
        <d:ControlConstructReference>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-reference-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:TypeOfObject><xsl:value-of select="enoddi32:get-reference-element-name($source-context)"/></r:TypeOfObject>
        </d:ControlConstructReference>
        <xsl:if test="not(name()=('Sequence','IfThenElse'))">
            <xsl:apply-templates select="enoddi32:get-related-controls($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
   
    <xsl:template match="QuestionSimple//ResponseDomain[not(ancestor::driver-ControlConstructScheme)]" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
            <xsl:with-param name="mandatory" select="enoddi32:get-ci-type($source-context)" tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <!--this part is disigned in this complicated way to maintain the order of the ddi 3.2 xsd schema-->
    <xsl:template match="driver-Binding//*" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
    </xsl:template>
 
    <xsl:template name="QuestionConstruct" match="driver-ControlConstructScheme//*[name() = ('QuestionMultipleChoice','QuestionTable','QuestionDynamicTable','QuestionSimple','QuestionSingleChoice')]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>        
        <d:QuestionConstruct>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-qc-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <d:ConstructName>
                <xsl:element name="r:String">
                   <xsl:attribute name="xml:lang" select="enoddi32:get-lang($source-context)"/>
                   <xsl:value-of select="enoddi32:get-name($source-context)"/>
                </xsl:element>
            </d:ConstructName>
            <xsl:apply-templates select="$source-context" mode="enoddi32:question-reference"/>
        </d:QuestionConstruct>
    </xsl:template>

    <xsl:template match="driver-QuestionScheme//QuestionSingleChoice//ResponseDomain" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:variable name="mandatory" select="enoddi32:get-ci-type($source-context)"/>
        <d:CodeDomain>            
            <r:GenericOutputFormat codeListID="INSEE-GOF-CV"><xsl:value-of select="enoddi32:get-generic-output-format($source-context)"/></r:GenericOutputFormat>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
            <r:OutParameter isArray="false">
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-rdop-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:CodeRepresentation>
                    <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                        <xsl:with-param name="driver" select="eno:append-empty-element('driver-CodeListReference', .)" tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                </r:CodeRepresentation>
            </r:OutParameter>
			<r:ResponseCardinality>
				<xsl:if test="$mandatory = 'mandatory'">
					<xsl:attribute name="minimumResponses">1</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="maximumResponses">1</xsl:attribute>
			</r:ResponseCardinality>
        </d:CodeDomain>
    </xsl:template>
    
    <xsl:template match="driver-SMGRD/ResponseDomain" mode="model" priority="3">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <!-- Because of the xsl:for-each, driver context needs to be kept. -->
        <xsl:variable name="driver" select="."/>
        <xsl:for-each select="enoddi32:get-grid-dimensions($source-context)">
            <d:GridResponseDomain>
               <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                    <xsl:with-param name="driver" select="$driver" tunnel="yes"/>
                    <xsl:with-param name="mandatory" select="enoddi32:get-ci-type($source-context)" tunnel="yes"/>
                </xsl:apply-templates>
                <d:GridAttachment>
                    <d:CellCoordinatesAsDefined>
                        <xsl:for-each select="enoddi32:get-cell-coordinates($source-context)">
                            <d:SelectDimension rank="{position()}" rangeMinimum="{.}" rangeMaximum="{.}"/>
                        </xsl:for-each>
                    </d:CellCoordinatesAsDefined>
                </d:GridAttachment>
            </d:GridResponseDomain>
        </xsl:for-each>
    </xsl:template>
    
    <!-- This template is only matched when call just after driver-ResponseDomain (why it got 3 priority), to check if SMR is needed. -->
    <xsl:template match="driver-ResponseDomain/QuestionSimple | driver-ResponseDomain/QuestionSingleChoice" mode="model" priority="3">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates> 
    </xsl:template>
    
    <!-- This template is only matched when call just after driver-ResponseDomain (why it got 3 priority), to check if SMR is needed. -->
    <xsl:template match="driver-ResponseDomain/QuestionDynamicTable | driver-ResponseDomain/QuestionTable | driver-ResponseDomain/QuestionMultipleChoice" mode="model" priority="3">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <d:StructuredMixedGridResponseDomain>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('driver-SMGRD', .)" tunnel="yes"/>
            </xsl:apply-templates> 
        </d:StructuredMixedGridResponseDomain>
    </xsl:template>

    <xsl:template name="Question" match="driver-QuestionScheme//*[name() = ('QuestionMultipleChoice','QuestionTable','QuestionDynamicTable','QuestionSimple','QuestionSingleChoice')]" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <!-- 
            Question drivers are implemented in a specific way to maintain strict ddi elements order.
            "Fake" drivers are used, they're named with a 'driver-' prefix and they control the exact output flow intended.
        -->
        <!-- Retrieve the ddi corresponding element name and store it -->
        <xsl:variable name="ddi-element-name">
            <xsl:choose>
                <xsl:when test="./name() = ('QuestionMultipleChoice','QuestionTable','QuestionDynamicTable')">
                    <xsl:value-of select="'d:QuestionGrid'"/>
                </xsl:when>
                <xsl:when test="./name() = ('QuestionSimple','QuestionSingleChoice')">
                    <xsl:value-of select="'d:QuestionItem'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:message select="concat('Driver ',./name(),' is not implemented as a Question')"/>                
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <!-- Retrieving the ddi corresponding element name for the QuestionName and store it. -->
        <xsl:variable name="ddi-question-name-element">
            <xsl:choose>
                <xsl:when test="$ddi-element-name = 'd:QuestionGrid'">
                    <xsl:value-of select="'d:QuestionGridName'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'d:QuestionItemName'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <!-- Creating the ddi question element. -->
        <xsl:element name="{$ddi-element-name}">
            <!-- Getting the triplet Agency/Id/Version -->
            <xsl:element name="r:Agency"><xsl:value-of select="$agency"/></xsl:element>
            <xsl:element name="r:ID"><xsl:value-of select="enoddi32:get-id($source-context)"/></xsl:element>
            <xsl:element name="r:Version"><xsl:value-of select="enoddi32:get-version($source-context)"/></xsl:element>
            <!-- QuestionName part -->
            <xsl:element name="{$ddi-question-name-element}">                
               <xsl:element name="r:String">
                   <xsl:attribute name="xml:lang" select="enoddi32:get-lang($source-context)"/>
                   <xsl:value-of select="enoddi32:get-name($source-context)"/>
               </xsl:element>                
            </xsl:element>            
            <!-- OutParameter part -->
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('driver-OutParameter', .)" tunnel="yes"/>
                <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
            </xsl:apply-templates>
            <!-- Binding part -->
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('driver-Binding', .)" tunnel="yes"/>
                <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
            </xsl:apply-templates>            
            <!-- QuestionText -->
            <xsl:element name="d:QuestionText">            
                <xsl:element name="d:LiteralText">
                    <xsl:element name="d:Text">
                        <xsl:attribute name="xml:lang" select="enoddi32:get-lang($source-context)"/>
                        <xsl:value-of select="enoddi32:get-label($source-context)"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <!-- GridDimension part -->
            <!--No need of any fake drivers as this part has common behavious for all questionTypes.-->
            <xsl:if test="$ddi-element-name = 'd:QuestionGrid'">
                <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                    <xsl:with-param name="driver" select="." tunnel="yes"/>
                </xsl:apply-templates>
            </xsl:if>           
            <!-- ResponseDomain Part. -->
            <!-- As a StructuredMixedGridResponseDomain may be needed, use of driver-ResponseDomain on the current source context instead of its childs. -->               
             <xsl:apply-templates select="$source-context" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('driver-ResponseDomain', .)" tunnel="yes"/>
             </xsl:apply-templates>
            <!--External-Aid part - Used to store specific Pogues UI element -->
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('driver-ExternalAid', .)" tunnel="yes"/>
                <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
            </xsl:apply-templates>
            <!-- Instruction part -->
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="eno:append-empty-element('driver-InterviewerInstructionReference', .)" tunnel="yes"/>
                <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>

    <!--this part is disigned in this complicated way to maintain the order of the ddi 3.2 xsd schema-->
    <xsl:template match="driver-OutParameter//*" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
    </xsl:template>
   
    
    <!--this part is disigned in this complicated way to maintain the order of the ddi 3.2 xsd schema-->
    <xsl:template match="driver-OutParameter//ResponseDomain" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:element name="r:OutParameter">
            <xsl:attribute name="isArray" select="'false'"/>
            <xsl:element name="r:Agency"><xsl:value-of select="$agency"/></xsl:element>
            <xsl:element name="r:ID"><xsl:value-of select="enoddi32:get-qop-id($source-context)"/></xsl:element>
            <xsl:element name="r:Version"><xsl:value-of select="enoddi32:get-version($source-context)"/></xsl:element><xsl:element name="r:ParameterName">
                <xsl:variable name="relatedVariable" select="enoddi32:get-related-variable($source-context)"/>
                <xsl:element name="r:String">
                    <xsl:attribute name="xml:lang" select="enoddi32:get-lang($source-context)"/>
                    <xsl:value-of select="enoddi32:get-name($relatedVariable)"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
           
    <!--this part is designed in this complicated way to maintain the order of the ddi 3.2 xsd schema-->
    <xsl:template match="driver-Binding//ResponseDomain" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:Binding>
            <r:SourceParameterReference>
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-rdop-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:TypeOfObject>OutParameter</r:TypeOfObject>
            </r:SourceParameterReference>
            <r:TargetParameterReference>
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-qop-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:TypeOfObject>OutParameter</r:TypeOfObject>
            </r:TargetParameterReference>
        </r:Binding>
    </xsl:template>
    
   
    <xsl:template match="driver-QuestionScheme//QuestionMultipleChoice//GridDimension" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>        
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>    
    </xsl:template>

    <xsl:template match="driver-QuestionScheme//*[name() = ('QuestionTable','QuestionDynamicTable')]//GridDimension" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>       
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
        <xsl:variable name="fake-dimension" select="enoddi32:get-fake-dimension($source-context)"/>
        <xsl:if test="$fake-dimension">
            <xsl:apply-templates select="$fake-dimension" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
  
   <!-- <xsl:template match="Sequence//Control | IfThenElse//Control" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:ControlConstructReference>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:TypeOfObject>ComputationItem</r:TypeOfObject>
        </d:ControlConstructReference>        
    </xsl:template>-->

    <xsl:template match="driver-CodeListReference//*" mode="model"/>
    

    <xsl:template match="driver-CodeListReference//CodeListReference | QuestionSingleChoice//ResponseDomain/CodeListReference" mode="model" priority="2">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:CodeListReference>	
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:TypeOfObject>CodeList</r:TypeOfObject>
        </r:CodeListReference>
    </xsl:template>

    <xsl:template match="TextDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:param name="mandatory" as="xs:string" tunnel="yes" select="''"/>
        <d:TextDomain maxLength="{enoddi32:get-max-length($source-context)}">
            <r:OutParameter isArray="false">
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-rdop-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:TextRepresentation maxLength="{enoddi32:get-max-length($source-context)}"/>
            </r:OutParameter>
            <xsl:if test="$mandatory = 'mandatory'">
				<r:ResponseCardinality minimumResponses="1" maximumResponses="1"/>
            </xsl:if>
        </d:TextDomain>
    </xsl:template>
    
    <xsl:template match="driver-VariableScheme//TextDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:TextRepresentation maxLength="{enoddi32:get-max-length($source-context)}"/>
    </xsl:template>

    <xsl:template match="NumericDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:param name="mandatory" as="xs:string" tunnel="yes" select="''"/>
        <d:NumericDomain>
            <xsl:variable name="decimalPositions" select="enoddi32:get-decimal-positions($source-context)"/>
            <xsl:if test="number($decimalPositions) = number($decimalPositions) and number($decimalPositions) &gt; 0">
                <xsl:attribute name="decimalPositions" select="$decimalPositions"/>
            </xsl:if>
            <r:NumberRange>
                <r:Low isInclusive="true">
                    <xsl:value-of select="enoddi32:get-low($source-context)"/>
                </r:Low>
                <r:High isInclusive="true">
                    <xsl:value-of select="enoddi32:get-high($source-context)"/>
                </r:High>
            </r:NumberRange>
            <r:NumericTypeCode codeListID="INSEE-CIS-NTC-CV">Decimal</r:NumericTypeCode>
            <r:OutParameter isArray="false">
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-rdop-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            </r:OutParameter>
            <xsl:if test="$mandatory = 'mandatory'">
				<r:ResponseCardinality minimumResponses="1" maximumResponses="1"/>
			</xsl:if>
        </d:NumericDomain>
    </xsl:template>
    
    <xsl:template match="driver-VariableScheme//NumericDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:variable name="driver" select="."/>
        <xsl:variable name="decimalPositions" select="enoddi32:get-decimal-positions($source-context)"/>
		<r:NumericRepresentation>
			<xsl:if test="number($decimalPositions) = number($decimalPositions) and number($decimalPositions) &gt; 0">
				<xsl:attribute name="decimalPositions" select="$decimalPositions"/>
			</xsl:if>
			<r:NumberRange>
                <r:Low isInclusive="true">
                    <xsl:value-of select="enoddi32:get-low($source-context)"/>
                </r:Low>
                <r:High isInclusive="true">
                    <xsl:value-of select="enoddi32:get-high($source-context)"/>
                </r:High>
            </r:NumberRange>
            <r:NumericTypeCode codeListID="INSEE-CIS-NTC-CV">Decimal</r:NumericTypeCode>
		</r:NumericRepresentation>
		<!-- MeasurementUnit -->
		<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="eno:append-empty-element('driver-VariableScheme', .)" tunnel="yes"/>
            <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="DateTimeDomain | DurationDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:param name="mandatory" as="xs:string" tunnel="yes" select="''"/>
        <!-- Id definition depend on date and duration format -->   
        <xsl:variable name="id-date-duration">
	    	<xsl:if test="$source-context != '' and $source-context != 'YYYY-MM-DD'">
	        	<xsl:value-of  select="concat('-',$source-context)"/>
	        </xsl:if>
        </xsl:variable>
        <d:DateTimeDomainReference>
            <r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID>INSEE-COMMUN-MNR-DateTimedate<xsl:value-of select="$id-date-duration"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:TypeOfObject>ManagedDateTimeRepresentation</r:TypeOfObject>
            <r:OutParameter isArray="false">
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-rdop-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:DateTimeRepresentationReference>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID>INSEE-COMMUN-MNR-DateTimedate<xsl:value-of select="$id-date-duration"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:TypeOfObject>ManagedDateTimeRepresentation</r:TypeOfObject>
                </r:DateTimeRepresentationReference>
            </r:OutParameter>
			<xsl:if test="$mandatory = 'mandatory'">
				<r:ResponseCardinality minimumResponses="1" maximumResponses="1"/>
			</xsl:if>
        </d:DateTimeDomainReference>        
    </xsl:template>
    
     <xsl:template match="driver-VariableScheme//DateTimeDomain | driver-VariableScheme//DurationDomain" mode="model">
    	<xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <!-- Id definition depend on date and duration format -->   
        <xsl:variable name="id-date-duration">
	    	<xsl:if test="$source-context != '' and $source-context != 'YYYY-MM-DD'">
	        	<xsl:value-of  select="concat('-',$source-context)"/>
	        </xsl:if>
        </xsl:variable>
		<r:DateTimeRepresentationReference>
			<r:Agency><xsl:value-of select="$agency"/></r:Agency>
            <r:ID>INSEE-COMMUN-MNR-DateTimedate<xsl:value-of select="$id-date-duration"/></r:ID>
            <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
            <r:TypeOfObject>ManagedDateTimeRepresentation</r:TypeOfObject>
		</r:DateTimeRepresentationReference>
    </xsl:template>

    <xsl:template match="BooleanDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:param name="mandatory" as="xs:string" tunnel="yes" select="''"/>
        <d:NominalDomain>
            <r:OutParameter isArray="false">
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-rdop-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:CodeRepresentation>
                    <r:CodeSubsetInformation>
                        <r:IncludedCode>
                            <r:CodeReference>
                                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                                <r:ID>INSEE-COMMUN-CL-Booleen-1</r:ID>
                                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                                <r:TypeOfObject>Code</r:TypeOfObject>
                            </r:CodeReference>
                        </r:IncludedCode>
                    </r:CodeSubsetInformation>
                </r:CodeRepresentation>
                <r:DefaultValue/>
            </r:OutParameter>
            <r:ResponseCardinality>
            	<xsl:if test="$mandatory = 'mandatory'">
            		<xsl:attribute name="minimumResponses">1</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="maximumResponses">1</xsl:attribute>
            </r:ResponseCardinality>
        </d:NominalDomain>
    </xsl:template>
    
	<xsl:template match="driver-VariableScheme//BooleanDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
		<r:CodeRepresentation>
        	<r:CodeSubsetInformation>
            	<r:IncludedCode>
	               	<r:CodeReference>
                       <r:Agency><xsl:value-of select="$agency"/></r:Agency>
	                   <r:ID>INSEE-COMMUN-CL-Booleen-1</r:ID>
	                   <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
	                   <r:TypeOfObject>Code</r:TypeOfObject>
	               	</r:CodeReference>
            	</r:IncludedCode>
        	</r:CodeSubsetInformation>
    	</r:CodeRepresentation>	
    </xsl:template>
    
    <xsl:template match="driver-SMGRD/*" mode="model" priority="2"/>
    
    <xsl:template match="driver-SMGRD/ResponseDomain/CodeDomain" mode="model" priority="3">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <xsl:param name="mandatory" as="xs:string" tunnel="yes" select="''"/>
        <d:CodeDomain>
            <r:GenericOutputFormat codeListID="INSEE-GOF-CV"><xsl:value-of select="enoddi32:get-generic-output-format($source-context)"/></r:GenericOutputFormat>            
            <r:CodeListReference>
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-code-list-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:TypeOfObject>CodeList</r:TypeOfObject>
            </r:CodeListReference>
            <r:OutParameter isArray="false">
                <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-rdop-id($source-context)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                <r:CodeRepresentation>
                    <r:CodeListReference>
                        <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                        <r:ID><xsl:value-of select="enoddi32:get-code-list-id($source-context)"/></r:ID>
                        <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                        <r:TypeOfObject>CodeList</r:TypeOfObject>
                    </r:CodeListReference>
                </r:CodeRepresentation>
            </r:OutParameter>
			<r:ResponseCardinality>
				<xsl:if test="$mandatory = 'mandatory'">
					<xsl:attribute name="minimumResponses">1</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="maximumResponses">1</xsl:attribute>
			</r:ResponseCardinality>
        </d:CodeDomain>
      </xsl:template>

    <xsl:template match="RosterDimension" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:GridDimension displayCode="false" displayLabel="false" rank="{enoddi32:get-rank($source-context)}">
            <d:Roster baseCodeValue="1" codeIterationValue="1">
                <xsl:attribute name="minimumRequired">
                    <xsl:value-of select="substring-before(enoddi32:get-dynamic($source-context), '-')"/>
                </xsl:attribute>
                <xsl:attribute name="maximumAllowed">
                    <xsl:value-of select="substring-after(enoddi32:get-dynamic($source-context), '-')"/>
                </xsl:attribute>
            </d:Roster>
       </d:GridDimension>
    </xsl:template>

    <xsl:template match="UnknownDimension" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
    </xsl:template>

    <xsl:template match="CodeDomainDimension" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>        
        <d:GridDimension displayCode="false" displayLabel="false" rank="{enoddi32:get-rank($source-context)}">
            <d:CodeDomain>
                <r:CodeListReference>
                    <r:Agency><xsl:value-of select="$agency"/></r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                    <r:Version><xsl:value-of select="enoddi32:get-version($source-context)"/></r:Version>
                    <r:TypeOfObject>CodeList</r:TypeOfObject>
                </r:CodeListReference>
            </d:CodeDomain>
        </d:GridDimension>
    </xsl:template>
    
    <xsl:template match="*" mode="enoddi32:question-reference">        
        <xsl:variable name="elementName" select="if(enoddi32:get-question-type(.) = ('MULTIPLE_CHOICE','TABLE','DYNAMIC_TABLE')) then('QuestionGrid') else('QuestionItem')"/>
            <r:QuestionReference>
                <r:Agency><xsl:value-of select="enoddi32:get-agency(.)"/></r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-question-id(.)"/></r:ID>
                <r:Version><xsl:value-of select="enoddi32:get-version(.)"/></r:Version>
                <r:TypeOfObject><xsl:value-of select="$elementName"/></r:TypeOfObject>
            </r:QuestionReference>        
    </xsl:template>

</xsl:stylesheet>
