# Spec Kit: Constitution Guide

The **Constitution** defines the project's governing rules, technical standards, architecture guardrails, and quality gates.

## Core Elements
1. **Governing Principles**: Core tenets such as test-driven development, zero-trust security, accessibility, performance budgets.
2. **Technical Stack Constraints**: Allowed languages, frameworks, library versions, and architectural patterns (e.g., MVVM, Clean Architecture).
3. **Coding Standards**: Lint rules, naming conventions, error handling policies, logging formats.
4. **Testing Gates**: Minimum unit test coverage, automated CI requirements, integration test protocols.
5. **Security & Privacy**: Secrets handling (never commit keys), PII sanitization, input validation.

## Template
```markdown
# Project Constitution

## 1. Architectural Guardrails
- Pattern: [e.g., Repository Pattern + Clean Architecture]
- Tech Stack: [e.g., Android SDK 34 / Java 17 / HTML5 Vanilla JS]

## 2. Coding & Style Standards
- Keep dependencies minimal.
- Do not introduce new frameworks without explicit approval.
- Enforce strict typing and error boundary handling.

## 3. Testing & Quality Requirements
- All new logic must have corresponding unit tests.
- Every build must pass compilation with zero unhandled warnings.
```
