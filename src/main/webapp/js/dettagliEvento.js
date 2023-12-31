document.addEventListener("DOMContentLoaded", function () {
    let modal = document.getElementById("modal");
    let closeButton = document.getElementById("modal-close");
    let rows = document.querySelectorAll("table tbody tr");

    rows.forEach(function (row) {
        row.addEventListener("click", function () {
            modal.style.display = "block";

            let nome = row.getAttribute("data-nome");
            let data = row.getAttribute("data-data");
            let inizio = row.getAttribute("data-inizio");
            let fine = row.getAttribute("data-fine");
            let descrizione = row.getAttribute("data-descrizione");
            let nomeCorso = row.getAttribute("data-nomeCorso");
            let emailResponsabile = row.getAttribute("data-emailResponsabile");
            let tipologia = row.getAttribute("data-tipologia");
            let nomeAula = row.getAttribute("data-aulaNome");
            let attrezzatura = row.getAttribute("data-attrezzi");

            document.getElementById("modal-nome").innerHTML = "<strong>Nome:</strong> " + nome;
            document.getElementById("modal-data").innerHTML = "<strong>Data:</strong> " + data;
            document.getElementById("modal-inizio").innerHTML = "<strong>Ora di inizio:</strong> " + inizio;
            document.getElementById("modal-fine").innerHTML = "<strong>Ora di fine:</strong> " + fine;
            document.getElementById("modal-descrizione").innerHTML = "<strong>Descrizione:</strong> " + descrizione;
            document.getElementById("modal-nomeCorso").innerHTML = "<strong>Corso:</strong> " + nomeCorso;
            document.getElementById("modal-emailResponsabile").innerHTML = "<strong>Email del responsabile:</strong> " + emailResponsabile;
            document.getElementById("modal-tipologia").innerHTML = "<strong>Tipologia evento:</strong> " + tipologia;
            document.getElementById("modal-nomeAula").innerHTML = "<strong>Aula:</strong> " + nomeAula;
            document.getElementById("modal-attrezzatura").innerHTML = "<strong>Attrezzatura:</strong> " + attrezzatura;

        });
    });


    closeButton.addEventListener("click", function () {
        modal.style.display = "none";
    });
});

// Funzione per abilitare/disabilitare il pulsante per effettuare il download degli eventi nell'intervallo di tempo
function toggleDownload() {
    let download = document.getElementById("download");
    let first = document.getElementById("first");
    let last = document.getElementById("last");

    if (first.value !== "" && last.value !== "") {
        download.disabled = false;
    } else {
        download.disabled = true;
    }
}

document.getElementById("first").addEventListener("change", function () {
    toggleDownload();
});

document.getElementById("last").addEventListener("change", function () {
    toggleDownload();
});
