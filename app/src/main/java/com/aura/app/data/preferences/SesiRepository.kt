package com.aura.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aura.app.core.model.Peran
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

internal val Context.auraSesiDataStore by preferencesDataStore(name = "aura_sesi")

/**
 * Data sesi akun yang sedang masuk di perangkat (tanpa menyimpan kata sandi).
 */
data class SesiAktif(
    val token: String,
    val expiresAt: Long,
    val akunId: String,
    val idToko: String,
    val peran: Peran,
    val nama: String,
    val email: String? = null,
    val nomorHp: String? = null,
    val fotoFileId: String? = null,
    val wajibGantiSandi: Boolean = false,
)

/**
 * Ringkasan akun yang pernah login di perangkat untuk fitur Ganti Akun (maks. 5 akun, tanpa sandi).
 */
data class RiwayatAkunLogin(
    val akunId: String,
    val idToko: String,
    val nama: String,
    val email: String? = null,
    val peran: Peran,
    val fotoFileId: String? = null,
    val terakhirLogin: Long,
)

/**
 * Pembantu murni untuk serialisasi dan batas maksimal 5 riwayat akun login (spec/03).
 */
object RiwayatAkunHelper {
    const val MAX_RIWAYAT_AKUN = 5
    private val gson = Gson()
    private val listType = object : TypeToken<List<RiwayatAkunLogin>>() {}.type

    fun tambahAtauPerbarui(
        daftarSaatIni: List<RiwayatAkunLogin>,
        akunBaru: RiwayatAkunLogin,
    ): List<RiwayatAkunLogin> {
        val tanpaDuplikat = daftarSaatIni.filterNot { it.akunId == akunBaru.akunId }
        return (listOf(akunBaru) + tanpaDuplikat).take(MAX_RIWAYAT_AKUN)
    }

    fun hapusAkun(
        daftarSaatIni: List<RiwayatAkunLogin>,
        akunId: String,
    ): List<RiwayatAkunLogin> = daftarSaatIni.filterNot { it.akunId == akunId }

    fun encode(daftar: List<RiwayatAkunLogin>): String = gson.toJson(daftar.take(MAX_RIWAYAT_AKUN))

    fun decode(json: String?): List<RiwayatAkunLogin> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            val parsed: List<RiwayatAkunLogin>? = gson.fromJson(json, listType)
            parsed.orEmpty().take(MAX_RIWAYAT_AKUN)
        }.getOrDefault(emptyList())
    }
}

/**
 * Mengelola penyimpanan sesi login aktif dan daftar akun pernah login via DataStore Preferences.
 */
class SesiRepository(private val context: Context) {
    private val tokenCache = AtomicReference<String?>(null)

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val EXPIRES_AT = longPreferencesKey("expires_at")
        val AKUN_ID = stringPreferencesKey("akun_id")
        val ID_TOKO = stringPreferencesKey("id_toko")
        val PERAN = stringPreferencesKey("peran")
        val NAMA = stringPreferencesKey("nama")
        val EMAIL = stringPreferencesKey("email")
        val NOMOR_HP = stringPreferencesKey("nomor_hp")
        val FOTO_FILE_ID = stringPreferencesKey("foto_file_id")
        val WAJIB_GANTI_SANDI = booleanPreferencesKey("wajib_ganti_sandi")
        val RIWAYAT_AKUN_JSON = stringPreferencesKey("riwayat_akun_json")
    }

    val sesiAktif: Flow<SesiAktif?> = context.auraSesiDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs.toSesiAktif().also { tokenCache.set(it?.token) } }

    val riwayatAkun: Flow<List<RiwayatAkunLogin>> = context.auraSesiDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> RiwayatAkunHelper.decode(prefs[Keys.RIWAYAT_AKUN_JSON]) }

    fun tokenTersimpanSinkron(): String? = tokenCache.get()

    suspend fun muatToken(): String? {
        val cached = tokenCache.get()
        if (!cached.isNullOrBlank()) return cached
        val loaded = sesiAktif.first()?.token
        tokenCache.set(loaded)
        return loaded
    }

    suspend fun simpanSesi(sesi: SesiAktif, terakhirLogin: Long = System.currentTimeMillis()) {
        tokenCache.set(sesi.token)
        context.auraSesiDataStore.edit { prefs ->
            prefs[Keys.TOKEN] = sesi.token
            prefs[Keys.EXPIRES_AT] = sesi.expiresAt
            prefs[Keys.AKUN_ID] = sesi.akunId
            prefs[Keys.ID_TOKO] = sesi.idToko
            prefs[Keys.PERAN] = sesi.peran.name
            prefs[Keys.NAMA] = sesi.nama
            if (sesi.email != null) prefs[Keys.EMAIL] = sesi.email else prefs.remove(Keys.EMAIL)
            if (sesi.nomorHp != null) prefs[Keys.NOMOR_HP] = sesi.nomorHp else prefs.remove(Keys.NOMOR_HP)
            if (sesi.fotoFileId != null) prefs[Keys.FOTO_FILE_ID] = sesi.fotoFileId else prefs.remove(Keys.FOTO_FILE_ID)
            prefs[Keys.WAJIB_GANTI_SANDI] = sesi.wajibGantiSandi

            val saatIni = RiwayatAkunHelper.decode(prefs[Keys.RIWAYAT_AKUN_JSON])
            val diperbarui = RiwayatAkunHelper.tambahAtauPerbarui(
                saatIni,
                RiwayatAkunLogin(
                    akunId = sesi.akunId,
                    idToko = sesi.idToko,
                    nama = sesi.nama,
                    email = sesi.email,
                    peran = sesi.peran,
                    fotoFileId = sesi.fotoFileId,
                    terakhirLogin = terakhirLogin,
                ),
            )
            prefs[Keys.RIWAYAT_AKUN_JSON] = RiwayatAkunHelper.encode(diperbarui)
        }
    }

    suspend fun perbaruiAkunAktif(
        nama: String,
        email: String?,
        nomorHp: String?,
        fotoFileId: String?,
        wajibGantiSandi: Boolean,
    ) {
        context.auraSesiDataStore.edit { prefs ->
            val akunId = prefs[Keys.AKUN_ID] ?: return@edit
            val idToko = prefs[Keys.ID_TOKO] ?: return@edit
            val peran = prefs[Keys.PERAN]?.let { runCatching { Peran.valueOf(it) }.getOrNull() } ?: return@edit
            prefs[Keys.NAMA] = nama
            if (email != null) prefs[Keys.EMAIL] = email else prefs.remove(Keys.EMAIL)
            if (nomorHp != null) prefs[Keys.NOMOR_HP] = nomorHp else prefs.remove(Keys.NOMOR_HP)
            if (fotoFileId != null) prefs[Keys.FOTO_FILE_ID] = fotoFileId else prefs.remove(Keys.FOTO_FILE_ID)
            prefs[Keys.WAJIB_GANTI_SANDI] = wajibGantiSandi

            val saatIni = RiwayatAkunHelper.decode(prefs[Keys.RIWAYAT_AKUN_JSON])
            val lama = saatIni.firstOrNull { it.akunId == akunId }
            val diperbarui = RiwayatAkunHelper.tambahAtauPerbarui(
                saatIni,
                RiwayatAkunLogin(
                    akunId = akunId,
                    idToko = idToko,
                    nama = nama,
                    email = email,
                    peran = peran,
                    fotoFileId = fotoFileId,
                    terakhirLogin = lama?.terakhirLogin ?: System.currentTimeMillis(),
                ),
            )
            prefs[Keys.RIWAYAT_AKUN_JSON] = RiwayatAkunHelper.encode(diperbarui)
        }
    }

    suspend fun hapusSesiAktif() {
        tokenCache.set(null)
        context.auraSesiDataStore.edit { prefs ->
            prefs.remove(Keys.TOKEN)
            prefs.remove(Keys.EXPIRES_AT)
            prefs.remove(Keys.AKUN_ID)
            prefs.remove(Keys.ID_TOKO)
            prefs.remove(Keys.PERAN)
            prefs.remove(Keys.NAMA)
            prefs.remove(Keys.EMAIL)
            prefs.remove(Keys.NOMOR_HP)
            prefs.remove(Keys.FOTO_FILE_ID)
            prefs.remove(Keys.WAJIB_GANTI_SANDI)
        }
    }

    suspend fun hapusAkunDariRiwayat(akunId: String) {
        context.auraSesiDataStore.edit { prefs ->
            val saatIni = RiwayatAkunHelper.decode(prefs[Keys.RIWAYAT_AKUN_JSON])
            val diperbarui = RiwayatAkunHelper.hapusAkun(saatIni, akunId)
            prefs[Keys.RIWAYAT_AKUN_JSON] = RiwayatAkunHelper.encode(diperbarui)
        }
    }

    suspend fun hapusSemua() {
        tokenCache.set(null)
        context.auraSesiDataStore.edit { it.clear() }
    }

    private fun Preferences.toSesiAktif(): SesiAktif? {
        val token = this[Keys.TOKEN]?.takeIf { it.isNotBlank() } ?: return null
        val akunId = this[Keys.AKUN_ID] ?: return null
        val idToko = this[Keys.ID_TOKO] ?: return null
        val peran = this[Keys.PERAN]?.let { runCatching { Peran.valueOf(it) }.getOrNull() } ?: return null
        val nama = this[Keys.NAMA] ?: return null
        return SesiAktif(
            token = token,
            expiresAt = this[Keys.EXPIRES_AT] ?: 0L,
            akunId = akunId,
            idToko = idToko,
            peran = peran,
            nama = nama,
            email = this[Keys.EMAIL],
            nomorHp = this[Keys.NOMOR_HP],
            fotoFileId = this[Keys.FOTO_FILE_ID],
            wajibGantiSandi = this[Keys.WAJIB_GANTI_SANDI] ?: false,
        )
    }
}
