<%@page pageEncoding="UTF-8" %>

<script>

    $(function () {

        $("#star-table").jqGrid({
            url : "${pageContext.request.contextPath}/star/findAllByPage",
            datatype : "json",
            height : 270,
            colNames : [ '编号', '真名', '艺名', '图片', '性别','生日', '状态' ],
            colModel : [
                {name : 'id'},
                {name : 'name', editable: true},
                {name : 'nickname',editable: true},
                {name : 'photo',editable: true,edittype:"file",formatter:function (value,option,rows) {
                        return "<img style='width:100px;height:50px;' src='${pageContext.request.contextPath}/star/image/"+rows.photo+"'>"
                    }},
                {name : 'sex',editable: true,edittype:"select",editoptions:{value:"男:男;女:女"}},
                {name : 'bir',editable: true, edittype:"date"},
                {name : 'status',editable: true,edittype:"select",editoptions:{value:"正常:正常;封杀:封杀"}}
            ],
            styleUI:"Bootstrap",
            autowidth:true,
            editurl: "${pageContext.request.contextPath}/star/edit",
            rowNum : 3,
            rowList : [ 3, 5, 10 ],
            pager : '#star-page',
            viewrecords : true,
            subGrid : true,
            caption : "明星列表",

            subGridRowExpanded : function(subgrid_id, row_id) {
                var subgrid_table_id, pager_id;
                subgrid_table_id = subgrid_id + "_t";
                pager_id = "p_" + subgrid_table_id;
                $("#" + subgrid_id).html(
                    "<table id='" + subgrid_table_id + "' class='scroll'></table>" +
                    "<div id='" + pager_id + "' class='scroll' style='height: 60px'></div>");
                $("#" + subgrid_table_id).jqGrid(
                    {
                        url : "${pageContext.request.contextPath}/user/findAllByStarIDAndPage?starId=" + row_id,
                        datatype : "json",
                        colNames : [ '编号', '头像', '用户名', '手机号', '昵称','地址', '签名', '性别' ],
                        colModel : [
                            {name : "id"},
                            {name : "photo"},
                            {name : "username"},
                            {name : "phone"},
                            {name : "nickname"},
                            {name : "address"},
                            {name : "sign"},
                            {name : "sex"}
                        ],
                        rowNum : 3,
                        pager : pager_id,
                        height : '120%',
                        autowidth: true,
                        styleUI: "Bootstrap"
                    });
                jQuery("#" + subgrid_table_id).jqGrid('navGrid',
                    "#" + pager_id, {
                        edit : false,
                        add : true,
                        del : false,
                        search: false
                    });
            },
            subGridRowColapsed : function(subgrid_id, row_id) {
               //删除父表数据执行下面的代码
                var subgrid_table_id;
                subgrid_table_id = subgrid_id+"_t";
                jQuery("#"+subgrid_table_id).remove();
            }
        });
        jQuery("#star-table").jqGrid('navGrid', '#star-page', {
            add : true,
            edit : false,
            del : true,
            search: false
        },{},{
            //控制添加
            closeAfterAdd: true,
            afterSubmit:function (data) {
                var status = data.responseJSON.status;
                var message = data.responseJSON.message;
                if(status){
                    $.ajaxFileUpload({
                        url:"${pageContext.request.contextPath}/star/upload",
                        type:"json",
                        fileElementId: "photo",
                        data:{id:message},
                        success:function () {
                            //自动刷新jqgrid表格
                            $("#star-table").trigger("reloadGrid");
                        }
                    });
                }
                return "123";
            }

        });

    })

</script>

<div class="navbar navbar-default">
    <h3>展示所有的明星</h3>
</div>

<table id="star-table"></table>
<div id="star-page" style="height: 60px"></div>