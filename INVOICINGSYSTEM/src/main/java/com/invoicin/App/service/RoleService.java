package com.invoicin.App.service;

import java.util.List;

import org.hibernate.sql.Delete;
import org.springframework.data.domain.Page;
import com.invoicin.App.entity.Role;

public interface RoleService {
	public Page<Role>  queryByDynamicSQLPage(String roleName ,Integer page, Integer size) ;
	List<Role> findByRoleName(String roleName);
	int QueryByroleId(Integer roleId);
}

