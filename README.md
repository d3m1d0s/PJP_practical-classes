# PJP Practical Classes

## Table of Contents

- [Overview](#overview)
- [cv1: Arithmetic Expression Evaluator](#cv1-arithmetic-expression-evaluator)
  - [Build and Run](#build-and-run)
  - [Usage](#usage)
- [cv2_cv5_cv6: Lexer and Parser](#cv2_cv5_cv6-lexer-and-parser)
  - [Build and Run](#build-and-run-1)
  - [Usage](#usage-1)
- [cv3_and_cv4: Grammar Operations](#cv3_and_cv4-grammar-operations)
  - [Build and Run](#build-and-run-2)
  - [Usage](#usage-2)
- [cv7_cv8_cv9_cv10: ANTLR Interpreter](#cv7_cv8_cv9_cv10-antlr-interpreter)
  - [Build and Run](#build-and-run-3)
  - [Usage](#usage-3)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Overview

Lab solutions for the PJP course (Programming Languages and Compilers) at VSB-TUO, spring 2025. The repository holds four mini projects:

- `cv1` - arithmetic expression evaluator
- `cv2_cv5_cv6` - lexer and parser built by hand on regular expressions
- `cv3_and_cv4` - grammar operations: EMPTY, FIRST and FOLLOW sets, LL(1) check
- `cv7_cv8_cv9_cv10` - ANTLR 4 interpreter with a type checker and a stack machine (Maven project)

The final version of the compiler grew into a separate repository: [PJP_antlr-compiler](https://github.com/d3m1d0s/PJP_antlr-compiler).

## cv1: Arithmetic Expression Evaluator

A console evaluator of integer arithmetic expressions with `+ - * /` and parentheses.

### Build and Run

To run it, ensure JDK 17 or newer is installed and execute from the repository root:

```
javac cv1/*.java
java cv1.ArithmeticInterpreter
```

### Usage

The first input line is the number of expressions, then one expression per line. The program prints the result of each expression, or `ERROR` for an invalid one.

Example input:

```
2
2+3*4
(2+3)*4
```

prints `14` and `20`.

## cv2_cv5_cv6: Lexer and Parser

A lexer built on regular expressions and a recursive descent parser of arithmetic expressions. One `main` covers three classes:

- cv2 - prints the token stream
- cv5 - prints the numbers of the applied grammar rules
- cv6 - evaluates the expressions (active by default)

To switch the variant, uncomment its block in `LexicalAnalyzer.java` and comment out the active one.

### Build and Run

To run it, ensure JDK 17 or newer is installed and execute from the repository root:

```
javac cv2_cv5_cv6/*.java
java cv2_cv5_cv6.LexicalAnalyzer
```

### Usage

The input format is the same as in cv1. Expressions may also contain `//` line comments.

## cv3_and_cv4: Grammar Operations

Reads a context-free grammar from a file, computes its EMPTY, FIRST and FOLLOW sets and checks whether it is LL(1).

### Build and Run

To run it, ensure JDK 17 or newer is installed and execute from the repository root:

```
javac cv3_and_cv4/grammar/*.java cv3_and_cv4/grammarOperation/*.java
java cv3_and_cv4.grammarOperation.Test cv3_and_cv4/grammar.txt
```

### Usage

The path to the grammar file is the only argument. Two test grammars are included:

- `cv3_and_cv4/grammar.txt` - not an LL(1) grammar
- `cv3_and_cv4/grammarLL1.txt` - an LL(1) grammar

## cv7_cv8_cv9_cv10: ANTLR Interpreter

An interpreter for a small language with `int` and `float` variables, built on ANTLR 4. It parses a program (cv7), checks the types (cv8), generates stack machine instructions (cv9) and executes them (cv10).

### Build and Run

To run it, ensure JDK 17 and Maven are installed and execute:

```
cd cv7_cv8_cv9_cv10/antlr-interpreter
mvn compile exec:java
```

### Usage

By default the interpreter runs `test.lang`. Input programs live in `src/test/resources`; to run another one, pass its name:

```
mvn compile exec:java "-Dexec.args=program.lang"
```

The program prints the generated instructions and then the value of every expression statement. `input.txt` in the project folder is a reference instruction listing for `test.lang`.

## License

This project is authored by Demid Ostiakov. All rights reserved.

## Acknowledgments

Thanks to the instructors of the PJP course at VSB-TUO:

- Ing. Marek Běhálek, Ph.D. for the lectures and the course materials these labs are built on
- Ing. Michal Vašinek, Ph.D. for leading the exercises and for his advice along the way

Thanks also to the instructors of UTI (Introduction to Theoretical Computer Science), where the theory of formal languages and automata behind these labs comes from:

- doc. Ing. Zdeněk Sawa, Ph.D. for the lectures and his presentations covering the whole course
- doc. Mgr. Pavla Dráždilová, Ph.D. for leading the exercises and her clear and illustrative explanations
