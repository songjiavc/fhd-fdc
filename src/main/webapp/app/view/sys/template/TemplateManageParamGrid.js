Ext.define('FHD.view.sys.template.TemplateManageParamGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.templateManageParamGrid',
 	requires: [
	],
	
    //重新加载数据方法
    reloadData: function(nodeId,type){
    	var me = this;
    },
    
    // 初始化方法
    initComponent: function() {
        var me = this;
        
        var cols = [
			{header:'参数名称',dataIndex:'parameter', flex:1},
	        {header:'参数描述',dataIndex:'describe', flex:1}
        ];
       
        Ext.apply(me,{
        	url: __ctxPath + '/sys/st/findDictEntryByType.f',
        	cols:cols,
		    border: false,
		    checked : false,
		    pagable : false,
		    listeners:{
       			itemdblclick:function(){//双击,添加文本到副文本编辑器
       				var templateManageEdit = me.up('templateManageEdit');
       				var selection = me.getSelectionModel().getSelection()[0];//得到选中的记录
       				templateManageEdit.editor.html(templateManageEdit.editor.html() + selection.data.parameter);
       			}
       		}
        });
       
        me.callParent(arguments);

    }

});