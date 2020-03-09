<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" indent="no" encoding="UTF-8" />

    <xsl:param name="properties-file" />
    <xsl:param name="parameters-file" />
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty />
    </xsl:param>

    <xsl:variable name="business" select="'business'" />
    <xsl:variable name="household" select="'household'" />
    <xsl:variable name="default" select="'default'" />

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

    <xsl:variable name="begin-questions-identification" as="xs:boolean" select="$parameters//BeginQuestion/Identification" />
    <xsl:variable name="end-response-time-question" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//EndQuestion/ResponseTimeQuestion != ''">
                <xsl:value-of select="$parameters//EndQuestion/ResponseTimeQuestion" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//EndQuestion/ResponseTimeQuestion" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="end-comment-question" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="$parameters//EndQuestion/CommentQuestion != ''">
                <xsl:value-of select="$parameters//EndQuestion/CommentQuestion" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//EndQuestion/CommentQuestion" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

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

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add elements to the main instance</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xf:instance[@id='fr-form-instance']/form">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            
            <xsl:if test="$begin-questions-identification">
                <xsl:choose>
                    <xsl:when test="$studyUnit=$business">
                        <INSEE-BUSINESS-QUEST-DEBUT>
                            <INSEE-BUSINESS-SEQ-0-1 />
                            <COMMENT_UE />
                        </INSEE-BUSINESS-QUEST-DEBUT>
                    </xsl:when>
                    <xsl:when test="$studyUnit=$household"/>
                    <xsl:when test="$studyUnit=$default"/>
                </xsl:choose>
            </xsl:if>
            
            <xsl:apply-templates select="node()" />
            
            <xsl:if test="$end-response-time-question or $end-comment-question">
                <xsl:choose>
                    <xsl:when test="$studyUnit=$business">
                        <INSEE-BUSINESS-TEMPS>
                            <xsl:if test="$end-response-time-question">
                                <INSEE-BUSINESS-TEMPS-QI-1 />
                                <HEURE_REMPL />
                                <MIN_REMPL />
                            </xsl:if>
                            <xsl:if test="$end-comment-question">
                                <COMMENT_QE />
                            </xsl:if>
                        </INSEE-BUSINESS-TEMPS>
                    </xsl:when>
                    <xsl:when test="$studyUnit=$household">
                        <INSEE-HOUSEHOLD-TEMPS>
                            <xsl:if test="$end-response-time-question">
                                <INSEE-HOUSEHOLD-TEMPS-QI-1 />
                                <HEURE_REMPL />
                                <MIN_REMPL />
                            </xsl:if>
                            <xsl:if test="$end-comment-question">
                                <COMMENT_QE />
                            </xsl:if>
                        </INSEE-HOUSEHOLD-TEMPS>
                    </xsl:when>
                    <xsl:when test="$studyUnit=$default"/>
                </xsl:choose>
            </xsl:if>
        </xsl:copy>
        
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add elements to the corresponding bind</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xf:bind[@id='fr-form-instance-binds']">
        <xsl:choose>
            <xsl:when test="$studyUnit=$business">
                <xsl:copy>
                    <xsl:apply-templates select="@*" />
                    <xsl:if test="$begin-questions-identification">
                        <xf:bind id="INSEE-BUSINESS-QUEST-DEBUT-bind" name="INSEE-BUSINESS-QUEST-DEBUT" ref="INSEE-BUSINESS-QUEST-DEBUT">
                            <xf:bind id="INSEE-BUSINESS-SEQ-0-1-bind" name="INSEE-BUSINESS-SEQ-0-1" ref="INSEE-BUSINESS-SEQ-0-1" />
                            <xf:bind id="COMMENT_UE-bind" name="COMMENT_UE" ref="COMMENT_UE" />
                        </xf:bind>
                    </xsl:if>
                    <xsl:apply-templates select="node()" />
                    <xsl:if test="$end-response-time-question or $end-comment-question">
                        <xf:bind id="INSEE-BUSINESS-TEMPS-bind" name="INSEE-BUSINESS-TEMPS" ref="INSEE-BUSINESS-TEMPS">
                            <xsl:if test="$end-response-time-question">
                                <xf:bind id="INSEE-BUSINESS-TEMPS-QI-1-bind" name="INSEE-BUSINESS-TEMPS-QI-1" ref="INSEE-BUSINESS-TEMPS-QI-1" />
                                <xf:bind id="HEURE_REMPL-bind" name="HEURE_REMPL" ref="HEURE_REMPL">
                                    <xf:constraint value="matches(.,'^[0-9]?[0-9]$') or .=''" />
                                </xf:bind>
                                <xf:bind id="MIN_REMPL-bind" name="MIN_REMPL" ref="MIN_REMPL">
                                    <xf:constraint value="matches(.,'^[0-5]?[0-9]$') or .=''" />
                                </xf:bind>
                            </xsl:if>
                            <xsl:if test="$end-comment-question">
                                <xf:bind id="COMMENT_QE-bind" name="COMMENT_QE" ref="COMMENT_QE" />
                            </xsl:if>
                        </xf:bind>
                    </xsl:if>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="$studyUnit=$household">
                <xsl:copy>
                    <xsl:apply-templates select="@*" />
                    <xsl:apply-templates select="node()" />
                    <xsl:if test="$end-response-time-question or $end-comment-question">
                        <xf:bind id="INSEE-HOUSEHOLD-TEMPS-bind" name="INSEE-HOUSEHOLD-TEMPS" ref="INSEE-HOUSEHOLD-TEMPS">
                            <xsl:if test="$end-response-time-question">
                                <xf:bind id="INSEE-HOUSEHOLD-TEMPS-QI-1-bind" name="INSEE-HOUSEHOLD-TEMPS-QI-1" ref="INSEE-HOUSEHOLD-TEMPS-QI-1" />
                                <xf:bind id="HEURE_REMPL-bind" name="HEURE_REMPL" ref="HEURE_REMPL">
                                    <xf:constraint value="matches(.,'^[0-9]?[0-9]$') or .=''" />
                                </xf:bind>
                                <xf:bind id="MIN_REMPL-bind" name="MIN_REMPL" ref="MIN_REMPL">
                                    <xf:constraint value="matches(.,'^[0-5]?[0-9]$') or .=''" />
                                </xf:bind>
                            </xsl:if>
                            <xsl:if test="$end-comment-question">
                                <xf:bind id="COMMENT_QE-bind" name="COMMENT_QE" ref="COMMENT_QE" />
                            </xsl:if>
                        </xf:bind>
                    </xsl:if>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="$studyUnit=$default">
                <xsl:copy>
                    <xsl:apply-templates select="@*" />
                    <xsl:apply-templates select="node()" />
                </xsl:copy>
            </xsl:when>
        </xsl:choose>

    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add elements to french resources</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xf:instance[@id='fr-form-resources']/resources/resource[@xml:lang='fr']">
        <xsl:choose>
            <xsl:when test="$studyUnit=$business">
                <xsl:copy>
                    <xsl:apply-templates select="@*" />
                    <xsl:if test="$begin-questions-identification">
                        <INSEE-BUSINESS-QUEST-DEBUT>
                            <label>Identification</label>
                        </INSEE-BUSINESS-QUEST-DEBUT>
                        <INSEE-BUSINESS-SEQ-0-1>
                            <label>
                                <xsl:value-of select="'Identification de votre øLabelUniteEnqueteeø'" />
                            </label>
                        </INSEE-BUSINESS-SEQ-0-1>
                        <COMMENT_UE>
                            <label>
                                <xsl:value-of select="'➡ Remarque, commentaire sur un changement concernant votre øLabelUniteEnqueteeø :'" />
                            </label>
                        </COMMENT_UE>
                    </xsl:if>
                    <xsl:apply-templates select="node()" />
                    <xsl:if test="$end-response-time-question or $end-comment-question">
                        <INSEE-BUSINESS-TEMPS>
                            <label>
                                <xsl:choose>
                                    <xsl:when test="$end-response-time-question and $end-comment-question">
                                        <xsl:value-of select="'Temps de réponse et commentaires'" />
                                    </xsl:when>
                                    <xsl:when test="$end-response-time-question">
                                        <xsl:value-of select="'Temps de réponse'" />
                                    </xsl:when>
                                    <xsl:when test="$end-comment-question">
                                        <xsl:value-of select="'Commentaires'" />
                                    </xsl:when>
                                </xsl:choose>
                            </label>
                        </INSEE-BUSINESS-TEMPS>
                        <xsl:if test="$end-response-time-question">
                            <INSEE-BUSINESS-TEMPS-QI-1>
                                <label>&lt;p&gt;➡ Combien de temps avez-vous mis en tout pour répondre à cette enquête
                                    (recherche des données + remplissage du questionnaire) ?&lt;/p&gt;</label>
                            </INSEE-BUSINESS-TEMPS-QI-1>
                            <HEURE_REMPL>
                                <alert>Le nombre d'heures doit être compris entre 0 et 99.</alert>
                            </HEURE_REMPL>
                            <MIN_REMPL>
                                <alert>Le nombre de minutes doit être compris entre 0 et 59.</alert>
                            </MIN_REMPL>
                        </xsl:if>
                        <xsl:if test="$end-comment-question">
                            <COMMENT_QE>
                                <label>➡ Commentaires et remarques éventuelles concernant l’enquête :</label>
                            </COMMENT_QE>
                        </xsl:if>
                    </xsl:if>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="$studyUnit=$household">
                <xsl:copy>
                    <xsl:apply-templates select="@*" />
                    <xsl:apply-templates select="node()" />
                    <xsl:if test="$end-response-time-question or $end-comment-question">
                        <INSEE-HOUSEHOLD-TEMPS>
                            <label>
                                <xsl:choose>
                                    <xsl:when test="$end-response-time-question and $end-comment-question">
                                        <xsl:value-of select="'Temps de réponse et commentaires'" />
                                    </xsl:when>
                                    <xsl:when test="$end-response-time-question">
                                        <xsl:value-of select="'Temps de réponse'" />
                                    </xsl:when>
                                    <xsl:when test="$end-comment-question">
                                        <xsl:value-of select="'Commentaires'" />
                                    </xsl:when>
                                </xsl:choose>
                            </label>
                        </INSEE-HOUSEHOLD-TEMPS>
                        <xsl:if test="$end-response-time-question">
                            <INSEE-HOUSEHOLD-TEMPS-QI-1>
                                <label>&lt;p&gt;➡ Combien de temps avez-vous mis en tout pour répondre à cette enquête
                                    (recherche des données + remplissage du questionnaire) ?&lt;/p&gt;</label>
                            </INSEE-HOUSEHOLD-TEMPS-QI-1>
                            <HEURE_REMPL>
                                <alert>Le nombre d'heures doit être compris entre 0 et 99.</alert>
                            </HEURE_REMPL>
                            <MIN_REMPL>
                                <alert>Le nombre de minutes doit être compris entre 0 et 59.</alert>
                            </MIN_REMPL>
                        </xsl:if>
                        <xsl:if test="$end-comment-question">
                            <COMMENT_QE>
                                <label>➡ Commentaires et remarques éventuelles concernant l’enquête :</label>
                            </COMMENT_QE>
                        </xsl:if>
                    </xsl:if>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="$studyUnit=$default">
                <xsl:copy>
                    <xsl:apply-templates select="@*" />
                    <xsl:apply-templates select="node()" />
                </xsl:copy>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add the new page</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="xf:instance[@id='fr-form-util']/Util/Pages/Beginning">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
        <xsl:if test="$begin-questions-identification and $studyUnit=$business">
            <INSEE-BUSINESS-QUEST-DEBUT />
        </xsl:if>
    </xsl:template>
    <xsl:template match="xf:instance[@id='fr-form-util']/Util/Pages/End">
        <xsl:if test="$end-response-time-question or $end-comment-question">
            <xsl:choose>
                <xsl:when test="$studyUnit=$business">
                    <INSEE-BUSINESS-TEMPS />
                </xsl:when>
                <xsl:when test="$studyUnit=$household">
                    <INSEE-HOUSEHOLD-TEMPS />
                </xsl:when>
                <xsl:when test="$studyUnit=$default"/>
            </xsl:choose>
        </xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" />
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Add elements into the body</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="fr:view/fr:body">
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            
            <xsl:if test="$begin-questions-identification">
                <xsl:choose>
                    <xsl:when test="$studyUnit=$business">
                        <fr:section id="INSEE-BUSINESS-QUEST-DEBUT-control" bind="INSEE-BUSINESS-QUEST-DEBUT-bind" name="INSEE-BUSINESS-QUEST-DEBUT">
                            <xf:label ref="$form-resources/INSEE-BUSINESS-QUEST-DEBUT/label" />
                            <xhtml:div class="submodule">
                                <xhtml:h3>
                                    <xf:output id="INSEE-BUSINESS-SEQ-0-1-control" bind="INSEE-BUSINESS-SEQ-0-1-bind">
                                        <xf:label ref="$form-resources/INSEE-BUSINESS-SEQ-0-1/label" class="submodule" mediatype="text/html" />
                                    </xf:output>
                                </xhtml:h3>
                                <xf:textarea id="COMMENT_UE-control" name="COMMENT_UE" bind="COMMENT_UE-bind" class="question text text2000" xxf:maxlength="2000">
                                    <xf:label ref="$form-resources/COMMENT_UE/label" />
                                </xf:textarea>
                            </xhtml:div>
                        </fr:section>
                    </xsl:when>
                    <xsl:when test="$studyUnit=$household"/>
                    <xsl:when test="$studyUnit=$default"/>
                </xsl:choose>
            </xsl:if>
            
            <xsl:apply-templates select="node()" />
            
            <xsl:if test="$end-response-time-question or $end-comment-question">
                <xsl:choose>
                    <xsl:when test="$studyUnit=$business">
                        <fr:section id="INSEE-BUSINESS-TEMPS-control" bind="INSEE-BUSINESS-TEMPS-bind" name="INSEE-BUSINESS-TEMPS">
                            <xf:label ref="$form-resources/INSEE-BUSINESS-TEMPS/label" />
                            <xsl:if test="$end-response-time-question">
                                <xhtml:div class="question">
                                    <xf:output id="INSEE-BUSINESS-TEMPS-QI-1-control" name="INSEE-BUSINESS-TEMPS-QI-1" bind="INSEE-BUSINESS-TEMPS-QI-1-bind" class="question">
                                        <xf:label ref="$form-resources/INSEE-BUSINESS-TEMPS-QI-1/label" mediatype="text/html" />
                                    </xf:output>
                                    <xf:input id="HEURE_REMPL-control" name="HEURE_REMPL" bind="HEURE_REMPL-bind" class="duree" xxf:maxlength="2">
                                        <xf:alert ref="$form-resources/HEURE_REMPL/alert" />
                                    </xf:input>
                                    <xhtml:span class="suffix">heures</xhtml:span>
                                    <xf:input id="MIN_REMPL-control" name="MIN_REMPL" bind="MIN_REMPL-bind" class="duree" xxf:maxlength="2">
                                        <xf:alert ref="$form-resources/MIN_REMPL/alert" />
                                    </xf:input>
                                    <xhtml:span class="suffix">minutes</xhtml:span>
                                </xhtml:div>
                            </xsl:if>
                            <xsl:if test="$end-comment-question">
                                <xf:textarea id="COMMENT_QE-control" name="COMMENT_QE" bind="COMMENT_QE-bind" class="question text text2000" xxf:maxlength="2000">
                                    <xf:label ref="$form-resources/COMMENT_QE/label" />
                                </xf:textarea>
                            </xsl:if>
                        </fr:section>
                    </xsl:when>
                    <xsl:when test="$studyUnit=$household">
                        <fr:section id="INSEE-HOUSEHOLD-TEMPS-control" bind="INSEE-HOUSEHOLD-TEMPS-bind" name="INSEE-HOUSEHOLD-TEMPS">
                            <xf:label ref="$form-resources/INSEE-HOUSEHOLD-TEMPS/label" />
                            <xsl:if test="$end-response-time-question">
                                <xhtml:div class="question">
                                    <xf:output id="INSEE-HOUSEHOLD-TEMPS-QI-1-control" name="INSEE-HOUSEHOLD-TEMPS-QI-1" bind="INSEE-HOUSEHOLD-TEMPS-QI-1-bind" class="question">
                                        <xf:label ref="$form-resources/INSEE-HOUSEHOLD-TEMPS-QI-1/label" mediatype="text/html" />
                                    </xf:output>
                                    <xf:input id="HEURE_REMPL-control" name="HEURE_REMPL" bind="HEURE_REMPL-bind" class="duree" xxf:maxlength="2">
                                        <xf:alert ref="$form-resources/HEURE_REMPL/alert" />
                                    </xf:input>
                                    <xhtml:span class="suffix">heures</xhtml:span>
                                    <xf:input id="MIN_REMPL-control" name="MIN_REMPL" bind="MIN_REMPL-bind" class="duree" xxf:maxlength="2">
                                        <xf:alert ref="$form-resources/MIN_REMPL/alert" />
                                    </xf:input>
                                    <xhtml:span class="suffix">minutes</xhtml:span>
                                </xhtml:div>
                            </xsl:if>
                            <xsl:if test="$end-comment-question">
                                <xf:textarea id="COMMENT_QE-control" name="COMMENT_QE" bind="COMMENT_QE-bind" class="question text text2000" xxf:maxlength="2000">
                                    <xf:label ref="$form-resources/COMMENT_QE/label" />
                                </xf:textarea>
                            </xsl:if>
                        </fr:section>
                    </xsl:when>
                    <xsl:when test="$studyUnit=$default"/>
                </xsl:choose>
            </xsl:if>            
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>