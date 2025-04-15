using MCBA.Data;
using MCBA.Utils;
using MCBA.Models;
using X.PagedList;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using static MCBA.Utils.ConstValues;

namespace MCBA.Controllers;

// Bill Pay
public class BillPayController : Controller
{
    private readonly McbaContext _context;

    // Constructor 
    public BillPayController(McbaContext context) => _context = context;

    // List og bills
    [HttpGet]
    public async Task<IActionResult> BillPay(int? page = 1)
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
        var pagedList = await _context.BillPays.Include(x => x.Account).
            Where(x => x.AccountNumber == accountNumber).
            OrderByDescending(x => x.ScheduleTimeUtc).
            ThenBy(x => x.State == ((char)BillPayState.InsufficientFunds)).
            ThenBy(x => x.State == ((char)BillPayState.Pending)).
            ToPagedListAsync(page, pageSize);

        return View(pagedList);
    }

    // Add Bill View
    [HttpGet]
    public async Task<IActionResult> AddBill()
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.BillPays).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        return View(account);
    }

    // Adds bill to db
    [HttpPost]
    public async Task<IActionResult> AddBill(int payeeID, decimal amount,
        DateTime dateTime, char period)
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.BillPays).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        // Payee ID / Date / Amount Validation
        if (!PayeeAccountExists(payeeID))
            ModelState.AddModelError(nameof(payeeID), "Destination Account number does not exists");
        if(dateTime.ToUniversalTime() < DateTime.UtcNow)
            ModelState.AddModelError(nameof(dateTime), "Schedule Time has to be in the future");
        if (amount <= 0)
            ModelState.AddModelError(nameof(amount), "Amount must be positive.");
        if (amount.HasMoreThanTwoDecimalPlaces())
            ModelState.AddModelError(nameof(amount), "Amounts can't have more than 2 decimal places");
        if (!ModelState.IsValid)
            return View(account);

        // New bill obj
        account.BillPays.Add(
        new BillPay
        {
            AccountNumber = account.AccountNumber,
            PayeeID = payeeID,
            Amount = amount,
            ScheduleTimeUtc = dateTime.ToUniversalTime(),
            Period = period,
            State = ((char)BillPayState.Pending)
        });

        // Add bill to bd
        await _context.SaveChangesAsync();

        return RedirectToAction(nameof(BillPay));
    }

    // Removes a bill from db
    [HttpPost]
    public async Task<IActionResult> Remove(int billPayID)
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.BillPays).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        var billPay = account.BillPays.FirstOrDefault(x => x.BillPayID == billPayID);
        _context.BillPays.Remove(billPay);
        _context.SaveChangesAsync();
         
        return RedirectToAction(nameof(BillPay));
    }

    // Checks if payee account exists
    private bool PayeeAccountExists(int payeeID)
    {
        var payees = _context.Payees.ToList();

        foreach (var payee in payees)
        {
            if (payee.PayeeID == payeeID)
                return true;
        }
        return false;
    }

}

