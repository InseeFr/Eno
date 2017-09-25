

# Outputting Instructions

## The DDI usecase
### Issue overview
In the DDI input format, Instructions are based on a specific element (d:Instruction), but via the ENO pattern, the output interface will handle them through the `xf-output` driver (FIXME - The driver should be renamed, it's too much linked to xforms output semantic).  
In a formal descriptive standard as DDI, the location of the instructions doesn't really matter, but in output format dedicated to collect process (xforms, pdf), location matters (after question title, before, at the end of a question,...).
So here it is a quick guide to help developpers to deal with instructions and their location in output format generated from DDI files.

### Instruction semantic

Instructions through the input interface will fire the `xf-output` driver.
Instructions could be typed in the DDI side (using the d:InstructionName element).  
This type will be avalaible to the ENO output interface through the `enoddi:get-format` getter. One may plug its own output getter (prefixed as eno\[output format nickname\], as enopdf).

### Instructions attached to question

#### Instruction location
The driver `xf-output` will arrive based on the order of the question body elements in the input format.  
In formal standard, as DDI, no semantic is attached to this order, aka: in DDI, Instruction must be the last elements of the question body.
But in output format used for collecting data (xforms, pdf), the layout order is meaningfull (at least for design purpose) and one needs to be able to output instructions in a specific location (after the question title, end of the question,...) based on the result of a `enoddi:get-format` call (aka: based on the instruction type).

#### Technical implementation
When Instructions need to be outputted at specific location, one needs specific getters.
In the DDI input interface, there are two in-getters for this : `enoddi:get-instructions` and `enoddi:get-instructions-by-format`.  
And in the output format, specific out-getter implementations based on the output location for instructions.  
In the pdf output interface, there are two drivers for this :
* `enopdf:get-after-question-title-instructions`
* `enopdf:get-end-question-instructions`

One needs to call explicitely these getters, inside the question body drivers where corresponding instructions must be outputted, to catch needed instructions.
Then one needs to apply-templates on the result of these calls in `source` mode.  
ex: 
```xslt
<xsl:apply-templates select="enopdf:get-after-question-title-instructions($context)" mode="source">
```

**Warning**  
One needs to deactivate standard drivers for instructions or output interface will output instructions twice (one during the explicit getter call and one during the implicit driver mechanism).
To deal with this issues, a simple way is using a silent driver like `noInstruction` (name is not reserved and one could choose its own, see the examples below where the name choosen is used).  
Inside the question body, when calling recursive templates on the question child, just add the silent driver like this :  
*models.xsl, inside the question driver body implementation*  
```xslt
<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
  <xsl:with-param name="driver" select="eno:append-empty-element('noInstruction', .)" tunnel="yes"/>
</xsl:apply-templates>
```

Then simply deactivate standard driver for instruction like this (inside models.xsl) :  
*models.xsl, driver implementation*  
```xslt
<xsl:template match="noInstruction//xf-output" mode="models"/>
```

### Instructions attached to Module or SubModule
During ConctrolConstruct sequencing, there is no constraint on Instruction location and simply basing their output location on the sequence of the driver tree should be sufficient.  
**It means that outside question body, one should use the standard driver mechanism to ouptut instructions.**

## Other input format usecases
In other input format, if instruction location is meaningfull just use the standard driver mechanism. But if instructions location is meaningless, use a similar mechanism than the one above (specific output drivers based on instruction locations, silent drivers).
