# Bank Statement Parser & Analyser

A robust Spring Boot application for ingesting, parsing, categorizing, and analyzing bank statements. This system leverages **Apache PDFBox** for text extraction and **Google Vertex AI Gemini** for intelligent transaction categorization and template detection.

## 🚀 Features

- **Document Ingestion**: Securely upload bank statements (PDF) via REST API.
- **Duplicate Detection**: SHA-256 hashing to prevent duplicate document processing.
- **PDF Processing**: Validates PDF integrity and extracts raw text using Apache PDFBox.
- **Template Detection**: Automatically identifies the bank and statement format using AI.
- **Hybrid Categorization**:
    - **Rule Engine**: Deterministic, high-speed categorization for known merchants (e.g., "UBER" -> "Transport").
    - **AI Fallback**: Uses Gemini AI to categorize unknown transactions based on context.
- **Analytics & Reporting**: Endpoints to retrieve spending breakdowns by category.
- **Security**: JWT-ready foundation and secure file handling.
- **Scalable Architecture**: Designed with clean separation of concerns (Controller, Service, Repository).

## 📋 Prerequisites

- **Java**: 21
- **Maven**: 3.8+
- **Docker**: For running PostgreSQL and Redis
- **Google Cloud Project**: With Vertex AI API enabled

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.x
- **Language**: Java 21
- **AI Integration**: Spring AI (Google GenAI)
- **PDF Processing**: Apache PDFBox 3.0.x
- **Database**: PostgreSQL 16 (with Flyway migrations)
- **Caching**: Redis
- **Testing**: JUnit 5, Mockito, Testcontainers

## ⚙️ Configuration

### Environment Variables

The application relies on the following environment variables. Create a `.env` file or export them in your shell:

```bash
# Database Configuration (Defaults in application.yml)
# DB_URL=jdbc:postgresql://localhost:5432/bsa_db
# DB_USERNAME=bsa_user
# DB_PASSWORD=bsa_password

# Google GenAI Configuration (REQUIRED)
export GEMINI_API_KEY=your-google-ai-studio-api-key
# export GOOGLE_APPLICATION_CREDENTIALS=... (Not needed for API Key access)

# Security
export JWT_SECRET=your-secure-jwt-secret-key-min-32-chars
```

### Application Configuration

Key settings in `src/main/resources/application.yml`:

```yaml
bsa:
  storage:
    upload-dir: ./uploads  # Directory for storing uploaded PDFs
  security:
    jwt-expiration-ms: 86400000 # 24 hours
```

## 🗄️ Database Setup

The project uses **Docker Compose** to spin up the required infrastructure (PostgreSQL and Redis).

1.  **Start Infrastructure**:
    ```bash
    docker-compose up -d
    ```

    This will start:
    - **PostgreSQL**: Port 5432, Database `bsa_db`
    - **Redis**: Port 6379

2.  **Schema Migration**:
    Flyway is configured to automatically run migrations on application startup.
    - `V1__init_schema.sql`: Creates `documents`, `transactions`, and `category_rules` tables.

## 📦 Installation & Running

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd bank-statement-analyser
    ```

2.  **Build the project**:
    ```bash
    mvn clean install
    ```

3.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

    The server will start on `http://localhost:8080`.

## 🔌 API Endpoints

### Documents

-   **Upload Document**:
    -   `POST /api/v1/documents/upload`
    -   Body: `multipart/form-data` with `files` (PDF)
    -   Params: `sessionId` (optional)

### Analytics & Categorization

-   **Trigger Categorization**:
    -   `POST /api/v1/analytics/categorize/{docNumber}`
    -   Triggers the categorization process (Rules + AI) for a specific document.

-   **Spending Report**:
    -   `GET /api/v1/analytics/spending-by-category?docNumber={docNumber}`
    -   Returns a JSON map of Total Spending per Category.

## 🏗️ Architecture

The application follows a standard layered architecture:

```
src/main/java/com/yourco/bsa/
├── controller/          # REST Controllers (Document, Analytics)
├── service/             # Business Logic
│   ├── ingestion/       # File upload & storage logic
│   ├── processing/      # PDFBox integration
│   ├── ai/              # Gemini Service integration
│   ├── template/        # Template detection orchestration
│   └── categorization/  # Rule Engine & Categorization logic
├── repository/          # Spring Data JPA Repositories
├── model/               # JPA Entities (Document, Transaction, CategoryRule)
└── exception/           # Global Exception Handling
```

### Key Components

-   **IngestionService**: Handles file uploads, SHA-256 hashing for deduplication, and saving metadata.
-   **PdfProcessor**: Wrapper around Apache PDFBox to validate and extract text from PDFs.
-   **RuleEngineService**: In-memory rule matcher for high-performance categorization of common recurring transactions.
-   **GeminiService**: Uses Spring AI ChatClient to send prompts to Google GenAI (Gemini) for advanced analysis.

## 🧪 Testing

Run unit and integration tests with Maven:

```bash
mvn test
```

-   **Unit Tests**: Mocked dependencies for Services and Controllers.
-   **Integration Tests**: `DocumentControllerTest`, `GeminiServiceTest`, `RuleEngineServiceTest`, etc.

## 🤝 Contributing

1.  Fork the project.
2.  Create your feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## 📝 License

Distributed under the GNU General Public License v3.0. See `LICENSE` for more information.

## 👤 Author

**Navneet Prabhakar**

## 📞 Support

For issues, please open a ticket in the repository or contact the maintainer.
