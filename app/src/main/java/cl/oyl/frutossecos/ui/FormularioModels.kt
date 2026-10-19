package cl.oyl.frutossecos.ui

data class CampoFormulario(
    val valor: String = "",
    val error: String? = null
) {
    val esValido: Boolean get() = error == null
}

data class FormularioUiState(
    val tipo: Ruta? = null,
    val campos: Map<String, CampoFormulario> = emptyMap(),
    val intentoEnviar: Boolean = false
) {
    fun campo(nombre: String): CampoFormulario = campos[nombre] ?: CampoFormulario()
    fun valor(nombre: String): String = campo(nombre).valor
    fun conErrores(errores: Map<String, String>): FormularioUiState = copy(
        intentoEnviar = true,
        campos = campos.mapValues { (nombre, campo) -> campo.copy(error = errores[nombre]) }
    )
}
