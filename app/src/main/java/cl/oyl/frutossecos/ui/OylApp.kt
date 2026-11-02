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
    val colores = darkColorScheme(
        primary = Color(0xFFA1D5AE), onPrimary = Color(0xFF102D1C),
        primaryContainer = Color(0xFF286044), onPrimaryContainer = Color(0xFFFFF9EF),
        secondary = Color(0xFFD5C1A2), onSecondary = Color(0xFF211C16),
        secondaryContainer = Color(0xFF234C35), onSecondaryContainer = Color(0xFFE1F2E5),
        tertiary = Color(0xFFD5C1A2), onTertiary = Color(0xFF211C16),
        tertiaryContainer = Color(0xFF443B2F), onTertiaryContainer = Color(0xFFF3EEE5),
        background = Color(0xFF101311), onBackground = Color(0xFFF3EEE5),
        surface = Color(0xFF191D1A), onSurface = Color(0xFFF3EEE5),
        surfaceVariant = Color(0xFF252C27), onSurfaceVariant = Color(0xFFC5CDC6),
        surfaceTint = Color(0xFFA1D5AE),
        surfaceDim = Color(0xFF101311), surfaceBright = Color(0xFF303831),
        surfaceContainerLowest = Color(0xFF0C100D), surfaceContainerLow = Color(0xFF151A16),
        surfaceContainer = Color(0xFF191F1B), surfaceContainerHigh = Color(0xFF222A24),
        surfaceContainerHighest = Color(0xFF2C352E),
        inverseSurface = Color(0xFFE1E7DE), inverseOnSurface = Color(0xFF191D1A),
        inversePrimary = Color(0xFF286044),
        outline = Color(0xFF89988D), outlineVariant = Color(0xFF414D44)
    )
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
