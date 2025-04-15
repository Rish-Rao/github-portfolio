using Microsoft.EntityFrameworkCore.Metadata.Internal;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MCBA.Models;

// Requires more validation
public class Transaction
{
    public int TransactionID { get; set; }

    [Required]
    [Column(TypeName = "char")]
    [Display(Name = "Type")]
    public char TransactionType { get; set; }

    [ForeignKey("Account")]
    public int AccountNumber { get; set; }
    public Account Account { get; set; }

    [ForeignKey("DestinationAccount")]
    [Display(Name = "Destination Account Number")]
    public int? DestinationAccountNumber { get; set; }
    public Account DestinationAccount { get; set; }

    [Required]
    [Column(TypeName = "money")]
    [DataType(DataType.Currency)]
    public decimal Amount { get; set; }

    [StringLength(30)]
    public string? Comment { get; set; }

    [Required]
    [DataType(DataType.DateTime)]
    [Display(Name = "Transaction Time")]
    [DisplayFormat(DataFormatString = "dd/MM/yyyy hh:mm:ss t", ApplyFormatInEditMode = true)]
    public DateTime TransactionTimeUtc { get; set; }
}

