# AGENTS.md

Repository-specific operating guide for coding agents in
`loan-limit-mock-server`.

## Scope

- This file captures observed project conventions.
- Prefer these rules over generic Kotlin/Ktor defaults.
- If repo files conflict with this guide, follow repo files.

## Project Snapshot

- Language: Kotlin (JVM)
- Runtime: Ktor server with Netty
- Serialization: kotlinx.serialization
- Build tool: Gradle (Kotlin DSL) via wrapper
- Java toolchain: 17
- Module shape: single-module service

## Key Files

- `build.gradle.kts` - plugins, dependencies, test platform
- `settings.gradle.kts` - root project name
- `src/main/kotlin/com/example/mockserver/Application.kt` - entrypoint,
  routing, DTOs, config parsing
- `README.md` - endpoint and env variable reference
- `gradlew`, `gradlew.bat`, `gradle/wrapper/*` - wrapper and Gradle version

## Build / Run / Test Commands

Run all commands from repo root with the wrapper.

### Core

- Run app: `./gradlew run`
- Build: `./gradlew build`
- Assemble only: `./gradlew assemble`
- Clean build artifacts: `./gradlew clean`
- Verification lifecycle: `./gradlew check`
- Run tests: `./gradlew test`

### Single Test Commands (important)

The repository currently has no `src/test` files, but these are the supported
Gradle forms when tests are added.

- Single class: `./gradlew test --tests "com.example.mockserver.SomeTest"`
- Single method:
  `./gradlew test --tests "com.example.mockserver.SomeTest.someMethod"`
- Wildcard pattern: `./gradlew test --tests "com.example.mockserver.*"`

Keep the selector quoted to avoid shell expansion issues.

### Useful Discovery

- List tasks: `./gradlew tasks --all`
- Show dependencies: `./gradlew dependencies`
- Show JVM toolchains: `./gradlew javaToolchains`

## Lint / Formatting Status

- No dedicated linter plugin is configured in this repo.
- `detekt`, `ktlint`, `spotless`, and `checkstyle` are not declared.
- Use `./gradlew check` as the default verification command.
- Keep formatting aligned with existing source style.

## Coding Conventions (derived from current code)

Apply these conventions for all Kotlin changes.

### Imports

- Use explicit imports; do not add wildcard imports.
- Keep imports grouped by namespace families (`io.ktor`, `kotlinx`, `kotlin`).
- Remove unused imports.

### Naming

- Package names: lowercase dotted notation.
- Classes/data classes/objects: PascalCase.
- Functions/properties/local vars: camelCase.
- Environment variable keys: UPPER_SNAKE_CASE string literals.
- HTTP path segments: kebab-case; Kotlin symbols remain camelCase.

### Formatting

- 4-space indentation; no tabs.
- Opening braces on the same line.
- Keep trailing commas for multiline argument/property lists.
- Wrap long expressions instead of compressing into dense one-liners.

### Types and Nullability

- Prefer explicit scalar types (`Int`, `Long`, `String`) for DTO/config fields.
- Use nullable types only when absence is domain-valid.
- Prefer safe parsing (`toIntOrNull`, `toLongOrNull`) with defaults.
- Avoid unsafe casts and ambiguous top-level `Any` typing.

### Error Handling and Guard Clauses

- Use early-return guard clauses for required inputs.
- Return meaningful HTTP statuses for invalid input paths.
- Use concise error messages.
- Avoid empty catch blocks and silent swallowing.

### Ktor Routing Pattern

- Install plugins before route registration.
- Use `routing {}` with nested `route()` groups for endpoint families.
- Keep existing endpoint version shape (`/api/v1/...`) consistent.
- Preserve a simple `/health` probe endpoint.

### DTO / Serialization

- Annotate request/response models with `@Serializable`.
- Prefer immutable DTO fields (`val`).
- Keep transport objects focused on payload data only.
- Keep response contract shape stable unless explicitly requested.

### Environment Configuration

- Keep env parsing centralized in config model/companion helpers.
- Maintain explicit default values in code.
- Preserve current `MOCK_*` naming pattern for env variables.

### Gradle Kotlin DSL

- Keep `plugins {}` at the top of `build.gradle.kts`.
- Keep dependency versions centralized where practical.
- Keep dependencies grouped and easy to scan.
- Preserve `useJUnitPlatform()` for test execution.

## Testing Expectations for Agents

- Add new tests under `src/test/kotlin`.
- During development, run targeted tests with `--tests` selectors.
- Before completion, run `./gradlew test`.
- For final verification, run `./gradlew check`.

## Change Discipline

- Make minimal, focused edits.
- Do not refactor unrelated code while fixing a bug.
- Do not introduce new dependencies without clear necessity.
- Update `README.md` when endpoint or env behavior changes.

## Cursor / Copilot Rules

Repository scan at generation time:

- `.cursor/rules/` not found
- `.cursorrules` not found
- `.github/copilot-instructions.md` not found

If these files appear later, treat them as higher-priority instruction layers
and merge their constraints into this file.

## Maintenance

- Keep this document synchronized with actual build and code conventions.
- Prefer repository-observed facts over generic advice.
