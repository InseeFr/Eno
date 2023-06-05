# Eno Releases note

## 2.4.2 - 05/06/23
- **[dependencies]** upgrading dependancies
- **[ci]** : update CI
- **[docs]** update docs
- **[ddi2lunaticXML][core]** add pairwise component
- **[ddi2lunaticXML][core]** enrich code card label
- **[ddi2lunatic][post-processing]** : fix to generate questionnaire with begin questions
- **[ddi2fo][post-processing]** ***Business*** : update fist page

- **[poguesXML2ddi][core]** add pairwise component

## 2.4.1 - 07/11/22
- **[ddi2lunaticXML][core]** Fix to correctly resolve the variable name which generates a loop (for shapeFrom attribute in Lunatic)

## 2.4.0 - 19/10/22
- **[ddi2lunaticXML][core]** switch to Lunatic-model v2.3.1 to lunatic V2 refactoring

## 2.3.12 - 20/09/22

- **[poguesXML2ddi][core]** Unit measures, adding `MWh` and `MWh PCS`

## 2.3.11 - 14/09/22

- **[ddi2fo][post-processing]** ***Household*** : modification of the first page of paper questionnaires (revision of text and images)
- **[ddi2fo][ddi2xforms][post-processing]** ***Business*** : update of the legal framework (for paper and web questionnaires)

## 2.3.10 - 03/08/22
- **[params][ddi2out]** splitting capi-cati into separated capi and cati modes

## 2.3.9 - 01/07/22
- **[poguesXML2ddi][core]** Add some measurement units

## 2.3.8 - 23/06/22
- **[dependencies]** Upgrading dependancies.
- **[poguesXML2ddi][core]** Add some measurement units
- **[ddi2fo][post-processing]** ***business*** Various changes to first page in fo format

## 2.3.7 - 24/02/22
- **[dependencies]** Upgrading dependancies.
- **[ddi2lunaticXML][post-processing]** fixing the identification of first and last sequence id, which was wrong and could previously return multiple ids (insert-generic-questions).
- **[ddi2lunaticXML][params]** Adding param 'UnusedVars' specifying if calculated variables that are not used inside the Questionnaire should be output or not. By default, is false.

## 2.3.6 - 03/02/22
- **[ddi2fo][post-processing]** Various changes to ***household*** first page in fo format
- **[ddi2fo][post-processing]** Changing the logo zone of business first page

## 2.3.5 - 13/12/21
- **[dependencies]** Upgrading dependancies. 
- **[ddi2lunaticXML][core]** : Fixing function checking if calculated variables are used.
- **[ddi2lunaticXML][core]** : Fixing absence of the control field in the Table component

## 2.3.4 - 22/12/21
- **[dependencies]** Upgrading log4j version. 

## 2.3.3 - 14/12/21
- **[dependencies]** Upgrading log4j version. 

## 2.3.2 - 07/12/21
- **[dependencies]** Upgrading dependencies versions. 
- **[poguesXML2ddi]** Adding units.
- **[xforms2xforms]** Fixing the noopPreprocessing.
- **[ddi2lunaticXML][core]** Adding support of the "Control" parameter (in lunatic-xml parameters fed to Eno) : if false, controls are not added to the resulting questionnaire.
- **[ddi2lunaticXML][core]** Adding a field inFilter, specifying if a calculated variable is used for a filter or a loop condition. A function is-used-in-filter supports this feature by, for a given variable, searching up for its possible use in a condition (in all variables that may call it) for a given variable.
- **[ddi2lunaticXML][post-processing]** ***business*** Fixing the bindingDependencies tag for HEURE_REMPL

## 2.3.1 - 15/11/2021
- **[ddi2lunaticXML][core]** improving performance (in terms of the resolution of variables in formulas)

## 2.3.0 - 27/10/2021
- **[dependencies]** Upgrading dependencies versions. 
- **[parameters][core]** Adding in Eno settings survey mode choice (particularly for the Lunatic output which generates questionnaires for several modes). 
- **[ddi2out][preprocessing]** Implementing declaration retrieval according to the mode (during multimodal selection pre-processing step)
- **[ddi2fo][core]** Correction of the display of the time response
- **[ddi2fo][post-processing]** ***business*** end questions : Adding  an empty inline before hour png fields, to fix old behaviour with boxes not being spaced out.
- **[ddi2fodt][core]** Several evolutionn of fodt output (among which Loop names are displayed and variables in filter condition are correctly resolved)
- **[ddi2lunaticXML][core]** Not unfolding calculated variables until the collected variables 
- **[ddi2lunaticXML][core]** Revising binding dependencies
- **[ddi2lunaticXML][core]** Adding the recursive-replace step for content of tooltips that allows to replace the ' symbol (U+0027) by ’ (U+2019) -> needed for correct interpretation by VTL/orchestrators
- **[ddi2lunaticXML][core]** sum on vectors
- **[ddi2lunaticXML][core]** Correction for lunatic labels with number or dash at the beginning

## 2.2.11 - 07/09/2021
- **[dependencies]** Upgrading dependencies versions. 
- **[ddi2lunaticXML][core]** Adding support of calculated variables scope (shapeFrom) to allow better VTL support
- **[ddi2lunaticXML][core]** Fixing various quoting problems for labels in lunatic-xml
- **[ddi2lunaticXML][core]** Preparing missing variables support with missingResponse field in relevant components
- **[ddi2lunaticXML][core]** Preparing controls support with controls field for simple question component
- **[poguesXML2ddi][core]** Adding formulas language choice : XPATH or VTL
- **[poguesXML2ddi][ddi2fodt][ddi2xforms][core]** Adding metadata generation : Eno version and generation date
- **[ddi2fo][post-processing]** Adding support of custom styles parameters for fo output

## 2.2.10 - 13/07/2021
- **[dependencies]** Upgrading dependencies versions. 
- **[javadoc]** Corrections of errors.
- **[ddi2out][pre-processing]** Pre-processing md2xhtml : adding support of markdown links (only specific cases were supported so far)
- **[ddi2out][pre-processing]** Pre-processing tweak-xhtml-for-ddi : not using xsl:key (does not work) but matches in conditions for footnote pattern recognition
- **[ddi2fo][post-processing]** Revert to non informative barcode in first page of ***business*** fo forms (released in Eno v2.2.1).

## 2.2.9 - 21/06/21

- **[ddi2out][pre-processing]** Redesigning of the numbering (titling.xsl). From now on, there are only 3 parameters which are: choice of the numbering of the questions (number the questions or not, and, if so, to number them in sequence or on the whole questionnaire), choice of sequence numbering (number or not the sequences), display of the symbol in front of the numbering (display or not the "arrow" symbol before the questions)
- **[ddi2xforms][post-processing]** Replacing 'lien-deconnexion' by 'logout-uri' for the property of the close button (insert-end.xsl) (internationalization)
- **[ddi2xforms][core]** adding a title attribute to img (digital accessibility)

## 2.2.8 - 04/06/2021

- **[dependencies]** Upgrading dependencies versions (in particular Saxon version to 10.5, maven compiler to 11). 
- **[ddi2fo][post-processing]** Renaming the fo file in the zip intended for Insee integrators. 

## 2.2.7 - 20/05/2021

- **[ddi2fo][core]** Fixing issue where the message related to a Filter would not be displayed in fo/pdf output
- **[poguesXML2ddi][core]** Fixing an issue with pogues2ddi transformation not preventing a GoTo that would point to a loop. It now crashes with an error
- **[poguesXML2ddi][core]** Retrieving name of loops from Pogues-XML into DDI
- **[ddi2out][pre-processing]** The transformation markdown to xhtml (md2xhtml) is no longer a post-processing for poguesXML2ddi but now a pre-processing for ddi2output. For now, all output formats use it, but should change soon with Lunatic
- **[poguesXML2ddi][core]** When Pogues defines the collection mode for Instructions, they are now correctly transcribed into DDI
- **[ddi2out][core]** When DDI defines the collection mode for Instructions, they are now kept for the right output formats based on the collection mode that has been specified

## 2.2.6 - 10/05/2021

- **[dependencies]** Upgrading Saxon version to 9.9.1-8 to solve some performance issues.

## 2.2.5 - 12/04/2021

- **[ddi2xforms][core]** Fixing issue for Insee data collection platform when specifying variable type in xforms bind for external variables when they were not initialized : now type is not specified for external variables. 
- **[poguesXML2ddi][core]** Adding support of formulas in loop minimum for ddi from pogues-xml format.
- **[ddi2lunaticXML][post-processing]** Adding lunatic post-processing to manage pagination.
- **[test]** Upgrading to unit test version : junit 5.


## 2.2.4 - 31/03/2021

- **[ddi2fo][post-processing]** Updating fo accompanying mails cnrCOL and medCOL.

## 2.2.3 - 24/03/2021

- **[poguesXML2ddi][core]** hotfix : The referenced variables must be framed by ¤ and not $.
- **[poguesXML2ddi][core]** hotfix : Adding support of filtered loops in pogues-xml.

## 2.2.2 - 16/03/2021

- **[poguesXML2ddi][core]** Allowing a ControlConstructReference to point to a QuestionConstruct (for dynamic tables). Until now, it was wrongly pointing as a Loop (in r:TypeOfObject).
- **[ddi2fodt][core]** Adding support of loops and filters for display in fodt format (the format used to produce the questionnaires' specifications).
- **[ddi2lunaticXML][post-processing]** Fixing vtl parser : (space after ',').

## 2.2.1 - 10/02/2021

- **[ddi2xforms][core]** Hotfix xforms calculated var in dynamic array
- **[ddi2fo][post-processing]** Adding meaningful barcode for the cover page of ***business*** fo forms in portrait format. The barcode is of the datamatrix kind and contains {Identifier of surveyed unit} - {Short label of survey} - {Year of the survey} - {Period of the survey}.
- **[ddi2fo][post-processing]** Updating ***business*** fo accompanying mails.
- **[ddi2lunaticXML][post-processing]** Adding specific treatment for ddi2lunaticXML pipeline.

## 2.2.0 - 22/01/2021

- **[ddi2lunaticXML][core]** Changing the lunatic-model, using v.2.0.0.
