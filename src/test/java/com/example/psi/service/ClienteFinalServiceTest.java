package com.example.psi.service;

import com.example.psi.model.ClienteFinal;
import com.example.psi.repository.ClienteFinalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteFinalServiceTest {

    @Mock
    private ClienteFinalRepository clienteFinalRepository;

    @InjectMocks
    private ClienteFinalService clienteFinalService;

    @Test
    void deveListarTodosOsClientes() {
        List<ClienteFinal> clientes = List.of(new ClienteFinal());
        when(clienteFinalRepository.findAll()).thenReturn(clientes);

        List<ClienteFinal> resultado = clienteFinalService.listarTodos();

        assertSame(clientes, resultado);
        verify(clienteFinalRepository).findAll();
    }

    @Test
    void deveListarClientesDoAdministrador() {
        Long admId = 1L;
        List<ClienteFinal> clientes = List.of(new ClienteFinal());
        when(clienteFinalRepository.findByClienteAdmId(admId)).thenReturn(clientes);

        List<ClienteFinal> resultado = clienteFinalService.listarPorClienteAdm(admId);

        assertSame(clientes, resultado);
        verify(clienteFinalRepository).findByClienteAdmId(admId);
    }

    @Test
    void deveAplicarFiltrosETratarEspacos() {
        Long admId = 1L;
        List<ClienteFinal> clientes = List.of(new ClienteFinal());
        when(clienteFinalRepository.buscarComFiltros(admId, "Ana", null, "ana@teste.com"))
                .thenReturn(clientes);

        List<ClienteFinal> resultado = clienteFinalService.buscarPorFiltros(
                admId, "  Ana  ", "   ", "ana@teste.com"
        );

        assertSame(clientes, resultado);
        verify(clienteFinalRepository).buscarComFiltros(admId, "Ana", null, "ana@teste.com");
    }

    @Test
    void deveNormalizarFiltrosNulosComoNulos() {
        when(clienteFinalRepository.buscarComFiltros(1L, null, null, null))
                .thenReturn(List.of());

        List<ClienteFinal> resultado = clienteFinalService.buscarPorFiltros(1L, null, "", "  ");

        assertEquals(List.of(), resultado);
        verify(clienteFinalRepository).buscarComFiltros(1L, null, null, null);
    }

    @Test
    void deveBuscarClientePorIdQuandoEncontrado() {
        ClienteFinal cliente = new ClienteFinal();
        when(clienteFinalRepository.findById(10L)).thenReturn(Optional.of(cliente));

        Optional<ClienteFinal> resultado = clienteFinalService.buscarPorId(10L);

        assertEquals(Optional.of(cliente), resultado);
        verify(clienteFinalRepository).findById(10L);
    }

    @Test
    void deveRetornarOptionalVazioQuandoClienteNaoEncontrado() {
        when(clienteFinalRepository.findById(10L)).thenReturn(Optional.empty());

        Optional<ClienteFinal> resultado = clienteFinalService.buscarPorId(10L);

        assertEquals(Optional.empty(), resultado);
        verify(clienteFinalRepository).findById(10L);
    }

    @Test
    void deveSalvarCliente() {
        ClienteFinal cliente = new ClienteFinal();
        when(clienteFinalRepository.save(cliente)).thenReturn(cliente);

        ClienteFinal resultado = clienteFinalService.salvar(cliente);

        assertSame(cliente, resultado);
        verify(clienteFinalRepository).save(cliente);
    }

    @Test
    void deveExcluirCliente() {
        clienteFinalService.deletar(10L);

        verify(clienteFinalRepository).deleteById(10L);
        verifyNoMoreInteractions(clienteFinalRepository);
    }

    @Test
    void devePropagarErroAoSalvarCliente() {
        ClienteFinal cliente = new ClienteFinal();
        RuntimeException erro = new RuntimeException("Falha de persistência");
        when(clienteFinalRepository.save(cliente)).thenThrow(erro);

        RuntimeException resultado = assertThrows(
                RuntimeException.class,
                () -> clienteFinalService.salvar(cliente)
        );

        assertSame(erro, resultado);
        verify(clienteFinalRepository).save(cliente);
    }

    @Test
    void devePropagarErroAoExcluirCliente() {
        RuntimeException erro = new RuntimeException("Falha de persistência");
        doThrow(erro).when(clienteFinalRepository).deleteById(10L);

        RuntimeException resultado = assertThrows(
                RuntimeException.class,
                () -> clienteFinalService.deletar(10L)
        );

        assertSame(erro, resultado);
        verify(clienteFinalRepository).deleteById(10L);
    }
}
