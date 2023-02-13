<!-- MENAGES CATi et CAPI - A MODIFIER :         Campagne ---->
<ENOParameters>
   <Parameters>
      <Context>household</Context>
      <Campagne>fam-2023-x00</Campagne>                                  <!-- nom de la campagne ---->
      <Languages>
         <Language>fr</Language>
      </Languages>
      <BeginQuestion>
         <Identification>false</Identification>                          <!-- ajout de la zone commentaires en début de questionnaire :FALSE pour ménages ---->
      </BeginQuestion>
      <EndQuestion>
         <ResponseTimeQuestion>false</ResponseTimeQuestion>            <!-- ajout du temps de remplissage en fin de questionnaire : FALSE pour ménages ---->
         <CommentQuestion>false</CommentQuestion>                      <!-- ajout de la zone commentaires en fin de questionnaire : true sur CAPI CATI---->
      </EndQuestion>
      <lunatic-xml-parameters>
	     <Control>false</Control>                                   <!--  activation des controles ---->
         <Tooltip>false</Tooltip>                                   <!--  activation des infobulles ---->
         <FilterDescription>false</FilterDescription>               <!-- besoin Generic ---->
         <AddFilterResult>false</AddFilterResult>                    <!-- ajout des variables VAR_FILTER_RESULT pour permettre de savoir quelles variables ont été affichées à l'enquêté---->
		 <MissingVar>true</MissingVar>                              <!-- ajout des variables VAR_MISSING pour enregistrer le NSP/REFUS---->
         <Pagination>question</Pagination>                         <!-- Pagination : une question par page ---->
		 <UnusedVars>false</UnusedVars>								<!-- Conservation des seules variables utilisées en collecte : FALSE toujours ----> 
      </lunatic-xml-parameters>
      <Numerotation>
         <QuestNum>no-number</QuestNum>							<!-- Numérotation des questions : continu (all), par séquence (module), aucune (no-number) ---->
         <SeqNum>true</SeqNum>						         	<!-- Numérotation des séquences ---->
         <PreQuestSymbol>true</PreQuestSymbol>					<!-- Symbole avant les questions ---->
      </Numerotation>
   </Parameters>
</ENOParameters>
