# POST `/api/e-invoice/generate` — Full Test Cases

## How I discovered these test cases

I read the API's actual validation code. The `/generate` endpoint runs **5 validation layers** before creating an invoice:

```
Your Payload
    │
    ▼
Layer 1: Regex Validation (regexPatterns.js)
    → Checks field FORMAT: GSTIN pattern, Pin digits, Date format, etc.
    │
    ▼
Layer 2: Schema Validation (jsonSchemaValidator.js)
    → Checks STRUCTURE: required fields, data types, enum values
    │
    ▼
Layer 3: Tax Consistency (inline in eInvoice.js)
    → Inter-state → CGST & SGST must be 0
    → Intra-state → IGST must be 0
    │
    ▼
Layer 4: Amount Validation (validateAmounts)
    → TotAmt = Qty × UnitPrice
    → TotItemVal = AssAmt + IgstAmt + CgstAmt + SgstAmt
    → ValDtls sums match ItemList sums
    → TotInvVal > 0
    │
    ▼
Layer 5: Duplicate Check (findDuplicateInvoice)
    → Same SellerGstin + DocType + DocNo + DocDate = 409 Conflict
    │
    ▼
✅ All pass → Generate IRN → Store → Return 201
```

Each layer is a DIFFERENT type of validation. Each needs its own test cases.

---

## Test Cases by Layer

### Layer 0: Happy Path + Response Validation

| # | Test Case | Input | Expected | Notes |
|---|-----------|-------|----------|-------|
| 0.1 | Valid payload → 201 Created | Full valid payload from GET /sample/1 → `data` field | Status 201, `success: true`, `message: "E-Invoice generated successfully"` | |
| 0.2 | Response has Irn, AckNo, AckDt, QRCode | Same | All 4 fields present and are strings | |
| 0.3 | AckDt in dd/MM/yyyy format | Same | `data.AckDt` matches `dd/MM/yyyy` | |
| 0.4 | SignedInvoice echoes the payload back | Same | `data.SignedInvoice` contains the same SellerDtls, BuyerDtls, etc. you sent | |
| 0.5 | Round-trip: POST → GET confirms creation | POST, extract Irn, then GET /invoices?irn= | `data` not empty, `invoiceNo` matches, `totalValue` matches, `status = Generated` | |
| 0.6 | No auth → 401 | Remove x-api-key header | Status 401 | |
| 0.7 | GET method → 405 | Send GET to /generate | Status 405 | |

**Subtotal: 7 tests**

---

### Layer 1: Regex / Format Validation (EP Groups)

The API validates these patterns (from `regexPatterns.js`). I group fields by SAME validation rule and test ONE representative per group.

| # | EP Group | Representative Field | Valid Input | Invalid Input (break format) | Expected on Invalid |
|---|----------|---------------------|-------------|------------------------------|---------------------|
| 1.1 | **Seller GSTIN** (15-char: 2 digits + 13 alphanumeric) | `SellerDtls.Gstin` | `"29AABCT1332L1Z1"` | `"INVALID"` (too short, wrong format) | 400, regex error |
| 1.2 | **Buyer GSTIN** (same pattern BUT also allows `"URP"`) | `BuyerDtls.Gstin` | `"29AWGPV7107B1Z1"` or `"URP"` | `"XYZ"` (3 chars but not URP) | 400, regex error |
| 1.3 | **Pin code** (6 digits, starts with 1-9) | `SellerDtls.Pin` | `560100` | `"ABCDEF"` or `56000` (5 digits) | 400, regex error |
| 1.4 | **State code** (01-38, 96, 97, 99) | `SellerDtls.Stcd` | `"29"` | `"40"` or `"00"` | 400, regex error |
| 1.5 | **Date format** (dd/MM/yyyy) | `DocDtls.Dt` | `"01/01/2024"` | `"2024-01-01"` (ISO format) | 400, regex error |
| 1.6 | **Doc number** (starts letter/1-9, up to 16 chars) | `DocDtls.No` | `"INV/2024/001"` | `"0INVALID"` (starts with 0) | 400, regex error |
| 1.7 | **HSN code** (4-8 digits) | `ItemList[0].HsnCd` | `"84713000"` | `"123"` (3 digits) | 400, regex error |
| 1.8 | **Y/N enum** (RegRev, IsServc, IgstOnIntra — all same rule) | `TranDtls.RegRev` | `"N"` | `"X"` | 400, regex error |
| 1.9 | **SupTyp enum** (B2B/SEZWP/SEZWOP/EXPWP/EXPWOP/DEXP) | `TranDtls.SupTyp` | `"B2B"` | `"BLAH"` | 400, regex error |
| 1.10 | **DocTyp enum** (INV/CRN/DBN) | `DocDtls.Typ` | `"INV"` | `"ABC"` | 400, regex error |

**Why Seller GSTIN and Buyer GSTIN are SEPARATE groups:**
Buyer GSTIN allows `"URP"` for export/unregistered parties. Seller GSTIN doesn't. Different validation rule = different group = separate test.

**Subtotal: 10 tests**

---

### Layer 2: Schema / Required Fields / Types

The API checks required fields and data types from `INVOICE_SCHEMA`.

| # | Test Case | Input | Expected |
|---|-----------|-------|----------|
| 2.1 | Empty body `{}` | `{}` | 400, errors list all 7 required top-level fields |
| 2.2 | Missing one required section (SellerDtls) | Everything valid EXCEPT remove SellerDtls entirely | 400, error says "Missing required field 'SellerDtls'" |
| 2.3 | Missing required nested field (BuyerDtls.Pos) | Everything valid EXCEPT remove Pos from BuyerDtls | 400, error says "Missing required field 'Pos'" |
| 2.4 | Empty ItemList `[]` | Everything valid EXCEPT `"ItemList": []` | 400, error says "Array has 0 items, minimum is 1" |
| 2.5 | Wrong type: send string where number expected | `"TotInvVal": "not-a-number"` | 400, type mismatch error |
| 2.6 | Wrong type: send number where string expected | `"Gstin": 12345` | 400, type mismatch error |
| 2.7 | Version must be "1.1" exactly | `"Version": "2.0"` | 400, enum error |
| 2.8 | LglNm too short (min 3 chars) | `"LglNm": "AB"` (2 chars) | 400, minLength error |

**Subtotal: 8 tests**

---

### Layer 3: Tax Consistency (Inter/Intra-State Logic)

| # | Test Case | Input | Expected |
|---|-----------|-------|----------|
| 3.1 | Intra-state with IGST > 0 → rejected | SellerStcd=29, Pos=29 (same state), BUT IgstVal=1000 | 400, "IgstVal must be 0 for CGST+SGST transactions" |
| 3.2 | Inter-state with CGST > 0 → rejected | SellerStcd=29, Pos=07 (different state), BUT CgstVal=1000 | 400, "CgstVal must be 0 for IGST transactions" |
| 3.3 | Inter-state with SGST > 0 → rejected | SellerStcd=29, Pos=07, BUT SgstVal=1000 | 400, "SgstVal must be 0 for IGST transactions" |

**Subtotal: 3 tests**

---

### Layer 4: Amount / Cross-Field Math Validation

| # | Test Case | Input | Expected |
|---|-----------|-------|----------|
| 4.1 | TotItemVal ≠ AssAmt + IgstAmt + CgstAmt + SgstAmt | Set TotItemVal to 999999 | 400, "TotItemVal does not match AssAmt + IgstAmt + CgstAmt + SgstAmt" |
| 4.2 | TotAmt ≠ Qty × UnitPrice | Set TotAmt to 1 (when Qty=5, UnitPrice=75000) | 400, "TotAmt does not match Qty * UnitPrice" |
| 4.3 | ValDtls.AssVal ≠ sum of ItemList AssAmt | Set AssVal to 1 | 400, "AssVal does not match sum of ItemList AssAmt" |
| 4.4 | TotInvVal ≤ 0 (for INV type) | Set TotInvVal to 0 | 400, "TotInvVal must be greater than 0" |
| 4.5 | Negative UnitPrice (for INV type) | Set UnitPrice to -100 | 400, "UnitPrice must not be negative" |

**Subtotal: 5 tests**

---

### Layer 5: Duplicate Detection

| # | Test Case | Input | Expected |
|---|-----------|-------|----------|
| 5.1 | Same document submitted twice → 409 | POST once (success), then POST again with exact same payload | 409, "IRN already generated for this document" |
| 5.2 | Same document → response contains original IRN | Same as above | Response has `data.Irn` matching the first call's IRN |

**Subtotal: 2 tests**

---

## Grand Total

| Layer | What it tests | Count |
|-------|--------------|-------|
| 0: Happy Path + Response | Does it work? Is the response correct? | 7 |
| 1: Regex / Format (EP) | Does it reject bad formats? | 10 |
| 2: Schema / Required / Types | Does it reject missing or wrong-typed fields? | 8 |
| 3: Tax Consistency | Does it catch inter/intra-state tax mismatches? | 3 |
| 4: Amount Math | Does it catch wrong calculations? | 5 |
| 5: Duplicate | Does it reject duplicate documents? | 2 |
| **TOTAL** | | **35** |

---

## How to break fields in RestAssured (same as Postman)

In Postman, you edited one field at a time in the JSON. In RestAssured:

**Happy path → POJO** (type-safe, clean):
```
Use your existing EInvoicePayload POJO with valid data
```

**Breaking fields → HashMap** (easy to corrupt):
```
Start with a valid HashMap → remove one key, or put a wrong value
Each negative test = one HashMap with ONE thing wrong
```

That's how you break every single field — one HashMap per broken field, assert 400, check error message.

---

## What I am NOT re-testing here

The `SignedInvoice` in the response contains SellerDtls, BuyerDtls, ValDtls, ItemList — the same structure I already validate in my 22 GET /sample/1 tests. I'm not re-validating that structure.

POST tests focus on:
- Does the API CREATE correctly? (Layer 0)
- Does the API REJECT bad input? (Layers 1-5)
