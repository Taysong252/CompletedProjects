function actualizarPreview() {
    const nombre = document.getElementById('iNombre')?.value || '';
    const apellido = document.getElementById('iApellido')?.value || '';
    const email = document.getElementById('iEmail')?.value || '';
    const radioMarcado = document.querySelector('input[name="tipoUsuario"]:checked');
    const tipo = radioMarcado ? radioMarcado.value : '';
    const iniciales = (nombre.charAt(0) + apellido.charAt(0)).toUpperCase();
    const avatar = document.getElementById('pAvatar');
    if (avatar)
        avatar.textContent = iniciales || '?';
    const prevNombre = document.getElementById('pNombre');
    if (prevNombre)
        prevNombre.textContent = (nombre + ' ' + apellido).trim() || 'Nombre Apellido';
    const prevEmail = document.getElementById('pEmail');
    if (prevEmail)
        prevEmail.textContent = email || 'correo@ejemplo.com';
    const clasesPorTipo = {'Cliente': 'badge-green', 'Administrador': 'badge-blue'};
    const prevBadge = document.getElementById('pBadge');
    if (prevBadge) {
        const claseBadge = tipo ? (clasesPorTipo[tipo] || 'badge-gray') : 'badge-gray';
        const texto = tipo || 'Sin tipo';
        prevBadge.innerHTML = '<span class="badge ' + claseBadge + '">' + texto + '</span>';
    }
}

['iNombre', 'iApellido', 'iEmail'].forEach(function (id) {
    const campo = document.getElementById(id);
    if (campo)
        campo.addEventListener('input', actualizarPreview);
});

const campoPass2 = document.getElementById('iPass2');
if (campoPass2) {
    campoPass2.addEventListener('input', function () {
        const campoPass1 = document.getElementById('iPass1');
        const mensaje = document.getElementById('msgPass');
        if (campoPass2.value !== '' && campoPass2.value !== campoPass1.value) {
            mensaje.style.display = 'block';
        } else {
            mensaje.style.display = 'none';
        }
    });
}

actualizarPreview();

function filtrarTabla() {
    const texto = document.getElementById('inputBusqueda')?.value.toLowerCase() || '';
    const tipo = document.getElementById('selectTipo')?.value || '';
    const estado = document.getElementById('selectEstado')?.value || '';

    document.querySelectorAll('#cuerpoTabla tr[data-tipo]').forEach(function (fila) {
        const coincideTexto = fila.dataset.busqueda.toLowerCase().includes(texto);
        const coincideTipo = tipo === '' || fila.dataset.tipo === tipo;
        const coincideEstado = estado === '' || fila.dataset.estado === estado;
        fila.dataset.filtrada = (coincideTexto && coincideTipo && coincideEstado) ? 'true' : 'false';
    });

    if (window['_pag_cuerpoTabla'])
        window['_pag_cuerpoTabla'].ir(1);
}


function iniciarPaginacion(idTabla, idPaginacion, idContador, porPagina) {
    porPagina = porPagina || 10;
    const tbody = document.getElementById(idTabla);
    const paginacion = document.getElementById(idPaginacion);
    const contador = document.getElementById(idContador);
    if (!tbody)
        return;
    let paginaActual = 1;

    function filasFiltradas() {
        return Array.from(tbody.querySelectorAll('tr')).filter(function (tr) {

            if (tr.querySelector('td.empty, div.empty'))
                return false;

            if (tr.dataset.filtrada === 'false')
                return false;
            return true;
        });
    }

    function render() {

        Array.from(tbody.querySelectorAll('tr')).forEach(function (tr) {
            if (!tr.querySelector('td.empty, div.empty'))
                tr.style.display = 'none';
        });

        const todas = filasFiltradas();
        const total = todas.length;
        const totalPaginas = Math.max(1, Math.ceil(total / porPagina));
        if (paginaActual > totalPaginas)
            paginaActual = totalPaginas;
        if (paginaActual < 1)
            paginaActual = 1;


        const desde = (paginaActual - 1) * porPagina;
        const hasta = desde + porPagina;
        todas.forEach(function (tr, i) {
            if (i >= desde && i < hasta)
                tr.style.display = '';
        });


        if (contador) {
            const desdeMostrado = Math.min(desde + 1, total);
            const hastaMostrado = Math.min(hasta, total);
            contador.innerHTML = total === 0
                    ? 'Sin resultados'
                    : 'Mostrando <strong>' + desdeMostrado + '&ndash;' + hastaMostrado + '</strong> de <strong>' + total + '</strong>';
        }


        if (paginacion) {
            let html = '';
            html += '<button class="pag-btn" '
                    + (paginaActual === 1 ? 'disabled' : '')
                    + ' onclick="cambiarPagina(\'' + idTabla + '\',\'' + idPaginacion + '\',\'' + idContador + '\',' + porPagina + ',' + (paginaActual - 1) + ')">&#8249;</button>';
            for (var p = 1; p <= totalPaginas; p++) {
                html += '<button class="pag-btn' + (p === paginaActual ? ' active' : '') + '"'
                        + ' onclick="cambiarPagina(\'' + idTabla + '\',\'' + idPaginacion + '\',\'' + idContador + '\',' + porPagina + ',' + p + ')">' + p + '</button>';
            }
            html += '<button class="pag-btn" '
                    + (paginaActual === totalPaginas ? 'disabled' : '')
                    + ' onclick="cambiarPagina(\'' + idTabla + '\',\'' + idPaginacion + '\',\'' + idContador + '\',' + porPagina + ',' + (paginaActual + 1) + ')">&#8250;</button>';
            paginacion.innerHTML = html;
        }
    }

    window['_pag_' + idTabla] = {
        ir: function (p) {
            paginaActual = p;
            render();
        },
        render: render
    };
    render();
}

function cambiarPagina(idTabla, idPaginacion, idContador, porPagina, pagina) {
    if (window['_pag_' + idTabla]) {
        window['_pag_' + idTabla].ir(pagina);
    }
}

function initVisitas() {

    const tabla = document.getElementById('cuerpoTablaVisitas');
    if (!tabla)
        return;

    const ctxBase = document.body.getAttribute('data-base') || '/';
    const esClienteInput = document.getElementById('esCliente');
    const esCliente = esClienteInput && esClienteInput.value === 'true';

    function formatHora(hora) {
        if (!hora)
            return '—';
        const [h, m] = hora.split(':');
        const hNum = parseInt(h);
        const ampm = hNum >= 12 ? 'PM' : 'AM';
        const h12 = hNum % 12 || 12;
        return h12 + ':' + m + ' ' + ampm;
    }

    function formatFecha(fecha) {
        if (!fecha)
            return '';
        const [y, m, d] = fecha.split('-');
        return d + '/' + m + '/' + y;
    }

    function estadoClass(estado) {
        if (estado === 'Confirmada')
            return 'badge badge-green';
        if (estado === 'Cancelada')
            return 'badge badge-gray';
        return 'badge badge-pendiente';
    }

    function renderVisitas(lista) {
        const cols = esCliente ? 5 : 8;

        if (!lista || lista.length === 0) {
            tabla.innerHTML = `<tr><td colspan="${cols}">
                <div class="empty">${esCliente ? 'No tenés visitas registradas.' : 'No se encontraron visitas.'}</div>
            </td></tr>`;
            iniciarPaginacion('cuerpoTablaVisitas', 'paginacionVisitas', 'contadorVisitas', 10);
            return;
        }

        tabla.innerHTML = lista.map(v => {
            const adminNombre = (v.adminNombre && v.adminNombre !== 'Desconocido') ? v.adminNombre : '—';

            const colCliente = esCliente ? '' : `<td>${v.clienteNombre || ''}</td>`;
            const colHorario = esCliente ? '' :
                    `<td>${v.horaInicio ? formatHora(v.horaInicio) + ' – ' + formatHora(v.horaFin) : '—'}</td>`;

            const colAcciones = esCliente ? '' : `
                <td>
                    <div class="btn-group">
                        <a href="${ctxBase}visitas/update?id=${v.id}" class="btn btn-outline btn-sm">Editar</a>
                        <a href="${ctxBase}visitas/delete?id=${v.id}" class="btn btn-danger btn-sm"
                           onclick="return confirm('¿Eliminar esta visita?')">Eliminar</a>
                    </div>
                </td>`;

            return `<tr>
                ${colCliente}
                <td>${adminNombre}</td>
                <td>${formatFecha(v.fechaVisita)}</td>
                ${colHorario}
                <td>${v.motivoVisita || ''}</td>
                <td>${v.cantidadPersonas}</td>
                <td><span class="${estadoClass(v.estado)}">${v.estado}</span></td>
                ${colAcciones}
            </tr>`;
        }).join('');

        iniciarPaginacion('cuerpoTablaVisitas', 'paginacionVisitas', 'contadorVisitas', 10);
    }

    function buscarVisitas() {
        const criterio = document.getElementById('inputBusqueda')?.value.trim() || '';
        const estado = document.getElementById('selectEstado')?.value || '';

        if (estado !== '') {
            fetch(ctxBase + 'visitas/filtrarEstado?estado=' + encodeURIComponent(estado))
                    .then(r => r.json()).then(renderVisitas);
        } else {
            fetch(ctxBase + 'visitas/buscar?criterio=' + encodeURIComponent(criterio))
                    .then(r => r.json()).then(renderVisitas);
        }
    }

    document.getElementById('inputBusqueda')?.addEventListener('input', buscarVisitas);
    document.getElementById('selectEstado')?.addEventListener('change', buscarVisitas);

    iniciarPaginacion('cuerpoTablaVisitas', 'paginacionVisitas', 'contadorVisitas', 10);
}

function initInsumos() {

    const tabla = document.getElementById('cuerpoTablaInsumos');
    if (!tabla)
        return;

    const ctxBase = document.body.getAttribute('data-base') || '/';

    function renderInsumos(lista) {
        if (!lista || lista.length === 0) {
            tabla.innerHTML = '<tr><td colspan="7"><div class="empty">No se encontraron insumos.</div></td></tr>';
            iniciarPaginacion('cuerpoTablaInsumos', 'paginacionInsumos', 'contadorInsumos', 10);
            return;
        }

        tabla.innerHTML = lista.map(i => {
            const stockAlerta = i.stockMinimo > 0 && i.cantidadStock <= i.stockMinimo;
            const precio = '₡ ' + Math.round(i.precioUnitario).toLocaleString('es-CR');

            return `<tr class="${stockAlerta ? 'row-warning' : ''}">
                <td>${i.nombreInsumo}</td>
                <td><span class="badge badge-green">${i.categoria}</span></td>
                <td><span class="${stockAlerta ? 'text-danger' : 'text-ok'}">${i.cantidadStock}</span></td>
                <td>${i.unidadMedida}</td>
                <td>${precio}</td>
                <td>${i.activo
                    ? '<span class="badge badge-green">Activo</span>'
                    : '<span class="badge badge-gray">Inactivo</span>'}
                </td>
                <td>
                    <div class="btn-group">
                        <a href="${ctxBase}compras/add?insumoId=${i.id}" class="btn btn-outline btn-sm">+ Compra</a>
                        <a href="${ctxBase}compras/historial?insumoId=${i.id}" class="btn btn-outline btn-sm">Historial</a>
                        <a href="${ctxBase}insumos/update?id=${i.id}" class="btn btn-outline btn-sm">Editar</a>
                        <a href="${ctxBase}insumos/delete?id=${i.id}" class="btn btn-danger btn-sm"
                           onclick="return confirm('¿Eliminar este insumo?')">Eliminar</a>
                    </div>
                </td>
            </tr>`;
        }).join('');

        iniciarPaginacion('cuerpoTablaInsumos', 'paginacionInsumos', 'contadorInsumos', 10);
    }

    function buscarInsumos() {
        const criterio = document.getElementById('inputBusqueda')?.value.trim() || '';
        const categoria = document.getElementById('selectCategoria')?.value || '';

        if (categoria !== '') {
            fetch(ctxBase + 'insumos/filtrarCategoria?categoria=' + encodeURIComponent(categoria))
                    .then(r => r.json()).then(renderInsumos);
        } else {
            fetch(ctxBase + 'insumos/buscar?criterio=' + encodeURIComponent(criterio))
                    .then(r => r.json()).then(renderInsumos);
        }
    }

    document.getElementById('inputBusqueda')?.addEventListener('input', buscarInsumos);
    document.getElementById('selectCategoria')?.addEventListener('change', buscarInsumos);

    iniciarPaginacion('cuerpoTablaInsumos', 'paginacionInsumos', 'contadorInsumos', 10);
}

function ocultarAlertas(segundos) {
    var ms = (segundos || 5) * 1000;
    document.querySelectorAll('.alert').forEach(function (alerta) {
        setTimeout(function () {
            alerta.style.transition = 'opacity 0.6s ease';
            alerta.style.opacity = '0';
            setTimeout(function () {
                alerta.style.display = 'none';
            }, 600);
        }, ms);
    });
}


function activarBusquedaEnVivo(idInput, idTabla, attrBuscar) {
    attrBuscar = attrBuscar || 'busqueda';
    var input = document.getElementById(idInput);
    if (!input)
        return;

    input.addEventListener('input', function () {
        var texto = input.value.toLowerCase().trim();
        var tbody = document.getElementById(idTabla);
        if (!tbody)
            return;

        tbody.querySelectorAll('tr').forEach(function (fila) {
            if (fila.querySelector('td.empty, div.empty'))
                return;
            var valor = (fila.dataset[attrBuscar] || '').toLowerCase();
            fila.dataset.filtrada = valor.includes(texto) ? 'true' : 'false';
        });

        if (window['_pag_' + idTabla])
            window['_pag_' + idTabla].ir(1);
    });

}

function activarFiltrosConSelect(idInput, idSelect, idTabla) {
    const input = document.getElementById(idInput);
    const select = document.getElementById(idSelect);
    const tbody = document.getElementById(idTabla);
    if (!tbody)
        return;

    function aplicarFiltros() {
        const texto = (input?.value || '').toLowerCase().trim();
        const activo = select?.value || '';

        tbody.querySelectorAll('tr').forEach(function (fila) {
            if (fila.querySelector('div.empty'))
                return;
            const coincideTexto = (fila.dataset.busqueda || '').toLowerCase().includes(texto);
            const coincideActivo = activo === '' || fila.dataset.activo === activo;
            fila.dataset.filtrada = (coincideTexto && coincideActivo) ? 'true' : 'false';
        });

        if (window['_pag_' + idTabla])
            window['_pag_' + idTabla].ir(1);
    }

    if (input)
        input.addEventListener('input', aplicarFiltros);
    if (select)
        select.addEventListener('change', aplicarFiltros);
}

var promoAplicada = null;

function fmtMoneda(n) {
    return '₡' + Number(n).toLocaleString('es-CR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function recalcularVenta() {
    var subtotal = parseFloat(document.getElementById('subtotal')?.value) || 0;
    var descuento = 0;
    var pct = 0;

    if (promoAplicada) {
        if (subtotal >= promoAplicada.montoMinimo) {
            pct = promoAplicada.porcentaje;
            var bruto = Math.round((subtotal * 1.13) * 100) / 100;
            descuento = Math.round(bruto * (pct / 100) * 100) / 100;
        } else {
            promoAplicada = null;
            var inp = document.getElementById('codigoPromo');
            if (inp)
                inp.value = '';
            var res = document.getElementById('promoResultado');
            if (res)
                res.innerHTML =
                        '<span style="color:#c0392b;">La promoción fue removida porque el subtotal no cumple el monto mínimo.</span>';
        }
    }

    var impuesto = Math.round(subtotal * 0.13 * 100) / 100;
    var bruto = Math.round((subtotal + impuesto) * 100) / 100;
    var total = Math.round((bruto - descuento) * 100) / 100;
    if (total < 0)
        total = 0;

    var elSub = document.getElementById('resSubtotal');
    var elDesc = document.getElementById('resDescuento');
    var elImp = document.getElementById('resImpuesto');
    var elTot = document.getElementById('resTotal');
    var elLabel = document.getElementById('labelDescuento');

    if (elSub)
        elSub.textContent = fmtMoneda(subtotal);
    if (elDesc)
        elDesc.textContent = '— ' + fmtMoneda(descuento);
    if (elImp)
        elImp.textContent = fmtMoneda(impuesto);
    if (elTot)
        elTot.textContent = fmtMoneda(total);
    if (elLabel)
        elLabel.textContent = 'Descuento (' + pct + '%):';
}

function verificarPromocion() {
    var inpCodigo = document.getElementById('codigoPromo');
    var inpSub = document.getElementById('subtotal');
    var div = document.getElementById('promoResultado');

    var codigo = (inpCodigo?.value || '').trim();
    var subtotal = parseFloat(inpSub?.value) || 0;

    if (!codigo) {
        if (div)
            div.innerHTML = '<span style="color:#c0392b;">Ingrese un código de promoción.</span>';
        return;
    }
    if (subtotal <= 0) {
        if (div)
            div.innerHTML = '<span style="color:#c0392b;"> Ingrese el subtotal antes de verificar.</span>';
        return;
    }

    if (div)
        div.innerHTML = '<span style="color:#888;">Verificando...</span>';

    fetch('/promociones/verificar?codigo=' + encodeURIComponent(codigo) + '&subtotal=' + subtotal)
            .then(function (r) {
                return r.json();
            })
            .then(function (data) {
                if (data.valida) {
                    promoAplicada = {
                        porcentaje: data.porcentaje,
                        montoMinimo: data.montoMinimo
                    };
                    if (div)
                        div.innerHTML =
                                '<span style="color:#2d7a4f;"> <strong>' + data.nombre + '</strong> — '
                                + data.porcentaje + '% de descuento aplicado.</span>';
                    recalcularVenta();
                } else {
                    promoAplicada = null;
                    if (div)
                        div.innerHTML = '<span style="color:#c0392b;"> ' + data.mensaje + '</span>';
                    recalcularVenta();
                }
            })
            .catch(function () {
                if (div)
                    div.innerHTML = '<span style="color:#c0392b;">Error al verificar. Intente de nuevo.</span>';
            });
}

function limpiarPromo() {
    promoAplicada = null;
    var inp = document.getElementById('codigoPromo');
    var div = document.getElementById('promoResultado');
    if (inp)
        inp.value = '';
    if (div)
        div.innerHTML = '';
    recalcularVenta();
}

function iniciarFormVenta(pct, min, nom) {
    if (pct > 0) {
        promoAplicada = {porcentaje: pct, montoMinimo: min};
        var div = document.getElementById('promoResultado');
        if (div)
            div.innerHTML =
                    '<span style="color:#2d7a4f;"> <strong>' + nom + '</strong> — '
                    + pct + '% de descuento aplicado.</span>';
    }
    var inpSub = document.getElementById('subtotal');
    if (inpSub)
        inpSub.addEventListener('input', recalcularVenta);
    recalcularVenta();
}

function initVentas() {
    iniciarPaginacion('cuerpoTablaVentas', 'paginacionVentas', 'contadorVentas', 10);
    ocultarAlertas();

    var inputBusq = document.getElementById('inputBusquedaVentas');
    var selEstado = document.getElementById('selectEstadoVentas');
    var selMetodo = document.getElementById('selectMetodoVentas');
    var tbody = document.getElementById('cuerpoTablaVentas');

    if (!tbody)
        return;

    function aplicarFiltrosVentas() {
        var texto = (inputBusq?.value || '').toLowerCase().trim();
        var estado = selEstado?.value || '';
        var metodo = selMetodo?.value || '';

        tbody.querySelectorAll('tr').forEach(function (fila) {
            if (fila.querySelector('div.empty'))
                return;
            var coincideTexto = (fila.dataset.busqueda || '').toLowerCase().includes(texto);
            var coincideEstado = estado === '' || fila.dataset.activo === estado;
            var celdas = fila.querySelectorAll('td');
            var metodoFila = celdas[9] ? celdas[9].textContent.trim() : '';
            var coincideMetodo = metodo === '' || metodoFila === metodo;
            fila.dataset.filtrada = (coincideTexto && coincideEstado && coincideMetodo) ? 'true' : 'false';
        });

        if (window['_pag_cuerpoTablaVentas'])
            window['_pag_cuerpoTablaVentas'].ir(1);
    }

    inputBusq?.addEventListener('input', aplicarFiltrosVentas);
    selEstado?.addEventListener('change', aplicarFiltrosVentas);
    selMetodo?.addEventListener('change', aplicarFiltrosVentas);
}

function initEntregas() {
    iniciarPaginacion('cuerpoTablaEntregas', 'paginacionEntregas', 'contadorEntregas', 10);
    ocultarAlertas();

    var inputBusq = document.getElementById('inputBusquedaEntregas');
    var selEstado = document.getElementById('selectEstadoEntregas');
    var selMetodo = document.getElementById('selectMetodoEntregas');
    var tbody = document.getElementById('cuerpoTablaEntregas');

    if (!tbody)
        return;

    function aplicarFiltrosEntregas() {
        var texto = (inputBusq?.value || '').toLowerCase().trim();
        var estado = selEstado?.value || '';
        var metodo = selMetodo?.value || '';

        tbody.querySelectorAll('tr').forEach(function (fila) {
            if (fila.querySelector('div.empty'))
                return;

            var coincideTexto = (fila.dataset.busqueda || '').toLowerCase().includes(texto);
            var coincideEstado = estado === '' || fila.dataset.activo === estado;
            var coincideMetodo = metodo === '' || fila.dataset.metodo === metodo;

            fila.dataset.filtrada = (coincideTexto && coincideEstado && coincideMetodo) ? 'true' : 'false';
        });

        if (window['_pag_cuerpoTablaEntregas'])
            window['_pag_cuerpoTablaEntregas'].ir(1);
    }

    inputBusq?.addEventListener('input', aplicarFiltrosEntregas);
    selEstado?.addEventListener('change', aplicarFiltrosEntregas);
    selMetodo?.addEventListener('change', aplicarFiltrosEntregas);
}