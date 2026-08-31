package com.backendSupermercado.supermercasdo.config.data;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;
import com.backendSupermercado.supermercasdo.modules.almacen.entity.TipoAlmacen;
import com.backendSupermercado.supermercasdo.modules.almacen.repository.AlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.almacen.repository.TipoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.EstadoDevolucion;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.MotivoDevolucion;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.EstadoDevolucionRepository;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.MotivoDevolucionRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.NaturalezaMovimiento;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.TipoMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.TipoMovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.pedidos.entity.EstadoPedido;
import com.backendSupermercado.supermercasdo.modules.pedidos.repository.EstadoPedidoRepository;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.EstadoRecepcion;
import com.backendSupermercado.supermercasdo.modules.recepcion.repository.EstadoRecepcionRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.TipoUbicacion;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.UbicacionAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.TipoUbicacionRepository;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.EstadoTransferencia;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.repository.EstadoTransferenciaRepository;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.TipoUnidadMedida;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.repository.TipoUnidadMedidaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TipoUnidadMedidaRepository tipoUnidadMedidaRepository;
    private final EstadoRecepcionRepository estadoRecepcionRepository;
    private final TipoAlmacenRepository tipoAlmacenRepository;
    private final AlmacenRepository almacenRepository;
    private final UbicacionAlmacenRepository ubicacionAlmacenRepository;
    private final TipoUbicacionRepository tipoUbicacionRepository;
    private final TipoMovimientoAlmacenRepository tipoMovimientoAlmacenRepository;
    private final MotivoDevolucionRepository motivoDevolucionRepository;
    private final EstadoDevolucionRepository estadoDevolucionRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final EstadoTransferenciaRepository estadoTransferenciaRepository;

    @Override
    public void run(String... args) {
        seedTiposUnidadMedida();
        seedEstadosPedido();
        seedEstadosRecepcion();
        seedAlmacenes();
        seedTiposUbicacion();
        seedTiposMovimientoAlmacen();
        seedMotivosDevolucion();
        seedEstadosDevolucion();
        seedEstadosTransferencia();
    }

    private void seedEstadosTransferencia() {
        List<EstadoTransferencia> estados = List.of(
                EstadoTransferencia.builder().nombre("BORRADOR").descripcion("Transferencia guardada incompleta").build(),
                EstadoTransferencia.builder().nombre("PENDIENTE").descripcion("Transferencia lista para procesar").build(),
                EstadoTransferencia.builder().nombre("EN_PROCESO").descripcion("Transferencia en ejecución").build(),
                EstadoTransferencia.builder().nombre("COMPLETADA").descripcion("Transferencia finalizada correctamente").build(),
                EstadoTransferencia.builder().nombre("CANCELADA").descripcion("Transferencia anulada").build());
        estados.forEach(estado -> {
            if (!estadoTransferenciaRepository.existsByNombreIgnoreCase(estado.getNombre())) {
                estadoTransferenciaRepository.save(estado);
            }
        });
    }

    private void seedTiposUbicacion() {
        List<TipoUbicacion> tipos = List.of(
                TipoUbicacion.builder().nombre("ZONA").descripcion("Área amplia del almacén").build(),
                TipoUbicacion.builder().nombre("PASILLO").descripcion("Corredor de almacenamiento").build(),
                TipoUbicacion.builder().nombre("ESTANTE").descripcion("Espacio específico para productos").build(),
                TipoUbicacion.builder().nombre("CAMARA_FRIA").descripcion("Área refrigerada").build(),
                TipoUbicacion.builder().nombre("SECTOR").descripcion("Área destinada a una categoría").build());
        tipos.forEach(tipo -> {
            if (!tipoUbicacionRepository.existsByNombreIgnoreCase(tipo.getNombre())) {
                tipoUbicacionRepository.save(tipo);
            }
        });
    }

    private void seedTiposUnidadMedida() {
        List<String> tipos = List.of("Peso", "Volumen", "Conteo", "Lineal");
        tipos.forEach(nombre -> {
            if (!tipoUnidadMedidaRepository.existsByNombreIgnoreCase(nombre)) {
                tipoUnidadMedidaRepository.save(TipoUnidadMedida.builder().nombre(nombre).build());
            }
        });
    }

    private void seedEstadosPedido() {
        List<EstadoPedido> estados = List.of(
                EstadoPedido.builder().nombre("BORRADOR").descripcion("Pedido en edición, aún no enviado").build(),
                EstadoPedido.builder().nombre("ENVIADO").descripcion("Pedido confirmado y enviado al proveedor").build(),
                EstadoPedido.builder().nombre("PARCIALMENTE_RECIBIDO").descripcion("Se recibió parte de la mercadería").build(),
                EstadoPedido.builder().nombre("COMPLETADO").descripcion("Se recibió toda la mercadería").build(),
                EstadoPedido.builder().nombre("CANCELADO").descripcion("Pedido cancelado").build());
        estados.forEach(estado -> {
            if (!estadoPedidoRepository.existsByNombreIgnoreCase(estado.getNombre())) {
                estadoPedidoRepository.save(estado);
            }
        });
    }

    private void seedEstadosRecepcion() {
        List<EstadoRecepcion> estados = List.of(
                EstadoRecepcion.builder().nombre("EN_REVISION").descripcion("Recepción en proceso de registro").build(),
                EstadoRecepcion.builder().nombre("PARCIAL").descripcion("Recibida parcialmente la mercadería").build(),
                EstadoRecepcion.builder().nombre("COMPLETADA").descripcion("Recibida toda la mercadería").build(),
                EstadoRecepcion.builder().nombre("RECHAZADA").descripcion("Recepción rechazada").build(),
                EstadoRecepcion.builder().nombre("ANULADA").descripcion("Recepción anulada").build());
        estados.forEach(estado -> {
            if (!estadoRecepcionRepository.existsByNombreIgnoreCase(estado.getNombre())) {
                estadoRecepcionRepository.save(estado);
            }
        });
    }

    private void seedAlmacenes() {
        TipoAlmacen almacenTipo = tipoAlmacenRepository.findByNombreIgnoreCase("ALMACEN")
                .orElseGet(() -> tipoAlmacenRepository.save(
                        TipoAlmacen.builder().nombre("ALMACEN").descripcion("Bodega general de depósito").build()));
        TipoAlmacen salaTipo = tipoAlmacenRepository.findByNombreIgnoreCase("SALA_VENTAS")
                .orElseGet(() -> tipoAlmacenRepository.save(
                        TipoAlmacen.builder().nombre("SALA_VENTAS").descripcion("Piso de venta al público").build()));

        if (!almacenRepository.existsByNombreIgnoreCase("Almacén Central")) {
            Almacen almacen = almacenRepository.save(Almacen.builder()
                    .tipoAlmacen(almacenTipo)
                    .codigo("ALM-001")
                    .nombre("Almacén Central")
                    .descripcion("Bodega principal de la sucursal")
                    .build());
            seedUbicaciones(almacen, List.of(
                    new String[]{"A-01", "Estantería A1", "Zona perecederos"},
                    new String[]{"A-02", "Estantería A2", "Zona no perecederos"},
                    new String[]{"B-01", "Zona refrigerada", "Productos con frío"}));
        }

        if (!almacenRepository.existsByNombreIgnoreCase("Sala de Ventas")) {
            Almacen sala = almacenRepository.save(Almacen.builder()
                    .tipoAlmacen(salaTipo)
                    .codigo("SLV-001")
                    .nombre("Sala de Ventas")
                    .descripcion("Exhibición y venta al público")
                    .build());
            seedUbicaciones(sala, List.of(
                    new String[]{"V-01", "Mostrador 1", "Piso de venta mostrador 1"},
                    new String[]{"V-02", "Mostrador 2", "Piso de venta mostrador 2"}));
        }
    }

    private void seedUbicaciones(Almacen almacen, List<String[]> ubicaciones) {
        ubicaciones.forEach(u -> {
            if (!ubicacionAlmacenRepository.existsByAlmacenAndCodigo(almacen, u[0])) {
                ubicacionAlmacenRepository.save(UbicacionAlmacen.builder()
                        .almacen(almacen)
                        .codigo(u[0])
                        .nombre(u[1])
                        .descripcion(u[2])
                        .build());
            }
        });
    }

    private void seedTiposMovimientoAlmacen() {
        List<TipoMovimientoAlmacen> tipos = List.of(
                TipoMovimientoAlmacen.builder().nombre("ENTRADA_COMPRA")
                        .descripcion("Entrada de mercadería por compra/recepción").naturaleza(NaturalezaMovimiento.E)
                        .build(),
                TipoMovimientoAlmacen.builder().nombre("SALIDA_VENTA")
                        .descripcion("Salida por venta al cliente").naturaleza(NaturalezaMovimiento.S).build(),
                TipoMovimientoAlmacen.builder().nombre("SALIDA_DEVOLUCION")
                        .descripcion("Salida por devolución al proveedor").naturaleza(NaturalezaMovimiento.S).build(),
                TipoMovimientoAlmacen.builder().nombre("AJUSTE_INVENTARIO")
                        .descripcion("Ajuste por inventario físico").naturaleza(NaturalezaMovimiento.E).build());
        tipos.forEach(tipo -> {
            if (!tipoMovimientoAlmacenRepository.existsByNombreIgnoreCase(tipo.getNombre())) {
                tipoMovimientoAlmacenRepository.save(tipo);
            }
        });
    }

    private void seedMotivosDevolucion() {
        List<MotivoDevolucion> motivos = List.of(
                MotivoDevolucion.builder().nombre("PRODUCTO_DAÑADO").descripcion("Producto con daño físico o empaque roto").build(),
                MotivoDevolucion.builder().nombre("PRODUCTO_VENCIDO").descripcion("Producto vencido o por vencer").build(),
                MotivoDevolucion.builder().nombre("PEDIDO_INCOMPLETO").descripcion("Cantidad recibida menor a la solicitada").build(),
                MotivoDevolucion.builder().nombre("PRODUCTO_EQUIVOCADO").descripcion("Producto no solicitado o distinto al pedido").build(),
                MotivoDevolucion.builder().nombre("FALTA_CALIDAD").descripcion("No cumple las condiciones de calidad acordadas").build());
        motivos.forEach(motivo -> {
            if (!motivoDevolucionRepository.existsByNombreIgnoreCase(motivo.getNombre())) {
                motivoDevolucionRepository.save(motivo);
            }
        });
    }

    private void seedEstadosDevolucion() {
        List<EstadoDevolucion> estados = List.of(
                EstadoDevolucion.builder().nombre("BORRADOR").descripcion("Devolución en elaboración, sin afectar inventario").build(),
                EstadoDevolucion.builder().nombre("CONFIRMADA").descripcion("Devolución confirmada y descontada del inventario").build(),
                EstadoDevolucion.builder().nombre("ENVIADA").descripcion("Mercadería enviada al proveedor").build(),
                EstadoDevolucion.builder().nombre("EN_PROCESO").descripcion("Devolución registrada, pendiente de gestionar").build(),
                EstadoDevolucion.builder().nombre("COMPLETADA").descripcion("Devolución concretada con el proveedor").build(),
                EstadoDevolucion.builder().nombre("CANCELADA").descripcion("Devolución anulada").build());
        estados.forEach(estado -> {
            if (!estadoDevolucionRepository.existsByNombreIgnoreCase(estado.getNombre())) {
                estadoDevolucionRepository.save(estado);
            }
        });
    }
}
