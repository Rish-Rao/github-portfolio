using Microsoft.Extensions.Caching.SqlServer;
using Microsoft.EntityFrameworkCore;
using MCBA.BackgroundServices;
using MCBA.Filters;
using MCBA.Data;

var builder = WebApplication.CreateBuilder(args);

// Db Connectrion String
var _connectionStr = builder.Configuration.GetConnectionString(nameof(McbaContext));


// Add services to the container.
builder.Services.AddControllersWithViews();

// Applyies AuthorizeCustomer Filter Globally 
builder.Services.AddControllersWithViews(options => options.Filters.Add(new AuthorizeCustomerAttribute()));

// Add people background service to automatically run in the background along-side the web-server.
builder.Services.AddHostedService<BillPayBackgroundService>();

// DbContext service 
builder.Services.AddDbContext<McbaContext>(options =>
    options.UseSqlServer(_connectionStr));

/** Sessions Service
 * Package required: Microsoft.Extensions.Caching.SqlServer
 * Schema - create schema dotnet;
 * Session Table - dotnet sql-cache create "Server=rmit.australiaeast.cloudapp.azure.com;Encrypt=False;Uid=s3842316_a2;Pwd=abc123;MultipleActiveResultSets=True" dotnet SessionCache
 */
builder.Services.AddDistributedSqlServerCache(options =>
{
    options.ConnectionString = _connectionStr;
    options.SchemaName = "dotnet";
    options.TableName = "SessionCache";
});
builder.Services.AddSession(options =>
{
    // Make the session cookie essential.
    options.Cookie.IsEssential = true;
    options.IdleTimeout = TimeSpan.FromMinutes(20);
});

var app = builder.Build();

// Seed data
using (var scope = app.Services.CreateScope())
{
    var services = scope.ServiceProvider;
    try
    {
        SeedData.Init(services);
    }
    catch (Exception e)
    {
        var logger = services.GetRequiredService<ILogger<Program>>();
        logger.LogError(e, "An error occurred while seeding the DB");
    }
} // ---SEED--END--- 

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseRouting();
app.UseAuthorization();

// Sessions Support
app.UseSession();

// Uses Default routing route
app.MapDefaultControllerRoute();

app.Run();

