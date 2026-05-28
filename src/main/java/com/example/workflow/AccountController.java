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
}