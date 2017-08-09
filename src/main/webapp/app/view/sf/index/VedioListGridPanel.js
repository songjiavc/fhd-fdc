Ext.define('FHD.view.sf.index.VedioListGridPanel', {
	extend : 'FHD.ux.GridPanel',
    
	border : true,
    searchable : false,
    columnLines: true,
    rowLines:true,
    pagable:false,
    storeGroupField:false,
	type:null,
	reloadData : function() {
		var me = this;
		if(me.type=='1'){
			me.store.proxy.url=__ctxPath + '/app/view/sf/vediogrid.json'
		}else if(me.type=='2'){
			me.store.proxy.url=__ctxPath + '/app/view/sf/vediogrid2.json'
		}else if(me.type=='3'){
			me.store.proxy.url=__ctxPath + '/app/view/sf/vediogrid3.json'
		}
		me.store.load();
	},
	initComponent : function() {
		var me = this;
		me.cols = null;
		if(me.type=='1'||me.type=='2'){
			me.cols = [
	      	{
				dataIndex:'id',
				hidden:true,
				width:0
			},{
	            header: "危险点名称",
	            dataIndex: 'name',
	            sortable: false,
	            //align: 'center',
	           	flex:2
	       	},{
	            header: "位置",
	            dataIndex: 'pos',
	            sortable: false,
	            //align: 'center',
	           	flex:1.5
	       	},{
	            header: "主管单位",
	            dataIndex: 'deptName',
	            sortable: false,
	            //align: 'center',
	           	flex:2
	       	},{
	            header: "操作",
	            dataIndex: '',
	            sortable: true,
	            flex:1.5,
	            renderer:function(){
					return "<a href=\"javascript:void(0);\" >查看现场</a>"	//
				},
				listeners:{
	        		click:function(){
	        			var selection = me.getSelectionModel().getSelection();
	        			me.getVedio(selection[0].data.id);
    				}
        		}
			}]
		}else if(me.type=='3'){
			me.cols = [
				{
					dataIndex:'id',
					hidden:true,
					width:0
				},{
		            header: "生产现场",
		            dataIndex: 'name',
		            sortable: false,
		            //align: 'center',
		           	flex:2
		       	},{
		            header: "厂房号",
		            dataIndex: 'pos',
		            sortable: false,
		            //align: 'center',
		           	flex:2
		       	},{
		            header: "操作",
		            dataIndex: '',
		            sortable: true,
		            flex:1,
		            renderer:function(){
						return "<a href=\"javascript:void(0);\" >查看现场</a>"	//
					},
					listeners:{
		        		click:function(){
		        			var selection = me.getSelectionModel().getSelection();
		        			me.getVedio(selection[0].data.id);
	    				}
	        		}
				}
			]
		}

		Ext.apply(me, {
			cols : me.cols
		});
		me.callParent(arguments);
	},
	
	getVedio: function (name){
     	var centerPanel = parent.Ext.getCmp('center-panel');
     	var tab = centerPanel.getComponent('FHD.view.sf.index.SFVedioPanel2');
     	if(tab){
     		tab.showVedio(name);
     	}else{
     		var vedioPanel = Ext.create('FHD.view.sf.index.SFVedioPanel2',{
      		});
     		vedioPanel.showVedio(name);
     	}
     	
     	
      }
});
