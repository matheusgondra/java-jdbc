package br.com.alura.bytebank;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionFactory {
	public Connection getConnection() {
		try {
			return createDataSource().getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private HikariDataSource createDataSource() {
		HikariConfig config = new HikariConfig();

		config.setJdbcUrl("jdbc:mysql://localhost:3306/byte_bank");
		config.setUsername("dev");
		config.setPassword("Dev@1234");
		config.setMaximumPoolSize(10);

		return new HikariDataSource(config);
	}
}
