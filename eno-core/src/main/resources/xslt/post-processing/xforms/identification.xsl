<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" indent="no" encoding="utf-8"/>
    <!-- Transformation to add the identification module -->

    <!-- We determine which is the identification page -->
    <xsl:variable name="nomPageId">
        <xsl:value-of select="//xf:instance[@id='fr-form-instance']/form/Beginning/following-sibling::*[name()!='Groupe' and name()!='Variable'][1]/name()"/>
    </xsl:variable>
    
    <!-- When the identification elements are put at the end of the first module, their labels are put just before the second module. -->
    <xsl:variable name="nomPageSuivanteId">
        <xsl:value-of select="//xf:instance[@id='fr-form-instance']/form/*[name()=$nomPageId]/following-sibling::*[name()!='Groupe' and name()!='Variable'][1]/name()"/>
    </xsl:variable>        
<!--
    <xsl:variable name="nbParagraphesPageId">
        <xsl:value-of select="count(//fr:section[@name=$nomPageId]//xhtml:div[@class='submodule'])"/>
    </xsl:variable>
-->
    <xsl:template match="/">
        <xsl:apply-templates select="xhtml:html"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de base pour tous les éléments et tous les attributs, on recopie
                simplement en sortie</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <!-- Elements are added to the main proceeding -->
    <xsl:template
        match="*[parent::form[parent::xf:instance[@id='fr-form-instance']] and name()=$nomPageId]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <Identification/>
            <Identification-1/>
            <Identification-2/>
            <Identification-3/>
        </xsl:copy>
    </xsl:template>

    <!-- Elements are added to the corresponding binds -->
    <xsl:template
        match="xf:bind[@name=$nomPageId and ancestor::xf:bind[@id='fr-form-instance-binds']]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xf:bind id="Identification-bind" name="Identification" ref="Identification"/>
            <xf:bind id="Identification-1-bind" name="Identification-1" ref="Identification-1"/>
            <xf:bind id="Identification-2-bind" name="Identification-2" ref="Identification-2"/>
            <xf:bind id="Identification-3-bind" name="Identification-3" ref="Identification-3"/>
        </xsl:copy>
    </xsl:template>

    <!-- Elements are added to the instance of the metadata -->
    <xsl:template match="xf:instance[@id='donnees-pilotage']/InformationsQuestionnaire">
        <xsl:copy>
            <xsl:apply-templates select="node()[name() !='UniteEnquetee'] | @*"/>
            <Contact>
                <Nom/>
                <Mel/>
                <Telephone/>
                <Adresse>
                    <ComplementAdresse/>
                    <MentionSpeciale/>
                    <LibellePays/>
                    <InformationsVoie/>
                    <Ville/>
                </Adresse>
            </Contact>
            <xsl:apply-templates select="UniteEnquetee"/>
        </xsl:copy>
    </xsl:template>

    <!-- Elements are added to the corresponding binds -->
    <xsl:template match="xf:bind[@id='donnees-pilotage-binds']">
        <xsl:copy>
            <xsl:apply-templates select="node()[@id !='UniteEnquetee-bind'] | @*"/>
            <xf:bind id="Contact-bind" ref="Contact">
                <xf:bind id="Nom-bind" ref="Nom"/>
                <xf:bind id="Mel-bind" ref="Mel"/>
                <xf:bind id="Telephone-bind" ref="Telephone"/>
                <xf:bind id="Adresse-bind" ref="Adresse">
                    <xf:bind id="ComplementAdresse-bind" ref="ComplementAdresse"/>
                    <xf:bind id="MentionSpeciale-bind" ref="MentionSpeciale"/>
                    <xf:bind id="LibellePays-bind" ref="LibellePays"/>
                    <xf:bind id="InformationsVoie-bind" ref="InformationsVoie"/>
                    <xf:bind id="Ville-bind" ref="Ville"/>
                </xf:bind>
            </xf:bind>
            <xsl:apply-templates select="xf:bind[@id ='UniteEnquetee-bind']"/>
        </xsl:copy>
    </xsl:template>

    <!-- Adding elements to the resources -->
    <xsl:template match="xf:instance[@id='fr-form-resources']/resources/resource/*[name()=$nomPageSuivanteId]">
        <Identification>
            <label>Identification de la personne</label>
            <!--<label><xsl:value-of select="$nbParagraphesPageId+1"/>. Identification de la personne</label>-->
        </Identification>
        <Identification-1>
            <label>Afin de bénéficier pleinement des services proposés
                dans le cadre de la réponse en ligne aux enquêtes de la statistique publique,
                nous vous invitons à vérifier que les coordonnées du contact répondant à
                l'enquête, rappelées ci-dessous, sont bien à jour. L'adresse de messagerie est
                en particulier nécessaire en cas de réinitialisation en ligne du mot de passe.</label>
        </Identification-1>
        <Identification-2>
            <label>&lt;b&gt;Vos informations personnelles : &lt;/b&gt;</label>
        </Identification-2>
        <Identification-3>
            <label>&lt;b&gt;Vos coordonnées postales : &lt;/b&gt;</label>
        </Identification-3>
        <Nom>
            <label>Nom :</label>
        </Nom>
        <Mel>
            <label>Mél :</label>
        </Mel>
        <Telephone>
            <label>Téléphone :</label>
        </Telephone>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <!-- At body level -->
    <xsl:template match="fr:section[@name=$nomPageId]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
            <xhtml:div class="submodule">
                <xhtml:h3>
                    <xf:output id="Identification-control" bind="Identification-bind">
                        <xf:label ref="$form-resources/Identification/label" class="submodule" mediatype="text/html"/>
                    </xf:output>
                </xhtml:h3>
                <xhtml:div class="framePerso">
                    <xf:output id="Identification-1-control" bind="Identification-1-bind">
                        <xf:label ref="$form-resources/Identification-1/label" class="submodule" mediatype="text/html"/>
                    </xf:output>
                </xhtml:div>
                <xhtml:div class="framePerso">
                    <xf:output id="Identification-2-control" bind="Identification-2-bind">
                        <xf:label ref="$form-resources/Identification-2/label" class="submodule" mediatype="text/html"/>
                    </xf:output>
                    <xf:output id="Nom-control" bind="Nom-bind">
                        <xf:label ref="$form-resources/Nom/label" mediatype="text/html"/>
                    </xf:output>
                    <xf:output id="Mel-control" bind="Mel-bind">
                        <xf:label ref="$form-resources/Mel/label" mediatype="text/html"/>
                    </xf:output>
                    <xf:output id="Telephone-control" bind="Telephone-bind">
                        <xf:label ref="$form-resources/Telephone/label" mediatype="text/html"/>
                    </xf:output>
                </xhtml:div>
                <xhtml:div class="framePerso">
                    <xf:output id="Identification-3-control" bind="Identification-3-bind">
                        <xf:label ref="$form-resources/Identification-3/label" class="submodule" mediatype="text/html"/>
                    </xf:output>
                    <xf:output id="ComplementAdresse-control" bind="ComplementAdresse-bind"/>
                    <xf:output id="InformationsVoie-control" bind="InformationsVoie-bind"/>
                    <xf:output id="MentionSpeciale-control" bind="MentionSpeciale-bind"/>
                    <xf:output id="Ville-control" bind="Ville-bind"/>
                    <xf:output id="LibellePays-control" bind="LibellePays-bind"/>
                </xhtml:div>
                <xhtml:div class="framePerso">
                    <!-- Depends on the environment, configured in Orbeon -->
                    <xsl:variable name="lien"
                        select="string('{concat(xxf:property(''url-portail''),xxf:property(''mon-compte''))}')"/>
                    <xhtml:p>
                        <xhtml:b>Pour renseigner ou mettre à jour vos coordonnées</xhtml:b>, cliquez
                        sur ce lien :&#160;<xhtml:a href="{$lien}" target="_blank">Mon
                            compte</xhtml:a>.</xhtml:p>
                </xhtml:div>
            </xhtml:div>
        </xsl:copy>
    </xsl:template>

</xsl:transform>
