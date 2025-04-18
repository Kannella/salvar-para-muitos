package com.devsuperior.aula.dto;

import com.devsuperior.aula.entities.Category;
import com.devsuperior.aula.entities.Product;

import java.util.ArrayList;
import java.util.List;

//ProductDTO eh um objeto com 4 atributos. Preenchendo normal o id name e price e a lista de categorias o framework Spring
// vai percorrer essa lista do JSON instanciando um CategoryDTO novo para cada categoria (topico 4 dessa classe)
//Portanto vai ser criado os objetos CategoryDTO a partir do JSON recebido e no atributo ProductDTO do tipo lista de vao ter os enderecos e vao apontar para esses objetos CategoryDTO criados e armazenados na lista
//A estrutura na memoria vai ser basicamente: Com base no JSON fornecido sera criado um objeto ProductDTO com seus atributos name id price que aponta para uma lista de CategoryDTO em que seus elementos foram instanciados automaticamente no topico 4.
//-> Se eu tiver um produto com duas categorias, eu vou ter o nome id preco do produto e um atributo do tipo Lista de CategoryDTO apontando para cada uma lista CategoryDTO que contem os dos dois objetos CategoryDTO criados e armazenados nessa lista
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;

    //Tem uma lista de objetos categorias
    //Lembrando a boa pratica: dentro de um dto voce so associa (na forma de atributos) com outro dto e nao com a propria entidade
    //O nome da lista precisa ser igual ao nome do campo categories recebido no json
    private List<CategoryDTO> categories = new ArrayList<>();

    // O Jackson precisa de um construtor vazio para instanciar o objeto antes de preencher os campos.
    public ProductDTO() {

    }

    public ProductDTO(Long id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public ProductDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.price = entity.getPrice();
        //topico 4 explicado la em cima
        for (Category cat : entity.getCategories()) {
            //para cada Category nomeada como cat, dentro do meu entity.getCategories() eu vou adicionar um Category nessa lista de categorias que o Product tem
            categories.add(new CategoryDTO(cat)); //adiciono um novo CategoryDTO a partir da categoria cat que esta na lista da entidade que veio por meio do JSON
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public Double getPrice() {
        return price;
    }
}
