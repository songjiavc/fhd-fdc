Ext.define('FHD.view.interaction.ReplyGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.replyGrid',
    
	//加载数据
    reloadData: function(postsId){
    	var me = this;
    	me.store.proxy.url = __ctxPath + '/sys/interaction/findreplysgridbypostsid.f';
 		me.store.proxy.extraParams.postsId = postsId;
 		me.store.load();
    },
    //删除回复
    deleteReply: function(id){
    	var me = this;
    	var selection = me.getSelectionModel().getSelection();
    },
	
    // 初始化方法
    initComponent: function() {
        var me = this;
        var cols = [
			{
				header: "replyId",
				dataIndex:'replyid',
				hidden:true
			},
	        {
	            header: "用户名",
	            dataIndex: 'createEmpName',
	            sortable: true,
	            width: 100
	        },{
	            header: "回复内容",
	            dataIndex: 'content',
	            sortable: false,
	            flex: 1,
	            renderer:function(value,metaData,record,colIndex,store,view) { 
     				metaData.tdAttr = 'data-qtip="'+value+'" data-qwidth="'+400+'"'; 
     	    		return value;
     			}
	        },{
	            header: "回复时间",
	            dataIndex: 'createTime',
	            sortable: false,
	            width: 200
	        }/*,{
	            header: "操作",
	            dataIndex: '',
	            sortable: true,
	            width: 100,
	            renderer:function(){
					return "<a href=\"javascript:void(0);\" >删除回复</a>&nbsp;&nbsp;&nbsp;"	//
				},
				listeners:{
	        		click:function(){
	        			me.deleteReply();
    				}
        		}
			}*/
        ];
       
        Ext.apply(me,{
        	cols:cols,
        	scroll: 'vertical',
		    border: true,
		    height: 150,
		    checked : false,
		    searchable: false,
		    pagable : false
        });
       
        me.callParent(arguments);

    }
});