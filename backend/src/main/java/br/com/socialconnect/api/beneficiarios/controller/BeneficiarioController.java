package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.service.BeneficiarioService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/beneficiarios")
public class BeneficiarioController {

    private final BeneficiarioService service;

    public BeneficiarioController(BeneficiarioService service) {
        this.service = service;
    }

    // GET /api/v1/beneficiarios?nome=&cpf=&page=0&size=10&sort=nome,asc
    // - Nome explícito nos @RequestParam e nos @PathVariable: não depende de o
    //   compilador guardar o nome dos parâmetros (-parameters), o que falha
    //   quando a IDE compila sozinha.
    // - @PageableDefault: size 10, como no docs/openapi.yaml (sem ele, o Boot
    //   usaria 20). O sort usa o nome do atributo da Entity (nome, dataCadastro),
    //   não o da coluna.
    // - @ParameterObject: sem ele, o springdoc documenta o Pageable como um JSON
    //   único com "sort": ["string"], e executar esse exemplo no Swagger dá 500.
    // - Page é serializado direto (modo DIRECT, o padrão do Spring Data). O log
    //   avisa uma vez que esse JSON não tem estrutura garantida, mas o modo VIA_DTO
    //   sugerido aninha os totais em "page" e remove first/last, o que quebraria
    //   o PageBeneficiarioResponse do contrato.
    @GetMapping
    public ResponseEntity<Page<BeneficiarioResponseDTO>> listar(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "cpf", required = false) String cpf,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, cpf, pageable));
    }

    @GetMapping("/{idBeneficiario}")
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorId(@PathVariable("idBeneficiario") Long idBeneficiario) {
        return ResponseEntity.ok(service.buscarPorId(idBeneficiario));
    }

    // 201 Created + Location: o recurso novo ganha endereço próprio.
    // Sem o @RequestBody, os campos do JSON chegariam nulos.
    @PostMapping
    public ResponseEntity<BeneficiarioResponseDTO> criar(@RequestBody BeneficiarioRequestDTO dto) {
        BeneficiarioResponseDTO criado = service.criar(dto);
        URI location = URI.create("/api/v1/beneficiarios/" + criado.idBeneficiario());
        return ResponseEntity.created(location).body(criado);
    }

    // PUT: substituição total (campo ausente no JSON vira null).
    // 200 com o recurso atualizado, como no contrato.
    @PutMapping("/{idBeneficiario}")
    public ResponseEntity<BeneficiarioResponseDTO> atualizar(
            @PathVariable("idBeneficiario") Long idBeneficiario,
            @RequestBody BeneficiarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idBeneficiario, dto));
    }

    // PATCH: atualização parcial (campo ausente ou null não muda).
    @PatchMapping("/{idBeneficiario}")
    public ResponseEntity<BeneficiarioResponseDTO> atualizarParcial(
            @PathVariable("idBeneficiario") Long idBeneficiario,
            @RequestBody BeneficiarioPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idBeneficiario, dto));
    }

    // 204 No Content: sucesso sem corpo.
    @DeleteMapping("/{idBeneficiario}")
    public ResponseEntity<Void> deletar(@PathVariable("idBeneficiario") Long idBeneficiario) {
        service.deletar(idBeneficiario);
        return ResponseEntity.noContent().build();
    }
}
