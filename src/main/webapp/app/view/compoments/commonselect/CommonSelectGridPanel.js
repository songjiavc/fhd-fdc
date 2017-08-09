/**
 * @author jia.song@pcitc.com
 * @desc     人员选择组件根据角色和部门删选人员
 */
Ext.define('FHD.view.compoments.commonselect.CommonSelectGridPanel', {
    extend: 'FHD.ux.GridPanel',
    autoLoad : false,
    searchable : false,
    pagable : false,
    url : __ctxPath + '/risk/risk/findRisksByIds.f',
    alias: 'widget.commonselectgridpanel',
    cols : [  //默认就这个
    	{
			dataIndex:'id',
			hidden:true
		},{
            header: "名称",
            dataIndex: 'name',
            flex:1
        }
    ],
    
    reloadData : function(extraParams){
    	var me=this;
    	me.getStore().proxy.extraParams = extraParams;
    	me.getStore().load();
    },
    
    //store中添加元素方法，如果存在则不用添加
    insertRecord : function(r){
		var me = this;
		if(!me.multiSelect){
			me.getStore().removeAll();
			me.getStore().insert(0,r);
		}else{
		//判断是否存在该元素
			if(!me.isHasItemInGrid(r.id)){
				me.getStore().insert(0,r);
			}
		}
	},
    /*
     * 
     */
	initComponent : function(){
		var me = this;
        Ext.apply(me,{
        	cols : me.columns
        });
        me.callParent(arguments);
	}
});