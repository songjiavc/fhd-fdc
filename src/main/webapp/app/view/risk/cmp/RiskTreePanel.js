/**
 * 风险树
 * 
 * @author zhengjunxiang
 */
Ext.define('FHD.view.risk.cmp.RiskTreePanel', {
    extend: 'FHD.ux.TreePanel',
    alias: 'widget.risktreepanel',

    /**
	 * public
	 * 接口属性
	 */
    type:'normaltree',//checkboxTree
    showLight:false,
    checkable:false,
	cascade : false,
	subCompany : false,
	border : false,
	/**
	 * public 选择选中的id
	 */
	getCheckedIds : function(node) {
		var me = this;
		var idArr = [];
		idArr = me.getCheckedNodes(me.getRootNode());
		return idArr.join();
	},
	getCheckedNodes : function(node) {
		var me = this;
		var checked = [];
		if (node.data.checked == true) {
			if(node.data.id != 'root'){
				checked.push(node.data.id);
			}
			if (!node.isLeaf()) {
				for ( var i = 0; i < node.childNodes.length; i++) {
					checked = checked.concat(me
							.getCheckedNodes(node.childNodes[i]));
				}
			}
		}
		return checked;
	},
	
    /**
	 * private 设置父节点级联选中
	 */
	setParentNode : function(node, checked) {
		var me = this;
		node.set('checked', checked);
		if (checked && node.parentNode != null) {
			me.setParentNode(node.parentNode, checked);
		}
	},
   
    // 初始化方法
    initComponent: function() {
    	var me = this;

    	//初始化参数
    	var extraParams = {};

    	me.queryUrl = __ctxPath + '/cmp/risk/getRiskTreeRecord?schm='+me.typeId;	//暂时不留作参数/component/riskTreeLoader
    	
    	//复选树
        if(me.type == 'checkboxtree'){
        	extraParams.showLight = false;
        	extraParams.checkable = true;
        	//添加级联事件  父组件有 checkchange 事件
        	if(me.cascade){
        		me.checkModel = 'cascade';
            	me.check = function(me,node,checked){
    				me.setParentNode(node,checked);//点击子节点选中父节点
    			};
        	}
        }else{
        	extraParams.showLight = me.showLight;
        	extraParams.checkable = false;
        }
    	extraParams.subCompany = me.subCompany;
        
    	Ext.apply(me, {
    		rootVisible: true,
            root: {
                "id": "root",
                "text": "风险",
                "dbid": "sm_root",
                "leaf": false,
                "code": "sm",
                "type": "orgRisk",
                "expanded": true,
                'iconCls':'icon-ibm-icon-scorecards'	//样式
            },
            url:me.queryUrl,
           	multiSelect: true,
           	rowLines:false,
          	singleExpand: false,
           	checked: false,
           	extraParams:extraParams
        });
    	
        me.callParent(arguments);
    }
});