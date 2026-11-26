package com.aura.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aura.app.data.local.DatabaseProvider
import com.aura.app.data.network.KoneksiChecker
import com.aura.app.data.preferences.SesiRepository
import com.aura.app.data.remote.GlobalSessionHandler
import com.aura.app.data.remote.RetrofitClient
import com.aura.app.data.remote.datasource.AuthRemoteDataSource
import com.aura.app.data.repository.AkunRepository
import com.aura.app.data.repository.AuthRepository
import com.aura.app.data.repository.DefaultAkunRepository
import com.aura.app.data.repository.DefaultAuthRepository
import com.aura.app.navigation.AuraNavHost

@Composable
fun AuraApp(vm: AuraViewModel = viewModel()) {
    val context = LocalContext.current.applicationContext
    val appDatabase = remember { DatabaseProvider.getDatabase(context) }
    val sesiRepository = remember { SesiRepository(context) }
    val koneksiChecker = remember { KoneksiChecker(context) }
    val globalSessionHandler = remember { GlobalSessionHandler(sesiRepository, appDatabase) }
    val authApiService = remember {
        RetrofitClient.createAuthApiService(
            tokenProvider = { sesiRepository.tokenTersimpanSinkron() },
            onUnauthorized = { globalSessionHandler.onUnauthorized() },
        )
    }
    val authRemoteDataSource = remember { AuthRemoteDataSource(authApiService) }
    val authRepository: AuthRepository = remember {
        DefaultAuthRepository(appDatabase, authRemoteDataSource, sesiRepository, koneksiChecker)
    }
    val akunRepository: AkunRepository = remember {
        DefaultAkunRepository(appDatabase, authRemoteDataSource, sesiRepository, koneksiChecker)
    }

    AuraNavHost(
        authRepository = authRepository,
        akunRepository = akunRepository,
        globalSessionHandler = globalSessionHandler,
        koneksiChecker = koneksiChecker,
        vm = vm,
    )
}
