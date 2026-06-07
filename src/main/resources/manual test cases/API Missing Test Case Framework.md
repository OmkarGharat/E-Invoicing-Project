# API Missing Test Case Framework

Use this after writing first-draft test cases.

Goal: find what is missing quickly, in a repeatable way.

---

## 1) The 3-Phase Method

### Phase 1: WRITE
Write normal test cases first (happy path + basic validations).

### Phase 2: CHECK
Run the 8-point Missing Checklist.

### Phase 3: FILL
Add only missing high-value test cases (P0 first, then P1, then P2).

---

## 2) 8-Point Missing Checklist

For each point, mark:
- `Covered`
- `Partially Covered`
- `Missing`

### 1. Status and Headers
- Is status code correct? (200/201/etc.)
- Is `Content-Type` correct?

### 2. Response Structure
- Are all required top-level keys present?
- Are all required item-level keys present?

### 3. Data Types
- Are types correct? (`string`, `number`, `boolean`, `array`, `object`)

### 4. Business Rules
- Enums valid?
- Date format valid?
- Domain logic valid? (example: interstate rules)

### 5. Field Relationships (Most Common Missing Area)
- Do related fields match each other?
- Example checks:
  - `count == data.length`
  - `total == pagination.total`
  - `sort` metadata matches actual returned order

### 6. Negative Input Handling
- Wrong method -> proper error?
- Missing auth -> proper error?
- Invalid query params -> proper error/default behavior?

### 7. Boundary / Edge Cases
- Minimum values
- Maximum values
- Empty result
- Out-of-range page

### 8. Consumer Safety
- Can downstream client safely use this response?
- If response contains links/endpoints, do they actually work?

---

## 3) Priority Rule (What to Add First)

### P0 (Must Have)
- Status + structure + types
- Auth and error basics
- Field relationships

### P1 (Important)
- Sorting/pagination behavior
- Boundary values
- Cross-endpoint link checks

### P2 (Nice to Have)
- Rare combinations
- Extra robustness checks

---

## 4) Reusable Template (Copy For Any Endpoint)

## Endpoint
- Method:
- URL:
- Purpose (1 line):

## Existing Test Cases
- TC01:
- TC02:
- TC03:

## Missing Checklist
- [ ] 1. Status and Headers
- [ ] 2. Response Structure
- [ ] 3. Data Types
- [ ] 4. Business Rules
- [ ] 5. Field Relationships
- [ ] 6. Negative Input Handling
- [ ] 7. Boundary / Edge Cases
- [ ] 8. Consumer Safety

## Missing Tests To Add
- TCxx (P0):
- TCxx (P0):
- TCxx (P1):

## Coverage Score
- Before: __ / 8
- After: __ / 8

---

## 5) Filled Example: `GET /api/e-invoice/samples`

## Endpoint
- Method: `GET`
- URL: `/api/e-invoice/samples`
- Purpose: Return list of sample invoice summaries.

## Existing Test Cases (Already Good)
- Status 200 + JSON content type
- Required keys present
- Item keys present
- Enum checks (`type`, `documentType`)
- Interstate logic
- Non-GET method check

## Missing Checklist (Example Evaluation)
- [x] 1. Status and Headers
- [x] 2. Response Structure
- [~] 3. Data Types (partially covered)
- [x] 4. Business Rules
- [ ] 5. Field Relationships
- [ ] 6. Negative Input Handling (auth + invalid query)
- [ ] 7. Boundary / Edge Cases (paging boundaries)
- [ ] 8. Consumer Safety (endpoint link integrity)

Legend:
- `[x]` Covered
- `[~]` Partially Covered
- `[ ]` Missing

## Missing Tests To Add (Concrete)
- TC24 (P0): Verify `count == data.length`.
- TC25 (P0): Verify `total == pagination.total`.
- TC26 (P0): Request without auth, expect 401 with clear error body.
- TC27 (P1): Verify each row `endpoint` is callable and matches row `id`.
- TC28 (P1): Validate pagination math: `pages == ceil(total/limit)`.
- TC29 (P1): For `sort=id&order=desc`, verify actual IDs are descending.
- TC30 (P1): Invalid `page`/`limit` (0, negative, non-numeric) -> documented error/default behavior.

## Coverage Score (Example)
- Before: 4 / 8
- After: 8 / 8

---

## 6) 60-Second Quick Use (Daily)

When reviewing any endpoint:
1. Fill endpoint details.
2. Tick 8 checklist points fast.
3. Add only missing P0/P1 tests.
4. Stop when score reaches at least 7/8.

That is enough for strong practical API coverage in most projects.
