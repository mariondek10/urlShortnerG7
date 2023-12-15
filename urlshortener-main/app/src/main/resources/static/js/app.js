$(document).ready(
    function () {

        /**
         * 1. Interceptamos el evento submit del formulario
         * 2. Prevenimos que se envíe el formulario
         * 3. Recuperamos el valor del campo url
         * 4. Enviamos una petición POST a /api/link
         * 5. Si la petición es correcta, mostramos el resultado
         * 6. Si la petición falla, mostramos el error
         */

        $("#shortener").submit(
            function (event) {
                event.preventDefault();

                let alias = document.getElementById('inputAlias').value;
                console.log("alias:", alias)
                            
                let isQRChecked = $("#QRcheckbox").prop("checked");
                console.log("isQRChecked:", isQRChecked)

                console.log("url:", $("#url").val())
                $.ajax({
                    type: "POST",
                    url: "/api/link",
                    data: {
                        url: $("#url").val(),
                        alias: alias,
                        qrBool: isQRChecked
                    },
                    success: async function (data, status, request) {
                        console.log("APP.js data recibida:", data)

                        if(data.properties.qr){

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

                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + data.url
                                + "'>"
                                + data.url
                                + "</a></div>");
                        }
                        
                    },
                    error: async function (data) {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>"
                            + data.responseJSON.message
                            + "</div>");
                    }
                });
            });
    });