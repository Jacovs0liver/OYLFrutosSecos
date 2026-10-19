> Estado actualizado: consulta `Revision_final.md`. Las verificaciones históricas de este documento no certifican la compilación de la revisión actual. La comparación directa de 101/102/106 está documentada en Comparacion_verificada.md.

# Pruebas antes de entregar

## Revisión del login con ejemplo 025

Se comparó el MainActivity.kt del 025 recibido y se adaptó su pantalla de campos y callbacks al estado del 102. Esta revisión mantiene el diseño sencillo. Se volvieron a ejecutar las seis pruebas puras de dominio con Kotlin 2.0.21 y JUnit 4.13.2: OK (6 tests). El análisis de los 26 archivos Kotlin/KTS no encontró errores de sintaxis ni comentarios; XML y TOML son válidos. Estas comprobaciones no son una compilación Android. Falta ejecutar testDebugUnitTest y assembleDebug de esta revisión en el equipo del estudiante y comprobar login/cámara/mapa/QR en el teléfono. Las pruebas instrumentadas siguen pendientes.

## Evidencia del equipo del estudiante

El 18 de octubre de 2026 las capturas mostraron BUILD SUCCESSFUL para assembleDebug y testDebugUnitTest. Una captura posterior mostró la bienvenida en el teléfono. El recorrido completo aún no está verificado.

Esta revisión elimina etiquetas académicas de la interfaz y limpia las descripciones originales ya guardadas, conservando modificaciones del usuario. Mantiene el aviso breve de pago simulado. Estos cambios requieren recompilar y comprobar la actualización sin desinstalar.

## Comprobaciones anteriores en este entorno

La compilación Android se intentó en modo offline. Se detuvo al resolver el plugin Android 8.13.0 porque este entorno no tiene las dependencias de Google almacenadas ni un SDK Android instalado. Esto no demuestra que la aplicación compile o funcione. Las pruebas instrumentadas y las pruebas manuales deben ejecutarse en Android Studio y en un dispositivo.

Las seis pruebas de `ValidacionesTest` sí se ejecutaron con JUnit 4.13.2: `OK (6 tests)`. Se compilaron las clases puras de dominio con el compilador Kotlin 2.0.21 incluido en Gradle; esto no sustituye la compilación completa con Kotlin 2.2.10 y el SDK Android.

Después de estas correcciones se analizaron 26 archivos Kotlin y scripts Kotlin sin errores de sintaxis. También se interpretaron los XML y TOML. Estas comprobaciones no resuelven tipos ni APIs de Android/Compose y no equivalen a una compilación Android aprobada. La alineación con los ejemplos originales sigue en revisión.

## Recorrido de comprobación

1. Abrir la app sin sesión: comprobar bienvenida y animación.
2. Registrar un cliente A con datos ficticios. Probar antes campos vacíos, correo inválido y claves distintas; cada error debe aparecer en su campo.
3. Cerrar sesión, probar clave incorrecta y luego ingresar correctamente.
4. Buscar y filtrar catálogo; revisar nombre, categoría, precio, presentación y stock.
5. Agregar productos, cambiar cantidades, quitar un artículo y cerrar la app. Al volver, sesión y carrito deben conservarse.
6. Crear cliente B: su carrito e historial deben estar vacíos. Volver al cliente A y comprobar que conserva los suyos.
7. Continuar al pago de demostración y simular primero un rechazo: no debe crearse pedido ni cambiar el carrito o stock. Reintentar con pago aprobado: guardar el número, revisar líneas y total, comprobar que su carrito se vacía.
8. Ingresar como admin y comprobar la rebaja exacta del stock. Avanzar el estado a Confirmado, Preparado y Entregado; el stock no debe rebajarse otra vez.
9. Crear producto con precio inválido y después corregirlo. Tomar una foto y guardarla. Reiniciar la app y comprobar la imagen.
10. Editar precio y presentación. Desactivar y reactivar el producto; un cliente no debe ver artículos inactivos.
11. Registrar entrada y salida de stock con motivo. Una salida excesiva debe rechazarse sin alterar existencias ni historial.
12. Generar un QR externo en modo Texto con uno de los objetos de `qr_productos.json`, como hace el profesor en clase 063. Leerlo y comprobar que muestra nombre, descripción y precio. Un cliente abre la ficha y admin abre inventario mediante el botón del resultado. Probar texto inválido, JSON incompleto y un producto no registrado.
13. Abrir mapa, tocar marcadores y consultar horario. Probar ubicación concedida, aproximada y denegada. Denegar no debe impedir ver las tiendas de prueba.
14. Cambiar avatar y perfil, reiniciar y verificar persistencia. Desactivar avisos y confirmar que no se crean nuevos para ese perfil.
15. Ver pedidos con cliente B: no debe mostrar pedidos ni líneas de A. Revisar avisos por usuario.
16. Probar Atrás, rotación, teclado abierto y pantalla pequeña. Los filtros deben poder desplazarse y los formularios deben poder recorrerse completos.

## Dos compradores y stock insuficiente

Dejar 3 unidades de un producto. Colocar 2 unidades en el carrito de A y 2 en el de B. Confirmar A: deben quedar 1. Confirmar B: debe rechazarse, conservar su carrito y no registrar un pedido parcial.

## Comandos

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat assembleDebug
```

Registrar los resultados reales y los errores antes de preparar la entrega. No marcar como aprobada una prueba que no se haya ejecutado.
