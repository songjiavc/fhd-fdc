
Ext.define('FHD.view.risk.assess.SendEmailCheckPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.sendEmailCheckPanel',
    
    requires: [
               
    ],
    
    // 初始化方法
    initComponent: function() {
    	var me = this;
    	
    	var valueStore = Ext.create('Ext.data.Store', {//状态store
    	    fields: ['id', 'text'],
    	    proxy: {
    	         type: 'ajax',
    	         url: __ctxPath + '/sys/assess/findcomboxvalue.f',
    	         reader: {
    	             type: 'json',
    	             root: 'datas'
    	         }
    	     }, 
    	    autoLoad:true
        });
    	
    	me.combValue = Ext.create('Ext.form.field.ComboBox', {
		    store: valueStore,
		    fieldLabel: '是否发送email',
		    allowBlank:false,//不允许为空
		    editable:false,
		    margin: '7 30 3 30',
		    queryMode: 'local',
		    //name:'value',
		    displayField: 'text',
		    valueField: 'id',
		    triggerAction :'all',
		    columnWidth:.5
		});
    	
    	me.array = [];
        Ext.Ajax.request({
		    url: __ctxPath + '/sys/assess/findissendemailfieldsetnew.f',
		    async:  false,
		    success: function(response){
		        var text = response.responseText;
		        Ext.each(Ext.JSON.decode(text).allList,function(r,i){
		        	var field;
		        	me.childrenArray = [];
		        	me.array.push(
		        		field = Ext.create('Ext.form.FieldSet',{
				    		collapsible: true,
				    		id: r.dictTypeId,
				    		collapsed: true,
				    		//height: 100,
				    		defaults: {
					                    margin: '3 30 3 30'
					            },
					        layout: {
				     	        type: 'column'
				     	    },
							title: r.dictTypeName
				    	})
			        );
		        	for(var i=0 ; i<r.children.length;i++){
		        		field.add(
		        			Ext.create('Ext.form.ComboBox',{
			    			store: valueStore,
			    			id:r.children[i].dictentryId,
			    			fieldLabel: r.children[i].Name,
			    			labelWidth: 200,
						    allowBlank:false,//不允许为空
						    editable:false,
						    margin: '7 30 3 30',
						    queryMode: 'local',
						    displayField: 'text',
						    valueField: 'id',
						    columnWidth : .5,
						    triggerAction :'all',
						    value: r.children[i].value||'0yn_n',
						    listeners:{
							    	change:function(field,newValue){
						    			FHD.ajax({//ajax调用
											url : __ctxPath+ '/sys/assess/mergevaluebydictentryid.f',
										    params : {
										    	dictEntryId: field.id,
										    	value: newValue
											},
											callback : function(data) {
												if(data){
													FHD.notification('操作成功',FHD.locale.get('fhd.common.prompt'));
												}else{
													FHD.alert('操作失败');
												}
											}
										});
							    	}
							    }
			    			})
		        		);
			        }
		        });
		        /*Ext.each(Ext.JSON.decode(text).dictEntryList,function(r,i){
		        	me.array.push(
		        		Ext.create('Ext.form.FieldSet',{
				    		collapsible: true,
				    		id: r.dictentryId,
				    		collapsed: true,
				    		//height: 100,
				    		defaults: {
					                    margin: '3 30 3 30'
					            },
							title: r.Name,
							items:[{
									xtype: 'combo',
									store: valueStore,
								    fieldLabel: '是否发送email',
								    allowBlank:false,//不允许为空
								    editable:false,
								    margin: '7 30 3 30',
								    queryMode: 'local',
								    displayField: 'text',
								    valueField: 'id',
								    triggerAction :'all',
								    value: r.value||'0yn_n',
								    listeners:{
								    	change:function(field,newValue){
							    			FHD.ajax({//ajax调用
												url : __ctxPath+ '/sys/assess/mergevaluebydictentryid.f',
											    params : {
											    	dictEntryId: field.up().id,
											    	value: newValue
												},
												callback : function(data) {
													if(data){
														FHD.notification('操作成功',FHD.locale.get('fhd.common.prompt'));
													}else{
														FHD.alert('操作失败');
													}
												}
											});
								    	}
								    }
								}]
				    	})
			        );
		        });*/
		    }});
    	//是否发送email
		me.formPanel = Ext.create('Ext.form.Panel',{
			region: 'center',
			autoScroll: true,
			border:false
		});
		for(var i = 0 ; i < me.array.length; i++){
    		me.formPanel.add(me.array[i]);
        }
    	
		
    	Ext.apply(me, {
            border:false,
            layout: 'border',
            items:[me.formPanel]
        });
    	
        me.callParent(arguments);
        
    }
});