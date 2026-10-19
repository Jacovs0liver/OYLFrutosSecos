package cl.oyl.frutossecos.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.oyl.frutossecos.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OylApp(vm: OylViewModel) {
    val s by vm.estado.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val colores = lightColorScheme(primary = Color(0xFF184D38), onPrimary = Color.White, primaryContainer = Color(0xFFD8EBDD), secondary = Color(0xFF87552F), background = Color(0xFFFCFAF5), surface = Color(0xFFFCFAF5), surfaceVariant = Color(0xFFF0EBDD))
    LaunchedEffect(s.mensaje) { s.mensaje?.let { snackbar.showSnackbar(it); vm.consumirMensaje() } }
    BackHandler(enabled = s.historial.isNotEmpty() || s.ruta !in listOf(Ruta.Inicio, Ruta.Bienvenida)) { vm.volver() }
    MaterialTheme(colorScheme = colores) {
        Scaffold(
            topBar = {
                if (s.ruta != Ruta.Bienvenida) TopAppBar(title = { Text(s.ruta.titulo) }, navigationIcon = {
                    if (s.ruta != Ruta.Inicio) IconButton(onClick = vm::volver) { Icon(Icons.Default.ArrowBack, "Volver") }
                }, actions = {
                    if (s.usuario != null) {
                        IconButton(onClick = { vm.abrir(Ruta.Avisos) }) { BadgeBox(s.avisos.count { !it.leido }) { Icon(Icons.Default.Notifications, "Notificaciones") } }
                        if (!s.esAdmin) IconButton(onClick = { vm.abrir(Ruta.Carrito) }) { BadgeBox(s.carrito.sumOf { it.cantidad }) { Icon(Icons.Default.ShoppingCart, "Carrito") } }
                    }
                })
            },
            bottomBar = {
                if (s.usuario != null) NavigationBar {
                    listOf(Ruta.Inicio, Ruta.Mapa, Ruta.Escaner, Ruta.Pedidos, Ruta.Perfil).forEach { ruta ->
                        NavigationBarItem(selected = s.ruta == ruta, onClick = { vm.abrir(ruta) }, label = { Text(if (ruta == Ruta.Escaner) "QR" else if (ruta == Ruta.Mapa) "Mapa" else ruta.titulo) }, icon = {
                            Icon(when (ruta) { Ruta.Inicio -> Icons.Default.Home; Ruta.Mapa -> Icons.Default.LocationOn; Ruta.Escaner -> Icons.Default.Search; Ruta.Pedidos -> Icons.Default.ShoppingCart; else -> Icons.Default.Person }, contentDescription = null)
                        })
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbar) }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                if (s.cargando) CircularProgressIndicator(Modifier.align(Alignment.Center))
                else Crossfade(targetState = s.ruta, label = "Cambio de pantalla") { ruta ->
                    when (ruta) {
                        Ruta.Bienvenida -> Bienvenida(vm)
                        Ruta.Login -> Login(s, vm)
                        Ruta.Registro -> Registro(s, vm)
                        Ruta.Inicio -> Inicio(s, vm)
                        Ruta.Catalogo -> Catalogo(s, vm)
                        Ruta.Detalle -> Detalle(s, vm)
                        Ruta.Carrito -> Carrito(s, vm)
                        Ruta.Confirmacion -> Confirmacion(s, vm)
                        Ruta.Pago -> Pago(s, vm)
                        Ruta.PagoExitoso -> PagoExitoso(s, vm)
                        Ruta.PagoFallido -> PagoFallido(vm)
                        Ruta.Productos -> GestionProducto(s, vm)
                        Ruta.Inventario -> Inventario(s, vm)
                        Ruta.Pedidos -> Pedidos(s, vm)
                        Ruta.Avisos -> Avisos(s, vm)
                        Ruta.Perfil -> Perfil(s, vm)
                        Ruta.Mapa -> Mapa(s, vm)
                        Ruta.Escaner -> Escaner(s, vm)
                    }
                }
                if (s.ocupado && !s.cargando) Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.12f)).clickable {}, contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
        }
    }
}

@Composable
private fun BadgeBox(cantidad: Int, contenido: @Composable () -> Unit) {
    BadgedBox(badge = { if (cantidad > 0) Badge { Text(if (cantidad > 99) "99+" else cantidad.toString()) } }) { contenido() }
}
