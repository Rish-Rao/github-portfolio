using MCBAAdminApi.Data;
using MCBAAdminApi.Models;
using MCBAAdminApi.Models.Repository;
using Microsoft.EntityFrameworkCore;

namespace MCBAAdminApi.Models.DataManager;

public class CustomerManager : IDataRepository<Customer, int>
{
    private readonly McbaAdminApiContext _context;

    // Constructor
    public CustomerManager(McbaAdminApiContext context) => _context = context;

    // Get All
    public IEnumerable<Customer> GetAll() => _context.Customers.ToList();

    // Get by Id
    public Customer Get(int id) => _context.Customers.Find(id);

    // Add
    public int Add(Customer customer)
    {
        _context.Customers.Add(customer);
        _context.SaveChanges();

        return customer.CustomerID;
    }

    // Delete
    public int Delete(int id)
    {
        _context.Customers.Remove(_context.Customers.Find(id));
        _context.SaveChanges();

        return id;
    }

    // Update
    public int Update(int id, Customer customer)
    {
        _context.Update(customer);
        _context.SaveChanges();

        return id;
    }
}

