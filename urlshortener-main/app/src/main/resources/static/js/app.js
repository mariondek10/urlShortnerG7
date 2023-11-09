$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                let isQRChecked = $("#QRcheckbox").prop("checked");
                $.ajax({
                    type: "POST",
                    url: "/api/link",
                    data: $(this).serialize(),
                    success: async function (msg, status, request) {

                        if(isQRChecked){
                            console.log("SIIIIIIIIIII SE HA PULSADO QR");
                            
                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + request.getResponseHeader('Location')
                                + "'>"
                                + request.getResponseHeader('Location')
                                + "</a></div>"
                                + "<div class='alert alert-success lead'><a target='_blank' href='"
                                + request.getResponseHeader('Location') + "/qr"
                                + "'>"
                                + request.getResponseHeader('Location') + "/qr"
                                + "</a></div>"
                                );
                        }else{
                            console.log("NOOOOOOOOOOO SE HA PULSADO QR");

                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + request.getResponseHeader('Location')
                                + "'>"
                                + request.getResponseHeader('Location')
                                + "</a></div>");
                        }
                        
                    },
                    error: function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });