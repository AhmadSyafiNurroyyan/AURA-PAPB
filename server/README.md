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

Dockerfile multi-stage membangun distribusi dengan JDK 21, lalu menyalin hasilnya ke runtime JRE 21 yang lebih kecil. Railway mendeteksi `Dockerfile` saat folder service menjadi Root Directory.

### Langkah deploy

1. Pastikan perubahan L06 sudah di-push user ke branch backend yang akan di-deploy.
2. Di Railway, buat project dari repository GitHub AURA dan pilih branch tersebut.
3. Tambahkan service PostgreSQL ke project. Biarkan nama service `Postgres` atau sesuaikan referensi variable pada langkah berikut dengan nama service yang dipilih.
4. Pada service aplikasi, buka **Settings** dan tetapkan **Root Directory** ke `/server`. Railway akan memakai `Dockerfile` dari folder itu.
5. Pada Variables service aplikasi, tambahkan:
   - `DATABASE_URL` = `${{Postgres.DATABASE_URL}}` (ganti `Postgres` jika nama service database berbeda).
   - `JWT_SECRET` = secret acak minimal 32 karakter; buat langsung di Railway, jangan simpan di repo.
   - `UPLOAD_DIR` = `/data/uploads`.
   Railway menyediakan `PORT`; aplikasi bind ke `0.0.0.0`.
6. Tambahkan Volume ke service aplikasi dengan mount path `/data`, agar unggahan di `/data/uploads` bertahan saat deployment diganti.
7. Pada pengaturan health check service, isi path `/health`. Jangan override start command; image menjalankan distribusi `aura-server`.
8. Deploy branch tersebut. Pastikan build log menunjukkan Dockerfile digunakan, deploy log menunjukkan migrasi Flyway selesai tanpa error, dan URL publik service memberi `200` dengan `{"status":"ok"}` pada `/health`.

Parser menerima URL `postgresql://...` dari PostgreSQL Railway maupun JDBC URL `jdbc:postgresql://...`. Status deploy dan URL publik harus dikonfirmasi setelah deployment di Railway; dokumentasi ini tidak menandakan server sudah ter-deploy.

`.env.example` hanya mencatat nama variabel. Jangan masukkan kredensial atau secret ke file yang di-commit.
