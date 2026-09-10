---
name: karpathy-guidelines
description: >-
  Behavioral guidelines to reduce common LLM coding mistakes, derived from Andrej Karpathy's observations.
  Use when writing, planning, reviewing, or refactoring code to enforce simplicity first, surgical changes,
  thinking before coding, and goal-driven execution.
---

# Karpathy Coding Guidelines

Behavioral guidelines to reduce common LLM coding mistakes, derived from [Andrej Karpathy's observations](https://x.com/karpathy/status/2015883857489522876) on LLM coding pitfalls.

**Core Philosophy:** Bias toward caution, precision, and simplicity over hasty, speculative changes.

---

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them — do not pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what is confusing and ask for clarification.

---

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- Write no features beyond what was asked.
- Avoid abstractions for single-use code.
- Avoid "flexibility" or "configurability" that wasn't requested.
- Avoid error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

*Ask yourself:* "Would a senior engineer say this is overcomplicated?" If yes, simplify.

---

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Do not "improve" adjacent code, comments, or formatting.
- Do not refactor things that are not broken.
- Match existing style, even if you would do it differently.
- If you notice unrelated dead code, mention it — do not delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Do not remove pre-existing dead code unless asked.

*The test:* Every changed line should trace directly to the user's request.

---

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → Write tests for invalid inputs, then make them pass.
- "Fix the bug" → Write a test that reproduces it, then make it pass.
- "Refactor X" → Ensure tests pass before and after.

For multi-step tasks, state a brief plan:
```text
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you execute independently and reliably.
