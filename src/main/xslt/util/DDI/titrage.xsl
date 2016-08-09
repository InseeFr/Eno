<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:i="ddi:instance:3_2" xmlns:g="ddi:group:3_2"
    xmlns:d="ddi:datacollection:3_2" xmlns:r="ddi:reusable:3_2" xmlns:a="ddi:archive:3_2"
    xmlns:l="ddi:logicalproduct:3_2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:iatddi="http://xml/insee.fr/xslt/apply-templates/ddi"
    xmlns:xhtml="http://www.w3.org/1999/xhtml">
    <xsl:param name="fichier_parametrage"/>
    <xsl:output method="xml" indent="no" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xsl:variable name="style">
        <xsl:copy-of select="document($fichier_parametrage)/parametres/titre"/>
    </xsl:variable>
    <xsl:variable name="seqSansNumero" select="$style/titre/sequence/numberFreeSeq"/>
    <xsl:variable name="filtreSansNumero" select="$style/titre/question/notNumberedLastFilter"/>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de racine, on applique les templates de tous les enfants</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template de base pour tous les éléments et tous les attributs, on passe à son enfant</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="node() | @*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template pour rajouter un numéro aux séquences</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:Sequence[d:TypeOfSequence='Module' or d:TypeOfSequence='Paragraphe' or d:TypeOfSequence='Groupe']/r:Label">
        <xsl:variable name="niveau" select="parent::d:Sequence/d:TypeOfSequence"/>
        <xsl:variable name="styleSeq" select="$style/titre/sequence/niveau[@nom=$niveau]"/>
        <xsl:variable name="niveauParent" select="$style/titre/sequence/niveau[following-sibling::niveau[1]/@nom=$niveau]/@nom"/>
        <xsl:variable name="niveauGrandPere" select="$style/titre/sequence/niveau[following-sibling::niveau[2]/@nom=$niveau]/@nom"/>

        <xsl:variable name="numero">
            <xsl:apply-templates select="parent::d:Sequence" mode="calculateNumber"/>
        </xsl:variable>
        
        <xsl:variable name="prefix">
            <xsl:choose>
                <xsl:when test="$numero=''">
                    <xsl:value-of select="$styleSeq/preSeq"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($styleSeq/preSeq,$numero,$styleSeq/postNumSeq)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="modifTitre">
                <xsl:with-param name="prefixe" select="$prefix" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template pour rajouter un numéro aux questions</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="d:QuestionText/d:LiteralText">
        <!-- Le but est de calculer prefixe, concaténation des éléments du numéro -->
        
        <xsl:variable name="niveauSeqQuest">
            <xsl:choose>
                <xsl:when test="ancestor::d:Sequence[d:TypeOfSequence='Groupe']">Groupe</xsl:when>
                <xsl:when test="ancestor::d:Sequence[d:TypeOfSequence='Paragraphe']">Paragraphe</xsl:when>
                <xsl:otherwise>Module</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="niveauParent" select="$style/titre/sequence/niveau[following-sibling::niveau[1]/@nom=$niveauSeqQuest]/@nom"/>
        <xsl:variable name="styleQuest" select="$style/titre/question/niveau[@nom=$niveauSeqQuest]"/>
        
        <xsl:variable name="numeroParent">
            <xsl:if test="$styleQuest/numParent !='N'">
                <xsl:apply-templates select="ancestor::d:Sequence[d:TypeOfSequence='Module' or d:TypeOfSequence='Paragraphe' or d:TypeOfSequence='Groupe']
                    [1]" mode="calculateNumber"/>
            </xsl:if>
        </xsl:variable>

        <xsl:variable name="numero">
            <xsl:if test="iatddi:isSubQuestion(ancestor::d:QuestionConstruct,$niveauSeqQuest)=0">
                <!-- On compte les questions qui ne sont pas des sous-questions -->
<!--                <xsl:number count="d:ControlConstructReference[d:QuestionConstruct and (iatddi:isSubQuestion(d:QuestionConstruct,$niveauSeqQuest))]" 
                    level="any" format="{$styleQuest/styleNumQuest}" from="d:ControlConstructReference[d:Sequence[d:TypeOfSequence=$niveauSeqQuest]]"/>
-->                <xsl:number count="*[(name()='d:QuestionItem' or name()='d:QuestionGrid') and (iatddi:isSubQuestion(ancestor::d:QuestionConstruct,$niveauSeqQuest))=0]" 
                    level="any" format="{$styleQuest/styleNumQuest}" from="d:ControlConstructReference[d:Sequence[d:TypeOfSequence=$niveauSeqQuest]]"/>
            </xsl:if>
        </xsl:variable>

        <!--Numéro de la question par concaténation : preQuest + (($numeroParent + postNumParentQuest) + $numero + postNumQuest)-->
        <xsl:variable name="prefixe">
            <xsl:value-of select="$styleQuest/preQuest"/>
            <xsl:if test="$numero!=''">
                <xsl:if test="$numeroParent!=''">
                    <xsl:value-of select="concat($numeroParent,$styleQuest/postNumParentQuest)"/>
                </xsl:if>
                <xsl:value-of select="concat($numero,$styleQuest/postNumQuest)"/>
            </xsl:if>
        </xsl:variable> 
        
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="modifTitre">
                <xsl:with-param name="prefixe" select="$prefixe" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template pour calculer par récurrence le numéro d'une séquence</xd:p>
        </xd:desc>
    </xd:doc>    
    <xsl:template match="d:Sequence" mode="calculateNumber">
        <xsl:variable name="niveau" select="d:TypeOfSequence"/>
        <xsl:variable name="styleSeq" select="$style/titre/sequence/niveau[@nom=$niveau]"/>
        <xsl:variable name="niveauParent" select="$style/titre/sequence/niveau[following-sibling::niveau[1]/@nom=$niveau]/@nom"/>
        
        <xsl:variable name="numero">
            <xsl:if test="not(index-of($seqSansNumero,r:ID)>0)">
                <xsl:number count="d:Sequence[d:TypeOfSequence/text()=$niveau and not(index-of($seqSansNumero,r:ID)>0)]" 
                    level="any" format="{$styleSeq/styleNumSeq}" from="d:Sequence[d:TypeOfSequence/text()=$niveauParent]"/>
            </xsl:if>
        </xsl:variable>
        <xsl:variable name="numeroParent">
            <xsl:if test="$styleSeq/numParent !='N'">
                <xsl:apply-templates select="ancestor::d:Sequence[d:TypeOfSequence/text()=$niveauParent]" mode="calculateNumber"/>
            </xsl:if>
        </xsl:variable>
        
        <xsl:if test="$numero!=''">
            <xsl:if test="$numeroParent!=''">
                <xsl:value-of select="concat($numeroParent,$styleSeq/postNumParentSeq)"/>
            </xsl:if>
            <xsl:value-of select="$numero"/>
        </xsl:if>
    </xsl:template>    
    
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Fonction pour identifier si 2 listes ont des éléments communs</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="iatddi:isCommon">
        <xsl:param name="list1"/>
        <xsl:param name="list2"/>
        <xsl:variable name="isCommon">
            <xsl:choose>
                <xsl:when test="empty($list2[1])">false</xsl:when>
                <!--<xsl:when test="contains($list1,$list2[1])">true</xsl:when>-->
                <xsl:when test="index-of($list1,$list2[1])>0">true</xsl:when>
                <xsl:when test="empty($list2[2])">false</xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="iatddi:isCommon($list1,$list2[position()>1])"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$isCommon"/>
    </xsl:function>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Fonction pour identifier si 2 listes ont des éléments communs</xd:p>
        </xd:desc>
    </xd:doc>
    <!-- Permet de vérifier si une question est une "sous-question", c'est-à-dire qu'elle dépend d'un filtre ayant les caractéristiques suivantes :
            - S'appuie sur la question immédiatement précédente
            - n'est pas le dernier élément de la séquence à laquelle il appartient 
    Elle peut dépendre d'un ou plusieurs filtres de ce type ; on vérifie juste s'il y en a 0 -->
    <xsl:function name="iatddi:isSubQuestion">
        <xsl:param name="context"/>
        <xsl:param name="niveauSeq"/>
        
        <!-- Prend le Module, Paragraphe ou Groupe (selon $niveauSeq) auquel la question appartient pour ne prendre que les filtres utiles -->
        <xsl:variable name="ancetres">
            <xsl:copy-of select="root($context)//d:Sequence[d:TypeOfSequence=$niveauSeq and descendant::d:QuestionConstruct=$context]"/>
        </xsl:variable>
        <xsl:value-of select="count($ancetres//d:ControlConstructReference
            [d:IfThenElse//d:TypeOfSequence[text()='Cachable']
            and descendant::d:QuestionConstruct[r:ID=$context/r:ID]
            and (following-sibling::d:ControlConstructReference[d:IfThenElse or d:QuestionConstruct]
            or index-of($filtreSansNumero,d:IfThenElse/r:ID)>0)
            and preceding-sibling::d:ControlConstructReference[d:QuestionConstruct]]
            [iatddi:isCommon(preceding-sibling::d:ControlConstructReference[d:QuestionConstruct][1]//r:TargetParameterReference/r:ID,
            d:IfThenElse/d:IfCondition//r:SourceParameterReference/r:ID)])"/>
    </xsl:function>

    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl">
        <xd:desc>
            <xd:p>Template pour rajouter le code devant les labels des listes déroulantes et des en-tête de tableaux</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="l:Code[ancestor::d:GridDimension[@displayCode='true' and @displayLabel='true']]
        /r:CategoryReference/l:Category/r:Label/r:Content">
        <xsl:variable name="prefixe">
            <xsl:value-of select="concat(../../../../r:Value,' - ')"/>
        </xsl:variable>
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="modifTitre">
                <xsl:with-param name="prefixe" select="$prefixe" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xhtml:p" mode="modifTitre" priority="2">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()[position()=1]" mode="modifTitre"/>
            <xsl:apply-templates select="node()[not(position()=1)]"/>
        </xsl:copy>
    </xsl:template>

    <!-- Quand on tombe sur un noeud qui commence par xhtml, on ne vas traiter que le premier noeud enfant en mode modification du titre -->
    <xsl:template match="*[starts-with(name(),'xhtml')]" mode="modifTitre">
        <xsl:param name="prefixe" tunnel="yes"/>
        <xsl:value-of select="$prefixe"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:span[@class='bloc']" mode="modifTitre" priority="2">
        <xsl:param name="prefixe" tunnel="yes"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:value-of select="$prefixe"/>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- On rajoute le préfixe -->
    <xsl:template match="text()" mode="modifTitre" priority="1">
        <xsl:param name="prefixe" tunnel="yes"/>
        <xsl:value-of select="concat($prefixe,.)"/>
    </xsl:template>

</xsl:transform>
