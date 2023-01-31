# DDI

DDI est un standard de description de métadonnées maintenu pas la [DDI Alliance](https://ddialliance.org).

:arrow_right: [Documentation de référence](https://ddialliance.github.io/ddimodel-web/DDI-L-3.3/)

## Spécificaitions Insee

DDI est le standard choisi dans la filière d'enquête pour décrire les métadonnées de questionnaire.

La documentation qui suit décrit l'implémentation des différents concepts de questionnaire en DDI.

_(en cours de rédaction)_

### Instruction

```xml
<g:ResourcePackage>
    <d:InterviewerInstructionScheme>
	    <d:Instruction>
	        <r:ID>string-id</r:ID>
		    <!-- ... -->
		</d:Instruction>
    </d:InterviewerInstructionScheme>
</g:ResourcePackage>
```

### Question à réponse unique

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
