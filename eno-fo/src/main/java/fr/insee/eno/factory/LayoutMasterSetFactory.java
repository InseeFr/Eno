package fr.insee.eno.factory;


import org.w3.x1999.xsl.format.*;
import org.w3.x1999.xsl.format.impl.LayoutMasterSetDocumentImpl.LayoutMasterSetImpl;

import java.util.List;

import static fr.insee.eno.factory.FOUtils.valueOf;

public class LayoutMasterSetFactory {

    public static SimplePageMasterDocument.SimplePageMaster createSimplePageMaster(
            String name,
            boolean onlyRegionBody
    ){
        RegionBodyDocument.RegionBody regionBody =  RegionBodyDocument.RegionBody.Factory.newInstance();
        regionBody.setMargin(valueOf("11mm"));
        regionBody.setColumnCount(valueOf(1));
        RegionBeforeDocument.RegionBefore regionBefore = RegionBeforeDocument.RegionBefore.Factory.newInstance();
        regionBefore.setDisplayAlign(RegionBeforeDocument.RegionBefore.DisplayAlign.BEFORE);
        regionBefore.setExtent(valueOf("10mm"));
        regionBefore.setPrecedence(RegionBeforeDocument.RegionBefore.Precedence.TRUE);
        regionBefore.setRegionName(valueOf("region-before-" + name));

        RegionAfterDocument.RegionAfter regionAfter = RegionAfterDocument.RegionAfter.Factory.newInstance();
        regionAfter.setDisplayAlign(RegionAfterDocument.RegionAfter.DisplayAlign.BEFORE);
        regionAfter.setExtent(valueOf("10mm"));
        regionAfter.setPrecedence(RegionAfterDocument.RegionAfter.Precedence.TRUE);
        regionAfter.setRegionName(valueOf("region-after-" + name));

        RegionStartDocument.RegionStart regionStart = RegionStartDocument.RegionStart.Factory.newInstance();
        regionStart.setDisplayAlign(RegionStartDocument.RegionStart.DisplayAlign.BEFORE);
        regionAfter.setExtent(valueOf("10mm"));
        regionAfter.setRegionName(valueOf("region-start-" + name));

        RegionEndDocument.RegionEnd regionEnd = RegionEndDocument.RegionEnd.Factory.newInstance();
        regionEnd.setDisplayAlign(RegionEndDocument.RegionEnd.DisplayAlign.BEFORE);
        regionEnd.setExtent(valueOf("10mm"));
        regionEnd.setRegionName(valueOf("region-end-" + name));

        SimplePageMasterDocument.SimplePageMaster simplePageMaster = SimplePageMasterDocument.SimplePageMaster.Factory.newInstance();
        simplePageMaster.setMargin(valueOf("5mm"));
        simplePageMaster.setMasterName(valueOf("page-"+name));
        simplePageMaster.setPageHeight(valueOf("297mm"));
        simplePageMaster.setPageWidth(valueOf("210mm"));
        simplePageMaster.setReferenceOrientation(SimplePageMasterDocument.SimplePageMaster.ReferenceOrientation.X_0);


        simplePageMaster.setRegionBody(regionBody);
        if(!onlyRegionBody){
            simplePageMaster.setRegionBefore(regionBefore);
            simplePageMaster.setRegionAfter(regionAfter);
            simplePageMaster.setRegionStart(regionStart);
            simplePageMaster.setRegionEnd(regionEnd);
        }

        return simplePageMaster;
    }

    public static ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference createConditionalPageMasterReference(
            String masterReference,
            ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.OddOrEven.Enum oddOrEven,
            ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.PagePosition.Enum pagePosition,
            ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.BlankOrNotBlank.Enum blankOrNotBlank
    ) {
        ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference conditionalPageMasterReference = ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.Factory.newInstance();
        if(masterReference != null) conditionalPageMasterReference.setMasterReference(valueOf(masterReference));
        if(oddOrEven != null) conditionalPageMasterReference.setOddOrEven(oddOrEven);
        if(pagePosition != null) conditionalPageMasterReference.setPagePosition(pagePosition);
        if(blankOrNotBlank != null) conditionalPageMasterReference.setBlankOrNotBlank(blankOrNotBlank);
        return conditionalPageMasterReference;
    }

    public static LayoutMasterSetDocument.LayoutMasterSet createLayoutMasterSetDocumentDefault(){
        LayoutMasterSetDocument.LayoutMasterSet layoutMasterSet = LayoutMasterSetDocument.LayoutMasterSet.Factory.newInstance();

        SimplePageMasterDocument.SimplePageMaster simplePageMasterEven =  createSimplePageMaster("even-default", false);
        SimplePageMasterDocument.SimplePageMaster simplePageMasterOdd =  createSimplePageMaster("odd-default", false);
        SimplePageMasterDocument.SimplePageMaster simplePageMasterA4Empty =  createSimplePageMaster("A4-empty", true);

        ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference page1 = createConditionalPageMasterReference(
                "page-odd-default",
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.OddOrEven.ODD, null, null);

        ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference page2 = createConditionalPageMasterReference(
                "page-even-default",
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.OddOrEven.EVEN,
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.PagePosition.FIRST, null);

        ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference page3 = createConditionalPageMasterReference(
                "page-even-default",
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.OddOrEven.EVEN,
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.PagePosition.REST, null);

        ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference page4 = createConditionalPageMasterReference(
                "page-even-default",
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.OddOrEven.EVEN,
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.PagePosition.LAST,
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.BlankOrNotBlank.NOT_BLANK);
        ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference page5 = createConditionalPageMasterReference(
                "page-A4-empty",
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.OddOrEven.EVEN,
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.PagePosition.LAST,
                ConditionalPageMasterReferenceDocument.ConditionalPageMasterReference.BlankOrNotBlank.BLANK);


        layoutMasterSet.getSimplePageMasterList().addAll(List.of(simplePageMasterEven, simplePageMasterOdd, simplePageMasterA4Empty));
        PageSequenceMasterDocument.PageSequenceMaster pageSequenceMaster = layoutMasterSet.addNewPageSequenceMaster();
        pageSequenceMaster.setMasterName(valueOf("A4"));
        RepeatablePageMasterAlternativesDocument.RepeatablePageMasterAlternatives repetable = pageSequenceMaster.addNewRepeatablePageMasterAlternatives();
        repetable.getConditionalPageMasterReferenceList().addAll(List.of(page1, page2, page3, page4, page5));

        return layoutMasterSet;
    }
}
