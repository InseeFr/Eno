<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:boom="boom" exclude-result-prefixes="#all">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="node()">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <!-- Deletion of any comments entered in the core of the form. -->
    <xsl:template match="comment()[following-sibling::xf:submission]" priority="2"/>

    <!--***********************************************Personal instance, adherence in eXist services.***********************************************-->

    <!-- Replacement of the util part of the perso instance and the associated binds -->
    <xsl:template match="Util[ancestor::xf:instance[@id='fr-form-instance']]">
        <stromae>
            <util>
                <sectionCourante>1</sectionCourante>
                <nomSectionCourante/>
                <xsl:apply-templates select="CurrentLoopElement"/>
                <expedie>non</expedie>
                <extrait>non</extrait>
                <dateHeure/>
            </util>
            <xsl:copy-of select="../perso"/>
        </stromae>
    </xsl:template>

    <xsl:template match="perso[ancestor::xf:instance[@id='fr-form-instance']]"/>

    <xsl:template match="xf:bind[@id='current-section-name-bind']">
        <xf:bind id="nomSectionCourante-bind" name="nomSectionCourante"
            ref="stromae/util/nomSectionCourante">
            <xsl:attribute name="calculate"
                select="replace(replace(@calculate,'CurrentSection','sectionCourante'),'/Util/','/stromae/util/')"
            />
        </xf:bind>
    </xsl:template>

    <!-- The priority is set to -1 while waiting to delete the string replacement template below, to simply replace the strings of the custom edit function -->
    <xsl:template
        match="@*[contains(.,('/Util/'))
        or contains(.,('CurrentSectionName'))
        or contains(.,('CurrentSection'))
        or contains(.,('Send'))
        or contains(.,('DateTime'))]"
        priority="-1">
        <xsl:attribute name="{name()}" select="boom:modifications-perso(.)"/>
    </xsl:template>

    <!-- String function for replacing elements contained in stroma/util -->
    <xsl:function name="boom:modifications-perso">
        <xsl:param name="input"/>
        <xsl:value-of
            select="replace(replace(replace(replace(replace($input,
            '/Util/','/stromae/util/'),
            'CurrentSectionName','nomSectionCourante'),
            'CurrentSection','sectionCourante'),
            
            'Send','expedie'),
            'DateTime','dateHeure')"
        />
        <!--'Send=''false''','expedie=''non'''),-->
    </xsl:function>

    <!-- Special templates, these attributes must not be modified, they correspond to the Send element of the user instance and not to the Send element of the customization instance. -->
    <xsl:template match="@ref[parent::xf:bind[@id='send-bind']]" priority="2">
        <xsl:copy/>
    </xsl:template>
    <xsl:template match="@ref[parent::xf:label/parent::xf:trigger[@bind='send-bind']]" priority="2">
        <xsl:copy/>
    </xsl:template>    
    

    <!-- For some elements, we must also replace the false string in non (value linked to the element shipped) but we must not systematically make this replacement,
    the false chain being often used in addition -->
    <xsl:template match="@*" mode="special_translation">
        <xsl:attribute name="{name()}">
            <xsl:variable name="modifie">
                <xsl:value-of select="replace(.,'false','non')"/>
            </xsl:variable>
            <xsl:value-of select="boom:modifications($modifie)"/>
        </xsl:attribute>
    </xsl:template>

    <!-- And the following entries are changed from false to no -->
    <xsl:template match="@relevant[parent::xf:bind[@id='send-bind' or @id='validation-bind' or @id='confirmation-bind']]" priority="2">
        <xsl:apply-templates select="." mode="special_translation"/>
    </xsl:template>
    <xsl:template
        match="@value[parent::xf:setvalue[parent::xf:action[@ev:event='xforms-submit-error']]]"
        priority="2">
        <xsl:apply-templates select="." mode="special_translation"/>
    </xsl:template>
    <xsl:template match="@if[parent::xxf:show[parent::xf:action[@ev:event='xforms-ready']]]"
        priority="2">
        <xsl:apply-templates select="." mode="special_translation"/>
    </xsl:template>

    <!--***********************************************Personal instance, adherence in eXist services***********************************************-->



    <!--*****************************Perso instance, adherence in already existing perso instances and in an Orbeon css*****************************-->

    <!-- Change '-Header-' to '-entete-'. Used in a css on the Orbeon side. -->
    <xsl:template
        match="*[ancestor::xf:instance[@id='fr-form-instance' or @id='fr-form-loop-model'] and contains(name(),'-Header-')]">
        <xsl:element name="{replace(name(),'\-Header\-','-entete-')}">
            <xsl:apply-templates select="node() | @*"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="xf:bind[contains(@id,'-Header-')]">
        <xsl:element name="xf:bind">
            <xsl:attribute name="id" select="replace(@id,'\-Header\-','-entete-')"/>
            <xsl:attribute name="name" select="replace(@name,'\-Header\-','-entete-')"/>
            <xsl:attribute name="ref" select="replace(@ref,'\-Header\-','-entete-')"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="@*[contains(.,'-Header-')]">
        <xsl:attribute name="{name()}" select="replace(.,'\-Header\-','-entete-')"/>
    </xsl:template>
    <xsl:template
        match="*[ancestor::xf:instance[@id='fr-form-resources'] and contains(name(),'-Header-')]">
        <xsl:element name="{replace(name(),'\-Header\-','-entete-')}">
            <xsl:apply-templates select="node() | @*"/>
        </xsl:element>
    </xsl:template>


 <!--   <xsl:template match="@origin[contains(.,'fr-form-loop-model')]" priority="2">
        <xsl:attribute name="origin"
            select="replace(.,'fr-form-loop-model','fr-form-modeles')"
        />
    </xsl:template>-->
    
    <xsl:template match="Validation[ancestor::xf:instance[@id='fr-form-instance' or @id='fr-form-util']]">
        <VALIDATION/>
    </xsl:template>
    <xsl:template match="@*[contains(.,'validation')]">
        <xsl:attribute name="{name()}" select="replace(.,'validation','VALIDATION')"/>
    </xsl:template>
    <xsl:template match="@*[contains(.,'Validation')]">
        <xsl:attribute name="{name()}" select="replace(.,'Validation','VALIDATION')"/>
    </xsl:template>
    <xsl:template match="Validation[ancestor::xf:instance[@id='fr-form-resources']]">
        <VALIDATION>
            <label>VALIDATION</label>
        </VALIDATION>
    </xsl:template>
    
    
    <xsl:template match="Confirmation[ancestor::xf:instance[@id='fr-form-instance' or @id='fr-form-util']]">
        <CONFIRMATION/>
    </xsl:template>
    <xsl:template match="@*[contains(.,'confirmation') and not(contains(.,'confirmationOui')) and not(contains(.,'confirmationNon'))]">
        <xsl:attribute name="{name()}" select="replace(.,'confirmation','CONFIRMATION')"/>
    </xsl:template>
    <xsl:template match="@*[contains(.,'Confirmation')]">
        <xsl:attribute name="{name()}" select="replace(.,'Confirmation','CONFIRMATION')"/>
    </xsl:template>
    <xsl:template match="Confirmation[ancestor::xf:instance[@id='fr-form-resources']]">
        <CONFIRMATION>
            <label>CONFIRMATION</label>
        </CONFIRMATION>
    </xsl:template>
    
    
    <xsl:template match="End[ancestor::xf:instance[@id='fr-form-instance' or @id='fr-form-util']]">
        <FIN/>
    </xsl:template>
    <!-- It is necessary to be precise in the modified labels to avoid creating "descFINant" for example -->
    <xsl:template match="@name[.='end']">
        <xsl:attribute name="name">FIN</xsl:attribute>
    </xsl:template>
    <xsl:template match="@ref[.='End']">
        <xsl:attribute name="ref">FIN</xsl:attribute>
    </xsl:template>
    <xsl:template match="@*[contains(.,'end-') and not(contains(.,'send-'))]">
        <xsl:attribute name="{name()}" select="replace(.,'end-','FIN-')"/>
    </xsl:template>
    <xsl:template match="@*[contains(.,'/End/')]">
        <xsl:attribute name="{name()}" select="replace(.,'/End/','/FIN/')"/>
    </xsl:template>
    
    <xsl:template match="End[ancestor::xf:instance[@id='fr-form-resources']]">
        <FIN>
            <label>FIN</label>
        </FIN>
    </xsl:template>
    
    
    <!--***********************************************************No adherences, replaceable***********************************************************-->
    <!-- An action that can be reintegrated and called up when sending -->
    <xsl:template match="xf:action[@ev:event='submit-form']"/>

    <!--<xsl:template match="xf:instance[@id='fr-form-loop-model']">
        <xf:instance id="fr-form-modeles">
            <xsl:apply-templates select="*"/>
        </xf:instance>
    </xsl:template>

    <xsl:template match="LoopModels[parent::xf:instance[@id='fr-form-loop-model']]">
        <modeles>
            <xsl:apply-templates select="*"/>
        </modeles>
    </xsl:template>-->
    
    
    <!--****************************************************************Adherences in css or xslt in Orbeon***************************************************-->

    <!-- For all of the attributes, certain string modifications are carried out -->
    <xsl:template match="@*">
        <xsl:attribute name="{name()}" select="boom:modifications(.)"/>
        <!--<xsl:attribute name="{name()}" select="."/>-->
    </xsl:template>

    <!-- Here is a list of strings to replace in attributes (adherence in Orbeon) -->
    <xsl:function name="boom:modifications">
        <xsl:param name="input"/>
        <xsl:variable name="modifie">
            <xsl:value-of
                select="replace(replace(replace(replace(replace(replace(
                $input,'PageChangeDone','changementPageEffectue')
                ,'page-change-done','ChangementPageEffectif')
                ,'page-change','ChangementPage')
                ,'section-body','corpsSection')
                ,'save','enregistrer')
                ,'^submit$','expedier')"
            />
        </xsl:variable>
        <!-- In the end, more chains are replaced... -->
        <xsl:value-of select="boom:modifications-perso($modifie)"/>
    </xsl:function>

    <!-- This element must be renamed, there is an adherence in readonly.xsl -->
    <xsl:template match="PageChangeDone[ancestor::xf:instance[@id='fr-form-util']]">
        <changementPageEffectue/>
    </xsl:template>

    <!-- WARNING: if we remove these differences and we change what is done on the OpenSource side (we have to modify fin.xsl), the generated HTML is not exactly the same for the corresponding text. -->
    <!-- So it is not only a question of translating the css class on the Orbeon side, it is also necessary to modify the selector in the margin so that it applies to the new and old questionnaires. -->
    <xsl:template match="@*[contains(.,'confirmation-message')]" priority="2">
        <xsl:attribute name="{name()}"
            select="replace(.,'confirmation\-message','messageConfirmation')"/>
    </xsl:template>
    <xsl:template match="ConfirmationMessage[ancestor::xf:instance[@id='fr-form-util']]">
        <messageConfirmation/>
    </xsl:template>
    <xsl:template match="xf:bind[@id='confirmation-message-bind']">
        <xf:bind id="messageConfirmation-bind" ref="messageConfirmation" name="messageConfirmation"
            calculate="concat('Votre questionnaire a bien été expédié le ',instance('fr-form-instance')/stromae/util/dateHeure)"
        />
    </xsl:template>

</xsl:transform>
