package testcases;

import base.TestBase;

public class InvoiceWorkflowTest extends TestBase {

//	POST /validate (valid payload) → 200, isValid: true
//    ↓ same payload
//	
//	POST /generate → 200, returns IRN + AckNo
//    ↓ use IRN
//
//	GET /invoices?irn=XXX → 200, invoice exists
//    ↓ use IRN  
//	
//	POST /cancel (IRN) → 200, cancelled
//    ↓ verify
//	
//	GET /invoices?irn=XXX → status = "Cancelled"

	
}
