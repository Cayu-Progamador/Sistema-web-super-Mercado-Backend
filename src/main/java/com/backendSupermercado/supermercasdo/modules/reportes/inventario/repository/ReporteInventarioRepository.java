package com.backendSupermercado.supermercasdo.modules.reportes.inventario.repository;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReporteInventarioRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public ReporteInventarioRepository(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Page<Map<String, Object>> consultar(String ruta, Map<String, Object> filtros, Pageable pagina) {
        MapSqlParameterSource parametros = new MapSqlParameterSource();
        filtros.forEach(parametros::addValue);
        Consulta consulta = construirConsulta(ruta, filtros);
        parametros.addValue("limite", pagina.isPaged() ? pagina.getPageSize() : Integer.MAX_VALUE);
        parametros.addValue("offset", pagina.isPaged() ? pagina.getOffset() : 0);
        var filas = jdbc.queryForList(consulta.datos + " LIMIT :limite OFFSET :offset", parametros);
        long total = jdbc.queryForObject(consulta.conteo, parametros, Long.class);
        return new PageImpl<>(filas, pagina, total);
    }

    private Consulta construirConsulta(String ruta, Map<String, Object> filtros) {
        if (ruta.equals("transferencias-inventario")) return consultaTransferencias(filtros);
        if (ruta.equals("inventario-fisico")) return consultaInventarioFisico(filtros);
        if (ruta.equals("ajustes-inventario")) return consultaAjustes(filtros);
        String from = " FROM producto p JOIN subcategoria sc ON sc.id_subcategoria=p.id_subcategoria "
                + "JOIN categoria cat ON cat.id_categoria=sc.id_categoria LEFT JOIN existencia ex ON ex.id_producto=p.id_producto "
                + "LEFT JOIN ubicacion_almacen u ON u.id_ubicacion=ex.id_ubicacion LEFT JOIN almacen a ON a.id_almacen=u.id_almacen "
                + "LEFT JOIN lote l ON l.id_lote=ex.id_lote ";
        String where = " WHERE p.estado=TRUE " + filtroIds(filtros) + filtroMarca(filtros);

        if (ruta.equals("kardex")) {
            String kFrom = " FROM detalle_movimiento_almacen dm JOIN movimiento_almacen mi ON mi.id_movimiento=dm.id_movimiento "
                    + "JOIN tipo_movimiento_almacen tm ON tm.id_tipo_movimiento=mi.id_tipo_movimiento JOIN producto p ON p.id_producto=dm.id_producto "
                    + "JOIN subcategoria sc ON sc.id_subcategoria=p.id_subcategoria JOIN categoria cat ON cat.id_categoria=sc.id_categoria "
                    + "LEFT JOIN ubicacion_almacen u ON u.id_ubicacion=dm.id_ubicacion LEFT JOIN almacen a ON a.id_almacen=u.id_almacen "
                    + "LEFT JOIN lote l ON l.id_lote=dm.id_lote LEFT JOIN usuario usr ON usr.id_usuario=mi.id_usuario ";
            String kWhere = " WHERE 1=1 " + filtroIds(filtros) + filtroMarca(filtros) + fechas(filtros, "mi.fecha_movimiento");
            if (filtros.get("tipoMovimiento") != null) kWhere += " AND tm.nombre=:tipoMovimiento ";
            if (filtros.get("lote") != null) kWhere += " AND l.codigo_lote=:lote ";
            String datos = "SELECT dm.id_detalle_movimiento AS id,mi.fecha_movimiento AS fecha,mi.referencia,tm.nombre AS movimiento,"
                    + "p.codigo_interno AS codigo,p.nombre AS producto,dm.cantidad,dm.saldo_resultante AS saldo,"
                    + "COALESCE(u.codigo,'Sin ubicación') AS ubicacion,COALESCE(usr.username,'Sin usuario') AS responsable"
                    + kFrom + kWhere + " ORDER BY mi.fecha_movimiento DESC";
            return new Consulta(datos, "SELECT COUNT(*)" + kFrom + kWhere);
        }

        where += fechas(filtros, ruta.equals("proximos-vencer") || ruta.equals("productos-vencidos") ? "l.fecha_vencimiento" : "l.fecha_ingreso");
        String group = " GROUP BY p.id_producto,p.codigo_interno,p.nombre,cat.nombre,a.nombre,u.codigo,u.nombre,l.codigo_lote,l.fecha_vencimiento ";
        if (ruta.equals("existencias")) {
            String select = "SELECT p.id_producto AS id,p.codigo_interno AS codigo,p.nombre AS producto,cat.nombre AS categoria,"
                    + "COALESCE(a.nombre,'Sin almacén') AS almacen,COALESCE(u.codigo,'Sin ubicación') AS ubicacion,SUM(COALESCE(ex.cantidad,0)) AS stock";
            String having = estadoStock(filtros);
            return new Consulta(select + from + where + group + having + " ORDER BY p.nombre", "SELECT COUNT(*) FROM (" + select + from + where + group + having + ") q");
        }
        if (ruta.equals("stock-minimo") || ruta.equals("productos-agotados")) {
            String select = "SELECT p.id_producto AS id,p.codigo_interno AS codigo,p.nombre AS producto,cat.nombre AS categoria,"
                    + "COALESCE((SELECT cs.stock_minimo FROM configuracion_stock cs WHERE cs.id_producto=p.id_producto),0) AS minimo,SUM(COALESCE(ex.cantidad,0)) AS stock";
            String having = ruta.equals("productos-agotados")
                    ? " HAVING SUM(COALESCE(ex.cantidad,0))<=0 "
                    : " HAVING SUM(COALESCE(ex.cantidad,0))<=COALESCE((SELECT cs.stock_minimo FROM configuracion_stock cs WHERE cs.id_producto=p.id_producto),0) ";
            String nivel = nivelStock(filtros);
            if (!nivel.isBlank()) having += nivel;
            return new Consulta(select + from + where + group + having + " ORDER BY p.nombre", "SELECT COUNT(*) FROM (" + select + from + where + group + having + ") q");
        }
        if (ruta.equals("proximos-vencer") || ruta.equals("productos-vencidos")) {
            String vencimiento = ruta.equals("proximos-vencer") ? " AND l.fecha_vencimiento BETWEEN CURRENT_DATE AND CURRENT_DATE+INTERVAL '60 days' " : " AND l.fecha_vencimiento<CURRENT_DATE ";
            String select = "SELECT l.codigo_lote AS lote,p.codigo_interno AS codigo,p.nombre AS producto,COALESCE(a.nombre,'Sin almacén') AS almacen,l.fecha_vencimiento AS vencimiento,COALESCE(ex.cantidad,0) AS cantidad";
            return new Consulta(select + from + where + vencimiento + " ORDER BY l.fecha_vencimiento,p.nombre", "SELECT COUNT(*)" + from + where + vencimiento);
        }
        return new Consulta("SELECT NULL WHERE FALSE", "SELECT 0");
    }

    private Consulta consultaTransferencias(Map<String, Object> f) {
        String from = " FROM transferencia_almacen tr JOIN detalle_transferencia dt ON dt.id_transferencia=tr.id_transferencia "
                + "JOIN producto p ON p.id_producto=dt.id_producto LEFT JOIN ubicacion_almacen uo ON uo.id_ubicacion=tr.id_ubicacion_origen "
                + "LEFT JOIN almacen ao ON ao.id_almacen=uo.id_almacen LEFT JOIN ubicacion_almacen ud ON ud.id_ubicacion=tr.id_ubicacion_destino "
                + "LEFT JOIN almacen ad ON ad.id_almacen=ud.id_almacen JOIN estado_transferencia et ON et.id_estado_transferencia=tr.id_estado_transferencia "
                + "LEFT JOIN usuario usr ON usr.id_usuario=tr.id_usuario ";
        String where = " WHERE 1=1 ";
        if (f.get("productoId") != null) where += " AND p.id_producto=:productoId ";
        if (f.get("almacenOrigenId") != null) where += " AND ao.id_almacen=:almacenOrigenId ";
        if (f.get("almacenDestinoId") != null) where += " AND ad.id_almacen=:almacenDestinoId ";
        if (f.get("estadoTransferencia") != null) where += " AND et.nombre=:estadoTransferencia ";
        where += fechas(f, "tr.fecha_transferencia");
        String datos = "SELECT dt.id_detalle_transferencia AS id,tr.fecha_transferencia AS fecha,tr.codigo,"
                + "p.codigo_interno AS \"codigoProducto\",p.nombre AS producto,COALESCE(ao.nombre,'Sin origen') AS origen,"
                + "COALESCE(ad.nombre,'Sin destino') AS destino,et.nombre AS estado,dt.cantidad,COALESCE(usr.username,'Sin usuario') AS responsable"
                + from + where + " ORDER BY tr.fecha_transferencia DESC, tr.codigo";
        return new Consulta(datos, "SELECT COUNT(*)" + from + where);
    }

    private Consulta consultaInventarioFisico(Map<String, Object> f) {
        String from = " FROM detalle_inventario_fisico d JOIN inventario_fisico i ON i.id_inventario_fisico=d.id_inventario_fisico "
                + "JOIN producto p ON p.id_producto=d.id_producto JOIN almacen a ON a.id_almacen=d.id_almacen "
                + "JOIN ubicacion_almacen u ON u.id_ubicacion=d.id_ubicacion JOIN empleado e ON e.id_empleado=i.id_responsable "
                + "JOIN persona pe ON pe.id_persona=e.id_persona ";
        String where = " WHERE 1=1 ";
        if (f.get("productoId") != null) where += " AND p.id_producto=:productoId ";
        if (f.get("almacenId") != null) where += " AND i.id_almacen=:almacenId ";
        if (f.get("responsableId") != null) where += " AND i.id_responsable=:responsableId ";
        if (f.get("estadoConteo") != null) where += " AND i.estado=:estadoConteo ";
        if ("PENDIENTE".equalsIgnoreCase(String.valueOf(f.get("resultadoInventario")))) where += " AND d.cantidad_fisica IS NULL ";
        if ("CON_DIFERENCIA".equalsIgnoreCase(String.valueOf(f.get("resultadoInventario")))) where += " AND d.diferencia IS NOT NULL AND d.diferencia<>0 ";
        if ("SIN_DIFERENCIA".equalsIgnoreCase(String.valueOf(f.get("resultadoInventario")))) where += " AND d.diferencia=0 ";
        where += fechas(f, "i.fecha_programada");
        String datos = "SELECT d.id_detalle_inventario_fisico AS id,i.codigo,i.fecha_programada AS fecha,i.estado,"
                + "a.nombre AS almacen,u.nombre AS ubicacion,p.codigo_interno AS \"codigoProducto\",p.nombre AS producto,"
                + "COALESCE(d.cantidad_sistema,0) AS \"cantidadSistema\","
                + "COALESCE(d.cantidad_fisica,0) AS \"cantidadFisica\","
                + "COALESCE(d.diferencia,COALESCE(d.cantidad_fisica,0)-COALESCE(d.cantidad_sistema,0)) AS diferencia,"
                + "CONCAT(COALESCE(pe.nombres,''),' ',COALESCE(pe.apellido_paterno,''),' ',COALESCE(pe.apellido_materno,'')) AS responsable"
                + from + where + " ORDER BY i.fecha_programada DESC,i.codigo,p.nombre";
        return new Consulta(datos, "SELECT COUNT(*)" + from + where);
    }

    private Consulta consultaAjustes(Map<String, Object> f) {
        String from = " FROM detalle_ajuste_inventario d JOIN ajuste_inventario aj ON aj.id_ajuste=d.id_ajuste "
                + "JOIN producto p ON p.id_producto=d.id_producto JOIN ubicacion_almacen u ON u.id_ubicacion=d.id_ubicacion "
                + "JOIN almacen a ON a.id_almacen=u.id_almacen LEFT JOIN usuario usr ON usr.id_usuario=aj.id_usuario_creador ";
        String where = " WHERE 1=1 ";
        if (f.get("productoId") != null) where += " AND p.id_producto=:productoId ";
        if (f.get("almacenId") != null) where += " AND a.id_almacen=:almacenId ";
        if (f.get("responsableId") != null) where += " AND aj.id_usuario_creador=:responsableId ";
        if (f.get("tipoAjuste") != null) where += " AND aj.tipo=:tipoAjuste ";
        if (f.get("motivoAjuste") != null) where += " AND aj.motivo=:motivoAjuste ";
        if (f.get("estadoAjuste") != null) where += " AND aj.estado=:estadoAjuste ";
        where += fechas(f, "aj.fecha_creacion");
        String datos = "SELECT d.id_detalle_ajuste AS id,aj.codigo,aj.fecha_creacion AS fecha,aj.tipo,aj.motivo,aj.estado,"
                + "p.codigo_interno AS codigoProducto,p.nombre AS producto,a.nombre AS almacen,COALESCE(u.codigo,'Sin ubicacion') AS ubicacion,"
                + "d.stock_anterior AS \"stockAnterior\",d.cantidad,d.stock_resultante AS \"stockResultante\",d.costo_unitario AS \"costoUnitario\",d.valor_total AS \"valorTotal\","
                + "COALESCE(usr.username,'Sin usuario') AS responsable" + from + where
                + " ORDER BY aj.fecha_creacion DESC,aj.codigo,p.nombre";
        return new Consulta(datos, "SELECT COUNT(*)" + from + where);
    }

    private String filtroIds(Map<String, Object> f) {
        String w = "";
        if (f.get("productoId") != null) w += " AND p.id_producto=:productoId ";
        if (f.get("categoriaId") != null) w += " AND cat.id_categoria=:categoriaId ";
        if (f.get("almacenId") != null) w += " AND a.id_almacen=:almacenId ";
        if (f.get("ubicacionId") != null) w += " AND u.id_ubicacion=:ubicacionId ";
        return w;
    }

    private String filtroMarca(Map<String, Object> f) { return f.get("marcaId") == null ? "" : " AND p.id_marca=:marcaId "; }

    private String fechas(Map<String, Object> f, String columna) {
        String w = "";
        if (f.get("fechaInicio") != null) w += " AND " + columna + ">=CAST(:fechaInicio AS date) ";
        if (f.get("fechaFin") != null) w += " AND " + columna + "<CAST(:fechaFin AS date)+INTERVAL '1 day' ";
        return w;
    }

    private String estadoStock(Map<String, Object> f) {
        Object valor = f.get("estadoStock");
        if (valor == null) return "";
        return switch (valor.toString().toUpperCase()) {
            case "AGOTADO" -> " HAVING SUM(COALESCE(ex.cantidad,0))<=0 ";
            case "BAJO" -> " HAVING SUM(COALESCE(ex.cantidad,0))>0 AND SUM(COALESCE(ex.cantidad,0))<=COALESCE((SELECT cs.stock_minimo FROM configuracion_stock cs WHERE cs.id_producto=p.id_producto),0) ";
            case "DISPONIBLE", "NORMAL" -> " HAVING SUM(COALESCE(ex.cantidad,0))>COALESCE((SELECT cs.stock_minimo FROM configuracion_stock cs WHERE cs.id_producto=p.id_producto),0) ";
            default -> "";
        };
    }

    private String nivelStock(Map<String, Object> f) {
        Object valor = f.get("nivelStock");
        if (valor == null) return "";
        return switch (valor.toString().toUpperCase()) {
            case "AGOTADO" -> " AND SUM(COALESCE(ex.cantidad,0))<=0 ";
            case "BAJO" -> " AND SUM(COALESCE(ex.cantidad,0))>0 AND SUM(COALESCE(ex.cantidad,0))<=COALESCE((SELECT cs.stock_minimo FROM configuracion_stock cs WHERE cs.id_producto=p.id_producto),0) ";
            case "NORMAL" -> " AND SUM(COALESCE(ex.cantidad,0))>COALESCE((SELECT cs.stock_minimo FROM configuracion_stock cs WHERE cs.id_producto=p.id_producto),0) ";
            default -> "";
        };
    }

    private record Consulta(String datos, String conteo) {}
}
