CREATE TABLE toko (
    id_toko VARCHAR(8) PRIMARY KEY CHECK (id_toko ~ '^[A-Z0-9]{8}$'),
    nama_toko VARCHAR(120) NOT NULL,
    jenis_usaha VARCHAR(80),
    alamat VARCHAR(500),
    logo_file_id UUID,
    saldo_awal BIGINT NOT NULL CHECK (saldo_awal >= 0),
    saldo_kumulatif BIGINT NOT NULL,
    hapus_pada BIGINT,
    dibuat_pada BIGINT NOT NULL
);

CREATE TABLE akun (
    id UUID PRIMARY KEY,
    id_toko VARCHAR(8) NOT NULL REFERENCES toko(id_toko) ON DELETE CASCADE,
    email VARCHAR(254),
    nama_akun VARCHAR(80) NOT NULL,
    nama_akun_normalisasi VARCHAR(80) NOT NULL,
    peran VARCHAR(10) NOT NULL CHECK (peran IN ('OWNER', 'KASIR')),
    sandi_hash VARCHAR(128) NOT NULL,
    sandi_salt VARCHAR(64) NOT NULL,
    status VARCHAR(12) NOT NULL CHECK (status IN ('AKTIF', 'NONAKTIF', 'DIHAPUS')),
    wajib_ganti_sandi BOOLEAN NOT NULL DEFAULT FALSE,
    nomor_hp VARCHAR(30),
    foto_file_id UUID,
    gagal_login INTEGER NOT NULL DEFAULT 0 CHECK (gagal_login >= 0),
    terkunci_sampai BIGINT,
    terakhir_login BIGINT,
    dibuat_pada BIGINT NOT NULL,
    dihapus_pada BIGINT,
    CONSTRAINT akun_email_peran CHECK ((peran = 'OWNER' AND email IS NOT NULL) OR (peran = 'KASIR' AND email IS NULL))
);

CREATE UNIQUE INDEX akun_owner_unik ON akun (id_toko) WHERE peran = 'OWNER';
CREATE UNIQUE INDEX akun_email_normalisasi_unik ON akun (LOWER(email)) WHERE email IS NOT NULL;
CREATE UNIQUE INDEX akun_nama_kasir_per_toko_unik ON akun (id_toko, nama_akun_normalisasi) WHERE peran = 'KASIR' AND status <> 'DIHAPUS';

CREATE TABLE sesi (
    jti UUID PRIMARY KEY,
    akun_id UUID NOT NULL REFERENCES akun(id) ON DELETE CASCADE,
    dibuat_pada BIGINT NOT NULL,
    berakhir_pada BIGINT NOT NULL,
    dicabut_pada BIGINT
);
CREATE INDEX sesi_akun_aktif_idx ON sesi (akun_id, berakhir_pada) WHERE dicabut_pada IS NULL;

CREATE TABLE percobaan_login (
    kunci CHAR(64) PRIMARY KEY,
    jumlah_gagal INTEGER NOT NULL DEFAULT 0 CHECK (jumlah_gagal >= 0),
    terkunci_sampai BIGINT,
    diperbarui_pada BIGINT NOT NULL
);

CREATE TABLE log_aktivitas (
    id UUID PRIMARY KEY,
    id_toko VARCHAR(8) NOT NULL REFERENCES toko(id_toko) ON DELETE CASCADE,
    akun_id UUID REFERENCES akun(id) ON DELETE SET NULL,
    nama_akun_snapshot VARCHAR(80) NOT NULL,
    kategori VARCHAR(10) NOT NULL CHECK (kategori IN ('AKUN', 'LOGIN', 'KEUANGAN')),
    aksi VARCHAR(40) NOT NULL,
    dibuat_pada BIGINT NOT NULL
);
CREATE INDEX log_aktivitas_toko_waktu_idx ON log_aktivitas (id_toko, dibuat_pada DESC);
