/*
 * 内控评价列表页面 
 * */
Ext.define('FHD.view.response.new.RiskGroupContainer',{
	extend: 'Ext.panel.Panel',
    alias: 'widget.riskgroupcontainer',
    layout : {
    	type : 'vbox',
    	align : 'stretch'
    },
    items : [
    	Ext.create('FHD.view.response.new.TestList',{margin : '10 0 2 10',flex : 1,title : '生产任务过重，各，车间加班情况普遍，工人疲劳作业。'}),
    	Ext.create('FHD.view.response.new.TestList',{margin : '10 0 2 10',flex : 1,title : '<font color="maroon">厂区周围区域定位由工业区向居民区转变，导致公司现有的环境状况不能满足环保要求。</font>'})
//    	Ext.create('FHD.view.response.new.TestList',{flex : 1,title : '<font color="green">投资项目执行缺乏有效的管理、监督，可能因不能保障投资安全和投资收益而导致重大损失。</font>'}),
//    	Ext.create('FHD.view.response.new.TestList',{flex : 1,title : '<font color="red">资金记录不准确，不完整，可能造成账实不符或导致财务报表信息失真。</font>'})
    ]

});