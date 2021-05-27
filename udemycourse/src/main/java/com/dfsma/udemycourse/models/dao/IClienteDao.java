package com.dfsma.udemycourse.models.dao;

import com.dfsma.udemycourse.models.entity.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface IClienteDao extends CrudRepository<Cliente, Long> {

}
