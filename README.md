# Eno – Questionnaire generator

This is a forked repo of ENO to implement Pogues Back-Office in eno v1 master

## Introduction

Eno is a tool that generates survey questionnaires starting from their formal description in [DDI](http://ddialliance.org/).

Due to its modular design, Eno can create questionnaires in different formats from the same DDI description. Currently, Eno generates XForms web questionnaires that can be executed on [Orbeon Forms Runner](http://www.orbeon.com/). PDF questionnaires is under development.

## Principles : 
 
The generation of XForms forms is performed using a number of XSLT transformations from a DDI input managed by a Ant script.
 

The main Ant build file is [/src/main/scripts/build_non_regression.xml] and its main target is "full".

 
This file includes the name forms to generate.

 
The DDI input and configuration are found in a subdirectory of [/questionnaire/] with the same name.

 
Generic Ant properties which drive the transformations are stored in [/src/main/scripts/build_configuration.xml]

 
The generated XForms output will be located in a subdirectory of [/questionnaire/].


## Getting Started
 
### Prior : 
 
 * Apache Ant. You can download Ant from Apache, see also : [Ant Apache](http://ant.apache.org/)
 
 * Source code from github.com.
 
Then you have to donwload Java Libraries : 

* Ant-Contrib 0.6 or higher (collection of tasks for Apache Ant), see also : [Ant contrib](http://ant-contrib.sourceforge.net/)
* Saxon HE 9.X or higher (The XSLT and XQuery Processor), see also : [Saxon](https://mvnrepository.com/artifact/net.sf.saxon/Saxon-HE)
* Saxon-unpack, included as standard in each Saxon edition
* Common lang 3 or higher (for the non regression test only), see also : [Apache Commons lang](https://commons.apache.org/proper/commons-lang/)

Paste the ".jar" file in a "/lib/" folder at the Eno project root.
 

### Example : 
 
You can find on the subdirectory [/questionnaire/], the questionnaire example "Simpsons" specified in the DDI format.


To generate the XForms form, execute the main ant build file [/src/main/scripts/build_non_regression.xml]


The generated XForms form should be created in [/questionnaire/simpsons/Xforms/v1/] folder.
 

### Non regression test : 
 
The expected XForms form file for the Simpsons questionnaire is present in [/questionnaire/simpsons/] folder, a specific ant target : "full-with-test" generates Xform file and calculates the difference with the expected Xforms file.


The difference file [questionnaire/simpsons/Xforms/v1/diff.txt] specifed, if there has, index at which the file begins to differ and the difference beetwen the generated Xform file and the expected Xform file.

## Road Map

New output format : PDF for paper questionnaire
