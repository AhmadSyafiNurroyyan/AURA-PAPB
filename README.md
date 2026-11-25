# AURA Android — Kotlin + Jetpack Compose + Room

Aplikasi Android native yang mengonversi prototype AURA HTML ke Kotlin. Runtime aplikasi tidak menggunakan WebView, HTML, atau JavaScript.

## Stack
- Kotlin 2.2.10
- Jetpack Compose + Material 3
- Room / SQLite untuk penyimpanan lokal
- Kotlin Coroutines + Flow
- KSP untuk Room compiler
- Version catalog `gradle/libs.versions.toml` disalin dari file yang diberikan.

## Cara membuka
1. Ekstrak ZIP.
2. Buka Android Studio dan pilih **Open** pada folder `AURA_Android`.
3. Gunakan JDK 17 (atau JDK yang disyaratkan versi Android Gradle Plugin di Android Studio).
4. Pastikan Android SDK Platform 36 terpasang.
5. Lakukan Gradle Sync, lalu Run pada emulator/perangkat.
6. Jika Android Studio meminta Gradle wrapper, gunakan distribusi Gradle 9.4.1 sesuai `gradle/wrapper/gradle-wrapper.properties`. File wrapper executable/JAR tidak disertakan oleh lingkungan pembuatan ini. Bila project dibuka tanpa wrapper, pilih distribusi Gradle lokal/unduh Gradle 9.4.1 pada Android Studio, lalu jalankan tugas `gradle wrapper` dari terminal Gradle yang sudah tersedia untuk menghasilkan `gradlew`, `gradlew.bat`, dan `gradle-wrapper.jar`.

## Akun demo lokal
Database akan diinisialisasi otomatis saat aplikasi pertama kali dijalankan.

| Role | Identitas | Password |
|---|---|---|
| Owner | `owner@tokoberkah.com` | `123456` |
| Kasir | `AB12CD34/siti.aminah` | `123456` |
| Kasir | `AB12CD34/andi.pratama` | `123456` |

Untuk login Kasir, masukkan `IDTOKO/username` pada kolom identitas. Jika hanya memasukkan username, aplikasi memakai ID toko demo yang tersimpan.

## Penyimpanan & aturan bisnis
- Database lokal: `aura_local.db` (Room/SQLite).
- Session disimpan lokal, sehingga sesi tetap ada setelah aplikasi dibuka ulang.
- Satu instalasi lokal dirancang untuk satu workspace toko. Jika memilih registrasi baru dari prototype demo, data demo lokal akan dibersihkan dan diganti dengan workspace toko baru.
- Password akun disimpan sebagai hash SHA-256, bukan teks biasa. Ini masih prototipe offline; autentikasi produksi sebaiknya menggunakan KDF password yang lebih kuat dan backend.
- Transaksi memeriksa stok sebelum checkout, membuat nomor invoice, menyimpan detail item, mengurangi stok, menambah saldo, dan membuat stock movement dalam satu transaksi database.
- Restock menambah stok, menyimpan catatan restock tersendiri, mengurangi saldo, dan menghitung HPP rata-rata tertimbang. Riwayat restock ditampilkan sebagai pembelian pada halaman Pengeluaran, tetapi tidak dihitung sebagai pengeluaran kustom rolling-window.
- Penyesuaian stok mencatat selisih, alasan, kerugian stok, dan stock movement.
- Prive mengurangi saldo tetapi dikecualikan dari pengeluaran untuk perhitungan laba.
- Tambah modal menambah saldo tetapi dikecualikan dari omzet/laba.
- Pembatalan transaksi Owner mengembalikan stok dan mengurangi saldo. Kasir hanya dapat mengajukan pembatalan dengan alasan.
- Transaksi, restock, penyesuaian stok, pengeluaran, dan pemasukan dibatasi rolling window maksimal 5.000 entri per jenis catatan. Stock movements dan activity logs tidak dipangkas.
- Data transaksi/keuangan bersifat append-only; tidak disediakan fitur edit nominal catatan yang sudah tersimpan.

## Pemetaan 31 file layar HTML
| Prototype | Implementasi Kotlin |
|---|---|
| welcome.js | WelcomePage |
| login.js | LoginPage |
| register_step1.js | RegisterOnePage |
| register_step2.js | RegisterTwoPage |
| dashboard.js | DashboardPage |
| menu_manajemen.js | ManagementPage |
| analisis.js | AnalysisPage |
| profil.js | ProfilePage |
| notifikasi.js | NotificationPage |
| pengeluaran.js | CashRecordListPage (expense) |
| catat_pengeluaran.js | ExpenseFormPage |
| pemasukan.js | CashRecordListPage (income) |
| catat_pemasukan.js | IncomeFormPage |
| manajemen_barang.js | ProductListPage |
| profil_barang.js | ProductDetailPage |
| tambah_barang.js | ProductFormPage |
| catat_restock.js | RestockPage |
| penyesuaian_stok.js | AdjustmentPage |
| pergerakan_stok.js | MovementPage |
| supplier.js | SupplierPage |
| riwayat_aktivitas.js | ActivityPage |
| pengaturan.js | SettingsPage |
| manajemen_akun_kasir.js | CashierAccountsPage |
| metode_pembayaran.js | PaymentSettingsPage |
| k1_beranda_kasir.js | CashierPage |
| k2_catat_transaksi.js | CartPage |
| k3_pembayaran.js | PaymentPage |
| k4_transaksi_berhasil.js | SuccessPage |
| k4_transaksi_berhasil_owner.js | SuccessPage (Owner route) |
| riwayat_hari_ini.js | HistoryPage |
| riwayat_hari_ini_owner.js | HistoryPage (Owner route) |

## Batasan yang masih perlu diketahui
- Backend REST API memang belum ada, jadi seluruh operasi saat ini lokal di satu perangkat. Sinkronisasi antarperangkat belum tersedia.
- Tampilan Compose sudah memakai warna, komponen, alur, dan struktur informasi AURA, tetapi belum dijamin pixel-perfect terhadap HTML prototype. Detail layout per halaman masih dapat disempurnakan.
- Prediksi analisis merupakan estimasi sederhana dari data lokal, bukan model prediksi statistik/ML.
- QRIS disimpan sebagai URI dokumen lokal menggunakan Android document picker; belum ada pembayaran QRIS/payment gateway.
- Nomor invoice demo dibuat lokal. Untuk penggunaan multi-device nanti, nomor invoice harus digenerate backend agar unik global.
- Build Gradle belum dapat dijalankan di lingkungan pembuat file karena Android SDK/Gradle tidak tersedia. Lakukan Sync dan Build di Android Studio; jika muncul error, kirimkan Build Output agar dapat diperbaiki berdasarkan error aktual.

## Struktur kode
- `data/AuraDatabase.kt`: Entity, DAO, Room database.
- `data/AuraRepository.kt`: seed demo, autentikasi lokal, transaksi atomik, stok, saldo, supplier, akun, audit, notifikasi, dan rolling window.
- Database menggunakan satu workspace toko per instalasi; registrasi baru menghapus workspace demo yang sudah disediakan.
- `ui/AuraViewModel.kt`: state Flow dan operasi UI.
- `ui/AuraApp.kt`: seluruh layar Compose dan navigasi.
- `MainActivity.kt`: entry point.


## Checklist uji manual yang disarankan
1. Login Owner dengan akun demo.
2. Tambah barang, edit barang, arsipkan barang.
3. Catat restock dan pastikan stok, HPP, saldo, pengeluaran, serta stock movement berubah.
4. Catat penyesuaian stok dengan alasan Rusak/Hilang/Kadaluarsa dan periksa nilai kerugian.
5. Login Kasir, tambah barang ke keranjang, coba kuantitas melebihi stok, lalu checkout.
6. Periksa invoice, transaksi, item transaksi, stok, saldo, dan stock movement.
7. Ajukan pembatalan dari Kasir; login Owner dan batalkan dengan alasan; periksa stok serta saldo kembali.
8. Catat pengeluaran biasa dan prive; catat pemasukan biasa dan modal; periksa saldo dan analisis.
9. Tambah/edit/nonaktifkan akun Kasir, tambah/edit/hapus supplier.
10. Tutup dan buka kembali aplikasi; periksa session dan data lokal tetap tersimpan.
