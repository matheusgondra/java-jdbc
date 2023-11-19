package br.com.alura.bytebank.domain.conta;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

public class ContaDAO {
	private Connection connection;

	ContaDAO(Connection connection) {
		this.connection = connection;
	}

	public void save(DadosAberturaConta dadosDaConta) {
		var cliente = new Cliente(dadosDaConta.dadosCliente());
		var conta = new Conta(dadosDaConta.numero(), BigDecimal.ZERO, cliente);

		String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email) VALUES (?, ?, ?, ?, ?)";

		try {
			PreparedStatement preparedStatement = this.connection.prepareStatement(sql);

			preparedStatement.setInt(1, conta.getNumero());
			preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
			preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
			preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
			preparedStatement.setString(5, dadosDaConta.dadosCliente().email());

			preparedStatement.execute();

			preparedStatement.close();
			this.connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Set<Conta> list() {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Set<Conta> contas = new HashSet<>();

		String sql = "SELECT * FROM conta";

		try {
			preparedStatement = this.connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Integer numero = resultSet.getInt(1);
				BigDecimal saldo = resultSet.getBigDecimal(2);
				String nome = resultSet.getString(3);
				String cpf = resultSet.getString(4);
				String email = resultSet.getString(5);

				DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);
				Cliente cliente = new Cliente(dadosCadastroCliente);
				contas.add(new Conta(numero, saldo, cliente));
			}

			resultSet.close();
			preparedStatement.close();
			this.connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return contas;
	}

	public void find(Integer number) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		String sql = "SELECT * FROM conta WHERE numero = ?";

		try {
			preparedStatement = this.connection.prepareStatement(sql);

			preparedStatement.setInt(1, number);

			resultSet = preparedStatement.executeQuery();

			System.out.println("ResultSet: " + resultSet);

			resultSet.close();
			preparedStatement.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Conta listByNumber(Integer number) {
		String sql = "SELECT * FROM conta WHERE numero = ?";

		PreparedStatement ps;
		ResultSet resultSet;
		Conta conta = null;

		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, number);
			resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Integer numeroRecuperado = resultSet.getInt(1);
				BigDecimal saldo = resultSet.getBigDecimal(2);
				String nome = resultSet.getString(3);
				String cpf = resultSet.getString(4);
				String email = resultSet.getString(5);

				DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome, cpf, email);
				Cliente cliente = new Cliente(dadosCadastroCliente);

				conta = new Conta(numeroRecuperado, saldo, cliente);
			}

			resultSet.close();
			ps.close();
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return conta;
	}

	public void update(Integer number, BigDecimal valor) {
		String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";
		PreparedStatement ps;

		try {
			connection.setAutoCommit(false);

			ps = connection.prepareStatement(sql);

			ps.setBigDecimal(1, valor);
			ps.setInt(2, number);
			
			ps.execute();
			connection.commit();

			ps.close();
			connection.close();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
			throw new RuntimeException(e);
		}

	} 
}
