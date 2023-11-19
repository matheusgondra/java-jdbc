package br.com.alura.bytebank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;

public class ContaService {
	private ConnectionFactory connection;

	public ContaService() {
		this.connection = new ConnectionFactory();
	}

	private Set<Conta> contas = new HashSet<>();

	public Set<Conta> listarContasAbertas() {
		Connection conn = this.connection.getConnection();
		return new ContaDAO(conn).list();
	}

	public BigDecimal consultarSaldo(Integer numeroDaConta) {
		var conta = buscarContaPorNumero(numeroDaConta);
		return conta.getSaldo();
	}

	public void abrir(DadosAberturaConta dadosDaConta) {
		Connection conn = this.connection.getConnection();
		new ContaDAO(conn).save(dadosDaConta);
	}

	public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
		var conta = buscarContaPorNumero(numeroDaConta);
		if (valor.compareTo(BigDecimal.ZERO) <= 0) {
			throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
		}

		if (valor.compareTo(conta.getSaldo()) > 0) {
			throw new RegraDeNegocioException("Saldo insuficiente!");
		}

		BigDecimal newValue = conta.getSaldo().subtract(valor);
		update(conta, newValue);
	}

	public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
		var conta = buscarContaPorNumero(numeroDaConta);
		BigDecimal newValue = conta.getSaldo().add(valor);
		update(conta, newValue);
	}

	public void realizarTransferencia(Integer numeroDaContaOrigem, Integer numeroDaContaDestino, BigDecimal valor) {
		realizarSaque(numeroDaContaOrigem, valor);
		realizarDeposito(numeroDaContaDestino, valor);
	}

	public void encerrar(Integer numeroDaConta) {
		var conta = buscarContaPorNumero(numeroDaConta);
		if (conta.possuiSaldo()) {
			throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
		}

		contas.remove(conta);
	}

	private Conta buscarContaPorNumero(Integer numero) {
		Connection conn = connection.getConnection();
		Conta conta = new ContaDAO(conn).listByNumber(numero);

		if (conta == null) {
			throw new RegraDeNegocioException("Não existe conta cadastrada com esse número");
		}

		return conta;
	}

	private void update(Conta conta, BigDecimal valor) {
		Connection conn = connection.getConnection();
		new ContaDAO(conn).update(conta.getNumero(), valor);
	}
}
