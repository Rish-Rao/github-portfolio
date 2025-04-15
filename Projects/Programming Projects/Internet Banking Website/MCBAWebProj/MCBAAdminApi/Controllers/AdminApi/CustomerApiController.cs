using MCBAAdminApi.Models.DataManager;
using Microsoft.AspNetCore.Mvc;
using MCBAAdminApi.Models;

namespace MCBAAdmin.Controllers.AdminApi;

/**
 * Use When runnning on MCBAAdmin - http
 * http://localhost:5201/"Corrosponding End Point"
 */
[ApiController]
[Route("customer")]
public class CustomerApiController : ControllerBase
{
    private readonly CustomerManager _repo;

    // Customer
    public CustomerApiController(CustomerManager repo) => _repo = repo;

    // GET: customer
    // https://localhost:5201/customer
    [HttpGet]
    public IEnumerable<Customer> Get() => _repo.GetAll();

    // GET customer/{id}
    // https://localhost:5201/customer/{Id}
    [HttpGet("{id}")]
    public Customer Get(int id) => _repo.Get(id);

    // PUT customer/customer
    // https://localhost:5201/customer
    [HttpPut]
    public void Put([FromBody] Customer customer) => _repo.Update(customer.CustomerID, customer);

}