

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

##### Technical implementation
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
For consistency purpose, as explicit in-getters are defined to retrieve instructions, no driver are fired inside question body for instructions.
It means, that even if a question has some instructions related, no `xf-output` driver will result of the classical call below :
```xslt
<xsl:apply-templates select="eno:child-fields($source-context)" mode="source">
  <xsl:with-param name="driver" select="eno:append-empty-element('noInstruction', .)" tunnel="yes"/>
</xsl:apply-templates>
```

Instead, one should use the explicit out-getters (as `enopdf:get-end-question-instructions` for example) to retrieve these instructions.

This particular driver deactivation is done by the navigation-tree.fods configuration through the line :

Parent | children
--- | ---
`d:QuestionItem` &#124; `d:QuestionGrid` |	`*[not(self::d:InterviewerInstructionReference)]`

#### Instructions whith anchors
Some Instructions may implement an anchor mechanism (footnotes, tooltips). For these, instruction text are related to specific text portion of the question (inside question label or even inside embedded code label).
As DDI provide no specific anchor mechanism, xhtml syntax is used to specify this kind of relations.
The text portion to which the anchor is related should be embedded in a `<xhtml:a>` element whit a `@href` attribute, as `<xhtml:a href="#ftn7">A</xhmtl:a>`.
The Instruction label, to which the previous anchor referred, should be embedd in a `<xhmtl:p>` element with an `@id` corresponding to the previous `@href` value.  

**Note :**  
The id notation uses the html anchor syntax whit a `#` prefix before the id value in the `@href` value.  

Example :  
*Instruction label related to an anchor*
```xml
<d:InstructionText>
   <d:LiteralText>
     <d:Text xml:lang="fr">
       <xhtml:p id="ftn7">
         ...............
```

*Text portion with an anchor*   
```xml
<xhtml:a href="#ftn7">...</xhtml:a>
```

#####   Technical implementation

As the handling of anchors is very specific to the output format, most of the implementation should be done on the output interface.  
Though, there are the below in-getters to help:
* *enoddi:get-instruction-index*: it retrieves the index of the instruction (ie: its number ordering) based on an optional formats param (by default #all, if not it considers only instructions whit the format specified are considered for the counting).
* *enoddi:get-instruction-by-anchor-ref*: it retrieves an Instruction based on an href param.

### Instructions attached to Module or SubModule
During ConctrolConstruct sequencing, there is no constraint on Instruction location and simply basing their output location on the sequence of the driver tree should be sufficient.  
**It means that outside question body, one should use the standard driver mechanism to ouptut instructions.**

## Other input format usecases
In other input format, if instruction location is meaningfull just use the standard driver mechanism. But if instructions location is meaningless, use a similar mechanism than the one above (specific output drivers based on instruction locations).
