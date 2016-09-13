<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:il="http://xml/insee.fr/xslt/lib"
    xmlns:iatxml="http://xml/insee.fr/xslt/apply-templates/xml"
    xmlns:iatxsl="http://xml/insee.fr/xslt/apply-templates/xsl"
    exclude-result-prefixes="xs xd" version="2.0">

    <xsl:import href="../entrees/xml/source.xsl"/>
    <xsl:import href="../sorties/xsl/models.xsl"/>
    <xsl:import href="../lib.xsl"/>

    <xsl:output method="xml" indent="yes"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Jan 7, 2013</xd:p>
            <xd:p>Transforms XML into XSL!</xd:p>
        </xd:desc>
    </xd:doc>

    <xsl:template match="/">
        <xsl:apply-templates select="/" mode="source"/>
    </xsl:template>

    <xd:desc>
        <xd:p>A l'élément racine du fichier xml, on associe une feuille xsl</xd:p>
    </xd:desc>
    <xsl:template match="racine" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('feuille',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A l'élément elementGenerique où un xpath est renseigné, et un driver associé, on associe le driver Driver (et oui :-))</xd:p>
        <xd:p>Il s'agit du cas où on associe à un élément en entrée un driver de sortie</xd:p>
    </xd:desc>
    <xsl:template match="elementGenerique[elementDefini[@nom='Xpath']/text()!='' and elementDefini[@nom='Driver']]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('template',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A l'élément elementGenerique où un xpath est renseigné, et où un match est renseigné, mais sans mode, on associe le driver implementation simple</xd:p>
        <xd:p>Il s'agit du cas où on implémente une fonction pour un élément source donné, et qu'on renvoie juste la value d'un élément</xd:p>
    </xd:desc>
    <xsl:template match="elementGenerique[elementDefini[@nom='Xpath']/text()!='' and elementDefini[@nom='Match']/text()!='' and not(elementDefini[@nom='Match_Mode']/text()!='')]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('implementation_simple',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A l'élément elementGenerique où un xpath est renseigné, et où un match est renseigné, avec un  mode, on associe le driver implementation complexe</xd:p>
        <xd:p>Il s'agit du cas où on implémente une fonction pour un élément source donné, et qu'on renvoie quelque chose de plus complexe (à l'aide d'un mode)</xd:p>
    </xd:desc>
    <xsl:template match="elementGenerique[elementDefini[@nom='Xpath']/text()!='' and elementDefini[@nom='Match']/text()!='' and elementDefini[@nom='Match_Mode']/text()!='']" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('implementation_complexe',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A l'élément elementGenerique où un xpath est renseigné, et ni driver ni match n'est renseigné, on associe le driver implementation_vide</xd:p>
        <xd:p>Il s'agit du cas où on implémente une fonction pour un élément source donné, et qu'on souhaite que cela ne renvoie rien</xd:p>
    </xd:desc>
    <xsl:template match="elementGenerique[elementDefini[@nom='Xpath']/text()!='' and not(elementDefini[@nom='Driver'] or elementDefini[@nom='Match']/text()!='')]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('implementation_vide',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A l'élément elementGenerique où une fonction est renseignée, on associe le driver fonction</xd:p>
    </xd:desc>
    <xsl:template match="elementGenerique[elementDefini[@nom='Out_Function']/text()!='' and elementDefini[@nom='In_Function']/text()]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('fonctionPassage',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A l'élément elementGenerique où une fonction n'est pas renseignée, on associe le driver fonctionNonSupportee</xd:p>
    </xd:desc>
    <xsl:template match="elementGenerique[elementDefini[@nom='Out_Function']/text()!='' and not(elementDefini[@nom='In_Function']/text())]" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:message>Je ne suis pas supporté</xsl:message>
        <xsl:apply-templates select="il:append-empty-element('fonctionNonSupportee',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A l'élément elementGenerique où une fonction est renseignée, on associe le driver fonction</xd:p>
    </xd:desc>
    <xsl:template match="elementGenerique[elementDefini[@nom='Fonction']/text()!='']" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('fonctionSource',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A l'élément elementGenerique où un parent est renseigné, on associe le driver recuperationEnfants</xd:p>
    </xd:desc>
    <xsl:template match="elementGenerique[elementDefini[@nom='Parent']/text()!='']" mode="source">
        <xsl:param name="driver" tunnel="yes">
            <driver/>
        </xsl:param>
        <xsl:apply-templates select="il:append-empty-element('recuperationEnfants',$driver)" mode="model">
            <xsl:with-param name="source-context" select="." tunnel="yes"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de documentation, on associe une fonction d'envoi de documentation</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-documentation">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-documentation"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-documentation">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Documentation'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de xpath, on renvoie la valeur associée à l'élément de nom Xpath</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-xpath">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-xpath"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-xpath">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Xpath'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de mode xpath, on renvoie la valeur associée à l'élément de nom Mode_Xpath</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-mode-xpath">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-mode-xpath"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-mode-xpath">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Xpath_Mode'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de match, on renvoie la valeur associée à l'élément de nom Match</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-match">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-match"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-match">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Match'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de mode du match, on renvoie la valeur associée à l'élément de nom Mode_Match</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-mode-match">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-mode-match"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-mode-match">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Match_Mode'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de fonction, on renvoie la valeur associée à l'élément de nom Fonction</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-fonction">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-fonction"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-fonction">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Fonction'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de fonction-sortie, on renvoie la valeur associée à l'élément de nom Fonction_Sortie</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-fonction-sortie">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-fonction-sortie"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-fonction-sortie">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Out_Function'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de fonction-entree, on renvoie la valeur associée à l'élément de nom Fonction_Entree</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-fonction-entree">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-fonction-entree"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-fonction-entree">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='In_Function'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de driver, on renvoie la valeur associée à l'élément de nom Driver</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-driver">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-driver"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-driver">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Driver'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande de fonctionAssociee, on renvoie la valeur associée à l'élément de nom Valeur</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-parameters" as="xs:string *">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-parametres"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-parametres">
        <xsl:call-template name="split">
            <xsl:with-param name="chaine" select="iatxml:get-valeur(./elementDefini[@nom='Parameters'])"/>
        </xsl:call-template>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande du parent, on renvoie la valeur associée à l'élément de nom Parent</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-parent">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-parent"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-parent">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Parent'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande du type du retour d'une fonction (as), on renvoie la valeur associée à l'élément de nom As</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-as">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-as"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-as">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='As'])"/>
    </xsl:template>
    
    <xd:desc>
        <xd:p>A la fonction de demande des enfants, on renvoie la valeur associée à l'élément de nom Enfants</xd:p>
    </xd:desc>
    <xsl:function name="iatxsl:get-enfants">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="iatxml:get-enfants"/>
    </xsl:function>
    
    <xsl:template match="elementGenerique" mode="iatxml:get-enfants">
        <xsl:value-of select="iatxml:get-valeur(./elementDefini[@nom='Children'])"/>
    </xsl:template>
    
    <xsl:template name="split">
        <xsl:param name="chaine"/>
        <xsl:choose>
            <xsl:when test="contains($chaine,',')">
                <xsl:value-of select="substring-before($chaine,',')"/>
                <xsl:call-template name="split">
                    <xsl:with-param name="chaine" select="substring-after($chaine,',')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$chaine"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>
