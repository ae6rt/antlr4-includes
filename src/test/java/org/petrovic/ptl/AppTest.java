package org.petrovic.ptl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.carpediem.IncludeBaseListener;
import org.carpediem.IncludeLexer;
import org.carpediem.IncludeParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class AppTest {

    IncludeLexer lexer;
    CommonTokenStream tokenStream;
    IncludeParser parser;
    ParseTree tree;

    @Before
    public void setup() throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream("/prog.in");
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(resourceAsStream);
        lexer = new IncludeLexer(antlrInputStream);
        tokenStream = new CommonTokenStream(lexer);
        parser = new IncludeParser(tokenStream);
        tree = parser.prog();
    }

    @Test
    public void testListener() throws IOException {
        ParseTreeWalker walker = new ParseTreeWalker();
        IncludeProcessor listener = new IncludeProcessor(parser);
        walker.walk(listener, tree);
    }

    class IncludeProcessor extends IncludeBaseListener {
        private final IncludeParser parser;

        IncludeProcessor(IncludeParser parser) {
            this.parser = parser;
        }

        @Override
        public void exitIncludeExpr(IncludeParser.IncludeExprContext ctx) {
            String text = ctx.ID().getText();
            System.out.printf("ID text: %s\n", text);
        }
    }

}
