# Online Library Management System - Makefile

.PHONY: help start stop restart build clean db-init db-reset

# Default target
help:
	@echo "Available commands:"
	@echo "  start     - Start all services"
	@echo "  stop      - Stop all services"
	@echo "  restart   - Restart all services"
	@echo "  build     - Rebuild and start services"
	@echo "  clean     - Clean all containers and data"
	@echo "  db-init   - Initialize database"
	@echo "  db-reset  - Reset database (delete all data)"

# Start all services
start:
	docker-compose -f docker-compose.dev.yml up -d

# Stop all services
stop:
	docker-compose -f docker-compose.dev.yml down

# Restart all services
restart: stop start

# Rebuild and start services
build:
	docker-compose -f docker-compose.dev.yml up --build -d

# Clean all containers and data
clean:
	docker-compose -f docker-compose.dev.yml down -v
	docker system prune -f

# Initialize database
db-init:
	@echo "Initializing database..."
	docker exec -i library-postgres psql -U postgres -d library < database_schema.sql
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

# View logs
logs:
	docker-compose -f docker-compose.dev.yml logs -f

# View application logs
logs-app:
	docker-compose -f docker-compose.dev.yml logs -f app-dev

# View database logs
logs-db:
	docker-compose -f docker-compose.dev.yml logs -f postgres
