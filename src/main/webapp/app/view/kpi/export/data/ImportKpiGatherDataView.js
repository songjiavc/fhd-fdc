Ext.define('FHD.view.kpi.export.data.ImportKpiGatherDataView', {
			extend : 'Ext.grid.Panel',
			border:false,
			initComponent : function() {
				var me = this;

				me.store = Ext.create('Ext.data.Store', {
							fields : ['rownum', 'code', 'name', 'frequence',
									'gatherDate', 'targetvalue','finishvalue','assessmentvalue', 'timeperiod',
									'eyear', 'validateMsg'],
							data : {
								'items' : me.items
							},
							proxy : {
								type : 'memory',
								reader : {
									type : 'json',
									root : 'items'
								}
							}
						});

				Ext.apply(me, {
							store : me.store,
							columns : [{
										text : '行号',
										dataIndex : 'rownum'
									}, {
										text : '编号',
										dataIndex : 'code',
										flex : 2
									}, {
										text : '名称',
										dataIndex : 'name',
										flex : 3
									}, {
										text : '频率',
										dataIndex : 'frequence'
									}, {
										text : '采集日期',
										dataIndex : 'gatherDate'
									}, {
										text : '目标值',
										dataIndex : 'targetvalue'
									}, {
										text : '实际值',
										dataIndex : 'finishvalue'
									}, {
										text : '评估值',
										dataIndex : 'assessmentvalue'
									}, {
										text : '区间纬度',
										dataIndex : 'timeperiod'
									}, {
										text : '年份',
										dataIndex : 'eyear'
									}, {
										text : '验证信息',
										dataIndex : 'validateMsg',
										flex : 4,
							            renderer: function (value, metaData, record, colIndex, store, view) {
							                metaData.tdAttr = 'data-qtip="' + (value?value:'') + '"';
							                return value;
							            }
									}],
							viewConfig : {
								getRowClass : function(record, rowIndex,
										rowParams, store) {
									return record.get("validateMsg")
											? "row-s"
											: "";
								}
							}
						});

				me.callParent(arguments);
			},

			reloadData : function() {
				var me = this;

			}

		})