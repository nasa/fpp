# Contributing Guidelines

FPP is a free and open source project used to model F´! Are you ready to contribute?

In this file you can find basic information on contributing to [FPP](https://github.com/nasa/fpp). We will walk
through how to contribute and the process contributions follow. Remember, we may ask for changes or adjustments to make
your submission the best it can be. Fear not! Your submission is still valued! You may even comment on other submissions
to help them improve.

## Ways of Contributing

The best way to contribute to FPP is to remain positive and engaged. Just about every contribution needs some improvement
before it is ready to be folded in. Stand behind your work, push it forward, and work with us!

Specific Ways to Contribute:
- [Ask a Question or Suggest Improvements](https://github.com/nasa/fpp/issues/new)
   - Mark your issue as a "Feature"
- [Report a Bug or Mistake](https://github.com/nasa/fpp/issues/new)
   - Mark your issue as a "Bug"
- [Review Contributions](https://github.com/nasa/fpp/pulls)
- Submit a Pull Request see: [Code Contribution Process](#code-contribution-process)

Feel free to contribute any way that suits your skills and enjoy.

## Where to Start

First, contributors should build some understanding of FPP. Read through the [language specification](https://nasa.github.io/fpp/fpp-users-guide.html), [wiki](https://github.com/nasa/fpp/wiki/Analysis) and [users guide](https://nasa.github.io/fpp/fpp-users-guide.html).

When you are ready to submit bug reports use one of the above links!

## Code Contribution Process

All code contributions to FPP begin with an issue. Whether you're fixing a bug, adding a feature, or improving documentation, please start by opening an issue describing your proposal. The Change Control Board (CCB) reviews and approves issues before work begins to ensure alignment with project goals and standards. Once approved, you can proceed with implementation and submit a pull request (PR).

If a PR is opened for work that does not correspond to an approved issue, the PR will be routed through the CCB process first—reviewed on a best-effort basis—and may be delayed or declined depending on CCB decisions.You can read more about how this process works in the [F´ Governance document](https://github.com/nasa/fprime/blob/devel/GOVERNANCE.md).

In general, when adding a new feature to FPP, we use the following procedure:

1. Revise the specification

1. Implement and test any new syntax

1. If necessary, update the Analysis and Tools sections on the wiki

1. Implement and test any new semantics

1. Implement and test any new code generation

1. Update the User's Guide

1. Integrate and test with F Prime

Please read our [FPP feature contributions](https://github.com/nasa/fpp/wiki/Adding-New-Features-to-FPP)
document to learn more about each step in this process.

It is generally recommended that each step is implemented in a _separate_ PR
against a `feature/...` branch.

### Development Process

FPP follows a standard git flow development model. External developers should start with a
[fork](https://docs.github.com/en/get-started/quickstart/fork-a-repo) of the FPP repository and then develop
according to [git flow](https://docs.github.com/en/get-started/quickstart/github-flow). Remember to add an
[upstream remote](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/configuring-a-remote-repository-for-a-fork) to your fork such that you may fetch the latest changes.

For each contribution, developers should first fetch the latest changes from upstream. Then create a new branch off
`main` and submit back to FPP using a pull request as described above.

If you are developing a new feature in multiple PRs, create PRs off of `feature/...`
instead of `main`. Once the feature is complete, a final staging PR may be made
against `main` and an [alpha release](https://github.com/nasa/fpp/releases) may be
tagged to test against the F Prime repository.

**Preparing A New Branch**
```
git fetch upstream
git checkout upstream/main
git checkout -b <desired branch name>
```

Once a pull request has been submitted the following process will begin.

### Submission Review

The pull request changes will be reviewed by the team and community supporting FPP. Often this means that a discussion on
how to improve the submission will arise. Engage in the conversation and work with reviewers to improve the code.
Remember, F´ is flight software running in remote environments. This means we hold submissions to very high standards.
Do not fear, we are happy to work with contributors to help meet these standards!

Submission reviews can take some time for the team to complete. These reviews may take additional time for pull requests
that are very large, touch sensitive code, or have not been [discussed](https://github.com/nasa/fpp/issues)
beforehand. Sometimes changes are determined to best fit in another repository or package. Please be patient with us and
remember we are all one team.

Anyone can review code on FPP but an approved review from a maintainer will be required to complete the submission.

### Automated Checking

Once the submission has been reviewed by a maintainer, automated checking will begin. There are many checks that must
pass on submitted code to ensure that it is not going to introduce a bug or regression to FPP. These checks ensure unit
tests pass, compiler outputs are deterministic and match expected outputs,
and semantic checks match specification.

## Project Structure

- `compiler/` - The FPP analysis and translation tools (Scala codebase)
  - `lib/` - Core compiler library containing the main phases
  - `tools/` - Individual FPP tool implementations (fpp-check, fpp-to-cpp, etc.)
  - `bin/` - Installed binaries and scripts
- `docs/` - FPP User's Guide and language specification (AsciiDoc)
- `python/` - Python packaging wrapper (fprime-fpp package)
- `editors/` - Syntax highlighting for Vim and Emacs

### Core Phases

1. **syntax** - Parsing FPP source into abstract syntax trees (AST)
   - Lexical analysis and tokenization
   - Parser combinators for FPP grammar

2. **transform** - AST transformations
   - Resolving of includes
   - Syntactical transformations

3. **analysis** - Semantic analysis and type checking
   - `Analyzers/` - Individual analyzers for different constructs
   - Symbol resolution and semantic validation
   - Type inference and checking
   - See [wiki](https://github.com/nasa/fpp/wiki/Checking-Semantics) for more detail

4. **codegen** - Code generation backends
   - C++ code generation (`fpp-to-cpp`)
   - Dictionary generation (`fpp-to-dict`)
   - JSON/XML generation

### Tool Structure

Each tool in `compiler/tools/` is a thin wrapper that:
1. Parses command-line arguments (using scopt)
2. Invokes the appropriate compiler phases
3. Outputs results in the tool's specific format

The main `fpp` tool (`tools/fpp/`) dispatches to other tools based on subcommands.

### Available Tools

- `fpp-check` - Semantic checking
- `fpp-depend` - Generate dependency information
- `fpp-filenames` - Extract file names
- `fpp-format` - Format FPP source code
- `fpp-from-xml` - Convert from XML to FPP
- `fpp-locate-defs` - Locate definitions
- `fpp-locate-uses` - Locate uses
- `fpp-syntax` - Syntax checking only
- `fpp-to-cpp` - Generate C++ code
- `fpp-to-dict` - Generate dictionaries
- `fpp-to-json` - Generate JSON
- `fpp-to-layout` - Generate layouts

## Helpful Tips

This section will describe some helpful tips for contributing to FPP.

