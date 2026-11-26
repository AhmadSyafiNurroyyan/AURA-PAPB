package com.aura.app.data.remote.dto

import com.aura.app.core.model.Peran
import com.aura.app.core.model.StatusAkun
import com.aura.app.data.local.entity.AkunEntity
import com.aura.app.data.local.entity.TokoEntity
import com.aura.app.data.preferences.RiwayatAkunLogin
import com.aura.app.data.preferences.SesiAktif

fun AkunDto.toEntity(): AkunEntity = AkunEntity(
    id = id,
    idToko = idToko,
    email = email,
    namaAkun = namaAkun,
    peran = Peran.valueOf(peran),
    status = StatusAkun.valueOf(status),
    wajibGantiSandi = wajibGantiSandi,
    nomorHp = nomorHp,
    fotoFileId = fotoFileId,
    terakhirLogin = terakhirLogin,
)

fun KasirListItemDto.toEntity(idToko: String): AkunEntity = AkunEntity(
    id = id,
    idToko = idToko,
    email = null,
    namaAkun = namaAkun,
    peran = Peran.KASIR,
    status = StatusAkun.valueOf(status),
    wajibGantiSandi = false,
    nomorHp = null,
    fotoFileId = null,
    terakhirLogin = terakhirLogin,
)

fun TokoDto.toEntity(existing: TokoEntity? = null): TokoEntity = TokoEntity(
    idToko = idToko,
    namaToko = namaToko,
    jenisUsaha = jenisUsaha,
    alamat = alamat,
    logoFileId = logoFileId,
    saldoAwal = saldoAwal,
    saldoKumulatif = saldoKumulatif,
    namaPenerima = existing?.namaPenerima,
    infoRekening = existing?.infoRekening,
    qrisFileId = existing?.qrisFileId,
)

fun SessionResponseDto.toSesiAktif(): SesiAktif = SesiAktif(
    token = token,
    expiresAt = expiresAt,
    akunId = akun.id,
    idToko = akun.idToko,
    peran = Peran.valueOf(akun.peran),
    nama = akun.namaAkun,
    email = akun.email,
    nomorHp = akun.nomorHp,
    fotoFileId = akun.fotoFileId,
    wajibGantiSandi = wajibGantiSandi || akun.wajibGantiSandi,
)

fun AkunEntity.toRiwayatAkun(fallbackTimestamp: Long = System.currentTimeMillis()): RiwayatAkunLogin =
    RiwayatAkunLogin(
        akunId = id,
        idToko = idToko,
        nama = namaAkun,
        email = email,
        peran = peran,
        fotoFileId = fotoFileId,
        terakhirLogin = terakhirLogin ?: fallbackTimestamp,
    )
