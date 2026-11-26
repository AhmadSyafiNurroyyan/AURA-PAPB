package com.aura.app.ui.screens.barang

import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.aura.app.R
import com.aura.app.core.format.RupiahFormatter
import com.aura.app.core.model.StatusAkun
import com.aura.app.data.local.entity.SupplierEntity
import com.aura.app.ui.components.Heading
import com.aura.app.ui.components.Input
import com.aura.app.ui.components.Page
import com.aura.app.ui.components.Primary
import com.aura.app.ui.components.Secondary
import com.aura.app.ui.barang.BarangTampilan
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RestockScreen(
    products: List<BarangTampilan>,
    suppliers: List<SupplierEntity>,
    initialProductId: String?,
    saving: Boolean,
    save: (String, String, Int, Long, String) -> Unit,
    manageSuppliers: () -> Unit,
    back: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeSuppliers = suppliers.filter { it.status == StatusAkun.AKTIF }
    var productId by rememberSaveable { mutableStateOf(initialProductId ?: products.firstOrNull()?.barang?.id.orEmpty()) }
    var supplierId by rememberSaveable { mutableStateOf(activeSuppliers.firstOrNull()?.id.orEmpty()) }
    var quantityText by rememberSaveable { mutableStateOf("1") }
    var totalText by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var productExpanded by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }
    val quantity = quantityText.toIntOrNull() ?: 0
    val total = totalText.toLongOrNull() ?: -1L
    val unitPrice = if (quantity > 0 && total >= 0) {
        BigDecimal.valueOf(total).divide(BigDecimal.valueOf(quantity.toLong()), 0, RoundingMode.HALF_UP).longValueExact()
    } else {
        null
    }

    Page(modifier = modifier) {
        Heading(stringResource(R.string.screen_copy_069), stringResource(R.string.restock_effects))
        ExposedDropdownMenuBox(productExpanded, { productExpanded = it }) {
            OutlinedTextField(
                value = products.firstOrNull { it.barang.id == productId }?.barang?.nama
                    ?: stringResource(R.string.restock_select_product),
                onValueChange = {},
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                readOnly = true,
                label = { Text(stringResource(R.string.screen_copy_070)) },
            )
            ExposedDropdownMenu(productExpanded, { productExpanded = false }) {
                products.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.barang.nama) },
                        onClick = { productId = item.barang.id; productExpanded = false },
                    )
                }
            }
        }
        ExposedDropdownMenuBox(supplierExpanded, { supplierExpanded = it }) {
            OutlinedTextField(
                value = activeSuppliers.firstOrNull { it.id == supplierId }?.nama
                    ?: stringResource(R.string.restock_select_supplier),
                onValueChange = {},
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                readOnly = true,
                label = { Text(stringResource(R.string.katalog_select_supplier)) },
            )
            ExposedDropdownMenu(supplierExpanded, { supplierExpanded = false }) {
                activeSuppliers.forEach { supplier ->
                    DropdownMenuItem(
                        text = { Text(supplier.nama) },
                        onClick = { supplierId = supplier.id; supplierExpanded = false },
                    )
                }
            }
        }
        TextButton(onClick = manageSuppliers) {
            Text(stringResource(R.string.restock_manage_suppliers))
        }
        Input(stringResource(R.string.restock_quantity), quantityText, { quantityText = it }, number = true)
        Input(stringResource(R.string.restock_total_paid), totalText, { totalText = it }, number = true)
        unitPrice?.let {
            Text(
                stringResource(R.string.restock_unit_price, RupiahFormatter.format(it)),
                fontWeight = FontWeight.SemiBold,
            )
        }
        Input(stringResource(R.string.screen_copy_041), note, { note = it })
        Primary(
            stringResource(R.string.screen_copy_075),
            { save(productId, supplierId, quantity, total, note) },
            !saving && productId.isNotBlank() && supplierId.isNotBlank() && quantity > 0 && total >= 0,
        )
        Secondary(stringResource(R.string.screen_copy_043), back)
    }
}
