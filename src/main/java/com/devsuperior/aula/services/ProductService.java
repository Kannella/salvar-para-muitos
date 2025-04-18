package com.devsuperior.aula.services;

import com.devsuperior.aula.dto.CategoryDTO;
import com.devsuperior.aula.dto.ProductDTO;
import com.devsuperior.aula.entities.Category;
import com.devsuperior.aula.entities.Product;
import com.devsuperior.aula.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service //registrando como um componente do sistema de Servico
public class ProductService {

    //Injetando (colocando como dependencia) o ProductRepository
    @Autowired
    private ProductRepository repository; //para evitar de termos esse cara como null colocamos o autowired pra fazer a injecao de dependecia automatica e nao apontar para o null

    //Metodo que me retorna como resposta um ProductDTO chamado insert que recebe um ProductDTO
    //tudo isso abaixo para criar uma estrutura de produto associado com suas categorias
    public ProductDTO insert(ProductDTO dto) {
        //Aprendemos que o objeto JSON recebido na requisicao, o Spring tem que instanciar o objeto java a partir do JSON
        //Convertendo o objeto ProductDTO em Product para salvar no banco com o JPA
        Product entity = new Product(); //Criei dentro da memoria um objeto Product que vai ter id nome e preco e uma lista de categories
        //Copiando os dados do DTO para entidade
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        //Criando as categorias e aninhar
        //Como eu tenho uma lista de CategoryDTO no meu ProductDTO eu tenho que percorrer essa lista pra criar as entidades das categorias
        for(CategoryDTO catDTO : dto.getCategories()) {
            //Pra cada elemento CategoryDTO (nomeado como catDTO) dentro da lista Categories do ProductDTO (nomeado como dto) eu vou instanciar (criar) uma categoria e associar com o meu produto
            Category cat = new Category();
            cat.setId(catDTO.getId());
            // cat.setName(catDTO.getName()); nao funciona pois o cat nao eh no caso uma entidade monitorada pelo JPA
            entity.getCategories().add(cat); //pega a lista de categorias e adiciono o objeto cat da iteracao nessa lista
        }

        //Salvando essa entidade no banco e armazenando ela na variavel entity
        entity = repository.save(entity); // aqui vai dar NullPointerException se não tiver sido injetado corretamente a dependencia ProductRepository no construtor de ProductService.
        // Entao na hora que essa classe ProductService for criada vaiu injetar automaticamente uma instância do ProductRepository no campo private ProductRepository repository;

        //retorna o dto correspondente dessa entidade salva
        return new ProductDTO(entity);


    }
}

/*
Por que você precisa do @Autowired?
1. Spring precisa saber que ele deve "injetar" o objeto
O Spring é responsável por criar e gerenciar os "Beans" — objetos que ele instancia e mantém vivos dentro de seu container.
    O que você fez aqui:

        @Autowired
        private ProductRepository repository;

    é dizer para o Spring:

    “Quando essa classe (ProductService) for criada, injete automaticamente uma instância do ProductRepository nesse campo.”

2. O Spring nunca usa new ProductService()
Você não está criando seus serviços manualmente com new. Quem faz isso é o próprio Spring. E para que ele
saiba como montar os objetos que seus serviços usam, ele precisa da injeção de dependência via @Autowired (ou via construtor).

Quando o ProductService é criado?
Durante o boot da aplicação, o Spring escaneia os pacotes e:
1. Encontra a anotação @Service na sua classe ProductService.
2. Reconhece que ela é um bean (componente) do Spring e deve ser gerenciada no container de IoC (Inversão de Controle).
3. Ao registrar esse bean, o Spring vê que ProductService depende de um ProductRepository.
4. Ele resolve essa dependência automaticamente, criando (ou reutilizando) um bean (componente) do tipo ProductRepository. So que se o Spring nao sabe que o ProductRepository eh uma dependencia a partir do @Autowired essa dependencia fica como null
5. Por fim, instancia ProductService passando ProductRepository no construtor (ou setando no atributo, se for @Autowired direto).

1. Spring inicia -> escaneia pacotes → acha @Service
             |
             ↓
2. "Preciso criar um ProductService"
             |
             ↓
3. "Opa, ele depende de um ProductRepository!"
             |
             ↓
4. "Já tenho um ProductRepository (por ser um @Repository / JpaRepository), vou injetar"
             |
             ↓
5. Cria ProductService com a dependência resolvida

Fluxo se o ProductRepository não existir
1. Spring inicia -> escaneia pacotes → acha @Service na classe ProductService
             |
             ↓
2. "Preciso criar um ProductService"
             |
             ↓
3. "Opa, ele depende de um ProductRepository!"
             |
             ↓
4. "Putz... não achei nenhum bean do tipo ProductRepository"
             |
             ↓
5. Erro: UnsatisfiedDependencyException

Ou seja:
O Spring vê que precisa de um ProductRepository.
Procura um bean desse tipo no contexto da aplicação.
Não encontra, então lança uma exceção antes mesmo da aplicação subir.

Como evitar esse erro no caso de ProductService
Você precisa garantir que:
    A interface ProductRepository existe.
    Ela está no mesmo pacote ou subpacote da sua classe @SpringBootApplication ou em pacotes escaneados.
    Ela estende uma interface como JpaRepository, CrudRepository, etc.
    Voce declara a dependencia da classe e faz uma injecao de dependencia para dizer ao Spring como ele deve "colocar" essa dependência dentro da sua classe ProductService das seguintes maneiras:
     1. Injeção por campo (mais simples, mas menos testável)
    @Service
    public class ProductService {

        @Autowired
        private ProductRepository repository;

        // métodos...
    }
    📌 Com isso:
    O Spring injeta automaticamente o ProductRepository nesse campo logo após construir o objeto ProductService.
    Você só precisa anotar com @Autowired e pronto.

    🔁 2. Injeção por construtor (recomendado)
    @Service
    public class ProductService {

        private final ProductRepository repository;

        public ProductService(ProductRepository repository) {
            this.repository = repository;
        }

        // métodos...
    }
    📌 Aqui:
    O Spring vê que o único construtor da classe exige um ProductRepository.
    Ele resolve a dependência automaticamente, sem precisar do @Autowired no construtor (a partir do Spring 4.3+).
    Isso torna sua classe mais fácil de testar (você pode passar mocks no construtor) e mais segura (campos final).


---------------------------------------------------------------------------------------
Sitaucao atual
    for (CategoryDTO catDTO : dto.getCategories()) {
        Category cat = new Category();
        cat.setId(catDTO.getId());
        cat.setName(catDTO.getName()); // ← aqui você define o nome
        entity.getCategories().add(cat);
    }

    Depois salva:

    entity = repository.save(entity);
    return new ProductDTO(entity); // ← aqui transforma de volta em DTO

Mas o cat.getName() vem null no retorno, certo?
Mesmo que você tenha setado cat.setName(catDTO.getName())!

Explicação
Quando você cria uma entidade Category nova, só com setId(...), sem buscar ela do banco, o JPA entende que:
    Essa entidade pode não existir no banco ainda.
    Quando você faz repository.save(product), ele apenas usa o id da categoria para montar o relacionamento — ele não tenta buscar os detalhes da categoria (como o nome).

Ou seja:
    O JPA não está populando o nome da categoria porque você só forneceu o ID e não associou a entidade real vinda do banco.

Como resolver?
Você precisa buscar do banco uma referência gerenciada à categoria.
Usando getReferenceById(...) (ou findById(...)):
    for (CategoryDTO catDTO : dto.getCategories()) {
        Category cat = categoryRepository.getReferenceById(catDTO.getId());
        entity.getCategories().add(cat);
    }


Abordagem	                            O que acontece	                                                                                                            name vai estar disponível?
new Category(); setId(...)	            Cria objeto transiente com id, mas não está sincronizado com o banco (essa entidade nao esta sendo monitorada pelo JPA)	    Não (a menos que você set explicitamente setName)
getReferenceById(id)	                Cria um proxy gerenciado — o Spring JPA pode carregar os dados conforme necessário	                                        Sim
findById(id).get()	                    Busca o objeto todo no banco                                                                                                Sim

getReferenceById(...) é mais performático que findById(...) porque só instancia o proxy, e o conteúdo real só é buscado se necessário.


Conclusao
Quando você faz .add(new Category(id)), você está só dizendo: “JPA, relacione a entidade Product com esse ID da entidade Category”.
Mas quando você quer dados completos (como o name), você precisa buscar a entidade real — via getReferenceById() ou findById().

Então, ao fazer um objeto transiente, você apenas pega o JSON recebido pelo corpo da requisição e relaciona cada categoria com a entidade.
Se você quisesse pegar os dados dessa categoria, você tinha que buscar a entidade real do banco (usando getReferenceById ou findById), e fazer ela ser monitorada pelo JPA ao trazer uma referência gerenciada ou uma entidade carregada.

Basicamente
Category cat = new Category();
cat.setId(2L); // apenas isso
Não está monitorado pelo JPA.
Serve só como marcador de relacionamento (FK).

Ou
@Autowired
private CategoryRepository categoryRespository; // O Spring injeta automaticamente o CategoryRepository nesse campo logo após construir o objeto ProductService. Ai sim conseguimos usar dos metodos certinho do CategoryRepository sem tomar um null pointer

Category cat = categoryRepository.getReferenceById(catDto.getId());

O JPA agora controla esse objeto cat
Ele pode carregar os dados automaticamente se necessário.
Quando você salva o Product, o relacionamento é salvo corretamente e os dados da Category podem ser acessados sem vir null.

Quando usar o quê?
Situação	                                                            Solução
Só quer salvar o relacionamento (não precisa de nome, etc)	            new Category(); setId(...) já basta
Quer retornar a entidade com os dados preenchidos (ex: name)	        getReferenceById(...) ou findById(...)


CONCLUINDO
Para acessar os métodos do repository de uma entidade, você precisa declarar o respectivo repository como dependência em uma classe
(ex: um @Service) e usá-lo para interagir com o banco de dados.
Se você quiser acessar os dados de uma entidade específica, você chama repository.getReferenceById(3) ou repository.findById(3) e
armazena o resultado em uma variável do tipo da entidade (ex: Category). Depois, acessa os dados com os getters da entidade (getName(), getId(), etc).

Exemplo concreto:
@Autowired
private CategoryRepository categoryRepository;

public void exemplo() {
    Category cat = categoryRepository.getReferenceById(3); // objeto gerenciado pelo JPA
    System.out.println(cat.getName()); // o nome só estará disponível se o proxy for resolvido
}

Diferença sutil:
    getReferenceById(id) → retorna um proxy (lazy load), o dado pode vir null até ser carregado (ex: se o EntityManager
    já estiver fechado, pode dar erro).
    findById(id).get() → faz a query no banco na hora e retorna a entidade com os dados já carregados.

Moral da história:
    Você nunca acessa diretamente o banco pela entidade (ex: Category) (Voce
    pode ate trafegar entidades entre a camada de servico e de acesso a dados
    mas nunca pode acessar diretamente os dados via entidade), e
    sim pelo repositório da entidade (CategoryRepository), que é injetado como dependência.
*/
