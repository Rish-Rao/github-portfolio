using MCBA.Models;
using Microsoft.EntityFrameworkCore;

namespace MCBA.Data;

public class McbaContext : DbContext
{
    public McbaContext(DbContextOptions<McbaContext> options) : base(options)
    { }

    public DbSet<Customer> Customers { get; set; }
    public DbSet<Login> Logins { get; set; }
    public DbSet<Account> Accounts { get; set; }
    public DbSet<Transaction> Transactions { get; set; }
    public DbSet<BillPay> BillPays { get; set; }
    public DbSet<Payee> Payees { get; set; }

    // Fluent-API.
    protected override void OnModelCreating(ModelBuilder builder)
    {
        base.OnModelCreating(builder);

        // Configure ambiguous Account.Transactions navigation property relationship.
        builder.Entity<Transaction>().
            HasOne(x => x.Account).WithMany(x => x.Transactions).HasForeignKey(x => x.AccountNumber);

        // Configure ambiguous Account.BillPays navigation property relationship.
        builder.Entity<BillPay>().
            HasOne(x => x.Account).WithMany(x => x.BillPays).HasForeignKey(x => x.AccountNumber);

        // Configure ambiguous Payee.BillPays navigation property relationship.
        builder.Entity<BillPay>().
            HasOne(x => x.Payee).WithMany(x => x.BillPays).HasForeignKey(x => x.PayeeID);
    }
}