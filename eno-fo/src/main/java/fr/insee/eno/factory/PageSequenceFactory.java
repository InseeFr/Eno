package fr.insee.eno.factory;

import org.w3.x1999.xsl.format.*;
import org.w3.x1999.xsl.format.impl.PageSequenceDocumentImpl;

import java.util.List;

import static fr.insee.eno.factory.FOUtils.valueOf;

public class PageSequenceFactory {

    private static StaticContentDocument.StaticContent createPaginationAtFoot(String flowName){
        StaticContentDocument.StaticContent staticContent = StaticContentDocument.StaticContent.Factory.newInstance();
        staticContent.setFlowName(valueOf(flowName));
        BlockContainerDocument.BlockContainer blockContainer = BlockContainerDocument.BlockContainer.Factory.newInstance();
        blockContainer.setAbsolutePosition(BlockContainerDocument.BlockContainer.AbsolutePosition.ABSOLUTE);
        blockContainer.setLeft(valueOf("90mm"));
        blockContainer.setTop(valueOf("5mm"));
        blockContainer.setWidth(valueOf("33mm"));
        blockContainer.setHeight(valueOf("10mm"));

        BlockDocument.Block block =  BlockDocument.Block.Factory.newInstance();
        block.setTextAlign(BlockDocument.Block.TextAlign.CENTER);
        PageNumberDocument.PageNumber pageNumber = PageNumberDocument.PageNumber.Factory.newInstance();
        PageNumberCitationDocument.PageNumberCitation pageNumberCitation = PageNumberCitationDocument.PageNumberCitation.Factory.newInstance();
        pageNumberCitation.setRefId(valueOf("TheVeryLastPage"));
        block.getBasicBlocksList().addAll(List.of(pageNumber,pageNumberCitation));
        blockContainer.getBlocksList().add(block);
        staticContent.getBlocksList().add(blockContainer);
        return staticContent;
    }

    public static PageSequenceDocument.PageSequence createEmptyPageSequence(){
        // PageSequenceDocument.PageSequence pageSequence = PageSequenceDocument.PageSequence.Factory.newInstance();
        PageSequenceDocument.PageSequence pageSequence = PageSequenceDocument.PageSequence.Factory.newInstance();
        pageSequence.setMasterReference(valueOf("A4"));

        // TODO: export this to paginationPostProcess
        pageSequence.setInitialPageNumber(valueOf(2));
        pageSequence.setForcePageCount(PageSequenceDocument.PageSequence.ForcePageCount.ODD);

        StaticContentDocument.StaticContent staticContentEven = createPaginationAtFoot("region-after-even-default");
        StaticContentDocument.StaticContent staticContentOdd = createPaginationAtFoot("region-after-odd-default");
        pageSequence.getStaticContentList().addAll(List.of(staticContentEven,staticContentOdd));

        FlowDocument.Flow initalFlow = FlowDocument.Flow.Factory.newInstance();
        initalFlow.setFlowName(valueOf("xsl-region-body"));
        BlockDocument.Block block =  BlockDocument.Block.Factory.newInstance();
        block.setId(valueOf("TheVeryLastPage"));
        initalFlow.getBlocksList().add(block);
        pageSequence.setFlow(initalFlow);
        pageSequence.addNewTitle();

        return  pageSequence;
    }
}
