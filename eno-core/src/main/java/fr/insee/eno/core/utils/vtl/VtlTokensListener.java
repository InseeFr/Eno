package fr.insee.eno.core.utils.vtl;

import fr.insee.vtl.parser.VtlBaseListener;
import lombok.Getter;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

@Getter
class VtlTokensListener extends VtlBaseListener {

    private final List<Integer> tokenIdInExpressions = new ArrayList<>();

    @Override
    public void visitTerminal(TerminalNode node) {
        tokenIdInExpressions.add(node.getSymbol().getType());
    }
    @Override
    public void visitErrorNode(ErrorNode node) {
        tokenIdInExpressions.add(node.getSymbol().getType());
    }

}
