using MCBA.Data;
using MCBA.Utils;
using MCBA.Models;
using ImageMagick;
using SimpleHashing.Net;
using static MCBA.Utils.HelperMethods;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MCBA.Controllers;

// Customer Controller
public class CustomerController : Controller
{
    private readonly McbaContext _context;
    private int _customerID => HttpContext.Session.GetInt32(nameof(Customer.CustomerID)).Value;
    private readonly ISimpleHash _simpleHash = new SimpleHash();

    // Constructor 
    public CustomerController(McbaContext context) => _context = context;

    [HttpGet]
    public async Task<IActionResult> Index()
    {
        var customer = await _context.Customers.Include(x => x.Accounts).
            FirstOrDefaultAsync(x => x.CustomerID == _customerID);

        // Sets up customer image in session
        if (customer.Image != null)
            HttpContext.Session.SetString(nameof(Customer.Image), Convert.ToBase64String(customer.Image));

        return View(customer);
    }

    // Edit Image View
    [HttpGet]
    public async Task<IActionResult> EditImage() => View(await _context.Customers.
        FirstOrDefaultAsync(x => x.CustomerID == _customerID));

    // MyProfile View
    [HttpGet]
    public async Task<IActionResult> MyProfile() => View(await _context.Customers.
        FirstOrDefaultAsync(x => x.CustomerID == _customerID));

    // Edit MyProfile View
    [HttpGet]
    public async Task<IActionResult> EditProfile() => View(await _context.Customers.
        FirstOrDefaultAsync(x => x.CustomerID == _customerID));

    // Edit Password View
    [HttpGet]
    public async Task<IActionResult> EditPassword() => View(await _context.Logins.
        FirstOrDefaultAsync(x => x.CustomerID == _customerID));

    // Updates Profile in db
    [HttpPost]
    public async Task<IActionResult> EditProfile(string name, string? tfn,
        string? address, string? city, string? state, string? postcode,
        string? mobile)
    {
        var customer = await _context.Customers.
            FirstOrDefaultAsync(x => x.CustomerID == _customerID);


        if (state != null && state != string.Empty)
            if (!StateExists(state.ToUpper()))
                ModelState.AddModelError(nameof(state), "State Doesnt Exists");
        if (!ModelState.IsValid)
        {
            return View(customer);
        }

        customer.Name = name;

        if (tfn == null || tfn == string.Empty)
            customer.TFN = null;
        else
            customer.TFN = tfn;

        if (address == null || address == string.Empty)
            customer.Address = null;
        else
            customer.Address = address;

        if (city == null || city == string.Empty)
            customer.City = null;
        else
            customer.City = city;

        if (state == null || state == string.Empty)
            customer.State = null;
        else
            customer.State = state;

        if (postcode == null || postcode == string.Empty)
            customer.Postcode = null;
        else
            customer.Postcode = postcode;

        if (mobile == null || mobile == string.Empty)
            customer.Mobile = null;
        else
            customer.Mobile = mobile;

        await _context.SaveChangesAsync();

        return RedirectToAction(nameof(MyProfile));
    }

    // Updates password in db - logout when pass updated
    [HttpPost]
    public async Task<IActionResult> EditPassword(string password, string confirmPassword)
    {
        var login = await _context.Logins.
            FirstOrDefaultAsync(x => x.CustomerID == _customerID);

        // Validation
        if(string.IsNullOrEmpty(password))
            ModelState.AddModelError(nameof(password), "Password can't be empty");
        if (string.IsNullOrEmpty(confirmPassword))
            ModelState.AddModelError(nameof(confirmPassword), "Confirm Password can't be empty");
        if (password != confirmPassword)
            ModelState.AddModelError(nameof(confirmPassword), "Password and confirm password don't match");
        if (!ModelState.IsValid)
            return View(login);

        // Hash and db update
        var passwordHash = _simpleHash.Compute(password);
        login.PasswordHash = passwordHash;
        await _context.SaveChangesAsync();

        return RedirectToAction("Logout", "Login");
    }

    // Updates Image in db
    [HttpPost]
    public async Task<IActionResult> EditImage(IFormFile imageFile)
    {
        var customer = await _context.Customers.
            FirstOrDefaultAsync(x => x.CustomerID == _customerID);

        // Error Handeling
        if (imageFile == null)
            ModelState.AddModelError(nameof(imageFile), "File is either empty");
        if (!ModelState.IsValid)
        {
            return View(customer);
        }

        // New Magick image
        byte[] data = null;
        using (var image = new MagickImage(ConvertImageToByteArray(imageFile)))
        {
            // Comverting to jpg
            image.Format = MagickFormat.Jpg;

            // Resizing to 400px by 400px while maintaining aspect ratio
            var size = new MagickGeometry(400, 400);
            image.Resize(size);

            // Create byte array that contains a jpeg file
            data = image.ToByteArray();
        }

        customer.Image = data;
        await _context.SaveChangesAsync();

        return RedirectToAction(nameof(Index));
    }


    // Removes image from db
    [HttpPost]
    public async Task<IActionResult> RemoveCurrentImage()
    {
        var customer = await _context.Customers.
            FirstOrDefaultAsync(x => x.CustomerID == _customerID);

        // Sets image in session 
        HttpContext.Session.SetString(nameof(Customer.Image), string.Empty);

        // Update in db
        customer.Image = null;
        await _context.SaveChangesAsync();

        return RedirectToAction(nameof(EditImage));
    }

    /**
     * Method adapted from
     * https://stackoverflow.com/questions/57381957/how-use-magick-net-losslesscompress-with-stream-and-iformfile
     */
    private byte[] ConvertImageToByteArray(IFormFile image)
    {
        byte[] result = null;

        // filestream
        using (var fileStream = image.OpenReadStream())

        // memory stream
        using (var memoryStream = new MemoryStream())
        {
            fileStream.CopyTo(memoryStream);
            memoryStream.Position = 0; // The position needs to be reset.

            var before = memoryStream.Length;

            ImageOptimizer optimizer = new ImageOptimizer();
            optimizer.LosslessCompress(memoryStream);

            var after = memoryStream.Length;

            // convert to byte[]
            result = memoryStream.ToArray();
        }

        return result;
    }

}
