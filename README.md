# LocalMind: On-Device Retrieval Augmented Generation for University Knowledge Base

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A research project demonstrating **Improving Usability of University Knowledge Base through On-Device Retrieval Augmented Generation (RAG)**. LocalMind combines on-device semantic search with remote Large Language Model capabilities to provide fast, context-aware, and privacy-preserving access to university support resources.

## Project Overview

LocalMind addresses the challenge of making university knowledge bases more accessible and user-friendly by implementing a hybrid RAG system that:

- **Processes documents locally** using on-device AI for privacy and speed
- **Performs semantic search** through vector embeddings and similarity matching
- **Enhances responses** by combining local context with remote LLM capabilities
- **Optimizes performance** through systematic chunking and embedding strategies

## Architecture

### Multi-Modular Clean Architecture

![Modular Architecture](https://github.com/n7-ved/LocalMind/blob/main/docs/diagrams/localmind_architecture.png)

### Module Structure
```
:androidApp                         # Main application entry point
|-- :core                           # Infrastructure layer - foundational services
|   |-- :cache                      # Local caching with DataStore preferences
|   |-- :common                     # Core utilities and shared functionality
|   |-- :design-system              # Reusable UI components, theming, and Material 3 design
|   |-- :network                    # Ktor HTTP client configuration and providers
|   |-- :sentence-embedding-model   # ONNX-based sentence embeddings (all-MiniLM-L6-v2)
|   |-- :vector-db                  # ObjectBox vector storage with HNSW indexing
|-- :component                      # Business logic layer - domain services
|   |-- :chat                       # Chat-related business logic
|   |-- :common                     # Component-level shared utilities and patterns
|   |-- :local-llm                  # Local LLM integration
|   |-- :local-rag                  # RAG processing pipeline and document management
|   |-- :remote-gpt                 # OpenAI GPT API client and chat logic
|-- :feature                        # Presentation layer - user-facing screens
    |-- :document-local-rag         # Document upload and management interface
    |-- :main                       # Bottom navigation and app coordination
    |-- :performance-local-rag      # RAG performance analysis and benchmarking
    |-- :remote-gpt                 # Hybrid RAG chat interface with GPT integration
```

### Technology Stack

| Component | Technology |
|-----------|------------|
| **IDE** | Android Studio |
| **Build System** | Gradle |
| **User Interface** | Jetpack Compose |
| **Navigation Flow** | Jetpack Navigation |
| **Concurrency** | Kotlin Coroutines |
| **State/Event Management** | StateFlow; SharedFlow |
| **Networking** | Ktor |
| **Offline Cache** | Preferences-DataStore |
| **Vector DB** | ObjectBox |
| **Sentence Embedding Model** | all-MiniLM-L6-v2.onnx |
| **Inference Engine** | ONNX Runtime |
| **Documentation** | Dokka |

#### Architectural Patterns
- **Clean Architecture:** Multi-module structure organized by feature/component/core.


- **Model-View-ViewModel (MVVM):** Used for the presentation layer.

#### Design Patterns
- **Repository Pattern:** Encapsulates data access logic and hides source implementation to promote separation of concerns and maintainable architecture.


- **Singleton Pattern:** Ensures a single, globally accessible instance of shared resources, preventing duplication and reducing overhead.


- **Dependency Injection Pattern (Manual DI):** Dependencies are passed explicitly through constructors or function parameters to promote modularity, testability, and loose coupling - without relying on external frameworks.

## Features

### Document Management
- **PDF Upload**: Select and process university documents
- **Automatic Chunking**: Intelligent text segmentation (200-250 tokens optimal)
- **Embedding Generation**: On-device sentence transformers via ONNX
- **Vector Storage**: High-performance local database with HNSW indexing

### Hybrid RAG Chat
- **Semantic Retrieval**: Find relevant context using vector similarity search
- **Context Enhancement**: Combine local knowledge with user queries
- **Remote LLM Integration**: OpenAI GPT models for natural language responses
- **Multi-turn Conversations**: Maintain context across chat sessions

### Performance Analysis
- **Systematic Testing**: Evaluate different chunking strategies
- **Metrics Collection**: Retrieval accuracy, speed, and relevance scores
- **Configuration Optimization**: Test chunk sizes and overlap percentages
- **Data Export**: CSV export for academic research and analysis

### Modern UI/UX
- **Material 3 Design**: Consistent, accessible interface
- **Bottom Navigation**: Easy access to core features
- **Real-time Updates**: Live progress tracking and feedback

## Technical Implementation

### On-Device AI Pipeline

```kotlin
// Document Processing Flow
PDF → Text Extraction → Chunking → Embedding → Vector Storage

// Query Processing Flow  
User Query → Embedding → Vector Search → Context Retrieval → LLM Enhancement → Response
```

### Key Components

#### Sentence Embedding Model
- **Model**: all-MiniLM-L6-v2.onnx
- **Dimensions**: 384-dimensional embeddings
- **Runtime**: ONNX Runtime Android for optimized inference

#### Vector Database
- **Technology**: ObjectBox with HNSW indexing
- **Storage**: On-device, privacy-preserving

#### Chunking Strategy
- **Optimal Size**: 200-250 tokens (determined through systematic testing)
- **Overlap**: 20% overlap between chunks for context preservation
- **Method**: Sentence-boundary aware splitting

## Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK API 29+ (Android 10+)
- OpenAI API key (for remote LLM features)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/n7-ved/LocalMind.git
   cd LocalMind
   ```

2. **Configure API Key**
   Create `local.properties` in the root directory:
   ```properties
   LOCAL_PROPERTIES_OPENAI_API_KEY=<your_openai_api_key_here>
   ```

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   # Or open in Android Studio and run
   ```

### Usage

1. **Upload Documents**: Use the Document tab to upload PDF files
2. **Chat Interface**: Navigate to Chat tab for RAG-powered conversations  
3. **Performance Testing**: Use Performance tab to analyze different configurations
4. **Export Results**: Generate CSV reports for research analysis


## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
