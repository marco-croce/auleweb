// Funzione per abilitare/disabilitare il selectbox delle aule
function toggleAulaSelect() {
    let aulaSelect = document.getElementById("aulaDropdown");
    let week = document.getElementById("week");

    if (week.value !== "") {
        aulaSelect.disabled = false;
    } else {
        aulaSelect.disabled = true;
    }
}

// Funzione per abilitare/disabilitare il selectbox dei corsi
function toggleCorsoSelect() {
    let corsoSelect = document.getElementById("corsoDropdown");
    let week = document.getElementById("week");

    if (week.value !== "") {
        corsoSelect.disabled = false;
    } else {
        corsoSelect.disabled = true;
    }
}

// Funzione per abilitare/disabilitare l'input date del giorno
function toggleData() {
    let gruppoSelect = document.getElementById("gruppoDropdown");
    let giornoDate = document.getElementById("giorno");

    if (gruppoSelect.value !== "") {
        giornoDate.disabled = false;
    } else {
        giornoDate.disabled = true;
    }
}

// Funzione per abilitare/disabilitare il pulsante degli eventi attuali
function toggleAttuali() {
    let gruppoSelect = document.getElementById("gruppoDropdown");
    let linkAttuali = document.getElementById("attuali");

    if (gruppoSelect.value !== "") {
        linkAttuali.href = "attuali?gruppo=" + gruppoSelect.value;
    } else {
        linkAttuali.href = location;
    }
}

function toggleWeek() {
    let gruppoSelect = document.getElementById("gruppoDropdown");
    let week = document.getElementById("week");

    if (gruppoSelect.value !== "") {
        week.disabled = false;
    } else {
        week.disabled = true;
    }
}

function caricaAuleCorsi() {
    let gruppo = document.getElementById("gruppoDropdown");
    let query = "index";
    if (gruppo.value !== "") {
        query = query + "?gruppo=" + gruppo.value;
    }
    location.href = query;
}

function caricaAule() {

    let gruppo = document.getElementById('gruppoDropdown');
    let aula = document.getElementById('aulaDropdown');
    let corso = document.getElementById('corsoDropdown');
    let week = document.getElementById('week');
    let query = "index";

    // Parametro gruppo
    query = query + "?gruppo=" + gruppo.value;
    // Parametri anno e settimana
    year = week.value.substring(0, 4);
    numberWeek = week.value.substring(6, 8);
    query = query + "&year=" + year;
    query = query + "&week=" + numberWeek;
    // Parametro aula
    if (aula.value !== "")
        query = query + "&aula=" + aula.value;
    // Parametro corso
    if (corso.value !== "")
        query = query + "&corso=" + corso.value;

    location.href = query;

}

function loadDay() {
    let gruppo = document.getElementById('gruppoDropdown').value;
    let linkGiornata = document.getElementById('giornata');

    if (document.getElementById('giorno').value !== "") {
        let giorno = document.getElementById('giorno').value;

        linkGiornata.href = "giorno?giornata=" + giorno + "&gruppo=" + gruppo;
    } else {
        document.getElementById('giorno').focus();
    }
}

function toggleAll() {
    let gruppo = document.getElementById('gruppoDropdown').value;
    let giornoDate = document.getElementById("giorno");
    let linkAttuali = document.getElementById('attuali');
    let week = document.getElementById('week');

    if (gruppo !== "") {
        giornoDate.disabled = false;
        week.disabled = false;
        linkAttuali.href = "attuali?gruppo=" + gruppo;
    }
}

// Aggiungi gli event listener per il cambio di valore nel selectbox del gruppo
document.getElementById("gruppoDropdown").addEventListener("change", function () {
    toggleData();
    toggleAttuali();
    toggleWeek();
    caricaAuleCorsi();
});

document.getElementById("week").addEventListener("change", function () {
    toggleAulaSelect();
    toggleCorsoSelect();
});

document.getElementById("aulaDropdown").addEventListener("change", function () {
    caricaAule();
});

document.getElementById("corsoDropdown").addEventListener("change", function () {
    caricaAule();
});

document.getElementById("giornata").addEventListener("click", function () {
    loadDay();
});

$(document).ready(function () {
    toggleAll();
});
  