using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Mvc;
using MCBA.Models;
using MCBA.Data;

namespace MCBA.Controllers;

public class AccountController : Controller
{
    private readonly McbaContext _context;
    private int _customerID => HttpContext.Session.GetInt32(nameof(Customer.CustomerID)).Value;

    // Constructor 
    public AccountController(McbaContext context) => _context = context;

    // Account details view
    [HttpGet]
    public async Task<IActionResult> Accounts(int id)
    {
        HttpContext.Session.SetInt32(nameof(Account.AccountNumber), id);

        var account = await _context.Accounts.Include(x => x.Transactions).
        FirstOrDefaultAsync(x => x.AccountNumber == id);

        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (account == null || account.CustomerID != _customerID)
            return RedirectToAction("Index", "Customer");

        return View(account);
    }
}

