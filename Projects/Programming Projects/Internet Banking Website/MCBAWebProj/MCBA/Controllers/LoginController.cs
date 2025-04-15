using Microsoft.AspNetCore.Authorization;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Mvc;
using static MCBA.Utils.ConstValues;
using SimpleHashing.Net;
using MCBA.Models;
using MCBA.Data;

namespace MCBA.Controllers;

// Login controller
[AllowAnonymous]
public class LoginController : Controller
{ 
    private readonly McbaContext _context;
    private readonly ISimpleHash _simpleHash = new SimpleHash();

    // Constructor
    public LoginController(McbaContext context) => _context = context;

    // Login View
    [HttpGet]
    public IActionResult Login() => View();

    // Logs use in with valid credentials 
    [HttpPost]
    public async Task<IActionResult> Login(string loginID, string passwordHash)
    {
        var login = await _context.Logins.Include(x => x.Customer).
            FirstOrDefaultAsync(x => x.LoginID == loginID);

        if (login == null || string.IsNullOrEmpty(passwordHash) || !_simpleHash.Verify(passwordHash, login.PasswordHash))
            ModelState.AddModelError("LoginFailed", "Login failed, please try again.");
        if(login.Customer.AccountState.Equals(((char)Accountstate.Locked)))
            ModelState.AddModelError("LoginFailed", "Your Account has been locked");
        if (!ModelState.IsValid)
        {
            return View(new Login { LoginID = loginID });
        }

        // Login customer
        HttpContext.Session.SetInt32(nameof(Customer.CustomerID), login.CustomerID);
        HttpContext.Session.SetString(nameof(Customer.Name), login.Customer.Name);

        return RedirectToAction("Index", "Customer");
    }

    // Logout 
    public IActionResult Logout()
    {
        // Logout customer.
        HttpContext.Session.Clear();

        return RedirectToAction("Index", "Home");
    }
}

