package cl.oyl.frutossecos.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import cl.oyl.frutossecos.ui.*
import cl.oyl.frutossecos.ui.components.*
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun Escaner(s: EstadoOyl, vm: OylViewModel) {
    val context = LocalContext.current
    var leyendo by rememberSaveable { mutableStateOf(false) }
    Pagina {
        Text("Conoce tu producto al instante", style = MaterialTheme.typography.headlineSmall)
        Text(if (s.esAdmin) "Escanea la etiqueta para abrir el inventario del producto." else "Escanea la etiqueta para consultar su precio, presentación y disponibilidad.")
        Button(onClick = {
            leyendo = true
            val opciones = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
            try {
                GmsBarcodeScanning.getClient(context, opciones).startScan()
                    .addOnSuccessListener { leyendo = false; vm.leerQr(it.rawValue.orEmpty()) }
                    .addOnCanceledListener { leyendo = false; vm.mensaje("Escaneo cancelado") }
                    .addOnFailureListener { leyendo = false; vm.mensaje("No se pudo iniciar el escáner. Revisa la conexión y los Servicios de Google Play") }
            } catch (_: Exception) { leyendo = false; vm.mensaje("El escáner no está disponible en este dispositivo") }
        }, enabled = !leyendo, modifier = Modifier.fillMaxWidth()) { Text(if (leyendo) "Leyendo código…" else "Escanear producto") }
        if (leyendo) LinearProgressIndicator(Modifier.fillMaxWidth())
        val leido = s.productoQr
        AnimatedVisibility(leido != null) {
            if (leido != null) {
                val registrado = if (leido.id != null) s.productos.firstOrNull { it.id == leido.id }
                else s.productos.firstOrNull { it.nombre.equals(leido.nombre, ignoreCase = true) }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(leido.nombre, style = MaterialTheme.typography.titleLarge)
                        Text(leido.descripcion)
                        Text(pesos(leido.precio.toLong()), style = MaterialTheme.typography.titleMedium)
                        if (registrado != null) Button(onClick = { vm.abrir(if (s.esAdmin) Ruta.Inventario else Ruta.Detalle, registrado.id) }) {
                            Text(if (s.esAdmin) "Abrir inventario" else "Ver ficha y disponibilidad")
                        } else Text("Este producto no está registrado en el catálogo disponible.")
                    }
                }
            }
        }
        OutlinedButton(onClick = { vm.abrir(Ruta.Catalogo) }) { Text("Buscar en el catálogo") }
    }
}

@Composable
fun Mapa(s: EstadoOyl, vm: OylViewModel) {
    val context = LocalContext.current
    var ubicacionActiva by rememberSaveable { mutableStateOf(false) }
    val mapa = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(12.0)
            controller.setCenter(GeoPoint(-33.4489, -70.6693))
            setOnTouchListener { vista, evento ->
                vista.parent?.requestDisallowInterceptTouchEvent(evento.action != android.view.MotionEvent.ACTION_UP)
                false
            }
        }
    }
    val capa = remember(mapa) {
        val proveedor = GpsMyLocationProvider(context).apply { addLocationSource(android.location.LocationManager.NETWORK_PROVIDER) }
        MyLocationNewOverlay(proveedor, mapa).also { mapa.overlays.add(it) }
    }
    fun ubicar() {
        ubicacionActiva = true
        capa.enableMyLocation()
        vm.mensaje("Buscando tu ubicación. Comprueba que la ubicación del dispositivo esté activada")
        capa.runOnFirstFix { mapa.post { capa.myLocation?.let { mapa.controller.animateTo(it, 16.0, 1000L) } } }
    }
    val permisos = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
        if (resultado.values.any { it }) ubicar() else vm.mensaje("Sin permiso de ubicación puedes seguir consultando los puntos de retiro")
    }
    DisposableEffect(mapa, s.puntos) {
        val marcadores = s.puntos.map { p ->
            Marker(mapa).apply {
                position = GeoPoint(p.latitud, p.longitud)
                title = p.nombre
                snippet = "${p.direccion}\n${p.horario}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapa.overlays.add(this)
            }
        }
        mapa.invalidate()
        onDispose { mapa.overlays.removeAll(marcadores.toSet()) }
    }
    DisposableEffect(mapa) {
        val ciclo = (context as? ComponentActivity)?.lifecycle
        val observador = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> {
                    mapa.onResume()
                    val autorizado = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
                    if (ubicacionActiva && autorizado) capa.enableMyLocation() else ubicacionActiva = false
                }
                Lifecycle.Event.ON_PAUSE -> { capa.disableMyLocation(); mapa.onPause() }
                else -> Unit
            }
        }
        ciclo?.addObserver(observador)
        mapa.onResume()
        onDispose { ciclo?.removeObserver(observador); capa.disableMyLocation(); mapa.onPause(); mapa.onDetach() }
    }
    Pagina {
        Text("Encuentra tu punto de retiro", style = MaterialTheme.typography.headlineSmall)
        AndroidView(factory = { mapa }, modifier = Modifier.fillMaxWidth().height(340.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { mapa.controller.zoomIn() }) { Text("+") }
            OutlinedButton(onClick = { mapa.controller.zoomOut() }) { Text("−") }
            Button(onClick = {
                val lista = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                if (lista.any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) ubicar()
                else permisos.launch(lista)
            }) { Text("Mi ubicación") }
        }
        s.puntos.forEach { p ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(p.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(p.direccion)
                    Text(p.horario)
                    TextButton(onClick = { mapa.controller.animateTo(GeoPoint(p.latitud, p.longitud), 17.0, 1000L) }) { Text("Ver en el mapa") }
                    OutlinedButton(onClick = {
                        try {
                            val uri = Uri.parse("geo:${p.latitud},${p.longitud}?q=${p.latitud},${p.longitud}(${Uri.encode(p.nombre)})")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        } catch (_: Exception) { vm.mensaje("No hay una aplicación de mapas disponible para abrir indicaciones") }
                    }) { Text("Cómo llegar") }
                }
            }
        }
        Text("© OpenStreetMap contributors", style = MaterialTheme.typography.labelSmall)
    }
}
