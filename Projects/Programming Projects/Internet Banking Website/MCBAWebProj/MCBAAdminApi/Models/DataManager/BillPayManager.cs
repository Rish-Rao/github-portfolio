using MCBAAdminApi.Data;
using MCBAAdminApi.Models;
using MCBAAdminApi.Models.Repository;
using Microsoft.EntityFrameworkCore;

namespace MCBAAdminApi.Models.DataManager;

public class BillPayManager : IDataRepository<BillPay, int>
{
    public enum BillPayState
    {
        InsufficientFunds = 'I',
        Success = 'S',
        Pending = 'P',
        Blocked = 'B'
    }

    private readonly McbaAdminApiContext _context;

    // Constructor
    public BillPayManager(McbaAdminApiContext context) => _context = context;

    // Get All
    //public IEnumerable<BillPay> GetAll() => _context.BillPays.ToList();

    public IEnumerable<BillPay> GetAll() => _context.BillPays.
        OrderByDescending(x => x.ScheduleTimeUtc).
        ThenBy(x => x.State == ((char)BillPayState.Pending)).
        ThenBy(x => x.State == ((char)BillPayState.Blocked)).
        ThenBy(x => x.State == ((char)BillPayState.InsufficientFunds)).
        ToList();

    // Get by Id
    public BillPay Get(int id) => _context.BillPays.Find(id);

    // Add
    public int Add(BillPay billPay)
    {
        _context.BillPays.Add(billPay);
        _context.SaveChanges();

        return billPay.BillPayID;
    }

    // Delete
    public int Delete(int id)
    {
        _context.BillPays.Remove(_context.BillPays.Find(id));
        _context.SaveChanges();

        return id;
    }

    // Update
    public int Update(int id, BillPay billPay)
    {
        _context.Update(billPay);
        _context.SaveChanges();

        return id;
    }
}

