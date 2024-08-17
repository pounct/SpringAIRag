RAG - Retrieval Augmented Generation y Sprig AI


Inyección de Dependencias:

Inyectas un ChatClient y un VectorStore a través del constructor del controlador. El ChatClient se utiliza para interactuar con un modelo de chat AI, mientras que el VectorStore se utiliza para recuperar los documentos similares para proporcionar un contexto relevante a la pregunta planteada.
Carga del Prompt:

El archivo de prompt (prompt.st) se carga desde los recursos del proyecto mediante @Value. Este archivo probablemente contiene una plantilla de prompt que se utiliza para formular la pregunta de manera óptima para la AI.
Búsqueda de Similitud:

vectorStore.similaritySearch(SearchRequest.query(question).withTopK(4)); busca los documentos más similares en el VectorStore en función de la pregunta, recuperando los 4 documentos más relevantes (TopK=4).
Creación del Prompt:

Los documentos similares se mapean a su contenido textual, y estos contenidos se utilizan para rellenar una plantilla de prompt (utilizando PromptTemplate) con las variables context y question.
Llamada al Modelo AI:

El ChatClient se utiliza para enviar el prompt al modelo AI y recuperar la respuesta, que se devuelve como texto plano.
