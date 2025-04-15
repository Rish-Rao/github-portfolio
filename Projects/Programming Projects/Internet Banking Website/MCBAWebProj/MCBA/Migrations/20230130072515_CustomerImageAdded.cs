using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MCBA.Migrations
{
    /// <inheritdoc />
    public partial class CustomerImageAdded : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AlterColumn<string>(
                name: "TransactionType",
                table: "Transactions",
                type: "char(1)",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char");

            migrationBuilder.AlterColumn<string>(
                name: "AccountState",
                table: "Customers",
                type: "char(1)",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char");

            migrationBuilder.AddColumn<byte[]>(
                name: "Image",
                table: "Customers",
                type: "varbinary(max)",
                nullable: true);

            migrationBuilder.AlterColumn<string>(
                name: "State",
                table: "BillPays",
                type: "char(1)",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char");

            migrationBuilder.AlterColumn<string>(
                name: "Period",
                table: "BillPays",
                type: "char(1)",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char");

            migrationBuilder.AlterColumn<string>(
                name: "AccountType",
                table: "Accounts",
                type: "char(1)",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Image",
                table: "Customers");

            migrationBuilder.AlterColumn<string>(
                name: "TransactionType",
                table: "Transactions",
                type: "char",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char(1)");

            migrationBuilder.AlterColumn<string>(
                name: "AccountState",
                table: "Customers",
                type: "char",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char(1)");

            migrationBuilder.AlterColumn<string>(
                name: "State",
                table: "BillPays",
                type: "char",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char(1)");

            migrationBuilder.AlterColumn<string>(
                name: "Period",
                table: "BillPays",
                type: "char",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char(1)");

            migrationBuilder.AlterColumn<string>(
                name: "AccountType",
                table: "Accounts",
                type: "char",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char(1)");
        }
    }
}
