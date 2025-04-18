package com.devsuperior.aula.controllers;

import com.devsuperior.aula.services.ProductService;
import com.devsuperior.aula.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    //Desclarando uma dependencia de ProductController, no caso ele depende de ProductService
    @Autowired
    //Quando essa classe (ProductController) for criada, injete automaticamente uma instância do ProductService nesse campo.
    private ProductService service; //declarei a classe ProductService como dependencia

    //Recebe como corpo da requisicao o ProductDTO
    //Vai casar com o ProductDTO recebido como parametro abaixo, vai fazer os processos do metodo e vai criar o objeto java
    @PostMapping
    public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri(); //gerando o codigo certo de erro
        return ResponseEntity.created(uri).body(dto);
    }

}
