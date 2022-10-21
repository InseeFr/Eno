# Modélisation des tableaux

## DDI

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
