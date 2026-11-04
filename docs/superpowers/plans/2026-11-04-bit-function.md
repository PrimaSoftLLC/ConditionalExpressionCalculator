# bit() Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** Add `bit(number, index)` throughout parsing, validation and calculation.

**Architecture:** Emit a binary numeric BIT token into the existing RPN evaluator. Parse function-bearing expressions with arity and operand-type checks; retain the legacy parser and checker for existing expressions. Validate known arguments during checking and all resolved arguments during evaluation.

**Tech Stack:** Java 11 API/bytecode, mise JDK 25 and Maven, JUnit 4.

**Spec:** Contract agreed in this conversation: finite integral number in [-2^53, 2^53], integral index 0–63, signed long conversion, numeric 0/1 result. Negative byte -1 is sign-extended, so bit 8 is 1; bit(255, 8) is 0. Both arguments may be numeric expressions. Invalid arguments throw IllegalArgumentException.

## Global Constraints

- Preserve public method signatures, existing formulas and logical &/| semantics.
- Keep Java 11 and current dependencies/version.
- Preserve target/thread-safe; build only in .worktrees/bit-function.
- Do not publish, push, tag or merge a release branch.
- Keep negative-literal syntax `(0-1)`.

## Review Focus

- Dynamic index must not be rejected because of dummy values during checking.
- Argument boundaries, nesting and stray commas must be checked by direct RPN parsing as well as the checker.
- Known invalid arguments must be rejected even when the other argument is dynamic.
- Neither boolean operands nor a naked numeric bit result are valid logical conditions.
- Prepared sensor calculations must remain independent across messages.

### Task 1: Syntax, numeric contract and validation

**Files:** EType.java, ETokens.java, SupportedOperations.java, ReversePolishNotation.java, ExpressionChecker.java; new FunctionExpressionParser.java and BitFunctionTest.java.

**Interfaces:** Existing ReversePolishNotation.create and ExpressionCalculator.calculateMath/calculateConditional remain unchanged. Append BIT and COMMA token kinds. Shared bit argument checks live in SupportedOperations; BIT is a numeric operation.

- [x] Add behavioral tests for signed bytes, indices 0/31/32/52/53/63, inclusive numeric bounds, invalid values, expression nesting, arity/type errors, arithmetic and conditions.
- [x] Run `mise exec -- mvn -B test -Dtest=BitFunctionTest`; confirm missing bit support fails.
- [x] Implement function parsing to existing RPN, strict long conversion checks, and validation of known constants without substituting fake dynamic values.
- [x] Run the full test suite and inspect results.

### Task 2: Sensor integration, documentation and final verification

**Files:** new BitSensorTest.java; readme.md; CHANGELOG.md.

**Interfaces:** Existing SensorCalculator preparation, resolver, formula and condition APIs.

- [x] Test raw telemetry, negative byte widening, current value, sensor dependencies, prepared/unprepared execution, invalid runtime values and shared prepared tokens.
- [x] Document long semantics, bounds, numeric result, exceptions and examples; note double conversion occurs before bit checks.
- [x] Run `mise exec -- mvn -B clean verify` in this worktree and `git diff --check`.
- [x] Review the complete change and resolve functional findings with regression tests.

Verification: 163 tests, zero failures/errors, one preexisting skip. Independent review found no functional defects; clarified the existing SensorCalculator syntax-error fallback in README.
