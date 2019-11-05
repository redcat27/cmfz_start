<%@page pageEncoding="UTF-8" %>
<h3>展示所有用户</h3>
<script>

    $(function () {

        $("#user-table").jqGrid({
            url : "${pageContext.request.contextPath}/user/findAllByPage",
            datatype : "json",
            colNames : [ '编号', '头像', '用户名', '性别', '手机', '昵称', '省','市', '签名' ],
            colModel : [
                {name : 'id'},
                {name : 'pthoto',formatter:function (value, option, rows) {
                        return "<img style='width:100px;height:50px;' src='${pageContext.request.contextPath}/user/photo/"+rows.photo+"'>";
                    }},
                {name : 'username'},
                {name : 'sex'},
                {name : 'phone'},
                {name : 'nickname'},
                {name : 'province'},
                {name : 'city'},
                {name : 'sign'}
            ],
            styleUI:"Bootstrap",
            autowidth:true,
            rowNum : 5,
            rowList : [ 5, 10, 20 ],
            pager : '#user-page',
            height:300,
            viewrecords : true,
            caption : "展示所有用户"
        }).navGrid("#user-page", {edit : false,add : false,del : false,search:false});;

    })

</script>

<%--标签页--%>
<ul class="nav nav-tabs">
    <li role="presentation" class="active"><a href="#">所有用户</a></li>
    <%--<li role="presentation"><a  href="${pageContext.request.contextPath}/user/exportUsers">导出所有用户Excle</a></li>--%>
    <li role="presentation" class="dropdown">
        <a class="dropdown-toggle" data-toggle="dropdown" href="" role="button" aria-haspopup="true" aria-expanded="false">
            导出用户列表 <span class="caret"></span>
        </a>
        <ul class="dropdown-menu">
            <li class=""> <a href="${pageContext.request.contextPath}/user/exportUsers?oper=inline"> 预览用户列表 </a> </li>
            <li> <a href="${pageContext.request.contextPath}/user/exportUsers?oper=attachment"> 下载用户列表 </a> </li>
        </ul>
    </li>
</ul>

<table id="user-table"></table>
<div style="height: 70px" id="user-page"></div>
