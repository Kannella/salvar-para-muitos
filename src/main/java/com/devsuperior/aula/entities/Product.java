package com.devsuperior.aula.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;

    //Dentro da classe Product eu tenho uma referencia para uma colecao de categorias,
    //so que a colecao no caso do muitos para muitos eu tenho que colocar o set para evitar duplicidade de linhas iguais
    @ManyToMany //Indicando que o atributo abaixo faz parte de um Relacionamento muito para muitos
    @JoinTable(name = "tb_product_category", // o nome da tabela do meio vai ser tb_product_category
            joinColumns = @JoinColumn(name = "product_id"), //vai juntar o product_id na nova tabela. Eh o product_id pq eh na classe que voce esta
            inverseJoinColumns = @JoinColumn(name = "category_id")) //vai juntar o category_id na nova tabela tambem
    private Set<Category> categories = new HashSet<>(); //Tenho uma colecao de categorias dentro de produtos

    public Product() {
    }

    public Product(Long id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Set<Category> getCategories() {
        return categories;
    }
}
