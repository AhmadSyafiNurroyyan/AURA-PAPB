# AURA Server

Backend AURA menggunakan Kotlin, Ktor, dan PostgreSQL. Aplikasi membutuhkan database PostgreSQL yang sudah berjalan sebelum server dimulai. Migrasi Flyway dijalankan saat startup; server berhenti jika koneksi atau migrasi gagal.

## Persiapan PostgreSQL lokal

1. Pasang PostgreSQL Server untuk Windows dari [halaman unduhan resmi PostgreSQL](https://www.postgresql.org/download/windows/). Installer menyertakan pgAdmin.
2. Jalankan PostgreSQL, lalu buat database bernama `aura_dev` melalui pgAdmin. Catat username dan kata sandi yang dibuat saat instalasi.
3. File JAR PostgreSQL JDBC adalah driver koneksi untuk aplikasi Java/Kotlin, bukan server database. Gradle mengambil driver yang diperlukan; JAR itu tidak perlu disalin ke folder proyek.

## Menjalankan server

Dari PowerShell di folder `server`, atur variabel untuk sesi terminal saat ini. Ganti nilai contoh dengan kredensial database lokalmu. `JWT_SECRET` harus sekurangnya 32 karakter.

```powershell
$env:PORT = "8080"
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/aura_dev?user=postgres&password=GANTI_PASSWORD&sslmode=disable"
$env:JWT_SECRET = "ganti-dengan-rahasia-lokal-minimal-32-karakter"
$env:UPLOAD_DIR = ".\var\uploads"
```

Jalankan pemeriksaan dan server:

```powershell
.\gradlew.bat test
.\gradlew.bat run
```

Setelah startup berhasil, cek `http://localhost:8080/health`. Respons yang diharapkan adalah `200` dengan `{"status":"ok"}`.

## Konfigurasi Railway

Set `DATABASE_URL` dan `JWT_SECRET` pada environment variables service Railway. Parser menerima URL `postgresql://...` yang diberikan PostgreSQL Railway maupun JDBC URL `jdbc:postgresql://...`. Railway menyediakan `PORT`; aplikasi bind ke `0.0.0.0`.

`.env.example` hanya mencatat nama variabel. Jangan masukkan kredensial atau secret ke file yang di-commit.
