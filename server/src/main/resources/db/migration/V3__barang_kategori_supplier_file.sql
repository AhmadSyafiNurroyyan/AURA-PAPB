ALTER TABLE log_aktivitas
    DROP CONSTRAINT IF EXISTS log_aktivitas_kategori_check;

ALTER TABLE log_aktivitas
    ADD CONSTRAINT log_aktivitas_kategori_check
        CHECK (kategori IN ('AKUN', 'LOGIN', 'KEUANGAN', 'BARANG', 'STOK'));

ALTER TABLE log_aktivitas
    ADD COLUMN referensi_id UUID,
    ADD COLUMN jenis_referensi VARCHAR(30);

CREATE TABLE barang (
    id UUID PRIMARY KEY,
    id_toko VARCHAR(8) NOT NULL REFERENCES toko(id_toko) ON DELETE CASCADE,
    nama VARCHAR(120) NOT NULL,
    satuan VARCHAR(30) NOT NULL,
    sku VARCHAR(100),
    harga_jual BIGINT NOT NULL CHECK (harga_jual >= 0),
    stok_minimum INTEGER NOT NULL CHECK (stok_minimum >= 0),
    stok_saat_ini INTEGER NOT NULL CHECK (stok_saat_ini >= 0),
    hpp_rata_rata BIGINT NOT NULL CHECK (hpp_rata_rata >= 0),
    status VARCHAR(12) NOT NULL CHECK (status IN ('AKTIF', 'DIARSIPKAN')),
    foto_file_id UUID,
    dibuat_oleh UUID NOT NULL REFERENCES akun(id),
    dibuat_pada BIGINT NOT NULL,
    diubah_pada BIGINT NOT NULL,
    UNIQUE (id, id_toko)
);
CREATE INDEX barang_toko_status_idx ON barang (id_toko, status, nama);
CREATE INDEX barang_toko_sku_idx ON barang (id_toko, sku) WHERE sku IS NOT NULL;

CREATE TABLE kategori (
    id UUID PRIMARY KEY,
    id_toko VARCHAR(8) NOT NULL REFERENCES toko(id_toko) ON DELETE CASCADE,
    nama VARCHAR(80) NOT NULL,
    dibuat_pada BIGINT NOT NULL,
    UNIQUE (id, id_toko)
);
CREATE UNIQUE INDEX kategori_nama_per_toko_unik ON kategori (id_toko, LOWER(nama));

CREATE TABLE barang_kategori (
    id_toko VARCHAR(8) NOT NULL,
    barang_id UUID NOT NULL,
    kategori_id UUID NOT NULL,
    PRIMARY KEY (barang_id, kategori_id),
    FOREIGN KEY (barang_id, id_toko) REFERENCES barang(id, id_toko) ON DELETE CASCADE,
    FOREIGN KEY (kategori_id, id_toko) REFERENCES kategori(id, id_toko) ON DELETE CASCADE
);
CREATE INDEX barang_kategori_kategori_idx ON barang_kategori (kategori_id);

CREATE TABLE supplier (
    id UUID PRIMARY KEY,
    id_toko VARCHAR(8) NOT NULL REFERENCES toko(id_toko) ON DELETE CASCADE,
    nama VARCHAR(120) NOT NULL,
    link VARCHAR(2048),
    catatan VARCHAR(500),
    status VARCHAR(10) NOT NULL CHECK (status IN ('AKTIF', 'NONAKTIF')),
    dibuat_pada BIGINT NOT NULL,
    UNIQUE (id, id_toko)
);
CREATE INDEX supplier_toko_status_idx ON supplier (id_toko, status, nama);

CREATE TABLE harga_beli_supplier (
    id_toko VARCHAR(8) NOT NULL,
    barang_id UUID NOT NULL,
    supplier_id UUID NOT NULL,
    harga_per_item BIGINT NOT NULL CHECK (harga_per_item >= 0),
    diperbarui_pada BIGINT NOT NULL,
    PRIMARY KEY (barang_id, supplier_id),
    FOREIGN KEY (barang_id, id_toko) REFERENCES barang(id, id_toko) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id, id_toko) REFERENCES supplier(id, id_toko) ON DELETE RESTRICT
);
CREATE INDEX harga_beli_supplier_supplier_idx ON harga_beli_supplier (supplier_id, diperbarui_pada DESC);

CREATE TABLE pergerakan_stok (
    id UUID PRIMARY KEY,
    id_toko VARCHAR(8) NOT NULL,
    barang_id UUID NOT NULL,
    akun_id UUID REFERENCES akun(id) ON DELETE SET NULL,
    jenis VARCHAR(20) NOT NULL CHECK (jenis IN ('MASUK', 'KELUAR', 'PENYESUAIAN')),
    sumber VARCHAR(40) NOT NULL,
    jumlah INTEGER NOT NULL CHECK (jumlah > 0),
    stok_sebelum INTEGER NOT NULL CHECK (stok_sebelum >= 0),
    stok_sesudah INTEGER NOT NULL CHECK (stok_sesudah >= 0),
    referensi_id UUID,
    jenis_referensi VARCHAR(30),
    dibuat_pada BIGINT NOT NULL,
    FOREIGN KEY (barang_id, id_toko) REFERENCES barang(id, id_toko) ON DELETE CASCADE
);
CREATE INDEX pergerakan_stok_barang_waktu_idx ON pergerakan_stok (id_toko, barang_id, dibuat_pada DESC);

CREATE TABLE file (
    id UUID PRIMARY KEY,
    id_toko VARCHAR(8) NOT NULL REFERENCES toko(id_toko) ON DELETE CASCADE,
    mime_type VARCHAR(30) NOT NULL CHECK (mime_type IN ('image/jpeg', 'image/png', 'image/webp')),
    ukuran BIGINT NOT NULL CHECK (ukuran > 0 AND ukuran <= 10485760),
    dibuat_pada BIGINT NOT NULL,
    UNIQUE (id, id_toko)
);

