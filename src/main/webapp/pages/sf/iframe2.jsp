<%@ page language="java" pageEncoding="utf-8"%> 
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp"%>
<%
response.setHeader("Cache-Control","no-store");
response.setHeader("Pragrma","no-cache");
response.setDateHeader("Expires",0);
%>
<html>
<head>
<base href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/">
		<title>沈阳飞机工业（集团）有限公司</title>
		<meta http-equiv="X-UA-Compatible" content="IE=7" /> 
		<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" /> -->
		
		<link rel="icon" href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/favicon.ico" type="image/x-icon" />  
		<script src="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/scripts/chart/FusionCharts.js" type="text/javascript"></script>
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/FHDstyle.css'/>"/>
				
		<script type="text/javascript" src="<c:url value='/scripts/jquery-1.10.2.min.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/Highstock-1.3.2/js/highstock.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/Highstock-1.3.2/js/highcharts-more.js'/>"></script>
		<script type="text/javascript" src="<c:url value ='/scripts/ckplayer6.3/ckplayer/ckplayer.js'/>"</script>
		
		<script type="text/javascript" src="<c:url value='/scripts/xbreadcrumbs.js'/>"></script>
		
		
		<script type="text/javascript" src="<c:url value='/scripts/fhd.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/sf-authory.js'/>"></script>
		
		<!-- ext4核心JS -->
		<script type="text/javascript" src="<c:url value='/scripts/ext-4.2/include-ext.js'/>"></script>
<%-- 		<script type="text/javascript" src="<c:url value='/scripts/ext-4.2/ext-all.js'/>"></script> --%>
		<!-- ext4中文支持JS -->
		<script type="text/javascript" src="<c:url value='/scripts/ext-4.2/locale/ext-lang-zh_CN.js'/>"></script>
		
		<script type="text/javascript" src="<c:url value='/scripts/kindeditor-4.1.1/kindeditor.js'/>"></script>
		
		<!-- 公用JS -->
		<script type="text/javascript" src="<c:url value='/app/view/kpi/cmp/kpi/result/ResultParam.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/app/view/kpi/kpi.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/App.js'/>" defer="defer"></script>
		<script type="text/javascript" src="<c:url value='/scripts/commons/dynamic.jsp'/>?"+Math.random();></script>
		
		<!-- 本地 国际化资源 -->
		<script type="text/javascript" src="<c:url value='/i18n'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/locale.js'/>"></script>
		
		<script type="text/javascript" src="<c:url value='/UserAuth'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/authority.js'/>"></script>
	

                <style type="text/css">
	                .x-grid-row-over .x-grid-cell-inner {
			            font-weight: bold;
			        }
					.x-action-col-cell img {
			            height: 16px;
			            width: 16px;
			            cursor: pointer;
			        }
				    /*  Custom styles for breadcrums (#breadcrumbs-3)  */
				    .xbreadcrumbs {
				        background: none;
				    } 
				    .xbreadcrumbs LI A {
				        text-decoration: underline;
        				color: #2c4674;
				    }
				    .xbreadcrumbs LI A:HOVER, .xbreadcrumbs#breadcrumbs-3 LI.hover A { color:#ff9900; text-decoration: none; }
				    .xbreadcrumbs LI.current A {
				        color: #333333;
				        text-decoration: none;
				    }
				    .xbreadcrumbs LI {
				    	height:25px;
				    	cursor:hand;
				        border-right: none;
				        background: url('images/separator-arrow.gif') no-repeat right center;
				        padding-right: 15px;
				        padding-left: 10px;
				    }
				    .xbreadcrumbs LI.current { background: none; }
				    .xbreadcrumbs LI UL LI { background: none; padding: 0;  }
				    
				
				.aaa-btn{
					background-image:none !important;
					background-attachment:scroll !important;
					background-repeat:repeat !important;
					background-position-x:0% !important;
					background-position-y:0% !important;
					background-color:rgb(217, 231, 248) !important;
				}
				
				.aaa-selected-btn{
					background-color:gray !important;
				}
				
				.menu-btn{
					background-image:none !important;
					background-attachment:scroll !important;
					background-repeat:repeat !important;
					background-position-x:0% !important;
					background-position-y:0% !important;
					background-color:#FFF !important; 
				}
				.menu-selected-btn{background-color:rgb(223, 232, 246) !important; border-left:25px rgb(0,117,170) solid  !important;}
				 #main-panel-mydatas td {
            			padding:5px;
       		 }
				</style>
		
		
		<style type="text/css">
		
			#loading {
				position: absolute;
				left: 42%;
				float:right;
				top: 45%;
				padding: 2px;
				z-index: 20001;
				height: 50px;
				border: 2px solid #ccc;
			}
			.x-panel-ghost {
			    z-index: 1;
			}
			.x-border-layout-ct {
			    background: #DFE8F6;
			}
			.x-portal-body {
			    padding: 0 0 0 8px;
			}
			.x-portal .x-portal-column {
			    padding: 8px 8px 0 0;
			}
			.x-portal .x-panel-dd-spacer {
			    border: 2px dashed #99bbe8;
			    background: #f6f6f6;
			    border-radius: 4px;
			    -moz-border-radius: 4px;
			    margin-bottom: 10px;
			}
			.x-portlet {
			    margin-bottom:10px;
			    padding: 1px;
			}
			.x-portlet .x-panel-body {
			    background: #fff;
			}
			.portlet-content {
			    padding: 10px;
			    font-size: 12px;
			}
			.x-tab-default-top .x-tab-inner {
			height: 14px !important;
			line-height: 14px !important;
			}
			
/*div菜单样式*/
.frame {width:150px; height:140px; padding:0px; border:1px solid #ccc; float:left; margin-right:5px; margin-top:2px;margin-bottom:2px;display:inline;}
.wrap {width:145px; height:135px; position:relative; overflow:hidden; font-family:arial, sans-serif;}
.wrap div {width:140px; height:165px; padding:60px 10px 10px 10px; position:absolute; left:0; top:0;}
.wrap div b {display:block; width:143px; height:155px; position:absolute; left:0; top:0px; z-index:1; background:#15498b;filter: alpha(opacity=90); }
.wrap div span {position:relative; z-index:500;}
.wrap div h1{font-size: 15px;color:#15498b;line-height: 26px;margin-bottom:0;position: relative; top:40px; text-align:right;font-family:helvetica, arial, verdana, sans-serif}
.wrap div p {font-size:12px; color:#15498b;margin-top:0; position: relative; bottom:-70px; font-family:helvetica, arial, verdana, sans-serif}
.frame:hover{border:1px solid #d5e2f2; background:#eee}
.clear {clear:both;}





.fieldsets{
    padding:5px;
    margin:5px;
    color:#333; 
    
} 
.fieldsett{
	padding:4px;
    margin:1px;	
	margin-bottom:0px;
	border: #16ebf8 solid 1px;
}
.fieldsett1{
	padding:1px;
    margin:1px;	
	margin-bottom:0px;
	border: #16ebf8 none 1px;
}
.tr_td_text01{
	font-size:14px;
	font-weight:bold;
}
.menuTable{
	border-right-color: [color=Blue]#AA9FFF[/color];
    border-bottom-color: #AA9FFF;
    border-left-color: #AA9FFF;
    border-top-color: #AA9FFF;
    border-top-width: 1px;
    border-right-width: 1px solid #fcfcfc;
    border-bottom-width: 1px;
    border-left-width: 1px;
}

	/*查询列表*/
/* whole table*/
.fhd_query {
	margin:0 auto;
	border-collapse:collapse;
}

.fhd_query tr, .fhd_query tr{
	FONT-SIZE: 14px;
	COLOR: #000;
	BACKGROUND: #fff;
	FONT-FAMILY: "宋体", Arial;
	text-decoration:none;
	text-indent:5px;
	border: 1px none #89c7ee;
	border-top:none;
}
/* table head */
.fhd_query tr td.alt  {
	FONT-SIZE: 16px;
	font-weight: bold;
	height: 30px;
	border: 1px solid #64a9ee;
	background: #cde1f7;
}
.fhd_query tr td.alt1  {
	border: 0px none #cdcdcd;
	background: #fbefd6;
}
.fhd_query tr td.alt2  {
	FONT-SIZE: 12px;
	font-weight: bold;
	height: 30px;
	border: 1px solid #cdcdcd;
	background: #fbefd6;
}
.fhd_query tr td.alt30  {
	Height: 20px;
	border-right: 1px solid #fcdc9a;
	border-left: 1px solid #fcdc9a;
	background: #e8efd3;
}
.fhd_query tr td.alt31  {
	border-left: 1px solid #fcdc9a;
	background: #e8efd3;
}
.fhd_query tr td.alt32  {
	background: #e8efd3;
}
.fhd_query tr td.alt33  {
	border-right: 1px solid #fcdc9a;
	background: #e8efd3;
}
.fhd_query tr td.alt34  {
	border-right: 1px solid #fcdc9a;
	background: #e8efd3;
}
.fhd_query tr td.alt35  {
	FONT-SIZE: 12px;
	border-bottom: 1px solid #fcdc9a;
	background: #e8efd3;
}
/* table head */
.fhd_query tr td.altt  {
	FONT-SIZE: 16px;
	font-weight: bold;
	height: 30px;
	border: 1px solid #64a9ee;
	background: #daeafb;
}
/* table head */
.fhd_query tr td.altt01  {
	FONT-SIZE: 14px;
	font-weight: bold;
	height: 30px;
	border-top: 1px solid #64a9ee;
	border-left: 1px solid #64a9ee;
	background: #cde1f7;
}
.fhd_query tr td.altt02  {
	FONT-SIZE: 12px;
	font-weight: bold;
	height: 30px;
	border-top: 1px solid #64a9ee;
	border-right: 1px solid #64a9ee;
	background: #cde1f7;
}




.fhd_query13 {
	margin:0 auto;
	border-collapse:collapse;
	border:1px solid #89c7ee;
}

.fhd_query13 tr, .fhd_query13 tr{
	FONT-SIZE: 12px;
	COLOR: #000;
	BACKGROUND: #fff;
	FONT-FAMILY: "宋体", Arial;
	HEIGHT: 25px;
	text-decoration:none;
	text-indent:5px;
	border: 1px none #89c7ee;
	border-top:none;
}
/* table head */
.fhd_query13 tr td.alt  {
	FONT-SIZE: 14px;
	height:33px;
	border:1px solid #89c7ee;
	border-top:none;
	background:#EBF3F5;
	text-align:center;
}
/* table head */
.fhd_query13 tr td.altt  {
	FONT-SIZE: 14px;
	height:28px;
	border:1px none #89c7ee;
	background:#ffffff;
}
/* table head */
.fhd_query13 tr td.altt2  {
	FONT-SIZE: 14px;
	height:32px;
	border:1px none #89c7ee;
	border-bottom:1px dashed #89c7ee;
	background:#fcfaf8;
}
/* table head */
.fhd_query13 tr td.altttt0  {
	FONT-SIZE: 12px;
	border:1px none #cdcdcd;
	background:#fbefd6;
}
/* table head */
.fhd_query13 tr td.altttt1  {
	FONT-SIZE: 12px;
	border-right:1px solid #cdcdcd;
	background:#fbefd6;
}
/* table head */
.fhd_query13 tr td.altttt2  {
	FONT-SIZE: 12px;
	border-left:1px solid #cdcdcd;
	background:#fbefd6;
}
/* table head */
.fhd_query13 tr td.altttt3  {
	FONT-SIZE: 12px;
	border-right:1px solid #cdcdcd;
	background:#fbefd6;
}
/* table head */
.fhd_query13 tr td.altttt13  {
	FONT-SIZE: 12px;
	border-right:1px solid #fcdc9a;
	background:#e8efd3;
}


.fhd_query14 {
	margin:0 auto;
	border-collapse:collapse;
	border:1px solid #89c7ee;
}

.fhd_query14 tr, .fhd_query14 tr{
	FONT-SIZE: 12px;
	COLOR: #000;
	BACKGROUND: #fff;
	FONT-FAMILY: "宋体", Arial;
	HEIGHT: 25px;
	text-decoration:none;
	text-indent:5px;
}
.fhd_query14 tr td.altt0  {
	FONT-SIZE: 14px;
	height:33px;
	border:1px solid #89c7ee;
	border-top:none;
	background:#EBF3F5;
	text-align:center;
}
.fhd_query14 tr td.altt1  {
	FONT-SIZE: 14px;
	height: 30px;
	border: 1px none #89c7ee;
	border-bottom:1px dashed #89c7ee;
	background:#ffffff;
}
.fhd_query14 tr td.altt2  {
	FONT-SIZE: 14px;
	height: 30px;
	border: 1px none #89c7ee;
	border-bottom:1px dashed #89c7ee;
	background:#fcfaf8;
}
.fhd_query14 tr td.alttt1  {
	FONT-SIZE: 14px;
	height: 40px;
	border: 1px none #89c7ee;
	border-bottom:1px dashed #89c7ee;
	background:#ffffff;
}
.fhd_query14 tr td.alttt2  {
	FONT-SIZE: 14px;
	height: 40px;
	border: 1px none #89c7ee;
	border-bottom:1px dashed #89c7ee;
	background:#fcfaf8;
}
.fhd_query14 tr td.altt3  {
	FONT-SIZE: 14px;
	height: 199px;
	border: 1px none #89c7ee;
	border-bottom:1px dashed #89c7ee;
	background:#fcfaf8;
}


/*按钮的样式*/
/* POSITIVE */
button.positive1, .buttons a.positive1{
    width: 255px;
    margin:0 1px;
    height: 48px;
    background-color:#d3e9f5;
    border:2px solid #ade8ec;
    color:#336699;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
}
.buttons a.positive1:hover, button.positive1:hover{
    background-color:#a1eb90;
    border:1px solid #a1eb90;
    color:#336699;
}
.buttons a.positive1:active{
    background-color:#a1eb90;
    border:2px solid #a1eb90;
    color:#fff;
}
button.positive2, .buttons a.positive2{
	float:left;
	margin:0 1px;
    width: 191px;
    height: 48px;
    background-color:#d3e9f5;
    border:2px solid #ade8ec;
    color:#336699;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
}
.buttons a.positive2:hover, button.positive2:hover{
    background-color:#a1eb90;
    border:1px solid #a1eb90;
    color:#336699;
}
.buttons a.positive2:active{
    background-color:#a1eb90;
    border:2px solid #a1eb90;
    color:#fff;
}



.altt01 a {
	width: auto;
	display: block;
	text-indent: 3px;
	padding: 2px 0;
	text-decoration: none;
	font-weight: bold;
	color: black;
	height: 22px;
}
.altt01 a:visited {
	FONT-SIZE: 14px;
	COLOR: #FFFFFF;
	FONT-FAMILY: "黑体";
	font-weight: bold;
	text-decoration: none;
}
.altt01 a:hover {
	color:FE6C00;
	background-color: #F0F0F0;
 	text-decoration: none;
}
.altt01 a:active {
 	text-decoration: none;
}

.x-grid-back-red { 
	background: #fafafa; 
}
</style>
<script type="text/javascript">
String.prototype.startWith = function(s) {
	if (s == null || s == "" || this.length == 0 || s.length > this.length)
		return false;
	if (this.substr(0, s.length) == s)
		return true;
	else
		return false;
	return true;
}
	function showDiv(divid){
        var _tips = document.getElementById(divid);
        _tips.style.display  	= "";
        
        if(divid == "newsDiv"){
        	document.getElementById("riskDiv").style.display = "none";
        }else{
        	document.getElementById("newsDiv").style.display = "none";
        }
	}
     function onMouseMoveFun(){
    	$(document).ready(function(){
        	$(".wrap div").hover(function() {
        		$(this).animate({"top": "-100px"}, 300, "swing");
        	},function() {
        		$(this).stop(true,false).animate({"top": "0px"}, 300, "swing");
        	});

        	});
    }
     function onMenuClickaa(url,title,businessId,nodeId){
 		var url = url;
 		var text = title;//FHD.titleJs[url];
 		var centerPanel = parent.Ext.getCmp('center-panel');
 		var tab = centerPanel.getComponent(url);
 		if(tab){
 			centerPanel.setActiveTab(tab);
 		}else{
 			if(url.startWith('FHD')){
 				var p = centerPanel.add(parent.Ext.create(url,{
 					id:url,
 					typeId:businessId,
 					businessId:businessId,
 					nodeId:nodeId,
 					title: text,
 					tabTip: text,
 					closable:true
 				}));
 				centerPanel.setActiveTab(p);
 			} else if (url.startWith('/pages')){
 				var p = centerPanel.add({
 					id:url,
 					title: text,
 					tabTip: text,
 					layout:'fit',
 					autoWidth:true,
 					border:false,
 					//iconCls: 'tabs',
 					closable:true,
 					autoLoad :{ url: __ctxPath+url,scripts: true}
 				});
 				centerPanel.setActiveTab(p);
 			}else{
 				var p = centerPanel.add({
 					id:url,
 					title: menu.text,
 					tabTip:menu.text,
 					layout:'fit',
 					autoWidth:true,
 					border:false,
 					//iconCls: 'tabs',
 					closable:true,
 					html : '<iframe width=\'100%\' height=\'100%\' frameborder=\'0\' src=\''+__ctxPath+url+'\'></iframe>'
 					//autoLoad :{ url: 'pages/icon.jsp',scripts: true}
 					//items:[{xtype:'dictTypelist'}]
 				});
 				centerPanel.setActiveTab(p);
 			}
 		}
 	}
     function getVedioList(type){
 		//制度详细查看
			var me = this;
			me.vedioGrid = Ext.create('FHD.view.sf.index.VedioListGridPanel',{
				border:false,
				layout:'fit',
				checked: false,
				type:type
			});
			me.vedioGrid.reloadData();
			me.formwindow = new Ext.Window({
				iconCls: 'icon-show',//标题前的图片
				modal:true,//是否模态窗口
				collapsible:true,
				title:'监控列表',
				width:600,
				height:400,
				layout: {
					type: 'fit'
		        },
				maximizable:true,//（是否增加最大化，默认没有）
				constrain:true,
				items : [me.vedioGrid],
				buttons: [{
		    				text: '关闭',
		    				handler:function(){
		    					me.formwindow.close();
		    				}
		    			}]
			});
			me.formwindow.show();
       }
     function getVedio(name){
     	var centerPanel = parent.Ext.getCmp('center-panel');
     	var tab = centerPanel.getComponent('FHD.view.sf.index.SFVedioPanel2');
     	if(tab){
     		tab.showVedio(name);
     	}else{
     		var vedioPanel = Ext.create('FHD.view.sf.index.SFVedioPanel2',{
      		});
     		vedioPanel.showVedio(name);
     	}
     	
     	
      }
    //$(document).ready(function(){
     Ext.onReady(function () {
    	var me = this;
    	var dateCon = Ext.create('FHD.view.sf.index.SFMemoMain', {
    		renderTo: 'dateMainPanelDiv', 
			border:true
    	});
    	/*
    	var dp = new Ext.DatePicker({  
            renderTo: 'dateMainPanelDiv',  
            minDate: Ext.Date.format("2009-01-01","Y-m-d"),  
            maxDate: Ext.Date.format("2009-12-30","Y-m-d"),  
            value: Ext.Date.format("2009-12-30","Y-m-d"),  
            handler: function(){  
                Ext.Msg.alert("日期",Ext.util.Format.date(this.getValue(),'Y-m-d'));  
            }  
        });  */
		var backlogGrid = Ext.create('FHD.view.sf.index.SFMyTaskGrid',{
			renderTo: 'backlogPanelDiv', 
			height:279
		});
		
		me.riskDocPanel = Ext.create('FHD.view.sf.index.SFRiskFileTab',{
			renderTo: 'riskFilePanelDiv',
			fn:onMenuClickaa
		});
    	
		var testPanel = null;
		var arr = [];
	    FHD.ajax({ //ajax调用
	    	async:false,
	        url: __ctxPath + '/sf/index/getRiskRbsBySome.f',
	        params: {
	        },
	        callback: function (data) {
	        	if(data.result){
	        		arr = data.result;
	        	}
	        }
	    });
	    //+'<td class="altt02" align="right"><a href="javascript:void(0)">更多&nbsp;</a></td>'
		Ext.create('Ext.panel.Panel',{
			renderTo: 'importantTenRisksDIV',
			border:false,
			/* html:'<tr>'
				+'<td colspan="2">'
				+'<fieldset class="fieldsett1">'
		  		+'<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query">'
		    	+'<tr>'
		    	+'<td class="altt01"><img src="images/littleTitle.png" width="16px" height="16px" />&nbsp;重大风险</td>'
		    	+'<td class="altt02" align="right"><span style="cursor: pointer;font-size:14px" onClick="onMenuClickaa(\'FHD.view.sf.index.MoreRisksGridPanel\',\'重大风险\')">更多&nbsp;</span></td>'
		    	+'</tr>'
		    	+'</table>'
		    	+'<table width="100%" cellpadding="0" cellspacing="4" style="border: 1px solid #89c7ee;">'
		    	+'<tr>'
		    	+'<td align="center" valign="middle">'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[0].riskId+'\')">'
		    	+'<span>'+'①'+arr[0].riskName+'</span>'
		    	+'<img src="'+arr[0].assessementStatus+'"/>&nbsp;<img src="'+arr[0].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[1].riskId+'\')">'
		    	+'②'+arr[1].riskName
		    	+'<img src="'+arr[1].assessementStatus+'"/>&nbsp;<img src="'+arr[1].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[2].riskId+'\')">'
		    	+'③'+arr[2].riskName
		    	+'<img src="'+arr[2].assessementStatus+'"/>&nbsp;<img src="'+arr[2].etrend+'"/>'
		    	+'</button>'
		    	+'</td>'
		    	+'</tr>'
		    	+'<tr>'
		    	+'<td align="center" valign="middle">'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[3].riskId+'\')">'
		    	+'④'+arr[3].riskName
		    	+'<img src="'+arr[3].assessementStatus+'"/>&nbsp;<img src="'+arr[3].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[4].riskId+'\')">'
		    	+'⑤'+arr[4].riskName
		    	+'<img src="'+arr[4].assessementStatus+'"/>&nbsp;<img src="'+arr[4].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[5].riskId+'\')">'
		    	+'⑥'+arr[5].riskName
		    	+'<img src="'+arr[5].assessementStatus+'"/>&nbsp;<img src="'+arr[5].etrend+'"/>'
		    	+'</button>'
		    	+'</td>'
		    	+'</tr>'
		    	+'<tr>'
		    	+'<td align="center" valign="middle">'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[6].riskId+'\')">'
		    	+'⑦'+arr[6].riskName
		    	+'<img src="'+arr[6].assessementStatus+'"/>&nbsp;<img src="'+arr[6].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[7].riskId+'\')">'
		    	+'⑧'+arr[7].riskName
		    	+'<img src="'+arr[7].assessementStatus+'"/>&nbsp;<img src="'+arr[7].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[8].riskId+'\')">'
		    	+'⑨'+arr[8].riskName
		    	+'<img src="'+arr[8].assessementStatus+'"/>&nbsp;<img src="'+arr[8].etrend+'"/>'
		    	+'</button>'
		    	//+'<button type="submit" class="positive2" name="save" onClick="showAssessResult(\''+arr[9].riskId+'\')">'
		    	//+'⑩'+arr[9].riskName
		    	//+'<img src="'+arr[9].assessementStatus+'"/>&nbsp;<img src="'+arr[9].etrend+'"/>'
		    	//+'</button>'
		    	+'</td>'
		    	+'</tr>'
		    	+'</table>'
		    	+'</fieldset>'
		    	+'</td>'
		    	+'</tr>' */
			html:'<tr>'
				+'<td colspan="2">'
				+'<fieldset class="fieldsett1">'
		  		+'<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query">'
		    	+'<tr>'
		    	+'<td class="altt01"><img src="images/littleTitle.png" width="16px" height="16px" />&nbsp;重大风险</td>'
		    	+'<td class="altt02" align="right"><span style="cursor: pointer;font-size:14px" onClick="onMenuClickaa(\'FHD.view.sf.index.MoreRisksGridPanel\',\'重大风险\')">更多&nbsp;</span></td>'
		    	+'</tr>'
		    	+'</table>'
		    	+'<table width="100%" cellpadding="0" cellspacing="4" style="border: 1px solid #89c7ee;">'
		    	+'<tr>'
		    	+'<td align="center" valign="middle">'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[0].riskId+'\')">'
		    	+'<span>'+'①'+arr[0].riskName+'</span>'
		    	+'<img src="'+arr[0].assessementStatus+'"/>&nbsp;<img src="'+arr[0].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[1].riskId+'\')">'
		    	+'②'+arr[1].riskName
		    	+'<img src="'+arr[1].assessementStatus+'"/>&nbsp;<img src="'+arr[1].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[2].riskId+'\')">'
		    	+'③'+arr[2].riskName
		    	+'<img src="'+arr[2].assessementStatus+'"/>&nbsp;<img src="'+arr[2].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[3].riskId+'\')">'
		    	+'④'+arr[3].riskName
		    	+'<img src="'+arr[3].assessementStatus+'"/>&nbsp;<img src="'+arr[3].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[4].riskId+'\')">'
		    	+'⑤'+arr[4].riskName
		    	+'<img src="'+arr[4].assessementStatus+'"/>&nbsp;<img src="'+arr[4].etrend+'"/>'
		    	+'</button>'
		    	+'</td>'
		    	+'</tr>'
		    	+'<tr>'
		    	+'<td align="center" valign="middle">'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[5].riskId+'\')">'
		    	+'⑥'+arr[5].riskName
		    	+'<img src="'+arr[5].assessementStatus+'"/>&nbsp;<img src="'+arr[5].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[6].riskId+'\')">'
		    	+'⑦'+arr[6].riskName
		    	+'<img src="'+arr[6].assessementStatus+'"/>&nbsp;<img src="'+arr[6].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[7].riskId+'\')">'
		    	+'⑧'+arr[7].riskName
		    	+'<img src="'+arr[7].assessementStatus+'"/>&nbsp;<img src="'+arr[7].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[8].riskId+'\')">'
		    	+'⑨'+arr[8].riskName
		    	+'<img src="'+arr[8].assessementStatus+'"/>&nbsp;<img src="'+arr[8].etrend+'"/>'
		    	+'</button>'
		    	+'<button type="submit" class="positive1" name="save" onClick="showAssessResult(\''+arr[9].riskId+'\')">'
		    	+'⑩'+arr[9].riskName
		    	+'<img src="'+arr[9].assessementStatus+'"/>&nbsp;<img src="'+arr[9].etrend+'"/>'
		    	+'</button>'
		    	+'</td>'
		    	+'</tr>'
		    	+'<tr>'
		    	+'<td align="center" valign="middle">'
		    	+'</td>'
		    	+'</tr>'
		    	+'</table>'
		    	+'</fieldset>'
		    	+'</td>'
		    	+'</tr>'
		});
     });
   // });
     
</script>		
	</head>
	<body onload="renderCss()">
<table style="width: 100%;height: 200px;"   border="0" cellpadding="0" cellspacing="0" align="center">
	<tr>
    	<td valign="top">
    		<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"/>
    			<tr>
    				<td width="64%" valign="top">
			    		<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"/>
			    			<tr>
			    				<td width="33%" valign="top">
									<fieldset class="fieldsett1">
									<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query">
								    	<tr>
									      	<td class="altt01">个人日志</td>
									      	<td class="altt02" align="right"></td>
									    </tr>
									</table>  
								  	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query">
								    	<tr>
								    		<td>
									      		<div id='dateMainPanelDiv' ></div>
									      	</td>
									    </tr>
									</table>
									</fieldset>
			    				</td>
			    				<td width="67%" valign="top">
									<fieldset class="fieldsett1">
									<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query">
								    	<tr>
									      	<td class="altt01"><span style="float: left;">待办工作</span><div class="" style=""><img src="images/icons/arrow_refresh_blue.png" style="cursor: pointer;" onClick="refreshMyTaskGrid();"/></div></td>
									      	<td class="altt02" align="right"></td>
									    </tr>
									</table> 
								  	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query">
								    	<tr>
								    		<td>
									      		<div id='backlogPanelDiv'></div>
									        </td>
									    </tr>
									</table>
									</fieldset>
			    				</td>
			    			</tr>
			    		</table>
			    				<!-- <div id="importantTenRisksDIV">
			    				</div> -->
    				</td>
    				<td width="36%" valign="top">
						<fieldset class="fieldsett1"> 
					  	<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query">
					    	<tr>
						      	<td class="altt01">指标监控</td>
						      	<td class="altt02" align="right"><span style="cursor: pointer;font-size:14px" onClick="onMenuClickaa('FHD.view.kpi.sf.SfMonitorHomeMainPanel','指标监控');">更多&nbsp;</span></td>
						    </tr>
						</table>
					  	<table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #89c7ee;">
			    			<tr>
						      	<td align="center" valign="middle" colspan="2">
						      	<!-- <img src="pages/sf/risk03.jpg" width="444px" height="296px" /> -->
						      	<div id="chartdivEVA" align="center" style="height:272px;margin:2px 0"></div>
						      	<script type="text/javascript">
								   	var chart = new FusionCharts("${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/images/chart/MSCombiDY2D.swf", "ChartIdEVA", "454", "271", "0", "0");
								   	chart.setXMLUrl("${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/pages/sf/data/CombiDY2D1.xml");		   
								   	chart.render("chartdivEVA");
								</script>		      	
						      	</td>
						    </tr>
						    <tr><td height="1" colspan="2"></td></tr>
						   <!--  <tr>
						      	<td align="center" valign="middle" width="20%" style="background:#cde1f7;border-top: 1px solid #89c7ee;border-bottom: 1px solid #89c7ee;">
						      		<font style="font-size: 12px; font-weight: bold;">生产安全风险<br>(2014年03月)</font>
						      	</td>
						      	<td align="center" valign="middle" width="80%" style="background:#cde1f7;border-top: 1px solid #89c7ee;border-bottom: 1px solid #89c7ee;">
						      	<img src="pages/sf/bbb1.png" width="354px" height="44px" />
						      	<div id="chartdivLinear1" align="center" style="margin:10px;"></div>
							   	<script type="text/javascript">
									var myChart = new FusionCharts("${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/images/chart/HLinearGauge.swf", "myChartId1", "354", "63", "0", "0");
									myChart.setDataURL("${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/pages/sf/data/Linear1.xml");
									myChart.render("chartdivLinear1");
							   	</script>  	
						      	</td>
						    </tr>
						    <tr><td height="2px" colspan="2"></td></tr>
						    <tr>
						      	<td align="center" valign="middle" style="background:#cde1f7;border-top: 1px solid #89c7ee;">
						      		<font style="font-size: 12px; font-weight: bold;">质量保证风险<br>(2014年03月)</font>
						      	</td>
						      	<td align="center" valign="middle" style="background:#cde1f7;border-top: 1px solid #89c7ee;">
						      	<img src="pages/sf/bbb2.png" width="354px" height="44px" />
						      	<div id="chartdivLinear2" align="center" style="margin:10px;"></div>
							   	<script type="text/javascript">
									var myChart = new FusionCharts("${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/images/chart/HLinearGauge.swf", "myChartId2", "354", "63", "0", "0");
									myChart.setDataURL("${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/pages/sf/data/Linear2.xml");
									myChart.render("chartdivLinear2");
							   	</script>
						      	</td>
						    </tr> -->
						    <!-- <tr><td height="2px" colspan="2"></td></tr>
						    <tr>
						      	<td align="center" valign="middle" style="background:#89c7ee;">
						      		<font style="font-size: 12px; font-weight: bold;">试飞安全风险<br>(2014年03月)</font>
						      	</td>
						      	<td align="center" valign="middle" style="background:#89c7ee;">
						      	<img src="pages/sf/bbb3.png" width="354px" height="44px" />
						      	<div id="chartdivLinear1" align="center"></div>
							   	<script type="text/javascript">
									var myChart = new FusionCharts("${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/images/chart/HLinearGauge.swf", "myChartId", "300", "80", "0", "0");
									myChart.setDataURL("${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/pages/sf/data/Linear1.xml");
									myChart.render("chartdivLinear1");
							   	</script>      	
						      	</td>
						    </tr> -->
					  	</table>
						</fieldset>
    				</td>
    			</tr>
    		</table>
		</td>
  	</tr>
  	<tr>
  	<td valign="top">
    	<div id="importantTenRisksDIV"></div>
    </td>
    </tr>
	<tr>
    	<td valign="top">
    		<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"/>
    			<tr>
    				<td valign="top">
						<fieldset class="fieldsett1">
					  	<table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #89c7ee;">
			    			<tr height="120px">
						      	<!-- 
						      	<td align="center" valign="middle" width="12.5%"><img src="pages/sf/risk01.jpg" width="148px" height="100px" style="border: 1px solid #89c7ee;"/></td>
						      	<td align="center" valign="middle" width="12.5%"><img src="pages/sf/risk01.jpg" width="148px" height="100px" style="border: 1px solid #89c7ee;"/></td>
						      	<td align="center" valign="middle" width="12.5%"><img src="pages/sf/risk01.jpg" width="148px" height="100px" style="border: 1px solid #89c7ee;"/></td>
						      	<td align="center" valign="middle" width="12.5%"><img src="pages/sf/risk01.jpg" width="148px" height="100px" style="border: 1px solid #89c7ee;"/></td>
						      	<td align="center" valign="middle" width="12.5%"><img src="pages/sf/risk01.jpg" width="148px" height="100px" style="border: 1px solid #89c7ee;"/></td>
						      	<td align="center" valign="middle" width="12.5%"><img src="pages/sf/risk01.jpg" width="148px" height="100px" style="border: 1px solid #89c7ee;"/></td>
						      	<td align="center" valign="middle" width="12.5%"><img src="pages/sf/risk01.jpg" width="148px" height="100px" style="border: 1px solid #89c7ee;"/></td>
						      	<td align="center" valign="middle" width="12.5%"><img src="pages/sf/risk01.jpg" width="148px" height="100px" style="border: 1px solid #89c7ee;"/></td>
						      	-->
						      	<td valign="middle">
						      	<div>
						      		<a href="javascript: onMenuClickaa('FHD.view.myallfolder.MyAllFolderMain','我的文件夹');">
						      			<div class="frame" onMouseMove="onMouseMoveFun();">
						      				<div class="wrap">
						      					<img src="images/homepage/super_mono_3d_28.png" border="0" style="width:70px; height:70px; padding:5px"/>
					      						<div>
					      						<span>
					      						<h1>我的文件夹</h1>
					      						<p>我的文件夹包括所在部门的目标、指标、各类风险、风险应对方案、管理流程、风险考核结果、突发风险事件。</p>
					      						</span>
					      						</div>
					      					</div>
					      				</div>
					      			</a>
					      			<a href="javascript:onMenuClickaa('FHD.view.sf.em.reportedRisk.ReportedRiskMainPanel','信息上报');">
					      				<div class="frame" onMouseMove="onMouseMoveFun();">
					      					<div class="wrap">
					      						<img src='images/homepage/super_mono_3d_part2_08.png' border="0" style="width:70px; height:70px; padding:5px"/>
			        							<div>
										        <span> 
										        <h1>信息上报</h1>   
										        <p>风险信息包括外部信息和内部信息，主要为风险管理过程相关的外部信息和组织内部影响其风险管理的任何事物。</p>      
										        </span>
										        </div>
									        </div>
								        </div>
							        </a>
							        <a href="javascript:onMenuClickaa('FHD.view.sf.em.emergency.EmergencyMainPanel','突发事件上报');">
								        <div class="frame" onMouseMove="onMouseMoveFun();">
									        <div class="wrap">
										        <img src='images/homepage/super_mono_3d_part2_28.png' border="0" style="width:70px; height:70px; padding:5px"/>
										        <div>
										        <span> 
										        <h1>突发事件上报</h1>   
										        <p>主要上报突发事件所发生的时间、地点、责任部门以及该事件所产生的影响和应急措施的效果。</p>  
										        </span>
										        </div>
									        </div>
								        </div>
							        </a>
							        <a href="javascript:onMenuClickaa('FHD.view.sf.em.emergencyPlan.EmergencyPlanMainPanel','应急方案制定');">
								        <div class="frame" onMouseMove="onMouseMoveFun();">
									        <div class="wrap">
										        <img src='images/homepage/super_mono_3d_85.png' border="0" style="width:70px; height:70px; padding:5px"/>
										        <div>
										        <span> 
										        <h1>应急方案制定</h1>   
										        <p>针对突发风险事件进行信息收集、分析评价并制定详细的应对措施，同时可以提出各类资源保障需求。</p>  
										        </span>
										        </div>
									        </div>
								        </div>
							        </a>
							        <a href="javascript:onMenuClickaa('FHD.view.sf.em.emergencyPlanExecute.EmergencyPlanExecuteMainPanel','应急实施报告');">
								        <div class="frame" onMouseMove="onMouseMoveFun();">
									        <div class="wrap">
										        <img src='images/homepage/super_mono_3d_part2_67.png' border="0" style="width:70px; height:70px; padding:5px"/>
										        <div >
										        <span> 
										        <h1>应急实施报告</h1>   
										        <p>对应急方案的实施情况和所取得的效果以及实施过程中遇到的问题进行上报，并提出应急方案的调整意见。</p>  
										        </span>
										        </div>
									        </div>
								        </div>
							        </a>
							        <a href="javascript:onMenuClickaa('FHD.view.sf.em.emergencyPrepPlan.EmergencyPrepPlanMainPanel','应急预案制定');">
								        <div class="frame" onMouseMove="onMouseMoveFun();">
									        <div class="wrap">
										        <img src='images/homepage/super_mono_3d_part2_76.png' border="0" style="width:70px; height:70px; padding:5px"/>
										        <div>
										        <span> 
										        <h1>应急预案制定</h1>   
										        <p>通过明确突发风险事件的应急组织体系和运行机制等事项来制定应急预案，并提出相关的资源投入与保障。</p>  	
										        </span>
										        </div>
									        </div>
								        </div>
									</a>
									<a href="javascript:onMenuClickaa('FHD.view.sf.em.importantRiskEvent.ImportantRiskEventMainPanel','潜在风险上报');">
								        <div class="frame" onMouseMove="onMouseMoveFun();">
									        <div class="wrap">
										        <img src='images/homepage/super_mono_3d_part2_36.png' border="0" style="width:70px; height:70px; padding:5px"/>
										        <div>
										        <span> 
										        <h1>潜在风险上报</h1>
										        <p>主要上报潜在重大风险事件发生的原因、条件和将产生的影响，并提出对某一潜在重大风险事件的应对建议。</p>  		
										        </span>
										        </div>
									        </div>
								        </div>
							        </a>
							        <a href="javascript:onMenuClickaa('FHD.view.sf.em.riskMonitorReport.RiskMonitorReportMainPanel','监控报告');">
								        <div class="frame" onMouseMove="onMouseMoveFun();">
									        <div class="wrap">
										        <img src='images/homepage/super_mono_3d_part2_66.png' border="0" style="width:70px; height:70px; padding:5px"/>
										        <div>
										        <span>
										        <h1>监控报告</h1>
										        <p>主要报告本单位负责管理或监督的风险变化情况，并对具体监控指标、监控区间等事项提出相关的调整建议。</p>
										        </span>
										        </div>
									        </div>
								        </div>
							        </a>
			      				</div>
			        			</td>
						    </tr>
					  	</table>
						</fieldset>
					</td>
			 	</tr>
    			<tr>
    				<td valign="top">
						<fieldset class="fieldsett1">
					  	<table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #89c7ee;">
			    			<tr height="44px">
						      	<td valign="middle">
						      	<!-- <span valign="bottom">
									<marquee width='100%' direction="left" align="center"
									scrollAmount='2' height='22' style='font-size:18;' onmouseover="this.stop()" onmouseout="this.start()">
									 	<a href="#" onclick="window.open('com.fhd.filem.indexNewsMaintainExamine.flow?newsid=441' , 'newwindow', 'height=440, width=600, top=140, left=300, toolbar=no,scrollbars=yes,resizable=no,menubar=no,');"> 
									 		<font style="font-size:14pt;" color="blue">咨询服务合同范本上传通知
									 	</a>
						 				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									 	<a href="#" onclick="window.open('com.fhd.filem.indexNewsMaintainExamine.flow?newsid=425' , 'newwindow', 'height=440, width=600, top=140, left=300, toolbar=no,scrollbars=yes,resizable=no,menubar=no,');"> 
									 		<font style="font-size:14pt;" color="blue">国资委发布关于印发《2010年度中央企业全面风险管理报告（模本）》的通知
									 	</a>
						 				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									</marquee>
			 					</span> -->
			 					<object id="oid" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="100" title="">
								    <param name="movie" value="pages/sf/SFshow01.swf">
								    <param name="quality" value="high">
								    <param name="wmode" value="opaque">
								    <param name="swfversion" value="6.0.65.0">
								    <!-- 此 param 标签提示使用 Flash Player 6.0 r65 和更高版本的用户下载最新版本的 Flash Player。如果您不想让用户看到该提示，请将其删除。 -->
								    <param name="expressinstall" value="Scripts/expressInstall.swf">
								    	<!-- 下一个对象标签用于非 IE 浏览器。所以使用 IECC 将其从 IE 隐藏。 -->
									    <!--[if !IE]>-->
									    <object type="application/x-shockwave-flash" data="pages/sf/SFshow01.swf" width="100%" height="100">
								      	<!--<![endif]-->
								      	<param name="quality" value="high">
								     	<param name="wmode" value="opaque">
								      	<param name="swfversion" value="6.0.65.0">
								      	<param name="expressinstall" value="Scripts/expressInstall.swf">
								      	<!-- 浏览器将以下替代内容显示给使用 Flash Player 6.0 和更低版本的用户。 -->
								      	<div>
									        <h4>此页面上的内容需要较新版本的 Adobe Flash Player。</h4>
									        <p><a href=""><img src="" alt="获取 Adobe Flash Player" width="112" height="33" /></a></p>
							          	</div>
								      	<!--[if !IE]>-->
								        </object>
									    <!--<![endif]-->
							      </object>
			        			</td>
						    </tr>
					  	</table>
						</fieldset>
					</td>
			 	</tr>
			</table>	
		</td>
  	</tr>
	<tr>
    	<td valign="top">
    		<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"/>
    			<tr>
    				<td width="60%" valign="top">
			    		<table width="790px" border="0" cellpadding="0" cellspacing="0" align="center"/>
			    			<tr>
			    				<td width="33%" valign="top">
									<fieldset class="fieldsett1">
					  				<table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #89c7ee;">
								    	<tr>
									      	<td valign="top">
												<img src="pages/sf/fhdLink.jpg" width="284px" height="198px" usemap="#Map"/>
									      	</td>
									    </tr>
									</table>
									<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query">
								    	<tr>
									      	<td valign="top" colspan="7" class="alt2">
												<input type="text" id="textImport" style="width:200px;" />
				              					<input id="idQuery" type="button" value="查询" onclick="textFullQuery();" class="button">
									      	</td>
									    </tr>
									</table>
									</fieldset>
								</td>
			    				<td width="67%" valign="top">
									<fieldset class="fieldsett1">
									<table width="100%" border="0" cellpadding="0" cellspacing="0">
								    	<tr>
									      	<td>
									      		<div id="riskFilePanelDiv">										  	
									      		</div>
									      	</td>
									    </tr>
									</table>
									</fieldset>
								</td>
						  	</tr>
						</table>	
					</td>
    				<td width="40%" valign="top">
						<fieldset class="fieldsett1"> 
						<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query">
					    	<tr>
						      	<td class="altt01">视频监控</td>
						      	<td class="altt02" align="right"></td>
						    </tr>
						</table>
						<table width="100%" border="0" cellpadding="0" cellspacing="0" class="fhd_query14">
					    	<tr>
						      	<td class="altt3" valign="bottom" align="center" style="background: url('pages/sf/riskAir.jpg')">
							      	<button  onclick="getVedioList('1');"style="filter:Alpha(style=3,opacity=100,finishOpacity=20);background:#dff4ff;width: 80px;height: 30px">
							      	<font style="font-weight: bold;">Ⅰ级危险点</font>
							      	</button>
							      	<button onclick="getVedioList('2');"style="filter:Alpha(style=3,opacity=100,finishOpacity=20);background:#dff4ff;width: 80px;height: 30px">
							      	<font style="font-weight: bold;">Ⅱ级危险点</font>
							      	</button>
							      	<button onclick="getVedioList('3');"style="filter:Alpha(style=3,opacity=100,finishOpacity=20);background:#dff4ff;width: 80px;height: 30px">
							      	<font style="font-weight: bold;">生产现场</font>
							      	</button>
							      	<!-- <button onclick="getVedio('702.mp4');"style="filter:Alpha(style=3,opacity=100,finishOpacity=20);background:#dff4ff;width: 80px;height: 30px">
							      	<font style="font-weight: bold;">风险监控</font>
							      	</button> -->
						      	</td>
						    </tr>
					  	</table>
						</fieldset>
					</td>
			  	</tr>
			</table>	
		</td>
  	</tr>
</table>
<script type="text/javascript">
function showAssessResult(riskId){
	if(riskId){
		var centerPanel = parent.Ext.getCmp('center-panel');
 		var tab = centerPanel.getComponent('FHD.view.sf.index.AssessAnalyseMainPanel');
 		if(tab){
 			centerPanel.remove(tab);
 		}
		onMenuClickaa('FHD.view.sf.index.AssessAnalyseMainPanel','风险分析',riskId);
	}
}
function refreshMyTaskGrid(){
	Ext.getCmp('ext-comp-1021').store.load();
}
function renderCss(){
}

function onMenubb(){
	 window.open("pages/sf/flvFile.jsp", "aa", "resizable=yes,scrollbars=yes,toolbar=no,status=no,height=300,width=400")
}
</script>
<map name="Map">
	<!--<area shape="rect" coords="28,42,104,68" href="http://sfmh.sac.com/eln3_asp/index.do" target="_blank">-->	
	<!--<area shape="rect" coords="28,42,104,68" href="javascript:onMenuClickaa('FHD.view.sys.documentlib.DocumentLibMainPanel','知识库','document_library_knowledge');javascript:onMenubb();">-->
	<area shape="rect" coords="28,42,104,68" href="javascript:onMenuClickaa('FHD.view.sf.index.SFStudy.SFStudyMain','知识库');">
	<area shape="rect" coords="175,43,252,73" href="javascript:onMenuClickaa('FHD.view.interaction.interactionMain','风险问答');">
	<area shape="rect" coords="35,132,115,158" href="javascript:onMenuClickaa('FHD.view.sf.common.tasknotice.TaskNoticeMain','任务通知');">
	<area shape="rect" coords="169,133,248,161" href="javascript:onMenuClickaa('FHD.view.sys.documentlib.DocumentLibMainPanel','操作指南','document_library_case');">
</map>
	</body>
</html>
