$(document).ready(
    function checkInputAlias() {
        var isInputaliasEmpty;
        var inputAliasValue = $("#inputAlias").val();

        if (inputAliasValue.trim() !== "") {
            alert("InputAlias has a value: " + inputAliasValue);
            isInputaliasEmpty = false;
        } else {
            alert("InputAlias is empty");
            isInputaliasEmpty = true;
        }

        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/api/link",
                    data: $(this).serialize(),
                    success: function (msg, status, request) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + request.getResponseHeader('Location')
                            + "'>"
                            + request.getResponseHeader('Location')
                            + "</a></div>");
                    },
                    error: function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });