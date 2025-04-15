using MCBAAdminApi.Models;
using Microsoft.EntityFrameworkCore;

namespace MCBAAdminApi.Data;

public class McbaAdminApiContext : DbContext
{
    public McbaAdminApiContext(DbContextOptions<McbaAdminApiContext> options) : base(options)
    { }

    public DbSet<Customer> Customers { get; set; }
    public DbSet<BillPay> BillPays { get; set; }
}