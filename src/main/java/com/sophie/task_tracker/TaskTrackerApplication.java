package com.sophie.task_tracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@SpringBootApplication
@EnableJpaAuditing
public class TaskTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskTrackerApplication.class, args);
	}

	@Bean
	public CommandLineRunner testDatabase(DataSource dataSource) {
		return args -> {
			System.out.println("=== DATABASE CONNECTION TEST ===");
			
			try (Connection connection = dataSource.getConnection()) {
				System.out.println("✅ Database connected successfully!");
				System.out.println("Database: " + connection.getMetaData().getDatabaseProductName());
				System.out.println("Version: " + connection.getMetaData().getDatabaseProductVersion());
				
				DatabaseMetaData metaData = connection.getMetaData();
				ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
				
				System.out.println("\n=== TABLES FOUND ===");
				boolean foundTables = false;
				while (tables.next()) {
					String tableName = tables.getString("TABLE_NAME");
					if (!tableName.startsWith("INFORMATION_SCHEMA") &&
						!tableName.equals("CONSTANTS") && 
						!tableName.equals("ENUM_VALUES") &&
						!tableName.equals("INDEXES") &&
						!tableName.equals("INDEX_COLUMNS") &&
						!tableName.equals("INFORMATION_SCHEMA_CATALOG_NAME") &&
						!tableName.equals("IN_DOUBT") &&
						!tableName.equals("LOCKS") &&
						!tableName.equals("QUERY_STATISTICS") &&
						!tableName.equals("RIGHTS") &&
						!tableName.equals("ROLES") &&
						!tableName.equals("SESSIONS") &&
						!tableName.equals("SESSION_STATE") &&
						!tableName.equals("SETTINGS") &&
						!tableName.equals("SYNONYMS")) {
						System.out.println("✅ Table: " + tableName);
						foundTables = true;
					}
				}
				
				if (!foundTables) {
					System.out.println("❌ No custom tables found!");
				}
				
			} catch (Exception e) {
				System.err.println("❌ Database connection failed: " + e.getMessage());
				e.printStackTrace();
			}

			System.out.println("📚 Swagger UI: http://localhost:8080/swagger-ui.html");
			System.out.println("🗄️ H2 Console: http://localhost:8080/h2-console");
		};
	}
}
