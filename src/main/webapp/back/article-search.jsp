<%@page pageEncoding="UTF-8" isELIgnored="false" %>

<script>

    $(function () {
        //给全文搜索添加点击事件
        $("#article-search").click(function () {
            //发送ajax请求
            $.ajax({
                url: "${pageContext.request.contextPath}/article/search",
                type: "post",
                dataType: "json",
                data: "content=" + $("#search-input").val(),
                success: function (data) {
                    //清空表格中的内容
                    $("#search-table").empty();

                }
            })
        });

    })

</script>

<div class="container-fluid">
    <div class="row">
        <div class="col-sm-8 col-sm-offset-2">
            <div class="input-group">
                <input type="text" id="search-input" class="form-control" placeholder="请输入关键字检索文章...">
                <span class="input-group-btn">
            <button class="btn btn-primary" type="button" id="article-search">全文检索</button>
                </span>
            </div><!-- /input-group -->

        </div>
    </div>
    <br>
    <div class="row">
        <div class="col-sm-10 col-sm-offset-1">

            <%--表格--%>
            <div class="bs-example" data-example-id="simple-table">
                <table class="table" id="search-table">
                    <caption>搜索结果</caption>
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>标题</th>
                        <th>作者</th>
                        <th>简介</th>
                        <th>详细</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <th scope="row">1</th>
                        <td>Mark</td>
                        <td>Otto</td>
                        <td>@mdo</td>
                    </tr>

                    </tbody>
                </table>
            </div>
            <%--表格结束--%>

        </div>
    </div>
</div>