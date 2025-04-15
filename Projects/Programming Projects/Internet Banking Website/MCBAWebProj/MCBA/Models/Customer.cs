using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
namespace MCBA.Models;

public class Customer
{
    /**
     * x = int range (0-9)
     * Follow's xxx xxx xxx
     * \d{3} 3int b\w [0-9]
     * \s whitespace 
     * Test String 
     * https://regex101.com/r/gz32iA/1
     */
    private const string _tfnRegex = @"^\d{3}\s\d{3}\s\d{3}$";

    /** 
     * Follow's 04xx xxx xxx
     * ^(04) start with 04
     * \d{2} 2int b\w [0-9]
     * \s whitespace 
     * \d{3} 3int b\w [0-9]
     * Test String 
     * https://regex101.com/r/pQ6RQe/1
     */
    private const string _mobileRegex = @"^(04)\d{2}\s\d{3}\s\d{3}$";

    [DatabaseGenerated(DatabaseGeneratedOption.None)]
    [MaxLength(4), MinLength(4)]
    public int CustomerID {get; set;}

    [Required]
    [StringLength(50)]
    public string Name { get; set; }

    [StringLength(11)]
    [RegularExpression(_tfnRegex, ErrorMessage = "Must be in format: 'XXX XXX XXX', Where X is between 0-9")]
    public string? TFN { get; set; }

    [StringLength(50)]
    public string? Address { get; set; }

    [StringLength(40)]
    public string? City { get; set; }

    [StringLength(3, MinimumLength = 2)]
    public string? State { get; set; }

    [StringLength(4), MinLength(4)]
    public string? Postcode { get; set; } // Need to make int only

    [StringLength(12)]
    [RegularExpression(_mobileRegex, ErrorMessage = "Must be in format: '04XX XXX XXX', Where X is between 0-9")]
    public string? Mobile { get; set; }

    public List<Account> Accounts { get; set; }

    [NotMapped]
    public Login Login { get; set; }

    /**
     * Checks State
     * 'U' Unlocked
     * 'L' Locked
     */
    [Required]
    [Column(TypeName = "char")]
    [Display(Name = "Account State")]
    public char AccountState { get; set; }

    public byte[]? Image { get; set; }
}