using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Diagnostics;
using MCBA.Models;
using Microsoft.EntityFrameworkCore;
using MCBA.Data;

namespace MCBA.Controllers;

[AllowAnonymous]
public class HomeController : Controller
{
    private readonly McbaContext _context;
    private readonly ILogger<HomeController> _logger;

    // Constructor
    public HomeController(ILogger<HomeController> logger, McbaContext context)
    {
        _context = context;
        _logger = logger;
    }

    public IActionResult Index() => View();

    public IActionResult Privacy() => View();

    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public IActionResult Error()
    {
        return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
    }
}

