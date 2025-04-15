using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using MCBAAdmin.Models;

namespace MCBAAdmin.Controllers;

[AllowAnonymous]
public class LoginController : Controller
{
    // Admin Stored here because not required to store in database
    private Admin admin = new Admin()
    {
        Username = "admin",
        Password = "admin"
    };

    [HttpGet]
    public IActionResult Login() => View();

    // Redirects to homepage after login
    [HttpPost]
    public async Task<IActionResult> Login(string username, string password)
    {
        string adminUsername = admin.Username;
        string adminPassword = admin.Password;

        if (adminUsername != username || adminPassword != password)
        {
            ModelState.AddModelError("LoginFailed", "Login failed, please try again.");
            return View(admin);
        }

        // Login customer
        HttpContext.Session.SetString("username", username);

        return RedirectToAction("Index", "Home");
    }

    // Logout 
    public IActionResult Logout()
    {
        // Logout customer.
        HttpContext.Session.Clear();

        return RedirectToAction("Index", "Home");
    }
}



