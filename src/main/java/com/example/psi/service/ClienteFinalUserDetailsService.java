package com.example.psi.service;

import com.example.psi.model.ClienteFinal;
import com.example.psi.model.ClienteAdm;
import com.example.psi.repository.ClienteAdmRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ClienteFinalUserDetailsService implements UserDetailsService {

    private final ClienteFinalService clienteFinalService;
    private final ClienteAdmRepository clienteAdmRepository;

    public ClienteFinalUserDetailsService(ClienteFinalService clienteFinalService,
                                          ClienteAdmRepository clienteAdmRepository) {
        this.clienteFinalService = clienteFinalService;
        this.clienteAdmRepository = clienteAdmRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        String email = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        ClienteAdm profissional = clienteAdmRepository.findByEmailIgnoreCase(email).orElse(null);
        if (profissional != null) {
            validarHash(profissional.getSenha(), email);
            return User.withUsername(profissional.getEmail())
                    .password(profissional.getSenha())
                    .roles("PROFISSIONAL")
                    .build();
        }

        ClienteFinal cliente = clienteFinalService.buscarPorEmail(email)
                .filter(item -> item.getSenha() != null && !item.getSenha().isBlank())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        validarHash(cliente.getSenha(), email);
        return User.withUsername(cliente.getEmail())
                .password(cliente.getSenha())
                .roles("CLIENTE_FINAL")
                .build();
    }

    private void validarHash(String senha, String email) {
        if (senha == null || senha.isBlank()
                || !senha.matches("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$")) {
            throw new UsernameNotFoundException("Credenciais inválidas para o usuário: " + email);
        }
    }
}
