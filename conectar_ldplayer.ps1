# Script para conectar LDPlayer a ADB
# Ejecuta este script como Administrador

Write-Host "🔧 Conectando LDPlayer a ADB..." -ForegroundColor Cyan

# Encontrar la ruta de ADB
$sdkPath = $env:LOCALAPPDATA + "\Android\Sdk\platform-tools"
$adbPath = Join-Path $sdkPath "adb.exe"

# Verificar si ADB existe
if (-not (Test-Path $adbPath)) {
    Write-Host "❌ ADB no encontrado en: $adbPath" -ForegroundColor Red
    Write-Host "Por favor, especifica la ruta manualmente o instala Android SDK" -ForegroundColor Yellow
    $customPath = Read-Host "Ingresa la ruta completa a adb.exe (o presiona Enter para salir)"
    if ($customPath -and (Test-Path $customPath)) {
        $adbPath = $customPath
    } else {
        exit
    }
}

Write-Host "✅ ADB encontrado en: $adbPath" -ForegroundColor Green

# Detener servidor ADB
Write-Host "`n🛑 Deteniendo servidor ADB..." -ForegroundColor Yellow
& $adbPath kill-server
Start-Sleep -Seconds 1

# Iniciar servidor ADB
Write-Host "🚀 Iniciando servidor ADB..." -ForegroundColor Yellow
& $adbPath start-server
Start-Sleep -Seconds 1

# Intentar conectar a diferentes puertos comunes de LDPlayer
$puertos = @(5555, 5037, 5554, 62001)

Write-Host "`n🔍 Intentando conectar a LDPlayer..." -ForegroundColor Cyan

foreach ($puerto in $puertos) {
    Write-Host "Intentando puerto $puerto..." -ForegroundColor Gray
    $resultado = & $adbPath connect "127.0.0.1:$puerto" 2>&1
    
    if ($resultado -match "connected" -or $resultado -match "already connected") {
        Write-Host "✅ Conectado exitosamente al puerto $puerto!" -ForegroundColor Green
        break
    }
}

# Verificar dispositivos conectados
Write-Host "`n📱 Dispositivos conectados:" -ForegroundColor Cyan
& $adbPath devices

Write-Host "`n✅ Proceso completado!" -ForegroundColor Green
Write-Host "Ahora puedes usar 'adb logcat' para ver los logs" -ForegroundColor Yellow
Write-Host "O abre Android Studio y verifica que LDPlayer aparezca en Logcat" -ForegroundColor Yellow

# Mantener la ventana abierta
Write-Host "`nPresiona cualquier tecla para salir..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

