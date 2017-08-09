Ext.define('FHD.view.sf.index.SFMemoMain',{
	extend:'Ext.form.Panel',
	
	initComponent : function(){
		var me = this;
		
     	me.datePanel = Ext.create('Ext.picker.Date',{
			border:false, 
			flex:1,
			minDate: new Date()
		});
		me.datePanel.on('select', me.onSelect, me.datePanel);
		
		me.memoGrid = Ext.create('FHD.view.sf.index.SFMemoGrid',{
    		border:false
    	});
		Ext.apply(me,{
			layout: {
                type: 'vbox',
                align: 'stretch'
          	},
			items:[me.datePanel,me.memoGrid],
			border:true
		});
		me.callParent(arguments);
	},
    onSelect:function (){
    	var me =this;
    	var date = me.value.getDate();
    	var year = me.value.getFullYear();
    	var month = me.value.getMonth();
    	var str = year+'-'+month+'-'+date
		var formPanel = Ext.create('FHD.view.sf.index.MemoForm',{
		});
		me.formwindow = new Ext.Window({
			layout:'fit',
			iconCls: 'icon-show',//标题前的图片
			modal:true,//是否模态窗口
			collapsible:true,
			title:'添加计划',
			width:500,
			height:350,
			layout: {
				type: 'fit',
	        	align:'stretch'
	        },
			maximizable:true,//（是否增加最大化，默认没有）
			constrain:true,
			border:false,
			items : [formPanel],
			buttons: [{
	    				text: '保存',
	    				handler:function(){
	    					var panel = me.up('panel');
	    					formPanel.save(null,panel.memoGrid,me.formwindow,me.value);
	    				}
		    			},{
						text: '关闭',
						handler:function(){
							me.formwindow.close();
						}
				}]
		});
		me.formwindow.show();
    }
    
})