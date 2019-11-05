<%@page pageEncoding="UTF-8" %>

<script>
    //重要别删
    var suburl = "";
    /*展示模态框方法*/
    function openModal(data) {
        if (data){
            //1.根据id获取一行文章的数据
            var article = $("#article-table").jqGrid("getRowData",data);
            //修改 回显数据
            $("#article-id").val(article.id);
            $("#article-title").val(article.title);
            $("#article-author").val(article.author);
            $("#article-brief").val(article.brief);
            KindEditor.html("#editor_id",article.content);
            suburl = "${pageContext.request.contextPath}/article/update";
        } else{
            //添加 清空之前缓存的数据
            $("#article-id").val("");
            $("#article-title").val("");
            $("#article-author").val("");
            $("#article-brief").val("");
            KindEditor.html("#editor_id","");
            suburl = "${pageContext.request.contextPath}/article/save";
        }
        $("#article-modal").modal("show");
    }


    //保存时，调用的表单提交的方法
    function  submitForm(){
        myKindEditor.sync();
        //1.关闭模态框
        $("#article-modal").modal("hide");
        //2.表单提交发送ajax请求
        console.log("请求路径"+suburl);
        $.ajax({
            url: suburl,
            type: "post",
            datatype: "json",
            data: $("#article-form").serialize(),
            success: function (data) {
                if (data.status) {
                    //自动刷新页面
                    $("#article-table").trigger("reloadGrid");
                }else {
                    alert(data.message);
                }
            }
        })
    }

    /*控制kindeditor编辑器的*/
    var myKindEditor = KindEditor.create('#editor_id',{
            //点击图片空间按钮时发送的请求
            fileManagerJson:"${pageContext.request.contextPath}/article/imageSpace",
            //展示图片空间按钮
            allowFileManager:true,
            //上传图片所对应的方法
            uploadJson:"${pageContext.request.contextPath}/article/upload",
            //上传图片是图片的形参名称
            filePostName:"articleImg",
            //当失去焦点时，编辑器中的内容同步到表单中
            afterBlur:function () {
                this.sync();
            },
            afterCreate:function () {
                this.sync();
            }
    });

    /*jqGrid*/
    $(function () {
        //页面加载完毕给保存按钮添加点击事件
        $("#save-article").click(function () {
            submitForm();
        });
        $("#article-table").jqGrid({
            url : "${pageContext.request.contextPath}/article/findAllByPage",
            datatype : "json",
            height : 300,
            colNames : [ '编号', '标题', '作者', '简介', '内容', '创建时间', '操作' ],
            colModel : [
                {name : 'id'},
                {name : 'title'},
                {name : 'author'},
                {name : 'brief'},
                {name : 'content', hidden:true},
                {name : 'createDate'},
                {name : 'options' ,formatter:function (value,option,rows) {
                        return "<a href='#' class='btn btn-primary' onclick=\"openModal(\'"+rows.id+"\')\">修改</a>" +
                            "<a href='#' onclick=\"deleteArticle(\'"+rows.id+"\')\" class='btn btn-danger'>删除</a>"
                    }}
            ],
            rowNum : 5,
            styleUI: "Bootstrap",
            autowidth:true,
            rowList : [ 5, 10, 20 ],
            pager : '#article-page',
            viewrecords : true,
            caption : "所有的文章展示"
        }).navGrid("#article-page",
            {edit : false,add : false,del : false,search:false});

    });  //此行是页面加载开始的结束

    //删除一个文章的方法byid
    function deleteArticle(data) {
        $.ajax({
            url:"${pageContext.request.contextPath}/article/delete",
            type:"post",
            datatype:"json",
            data:{id:data},
            success:function (data) {
                if (data.status) {
                    //刷新表格
                    $("#article-table").trigger("reloadGrid");
                }else{
                    alert(data.message);
                }
            }
        })
    }

</script>

<%--标签页--%>
<ul class="nav nav-tabs" role="tablist">
    <li role="presentation" class="active"><a href="#home" >所有文章</a></li>
    <li role="presentation"><a href="#" onclick="openModal()" >添加文章</a></li>
</ul>

<div class="tab-content">
    <div role="tabpanel" class="tab-pane active" id="home">
        <%--此处是展示所有的文章--%>
        <table id="article-table"></table>
        <div id="article-page" style="height: 70px"></div>
    </div>
</div>

<%--模态框--%>
<div id="article-modal" class="modal fade" tabindex="-1" role="dialog" >
    <div class="modal-dialog" role="document">
        <div class="modal-content" style="width: 681px">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">文章操作</h4>
            </div>
            <div class="modal-body">
                <%--form表单--%>
                    <form class="form-horizontal" id="article-form">
                        <input id="article-id" type="text" name="id" class="hidden" >
                        <div class="form-group">
                            <label for="article-title" class="col-sm-2 control-label">文章标题</label>
                            <div class="col-sm-10">
                                <input type="text" name="title" class="form-control" id="article-title" placeholder="请输入文章标题">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="article-author" class="col-sm-2 control-label">文章作者</label>
                            <div class="col-sm-10">
                                <input type="text" name="author" class="form-control" id="article-author" placeholder="请输入文章作者">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="article-brief" class="col-sm-2 control-label">文章简介</label>
                            <div class="col-sm-10">
                                <input type="text" name="brief" class="form-control" id="article-brief" placeholder="请输入文章简介">
                            </div>
                        </div>
                        <%--kindeditor--%>
                       <textarea id="editor_id" name="content" style="width:500px;height:300px;"></textarea>
                    </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="save-article">保存</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->








