<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>index</title>
</head>
<body>
transfer out account amt:${transferOutAccount.amt}
<br>
transfer out account score:
<#if transferOutScore??>${transferOutScore.score}<#else>0</#if>

<br>
<br>
<br>
transfer in account amt:${transferInAccount.amt}
<br>
transfer in account score:
<#if transferInScore??>${transferInScore.score}<#else>0</#if>
<br>


<br>
<br>
click <a href="/transferout">transfer amt</a>
</body>
</html>