package com.devsuperior.aula.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    //Dentro da classe Category eu tenho uma referencia para uma colecao de produtos,
    // so que a colecao no caso do muitos para muitos eu tenho que colocar o set para evitar duplicidade de linhas iguais

    @ManyToMany(mappedBy = "categories") //Indicando que o atributo abaixo faz parte de um Relacionamento muito para muitos
    private Set<Product> products = new HashSet<>();

    public Category() {
    }

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }
}
