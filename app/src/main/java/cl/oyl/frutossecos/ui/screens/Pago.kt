package cl.oyl.frutossecos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.oyl.frutossecos.ui.*
import cl.oyl.frutossecos.ui.components.*

@Composable
fun Pago(s: EstadoOyl, vm: OylViewModel) {
    Pagina {
        Text("Finaliza tu pedido", style = MaterialTheme.typography.headlineSmall)
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Total del pedido", style = MaterialTheme.typography.titleMedium)
                Text(pesos(s.total), style = MaterialTheme.typography.headlineMedium)
                Text("Compra simulada: no se realizará ningún cobro.", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text("Confirma para registrar tu pedido y consultar su estado en Mis pedidos.")
        Button(onClick = vm::comprar, enabled = !s.ocupado && s.carrito.isNotEmpty(), modifier = Modifier.fillMaxWidth()) {
            Text("Confirmar pedido")
        }
        OutlinedButton(onClick = { vm.abrir(Ruta.PagoFallido) }, enabled = !s.ocupado, modifier = Modifier.fillMaxWidth()) {
            Text("Ver ejemplo de pago rechazado")
        }
    }
}

@Composable
fun PagoExitoso(s: EstadoOyl, vm: OylViewModel) {
    Pagina {
        Exito("Pedido recibido", "Pedido #${s.pedidoCreado} recibido. Estado: Pendiente de Validación.")
        Text("Compra simulada. No se realizó ningún cobro.")
        Button(onClick = { vm.abrir(Ruta.Pedidos) }, modifier = Modifier.fillMaxWidth()) { Text("Ver mis pedidos") }
    }
}

@Composable
fun PagoFallido(vm: OylViewModel) {
    Pagina {
        Text("No se pudo completar el pago", style = MaterialTheme.typography.headlineMedium)
        Text("Este es un ejemplo de pago rechazado. No se realizó ningún cobro y tus productos siguen en el carrito.")
        Button(onClick = { vm.abrir(Ruta.Pago) }, modifier = Modifier.fillMaxWidth()) { Text("Volver a intentar") }
        OutlinedButton(onClick = { vm.abrir(Ruta.Carrito) }) { Text("Volver a mi carrito") }
    }
}
