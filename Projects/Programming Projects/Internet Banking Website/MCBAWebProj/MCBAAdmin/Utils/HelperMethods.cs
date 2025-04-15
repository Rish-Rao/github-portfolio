namespace MCBAAdmin.Utils;

public static class HelperMethods
{
    public enum Accountstate
    {
        Locked = 'L',
        Unlocked = 'U'
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

    public static string AccountstateToString(this char c) =>
     c == ((char)Accountstate.Locked) ? "Locked" : "Unlocked";

    public static string BillPayPeriodToString(this char c) =>
    c == ((char)BillPayPeriod.Monthly) ? "Monthly" : "One Off";

    public static readonly string[] States =
    {
        "VIC", "NSW", "NT", "QLD", "SA", "TAS", "WA"
    };

    public static bool StateExists(string state)
    {
        foreach(var stateArray in States)
        {
            if (state.Equals(stateArray))
                return true;
        }
        return false;
    }

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
}

