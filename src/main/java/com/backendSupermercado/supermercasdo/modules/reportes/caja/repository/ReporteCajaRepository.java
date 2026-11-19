package com.backendSupermercado.supermercasdo.modules.reportes.caja.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReporteCajaRepository {
    private final NamedParameterJdbcTemplate jdbc;
    public ReporteCajaRepository(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Page<Map<String,Object>> consultar(String ruta, Map<String,Object> filtros, Pageable pageable) {
        MapSqlParameterSource p = parametros(filtros);
        Consulta consulta = consulta(ruta, filtros, p);
        String sql = consulta.datos + " LIMIT :limite OFFSET :desplazamiento";
        p.addValue("limite", pageable.isPaged() ? pageable.getPageSize() : Integer.MAX_VALUE);
        p.addValue("desplazamiento", pageable.isPaged() ? pageable.getOffset() : 0);
        List<Map<String,Object>> datos = jdbc.queryForList(sql, p);
        long total = jdbc.queryForObject(consulta.conteo, p, Long.class);
        return new PageImpl<>(datos, pageable, total);
    }

    private Consulta consulta(String ruta, Map<String,Object> f, MapSqlParameterSource p) {
        String fecha = "";
        if (f.get("fechaInicio") != null) { fecha += " AND sc.fecha_apertura >= CAST(:fechaInicio AS date) "; }
        if (f.get("fechaFin") != null) { fecha += " AND sc.fecha_apertura < CAST(:fechaFin AS date) + INTERVAL '1 day' "; }
        String filtros = fecha;
        if (f.get("cajaId") != null) filtros += " AND sc.id_caja = :cajaId ";
        if (f.get("cajeroId") != null) filtros += " AND sc.id_usuario = :cajeroId ";
        if (f.get("estadoSesion") != null) filtros += " AND sc.estado = :estadoSesion ";
        if ("sesiones-caja".equals(ruta)) {
            String from = " FROM sesion_caja sc JOIN caja ca ON ca.id_caja=sc.id_caja JOIN usuario u ON u.id_usuario=sc.id_usuario LEFT JOIN empleado em ON em.id_empleado=u.id_empleado LEFT JOIN persona pe ON pe.id_persona=em.id_persona ";
            String select = "SELECT sc.id_sesion_caja AS id, CONCAT('SES-', LPAD(sc.id_sesion_caja::text,4,'0')) AS sesion, sc.fecha_apertura AS fecha, ca.nombre AS caja, COALESCE(NULLIF(TRIM(CONCAT_WS(' ',pe.nombres,pe.apellido_paterno,pe.apellido_materno)),''),u.username) AS cajero, sc.monto_inicial AS apertura, sc.efectivo_contado AS cierre, sc.monto_esperado AS esperado, sc.diferencia, sc.estado";
            return q(select + from + " WHERE 1=1 " + filtros + " ORDER BY sc.fecha_apertura DESC, sc.id_sesion_caja DESC", "SELECT COUNT(*)" + from + " WHERE 1=1 " + filtros);
        }
        if ("conciliacion-caja".equals(ruta)) {
            String from = " FROM sesion_caja sc JOIN caja ca ON ca.id_caja=sc.id_caja JOIN usuario u ON u.id_usuario=sc.id_usuario LEFT JOIN empleado em ON em.id_empleado=u.id_empleado LEFT JOIN persona pe ON pe.id_persona=em.id_persona ";
            String select = "SELECT sc.id_sesion_caja AS id, CONCAT('SES-', LPAD(sc.id_sesion_caja::text,4,'0')) AS sesion, sc.fecha_apertura AS fecha, ca.nombre AS caja, COALESCE(NULLIF(TRIM(CONCAT_WS(' ',pe.nombres,pe.apellido_paterno,pe.apellido_materno)),''),u.username) AS cajero, sc.monto_esperado AS esperado, sc.efectivo_contado AS contado, sc.diferencia, CASE WHEN sc.diferencia IS NULL THEN 'PENDIENTE' WHEN sc.diferencia=0 THEN 'SIN_DIFERENCIA' WHEN sc.diferencia<0 THEN 'FALTANTE' ELSE 'SOBRANTE' END AS resultado";
            if (f.get("resultado") != null) filtros += " AND (CASE WHEN sc.diferencia IS NULL THEN 'PENDIENTE' WHEN sc.diferencia=0 THEN 'SIN_DIFERENCIA' WHEN sc.diferencia<0 THEN 'FALTANTE' ELSE 'SOBRANTE' END) = :resultado ";
            return q(select + from + " WHERE 1=1 " + filtros + " ORDER BY sc.fecha_apertura DESC", "SELECT COUNT(*)" + from + " WHERE 1=1 " + filtros);
        }
        if ("ventas-sesion-caja".equals(ruta)) {
            String from = " FROM sesion_caja sc JOIN caja ca ON ca.id_caja=sc.id_caja JOIN usuario u ON u.id_usuario=sc.id_usuario LEFT JOIN empleado em ON em.id_empleado=u.id_empleado LEFT JOIN persona pe ON pe.id_persona=em.id_persona LEFT JOIN venta v ON v.id_sesion_caja=sc.id_sesion_caja AND v.estado='CONFIRMADA' ";
            String filtrosSesion = filtros;
            if (f.get("sesionId") != null) filtrosSesion += " AND sc.id_sesion_caja = :sesionId ";
            if (f.get("metodoPago") != null) filtrosSesion += " AND EXISTS (SELECT 1 FROM pago_venta px WHERE px.id_venta=v.id_venta AND px.metodo_pago=:metodoPago) ";
            String group = " GROUP BY sc.id_sesion_caja,sc.fecha_apertura,ca.nombre,u.username,pe.nombres,pe.apellido_paterno,pe.apellido_materno,sc.estado ";
            String select = "SELECT sc.id_sesion_caja AS id, CONCAT('SES-', LPAD(sc.id_sesion_caja::text,4,'0')) AS sesion, sc.fecha_apertura AS fecha, ca.nombre AS caja, COALESCE(NULLIF(TRIM(CONCAT_WS(' ',pe.nombres,pe.apellido_paterno,pe.apellido_materno)),''),u.username) AS cajero, COUNT(v.id_venta) AS ventas, COALESCE(SUM(v.total),0) AS recaudacion, sc.estado";
            return q(select + from + " WHERE 1=1 " + filtrosSesion + group + " ORDER BY sc.fecha_apertura DESC", "SELECT COUNT(*) FROM (SELECT sc.id_sesion_caja" + from + " WHERE 1=1 " + filtrosSesion + group + ") q");
        }
        if ("recaudacion-metodo".equals(ruta)) {
            String from = " FROM pago_venta pv JOIN venta v ON v.id_venta=pv.id_venta AND v.estado='CONFIRMADA' JOIN sesion_caja sc ON sc.id_sesion_caja=v.id_sesion_caja ";
            String where = filtros.replace("sc.fecha_apertura", "pv.fecha_pago");
            if (f.get("metodoPago") != null) where += " AND pv.metodo_pago=:metodoPago ";
            String select = "SELECT pv.metodo_pago AS metodo, COUNT(DISTINCT v.id_venta) AS operaciones, COUNT(DISTINCT sc.id_sesion_caja) AS sesiones, COALESCE(100*SUM(pv.total_recibido)/NULLIF(SUM(SUM(pv.total_recibido)) OVER(),0),0) AS porcentaje, SUM(pv.total_recibido) AS total";
            return q(select + from + " WHERE 1=1 " + where + " GROUP BY pv.metodo_pago ORDER BY total DESC", "SELECT COUNT(*) FROM (SELECT pv.metodo_pago" + from + " WHERE 1=1 " + where + " GROUP BY pv.metodo_pago) q");
        }
        if ("rendimiento-cajero".equals(ruta)) {
            String from = " FROM sesion_caja sc JOIN usuario u ON u.id_usuario=sc.id_usuario LEFT JOIN empleado em ON em.id_empleado=u.id_empleado LEFT JOIN persona pe ON pe.id_persona=em.id_persona LEFT JOIN venta v ON v.id_sesion_caja=sc.id_sesion_caja AND v.estado='CONFIRMADA' ";
            String select = "SELECT u.id_usuario AS id, COALESCE(NULLIF(TRIM(CONCAT_WS(' ',pe.nombres,pe.apellido_paterno,pe.apellido_materno)),''),u.username) AS cajero, COUNT(DISTINCT sc.id_sesion_caja) AS sesiones, COUNT(v.id_venta) AS ventas, COALESCE(SUM(v.total),0) AS recaudacion, COALESCE(SUM(v.total)/NULLIF(COUNT(v.id_venta),0),0) AS promedio, COALESCE(SUM(sc.diferencia),0) AS diferencia";
            return q(select + from + " WHERE 1=1 " + filtros + " GROUP BY u.id_usuario,u.username,pe.nombres,pe.apellido_paterno,pe.apellido_materno ORDER BY recaudacion DESC", "SELECT COUNT(*) FROM (SELECT u.id_usuario" + from + " WHERE 1=1 " + filtros + " GROUP BY u.id_usuario) q");
        }
        // Movimientos reales derivados de aperturas y pagos de ventas de cada sesión.
        String where = filtros.replace("sc.fecha_apertura", "mov.fecha");
        String from = " FROM (SELECT sc.id_sesion_caja,sc.id_caja,sc.id_usuario,sc.fecha_apertura AS fecha,sc.monto_inicial AS monto,'INGRESO' AS tipo,'FONDO_INICIAL' AS concepto FROM sesion_caja sc UNION ALL SELECT sc.id_sesion_caja,sc.id_caja,sc.id_usuario,pv.fecha_pago,pv.total_recibido,'INGRESO','VENTA' FROM pago_venta pv JOIN venta v ON v.id_venta=pv.id_venta AND v.estado='CONFIRMADA' JOIN sesion_caja sc ON sc.id_sesion_caja=v.id_sesion_caja) mov JOIN sesion_caja sc ON sc.id_sesion_caja=mov.id_sesion_caja JOIN caja ca ON ca.id_caja=mov.id_caja JOIN usuario u ON u.id_usuario=mov.id_usuario LEFT JOIN empleado em ON em.id_empleado=u.id_empleado LEFT JOIN persona pe ON pe.id_persona=em.id_persona ";
        if (f.get("tipoMovimiento") != null) where += " AND mov.tipo=:tipoMovimiento ";
        if (f.get("concepto") != null) where += " AND mov.concepto=:concepto ";
        String select = "SELECT CONCAT(mov.id_sesion_caja,'-',EXTRACT(EPOCH FROM mov.fecha)::bigint) AS id, mov.fecha, CONCAT('SES-',LPAD(mov.id_sesion_caja::text,4,'0')) AS sesion, mov.tipo, mov.concepto, COALESCE(NULLIF(TRIM(CONCAT_WS(' ',pe.nombres,pe.apellido_paterno,pe.apellido_materno)),''),u.username) AS responsable, ca.nombre AS caja, mov.monto";
        return q(select + from + " WHERE 1=1 " + where + " ORDER BY mov.fecha DESC", "SELECT COUNT(*)" + from + " WHERE 1=1 " + where);
    }

    private Consulta q(String datos, String conteo) { return new Consulta(datos, conteo); }
    private MapSqlParameterSource parametros(Map<String,Object> f) { MapSqlParameterSource p = new MapSqlParameterSource(); f.forEach(p::addValue); return p; }
    private record Consulta(String datos, String conteo) {}
}
