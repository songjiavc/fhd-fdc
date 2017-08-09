Ext.define('FHD.view.sf.index.SFStudy.SFStudyVideoGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.sfstudyvideogrid',
 	requires: [
	],
	
	//查看视频
	getVedio: function(id){
		var me = this;
		var centerPanel = parent.Ext.getCmp('center-panel');
     	var tab = centerPanel.getComponent('FHD.view.sf.index.SFVedioPanel2');
     	if(tab){
     		tab.showVedio(id);
     	}else{
     		var vedioPanel = Ext.create('FHD.view.sf.index.SFVedioPanel2',{
      		});
     		vedioPanel.showVedio(id);
     	}
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
	            header: "视频名称",
	            dataIndex: 'name',
	            sortable: true,
	            flex: 1
	        },
			{
	            header: "操作",
	            dataIndex: '',
	            sortable: true,
	            flex: 0.5,
	            renderer:function(){
					return "<a href=\"javascript:void(0);\" >查看视频</a>"
				},
				listeners:{
	        		click:function(){
	        			var selection = me.getSelectionModel().getSelection();
	        			me.getVedio(selection[0].data.id);
    				}
        		}
			}
        ];
        
        var self = {
        	cols:cols,
        	margin: '2 0 0 0',
        	url:  __ctxPath + '/app/view/sf/index/SFStudy/SFStudyVideoGridStore.json',
		    border: true,
		    pagable : false,
		    checked: false,
		    searchable: false
        };
        Ext.apply(me,self);
       
        me.callParent(arguments);

    }

});