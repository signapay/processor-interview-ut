# Contributing to Transaction Processor

We're excited that you're interested in contributing to the Transaction Processor project! This document outlines the process for contributing and some best practices to follow.

## Getting Started

1. Fork the repository on GitHub.
2. Clone your fork locally:
   ```
   git clone https://github.com/signapay/processor-interview-ut/tree/main
   cd processor-interview-ut
   ```
3. Create a new branch for your feature or bug fix:
   ```
   git checkout -b PLATFORM-N-Feature
   ```

## Making Changes

1. Make your changes in your feature branch.
2. Add or update tests as necessary.
3. Ensure all tests pass:
   ```
   python -m unittest discover tests
   ```
4. Make sure your code follows the project's coding style (we use PEP 8 for Python code).
5. Commit your changes:
   ```
   git commit -am "Add a brief commit message"
   ```

## Submitting a Pull Request

1. Push your changes to your fork on GitHub:
   ```
   git push origin PLATFORM-N-Feature
   ```
2. Go to the original repository on GitHub and create a new pull request.
3. Provide a clear title and description for your pull request, explaining the changes you've made.
4. Wait for the maintainers to review your pull request. They may ask for changes or clarifications.

## Pull Request Process

1. Ensure your PR adheres to the project's coding standards and includes appropriate tests.
2. Update the README.md with details of changes to the interface, if applicable.
3. You may merge the Pull Request once you have the sign-off of two other developers, or if you do not have permission to do that, you may request the second reviewer to merge it for you.

## Code Review Process

1. Other contributors will review your code for clarity, correctness, and adherence to project standards.
2. Address any comments or requested changes promptly.
3. Once approved, a maintainer will merge your PR into the main branch.

## Reporting Bugs

1. Use the GitHub Issues page to report bugs.
2. Describe the bug in detail, including steps to reproduce.
3. Include information about your environment (OS, Python version, etc.).

## Suggesting Enhancements

1. Use the GitHub Issues page to suggest enhancements.
2. Clearly describe the enhancement and its potential benefits.
3. Be open to discussion about the suggestion.

## Code of Conduct

Please note that this project is released with a [Contributor Code of Conduct](CODE_OF_CONDUCT.md). By participating in this project you agree to abide by its terms.

Thank you for contributing to Transaction Processor!
