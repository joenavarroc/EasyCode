const tipoInput = document.getElementById('tipo');
const textoInput = document.getElementById('texto');
const validacionDiv = document.getElementById('validacion');
const infoTipo = document.getElementById('infoTipo');
const autoGenerar = document.getElementById('autoGenerar');
const imagenCodigo = document.getElementById('imagenCodigo');
const descargarBtn = document.getElementById('descargar');

const infoMap = {
    QR_CODE: '✅ Puede contener cualquier tipo de texto o enlace.',
    CODE_128: '✅ Solo caracteres ASCII (0–127). Ej: texto, números y símbolos.',
    EAN_13: '✅ Solo 12 dígitos numéricos. El sistema calcula el dígito de control.',
    UPC_A: '✅ Exactamente 11 o 12 dígitos numéricos.',
    CODE_39: '✅ Letras mayúsculas, dígitos y algunos símbolos (- . $ / + % SPACE).',
    PDF_417: '✅ Permite texto largo, números y caracteres especiales. Ideal para documentos.'
};

// Mostrar info según tipo
tipoInput.addEventListener('change', () => {
    const tipo = tipoInput.value;
    infoTipo.textContent = infoMap[tipo] || '';
    validacionDiv.textContent = '';
    imagenCodigo.hidden = true;
    imagenCodigo.style.display = 'none';
    descargarBtn.disabled = true;
    descargarBtn.style.display = 'inline-block';
});

// Ocultar imagen si se borra el texto
textoInput.addEventListener('input', () => {
    if (!textoInput.value.trim()) {
        imagenCodigo.hidden = true;
        imagenCodigo.style.display = 'none';
        descargarBtn.disabled = true;
    }
});

// Autogenerar códigos válidos
autoGenerar.addEventListener('change', () => {
    const tipo = tipoInput.value;
    if (!autoGenerar.checked) return;

    if (tipo === 'EAN_13') {
        fetch('/codigo/generar-ean')
            .then(res => res.json())
            .then(data => textoInput.value = data.codigo);
    } else if (tipo === 'UPC_A') {
        fetch('/codigo/generar-upc')
            .then(res => res.json())
            .then(data => textoInput.value = data.codigo);
    }
});

// Enviar formulario y generar código
document.getElementById('formulario').addEventListener('submit', async (e) => {
    e.preventDefault();
    const tipo = tipoInput.value;
    const texto = textoInput.value;

    const resp = await fetch(`/codigo/validar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ texto, tipo })
    });

    const data = await resp.json();
    validacionDiv.textContent = data.resultado;

    if (!data.resultado.includes('✅')) {
        descargarBtn.disabled = true;
        return;
    }

    const url = `/codigo/generar?texto=${encodeURIComponent(texto)}&tipo=${tipo}`;
    imagenCodigo.hidden = false;
    imagenCodigo.style.display = 'block';

    // Establecer el src y esperar a que cargue la imagen antes de habilitar el botón
    imagenCodigo.onload = () => {
        console.log("Imagen cargada correctamente");
        descargarBtn.disabled = false;
    };
    imagenCodigo.src = url + `&t=${Date.now()}`;
});

// Descargar imagen
descargarBtn.addEventListener('click', () => {
    if (!imagenCodigo.src || imagenCodigo.hidden) {
        alert("La imagen aún no está lista para descargar.");
        return;
    }

    const link = document.createElement('a');
    link.href = imagenCodigo.src;
    link.download = 'codigo.png';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
});
