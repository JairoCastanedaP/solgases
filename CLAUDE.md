# SOLGASES - Claude Code Instructions

## Source of truth

The project-wide technical and architectural standards are defined in:

`docs/lineamientos.md`

`docs/lineamientos.md` contains the tool-agnostic technical standards. This `CLAUDE.md` is the entry point for Claude Code and describes how to apply the project documentation during iterative work.

Always read and follow `docs/lineamientos.md` before making significant changes to the project.

## Project documentation

Before starting development work, consult the relevant documentation under `docs/`:

- `docs/lineamientos.md`
  - Project-wide technical and architectural standards.

- `docs/analisis_requerimientos_solgases.md`
  - Requirements analysis, actors, scope, business rules, assumptions, and pending decisions.

- `docs/roadmap_mvps_solgases.md`
  - Overall roadmap and definition of the project's MVPs.

- `docs/mvp1.md`
  - Detailed scope and incremental implementation plan for MVP1.

- `docs/prompt_inicial_mvp1.md`
  - Historical initial prompt and context for MVP1; it is not required for each review cycle.

## Working rules

1. Do not invent requirements, business rules, technologies, or functionality.
2. At the start of each task, inspect the current repository state and read the relevant Markdown files. Treat decisions marked approved there as requirements; keep unresolved decisions pending and do not invent values or behavior for them.
3. Respect the architecture, technologies, conventions, and restrictions defined in `docs/lineamientos.md` and the active increment in `docs/mvp1.md`.
4. Work incrementally according to the active increment in `docs/mvp1.md`; do not implement a later increment as part of the current one.
5. Do not ask the developer to re-approve decisions already marked approved. If an unresolved decision blocks only an optional item, implement the rest of the approved increment and leave that item clearly pending. Ask a focused question only when the unresolved decision blocks the core scope or could cause an irreversible or consequential choice.
6. The developer may use ChatGPT to review decisions and maintain project documentation, while Claude Code implements the approved scope. When asked to review and continue an increment, re-read the current documentation and repository state, then implement the approved increment without requiring a repeated long prompt. Documentation marked as approved is the implementation brief; this instruction does not authorize scope beyond it.
7. Do not introduce additional frameworks, libraries, patterns, or infrastructure unless explicitly requested or justified against the project documentation.
8. After implementation work, compile and run relevant tests, report changed files, and identify unresolved issues. Never claim code works unless verified. Follow any narrower instruction for the current task (for example, documentation-only work).

## Communication

Communicate with the developer in Spanish.

Use English for source code:
- classes
- interfaces
- methods
- variables
- packages
- comments
- log messages

When explaining implementation decisions, clearly distinguish:
- documented requirements;
- implementation decisions;
- assumptions;
- pending decisions.

## Git

The current development branch for MVP1 is:

`feature/MVP1`

Do not create commits, branches, merge requests, or push changes unless explicitly requested by the developer.
