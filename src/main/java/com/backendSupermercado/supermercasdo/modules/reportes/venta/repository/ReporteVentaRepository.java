package com.backendSupermercado.supermercasdo.modules.reportes.venta.repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaDto;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaFiltro;

@Repository
public class ReporteVentaRepository {

    private static final String NOMBRE_CLIENTE =
            "COALESCE(NULLIF(TRIM(CONCAT_WS(' ', cl.nombres, cl.apellido_paterno, cl.apellido_materno)), ''), 'Consumidor final')";
    private static final String NOMBRE_CAJERO =
            "COALESCE(NULLIF(TRIM(CONCAT_WS(' ', pe.nombres, pe.apellido_paterno, pe.apellido_materno)), ''), u.username)";
    private static final String CAJA_VENTA =
            "COALESCE(v.id_caja, (SELECT scf.id_caja FROM sesion_caja scf WHERE scf.id_sesion_caja = v.id_sesion_caja))";

    private final NamedParameterJdbcTemplate jdbc;

    public ReporteVentaRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Page<ReporteVentaDto.General> general(ReporteVentaFiltro filtro, Pageable pageable) {
        MapSqlParameterSource parametros = parametrosFecha(filtro);
        StringBuilder where = new StringBuilder(" WHERE 1=1 " + rangoFecha("v", filtro));
        filtrosVenta(where, parametros, filtro, true, true, true, true, true);

        String desde = """
                FROM venta v
                LEFT JOIN cliente cl ON cl.id_cliente = v.id_cliente
                JOIN usuario u ON u.id_usuario = v.id_usuario
                LEFT JOIN empleado em ON em.id_empleado = u.id_empleado
                LEFT JOIN persona pe ON pe.id_persona = em.id_persona
                LEFT JOIN cuenta_por_cobrar cxc ON cxc.id_venta = v.id_venta
                """;
        String datos = """
                SELECT v.fecha_venta, v.codigo, %s AS cliente, %s AS cajero,
                       CASE WHEN cxc.id_cuenta_por_cobrar IS NULL THEN 'CONTADO' ELSE 'CREDITO' END AS tipo_venta,
                       COALESCE((SELECT STRING_AGG(DISTINCT pv.metodo_pago, ', ')
                                 FROM pago_venta pv WHERE pv.id_venta = v.id_venta), 'SIN PAGO') AS metodo_pago,
                       v.estado, v.total
                """.formatted(NOMBRE_CLIENTE, NOMBRE_CAJERO) + desde + where + " ORDER BY v.fecha_venta DESC, v.id_venta DESC";
        String conteo = "SELECT COUNT(*) " + desde + where;

        return pagina(datos, conteo, parametros, pageable, (rs, row) -> new ReporteVentaDto.General(
                fechaHora(rs, "fecha_venta"), rs.getString("codigo"), rs.getString("cliente"),
                rs.getString("cajero"), rs.getString("tipo_venta"), rs.getString("metodo_pago"),
                rs.getString("estado"), decimal(rs, "total")));
    }

    public Page<ReporteVentaDto.Ganancia> ganancias(ReporteVentaFiltro filtro, Pageable pageable) {
        MapSqlParameterSource parametros = parametrosFecha(filtro);
        StringBuilder where = new StringBuilder(
                " WHERE 1=1 " + rangoFecha("v", filtro) + " AND v.estado = 'CONFIRMADA' ");
        filtrosVenta(where, parametros, filtro, false, false, false, true, false);
        filtrosProducto(where, parametros, filtro);

        String desde = """
                FROM venta v
                JOIN detalle_venta d ON d.id_venta = v.id_venta
                JOIN producto p ON p.id_producto = d.id_producto
                JOIN subcategoria sc ON sc.id_subcategoria = p.id_subcategoria
                JOIN categoria cat ON cat.id_categoria = sc.id_categoria
                JOIN usuario u ON u.id_usuario = v.id_usuario
                LEFT JOIN empleado em ON em.id_empleado = u.id_empleado
                LEFT JOIN persona pe ON pe.id_persona = em.id_persona
                """;
        String expresionVenta = "MAX(v.total)";
        String costoCompleto = "COUNT(*) FILTER (WHERE d.costo_unitario IS NULL OR d.costo_unitario <= 0) = 0";
        String costoCalculado = "SUM(d.costo_unitario * COALESCE(d.cantidad_base, d.cantidad))";
        String expresionCosto = "CASE WHEN " + costoCompleto + " THEN " + costoCalculado + " ELSE NULL END";
        String expresionGanancia = "CASE WHEN " + costoCompleto + " THEN (" + expresionVenta
                + " - " + costoCalculado + ") ELSE NULL END";
        String expresionMargen = "CASE WHEN " + costoCompleto + " THEN COALESCE(100 * ("
                + expresionVenta + " - " + costoCalculado + ") / NULLIF(" + expresionVenta
                + ", 0), 0) ELSE NULL END";
        StringBuilder having = new StringBuilder(" HAVING 1 = 1 ");
        if (filtro.getMargenMinimo() != null) {
            having.append(" AND ").append(expresionMargen).append(" >= :margenMinimo ");
            parametros.addValue("margenMinimo", filtro.getMargenMinimo());
        }
        if (filtro.getMargenMaximo() != null) {
            having.append(" AND ").append(expresionMargen).append(" <= :margenMaximo ");
            parametros.addValue("margenMaximo", filtro.getMargenMaximo());
        }
        String agrupacion = " GROUP BY v.id_venta, v.codigo, v.fecha_venta, u.username, pe.nombres, pe.apellido_paterno, pe.apellido_materno ";
        String datos = "SELECT v.id_venta, v.codigo, v.fecha_venta, " + NOMBRE_CAJERO + " AS cajero, "
                + "COUNT(d.id_detalle_venta) AS cantidad_productos, " + expresionVenta + " AS total_vendido, "
                + expresionCosto + " AS costo_total, " + expresionGanancia + " AS ganancia, "
                + expresionMargen + " AS margen, CASE WHEN " + costoCompleto
                + " THEN 'COMPLETO' ELSE 'NO_DISPONIBLE' END AS estado_costo "
                + desde + where + agrupacion + having + " ORDER BY v.fecha_venta DESC";
        String conteo = "SELECT COUNT(*) FROM (SELECT v.id_venta " + desde + where + agrupacion + having + ") q";

        return pagina(datos, conteo, parametros, pageable, (rs, row) -> new ReporteVentaDto.Ganancia(
                rs.getLong("id_venta"), rs.getString("codigo"), fechaHora(rs, "fecha_venta"),
                rs.getString("cajero"), numeroLong(rs, "cantidad_productos"), decimal(rs, "total_vendido"),
                decimalNullable(rs, "costo_total"), decimalNullable(rs, "ganancia"),
                decimalNullable(rs, "margen"), rs.getString("estado_costo")));
    }

    public List<ReporteVentaDto.DetalleGanancia> detalleGanancia(Long idVenta) {
        String consulta = """
                SELECT p.codigo_interno, p.nombre AS producto, d.cantidad,
                       d.precio_unitario, d.costo_unitario,
                       CASE WHEN v.subtotal > 0 THEN d.importe * v.total / v.subtotal
                            ELSE d.importe END AS total_vendido,
                       CASE WHEN d.costo_unitario IS NULL OR d.costo_unitario <= 0 THEN NULL
                            ELSE d.costo_unitario * COALESCE(d.cantidad_base, d.cantidad) END AS costo_total,
                       CASE WHEN d.costo_unitario IS NULL OR d.costo_unitario <= 0 THEN NULL
                            ELSE (CASE WHEN v.subtotal > 0 THEN d.importe * v.total / v.subtotal ELSE d.importe END)
                                 - (d.costo_unitario * COALESCE(d.cantidad_base, d.cantidad)) END AS ganancia
                FROM detalle_venta d
                JOIN venta v ON v.id_venta = d.id_venta
                JOIN producto p ON p.id_producto = d.id_producto
                WHERE d.id_venta = :idVenta
                ORDER BY p.nombre
                """;
        return jdbc.query(consulta, new MapSqlParameterSource("idVenta", idVenta), (rs, row) ->
                new ReporteVentaDto.DetalleGanancia(
                        rs.getString("codigo_interno"), rs.getString("producto"), decimal(rs, "cantidad"),
                        decimal(rs, "precio_unitario"), decimalNullable(rs, "costo_unitario"),
                        decimal(rs, "total_vendido"), decimalNullable(rs, "costo_total"),
                        decimalNullable(rs, "ganancia")));
    }

    public Page<ReporteVentaDto.ProductoVendido> productosVendidos(
            ReporteVentaFiltro filtro, Pageable pageable, boolean descendente) {
        MapSqlParameterSource parametros = parametrosFecha(filtro);
        StringBuilder where = new StringBuilder(
                " WHERE 1=1 " + rangoFecha("v", filtro) + " AND v.estado = 'CONFIRMADA' ");
        filtrosProducto(where, parametros, filtro);
        String desde = """
                FROM detalle_venta d
                JOIN venta v ON v.id_venta = d.id_venta
                JOIN producto p ON p.id_producto = d.id_producto
                JOIN subcategoria sc ON sc.id_subcategoria = p.id_subcategoria
                JOIN categoria cat ON cat.id_categoria = sc.id_categoria
                """;
        String agrupacion = " GROUP BY p.id_producto, p.codigo_interno, p.nombre, cat.nombre ";
        String datos = "SELECT p.codigo_interno, p.nombre AS producto, cat.nombre AS categoria, "
                + "SUM(d.cantidad) AS cantidad, MAX(v.fecha_venta) AS ultima_venta, SUM(d.importe) AS importe "
                + desde + where + agrupacion + " ORDER BY cantidad " + (descendente ? "DESC" : "ASC")
                + ", p.nombre ASC";
        String conteo = "SELECT COUNT(*) FROM (SELECT p.id_producto " + desde + where + agrupacion + ") q";
        return pagina(datos, conteo, parametros, pageable, this::mapearProductoVendido);
    }

    public Page<ReporteVentaDto.ProductoSinVenta> productosSinVentas(
            ReporteVentaFiltro filtro, Pageable pageable) {
        MapSqlParameterSource parametros = parametrosFecha(filtro);
        StringBuilder where = new StringBuilder(" WHERE p.estado = TRUE ");
        filtrosProducto(where, parametros, filtro);
        where.append(" AND NOT EXISTS (SELECT 1 FROM detalle_venta dx "
                + "JOIN venta vx ON vx.id_venta = dx.id_venta "
                + "WHERE dx.id_producto = p.id_producto AND vx.estado = 'CONFIRMADA' "
                + rangoFecha("vx", filtro) + ") ");
        String desde = """
                FROM producto p
                JOIN subcategoria sc ON sc.id_subcategoria = p.id_subcategoria
                JOIN categoria cat ON cat.id_categoria = sc.id_categoria
                LEFT JOIN (SELECT id_producto, SUM(cantidad) AS stock FROM existencia GROUP BY id_producto) ex
                       ON ex.id_producto = p.id_producto
                LEFT JOIN (
                    SELECT d.id_producto, MAX(v.fecha_venta) AS ultima_venta
                    FROM detalle_venta d JOIN venta v ON v.id_venta = d.id_venta
                    WHERE v.estado = 'CONFIRMADA' GROUP BY d.id_producto
                ) hist ON hist.id_producto = p.id_producto
                """;
        String datos = "SELECT p.codigo_interno, p.nombre AS producto, cat.nombre AS categoria, "
                + "COALESCE(ex.stock, 0) AS stock, hist.ultima_venta, "
                + "GREATEST(CURRENT_DATE - CAST(COALESCE(hist.ultima_venta, p.fecha_creacion) AS date), 0) AS dias "
                + desde + where + " ORDER BY hist.ultima_venta NULLS FIRST, p.nombre";
        String conteo = "SELECT COUNT(*) " + desde + where;
        return pagina(datos, conteo, parametros, pageable, (rs, row) -> new ReporteVentaDto.ProductoSinVenta(
                rs.getString("codigo_interno"), rs.getString("producto"), rs.getString("categoria"),
                decimal(rs, "stock"), fechaHora(rs, "ultima_venta"), numeroLong(rs, "dias")));
    }

    public Page<ReporteVentaDto.PorMetodoPago> porMetodoPago(
            ReporteVentaFiltro filtro, Pageable pageable) {
        MapSqlParameterSource parametros = parametrosFecha(filtro);
        StringBuilder where = new StringBuilder(
                " WHERE 1=1 " + rangoFecha("v", filtro) + " AND v.estado = 'CONFIRMADA' ");
        filtrosVenta(where, parametros, filtro, false, true, false, true, true);
        if (filtro.getMetodoPago() != null) {
            where.append(" AND pv.metodo_pago = :metodoPago ");
        }
        String desde = " FROM pago_venta pv JOIN venta v ON v.id_venta = pv.id_venta ";
        String agrupacion = " GROUP BY pv.metodo_pago ";
        String datos = "SELECT pv.metodo_pago, COUNT(DISTINCT v.id_venta) AS operaciones, "
                + "COALESCE(100.0 * SUM(pv.total_recibido) / NULLIF(SUM(SUM(pv.total_recibido)) OVER (), 0), 0) AS participacion, "
                + "SUM(pv.total_recibido) AS total " + desde + where + agrupacion + " ORDER BY total DESC";
        String conteo = "SELECT COUNT(*) FROM (SELECT pv.metodo_pago " + desde + where + agrupacion + ") q";
        return pagina(datos, conteo, parametros, pageable, (rs, row) -> new ReporteVentaDto.PorMetodoPago(
                rs.getString("metodo_pago"), numeroLong(rs, "operaciones"), decimal(rs, "participacion"),
                decimal(rs, "total")));
    }

    public Page<ReporteVentaDto.PorCajero> porCajero(ReporteVentaFiltro filtro, Pageable pageable) {
        MapSqlParameterSource parametros = parametrosFecha(filtro);
        StringBuilder where = new StringBuilder(" WHERE 1=1 " + rangoFecha("v", filtro));
        filtrosVenta(where, parametros, filtro, true, false, false, true, true);
        if (filtro.getEstadoVenta() == null) where.append(" AND v.estado = 'CONFIRMADA' ");
        String desde = """
                FROM venta v
                JOIN usuario u ON u.id_usuario = v.id_usuario
                LEFT JOIN empleado em ON em.id_empleado = u.id_empleado
                LEFT JOIN persona pe ON pe.id_persona = em.id_persona
                LEFT JOIN sesion_caja sca ON sca.id_sesion_caja = v.id_sesion_caja
                LEFT JOIN caja ca ON ca.id_caja = COALESCE(v.id_caja, sca.id_caja)
                """;
        String agrupacion = " GROUP BY u.id_usuario, u.username, pe.nombres, pe.apellido_paterno, pe.apellido_materno, ca.id_caja, ca.nombre ";
        String datos = "SELECT u.id_usuario, " + NOMBRE_CAJERO + " AS cajero, COALESCE(ca.nombre, 'Sin caja') AS caja, "
                + "COUNT(v.id_venta) AS ventas, AVG(v.total) AS promedio, SUM(v.total) AS total "
                + desde + where + agrupacion + " ORDER BY total DESC";
        String conteo = "SELECT COUNT(*) FROM (SELECT u.id_usuario, ca.id_caja " + desde + where + agrupacion + ") q";
        return pagina(datos, conteo, parametros, pageable, (rs, row) -> new ReporteVentaDto.PorCajero(
                rs.getLong("id_usuario"), rs.getString("cajero"), rs.getString("caja"),
                numeroLong(rs, "ventas"), decimal(rs, "promedio"), decimal(rs, "total")));
    }

    public Page<ReporteVentaDto.ContadoCredito> contadoCredito(
            ReporteVentaFiltro filtro, Pageable pageable) {
        MapSqlParameterSource parametros = parametrosFecha(filtro);
        StringBuilder where = new StringBuilder(
                " WHERE 1=1 " + rangoFecha("v", filtro) + " AND v.estado = 'CONFIRMADA' ");
        filtrosVenta(where, parametros, filtro, false, false, true, true, false);
        if (filtro.getEstadoCredito() != null) {
            where.append(" AND cxc.estado = :estadoCredito ");
            parametros.addValue("estadoCredito", filtro.getEstadoCredito().name());
        }
        String desde = " FROM venta v LEFT JOIN cuenta_por_cobrar cxc ON cxc.id_venta = v.id_venta ";
        String tipo = "CASE WHEN cxc.id_cuenta_por_cobrar IS NULL THEN 'CONTADO' ELSE 'CREDITO' END";
        String agrupacion = " GROUP BY " + tipo;
        String datos = "SELECT " + tipo + " AS tipo_venta, COUNT(*) AS operaciones, SUM(v.total) AS importe, "
                + "SUM(CASE WHEN cxc.id_cuenta_por_cobrar IS NULL THEN v.total ELSE cxc.monto_total - cxc.saldo_pendiente END) AS cobrado, "
                + "SUM(COALESCE(cxc.saldo_pendiente, 0)) AS saldo " + desde + where + agrupacion + " ORDER BY tipo_venta";
        String conteo = "SELECT COUNT(*) FROM (SELECT " + tipo + desde + where + agrupacion + ") q";
        return pagina(datos, conteo, parametros, pageable, (rs, row) -> new ReporteVentaDto.ContadoCredito(
                rs.getString("tipo_venta"), numeroLong(rs, "operaciones"), decimal(rs, "importe"),
                decimal(rs, "cobrado"), decimal(rs, "saldo")));
    }

    public Page<ReporteVentaDto.PorPeriodo> porPeriodo(ReporteVentaFiltro filtro, Pageable pageable) {
        MapSqlParameterSource parametros = parametrosFecha(filtro);
        StringBuilder where = new StringBuilder(
                " WHERE 1=1 " + rangoFecha("v", filtro) + " AND v.estado = 'CONFIRMADA' ");
        filtrosVenta(where, parametros, filtro, false, true, false, true, false);
        String unidad = switch (filtro.getAgrupacion()) {
            case "SEMANA" -> "week";
            case "MES" -> "month";
            default -> "day";
        };
        String periodo = "CAST(DATE_TRUNC('" + unidad + "', v.fecha_venta) AS date)";
        String desde = " FROM venta v LEFT JOIN cuenta_por_cobrar cxc ON cxc.id_venta = v.id_venta ";
        String agrupacion = " GROUP BY " + periodo;
        String datos = "SELECT " + periodo + " AS periodo, COUNT(*) AS ventas, "
                + "SUM(CASE WHEN cxc.id_cuenta_por_cobrar IS NULL THEN v.total ELSE 0 END) AS contado, "
                + "SUM(CASE WHEN cxc.id_cuenta_por_cobrar IS NOT NULL THEN v.total ELSE 0 END) AS credito, "
                + "SUM(v.total) AS total, AVG(v.total) AS promedio " + desde + where + agrupacion + " ORDER BY periodo DESC";
        String conteo = "SELECT COUNT(*) FROM (SELECT " + periodo + desde + where + agrupacion + ") q";
        return pagina(datos, conteo, parametros, pageable, (rs, row) -> new ReporteVentaDto.PorPeriodo(
                rs.getDate("periodo").toLocalDate(), numeroLong(rs, "ventas"), decimal(rs, "contado"),
                decimal(rs, "credito"), decimal(rs, "total"), decimal(rs, "promedio")));
    }

    public Page<ReporteVentaDto.PorCliente> porCliente(ReporteVentaFiltro filtro, Pageable pageable) {
        MapSqlParameterSource parametros = parametrosFecha(filtro);
        StringBuilder where = new StringBuilder(
                " WHERE 1=1 " + rangoFecha("v", filtro) + " AND cl.id_cliente IS NOT NULL ");
        filtrosVenta(where, parametros, filtro, true, false, true, false, false);
        if (filtro.getEstadoVenta() == null) where.append(" AND v.estado = 'CONFIRMADA' ");
        String desde = """
                FROM venta v
                JOIN cliente cl ON cl.id_cliente = v.id_cliente
                LEFT JOIN cuenta_por_cobrar cxc ON cxc.id_venta = v.id_venta
                """;
        String agrupacion = " GROUP BY cl.id_cliente, cl.ci, cl.nombres, cl.apellido_paterno, cl.apellido_materno ";
        String datos = "SELECT cl.id_cliente, cl.ci, " + NOMBRE_CLIENTE + " AS cliente, COUNT(*) AS compras, "
                + "MAX(v.fecha_venta) AS ultima_compra, "
                + "SUM(CASE WHEN cxc.id_cuenta_por_cobrar IS NULL THEN v.total ELSE 0 END) AS contado, "
                + "SUM(CASE WHEN cxc.id_cuenta_por_cobrar IS NOT NULL THEN v.total ELSE 0 END) AS credito, "
                + "SUM(v.total) AS total " + desde + where + agrupacion + " ORDER BY total DESC";
        String conteo = "SELECT COUNT(*) FROM (SELECT cl.id_cliente " + desde + where + agrupacion + ") q";
        return pagina(datos, conteo, parametros, pageable, (rs, row) -> new ReporteVentaDto.PorCliente(
                rs.getLong("id_cliente"), rs.getString("ci"), rs.getString("cliente"),
                numeroLong(rs, "compras"), fechaHora(rs, "ultima_compra"), decimal(rs, "contado"),
                decimal(rs, "credito"), decimal(rs, "total")));
    }

    private void filtrosVenta(StringBuilder where, MapSqlParameterSource parametros, ReporteVentaFiltro filtro,
            boolean estado, boolean metodo, boolean tipo, boolean cajero, boolean caja) {
        if (estado && filtro.getEstadoVenta() != null) {
            where.append(" AND v.estado = :estadoVenta ");
            parametros.addValue("estadoVenta", filtro.getEstadoVenta().name());
        }
        if (metodo && filtro.getMetodoPago() != null) {
            where.append(" AND EXISTS (SELECT 1 FROM pago_venta pvf WHERE pvf.id_venta = v.id_venta AND pvf.metodo_pago = :metodoPago) ");
            parametros.addValue("metodoPago", filtro.getMetodoPago().name());
        }
        if (tipo && filtro.getTipoVenta() != null) {
            if ("CREDITO".equals(filtro.getTipoVenta())) {
                where.append(" AND EXISTS (SELECT 1 FROM cuenta_por_cobrar ctf WHERE ctf.id_venta = v.id_venta) ");
            } else {
                where.append(" AND NOT EXISTS (SELECT 1 FROM cuenta_por_cobrar ctf WHERE ctf.id_venta = v.id_venta) ");
            }
        }
        if (filtro.getClienteId() != null) {
            where.append(" AND v.id_cliente = :clienteId ");
            parametros.addValue("clienteId", filtro.getClienteId());
        }
        if (cajero && filtro.getCajeroId() != null) {
            where.append(" AND v.id_usuario = :cajeroId ");
            parametros.addValue("cajeroId", filtro.getCajeroId());
        }
        if (caja && filtro.getCajaId() != null) {
            where.append(" AND ").append(CAJA_VENTA).append(" = :cajaId ");
            parametros.addValue("cajaId", filtro.getCajaId());
        }
    }

    private void filtrosProducto(StringBuilder where, MapSqlParameterSource parametros, ReporteVentaFiltro filtro) {
        if (filtro.getProductoId() != null) {
            where.append(" AND p.id_producto = :productoId ");
            parametros.addValue("productoId", filtro.getProductoId());
        }
        if (filtro.getCategoriaId() != null) {
            where.append(" AND cat.id_categoria = :categoriaId ");
            parametros.addValue("categoriaId", filtro.getCategoriaId());
        }
        if (filtro.getMarcaId() != null) {
            where.append(" AND p.id_marca = :marcaId ");
            parametros.addValue("marcaId", filtro.getMarcaId());
        }
    }

    private MapSqlParameterSource parametrosFecha(ReporteVentaFiltro filtro) {
        MapSqlParameterSource parametros = new MapSqlParameterSource();
        if (filtro.getFechaInicio() != null) {
            parametros.addValue("inicio", filtro.getFechaInicio().atStartOfDay());
        }
        if (filtro.getFechaFin() != null) {
            parametros.addValue("fin", filtro.getFechaFin().plusDays(1).atStartOfDay());
        }
        return parametros;
    }

    private String rangoFecha(String alias, ReporteVentaFiltro filtro) {
        StringBuilder rango = new StringBuilder();
        if (filtro.getFechaInicio() != null) rango.append(" AND ").append(alias).append(".fecha_venta >= :inicio ");
        if (filtro.getFechaFin() != null) rango.append(" AND ").append(alias).append(".fecha_venta < :fin ");
        return rango.toString();
    }

    private <T> Page<T> pagina(String consulta, String conteo, MapSqlParameterSource parametros,
            Pageable pageable, RowMapper<T> mapper) {
        if (!pageable.isPaged()) {
            List<T> contenido = jdbc.query(consulta, parametros, mapper);
            return new PageImpl<>(contenido);
        }
        parametros.addValue("limite", pageable.getPageSize());
        parametros.addValue("desplazamiento", pageable.getOffset());
        List<T> contenido = jdbc.query(consulta + " LIMIT :limite OFFSET :desplazamiento", parametros, mapper);
        Number total = jdbc.queryForObject(conteo, parametros, Number.class);
        return new PageImpl<>(contenido, pageable, total == null ? 0 : total.longValue());
    }

    private ReporteVentaDto.ProductoVendido mapearProductoVendido(ResultSet rs, int row) throws SQLException {
        return new ReporteVentaDto.ProductoVendido(
                rs.getString("codigo_interno"), rs.getString("producto"), rs.getString("categoria"),
                decimal(rs, "cantidad"), fechaHora(rs, "ultima_venta"), decimal(rs, "importe"));
    }

    private static LocalDateTime fechaHora(ResultSet rs, String columna) throws SQLException {
        java.sql.Timestamp valor = rs.getTimestamp(columna);
        return valor == null ? null : valor.toLocalDateTime();
    }

    private static BigDecimal decimal(ResultSet rs, String columna) throws SQLException {
        BigDecimal valor = rs.getBigDecimal(columna);
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static BigDecimal decimalNullable(ResultSet rs, String columna) throws SQLException {
        return rs.getBigDecimal(columna);
    }

    private static Long numeroLong(ResultSet rs, String columna) throws SQLException {
        Number valor = (Number) rs.getObject(columna);
        return valor == null ? null : valor.longValue();
    }
}
