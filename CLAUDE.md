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
2. At the start of each task, inspect the current repository state and read the relevant Markdown files. Treat only decisions marked approved there as requirements; keep proposals and unresolved questions clearly pending until the developer approves them in the Markdown files.
3. Respect the architecture, technologies, conventions, and restrictions defined in `docs/lineamientos.md` and the active increment in `docs/mvp1.md`.
4. Work incrementally according to `docs/mvp1.md`; do not implement a later increment before review and approval.
5. If a real ambiguity or contradiction remains, ask a focused question before making a consequential assumption. Do not reopen decisions already recorded as approved.
6. Keep changes focused on the current request. After a review or clarification updates the Markdown files, a short request to review the updated documentation and continue the current increment is sufficient; do not require the developer to maintain or repeat a separate long prompt for each cycle.
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
