using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MCBA.Migrations
{
    /// <inheritdoc />
    public partial class BillPayChanges : Migration
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
                name: "Period",
                table: "BillPays",
                type: "char(1)",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "int");

            migrationBuilder.AddColumn<string>(
                name: "State",
                table: "BillPays",
                type: "char(1)",
                nullable: false,
                defaultValue: "");

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
                name: "State",
                table: "BillPays");

            migrationBuilder.AlterColumn<string>(
                name: "TransactionType",
                table: "Transactions",
                type: "char",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "char(1)");

            migrationBuilder.AlterColumn<int>(
                name: "Period",
                table: "BillPays",
                type: "int",
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
