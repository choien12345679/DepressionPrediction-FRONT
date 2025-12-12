package com.imp.data.ml

import android.content.Context
import androidx.annotation.RawRes
import smile.base.cart.SplitRule
import smile.classification.RandomForest
import smile.data.DataFrame
import smile.data.formula.Formula
import smile.data.Tuple
import smile.data.vector.IntVector
import smile.data.type.StructType
import java.io.File
import java.io.InputStream
import kotlin.math.sqrt

/**
 * Utility to train/evaluate a RandomForest classifier on the
 * daily life pattern CSV (dlp_labeled.csv).
 *
 * This is intended for offline JVM usage (not shipping in APK).
 */
object DailyLifePatternTrainer {

    // Default path for JVM/local runs. On Android, prefer trainFromStream/trainFromRaw.
    private const val DEFAULT_PATH = "presentation/src/main/assets/dlp_labeled.csv"

    data class TrainingResult(
        val iterations: Int,
        val accuracyMean: Double,
        val precisionMean: Double,
        val recallMean: Double,
        val f1Mean: Double,
        val rocAucMean: Double?,
        val confusionMatrix: Array<IntArray>
    )

    /**
     * Run bootstrapped training/evaluation similar to the Python script (JVM/local file).
     */
    fun train(
        csvPath: String = DEFAULT_PATH,
        iterations: Int = 100,
        testRatio: Double = 0.2
    ): TrainingResult {

        val (features, labels) = loadCsv(csvPath)
        require(features.isNotEmpty()) { "No data loaded from $csvPath" }
        cacheModel(features, labels)
        return trainInternal(features, labels, iterations, testRatio)
    }

    /**
     * Train using a CSV packaged in raw resources (Android).
     */
    fun trainFromRaw(
        context: Context,
        @RawRes rawResId: Int,
        iterations: Int = 100,
        testRatio: Double = 0.2
    ): TrainingResult {

        context.resources.openRawResource(rawResId).use { input ->
            val (features, labels) = loadCsv(input)
            require(features.isNotEmpty()) { "No data loaded from raw resource $rawResId" }
            cacheModel(features, labels)
            return trainInternal(features, labels, iterations, testRatio)
        }
    }

    /**
     * Train using an arbitrary input stream (e.g., assets).
     */
    fun trainFromStream(
        inputStream: InputStream,
        iterations: Int = 100,
        testRatio: Double = 0.2
    ): TrainingResult {

        inputStream.use { stream ->
            val (features, labels) = loadCsv(stream)
            require(features.isNotEmpty()) { "No data loaded from input stream" }
            cacheModel(features, labels)
            return trainInternal(features, labels, iterations, testRatio)
        }
    }

    private fun trainInternal(
        features: Array<DoubleArray>,
        labels: IntArray,
        iterations: Int,
        testRatio: Double
    ): TrainingResult {

        val featureSize = features[0].size
        val accList = ArrayList<Double>()
        val precisionList = ArrayList<Double>()
        val recallList = ArrayList<Double>()
        val f1List = ArrayList<Double>()
        val rocAucList = ArrayList<Double>()
        val cmSum = Array(2) { IntArray(2) }

        repeat(iterations) { idx ->

            val seed = idx + 1
            val split = trainTestSplit(features, labels, testRatio, seed.toLong())
            val model = fitModel(split.trainX, split.trainY, featureSize)
            val rf = model.model
            val schema = model.featureSchema

            val probs = DoubleArray(split.testX.size)
            val preds = IntArray(split.testX.size)

            split.testX.forEachIndexed { i, row ->

                val posterior = DoubleArray(2)
                val tuple = Tuple.of(row, schema)
                preds[i] = rf.predict(tuple, posterior)
                probs[i] = posterior.getOrElse(1) { 0.0 }
            }

            val cm = confusionMatrix(split.testY, preds)
            cmSum[0][0] += cm[0][0]; cmSum[0][1] += cm[0][1]
            cmSum[1][0] += cm[1][0]; cmSum[1][1] += cm[1][1]

            val acc = accuracy(split.testY, preds)
            val precision = precision(split.testY, preds)
            val recall = recall(split.testY, preds)
            val f1 = f1(precision, recall)
            val rocAuc = rocAuc(split.testY, probs)

            accList.add(acc)
            precisionList.add(precision)
            recallList.add(recall)
            f1List.add(f1)
            rocAuc?.let { rocAucList.add(it) }
        }

        return TrainingResult(
            iterations = iterations,
            accuracyMean = accList.average(),
            precisionMean = precisionList.average(),
            recallMean = recallList.average(),
            f1Mean = f1List.average(),
            rocAucMean = rocAucList.takeIf { it.isNotEmpty() }?.average(),
            confusionMatrix = cmSum
        )
    }

    private data class ModelWithSchema(
        val model: RandomForest,
        val featureSchema: StructType
    )

    @Volatile
    private var cachedModel: ModelWithSchema? = null

    fun hasCachedModel(): Boolean = cachedModel != null

    fun predict(features: DoubleArray): Pair<Int, Double> {
        val model = cachedModel ?: error("Model is not trained/cached yet")
        val posterior = DoubleArray(2)
        val tuple = Tuple.of(features, model.featureSchema)
        val pred = model.model.predict(tuple, posterior)
        val confidence = posterior.getOrElse(1) { 0.0 }
        return pred to confidence
    }

    fun predictWithProb(features: DoubleArray): Triple<Int, Double, DoubleArray> {
        val model = cachedModel ?: error("Model is not trained/cached yet")
        val posterior = DoubleArray(2)
        val tuple = Tuple.of(features, model.featureSchema)
        val pred = model.model.predict(tuple, posterior)
        val confidence = posterior.getOrElse(1) { 0.0 }
        return Triple(pred, confidence, posterior)
    }

    private fun fitModel(
        x: Array<DoubleArray>,
        y: IntArray,
        featureSize: Int
    ): ModelWithSchema {

        val featureNames = Array(featureSize) { "f$it" }
        val featureDf = DataFrame.of(x, *featureNames)
        val df = featureDf.merge(IntVector.of("label", y))

        val model = RandomForest.fit(
            Formula.lhs("label"),
            df,
            /* ntree */ 100,
            /* maxNodes */ 0,
            /* split rule */ SplitRule.GINI,
            /* maxDepth */ 20,
            /* nodeSize */ 2,
            /* mtry */ sqrt(featureSize.toDouble()).toInt(),
            /* sampleRate */ 1.0
        )
        return ModelWithSchema(model, featureDf.schema())
    }

    private fun cacheModel(features: Array<DoubleArray>, labels: IntArray): ModelWithSchema {
        val model = fitModel(features, labels, features[0].size)
        cachedModel = model
        return model
    }

    private fun loadCsv(path: String): Pair<Array<DoubleArray>, IntArray> {

        val file = File(path)
        require(file.exists()) { "CSV not found at $path" }

        val featureRows = ArrayList<DoubleArray>()
        val labelList = ArrayList<Int>()

        file.useLines { lines ->
            val iterator = lines.iterator()
            if (!iterator.hasNext()) return@useLines
            val header = iterator.next()
            val columns = parseHeader(header)
            iterator.forEachRemaining { parseLine(it, columns, featureRows, labelList) }
        }

        return featureRows.toTypedArray() to labelList.toIntArray()
    }

    private fun loadCsv(inputStream: InputStream): Pair<Array<DoubleArray>, IntArray> {

        val featureRows = ArrayList<DoubleArray>()
        val labelList = ArrayList<Int>()

        inputStream.bufferedReader().useLines { lines ->
            val iterator = lines.iterator()
            if (!iterator.hasNext()) return@useLines
            val header = iterator.next()
            val columns = parseHeader(header)
            iterator.forEachRemaining { parseLine(it, columns, featureRows, labelList) }
        }

        return featureRows.toTypedArray() to labelList.toIntArray()
    }

    private data class Columns(
        val indices: Map<String, Int>,
        val labelIndex: Int
    )

    private fun parseHeader(header: String): Columns {

        val delimiter = when {
            header.contains(",") -> ","
            header.contains(";") -> ";"
            else -> ","
        }
        val names = header.split(delimiter).map { it.trim() }

        val wanted = listOf(
            "place_diversity",
            "home_stay_percentage",
            "life_routine_consistency",
            "day_phone_use_frequency",
            "night_phone_use_frequency",
            "day_phone_use_duration",
            "night_phone_use_duration",
            "sleeptime_screen_duration",
            "day_call_frequency",
            "night_call_frequency",
            "day_call_duration",
            "night_call_duration",
            "day_light_exposure",
            "night_light_exposure",
            "day_step_count",
            "night_step_count"
        )

        val indexMap = wanted.mapNotNull { key ->
            val idx = names.indexOf(key)
            if (idx >= 0) key to idx else null
        }.toMap()

        val labelIdx = names.indexOfLast { it.equals("label", ignoreCase = true) }
        return Columns(indices = indexMap, labelIndex = labelIdx)
    }

    private fun parseLine(
        line: String,
        columns: Columns,
        featureRows: MutableList<DoubleArray>,
        labels: MutableList<Int>
    ) {

        if (line.isBlank()) return

        val delimiter = when {
            line.contains(",") -> ","
            line.contains(";") -> ";"
            else -> return
        }

        val tokens = line.split(delimiter)
        if (tokens.size <= columns.labelIndex || columns.indices.isEmpty()) return

        val features = DoubleArray(columns.indices.size) { idx ->
            val key = columns.indices.keys.elementAt(idx)
            val tokenIndex = columns.indices[key] ?: -1
            if (tokenIndex >= 0 && tokenIndex < tokens.size) {
                tokens[tokenIndex].toDoubleOrNull() ?: 0.0
            } else 0.0
        }
        val label = tokens[columns.labelIndex].trim().toIntOrNull() ?: return

        featureRows.add(features)
        labels.add(label)
    }

    private data class Split(
        val trainX: Array<DoubleArray>,
        val trainY: IntArray,
        val testX: Array<DoubleArray>,
        val testY: IntArray
    )

    private fun trainTestSplit(
        x: Array<DoubleArray>,
        y: IntArray,
        testRatio: Double,
        seed: Long
    ): Split {

        val indices = x.indices.toMutableList()
        indices.shuffle(java.util.Random(seed))

        val testSize = (x.size * testRatio).toInt().coerceAtLeast(1)
        val testIdx = indices.take(testSize)
        val trainIdx = indices.drop(testSize)

        val trainX = Array(trainIdx.size) { i -> x[trainIdx[i]] }
        val trainY = IntArray(trainIdx.size) { i -> y[trainIdx[i]] }
        val testX = Array(testIdx.size) { i -> x[testIdx[i]] }
        val testY = IntArray(testIdx.size) { i -> y[testIdx[i]] }

        return Split(trainX, trainY, testX, testY)
    }

    private fun accuracy(yTrue: IntArray, yPred: IntArray): Double {
        var correct = 0
        for (i in yTrue.indices) if (yTrue[i] == yPred[i]) correct++
        return correct.toDouble() / yTrue.size
    }

    private fun precision(yTrue: IntArray, yPred: IntArray): Double {
        var tp = 0
        var fp = 0
        for (i in yTrue.indices) {
            if (yPred[i] == 1) {
                if (yTrue[i] == 1) tp++ else fp++
            }
        }
        return if (tp + fp == 0) 0.0 else tp.toDouble() / (tp + fp)
    }

    private fun recall(yTrue: IntArray, yPred: IntArray): Double {
        var tp = 0
        var fn = 0
        for (i in yTrue.indices) {
            if (yTrue[i] == 1) {
                if (yPred[i] == 1) tp++ else fn++
            }
        }
        return if (tp + fn == 0) 0.0 else tp.toDouble() / (tp + fn)
    }

    private fun f1(precision: Double, recall: Double): Double {
        return if (precision + recall == 0.0) 0.0 else 2 * precision * recall / (precision + recall)
    }

    private fun confusionMatrix(yTrue: IntArray, yPred: IntArray): Array<IntArray> {
        val cm = Array(2) { IntArray(2) }
        for (i in yTrue.indices) {
            val r = yTrue[i].coerceIn(0, 1)
            val c = yPred[i].coerceIn(0, 1)
            cm[r][c] += 1
        }
        return cm
    }

    private fun rocAuc(yTrue: IntArray, probs: DoubleArray): Double? {

        val pairs = yTrue.indices.map { i -> probs[i] to yTrue[i] }
        val pos = yTrue.count { it == 1 }
        val neg = yTrue.size - pos
        if (pos == 0 || neg == 0) return null

        val sorted = pairs.sortedByDescending { it.first }
        var tp = 0
        var fp = 0
        var prevTpr = 0.0
        var prevFpr = 0.0
        var auc = 0.0

        for ((score, label) in sorted) {
            if (label == 1) tp++ else fp++
            val tpr = tp.toDouble() / pos
            val fpr = fp.toDouble() / neg
            auc += (fpr - prevFpr) * (tpr + prevTpr) / 2.0
            prevTpr = tpr
            prevFpr = fpr
        }
        return auc
    }
}
