using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MCBA.Models;

// Requires Validation
public class Payee
{
    /** 
     * Follow's 04xx xxx xxx
     * ^\( start with (
     * (0) then 0
     * \d{1} 1int b\w [0-9]
     * \) then )
     * \s whitespace 
     * \d{4} 4int b\w [0-9]
     * \s whitespace 
     * \d{4} 4int b\w [0-9]
     * Test String 
     * https://regex101.com/r/94ZgN4/1
     */
    private const string _phoneRegex = @"^\((0)\d{1}\)\s\d{4}\s\d{4}$";

    public int PayeeID { get; set; }

    [Required]
    [StringLength(50)]
    public string Name { get; set; }

    [Required]
    [StringLength(50)]
    public string Address { get; set; }

    [Required]
    [StringLength(40)]
    public string City { get; set; }

    [Required]
    [StringLength(3, MinimumLength = 2)]
    public string State { get; set; }

    [Required]
    [StringLength(4), MinLength(4)]
    public string Postcode { get; set; } // Need to make int only

    [Required]
    [StringLength(14)]
    [RegularExpression(_phoneRegex, ErrorMessage = "Must be in format: '(0X) XXXX XXXX', Where X is between 0-9")]
    public string Phone { get; set; }

    public List<BillPay> BillPays { get; set; }

    [NotMapped]
    public BillPay BillPay;
}