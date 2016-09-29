<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xf="http://www.w3.org/2002/xforms" xmlns:fr="http://orbeon.org/oxf/xml/form-runner"
    xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ev="http://www.w3.org/2001/xml-events">

    <!-- Not used: supposed to initialize a file with the following namespaces. -->
    
    <!-- The output file generated will be xml type -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="form">
        <form xmlns:xxi="http://orbeon.org/oxf/xml/xinclude" 
            xmlns:xh="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xi="http://www.w3.org/2001/XInclude" 
            xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:saxon="http://saxon.sf.net/" xmlns:xs="http://www.w3.org/2001/XMLSchema" 
            xmlns:xbl="http://www.w3.org/ns/xbl" xmlns:xxf="http://orbeon.org/oxf/xml/xforms" xmlns:sql="http://orbeon.org/oxf/xml/sql" 
            xmlns:iatfr="http://xml/insee.fr/xslt/apply-templates/form-runner" xmlns:p="http://www.orbeon.com/oxf/pipeline" 
            xmlns:fr="http://orbeon.org/oxf/xml/form-runner" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope" 
            xmlns:xf="http://www.w3.org/2002/xforms" xmlns:fn="http://www.w3.org/2005/xpath-functions" 
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:exf="http://www.exforms.org/exf/1-0">
            <xsl:copy-of select="*"/>
        </form>
    </xsl:template>
    <xsl:template match="text()"/>

</xsl:transform>
