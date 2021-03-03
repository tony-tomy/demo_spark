
import org.apache.log4j._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.DecisionTreeRegressor
import org.apache.spark.sql._

object HandsOnRealEstate {

  case class RegressionSchema(No: Integer, TransactionDate: Double, HouseAge: Double,
                              DistanceToMRT: Double, NumberConvenienceStores: Integer, Latitude: Double,
                              Longitude: Double, PriceOfUnitArea: Double)

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("HandsOnRealEstate")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val dsRaw = spark.read
      .option("sep", ",")
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("data/realestate.csv")
      .as[RegressionSchema]

    val assembler = new VectorAssembler()
      .setInputCols(Array("HouseAge","DistanceToMRT","NumberConvenienceStores"))
      .setOutputCol("features")
    val df = assembler.transform(dsRaw)
      .select("PriceOfUnitArea", "features")

    // Split data to training and testing data

    val trainTest = df.randomSplit(Array(0.5,0.5))
    val trainingDF = trainTest(0)
    val testDF = trainTest(1)

    // model
    val lir = new DecisionTreeRegressor()
      .setFeaturesCol("features")
      .setLabelCol("PriceOfUnitArea")

    val model = lir.fit(trainingDF)

    val fullPredictions = model.transform(testDF).cache()

    val predictionAndLabel = fullPredictions.select("prediction","PriceOfUnitArea").collect()

    for(prediction <- predictionAndLabel){
      println(prediction)
    }

    spark.stop()

  }

}
