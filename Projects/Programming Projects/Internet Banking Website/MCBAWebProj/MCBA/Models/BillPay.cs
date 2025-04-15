using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MCBA.Models;

public class BillPay
{
	public int BillPayID { get; set; }

    [ForeignKey("Account")]
    public int AccountNumber { get; set; }
    public Account Account { get; set; }

    public int PayeeID { get; set; }
    public Payee Payee { get; set; }

    [Required]
    [Column(TypeName = "money")]
    [DataType(DataType.Currency)]
    public decimal Amount { get; set; }

    [Required]
    [DataType(DataType.DateTime)]
    [Display(Name = "Schedule Time")]
    public DateTime ScheduleTimeUtc { get; set; }

    [Required]
    [Column(TypeName = "char")]
    public char Period { get; set; }

    /**
     * Checks State
     * 'I' Insufficient Funds
     * 'S' Success
     * 'P' Pending
     * 'B' Blocked
     */
    [Required]
    [Column(TypeName = "char")]
    [Display(Name = "Status")]
    public char State { get; set; }
}

