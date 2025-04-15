using static MCBA.Utils.ConstValues;

namespace MCBA.Utils;

public static class HelperMethods
{
    public static string AccountTypeToString(this char c) =>
         c == ((char)AccountType.Saving) ? "Savings" : "Checking";

    public static bool HasMoreThanNDecimalPlaces(this decimal value, int n) => decimal.Round(value, n) != value;

    public static bool HasMoreThanTwoDecimalPlaces(this decimal value) => value.HasMoreThanNDecimalPlaces(2);

    public static string BillPayPeriodToString(this char c) =>
    c == ((char)BillPayPeriod.Monthly) ? "Monthly" : "One Off";

    public static string DestAccountValue(int? desAccount) =>
        desAccount == null ? DefaultNullvalue : desAccount.ToString();

    public static string CommentValue(string? comment) =>
    comment == null ? DefaultNullvalue : comment;

    public static string BillPayStateToString(this char c)
    {
        if (c == ((char)BillPayState.InsufficientFunds))
            return "Insufficient Funds";
        else if (c == ((char)BillPayState.Pending))
            return "Pending";
        else if (c == ((char)BillPayState.Success))
            return "Success";
        else
            return "Blocked";
    }

    public static string TransactionTypeToString(this char c)
    {
        if (c == ((char)TransactionType.Deposit))
            return "Deposit";
        else if (c == ((char)TransactionType.Withdraw))
            return "Withdraw";
        else if (c == ((char)TransactionType.Transfer))
            return "Transfer";
        else if (c == ((char)TransactionType.ServiceCharge))
            return "ServiceCharge";
        else
            return "BillPay";
    }

    public static bool StateExists(string state)
    {
        foreach (var stateArray in States)
        {
            if (state.Equals(stateArray))
                return true;
        }
        return false;
    }


}

