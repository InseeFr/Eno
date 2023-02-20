# DDI Java

Module pour générer les classes du standard DDI en Java.

## Code source DDI

Le standard DDI est implémenté via des schémas XSD.

:arrow_right: [Sources XSD](https://ddialliance.org/Specification/DDI-Lifecycle/3.3/).

## Fonctionnement du module

1. Édition des sources XSD pour définir le package de base des classes.
1. Génération des classes java avec le [`SchemaCompiler` de Apache XMLBeans](https://xmlbeans.apache.org/guide/Tools.html#scomp).

Voir les détails technique dans le [README](https://github.com/InseeFr/Eno/ddi-beans/README.md).
