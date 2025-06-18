package com.n7.localmind.core.sentence.embedding.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.content.Context
import android.util.Log
import com.n7.localmind.core.sentence.embedding.model.tokenizer.BertTokenizer
import com.n7.localmind.core.sentence.embedding.model.tokenizer.Tokenizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.LongBuffer


class OnnxEmbeddingModelImpl(
    private val context: Context,
    private val modelAssetPath: String = "model.onnx",
    private val tokenizerAssetsDir: String = "",
) : OnnxEmbeddingModel {
    private val modelMaxSeqLength = 256
    private var embeddingDim: Int = 384

    private var ortSession: OrtSession? = null
    private var ortEnv: OrtEnvironment? = null
    private lateinit var tokenizer: Tokenizer
    private var isInitialized = false

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initialize()
        }
    }

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (isInitialized) {
            Log.d("Nick-Onnx-Sentence-Embedding","Model already initialized.")
            return@withContext
        }

        // Initializing OnnxEmbeddingModelImpl
        try {
            ortEnv = OrtEnvironment.getEnvironment()

            val modelBytes = context.assets.open(modelAssetPath).readBytes()

            // Create ORT session
            ortSession = ortEnv?.createSession(modelBytes, OrtSession.SessionOptions())

            tokenizer = BertTokenizer(context, tokenizerAssetsDir)

            determineEmbeddingDimension()

            isInitialized = true
            Log.d("Nick-Onnx-Sentence-Embedding","ONNX Embedding Model Initialized Successfully. Embedding dimension: $embeddingDim")

        } catch (e: Exception) {
            Log.d("Nick-Onnx-Sentence-Embedding","Error initializing ONNX Embedding Model: ${e.message}")
            e.printStackTrace()
            close()
        }
    }

    private fun determineEmbeddingDimension() {
        try {
            val outputName = "sentence_embedding"
            ortSession?.outputInfo?.get(outputName)?.info?.let { info ->
                if (info is TensorInfo && info.shape.size == 2 && info.shape[1] > 0) {
                    embeddingDim = info.shape[1].toInt()
                    Log.d("Nick-Onnx-Sentence-Embedding","Determined embedding dimension from model output '$outputName': $embeddingDim")
                } else {
                    Log.d("Nick-Onnx-Sentence-Embedding","Warning: Could not reliably determine embedding dimension from model output '$outputName'. Using default: $embeddingDim. Shape: ${info.toString()}")
                }
            } ?: Log.d("Nick-Onnx-Sentence-Embedding","Warning: Output '$outputName' not found in model info. Using default dimension: $embeddingDim.")
        } catch (e: Exception) {
            Log.d("Nick-Onnx-Sentence-Embedding","Warning: Error reading model output info. Using default dimension: $embeddingDim. Error: ${e.message}")
        }
    }

    override suspend fun getEmbedding(text: String): FloatArray = withContext(Dispatchers.IO) {
        if (!isInitialized || ortSession == null || ortEnv == null) {
            Log.d("Nick-Onnx-Sentence-Embedding","Error: ONNX Model is not initialized. Call initialize() first.")
            return@withContext FloatArray(embeddingDim)
        }

        var inputIdsTensor: OnnxTensor? = null
        var attentionMaskTensor: OnnxTensor? = null
        var tokenTypeIdsTensor: OnnxTensor? = null

        try {
            val modelInput = tokenizer.tokenize(text, modelMaxSeqLength)

            if (modelInput.inputIds.size != modelMaxSeqLength ||
                modelInput.attentionMask.size != modelMaxSeqLength ||
                modelInput.tokenTypeIds.size != modelMaxSeqLength) {
                Log.d("Nick-Onnx-Sentence-Embedding","Error: Tokenizer output size mismatch. Expected $modelMaxSeqLength for all inputs after padding.")
                Log.d("Nick-Onnx-Sentence-Embedding","Got: inputIds=${modelInput.inputIds.size}, attentionMask=${modelInput.attentionMask.size}, tokenTypeIds=${modelInput.tokenTypeIds.size}")
                return@withContext FloatArray(embeddingDim)
            }

            val shape = longArrayOf(1, modelMaxSeqLength.toLong())

            val inputIdsBuffer = LongBuffer.wrap(modelInput.inputIds)
            val attentionMaskBuffer = LongBuffer.wrap(modelInput.attentionMask)
            val tokenTypeIdsBuffer = LongBuffer.wrap(modelInput.tokenTypeIds)

            inputIdsTensor = OnnxTensor.createTensor(ortEnv!!, inputIdsBuffer, shape)
            attentionMaskTensor = OnnxTensor.createTensor(ortEnv!!, attentionMaskBuffer, shape)
            tokenTypeIdsTensor = OnnxTensor.createTensor(ortEnv!!, tokenTypeIdsBuffer, shape)

            val inputs: Map<String, OnnxTensor> = mapOf(
                "input_ids" to inputIdsTensor,
                "attention_mask" to attentionMaskTensor,
                "token_type_ids" to tokenTypeIdsTensor
            )

            ortSession!!.run(inputs).use { outputs ->
                val outputTensor = outputs.get("sentence_embedding").orElse(null) as? OnnxTensor
                    ?: throw IllegalStateException("Output tensor 'sentence_embedding' not found or not an OnnxTensor.")

                val outputBuffer = outputTensor.floatBuffer
                if (outputBuffer.remaining() == embeddingDim) {
                    val result = FloatArray(embeddingDim)
                    outputBuffer.get(result)
                    return@withContext result
                } else {
                    Log.d("Nick-Onnx-Sentence-Embedding","Error: Output buffer size (${outputBuffer.remaining()}) != expected dimension ($embeddingDim). Output shape: ${outputTensor.info.shape.contentToString()}")
                    return@withContext FloatArray(embeddingDim)
                }
            }

        } catch (e: Exception) {
            Log.d("Nick-Onnx-Sentence-Embedding","Error during embedding generation for text '$text': ${e.message}")
            e.printStackTrace()
            return@withContext FloatArray(embeddingDim)
        } finally {
            try {
                inputIdsTensor?.close()
                attentionMaskTensor?.close()
                tokenTypeIdsTensor?.close()
            } catch (e: Exception) {
                Log.d("Nick-Onnx-Sentence-Embedding","Error closing input tensors: ${e.message}")
            }
        }
    }

    override fun getModelDimensions(): Int = embeddingDim

    override fun close() {
        if (!isInitialized && ortSession == null && ortEnv == null) {
            return
        }
        Log.d("Nick-Onnx-Sentence-Embedding","Closing OnnxEmbeddingModelImpl resources...")
        try {
            ortSession?.close()
            ortEnv?.close()
        } catch (e: Exception) {
            Log.d("Nick-Onnx-Sentence-Embedding","Error closing ONNX resources: ${e.message}")
        } finally {
            ortSession = null
            ortEnv = null
            isInitialized = false
            Log.d("Nick-Onnx-Sentence-Embedding","ONNX resources closed.")
        }
    }
} 