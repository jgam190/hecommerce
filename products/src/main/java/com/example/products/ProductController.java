package com.example.products;

import com.example.products.model.Produto;
import com.example.products.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller REST para o serviço de Catálogo de Produtos.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProdutoRepository produtoRepository;

    public ProductController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> getAllProdutos(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Page<Produto> pageResult = produtoRepository.findAll(PageRequest.of(page - 1, size));
        return ResponseEntity.ok(pageResult.getContent());
    }

    @GetMapping("/active")
    public ResponseEntity<Page<Produto>> getAllAtivos(Pageable pageable) {
        return ResponseEntity.ok(produtoRepository.findByAtivoTrue(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProdutoById(@PathVariable String id) {
        Produto produto = produtoRepository.findById(id)
                .orElse(null);
        if (produto == null || !produto.isAtivo()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Produto>> searchProdutos(@RequestParam String nome) {
        List<Produto> resultados = produtoRepository.findByNomeContainingIgnoreCase(nome);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Produto>> findByPrecoRange(@RequestParam BigDecimal menor,
                                                          @RequestParam BigDecimal maior) {
        return ResponseEntity.ok(produtoRepository.findByPrecoBetween(menor, maior));
    }

    @PostMapping
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (produto.getEstoque() == null || produto.getEstoque() < 0) {
            return ResponseEntity.badRequest().build();
        }
        Produto salvo = produtoRepository.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable String id, @RequestBody Produto produtoAtualizado) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produto.setNome(produtoAtualizado.getNome());
                    produto.setDescricao(produtoAtualizado.getDescricao());
                    produto.setPreco(produtoAtualizado.getPreco());
                    produto.setEstoque(produtoAtualizado.getEstoque());
                    produto.setAtivo(produtoAtualizado.isAtivo());
                    Produto salvo = produtoRepository.save(produto);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable String id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        produtoRepository.findById(id).ifPresent(produto -> {
            produto.setAtivo(false);
            produtoRepository.save(produto);
        });
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Produto> restoreProduto(@PathVariable String id) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produto.setAtivo(true);
                    Produto salvo = produtoRepository.save(produto);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
