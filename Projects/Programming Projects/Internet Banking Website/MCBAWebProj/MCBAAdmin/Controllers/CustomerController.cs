using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using MCBAAdmin.Models;
using MCBAdmin.Controllers;
using static MCBAAdmin.Utils.HelperMethods;
using System.Text;
using System.Security.Principal;

namespace MCBAAdmin.Controllers;

public class CustomerController : Controller
{
    private readonly IHttpClientFactory _clientFactory;
    private HttpClient Client => _clientFactory.CreateClient("api");

    // Controller
    public CustomerController(IHttpClientFactory clientFactory) => _clientFactory = clientFactory;

    /** GET: customer
     * Displays list of customers
     */
    public async Task<IActionResult> Customers()
    {
        var response = await Client.GetAsync("customer");

        if (!response.IsSuccessStatusCode)
            return RedirectToAction("Index", "Home");

        var result = await response.Content.ReadAsStringAsync();

        var customer = JsonConvert.DeserializeObject<List<Customer>>(result);

        return View(customer);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> LockUnlock(int customerID)
    {
        var response = await Client.GetAsync($"customer/{customerID}");
        if (!response.IsSuccessStatusCode)
            return RedirectToAction(nameof(Customers));

        var result = await response.Content.ReadAsStringAsync();
        var customer = JsonConvert.DeserializeObject<Customer>(result);

        if (customer.AccountState == ((char)Accountstate.Unlocked))
            customer.AccountState = ((char)Accountstate.Locked);
        else
            customer.AccountState = ((char)Accountstate.Unlocked);

        var content = new StringContent(JsonConvert.SerializeObject(customer), Encoding.UTF8, "application/json");
        var postResponse = Client.PutAsync("customer", content).Result;

        return RedirectToAction(nameof(Customers));
    }

    [HttpGet]
    public async Task<IActionResult> Edit(int id)
    {
        var response = await Client.GetAsync($"customer/{id}");
        if (!response.IsSuccessStatusCode)
            return RedirectToAction(nameof(Customers));

        var result = await response.Content.ReadAsStringAsync();
        var customer = JsonConvert.DeserializeObject<Customer>(result);

        return View(customer);
    }

    [HttpPost]
    public async Task<IActionResult> Edit(string name, string? tfn,
        string? address, string? city, string? state, string? postcode,
        string? mobile, int customerID)
    {
        var response = await Client.GetAsync($"customer/{customerID}");
        if (!response.IsSuccessStatusCode)
            return RedirectToAction(nameof(Customers));

        var result = await response.Content.ReadAsStringAsync();
        var customer = JsonConvert.DeserializeObject<Customer>(result);

        if(state != null && state != string.Empty)
            if(!StateExists(state.ToUpper()))
                ModelState.AddModelError(nameof(state), "State Doesnt Exists");
        if (!ModelState.IsValid)
        {
            return View(customer);
        }

        customer.Name = name;
        if (tfn == null || tfn == string.Empty)
            customer.TFN = null;
        else
            customer.TFN = tfn;

        if (address == null || address == string.Empty)
            customer.Address = null;
        else
            customer.Address = address;

        if (city == null || city == string.Empty)
            customer.City = null;
        else
            customer.City = city;

        if (state == null || state == string.Empty)
            customer.State = null;
        else
            customer.State = state;

        if (postcode == null || postcode == string.Empty)
            customer.Postcode = null;
        else
            customer.Postcode = postcode;

        if (mobile == null || mobile == string.Empty)
            customer.Mobile = null;
        else
            customer.Mobile = mobile;

        var content = new StringContent(JsonConvert.SerializeObject(customer), Encoding.UTF8, "application/json");
        var postResponse = Client.PutAsync("customer", content).Result;

        return RedirectToAction(nameof(Customers));
    }
}

