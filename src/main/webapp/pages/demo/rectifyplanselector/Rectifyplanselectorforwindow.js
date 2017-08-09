Ext.define('FHD.demo.rectifyplanselector.Rectifyplanselectorforwindow', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Rectifyplanselectorforwindow',
			/**
			 * 初始化页面组件
			 */
			initComponent : function() {
				var me = this;
					var button = Ext.create('Ext.Button', {
		                text: '整改计划弹窗',
		                handler: function() {
		                	var rectifyplanforwindowselector = Ext.create('FHD.ux.icm.rectify.ImproveSelectorWindow',{
		        				multiSelect:true,
		        				modal: true,
		        				onSubmit:function(win){
		        					
		        				}
		        			}).show();
		                }
		            });
					
					//表单panel
					var form = Ext.create("Ext.form.Panel",{
						autoScroll: true,
				        border: false,
				        bodyPadding: "5 5 5 5",
				        items: [{
				            xtype: 'fieldset',//基本信息fieldset
				            collapsible: false,
				            defaults: {
				            	margin: '7 30 3 30',
				            	columnWidth:.2
				            },
				            layout: {
				                type: 'column'
				            },
				            title: ' 整改计划弹窗选择',
				            items:[button]
				        }]
					});
				Ext.applyIf(me, {
							autoScroll : true,
							border : false,
							items : [form]
						});
				me.callParent(arguments);

			}
		})