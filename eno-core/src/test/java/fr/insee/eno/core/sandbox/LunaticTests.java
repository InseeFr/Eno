package fr.insee.eno.core.sandbox;

import fr.insee.eno.core.Constant;
import fr.insee.eno.core.output.LunaticWriter;
import fr.insee.lunatic.conversion.JsonDeserializer;
import fr.insee.lunatic.exception.SerializationException;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LunaticTests {

    @Test
    void componentType() {
        InputNumber inputNumber = new InputNumber();
        inputNumber.setComponentType(ComponentTypeEnum.TEXTAREA);
        // This enum shouldn't exist, Lunatic serializer should automatically write this value
        // (nothing prevents you from writing the wrong component type...)
        assertNotEquals(ComponentTypeEnum.INPUT_NUMBER, inputNumber.getComponentType());
    }

    void helloLunaticQuestionnaire() {
        // New Lunatic questionnaire
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        // Questionnaire level metadata
        lunaticQuestionnaire.setEnoCoreVersion("3.0.0-SNAPSHOT");
        lunaticQuestionnaire.setModele("FOO");
        lunaticQuestionnaire.setPagination("question");
        lunaticQuestionnaire.setGeneratingDate("No restriction on date format");
        // NEW: complex object for labels
        LabelType lunaticLabel = new LabelType();
        lunaticLabel.setValue("Hello questionnaire");
        lunaticLabel.setType("I can write anything I want here.");
        lunaticQuestionnaire.setLabel(lunaticLabel);
        // Variables list
        List<IVariableType> lunaticVariables = lunaticQuestionnaire.getVariables();
        // Add a variable
        IVariableType lunaticVariable = new VariableType();
        lunaticVariable.setName("FOO_VARIABLE");
        lunaticVariable.setComponentRef("azerty");
        lunaticVariables.add(lunaticVariable);
        // Components
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        // Sequence
        Sequence sequence = new Sequence();
        ComponentTypeEnum sequenceType = ComponentTypeEnum.valueOf("SEQUENCE");
        sequence.setComponentType(sequenceType);
        // Declaration
        DeclarationType declaration = new DeclarationType();
        DeclarationPositionEnum declarationPosition = DeclarationPositionEnum.valueOf("AFTER_QUESTION_TEXT");
        declaration.setPosition(declarationPosition);
    }

    void lunaticOptions() {
        Options optionA = new Options();
        optionA.setValue("Option A value");
        optionA.setLabel(new LabelType());
        optionA.getLabel().setValue("Option A label");
        optionA.getLabel().setType(Constant.LUNATIC_LABEL_VTL_MD);
        Options optionB = new Options();
        optionB.setValue("Option B value");
        optionB.setLabel(new LabelType());
        optionB.getLabel().setValue("Option B label");
        optionB.getLabel().setType(Constant.LUNATIC_LABEL_VTL_MD);
    }

    void lunaticTable_headerOnly() throws SerializationException, IOException {
        Table table = new Table();
        //
        table.setPositioning("HORIZONTAL");
        // Note: lines roster is currently useless
        LinesRoster linesRoster = new LinesRoster();
        linesRoster.setMin(new LabelType());
        linesRoster.getMin().setValue("1");
        linesRoster.getMin().setType(Constant.LUNATIC_LABEL_VTL_MD);
        linesRoster.setMax(new LabelType());
        linesRoster.getMax().setValue("5");
        linesRoster.getMax().setType(Constant.LUNATIC_LABEL_VTL_MD);
        //
        HeaderType headerType0 = new HeaderType();
        headerType0.setValue("Left column value");
        headerType0.setLabel(new LabelType());
        headerType0.getLabel().setValue("Left column label");
        headerType0.getLabel().setType(Constant.LUNATIC_LABEL_VTL_MD);
        //
        HeaderType headerType1 = new HeaderType();
        headerType1.setValue("Column 1 value");
        headerType1.setLabel(new LabelType());
        headerType1.getLabel().setValue("Column 1 label");
        headerType1.getLabel().setType(Constant.LUNATIC_LABEL_VTL_MD);
        //
        table.getHeader().add(headerType0);
        table.getHeader().add(headerType1);

        //
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().add(table);
        LunaticWriter.writeJsonQuestionnaire(questionnaire,
                Path.of("src/test/resources/out/lunatic/testTable.json"));
    }

    void tableauTIC() throws IOException, SerializationException {
        Table table = new Table();
        //
        table.setId("l8u8d67h");
        table.setMandatory(false);
        table.setPage("24");
        table.setPositioning("HORIZONTAL");
        table.setLabel(new LabelType());
        table.getLabel().setValue(
                "➡ 1. Tableau TIC - répartition du nb habitants, comparaison question INTEGER");
        table.getLabel().setType(Constant.LUNATIC_LABEL_VTL_MD);
        //
        // Skipped: declarations conditionFilter controls hierarchy bindingDependencies
        //
        HeaderType headerLeft = new HeaderType();
        headerLeft.setLabel(new LabelType());
        headerLeft.getLabel().setValue("");
        headerLeft.getLabel().setType(Constant.LUNATIC_LABEL_VTL_MD);
        table.getHeader().add(headerLeft);
        //
        HeaderType headerColumn1 = new HeaderType();
        headerColumn1.setLabel(new LabelType());
        headerColumn1.getLabel().setValue("Nombre de personnes, entre 0 et 20");
        headerColumn1.getLabel().setType(Constant.LUNATIC_LABEL_VTL_MD);
        table.getHeader().add(headerColumn1);
        //
        List<String> bodyValues = List.of("1", "2", "3", "4");
        List<String> bodyLabels = List.of(
                "moins de 15",
                "de 16 à 17 ans",
                "de 18 à 19 ans",
                "20 et plus");
        for (int i=0; i<4; i++) {
            String value = bodyValues.get(i);
            String label = bodyLabels.get(i);
            BodyLine bodyLine = new BodyLine();
            BodyCell bodyLeft = new BodyCell();
            bodyLeft.setValue(value);
            bodyLeft.setLabel(new LabelType());
            bodyLeft.getLabel().setValue(label);
            bodyLeft.getLabel().setType(Constant.LUNATIC_LABEL_VTL_MD);
            BodyCell bodyColumn1 = new BodyCell();
            bodyColumn1.setComponentType("InputNumber");
            bodyColumn1.setMin(0d);
            bodyColumn1.setMax(20d);
            bodyColumn1.setDecimals(BigInteger.ZERO);
            bodyColumn1.setId("l8u8d67h-QOP-"+value);
            bodyLine.getBodyCells().add(bodyLeft);
            bodyLine.getBodyCells().add(bodyColumn1);
            table.getBodyLines().add(bodyLine);
        }

        //
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().add(table);
        LunaticWriter.writeJsonQuestionnaire(questionnaire,
                Path.of("src/test/resources/out/lunatic/TICTable.json"));
    }

    @Test
    @Disabled("Lunatic-Model deserialization doesn't work for now.")
    void deserializeQuestionnaire() throws SerializationException {
        //
        JsonDeserializer jsonDeserializer = new JsonDeserializer();
        URL fileUrl = this.getClass().getClassLoader()
                .getResource("expected/lunatic/l20g2ba7_expected.json");
        assert fileUrl != null;
        String filePath = fileUrl.getPath();
        //
        Questionnaire questionnaire = jsonDeserializer.deserialize(filePath);
        //
        assertNotNull(questionnaire);
    }

    @Test
    void nullValueOnDoubleAttribute() {
        Double d = null;
        BodyCell bodyCell = new BodyCell();
        bodyCell.setMax(d);
        assertNull(bodyCell.getMax());
    }

}
