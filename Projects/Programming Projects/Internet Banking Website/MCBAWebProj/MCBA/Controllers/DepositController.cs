using MCBA.Data;
using MCBA.Models;
using Newtonsoft.Json;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using static MCBA.Utils.ConstValues;
using static MCBA.Utils.HelperMethods;

namespace MCBA.Controllers;

// Deposit controller
public class DepositController : Controller
{
    private readonly McbaContext _context;

    // Deposit Session
    public const string SessionKey_Amount = $"{nameof(Transaction.Account)}";
    public const string SessionKey_Comment = $"{nameof(Transaction.Comment)}";

    private int _customerID => HttpContext.Session.GetInt32(nameof(Customer.CustomerID)).Value;

    // Constructor 
    public DepositController(McbaContext context) => _context = context;

   // Deposit View
    [HttpGet]
    public async Task<IActionResult> Deposit()
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        return View(account);
    }

    // Retrives deposit on data 
    [HttpPost]
    public async Task<IActionResult> Deposit(decimal amount, string? comment)
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        if (amount <= 0)
            ModelState.AddModelError(nameof(amount), "Amount must be positive.");
        if (amount.HasMoreThanTwoDecimalPlaces())
            ModelState.AddModelError(nameof(amount), "Amounts can't have more than 2 decimal places");
        if (!ModelState.IsValid)
        {
            ViewBag.Amount = amount;
            return View(accountNumber);
        }

        if (comment == null || comment == string.Empty)
            comment = string.Empty;

        HttpContext.Session.SetString(SessionKey_Amount, amount.ToString());
        HttpContext.Session.SetString(SessionKey_Comment, comment);

        return RedirectToAction(nameof(DepositConfirmation));
    }

    // Deposit confirmation page
    [HttpGet]
    public async Task<IActionResult> DepositConfirmation()
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        return View(account);
    }

    [HttpPost]
    public async Task<IActionResult> DepositNow(int id)
    {
        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == id);

        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (account == null || account.CustomerID != _customerID)
            return RedirectToAction("Index", "Customer");

        string comment = null;
        if (HttpContext.Session.GetString(SessionKey_Comment) != string.Empty)
            comment = HttpContext.Session.GetString(SessionKey_Comment);

        var ammount = Decimal.Parse(HttpContext.Session.GetString(SessionKey_Amount));

        account.Balance += ammount;
        account.Transactions.Add(
        new Transaction
        {
            TransactionType = ((char)TransactionType.Deposit),
            AccountNumber = account.AccountNumber,
            DestinationAccountNumber = null,
            Amount = ammount,
            Comment = comment,
            TransactionTimeUtc = DateTime.UtcNow
        });

        await _context.SaveChangesAsync();

        return RedirectToAction("Accounts", "Account", new { id });
    }
}

