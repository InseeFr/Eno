<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:pogues="http://xml.insee.fr/schema/applis/pogues"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enofr="http://xml.insee.fr/apps/eno/form-runner"
    xmlns:enoddi2fr="http://xml.insee.fr/apps/eno/ddi2form-runner"
    xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:l="ddi:logicalproduct:3_3"
    xmlns:enoddi33="http://xml.insee.fr/apps/eno/out/ddi33"
    xmlns:enopogues="http://xml.insee.fr/apps/eno/in/pogues-xml"
    version="2.0">

    <!-- Importing the different resources -->
    <xsl:import href="../../inputs/pogues-xml/source.xsl"/>
    <xsl:import href="../../outputs/ddi/models.xsl"/>
    <xsl:import href="../../lib.xsl"/>
    
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This stylesheet is used to transform a DDI input into an Xforms form (containing orbeon form runner adherences).</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:strip-space elements="*"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The parameter file used by the stylesheet.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="parameters-file"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The parameters are charged as an xml tree.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="parameters" select="doc($parameters-file)"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>Root template :</xd:p>
            <xd:p>The transformation starts with the main Sequence.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="/pogues:Questionnaire" mode="source"/>
    </xsl:template>
    
    <!--TODO The implementation of retrieving reference id (aka qc-id,cc-id...) should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:get-reference-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-reference-id"/>
    </xsl:function>
    
    <xsl:template match="*[enopogues:is-question(.) = 'true']" mode="enoddi33:get-reference-id">
        <xsl:value-of select="enoddi33:get-qc-id(.)"/>
    </xsl:template>
    
    <xsl:template match="*[enopogues:is-sequence(.) = 'true']" mode="enoddi33:get-reference-id">
        <xsl:value-of select="enoddi33:get-id(.)"/>
    </xsl:template>
    
    <xsl:template match="pogues:Control" mode="enoddi33:get-reference-id">
        <xsl:value-of select="enoddi33:get-id(.)"/>
    </xsl:template>
    
    <xsl:template match="pogues:Response[@mandatory='true']" mode="enoddi33:get-reference-id">
        <xsl:value-of select="enoddi33:get-ci-id(.)"/>
    </xsl:template>
    
    
    <!--TODO The implementation of retrieving reference element name (aka Sequence,QuestionConstruct...) should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:get-reference-element-name">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-reference-element-name"/>
    </xsl:function>
    
    <xsl:template match="*[enopogues:is-question(.) = 'true']" mode="enoddi33:get-reference-element-name">
        <xsl:value-of select="'QuestionConstruct'"/>
    </xsl:template>
    
    <xsl:template match="*[enopogues:is-sequence(.) = 'true']" mode="enoddi33:get-reference-element-name">
        <xsl:choose>
            <xsl:when test="local-name(.)='IfThenElse'">                                   
                <xsl:value-of select="'IfThenElse'"/>
            </xsl:when>
            <xsl:when test="local-name(.)='Child' or pogues:Child or pogues:IfThenElse/descendant::pogues:Child">
                <xsl:value-of select="'Sequence'"/>                
            </xsl:when>
            <xsl:otherwise>
                <xsl:message select="concat('enoddi33:get-reference-element-name does not explicitly support ',name(.))"/>
                <xsl:value-of select="local-name(.)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="pogues:Control | pogues:Response[@mandatory='true']" mode="enoddi33:get-reference-element-name">
        <xsl:value-of select="'ComputationItem'"/>
    </xsl:template>
    
    <xsl:template match="pogues:IfThenElse" mode="enoddi33:get-reference-element-name">
        <xsl:value-of select="local-name(.)"/>
    </xsl:template>        
    
    <!-- Building Id Implementation, should be done through rich out-getters in @v2.0 -->
    <!-- **************************************************************************** -->
    
    <!-- Id for QuestionConstruct (QC) -->
    <xsl:function name="enoddi33:get-qc-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-qc-id"/>
    </xsl:function>
    
    <xsl:template match="*[enopogues:is-question(.)]" mode="enoddi33:get-qc-id">
        <xsl:value-of select="concat(enopogues:get-id(.),'-QC')"/>
    </xsl:template>
    
    <!-- Id for Then Sequence in IfThenElse -->
    <xsl:function name="enoddi33:get-then-sequence-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-then-sequence-id"/>
    </xsl:function>
    
    <!-- TODO : Reduce the XPath, * is needed because of a bug on the namespace for outputted pogues:IfThenElse-->
    <xsl:template match="*" mode="enoddi33:get-then-sequence-id">
        <xsl:value-of select="concat(enoddi33:get-id(.),'-THEN')"/>
    </xsl:template>
    
    <!-- Id for ResponseDomain OutParameter (rdop) -->
    <xsl:function name="enoddi33:get-rdop-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-rdop-id"/>
    </xsl:function>
    
    <xsl:template match="pogues:Response" mode="enoddi33:get-rdop-id">
        <xsl:value-of select="concat(parent::*/@id,'-RDOP-',@id)"/>
    </xsl:template>
    
    <xsl:template match="pogues:Datatype" mode="enoddi33:get-rdop-id">
        <xsl:value-of select="enoddi33:get-rdop-id(parent::*)"/>
    </xsl:template>
    
    <xsl:function name="enoddi33:get-si-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-si-id"/>
    </xsl:function>
    
    <xsl:template match="pogues:Declaration" mode="enoddi33:get-si-id">
        <xsl:value-of select="concat(enoddi33:get-id(.),'-SI')"/>
    </xsl:template>
    
    <!--TODO The implementation of building id should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:get-vrop-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-vrop-id"/>
    </xsl:function>
    
    <xsl:template match="pogues:Formula" mode="enoddi33:get-vrop-id">
        <xsl:value-of select="concat(enoddi33:get-id(parent::pogues:Variable),'-VROP')"/>
    </xsl:template>
    
    <!--TODO The implementation of building id should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:get-gi-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-gi-id"/>
    </xsl:function>
    
    <xsl:template match="pogues:Variable[@xsi:type='CalculatedVariableType']" mode="enoddi33:get-gi-id">
        <xsl:value-of select="concat(enoddi33:get-id(.),'-GI')"/>
    </xsl:template>
    
    <xsl:template match="pogues:Formula" mode="enoddi33:get-gi-id">
        <xsl:apply-templates select="parent::pogues:Variable" mode="enoddi33:get-gi-id"/>
    </xsl:template>
    
    <!--TODO The implementation building id should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:get-main-sequence-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-main-sequence-id"/>
    </xsl:function>
    
    <xsl:template match="*" mode="enoddi33:get-main-sequence-id">
        <xsl:value-of select="concat('Sequence-',enopogues:get-questionnaire-id(.))"/>
    </xsl:template>
    
    <!--TODO The implementation building id should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:get-referenced-sequence-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-referenced-sequence-id"/>
    </xsl:function>
    
    <xsl:template match="pogues:Formula" mode="enoddi33:get-referenced-sequence-id">
        <xsl:value-of select="enoddi33:get-main-sequence-id(.)"/>
    </xsl:template>
    
    <!--TODO The implementation of ComputationItem type should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:get-ci-type">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-ci-type"/>
    </xsl:function>
    
    <xsl:template match="pogues:Control" mode="enoddi33:get-ci-type">
        <xsl:value-of select="enoddi33:get-type(.)"/>
    </xsl:template>
    
    <xsl:template match="pogues:Response[@mandatory='true']" mode="enoddi33:get-ci-type">
        <xsl:value-of select="'mandatory'"/>
    </xsl:template>
    
    <!--TODO The implementation of ComputationItem type should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:get-ci-name">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-ci-name"/>
    </xsl:function>
    
    <xsl:template match="pogues:Control" mode="enoddi33:get-ci-name">
        <xsl:value-of select="enoddi33:get-description(.)"/>
    </xsl:template>
    
    <xsl:template match="pogues:Response[@mandatory='true']" mode="enoddi33:get-ci-name">
        <xsl:value-of select="concat('ComputationItem for the mandatory question ',enoddi33:get-id(.))"/>
    </xsl:template>
    
    <!--TODO The implementation of Parameter Reference for conditional text should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:get-qop-conditional-text-id">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:get-qop-conditional-text-id"/>
    </xsl:function>
    
    <xsl:template match="pogues:Control[pogues:FailMessage]" mode="enoddi33:get-qop-conditional-text-id">
        <xsl:value-of select="enoddi33:get-qop-id(pogues:FailMessage)"/>
    </xsl:template>          
    
    <xsl:template match="pogues:Declaration" mode="enoddi33:get-qop-conditional-text-id">
        <xsl:value-of select="enoddi33:get-qop-id(pogues:Text)"/>
    </xsl:template>
    
    <!--TODO The implementation of Parameter Reference for conditional text should be done with "rich" outGetter mechanism =>@v2.0 -->
    <xsl:function name="enoddi33:is-with-conditionnal-text">
        <xsl:param name="context" as="item()"/>
        <xsl:apply-templates select="$context" mode="enoddi33:is-with-conditionnal-text"/>
    </xsl:function>
    
    <xsl:template match="*[pogues:Text or pogues:FailMessage]" mode="enoddi33:is-with-conditionnal-text">
        <xsl:value-of select="enoddi33:is-with-conditionnal-text(*[self::pogues:Text or self::pogues:FailMessage])"/>
    </xsl:template>    

    <!-- ConditionnalText are required when dynamic text with at least one non external variable. -->
    <xsl:template match="pogues:Text | pogues:FailMessage" mode="enoddi33:is-with-conditionnal-text">
        <xsl:choose>
            <xsl:when test="enopogues:is-with-dynamic-text(.) = true()">
                <xsl:variable name="variables" select="enopogues:get-related-variable(.)"/>
                <xsl:value-of select="some $x in $variables satisfies enopogues:get-type($x) = ('CollectedVariableType','CalculatedVariableType')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="*" mode="enoddi33:is-with-conditionnal-text">
        <xsl:apply-templates select="." mode="enopogues:is-with-dynamic-text"/>
    </xsl:template>
    
    
</xsl:stylesheet>
