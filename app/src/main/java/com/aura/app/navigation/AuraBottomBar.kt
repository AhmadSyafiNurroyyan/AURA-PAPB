package com.aura.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.app.ui.theme.Red

@Composable
fun AuraBottomBar(
    currentRoute: String,
    onSelect: (route: String, isRoot: Boolean) -> Unit,
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(currentRoute == Routes.DASHBOARD, { onSelect(Routes.DASHBOARD, true) },
            icon = { Text("⌂") }, label = { Text("Home", fontSize = 10.sp) })
        NavigationBarItem(currentRoute == Routes.MENU_MANAJEMEN, { onSelect(Routes.MENU_MANAJEMEN, true) },
            icon = { Text("▦") }, label = { Text("Manajemen", fontSize = 10.sp) })
        NavigationBarItem(false, { onSelect(Routes.K1_BERANDA_KASIR, false) },
            icon = { Text("+", fontSize = 22.sp, color = Color.White,
                modifier = Modifier.clip(CircleShape).background(Red).padding(horizontal = 12.dp, vertical = 2.dp)) },
            label = { Text("Kasir", fontSize = 10.sp) })
        NavigationBarItem(currentRoute == Routes.ANALISIS, { onSelect(Routes.ANALISIS, true) },
            icon = { Text("▥") }, label = { Text("Analisis", fontSize = 10.sp) })
        NavigationBarItem(currentRoute == Routes.PROFIL, { onSelect(Routes.PROFIL, true) },
            icon = { Text("●") }, label = { Text("Profil", fontSize = 10.sp) })
    }
}
