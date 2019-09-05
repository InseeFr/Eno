<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" encoding="utf-8"/>
    <!-- Transformation pour ajouter le module d'identification -->

    <!-- En paramètre le fichier de paramétrage qui contient l'information du besoin des variables Coltrane standards -->
    <xsl:param name="parameters-file"/>

    <!--<xsl:variable name="type-repondant-coltrane" select="doc($parameters-file)//ColtraneQuestions/TypeRepondantLabel"/>-->
    <xsl:variable name="begin-questions" as="xs:boolean"
        select="if (doc($parameters-file)//ColtraneQuestions/*[name()='TypeRepondantLabel' or name()='Debut']) then true() else false()"/>
    <xsl:variable name="end-questions" as="xs:boolean"
        select="if (doc($parameters-file)//ColtraneQuestions/*[name()='TypeRepondantLabel' or name()='Fin']) then true() else false()"/>

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

    <!-- On rajoute des éléments à l'instance principale -->
    <xsl:template match="xf:instance[@id='fr-form-instance']/form">
        <xsl:copy>
            <xsl:if test="$begin-questions">
                <INSEE-COLTRANE-QUEST-DEBUT>
                    <INSEE-COLTRANE-SEQ-0-1/>
                    <COMMENT_UE/>
                </INSEE-COLTRANE-QUEST-DEBUT>
            </xsl:if>            
            <xsl:apply-templates select="node()"/>
            <xsl:if test="$end-questions">
                <INSEE-COLTRANE-TEMPS>
                    <INSEE-COLTRANE-TEMPS-QI-1/>
                    <HEURE_REMPL/>
                    <MIN_REMPL/>
                    <COMMENT_QE/>
                </INSEE-COLTRANE-TEMPS>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <!-- On rajoute des éléments aux binds correspondants -->
    <!-- On rajoute des éléments à l'instance principale -->
    <xsl:template match="xf:bind[@id='fr-form-instance-binds']">

        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:if test="$begin-questions">
                <xf:bind id="INSEE-COLTRANE-QUEST-DEBUT-bind" name="INSEE-COLTRANE-QUEST-DEBUT" ref="INSEE-COLTRANE-QUEST-DEBUT">
                    <xf:bind id="INSEE-COLTRANE-SEQ-0-1-bind" name="INSEE-COLTRANE-SEQ-0-1" ref="INSEE-COLTRANE-SEQ-0-1"/>
                    <xf:bind id="COMMENT_UE-bind" name="COMMENT_UE" ref="COMMENT_UE"/>
                </xf:bind>
            </xsl:if>
            <xsl:apply-templates select="node()"/>
            <xsl:if test="$end-questions">
                <xf:bind id="INSEE-COLTRANE-TEMPS-bind" name="INSEE-COLTRANE-TEMPS" ref="INSEE-COLTRANE-TEMPS">
                    <xf:bind id="INSEE-COLTRANE-TEMPS-QI-1-bind" name="INSEE-COLTRANE-TEMPS-QI-1" ref="INSEE-COLTRANE-TEMPS-QI-1"/>
                    <xf:bind id="HEURE_REMPL-bind" name="HEURE_REMPL" ref="HEURE_REMPL">
                        <xf:constraint value="matches(.,'^[0-9]?[0-9]$') or .=''"/>
                    </xf:bind>
                    <xf:bind id="MIN_REMPL-bind" name="MIN_REMPL" ref="MIN_REMPL">
                        <xf:constraint value="matches(.,'^[0-5]?[0-9]$') or .=''"/>
                    </xf:bind>
                    <xf:bind id="COMMENT_QE-bind" name="COMMENT_QE" ref="COMMENT_QE"/>
                </xf:bind>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <!-- On rajoute des éléments aux ressources -->
    <xsl:template match="xf:instance[@id='fr-form-resources']/resources/resource[@xml:lang='fr']">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:if test="$begin-questions">
                <INSEE-COLTRANE-QUEST-DEBUT>
                    <label>Identification</label>
                </INSEE-COLTRANE-QUEST-DEBUT>
                <INSEE-COLTRANE-SEQ-0-1>
                    <label>
                        <xsl:value-of select="'Identification de votre øLabelUniteEnqueteeø'"/>
                    </label>
                </INSEE-COLTRANE-SEQ-0-1>
                <COMMENT_UE>
                    <label>
                        <xsl:value-of select="'➡ Remarque, commentaire sur un changement concernant votre øLabelUniteEnqueteeø :'"/>
                    </label>
                </COMMENT_UE>
            </xsl:if>
            <xsl:apply-templates select="node()"/>
            <xsl:if test="$end-questions">
                <INSEE-COLTRANE-TEMPS>
                    <label>Temps de réponse et commentaires</label>
                </INSEE-COLTRANE-TEMPS>
                <INSEE-COLTRANE-TEMPS-QI-1>
                    <label>&lt;p&gt;➡ Combien de temps avez-vous mis en tout pour répondre à cette enquête
                        (recherche des données + remplissage du questionnaire) ?&lt;/p&gt;</label>
                </INSEE-COLTRANE-TEMPS-QI-1>
                <HEURE_REMPL>
                    <alert>Le nombre d'heures doit être compris entre 0 et 99.</alert>
                </HEURE_REMPL>
                <MIN_REMPL>
                    <alert>Le nombre de minutes doit être compris entre 0 et 59.</alert>
                </MIN_REMPL>
                <COMMENT_QE>
                    <label>➡ Commentaires et remarques éventuelles concernant l’enquête :</label>
                </COMMENT_QE>
            </xsl:if>
        </xsl:copy>
    </xsl:template>
    
    <!-- On rajoute des éléments au niveau de la liste des pages s'il y a une page supplémentaire -->
    <xsl:template match="xf:instance[@id='fr-form-util']/Util/Pages/Beginning">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
        <xsl:if test="$begin-questions">
            <INSEE-COLTRANE-QUEST-DEBUT/>
        </xsl:if>        
    </xsl:template>

    <xsl:template match="xf:instance[@id='fr-form-util']/Util/Pages/End">
        <xsl:if test="$end-questions">
            <INSEE-COLTRANE-TEMPS/>
        </xsl:if>        
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Au niveau du body -->
    
    <xsl:template match="fr:view/fr:body">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:if test="$begin-questions">
                <fr:section id="INSEE-COLTRANE-QUEST-DEBUT-control" bind="INSEE-COLTRANE-QUEST-DEBUT-bind" name="INSEE-COLTRANE-QUEST-DEBUT">
                    <xf:label ref="$form-resources/INSEE-COLTRANE-QUEST-DEBUT/label"/>
                    <xhtml:div class="submodule">
                        <xhtml:h3>
                            <xf:output id="INSEE-COLTRANE-SEQ-0-1-control" bind="INSEE-COLTRANE-SEQ-0-1-bind">
                                <xf:label ref="$form-resources/INSEE-COLTRANE-SEQ-0-1/label" class="submodule" mediatype="text/html"/>
                            </xf:output>
                        </xhtml:h3>
                        <xf:textarea id="COMMENT_UE-control" name="COMMENT_UE" bind="COMMENT_UE-bind"
                            class="question text text2000" xxf:order="label control" xxf:maxlength="2000">
                            <xf:label ref="$form-resources/COMMENT_UE/label"/>
                        </xf:textarea>
                    </xhtml:div>
                </fr:section>
            </xsl:if>        
            <xsl:apply-templates select="node()"/>
            <xsl:if test="$end-questions">
                <fr:section id="INSEE-COLTRANE-TEMPS-control" bind="INSEE-COLTRANE-TEMPS-bind" name="INSEE-COLTRANE-TEMPS">
                    <xf:label ref="$form-resources/INSEE-COLTRANE-TEMPS/label"/>
                    <xhtml:div class="question">
                        <xf:output id="INSEE-COLTRANE-TEMPS-QI-1-control" name="INSEE-COLTRANE-TEMPS-QI-1" bind="INSEE-COLTRANE-TEMPS-QI-1-bind"
                            class="question" xxf:order="label control">
                            <xf:label ref="$form-resources/INSEE-COLTRANE-TEMPS-QI-1/label" mediatype="text/html"/>
                        </xf:output>
                        <xf:input id="HEURE_REMPL-control" name="HEURE_REMPL" bind="HEURE_REMPL-bind"
                            class="duree" xxf:order="control alert" xxf:maxlength="2">
                            <xf:alert ref="$form-resources/HEURE_REMPL/alert"/>
                        </xf:input>
                        <xhtml:span class="suffixe">heures</xhtml:span>
                        <xf:input id="MIN_REMPL-control" name="MIN_REMPL" bind="MIN_REMPL-bind"
                            class="duree" xxf:order="control alert" xxf:maxlength="2">
                            <xf:alert ref="$form-resources/MIN_REMPL/alert"/>
                        </xf:input>
                        <xhtml:span class="suffixe">minutes</xhtml:span>
                    </xhtml:div>
                    <xf:textarea id="COMMENT_QE-control" name="COMMENT_QE" bind="COMMENT_QE-bind"
                        class="question text text2000" xxf:order="label control" xxf:maxlength="2000">
                        <xf:label ref="$form-resources/COMMENT_QE/label"/>
                    </xf:textarea>
                </fr:section>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

</xsl:transform>
