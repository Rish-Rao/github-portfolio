using MCBAAdminApi.Data;
using MCBAAdminApi.Models.DataManager;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();

// Db Connectrion String
var _connectionStr = builder.Configuration.GetConnectionString(nameof(McbaAdminApiContext));

// DbContext service 
builder.Services.AddDbContext<McbaAdminApiContext>(options =>
    options.UseSqlServer(_connectionStr));

// Add Scope for API
builder.Services.AddScoped<CustomerManager>();
builder.Services.AddScoped<BillPayManager>();

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();

app.MapControllers();

app.Run();

