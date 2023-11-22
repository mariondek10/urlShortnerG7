$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                let isQRChecked = $("#QRcheckbox").prop("checked");
                console.log("isQRChecked:", isQRChecked)
                console.log("url:", $("#url").val())
                $.ajax({
                    type: "POST",
                    url: "/api/link",
                    data: {
                        url: $("#url").val(),
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
                    error: function (data) {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>"
                            + data.responseJSON.message
                            + "</div>");
                    }
                });
            });
    });