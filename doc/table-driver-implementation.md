#Implementation of the `Table` driver

##General approach
First of all, the conceptual model behind the `Table` driver has two layers :

- headers
- body

![Grid has two layers : headers and body](/img/grid-model.png)

So, when implementing the `Table` driver inside a `models.xsl` output implementation, one has to first deal with the headers, then with the body.

Both layers are handled the same way :

- First getting all the lines through a specific getter.
- Second dealing with lines through another specific getter, one by one.
- Then, for each line dealing with cells one by one through the common pattern to retrieve drivers.

##Getting all the lines
*Examples are based on the ddi2fr implementation*

The ddi source exposes specific getters to retrieve all lines :

- `enofr:get-header-lines($source-context)` retrieves lines for headers
- `enofr:get-body-lines($source-context)` retrieves lines for headers

**This getters are not made for dealing with the line contents (see below) but only for iterating over lines.**

In face both these getters don't return line contents but only line labels.

Iterating could be done through a classic `xsl:for-each` element.

##Dealing with lines one by one
*Examples are based on the ddi2fr implementation*

The ddi source exposes specific getters to retrieve line contents :

- `enofr:get-header-line($source-context,position)` retrieves lines for headers
- `enofr:get-body-line($source-context,position)` retrieves lines for headers

A position parameter is used to define which line content should be returned.

##Dealing with line content using the common pattern
*Examples are based on the ddi2fr implementation*

The upper getters should be used to deal with the line contents through the 'common pattern' :

    <xsl:apply-templates   
        select="enofr:get-body-line($source-context, position())" 
    mode="source">

This way drivers will be fired and catched the same way as "simple" questions (aka : not grid ones).

##Pluging all together
*Examples are based on the ddi2fr implementation*

First one iterates on lines, then dealing with lines one by one :

    <!--Iteration over lines -->
    <xsl:for-each select="enofr:get-header-lines($source-context)">
        <!-- Dealing with line content
        <xsl:apply-templates   
             select="enofr:get-body-line($source-context, position())" 
        mode="source">

One should care about the xpath context : Inside an `xsl:for-each` element, the xpath context is changed for the temporary tree build upon the `@select` value.
Then when calling back to the source interface (`mode='source'`), the driver tree needs to be passed through a param.
Usually like this :

    <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
          <xsl:with-param name="driver" select="." tunnel="yes"/>
    </xsl:apply-templates>

But inside an `xsl:for-each`, the `.` will refer to the temporary tree and not anymore to the driver tree.
So, one should save the driver tree before, using a xsl:variable is a common way to do this :

    <xsl:variable name="driver-tree">
	    <xsl:copy-of select="."/>
    </xsl:variable>
    
Then simply call back with this variable as param :

    <xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
    	<xsl:with-param name="driver" select="$driver-tree" tunnel="yes"/>
    </xsl:apply-templates>