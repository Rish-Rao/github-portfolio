using static MCBA.Utils.ConstValues;
using static MCBA.Utils.HelperMethods;
using MCBA.Models;

namespace MCBA.Utils;

public static class TransactionHelperMethods
{

    // Available Balance
    public static decimal ComputeAvailableBalance(this Account account,
        TransactionType transactionType)
    {
        if (account.AccountType == ((char)ConstValues.AccountType.Saving))
        {
            decimal fee = 0;
            if (account.ServiceFeeRequired())
            {
                if (transactionType == TransactionType.Withdraw)
                    fee = WithdrawFee;
                else if (transactionType == TransactionType.Transfer)
                    fee = TransferFee;
            }

            return (account.Balance - fee) <= 0 ? 0 : (account.Balance - fee);
        } 
        else
        {
            var minimumBalance = ConstValues.CheckingAccountMinBalance;
            if (account.ServiceFeeRequired())
            {
                if (transactionType == TransactionType.Withdraw)
                    minimumBalance += WithdrawFee;
                else if (transactionType == TransactionType.Transfer)
                    minimumBalance += TransferFee;
            }

            return (account.Balance - minimumBalance) <=
                0 ? 0 : (account.Balance - minimumBalance);
        }
    }

    // Checks if service fee is required or not
    public static bool ServiceFeeRequired(this Account account) =>
        account.GetNoOfTransactions() >= MaxFreeTransactionsAllowed ? true : false;

    // Returnes No of Widthraw or Transfer transactions
    private static int GetNoOfTransactions(this Account account)
    {
        int noOfTransactions = 0;
        foreach (var transaction in account.Transactions)
        {
            if (transaction.TransactionType == ((char)TransactionType.Withdraw)
                || transaction.TransactionType == ((char)TransactionType.Transfer))
                noOfTransactions++;
        }
        return noOfTransactions;
    }
}

