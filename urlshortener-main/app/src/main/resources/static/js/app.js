$(document).ready(
    function () {
        /*var isInputaliasEmpty;
        var inputAliasValue = $("#inputAlias").val();

        if (inputAliasValue.trim() !== "") {
            alert("InputAlias has a value: " + inputAliasValue);
            isInputaliasEmpty = false;
        } else {
            alert("InputAlias is empty");
            isInputaliasEmpty = true;
        }*/

        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                let alias = document.getElementById('inputAlias').value;
                console.log("alias:", alias)
                console.log("url:", $("#url").val())
                $.ajax({
                    type: "POST",
                    url: "/api/link",
                    data: {
                        url: $("#url").val(),
                        alias: alias
                    },
                    success: async function (msg, status, request) {
                        console.log(msg)

                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + request.getResponseHeader('Location')
                            + "'>"
                            + request.getResponseHeader('Location')
                            + "</a></div>");
                    },
                    error: async function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });