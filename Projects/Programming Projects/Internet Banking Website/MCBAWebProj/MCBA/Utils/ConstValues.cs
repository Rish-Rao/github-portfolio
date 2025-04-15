using System.Collections.Generic;

namespace MCBA.Utils;

public static class ConstValues
{
    public enum AccountType
    {
        Saving = 'S',
        Checking = 'C'
    }

    public enum TransactionType
    {
        Deposit = 'D',
        Withdraw = 'W',
        Transfer = 'T',
        ServiceCharge = 'S',
        Billpay = 'B'
    }

    public enum BillPayState
    {
        InsufficientFunds = 'I',
        Success = 'S',
        Pending = 'P',
        Blocked = 'B'
    }

    public enum BillPayPeriod
    {
        OneOff = 'O',
        Monthly = 'M'
    }

    public enum Accountstate
    {
        Locked = 'L',
        Unlocked = 'U'
    }

    // Can use either data
    public static readonly string[] States =
    {
        "VIC", "NSW", "NT", "QLD", "SA", "TAS", "WA"
    };
    public static readonly Dictionary<string, string> State = new Dictionary<string, string>{
        { "Victoria", "VIC" }, { "NewSouthWales", "NSW"},
        { "NorthernTerritory", "NT" }, { "Queensland", "QLD"},
        { "SouthAustralia", "SA" }, { "Tasmania", "TAS"},
        { "WesternAustralia", "WA" }
    };

    public const string DefaultNullvalue = "n/a";

    public const decimal CheckingAccountMinBalance = 300;

    public const int MaxFreeTransactionsAllowed = 2;

    public const decimal WithdrawFee = 0.05M;

    public const decimal TransferFee = 0.10M;

    public const int MaxTransactionPerStatmentPage = 4;
  
}

