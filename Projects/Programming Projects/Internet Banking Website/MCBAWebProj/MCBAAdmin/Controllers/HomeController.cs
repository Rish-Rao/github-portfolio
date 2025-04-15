using System.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using MCBAAdmin.Models;
using Microsoft.AspNetCore.Authorization;

namespace MCBAdmin.Controllers;

[AllowAnonymous]
public class HomeController : Controller
{
    private readonly ILogger<HomeController> _logger;

    public HomeController(ILogger<HomeController> logger) => _logger = logger;
    

    public IActionResult Index() => View();
    

    public IActionResult Privacy() => View();
    

    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public IActionResult Error() => View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });

}

