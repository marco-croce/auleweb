function aggiungiValoreSelect(select, input) {
    let list = document.getElementById(select);
    let inp = document.getElementById(input);

    if (inp.value === '') {
        document.getElementById("resp").focus();
        return;
    }

    let option = new Option(inp.value, inp.value, false, true);
    list.add(option);

    inp.value = '';

}

function loadCorso() {
    let value_tipologia = document.getElementById('tipologia').value;

    if (value_tipologia === 'PARZIALE' || value_tipologia === 'ESAME' || value_tipologia === 'LEZIONE') {
        document.getElementById('corso').disabled = false;
    } else {
        document.getElementById('corso').disabled = true;
        document.getElementById('corso').value = "";
    }
}

function toggleData() {
    let ricorrenzaSelect = document.getElementById("tipologiaRicorrenza");
    let ricorrenzaDate = document.getElementById("data_fine_ricorrenza");

    if (ricorrenzaSelect.value === "") {
        ricorrenzaDate.disabled = true;
        ricorrenzaDate.value = "";
    } else {
        ricorrenzaDate.disabled = false;
    }
}

document.getElementById("tipologia").addEventListener("change", function () {
    loadCorso();
});

document.getElementById("tipologiaRicorrenza").addEventListener("change", function () {
    toggleData();
});
