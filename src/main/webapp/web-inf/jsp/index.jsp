<html>
<head>
    <title>vyazanie</title>
    <link href="webjars/bootstrap/5.1.3/css/bootstrap.min.css" rel="stylesheet">
    <link href="css/custom.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <table class="table table-striped">
        <caption>your todos are</caption>
        <thead>
        <tr>
            <th>description</th>
            <th>target date</th>
            <th>is it done?</th>
            <th>edit</th>
            <th>delete</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>todo 1</td>
            <td>10/12/2017</td>
            <td>no</td>
            <td><a class="btn btn-warning" href="/edit-todo">edit todo</a></td>
            <td><a class="btn btn-warning" href="/delete-todo">delete todo</a></td>
        </tr>
        </tbody>
    </table>
    <div>
        <a class="btn btn-default" href="/add-todo">add a todo</a>

    </div>
    <script src="webjars/jquery/3.6.0/jquery.min.js"></script>
    <script src="webjars/jquery-ui/1.12.0/jquery.min.js"></script>
    <script src="webjars/bootstrap/5.1.3/js/bootstrap.min.js"></script>
    <script src="js/ui.js"></script>
</div>
</body>
</html>