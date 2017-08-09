/**
 * 沈飞首页-视频面板 
 * 
 * @author 郝静
 */
Ext.define('FHD.view.sf.index.SFVedioPanel2', {
    extend: 'Ext.panel.Panel',
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	Ext.apply(me, {
    	    flex:1,
        	border:true,
    		style:'padding:1px 0px 0px 1px',
        	layout: {
                type: 'fit'  
            },
            /*<img style="" src="'+__ctxPath+'/app/view/sf/images/vedio3.jpg"></img>*/
//            	html:'<div style="width:100%;height:100%"><a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreVedios()\' ><img style="width:100%;height:100%" src="'+__ctxPath+'/app/view/sf/images/vedio.jpg"></img></a></div>', <img style="width:100%;height:100%" src="'+__ctxPath+'/app/view/sf/images/702.jpg"></img>
         	html:'<div style="width:100%;height:100%"><img src="pages/sf/riskAir.jpg" ></img></div>',
         	buttons:[{
         			  text:'危险点',
         			  handler:function(){
	    					me.showVedio('702.mp4');
	    				}
	    			 },{
	    			 	text:'生产现场',	    				
	    			 	handler:function(){
	    				}
	    			 },{
	    			 	text:'指标监控',
	    			 	handler:function(){
	    				}
	    			 },{
	    			 	text:'风险监控',	    				
	    			 	handler:function(){
	    				}
	    			}],
           	tbar:['视频监控'/*,'->','<a href="javascript:void(0)" onclick=\'Ext.getCmp("'+me.id+'").showMoreVedios()\' >更多...</a>'*/]
		});
        me.callParent(arguments);
    },
    showVedio:function(name){
		var me = this;
		me.formwindow = new Ext.Window({
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			title:'视频',
			width:550,
			height:490,
			layout: {
				type: 'fit'
	        },
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			items : {
           			xtype:'flash',
           			url:__ctxPath + '/scripts/ckplayer6.3/ckplayer/ckplayer.swf',
           			flashVars:{
//							f:'http://movie.ks.js.cn/flv/other/1_0.flv',
							f:__ctxPath + '/app/view/sf/' + name,
							c:0,
							b:1
						},
					flashParams:{bgcolor:'#FFF',allowFullScreen:true,allowScriptAccess:'always'}},
			buttons: [{
	    				text: '关闭',
	    				handler:function(){
	    					me.formwindow.close();
	    				}
	    	}]
		});
		me.formwindow.show();
	},
	showMoreVedios:function(){
		var me = this;
		var url = 'FHD.view.sf.kpiMonitor.SFKpiMonitorVedioMain';
	    var idurl = url;
	    var centerPanel = Ext.getCmp('center-panel');
	    var tab = centerPanel.getComponent(idurl);
	    if(tab){
			centerPanel.setActiveTab(tab);
		}else{
			if(url.startWith('FHD')){
			    var myTask = Ext.create(url, {
			        closable: true,
			        id:idurl,
			        title:'视频监控'
			    });
			    var p = centerPanel.add(myTask);
			    centerPanel.setActiveTab(p);
			}
		}
	},
	showKpi:function(){
		var me = this;
		var url = 'FHD.view.sf.kpiMonitor.SFKpiMonitorVedioMain';
	    var idurl = url;
	    var centerPanel = Ext.getCmp('center-panel');
	    var tab = centerPanel.getComponent(idurl);
	    if(tab){
			centerPanel.setActiveTab(tab);
		}else{
			if(url.startWith('FHD')){
			    var myTask = Ext.create(url, {
			        closable: true,
			        id:idurl,
			        title:'视频监控'
			    });
			    var p = centerPanel.add(myTask);
			    myTask.setActiveTab(4);
			    centerPanel.setActiveTab(p);
			}
		}
	}
    
});