$(document).ready(
    function () {
        var url = null
        /**
         * 1. Interceptamos el evento submit del formulario
         * 2. Prevenimos que se envíe el formulario
         * 3. Recuperamos el valor del campo url
         * 4. Enviamos una petición POST a /api/link
         * 5. Si la petición es correcta, mostramos el resultado
         * 6. Si la petición falla, mostramos el error
         */
        $("#ver_info").prop('disabled', true)

        $("#shortener").submit(
            function (event) {
                event.preventDefault();

                let alias_ = document.getElementById('inputAlias').value;
                console.log("alias:", alias_)
                            
                let isQRChecked = $("#QRcheckbox").prop("checked");
                console.log("isQRChecked:", isQRChecked)

                console.log("url:", $("#url").val())

                $.ajax({
                    type: "POST",
                    url: "/api/link",
                    data: {
                        url: $("#url").val(),
                        qrBool: isQRChecked,
                        alias: alias_
                    },
                    success: async function (data, request) {
                        console.log("APP.js data recibida:", data)
                        url = "http://localhost:8080" + data.url
                        console.log("APP.js valor url conjunta:", url)
                        if(data.properties.qr){
                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + data.url
                                + "'>"
                                + url
                                + "</a></div>"
                                + "<div class='alert alert-success lead'><a target='_blank' href='"
                                + data.properties.qr
                                + "'>"
                                + "http://localhost:8080" + data.properties.qr
                                + "</a></div>"
                                );
                        }else{

                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + data.url
                                + "'>"
                                + url
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

        $("#ver_info").click(
            function(event) {
                const hash = url.substring(url.lastIndexOf('/') + 1);
                $.ajax({
                    type: "GET",
                    url: '/api/link/' + hash,
                    success: function (msg, status, request) {
                        try {
                            if (msg) {
                                $('#tabla_info_clicks').empty();

                                $('#tabla_info_clicks').append(
                                    '<tr>' +
                                    '<th>Origin</th>' +
                                    '<th>Clicks</th>' +
                                    '</tr>'
                                );

                                Object.keys(msg).forEach(key => {
                                    const fila = '<tr><td>' + key + '</td><td>' + msg[key] + '</td></tr>';
                                    $('#tabla_info_clicks').append(fila);
                                });
                            }
                        } catch (error) {
                            console.error('Error en el manejo de la respuesta AJAX:', error);
                        }
                    }
                });
        });

        $("#result").on("click", "a", function() {
            $("#ver_info").prop('disabled', false);
        });
    });