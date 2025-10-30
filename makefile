# 🏦 BankPOC Makefile
# Simplify common Spring Boot tasks (build, run, db, test, docker)

APP_NAME=bankpoc-core
JAR_FILE=build/libs/core-0.0.1-SNAPSHOT.jar
DB_CONTAINER=bankpoc-db
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin
POSTGRES_DB=bankpoc
POSTGRES_PORT=5432

# 🧱 Default target
help:
	@echo ""
	@echo "🚀 BankPOC Developer Commands"
	@echo "--------------------------------"
	@echo "make run           → Run the Spring Boot app"
	@echo "make build         → Build the project"
	@echo "make jar           → Build runnable JAR"
	@echo "make clean         → Clean Gradle build"
	@echo "make test          → Run tests"
	@echo "make migrate       → Run Flyway migrations"
	@echo "make db-up         → Start PostgreSQL via Docker"
	@echo "make db-down       → Stop PostgreSQL container"
	@echo "make db-restart    → Restart PostgreSQL"
	@echo "make logs          → View Spring Boot logs"
	@echo "make docker-build  → Build Docker image"
	@echo "make docker-run    → Run Docker container"
	@echo ""

# 🧩 Run the Spring Boot app
run:
	./gradlew bootRun

# 🛠️ Build the project
build:
	./gradlew clean build

# 📦 Build runnable JAR
jar:
	./gradlew bootJar

# 🧹 Clean build artifacts
clean:
	./gradlew clean

# 🧪 Run tests
test:
	./gradlew test

# 🗃️ Run Flyway migrations
migrate:
	./gradlew flywayMigrate

# 🐘 Database (PostgreSQL)
db-up:
	docker run --name $(DB_CONTAINER) \
		-e POSTGRES_USER=$(POSTGRES_USER) \
		-e POSTGRES_PASSWORD=$(POSTGRES_PASSWORD) \
		-e POSTGRES_DB=$(POSTGRES_DB) \
		-p $(POSTGRES_PORT):5432 \
		-d postgres:16

db-down:
	docker stop $(DB_CONTAINER) && docker rm $(DB_CONTAINER)

db-restart: db-down db-up

# 📜 View application logs
logs:
	tail -f logs/spring.log || echo "No log file found yet."

# 🐳 Docker commands
docker-build:
	docker build -t $(APP_NAME):latest .

docker-run:
	docker run -d --name $(APP_NAME) \
		-p 8080:8080 \
		--env-file .env \
		$(APP_NAME):latest

docker-stop:
	docker stop $(APP_NAME) && docker rm $(APP_NAME)

# 🧠 Utilities
status:
	@echo "App JAR: $(JAR_FILE)"
	@echo "DB: $(DB_CON_
