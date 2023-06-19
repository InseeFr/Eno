# DDI

DDI est un standard de description de métadonnées maintenu pas la [DDI Alliance](https://ddialliance.org).

:arrow_right: [Documentation de référence](https://ddialliance.github.io/ddimodel-web/DDI-L-3.3/)

## Spécificaitions Insee

DDI est le standard choisi dans la filière d'enquête pour décrire les métadonnées de questionnaire.

La documentation qui suit décrit l'implémentation des différents concepts de questionnaire en DDI.

Note : À l'heure actuelle, les objets d'un DDI appartiennent à un objet `DDIInstance`. La cible à terme est d'utiliser des objets `DDIFragment`.

Dans la documentation qui suit, le niveau `<DDIInstance>` est implicite.

_(en cours de rédaction)_

### Mode de collecte

À l'heure actuelle, le mode de collecte ne s'applique qu'aux déclarations et instructions.

Les libellés des modes de collecte sont :

- `Interview.FaceToFace.CAPIorCAMI`
- `Interview.Telephone.CATI`
- `SelfAdministeredQuestionnaire.WebBased`
- `SelfAdministeredQuestionnaire.Paper`

### Déclaration

```xml
<g:ResourcePackage>
    <d:ControlConstructScheme>
		<d:StatementItem>
            <r:Agency>fr.insee</r:Agency>
            <r:ID>string-id</r:ID>
            <r:Version>1</r:Version>
			<!-- Pour chaque mode de collecte pour lequel la déclaration est spécifiée : -->
            <d:ConstructName>
               <r:String xml:lang="fr-FR"><!-- Mode de collecte --></r:String>
            </d:ConstructName>
			<!-- ... -->
            <d:DisplayText>
               <d:LiteralText>
                  <d:Text xml:lang="fr-FR"><!-- Libellé de la déclaration --></d:Text>
               </d:LiteralText>
            </d:DisplayText>
         </d:StatementItem>
	</d:ControlConstructScheme>
</g:ResourcePackage>
```

### Instruction

Types d'instructions :

- `instruction`
- `help`
- `warning`
- TODO : autres ?

```xml
<g:ResourcePackage>
    <d:InterviewerInstructionScheme>
		<d:Instruction>
            <r:Agency>fr.insee</r:Agency>
            <r:ID>string-id</r:ID>
            <r:Version>1</r:Version>
            <d:InstructionName>
               <r:String xml:lang="fr-FR"><!-- Type d'instruction --></r:String>
            </d:InstructionName>
			<!-- Pour chaque mode de collecte pour lequel l'instruction est spécifiée : -->
            <d:ConstructName>
               <r:String xml:lang="fr-FR"><!-- Mode de collecte --></r:String>
            </d:ConstructName>
            <d:InstructionText>
               <d:LiteralText>
                  <d:Text xml:lang="fr-FR"><!-- Libellé de l'instruction --></d:Text>
               </d:LiteralText>
            </d:InstructionText>
         </d:Instruction>
    </d:InterviewerInstructionScheme>
</g:ResourcePackage>
```

### Question à réponse unique

```xml
<g:ResourcePackage>
    <d:QuestionScheme>
	    <d:QuestionItem>
            <r:Agency>fr.insee</r:Agency>
		    <!-- ... -->
		</d:QuestionItem>
    </d:QuestionScheme>
</g:ResourcePackage>
```

### Question à réponses multiples

```xml
<g:ResourcePackage>
    <d:QuestionScheme>
	    <d:QuestionItem>
	        <r:ID>string-id</r:ID>
		    <!-- ... -->
		</d:QuestionItem>
    </d:QuestionScheme>
</g:ResourcePackage>
```

# Tableaux

```xml
<d:QuestionGrid>

	<!-- Métadonnées classiques d'une question grid -->
	<r:Agency>fr.insee</r:Agency>
	<r:ID><!--identifiant de la question--></r:ID>
	<r:Version>1</r:Version>
	<d:QuestionGridName>
	   <r:String xml:lang="fr-FR"><!--nom de la question--></r:String>
	</d:QuestionGridName>

	<!-- Variables du tableau -->
	<!-- Pour chaque case à l'intérieur du tableau (ie. hors en-tête & colonne de gauche) : -->
	<r:OutParameter isArray="false">
	   <r:Agency>fr.insee</r:Agency>
	   <r:ID><!--Identifiant du <r:SourceParameterReference> de la variable--></r:ID>
	   <r:Version>1</r:Version>
	   <r:ParameterName>
		  <r:String xml:lang="fr-FR"><!--nom de la variable collectée dans la case--></r:String>
	   </r:ParameterName>
	</r:OutParameter>
	<!-- ... -->

	<!-- Lien entre les variables et les cases du tableau -->
	<!-- Pour chaque case à l'intérieur du tableau (ie. hors en-tête & colonne de gauche) : -->
	<r:Binding>
	   <r:SourceParameterReference>
		  <r:Agency>fr.insee</r:Agency>
		  <r:ID><!--Identifiant du <r:OutParameter> du response domain (le même que dans le r:OutParameter -> r:ID associé plus bas)--></r:ID>
		  <r:Version>1</r:Version>
		  <r:TypeOfObject>OutParameter</r:TypeOfObject>
	   </r:SourceParameterReference>
	   <r:TargetParameterReference>
		  <r:Agency>fr.insee</r:Agency>
		  <r:ID><!--Identifiant du <r:SourceParameterReference> de la variable (le même que dans le r:OutParameter -> r:ID associé plus haut)--></r:ID>
		  <r:Version>1</r:Version>
		  <r:TypeOfObject>OutParameter</r:TypeOfObject>
	   </r:TargetParameterReference>
	</r:Binding>
	<!-- ... -->

	<!-- Libellé de la question -->
	<d:QuestionText>
	   <d:LiteralText>
		  <d:Text xml:lang="fr-FR">"<!--libellé de la question-->"</d:Text>
	   </d:LiteralText>
	</d:QuestionText>

	<!-- Colonne de gauche / axe vertical / axe d'information principal : -->
	<d:GridDimension displayCode="false" displayLabel="false" rank="1">
	   <d:CodeDomain>
		  <r:CodeListReference>
			 <r:Agency>fr.insee</r:Agency>
			 <r:ID><!--identifiant d'une liste de code--></r:ID>
			 <r:Version>1</r:Version>
			 <r:TypeOfObject>CodeList</r:TypeOfObject>
		  </r:CodeListReference>
	   </d:CodeDomain>
	</d:GridDimension>

	<!-- En-tête / ligne du haut / axe horizontal / axe d'information secondaire : -->
	<d:GridDimension displayCode="false" displayLabel="false" rank="2">
	   <d:CodeDomain>
		  <r:CodeListReference>
			 <r:Agency>fr.insee</r:Agency>
			 <r:ID><!--identifiant d'une liste de code--></r:ID>
			 <r:Version>1</r:Version>
			 <r:TypeOfObject>CodeList</r:TypeOfObject>
		  </r:CodeListReference>
	   </d:CodeDomain>
	</d:GridDimension>

	<!-- Cases du tableau / information collectée -->
	<d:StructuredMixedGridResponseDomain>
	   <!-- Pour chaque case à l'intérieur du tableau (ie. hors en-tête & colonne de gauche) : -->
	   <d:GridResponseDomainInMixed>
		  <!-- Un objet response domain, exemple <d:NumericDomain>...</d:NumericDomain> -->
		  <d:GridAttachment>
			 <d:CellCoordinatesAsDefined>
				<d:SelectDimension rank="1" rangeMinimum="<!--numéro de ligne-->" rangeMaximum="<!--numéro de ligne-->"/>
				<d:SelectDimension rank="2" rangeMinimum="<!--numéro de colonne-->" rangeMaximum="<!--numéro de colonne-->"/>
			 </d:CellCoordinatesAsDefined>
		  </d:GridAttachment>
	   </d:GridResponseDomainInMixed>
	   <!--...-->
	</d:StructuredMixedGridResponseDomain>

</d:QuestionGrid>
```
