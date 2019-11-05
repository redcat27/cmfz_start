<%@page contentType="text/html; UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
    <script>
        $(function () {
            $("#viewTable").jqGrid({
                url : '${pageContext.request.contextPath}/banner/findAllByPager',
                datatype : "json",
                colNames : [ '编号', '名称', '封面', '描述', '状态','上传日期'],
                colModel : [
                    {name : 'id',hidden:true,editable:false},
                    {name : 'name',editable:true},
                    {name : 'cover',editable:true,edittype:"file",formatter:function (value,option,rows) {
                            return "<img style='width:120px;height:60px;' src='${pageContext.request.contextPath}/banner/"+rows.folder+"/"+rows.cover+"'>";
                        }},
                    {name : 'description',editable:true},
                    {name : 'status',editable:true,edittype:"select",editoptions:{value:"正常:正常;冻结:冻结"}},
                    {name : 'createDate'}
                ],
                height:250,
                autowidth:true,
                styleUI:"Bootstrap",
                rowNum : 3,
                rowList : [ 3,5,10 ],
                pager : '#banner-pager',
                sortname : 'id',
                viewrecords : true,
                caption : "轮播图列表",
                editurl : "${pageContext.request.contextPath}/banner/edit"
            }).navGrid("#banner-pager", {edit : true,add : true,del : true,search:false},{
                //控制修改
                closeAfterEdit:true,
                beforeShowForm:function(fmt){
                    fmt.find("#cover").attr("disabled",true);
                }
            },{
                //控制添加
                closeAfterAdd:true,
                afterSubmit:function (data) {
                    var status = data.responseJSON.status;
                    var id = data.responseJSON.message;
                    if(status){
                        //如果基本数据添加成功后，完成文件上传功能
                        $.ajaxFileUpload({
                            url:"${pageContext.request.contextPath}/banner/uploade",
                            type:"post",
                            fileElementId:"cover",
                            data:{id:id},
                            success:function (response) {
                                //自动刷新jqgrid表格
                                $("#viewTable").trigger("reloadGrid");
                            }
                        });
                    }
                    return "123";
                }
            });

        });
    </script>
<div class="panel panel-heading">
    <h3>所有的轮播图</h3>
</div>
<table id="viewTable"></table>
<div id="banner-pager" style="height: 100px"></div>
