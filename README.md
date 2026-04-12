# SyntaxAnalyzer

Syntax analyzer for arithmetic expressions and small C-like syntax, built in Java using a custom lexer and parser.

## Features

- Lexical and syntactic analysis  
- Support for arithmetic and logical expressions  
- Abstract Syntax Tree (AST) generation  
- Optional code evaluation or translation  

## Build
javac -d out $(find src -name "*.java")
cd out
jar cfe SimpleSyntaxAnalyzer.jar ua.hackhud.simplesyntaxanalyzer.Main .
## Usage
java -jar SimpleSyntaxAnalyzer.jar examples/test.c
## Output Modes
tokens — list of lexical tokens
ast — visualize syntax tree
eval — evaluate arithmetic expression
