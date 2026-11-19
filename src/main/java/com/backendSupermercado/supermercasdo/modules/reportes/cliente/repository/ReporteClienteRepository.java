package com.backendSupermercado.supermercasdo.modules.reportes.cliente.repository;

import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;

@Repository
public class ReporteClienteRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public ReporteClienteRepository(NamedParameterJdbcTemplate j) {
        jdbc = j;
    }

    public Page<Map<String, Object>> consultar(String r, Map<String, Object> f, Pageable pg) {
        MapSqlParameterSource p = new MapSqlParameterSource();
        f.forEach(p::addValue);

        String s, from, w = " WHERE 1=1 ";

        if (r.equals("clientes-general")) {
            s = "SELECT c.id_cliente AS id,c.ci AS documento,CONCAT_WS(' ',c.nombres,c.apellido_paterno,c.apellido_materno) AS cliente,c.estado,c.fecha_registro AS fecha";
            from = " FROM cliente c ";
            return page(s + from + w + " ORDER BY cliente",
                    "SELECT COUNT(*)" + from + w, p, pg);
        }

        if (r.equals("compras-cliente") || r.equals("clientes-frecuentes")) {
            s = "SELECT c.id_cliente AS id,c.ci AS documento,CONCAT_WS(' ',c.nombres,c.apellido_paterno,c.apellido_materno) AS cliente,COUNT(v.id_venta) AS compras,MAX(v.fecha_venta) AS ultima,SUM(v.total) AS total";
            from = " FROM cliente c JOIN venta v ON v.id_cliente=c.id_cliente AND v.estado='CONFIRMADA' ";
            return page(s + from + w
                            + " GROUP BY c.id_cliente,c.ci,c.nombres,c.apellido_paterno,c.apellido_materno ORDER BY compras DESC",
                    "SELECT COUNT(*) FROM (" + s + from + w
                            + " GROUP BY c.id_cliente,c.ci,c.nombres,c.apellido_paterno,c.apellido_materno)q",
                    p, pg);
        }

        from = " FROM cuenta_por_cobrar cc JOIN cliente c ON c.id_cliente=cc.id_cliente JOIN venta v ON v.id_venta=cc.id_venta ";

        if (r.equals("pagos-creditos")) {
            s = "SELECT pc.id_pago_credito AS id,pc.fecha_pago AS fecha,CONCAT_WS(' ',c.nombres,c.apellido_paterno,c.apellido_materno) AS cliente,pc.metodo_pago AS metodo,pc.monto";
            from = " FROM pago_credito pc JOIN cuenta_por_cobrar cc ON cc.id_cuenta_por_cobrar=pc.id_cuenta_por_cobrar JOIN cliente c ON c.id_cliente=cc.id_cliente ";
            return page(s + from + w + " ORDER BY pc.fecha_pago DESC",
                    "SELECT COUNT(*)" + from + w, p, pg);
        }

        if (r.equals("creditos-pendientes") || r.equals("creditos-vencidos")) {
            w += r.equals("creditos-vencidos")
                    ? " AND cc.fecha_limite<CURRENT_DATE AND cc.saldo_pendiente>0 "
                    : " AND cc.saldo_pendiente>0 ";
        }

        s = "SELECT cc.id_cuenta_por_cobrar AS id,CONCAT_WS(' ',c.nombres,c.apellido_paterno,c.apellido_materno) AS cliente,cc.monto_total AS importe,cc.saldo_pendiente AS saldo,cc.fecha_limite AS vencimiento,cc.estado";
        if (r.equals("creditos-general") || r.equals("saldos-cliente") || r.equals("recuperacion-creditos")) {
            s = "SELECT c.id_cliente AS id,CONCAT_WS(' ',c.nombres,c.apellido_paterno,c.apellido_materno) AS cliente,COUNT(cc.id_cuenta_por_cobrar) AS creditos,SUM(cc.monto_total) AS otorgado,SUM(cc.saldo_pendiente) AS saldo";
            return page(s + from + w
                            + " GROUP BY c.id_cliente,c.nombres,c.apellido_paterno,c.apellido_materno ORDER BY saldo DESC",
                    "SELECT COUNT(*) FROM (" + s + from + w
                            + " GROUP BY c.id_cliente,c.nombres,c.apellido_paterno,c.apellido_materno)q",
                    p, pg);
        }
        return page(s + from + w, "SELECT COUNT(*)" + from + w, p, pg);
    }

    /* reserved for future aggregate reports */
    /*
    s = "SELECT c.id_cliente AS id,CONCAT_WS(' ',c.nombres,c.apellido_paterno,c.apellido_materno) AS cliente,COUNT(cc.id_cuenta_por_cobrar) AS creditos,SUM(cc.monto_total) AS otorgado,SUM(cc.saldo_pendiente) AS saldo";
        return page(s + from + w
                        + " GROUP BY c.id_cliente,c.nombres,c.apellido_paterno,c.apellido_materno ORDER BY saldo DESC",
                "SELECT COUNT(*) FROM (" + s + from + w
                        + " GROUP BY c.id_cliente,c.nombres,c.apellido_paterno,c.apellido_materno)q",
                p, pg);
    }
    */

private Page<Map<String, Object>> page(String s, String c, MapSqlParameterSource p, Pageable pg) {
    p.addValue("limite", pg.getPageSize()).addValue("offset", pg.getOffset());
    return new PageImpl<>(
            jdbc.queryForList(s + " LIMIT :limite OFFSET :offset", p),
            pg,
            jdbc.queryForObject(c, p, Long.class)
    );
}
}
