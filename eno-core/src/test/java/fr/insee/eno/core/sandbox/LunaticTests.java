package fr.insee.eno.core.sandbox;

import fr.insee.eno.core.output.LunaticWriter;
import fr.insee.lunatic.conversion.JSONDeserializer;
import fr.insee.lunatic.conversion.XMLLunaticFlatToJSONLunaticFlatTranslator;
import fr.insee.lunatic.conversion.XMLLunaticToXMLLunaticFlatTranslator;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LunaticTests {

    private static final String VTL_MD = "VTL|MD";

    @Test
    public void helloLunaticQuestionnaire() {
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
        SequenceType sequence = new SequenceType();
        ComponentTypeEnum sequenceType = ComponentTypeEnum.valueOf("SEQUENCE");
        sequence.setComponentType(sequenceType);
        // Declaration
        DeclarationType declaration = new DeclarationType();
        DeclarationPositionEnum declarationPosition = DeclarationPositionEnum.valueOf("AFTER_QUESTION_TEXT");
        declaration.setPosition(declarationPosition);
    }

    public void lunaticOptions() {
        Options optionA = new Options();
        optionA.setValue("Option A value");
        optionA.setLabel(new LabelType());
        optionA.getLabel().setValue("Option A label");
        optionA.getLabel().setType(VTL_MD);
        Options optionB = new Options();
        optionB.setValue("Option B value");
        optionB.setLabel(new LabelType());
        optionB.getLabel().setValue("Option B label");
        optionB.getLabel().setType(VTL_MD);
    }

    @Test
    public void lunaticTable_headerOnly() throws JAXBException, IOException {
        Table table = new Table();
        //
        table.setPositioning("HORIZONTAL");
        // Note: lines roster is currently useless
        LinesRoster linesRoster = new LinesRoster();
        linesRoster.setMin(new LabelType()); // TODO: why is there a label for min & max?
        linesRoster.getMin().setValue("1");
        linesRoster.getMin().setType("???"); // TODO: ???
        linesRoster.setMax(new LabelType());
        linesRoster.getMax().setValue("5");
        linesRoster.getMax().setType("???");
        //
        HeaderType headerType0 = new HeaderType();
        headerType0.setValue("Left column value");
        headerType0.setLabel(new LabelType());
        headerType0.getLabel().setValue("Left column label");
        headerType0.getLabel().setType(VTL_MD);
        //
        HeaderType headerType1 = new HeaderType();
        headerType1.setValue("Column 1 value");
        headerType1.setLabel(new LabelType());
        headerType1.getLabel().setValue("Column 1 label");
        headerType1.getLabel().setType(VTL_MD);
        //
        table.getHeader().add(headerType0);
        table.getHeader().add(headerType1);

        //
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().add(table);
        LunaticWriter.writeJsonQuestionnaire(questionnaire,
                Path.of("src/test/resources/out/lunatic/testTable.json"));
    }

    @Test
    public void tableauTIC() throws JAXBException, IOException {
        Table table = new Table();
        //
        table.setId("l8u8d67h");
        table.setMandatory(false);
        table.setPage("24");
        table.setPositioning("HORIZONTAL");
        table.setLabel(new LabelType());
        table.getLabel().setValue(
                "➡ 1. Tableau TIC - répartition du nb habitants, comparaison question INTEGER");
        table.getLabel().setType(VTL_MD);
        //
        // Skipped: declarations conditionFilter controls hierarchy bindingDependencies
        //
        HeaderType headerLeft = new HeaderType();
        headerLeft.setLabel(new LabelType());
        headerLeft.getLabel().setValue("");
        headerLeft.getLabel().setType(VTL_MD);
        table.getHeader().add(headerLeft);
        //
        HeaderType headerColumn1 = new HeaderType();
        headerColumn1.setLabel(new LabelType());
        headerColumn1.getLabel().setValue("Nombre de personnes, entre 0 et 20");
        headerColumn1.getLabel().setType(VTL_MD);
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
            BodyType bodyType = new BodyType();
            BodyLine bodyLeft = new BodyLine();
            bodyLeft.setValue(value);
            bodyLeft.setLabel(new LabelType());
            bodyLeft.getLabel().setValue(label);
            bodyLeft.getLabel().setType(VTL_MD);
            BodyLine bodyColumn1 = new BodyLine();
            bodyColumn1.setComponentType("InputNumber");
            bodyColumn1.setMin(0d);
            bodyColumn1.setMax(20d);
            bodyColumn1.setDecimals(BigInteger.ZERO);
            bodyColumn1.setId("l8u8d67h-QOP-"+value);
            bodyType.getBodyLine().add(bodyLeft);
            bodyType.getBodyLine().add(bodyColumn1);
            table.getBody().add(bodyType);
        }

        //
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.getComponents().add(table);
        LunaticWriter.writeJsonQuestionnaire(questionnaire,
                Path.of("src/test/resources/out/lunatic/TICTable.json"));
    }

    @Test
    public void deserializeQuestionnaire() throws JAXBException {
        //
        JSONDeserializer jsonDeserializer = new JSONDeserializer();
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
    public void flattenPairwise() throws Exception {

        XMLLunaticToXMLLunaticFlatTranslator translator = new XMLLunaticToXMLLunaticFlatTranslator();
        String lunaticXmlFlat = translator.generate(this.getClass().getClassLoader()
                .getResourceAsStream("pairwise/form-lunatic-xml-household-links.xml"));
        XMLLunaticFlatToJSONLunaticFlatTranslator translator2 = new XMLLunaticFlatToJSONLunaticFlatTranslator();
        String result = translator2.translate(lunaticXmlFlat);
        Files.writeString(Path.of("src/test/resources/pairwise/form-lunatic-xml-household-links.json"), result);
    }

}
