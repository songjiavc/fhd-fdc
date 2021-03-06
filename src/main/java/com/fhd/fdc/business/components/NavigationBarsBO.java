package com.fhd.fdc.business.components;

import static org.hibernate.FetchMode.SELECT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fhd.entity.process.Process;
import com.fhd.entity.comm.Category;
import com.fhd.dao.fdc.NavigationBarsDAO;
import com.fhd.fdc.utils.UserContext;
import com.fhd.entity.kpi.Kpi;
import com.fhd.entity.kpi.StrategyMap;
import com.fhd.sys.business.organization.OrgPositionBO;
import com.fhd.dao.sys.autho.SysoRoleDAO;
import com.fhd.entity.risk.Risk;
import com.fhd.entity.sys.auth.SysRole;
import com.fhd.entity.sys.orgstructure.SysOrganization;
import com.fhd.entity.sys.orgstructure.SysPosition;

@Service
public class NavigationBarsBO {

	@Autowired
	private NavigationBarsDAO o_navigationBarsDAO;
	
	@Autowired
	private SysoRoleDAO o_sysoRoleDAO;
	
	@Autowired
	private OrgPositionBO o_orgPositionBO;
	
	public Map<String, Object> findNavigationBars(String id, String type) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		List<Object> list = null;
		String companyId = UserContext.getUser().getCompanyid();//所在公司id
		String userName = UserContext.getUser().getUsername();//用户名
		int i = 1;
		String countroot = "";
		
		if("sc".equalsIgnoreCase(type)){
			//通过ID找到对应实体
			Category en = (Category) this.findObjectById(id, Category.class);
			
			if(en != null){
				if(en.getParent() != null){//如果不是根节点的情况
					
					list = this.findObjectListSome(companyId, en.getParent().getId(), true, false, Category.class);
					for (Object obj : list) {
						Category ens = (Category)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					i++;
					
					while (true) {
						//通过当前节点parentId找上级别节点
						if(list == null){
							en = (Category)this.findObjectById(en.getParent().getId(), Category.class);
						}else{
							en = (Category)this.findObjectById(((Category)list.get(0)).getParent().getId(), Category.class);
						}
						
						if(en != null){
							//如果是根的情况存储,并退出
							if(en.getParent() == null){
								list = this.findObjectListSome(companyId, null, true, true, Category.class);
								for (Object obj : list) {
									Category ens = (Category)obj;
									if(ens.getId().equalsIgnoreCase(en.getId())){
										//是自己
										mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
									}else{
										mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
									}
								}
								countroot = "1";
								break;
							}
							
							//不是根的情况
							list = this.findObjectListSome(companyId, en.getParent().getId(), true, false, Category.class);
							for (Object obj : list) {
								Category ens = (Category)obj;
								if(ens.getId().equalsIgnoreCase(en.getId())){
									//是自己
									mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
								}else{
									mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
								}
							}
							i++;
						}
					}
				}else{
					list = this.findObjectListSome(companyId, null, true, true, Category.class);
					for (Object obj : list) {
						Category ens = (Category)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					countroot = "1";
				}
			}
		}else if("kpi".equalsIgnoreCase(type)){
			//通过ID找到对应实体
			Kpi en = (Kpi) this.findObjectById(id, "KC", Kpi.class);
			
			if(en != null){
				if(en.getParent() != null){//如果不是根节点的情况
					
					list = this.findObjectListSome(companyId, en.getParent().getId(), "KC", true, false, Kpi.class);
					for (Object obj : list) {
						Kpi ens = (Kpi)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					i++;
					
					while (true) {
						//通过当前节点parentId找上级别节点
						if(list == null){
							en = (Kpi)this.findObjectById(en.getParent().getId(), "KC", Kpi.class);
						}else{
							en = (Kpi)this.findObjectById(((Kpi)list.get(0)).getParent().getId(), "KC", Kpi.class);
						}
						
						if(en != null){
							//如果是根的情况存储,并退出
							if(en.getParent() == null){
								list = this.findObjectListSome(companyId, null, "KC", true, true, Kpi.class);
								for (Object obj : list) {
									Kpi ens = (Kpi)obj;
									if(ens.getId().equalsIgnoreCase(en.getId())){
										//是自己
										mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
									}else{
										mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
									}
								}
								countroot = "1";
								break;
							}
							
							//不是根的情况
							list = this.findObjectListSome(companyId, en.getParent().getId(), "KC", true, false, Kpi.class);
							for (Object obj : list) {
								Kpi ens = (Kpi)obj;
								if(ens.getId().equalsIgnoreCase(en.getId())){
									//是自己
									mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
								}else{
									mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
								}
							}
							i++;
						}
					}
				}else{
					list = this.findObjectListSome(companyId, null, "KC", true, true, Kpi.class);
					for (Object obj : list) {
						Kpi ens = (Kpi)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					countroot = "1";
				}
			}
		}else if("sm".equalsIgnoreCase(type)){
			//通过ID找到对应实体
			StrategyMap en = (StrategyMap) this.findObjectById(id, StrategyMap.class);
			
			if(en != null){
				if(en.getParent() != null){//如果不是根节点的情况
					
					list = this.findObjectListSome(companyId, en.getParent().getId(), true, false, StrategyMap.class);
					for (Object obj : list) {
						StrategyMap ens = (StrategyMap)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					i++;
					
					while (true) {
						//通过当前节点parentId找上级别节点
						if(list == null){
							en = (StrategyMap)this.findObjectById(en.getParent().getId(), StrategyMap.class);
						}else{
							en = (StrategyMap)this.findObjectById(((StrategyMap)list.get(0)).getParent().getId(), StrategyMap.class);
						}
						
						if(en != null){
							//如果是根的情况存储,并退出
							if(en.getParent() == null){
								list = this.findObjectListSome(companyId, null, true, true, StrategyMap.class);
								for (Object obj : list) {
									StrategyMap ens = (StrategyMap)obj;
									if(ens.getId().equalsIgnoreCase(en.getId())){
										//是自己
										mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
									}else{
										mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
									}
								}
								countroot = "1";
								break;
							}
							
							//不是根的情况
							list = this.findObjectListSome(companyId, en.getParent().getId(), true, false, StrategyMap.class);
							for (Object obj : list) {
								StrategyMap ens = (StrategyMap)obj;
								if(ens.getId().equalsIgnoreCase(en.getId())){
									//是自己
									mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
								}else{
									mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
								}
							}
							i++;
						}
					}
				}else{
					list = this.findObjectListSome(companyId, null, true, true, StrategyMap.class);
					for (Object obj : list) {
						StrategyMap ens = (StrategyMap)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					countroot = "1";
				}
			}
		}else if("role".equalsIgnoreCase(type)){
			if(StringUtils.isNotBlank(id)){
				//通过ID找到对应实体
				SysRole en = o_sysoRoleDAO.get(id);
				
				if(en != null){
					list = this.findObjectListSome(SysRole.class);
					for (Object obj : list) {
						SysRole ens = (SysRole)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getRoleName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_root", ens.getRoleName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					countroot = "1";
				}
			}
		}else if("org".equalsIgnoreCase(type)){
			SysPosition position = o_orgPositionBO.findPositionByIds(id);
			String positionId = "";
			boolean isRoot = false;
			result.put("gw", isRoot);
			if(position != null){
				id = position.getSysOrganization().getId();
				positionId = position.getId();
				isRoot = true;
				result.put("gw", isRoot);
			}
			if(userName.equalsIgnoreCase("admin")){
				//通过ID找到对应实体
				SysOrganization en = (SysOrganization) this.findObjectById(id, SysOrganization.class, "org");
				if(en != null){
					if(en.getParentOrg() != null){//如果不是根节点的情况
						
						list = this.findObjectListSome(companyId, en.getParentOrg().getId(), false, SysOrganization.class);
						for (Object obj : list) {
							SysOrganization ens = (SysOrganization)obj;
							if(ens.getId().equalsIgnoreCase(en.getId())){
								//是自己
								mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getOrgname()));//传给前台key已实体ID++,,count区分当前选中节点
								this.orgAddPostion(mapList, ens.getId(), isRoot, positionId, id);
							}else{
								mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getOrgname()));//传给前台key已实体ID_count区分其他节点
							}
						}
						i++;
						
						while (true) {
							//通过当前节点parentId找上级别节点
							if(list == null){
								en = (SysOrganization)this.findObjectById(en.getParentOrg().getId(), SysOrganization.class, "org");
							}else{
								en = (SysOrganization)this.findObjectById(((SysOrganization)list.get(0)).getParentOrg().getId(), SysOrganization.class, "org");
							}
							
							if(en != null){
								//如果是根的情况存储,并退出
								if(en.getParentOrg() == null){
									list = this.findObjectListSome(companyId, null, true, SysOrganization.class);
									for (Object obj : list) {
										SysOrganization ens = (SysOrganization)obj;
										if(ens.getId().equalsIgnoreCase(en.getId())){
											//是自己
											mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getOrgname()));//迭代传给前台key已实体ID++,,count区分当前选中节点
											this.orgAddPostion(mapList, ens.getId(), isRoot, positionId, id);
										}else{
											mapList.add(this.getMap(ens.getId() + "_root", ens.getOrgname()));//迭代传给前台key已实体ID_count区分其他节点
										}
									}
									countroot = "1";
									break;
								}
								
								//不是根的情况
								list = this.findObjectListSome(companyId, en.getParentOrg().getId(), false, SysOrganization.class);
								for (Object obj : list) {
									SysOrganization ens = (SysOrganization)obj;
									if(ens.getId().equalsIgnoreCase(en.getId())){
										//是自己
										mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getOrgname()));//迭代传给前台key已实体ID++,,count区分当前选中节点
										this.orgAddPostion(mapList, ens.getId(), isRoot, positionId, id);
									}else{
										mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getOrgname()));//迭代传给前台key已实体ID_count区分其他节点
									}
								}
								i++;
							}
						}
					}else{
						list = this.findObjectListSome(companyId, null, true, SysOrganization.class);
						for (Object obj : list) {
							SysOrganization ens = (SysOrganization)obj;
							if(ens.getId().equalsIgnoreCase(en.getId())){
								//是自己
								mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getOrgname()));//传给前台key已实体ID++,,count区分当前选中节点
								this.orgAddPostion(mapList, ens.getId(), isRoot, positionId, id);
							}else{
								mapList.add(this.getMap(ens.getId() + "_root", ens.getOrgname()));//传给前台key已实体ID_count区分其他节点
							}
						}
						countroot = "1";
					}
				}
			}else{
				//通过ID找到对应实体
				boolean root = false;
				if(id.equalsIgnoreCase(companyId)){
					root = true;
				}else{
					root = false;
				}
				SysOrganization en = (SysOrganization) this.findObjectById(id, SysOrganization.class, "org");
				if(en != null){
					if(en.getParentOrg() != null){//如果不是根节点的情况
						if(root){
							list = this.findObjectListSome(companyId, en.getParentOrg().getId(), false, SysOrganization.class);
							for (Object obj : list) {
								SysOrganization ens = (SysOrganization)obj;
								if(ens.getId().equalsIgnoreCase(en.getId())){
									//是自己
									mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getOrgname()));//传给前台key已实体ID++,,count区分当前选中节点
									this.orgAddPostion(mapList, ens.getId(), isRoot, positionId, id);
								}else{
									mapList.add(this.getMap(ens.getId() + "_root", ens.getOrgname()));//传给前台key已实体ID_count区分其他节点
								}
							}
							countroot = "1";
						}else{
							list = this.findObjectListSome(companyId, en.getParentOrg().getId(), false, SysOrganization.class);
							for (Object obj : list) {
								SysOrganization ens = (SysOrganization)obj;
								if(ens.getId().equalsIgnoreCase(en.getId())){
									//是自己
									mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getOrgname()));//传给前台key已实体ID++,,count区分当前选中节点
									this.orgAddPostion(mapList, ens.getId(), isRoot, positionId, id);
								}else{
									mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getOrgname()));//传给前台key已实体ID_count区分其他节点
								}
							}
							i++;
							
							while (true) {
								//通过当前节点parentId找上级别节点
								if(list == null){
									en = (SysOrganization)this.findObjectById(en.getParentOrg().getId(), SysOrganization.class, "org");
								}else{
									en = (SysOrganization)this.findObjectById(((SysOrganization)list.get(0)).getParentOrg().getId(), SysOrganization.class, "org");
								}
								
								if(en != null){
									//如果是根的情况存储,并退出
									if(en.getId().equalsIgnoreCase(companyId)){
										if(en.getParentOrg()==null){
											list = this.findObjectListSome(companyId, null, true, SysOrganization.class);
										}else{
											list = this.findObjectListSome(companyId, en.getParentOrg().getId(), false, SysOrganization.class);
										}
										for (Object obj : list) {
											SysOrganization ens = (SysOrganization)obj;
											if(ens.getId().equalsIgnoreCase(en.getId())){
												//是自己
												mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getOrgname()));//迭代传给前台key已实体ID++,,count区分当前选中节点
												this.orgAddPostion(mapList, ens.getId(), isRoot, positionId, id);
											}else{
												mapList.add(this.getMap(ens.getId() + "_root", ens.getOrgname()));//迭代传给前台key已实体ID_count区分其他节点
											}
										}
										countroot = "1";
										break;
									}
									
									//不是根的情况
									list = this.findObjectListSome(companyId, en.getParentOrg().getId(), false, SysOrganization.class);
									for (Object obj : list) {
										SysOrganization ens = (SysOrganization)obj;
										if(ens.getId().equalsIgnoreCase(en.getId())){
											//是自己
											mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getOrgname()));//迭代传给前台key已实体ID++,,count区分当前选中节点
											this.orgAddPostion(mapList, ens.getId(), isRoot, positionId, id);
										}else{
											mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getOrgname()));//迭代传给前台key已实体ID_count区分其他节点
										}
									}
									i++;
								}
							}
						}
					}
				}
			}
		}else if("risk".equalsIgnoreCase(type)){	//风险导航
			//通过ID找到对应实体
			Risk en = (Risk) this.findRiskObjectById(id, Risk.class);
			
			if(en != null){
				if(en.getParent() != null){//如果不是根节点的情况
					
					list = this.findRiskObjectListSome(companyId, en.getParent().getId(), false, "1", "rbs", Risk.class);
					for (Object obj : list) {
						Risk ens = (Risk)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					i++;
					
					while (true) {
						//通过当前节点parentId找上级别节点
						en = (Risk)this.findRiskObjectById(en.getParent().getId(), Risk.class);
//						if(list == null){
//							en = (Risk)this.findRiskObjectById(en.getParent().getId(), Risk.class);
//						}else{
//							en = (Risk)this.findRiskObjectById(((Risk)list.get(0)).getParent().getId(), Risk.class);
//						}
						
						if(en != null){
							//如果是根的情况存储,并退出
							if(en.getParent() == null){
								list = this.findRiskObjectListSome(companyId, null, true, "1", "rbs", Risk.class);
								for (Object obj : list) {
									Risk ens = (Risk)obj;
									if(ens.getId().equalsIgnoreCase(en.getId())){
										//是自己
										mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
									}else{
										mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
									}
								}
								countroot = "1";	//找到根节点，标识一下
								break;
							}
							
							//不是根的情况
							list = this.findRiskObjectListSome(companyId, en.getParent().getId(), false, "1", "rbs", Risk.class);
							for (Object obj : list) {
								Risk ens = (Risk)obj;
								if(ens.getId().equalsIgnoreCase(en.getId())){
									//是自己
									mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
								}else{
									mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
								}
							}
							i++;		//标识节点的侧层数
						}
					}
				}else{
					list = this.findRiskObjectListSome(companyId, null, true, "1", "rbs", Risk.class);
					for (Object obj : list) {
						Risk ens = (Risk)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					countroot = "1";
				}
			}
		}else if("process".equalsIgnoreCase(type)){	//流程导航
			//通过ID找到对应实体
			Process en = (Process) this.findProcessObjectById(id, Process.class);
			
			if(en != null){
				if(en.getParent() != null){//如果不是根节点的情况
					
					list = this.findProcessObjectListSome(companyId, en.getParent().getId(), false, Process.class);
					for (Object obj : list) {
						Process ens = (Process)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					i++;
					
					while (true) {
						//通过当前节点parentId找上级别节点
						if(list == null || list.size() == 0){
							en = (Process)this.findProcessObjectById(en.getParent().getId(), Process.class);
						}else{
							en = (Process)this.findProcessObjectById(((Process)list.get(0)).getParent().getId(), Process.class);
						}
						
						if(en != null){
							//如果是根的情况存储,并退出
							if(en.getParent() == null){
								list = this.findProcessObjectListSome(companyId, null, true, Process.class);
								for (Object obj : list) {
									Process ens = (Process)obj;
									if(ens.getId().equalsIgnoreCase(en.getId())){
										//是自己
										mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
									}else{
										mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
									}
								}
								countroot = "1";
								break;
							}
							
							//不是根的情况
							list = this.findProcessObjectListSome(companyId, en.getParent().getId(), false, Process.class);
							for (Object obj : list) {
								Process ens = (Process)obj;
								if(ens.getId().equalsIgnoreCase(en.getId())){
									//是自己
									mapList.add(this.getMap(ens.getId() + "++,," +  i + "my", ens.getName()));//迭代传给前台key已实体ID++,,count区分当前选中节点
								}else{
									mapList.add(this.getMap(ens.getId() + "_" +  i, ens.getName()));//迭代传给前台key已实体ID_count区分其他节点
								}
							}
							i++;
						}
					}
				}else{
					list = this.findProcessObjectListSome(companyId, null, true, Process.class);
					for (Object obj : list) {
						Process ens = (Process)obj;
						if(ens.getId().equalsIgnoreCase(en.getId())){
							//是自己
							mapList.add(this.getMap(ens.getId() + "++,,root" + "my", ens.getName()));//传给前台key已实体ID++,,count区分当前选中节点
						}else{
							mapList.add(this.getMap(ens.getId() + "_root", ens.getName()));//传给前台key已实体ID_count区分其他节点
						}
					}
					countroot = "1";
				}
			}
		}
		
		result.put("result", mapList);
		result.put("countroot", countroot);
		result.put("count", i);
		
		return result;
	}
	
	private Map<String, String> getMap(String id, String name){
		Map<String, String> map=new HashMap<String, String>();
		map.put("id", id);
		map.put("name", name);
		return map;
	}
	
	public Object findObjectById(String id, Class<?> obj){
        return o_navigationBarsDAO.getSession().createCriteria(obj).add(Property.forName("id").eq(id))
                .add(Property.forName("deleteStatus").eq(true)).setFetchMode("status", SELECT).uniqueResult();
    }
	
	public Object findObjectById(String id, Class<?> obj, String type){
		if("org".equalsIgnoreCase(type)){
			return o_navigationBarsDAO.getSession().createCriteria(obj).add(Property.forName("id").eq(id)).uniqueResult();
		}
		return null;
    }
	
	public Object findObjectById(String id, String isKpiCategory, Class<?> obj){
        return o_navigationBarsDAO.getSession().createCriteria(obj).add(Property.forName("id").eq(id))
                .add(Property.forName("deleteStatus").eq(true)).add(Property.forName("isKpiCategory").eq(isKpiCategory))
                .setFetchMode("status", SELECT).uniqueResult();
    }
	@SuppressWarnings("unchecked")
	public List<Object> findObjectListSome(String companyId ,String parentId, boolean isParentIdNull, String deleteStatus, Class<?> obj) {
		List<Object> categoryList = null;
		Criteria criteria =  o_navigationBarsDAO.getSession().createCriteria(obj);
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		
		if(isParentIdNull){
			criteria.add(Restrictions.isNull("parent"));
		}else{
			criteria.add(Restrictions.eq("parent.id", parentId));
		}
		
		criteria.add(Restrictions.eq("deleteStatus", deleteStatus));
		criteria.addOrder(Order.asc("id"));
		criteria.addOrder(Order.asc("name"));
		
		categoryList = criteria.list();
		
		return categoryList;
	}
	/**
	 * 用于风险导航
	 * @param id
	 * @param obj
	 * @return
	 */
	public Object findRiskObjectById(String id, Class<?> obj){
        return o_navigationBarsDAO.getSession().createCriteria(obj).add(Property.forName("id").eq(id))
                .add(Property.forName("deleteStatus").eq("1")).setFetchMode("status", SELECT).uniqueResult();
    }
	/**
	 * 用于风险导航
	 * @author zhengjunxiang
	 */
	@SuppressWarnings("unchecked")
	public List<Object> findRiskObjectListSome(String companyId ,String parentId, boolean isParentIdNull, String deleteStatus, String isRiskClass,Class<?> obj) {
		List<Object> categoryList = null;
		Criteria criteria =  o_navigationBarsDAO.getSession().createCriteria(obj);
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		
		if(isParentIdNull){
			criteria.add(Restrictions.isNull("parent"));
		}else{
			criteria.add(Restrictions.eq("parent.id", parentId));
		}
		
		criteria.add(Restrictions.eq("deleteStatus", deleteStatus));
		criteria.add(Restrictions.eq("isRiskClass", isRiskClass));
		criteria.addOrder(Order.asc("id"));
		criteria.addOrder(Order.asc("name"));
		
		categoryList = criteria.list();
		
		return categoryList;
	}
	/**
	 * 用于流程导航
	 * @param id
	 * @param obj
	 * @return
	 */
	public Object findProcessObjectById(String id, Class<?> obj){
        return o_navigationBarsDAO.getSession().createCriteria(obj).add(Property.forName("id").eq(id))
                .setFetchMode("status", SELECT).uniqueResult();
    }
	/**
	 * 用于流程导航
	 * @author zhengjunxiang
	 */
	@SuppressWarnings("unchecked")
	public List<Object> findProcessObjectListSome(String companyId ,String parentId, boolean isParentIdNull, Class<?> obj) {
		List<Object> categoryList = null;
		Criteria criteria =  o_navigationBarsDAO.getSession().createCriteria(obj);
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		
		if(isParentIdNull){
			criteria.add(Restrictions.isNull("parent"));
		}else{
			criteria.add(Restrictions.eq("parent.id", parentId));
		}
		criteria.addOrder(Order.asc("id"));
		criteria.addOrder(Order.asc("name"));
		
		categoryList = criteria.list();
		
		return categoryList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> findObjectListSome(String companyId ,String parentId, boolean deleteStatus, boolean isParentIdNull, Class<?> obj) {
		List<Object> categoryList = null;
		Criteria criteria =  o_navigationBarsDAO.getSession().createCriteria(obj);
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		
		if(isParentIdNull){
			criteria.add(Restrictions.isNull("parent"));
		}else{
			criteria.add(Restrictions.eq("parent.id", parentId));
		}
		
		criteria.add(Restrictions.eq("deleteStatus", deleteStatus));
		criteria.addOrder(Order.asc("id"));
		criteria.addOrder(Order.asc("name"));
		
		categoryList = criteria.list();
		
		return categoryList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> findObjectListSome(String companyId ,String parentId, boolean isParentIdNull, Class<?> obj) {
		List<Object> categoryList = null;
		Criteria criteria =  o_navigationBarsDAO.getSession().createCriteria(obj);
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		
		if(isParentIdNull){
			criteria.add(Restrictions.isNull("parentOrg"));
		}else{
			criteria.add(Restrictions.eq("parentOrg.id", parentId));
		}
		
		criteria.addOrder(Order.asc("id"));
		criteria.addOrder(Order.asc("orgname"));
		
		categoryList = criteria.list();
		
		return categoryList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> findObjectListSome(String companyId, Class<?> obj) {
		List<Object> categoryList = null;
		Criteria criteria =  o_navigationBarsDAO.getSession().createCriteria(obj);
		if(StringUtils.isNotBlank(companyId)){
			criteria.add(Restrictions.eq("company.id", companyId));
		}
		
		criteria.addOrder(Order.asc("id"));
		criteria.addOrder(Order.asc("orgname"));
		
		categoryList = criteria.list();
		
		return categoryList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> findObjectListSome(Class<?> obj) {
		List<Object> categoryList = null;
		Criteria criteria =  o_navigationBarsDAO.getSession().createCriteria(obj);
		criteria.addOrder(Order.asc("id"));
		
		categoryList = criteria.list();
		
		return categoryList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> findObjectListSome(String companyId ,String parentId, String isKpiCategory, boolean deleteStatus, boolean isParentIdNull, Class<?> obj) {
		List<Object> categoryList = null;
		Criteria criteria =  o_navigationBarsDAO.getSession().createCriteria(obj);
		criteria.add(Restrictions.eq("company.id", companyId));
		if(isParentIdNull){
			criteria.add(Restrictions.isNull("parent"));
		}else{
			criteria.add(Restrictions.eq("parent.id", parentId));
		}
		criteria.add(Restrictions.eq("isKpiCategory", isKpiCategory));
		criteria.add(Restrictions.eq("deleteStatus", deleteStatus));
		criteria.addOrder(Order.asc("id"));
		criteria.addOrder(Order.asc("name"));
		
		categoryList = criteria.list();
		
		return categoryList;
	}
	
	private void orgAddPostion(List<Map<String, String>> mapList, String orgId, boolean isRoot, String positionId, String positionOrgId){
		if(positionOrgId.equalsIgnoreCase(orgId)){
			List<SysPosition> positionList = o_orgPositionBO.findPositionByOrgIds(orgId);
			int g = 0;
			if(!isRoot){
//				for (SysPosition sysPosition : positionList) {
//					if(g == 0){
//						mapList.add(this.getMap(sysPosition.getId() + "++gw" +  g + "", sysPosition.getPosiname()));
//					}else{
//						mapList.add(this.getMap(sysPosition.getId() + "__gw" +  g, sysPosition.getPosiname()));
//					}
//					g++;
//				}
			}else{
				for (SysPosition sysPosition : positionList) {
					if(positionId.equalsIgnoreCase(sysPosition.getId())){
						mapList.add(this.getMap(sysPosition.getId() + "++gw" +  g + "", sysPosition.getPosiname()));
					}else{
						mapList.add(this.getMap(sysPosition.getId() + "__gw" +  g, sysPosition.getPosiname()));
					}
					g++;
				}
			}
		}
	}
}