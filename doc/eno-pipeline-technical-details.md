# Eno pipeline technical details

## Eno pipelines

### Pre-processing
* Input
Expect a file in a specific location.
Ant implementation: ${questionnaires.home}/${survey-name}/ddi/${survey-name}.xml
(Expect a param file in a specific location)
* Output
  1 or more files (see derefencing.xsl) in a specific location with a specific name
    Ant implementation: dir=${temp.home}, files' suffix='-final.tmp'

### EnoConfiguration
* Input
Expect fods files and xslt files with specific names, formats and locations
An fods2xsl instanciation for each fods files (+1 if debug mode)
An incorporation instanciation for each fods and xslt files
Expected fods files:
  * InGetterLibrairy: fods file listing inGetter names and params
  Ant implementation:
  ${xslt.home}/inputs/${in-format}/functions.fods
  format='InGetterLibrairy'

  * DriverFlow: fods file listing input matching xpath and its associated driver
  Ant implementation:
    ${xslt.home}/transformations/${in-format}2${out-format}/drivers.fods
  format='DriverFlow'
  * OutGetterImplementation: fods file listing outGetter names and the inGetters associated
      Ant implementation:
      ${xslt.home}/transformations/${in-format}2${out-format}/functions.fods
      format='OutGetterImplementation'
  * TreeNavigation: fods file listing input matching xpath and its extended children
      Ant implementation:
      ${xslt.home}/transformations/${in-format}2${out-format}/tree-navigation.fods
      format='TreeNavigation'
  * InGetterImplementation: fods file listing input matching xpath, associated inGetter name and Xpath value
      Ant implementation:
      ${xslt.home}/inputs/${in-format}/template.fods
      format='InGetterImplementation'

  Expected xslt files:
  InGetterExtension: xslt file which extended inGetter implementation for complex cases not matching fods file pattern.
  Ant implementation: ${xslt.home}/transformations/${in-format}2${out-format}/${in-format}2${out-format}-fixed.xsl

  For Debug mode:  
  DriverImplementation: an xslt files with implementation template based on driver matching
  XSLT implementation:
  ../../outputs/${out-format}/models.xsl relatively to the in2out.xsl location ($out-format has a static value in the xslt file)
  ...To be done...

### In2Out
* Input:
Expect an input file with a specific name and a specific xslt file.
Ant implementation:
input file's suffix='-final.tmp'
xslt file= ${xslt.home}/transformations/${in-format}2${out-format}/${in-format}2${out-format}.xsl
(Expect a param file in a specific location)
* Output:
1 file in a specific location with a specific name.
Ant implementation:
 ${temp.home}/${out-format}/${form-name}/basic-form.tmp

### PostProcessing
* Input:
Expect a file with a specific name in a specific location.
Ant implementation: ${temp.home}/${out-format}/${form-name}/basic-form.tmp
(Expect a param file in a specific location)
* Output:
1 file in a specific location with a specific name.
Ant implementation:
${root-folder}/target/${survey-name}/${form-name}/form/form.${out-extension}
