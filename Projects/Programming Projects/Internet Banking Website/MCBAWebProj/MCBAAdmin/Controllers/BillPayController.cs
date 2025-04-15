using MCBAAdmin.Models;
using System.Text;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using static MCBAAdmin.Utils.HelperMethods;

namespace MCBAAdmin.Controllers;

public class BillPayController : Controller
{
    private readonly IHttpClientFactory _clientFactory;
    private HttpClient Client => _clientFactory.CreateClient("api");

    // Controller
    public BillPayController(IHttpClientFactory clientFactory) => _clientFactory = clientFactory;

    /** 
     * GET: BillPay
     * Returns list of billpays
     */
    public async Task<IActionResult> BillPays()
    {
        var response = await Client.GetAsync("billpay");

        if (!response.IsSuccessStatusCode)
            return RedirectToAction("Index", "Home");

        var result = await response.Content.ReadAsStringAsync();

        var billPay = JsonConvert.DeserializeObject<List<BillPay>>(result);

        return View(billPay);
    }

    /**
     * Block Block updates state of the object to Blocked or Pending
     */
    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> BlockUnblock(int billPayID)
    {
        var response = await Client.GetAsync($"billpay/{billPayID}");
        if (!response.IsSuccessStatusCode)
            return RedirectToAction(nameof(BillPays));

        var result = await response.Content.ReadAsStringAsync();
        var billPay = JsonConvert.DeserializeObject<BillPay>(result);

        if (billPay.State == ((char)BillPayState.Pending) ||
            billPay.State == ((char)BillPayState.InsufficientFunds))
        {
            billPay.State = ((char)BillPayState.Blocked);
        }
        else
            billPay.State = ((char)BillPayState.Pending);

        var content = new StringContent(JsonConvert.SerializeObject(billPay), Encoding.UTF8, "application/json");
        var postResponse = Client.PutAsync("billpay", content).Result;

        return RedirectToAction(nameof(BillPays));
    }
}

