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
        <DDIInstance xmlns="ddi:instance:3_2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:g="ddi:group:3_2" xmlns:d="ddi:datacollection:3_2" xmlns:s="ddi:studyunit:3_2"
            xmlns:r="ddi:reusable:3_2" xmlns:xhtml="http://www.w3.org/1999/xhtml"
            xmlns:a="ddi:archive:3_2" xmlns:l="ddi:logicalproduct:3_2"
            xmlns:xs="http://www.w3.org/2001/XMLSchema"
            xsi:schemaLocation="ddi:instance:3_2 ../../../schema/instance.xsd" isMaintainable="true">
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID>
                <xsl:value-of select="concat('INSEE-', enoddi32:get-id($source-context))"/>
            </r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:Citation>
                <r:Title>
                    <r:String>
                        <xsl:value-of select="$citation"/>
                    </r:String>
                </r:Title>
            </r:Citation>
            <g:ResourcePackage isMaintainable="true" versionDate="${current-date()}">
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <d:InterviewerInstructionScheme>
                    <r:Agency>
                        <xsl:value-of select="$agency"/>
                    </r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                    <r:Version>
                        <xsl:value-of select="enoddi32:get-version($source-context)"/>
                    </r:Version>
                    <r:Label>
                        <r:Content xml:lang="{enoddi32:get-lang($source-context)}">A définir</r:Content>
                    </r:Label>
                    <xsl:apply-templates select="enoddi32:get-instructions($source-context)" mode="source">
                        <xsl:with-param name="driver"
                            select="eno:append-empty-element('driver-InterviewerInstructionScheme', .)"
                            tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                </d:InterviewerInstructionScheme>

                <d:ControlConstructScheme>
                    <r:Agency>
                        <xsl:value-of select="$agency"/>
                    </r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                    <r:Version>
                        <xsl:value-of select="enoddi32:get-version($source-context)"/>
                    </r:Version>
                    <d:Sequence>		
                        <r:Agency>
                            <xsl:value-of select="$agency"/>
                        </r:Agency>	
                        <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                        <r:Version>
                            <xsl:value-of select="enoddi32:get-version($source-context)"/>
                        </r:Version>	
                        <r:Label>	
                            <r:Content xml:lang="fr-FR"><xsl:value-of select="enoddi32:get-label($source-context)"/></r:Content>
                        </r:Label>	
                        <d:TypeOfSequence>template</d:TypeOfSequence>	
                        <!--creation of references of direct children-->
                        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                            <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                        </xsl:apply-templates>
                    </d:Sequence>		
                    <!--creation of control construct from children (everything since we are at the root node), whose reference were created sooner-->
                    <xsl:apply-templates select="enoddi32:get-sequences($source-context)" mode="source">
                        <xsl:with-param name="driver"
                            select="eno:append-empty-element('driver-ControlConstructScheme', .)"
                            tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="enoddi32:get-questions($source-context)" mode="source">
                        <xsl:with-param name="driver"
                            select="eno:append-empty-element('driver-ControlConstructScheme', .)"
                            tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                </d:ControlConstructScheme>
                
                <d:QuestionScheme>
                    <r:Agency>
                        <xsl:value-of select="$agency"/>
                    </r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                    <r:Version>
                        <xsl:value-of select="enoddi32:get-version($source-context)"/>
                    </r:Version>
                    <r:Label>
                        <r:Content xml:lang="{enoddi32:get-lang($source-context)}">A définir</r:Content>
                    </r:Label>
                    <xsl:apply-templates select="enoddi32:get-questions($source-context)" mode="source">
                        <xsl:with-param name="driver"
                            select="eno:append-empty-element('driver-QuestionScheme', .)"
                            tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                </d:QuestionScheme>
                <l:CategoryScheme>
                    <r:Agency>
                        <xsl:value-of select="enoddi32:get-agency($source-context)"/>
                    </r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                    <r:Version>
                        <xsl:value-of select="enoddi32:get-version($source-context)"/>
                    </r:Version>
                    <r:Label>
                        <r:Content xml:lang="{enoddi32:get-lang($source-context)}">A définir</r:Content>
                    </r:Label>
                    <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                        <xsl:with-param name="driver"
                            select="eno:append-empty-element('driver-CategoryScheme', .)"
                            tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                </l:CategoryScheme>
                <l:CodeListScheme>
                    <r:Agency>
                        <xsl:value-of select="$agency"/>
                    </r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                    <r:Version>
                        <xsl:value-of select="enoddi32:get-version($source-context)"/>
                    </r:Version>
                    <r:Label>
                        <r:Content xml:lang="{enoddi32:get-lang($source-context)}">Codelists for the survey</r:Content>
                    </r:Label>
                    <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                        <xsl:with-param name="driver"
                            select="eno:append-empty-element('driver-CodeListScheme', .)"
                            tunnel="yes"/>
                        <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                </l:CodeListScheme>
                <l:VariableScheme>
                    <r:Agency>
                        <xsl:value-of select="$agency"/>
                    </r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                    <r:Version>
                        <xsl:value-of select="enoddi32:get-version($source-context)"/>
                    </r:Version>
                    <r:Label>
                        <r:Content xml:lang="{enoddi32:get-lang($source-context)}">Variable Scheme for the survey</r:Content>
                    </r:Label>
                </l:VariableScheme>
            </g:ResourcePackage>
            <s:StudyUnit xmlns="ddi:studyunit:3_2">
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:ExPostEvaluation/>
                <d:DataCollection>
                    <r:Agency>
                        <xsl:value-of select="$agency"/>
                    </r:Agency>
                    <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                    <r:Version>
                        <xsl:value-of select="enoddi32:get-version($source-context)"/>
                    </r:Version>
                    <r:QuestionSchemeReference>
                        <r:Agency>
                            <xsl:value-of select="$agency"/>
                        </r:Agency>
                        <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                        <r:Version>
                            <xsl:value-of select="enoddi32:get-version($source-context)"/>
                        </r:Version>
                        <r:TypeOfObject>QuestionScheme</r:TypeOfObject>
                    </r:QuestionSchemeReference>
                    <r:ControlConstructSchemeReference>
                        <r:Agency>
                            <xsl:value-of select="$agency"/>
                        </r:Agency>
                        <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                        <r:Version>
                            <xsl:value-of select="enoddi32:get-version($source-context)"/>
                        </r:Version>
                        <r:TypeOfObject>ControlConstructScheme</r:TypeOfObject>
                    </r:ControlConstructSchemeReference>
                    <r:InterviewerInstructionSchemeReference>
                        <r:Agency>
                            <xsl:value-of select="$agency"/>
                        </r:Agency>
                        <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                        <r:Version>
                            <xsl:value-of select="enoddi32:get-version($source-context)"/>
                        </r:Version>
                        <r:TypeOfObject>InterviewerInstructionScheme</r:TypeOfObject>
                    </r:InterviewerInstructionSchemeReference>
                    <d:InstrumentScheme xml:lang="{enoddi32:get-lang($source-context)}">
                        <r:Agency>
                            <xsl:value-of select="$agency"/>
                        </r:Agency>
                        <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                        <r:Version>
                            <xsl:value-of select="enoddi32:get-version($source-context)"/>
                        </r:Version>
                        <d:Instrument xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
                            xmlns:pr="ddi:ddiprofile:3_2" xmlns:c="ddi:conceptualcomponent:3_2"
                            xmlns:cm="ddi:comparative:3_2">
                            <r:Agency>
                                <xsl:value-of select="$agency"/>
                            </r:Agency>
                            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                            <r:Version>
                                <xsl:value-of select="enoddi32:get-version($source-context)"/>
                            </r:Version>
                            <r:Label>
                                <r:Content xml:lang="{enoddi32:get-lang($source-context)}"><xsl:value-of select="enoddi32:get-label($source-context)"/> questionnaire</r:Content>
                            </r:Label>
                            <d:TypeOfInstrument>A définir</d:TypeOfInstrument>
                            <d:ControlConstructReference>
                                <r:Agency>
                                    <xsl:value-of select="$agency"/>
                                </r:Agency>
                                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                                <r:Version>
                                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                                </r:Version>
                                <r:TypeOfObject>Sequence</r:TypeOfObject>
                            </d:ControlConstructReference>
                        </d:Instrument>
                    </d:InstrumentScheme>
                </d:DataCollection>
            </s:StudyUnit>
        </DDIInstance>
    </xsl:template>


<!--    <xsl:template match="driver-InterviewerInstructionScheme//* | driver-CodeListScheme//* | driver-CategoryScheme//*" mode="model" priority="3">        
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>        
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
            <xsl:with-param name="agency" select="$agency" as="xs:string" tunnel="yes"/>
        </xsl:apply-templates>        
    </xsl:template>-->

    <xsl:template match="driver-InterviewerInstructionScheme//Instruction" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:Instruction>
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <d:InstructionName>
                <r:String xml:lang="{enoddi32:get-lang($source-context)}">
                    <xsl:value-of select="enoddi32:get-name($source-context)"/>
                </r:String>
            </d:InstructionName>
            <d:InstructionText>
                <d:LiteralText>
                    <d:Text xml:lang="{enoddi32:get-lang($source-context)}">
                        <xsl:value-of select="enoddi32:get-text($source-context)"/>
                    </d:Text>
                </d:LiteralText>
            </d:InstructionText>
        </d:Instruction>
    </xsl:template>
    
    <!--creation de la reference de l'InterviwerInstruction-->
    <xsl:template match="Instruction" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:InterviewerInstructionReference>	
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:TypeOfObject>Instruction</r:TypeOfObject>
        </d:InterviewerInstructionReference>	
    </xsl:template>
    
    <xsl:template match="driver-CodeListScheme//CodeList" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <l:CodeList>
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"></xsl:value-of></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:Label>
                <r:Content xml:lang="{enoddi32:get-lang($source-context)}">
                    <xsl:value-of select="enoddi32:get-label($source-context)"/>
                </r:Content>
            </r:Label>
            <l:HierarchyType>A définir</l:HierarchyType>
            <l:Level levelNumber="TODO">
                <l:CategoryRelationship>A définir</l:CategoryRelationship>
            </l:Level>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </l:CodeList>
    </xsl:template>

    <xsl:template match="driver-CodeListScheme//Code" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <l:Code levelNumber="TODO" isDiscrete="{enoddi32:is-discrete($source-context)}">
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:CategoryReference>
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:TypeOfObject>Category</r:TypeOfObject>
            </r:CategoryReference>
            <r:Value>
                <xsl:value-of select="enoddi32:get-value($source-context)"/>
            </r:Value>
        </l:Code>
    </xsl:template>
    
    <xsl:template match="Code" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <l:Code levelNumber="1" isDiscrete="true">		
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>	
            <r:CategoryReference>	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
            </r:CategoryReference>	
            <r:Value>Code.Value</r:Value>	
        </l:Code>		
    </xsl:template>
   
    <xsl:template match="driver-CategoryScheme//CodeList" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>        
    </xsl:template>

    <xsl:template match="driver-CategoryScheme//Code" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <l:Category>
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:Label>
                <r:Content xml:lang="{enoddi32:get-lang($source-context)}">
                    <xsl:value-of select="enoddi32:get-label($source-context)"/>
                </r:Content>
            </r:Label>
        </l:Category>
    </xsl:template>
    
    <xsl:template match="driver-ControlConstructScheme//Sequence" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:Sequence>		
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>	
            <r:Label>	
                <r:Content xml:lang="fr-FR"><xsl:value-of select="enoddi32:get-label($source-context)"/></r:Content>
            </r:Label>	
            <d:TypeOfSequence><xsl:value-of select="enoddi32:get-sequence-type($source-context)"/></d:TypeOfSequence>	
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>	
        </d:Sequence>		
    </xsl:template>

    <xsl:template match="Sequence//Sequence" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:ControlConstructReference>	
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:TypeOfObject>Sequence</r:TypeOfObject>
        </d:ControlConstructReference>	
    </xsl:template>
    
   <!-- <!-\-ne sert peut etre a rien-\->
    <xsl:template match="Sequence" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:ControlConstructReference>	
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:TypeOfObject>Sequence</r:TypeOfObject>
        </d:ControlConstructReference>	
    </xsl:template>-->
    
    <xsl:template match="driver-ControlConstructScheme//QuestionSimple" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:QuestionConstruct>		
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>	
            <r:QuestionReference>	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:TypeOfObject>QuestionItem</r:TypeOfObject>
            </r:QuestionReference>	
        </d:QuestionConstruct>	
    </xsl:template>
    
    <xsl:template match="driver-QuestionScheme//QuestionSimple" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:QuestionItem>			
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>		
            <d:QuestionText>		
                <d:LiteralText>	
                    <d:Text xml:lang="fr-FR"><xsl:value-of select="enoddi32:get-label($source-context)"/></d:Text>
                </d:LiteralText>	
            </d:QuestionText>		
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>			
        </d:QuestionItem>		
    </xsl:template>
    
    <xsl:template match="Sequence//QuestionSimple" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:ControlConstructReference>	
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:TypeOfObject>QuestionConstruct</r:TypeOfObject>
        </d:ControlConstructReference>	
    </xsl:template>
    
    <xsl:template match="QuestionSimple//ResponseDomain" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:OutParameter isArray="false">		
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:ParameterName>	
                <r:String xml:lang="fr-FR">A définir</r:String>
            </r:ParameterName>	
        </r:OutParameter>		
        <r:Binding>		
            <r:SourceParameterReference>	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:TypeOfObject>OutParameter</r:TypeOfObject>
            </r:SourceParameterReference>	
            <r:TargetParameterReference>	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:TypeOfObject>OutParameter</r:TypeOfObject>
            </r:TargetParameterReference>	
        </r:Binding>		
        <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    
    <xsl:template match="driver-QuestionScheme//QuestionSingleChoice" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:QuestionItem>			
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <d:QuestionText>		
                <d:LiteralText>	
                    <d:Text xml:lang="fr-FR"><xsl:value-of select="enoddi32:get-label($source-context)"/></d:Text>
                </d:LiteralText>	
            </d:QuestionText>
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
        </d:QuestionItem>
    </xsl:template>
    
    <xsl:template match="driver-ControlConstructScheme//QuestionSingleChoice" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:QuestionConstruct>		
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>	
            <r:QuestionReference>	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:TypeOfObject>QuestionItem</r:TypeOfObject>
            </r:QuestionReference>	
        </d:QuestionConstruct>
    </xsl:template>
    
    
    <xsl:template match="Sequence//QuestionSingleChoice" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:ControlConstructReference>	
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:TypeOfObject>QuestionConstruct</r:TypeOfObject>
        </d:ControlConstructReference>	
    </xsl:template>
    
    <xsl:template match="QuestionSingleChoice//ResponseDomain" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:OutParameter isArray="false">		
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:ParameterName>	
                <r:String xml:lang="fr-FR">A définir</r:String>
            </r:ParameterName>	
        </r:OutParameter>		
        <r:Binding>		
            <r:SourceParameterReference>	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:TypeOfObject>OutParameter</r:TypeOfObject>
            </r:SourceParameterReference>	
            <r:TargetParameterReference>	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:TypeOfObject>OutParameter</r:TypeOfObject>
            </r:TargetParameterReference>	
        </r:Binding>		
        <d:CodeDomain>		
            <r:OutParameter isArray="false">	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:CodeRepresentation>
                    <xsl:call-template name="CodeRepresentation_CodeListReference"/>
                </r:CodeRepresentation>
            </r:OutParameter>	
            <r:ResponseCardinality maximumResponses="1"/>	
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>		
        </d:CodeDomain>		
    </xsl:template>
    
    <xsl:template name="CodeRepresentation_CodeListReference">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:CodeListReference>	
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:TypeOfObject>CodeList</r:TypeOfObject>
        </r:CodeListReference>
    </xsl:template>
    
    <xsl:template match="driver-QuestionScheme//QuestionMultipleChoice" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:QuestionGrid>					
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>				
            <d:QuestionText>		
                <d:LiteralText>	
                    <d:Text xml:lang="fr-FR"><xsl:value-of select="enoddi32:get-label($source-context)"/></d:Text>
                </d:LiteralText>	
            </d:QuestionText>				
            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                <xsl:with-param name="driver" select="." tunnel="yes"/>
            </xsl:apply-templates>
            <d:StructuredMixedGridResponseDomain>
                <xsl:for-each select="eno:child-fields($source-context)[local-name()='Response']">
                    <GridResponseDomain> 			
                        <d:CodeDomain>		
                            <r:OutParameter isArray="false">	
                                <r:Agency>
                                    <xsl:value-of select="$agency"/>
                                </r:Agency>
                                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                                <r:Version>
                                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                                </r:Version>	
                                <r:CodeRepresentation>
                                    <xsl:call-template name="CodeRepresentation_CodeListReference"/>
                                </r:CodeRepresentation>
                            </r:OutParameter>	
                            <r:ResponseCardinality maximumResponses="1"/>	
                            <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
                                <xsl:with-param name="driver" select="." tunnel="yes"/>
                            </xsl:apply-templates>
                        </d:CodeDomain>
                    </GridResponseDomain>
                </xsl:for-each>
            </d:StructuredMixedGridResponseDomain>	
        </d:QuestionGrid>					
    </xsl:template>
    
    <xsl:template match="driver-ControlConstructScheme//QuestionMultipleChoice" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:QuestionConstruct>		
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>	
            <r:QuestionReference>	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:TypeOfObject>QuestionGrid</r:TypeOfObject>
            </r:QuestionReference>	
        </d:QuestionConstruct>
    </xsl:template>
    
    
    <xsl:template match="Sequence//QuestionMultipleChoice" mode="model" priority="1">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:ControlConstructReference>	
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:TypeOfObject>QuestionConstruct</r:TypeOfObject>
        </d:ControlConstructReference>	
    </xsl:template>
    
    
    
    
    
    <!--    <xsl:template match="CodeListReference" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:CodeListReference>	
            <r:Agency>
                <xsl:value-of select="$agency"/>
            </r:Agency>
            <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
            <r:Version>
                <xsl:value-of select="enoddi32:get-version($source-context)"/>
            </r:Version>
            <r:TypeOfObject>CodeList</r:TypeOfObject>
        </r:CodeListReference>
    </xsl:template>-->

    <xsl:template match="Goto" mode="model"/>
    <xsl:template match="DataCollection" mode="model"/>
    <xsl:template match="ComponentGroup" mode="model"/>
    <xsl:template match="MemberReference" mode="model"/>
    
    <xsl:template match="TextDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:TextDomain maxLength="{enoddi32:get-max-length($source-context)}">            
            <r:OutParameter isArray="false">	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:TextRepresentation maxLength="{enoddi32:get-max-length($source-context)}"/>
            </r:OutParameter>	
        </d:TextDomain>		
    </xsl:template>
    
    <xsl:template match="NumericDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:NumericDomain>
            <xsl:variable name="decimalPositions" select="enoddi32:get-decimal-positions($source-context)"/>
            <xsl:if test="number($decimalPositions)=number($decimalPositions)">
                <xsl:attribute name="decimalPositions" select="$decimalPositions"/>
            </xsl:if>
            <r:NumberRange>	
                <r:Low isInclusive="true"><xsl:value-of select="enoddi32:get-low($source-context)"/></r:Low>
                <r:High isInclusive="true"><xsl:value-of select="enoddi32:get-high($source-context)"/></r:High>
            </r:NumberRange>	
            <r:NumericTypeCode codeListID="INSEE-CIS-NTC-CV">Decimal</r:NumericTypeCode>	
            <r:OutParameter isArray="false">	
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
            </r:OutParameter>	
        </d:NumericDomain>		
    </xsl:template>
    
    <xsl:template match="DateTimeDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:DateTimeDomain>			
            <r:DateFieldFormat>jj/mm/aaaa</r:DateFieldFormat>		
            <r:DateTypeCode codeListID="INSEE-DTC-CV">date</r:DateTypeCode>		
            <r:OutParameter isArray="false">		
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:DateTimeRepresentation>	
                    <r:DateFieldFormat>jj/mm/aaaa</r:DateFieldFormat>
                    <r:DateTypeCode codeListID="INSEE-DTC-CV">date</r:DateTypeCode>
                </r:DateTimeRepresentation>	
            </r:OutParameter>		
        </d:DateTimeDomain>			        
    </xsl:template>
    
    <xsl:template match="BooleanDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:NominalDomain>						
            <r:GenericOutputFormat codeListID="INSEE-GOF-CV">checkbox</r:GenericOutputFormat>					
            <r:OutParameter isArray="false">					
                <r:Agency>
                    <xsl:value-of select="$agency"/>
                </r:Agency>
                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                <r:Version>
                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                </r:Version>
                <r:CodeRepresentation>				
                    <r:CodeSubsetInformation>			
                        <r:IncludedCode>		
                            <r:CodeReference>	
                                <r:Agency>
                                    <xsl:value-of select="$agency"/>
                                </r:Agency>
                                <r:ID><xsl:value-of select="enoddi32:get-id($source-context)"/></r:ID>
                                <r:Version>
                                    <xsl:value-of select="enoddi32:get-version($source-context)"/>
                                </r:Version>
                                <r:TypeOfObject>Code</r:TypeOfObject>
                            </r:CodeReference>	
                        </r:IncludedCode>		
                    </r:CodeSubsetInformation>			
                </r:CodeRepresentation>				
                <r:DefaultValue/>				
            </r:OutParameter>					
            <r:ResponseCardinality maximumResponses="1"/>					
        </d:NominalDomain>						
        
    </xsl:template>
    
    <xsl:template match="RadioDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:GenericOutputFormat codeListID="INSEE'-GOF-CV'">radio-button</r:GenericOutputFormat>
    </xsl:template>
    
    <xsl:template match="CheckBoxDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:GenericOutputFormat codeListID="INSEE'-GOF-CV'">checkbox</r:GenericOutputFormat>
    </xsl:template>
    
    <xsl:template match="DropDownListDomain" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <r:GenericOutputFormat codeListID="INSEE'-GOF-CV'">drop-down-list</r:GenericOutputFormat>
    </xsl:template>
    
    
    
    
    
    <xsl:template match="RosterDimension" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:Roster baseCodeValue="1" codeIterationValue="1">
            <xsl:attribute name="minimumRequired">@dynamic.min</xsl:attribute>
            <xsl:attribute name="maximumAllowed">@dynamic.max</xsl:attribute>
        </d:Roster>
    </xsl:template>
    
    <xsl:template match="UnknownDimension" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        
    </xsl:template>
    
    <xsl:template match=" CodeDomainDimension" mode="model">
        <xsl:param name="source-context" as="item()" tunnel="yes"/>
        <xsl:param name="agency" as="xs:string" tunnel="yes"/>
        <d:CodeDomain>
            <xsl:copy-of select="./*"></xsl:copy-of>
        </d:CodeDomain>
    </xsl:template>

</xsl:stylesheet>
