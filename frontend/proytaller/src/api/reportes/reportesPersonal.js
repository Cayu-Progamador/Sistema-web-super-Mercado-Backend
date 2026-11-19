import request from '../../util/request'
export const rutasReportePersonal = { 'empleados-general':'empleados-general','contratos-general':'contratos-general','asistencia-general':'asistencia-general','atrasos-inasistencias':'atrasos-inasistencias','horas-trabajadas':'horas-trabajadas','asistencias-justificadas':'asistencias-justificadas','permisos-laborales':'permisos-laborales' }
export function consultarReportePersonal(tipo, params = {}) { return request({ url: `/api/reportes/personal/${rutasReportePersonal[tipo]}`, method:'get', params }) }
export function exportarReportePersonal(tipo, formato, params = {}) { return request({ url:`/api/reportes/personal/${rutasReportePersonal[tipo]}/${formato.toLowerCase()}`, method:'get', params, responseType:'blob' }) }
