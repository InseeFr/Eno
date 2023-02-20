# Eno Java V3

## I - Rappel essentiel sur Eno V2

### Eno : les transformations
Eno est un **moteur utilisé pour générer des modèles de questionnaires**. Ces modèles de questionnaires sont ensuite utilisés pour produire les supports de collecte et de post-collecte, dans différents modes, au sein d’infrastructures de collecte et de post-collecte de production.

![](.images/schema_eno.jpg)


Au sens technique, Eno est un transformateur de XML : **Eno peut être vu comme un ré-arrangeur de XML, d’un format de fichier (standardisé) vers un autre.**

D’un point de vue statique, **cinq transformations** sont déjà utilisées en production :
- **XML Pogues → DDI** 
- **DDI → Xforms** 
- **DDI → fo**
- **DDI → fodt** 
- **DDI → XML Lunatic** 

Eno, par construction, ne traite que des formats XML. Ainsi, les transformations JSON<->XML (dans le cas d'interfaçage avec Pogues et Lunatic) sont effectuées ailleurs. 

### Pipeline XSLT
Le générateur Eno repose sur le principe du **pipeline XSLT**, c’est-à-dire un **enchaînement de transformations XSLT (transformations de fichiers XML en fichiers XML).**

Quels que soient les langages en entrée et en sortie, ce pipeline peut se découper en **3 étapes principales** : 

![](./images/vue_dynamique.png)

- **Pré-Processing** : Les étapes de pré processing, désignent des traitements intervenant avant la transformation centrale, et adaptant le fichier dans son format d’entrée.
- **Transformation centrale** : cette étape permet de passer du langage en entrée au langage en sortie et est la plus complexe. 
- **Post-Processing** : Les étapes de post processing interviennent après la transformation centrale, et consistent souvent à contextualiser le questionnaire, en adaptant son contenu pour la réalisation effective de la collecte.

### Les  pré-processing et des post-processing
On distingue ainsi **deux catégories de traitements pré-processing et post-processing** :
- Les **transformations purement techniques**, adhérentes aux formats in (pour pré) et out (pour post) et servant à la préparation de ces formats avant transformation. (Exemple : le déréférencement qui vise à produire un format technique "à plat" plus facilement manipulable qu'un format à base de reférences)
- Les **transformations métier de contextualisation** qui ne sont pas adhérentes à un format mais à une utilisation (exemple : la numérotation qui ajoute la numérotation paramétrée aux questions/modules d'un questionnaire).

### Le paramétrage d'Eno

L'appel à Eno est **"configuré"** par : 
- **le pipeline** : format in, format out (permet de définir la transformation centrale) et les pré-processing et post-processing
- **le mode de collecte**
- **le paramètrage contextuel métier** : définit les options choisies dans le contexte métier (voir le [recensement des paramètres](./Liste-des-parametres.md)). 

### Eno-WS
[Eno-WS](https://github.com/InseeFr/Eno-WS) (Eno webservices) est l'API (interface) qui encapsule les transformations d'Eno dans des web-services REST pour les mettre à dispostion de l'utilisateur. Elle importe la [bibliothèque Eno](https://github.com/InseeFr/Eno) qui contient les fonctionnalités d'Eno. 

### Les fonctionalités d'Eno

- [Les fonctionnalités de composants de questionnaires](./Couverture-fonctionnelle.md)



## II - Le refactoring technique

### Les enjeux
- **Le cœur d’Eno, est développé dans une technologie (le XSLT), dont les ressources sont rares**
-	Le **support communautaire XML/XSLT est en perte de vitesse**
-	Les bibliothèques java qui traitent XML/XSLT évoluent peu par conséquence, il y a donc un plus **fort risque d’obsolescence** (ou de galère…)
-	Les outils autour du XSLT ne sont pas bien optimisés pour le développement : le débuggage est compliqué et cela **ne permet pas une productivité équivalente aux autres langages**
-	Le XSLT manque d'attractivité et est difficilement valorisable dans une carrière. Il existe donc une **difficulté de recrutement et le coût de remplacement est élevé**.
-	Il y a un **risque de rupture dans la continuité de service** par le manque de connaisseur disponible immédiatement.
-	**Une des orientations du DSI de l'Insee est de s’affranchir de la filière XML.**

La solution à cela est la **ré-écriture d'Eno dans un langage plus classique (Java, fortement répandue pour les back-office à l'Insee)**.

**Cette version d'Eno "full Java" est appelé Eno V3.**

Il s'agit d'une montée de version majeure. 
Pour mémoire, les **version précédentes majeures** étaient les suivantes : 
- **Fist release (POC) - fév 2013** :  questionnaires modélisés en DDI 3.1 (à la main puis avec XSLT) génère PDF modifiables
- **Second release  - 2015** : Questionnaires modélisés en DDI 3.2 à la main, génération Xforms.
- **Eno V1 - 2017** : Eno sur Github. Extensions format de sortie : FO (2018), XMLPogues2DDI (2018), spécification (FODT - 2018), Lunatic (2019)
- **Eno V2 (Eno-WS+Eno-params) - 2019** : appels (création d'une api "Eno-WS") paramétrés des pipelines d'Eno.

### Principe de développement

Par rapport à Eno XSLT, la transformation centrale est désormais découpée en deux étapes : 
- du format in au format modèle pivot 
- du format pivot au format out

Ainsi, **le format in et le format out sont totalement décorrélés**.
**Le DDI n'est plus le format pivot pour l'implémentation technique du coeur d'Eno : c'est un modèle conceptuel propre à Eno qui joue ce rôle.**

Ainsi, les questionnaires de Pogues pourront être transformés directement du modèle Pogues vers le format de visualisation (Lunatic, fo, etc) sans passer par le DDI.

Les **étapes de transformations** sont alors les suivantes : 

![Principe eno](./images/Principe_Eno_Java.png)


**Idée centrale retenue pour l'implémentation Java :**

- Décrire le **modèle pivot Eno** par un ensemble d'objets java _simples_ (Questionnaire, Variable, Sequence, Filtre etc.) mais assez complet pour décrire l'ensemble des concepts qu'on utilise dans le DDI, ceux de lunatic model et pogues model. Ce modèle est déjà existant sous la forme de "Driver" dans Eno XSLT.
- Poser des **annotations** sur les attributs des différentes classes de ce modèle pivot, pour décrire le lien entre le format d'entrée ou de sortie et le modèle pivot.
- Implémenter un **mapper générique** qui permet de transformer les objets Java d'un modèle de données vers un autre:
  -  à partir d'une entrée, va parcourir les annotations pour alimenter le modèle pivot
  -  à partir d'un objet pivot alimenté, parcourir les annotations pour produire une sortie

Le **parser** est une étape technique permettant de transformer un fichier en objets java. (XML -> Java ou Json -> Java). Le **writer** est l'étape technique inverse de transformation d'objets java en fichier (Java -> XML ou Java -> Json).

La complexité du code est concentrée au niveau du/des mappers (a priori un mapper "in", et un mapper "out"), _peu de code est nécessaire_ (généricité). La part de code qui décrit la correspondance entre le format pivot et un autre format est réduit à son strict minimum au sein des annotations.

On a un _découplage absolu_ entre entrées et sorties.

Le _paramétrage_ permet de configurer parser/mappers/writer, et les éventuels pré/post-processing à effectuer.

Les **pré et post processing** sont réalisés directement depuis Java (la plupart des pré-processing d'ordre technique dans la version xml/xslt font directement partie du coeur en java). 
Certains se feront au niveau du modèle Pivot (**Core-processing**) lorsqu'il n'est pas spécifique à un format d'entrée ou de sortie et d'autres au niveau du format de sortie (spécifique à un format Out : **Out-processing**. Pas de In-processing identifié pour le moment.).  

### Modèle conceptuel

_(diagramme en cours d'édition)_

![Eno-Model](./images/eno-model.png)


### Implémentation

Les développements d'Eno V3 sont sur la branche [v3-develop du projet Eno](https://github.com/InseeFr/Eno/tree/v3-develop)

Choix de la version Java : Java 17

Choix de [`gradle`](https://gradle.org/maven-vs-gradle/) pour la gestion des dépendances.

Composition du projet : 
- `eno-core` : module principal
- `ddi-beans` : module pour générer les classes DDI à partir des schémas

Les classes Java in et out sont générées à partir des schémas existants : 
- [sources DDI](https://ddialliance.org/Specification/DDI-Lifecycle/3.3/)
    - [module ddi-beans](https://github.com/InseeFr/Eno/tree/v3-develop/ddi-beans)
- [sources Pogues-model](https://github.com/InseeFr/Pogues-model)
    - Mise en dépendance d'Eno de la lib Pogues-model (classe Java déjà générées)
- [sources Lunatic-model](https://github.com/InseeFr/Lunatic-model)
    - Mise en dépendance d'Eno de la lib Lunatic-model (classe Java déjà générées)
- [Normes XSL-FO](https://www.w3.org/TR/xslfo20/) **pas de schéma XSD** existants 
Pas de schéma pour le format spécification (fodt)


## III - Analyse pour le passage à Eno Java

### Décorrélation format in/format out
La logique "transformation in2out" disparaît avec la décorrélation des formats in et out d'Eno V3 rendu réalisable avec le modèle pivot. Elle laisse place à une logique "format d'entrée" et "format de sortie" sur laquelle repose notre réflexion. 

### Pas de passage à Eno-V3 pour le Xforms
La sortie Xforms ne sera pas implémentée.

### ddi2lunatic : transformation pilote
La transformation pilote est la transformation ddi2lunatic. Le format In DDI et le format Out Lunatic sont les deux premiers formats implémentés.

### Formats à logique "objet" vs formats à logique "document"

Les formats Lunatic, DDI et Pogues sont des formats qui ne présentent pas de difficultés pour l'intégration à Eno-V3 car ce sont des formats dont la logique est objet (questionnaire).

Les formats Spec et Fo sont plus complexes à appréhender car la logique est plutôt document. 
La difficulté est plus importante pour le format FO (peu de documentation disponible, implémentation de VTL velocity) par rapport au format spec.

Les formats techniques fodt et fo étaient des choix fortement adhérents à la contrainte technique XSLT. 
Une réflexion est à avoir sur la pertinence de ces choix pour le passage à Eno Java. 
Pour cela, un retour aux besoins pour les deux formats est nécessaire. 

Une instruction doit être effectuée.

**Pré & post-processing**
Après recensement et analyse des pré et post processing existants pour toutes les transformations dans Eno-V2, il est apparu qu'une grande majorité était techniques ou adhérents au XML. 
D'autres part, certains post-processing qui étaient développés dans chacun des formats sont mutualisés dans Eno-V3.

Il reste alors **5 core-processing** à implémenter au niveau du modèle pivot : 
- la *gestion des modes* (sélection des objets du mode renseigné en paramètre (CAWI, CAPI, CATI, PAPI…) dans le cas d'un questionnaire multimode in (qu'il soit pogues ou ddi).
- la *gestion de la mise en forme texte* (permettre la transformation markdown (mise en forme pogues) à texte brut si besoin).
- la *gestion du langage de saisie des formules* (pseudo-Xpath -> VTL dans certains cas)
- la *numérotation des séquences et des questions*
-  l'*ajout des questions génériques* (questions de début (Identification) et de fin (temps de réponses et commentaires) suivant le paramétrage)

**2 lunatic-processing** : 
- *lunatic-cleaning (Lunatic V2)* (permet de rajouter les blocs "cleaning", "missing" et "resizing" à Lunatic pour faciliter les traitements de Lunatic en aval) 
- *lunatic-pagination* (permet de numéroter les pages du document Lunatic selon le type de pagination choisi)

Et a priori **2 papi-processing** : 
- *papi-insertCoverPage* (permet d'ajouter une page de garde) 
- *papi-insertAccompanyingMails* (permet d'ajouter un courrier d'accompagnement)

### Les traitements spécifiques
Anciennement connu sous le nom de "verrue", il correspond à une transformation XSL en bout de chaîne Eno. 
Les traitements spécifiques visent à combler les éventuelles fonctionnalités manquantes dans Eno ou non modélisables par le DDI à ce jour (ex : le suggester) et/ou prendre en compte un besoin trop particulier mais acceptable d’une enquête (ex : une pagination spécifique pour certaines questions ménages). 
Ce post-processing est disponible pour tous les outputs. Il prend la forme dans Eno V2 d'une feuille XSL en input de l'appel à Eno. Cette feuille XSL n'est pas développé par les intégrateurs mais par le maintenicien Eno. 

Pour remplacer les traitements spécifiques avec Eno-V3, il sera mis en place une bibliothèque de traitements spécifiques, que ce soit au sens littéral ou au sens java, avec le passage d'un paramétrage dans l'appel à Eno pour spécifier l'appel du traitement et ses paramètres (exemple : la liste des questions à regrouper). 
Elle sera, comme c'est le cas actuellement, maintenu par l'équipe Eno.

A instruire : recenser les enquêtes à traitements spécifiques, recenser les traitements spécifiques existants.

### Passage à Eno V3
Eno V2 et Eno V3 peuvent et doivent co-exister. 

Cependant, lorsqu'un pipeline passe dans Eno V3, il faut que l'intégralité des fonctionnalités de cette transformation soit développé. 
Ainsi, les Xforms  peuvent continuer d'être générés avec Eno V2 et les Lunatic peuvent être générés avec Eno V3.

Cette co-existence implique une implémentation technique à instruire. 


### Paramétrage
Le paramétrage d'Eno va évoluer à la marge pour s'adapter à la nouvelle logique : pas de "Pipeline" avec des pré et post-processing mais d'autres paramètres qui activeront des actions ou non. 
Par exemple « Formatting » avec Markdown ou RawText suivant si on veut en sorti du markdown ou du texte brut (ancien pré-processing ddi-markdownToXhtml).


### Pogues-model et Lunatic-model
Plus de transformation xml -> json pour les format Pogues et Lunatic : Eno prend en entrée directement le Json Pogues et écrit en sortie le Json Lunatic. 
A instruire : l'abandon des schémas XSD (Pogues-model et Lunatic-model) au profit d'un modèle d'objet Java ou un schéma Json pour ces modèles pivot qui font office d'interface entre les systèmes.

## IV - Proposition de jalons

### **Eno v3.0.0 et Eno-WS v2.0.0** : Eno-V3 iso-fonctionnel Eno-V2 ddi2lunatic : 

Première mise en production d'Eno-V3 : ddi -> Lunatic fonctionnel sans les traitements spécifiques

*Eno-V3*
- [ ] Mapper In DDI
- [ ] Mapper Out Lunatic
- [ ] Processing Core : Gestion des modes
- [ ] Processing Core : Ajout des questions génériques
- [ ] Processing Core : Gestion de la mise en forme texte
- [ ] Processing Core : Gestion du langage de saisie des formules
- [ ] Processing Core : Numérotation des séquences et des questions
- [ ] Processing Out : lunatic-cleaning (Lunatic V2)
- [ ] Processing Out : lunatic-pagination
- [ ] Refonte du paramétrage (Pipeline Eno)

*Eno-WS*
- [ ] Refonte d'Eno-WS
- [ ] Implémentation co-existence Eno-V2 et Eno-V3

**Echéance Eno-V3 : fin 2022**
**Echéance Eno-WS : début 2023**

### **Eno v3.1.0 et Eno-WS v2.1.0** Bibliothèque de traitements spécifiques (en java) :

ddi -> Lunatic fonctionnel avec les traitements spécifiques.

- [ ] Mise en place de la bibliothèque de traitements spécifiques

**Echéance : début 2023**

### **Eno v3.2.0** : Entrée pogues

pogues -> lunatic fonctionnel.

- [ ] Mapper In Pogues

**Echéance : en attente d'estimation par NS (réponse jeudi 16/06 au soir)**

### **Eno v3.3.0** Sortie DDI

pogues -> lunatic fonctionnel.

- [ ] Mapper Out DDI

**Echéance : en attente d'estimation par NS (réponse jeudi 16/06 au soir)**

### **Eno v3.4.0** Sortie Spec

pogues -> spec
ddi -> spec

- [ ] Mapper Out Spec

**Echéance instruction : prochain comité produit**

### **Eno v3.5.0** Sortie PAPI

pogues -> papi
ddi -> papi

Eno-V3 est en service et iso-fonctionnel sur son périmètre cible. 
Seule ddi -> xforms reste dans Eno-V2.

- [ ] Refonte Module courrier ?
- [ ] Mapper Out PAPI

**Echéance instruction : prochain comité produit**

### Refonte de Pogues-model et Lunatic-model (bonus)
- [ ] Abandon du XSD pour Pogues-model
- [ ] Abandon du XSD pour Lunatic-model

**Bonus : pas d'échéance**
### **Eno v3.6.0** : ré-utilisation de questionnaires du référentiel par Pogues (bonus)

Transformation inverse ddi -> Pogues fonctionnelle

- [ ] Mapper Out Pogues

**Bonus : pas d'échéance**
