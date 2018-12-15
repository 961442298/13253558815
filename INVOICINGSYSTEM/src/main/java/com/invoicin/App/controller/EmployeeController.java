package com.invoicin.App.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.booleanThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.catalina.filters.AddDefaultCharsetFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.invoicin.App.dao.EmployeeDao;
import com.invoicin.App.dao.RoleDao;
import com.invoicin.App.entity.Employee;
import com.invoicin.App.entity.EmployeeSerch;
import com.invoicin.App.entity.Role;
import com.invoicin.App.service.EmployeeService;


@CrossOrigin
@RestController
@RequestMapping("/Employee")

public class EmployeeController {
	@Autowired
	private EmployeeDao dao;
	@Autowired
	private RoleDao rdao;
	@Autowired
	private EmployeeService employeeService;
	Map<String, Object> map = new HashMap<String, Object>();
	
	/**
	 * http://localhost:8090/Employee/ShowEmployeeRole
	 * 显示角色
	 * @return
	 */
	@RequestMapping("/ShowEmployeeRole")
	public Object ShowEmployeeRole(Integer employeeId) {
		List<Role> rList = rdao.findAll();
		List<Role> eRoles =new ArrayList();
		List<Integer> list = dao.queryRoleIdByEid(employeeId);
		for (Integer integer : list) {
			eRoles.add(rdao.findOne(integer));
		}
		rList.removeAll(eRoles);
		map.put("data1", rList);
		map.put("data2", eRoles);
		return map;
	}
	
	/**
	 * http://localhost:8090/Employee/SetEmployeeRole
	 * 给员工设置角色
	 * @return
	 */
	@RequestMapping("/SetEmployeeRole")
	public Object SetEmployeeRole(String rightid,Integer employeeId) {
		Employee employee = dao.findOne(employeeId);
		System.out.println(rightid+"6666");
		List<Role> rList = new ArrayList();
		if(rightid!=null&&rightid!="") {
		String a[] = rightid.split(",");
		
		for (int i = 0; i < a.length; i++) {
			rList.add(rdao.findOne(Integer.parseInt(a[i])));
		}}
		employee.setRole(rList);
		if(dao.save(employee)!=null) {
			map.put("boo", true);
		}else {
			map.put("boo", false);
		}
		return map;
	}
	/**
	 * http://localhost:8090/Employee/all
	 * @return
	 */
	@RequestMapping("/all")
	public Object all() {
		return dao.findAll();
	}
	
	/**
	 * 登录
	 * http://localhost:8090/Employee/login
	 * @param login
	 * @param pwd
	 * @param code
	 * @return
	 */
	@RequestMapping("/login")
	public Object login(String login, String pwd, String code) {// login 卡号 pwd 密码
		List<Role> lists = new ArrayList<Role>();
		Integer EmployeeCard = Integer.parseInt(login);
		Employee employee = dao.querylogin(EmployeeCard, pwd);
		Employee e1 = dao.findByEmployeeCard(login);
		if (employee != null&&employee.getEmployeePassErrorCount()<5&&employee.getEmployeeStatic().equals("正常使用")) {
			map.put("employeeId", employee.getEmployeeId());
			map.put("name", employee.getEmployeeName());
			map.put("Status", "ok");
			map.put("Text", "登录成功<br /><br />欢迎回来");
		}else{
			if(e1==null) {
				map.put("Status", "Erro");
				map.put("Erro", "账号名或密码或验证码有误");
				
			}else if(employee.getEmployeeStatic().equals("限制使用")) {
				map.put("Status", "Erro");
				map.put("Erro", "账号被限制使用");
			}else if(e1.getEmployeePassErrorCount()<5) {
				e1.setEmployeePassErrorCount(e1.getEmployeePassErrorCount()+1);
				map.put("Status", "Erro");
				map.put("Erro", "账号名或密码或验证码有误");
			} else{
				e1.setEmployeeStatic("限制使用");
				map.put("Status", "Erro");
				map.put("Erro", "密码错误5次,已被锁定");
			}
			dao.save(e1);
		}
		return map;
	}

	/**
	 * 员工添加
	 * http://localhost:8090/Employee/add
	 * @param e
	 * @return
	 */
	@RequestMapping("/add")
	public Object add(Employee e) {
		map.put("boo", false);
		map.put("message", "添加失败!");
		List<Employee> e1 = employeeService.findByEmployeeName(e.getEmployeeCard());
		if(e1.size()>0)  return map;
		e.setEmployeePassErrorCount(0);
		Employee employee = dao.save(e);
		if (employee != null) {
			map.put("boo", true);
		} 
		return map;
	}

	/**
	 * 员工修改
	 * http://localhost:8090/Employee/add
	 * @param e
	 * @return
	 */
	@RequestMapping("/update")
	public Object update(Employee e) {
		List<Employee> e1 = employeeService.findByEmployeeName(e.getEmployeeCard());
		Employee employee = dao.findOne(e.getEmployeeId());
		employee.setEmployeeCard(e.getEmployeeCard());
		employee.setEmployeeName(e.getEmployeeName());
		employee.setEmployeeSex(e.getEmployeeSex());
		employee.setPhone(e.getPhone());
		employee.setQqnumber(e.getQqnumber());
		System.out.println(employee);
		if (dao.save(employee) != null&&e1.size()<=1) {
			map.put("boo", true);
		} else {
			map.put("boo", false);
			map.put("message", "修改失败!");
		}
		return map;
	}

	/**
	 * 删除员工
	 * http://localhost:8090/Employee/delete
	 * @param employeeId
	 * @return
	 */
	@RequestMapping("/delete")
	public Object delete(Integer employeeId) {
		return employeeService.delete(employeeId);
	}

	
	/**
	 * 删除员工
	 * http://localhost:8090/Employee/deleteChecked
	 * @param employeeId
	 * @return
	 */
	@RequestMapping("/deleteChecked")
	public Object deleteChecked(String  userIds) {
		int s = 0 ;
		int c = 0 ;
		String a[] = userIds.split(",");
		for (int i = 0; i < a.length; i++) {
		  boolean bo = employeeService.delete(Integer.parseInt(a[i]));
		  if(bo) {
			 s++; 
		  }else {
			 c++; 
		  }
		}
		map.put("message", "失败了"+c+"个,成功了"+s);
		return map;
	}
	
	/**
	 * tree
	 * 左侧树
	 * http://localhost:8090/Employee/lock
	 * @param e
	 * @return
	 */
	@RequestMapping("/lock")
	public Object lock(Integer employeeId ,Integer s,String newPwd) {
		Employee e = dao.findOne(employeeId);
		map.put("boo", true);
		if(s==1) {
			e.setEmployeeStatic("限制使用");
			if(dao.save(e)==null) {
				map.put("message", "操作失败");
				map.put("boo", false);
			}	
		}else if(s==2) {
			e.setEmployeeStatic("正常使用");
			if(dao.save(e)==null) {
				map.put("message", "操作失败");
				map.put("boo", false);
			}	
		}else {
			e.setEmployeePassWork(newPwd);
			if(dao.save(e)==null) {
				map.put("message", "操作失败");
				map.put("boo", false);
			}	
		}
		
		
		return map;
	}
	/**
	 * 员工的带条件分页查询
	 * http://localhost:8090/Employee/queryBylike
	 * @param employeeSerch
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/queryBylike")
	public Map<String, Object> QueryBylike(EmployeeSerch employeeSerch, Integer page, Integer limit) {
		Page<Employee> pa = employeeService.queryByDynamicSQLPage(employeeSerch, page - 1, limit);
		map.put("data", pa.getContent());
		map.put("count", pa.getTotalElements());
		map.put("code", 0);
		return map;
	}

	/**
	 * 给员工设置角色
	 * http://localhost:8090/Employee/SetRole
	 * @param角色id集合 roleids
	 * @param员工id employeeId
	 * @return
	 */
	@RequestMapping("/SetRole")
	public Map<String, Object> SetRole(String roleids, Integer employeeId) {
		Employee employee = dao.findOne(employeeId);
		List<Role> roles = new ArrayList();
		String a[] = roleids.split(",");
		if (roleids == "")
		  {
			employee.setRole(roles);
			dao.save(employee);
			map.put("boo", false);
			return map;
		  }
		for (int i = 0; i < a.length; i++)
		 {
			Role m = rdao.findOne(Integer.parseInt(a[i]));
			roles.add(m);
		 }
		employee.setRole(roles);
		Employee r = dao.save(employee);
		
		if (r != null) {
			map.put("boo", true);
		} else {
			map.put("boo", false);
			map.put("message", "设置失败");
		}
		return map;
	}
	
}
