Ext.define('FHD.view.interaction.ReplyFormMain', {
    extend: 'Ext.form.Panel',
    alias: 'widget.replyFormMain',
	
	reloadData : function(postsId){
		var me = this;
		me.load({
	        url:  __ctxPath + '/sys/interaction/findpostsformbyid',
	        params:{
	        	postsId: postsId
	        },
	        failure:function(form,action) {
	            return true;
	        },
	        success:function(form,action){
	        	me.idField.setValue(action.result.data.id);
	        	if(action.result.data.countentStr != ''){
	            	me.editor.html(action.result.data.countentStr);
            	}else{
            		me.editor.html('');
            	}
            	return true;
	        }
	    });
	    me.replyGrid.reloadData(postsId);
	},
	//回复
	reply: function(){
		var me = this;
		if('' == me.replyText.getValue()){
			FHD.notification('请填写回复内容！',FHD.locale.get('fhd.common.prompt'));
			return false;
		}
		FHD.ajax({
		   url: __ctxPath + '/sys/interaction/savereplybypostsid',
	       params: {
	       		postsId: me.idField.getValue(),
	       		countentText: me.replyText.getValue()
	       },
	       callback: function (data) {
	       		FHD.notification('操作成功！',FHD.locale.get('fhd.common.prompt'));
	       		me.replyText.setValue("");
	       		me.replyGrid.reloadData(me.idField.getValue());
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
        
        me.idField=Ext.widget('hiddenfield',{
            name:"id",
            value:''
        });
        
        //帖子内容
        me.content = Ext.widget('textareafield', {
            xtype: 'textareafield',
            margin: '7 10 0 30',
            name: 'content',
            height : 180,
            columnWidth: 1
        });
        //回复
        me.replyText = Ext.widget('textareafield', {
            xtype: 'textareafield',
            margin: '7 10 0 30',
            name: 'content',
            columnWidth: 1
        });
        //回复列表
        me.replyGrid = Ext.create('FHD.view.interaction.ReplyGrid',{
        	margin: '7 10 0 30'
        });
        
        me.fieldSet = Ext.create('Ext.form.FieldSet',{
        	collapsible: true,
            defaultType: 'textfield',
            columnWidth: 1,
            margin: '5 5 0 5',
            layout: {
     	        type: 'column'
     	    },
     	    items : [me.content]
        });
        
        me.fieldSetGrid = {
            xtype:'fieldset',
            title: '回复列表',
            collapsible: true,
            columnWidth: 1,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            layout: {
     	        type: 'fit'
     	    },
     	    items : [me.replyGrid]
        };
        
        me.replyFieldSet = {
            xtype:'fieldset',
            title: '回复',
            collapsible: true,
            columnWidth: 1,
            defaultType: 'textfield',
            margin: '5 5 0 5',
            layout: {
     	        type: 'fit'
     	    },
     	    items : [me.replyText]
        };
        
        Ext.apply(me, {
        	border:false,
        	autoScroll: true,
        	bbar: {items: [ '->',{
			            text: '返回', //保存按钮
			            iconCls: 'icon-operator-home',
			            handler: function () {
			               me.showGrid();
			            }
			        },{text: '回复', //保存按钮
			            iconCls: 'icon-operator-submit',
			            handler: function () {
			              me.reply();
			            }
			        }
	  			]
	   		},
        	layout: {
     	        type: 'column'
     	    },
            items : [me.fieldSet,me.fieldSetGrid,me.replyFieldSet],
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