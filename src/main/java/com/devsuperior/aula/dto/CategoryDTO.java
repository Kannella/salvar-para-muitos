package com.devsuperior.aula.dto;

import com.devsuperior.aula.entities.Category;

public class CategoryDTO {

    private Long id;
    private String name;

    // O Jackson precisa de um construtor vazio para instanciar o objeto antes de preencher os campos.
    public CategoryDTO() {

    }

    public CategoryDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }


    public CategoryDTO(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
