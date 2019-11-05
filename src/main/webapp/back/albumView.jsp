<%@page pageEncoding="UTF-8" %>


<script>

    $(function () {
        $("#album-table").jqGrid({
            url : "${pageContext.request.contextPath}/album/findAllByPage",
            datatype : "json",
            height : 270,
            colNames : [ '编号', '专辑名', '封面', '歌曲数量', '评分','作者', '播音员' , '简介' , '发布时间' ],
            colModel : [
                {name : 'id'},
                {name : 'title',editable:true},
                {name : 'cover',editable:true,edittype:"file",formatter:function (value, option, rows) {
                        return "<img style='width:110px;height:60px;' src='${pageContext.request.contextPath}/album/image/"+rows.cover+"'>"
                    }},
                {name : 'count'},
                {name : 'score'},
                {name : 'author',editable:true,edittype: "select",editoptions:{value:getOptions()}},
                {name : 'broadcast',editable:true},
                {name : 'brief',editable:true},
                {name : 'createDate'}
            ],
            editurl:"${pageContext.request.contextPath}/album/edit",
            styleUI: "Bootstrap",
            autowidth: true,
            rowNum : 3,
            rowList : [ 3, 5, 10 ],
            pager : '#album-page',
            viewrecords : true,
            subGrid : true,
            caption : "所有专辑列表",
            subGridRowExpanded : function(subgrid_id, row_id) {
                var subgrid_table_id, pager_id;
                subgrid_table_id = subgrid_id + "_t";
                pager_id = "p_" + subgrid_table_id;
                $("#" + subgrid_id).html(
                    "<table id='" + subgrid_table_id
                    + "' class='scroll'></table><div id='"
                    + pager_id + "' class='scroll' style='height: 60px'></div>");
                jQuery("#" + subgrid_table_id).jqGrid(
                    {
                        url : "${pageContext.request.contextPath}/chapter/findAllByPageAndChapterId?id=" + row_id,
                        datatype : "json",
                        colNames : [ '编号', '歌曲名', '歌手', '音频大小', '时长','文件名','发布时间','在线播放' ],
                        colModel : [
                            {name : "id"},
                            {name : "title"},
                            {name : "singer"},
                            {name : "size"},
                            {name : "duration"},
                            {name : "name",edittype:"file",editable:true},
                            {name : "createDate"},
                            {name : "option", width:400, formatter:function (value, option, rows) {
                                    return "<audio controls>\n" +
                                        "  <source src='${pageContext.request.contextPath}/chapter/audio/"+rows.folder+"/"+rows.name+"' >\n" +
                                        "</audio>";
                                }}
                        ],
                        editurl:"${pageContext.request.contextPath}/chapter/edit?albumId="+row_id,
                        styleUI:"Bootstrap",
                        autowidth:true,
                        rowNum : 3,
                        pager : pager_id,
                        height : '120%'
                    });
                jQuery("#" + subgrid_table_id).jqGrid('navGrid',
                    "#" + pager_id, {
                        edit : true,
                        add : true,
                        del : true,
                        search:false
                    },{
                    //控制章节（歌曲）修改
                        closeAfterEdit: true,
                        afterSubmit:function (data) {
                            var status = data.responseJSON.status;
                            var id = data.responseJSON.message;
                            var change = data.responseJSON.change;
                            if(status&&change){
                                $.ajaxFileUpload({
                                    url:"${pageContext.request.contextPath}/chapter/upload",
                                    type:"post",
                                    fileElementId:"name",
                                    data:{id:id,albumId:row_id},
                                    success:function () {
                                        //自动刷新表格
                                        $("#album-table").trigger("reloadGrid");
                                    }
                                })
                            }
                            return "123";
                        }
                    },{
                    //控制章节（歌曲）添加
                        closeAfterAdd: true,
                        afterSubmit:function (data) {
                            var status = data.responseJSON.status;
                            var id = data.responseJSON.message;
                            if (status){
                                $.ajaxFileUpload({
                                    url:"${pageContext.request.contextPath}/chapter/upload",
                                    type:"post",
                                    fileElementId:"name",
                                    data:{id:id,albumId:row_id},
                                    success:function () {
                                        //自动刷新页面
                                        $("#album-table").trigger("reloadGrid");
                                    }
                                })
                            }
                            return "123";
                        }
                    });
            },
            subGridRowColapsed : function(subgrid_id, row_id) {
                //删除专辑时调用的方法
                var subgrid_table_id;
                subgrid_table_id = subgrid_id+"_t";
                jQuery("#"+subgrid_table_id).remove();
            }
        });//1
        jQuery("#album-table").jqGrid('navGrid', '#album-page', {
            add : true,
            edit : true,
            del : true
        },{
            //控制编辑
            closeAfterEdit:true,
            afterSubmit:function (data) {
                var status = data.responseJSON.status;
                var id = data.responseJSON.message;
                var change = data.responseJSON.change;
                //如果修改完成，并且用户更改了专辑封面，进行文件上传
                if(status&&change){
                    $.ajaxFileUpload({
                        url:"${pageContext.request.contextPath}/album/upload",
                        type:"post",
                        fileElementId:"cover",
                        data:{id:id},
                        success:function () {
                            //响应回来后自动刷新表格
                            $("#album-table").trigger("reloadGrid");
                        }
                    })
                }
                return "123";
            }
        },{
            //控制添加
            closeAfterAdd:true,
            afterSubmit:function (data) {
                var status = data.responseJSON.status;
                var id = data.responseJSON.message;
                if(status){
                    $.ajaxFileUpload({
                       url:"${pageContext.request.contextPath}/album/upload",
                       type:"post",
                       fileElementId: "cover",
                       data:{id:id},
                       success:function (result) {
                           //自动刷新表格
                           $("#album-table").trigger("reloadGrid");
                       }

                    });
                }
                return "123";
            }

        });
    });

    function getOptions(){
        var options = "";
        var ops = "";
        $.ajax({
            async: false,
            url:"${pageContext.request.contextPath}/star/findAllOnlyStarName",
            type:"POST",
            success:function (result) {
                //遍历map集合，封装成options中需要的;号间隔的格式
                for (var key in result){
                    options += key+":"+result[key]+";";
                }
                ops = options.slice(0,options.length-1);
            }
        });
        return ops;
    }

</script>

<div class="navbar-default">
    <h3>展示所有的专辑</h3>
</div>

<table id="album-table"></table>
<div id="album-page" style="height: 60px"></div>