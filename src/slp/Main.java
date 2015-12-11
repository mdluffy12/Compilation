package slp;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import java_cup.runtime.*;

/** The entry point of the SLP (Straight Line Program) application.
 *
 */
public class Main {
	private static boolean printtokens = false;
	
	/** Reads an SLP and pretty-prints it.
	 * 
	 * @param args Should be the name of the file containing an SLP.
	 */
	public static void main(String[] args) {
		Path p = Paths.get("Input/illegal_files");
	    FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
	      @Override
	      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	          throws IOException {
	    	  try {

	  			// Parse the input file
	  			FileReader txtFile = new FileReader(file.toString());
	  			Lexer scanner = new Lexer(txtFile);
	  			Parser parser = new Parser(scanner);
	  			parser.printTokens = false;
	  			
	  			Symbol parseSymbol = parser.parse();
	  			ASTNode root = (ASTNode) parseSymbol.value;
	  			
	  			// Pretty-print the program to System.out
	  			PrettyPrinter printer = new PrettyPrinter(root);
	  			//printer.print();
	  			
	  			SyntaxAnalyzer analyzer = new SyntaxAnalyzer(root);
	  			analyzer.Analyze();
	  			System.out.println(file.toString() + " FAILED!");
	  			
	  			
	  		} catch (Exception e) {
	  			System.out.println(file.toString() + " PASSED!");
	  		}
	        return FileVisitResult.CONTINUE;
	      }
	    };

	    try {
	      Files.walkFileTree(p, fv);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    
	    p = Paths.get("Input/legal_files");
	    fv = new SimpleFileVisitor<Path>() {
	      @Override
	      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	          throws IOException {
	    	  try {

	  			// Parse the input file
	  			FileReader txtFile = new FileReader(file.toString());
	  			Lexer scanner = new Lexer(txtFile);
	  			Parser parser = new Parser(scanner);
	  			parser.printTokens = false;
	  			
	  			Symbol parseSymbol = parser.parse();
	  			ASTNode root = (ASTNode) parseSymbol.value;
	  			
	  			// Pretty-print the program to System.out
	  			PrettyPrinter printer = new PrettyPrinter(root);
	  			//printer.print();
	  			
	  			SyntaxAnalyzer analyzer = new SyntaxAnalyzer(root);
	  			analyzer.Analyze();
	  			System.out.println(file.toString() + " PASSED!");
	  			
	  		} catch (Exception e) {
	  			System.out.println(file.toString() + " FAILED!");
	  		}
	        return FileVisitResult.CONTINUE;
	      }
	    };
	    
	    try {
		      Files.walkFileTree(p, fv);
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	    
	    
		try {
			if (args.length == 0) {
				System.out.println("Error: Missing input file argument!");
				printUsage();
				System.exit(-1);
			}
			if (args.length == 2) {
				if (args[1].equals("-printtokens")) {
					printtokens = true;
				}
				else {
					printUsage();
					System.exit(-1);
				}
			}
			
			// Parse the input file
			FileReader txtFile = new FileReader(args[0]);
			Lexer scanner = new Lexer(txtFile);
			Parser parser = new Parser(scanner);
			parser.printTokens = printtokens;
			
			Symbol parseSymbol = parser.parse();
			System.out.println("Parsed " + args[0] + " successfully!");
			ASTNode root = (ASTNode) parseSymbol.value;
			
			// Pretty-print the program to System.out
			PrettyPrinter printer = new PrettyPrinter(root);
			printer.print();
			
			System.out.println("Finished Printing the AST!!");
			
			SyntaxAnalyzer analyzer = new SyntaxAnalyzer(root);
			analyzer.Analyze();
			
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	/** Prints usage information about this application to System.out
	 */
	public static void printUsage() {
		System.out.println("Usage: slp file [-printtokens]");
	}
}