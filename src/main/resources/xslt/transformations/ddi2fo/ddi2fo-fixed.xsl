<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" xmlns:eno="http://xml.insee.fr/apps/eno"
    xmlns:enoddi="http://xml.insee.fr/apps/eno/ddi"
    xmlns:enofo="http://xml.insee.fr/apps/eno/out/fo"
    xmlns:enoddi2fo="http://xml.insee.fr/apps/eno/ddi2fo"
    xmlns:d="ddi:datacollection:3_3"
    xmlns:r="ddi:reusable:3_3" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:l="ddi:logicalproduct:3_3" version="2.0">

    <!-- Importing the different resources -->
    <xsl:import href="../../inputs/ddi/source.xsl"/>
    <xsl:import href="../../outputs/fo/models.xsl"/>
    <xsl:import href="../../lib.xsl"/>

    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>This stylesheet is used to transform a DDI input into an Xforms form (containing orbeon form runner adherences).</xd:p>
        </xd:desc>
    </xd:doc>

    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <!--<xsl:strip-space elements="*"/>-->

    <xd:doc>
        <xd:desc>
            <xd:p>The folder containing label resources in different languages.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="labels-folder"/>

    <xd:doc>
        <xd:desc>
            <xd:p>The properties file used by the stylesheet.</xd:p>
            <xd:p>It's on a transformation level.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:param name="properties-file"/>
    <xsl:param name="parameters-file"/>
    <xsl:param name="parameters-node" as="node()" required="no">
        <empty/>
    </xsl:param>
    
    <xsl:variable name="page-model-default" select="doc('../../../xslt/post-processing/fo/page-model/page-model-default.fo')"/>
    
    <xd:doc>
        <xd:desc>
            <xd:p>A variable is created to build a set of label resources in different languages.</xd:p>
            <xd:p>Only the resources in languages already present in the DDI input are charged.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="labels-resource">
        <xsl:sequence select="eno:build-labels-resource($labels-folder,enofo:get-form-languages(//d:Sequence[d:TypeOfSequence/text()='template']))"/>
    </xsl:variable>
    
    <xd:doc>
        <xd:desc>
            <xd:p>The properties and parameters files are charged as xml trees.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="properties" select="doc($properties-file)"/>
    <xsl:variable name="parameters">
        <xsl:choose>
            <xsl:when test="$parameters-node/*">
                <xsl:copy-of select="$parameters-node"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="doc($parameters-file)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xd:doc>
        <xd:desc>Variables from propertiers and parameters</xd:desc>
    </xd:doc>
    <xsl:variable name="context">
        <xsl:choose>
            <xsl:when test="$parameters//Context != ''">
                <xsl:value-of select="$parameters//Context"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//Context"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="orientation">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Format/Orientation != ''">
                <xsl:value-of select="$parameters//fo-parameters/Format/Orientation"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Format/Orientation"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="column-count">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Format/Columns != ''">
                <xsl:value-of select="$parameters//fo-parameters/Format/Columns"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Format/Columns"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="roster-minimum-empty-row" as="xs:integer">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Roster/Row/MinimumEmpty != ''">
                <xsl:value-of select="$parameters//fo-parameters/Roster/Row/MinimumEmpty"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Roster/Row/MinimumEmpty"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="roster-defaultsize" as="xs:integer">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Roster/Row/DefaultSize != ''">
                <xsl:value-of select="$parameters//fo-parameters/Roster/Row/DefaultSize"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Roster/Row/DefaultSize"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="loop-default-occurrence" as="xs:integer">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Loop/DefaultOccurrence != ''">
                <xsl:value-of select="$parameters//fo-parameters/Loop/DefaultOccurrence"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Loop/DefaultOccurrence"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="loop-minimum-empty-occurrence" as="xs:integer">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Loop/MinimumEmptyOccurrence != ''">
                <xsl:value-of select="$parameters//fo-parameters/Loop/MinimumEmptyOccurrence"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Loop/MinimumEmptyOccurrence"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="table-defaultsize">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Table/Row/DefaultSize != ''">
                <xsl:value-of select="$parameters//fo-parameters/Table/Row/DefaultSize"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Table/Row/DefaultSize"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="textarea-defaultsize">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/TextArea/Row/DefaultSize != ''">
                <xsl:value-of select="$parameters//fo-parameters/TextArea/Row/DefaultSize"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/TextArea/Row/DefaultSize"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="images-folder">
        <xsl:choose>
            <xsl:when test="$parameters//Images/Folder != ''">
                <xsl:value-of select="$parameters//Images/Folder"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//Images/Folder"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="numeric-capture">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/Capture/Numeric != ''">
                <xsl:value-of select="$parameters//fo-parameters/Capture/Numeric"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/Capture/Numeric"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="page-break-between">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/PageBreakBetween/pdf != ''">
                <xsl:value-of select="$parameters//fo-parameters/PageBreakBetween/pdf"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/PageBreakBetween/pdf"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="initialize-all-variables">
        <xsl:choose>
            <xsl:when test="$parameters//fo-parameters/InitializeAllVariables  != ''">
                <xsl:value-of select="$parameters//fo-parameters/InitializeAllVariables "/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$properties//fo-parameters/InitializeAllVariables "/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    
    <!--New style variables... LONG-->
   <xsl:variable name="Titre-sequence-background-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/background-color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/background-color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/background-color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-sequence-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-sequence-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-sequence-margin-bottom">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/margin-bottom != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/margin-bottom"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/margin-bottom"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-sequence-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-sequence-border-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/border-color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/border-color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/border-color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-sequence-border-style">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/border-style != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/border-style"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/border-style"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-sequence-space-before">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/space-before != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/space-before"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/space-before"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-sequence-space-before.conditionality">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/space-before.conditionality != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/space-before.conditionality"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/space-before.conditionality"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-sequence-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-sequence/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-sequence/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-sequence/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-paragraphe-background-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-paragraphe/background-color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-paragraphe/background-color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-paragraphe/background-color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-paragraphe-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-paragraphe/color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-paragraphe/color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-paragraphe/color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-paragraphe-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-paragraphe/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-paragraphe/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-paragraphe/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-paragraphe-margin-bottom">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-paragraphe/margin-bottom != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-paragraphe/margin-bottom"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-paragraphe/margin-bottom"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-paragraphe-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-paragraphe/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-paragraphe/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-paragraphe/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-paragraphe-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-paragraphe/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-paragraphe/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-paragraphe/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-paragraphe-space-before">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-paragraphe/space-before != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-paragraphe/space-before"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-paragraphe/space-before"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Titre-paragraphe-space-before.conditionality">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Titre-paragraphe/space-before.conditionality != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Titre-paragraphe/space-before.conditionality"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Titre-paragraphe/space-before.conditionality"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="general-style-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/general-style/color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/general-style/color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/general-style/color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="general-style-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/general-style/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/general-style/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/general-style/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="general-style-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/general-style/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/general-style/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/general-style/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="general-style-padding">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/general-style/padding != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/general-style/padding"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/general-style/padding"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="general-style-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/general-style/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/general-style/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/general-style/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-cell-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-cell/color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-cell/color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-cell/color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-cell-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-cell/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-cell/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-cell/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-cell-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-cell/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-cell/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-cell/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-cell-padding">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-cell/padding != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-cell/padding"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-cell/padding"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-question-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-question/color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-question/color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-question/color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-question-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-question/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-question/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-question/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-question-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-question/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-question/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-question/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-question-margin-top">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-question/margin-top != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-question/margin-top"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-question/margin-top"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-question-margin-bottom">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-question/margin-bottom != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-question/margin-bottom"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-question/margin-bottom"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="label-question-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/label-question/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/label-question/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/label-question/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="entete-ligne-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/entete-ligne/color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/entete-ligne/color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/entete-ligne/color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="entete-ligne-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/entete-ligne/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/entete-ligne/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/entete-ligne/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="entete-ligne-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/entete-ligne/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/entete-ligne/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/entete-ligne/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="entete-ligne-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/entete-ligne/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/entete-ligne/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/entete-ligne/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="colonne-tableau-border-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/colonne-tableau/border-color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/colonne-tableau/border-color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/colonne-tableau/border-color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="colonne-tableau-border-style">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/colonne-tableau/border-style != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/colonne-tableau/border-style"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/colonne-tableau/border-style"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="colonne-tableau-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/colonne-tableau/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/colonne-tableau/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/colonne-tableau/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="colonne-tableau-padding-left">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/colonne-tableau/padding-left != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/colonne-tableau/padding-left"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/colonne-tableau/padding-left"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="colonne-tableau-padding-right">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/colonne-tableau/padding-right != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/colonne-tableau/padding-right"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/colonne-tableau/padding-right"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="data-cell-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/data-cell/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/data-cell/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/data-cell/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="data-cell-border-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/data-cell/border-color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/data-cell/border-color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/data-cell/border-color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="data-cell-border-style">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/data-cell/border-style != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/data-cell/border-style"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/data-cell/border-style"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="data-cell-padding">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/data-cell/padding != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/data-cell/padding"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/data-cell/padding"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Line-drawing-width">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Line-drawing/width != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Line-drawing/width"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Line-drawing/width"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Line-drawing-height">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Line-drawing/height != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Line-drawing/height"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Line-drawing/height"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Line-drawing-position">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Line-drawing/position != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Line-drawing/position"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Line-drawing/position"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Line-drawing-border-bottom">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Line-drawing/border-bottom != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Line-drawing/border-bottom"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Line-drawing/border-bottom"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Line-drawing-Garde-position">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Line-drawing-Garde/position != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Line-drawing-Garde/position"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Line-drawing-Garde/position"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="Line-drawing-Garde-border-bottom">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/Line-drawing-Garde/border-bottom != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/Line-drawing-Garde/border-bottom"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/Line-drawing-Garde/border-bottom"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-block-space-before">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-block/space-before != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-block/space-before"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-block/space-before"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-block-space-after">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-block/space-after != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-block/space-after"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-block/space-after"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-block-start-indent">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-block/start-indent != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-block/start-indent"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-block/start-indent"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-block-end-indent">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-block/end-indent != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-block/end-indent"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-block/end-indent"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-block-background-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-block/background-color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-block/background-color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-block/background-color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-inline-container-width">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-inline-container/width != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-inline-container/width"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-inline-container/width"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-inline-container-vertical-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-inline-container/vertical-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-inline-container/vertical-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-inline-container/vertical-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-inline-container-padding-top">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-inline-container/padding-top != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-inline-container/padding-top"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-inline-container/padding-top"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-inline-container-start-indent">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-inline-container/start-indent != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-inline-container/start-indent"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-inline-container/start-indent"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-inline-container-end-indent">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-inline-container/end-indent != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-inline-container/end-indent"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-inline-container/end-indent"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-alternative-width">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-alternative/width != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-alternative/width"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-alternative/width"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-alternative-margin">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-alternative/margin != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-alternative/margin"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-alternative/margin"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-alternative-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-alternative/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-alternative/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-alternative/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-alternative-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-alternative/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-alternative/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-alternative/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="filter-alternative-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/filter-alternative/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/filter-alternative/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/filter-alternative/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="footnote-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/footnote/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/footnote/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/footnote/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="footnote-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/footnote/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/footnote/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/footnote/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="footnote-margin-bottom">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/footnote/margin-bottom != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/footnote/margin-bottom"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/footnote/margin-bottom"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="footnote-margin-left">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/footnote/margin-left != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/footnote/margin-left"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/footnote/margin-left"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="instruction-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/instruction/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/instruction/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/instruction/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="instruction-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/instruction/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/instruction/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/instruction/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="instruction-font-style">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/instruction/font-style != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/instruction/font-style"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/instruction/font-style"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="instruction-margin-bottom">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/instruction/margin-bottom != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/instruction/margin-bottom"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/instruction/margin-bottom"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="instruction-margin-left">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/instruction/margin-left != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/instruction/margin-left"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/instruction/margin-left"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="instruction-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/instruction/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/instruction/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/instruction/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="statement-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/statement/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/statement/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/statement/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="statement-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/statement/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/statement/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/statement/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="statement-font-style">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/statement/font-style != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/statement/font-style"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/statement/font-style"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="statement-space-before">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/statement/space-before != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/statement/space-before"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/statement/space-before"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="statement-margin-bottom">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/statement/margin-bottom != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/statement/margin-bottom"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/statement/margin-bottom"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="statement-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/statement/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/statement/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/statement/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="answer-item-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/answer-item/color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/answer-item/color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/answer-item/color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="answer-item-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/answer-item/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/answer-item/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/answer-item/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="answer-item-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/answer-item/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/answer-item/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/answer-item/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="answer-item-padding-left">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/answer-item/padding-left != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/answer-item/padding-left"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/answer-item/padding-left"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="answer-item-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/answer-item/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/answer-item/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/answer-item/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="details-color">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/details/color != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/details/color"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/details/color"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="details-font-weight">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/details/font-weight != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/details/font-weight"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/details/font-weight"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="details-font-size">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/details/font-size != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/details/font-size"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/details/font-size"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="details-padding">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/details/padding != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/details/padding"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/details/padding"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="details-text-align">
      <xsl:choose>
         <xsl:when test="$parameters//fo-parameters/Style/details/text-align != ''">
            <xsl:value-of select="$parameters//fo-parameters/Style/details/text-align"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$properties//fo-parameters/Style/details/text-align"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

    <xd:doc>
        <xd:desc>
            <xd:p>Characters used to surround variables in conditioned text.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:variable name="conditioning-variable-begin" select="$properties//TextConditioningVariable/ddi/Before"/>
    <xsl:variable name="conditioning-variable-end" select="$properties//TextConditioningVariable/ddi/After"/>

    <xd:doc>
        <xd:desc>
            <xd:p>Root template :</xd:p>
            <xd:p>The transformation starts with the main Sequence.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <xsl:apply-templates select="//d:Sequence[d:TypeOfSequence/text()='template']" mode="source"/>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Linking output function enofo:get-body-line to input function enoddi:get-table-line.</xd:p>
            <xd:p>This function has too many parameters to stay in the functions.fods file</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-body-line">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="index"/>
        <xsl:param name="table-first-line"/>
        <xsl:sequence select="enoddi:get-table-line($context,$index,$table-first-line)"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Linking output function enofo:get-rowspan to input function enoddi:get-rowspan.</xd:p>
            <xd:p>This function has too many parameters to stay in the functions.fods file</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-rowspan">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="table-first-line"/>
        <xsl:param name="table-last-line"/>
        <xsl:sequence select="enoddi:get-rowspan($context,$table-first-line,$table-last-line)"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>This function retrieves the languages to appear in the generated Xforms.</xd:p>
            <xd:p>Those languages can be specified in a parameters file on a questionnaire level.</xd:p>
            <xd:p>If not, it will get the languages defined in the DDI input.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-form-languages">
        <xsl:param name="context" as="item()"/>
        <xsl:choose>
            <xsl:when test="$parameters/Parameters/Languages">
                <xsl:for-each select="$parameters/Parameters/Languages/Language">
                    <xsl:value-of select="."/>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="enoddi:get-languages($context)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="enofo:get-label">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <xsl:param name="loop-navigation" as="node()"/>
        <xsl:variable name="tempLabel">
            <xsl:apply-templates select="enoddi:get-label($context,$language)" mode="enofo:format-label">
                <xsl:with-param name="label-variables" select="enoddi:get-label-conditioning-variables($context,$language)" tunnel="yes"/>
                <xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:sequence select="$tempLabel"/>
    </xsl:function>
    
    <xsl:function name="enofo:get-fixed-value">
        <xsl:param name="context" as="item()"/>
        <xsl:param name="language"/>
        <xsl:param name="loop-navigation" as="node()"/>
        <xsl:variable name="tempLabel">
            <xsl:apply-templates select="enoddi:get-cell-value($context)" mode="enofo:format-label">
                <xsl:with-param name="label-variables" select="enoddi:get-cell-value-variables($context)" tunnel="yes"/>
                <xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()" tunnel="yes"/>
            </xsl:apply-templates>
        </xsl:variable>
        <xsl:sequence select="$tempLabel"/>
    </xsl:function>

    <xsl:template match="*" mode="enofo:format-label" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="enofo:format-label"/>
        </xsl:copy>
    </xsl:template>
     
    <xsl:template match="xhtml:p | xhtml:span" mode="enofo:format-label">
        <xsl:apply-templates select="node()" mode="enofo:format-label"/>
    </xsl:template>
    
    <xsl:template match="xhtml:span[@class='block']" mode="enofo:format-label">
        <xsl:element name="fo:block">
            <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        </xsl:element>
    </xsl:template>
    
<!--
    <xsl:template match="*[not(descendant-or-self::xhtml:*)]" mode="enofo:format-label">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="enofo:format-label"/>
        </xsl:copy>
    </xsl:template>-->

    <xsl:template match="text()" mode="enofo:format-label">
        <xsl:param name="label-variables" tunnel="yes"/>
        <xsl:param name="loop-navigation" tunnel="yes" as="node()"/>
        
        <xsl:if test="substring(.,1,1)=' '">
            <xsl:text xml:space="preserve"> </xsl:text>
        </xsl:if>
        <xsl:call-template name="velocity-label">
            <xsl:with-param name="label" select="normalize-space(.)"/>
            <xsl:with-param name="variables" select="$label-variables"/>
            <xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()"/>
        </xsl:call-template>
        <xsl:if test="substring(.,string-length(.),1)=' ' and string-length(.) &gt; 1">
            <xsl:text xml:space="preserve"> </xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template name="velocity-label">
        <xsl:param name="label"/>
        <xsl:param name="variables"/>
        <xsl:param name="loop-navigation" as="node()"/>
        
        <xsl:choose>
            <xsl:when test="contains($label,$conditioning-variable-begin) and contains(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)">
                <xsl:value-of select="substring-before($label,$conditioning-variable-begin)"/>
                <xsl:variable name="variable-name" select="substring-before(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)"/>
                <xsl:variable name="variable-type">
                    <xsl:call-template name="enoddi:get-variable-type">
                        <xsl:with-param name="variable" select="$variable-name"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="variable-ancestors" as="xs:string *">
                    <xsl:call-template name="enoddi:get-business-ancestors">
                        <xsl:with-param name="variable" select="$variable-name"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$variable-ancestors != ''">
                        <xsl:variable name="current-ancestor" select="$variable-ancestors[last()]"/>
                        <xsl:choose>
                            <xsl:when test="$loop-navigation//Loop[@name=$current-ancestor]/text() != ''">
                                <xsl:value-of select="concat('$!{',$current-ancestor,'-0-')"/>
                            </xsl:when>
                            <xsl:when test="$variable-type = 'external'">
                                <xsl:value-of select="concat('${',$current-ancestor,'.')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('$!{',$current-ancestor,'.')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$variable-type = 'external'">
                        <xsl:value-of select="'${'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'$!{'"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="enoddi:get-business-name">
                    <xsl:with-param name="variable" select="$variable-name"/>
                </xsl:call-template>
                <xsl:value-of select="'}'"/>
                <xsl:call-template name="velocity-label">
                    <xsl:with-param name="label" select="substring-after(substring-after($label,$conditioning-variable-begin),$conditioning-variable-end)"/>
                    <xsl:with-param name="variables" select="$variables"/>
                    <xsl:with-param name="loop-navigation" select="$loop-navigation" as="node()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$label"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="xhtml:i" mode="enofo:format-label">
        <xsl:element name="fo:inline">
            <xsl:attribute name="font-style" select="'italic'"/>
            <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xhtml:b" mode="enofo:format-label">
        <xsl:element name="fo:inline">
            <xsl:attribute name="font-weight" select="'bold'"/>
            <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xhtml:span[@style='text-decoration:underline']" mode="enofo:format-label">
        <xsl:element name="fo:wrapper">
            <xsl:attribute name="text-decoration" select="'underline'"/>
            <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xhtml:br" mode="enofo:format-label">
        <xsl:text xml:space="preserve">&#xA;</xsl:text>
    </xsl:template>

    <xsl:template match="xhtml:a[contains(@href,'#ftn')]" mode="enofo:format-label">
        <xsl:apply-templates select="node()" mode="enofo:format-label"/>
        <xsl:variable name="relatedInstruction" select="enoddi:get-instruction-by-anchor-ref(.,@href)"/>
        <xsl:choose>
            <xsl:when test="$relatedInstruction/d:InstructionName/r:String = 'tooltip'">
                <xsl:text>*</xsl:text>
            </xsl:when>
            <xsl:when test="$relatedInstruction/d:InstructionName/r:String = 'footnote'">
                <xsl:value-of select="enoddi:get-instruction-index($relatedInstruction,'footnote')"/>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:template>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for debugging, it outputs the input name of the element related to the driver.</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-ddi-element">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="local-name($context)"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-after-question-title-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'instruction,comment,help')"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving instructions based on the location they need to be outputted</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-end-question-instructions">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instructions-by-format($context,'footnote') | enoddi:get-next-filter-description($context)"/>
    </xsl:function>


    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving style for QuestionTable (only 'no-border' or '' as values yet)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-style">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="if(enoddi:get-style($context) = 'question multiple-choice-question') then ('no-border') else(if(enoddi:get-style($context) = 'image') then ('image') else())"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function for retrieving an index for footnote instructions (based on their ordering in the questionnaire)</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:get-end-question-instructions-index">
        <xsl:param name="context" as="item()"/>
        <xsl:sequence select="enoddi:get-instruction-index($context,'footnote,tooltip')"/>
    </xsl:function>

    <xd:doc>
        <xd:desc>
            <xd:p>Function that returns if a variable is initializable or not</xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:function name="enofo:is-initializable-variable" as="xs:boolean">
        <xsl:param name="context" as="item()"/>
        <xsl:choose>
            <xsl:when test="lower-case($initialize-all-variables) = 'true'">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- TODO : improve DDI content -->
                <xsl:value-of select="enoddi:get-variable-type($context,enoddi:get-id($context)) = 'external'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
</xsl:stylesheet>