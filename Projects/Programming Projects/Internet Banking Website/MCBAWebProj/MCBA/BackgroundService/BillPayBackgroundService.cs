using MCBA.Data;
using MCBA.Models;
using Microsoft.EntityFrameworkCore;
using static MCBA.Utils.ConstValues;
using static MCBA.Utils.TransactionHelperMethods;

namespace MCBA.BackgroundServices;

// Background service runs every 1 minute
public class BillPayBackgroundService : BackgroundService
{
    private readonly IServiceProvider _services;
    private readonly ILogger<BillPayBackgroundService> _logger;

    public BillPayBackgroundService(IServiceProvider services, ILogger<BillPayBackgroundService> logger)
    {
        _services = services;
        _logger = logger;
    }

    protected override async Task ExecuteAsync(CancellationToken cancellationToken)
    {
        _logger.LogInformation("\n\nBillPay Background Service is running.");

        while (!cancellationToken.IsCancellationRequested)
        {
            await DoWork(cancellationToken);

            _logger.LogInformation("BillPay Background Service is waiting a minute.");

            // One minute delay before next service runs
            await Task.Delay(TimeSpan.FromMinutes(1), cancellationToken);
        }
    }

    private async Task DoWork(CancellationToken cancellationToken)
    {
        _logger.LogInformation("\n\nBillPay Background Service is working.");

        using var scope = _services.CreateScope();
        var context = scope.ServiceProvider.GetRequiredService<McbaContext>();

        /** Gets List<BillPays> 
         * Where State is pending
         * and
         * Where ScheduleTimeUtc Is Current time or in past
         */
        var billPays = await context.BillPays.Include(x => x.Account).
            Where(x => x.State == ((char)BillPayState.Pending)).
            Where(x => x.ScheduleTimeUtc <= DateTime.UtcNow)
            .ToListAsync(cancellationToken);

        foreach (var billPay in billPays)
        {
            var account = await context.Accounts.Include(x => x.Transactions).
                FirstOrDefaultAsync(x => x.AccountNumber == billPay.AccountNumber);

            var availableBalance = account.ComputeAvailableBalance(TransactionType.Billpay);
            // If Amount not available
            if (availableBalance < billPay.Amount)
                billPay.State = ((char)BillPayState.InsufficientFunds);
            else
            {
                // Account balance updated
                account.Balance -= billPay.Amount;

                // Transaction updated
                account.Transactions.Add(
                new Transaction
                {
                    TransactionType = ((char)TransactionType.Billpay),
                    AccountNumber = account.AccountNumber,
                    DestinationAccountNumber = null,
                    Amount = billPay.Amount,
                    Comment = null,
                    TransactionTimeUtc = DateTime.UtcNow
                });

                // BillPay Updated
                if (billPay.Period == ((char)BillPayPeriod.Monthly))
                {
                    billPay.ScheduleTimeUtc = billPay.ScheduleTimeUtc.AddMonths(1);
                    billPay.State = ((char)BillPayState.Pending);
                }
                else
                    billPay.State = ((char)BillPayState.Success);
            }

            await context.SaveChangesAsync(cancellationToken);
        }

        _logger.LogInformation("BillPay Background Service work complete.");
    }

}
