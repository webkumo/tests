<html>
<head>
    <title>Test for Embarcadero</title>
    <script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript">
        var $div;
        $(document).ready(
                function() {
                    $div = $('#forTable');
                    refreshData();
                }
        );

        function refreshData() {
            $.ajax('api/users/', {
                error: function(xhr, status, error) {
                    console.log("Error status: " + status + "\n\tError:" + error);
                },
                success: function(data) {
                    $.each(JSON.parse(data).entities, addElement);
                }
            });
        }
        function addElement(o, element) {
            var $d = $('<div></div>');
            $d.append($('<span>' + element['firstName'] + ' </span>'));
            $d.append($('<span>' + element['lastName'] + ' </span>'));
            $d.append($('<span>' + element['loginName'] + ' </span>'));
            $d.append($('<span>' + (new Date(element['date']).toDateString()) + ' </span>'));
            $d.append($('<span>' + (element['enabled'] ? "Enabled" : "Disabled") + ' </span>'));
            $div.append($d);
        }
    </script>
</head>
<body>
<h2 onclick="refreshData()">Hello World!</h2>
<div id="forTable"></div>
</body>
</html>
