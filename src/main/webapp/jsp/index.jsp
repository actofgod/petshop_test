<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>${pageTitle}</title>

    <link rel="stylesheet" type="text/css" href="ext-js/resources/css/ext-all.css" />

    <style type=text/css>
        /* style rows on mouseover */
        .x-grid3-row-over .x-grid3-cell-inner {
            font-weight: bold;
        }
    </style>

    <!-- ** Javascript ** -->
    <!-- ExtJS library: base/adapter -->
    <script type="text/javascript" src="ext-js/adapter/ext/ext-base.js"></script>

    <!-- ExtJS library: all widgets -->
    <script type="text/javascript" src="ext-js/ext-all-debug.js"></script>

    <!-- overrides to base library -->

    <!-- page specific -->
    <script type="text/javascript" src="js/app.js"></script>
    <script type="text/javascript" src="js/main.js"></script>
    <script type="text/javascript" src="js/user.js"></script>

</head>
<body>
<h1>${pageTitle}</h1>
<div id="user-grid"></div>
<div id="user-window"></div>
</body>
</html>