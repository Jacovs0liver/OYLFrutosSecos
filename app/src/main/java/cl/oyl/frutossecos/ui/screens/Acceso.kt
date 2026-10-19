package cl.oyl.frutossecos.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import cl.oyl.frutossecos.ui.*
import cl.oyl.frutossecos.ui.components.*

@Composable
fun Bienvenida(vm: OylViewModel) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    Pagina {
        Spacer(Modifier.height(32.dp))
        AnimatedVisibility(visible, enter = fadeIn() + slideInVertically { it / 3 }) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                MarcaAnimada(Modifier.fillMaxWidth().height(220.dp))
                Text("Naturalmente,\npara tu día.", style = MaterialTheme.typography.displaySmall)
                Text("Descubre nuestros productos, prepara tu pedido y elige dónde retirarlo.", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { vm.abrir(Ruta.Login) }, modifier = Modifier.fillMaxWidth()) { Text("Comenzar") }
                OutlinedButton(onClick = { vm.abrir(Ruta.Catalogo) }, modifier = Modifier.fillMaxWidth()) { Text("Explorar catálogo") }
                TextButton(onClick = { vm.abrir(Ruta.Mapa) }) { Text("Ver puntos de retiro") }
            }
        }
    }
}

@Composable
fun Login(s: EstadoOyl, vm: OylViewModel) {
    val formulario = s.formulario
    LoginScreen(
        alias = formulario.campo("alias"),
        clave = formulario.campo("clave"),
        rol = formulario.valor("rol"),
        ocupado = s.ocupado,
        alCambiarAlias = { vm.cambiarCampo("alias", it) },
        alCambiarClave = { vm.cambiarCampo("clave", it) },
        alCambiarRol = { vm.cambiarCampo("rol", it) },
        alIngresar = {
            vm.ingresar(formulario.valor("alias"), formulario.valor("clave"), formulario.valor("rol"))
        },
        alRegistrarse = { vm.abrir(Ruta.Registro) }
    )
}

@Composable
private fun LoginScreen(
    alias: CampoFormulario,
    clave: CampoFormulario,
    rol: String,
    ocupado: Boolean,
    alCambiarAlias: (String) -> Unit,
    alCambiarClave: (String) -> Unit,
    alCambiarRol: (String) -> Unit,
    alIngresar: () -> Unit,
    alRegistrarse: () -> Unit
) {
    Pagina {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)
        Opciones(listOf("Cliente", "Administrador"), rol, alCambiarRol)
        OutlinedTextField(
            value = alias.valor,
            onValueChange = alCambiarAlias,
            label = { Text("Alias") },
            singleLine = true,
            isError = alias.error != null,
            supportingText = { alias.error?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = clave.valor,
            onValueChange = alCambiarClave,
            label = { Text("Contraseña") },
            singleLine = true,
            isError = clave.error != null,
            supportingText = { clave.error?.let { Text(it) } },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = alIngresar, enabled = !ocupado, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar")
        }
        TextButton(onClick = alRegistrarse) { Text("Crear cuenta de cliente") }
    }
}

@Composable
fun Registro(s: EstadoOyl, vm: OylViewModel) {
    FormularioPerfil(s, vm, true)
}

@Composable
fun Perfil(s: EstadoOyl, vm: OylViewModel) {
    if (s.usuario == null) return
    FormularioPerfil(s, vm, false)
}

@Composable
private fun FormularioPerfil(s: EstadoOyl, vm: OylViewModel, registro: Boolean) {
    val u = if (registro) null else s.usuario
    val formulario = s.formulario
    val nombre = formulario.valor("nombre")
    val alias = formulario.valor("alias")
    val correo = formulario.valor("correo")
    val telefono = formulario.valor("telefono")
    val direccion = formulario.valor("direccion")
    val clave = formulario.valor("clave")
    val repetir = formulario.valor("repetir")
    val avisos = formulario.valor("avisos").toBoolean()
    val context = androidx.compose.ui.platform.LocalContext.current
    Pagina {
        Text(if (registro) "Tu próxima compra empieza aquí" else u?.rol.orEmpty(), style = MaterialTheme.typography.titleLarge)
        if (!registro && u != null) {
            Foto(u.avatar, u.nombre, Modifier.size(140.dp).align(Alignment.CenterHorizontally))
            BotonFoto(vm::guardarAvatar, vm::mensaje)
        }
        CampoFormularioTexto("Nombre", "nombre", s, vm)
        CampoFormularioTexto("Alias", "alias", s, vm)
        CampoFormularioTexto("Correo", "correo", s, vm)
        CampoFormularioTexto("Teléfono", "telefono", s, vm)
        CampoFormularioTexto("Dirección", "direccion", s, vm)
        if (registro) {
            CampoFormularioTexto("Contraseña", "clave", s, vm, clave = true)
            CampoFormularioTexto("Repetir contraseña", "repetir", s, vm, clave = true)
        } else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Avisos de la aplicación")
            Switch(checked = avisos, onCheckedChange = { vm.cambiarCampo("avisos", it.toString()) })
        }
        Button(onClick = {
            if (registro) vm.registrar(nombre, alias, correo, telefono, direccion, clave, repetir)
            else vm.guardarPerfil(nombre, alias, correo, telefono, direccion, avisos)
        }, enabled = !s.ocupado, modifier = Modifier.fillMaxWidth()) { Text(if (registro) "Crear mi cuenta" else "Guardar cambios") }
        if (!registro) {
            OutlinedButton(onClick = {
                context.startActivity(android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:${context.packageName}")))
            }) { Text("Permisos del dispositivo") }
            TextButton(onClick = vm::salir) { Text("Cerrar sesión") }
        }
    }
}
