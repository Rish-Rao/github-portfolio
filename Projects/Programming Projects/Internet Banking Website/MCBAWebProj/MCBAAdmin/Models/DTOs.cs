using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MCBAAdmin.Models;

public class Customer
{
    // Regex
    private const string _tfnRegex = @"^\d{3}\s\d{3}\s\d{3}$";
    private const string _mobileRegex = @"^(04)\d{2}\s\d{3}\s\d{3}$";

    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [MaxLength(4), MinLength(4)]
    public int CustomerID {get; set;}

    [Required, StringLength(50)]
    public string Name { get; set; }

    [StringLength(11), RegularExpression(_tfnRegex, ErrorMessage = "Must be in format: 'XXX XXX XXX', Where X is between 0-9")]
    public string? TFN { get; set; }

    [StringLength(50)]
    public string? Address { get; set; }

    [StringLength(40)]
    public string? City { get; set; }

    [StringLength(3, MinimumLength = 2, ErrorMessage = "Length has to be between 2-3")]
    public string? State { get; set; }

    [StringLength(4, MinimumLength = 4, ErrorMessage = "Length has to be 4")]
    public string? Postcode { get; set; } // Need to make int only

    [StringLength(12), RegularExpression(_mobileRegex, ErrorMessage = "Must be in format: '04XX XXX XXX', Where X is between 0-9")]
    public string? Mobile { get; set; }

    /**
     * Checks State
     * 'U' Unlocked
     * 'L' Locked
     */
    [Required]
    [Column(TypeName = "char")]
    [Display(Name = "Account State")]
    public char AccountState { get; set; }
}

public class BillPay
{
    public int BillPayID { get; set; }

    [Required]
    public int AccountNumber { get; set; }

    [Required]
    public int PayeeID { get; set; }

    [Required, Column(TypeName = "money"), DataType(DataType.Currency)]
    public decimal Amount { get; set; }

    [Required, DataType(DataType.DateTime), Display(Name = "Schedule Time")]
    public DateTime ScheduleTimeUtc { get; set; }

    [Required, Column(TypeName = "char")]
    public char Period { get; set; }

    [Required, Column(TypeName = "char"), Display(Name = "Status")]
    public char State { get; set; }
}

public class Login
{
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column(TypeName = "char")]
    [StringLength(8), MinLength(8, ErrorMessage = "Must be 8 digit long")]
    [Display(Name = "Login ID")]
    public string LoginID { get; set; }

    // FK to Customer
    public int CustomerID { get; set; }

    [Required]
    [Column(TypeName = "char")]
    [StringLength(94)]
    [Display(Name = "Password")]
    public string PasswordHash { get; set; }

    /**
     * Checks State
     * 'U' Unlocked
     * 'L' Locked
     */
    [Required]
    [Column(TypeName = "char")]
    [Display(Name = "State")]
    public char State { get; set; }
}

public class Admin
{
    public required string Username { get; set; }
    public required string Password { get; set; }
}