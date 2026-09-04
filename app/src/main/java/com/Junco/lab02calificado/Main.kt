package com.Junco

import java.util.Scanner

// Enum con tarifas base
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

// Data class con escala de recargos e IGV
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

    // Configuración de aforo máximo
    val aforoMaximo = 10

    println("=== REGISTRO DE VEHÍCULOS (AFORO MÁXIMO: $aforoMaximo) ===")

    while (listaVehiculos.size < aforoMaximo) {
        println("\nVehículos registrados: ${listaVehiculos.size}/$aforoMaximo")
        print("Ingrese Placa (o 'salir' para terminar): ")
        val placa = scanner.nextLine()
        if (placa.lowercase() == "salir") break

        print("Tipo (Moto, Auto, Camioneta, Trailer): ")
        val tipoInput = scanner.nextLine()
        val tipo = TipoVehiculo.desdeString(tipoInput)

        if (tipo == null) {
            println("Tipo inválido. Intente de nuevo.")
            continue
        }

        print("Horas estacionado: ")
        val horas = scanner.nextLine().toIntOrNull() ?: 0

        print("¿Es cliente frecuente? (si/no): ")
        val esFrecuente = scanner.nextLine().trim().lowercase() == "si"

        listaVehiculos.add(Vehiculo(placa, tipo, horas, esFrecuente))
    }

    if (listaVehiculos.size >= aforoMaximo) {
        println("\n⚠️ ¡SE HA ALCANZADO EL AFORO MÁXIMO PERMITIDO ($aforoMaximo VEHÍCULOS)! ⚠️")
    }

    if (listaVehiculos.isEmpty()) {
        println("No se registraron vehículos.")
        return
    }

    // --- REPORTE DE BOLETAS ---
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