package com.dao;

import com.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created on 2017/08/02.
 */
public interface CustomerDao extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {

}
