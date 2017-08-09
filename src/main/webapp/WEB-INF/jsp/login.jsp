<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp"%>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>中航工业沈飞公司</title>
	<script type="text/javascript" src="${ctx }/scripts/CFInstall.min.js"></script>
	<div id="chromeFramePrompt"  style="display: none;">
		<div>
			<a href="${ctx}/tmp/attached/FHD ERMIS4.5 Plugin.msi" target="_blank">如果想让系统运行更顺畅请下载安装 FHD ERMIS4.5 Plugin</a>
		</div>
	</div>

	<script type="text/javascript">
	
		function getRootWin() {
			var win = window;
			while (win != win.parent) {
				win = win.parent;
			}
			return win;
		}
		
		if (getRootWin().location != self.location) {
			getRootWin().location = self.location;
		}
		function valid() {
			var username = document.getElementById("userid").value;
			var password = document.getElementById("password").value;
			if (username == null || username == "") {
				alert("请输入用户名!");
				return false;
			}
			if (password == null || password == "") {
				alert("请输入密码!");
				return false;
			}
			return true;
		}
		function logon1(user,pass){
			document.getElementById("userid").value = user;
			document.getElementById("password").value = pass;
			setTimeout(function() {
				document.getElementById("logonform").submit();
			}, 100);	
		}
	</script>
	<style>
		
		body{
			background-color : #0656a8;
			margin : 0;
		}
		.backimg{
			margin-top : 50px;
			margin-left : 100px;
			background:url(${ctx}/images/loginimg1.png) no-repeat;
			position : absolute;
			width : 900px;
			height : 600px;
		}
		.main{
			position : absolute;
			width : 100%;
			height : 100%;
		}
		.header{
			height : 40%;
			width : 100%;
		}
		.center{
			height : 50%;
			width : 100%;
		}
		.centerLeft {
			float : left;
			height : 100%;
			width : 50%;
		}
		.centerCenter {
			float : left;
			height : 100%;
			background-image: url(${ctx}/images/loginlogin4.png);
			width : 45%;
			background-repeat: no-repeat;
		}
		
		.centerRight{
			float : left;
			height : 100%;
			width : 5%;
		}
		
		.footer{
			height : 10%;
			width : 100%;
		}

	.button_sub{
		background-image: url(${ctx}/images/loginbutton.png);
		background-repeat: no-repeat;
		width: 80px;
		height : 80px;
	}
	.buttondown_sub{
		background-image: url(${ctx}/images/loginbutton.png);
		background-repeat: no-repeat;
		border: 1px none #000;
		height: 80px;
		width: 80px;
		color: #FFF;
	}
	</style>
</head>
<body>
	<%
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0
				|| "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0
				|| "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0
				|| "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
	%>
		<div class="backimg"></div>
		<div class="main">
			<div class="header"></div>
			<div class="center">
			<div class="centerLeft"></div>
			<div class="centerCenter" >
				<div style="height:20%;margin-top:10px;font-size: 27pt;font-family:'Times New Roman';color: gray;">&nbsp;&nbsp;&nbsp;中航工业沈飞公司&nbsp;&nbsp;&nbsp;</div>
				<div style="height:15%;font-size: 18pt;font-family:'Times New Roman';color: black;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;风险管理系统</div>
				<div style="height:35%;">
					<form action="<c:url value='j_spring_security_check'/>" method="POST" id="logonform" onsubmit="return valid();">
						<input type="hidden" name="j_ip" id="ip" value="<%=ip%>" />
						<div style="width:110px;height:100%;float:left;">
							<div style="width:100%;height:50px;font-size:16pt;color:black;float:right;margin-right:10px;" align="right">用户名:</div>
							<div style="width:100%;height:50px;font-size:16pt;color:black;float:right;margin-right:10px;" align="right">密码:</div>
						</div>
						<div style="width:200px;height:100%;float:left;">
							<div style="width:100%;height:50px;font-size:16pt;color:black;">
								<input type="text" value="${param.j_username}" id="userid" name="j_username"
									style="width: 150px; height: 25px; background-color: white; border: solid 1px #153966; font-size: 14px; "
									size="20"/>
							</div>
							<div style="width:100%;height:50%;font-size:16pt;color:black;">
								<input type="password" id="password" name="j_password" validateAttr="allowNull=false"
									style="width: 150px; height: 25px; background-color: white; border: solid 1px #153966; font-size: 14px; "
									size="20" />
							</div>
						</div>
						<div style="width:100px;height:100%;float:left;">
							<input type="submit" name="button" value="登录" class="button_sub"  onmouseout="this.className='button_sub'"
								style="font-size: 20pt;"
								onMouseDown="this.className='buttondown_sub'" onMouseUp="this.className='button_sub'" />
						</div>
						<div style="width:10%;height:100%;float:left;">
						</div>
					</form>
				</div>
				<div style="height:30%;">
				<div id="status" class="errors" >
				<c:if test="${not empty param.login_error}">
						<font color="red">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;登录失败:用户名或者密码错误</font>
				</c:if>
				</div>
				</div>
			</div>
			<div class="centerRight"></div>
			</div>
			<div class="footer"></div>
		</div>
</body>

<script type="text/javascript">
<!--

// The conditional ensures that this code will only execute in IE,
// Therefore we can use the IE-specific attachEvent without worry
document.onload = function() {
	
	CFInstall.check({
		destination: document.URL,
		mode: "overlay", // the default
		node: "chromeFramePrompt",
		oninstall: function() {
			alert('Google Chrome Frame was successfully installed');
			window.location.reload(true);
			
		},
		onmissing: function() {
			var promptPane = document.getElementById('chromeFramePrompt');
			promptPane.style.display = 'block';
		},
		preventPrompt: true
	});
}

//-->
</script>
</html>
