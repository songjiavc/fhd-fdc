<%@ page language="java" pageEncoding="utf-8"%> 
<%@ include file="/WEB-INF/jsp/commons/include-tagsOnly.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<base href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/">
		<title>中航工业沈飞公司</title>
		
		<link rel="icon" href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="${req.scheme}://${req.serverName}:${req.serverPort}${ctx}/favicon.ico" type="image/x-icon" />  
		
		
		<!-- 主界面CSS -->
<%-- 		<link rel="stylesheet" type="text/css" href="<c:url value='/scripts/ext-4.2/resources/css/ext-all-neptune.css'/>"/> --%>
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/FHDstyle.css'/>"/>
		<!-- Shared -->
		<!-- ext4默认样式CSS-->
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/icon.css'/>" />
		<link rel="stylesheet" type="text/css" href="<c:url value='/font-awesome/css/font-awesome.min.css'/>" />
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/w.css'/>" />
		
		<!-- 增加fontawe 字体按钮 start -->
		
		<!-- end -->
		
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/xbreadcrumbs.css'/>" />
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/monitor.css'/>" />
		
		<!--  <link rel="stylesheet"  href="<c:url value='/css/fishBone.css'/>" />  -->
		<link rel="stylesheet" type="text/css" href="<c:url value='/scripts/ext-4.2/ux/form/BoxSelect.css'/>" />
		
		 
		<link rel="shortcut icon" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
		<link rel="icon" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
		
		<script type="text/javascript" src="<c:url value='/scripts/jquery-1.10.2.min.js'/>"></script>
		<!-- <script type="text/javascript" src="<c:url value='/scripts/fishBone.js'/>"></script> -->
		<!-- <script type="text/javascript" src="<c:url value='/scripts/jquery.SuperSlide.2.1.1.js'/>"></script> -->
		<script type="text/javascript" src="<c:url value='/scripts/Highstock-1.3.2/js/highstock.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/Highstock-1.3.2/js/highcharts-more.js'/>"></script>
		
		
		<script type="text/javascript" src="<c:url value='/scripts/xbreadcrumbs.js'/>"></script>
		
		
		<script type="text/javascript" src="<c:url value='/scripts/fhd.js'/>"></script>
		
		<!-- ext4核心JS -->
		<script type="text/javascript" src="<c:url value='/scripts/ext-4.2/include-ext.js'/>"></script>
<%-- 		 <script type="text/javascript" src="<c:url value='/scripts/ext-4.2/options-toolbar.js'/>"></script>  --%>
		
<%-- 		<script type="text/javascript" src="<c:url value='/scripts/ext-4.2/ext-all.js'/>"></script> --%>
		<!-- ext4中文支持JS -->
		<script type="text/javascript" src="<c:url value='/scripts/ext-4.2/locale/ext-lang-zh_CN.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/scripts/ext-override.js'/>"></script>
		
		<script type="text/javascript" src="<c:url value='/scripts/kindeditor-4.1.1/kindeditor.js'/>"></script>
		
		<!-- 公用JS -->
		<script type="text/javascript" src="<c:url value='/app/view/kpi/cmp/kpi/result/ResultParam.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/app/view/kpi/kpi.js'/>"></script>
		<script type="text/javascript" src="<c:url value='/App-src.js'/>" defer="defer"></script>
		<script type="text/javascript" src="<c:url value='/scripts/commons/dynamic.jsp'/>?"+Math.random();></script>
		<!-- 本地 国际化资源 -->
		<script type="text/javascript">
			${locale}
		</script>
		
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
					background-color:#e3e3e3 !important;
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
			
		 #fhd-header{
			width : 100%;
		 	height : 100%;
		 }
		 
	 	#fhd-middle-left{
			width :25px;
			height : 25px;
			float : left;
			background : url(images/icons/application_home.png) no-repeat  right center;
		}
		    		 
		#fhd-header-left{
			width : 100px;
			height : 90px;
			float : left;
			align : right;
			
		}
		    		 
		#fhd-header-center{
			width :40%;
			height : 100%;
			float : left;
			text-align:left;
			padding-top : 28px;
			font-size:20pt;
			color:white;
		}
		    		 
		    		 
		#fhd-header-right{
			width : 40%;
			height : 100%;
			text-align:right;
			float : left;
			align : right;
			color : gray;
			padding-top : 35px;
			padding-right : 30px;
		}

		</style>
		<script type="text/javascript">
			if(Ext.isIE){
				window.setInterval("CollectGarbage();", 10000);
			}
			String.prototype.endWith = function(s) {
				if (s == null || s == "" || this.length == 0 || s.length > this.length)
					return false;
				if (this.substring(this.length - s.length) == s)
					return true;
				else
					return false;
				return true;
			}
			String.prototype.startWith = function(s) {
				if (s == null || s == "" || this.length == 0 || s.length > this.length)
					return false;
				if (this.substr(0, s.length) == s)
					return true;
				else
					return false;
				return true;
			}
			/*
			 * string:原始字符串 substr:子字符串 isIgnoreCase:忽略大小写
			 */
			function contains(string, substr, isIgnoreCase) {
				if (isIgnoreCase) {
					string = string.toLowerCase();
					substr = substr.toLowerCase();
				}
				var startChar = substr.substring(0, 1);
				var strLen = substr.length;
				for (var j = 0; j < string.length - strLen + 1; j++) {
					if (string.charAt(j) == startChar)// 如果匹配起始字符,开始查找
					{
						if (string.substring(j, j + strLen) == substr)// 如果从j开始的字符与str匹配，那ok
						{
							return true;
						}
					}
				}
				return false;
			}
			
		   	//在线帮助
		   	function helponline(){
				var btn = new Object();
				btn.url = "/sys/helponline/helpOnlineView.do";
				btn.text = "在线帮助";
				onButtonClick(btn);
				return false;
			}
			
		   	function updatePass(){
		   		updatePass1();
		   	}
		   	
		   	function showNotification(){
		   		Ext.getCmp('notification').show();
		   	}
		   	
		   	function showFirstPage(){
		   		location.reload();
		   	}
		   	
		   	function loginOut(){
		   		window.location.href = __ctxPath + '/j_spring_security_logout';
		   	}
		   	
		   	function showWaitWork(){
		   		onMenuClick(
		   				{
		   					url1:'FHD.view.myallfolder.SF2mytodo.MyTodoMain',
		   					text:'个人工作台'
		   				});
		   	}
		   	
			//操作的次数
			var version = 1;
			//当次的数组值--为store赋值使用
			var oldDataJson = {};
			var dataJson=[];
			var store;
			var grid;
			var popWindow;
			//当前总的对象个数
			var allCount=0;
			//没变的对象个数
			var oldCount=0;
			//当次新增操作的对象个数
			var addCount=0;
			//当次删除操作的对象个数
			var delCount=0;
			
			//优化
			function optimize(){
				//debugger;
				var oldDataJsonTemp={};
				dataJson=[];
				addCount=0;
				oldCount=0;
				
				Ext.ComponentManager.each(function(k,v,l){
					if(oldDataJson[v.id]){
	    				dataJson.push(oldDataJson[v.id]);
	    				oldDataJsonTemp[v.id]=oldDataJson[v.id];
	    				oldCount++;
	    			}else{
	    				var obj = {};
		    			obj.id = v.id;
		    			obj.name = v.$className;
		    			obj.itemId = v.tabId;
		    			obj.type = v.xtype;
		    			obj.registerDate = fromatDate();
		    			obj.version = version;
		    			dataJson.push(obj);
		    			oldDataJsonTemp[v.id]=obj;
		    			addCount++;
	    			}
				});
				oldDataJson=oldDataJsonTemp;
				delCount=allCount-oldCount;
				allCount=Ext.ComponentManager.all.length;
				
				store = Ext.create('Ext.data.JsonStore',{
					idProperty: 'id',
					groupField: 'version',
					fields: [
				         {name: 'id', type: 'string'},
				         {name: 'name', type: 'string'},
				         {name: 'itemId', type: 'string'},
				         {name: 'type', type: 'string'},
				         {name: 'registerDate', type: 'string'},
				         {name: 'version', type: 'string'}
				    ],
				    data: dataJson
				});
				store.sort('type', 'ASC');
				store.sort('version', 'DESC');
	
				if(allCount != oldCount){
					version = version+1;
				}
				
				grid = Ext.create('Ext.grid.Panel', {
					tbar: [
						{ 
					   		xtype: 'button',
					   		iconCls: 'icon-export',
							text:'导出',
							tooltip: '导出当前页日志',
							handler: function(){
								exportLog();
							}
					    }
					],
					/*
					features: Ext.create('Ext.grid.feature.Grouping',{
				        groupHeaderTpl: '{columnName}: {name} ({rows.length} 项)',
				        hideGroupedHeader: true,
				        startCollapsed: true
				    }),
				    */
				    store: store,
				    columns: [
				        { text: '组件id',  dataIndex: 'id',  flex: 5,
				        	renderer:function(value,metaData,record,colIndex,store,view) { 
								metaData.tdAttr = 'data-qtip="'+value+'"';
								return value;  
							}
				        },
				        { text: '组件名称', dataIndex: 'name', flex: 4,
				        	renderer:function(value,metaData,record,colIndex,store,view) { 
								metaData.tdAttr = 'data-qtip="'+value+'"';
								return value;  
							}
				        },
				        { text: 'itemId', dataIndex: 'itemId', flex: 4,
				        	renderer:function(value,metaData,record,colIndex,store,view) { 
								metaData.tdAttr = 'data-qtip="'+value+'"';
								return value;  
							}
				        },
				        { text: '组件类型', dataIndex: 'type', flex: 2},
				        { text: '注册时间', dataIndex: 'registerDate', flex: 3},
				        { text: '版本', dataIndex: 'version', flex: 1}
				    ]
				});
				
				popWindow = Ext.create('FHD.ux.Window',{
					title:'Ext.ComponentManager对象列表--(总计：'+allCount+"个|新增："+addCount+"个|删除："+delCount+"个)",
					collapsible:false,
					maximizable:true
				});
				
				popWindow.show();
				popWindow.add(grid);
			}
			//月、天、小时、分、秒不足2位前面补0
			function add0(m){
				return m<10?'0'+m:m;
			}
			//日期转换格式
			function fromatDate(){ 
				var time = new Date();
				var y = time.getFullYear(); 
				var m = time.getMonth()+1; 
				var d = time.getDate(); 
				var h = time.getHours(); 
				var mm = time.getMinutes(); 
				var s = time.getSeconds();
				
				return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s); 
			}
			//导出grid列表
		    function exportLog(){
		    	if(grid){
		    		FHD.exportExcel(grid,'exportExtComponentManagerLog','exportExtComponentManagerLog');
		    	}else{
		    		Ext.Msg.alert(FHD.locale.get('fhd.common.prompt'),'没有grid数据源!');
		    	}
		    }
		   	//点击按钮事件处理方法
		   	function onButtonClick(btn){
				var url = '${ctx}' + btn.url;
				var centerPanel = Ext.getCmp('center-panel');
		    	var tab = centerPanel.getComponent(url+'${param._dc}');
		    	if(tab){
		    		centerPanel.setActiveTab(tab);
		    	}else{
		    		if(centerPanel.items.length >= 6)
		    			centerPanel.remove(1);
		        	    var p = centerPanel.add({
		        	    	id:url+'${param._dc}',
		        	        title: btn.text,
		                	tabTip:btn.text,
		        	        layout:'fit',
		        	        autoWidth:true,
		        	        //iconCls: 'tabs',
		        	        html:"<iframe width='100%' height='100%' scrolling='auto' stype='overflow-y:hidden;' noresize='noresize' src='" + url + "' frameborder='0'></iframe>",
		        	        closable:true
		        	    });
		        	    centerPanel.setActiveTab(p);
		    	}
		    } 
		   //点击菜单添加tab
		   function onMenuClick(menu){
			  	var url = menu.url1;
				var temp = url.split('?');//url传参，以“？”分隔
				var typeId;
				if(temp.length>1){
					url = temp[0];
					typeId = temp[1];
				}
				var centerPanel = Ext.getCmp('center-panel');
				centerPanel.removeAll(true);
				if(url.startWith('FHD')){
					centerPanel.add(Ext.create(url,{
						itemId:url,
						tabTip:menu.text,
						typeId:typeId,
						closable:false,
						destroy:function (){}
					}));
				} else if (url.startWith('/pages')){
					var p = centerPanel.add({
						itemId:url,
						title: menu.text,
						tabTip:menu.text,
						layout:'fit',
						autoWidth:true,
						border:false,
						//iconCls: 'tabs',
						closable:true,
						autoLoad :{ url: __ctxPath+url,scripts: true}
					});
				}else{
					var p = centerPanel.add({
						itemId:url,
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
					}
				}
		   //添加到收藏
		   var addRemark = function (obj, url, title) {
			    var e = window.event || arguments.callee.caller.arguments[0];
			    var B = {
			        IE : /MSIE/.test(window.navigator.userAgent) && !window.opera
			        , FF : /Firefox/.test(window.navigator.userAgent)
			        , OP : !!window.opera
			    };
			    obj.onmousedown = null;
			    if (B.IE) {
			        obj.attachEvent("onmouseup", function () {
			            try {
			                window.external.AddFavorite(url, title);
			                window.event.returnValue = false;
			            } catch (exp) {}
			        });
			    } else {
			        if (B.FF || obj.nodeName.toLowerCase() == "a") {
			            obj.setAttribute("rel", "sidebar"), obj.title = title, obj.href = url;
			        } else if (B.OP) {
			            var a = document.createElement("a");
			            a.rel = "sidebar", a.title = title, a.href = url;
			            obj.parentNode.insertBefore(a, obj);
			            a.appendChild(obj);
			            a = null;
			        }
			    }
			};
			//遍历centerPanel，查找相同url相同typeId的panel，如果不存在，返回新创建的panel 
			function isSamePanel(centerPanel,menu,typeId,url){
				var isSamePanel;//存在参数相同的panel
				for(var k in centerPanel.items.items){ 
					if(typeId == centerPanel.items.items[k].typeId){
						isSamePanel = centerPanel.items.items[k];
					}
				}
				if(!isSamePanel){
					var p = centerPanel.add(Ext.create(url,{
						itemId:url+typeId,
						title: menu.text,
						tabTip:menu.text,
						typeId:typeId,
						closable:true,
						destroy:function (){//销毁tabpanel
                                if(this.fireEvent("destroy",this)!=false){
                                    this.el.remove();
                                    p = null;
                                    if(Ext.isIE){
                                        CollectGarbage();
                                    }
                             }
						}
					}));
					centerPanel.setActiveTab(p);
				}else{
					return isSamePanel;
				}
			}
			
			//显示浮动框
		
			function showTip(context){
				//var objDiv = $("div3"); 
				$("#div3").css("display","block"); 
				$("#div3").css("left", event.clientX); 
				$("#div3").css("top", event.clientY + 10);
				$("#div3").html(context);
				
			}
			
			//关闭浮动框
			
			function closeTip(){    
				var div3 = document.getElementById('div3');    
				div3.style.display="none";    
			}    
			
	    </script>
	</head>
	<body oncontextmenu="return false">
	<script type="text/javascript" src="<c:url value='/scripts/commons/Vtypes.js'/>"></script>
		<div id="loading">
             <div class="loading-indicator">
             	 <img
					src="${ctx}/images/extanim32.gif"
					width="32" height="32"
					style="margin-right: 8px; float: left; vertical-align: top;" />
				<span id="loading-msg">验证用户身份...</span>
             </div>
        </div> 
        <div id="loading-mask"></div>
		
		<div id="fhd-header" >
			<div id="fhd-header-left"></div>
			
			<div id="fhd-header-center">
				全面风险管理信息系统
			</div>
			<div id="fhd-header-right" > 
				<i style="color:red;font-size:14pt;" id="waitWorkNum"></i>
				<i style="padding-right:15px;cursor:pointer;color:white;font-size:14pt;" class="fa fa-envelope-o" title="待办" onclick="showWaitWork()"></i>
				<i style="padding-right:15px;padding-left:15px;cursor:pointer;color:white;font-size:14pt;" class="fa fa-home" title="首页" onclick="showFirstPage()"></i>
				<i style="padding-right:15px;padding-left:15px;cursor:pointer;color:white;font-size:14pt;" class="fa fa-bell" title="通知" onclick="showNotification()"></i>
				<i style="padding-right:15px;padding-left:15px;cursor:pointer;color:white;font-size:14pt;" class="fa fa-pencil" title="修改密码" onclick="updatePass()"></i>
				<i style="padding-right:15px;padding-left:15px;cursor:pointer;color:white;font-size:14pt;" class="fa fa-power-off" title="系统退出" onclick="loginOut()"></i>
				<label style="font-size:14pt;color:white;" id="loginName"></label>
			</div>
		</div>
	</body>
	<script type="text/javascript">
	
		setTimeout(function(){
			document.getElementById('loading-msg').innerHTML = '正在装载页面...';
		}, 100);
	
		
		 //通过递归获取节点的层次关系  
	    var domCount = 0;  
	    function listNodes(node){
	        var nodes = node.childNodes;   
	        for(var x=0;x<nodes.length;x++){  
	            if(nodes[x].hasChildNodes()) 
	                listNodes(nodes[x]);
	            else   
	            	domCount++;
	        }   
	    }
	      
	   
	    function getNodes(){  //递归调用 
	        listNodes(document);  
	        document.getElementById('showCount').innerHTML = "dom节点总数为:"+domCount;
	        domCount = 0;
	    }
	</script>
		<!-- 图表JS -->
		<!--[if IE 6]>
		        <script src="<c:url value='/scripts/chart/DD_belatedPNG_0.0.8a-min.js'/>"></script>      
		<![endif]-->
		
<%-- 	<script type="text/javascript" src="<c:url value='/scripts/chart/highcharts.js'/>" ></script> --%>
	<script type="text/javascript" src="<c:url value='/scripts/chart/FusionCharts.js'/>" ></script>
	<script type="text/javascript" src="${ctx}/scripts/component/meshStructureChart/meshStructureChart.js"></script>
	<link rel="stylesheet" type="text/css" href="<c:url value='/scripts/component/meshStructureChart/meshStructureChart.css'/>" />
	
	<script type="text/javascript">
  		mxBasePath = '<c:url value='/scripts/mxgraph-1.10/'/>';
 	</script>
	<script type="text/javascript" src="<c:url value='/scripts/mxgraph-1.10/js/mxgraph.js'/>"></script>
	<link rel="stylesheet" type="text/css" href="<c:url value='/scripts/component/treeChar/treeChar.css'/>" />
	
	<script type="text/javascript" src="<c:url value='/scripts/FHDDebugTool.js'/>" ></script>
	<script type="text/javascript" src="<c:url value='/scripts/FHDException.js'/>" ></script>
	<script type="text/javascript" src="<c:url value='/scripts/swfupload/swfupload.js'/>" ></script>
	
</html>
