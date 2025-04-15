using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MCBA.Models;

public class Account
{
    [Key, DatabaseGenerated(DatabaseGeneratedOption.None)]
    [MaxLength(4), MinLength(4)]
    [Display(Name = "Account Number")]
    public int AccountNumber { get; set; }

    [Required]
    [Column(TypeName = "char")]
    [Display(Name = "Type")]
    public char AccountType { get; set; }

    // FK to Customer
    public int CustomerID { get; set; }
    public Customer Customer { get; set; }

    [Required]
    [Column(TypeName = "money")]
    [DataType(DataType.Currency)]
    public decimal Balance { get; set; }

    public List<Transaction> Transactions { get; set; }

    public List<BillPay> BillPays { get; set; }

    [NotMapped]
    public Transaction Transaction { get; set; }

    [NotMapped]
    public BillPay BillPay;
}