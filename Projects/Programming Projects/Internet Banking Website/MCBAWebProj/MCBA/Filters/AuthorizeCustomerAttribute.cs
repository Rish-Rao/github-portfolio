using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using MCBA.Models;
using Microsoft.AspNetCore.Authorization;

namespace MCBA.Filters;

/**
 * Adapted from RMIT/WDT/Modules/Week6/Day6.zip/McbaExampleWithLogin
 */
public class AuthorizeCustomerAttribute : Attribute, IAuthorizationFilter
{
    public void OnAuthorization(AuthorizationFilterContext context)
    {
        if (context.ActionDescriptor.EndpointMetadata.Any(x => x is AllowAnonymousAttribute))
            return;

        int? customerID = context.HttpContext.Session.GetInt32(nameof(Customer.CustomerID));
        if (!customerID.HasValue)
            context.Result = new RedirectToActionResult("Index", "Home", null);
    }
}
