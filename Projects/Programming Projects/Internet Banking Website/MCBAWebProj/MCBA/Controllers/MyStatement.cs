using MCBA.Data;
using MCBA.Models;
using X.PagedList;
using Newtonsoft.Json;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using static MCBA.Utils.ConstValues;

namespace MCBA.Controllers;

// MyStatement controller
public class MyStatementController : Controller
{
    private readonly McbaContext _context;

    // Constructor 
    public MyStatementController(McbaContext context) => _context = context;

    // MyStatement View
    [HttpGet]
    public async Task<IActionResult> MyStatement(int? page = 1)
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.BillPays).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        ViewBag.Account = account;

        // paging
        const int pageSize = 4;
        var pagedList = await _context.Transactions.Include(x => x.Account).
            Where(x => x.AccountNumber == accountNumber).
            OrderByDescending(x => x.TransactionTimeUtc).
            ToPagedListAsync(page, pageSize);

        return View(pagedList);
    }
}

