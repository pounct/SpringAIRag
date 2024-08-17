# RAG - Retrieval Augmented Generation y Sprig AI

# class:

## DataLoader:

Logger y LoggerFactory:

Se utilizan para registrar información en la consola o en archivos de registro, lo que ayuda en la depuración y monitoreo del programa.

Document y EmbeddingModel:

Document representa un documento de texto que será procesado y dividido en fragmentos.
EmbeddingModel es el modelo que convierte texto en vectores numéricos para su almacenamiento y búsqueda eficiente.

TextSplitter y TokenTextSplitter:

TextSplitter es una interfaz o clase base utilizada para dividir un documento en fragmentos más pequeños.
TokenTextSplitter es una implementación específica de TextSplitter que divide el texto en fragmentos basados en tokens (generalmente palabras o frases).

SimpleVectorStore:

Es un almacén de vectores sencillo que se utiliza para almacenar los vectores generados por el modelo de incrustación.
Resource y Value:

Resource se utiliza para manejar recursos como archivos dentro del proyecto.
@Value se utiliza para inyectar valores desde archivos de configuración, como rutas de archivos.

Component:

@Component indica que esta clase es un componente de Spring, lo que permite que sea detectada y gestionada por el contenedor de Spring.

El Código:
Variables Miembro

Logger log: Un registrador para registrar mensajes en el log.
Resource txtFile: Representa el archivo de texto a cargar, en este caso, el archivo quran-simple.txt.
String vectorStoreName: El nombre del archivo donde se guardará el "Vector Store".
Método simpleVectorStore

Es un @Bean, lo que significa que Spring lo gestionará y lo proporcionará como un componente en la aplicación.
Crea una instancia de SimpleVectorStore utilizando el EmbeddingModel proporcionado.
Genera la ruta al archivo donde se almacenará el "Vector Store".
Cargar o Crear el "Vector Store"

Si el archivo del "Vector Store" ya existe, se carga.
Si no existe, se procesa el archivo de texto (o PDF, en la versión anterior).

Procesamiento de Texto:

El archivo de texto se carga y se convierte en un único Document.
Luego, se utiliza TokenTextSplitter para dividir el documento en fragmentos más pequeños (chunks).
Los fragmentos se añaden al "Vector Store" y se guarda el archivo.
Manejo de Errores

NonTransientAiException: Se captura para manejar errores relacionados con la API AI, especialmente si se excede la cuota de uso.
Exception: Se captura cualquier otro tipo de error que ocurra durante el procesamiento del archivo.
Método loadTextFile

Lee el contenido del archivo de texto y lo convierte en un Document.
Divide el documento en fragmentos utilizando TokenTextSplitter.
Retorna los fragmentos para ser añadidos al "Vector Store".

Este código es responsable de cargar un archivo de texto, procesarlo en fragmentos pequeños, y almacenar esos fragmentos como vectores en un almacén especializado. Si el almacén ya existe, simplemente se carga; si no, se crea uno nuevo a partir del archivo de texto. Además, maneja posibles errores que puedan ocurrir durante este proceso, como problemas de cuota o errores de lectura del archivo.

## RagRestController:

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
