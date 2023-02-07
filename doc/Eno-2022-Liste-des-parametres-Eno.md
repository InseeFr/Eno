# Listes des paramètres Eno 

**Version au 31 mai 2022** 

Une partie des paramètres Eno sont communs à tous les pipelines, d'autres spécifiques au format de sortie. 

Le paramètrage d'un pipeline Eno complet comprend donc à chaque lancement : 
- les paramètres au niveau de la racine <EnoParameters/> communs à tous les formats
- les paramètres au niveau de de <Parameters/> communs à tous les formats
- le paramétrage spécifique (au format Out)
Il n'existe pas de paramétrage spécifique pour ddi2fodt ni pour poguesxml2ddi. 

Vous trouverez le paramétrage par défaut via le web-service d'Eno-WS : 
`GET /parameter/{context}/{outFormat}/default`

## Paramètres au niveau de la racine `<EnoParameters/>` communs à tous les formats
|**Parameters**|**valeurs**|**defaut**|**description**|
|--|--|--|--|
|      Context|household, business, default||Le contexte métier dans lequel est généré le questionnaire, respectivement enquêtes ménages, enquêtes entreprises, visualisation pogues. Il permet de récupérer le paramétrage par défaut en cas d'absence d'un paramètre.||
|      Campagne|*champ texte*|test-2020-x00|Nom de la campagne. Il sert notamment au nommage de l'output.|
|      Mode**|cawi (web), capi (face à face), cati (téléphone), papi (papier), process**||Mode de collecte pour le questionnaire généré (cas particulier : phase GSBPM "process" pour le post-collecte). |
|         Languages/Language|fr, en, it, es, de  | fr|Langage du questionnaire|


## Paramètres au niveau de de `<Parameters/>` communs à tous les formats
|**Parameters**|**valeurs**|**défaut**|**description**|
|--|--|--|--|
|         BeginQuestion/Identification|true, false|default et household : false, business : true |Ajoute une question de début (zone de commentaires)|
|         EndQuestion/ResponseTimeQuestion|true, false|default et household : false, business : true |Ajouter une question de fin : temps de réponse|
|         EndQuestion/CommentQuestion|true, false|default : false, business : et household : true |Ajouter une question de fin : page de commentaires|
|         Numerotation/QuestNum|no-number, module, all| default et business : module, household : all|Numérotation des questions : aucune, continue sur le questionnaire, continue par séquence |
|         Numerotation/SeqNum|true, false| true |Numérotation des séquences|
|         Numerotation/PreQuestSymbol|true,false| true |Symbole (flèche) avant chaque question|

## Paramètres spécifique au pipeline ddi2lunaticXML (`<lunatic-xml-parameters/>`) 
|**Parameters**|**valeurs**|**defaut**|**description**|
|--|--|--|--|
|         Control|true, false|cawi et process : true, cati et capi : false|Ajouter les contrôles|
|         Tooltip|true, false|cawi et process : true, cati et capi : false|Ajouter les infobulles (non implémenté)|
|         FilterDescription|true,false|cawi, cati et capi : false, process : true|Ajouter la description des filtres|
|         AddFilterResult|true,false|cawi : true, cati, capi et process : false|Ajouter des variables précisant si la question a été affichée sur le web|
|         MissingVar|true, false|cawi : false, cati et capi : true|Ajouter des variables pour enregistrer le NSP refus|
|         Pagination|none, sequence, question|cawi, capi et cati : question, process : none|Pagination (par question, séquence ou sans) -  (subsequence : non implementé)|
|         UnusedVars|true,false| |Garder les variables inutilisées (true=garder toutes les variables décrites)|


## Paramètres spécifique au pipeline ddi2fo (`<fo-parameters/>`)
|**Parameters**|**valeurs**|**defaut**|**description**|
|--|--|--|--|
|         InitializeAllVariables|true, false|false |Possibilité de préremplir des variables collectées (à partir du fichier de personnalisation)|
|            Format/Orientation|0, 90| 0|Format portrait (0) ou paysage (90)|
|            Format/Columns|1, 2|business et default : 1, household : 2|Colonnes : 1 ou 2|
|               Roster/Row/MinimumEmpty| *champ nombre entier*|1|Nombre de lignes affichées par défaut dans le cas d’un tableau dynamique|
|               Roster/Row/DefaultSize|*champ nombre entier*|   10|nombre de lignes vides supplémentaires affichées par défaut dans le cas d’un tableau dynamique pré-rempli (lignes en plus de la personnalisation)|
|            Loop/DefaultOccurrence|*champ nombre entier*|   5|nombre de boucles par défaut|
|            Loop/MinimumEmptyOccurrence|*champ nombre entier*|   1|nombre de boucles vides supplémentaires par défaut (boucles en plus de la perso)|
|               TextArea/Row/DefaultSize| *champ nombre entier*|  5|Hauteur des zones de texte|
|               Table/Row/DefaultSize| *champ nombre entier*|  19|Nombre de lignes maximum par page pour les tableaux et tableaux dynamiques.|
|            Capture/Numeric|manual, optical|default : manual, business et household : optical|Saisie optique (précasage) ou manuelle (champ unique pour les numériques, sans distinction du nombre de positions).|
|         PageBreakBetween||vide |Saut de page : après chaque module ou sans saut de page « forcé »|
|         AccompanyingMail|vide ou entreeCOL, ouvertureCOL, relanceCOL, medCOL, cnrCOL, accompagnementCOL|vide |Entreprises : choix du courrier d’accompagnement|
|         Style||vide |Exceptionnel : appliquer un style|



## Paramètres spécifique au pipeline ddi2xforms (`<xforms-parameters/>`)

|**Parameters**|**valeurs**|**defaut**|**description**|
|--|--|--|--|
|         NumericExample|true, false|false|Affichage d’un exemple pour les variables numériques|
|         Deblocage|true, false|false|Questionnaire « reexpédiable » – l’enquêté peut renvoyer son questionnaire autant de fois qu’il le souhaite|
|         Satisfaction|true, false|false|Ajout d’une enquête de satisfaction  (Coltrane)|
|         LengthOfLongTable|*champ nombre entier*| 7||
|         DecimalSeparator|. (point) ou , (virgule)|,|Typologie du séparateur de décimal|
|         Css|*champ texte* |vide|Ajout d’une feuille de style particulière (pour gérer la taille de certaines colonnes par exemple). La feuille de style doit être présente dans Orbeon.||



