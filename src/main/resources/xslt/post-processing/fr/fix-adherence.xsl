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

    <!-- On dégage les commentaires éventuellement inscrits dans le core du formulaire -->
    <xsl:template match="comment()[following-sibling::xf:submission]" priority="2"/>

    <!--***********************************************Instance de perso, adhérences dans les services eXist***********************************************-->

    <!-- On remplace la partie util de l'instance de perso et les binds associés -->
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

    <!-- La priorité est à -1 en attendant de supprimer le template de remplacement de chaîne plus bas, pour se contenter de remplacer les chaînes de la fonction modifications-perso -->
    <xsl:template
        match="@*[contains(.,('/Util/'))
        or contains(.,('CurrentSectionName'))
        or contains(.,('CurrentSection'))
        or contains(.,('Send'))
        or contains(.,('DateTime'))]"
        priority="-1">
        <xsl:attribute name="{name()}" select="boom:modifications-perso(.)"/>
    </xsl:template>

    <!-- Fonction de chaîne de remplacement des éléments contenus dans stromae/util -->
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

    <!-- Templates spéciaux, ces attributs ne doivent pas être modifiés, ils correspondent à l'élément Send de l'instance util et non à l'élément Send de l'instance de personnalisation -->
    <xsl:template match="@ref[parent::xf:bind[@id='send-bind']]" priority="2">
        <xsl:copy/>
    </xsl:template>
    <xsl:template match="@ref[parent::xf:label/parent::xf:trigger[@bind='send-bind']]" priority="2">
        <xsl:copy/>
    </xsl:template>    
    

    <!-- Pour certains éléments, on doit aussi remplacer la chaîne false en non (valeur liée l'élément expedie) mais on ne doit pas faire systématiquement ce remplacement,
    la chaîne false étant souvent utilisée par ailleurs -->
    <xsl:template match="@*" mode="special_translation">
        <xsl:attribute name="{name()}">
            <xsl:variable name="modifie">
                <xsl:value-of select="replace(.,'false','non')"/>
            </xsl:variable>
            <xsl:value-of select="boom:modifications($modifie)"/>
        </xsl:attribute>
    </xsl:template>

    <!-- Et on modifie les mentions suivantes de false à non -->
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

    <!--***********************************************Instance de perso, adhérences dans les services eXist***********************************************-->



    <!--*****************************Instance de perso, adhérence dans les instances perso déjà en base et dans une css Orbeon*****************************-->

    <!-- On remplace les '-Header-' en '-entete-'. Utilisé dans une css côté Orbeon. -->
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
    <!-- Il faut être précis dans les libellés modifiés pour éviter de créer descFINant par exemple -->
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
    
    <!--*****************************Instance de perso, adhérence dans les instances perso déjà en base et dans une css Orbeon*****************************-->



    <!--***********************************************************Pas d'adhérences, remplaçable***********************************************************-->
    <!-- Une action qu'on peut réintégrer et appeler lors de l'envoi -->
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

    <!--***********************************************************Pas d'adhérences, remplaçable***********************************************************-->  
    
     <!--********************************Adhérences dans les css (pas les classes, c'est au dessus) ou dans les xslt Orbeon********************************-->

    <!-- Pour l'ensemble des attributs, on procède à certaines modifications de chaînes -->
    <xsl:template match="@*">
        <xsl:attribute name="{name()}" select="boom:modifications(.)"/>
        <!--<xsl:attribute name="{name()}" select="."/>-->
    </xsl:template>

    <!-- Voici une liste de chaînes à remplacer dans les attributs (adhérences dans Orbeon) -->
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
        <!-- A la fin, on remplace encore d'autres chaînes -->
        <xsl:value-of select="boom:modifications-perso($modifie)"/>
    </xsl:function>

    <!-- Cet élément doit être renommé, il y a une adhérence dans readonly.xsl -->
    <xsl:template match="PageChangeDone[ancestor::xf:instance[@id='fr-form-util']]">
        <changementPageEffectue/>
    </xsl:template>

    <!-- Cet attribut doit être renommé, l'identifiant HTML correspondant est utilisé dans les css Orbeon -->
   <!-- <xsl:template match="@id[.='progress-percent' and parent::xf:output]" priority="2">
        <xsl:attribute name="id" select="'pourcentageAvancement'"/>
    </xsl:template>-->

    <!-- ATTENTION : si on supprime ces différences et qu'on se cale sur ce qui est fait côté OpenSource (il faut modifier fin.xsl), le HTML généré n'est plus exactement le même pour le texte correspondant -->
    <!-- Il ne s'agit donc pas simplement de traduire la classe css côté Orbeon, il faut aussi modifier à la marge le sélecteur pour qu'elle s'applique sur les nouveaux et les anciens questionnaires -->
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
    <!-- ATTENTION -->

    <!--********************************Adhérences dans les css (pas les classes, c'est au dessus) ou dans les xslt Orbeon********************************-->

</xsl:transform>
