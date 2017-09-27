# Eno evolutions from ddi2pfd - impacts on ddi2fr

## d:Instruction
* **Extension of the xf-output driver**  
**Previously**, not all d:Instructions were linked to the xf-output driver, tooltip were excluded as all d:Instructions in question body.  
**After evolution**, all d:Instruction are now linked to the xf-output driver.
So in ddi2fr new xf-output drivers will be fired, see [Outputting Instructions](eno-pipeline-technical-details.md) for technical details.

* **Deconcatenation of question and instruction label**  
**Previously**, d:Instruction inside a question body had their label concatened with the question label.
The enoddi:get-label inGetter, on a question driver (QuestionTable, MultipleQuestion, xf-input, xf-select, xf-select1,...) context  will return a concatenation of question and possible instruction label. As no driver was fired from the instruction (see above), no inGetter avalaible for getting only the d:Instruction label.  
**After evolution**, enoddi:get-label on a question driver context will only return the question label. And with the explicit and exhaustive xf-output driver, the enoddi:get-label will return the d:Instruction label in a appropriate xf-output context.

## d:IfThenElse
* **A new driver for the r:Description**  
**Previously**, the r:Description of a d:IfThenElse was completely silent (no driver linked, nor inGetter to retrieve the value).  
**After evolution**, r:Description of d:IfThenElse will fire a xf-output driver. This new driver output needs to be handled in ddi2fr (deactivate ?).
