<%@page pageEncoding="UTF-8" contentType="text/html; UTF-8" isELIgnored="false" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>后台管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/statics/boot/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/statics/jqgrid/css/trirand/ui.jqgrid.css">
    <script src="${pageContext.request.contextPath}/statics/boot/js/jquery-3.3.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/statics/boot/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/statics/jqgrid/js/trirand/i18n/grid.locale-cn.js"></script>
    <script src="${pageContext.request.contextPath}/statics/jqgrid/js/trirand/jquery.jqGrid.min.js"></script>
    <script src="${pageContext.request.contextPath}/statics/jqgrid/js/ajaxfileupload.js"></script>
    <script charset="utf-8" src="${pageContext.request.contextPath}/kindeditor/kindeditor-all-min.js"></script>
    <script charset="utf-8" src="${pageContext.request.contextPath}/kindeditor/lang/zh-CN.js"></script>
    <script src="${pageContext.request.contextPath}/echarts/echarts.min.js"></script>
    <script  type='text/javascript' src='https://cdn.goeasy.io/json2.js​'></script>
    <%--goeasy--%>
    <script type="text/javascript" src="https://cdn.goeasy.io/goeasy-1.0.0.js​"></script>
</head>
<body>

<%--导航条--%>
<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">持明法洲后台管理系统<small>V1.0</small></a>
        </div>
        <div class="collapse navbar-collapse">
            <ul class="nav navbar-nav navbar-right">
                <li><a href="#">欢迎：<strong style="color: blue;">${sessionScope.admin.nickname}</strong></a></li>
                <li><a href="#">安全退出 <strong class="glyphicon glyphicon-log-out"></strong></a></li>
            </ul>
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav>

<%--主题内容--%>
<div class="container-fluid">

    <div class="row">

        <%--左侧菜单栏--%>
        <div class="col-sm-2">
            <!--面板组-->
            <div class="panel-group" id="acc">
                <!--第一个面板:轮播图管理-->
                <div class="panel panel-default">
                    <!--头-->
                    <div class="panel-heading" role="tab" id="headingOne">
                        <h4 class="panel-title">
                            <a role="button" data-toggle="collapse" data-parent="#acc" href="#collapseOne" >
                                轮播图管理
                            </a>
                        </h4>
                    </div>
                    <!--身体-->
                    <div id="collapseOne" class="panel-collapse collapse">
                        <div class="panel-body">
                            <!--列表组-->
                            <ul class="list-group">
                                <li class="list-group-item"><a href="javascript:$('#content').load('${pageContext.request.contextPath}/back/viewpager.jsp')" id="viewpager"><strong>所有轮播图</strong></a> </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <!--第二个面板:专辑管理-->
                <div class="panel panel-default">
                    <!--头-->
                    <div class="panel-heading" role="tab" id="headingTwo">
                        <h4 class="panel-title">
                            <a role="button" data-toggle="collapse" data-parent="#acc" href="#collapseTwo" >
                                专辑管理
                            </a>
                        </h4>
                    </div>
                    <!--身体-->
                    <div id="collapseTwo" class="panel-collapse collapse">
                        <div class="panel-body">
                            <!--列表组-->
                            <ul class="list-group">
                                <li class="list-group-item">
                                    <a href="javascript:$('#content').load('${pageContext.request.contextPath}/back/albumView.jsp')">所有专辑</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <!--第三个面板:文章管理-->
                <div class="panel panel-default">
                    <!--头-->
                    <div class="panel-heading" role="tab" id="headingThree">
                        <h4 class="panel-title">
                            <a role="button" data-toggle="collapse" data-parent="#acc" href="#collapseThree" >
                                文章管理
                            </a>
                        </h4>
                    </div>
                    <!--身体-->
                    <div id="collapseThree" class="panel-collapse collapse">
                        <div class="panel-body">
                            <!--列表组-->
                            <ul class="list-group">
                                <li class="list-group-item">
                                    <a href="javascript:$('#content').load('${pageContext.request.contextPath}/back/articleView.jsp')">所有文章</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <!--第四个面板:用户管理-->
                <div class="panel panel-default">
                    <!--头-->
                    <div class="panel-heading" role="tab" id="headingFore">
                        <h4 class="panel-title">
                            <a role="button" data-toggle="collapse" data-parent="#acc" href="#collapseFore" >
                                用户管理
                            </a>
                        </h4>
                    </div>
                    <!--身体-->
                    <div id="collapseFore" class="panel-collapse collapse">
                        <div class="panel-body">
                            <!--列表组-->
                            <ul class="list-group">
                                <li class="list-group-item">
                                    <a href="javascript:$('#content').load('${pageContext.request.contextPath}/back/userView.jsp')">所有用户</a>
                                </li>
                                <li class="list-group-item">
                                    <a href="javascript:$('#content').load('${pageContext.request.contextPath}/back/userRegistView.jsp')">用户注册趋势</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <!--第五个面板:明星管理-->
                    <div class="panel panel-default">
                        <!--头-->
                        <div class="panel-heading" role="tab" id="headingFive">
                            <h4 class="panel-title">
                                <a role="button" data-toggle="collapse" data-parent="#acc" href="#collapseFive" >
                                    明星管理
                                </a>
                            </h4>
                    </div>
                    <!--身体-->
                    <div id="collapseFive" class="panel-collapse collapse">
                        <div class="panel-body">
                            <!--列表组-->
                            <ul class="list-group">
                                <li class="list-group-item">
                                    <a href="javascript:$('#content').load('${pageContext.request.contextPath}/back/starsView.jsp')">所有明星</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

        <%--右侧正文内容--%>
        <div class="col-sm-10" id="content">
            <%--面板--%>
            <div class="panel panel-default">
                <div class="panel-body">
                    欢迎来到持明法洲后台管理系统!
                </div>
            </div>
            <%--巨幕--%>
            <div class="jumbotron"
            style="padding-left: 0px;padding-top: 0px;padding-right: 0px;padding-bottom: 0px">
                <p><img src="${pageContext.request.contextPath}/statics/car.jpg"></p>
            </div>
        </div>


    </div>
</div>


</body>
</html>