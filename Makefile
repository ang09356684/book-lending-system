# Online Library Management System - Makefile

.PHONY: help start stop restart build clean db-init db-reset test

# Default target
help:
	@echo "Available commands:"
	@echo "  start     - Start all services"
	@echo "  stop      - Stop all services"
	@echo "  restart   - Restart all services"
	@echo "  Build     - Build images"
	@echo "  clean     - Clean all containers and data"
	@echo "  db-init   - Initialize database"
	@echo "  db-reset  - Reset database (delete all data)"
	@echo "  db-clear  - Clear database tables (keep container)"
	@echo ""
	@echo "Testing commands:"
	@echo "  test              - Run all unit tests with coverage report"
	@echo "  test-repo-service - Run Repository and Service tests (optimized)"
	@echo "  test-repo         - Run Repository tests only"
	@echo "  test-service      - Run Service tests only"
	@echo "  test-class        - Run specific test class (use CLASS=ClassName)"

# Start all services
start:
	docker-compose -f docker-compose.dev.yml up -d

# Stop all services
stop:
	docker-compose -f docker-compose.dev.yml down

# Restart all services
restart: stop start

# Build images
build:
	docker-compose -f docker-compose.dev.yml build

# Clean all containers and data
clean:
	@echo "Cleaning all containers and data..."
	docker-compose -f docker-compose.dev.yml down -v --remove-orphans
	docker system prune -f
	@echo "Clean completed!"

# Initialize database
db-init:
	@echo "Initializing database..."
	docker exec -i library-postgres psql -U postgres -d library < database_schema.sql
	docker exec -i library-postgres psql -U postgres -d library < src/main/resources/data.sql
	@echo "Database initialization completed!"

# Reset database (delete all data)
db-reset:
	@echo "Resetting database..."
	docker-compose -f docker-compose.dev.yml down -v
	docker-compose -f docker-compose.dev.yml up -d postgres
	@echo "Waiting for PostgreSQL to start..."
	sleep 10
	docker exec -i library-postgres psql -U postgres -d library < database_schema.sql
	@echo "Database reset completed!"

# Clear database (drop and recreate tables)
db-clear:
	@echo "Clearing database tables..."
	docker exec -i library-postgres psql -U postgres -d library -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
	docker exec -i library-postgres psql -U postgres -d library < database_schema.sql
	docker exec -i library-postgres psql -U postgres -d library < src/main/resources/data.sql
	@echo "Database cleared and reinitialized!"

# View logs
logs:
	docker-compose -f docker-compose.dev.yml logs -f

# View application logs
logs-app:
	docker-compose -f docker-compose.dev.yml logs -f app-dev

# View database logs
logs-db:
	docker-compose -f docker-compose.dev.yml logs -f postgres

# Run all unit tests with coverage report
test:
	@echo "Running all unit tests with coverage report..."
	docker-compose -f docker-compose.dev.yml exec app-dev mvn test jacoco:report
	@echo "All tests completed!"
	@echo "Coverage report generated in target/site/jacoco/index.html"
	@echo "To view coverage report, open: target/site/jacoco/index.html"

# Run Repository and Service tests only (optimized)
test-repo-service:
	@echo "Running Repository and Service tests (optimized execution)..."
	docker-compose -f docker-compose.dev.yml exec app-dev mvn test -Dtest=CompleteTestSuite
	@echo "Repository and Service tests completed!"

# Run Repository tests only
test-repo:
	@echo "Running Repository tests only..."
	docker-compose -f docker-compose.dev.yml exec app-dev mvn test -Dtest=RepositoryTestSuite
	@echo "Repository tests completed!"

# Run Service tests only
test-service:
	@echo "Running Service tests only..."
	docker-compose -f docker-compose.dev.yml exec app-dev mvn test -Dtest=ServiceTestSuite
	@echo "Service tests completed!"

# Run specific test class
test-class:
	@echo "Usage: make test-class CLASS=ClassName"
	@echo "Example: make test-class CLASS=UserServiceTest"
	@if [ -z "$(CLASS)" ]; then \
		echo "Please specify a test class name using CLASS=ClassName"; \
		exit 1; \
	fi
	docker-compose -f docker-compose.dev.yml exec app-dev mvn test -Dtest=$(CLASS)
	@echo "Test class $(CLASS) completed!"
