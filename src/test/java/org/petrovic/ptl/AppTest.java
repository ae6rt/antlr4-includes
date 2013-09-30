package org.petrovic.ptl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.carpediem.IncludeBaseListener;
import org.carpediem.IncludeLexer;
import org.carpediem.IncludeParser;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class AppTest {

    IncludeLexer lexer;
    CommonTokenStream tokenStream;
    IncludeParser parser;
    ParseTree tree;

    //    @Before
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
        InputStream resourceAsStream = getClass().getResourceAsStream("/prog.in");
        
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(resourceAsStream);
        lexer = new IncludeLexer(antlrInputStream);
        tokenStream = new CommonTokenStream(lexer);
        parser = new IncludeParser(tokenStream);
        tree = parser.prog();

        ParseTreeWalker walker = new ParseTreeWalker();
        IncludeProcessor listener = new IncludeProcessor(parser);

        walker.walk(listener, tree);
    }

    class IncludeProcessor extends IncludeBaseListener {
        private final IncludeParser parser;
        private boolean encounteredInclude;

        IncludeProcessor(IncludeParser parser) {
            this.parser = parser;
        }

        @Override
        public void exitIncludeExpr(IncludeParser.IncludeExprContext ctx) {
            encounteredInclude = true;
            int startIndex = ctx.getStart().getStartIndex();
            System.out.printf("startIndex: %d\n", startIndex);

            String text = ctx.ID().getText();
            InputStream resolvedStream = resolve(text);
            System.out.printf("ID text: %s, stream: %s\n", text, resolvedStream);
            close(resolvedStream);
        }

        @Override
        public void exitProg(IncludeParser.ProgContext ctx) {
            System.out.printf("@@@ Encountered include : %s\n", encounteredInclude);
        }

        private void close(InputStream resolvedStream) {
            if (resolvedStream == null) {
                return;
            }
            try {
                resolvedStream.close();
            } catch (IOException e) {
            }
        }

        private InputStream resolve(String text) {
            InputStream resourceAsStream = getClass().getResourceAsStream("/" + text);
            return resourceAsStream;
        }
    }

}
