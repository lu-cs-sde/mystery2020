package mystery2020;
import AST.*;

import java.io.*;

public class Compiler {

    public static void
    main(String[] args) throws FileNotFoundException {
        String filename = getFilename(args);

        // Construct the AST
        Block m = parseFile(filename);
        System.out.println(m.toString());
    }

    public static String
    getFilename(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: java Compiler filename");
            System.exit(1);
        }
        return args[0];
    }

    public static Block
    parseFile(String filename) throws FileNotFoundException {
    	try {
    		return parse(new FileReader(filename));
    	} catch (FileNotFoundException exn) {
    		throw new IOFailureException(exn);
    	}
    }

    public static Block
    parseString(String body) {
    	return parse(new StringReader(body));
    }

    /**
     * Parses the Mystery program from the given reader
     *
     * @throw LexerException
     * @throw ParserException
     * @throw IOException
     */
    public static Block
    parse(Reader reader) {
    	ProgramScanner scanner = new ProgramScanner(reader);
    	ProgramParser parser = new ProgramParser();
    	try {
    		Block result = (Block)parser.parse(scanner);
    		return result;
    	} catch (IOException exn) {
    		throw new IOFailureException(exn);
    	} catch (beaver.Parser.Exception e) {
    		throw new Error(e);
    	}
    }
}
