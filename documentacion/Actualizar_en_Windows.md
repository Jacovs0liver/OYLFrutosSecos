# Actualizar el proyecto existente

1. En Android Studio: File → Close Project.
2. Descarga el ZIP actualizado y usa Extraer todo en una carpeta temporal diferente de tu proyecto actual.
3. Dentro de lo extraído entra hasta ver app, gradle y settings.gradle.kts.
4. Copia únicamente app, documentacion y README.md a la carpeta de trabajo que ya abrías. En las capturas era C:\Users\jacov\Downloads\OYLFrutosSecos\OYLFrutosSecos. Acepta reemplazar archivos coincidentes. No copies otra carpeta OYLFrutosSecos dentro de ella.
5. Abre el proyecto existente. Conserva Gradle JDK 21, SDK Platform 36 y Build-Tools 36.0.0. Abre su terminal y ejecuta:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

6. Tras BUILD SUCCESSFUL conecta el Honor, selecciónalo junto a app y pulsa Run. No desinstales la app ni borres datos: así conserva usuarios, fotos, pedidos y stock.
7. Comprueba el login correcto e incorrecto con ambos roles, las validaciones por campo y las descripciones del catálogo. La limpieza de textos solo reemplaza las descripciones originales; no reescribe las personalizadas ni reinicia el stock.
8. Realiza el recorrido de Pruebas.md. Con el teléfono conectado puedes ejecutar además:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

Esto ejecuta pruebas de Room y QR con una base de prueba, sin borrar la base de la aplicación. Las pruebas instrumentadas todavía están pendientes de ejecución. No son una sustitución del recorrido manual de cámara, mapa y escáner.
