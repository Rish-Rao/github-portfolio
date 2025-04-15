using MCBA.Data;
using MCBA.Models;
using MCBA.Utils;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using static MCBA.Utils.ConstValues;
using static MCBA.Utils.HelperMethods;
using static MCBA.Utils.TransactionHelperMethods;

namespace MCBA.Controllers;

// Transafer Controller
public class TransferController : Controller
{
    private readonly McbaContext _context;
    private int _customerID => HttpContext.Session.GetInt32(nameof(Customer.CustomerID)).Value;

    // Transfer Session
    public const string SessionKey_Amount = $"{nameof(Transaction.Account)}";
    public const string SessionKey_Comment = $"{nameof(Transaction.Comment)}";
    public const string SessionKey_DestAccountNumber = $"{nameof(Transaction.DestinationAccountNumber)}";

    // Constructor 
    public TransferController(McbaContext context) => _context = context;

    // Transfer view page
    [HttpGet]
    public async Task<IActionResult> Transfer()
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        return View(account);
    }

    // Gets transfer data and stores it in sessions variables
    [HttpPost]
    public async Task<IActionResult> Transfer(int destAccountNumber, decimal amount, string? comment)
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        // Dest Acc validation 
        if (!DestAccountExists(destAccountNumber))
            ModelState.AddModelError(nameof(destAccountNumber), "Destination Account number does not exists");
        if (destAccountNumber == account.AccountNumber)
            ModelState.AddModelError(nameof(destAccountNumber),
                "Destination account number can't be same as current account number");

        var availableBalance = account.ComputeAvailableBalance(TransactionType.Withdraw);

        // Amount validation
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
        HttpContext.Session.SetInt32(SessionKey_DestAccountNumber, destAccountNumber);

        return RedirectToAction(nameof(TransferConfirmation));
    }

    // Transsfer Confirmation page view
    [HttpGet]
    public async Task<IActionResult> TransferConfirmation()
    {
        var accountNumber = HttpContext.Session.GetInt32(nameof(Account.AccountNumber));
        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (string.IsNullOrEmpty(accountNumber.ToString()))
            return RedirectToAction("Index", "Customer");

        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == accountNumber);

        return View(account);
    }

    // Updates database with transfer
    [HttpPost]
    public async Task<IActionResult> TransferNow(int id)
    {
        var destAccountNumber = HttpContext.Session.GetInt32(SessionKey_DestAccountNumber);

        var account = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == id);

        // Returns to customer page if does'nt exists or customer id is not same as logged in
        if (account == null || account.CustomerID != _customerID)
            return RedirectToAction("Index", "Customer");

        var destAccount = await _context.Accounts.Include(x => x.Transactions).
            FirstOrDefaultAsync(x => x.AccountNumber == destAccountNumber);

        // Comment
        string comment = null;
        if (HttpContext.Session.GetString(SessionKey_Comment) != string.Empty)
            comment = HttpContext.Session.GetString(SessionKey_Comment);

        var amount = Decimal.Parse(HttpContext.Session.GetString(SessionKey_Amount));

        // Balance Update
        account.Balance -= amount;
        destAccount.Balance += amount;

        // Service Fee Transaction
        if (account.ServiceFeeRequired())
        {
            account.Balance -= TransferFee;
            account.Transactions.Add(
            new Transaction
            {
                TransactionType = ((char)TransactionType.ServiceCharge),
                AccountNumber = account.AccountNumber,
                DestinationAccountNumber = null,
                Amount = TransferFee,
                Comment = null,
                TransactionTimeUtc = DateTime.UtcNow
            });
        }

        account.Transactions.Add(
        new Transaction
        {
            TransactionType = ((char)TransactionType.Transfer),
            AccountNumber = account.AccountNumber,
            DestinationAccountNumber = destAccountNumber,
            Amount = amount,
            Comment = comment,
            TransactionTimeUtc = DateTime.UtcNow
        });

        destAccount.Transactions.Add(
        new Transaction
        {
            TransactionType = ((char)TransactionType.Transfer),
            AccountNumber = account.AccountNumber,
            DestinationAccount = null,
            Amount = amount,
            Comment = comment,
            TransactionTimeUtc = DateTime.UtcNow
        });

        await _context.SaveChangesAsync();

        return RedirectToAction("Accounts", "Account", new { id });
    }

    // Checks if destination account exists
    private bool DestAccountExists(int destAccountNumber)
    {
        var accounts = _context.Accounts.Include(x => x.Transactions).ToList();

        foreach (var destAccount in accounts)
        {
            if (destAccount.AccountNumber == destAccountNumber)
                return true;
        }
        return false;
    }
}

