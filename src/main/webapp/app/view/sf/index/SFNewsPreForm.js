/**
 * @author : 邓广义
 *  风险评价报告模板管理表单面板
 */
Ext.define('FHD.view.sf.index.SFNewsPreForm',{
 	extend: 'Ext.form.Panel',
 	border:false,
    requires : [
                ],
	reloadData:function(templateId){
		var me = this;
		var url = __ctxPath + '/sys/report/findTemplateByid.f';
				me.load({
    	        	url:url,
	    	        params:{
	    	        	id:templateId
	    	        },
	    	        failure:function(form,action) {
	    	            alert("err 155");
	    	        },
	    	        success:function(form,action){
//	    	        	me.currentId = action.result.data.currentId;
    	            	var responseJson = Ext.JSON.decode(action.response.responseText);
            			//加载报告内容
//            			me.editor.html(responseJson.data.templateDataText);
            			me.showPanel = Ext.widget('panel',{
            				border:false,
            				html:responseJson.data.templateDataText
            			});
            			me.add(me.showPanel);
	    	        }
	    	    });
	},
    initComponent: function () {
		var me = this;
		me.templateCode=Ext.create('Ext.form.TextField', {
    	    fieldLabel: '模板编号'+'<font color=red>*</font>',
    	    allowBlank:false,//不允许为空
    	    name:'templateCode',
    	    flex:.5
    	});
		me.templateName=Ext.create('Ext.form.TextField', {
    	    fieldLabel: '模板描述'+'<font color=red>*</font>',
    	    allowBlank:false,//不允许为空
    	    name:'templateName',
    	    flex:.5
    	});
    	
		Ext.applyIf(me,{
            border: false,
            bodyPadding: "5 5 5 5",
            flex:1,
            overflowY:'auto'
		});
		me.callParent(arguments);
		me.createReportContent();
		
    	
    	
		
	},
	createReportContent:function(){

    	var me = this;
    
	}

});