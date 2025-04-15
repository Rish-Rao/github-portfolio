using MCBAAdminApi.Models.DataManager;
using Microsoft.AspNetCore.Mvc;
using MCBAAdminApi.Models;

namespace MCBAAdmin.Controllers.AdminApi;

/**
 * Use When runnning on MCBAAdmin - http
 * http://localhost:5201/"Corrosponding End Point"
 */
[ApiController]
[Route("billpay")]
public class BillPayApiController : ControllerBase
{
    private readonly BillPayManager _repo;

    // BillPay Controller
    public BillPayApiController(BillPayManager repo) => _repo = repo;

    // GET: billPay
    // https://localhost:5201/billPay
    [HttpGet]
    public IEnumerable<BillPay> Get() => _repo.GetAll();

    // GET billPay/{id}
    // https://localhost:5201/billPay/{Id}
    [HttpGet("{id}")]
    public BillPay Get(int id) => _repo.Get(id);

    // PUT billPay/billPay
    // https://localhost:5201/billPay
    [HttpPut]
    public void Put([FromBody] BillPay billPay) => _repo.Update(billPay.BillPayID, billPay);
}

