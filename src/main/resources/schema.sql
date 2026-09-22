ALTER TABLE IF EXISTS pago_credito
    ADD COLUMN IF NOT EXISTS id_sesion_caja BIGINT;

ALTER TABLE IF EXISTS menu
    ADD COLUMN IF NOT EXISTS id_menu_padre BIGINT;

ALTER TABLE IF EXISTS menu
    DROP CONSTRAINT IF EXISTS fk_menu_padre;

ALTER TABLE IF EXISTS menu
    ADD CONSTRAINT fk_menu_padre
    FOREIGN KEY (id_menu_padre) REFERENCES menu(id_menu);

INSERT INTO menu (nombre, ruta, icono, orden, visible, id_menu_padre)
VALUES
    ('Inicio', '/', 'home', 1, true, NULL),
    ('Seguridad', '#seguridad', 'security', 2, true, NULL),
    ('Personal', '#personal', 'people', 3, true, NULL),
    ('Compras', '#compras', 'shopping_bag', 4, true, NULL),
    ('Inventario', '#inventario', 'inventory_2', 5, true, NULL),
    ('Ventas', '#ventas', 'point_of_sale', 6, true, NULL),
    ('Caja', '/caja', 'account_balance_wallet', 7, true, NULL)
ON CONFLICT (ruta) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    icono = EXCLUDED.icono,
    orden = EXCLUDED.orden,
    visible = EXCLUDED.visible;

INSERT INTO menu (nombre, ruta, icono, orden, visible, id_menu_padre)
SELECT x.nombre, x.ruta, x.icono, x.orden, true, p.id_menu
FROM (VALUES
    ('Usuarios', '/usuarios', 'person', 1, '#seguridad'),
    ('Roles', '/roles', 'admin_panel_settings', 2, '#seguridad'),
    ('Permisos del sistema', '/permisos-sistema', 'lock', 3, '#seguridad'),
    ('Perfil', '/perfil', 'account_circle', 4, '#seguridad'),
    ('Empleados', '/empleado', 'badge', 1, '#personal'),
    ('Cargos', '/cargos', 'work', 2, '#personal'),
    ('Contratos', '/contratos', 'description', 3, '#personal'),
    ('Asistencia', '/asistencia', 'event_available', 4, '#personal'),
    ('Proveedores', '/proveedores', 'local_shipping', 1, '#compras'),
    ('Pedidos', '/pedidos', 'shopping_cart', 2, '#compras'),
    ('Recepción de mercadería', '/recepcion-mercaderia', 'move_to_inbox', 3, '#compras'),
    ('Devoluciones', '/devoluciones', 'assignment_return', 4, '#compras'),
    ('Productos', '/productos', 'inventory', 1, '#inventario'),
    ('Categorías', '/categorias', 'category', 2, '#inventario'),
    ('Unidades de medida', '/unidad_medida', 'straighten', 3, '#inventario'),
    ('Marcas', '/marcas', 'sell', 4, '#inventario'),
    ('Almacenes', '/almacenes', 'warehouse', 5, '#inventario'),
    ('Inventario', '/inventario', 'inventory_2', 6, '#inventario'),
    ('Inventario físico', '/inventario-fisico', 'fact_check', 7, '#inventario'),
    ('Ubicaciones', '/ubicaciones-almacen', 'location_on', 8, '#inventario'),
    ('Transferencias', '/transferencias-almacen', 'swap_horiz', 9, '#inventario'),
    ('Ajustes de inventario', '/ajustes-inventario', 'tune', 10, '#inventario'),
    ('Nueva venta', '/ventas', 'shopping_cart', 1, '#ventas'),
    ('Historial de ventas', '/historial-ventas', 'history', 2, '#ventas'),
    ('Crédito', '/gestion-credito', 'credit_score', 3, '#ventas'),
    ('Clientes', '/clientes', 'groups', 4, '#ventas')
) AS x(nombre, ruta, icono, orden, padre_ruta)
JOIN menu p ON p.ruta = x.padre_ruta
ON CONFLICT (ruta) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    icono = EXCLUDED.icono,
    orden = EXCLUDED.orden,
    visible = EXCLUDED.visible,
    id_menu_padre = EXCLUDED.id_menu_padre;

-- Normalización final: nombres y jerarquía idénticos al Aside.vue.
UPDATE menu SET nombre = 'Dashboard', orden = 1, visible = true, id_menu_padre = NULL WHERE ruta = '/';
UPDATE menu SET nombre = 'Clientes', orden = 2, visible = true, id_menu_padre = NULL WHERE ruta = '/clientes';
UPDATE menu SET nombre = 'Ventas', orden = 3, visible = true, id_menu_padre = NULL WHERE ruta = '#ventas';
UPDATE menu SET nombre = 'Caja', orden = 4, visible = true, id_menu_padre = NULL WHERE ruta = '/caja';
UPDATE menu SET nombre = 'Seguridad', orden = 5, visible = true, id_menu_padre = NULL WHERE ruta = '#seguridad';
UPDATE menu SET nombre = 'Empleado', orden = 6, visible = true, id_menu_padre = NULL WHERE ruta = '#personal';
UPDATE menu SET nombre = 'Proveedor', orden = 7, visible = true, id_menu_padre = NULL WHERE ruta = '#compras-proveedor';
UPDATE menu SET nombre = 'Productos', orden = 8, visible = true, id_menu_padre = NULL WHERE ruta = '#compras-productos';
UPDATE menu SET nombre = 'Almacén', orden = 9, visible = true, id_menu_padre = NULL WHERE ruta = '#inventario-almacen';
UPDATE menu SET nombre = 'Inventario', orden = 10, visible = true, id_menu_padre = NULL WHERE ruta = '#inventario';

UPDATE menu SET nombre = 'Nueva venta', id_menu_padre = (SELECT id_menu FROM menu WHERE ruta = '#ventas') WHERE ruta = '/ventas';
UPDATE menu SET nombre = 'Historial de ventas', id_menu_padre = (SELECT id_menu FROM menu WHERE ruta = '#ventas') WHERE ruta = '/historial-ventas';
UPDATE menu SET nombre = 'Crédito', id_menu_padre = (SELECT id_menu FROM menu WHERE ruta = '#ventas') WHERE ruta = '/gestion-credito';
UPDATE menu SET id_menu_padre = NULL WHERE ruta = '/clientes';

-- Perfil solo admite consulta y edición; crear y aprobar no aplican.
DELETE FROM rol_permiso rp
USING permiso p
WHERE rp.id_permiso = p.id_permiso
  AND p.codigo IN ('PERFIL_CREAR', 'PERFIL_APROBAR');

DELETE FROM menu_permiso mp
USING menu m, permiso p
WHERE mp.id_menu = m.id_menu
  AND mp.id_permiso = p.id_permiso
  AND m.ruta = '/perfil'
  AND p.codigo IN ('PERFIL_CREAR', 'PERFIL_APROBAR');

-- Dashboard solo admite consulta.
INSERT INTO permiso (codigo, nombre, estado)
VALUES ('DASHBOARD_VER', 'Ver dashboard', true)
ON CONFLICT (codigo) DO UPDATE SET nombre = EXCLUDED.nombre, estado = true;

INSERT INTO menu_permiso (id_menu, id_permiso)
SELECT m.id_menu, p.id_permiso
FROM menu m
JOIN permiso p ON p.codigo = 'DASHBOARD_VER'
WHERE m.ruta = '/'
  AND NOT EXISTS (
      SELECT 1 FROM menu_permiso mp
      WHERE mp.id_menu = m.id_menu AND mp.id_permiso = p.id_permiso
  );

-- Mantener únicamente las acciones que realmente admite cada submódulo.
WITH configuracion(ruta, prefijo, acciones) AS (
    VALUES
        ('/', 'DASHBOARD', ARRAY['VER']),
        ('/clientes', 'CLIENTES', ARRAY['VER','CREAR','EDITAR']),
        ('/ventas', 'NUEVA_VENTA', ARRAY['VER','CREAR']),
        ('/historial-ventas', 'HISTORIAL_VENTAS', ARRAY['VER','EDITAR']),
        ('/gestion-credito', 'CREDITO', ARRAY['VER','EDITAR']),
        ('/caja', 'CAJA', ARRAY['VER','CREAR','EDITAR']),
        ('/usuarios', 'USUARIOS', ARRAY['VER','CREAR','EDITAR']),
        ('/roles', 'ROLES', ARRAY['VER','CREAR','EDITAR']),
        ('/perfil', 'PERFIL', ARRAY['VER','EDITAR']),
        ('/permisos-sistema', 'PERMISOS_SISTEMA', ARRAY['VER','EDITAR']),
        ('/empleado', 'EMPLEADOS', ARRAY['VER','CREAR','EDITAR']),
        ('/cargos', 'CARGOS', ARRAY['VER','CREAR','EDITAR']),
        ('/contratos', 'CONTRATOS', ARRAY['VER','CREAR','EDITAR']),
        ('/asistencia/admin', 'PANEL_ASISTENCIA', ARRAY['VER','CREAR','EDITAR']),
        ('/permisos-admin', 'ADMIN_PERMISOS', ARRAY['VER','EDITAR','APROBAR']),
        ('/proveedores', 'PROVEEDORES', ARRAY['VER','CREAR','EDITAR']),
        ('/pedidos', 'PEDIDOS', ARRAY['VER','CREAR','EDITAR']),
        ('/recepcion-mercaderia', 'RECEPCION', ARRAY['VER','CREAR','EDITAR']),
        ('/devoluciones', 'DEVOLUCIONES', ARRAY['VER','CREAR','EDITAR']),
        ('/productos', 'PRODUCTOS', ARRAY['VER','CREAR','EDITAR']),
        ('/categorias', 'CATEGORIAS', ARRAY['VER','CREAR','EDITAR']),
        ('/marcas', 'MARCAS', ARRAY['VER','CREAR','EDITAR']),
        ('/unidad_medida', 'UNIDAD_MEDIDA', ARRAY['VER','CREAR','EDITAR']),
        ('/almacenes', 'ALMACEN', ARRAY['VER','CREAR','EDITAR']),
        ('/ubicaciones-almacen', 'UBICACIONES', ARRAY['VER','CREAR','EDITAR']),
        ('/transferencias-almacen', 'TRANSFERENCIAS', ARRAY['VER','CREAR','EDITAR','APROBAR']),
        ('/inventario', 'INVENTARIO', ARRAY['VER','EDITAR']),
        ('/inventario-fisico', 'INVENTARIO_FISICO', ARRAY['VER','CREAR','EDITAR','APROBAR']),
        ('/ajustes-inventario', 'AJUSTES_INVENTARIO', ARRAY['VER','CREAR','EDITAR','APROBAR'])
)
DELETE FROM menu_permiso mp
USING menu m, permiso p, configuracion c
WHERE mp.id_menu = m.id_menu
  AND mp.id_permiso = p.id_permiso
  AND m.ruta = c.ruta
  AND p.codigo LIKE c.prefijo || '\_%' ESCAPE '\'
  AND NOT EXISTS (
      SELECT 1 FROM unnest(c.acciones) AS a(accion)
      WHERE p.codigo = c.prefijo || '_' || a.accion
  );

WITH configuracion(prefijo, acciones) AS (
    VALUES
        ('DASHBOARD', ARRAY['VER']), ('CLIENTES', ARRAY['VER','CREAR','EDITAR']),
        ('NUEVA_VENTA', ARRAY['VER','CREAR']), ('HISTORIAL_VENTAS', ARRAY['VER','EDITAR']),
        ('CREDITO', ARRAY['VER','EDITAR']), ('CAJA', ARRAY['VER','CREAR','EDITAR']),
        ('USUARIOS', ARRAY['VER','CREAR','EDITAR']), ('ROLES', ARRAY['VER','CREAR','EDITAR']),
        ('PERFIL', ARRAY['VER','EDITAR']), ('PERMISOS_SISTEMA', ARRAY['VER','EDITAR']),
        ('EMPLEADOS', ARRAY['VER','CREAR','EDITAR']), ('CARGOS', ARRAY['VER','CREAR','EDITAR']),
        ('CONTRATOS', ARRAY['VER','CREAR','EDITAR']), ('PANEL_ASISTENCIA', ARRAY['VER','CREAR','EDITAR']),
        ('ADMIN_PERMISOS', ARRAY['VER','EDITAR','APROBAR']), ('PROVEEDORES', ARRAY['VER','CREAR','EDITAR']),
        ('PEDIDOS', ARRAY['VER','CREAR','EDITAR']), ('RECEPCION', ARRAY['VER','CREAR','EDITAR']),
        ('DEVOLUCIONES', ARRAY['VER','CREAR','EDITAR']), ('PRODUCTOS', ARRAY['VER','CREAR','EDITAR']),
        ('CATEGORIAS', ARRAY['VER','CREAR','EDITAR']), ('MARCAS', ARRAY['VER','CREAR','EDITAR']),
        ('UNIDAD_MEDIDA', ARRAY['VER','CREAR','EDITAR']), ('ALMACEN', ARRAY['VER','CREAR','EDITAR']),
        ('UBICACIONES', ARRAY['VER','CREAR','EDITAR']), ('TRANSFERENCIAS', ARRAY['VER','CREAR','EDITAR','APROBAR']),
        ('INVENTARIO', ARRAY['VER','EDITAR']), ('INVENTARIO_FISICO', ARRAY['VER','CREAR','EDITAR','APROBAR']),
        ('AJUSTES_INVENTARIO', ARRAY['VER','CREAR','EDITAR','APROBAR'])
)
DELETE FROM rol_permiso rp
USING permiso p, configuracion c
WHERE rp.id_permiso = p.id_permiso
  AND p.codigo LIKE c.prefijo || '\_%' ESCAPE '\'
  AND NOT EXISTS (
      SELECT 1 FROM unnest(c.acciones) AS a(accion)
      WHERE p.codigo = c.prefijo || '_' || a.accion
  );

-- Permisos independientes por submódulo. Cada checkbox usa un id_permiso distinto.
WITH modulos(ruta, prefijo, etiqueta) AS (
    VALUES
        ('/clientes', 'CLIENTES', 'Clientes'),
        ('/ventas', 'NUEVA_VENTA', 'Nueva venta'),
        ('/historial-ventas', 'HISTORIAL_VENTAS', 'Historial de ventas'),
        ('/gestion-credito', 'CREDITO', 'Crédito'),
        ('/caja', 'CAJA', 'Caja'),
        ('/usuarios', 'USUARIOS', 'Usuarios'),
        ('/roles', 'ROLES', 'Roles'),
        ('/perfil', 'PERFIL', 'Perfil'),
        ('/permisos-sistema', 'PERMISOS_SISTEMA', 'Permisos del sistema'),
        ('/empleado', 'EMPLEADOS', 'Empleados'),
        ('/cargos', 'CARGOS', 'Cargos'),
        ('/contratos', 'CONTRATOS', 'Contratos'),
        ('/asistencia/admin', 'PANEL_ASISTENCIA', 'Panel asistencia'),
        ('/permisos-admin', 'ADMIN_PERMISOS', 'Admin permisos'),
        ('/proveedores', 'PROVEEDORES', 'Proveedores'),
        ('/pedidos', 'PEDIDOS', 'Pedidos'),
        ('/recepcion-mercaderia', 'RECEPCION', 'Recepción'),
        ('/devoluciones', 'DEVOLUCIONES', 'Devoluciones'),
        ('/productos', 'PRODUCTOS', 'Productos'),
        ('/categorias', 'CATEGORIAS', 'Categorías'),
        ('/marcas', 'MARCAS', 'Marcas'),
        ('/unidad_medida', 'UNIDAD_MEDIDA', 'Unidad de medida'),
        ('/almacenes', 'ALMACEN', 'Almacén'),
        ('/ubicaciones-almacen', 'UBICACIONES', 'Ubicaciones'),
        ('/transferencias-almacen', 'TRANSFERENCIAS', 'Transferencias'),
        ('/inventario', 'INVENTARIO', 'Inventario'),
        ('/inventario-fisico', 'INVENTARIO_FISICO', 'Inventario físico'),
        ('/ajustes-inventario', 'AJUSTES_INVENTARIO', 'Ajustes de inventario')
), acciones(codigo, etiqueta) AS (
    VALUES ('VER', 'Ver'), ('CREAR', 'Crear'), ('EDITAR', 'Editar'), ('APROBAR', 'Aprobar')
)
INSERT INTO permiso (codigo, nombre, estado)
SELECT m.prefijo || '_' || a.codigo, a.etiqueta || ' ' || m.etiqueta, true
FROM modulos m CROSS JOIN acciones a
ON CONFLICT (codigo) DO UPDATE SET nombre = EXCLUDED.nombre, estado = true;

-- Sustituir las relaciones compartidas por relaciones exclusivas de cada submódulo.
WITH modulos(ruta, prefijo) AS (
    VALUES
        ('/clientes', 'CLIENTES'), ('/ventas', 'NUEVA_VENTA'),
        ('/historial-ventas', 'HISTORIAL_VENTAS'), ('/gestion-credito', 'CREDITO'),
        ('/caja', 'CAJA'), ('/usuarios', 'USUARIOS'), ('/roles', 'ROLES'),
        ('/perfil', 'PERFIL'), ('/permisos-sistema', 'PERMISOS_SISTEMA'),
        ('/empleado', 'EMPLEADOS'), ('/cargos', 'CARGOS'), ('/contratos', 'CONTRATOS'),
        ('/asistencia/admin', 'PANEL_ASISTENCIA'), ('/permisos-admin', 'ADMIN_PERMISOS'),
        ('/proveedores', 'PROVEEDORES'), ('/pedidos', 'PEDIDOS'),
        ('/recepcion-mercaderia', 'RECEPCION'), ('/devoluciones', 'DEVOLUCIONES'),
        ('/productos', 'PRODUCTOS'), ('/categorias', 'CATEGORIAS'),
        ('/marcas', 'MARCAS'), ('/unidad_medida', 'UNIDAD_MEDIDA'),
        ('/almacenes', 'ALMACEN'), ('/ubicaciones-almacen', 'UBICACIONES'),
        ('/transferencias-almacen', 'TRANSFERENCIAS'), ('/inventario', 'INVENTARIO'),
        ('/inventario-fisico', 'INVENTARIO_FISICO'), ('/ajustes-inventario', 'AJUSTES_INVENTARIO')
)
DELETE FROM menu_permiso mp
USING menu m, modulos d
WHERE mp.id_menu = m.id_menu AND m.ruta = d.ruta;

WITH modulos(ruta, prefijo) AS (
    VALUES
        ('/clientes', 'CLIENTES'), ('/ventas', 'NUEVA_VENTA'),
        ('/historial-ventas', 'HISTORIAL_VENTAS'), ('/gestion-credito', 'CREDITO'),
        ('/caja', 'CAJA'), ('/usuarios', 'USUARIOS'), ('/roles', 'ROLES'),
        ('/perfil', 'PERFIL'), ('/permisos-sistema', 'PERMISOS_SISTEMA'),
        ('/empleado', 'EMPLEADOS'), ('/cargos', 'CARGOS'), ('/contratos', 'CONTRATOS'),
        ('/asistencia/admin', 'PANEL_ASISTENCIA'), ('/permisos-admin', 'ADMIN_PERMISOS'),
        ('/proveedores', 'PROVEEDORES'), ('/pedidos', 'PEDIDOS'),
        ('/recepcion-mercaderia', 'RECEPCION'), ('/devoluciones', 'DEVOLUCIONES'),
        ('/productos', 'PRODUCTOS'), ('/categorias', 'CATEGORIAS'),
        ('/marcas', 'MARCAS'), ('/unidad_medida', 'UNIDAD_MEDIDA'),
        ('/almacenes', 'ALMACEN'), ('/ubicaciones-almacen', 'UBICACIONES'),
        ('/transferencias-almacen', 'TRANSFERENCIAS'), ('/inventario', 'INVENTARIO'),
        ('/inventario-fisico', 'INVENTARIO_FISICO'), ('/ajustes-inventario', 'AJUSTES_INVENTARIO')
), acciones(codigo) AS (VALUES ('VER'), ('CREAR'), ('EDITAR'), ('APROBAR'))
INSERT INTO menu_permiso (id_menu, id_permiso)
SELECT m.id_menu, p.id_permiso
FROM modulos d
JOIN menu m ON m.ruta = d.ruta
CROSS JOIN acciones a
JOIN permiso p ON p.codigo = d.prefijo || '_' || a.codigo
WHERE NOT EXISTS (
    SELECT 1 FROM menu_permiso mp
    WHERE mp.id_menu = m.id_menu AND mp.id_permiso = p.id_permiso
);

-- Permisos específicos de edición y aprobación por módulo.
INSERT INTO permiso (codigo, nombre, estado)
VALUES
    ('VENTAS_EDITAR', 'Editar ventas', true), ('VENTAS_APROBAR', 'Aprobar ventas', true),
    ('CLIENTES_EDITAR', 'Editar clientes', true), ('CLIENTES_APROBAR', 'Aprobar clientes', true),
    ('CREDITO_EDITAR', 'Editar créditos', true), ('CREDITO_APROBAR', 'Aprobar créditos', true),
    ('CAJA_EDITAR', 'Editar cajas', true), ('CAJA_APROBAR', 'Aprobar cajas', true),
    ('CATALOGOS_EDITAR', 'Editar catálogos', true), ('CATALOGOS_APROBAR', 'Aprobar catálogos', true),
    ('COMPRAS_EDITAR', 'Editar compras', true), ('COMPRAS_APROBAR', 'Aprobar compras', true),
    ('PERSONAL_EDITAR', 'Editar personal', true), ('PERSONAL_APROBAR', 'Aprobar personal', true),
    ('INVENTARIO_EDITAR', 'Editar inventario', true), ('INVENTARIO_APROBAR', 'Aprobar inventario', true),
    ('SEGURIDAD_APROBAR', 'Aprobar cambios de seguridad', true)
ON CONFLICT (codigo) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    estado = EXCLUDED.estado;

INSERT INTO menu_permiso (id_menu, id_permiso)
SELECT m.id_menu, p.id_permiso
FROM (VALUES
    ('/ventas', 'VENTAS_EDITAR'), ('/ventas', 'VENTAS_APROBAR'),
    ('/clientes', 'CLIENTES_EDITAR'), ('/clientes', 'CLIENTES_APROBAR'),
    ('/gestion-credito', 'CREDITO_EDITAR'), ('/gestion-credito', 'CREDITO_APROBAR'),
    ('/caja', 'CAJA_EDITAR'), ('/caja', 'CAJA_APROBAR'),
    ('/productos', 'CATALOGOS_EDITAR'), ('/productos', 'CATALOGOS_APROBAR'),
    ('/categorias', 'CATALOGOS_EDITAR'), ('/categorias', 'CATALOGOS_APROBAR'),
    ('/marcas', 'CATALOGOS_EDITAR'), ('/marcas', 'CATALOGOS_APROBAR'),
    ('/unidad_medida', 'CATALOGOS_EDITAR'), ('/unidad_medida', 'CATALOGOS_APROBAR'),
    ('/proveedores', 'COMPRAS_EDITAR'), ('/proveedores', 'COMPRAS_APROBAR'),
    ('/pedidos', 'COMPRAS_EDITAR'), ('/pedidos', 'COMPRAS_APROBAR'),
    ('/recepcion-mercaderia', 'COMPRAS_EDITAR'), ('/recepcion-mercaderia', 'COMPRAS_APROBAR'),
    ('/devoluciones', 'COMPRAS_EDITAR'), ('/devoluciones', 'COMPRAS_APROBAR'),
    ('/empleado', 'PERSONAL_EDITAR'), ('/empleado', 'PERSONAL_APROBAR'),
    ('/cargos', 'PERSONAL_EDITAR'), ('/cargos', 'PERSONAL_APROBAR'),
    ('/contratos', 'PERSONAL_EDITAR'), ('/contratos', 'PERSONAL_APROBAR'),
    ('/almacenes', 'INVENTARIO_EDITAR'), ('/almacenes', 'INVENTARIO_APROBAR'),
    ('/ubicaciones-almacen', 'INVENTARIO_EDITAR'), ('/ubicaciones-almacen', 'INVENTARIO_APROBAR'),
    ('/transferencias-almacen', 'INVENTARIO_EDITAR'), ('/transferencias-almacen', 'INVENTARIO_APROBAR'),
    ('/inventario', 'INVENTARIO_EDITAR'), ('/inventario', 'INVENTARIO_APROBAR'),
    ('/inventario-fisico', 'INVENTARIO_EDITAR'), ('/inventario-fisico', 'INVENTARIO_APROBAR'),
    ('/ajustes-inventario', 'INVENTARIO_EDITAR'), ('/ajustes-inventario', 'INVENTARIO_APROBAR'),
    ('/permisos-sistema', 'SEGURIDAD_APROBAR')
) AS x(ruta, codigo)
JOIN menu m ON m.ruta = x.ruta
JOIN permiso p ON p.codigo = x.codigo
WHERE NOT EXISTS (
    SELECT 1 FROM menu_permiso mp
    WHERE mp.id_menu = m.id_menu AND mp.id_permiso = p.id_permiso
);

-- Relaciones de permisos para los menús creados en la segunda carga de datos.
INSERT INTO menu_permiso (id_menu, id_permiso)
SELECT m.id_menu, p.id_permiso
FROM (VALUES
    ('/asistencia', 'PERSONAL_VER'), ('/asistencia/admin', 'PERSONAL_VER'),
    ('/permisos', 'PERSONAL_VER'), ('/permisos-admin', 'PERSONAL_VER'),
    ('/almacenes', 'INVENTARIO_VER'), ('/ubicaciones-almacen', 'INVENTARIO_VER'),
    ('/transferencias-almacen', 'INVENTARIO_VER'), ('/ajustes-inventario', 'INVENTARIO_VER'),
    ('/almacenes', 'INVENTARIO_CREAR'), ('/ubicaciones-almacen', 'INVENTARIO_CREAR'),
    ('/transferencias-almacen', 'INVENTARIO_CREAR'), ('/ajustes-inventario', 'INVENTARIO_CREAR'),
    ('/proveedores', 'COMPRAS_VER'), ('/pedidos', 'COMPRAS_VER'),
    ('/recepcion-mercaderia', 'COMPRAS_VER'), ('/devoluciones', 'COMPRAS_VER')
) AS x(ruta, codigo)
JOIN menu m ON m.ruta = x.ruta
JOIN permiso p ON p.codigo = x.codigo
WHERE NOT EXISTS (
    SELECT 1 FROM menu_permiso mp
    WHERE mp.id_menu = m.id_menu AND mp.id_permiso = p.id_permiso
);

-- Sincronizar la jerarquía de menu con Aside.vue
UPDATE menu SET nombre = 'Dashboard', orden = 1, visible = true, id_menu_padre = NULL WHERE ruta = '/';
UPDATE menu SET nombre = 'Clientes', orden = 2, visible = true, id_menu_padre = NULL WHERE ruta = '/clientes';
UPDATE menu SET nombre = 'Ventas', orden = 3, visible = true, id_menu_padre = NULL WHERE ruta = '#ventas';
UPDATE menu SET nombre = 'Caja', orden = 4, visible = true, id_menu_padre = NULL WHERE ruta = '/caja';
UPDATE menu SET nombre = 'Seguridad', orden = 5, visible = true, id_menu_padre = NULL WHERE ruta = '#seguridad';
UPDATE menu SET nombre = 'Empleado', orden = 6, visible = true, id_menu_padre = NULL WHERE ruta = '#personal';
UPDATE menu SET nombre = 'Proveedor', orden = 7, visible = true, id_menu_padre = NULL WHERE ruta = '#compras-proveedor';
UPDATE menu SET nombre = 'Productos', orden = 8, visible = true, id_menu_padre = NULL WHERE ruta = '#compras-productos';
UPDATE menu SET nombre = 'Almacén', orden = 9, visible = true, id_menu_padre = NULL WHERE ruta = '#inventario-almacen';
UPDATE menu SET nombre = 'Inventario', orden = 10, visible = true, id_menu_padre = NULL WHERE ruta = '#inventario';

UPDATE menu SET nombre = 'Crédito', id_menu_padre = (SELECT id_menu FROM menu WHERE ruta = '#ventas') WHERE ruta = '/gestion-credito';
UPDATE menu SET nombre = 'Almacén', id_menu_padre = (SELECT id_menu FROM menu WHERE ruta = '#inventario-almacen') WHERE ruta = '/almacenes';

-- Completar permisos usados por el Aside y sus submódulos.
INSERT INTO permiso (codigo, nombre, estado)
VALUES ('COMPRAS_VER', 'Ver compras', true)
ON CONFLICT (codigo) DO UPDATE SET estado = true;

INSERT INTO menu_permiso (id_menu, id_permiso)
SELECT m.id_menu, p.id_permiso
FROM (VALUES
    ('/perfil', 'SEGURIDAD_VER'),
    ('/asistencia', 'PERSONAL_VER'), ('/asistencia/admin', 'PERSONAL_VER'),
    ('/permisos', 'PERSONAL_VER'), ('/permisos-admin', 'PERSONAL_VER'),
    ('/almacenes', 'INVENTARIO_VER'), ('/ubicaciones-almacen', 'INVENTARIO_VER'),
    ('/transferencias-almacen', 'INVENTARIO_VER'), ('/ajustes-inventario', 'INVENTARIO_VER'),
    ('/almacenes', 'INVENTARIO_CREAR'), ('/ubicaciones-almacen', 'INVENTARIO_CREAR'),
    ('/transferencias-almacen', 'INVENTARIO_CREAR'), ('/ajustes-inventario', 'INVENTARIO_CREAR'),
    ('/proveedores', 'COMPRAS_VER'), ('/pedidos', 'COMPRAS_VER'),
    ('/recepcion-mercaderia', 'COMPRAS_VER'), ('/devoluciones', 'COMPRAS_VER')
) AS x(ruta, codigo)
JOIN menu m ON m.ruta = x.ruta
JOIN permiso p ON p.codigo = x.codigo
WHERE NOT EXISTS (
    SELECT 1 FROM menu_permiso mp
    WHERE mp.id_menu = m.id_menu AND mp.id_permiso = p.id_permiso
);

-- Permisos del sistema usados por SecurityConfig
INSERT INTO permiso (codigo, nombre, estado)
VALUES
    ('VENTAS_VER', 'Ver ventas', true),
    ('VENTAS_CREAR', 'Crear ventas', true),
    ('CLIENTES_VER', 'Ver clientes', true),
    ('CLIENTES_CREAR', 'Crear clientes', true),
    ('CREDITO_VER', 'Ver créditos', true),
    ('CREDITO_PAGAR', 'Registrar pagos de crédito', true),
    ('CAJA_VER', 'Ver cajas', true),
    ('CAJA_ABRIR', 'Abrir caja', true),
    ('CAJA_CERRAR', 'Cerrar caja', true),
    ('CAJA_CREAR', 'Crear cajas', true),
    ('PRODUCTOS_VER', 'Ver productos', true),
    ('CATALOGOS_CREAR', 'Crear elementos del catálogo', true),
    ('CATEGORIAS_VER', 'Ver categorías', true),
    ('MARCAS_VER', 'Ver marcas', true),
    ('UNIDADES_VER', 'Ver unidades de medida', true),
    ('PROVEEDORES_VER', 'Ver proveedores', true),
    ('PEDIDOS_VER', 'Ver pedidos', true),
    ('RECEPCIONES_VER', 'Ver recepciones', true),
    ('DEVOLUCIONES_VER', 'Ver devoluciones', true),
    ('COMPRAS_CREAR', 'Crear operaciones de compras', true),
    ('EMPLEADOS_VER', 'Ver empleados', true),
    ('PERSONAL_VER', 'Ver información de personal', true),
    ('PERSONAL_CREAR', 'Crear información de personal', true),
    ('INVENTARIO_VER', 'Ver inventario', true),
    ('INVENTARIO_CREAR', 'Crear operaciones de inventario', true),
    ('SEGURIDAD_VER', 'Ver seguridad', true),
    ('SEGURIDAD_CREAR', 'Crear elementos de seguridad', true),
    ('SEGURIDAD_EDITAR', 'Editar permisos del sistema', true),
    ('GENERAL_EDITAR', 'Editar registros', true),
    ('GENERAL_APROBAR', 'Aprobar registros', true),
    ('GENERAL_ELIMINAR', 'Eliminar registros', true)
ON CONFLICT (codigo) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    estado = EXCLUDED.estado;

-- Relacionar cada permiso con el menú correspondiente.
INSERT INTO menu_permiso (id_menu, id_permiso)
SELECT m.id_menu, p.id_permiso
FROM (VALUES
    ('/ventas', 'VENTAS_VER'), ('/ventas', 'VENTAS_CREAR'),
    ('/clientes', 'CLIENTES_VER'), ('/clientes', 'CLIENTES_CREAR'),
    ('/gestion-credito', 'CREDITO_VER'), ('/gestion-credito', 'CREDITO_PAGAR'),
    ('/caja', 'CAJA_VER'), ('/caja', 'CAJA_ABRIR'), ('/caja', 'CAJA_CERRAR'), ('/caja', 'CAJA_CREAR'),
    ('/productos', 'PRODUCTOS_VER'), ('/productos', 'CATALOGOS_CREAR'),
    ('/categorias', 'CATEGORIAS_VER'), ('/categorias', 'CATALOGOS_CREAR'),
    ('/marcas', 'MARCAS_VER'), ('/marcas', 'CATALOGOS_CREAR'),
    ('/unidad_medida', 'UNIDADES_VER'), ('/unidad_medida', 'CATALOGOS_CREAR'),
    ('/proveedores', 'PROVEEDORES_VER'), ('/proveedores', 'COMPRAS_CREAR'),
    ('/pedidos', 'PEDIDOS_VER'), ('/pedidos', 'COMPRAS_CREAR'),
    ('/recepcion-mercaderia', 'RECEPCIONES_VER'), ('/recepcion-mercaderia', 'COMPRAS_CREAR'),
    ('/devoluciones', 'DEVOLUCIONES_VER'), ('/devoluciones', 'COMPRAS_CREAR'),
    ('/empleado', 'EMPLEADOS_VER'), ('/empleado', 'PERSONAL_VER'), ('/empleado', 'PERSONAL_CREAR'),
    ('/contratos', 'PERSONAL_VER'), ('/contratos', 'PERSONAL_CREAR'),
    ('/inventario', 'INVENTARIO_VER'), ('/inventario', 'INVENTARIO_CREAR'),
    ('/inventario-fisico', 'INVENTARIO_VER'), ('/inventario-fisico', 'INVENTARIO_CREAR'),
    ('/roles', 'SEGURIDAD_VER'), ('/roles', 'SEGURIDAD_CREAR'),
    ('/usuarios', 'SEGURIDAD_VER'), ('/usuarios', 'SEGURIDAD_CREAR'),
    ('/permisos-sistema', 'SEGURIDAD_VER'), ('/permisos-sistema', 'SEGURIDAD_EDITAR')
) AS x(ruta, codigo)
JOIN menu m ON m.ruta = x.ruta
JOIN permiso p ON p.codigo = x.codigo
WHERE NOT EXISTS (
    SELECT 1 FROM menu_permiso mp
    WHERE mp.id_menu = m.id_menu AND mp.id_permiso = p.id_permiso
);

-- Orden y agrupacion exactos del Aside.vue
UPDATE menu SET nombre = 'Dashboard', icono = 'dashboard', orden = 1,
    visible = true, id_menu_padre = NULL WHERE ruta = '/';
UPDATE menu SET nombre = 'Clientes', icono = 'people', orden = 2,
    visible = true, id_menu_padre = NULL WHERE ruta = '/clientes';
UPDATE menu SET nombre = 'Ventas', icono = 'point_of_sale', orden = 3,
    visible = true, id_menu_padre = NULL WHERE ruta = '#ventas';
UPDATE menu SET nombre = 'Caja', icono = 'point_of_sale', orden = 4,
    visible = true, id_menu_padre = NULL WHERE ruta = '/caja';
UPDATE menu SET nombre = 'Seguridad', icono = 'security', orden = 5,
    visible = true, id_menu_padre = NULL WHERE ruta = '#seguridad';
UPDATE menu SET nombre = 'Empleado', icono = 'badge', orden = 6,
    visible = true, id_menu_padre = NULL WHERE ruta = '#personal';
UPDATE menu SET visible = false WHERE ruta IN ('#compras', '#inventario');

INSERT INTO menu (nombre, ruta, icono, orden, visible, id_menu_padre)
VALUES
    ('Proveedor', '#compras-proveedor', 'storefront', 7, true, NULL),
    ('Productos', '#compras-productos', 'inventory_2', 8, true, NULL),
    ('Almacen', '#inventario-almacen', 'warehouse', 9, true, NULL),
    ('Inventario', '#inventario', 'inventory', 10, true, NULL)
ON CONFLICT (ruta) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    icono = EXCLUDED.icono,
    orden = EXCLUDED.orden,
    visible = EXCLUDED.visible,
    id_menu_padre = NULL;

INSERT INTO menu (nombre, ruta, icono, orden, visible, id_menu_padre)
SELECT x.nombre, x.ruta, x.icono, x.orden, true, p.id_menu
FROM (VALUES
    ('Nueva venta', '/ventas', 'add_shopping_cart', 1, '#ventas'),
    ('Historial de ventas', '/historial-ventas', 'history', 2, '#ventas'),
    ('Gestion de credito', '/gestion-credito', 'credit_score', 3, '#ventas'),
    ('Usuarios', '/usuarios', 'people', 1, '#seguridad'),
    ('Roles', '/roles', 'security', 2, '#seguridad'),
    ('Perfil', '/perfil', 'person', 3, '#seguridad'),
    ('Permisos del sistema', '/permisos-sistema', 'security', 4, '#seguridad'),
    ('Empleados', '/empleado', 'groups', 1, '#personal'),
    ('Cargos', '/cargos', 'work_history', 2, '#personal'),
    ('Contratos', '/contratos', 'history_edu', 3, '#personal'),
    ('Mi Asistencia', '/asistencia', 'person_pin', 4, '#personal'),
    ('Panel Asistencia', '/asistencia/admin', 'admin_panel_settings', 5, '#personal'),
    ('Mis Permisos', '/permisos', 'event_note', 6, '#personal'),
    ('Admin Permisos', '/permisos-admin', 'admin_panel_settings', 7, '#personal'),
    ('Proveedores', '/proveedores', 'local_shipping', 1, '#compras-proveedor'),
    ('Pedidos', '/pedidos', 'shopping_cart', 2, '#compras-proveedor'),
    ('Recepcion Mercaderia', '/recepcion-mercaderia', 'inventory', 3, '#compras-proveedor'),
    ('Devoluciones', '/devoluciones', 'assignment_return', 4, '#compras-proveedor'),
    ('Productos', '/productos', 'category', 1, '#compras-productos'),
    ('Categorias', '/categorias', 'category', 2, '#compras-productos'),
    ('Marcas', '/marcas', 'label', 3, '#compras-productos'),
    ('Unidad Medida', '/unidad_medida', 'straighten', 4, '#compras-productos'),
    ('Almacen', '/almacenes', 'inventory', 1, '#inventario-almacen'),
    ('Ubicaciones', '/ubicaciones-almacen', 'shelves', 2, '#inventario-almacen'),
    ('Transferencias', '/transferencias-almacen', 'swap_horiz', 3, '#inventario-almacen'),
    ('Inventario', '/inventario', 'inventory_2', 1, '#inventario'),
    ('Inventario Fisico', '/inventario-fisico', 'fact_check', 2, '#inventario'),
    ('Ajustes de Inventario', '/ajustes-inventario', 'tune', 3, '#inventario')
) AS x(nombre, ruta, icono, orden, padre_ruta)
JOIN menu p ON p.ruta = x.padre_ruta
ON CONFLICT (ruta) DO UPDATE SET
    nombre = EXCLUDED.nombre,
    icono = EXCLUDED.icono,
    orden = EXCLUDED.orden,
    visible = EXCLUDED.visible,
    id_menu_padre = EXCLUDED.id_menu_padre;

-- Normalización final de la jerarquía para que coincida con Aside.vue.
UPDATE menu SET nombre = 'Dashboard', orden = 1, visible = true, id_menu_padre = NULL WHERE ruta = '/';
UPDATE menu SET nombre = 'Clientes', orden = 2, visible = true, id_menu_padre = NULL WHERE ruta = '/clientes';
UPDATE menu SET nombre = 'Ventas', orden = 3, visible = true, id_menu_padre = NULL WHERE ruta = '#ventas';
UPDATE menu SET nombre = 'Caja', orden = 4, visible = true, id_menu_padre = NULL WHERE ruta = '/caja';
UPDATE menu SET nombre = 'Seguridad', orden = 5, visible = true, id_menu_padre = NULL WHERE ruta = '#seguridad';
UPDATE menu SET nombre = 'Empleado', orden = 6, visible = true, id_menu_padre = NULL WHERE ruta = '#personal';
UPDATE menu SET nombre = 'Proveedor', orden = 7, visible = true, id_menu_padre = NULL WHERE ruta = '#compras-proveedor';
UPDATE menu SET nombre = 'Productos', orden = 8, visible = true, id_menu_padre = NULL WHERE ruta = '#compras-productos';
UPDATE menu SET nombre = 'Almacén', orden = 9, visible = true, id_menu_padre = NULL WHERE ruta = '#inventario-almacen';
UPDATE menu SET nombre = 'Inventario', orden = 10, visible = true, id_menu_padre = NULL WHERE ruta = '#inventario';
UPDATE menu SET nombre = 'Nueva venta', id_menu_padre = (SELECT id_menu FROM menu WHERE ruta = '#ventas') WHERE ruta = '/ventas';
UPDATE menu SET nombre = 'Historial de ventas', id_menu_padre = (SELECT id_menu FROM menu WHERE ruta = '#ventas') WHERE ruta = '/historial-ventas';
UPDATE menu SET nombre = 'Crédito', id_menu_padre = (SELECT id_menu FROM menu WHERE ruta = '#ventas') WHERE ruta = '/gestion-credito';
UPDATE menu SET id_menu_padre = NULL WHERE ruta = '/clientes';
