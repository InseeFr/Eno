

# Models.xsl - Development guidelines

## Overview
Each models.xsl stylesheet is the implementation of the driver elements for a specific output format.
It will be directly imported by the final transformation produced by the ENO configuration process. Thus to be able to interact correctly with the whole ENO generation process, when designing new output format and implementing new models.xsl stylesheet, one should respect some XSLT syntax constraints design principles.

## XSLT syntax and design principles of models.xsl
* Templates from models.xsl will be applied an a 'driver-tree'

   Then all match attribute value should be valid XPath on this 'driver tree', often simple driver-name.
   ```xslt
   <xslt:template match="driver-name"...
   ```   
* Each template must be in "model" mode.
   
   ```xslt   
   <xslt:template match="driver-name" mode="model">
   ```
* Each template should have a 'source-context' parameter declaration with tunnel mode on

   As the models stylesheet is applied on a driver tree, this parameter permits to retrieve the real input tree in the context of the template.
   ```xslt   
   <xslt:template match="driver-name" mode="model">
      <xsl:param name="source-context" as="item()" tunnel="yes"/>
   ```
* Getting back to the input tree iteration is done by applying templates on the source-context parameter through the `eno:child-fields(input-tree)` XPath function

   The `eno:child-fields(input-tree)` XPath function offers tree navigation overwrite mechanism driven by the navigation-tree.fods sheet.
   ```xslt   
   <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="." tunnel="yes"/>
   </xsl:apply-templates>  
   ```
   Note the use of the parameter named 'driver' to keep the driver tree, tunnel mode must be on for this parameter too.

* Adding new driver to the driver tree is done through the XPath function `eno:append-empty-element('Driver-name', driver-tree)` during the callback to the input tree

   ```xslt   
   <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
            <xsl:with-param name="driver" select="eno:append-empty-element('Instance', .)" tunnel="yes"/>
   </xsl:apply-templates>  
   ```
   This offers mechanism to modify the matching context for the next appended driver.
   In this example, the next appended driver will match `Instance/*` XPath over `*`. Thus, one could have several templates for the same driver depending of the driver's XPath context (ie: being or not child of an Instance driver in this example).

