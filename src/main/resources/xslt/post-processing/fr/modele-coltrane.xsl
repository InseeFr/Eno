<?xml version="1.0" encoding='utf-8'?>
<xsl:transform version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:xxf="http://orbeon.org/oxf/xml/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <xsl:output method="xml" encoding="utf-8"/>

    <!-- On passe l'instance, et par conséquence d'autres éléments du form au modèle Coltrane -->

    <!-- Fichier de mapping -->
    <xsl:param name="fichier-mapping"/>
    <xsl:param name="fichier-mapping-node" as="node()" required="no"/>

    <xsl:variable name="list">
        <xsl:choose>
            <xsl:when test="$fichier-mapping-node/*">
                <mapping>
                    <xsl:for-each select="$fichier-mapping-node//mapping/*">
                        <xsl:sort select="string-length(@name)" order="descending"/>
                        <xsl:sort select="name()" order="ascending"/>
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </mapping>
            </xsl:when>
            <xsl:otherwise>
                <mapping>
                    <xsl:for-each select="doc($fichier-mapping)//mapping/*">
                        <xsl:sort select="string-length(@name)" order="descending"/>
                        <xsl:sort select="name()" order="ascending"/>
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </mapping>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="list-length" select="count($list/*)"/>

    <xsl:template match="/">
        <xsl:copy>
            <xsl:apply-templates select="*"/>
        </xsl:copy>
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

    <xsl:template match="xf:instance[@id='fr-form-instance' or @id='fr-form-loop-model']//*">
        <xsl:choose>
            <xsl:when test="name() = $list//Variable/@name">
                <Variable idVariable="{name()}"/>
            </xsl:when>
            <xsl:when test="name() = $list//Group/@name and (parent::LoopModels or parent::*[ends-with(name(),'Container')])">
                <Groupe typeGroupe="{name()}">
                    <xsl:apply-templates select="node() | @*"/>
                </Groupe>
            </xsl:when>
            <xsl:when test="ends-with(name(),'-Container') and substring-before(name(),'-Container') = $list//Group/@name">
                <Groupe idGroupe="{substring-before(name(),'-Container')}">
                    <xsl:apply-templates select="node() | @*"/>
                </Groupe>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="node() | @*"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*/@nodeset | */@relevant | */@readonly | */@calculate | xf:action/@if | xf:action/@iterate | xf:action/@while | xf:setvalue/@* | xf:constraint/@value | xf:insert/@*">
        <xsl:attribute name="{name()}">
            <xsl:call-template name="replace-element">
                <xsl:with-param name="position" as="xs:integer" select="1"/>
                <xsl:with-param name="text" select="."/>
            </xsl:call-template>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xf:label/@ref | xf:alert/@ref">
        <xsl:attribute name="{name()}">
            <xsl:choose>
                <xsl:when test="starts-with(.,'replace')">
                    <xsl:value-of select="concat(substring-before(.,','),',')"/>
                    <xsl:call-template name="replace-element">
                        <xsl:with-param name="position" as="xs:integer" select="1"/>
                        <xsl:with-param name="text" select="substring-after(.,',')"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xf:bind[@id='pages-bind']/xf:bind[contains(@id,'-Container')]/@ref" priority="2">
        <xsl:attribute name="ref">
            <xsl:call-template name="replace-element">
                <xsl:with-param name="position" as="xs:integer" select="1"/>
                <xsl:with-param name="text" select="."/>
            </xsl:call-template>
        </xsl:attribute>
    </xsl:template>
    
    <xsl:template match="xf:bind/@ref">
        <xsl:attribute name="ref">
            <xsl:choose>
                <xsl:when test=". = $list//Variable/@name">
                    <xsl:value-of select="concat('Variable[@idVariable=''',.,''']')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="replace-element">
        <xsl:param name="position" as="xs:integer"/>
        <xsl:param name="text"/>

        <xsl:choose>
            <xsl:when test="$list//*[$position]/name()='Variable'">
                <xsl:variable name="current-variable" select="$list//*[$position]/@name"/>
                <xsl:choose>
                    <xsl:when test="contains($text,$current-variable)">
                        <xsl:choose>
                            <xsl:when test="contains($text,concat($current-variable,'-layout-'))">
                                <xsl:for-each select="tokenize($text,concat($current-variable,'-layout-'))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat($current-variable,'-layout-')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('descendant::',$current-variable))">
                                <xsl:for-each select="tokenize($text,concat('descendant::',$current-variable))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('descendant::Variable[@idVariable=''',$current-variable,''']')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('/',$current-variable))">
                                <xsl:for-each select="tokenize($text,concat('/',$current-variable))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('/Variable[@idVariable=''',$current-variable,''']')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:for-each select="tokenize($text,$current-variable)">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="$current-variable"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position +1"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="replace-element">
                            <xsl:with-param name="position" select="$position +1"/>
                            <xsl:with-param name="text" select="$text"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$list//*[$position]/name()='Group'">
                <xsl:variable name="current-group" select="$list//*[$position]/@name"/>
                <xsl:choose>
                    <xsl:when test="contains($text,$current-group)">
                        <xsl:choose>
                            <xsl:when test="contains($text,concat($current-group,'-Container/',$current-group))">
                                <xsl:for-each select="tokenize($text,concat($current-group,'-Container/',$current-group))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('Groupe[@idGroupe=''',$current-group,''']/Groupe[@typeGroupe=''',$current-group,''']')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('//',$current-group,'[$',$current-group,'-position]'))">
                                <xsl:for-each select="tokenize($text,concat('//',$current-group,'\[\$',$current-group,'-position\]'))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('//Groupe[@typeGroupe=''',$current-group,'''][$',$current-group,'-position]')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('*[name()=''',$current-group,''''))">
                                <xsl:for-each select="tokenize($text,concat('\*\[name\(\)=''',$current-group,''''))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('Groupe[@typeGroupe=''',$current-group,'''')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('/',$current-group,'-Container'))">
                                <xsl:for-each select="tokenize($text,concat('/',$current-group,'-Container'))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('/Groupe[@idGroupe=''',$current-group,''']')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('$',$current-group,'-position'))">
                                <xsl:for-each select="tokenize($text,concat('\$',$current-group,'-position'))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('$',$current-group,'-position')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat($current-group,'-position'))">
                                <xsl:for-each select="tokenize($text,concat($current-group,'-position'))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat($current-group,'-position')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat($current-group,'-AddLine'))">
                                <xsl:for-each select="tokenize($text,concat($current-group,'-AddLine'))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat($current-group,'-AddLine')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat($current-group,'-Count'))">
                                <xsl:for-each select="tokenize($text,concat($current-group,'-Count'))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat($current-group,'-Count')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('CurrentLoopElement[@loop-name=''',$current-group,''']'))">
                                <xsl:for-each select="tokenize($text,concat('CurrentLoopElement\[@loop-name=''',$current-group,'''\]'))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('CurrentLoopElement[@loop-name=''',$current-group,''']')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('descendant::',$current-group))">
                                <xsl:for-each select="tokenize($text,concat('descendant::',$current-group))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('descendant::Groupe[@typeGroupe=''',$current-group,''']')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('ancestor::',$current-group))">
                                <xsl:for-each select="tokenize($text,concat('ancestor::',$current-group))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('ancestor::Groupe[@typeGroupe=''',$current-group,''']')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:when test="contains($text,concat('/',$current-group))">
                                <xsl:for-each select="tokenize($text,concat('/',$current-group))">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('/Groupe[@typeGroupe=''',$current-group,''']')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="'toto'"/>
                                <xsl:for-each select="tokenize($text,$current-group)">
                                    <xsl:if test="not(position()=1)">
                                        <xsl:value-of select="concat('toto',$current-group,'toto')"/>
                                    </xsl:if>
                                    <xsl:call-template name="replace-element">
                                        <xsl:with-param name="position" select="$position"/>
                                        <xsl:with-param name="text" select="current()"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="replace-element">
                            <xsl:with-param name="position" select="$position +1"/>
                            <xsl:with-param name="text" select="$text"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:transform>
