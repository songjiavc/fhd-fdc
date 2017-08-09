Ext.define('FHD.view.interaction.InteractionForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.interactionForm',
	
	reloadData : function(){
		var me = this;
	},
	//发表新帖
	submit: function(){
		var me = this;
		if(me.editor.html() == ''){
			alert('请输入内容!');
			return false;
		}
	    var form = me.getForm();
	    var requestObj = form.getValues();
	    requestObj.contents = me.editor.html();
	    FHD.ajax({
		   url: __ctxPath + '/sys/interaction/saveinteractioncontents',
	       params: {
	       		contentEdit: Ext.JSON.encode(requestObj)
	       },
	       callback: function (data) {
	       		FHD.notification('操作成功！',FHD.locale.get('fhd.common.prompt'));
	       		me.up('interactionCard').interactionGrid.store.load();
   				me.showGrid();
	       }
	   });
	},
	//返回列表
	showGrid: function(){
		var me = this;
    	var interactionCard = me.up('interactionCard');
    	interactionCard.showInteractionGrid();
	},
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        me.idField = Ext.widget('hiddenfield',{
            name:"id",
            value:''
        });
        //帖子名称
        me.name = Ext.widget('textfield', {//计划名称
            fieldLabel: '计划名称'+'<font color=red>*</font>',
            allowBlank:false,//不允许为空
            margin: '15 20 0 30',
            name: 'title',
            columnWidth: 1
        });
        
        //帖子内容
        me.content = Ext.widget('textareafield', {
            xtype: 'textareafield',
            fieldLabel: '内容' + '<font color=red>*</font>',
            margin: '15 20 0 30',
            name: 'content',
            height : 350,
            columnWidth: 1
        });
        
        Ext.apply(me, {
        	border:false,
        	bbar: {items: [ '->',{
			            text: '返回', //保存按钮
			            iconCls: 'icon-operator-home',
			            handler: function () {
			               me.showGrid();
			            }
			        },{text: '发表', //保存按钮
			            iconCls: 'icon-operator-submit',
			            handler: function () {
			              me.submit();
			            }
			        }
	  			]
	   		},
        	layout: {
     	        type: 'column'
     	    },
            items : [me.name,me.content,me.idField],
            listeners:{
            	render:function(){
    		        setTimeout(function(){
    		        	me.editor = KindEditor.create('#' + (me.content.getEl().query('textarea')[0]).id);
    		        	me.editor.resizeType = 1;
    		        });
    	        }  
    		}
        });

       me.callParent(arguments);
    }

});