using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MCBA.Models;

public class Login
{
    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [Column(TypeName = "char")]
    [StringLength(8), MinLength(8, ErrorMessage = "Must be 8 digit long")]
    [Display(Name = "Login ID")]
    public string LoginID { get; set; }

    // FK to Customer
    public int CustomerID { get; set; }
    public Customer Customer { get; set; }

    [Required]
    [Column(TypeName = "char")]
    [StringLength(94)]
    [Display(Name = "Password")]
    public string PasswordHash { get; set; }

}