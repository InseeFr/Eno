# Modèle Eno

## Concepts

Résumé des concepts de questionnaire manipulés par Eno :

- Questionnaire
- Séquence
- Sous-séquence
- Libellé / libellé dynamique
- Expression calculée
- Déclaration / Instruction
- Mode de collecte
- Question à réponse unique
  - Question simple
    - booléenne
    - texte
    - numérique
    - date
  - Question à choix unique
- Question à réponses multiples
  - Question à choix multiple
  - Question "lien deux à deux"
  - Tableau
    - statique
    - dynamique
- Réponse
- Contrôle
- Filtre
- Boucle
- Variable 
  - collectée
  - calculée
  - externe
- Groupe de variables
- Liste de codes
- Suggesteur

### Structure du questionnaire

Un questionnaire est structuré en séquences et éventuellement sous-séquences. 

Il n'y a pas de "sous-sous-séquence" etc.

Un questionnaire commence obligatoirement pas une séquence, et toutes les questions appartiennent à une séquence ou sous-séquence.

### Libellés & expressions

Libellé (statique) : libellé à valeur fixe (la cible dans Pogues étant que tous les libellés soient dynamiques).

Libellé dynamique : libellé défini par une expression calculée (renvoyant une chaîne de caractères).

Expression calculée : expression

Les expressions sont définies en Xpath ou VTL. Le XPath a vocation a disparaître.

### Déclarations / Instructions

Déclaration = texte affiché _avant_ la question

Instruction = texte affiché _après_ la question

### Mode de collecte

Certains éléments portent les modes de collecte pour lesquels ils sont définis.

Actuellement : uniquement les déclaration / instructions.

### Questions / Réponses

Dans le modèle Eno, on distingue trois types de questions :

- Questions à réponse unique : questions simples et QCU
- Questions à réponses multiples : uniquement les QCM dont les modalités sont des booléens
- Tableaux : QCM "complexes" et tableaux

Voir la documentation sur les modélisations DDI, Pogues, Lunatic pour les détails.

Les objets de réponse sont propres à Lunatic.

### Élements de navigation

Contrôle : contient une expression calculée pour afficher ou non un avertissement selon la saisie du répondant.

Filtre : expression calculée définissant des composants à ne pas afficher en fonction des réponses dans le questionnaire.

Boucle : un ensemble de séquences ou de sous-séquences peut être itéré plusieurs fois (exemple : individus d'un ménage).

### Variables

Collectée : valeur saisie par le répondant.

Calculée : valeur calculée pendant la passation du questionnaire.

Externe : variable destinée à être valorisée avant la passation du questionnaire.

### Suggesteur

Auto-complétion.

## Correspondances entre modèles

Table de correspondance entre les classes des différents modèles

| _Concept_ | Pogues | DDI | Eno | Lunatic |
| --- | --- | --- | --- | --- |
| Questionnaire | `Questionnaire` | `Questionnaire` | `EnoQuestionnaire` | `Questionnaire` |

...

_(TODO)_