/**
 * 
 * 工作计划
 * 
 * @author 胡迪新
 */
Ext.define('FHD.view.comm.report.icsystem.ConstructTestReportForm', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.constructtestreportform',
	
    requires: [
    	'FHD.view.comm.report.icsystem.ConstrcutTestReportBaseForm',
    	'FHD.view.comm.report.icsystem.ConstructPlanTestReportList',
    	'FHD.view.comm.report.assess.TestReportList'
    ],
    
    frame: false,
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    border : false,
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        me.formPanel = Ext.widget('constrcuttestreportbaseform',{
        	flex:1,
        	bbar:['->',{   
	            	   text: FHD.locale.get('fhd.strategymap.strategymapmgr.form.undo'),
	                   iconCls: 'icon-operator-home',
	                   handler: me.cancel
	               },{
        		iconCls : 'icon-control-stop-blue',
        		text : FHD.locale.get("fhd.common.save"),
        		handler: me.onSave
        	}
//        	,'-',{
//        		iconCls : 'icon-operator-submit',
//        		text : FHD.locale.get("fhd.common.submit"),
//        		handler : me.onSubmit
//        	}
        	]
        });
        
        me.form = me.formPanel.getForm();
        
        Ext.applyIf(me, {
            items: [
//            	Ext.widget('flowtaskbar',{
//    				jsonArray:[
//			    		{index: 1, context:'1.测试报告制定',status:'current'}
//	    			]
//    			}),
    			me.formPanel]
        });

        me.callParent(arguments);
    },
    onSave : function(){
    	var me = this.up('constructtestreportform');
    	me.form.waitMsgTarget = true;
    	if(me.form.isValid()) {
    		this.setDisabled(true);
    		me.reportSubmit({
    			waitMsg : "正在保存数据...",
				form : me.form,
				url : __ctxPath + '/comm/report/saveconstructplantestreport.f',
				/*
				params : {
					reportDataText: me.formPanel.editor.html()
				},
				*/
				callback: function (data) {
					var centerpanel = me.up('panel');
			    	centerpanel.removeAll();
					centerpanel.add(Ext.widget('constructplantestreportlist'));
				}
			});
		}else{
			this.setDisabled(false);
		}
    },
    cancel : function(){
    	var me = this.up('constructtestreportform');
   	    var centerpanel = me.up('panel');
    	centerpanel.removeAll();
		centerpanel.add(Ext.widget('constructplantestreportlist'));
    },
    onSubmit : function() {
    	var me = this.up('constructtestreportform');
    	if(me.form.isValid()) {
    		me.reportSubmit({
				form : me.form,
				url : __ctxPath + '/comm/report/savecompanyyearreportsubmit.f',
				params : {
					reportData: me.formPanel.editor.html()
				},
				callback: function (data) {
					var centerpanel = me.up('panel');
			    	centerpanel.removeAll();
					centerpanel.add(Ext.widget('testreportlist'));
				}
			});
		}  	
    },
    reloadData: function() {
    	var me = this;
    	
	    me.form.waitMsgTarget = true;
    	me.form.load({
            waitMsg: '加载中...',
            url: __ctxPath + '/comm/report/findconstructionplantestreportbyid.f',
            params: {
            	reportId: me.reportId
            },
            // form加载数据成功后回调函数
            success: function (form, action) {
            	var responseJson = Ext.JSON.decode(action.response.responseText);
            	//加载报告内容
            	me.formPanel.editor.html(responseJson.data.reportData);
            },
 	        failure: function (form, action) {
 	     	   return false;
 	        }
        });
    },
    reportSubmit:function(cfg){
		cfg.form.submit({
			url:cfg.url,
			method : cfg.mehtod!=null?cfg.mehtod:'post', // 方法
			params : cfg.params,
			waitMsg : FHD.locale.get('fhd.common.saving'),
			success: function(form, action) {
				var data;
				if(action.response && action.response.responseText){
					data = Ext.JSON.decode(action.response.responseText);
				}
				cfg.callback(data);
		       	FHD.notification(FHD.locale.get('fhd.common.operateSuccess'),FHD.locale.get('fhd.common.prompt'));
		    },  
		    failure: function(form, action) {  
		    	var data;
				if(action.response && action.response.responseText){
					data = Ext.JSON.decode(action.response.responseText);
				}
				cfg.callback(data);
		        switch (action.failureType) {  
		            case Ext.form.Action.CLIENT_INVALID:  
						FHD.notification('<font color=red>'+FHD.locale.get('fhd.common.clientInvalid')+'</font>',FHD.locale.get('fhd.common.prompt'));
		                break;  
		            case Ext.form.Action.CONNECT_FAILURE:  
						FHD.notification('<font color=red>'+FHD.locale.get('fhd.common.connectFailure')+'</font>',FHD.locale.get('fhd.common.prompt'));
		                break;  
		            case Ext.form.Action.SERVER_INVALID:  
						FHD.notification('<font color=red>'+FHD.locale.get('fhd.common.operateFailure')+'</font>',FHD.locale.get('fhd.common.error'));
		       }  
		    }
		});
	}
});