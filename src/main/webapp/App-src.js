﻿/**
 * 登陆后加载页面
 */
/**
 * Ext参数设置
 */
Ext.Loader.setConfig({
	enabled : true
});
Ext.Loader.setPath({
	'Ext.ux' : 'scripts/ext-4.2/ux',
	'Ext.app' : 'scripts/ext-4.2/app',
	'FHD.ux' : 'scripts/component',
	'FHD.view' : 'app/view',
	'FHD.demo' : 'pages/demo'
});


	/**
	 * 定义全局变量函数来存放修改密码函数句柄
	 */
	var updatePass1 = function(){
		var passwordFormPanel = Ext.create('FHD.view.sys.user.UserPasswordForm');
		var popWindow = Ext.create('FHD.ux.Window',{
			width:400,
			height:200,
			title:'修改密码',
			collapsible:false,
			maximizable:true,
			items:[passwordFormPanel],
			buttonAlign: 'center',
			buttons: [
				{ 
					text: '保存',
					name:'sample_pre_btn',
					iconCls: 'icon-save',
					handler:function(){
						passwordFormPanel.modPassword(popWindow);
					}
				},
				{ 
					text: '关闭',
					name:'sample_next_btn',
					iconCls: 'icon-ibm-close',
					handler:function(){
						popWindow.close();
			    	}
			    }
			]
    	});
		popWindow.show();
	}
    
Ext.onReady(function() {

	
	if(Ext.isIE6){
		Ext.BLANK_IMAGE_URL = __ctxPath +"/images/s.gif";
	}
	Ext.tip.QuickTipManager.init();
	Ext.apply(Ext.tip.QuickTipManager.getQuickTip(), {
	    showDelay: 500      // Show 50ms after entering target
	});  
	
	setTimeout(function() {
		Ext.get('loading').remove();
		Ext.get('loading-mask').fadeOut({
			remove : true
		});
	}, 100);

	// 等待金鹏翔封装
	
		/*最新的值组件开始*/
		var timeId={
        	id:'timeId',
        	text : '最新的值',
        	iconCls : 'icon-dateControl',
        	handler:function(){
        		var me = this;
				this.selectorWindow= Ext.create('FHD.ux.timestamp.TimestampWindow',{
					onSubmit:function(values){
						var valuesStr = values.split(',');
						var data = {};
						data.yearId = valuesStr[0];
						data.quarterId = valuesStr[1];
						data.monthId = valuesStr[2];
						data.weekId = valuesStr[3];
						if(data.yearId == null || data.yearId == ''){
							FHD.data.yearId = '';
						}if(data.quarterId == null || data.quarterId == ''){
							FHD.data.quarterId = '';
						}if(data.monthId == null || data.monthId == ''){
							FHD.data.monthId = '';
						}if(data.weekId == null || data.weekId == ''){
							FHD.data.weekId = '';
						}
						FHD.data.yearId = data.yearId;
						var gridPanelArray = Ext.ComponentQuery.query('gridpanel');
						for(var i = 0; i < toolbar.items.items.length; i++){
							if(toolbar.items.items[i].id == 'timeId'){
								if(FHD.data.newValue != ''){
									toolbar.items.items[i].setText(FHD.data.newValue);
								}
								break;
							}
						}
						// 王鑫添加  增加报表类型TreePanel数据动态加载
						var treePanelArray = Ext.ComponentQuery.query("treepanel[name^='report']");
						if(treePanelArray != null) {
							for(var i = 0; i < treePanelArray.length; i++) {
								if(treePanelArray[i].store.proxy.extraParams){
									treePanelArray[i].store.proxy.extraParams.year=FHD.data.yearId;
									treePanelArray[i].store.proxy.extraParams.month=FHD.data.monthId;
									treePanelArray[i].store.proxy.extraParams.quarter=FHD.data.quarterId;
									treePanelArray[i].store.proxy.extraParams.week=FHD.data.weekId;
									treePanelArray[i].store.proxy.extraParams.isNewValue=FHD.data.isNewValue;
									treePanelArray[i].store.proxy.extraParams.eType = FHD.data.eType;
								}
								treePanelArray[i].store.load();
							}
						}
						if(gridPanelArray != null){
							for(var i = 0; i < gridPanelArray.length; i++){
								if(gridPanelArray[i].store.proxy.extraParams){
									gridPanelArray[i].store.proxy.extraParams.year=FHD.data.yearId;
									gridPanelArray[i].store.proxy.extraParams.month=FHD.data.monthId;
									gridPanelArray[i].store.proxy.extraParams.quarter=FHD.data.quarterId;
									gridPanelArray[i].store.proxy.extraParams.week=FHD.data.weekId;
									gridPanelArray[i].store.proxy.extraParams.isNewValue=FHD.data.isNewValue;
									gridPanelArray[i].store.proxy.extraParams.eType = FHD.data.eType;
								}
								gridPanelArray[i].store.load();
							}
						}
						
						var resultContainer =  Ext.ComponentQuery.query("container[timeRefresh=true]") ;
						if(resultContainer&&resultContainer.length>0){
							var param = {
									paraobj:{},
									kpiname:FHD.data.kpiName,
									timeId:''
							};
							for(var k=0;k<resultContainer.length;k++){
								resultContainer[k].load(param);
							}
						}
						
					}
				}).show();
			}
        };
		/*最新的值组件结束*/

	// 窗口改变时重新更改菜单的显示位置
	var northPanel = Ext.create('Ext.panel.Panel', {
		id : 'north-panel',
		region : 'north',
		height : 90,
		margin : '0 0 5 0',
		contentEl : 'fhd-header',
		border:false,
		bodyStyle : {
			background : '#4e72b8'
		}
	});
	
	var westTreePanel = Ext.create('FHD.view.firstpage.FirstAccordionPanel',{
		id : 'west-panel',
		width : 200,
		region : 'west',
		border:false
	});
	
	
	/**
	 * 默认显示面板
	 * @author 杨鹏
	 */
	/**
	 * 现系统默认首页，如未设置首页则使用该页面。
	 * @author 杨鹏
	 */
	var home_url='';
	/**
	 * 读取首页信息
	 * @author 杨鹏
	 */
	jQuery.ajax({
		type: "POST",
		url: __ctxPath +'/sys/auth/user/getUserHomeUrl.f',
		async:false,
		success: function(data){
			if(""!=data){
				home_url=data;
			}
		},
		error: function(){
			FHD.notification("<font color=red>首页读取失败，请确认该用户数据！</font>",FHD.locale.get('fhd.common.error'));
		}
	});
	/**
	 * 首页EXT对象
	 * @author 杨鹏
	 */
	var home=null;
	/**
	 * 首页创建失败也可继续运行
	 * @author 杨鹏
	 */
	try{
		if(home_url){
			var temp = home_url.split('?');//url传参，以“？”分隔
			var typeId;
			if(temp.length>1){
				home_url = temp[0];
				typeId = temp[1];
			}
			home=Ext.create(home_url,{
				title:'首页',
				typeId:typeId
			});
			centerPanelItems.push(home);
		}
	}catch(e){
		FHD.notification( "<font color=red>首页构建失败，请确认该首页是否可用！</font>",FHD.locale.get('fhd.common.error'));
	}
	/*
	var firstPage = Ext.create('Ext.panel.Panel',{
		layout : {
			type : 'vbox',
			align : 'stretch'
		},
		items : [
			{
				xtype : 'panel',
				layout : 'fit',
				flex : .45,
				html : '<iframe width=\'100%\' scrolling=\'no\' height=\'100%\' frameborder=\'0\' src=\'' + __ctxPath + '/pages/echarts/fishbone.html' +'\'></iframe>'
			},
			{
				xtype : 'panel',
				flex : .55,
				html : '<iframe width=\'100%\' scrolling=\'no\' height=\'100%\' frameborder=\'0\' src=\'' + __ctxPath + '/pages/echarts/echartcolumnpie.html' +'\'></iframe>'
			}
		]
		
	});
	*/
	var firstPage = Ext.create('FHD.view.myallfolder.SF2mytodo.MyTodoMain');
	var centerPanel = Ext.create('Ext.container.Container', {
		id : 'center-panel',
		animScroll : true,
		border : false,
		bodyBorder : false,
		flex : 10,
		margin : '0 0 0 5',
		region : 'center',
		layout : 'fit',
		firstPage : firstPage
		
		//margin:'6 0 0 0',
	});
	
	/**
	 * 宋佳  添加
	 * 
	 * 首页增加图标显示
	 */
	
	
	
	var leftTopPanel = Ext.create('Ext.container.Container',{
		height : 100,
		style : {
			background : 'white'
		},
		html : "<div style='width : 100%;height : 100%;background : url(images/leftlogo.png) no-repeat  right center;'></div>"
	});
	
	var middlePanel = Ext.create('Ext.form.Label', {
		id : 'middlePanel',
		animScroll : true,
		border : false,
		bodyBorder : false,
		height : 26,
		html : '<div id="fhd-middle-left"></div><div style="text-align:left;line-height:25px;vertical-align:middle;margin-left:30px;font-size:10pt;color:#303030;font-family:tahoma, arial, verdana, sans-serif;">首页</div>',
		margin : '0 0 5 5',
		style : {
			background : '#e5e5e5'
		},
		setTitle : function(text){
			var me = this;
			me.value = text;
			var here = '<div id="fhd-middle-left"></div><div style="text-align:left;line-height:25px;vertical-align:middle;margin-left:30px;font-size:10pt;color:#303030;font-family:"Times New Roman",Georgia,Serif;">';
			me.setText(here+text+'</div>',false);
		},
		getTitle : function(){
			var me = this;
			return me.value;
		}
		
		//margin:'6 0 0 0',
	});
	
	var contentPanel = Ext.create('Ext.container.Container', {
		animScroll : true,
		border : false,
		bodyBorder : false,
		region : 'center',
		layout : {
			type : 'vbox',
			align : 'stretch'
		},
		style : {
			background : 'white'
		},
		items : [middlePanel,centerPanel]
		
		//margin:'6 0 0 0',
	});

	
	
	
	Ext.create('Ext.container.Viewport',{
		layout: {
	        type: 'border'	        
	        //padding: '3 3 3 3'
	    },
	    style : {
	    	background : 'white'
	    },
	    border:true,
		items : [northPanel,contentPanel,westTreePanel],
		listeners : {
			afterrender : function(){
				centerPanel.removeAll(true);
				centerPanel.add(centerPanel.firstPage);
			}
		}
	});
	
	
	FHDDebugTool.init();
	
	/**
	 * 开启全局异常处理
	 */
	FHDException.init();
	
	/**
	 * 首页初始化完成后，记录首页内所有组件的tabId,指定为framework
	 */
	var tabId = 'framework';
	Ext.ComponentManager.each(function(k,v,l){
		if(!FHD.cmpData[v.id]){
			if(v.tabId == null){
				v.tabId = tabId;
				FHD.cmpData[v.id] = v;
			}
		}
	});
	
	
	/**
	 * 加载通知窗口
	 * 胡迪新
	 */
	var notification;
	Ext.Ajax.request({
	    url: __ctxPath + '/notificationcontent.f',
	    success: function(response){
	        var content = Ext.JSON.decode(response.responseText).taskNum;// 返回json对象，目前只有代表人物数量
	        notification = Ext.create('Ext.ux.window.Notification', {
	    		title: '通知',
	    		closeAction: 'hide',
	    		position: 'br',
	    		id : 'notification',
	    		cls: 'ux-notification-light',
	    		iconCls: 'ux-notification-icon-information',
	    		useXAxis: false,
	    		stickOnClick: false,
	    		width: 200,
	    	    content : Ext.JSON.decode(response.responseText).taskNum,
	    		autoCloseDelay: 8000,
	    		html: '欢迎登录风险管理平台</br><a href="javascript:void(0);" onclick="onMenuClick({url1:\'FHD.view.myallfolder.SF2mytodo.MyTodoMain\',text:\'个人工作台\'})">当前您有<font color="red">'+content +'</font>个待办任务!</a>'
	    	});
	    	$('#waitWorkNum').html(notification.content);
	    	$('#loginName').text(__user.realName);
	    	
	    	notification.show();
	    	
	    }
	});
	
	
	
});



