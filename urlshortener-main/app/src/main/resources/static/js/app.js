$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                let isQRChecked = $("#QRcheckbox").prop("checked");
                $.ajax({
                    type: "POST",
                    url: "/api/link",
                    data: {
                        url: $("#url").val(),
                        qr_bool: isQRChecked
                    },
                    success: function (data, status, request) {
                        console.log("APP.js data:", data)

                        if(data.properties.qr){
                            console.log("SIIIIIIIIIII SE HA PULSADO QR");
                            console.log("QR DEVUELTO");
                            
                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + data.url
                                + "'>"
                                + data.url
                                + "</a></div>"
                                + "<div class='alert alert-success lead'><a target='_blank' href='"
                                + data.properties.qr
                                + "'>"
                                + data.properties.qr
                                + "</a></div>"
                                );
                        }else{
                            console.log("NOOOOOOOOOOO SE HA PULSADO QR");

                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + data.url
                                + "'>"
                                + data.url
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