using MCBAAdmin.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.AspNetCore.Authorization;

namespace MCBAAdmin.Filters;

/**
 * Adapted from RMIT/WDT/Modules/Week6/Day6.zip/McbaExampleWithLogin
 */
public class AuthorizeAdminAttribute : Attribute, IAuthorizationFilter
{
    public void OnAuthorization(AuthorizationFilterContext context)
    {
        if (context.ActionDescriptor.EndpointMetadata.Any(x => x is AllowAnonymousAttribute))
            return;

        string? adminUsername = context.HttpContext.Session.GetString("username");
        if (adminUsername == null || adminUsername == string.Empty)
            context.Result = new RedirectToActionResult("Index", "Home", null);
    }
}
