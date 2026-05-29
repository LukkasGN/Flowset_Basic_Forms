package com.example.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:5173")
public class AccountController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/search")
    public ResponseEntity<?> searchAccount(@RequestParam("accountNumber") String accountNumber) {
        try {
            // Query account details
            List<Map<String, Object>> accounts = jdbcTemplate.queryForList(
                    "SELECT nome, centro_negocios FROM bank_accounts WHERE account_number = ?",
                    accountNumber
            );

            if (accounts.isEmpty()) {
                return ResponseEntity.status(404).body(
                        Map.of("error", "Conta não encontrada")
                );
            }

            Map<String, Object> account = accounts.get(0);

            // Query NIFs for this account
            List<Map<String, Object>> nifRows = jdbcTemplate.queryForList(
                    "SELECT nif FROM account_nifs WHERE account_number = ?",
                    accountNumber
            );

            // Format NIFs as label/value pairs for the dropdown
            List<Map<String, String>> nifs = nifRows.stream()
                    .map(row -> Map.of(
                            "label", row.get("nif").toString(),
                            "value", row.get("nif").toString()
                    ))
                    .toList();

            Map<String, Object> result = new HashMap<>();
            result.put("nome", account.get("nome").toString());
            result.put("centro_negocios", account.get("centro_negocios").toString());
            result.put("nifs", nifs);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("error", "Erro ao pesquisar conta")
            );
        }
    }
    @GetMapping("/suggest")
    public ResponseEntity<?> suggestAccounts(@RequestParam("query") String query) {
        try {
            List<Map<String, Object>> accounts = jdbcTemplate.queryForList(
                    "SELECT account_number, nome FROM bank_accounts WHERE account_number ILIKE ? OR nome ILIKE ? LIMIT 5",
                    "%" + query + "%", "%" + query + "%"
            );
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao pesquisar contas"));
        }
    }

    @GetMapping("/options/{tableName}")
    public ResponseEntity<?> getOptions(@PathVariable("tableName") String tableName) {
        // Whitelist to prevent SQL injection
        List<String> allowed = List.of(
                "finalidade", "descricao_finalidade", "detalhe_finalidade",
                "objetivo_operacao", "cobertura_cambial", "despesas",
                "moeda", "pais_destino", "instrumento_pagamento",
                "residencia_cambial", "cae", "entidade_petrolifera",
                "banco_beneficiario"
        );

        if (!allowed.contains(tableName)) {
            return ResponseEntity.status(400).body(Map.of("error", "Tabela não permitida"));
        }

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT valor, label FROM " + tableName + " ORDER BY label"
            );
            List<Map<String, String>> options = rows.stream()
                    .map(row -> Map.of(
                            "label", row.get("label").toString(),
                            "value", row.get("valor").toString()
                    ))
                    .toList();
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erro ao carregar opções"));
        }
    }
}