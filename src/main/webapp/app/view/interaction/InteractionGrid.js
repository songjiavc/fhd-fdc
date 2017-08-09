Ext.define('FHD.view.interaction.InteractionGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.interactionGrid',
 	requires: [
	],
	//发帖
    edit : function(){
    	var me = this;
    	var interactionCard = me.up('interactionCard');
    	interactionCard.showInteractionForm();
    },
    //回帖页面
    showReplyForm: function(id,name){
    	var me = this;
    	var interactionCard = me.up('interactionCard');
    	interactionCard.replyFormMain.idField.setValue(id);//赋值帖子id
    	interactionCard.replyFormMain.fieldSet.setTitle(name);//帖子名称
    	interactionCard.replyFormMain.reloadData(id);
    	interactionCard.showReplyFormMain();
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
			{
				header: "id",
				dataIndex:'id',
				hidden:true
			},
	        {
	            header: "标题",
	            dataIndex: 'title',
	            sortable: true,
	            flex: 2,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+150+'"'; 
     	    		return "<a href=\"javascript:void(0);\" onclick=\"Ext.getCmp('" + me.id 
     	    					+ "').showReplyForm('"+record.data.id+"','"+value+"')\">"+value+"</a>";
     			}
	        },{
	            header: "创建人",
	            dataIndex: 'createEmp',
	            sortable: false,
	            flex: 1
	        },{
	            header: "创建时间",
	            dataIndex: 'createTime',
	            sortable: false,
	            flex: 1
	        }
        ];
       
        Ext.apply(me,{
        	url : __ctxPath + '/sys/interaction/findinteractionsgrid.f',//查询列表url
        	cols:cols,
        	tbarItems:[{
        			text: '发帖',
        			iconCls:'icon-add',
        			handler:function(){
        				me.edit(true);
        			}
    			}],
		    border: false,
		    checked : true,
		    pagable : false
        });
       
        me.callParent(arguments);

    }
});