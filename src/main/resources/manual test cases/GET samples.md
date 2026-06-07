# API Response and Validation Guidelines

**Endpoint:** `GET /api/e-invoice/samples` (base URL, e.g. `https://e-invoice-api.vercel.app`)

**How this list was built (5 questions — see `how_to_think_manual_test_cases_v2.md`):**

| Question | What it generates here |
|----------|-------------------------|
| Q1 What does it do? | Happy path: 200, list of sample invoice summaries for the UI / docs. |
| Q2 What shape? | Keys at root and per item; optional JSON Schema instead of repeating every key. |
| Q3 Rules on values? | Enums, dates, interstate logic, CRN negative amount, non-empty strings. |
| Q4 Break the input? | Wrong HTTP method, missing auth, bad query params (if the API supports them). |
| Q5 What breaks the consumer? | `count`/`total`/`data` consistent; each `endpoint` actually works; sort matches reality. |

---

1. `GET /api/e-invoice/samples` returns status code **200**, and `Content-Type` includes `application/json`.
2. The response includes the following keys: `success`, `data`, `count`, `total`, `filters`, `pagination`, and `sort`.
3. Each item in the `data` array contains the following keys:
   - `id`
   - `type`
   - `description`
   - `invoiceNo`
   - `totalValue`
   - `documentType`
   - `sellerState`
   - `buyerState`
   - `isInterstate`
   - `reverseCharge`
   - `itemCount`
   - `invoiceDate`
   - `endpoint`
4. The value of `id` must be unique (no duplicates).
5. The value of `invoiceNo` cannot be an empty string or null.
6. The value of `totalValue` cannot be null; it must be a **number** (for `documentType` `INV`, typically non-negative; for `CRN`, negative values are allowed per business rule).
7. If `documentType` is `CRN`, `totalValue` may be negative.
8. `invoiceDate` format must be `dd/MM/yyyy` (string).
9. The field `type` must be one of the following: `B2B`, `SEZWP`, `SEZWOP`, `EXPWP`, `EXPWOP`, `DEXP`.
10. The field `documentType` must be one of the following: `INV`, `CRN`, `DBN`.
11. The response must validate against the JSON Schema (when available), including **types** (e.g. `success` boolean, `id` number, `isInterstate` boolean).
12. The values of `buyerState` and `sellerState` must be valid state codes (typically `01`–`38` as two-digit strings, or `96`, `97`, `99` — align with your schema/spec).
13. If `sellerState` and `buyerState` are different and both are “domestic” state codes (1–38), then `isInterstate` must be `true`.
14. If `sellerState` and `buyerState` are the same and both are “domestic” state codes (1–38), then `isInterstate` must be `false`.
15. The value of `itemCount` must be at least 1.
16. **Cross-field consistency (consumer safety):** `count` must equal `data.length`; `total` must equal `pagination.total`; `pagination.page` and `pagination.limit` must match the request (or documented defaults).
17. **Pagination math:** `pagination.pages` must be consistent with `pagination.total` and `pagination.limit` (e.g. `pages = ceil(total / limit)` unless the API documents a different rule).
18. **Sort matches data:** For the default response, `data` must be ordered according to `sort.by` and `sort.order` (e.g. if `by` is `id` and `order` is `asc`, IDs are strictly ascending with no gaps in sequence only if that is a documented guarantee).
19. The value of `endpoint` cannot be an empty string or null; it must look like a path under `/api/e-invoice/sample/…`.
20. **Link integrity (Q5 — downstream):** For each row, `GET {baseUrl}{endpoint}` (with the same auth as list) returns **200** and a body whose sample identifier matches that row’s `id` (adjust assertion to your detail response shape).
21. The value of `pagination.page` must be at least 1.
22. The value of `pagination.limit` must be at least 1.
23. The value of `pagination.total` must be at least 1 when the list is non-empty.
24. The value of `pagination.pages` must be at least 1 when `total` ≥ 1.
25. Using any HTTP method other than GET (e.g., POST, PUT, DELETE, PATCH) on `/api/e-invoice/samples` should result in **405 Method Not Allowed** (or the status your API documents).
26. **Auth (Q4):** A request **without** `Authorization`, `x-api-key`, or `api_key` (per API message) must return **401** with a clear error body — unless a public anonymous mode is explicitly documented.
27. **Query parameters (if supported by the deployed API):** e.g. `?page=1&limit=2` returns at most 2 items and `pagination` reflects `page`, `limit`, `total`, `pages`; invalid values (e.g. `page=0`, `limit=0`, non-numeric) return **400** or documented defaults — **confirm against actual API behavior once**.
28. **Sorting query (if supported):** e.g. `sort=id&order=desc` returns `data` ordered by `id` descending; `order=asc` ascending. Invalid `sort`/`order` return **400** or a documented fallback — **confirm against actual API behavior once**.

---

## What you already had vs what was added

- **Kept:** Structure, enums, interstate rules, CRN, methods other than GET, basic pagination fields, JSON Schema.
- **Added:** Correct path, type hints, **consistency** (`count` / `total` / pagination), **sort vs actual order**, **auth**, **each `endpoint` works**, and **query-param** tests phrased so you verify behavior on the real deployment.
