package com.backendSupermercado.supermercasdo.modules.pedidos.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoDetalleListadoDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoListadoDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto.DetallePdfDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto.EmpresaDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto.PedidoPdfInnerDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto.ProveedorPdfDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoRequestDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.entity.DetallePedidoProveedor;
import com.backendSupermercado.supermercasdo.modules.pedidos.entity.EstadoPedido;
import com.backendSupermercado.supermercasdo.modules.pedidos.entity.PedidoProveedor;
import com.backendSupermercado.supermercasdo.modules.pedidos.repository.DetallePedidoProveedorRepository;
import com.backendSupermercado.supermercasdo.modules.pedidos.repository.EstadoPedidoRepository;
import com.backendSupermercado.supermercasdo.modules.pedidos.repository.PedidoRepository;
import com.backendSupermercado.supermercasdo.modules.pedidos.service.PedidoPdfService;
import com.backendSupermercado.supermercasdo.modules.pedidos.service.PedidoService;
import com.backendSupermercado.supermercasdo.modules.productos.entity.PresentacionProducto;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.productos.repository.PresentacionProductoRepository;
import com.backendSupermercado.supermercasdo.modules.productos.repository.ProductoRepository;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.ContactoEmpresa;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.DireccionEmpresa;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Empresa;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Proveedor;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.TipoProveedor;
import com.backendSupermercado.supermercasdo.modules.proveedor.repository.ProveedorRepository;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.UnidadMedida;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.repository.UnidadMedidaRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.shared.specification.PedidoSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private static final Set<String> ESTADOS_CREACION = Set.of("BORRADOR", "ENVIADO");
    private static final Set<String> ESTADOS_CANCELABLES = Set.of("BORRADOR", "ENVIADO");

    private final PedidoRepository pedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final DetallePedidoProveedorRepository detallePedidoProveedorRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final PresentacionProductoRepository presentacionProductoRepository;
    private final PedidoPdfService pedidoPdfService;

    @Value("${app.empresa.nombre}")
    private String empresaNombre;

    @Value("${app.empresa.nit}")
    private String empresaNit;

    @Value("${app.empresa.direccion}")
    private String empresaDireccion;

    @Value("${app.empresa.telefono}")
    private String empresaTelefono;

    @Value("${app.empresa.ciudad}")
    private String empresaCiudad;

    @Value("${app.empresa.logo}")
    private String empresaLogo;

    @Override
    @Transactional
    public PedidoListadoDto crear(Long idUsuario, PedidoRequestDto dto) {
        validarCamposRequeridos(dto);

        EstadoPedido estadoPedido = resolverEstado(dto.getEstado());

        Proveedor proveedor = proveedorRepository.findById(dto.getIdProveedor())
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con id: " + dto.getIdProveedor()));
        if (Boolean.FALSE.equals(proveedor.getEstado())) {
            throw new IllegalArgumentException("El proveedor no está activo");
        }

        validarPedidoActivoPorProveedor(dto.getIdProveedor(), null);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + idUsuario));

        validarFechaEntrega(dto.getFechaEntregaEstimada());

        PedidoProveedor pedido = PedidoProveedor.builder()
                .proveedor(proveedor)
                .usuario(usuario)
                .fechaPedido(LocalDateTime.now())
                .fechaEntregaEstimada(dto.getFechaEntregaEstimada())
                .observacion(dto.getObservacion())
                .estadoPedido(estadoPedido)
                .detalles(new ArrayList<>())
                .recepciones(new ArrayList<>())
                .build();

        List<DetallePedidoProveedor> detalles = mapearDetalles(dto, pedido);
        pedido.getDetalles().addAll(detalles);

        pedido = pedidoRepository.save(pedido);
        return toListadoDto(pedido);
    }

    @Override
    @Transactional
    public PedidoListadoDto actualizar(Long id, Long idUsuario, PedidoRequestDto dto) {
        PedidoProveedor pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + id));

        String estadoActual = pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombre() : null;
        if (!"BORRADOR".equals(estadoActual)) {
            throw new ResourceConflictException("Solo se pueden editar pedidos en estado BORRADOR");
        }

        validarCamposRequeridos(dto);
        validarFechaEntrega(dto.getFechaEntregaEstimada());

        EstadoPedido estadoPedido = resolverEstado(dto.getEstado());

        Proveedor proveedor = proveedorRepository.findById(dto.getIdProveedor())
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado con id: " + dto.getIdProveedor()));
        if (Boolean.FALSE.equals(proveedor.getEstado())) {
            throw new IllegalArgumentException("El proveedor no está activo");
        }

        validarPedidoActivoPorProveedor(dto.getIdProveedor(), id);

        pedido.setProveedor(proveedor);
        pedido.setFechaEntregaEstimada(dto.getFechaEntregaEstimada());
        pedido.setObservacion(dto.getObservacion());
        pedido.setEstadoPedido(estadoPedido);

        detallePedidoProveedorRepository.deleteByPedidoId(pedido.getIdPedido());
        pedido.getDetalles().clear();
        pedido.getDetalles().addAll(mapearDetalles(dto, pedido));

        pedido = pedidoRepository.save(pedido);
        return toListadoDto(pedido);
    }

    @Override
    @Transactional
    public PedidoListadoDto enviar(Long id) {
        PedidoProveedor pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + id));

        String estadoActual = pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombre() : null;
        if (!"BORRADOR".equals(estadoActual)) {
            throw new ResourceConflictException("Solo se puede enviar un pedido en estado BORRADOR");
        }

        EstadoPedido enviado = estadoPedidoRepository.findByNombreIgnoreCase("ENVIADO")
                .orElseThrow(() -> new EntityNotFoundException("Estado de pedido no encontrado: ENVIADO"));
        pedido.setEstadoPedido(enviado);
        pedido = pedidoRepository.save(pedido);
        return toListadoDto(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoListadoDto obtenerPorId(Long id) {
        PedidoProveedor pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + id));
        return toListadoDto(pedido);
    }

    @Override
    @Transactional
    public PedidoListadoDto cancelar(Long id) {
        PedidoProveedor pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + id));

        String estadoActual = pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombre() : null;
        if (!ESTADOS_CANCELABLES.contains(estadoActual)) {
            throw new ResourceConflictException("No se puede cancelar un pedido en estado " + estadoActual);
        }

        EstadoPedido cancelado = estadoPedidoRepository.findByNombreIgnoreCase("CANCELADO")
                .orElseThrow(() -> new EntityNotFoundException("Estado de pedido no encontrado: CANCELADO"));
        pedido.setEstadoPedido(cancelado);
        pedido = pedidoRepository.save(pedido);
        return toListadoDto(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoListadoDto> listar(String estado, String search, Long idProveedor,
            LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable) {
        Specification<PedidoProveedor> spec = Specification
                .where(PedidoSpecification.estadoEqual(estado))
                .and(PedidoSpecification.busquedaGeneral(search))
                .and(PedidoSpecification.proveedorEqual(idProveedor))
                .and(PedidoSpecification.fechaDesde(fechaDesde))
                .and(PedidoSpecification.fechaHasta(fechaHasta));
        return pedidoRepository.findAll(spec, pageable)
                .map(this::toListadoDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return pedidoRepository.count();
        }
        return pedidoRepository.count(
                Specification.where(PedidoSpecification.estadoEqual(estado)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarEstados() {
        return estadoPedidoRepository.findAll().stream()
                .filter(EstadoPedido::getActivo)
                .map(EstadoPedido::getNombre)
                .sorted()
                .toList();
    }

    private void validarCamposRequeridos(PedidoRequestDto dto) {
        if (dto.getIdProveedor() == null) {
            throw new IllegalArgumentException("El proveedor es obligatorio");
        }
        if (dto.getEstado() == null || dto.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un producto en el pedido");
        }
    }

    private EstadoPedido resolverEstado(String estado) {
        String estadoNormalizado = estado.trim().toUpperCase();
        if (!ESTADOS_CREACION.contains(estadoNormalizado)) {
            throw new IllegalArgumentException(
                    "Estado inválido: " + estado + ". Valores permitidos al crear: BORRADOR, ENVIADO");
        }
        return estadoPedidoRepository.findByNombreIgnoreCase(estadoNormalizado)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Estado de pedido no encontrado: " + estadoNormalizado));
    }

    private void validarPedidoActivoPorProveedor(Long idProveedor, Long idPedidoExcluido) {
        long activos = pedidoRepository.countActivosPorProveedor(idProveedor, idPedidoExcluido);
        if (activos > 0) {
            throw new ResourceConflictException(
                    "El proveedor ya tiene un pedido activo (BORRADOR o ENVIADO). Debe completarlo, recibirlo o cancelarlo antes de crear otro.");
        }
    }

    private void validarFechaEntrega(LocalDate fechaEntregaEstimada) {
        if (fechaEntregaEstimada == null) {
            throw new IllegalArgumentException("La fecha de entrega estimada es obligatoria");
        }
        if (fechaEntregaEstimada.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de entrega estimada no puede ser anterior a hoy");
        }
    }

    private List<DetallePedidoProveedor> mapearDetalles(PedidoRequestDto dto, PedidoProveedor pedido) {
        List<DetallePedidoProveedor> detalles = new ArrayList<>();
        Set<String> claves = new HashSet<>();

        for (var detalleDto : dto.getDetalles()) {
            if (detalleDto.getIdProducto() == null) {
                throw new IllegalArgumentException("Cada detalle debe tener un producto");
            }
            if (detalleDto.getIdUnidadMedida() == null) {
                throw new IllegalArgumentException("Cada detalle debe tener una unidad de medida");
            }

            Producto producto = productoRepository.findById(detalleDto.getIdProducto())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Producto no encontrado con id: " + detalleDto.getIdProducto()));

            UnidadMedida unidadMedida = unidadMedidaRepository.findById(detalleDto.getIdUnidadMedida())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Unidad de medida no encontrada con id: " + detalleDto.getIdUnidadMedida()));

            presentacionProductoRepository
                    .findByProductoIdAndUnidadMedidaId(producto.getIdProducto(), unidadMedida.getIdUnidadMedida())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "La unidad '" + unidadMedida.getNombre() + "' no corresponde al producto '"
                                    + producto.getNombre() + "'"));

            BigDecimal cantidad = detalleDto.getCantidadSolicitada();
            if (cantidad == null || cantidad.compareTo(BigDecimal.ONE) < 0) {
                throw new IllegalArgumentException("La cantidad solicitada debe ser al menos 1");
            }
            if (cantidad.scale() > 3) {
                throw new IllegalArgumentException("La cantidad no puede tener más de 3 decimales");
            }

            String clave = producto.getIdProducto() + "-" + unidadMedida.getIdUnidadMedida();
            if (!claves.add(clave)) {
                throw new IllegalArgumentException(
                        "Producto duplicado en el pedido: " + producto.getNombre() + " (" + unidadMedida.getNombre() + ")");
            }

            DetallePedidoProveedor detalle = DetallePedidoProveedor.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .unidadMedida(unidadMedida)
                    .cantidadSolicitada(cantidad)
                    .recepciones(new ArrayList<>())
                    .build();
            detalles.add(detalle);
        }

        return detalles;
    }

    private PedidoListadoDto toListadoDto(PedidoProveedor pedido) {
        PedidoListadoDto dto = new PedidoListadoDto();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setNumero("PED-" + String.format("%05d", pedido.getIdPedido()));
        dto.setIdProveedor(pedido.getProveedor() != null ? pedido.getProveedor().getIdProveedor() : null);
        dto.setProveedor(nombreProveedor(pedido.getProveedor()));
        dto.setIdUsuario(pedido.getUsuario() != null ? pedido.getUsuario().getIdUsuario() : null);
        dto.setUsuario(pedido.getUsuario() != null ? pedido.getUsuario().getUsername() : null);
        dto.setFechaPedido(pedido.getFechaPedido());
        dto.setFechaEntregaEstimada(pedido.getFechaEntregaEstimada());
        dto.setEstado(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombre() : null);
        dto.setObservacion(pedido.getObservacion());

        List<PedidoDetalleListadoDto> detalles = new ArrayList<>();
        if (pedido.getDetalles() != null) {
            for (DetallePedidoProveedor det : pedido.getDetalles()) {
                PedidoDetalleListadoDto detDto = new PedidoDetalleListadoDto();
                detDto.setIdDetalle(det.getIdDetallePedido());
                detDto.setIdProducto(det.getProducto() != null ? det.getProducto().getIdProducto() : null);
                detDto.setProducto(det.getProducto() != null ? det.getProducto().getNombre() : null);
                detDto.setIdUnidadMedida(det.getUnidadMedida() != null ? det.getUnidadMedida().getIdUnidadMedida() : null);
                detDto.setUnidad(det.getUnidadMedida() != null ? det.getUnidadMedida().getNombre() : null);
                detDto.setCantidad(det.getCantidadSolicitada());
                BigDecimal factor = buscarFactor(det);
                detDto.setFactor(factor);
                detDto.setEquivalencia(calcularEquivalencia(det.getCantidadSolicitada(), factor));
                if (det.getProducto() != null) {
                    detDto.setControlaLote(det.getProducto().getControlaLote());
                    detDto.setControlaVencimiento(det.getProducto().getControlaVencimiento());
                    if (det.getProducto().getUnidadBase() != null) {
                        detDto.setUnidadBase(det.getProducto().getUnidadBase().getAbreviatura());
                    }
                }
                detDto.setPrecio(buscarPrecio(det));
                detalles.add(detDto);
            }
        }
        dto.setDetalles(detalles);
        return dto;
    }

    private String calcularEquivalencia(BigDecimal cantidad, BigDecimal factor) {
        if (cantidad == null || factor == null) return "—";
        return cantidad.stripTrailingZeros().toPlainString() + " × " + factor.stripTrailingZeros().toPlainString()
                + " = " + cantidad.multiply(factor).stripTrailingZeros().toPlainString();
    }

    private BigDecimal buscarFactor(DetallePedidoProveedor det) {
        if (det.getProducto() == null || det.getUnidadMedida() == null) return null;
        return presentacionProductoRepository
                .findByProductoIdAndUnidadMedidaId(det.getProducto().getIdProducto(), det.getUnidadMedida().getIdUnidadMedida())
                .map(PresentacionProducto::getFactorConversion)
                .orElse(null);
    }

    private BigDecimal buscarPrecio(DetallePedidoProveedor det) {
        if (det.getProducto() == null || det.getUnidadMedida() == null) return null;
        return presentacionProductoRepository
                .findByProductoIdAndUnidadMedidaId(det.getProducto().getIdProducto(), det.getUnidadMedida().getIdUnidadMedida())
                .map(PresentacionProducto::getPrecioVenta)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarPdf(Long id, String username) {
        PedidoProveedor pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + id));

        String estadoActual = pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombre() : null;
        if (!"ENVIADO".equals(estadoActual)
                && !"PARCIALMENTE_RECIBIDO".equals(estadoActual)
                && !"COMPLETADO".equals(estadoActual)) {
            throw new ResourceConflictException(
                    "El PDF solo está disponible para pedidos confirmados (ENVIADO, PARCIALMENTE_RECIBIDO o COMPLETADO)");
        }

        PedidoPdfDto pdfDto = toPdfDto(pedido, username != null ? username : "Sistema");
        return pedidoPdfService.generarPdf(pdfDto);
    }

    private PedidoPdfDto toPdfDto(PedidoProveedor pedido, String generadoPor) {
        PedidoPdfDto dto = new PedidoPdfDto();
        dto.setEmpresa(mapearEmpresa());

        Proveedor proveedor = pedido.getProveedor();
        if (proveedor != null) {
            dto.setProveedor(mapearProveedor(proveedor));
        }

        PedidoPdfInnerDto pedidoPdf = new PedidoPdfInnerDto();
        pedidoPdf.setNumero("PED-" + String.format("%05d", pedido.getIdPedido()));
        pedidoPdf.setFechaPedido(pedido.getFechaPedido());
        pedidoPdf.setFechaEntregaEstimada(pedido.getFechaEntregaEstimada());
        pedidoPdf.setEstado(pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombre() : null);
        pedidoPdf.setUsuario(pedido.getUsuario() != null ? pedido.getUsuario().getUsername() : null);
        pedidoPdf.setObservacion(pedido.getObservacion());
        dto.setPedido(pedidoPdf);

        List<DetallePdfDto> detalles = new ArrayList<>();
        if (pedido.getDetalles() != null) {
            for (DetallePedidoProveedor det : pedido.getDetalles()) {
                DetallePdfDto detDto = new DetallePdfDto();
                detDto.setProducto(det.getProducto() != null ? det.getProducto().getNombre() : null);
                detDto.setUnidad(det.getUnidadMedida() != null ? det.getUnidadMedida().getNombre() : null);
                detDto.setCantidad(det.getCantidadSolicitada());
                BigDecimal factor = buscarFactor(det);
                detDto.setFactor(factor);
                detDto.setEquivalencia(calcularEquivalencia(det.getCantidadSolicitada(), factor));
                detalles.add(detDto);
            }
        }
        dto.setDetalles(detalles);
        dto.setGeneradoPor(generadoPor);
        return dto;
    }

    private EmpresaDto mapearEmpresa() {
        EmpresaDto dto = new EmpresaDto();
        dto.setNombre(empresaNombre);
        dto.setNit(empresaNit);
        dto.setDireccion(empresaDireccion);
        dto.setTelefono(empresaTelefono);
        dto.setCiudad(empresaCiudad);
        dto.setLogoPath(empresaLogo);
        return dto;
    }

    private ProveedorPdfDto mapearProveedor(Proveedor proveedor) {
        ProveedorPdfDto dto = new ProveedorPdfDto();
        dto.setNombre(nombreProveedor(proveedor));

        if (proveedor.getTipoProveedor() == TipoProveedor.EMPRESA && proveedor.getEmpresa() != null) {
            Empresa empresa = proveedor.getEmpresa();
            dto.setDocumento(empresa.getNit());
            dto.setDireccion(mapearDireccionEmpresa(empresa));
            if (empresa.getContactos() != null && !empresa.getContactos().isEmpty()) {
                ContactoEmpresa contacto = empresa.getContactos().get(0);
                dto.setTelefono(contacto.getTelefono());
                dto.setCorreo(contacto.getCorreo());
            }
        } else if (proveedor.getPersona() != null) {
            var persona = proveedor.getPersona();
            dto.setDocumento(persona.getCi());
            if (persona.getDireccion() != null) {
                var direccion = persona.getDireccion();
                String ciudad = direccion.getCiudad() != null ? direccion.getCiudad().getNombre() : null;
                dto.setDireccion(concatenarDireccion(direccion.getZona(), direccion.getCalle(),
                        direccion.getNumero(), ciudad));
            }
            if (persona.getContactos() != null && !persona.getContactos().isEmpty()) {
                var contacto = persona.getContactos().get(0);
                dto.setTelefono(contacto.getTelefono());
                dto.setCorreo(contacto.getCorreo());
            }
        }
        return dto;
    }

    private String mapearDireccionEmpresa(Empresa empresa) {
        DireccionEmpresa direccion = empresa.getDireccion();
        if (direccion == null) return null;
        String ciudad = direccion.getCiudad() != null ? direccion.getCiudad().getNombre() : null;
        return concatenarDireccion(direccion.getZona(), direccion.getCalle(), direccion.getNumero(), ciudad);
    }

    private String concatenarDireccion(String zona, String calle, String numero, String ciudad) {
        StringBuilder sb = new StringBuilder();
        if (zona != null && !zona.isBlank()) sb.append(zona);
        if (calle != null && !calle.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(calle);
        }
        if (numero != null && !numero.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(numero);
        }
        if (ciudad != null && !ciudad.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ciudad);
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private String nombreProveedor(Proveedor proveedor) {
        if (proveedor == null) return null;
        if (proveedor.getTipoProveedor() == TipoProveedor.EMPRESA && proveedor.getEmpresa() != null) {
            Empresa empresa = proveedor.getEmpresa();
            return empresa.getNombreComercial() != null && !empresa.getNombreComercial().isBlank()
                    ? empresa.getNombreComercial()
                    : empresa.getRazonSocial();
        }
        if (proveedor.getPersona() != null) {
            var persona = proveedor.getPersona();
            String nombre = (persona.getNombres() != null ? persona.getNombres() : "")
                    + " " + (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "")
                    + " " + (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
            return nombre.trim().replaceAll("\\s+", " ");
        }
        return null;
    }
}
