package com.Junco

import java.util.Scanner

enum class TipoVehiculo(val descripcion: String, val tarifaBase: Double) {
    MOTO("Moto", 2.0),
    AUTO("Auto", 4.0),
    CAMIONETA("Camioneta", 10.0),
    TRAILER("Trailer", 20.0);

    companion object {
        fun desdeString(input: String): TipoVehiculo? {
            return entries.find { it.descripcion.equals(input.trim(), ignoreCase = true) }
        }
    }
}

data class Vehiculo(
    val placa: String,
    val tipo: TipoVehiculo,
    val horasEstacionado: Int,
    val esClienteFrecuente: Boolean
) {
    val subtotal: Double
        get() {
            var total = 0.0
            val base = tipo.tarifaBase

            for (hora in 1..horasEstacionado) {
                total += when {
                    hora <= 2 -> base                  // 0 a 2 hrs: tarifa base
                    hora in 3..5 -> base * 1.20        // 3 a 5 hrs: +20%
                    hora in 6..10 -> base * 1.40       // 6 a 10 hrs: +40%
                    else -> base * 1.50                // 11 a más hrs: +50%
                }
            }
            return total
        }

    val descuento: Double
        get() = if (esClienteFrecuente) subtotal * 0.10 else 0.0

    val igv: Double
        get() = (subtotal - descuento) * 0.18

    val totalPagar: Double
        get() = (subtotal - descuento) + igv
}

fun main() {
    val scanner = Scanner(System.`in`)
    val listaVehiculos = mutableListOf<Vehiculo>()

    // ==========================================
    // PARTE 1: CONFIGURACIÓN DE AFORO
    // ==========================================
    println("==========================================")
    println("      PARTE 1: CONFIGURACIÓN DE AFORO    ")
    println("==========================================")

    var aforoIngresado = 0
    val limiteMinimoAforo = 1

    while (true) {
        print("Ingrese el aforo del estacionamiento (Mínimo $limiteMinimoAforo): ")
        val input = scanner.nextLine().toIntOrNull()

        if (input != null && input >= limiteMinimoAforo) {
            aforoIngresado = input
            println("✓ Aforo guardado correctamente: $aforoIngresado espacios.")
            break
        } else {
            println("⚠️ Error: El aforo debe ser un número entero mayor o igual a $limiteMinimoAforo.")
        }
    }

    // ==========================================
    // PARTE 2: REGISTRO DE VEHÍCULOS
    // ==========================================
    println("\n==========================================")
    println("     PARTE 2: REGISTRO DE VEHÍCULOS       ")
    println("==========================================")

    val maxHorasPermitidas = 24

    while (listaVehiculos.size < aforoIngresado) {
        println("\n--- Vehículo ${listaVehiculos.size + 1} de $aforoIngresado ---")
        print("Ingrese Placa (o 'salir' para terminar el registro): ")
        val placa = scanner.nextLine()
        if (placa.lowercase() == "salir") break

        print("Tipo (Moto, Auto, Camioneta, Trailer): ")
        val tipoInput = scanner.nextLine()
        val tipo = TipoVehiculo.desdeString(tipoInput)

        if (tipo == null) {
            println("⚠️ Tipo de vehículo inválido. Intente de nuevo.")
            continue
        }

        var horas = 0
        while (true) {
            print("Horas estacionado (1 - $maxHorasPermitidas hrs): ")
            val inputHoras = scanner.nextLine().toIntOrNull()

            if (inputHoras != null && inputHoras in 1..maxHorasPermitidas) {
                horas = inputHoras
                break
            } else {
                println("⚠️ Error: Las horas deben estar entre 1 y $maxHorasPermitidas horas.")
            }
        }

        print("¿Es cliente frecuente? (si/no): ")
        val esFrecuente = scanner.nextLine().trim().lowercase() == "si"

        listaVehiculos.add(Vehiculo(placa, tipo, horas, esFrecuente))
        println("✓ Vehículo registrado con éxito.")
    }

    if (listaVehiculos.size >= aforoIngresado) {
        println("\n⚠️ ¡SE HA ALCANZADO EL AFORO MÁXIMO DEL ESTACIONAMIENTO ($aforoIngresado VEHÍCULOS)! ⚠️")
    }

    if (listaVehiculos.isEmpty()) {
        println("No se registraron vehículos.")
        return
    }

    // --- REPORTE FINAL ---
    println("\n========================================================================================")
    println("                             RESUMEN DE COBRO DE BOLETAS                                ")
    println("========================================================================================")
    println(String.format("%-10s | %-10s | %-5s | %-10s | %-10s | %-8s | %-10s", "PLACA", "TIPO", "HORAS", "SUBTOTAL", "DESCUENTO", "IGV", "TOTAL"))
    println("----------------------------------------------------------------------------------------")

    var granTotal = 0.0
    for (v in listaVehiculos) {
        println(
            String.format(
                "%-10s | %-10s | %-5d | S/ %-7.2f | S/ %-8.2f | S/ %-6.2f | S/ %-8.2f",
                v.placa, v.tipo.descripcion, v.horasEstacionado, v.subtotal, v.descuento, v.igv, v.totalPagar
            )
        )
        granTotal += v.totalPagar
    }

    println("========================================================================================")
    println(String.format("GRAN TOTAL RECAUDADO: S/ %.2f", granTotal))
    println("========================================================================================")

    // --- ESTADÍSTICAS ---
    val mayorPago = listaVehiculos.maxByOrNull { it.totalPagar }
    val masHoras = listaVehiculos.maxByOrNull { it.horasEstacionado }
    val promedio = listaVehiculos.map { it.totalPagar }.average()

    val motos = listaVehiculos.count { it.tipo == TipoVehiculo.MOTO }
    val autos = listaVehiculos.count { it.tipo == TipoVehiculo.AUTO }
    val camionetas = listaVehiculos.count { it.tipo == TipoVehiculo.CAMIONETA }
    val trailers = listaVehiculos.count { it.tipo == TipoVehiculo.TRAILER }

    println("\n==========================================================================")
    println("                        ESTADÍSTICAS Y RESULTADOS                         ")
    println("==========================================================================")
    println("◆ Vehículo con mayor pago   : Placa ${mayorPago?.placa} (${mayorPago?.tipo?.descripcion}) - Total: S/ ${String.format("%.2f", mayorPago?.totalPagar)}")
    println("◆ Vehículo con más horas    : Placa ${masHoras?.placa} (${masHoras?.tipo?.descripcion}) - ${masHoras?.horasEstacionado} horas")
    println("◆ Promedio de cobro por vehículo: S/ ${String.format("%.2f", promedio)}")
    println("\n--- Conteo de Vehículos por Tipo ---")
    println("◆ Moto      : $motos")
    println("◆ Auto      : $autos")
    println("◆ Camioneta : $camionetas")
    println("◆ Trailer   : $trailers")
}
