#!/usr/bin/env bash

set -Eeuo pipefail

# ==========================================
# MiInventario - Herramientas de desarrollo
# Compatible con Linux, macOS y Windows
# mediante Git Bash
# ==========================================

# Manejo general de errores
on_error() {
    local exit_code=$?
    local line_number="$1"

    echo
    echo "ERROR: La operación no pudo completarse."
    echo "Código de salida: $exit_code"
    echo "Línea del script: $line_number"
    echo "Revisa los mensajes anteriores para identificar la causa."
}

trap 'on_error "$LINENO"' ERR

# Obtener la raíz del proyecto
PROJECT_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

# Detectar sistema operativo
case "${OSTYPE:-}" in
    msys*|cygwin*)
        GRADLE="./gradlew.bat"
        ;;
    *)
        GRADLE="./gradlew"
        ;;
esac

APK="androidApp/build/outputs/apk/debug/androidApp-debug.apk"

# Verificar Gradle Wrapper
if [[ ! -f "$GRADLE" ]]; then
    echo "ERROR: No se encontró $GRADLE"
    exit 1
fi

# ------------------------------------------
# 1. Ejecutar pruebas automáticas
# ------------------------------------------
run_tests() {
    echo
    echo "Ejecutando pruebas automáticas..."
    "$GRADLE" :shared:jvmTest
    echo "Pruebas finalizadas correctamente."
}

# ------------------------------------------
# 2. Verificación general del proyecto
# ------------------------------------------
verify_project() {
    echo
    echo "Verificando MiInventario..."

    "$GRADLE" \
        :shared:jvmTest \
        :androidApp:assembleDebug \
        :desktopApp:compileKotlin

    echo "Verificación general completada."
}

# ------------------------------------------
# 3. Generar APK Android
# ------------------------------------------
build_apk() {
    echo
    echo "Generando APK Android..."

    "$GRADLE" :androidApp:assembleDebug

    if [[ ! -f "$APK" ]]; then
        echo "ERROR: No se encontró el APK generado."
        return 1
    fi

    echo "APK generado correctamente:"
    echo "$PROJECT_ROOT/$APK"
}

# ------------------------------------------
# 4. Ejecutar aplicación Desktop
# ------------------------------------------
run_desktop() {
    echo
    echo "Iniciando MiInventario Desktop..."
    "$GRADLE" :desktopApp:run
}

# ------------------------------------------
# 5. Actualizar APK en celular Android
# ------------------------------------------
install_android() {
    echo
    echo "Preparando instalación en Android..."

    # Comprobar disponibilidad de ADB
    if ! command -v adb >/dev/null 2>&1; then
        echo "ERROR: ADB no está instalado o no está en PATH."
        echo "Instala Android SDK Platform-Tools."
        return 1
    fi

    echo
    echo "Dispositivos detectados:"
    adb devices

    # Contar dispositivos autorizados
    local device_count
    device_count="$(
        adb devices |
        awk '$2 == "device" {count++} END {print count+0}'
    )"

    if [[ "$device_count" -ne 1 ]]; then
        echo
        echo "ERROR: Se requiere exactamente un dispositivo autorizado."
        echo "Verifica el cable USB y la depuración USB."
        return 1
    fi

    # Compilar APK actualizado
    build_apk

    echo
    echo "Instalando APK en el celular..."
    adb install -r "$APK"

    echo
    echo "APK instalado correctamente."
    echo "Puedes abrir MiInventario en tu celular."
}

# ------------------------------------------
# Menú principal
# ------------------------------------------
while true; do
    echo
    echo "======================================"
    echo "      MiInventario - Desarrollo"
    echo "======================================"
    echo "1) Ejecutar pruebas automáticas"
    echo "2) Verificación general del proyecto"
    echo "3) Generar APK Android"
    echo "4) Ejecutar aplicación Desktop"
    echo "5) Actualizar APK en celular por USB"
    echo "0) Salir"
    echo

    read -r -p "Selecciona una opción: " option || break

    case "$option" in
        1)
            run_tests
            ;;
        2)
            verify_project
            ;;
        3)
            build_apk
            ;;
        4)
            run_desktop
            ;;
        5)
            install_android
            ;;
        0)
            echo "Hasta luego."
            break
            ;;
        *)
            echo "Opción no válida. Intenta nuevamente."
            ;;
    esac
done
