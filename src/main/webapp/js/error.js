
function openModal() {

    $('#myModal').modal('show');

}

$(document).ready(function () {
    // Chiudi la finestra modale quando viene cliccato il pulsante "Chiudi"
    $('.btn-secondary').click(function () {
        $('#myModal').modal('hide');
    });

});

