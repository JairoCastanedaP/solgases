# SOLGASES - Claude Code Instructions

## Source of truth

The project-wide technical and architectural standards are defined in:

`docs/lineamientos.md`

This file was originally refined as `CLAUDE.md` during the course and was intentionally renamed to `lineamientos.md` so that the standards remain tool-agnostic and can be reused with Claude Code, GitHub Copilot, Codex, or other AI-assisted development tools.

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
  - Instructions for the initial MVP1 analysis and architecture review.

## Working rules

1. Do not invent requirements, business rules, technologies, or functionality.
2. Before significant changes, inspect the existing code and relevant project documentation.
3. Respect the architecture, technologies, conventions, and restrictions defined in `docs/lineamientos.md`.
4. Work incrementally according to `docs/mvp1.md`.
5. Do not implement a later increment before the current increment has been reviewed and approved.
6. When requirements or design decisions are unclear, identify the ambiguity instead of assuming a solution.
7. Keep changes focused on the requested scope.
8. Do not introduce additional frameworks, libraries, patterns, or infrastructure unless explicitly requested or justified against the project documentation.
9. After making changes:
   - compile the project;
   - run the relevant tests;
   - report the files created or modified;
   - report any unresolved issues or assumptions.
10. Never claim that the implementation works unless it has been verified by compilation and/or the relevant tests.

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