<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
    <table>
        <tr>
            <td>序号</td>
            <td>姓名</td>
            <td>年龄</td>
            <td>金额</td>
            <td>出生日期</td>
        </tr>
        <#if stus??>
            <#list stus as stu>
                <tr>
                    <td>${stu_index+1}</td>
                    <td <#if stu.name=='小明'>style="background-color:yellow;"</#if>>${stu.name}</td>
                    <td>${stu.age}</td>
                    <td <#if (stu.money > 300)>style="background-color:yellow;"</#if>>${stu.money}</td>
<#--                    <td>${stu.birthday?date}</td>-->
                    <td>${stu.birthday?string("yyyy年MM月dd日")}</td>
                </tr>
            </#list>
        </#if>
    </table>
<br>
    学生个数：${stus?size}
    <br>
遍历数据模型中的map数据<br>
<#--<#if stuMap?? && stuMap.stu1??>-->
    姓名：${(stuMap['stu1'].name)!'为空'}<br>
    年龄：${(stuMap['stu1'].age)!''}<br>
    姓名：${(stuMap.stu1.name)!''}<br>
    年龄：${(stuMap.stu1.age)!''}<br>
<#--</#if>-->
遍历map中的key<br>
<#list stuMap?keys as k>
    姓名：${stuMap[k].name}
    年龄：${stuMap[k].age}<br>
</#list>
<br>
${point?c}
<br>
    <#assign  text="{'bank':'工商银行','account':'10101920201920212'}"  />
    <#assign  data=text?eval  />
    开户行：${data.bank}    账号：${data.account}
</body>
</html>