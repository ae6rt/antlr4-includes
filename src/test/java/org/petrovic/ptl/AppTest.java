package org.petrovic.ptl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.carpediem.IncludeBaseListener;
import org.carpediem.IncludeLexer;
import org.carpediem.IncludeParser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AppTest {

    IncludeLexer lexer;
    CommonTokenStream tokenStream;
    IncludeParser parser;
    ParseTree tree;
    List<String> program = new ArrayList<String>();

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
        List<String> prog = main();
        // now feed prog to the final translator
    }

    private List<String> main() throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream("/prog.in");
        quench(resourceAsStream);
        return program;
    }

    private void quench(InputStream resourceAsStream) throws IOException {
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(resourceAsStream);
        lexer = new IncludeLexer(antlrInputStream);
        tokenStream = new CommonTokenStream(lexer);
        parser = new IncludeParser(tokenStream);
        tree = parser.prog();
        ParseTreeWalker walker = new ParseTreeWalker();
        IncludeProcessor listener = new IncludeProcessor(parser);
        walker.walk(listener, tree);
        if (listener.encounteredInclude) {
            ByteArrayInputStream byteArrayInputStream = toStream(program);
            quench(byteArrayInputStream);
        }
    }

    private ByteArrayInputStream toStream(List<String> program) {
        StringBuilder sb = new StringBuilder();
        for (String s : program) {
            sb.append(s);
        }
        try {
            return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException wonthappen) {
            throw new RuntimeException(wonthappen);
        }
    }

    class IncludeProcessor extends IncludeBaseListener {
        private final IncludeParser parser;
        boolean encounteredInclude;

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
