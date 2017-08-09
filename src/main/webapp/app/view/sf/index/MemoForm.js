Ext.define('FHD.view.sf.index.MemoForm', {
    extend: 'Ext.form.Panel',

    
    save: function (id,grid,win,str) {
    	var me = this;
    	value =me.form.getValues();
    	memoName= me.form.getValues()['memoName'];
  		var jsonArray = [];
  		jsonArray.push(str);
		FHD.submit({
			form : me.form,
			url:__ctxPath + '/sf/index/saveMemo.f',
			params : {
					id:id,
					memoName: me.form.getValues()['memoName'],
					memoTime: Ext.JSON.encode(jsonArray),
					memoContent:me.form.getValues()['memoContent']
				},
			callback:function(data){
				win.close();
				grid.store.load();
			}
		});
		
    		
    },

    // 初始化方法
    initComponent: function () {
        var me = this;
    	me.memoName = {
				labelWidth:90,
				xtype:me.isView?'displayfield':'textfield',
				lblAlign:'right',
				fieldLabel:'计划名称',
				value:'',
				name:'memoName',
				margin:'20 10 0 10',
				maxLength:200,
				width:400
		};
		me.memoContent = {
				labelWidth:90,
				xtype:'textarea',
				lblAlign:'right',
				fieldLabel:'计划内容',
				value:'',
				name:'memoContent',
				margin:'7 10 0 10',
				maxLength:400,
				width:400,
				height:200
		};
		//
        Ext.applyIf(me, {
            items : [me.memoName, me.memoContent]
        });
        me.callParent(arguments);
        
    },
    reloadData: function (id,grid) {
    	var me = this;
    	me.load({
			url: __ctxPath + '/sf/index/findmemo.f',
            params: {
                id: id
            },
            callback: function (data)  {
	    	  
            }
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
			items : [me],
			buttons: [{
	    				text: '保存',
	    				handler:function(){
	    					me.save(id,grid,me.formwindow,null);
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