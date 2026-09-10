---
name: spec-kit
description: >-
  Spec-Driven Development (SDD) toolkit for AI coding agents based on GitHub's Spec Kit.
  Use when defining requirements, designing architecture, planning multi-step implementations,
  creating task breakdowns, or executing disciplined specification-first software development.
---

# Spec Kit - Spec-Driven Development (SDD)

**Spec Kit** brings disciplined, specification-first development to AI coding agents, moving away from loose prompt-and-guess workflows toward reliable, structured execution.

---

## The Spec-Driven Workflow

Spec Kit divides software engineering tasks into five structured phases:

```text
[Constitution] ➔ [Specify] ➔ [Plan] ➔ [Tasks] ➔ [Implement]
```

| Phase | Purpose | Output Artifact | Reference |
|---|---|---|---|
| **1. Constitution** | Define governing principles, standards, tech stack, and quality gates | `constitution.md` | [Constitution Guide](./references/constitution.md) |
| **2. Specify** | Capture requirements, user stories, constraints, and success criteria | `spec.md` | [Specification Guide](./references/specify.md) |
| **3. Plan** | Formulate technical implementation strategy, architecture, and tradeoffs | `plan.md` | [Planning Guide](./references/plan.md) |
| **4. Tasks** | Decompose plan into prioritized, checkable work items | `tasks.md` | [Tasks Guide](./references/tasks.md) |
| **5. Implement** | Execute code changes surgically, verifying each task in sequence | Code & Tests | [Implementation Guide](./references/implement.md) |

---

## How to Execute the Workflow

### Phase 1: Establish Constitution (`/speckit.constitution`)
- Formulate the non-negotiable architectural principles, security guidelines, and coding conventions of the project.
- Ensure every subsequent phase adheres strictly to these boundaries.

### Phase 2: Create Specification (`/speckit.specify`)
- Focus on the **WHAT** and **WHY** before jumping into HOW.
- List acceptance criteria using Given/When/Then or verifiable checklist items.
- Identify edge cases, performance budgets, and error states.

### Phase 3: Technical Plan (`/speckit.plan`)
- Define the exact files to create, modify, or delete.
- Outline data structures, API contracts, and integration points.
- Map out risks, alternatives considered, and verification steps.

### Phase 4: Task Decomposition (`/speckit.tasks`)
- Break the plan down into atomic tasks (each ideally 15–30 minutes of focused agent execution).
- Include verification commands for each task (`npm test`, `./gradlew test`, curl checks).
- Order tasks by dependency: Data Layer ➔ Logic Layer ➔ UI/API Layer ➔ Verification.

### Phase 5: Disciplined Implementation (`/speckit.implement`)
- Work task-by-task.
- Never move to the next task until the current task is verified.
- Run regression tests frequently to prevent breaking existing features.

---

## Detailed References

- [Constitution Reference](./references/constitution.md)
- [Specification Reference](./references/specify.md)
- [Planning Reference](./references/plan.md)
- [Tasks Reference](./references/tasks.md)
- [Implementation Reference](./references/implement.md)
- [Quality Checklist](./references/checklist.md)
- [Clarification Protocol](./references/clarify.md)
