using MCBA.Data;
using MCBA.Models;
using MCBA.Utils;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using static System.Runtime.InteropServices.JavaScript.JSType;
using static MCBA.Utils.ConstValues;
using static MCBA.Utils.HelperMethods;
using static MCBA.Utils.TransactionHelperMethods;

namespace MCBA.Controllers;

// Withdraw controller
public class WithdrawController : Controller
{
    private readonly McbaContext _context;

    // Deposit Session
    public const string SessionKey_Amount = $"{nameof(Transaction.Account)}";
    public const string SessionKey_Comment = $"{nameof(Transaction.Comment)}";

    private int _customerID => HttpContext.Session.GetInt32(nameof(Customer.CustomerID)).Value;

    // Constructor 
    public WithdrawController(McbaContext context) => _context = context;

    // Withdraw view
    [HttpGet]
    public async Task<IActionResult> Withdraw()
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);
        return View(account);
    }

    //Gets Withdraw data and stores it in sessions variables[HttpPost]
    public async Task<IActionResult> Withdraw(decimal amount, string? comment)
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        var availableBalance = account.ComputeAvailableBalance(TransactionType.Withdraw);
        
        // Ammount validation 
        if (amount <= 0)
            ModelState.AddModelError(nameof(amount), "Amount must be positive.");
        if (amount.HasMoreThanTwoDecimalPlaces())
            ModelState.AddModelError(nameof(amount), "Amounts can't have more than 2 decimal places");
        if (amount > availableBalance)
            ModelState.AddModelError(nameof(amount), "Amounts cannot be more than available balance");
        if (!ModelState.IsValid)
        {
            ViewBag.Amount = amount;
            return View(account);
        }

        if (comment == null || comment == string.Empty)
            comment = string.Empty;

        HttpContext.Session.SetString(SessionKey_Amount, amount.ToString());
        HttpContext.Session.SetString(SessionKey_Comment, comment);

        return RedirectToAction(nameof(WithdrawConfirmation), new { accountNumber });
    }

    // Withdraw confirmation view
    [HttpGet]
    public async Task<IActionResult> WithdrawConfirmation()
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        return View(account);
    }

    // Updates database with withdraw
    [HttpPost]
    public async Task<IActionResult> WithdrawNow(int id)
    {
        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == id);

        if (account == null || account.CustomerID != _customerID)
            return RedirectToAction("Index", "Customer");

        string comment = null;
        if (HttpContext.Session.GetString(SessionKey_Comment) != string.Empty)
            comment = HttpContext.Session.GetString(SessionKey_Comment);

        var amount = Decimal.Parse(HttpContext.Session.GetString(SessionKey_Amount));

        account.Balance -= amount;

        // Service Fee Transaction
        if (account.ServiceFeeRequired())
        {
            account.Balance -= WithdrawFee;
            account.Transactions.Add(
            new Transaction
            {
                TransactionType = ((char)TransactionType.ServiceCharge),
                AccountNumber = account.AccountNumber,
                DestinationAccountNumber = null,
                Amount = WithdrawFee,
                Comment = null,
                TransactionTimeUtc = DateTime.UtcNow
            });
        }

        account.Transactions.Add(
        new Transaction
        {
            TransactionType = ((char)TransactionType.Withdraw),
            AccountNumber = account.AccountNumber,
            DestinationAccountNumber = null,
            Amount = amount,
            Comment = comment,
            TransactionTimeUtc = DateTime.UtcNow
        });

        await _context.SaveChangesAsync();

        return RedirectToAction("Accounts", "Account", new { id });
    }
}

