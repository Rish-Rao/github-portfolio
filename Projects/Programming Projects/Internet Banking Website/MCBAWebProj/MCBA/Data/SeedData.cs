using Newtonsoft.Json;
using MCBA.Models;
using static MCBA.Utils.ConstValues;
using static System.Net.Mime.MediaTypeNames;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace MCBA.Data;

public static class SeedData
{
    private static readonly string _url =
        "https://coreteaching01.csit.rmit.edu.au/~e103884/wdt/services/customers/";

    // Initial method called 
    public static void Init(IServiceProvider serviceProvider)
    {
        var context = serviceProvider.GetRequiredService<McbaContext>();

        // Checks if any row exists in customer 
        if (context.Customers.Any())
            return;
        else
            Populate(context);
    }

    // Populated database
    private static void Populate(McbaContext context)
    {
        var customers = GetDeserializedCustomers();

        // Checks if Customers is empty 
        if (!customers.Any())
            return;

        // Inserting data in DataBase
        foreach (var customer in customers)
        {
            // Add Customer
            customer.AccountState = ((char)Accountstate.Unlocked);
            customer.Image = null;
            context.Customers.Add(customer);

            // Add Accounts
            foreach (var account in customer.Accounts)
            {
                account.CustomerID = customer.CustomerID;
                account.Balance = account.ComputeBalance();
                context.Accounts.Add(account);

                // Add Transactions
                foreach (var transaction in account.Transactions)
                {
                    transaction.TransactionType = ((char)TransactionType.Deposit);
                    transaction.AccountNumber = account.AccountNumber;
                    context.Transactions.Add(transaction);
                }
            }
            //Login
            customer.Login.CustomerID = customer.CustomerID;
            context.Logins.Add(customer.Login);
        }

        // Haerd coded payee
        context.Payees.AddRange(
            new Payee
            {
                Name = "Telstra",
                Address = "2 Random Street",
                City = "Melbourne",
                State = State["Victoria"],
                Postcode = "1111",
                Phone = "(04) 1234 5678",
            },
            new Payee
            {
                Name = "Optus",
                Address = "5 Some Way",
                City = "Sydney",
                State = State["NewSouthWales"],
                Postcode = "1234",
                Phone = "(08) 8765 4321",
            },
            new Payee
            {
                Name = "TPG",
                Address = "10 Where Grove",
                City = "Brisbane",
                State = State["Queensland"],
                Postcode = "1234",
                Phone = "(09) 2222 3333",
            });

        context.SaveChanges();
    }

    private static List<Customer>? GetDeserializedCustomers()
    {
        // Gets Json Async - Then Deserializes the data 
        var json = GetJsonAsync().Result;

        return JsonConvert.DeserializeObject<List<Customer>>(json, new JsonSerializerSettings
        {
            DateFormatString = "dd/MM/yyyy hh:mm:ss tt"
        });
    }

    private static async Task<string> GetJsonAsync()
    {
        using var client = new HttpClient();
        var response = await client.GetStringAsync(_url);
        return response;
    }

    // Calculates Balance through transactions
    private static decimal ComputeBalance(this Account account)
    {
        decimal balance = 0;
        foreach (var transaction in account.Transactions)
        {
            balance += transaction.Amount;
        }
        return balance;
    }
}
