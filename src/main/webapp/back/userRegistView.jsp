<%@page pageEncoding="UTF-8" %>

<script>

    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('main'));

    var option = {
        title: {
            text: '用户注册趋势'
        },
        tooltip: {},
        legend: {
            data:['男','女']
        },
        xAxis: {
            data: ["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"]
        },
        yAxis: {},
        series: [{
            name: '男',
            type: 'bar',
            data: []
        },{
            name: '女',
            type: 'bar',
            data: []
        }]
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);

    //页面加载完毕发送ajax请求
        $.ajax({
            url:"${pageContext.request.contextPath}/user/userRegistData",
            type:"post",
            datatype:"json",
            success:function (data) {
                console.log("男"+data.nan);
                console.log("女"+data.nv);
                // 指定图表的配置项和数据
                myChart.setOption({
                    series: [{
                    name: '男',
                    type: 'line',
                    data: data.nan
                },{
                    name: '女',
                    type: 'line',
                    data: data.nv
                }]
                });

            }
    });

</script>


<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 1000px;height:400px;"></div>