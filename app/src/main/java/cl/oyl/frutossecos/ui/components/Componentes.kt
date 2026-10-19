package cl.oyl.frutossecos.ui.components

import android.content.Context
import android.content.ActivityNotFoundException
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.oyl.frutossecos.util.cargarImagenReducida
import cl.oyl.frutossecos.util.crearArchivoTemporal
import cl.oyl.frutossecos.util.uriCompartible
import cl.oyl.frutossecos.util.guardarEnGaleria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import cl.oyl.frutossecos.ui.EstadoOyl
import cl.oyl.frutossecos.ui.OylViewModel

fun pesos(valor: Long): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL")).apply { maximumFractionDigits = 0 }.format(valor)
fun fecha(valor: Long): String = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.forLanguageTag("es-CL")).format(Date(valor))

@Composable
fun Pagina(contenido: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = contenido)
}

@Composable
fun Campo(etiqueta: String, valor: String, cambiar: (String) -> Unit, error: String? = null, clave: Boolean = false, numero: Boolean = false, habilitado: Boolean = true, teclado: KeyboardType? = null) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = valor, onValueChange = cambiar, modifier = Modifier.fillMaxWidth(), label = { Text(etiqueta) },
        singleLine = true, isError = error != null, enabled = habilitado,
        leadingIcon = { Icon(if (clave) Icons.Default.Lock else Icons.Default.Edit, contentDescription = null) },
        trailingIcon = if (clave) ({ TextButton(onClick = { visible = !visible }) { Text(if (visible) "Ocultar" else "Ver") } }) else null,
        supportingText = { if (error != null) Text(error) },
        visualTransformation = if (clave && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = teclado ?: if (numero) KeyboardType.Number else if (clave) KeyboardType.Password else KeyboardType.Text)
    )
}

@Composable
fun CampoFormularioTexto(etiqueta: String, nombre: String, estado: EstadoOyl, vm: OylViewModel, clave: Boolean = false, numero: Boolean = false, habilitado: Boolean = true) {
    val campo = estado.formulario.campo(nombre)
    Campo(
        etiqueta = etiqueta,
        valor = campo.valor,
        cambiar = { vm.cambiarCampo(nombre, it) },
        error = campo.error,
        clave = clave,
        numero = numero,
        habilitado = habilitado,
        teclado = when (nombre) {
            "correo" -> KeyboardType.Email
            "telefono" -> KeyboardType.Phone
            else -> null
        }
    )
}

@Composable
fun Opciones(opciones: List<String>, actual: String, cambiar: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        opciones.forEach { opcion ->
            FilterChip(selected = opcion == actual, onClick = { cambiar(opcion) }, label = { Text(opcion) })
        }
    }
}

@Composable
fun MensajeVacio(titulo: String, detalle: String) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(detalle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun Exito(titulo: String, detalle: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Check, contentDescription = "Operación completada", modifier = Modifier.size(40.dp))
            Text(titulo, style = MaterialTheme.typography.headlineSmall)
            Text(detalle)
        }
    }
}

@Composable
fun Foto(uri: String, nombre: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(null, uri) {
        value = if (uri.isBlank()) null else withContext(Dispatchers.IO) {
            runCatching {
                context.cargarImagenReducida(Uri.parse(uri), 600)?.asImageBitmap()
            }.getOrNull()
        }
    }
    val img = bitmap
    if (img != null) Image(img, contentDescription = nombre, modifier = modifier.clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
    else Box(modifier.clip(RoundedCornerShape(16.dp)).background(Color(0xFFF0E4CE)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
            Text("OYL", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF184D38))
            Text(nombre, style = MaterialTheme.typography.labelMedium, color = Color(0xFF67472F))
        }
    }
}

private fun Context.faltaPermisoParaGuardar(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

private fun Context.hayAppDeCamara(): Boolean = Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager) != null

@Composable
fun BotonFoto(alGuardar: (String) -> Unit, alError: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var rutaTemporal by rememberSaveable { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }
    val captura = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val archivo = rutaTemporal?.let { File(it) }
        if (!ok || archivo == null || archivo.length() == 0L) {
            archivo?.delete()
            alError("No se tomó ninguna foto")
        } else {
            guardando = true
            scope.launch {
                val uri = withContext(Dispatchers.IO) {
                    context.guardarEnGaleria(archivo).also { archivo.delete() }
                }
                guardando = false
                if (uri != null) alGuardar(uri.toString()) else alError("No se pudo guardar la foto")
            }
        }
        rutaTemporal = null
    }
    fun abrirCamara() {
        if (!context.hayAppDeCamara()) { alError("Este dispositivo no tiene una aplicación de cámara"); return }
        val archivo = context.crearArchivoTemporal("temporal_${System.currentTimeMillis()}.jpg")
        rutaTemporal = archivo.absolutePath
        try { captura.launch(context.uriCompartible(archivo)) }
        catch (_: ActivityNotFoundException) { alError("No se pudo abrir la cámara") }
    }
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) abrirCamara() else alError("Sin permiso no se puede guardar la foto en la galería")
    }
    OutlinedButton(onClick = {
        if (context.faltaPermisoParaGuardar()) permiso.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) else abrirCamara()
    }, enabled = !guardando) { Text(if (guardando) "Guardando fotografía…" else "Tomar fotografía") }
}
