# ticket-booking-engine

## Running locally

1. Install PostgreSQL and create a database named `ticket_booking_auth`
2. Set the following environment variables before running the app:
   - `DB_PASSWORD` — your PostgreSQL password
   - `JWT_SECRET` — a long, random secret string used to sign JWTs

   You can set these however your environment supports it, for example:
   - **Terminal (Mac/Linux):** `export DB_PASSWORD=your_password`
   - **Terminal (Windows PowerShell):** `$env:DB_PASSWORD="your_password"`
   - **IntelliJ:** Run → Edit Configurations → Environment Variables
   - **VS Code:** add to a `launch.json` configuration, or set in your terminal before running