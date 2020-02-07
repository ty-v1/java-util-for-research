package com.github.ty_v1.ast;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ASTConstructor {

  private final static Logger log = LoggerFactory.getLogger(ASTConstructor.class);

  public CompilationUnit constructJDTAST(final Path javaFile) {
    final ASTParser parser = createParser();
    parser.setSource(readFile(javaFile));
    final CompilationUnit unit = (CompilationUnit) parser.createAST(
        new NullProgressMonitor());
    unit.recordModifications();

    return unit;
  }

  public CompilationUnit constructJDTASTWithBinding(final Path javaFile) {
    final ASTParser parser = createParserWithBinding(javaFile);
    parser.setSource(readFile(javaFile));

    final CompilationUnit unit = (CompilationUnit) parser.createAST(
        new NullProgressMonitor());
    unit.recordModifications();

    return unit;
  }

  @SuppressWarnings("unchecked")
  private ASTParser createParser() {
    final ASTParser parser = ASTParser.newParser(AST.JLS12);

    final Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();
    options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_12);
    options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_12);
    options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_12);

    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setCompilerOptions(options);

    return parser;
  }

  private ASTParser createParserWithBinding(final Path path) {
    final ASTParser parser = createParser();
    parser.setResolveBindings(true);
    parser.setBindingsRecovery(true);
    parser.setEnvironment(null, null, null, true);
    parser.setUnitName(path.toAbsolutePath()
        .toString());

    return parser;
  }

  private char[] readFile(final Path file) {
    try {
      return String.join("\n", Files.readAllLines(file))
          .toCharArray();
    } catch (final IOException e) {
      log.error(e.getMessage());
      return new char[] {};
    }
  }
}
