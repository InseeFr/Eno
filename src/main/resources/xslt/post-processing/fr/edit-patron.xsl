<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" encoding="utf-8" />
    <!-- Transformation to add the homepage of the different questionnaires -->

    <!-- The campaign -->
    <xsl:param name="campaign" as="xs:string" select="''" />

    <!-- The model -->
    <xsl:variable name="modele" select="//xf:instance[@id='fr-form-instance']/form/@modele" />

    <xsl:param name="properties-file" />
    <xsl:param name="parameters-file" />
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <!-- In parameter : the parameter file which contains the information coming from metadata database ("Pilotage") -->
    <xsl:param name="metadata-file" />
    <xsl:param name="metadata-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <xsl:variable name="metadata">
        <xsl:choose>
            <xsl:when test="$metadata-node/*">
                <xsl:copy-of select="$metadata-node" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="doc($metadata-file)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="business" select="'business'" />
    <xsl:variable name="household" select="'household'" />

    <xsl:variable name="properties" select="doc($properties-file)" />
    <xsl:variable name="parameters">
        <xsl:choose>
            <xsl:when test="$parameters-node/*">
                <xsl:copy-of select="$parameters-node" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="doc($parameters-file)" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="campaign-value">
        <xsl:choose>
            <xsl:when test="$campaign!=''">
                <xsl:value-of select="$campaign" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$parameters//Campagne" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="studyUnit">
        <xsl:choose>
            <xsl:when test="$parameters//StudyUnit != ''">
                <xsl:value-of select="$parameters//StudyUnit" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//StudyUnit" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>


    <xsl:variable name="LibelleEnquete">
        <xsl:choose>
            <xsl:when test="$metadata//LibelleEnquete!=''">
                <xsl:value-of select="$metadata//LibelleEnquete" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$metadata//Campagne/Libelle" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="frequences" select="('mensuelle','trimestrielle','bimestrielle','semestrielle')" as="xs:string *" />
    <xsl:variable name="URLNotice" select="$metadata//URLNotice" />
    <xsl:variable name="URLSpecimen" select="$metadata//URLSpecimen" />
    <xsl:variable name="URLDiffusion" select="$metadata//URLDiffusion" />

    <xsl:template match="/">
        <xsl:apply-templates select="xhtml:html" />
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de base pour tous les éléments et tous les attributs, on recopie
                simplement en sortie</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xhtml:head">
        <xsl:copy>
            <xsl:apply-templates select="*[not(name()='xf:model')]" />
            <xsl:apply-templates select="xf:model" />
        </xsl:copy>
    </xsl:template>

    <!-- We integrate the metadata zone -->
    <xsl:template match="xf:bind[@id='fr-form-instance-binds']">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
        <!-- An instance to possibly host the metadata -->
        <xf:instance id="donnees-pilotage">
            <InformationsQuestionnaire>
                <xsl:if test="$studyUnit=$business">
                    <UniteEnquetee>
                        <BarreFixe />
                        <LabelUniteEnquetee />
                    </UniteEnquetee>
                </xsl:if>
            </InformationsQuestionnaire>
        </xf:instance>
        <!-- and the corresponding bind -->
        <xf:bind id="donnees-pilotage-binds" ref="instance('donnees-pilotage')">
            <xsl:if test="$studyUnit=$business">
                <xf:bind id="UniteEnquetee-bind" ref="UniteEnquetee">
                    <xf:bind id="BarreFixe-bind" ref="BarreFixe" />
                    <xf:bind id="LabelUniteEnquetee-bind" ref="LabelUniteEnquetee" />
                </xf:bind>
            </xsl:if>
        </xf:bind>
    </xsl:template>

    <!-- Adding a label for the item that displays the investigative unit's info -->
    <xsl:template match="resource[ancestor::xf:instance[@id='fr-form-resources']]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
            <xsl:if test="$studyUnit=$business">
                <BarreFixe>
                    <label>Votre </label>
                </BarreFixe>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <!-- We add this element in this div -->
    <xsl:template match="xhtml:div[parent::fr:body]">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:if test="$studyUnit=$business">
                <xf:output id="BarreFixe-control" bind="BarreFixe-bind">
                    <xf:label ref="$form-resources/BarreFixe/label" mediatype="text/html" />
                </xf:output>
            </xsl:if>
            <xsl:apply-templates select="node()" />
        </xsl:copy>
    </xsl:template>

    <!-- Changing the title of the form -->
    <xsl:template match="xhtml:title | title[parent::metadata[parent::xf:instance[@id='fr-form-metadata']]]">
        <xsl:copy>
            <xsl:value-of select="$LibelleEnquete" />
        </xsl:copy>
    </xsl:template>

    <!-- Inserting the <perso> tag after the <Util> tag of stromae -->
    <xsl:template match="Util[ancestor::xf:instance[@id='fr-form-instance']]">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
        <perso />
    </xsl:template>

    <!-- Overloads the registration resource for both submissions used in eno-core. -->
    <xsl:template match="xf:submission[@id='save' or @id='submit']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:variable name="resource">
                <!-- TODO : change /restxq/ to /apps/orbeon/ -->
                <xsl:value-of select="concat('{xxf:property(''server-exist-orbeon'')}/restxq/{xxf:property(''enregistrer-service'')}/',$campaign-value,'/',$modele,'/{xxf:get-request-parameter(''unite-enquete'')}?ongletproof=oui')" />
            </xsl:variable>
            <xsl:attribute name="resource" select="$resource" />
            <xsl:apply-templates select="node()" />
        </xsl:copy>
    </xsl:template>

    <!-- Adding a submission to send a PDF to database just before the "xforms-ready" -->
    <xsl:template match="xf:action[@ev:event='xforms-ready' and following-sibling::*[position()=1 and name()='xf:action' and @ev:event='page-change']]">
        <xf:submission id="expedierPdf" method="post" replace="none">
            <xsl:variable name="resource">
                <xsl:value-of select="concat('/expedier/',$campaign-value,'/{xxf:get-request-parameter(''unite-enquete'')}?modele=',$modele)" />
            </xsl:variable>
            <xsl:attribute name="resource" select="$resource" />
        </xf:submission>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>

    <!-- We enter this information in the personal section. They will be reorganized in Orbeon. -->
    <xsl:template match="fr:body/xf:switch[@id='section-body']">
        <xhtml:div class="perso-formulaire">
            <xsl:if test="$URLNotice/text()">
                <xhtml:a href="{$URLNotice}" id="URLNotice" />
            </xsl:if>
            <xsl:if test="$URLSpecimen/text()">
                <xhtml:a href="{$URLSpecimen}" id="URLSpecimen" />
            </xsl:if>
            <xsl:if test="$URLDiffusion/text()">
                <xhtml:a href="{$URLDiffusion}" id="URLDiffusion" />
            </xsl:if>
        </xhtml:div>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>