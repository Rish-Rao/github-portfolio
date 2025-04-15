namespace MCBAAdminApi.Models;

public class Customer
{
    public int CustomerID { get; set; }
    public string Name { get; set; }
    public string? TFN { get; set; }
    public string? Address { get; set; }
    public string? City { get; set; }
    public string? State { get; set; }
    public string? Postcode { get; set; }
    public string? Mobile { get; set; }
    public char AccountState { get; set; }
}

public class BillPay
{
    public int BillPayID { get; set; }
    public int AccountNumber { get; set; }
    public int PayeeID { get; set; }
    public decimal Amount { get; set; }
    public DateTime ScheduleTimeUtc { get; set; }
    public char Period { get; set; }
    public char State { get; set; }
}

public class Admin
{
    public required string Username { get; set; }
    public required string Password { get; set; }
}