package fr.insee.eno.core;

import datacollection33.*;
import datacollection33.SequenceType;
import datacollection33.impl.TextTypeImpl;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.mappers.DDIMapperTest;
import fr.insee.eno.core.mappers.Mapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Variable;
import fr.insee.eno.core.parsers.DDIParser;
import fr.insee.eno.core.reference.DDIIndex;
import fr.insee.lunatic.model.flat.*;
import instance33.DDIInstanceDocument;
import logicalproduct33.VariableGroupType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import reusable33.IDType;
import reusable33.ReferenceType;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sandbox
 */
public class HelloTest {

    @Test
    public void hello() {
        //
        DDIInstanceDocument newInstance = DDIInstanceDocument.Factory.newInstance();
        System.out.println("Hello !");
    }

    @Test
    public void ddiObjects() throws IOException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIParser.parse(
                this.getClass().getClassLoader().getResource("l10xmg2l.xml"));
        //
        ddiInstanceDocument.getDDIInstance().getCitation().getTitle().getStringArray(0).getStringValue();
        //
        logicalproduct33.VariableType v;
        //
        VariableGroupType firstVariableGroupType = ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0)
                .getVariableSchemeArray(0).getVariableGroupArray(0);
        List<ReferenceType> referenceList = firstVariableGroupType.getVariableGroupReferenceList();
        referenceList.get(0).getIDArray(0).getStringValue();
        //
        SequenceType s;
        ReferenceType r;
        IfThenElseType ite;
        //
        QuestionConstructType q;
        //
        StatementItemType st;
        //((TextTypeImpl) ((LiteralTextType) st.getDisplayTextArray(0).getTextContentArray(0)).getText()).getStringValue();
        // Note: why does TextType don't have the getStringValue() method, but the implementation has it?
        //
        ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0)
                .getInterviewerInstructionSchemeArray(0).getInstructionArray(0)
                .getInstructionNameArray(0).getStringArray(0).getStringValue();
        InstructionType instructionType;
        QuestionItemType questionItemType;
        //
        ddiInstanceDocument.getDDIInstance().getResourcePackageArray(0).getQuestionSchemeArray(0);
    }

    @Test
    public void getDDIIndexUsingSpel() throws IOException {
        //
        DDIIndex ddiIndex = new DDIIndex();
        ddiIndex.indexDDI(DDIParser.parse(
                DDIMapperTest.class.getClassLoader().getResource("l10xmg2l.xml")));
        //
        Expression expression = new SpelExpressionParser()
                .parseExpression("#index.get(\"kzwoti00\")");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("index", ddiIndex);

        //
        logicalproduct33.VariableType ddiVariable = expression.getValue(context, logicalproduct33.VariableType.class);
        assertNotNull(ddiVariable);
        assertEquals("COCHECASE",
                ddiVariable.getVariableNameArray(0).getStringArray(0).getStringValue());
    }

    @Test
    public void xmlBeansAndDDI() {
        IDType idType = IDType.Factory.newInstance();
        String stringId = "foo";
        idType.setStringValue(stringId);
        assertEquals(stringId, idType.getStringValue());
    }

    @Test
    public void helloLunaticQuestionnaire() {
        // New Lunatic questionnaire
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        //
        lunaticQuestionnaire.setEnoCoreVersion("3.0.0-SNAPSHOT");
        lunaticQuestionnaire.setModele("TOTO");
        lunaticQuestionnaire.setLabel("i'm a Lunatic questionnaire :)");
        lunaticQuestionnaire.setPagination("question");
        //
        //lunaticQuestionnaire.setGeneratingDate("");
        // Variables list
        List<IVariableType> lunaticVariables = lunaticQuestionnaire.getVariables();
        // Add a variable
        IVariableType lunaticVariable = new VariableType();
        lunaticVariable.setName("foo");
        lunaticVariable.setComponentRef("azerty");
        lunaticVariables.add(lunaticVariable);
        //
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        DeclarationPositionEnum foo = DeclarationPositionEnum.valueOf("AFTER_QUESTION_TEXT");
        DeclarationType foo2;
        ComponentTypeEnum sequence = ComponentTypeEnum.valueOf("SEQUENCE");
        InputNumber ff3;

        //
        assertEquals(1, lunaticQuestionnaire.getVariables().size());
        assertEquals("foo", lunaticQuestionnaire.getVariables().get(0).getName());
    }

    @Test
    public void chainedSpelExpression() {
        Variable variable = new Variable();
        variable.setName("hello");
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        String hello = spelExpressionParser.parseExpression("getName()").getValue(variable, String.class);
        assertNotNull(hello);
        assertEquals(hello, "hello");
        spelExpressionParser.parseExpression("setName(\"foo\")").getValue(variable);
        assertEquals("foo", variable.getName());
        spelExpressionParser.parseExpression("setName(\"bar\")").getValue(variable);
        assertEquals("bar", variable.getName());
    }

    @Test
    public void modifyListContentWithSpel() {
        // Idea : convert a list of something (e.g. Variable) into a list of something else (e.g. String)
        //
        Variable v1 = new Variable();
        v1.setName("foo");
        Variable v2 = new Variable();
        v2.setName("bar");
        List<Variable> variableList = new ArrayList<>();
        variableList.add(v1);
        variableList.add(v2);
        // desired output
        List<String> stringList1 = variableList.stream().map(Variable::getName).toList();
        // do it with spel
        String stringExpression = "![getName()]";
        @SuppressWarnings("unchecked")
        List<String> stringList = new SpelExpressionParser().parseExpression(stringExpression)
                .getValue(variableList, List.class);
        //
        assertNotNull(stringList);
        assertEquals(stringList1, stringList);
    }

    @Test
    public void usingIndexOnListWithSpel() {
        // Idea : convert a list of something (e.g. Variable) into a list of something else (e.g. String)
        //
        Variable v1 = new Variable();
        v1.setName("foo");
        Variable v2 = new Variable();
        v2.setName("bar");
        //
        Map<String, Variable> indexMap = new HashMap<>();
        indexMap.put("id1", v1);
        indexMap.put("id2", v2);
        //
        Variable variableReference1 = new Variable();
        variableReference1.setName("id1");
        Variable variableReference2 = new Variable();
        variableReference2.setName("id2");
        List<Variable> referenceList = new ArrayList<>();
        referenceList.add(variableReference1);
        referenceList.add(variableReference2);
        //
        List<Variable> expected = referenceList.stream().map(referenceVariable -> indexMap.get(referenceVariable.getName())).toList();
        //
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("index", indexMap);
        String stringExpression = "![#index.get(#this.getName())]";
        @SuppressWarnings("unchecked")
        List<Variable> result = new SpelExpressionParser().parseExpression(stringExpression)
                .getValue(context, referenceList, List.class);
        //
        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void helloSpel() {
        // Given a Eno questionnaire
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setId("hello");

        // Put the Eno questionnaire id in a Lunatic questionnaire using Spring expression language
        Object value = enoQuestionnaire.getId();
        Questionnaire lunaticQuestionnaire = new Questionnaire();

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("param", value);

        (new SpelExpressionParser().parseExpression("setId(#param)")).getValue(context, lunaticQuestionnaire);

        //
        assertEquals("hello", lunaticQuestionnaire.getId());
    }

    @Test
    public void spelInlineVariable() {
        String fooString = new SpelExpressionParser().parseExpression("true ? 'hello' : 'goodbye'").getValue(String.class);
        assertEquals("hello", fooString);
    }

    public void runtimeCast() {
        Class<EnoQuestionnaire> clazz = EnoQuestionnaire.class;
        SequenceType foo;
    }

    @Test
    public void intType() {
        assertTrue(int.class.isAssignableFrom(int.class));
    }

    @Test
    public void intConversion() {
        int expected = 10;
        int converted = Integer.parseInt("10");
        assertEquals(expected, Integer.parseInt("10"));
    }

    @Test
    public void introspectionAndAbstract() {
        FooSuper foo = new FooSuper();
        foo.setA(1);
        foo.setS(7);
        //
        BeanWrapper beanWrapper = new BeanWrapperImpl(foo);
        for (Iterator<PropertyDescriptor> iterator = Mapper.propertyDescriptorIterator(beanWrapper); iterator.hasNext(); ) {
            System.out.println(iterator.next().getName());
        }
        //
        System.out.println(foo.getClass().getSuperclass());
        //
        TypeDescriptor sTypeDescriptor = beanWrapper.getPropertyTypeDescriptor("a");
        assertNotNull(sTypeDescriptor);
        DDI ddiAnnotation = sTypeDescriptor.getAnnotation(DDI.class);
        assertNotNull(ddiAnnotation);
        assertEquals("hello", ddiAnnotation.field());
    }

}
