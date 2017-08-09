Ext.define('FHD.view.SASACdemo.homepage.MajorRiskAnalysisGrid', {
    extend: 'FHD.ux.GridPanel',
    alias: 'widget.majorRiskAnalysisGrid',
 	requires: [
	],
	
	//添加，修改
	edit: function(isadd){
		var me = this;
		me.majorRiskAnalysisForm = Ext.create('FHD.view.SASACdemo.homepage.MajorRiskAnalysisForm');
		
		if(!isadd){
			var selection = me.getSelectionModel().getSelection();
			me.majorRiskAnalysisForm.riskName.setValue(selection[0].get('id'));
		}
		
		me.preWin = Ext.create('FHD.ux.Window', {
			title:'预测分析信息',
   		 	height: 600,
    		width: 1000,
    		maximizable: true,
   			layout: 'fit',
   			buttonAlign: 'center',
   			fbar: [
   					{ xtype: 'button', text: '保存', handler:function(){
   						FHD.notification('操作成功！','提示');
   						me.preWin.hide();}
   					}
				  ],
    		items: [me.majorRiskAnalysisForm]
		}).show();
	},
	
	setstatus : function(me){//设置按钮可用状态
    	if (me.down("[name='edit']")) {
            me.down("[name='edit']").setDisabled(me.getSelectionModel().getSelection().length === 0);
        }
        if (me.down("[name='delete']")) {
            me.down("[name='delete']").setDisabled(me.getSelectionModel().getSelection().length === 0);
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
	            header: "风险名称",
	            dataIndex: 'riskName',
	            sortable: true,
	            flex: 1
	        },
	        {
	            header: "所属类型",
	            dataIndex: 'kind',
	            sortable: true,
	            flex: 1
	        },{
	        	header: "风险描述",
				dataIndex:'desc',
				hidden:false,
				flex: 1
			},{
	        	header: "影响范围",
				dataIndex:'influRange',
				hidden:false,
				flex: 1
			},{
	        	header: "对收入的影响分析",
				dataIndex:'influIncome',
				hidden:false,
				flex: 1
			},{
	        	header: "对利润的影响分析",
				dataIndex:'influPro',
				hidden:false,
				flex: 1
			},{
	        	header: "应对计划",
				dataIndex:'solution',
				hidden:false,
				flex: 1
			},{
	        	header: "需国资委支持的工作",
				dataIndex:'work',
				hidden:false,
				flex: 1
			}
        ];
       
        Ext.apply(me,{
        	flex: 1,
        	url : __ctxPath + '/app/view/SASACdemo/homepage/majorriskgrid.json',//查询列表url
        	cols:cols,
		    border: false,
		    checked : false,
		    pagable : false,
		    searchable:false,
		    tbarItems:[{
    			btype:'add',
    			handler:function(){
    				me.edit(true);
    			}
			},'-',{
    			btype:'edit',
    			disabled:true,
    			name : 'edit',
    			handler:function(){
    				me.edit(false);
    			}
			},'-',{
    			btype:'delete',
    			disabled:true,
    			name : 'delete',
    			handler:function(){
    				
    			}
			}]
        });
        
        me.on('selectionchange',function(){me.setstatus(me)});
        me.callParent(arguments);
    }

});