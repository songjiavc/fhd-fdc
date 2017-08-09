Ext.define('FHD.view.kpi.cmp.selector.StrategyMapTreeSelector', {
	extend : 'Ext.Panel',
	border:false,
	requires : [ 'Ext.tree.Panel' ],

	smTreeVisible : true,
	OrgSmTreeRoot : {},
	smTreeRoot : {},
	animate:false,

	orgSmTreeIcon : 'icon-org',
	smTreeIcon : 'icon-flag-red',
	mineSmTreeIcon : 'icon-orgsub',

	smTreeTitle : FHD.locale.get('strategymaptree.title'),

	smTree : null,
	dateFieldable:false,

	extraParams : {
		canChecked : true,
		smIcon:''//目标显示的图标
	},
	items : new Array(),
	values : new Array(),
	onCheckchange : function() {
	},

	smClickFunction:function(){
		
	},
	orgClickFunction : function(){
	},
	checkModel : 'cascade',

	smTreeContextMenuFc : function(tree, node) {
	},

	/* 方法 */
	initValue : function() {
		var me = this;
		FHD.ajax( {
			url : __ctxPath + '/kpi/KpiTree/findrootbycompany',
			callback : function(objectMaps) {
				Ext.Array.each(objectMaps, function(object) {
					//me.mineSmTreeRoot = eval(object.sm);
					//me.smTreeRoot = eval(object.sm);
					/*目标树添加假根*/
					me.smTreeRoot = {
                            "id": "sm_root",
                            "text": FHD.locale.get('fhd.sm.strategymaps'),
                            "dbid": "sm_root",
                            "leaf": false,
                            "code": "sm",
                            "type": "sm",
                            "expanded":true
                        };
				});
				if (me.smTreeVisible) {
					me.smTree = me.createTree( {
						//'onCheckchange' : me.onCheckchange,
						'extraParams' : me.extraParams,
						'kpiUrl' : __ctxPath
								+ '/kpi/StrategyMapTreeSelector/treeloader',
						'titleIcon' : me.smTreeIcon,
						'title' : me.smTreeTitle,
						'clickFunction' : me.smClickFunction,
						'values' : me.values,
						//'checkModel' : me.checkModel,
						'root' : me.smTreeRoot,
						'contextItemMenuFc' : me.smTreeContextMenuFc
						,'dateFieldable':me.dateFieldable
						,'animate':me.animate
					});
					me.add(me.smTree);
				}
			}
		});
	},
	setTreeValues : function(checked) {
		if (this.smTree) {
			this.smTree.values = this.values;
			//this.smTree.setChecked(this.smTree.getRootNode(), checked);
		}
	},
	createTree : function(o) {
		return Ext.create('FHD.ux.TreePanel', {
			border:false,
			animate:o.animate,
			rowLines: false,
			dateFieldable:o.dateFieldable,
			id:o.id,
			iconCls : o.titleIcon,
			title : o.title,
			extraParams : o.extraParams,
//			onCheckchange : o.onCheckchange,
			url : o.kpiUrl,
			rootVisible : true,
			myexpand : false,
			//canSelect : 'sm',
			//checkModel : o.checkModel,
//			values : o.values,
//			monthClick:o.monthClick,
			viewConfig : {
				//stripeRows : true,
				listeners : {
					itemcontextmenu : function(view, rec, node, index, e) {
						e.stopEvent();
						var menu = o.contextItemMenuFc(view, rec, node, index, e);
						if (menu) {
							menu.showAt(e.getPoint());
						}
						return false;
					}
				}
			},
			listeners : {
				itemclick : function(node, record, item) {
					o.clickFunction(record);
				}
			},
			root : o.root,
			checked:false
//			check : function(thiz, item, check) {
//				this.setNodeChecked(item.data.dbid, check);
//				this.onCheckchange(item, check);
//			}
		})
	},
	initComponent : function() {
		// Ext.define('ObjMap', {
		// extend: 'Ext.data.Model',
		// fields:['kpi', 'sm', 'org']
		// });
		var me = this;
		Ext.applyIf(me, {
			layout : {
				type : 'accordion'
			},
			height : Ext.getBody().getHeight(),
			//title : FHD.locale.get('fhd.strategymap.strategymapmgr.tree.title'),
			border:false
		});
		me.callParent(arguments);
		me.initValue();
	}
})