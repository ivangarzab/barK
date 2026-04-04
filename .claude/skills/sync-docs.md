# Sync Docs

Audit recent code changes and update the barK documentation site accordingly.

## Steps

1. **Identify what changed**
   Run `git diff main...HEAD --name-only` (or `git diff HEAD~1 --name-only` if on main) to find modified files. Focus on:
   - `shared/src/commonMain/` → may affect multiple docs pages
   - `shared/src/androidMain/` → `docs/android/`
   - `shared/src/iosMain/` → `docs/ios/`
   - Trainer files → `docs/advanced/custom-trainers.md`
   - `Level.kt` or `Pack.kt` → `docs/advanced/index.md`, `docs/index.md` (log levels table)
   - `Bark.kt` → `docs/api-reference.md` quick reference table + any page showing the public API

2. **Map changes to docs pages**
   Use this mapping:

   | Changed area | Docs page(s) to review |
   |---|---|
   | `androidMain/trainers/` | `docs/android/index.md`, `docs/android/unit-tests.md` |
   | `iosMain/trainers/` | `docs/ios/index.md`, `docs/ios/unit-tests.md` |
   | `androidMain/detectors/` | `docs/android/unit-tests.md` |
   | `iosMain/detectors/` | `docs/ios/unit-tests.md` |
   | `commonMain/Bark.kt` | `docs/api-reference.md`, `docs/index.md` |
   | `commonMain/Level.kt` | `docs/index.md` (log levels table), `docs/advanced/index.md` |
   | `commonMain/Pack.kt` | `docs/advanced/index.md`, `docs/advanced/custom-trainers.md` |
   | `commonMain/Trainer.kt` | `docs/advanced/custom-trainers.md` |
   | Any trainer | `docs/advanced/custom-trainers.md` |

3. **Read each affected docs page** and compare against the current code. Look for:
   - Outdated method signatures or parameters
   - Missing new features or trainers
   - Removed features still being documented
   - Code examples that no longer compile
   - Broken internal links (especially `See Also` sections)

4. **Update only what is stale** — don't rewrite pages that are still accurate. Make surgical edits.

5. **Report what changed** — summarize which docs were updated and why, and flag anything that needs human judgment (e.g. comparison table claims, external links).
