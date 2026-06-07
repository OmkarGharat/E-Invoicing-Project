1. **GET `api/e-invoice/sample/1` returns 200 and Content-Type is application/json**

2. **JSON SCHEMA VALIDATION**

3. **TranDtls.SupTyp** → B2B, SEZWP, SEZWOP, EXPWP, EXPWOP, DEXP  

4. **TranDtls.RegRev** → Y, N  

5. **DocDtls.Typ** → INV, CRN, DBN  

6. Verify that the Pin provided in `SellerDtls` or `BuyerDtls` matches the corresponding `Stcd` (State Code) as per India Post master data.  

7. `buyerState`, `sellerState`, and `Pos` (Place of Supply) must have values between 1 to 38 and also allow 96, 97, 99.  

8. If `sellerState` and `buyerState` are different and between 1 to 38, then `isInterstate` must be **true**.  

9. If `sellerState` and `buyerState` are the same and between 1 to 38, then `isInterstate` must be **false**.  

10. **Total Item Value** = AssAmt + IgstAmt + CgstAmt + SgstAmt + CesAmt + CesNonAdvlAmt + StateCesAmt + StateCesNonAdvlAmt + OthChrg  

11. **TotInvVal** = AssVal + IgstVal + SgstVal + CgstVal  

12. `ItemList` cannot be empty  

13. The `Unit` must be a valid UQM (check from Excel sheet), exactly 3 uppercase characters (e.g., `NOS` is correct, `Nos` is incorrect).  

14. Missing auth header throws **401**  

15. `ItemCount` matches the total number of items in `ItemList`  

16. Using any HTTP method other than **GET** should result in **405 Method Not Allowed**  

17. GSTIN is a valid number  

18. `ItemList.IsServc` → Y, N  

19. If `isInterstate = false` (intrastate), then:  
    - CgstVal == SgstVal  
    - IGST = 0  

20. If `isInterstate = true`, then:  
    - CgstVal == 0  
    - SgstVal == 0  

21. If `SupTyp` is **B2B**, both parties must have regular GSTINs  

22. Verify that `Qty`, `UnitPrice`, or `AssAmt` is not negative  