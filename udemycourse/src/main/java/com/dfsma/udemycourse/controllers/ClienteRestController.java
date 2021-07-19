package com.dfsma.udemycourse.controllers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dfsma.udemycourse.models.entity.Cliente;
import com.dfsma.udemycourse.models.services.IClienteService;




@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ClienteRestController {

    @Autowired
    private IClienteService clienteService;

    @GetMapping("/clientes")
    public List<Cliente> index(){
        return  clienteService.findAll();
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> show(@PathVariable Long id){

        Cliente cliente;
        Map<String, Object> response = new HashMap<>();

        try {
            cliente = clienteService.findById(id);

        }catch (DataAccessException e){
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(cliente == null){
            response.put("mensaje", "El cliente con el id: ".concat(id.toString().concat(" No existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result){

        Cliente clienteNuevo;
        Map<String, Object> response = new HashMap<>();

        if(result.hasErrors()) {
             /*
            List<String> errors = new ArrayList<>();

            for (FieldError err: result.getFieldErrors()){
                errors.add("El campo '" + err.getField() + "' " +err.getDefaultMessage());
            }
            */
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() +"' "+ err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            clienteNuevo = clienteService.save(cliente);

        }catch (DataAccessException e){
            response.put("mensaje", "Error al realizar la insercion en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "El cliente ha sido creado con exito.");
        response.put("cliente", clienteNuevo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id){

        Cliente clienteActual = clienteService.findById(id);
        Cliente clienteUpdated;
        Map<String, Object> response = new HashMap<>();

        if(result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() +"' "+ err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(clienteActual == null){
            response.put("mensaje", "Error: no se pudo editar el cliente con el id: ".concat(id.toString().concat(" No existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try{
            clienteActual.setNombre(cliente.getNombre());
            clienteActual.setApellido(cliente.getApellido());
            clienteActual.setEmail(cliente.getEmail());
            clienteActual.setCreateAt(cliente.getCreateAt());

            clienteUpdated = clienteService.save(clienteActual);

        }catch (DataAccessException e){
            response.put("mensaje", "Error al realizar la modificacion en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente fue modificado con exito.");
        response.put("cliente", clienteUpdated);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();

        try{
            clienteService.delete(id);
        }catch (DataAccessException e){
            response.put("mensaje", "Error al realizar la eliminacion del cliente en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "El cliente con el id: ".concat(id.toString().concat(" fue eliminado con exito")));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
