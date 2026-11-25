package com.aura.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aura.app.navigation.AuraNavHost

@Composable
fun AuraApp(vm: AuraViewModel = viewModel()) {
    AuraNavHost(vm)
}
