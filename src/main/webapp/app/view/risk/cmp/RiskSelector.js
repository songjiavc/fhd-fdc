/**
 * 左侧事件折叠树，右侧待选列表和已选列表布局
 * @author 郑军祥 2013-07-13
 * */

Ext.define('FHD.view.risk.cmp.RiskSelector', {
	extend : 'FHD.ux.treeselector.TreeSelector',
	alias : 'widget.riskselector',
	
	onlyLeaf:false,	//只叶子节点可选
	afterEnter:Ext.emptyFn(),
	initComponent : function() {
		var me = this;
        Ext.apply(me, {
        	title : me.title || '请您选择风险',
        	treeRootText:'风险',
			columns : [{
						dataIndex : 'code',
						header : '风险编号'
					}, {
						dataIndex : 'name',
						header : '风险名称',
						isPrimaryName : true
					}],
			treeUrl:'/cmp/risk/getRiskTreeRecord?onlyLeaf='+me.onlyLeaf+'&schm='+me.schm,
			initUrl:'/cmp/risk/getRiskByIds',
			fieldLabel : me.fieldLabel || '',
			labelAlign : me.labelAlign || 'left',
			multiSelect : me.multiSelect || false,
			name : me.name,
			columnWidth : me.columnWidth || .5
        });
        me.callParent(arguments);
	}
});