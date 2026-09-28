package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioDTO;
import br.com.socialconnect.api.beneficiarios.service.BeneficiarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiarios")
public class BeneficiarioController {

    private final BeneficiarioService service;

    public BeneficiarioController(BeneficiarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BeneficiarioDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Nome explícito no @PathVariable: não depende de o compilador guardar o
    // nome dos parâmetros (-parameters), o que falha quando a IDE compila sozinha.
    @GetMapping("/{idBeneficiario}")
    public ResponseEntity<BeneficiarioDTO> buscarPorId(@PathVariable("idBeneficiario") Long idBeneficiario) {
        return ResponseEntity.ok(service.buscarPorId(idBeneficiario));
    }

    // 201 Created + Location: o recurso novo ganha endereço próprio.
    // Sem o @RequestBody, os campos do JSON chegariam nulos.
    @PostMapping
    public ResponseEntity<BeneficiarioDTO> salvar(@RequestBody BeneficiarioDTO dto) {
        BeneficiarioDTO salvo = service.salvar(dto);
        URI location = URI.create("/api/v1/beneficiarios/" + salvo.idBeneficiario());
        return ResponseEntity.created(location).body(salvo);
    }

    @DeleteMapping("/{idBeneficiario}")
    public ResponseEntity<Void> deletar(@PathVariable("idBeneficiario") Long idBeneficiario) {
        service.deletar(idBeneficiario);
        return ResponseEntity.noContent().build();
    }
}
