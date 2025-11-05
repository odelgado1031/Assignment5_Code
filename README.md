# Assignment 5 — Unit, Mocking & Integration Testing

[![CI Status](https://github.com/OWNER/REPO/actions/workflows/SE333_CI.yml/badge.svg)](https://github.com/OWNER/REPO/actions/workflows/SE333_CI.yml)

## Overview
This repository contains my solution for SE 333 Assignment 5. It includes tests and a CI workflow that runs Checkstyle (static analysis), JUnit tests, and JaCoCo coverage on each push.

**Workflow file:** `.github/workflows/SE333_CI.yml`  
**Status:** All GitHub Actions checks (static analysis, tests, and coverage upload) are passing on `main`. See the badge above and the Actions tab for the latest run.

---

## Written Responses & Notes

### Part 1 — BarnesAndNoble testing
- **Specification-based tests:** Designed from the external behavior (expected inputs/outputs). I verified correct behavior for typical lookups and user-facing scenarios (e.g., valid identifiers return expected results; invalid inputs are handled gracefully).
- **Structural-based tests:** Targeted internal control-flow and edge cases to exercise alternative branches and boundary conditions (e.g., missing/unknown items, empty inputs, and any conditional paths uncovered by the code).

### Part 3 — Amazon testing
- **Integration tests (`AmazonIntegrationTest`):** End-to-end verification that totals are computed correctly when combining multiple pricing rules (e.g., subtotal plus delivery-tier charges). Tests include both a **specification-based** scenario and a **structural-based** tier-boundary scenario.
- **Unit tests (`AmazonUnitTest`):** Focused on individual classes in isolation. Where a dependency would constitute an external service or shared state, I used mocks/stubs so unit tests stay isolated and deterministic.

