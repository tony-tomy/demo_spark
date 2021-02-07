
import org.apache.spark
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.avg
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._


object firstdemo {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val conf = new SparkConf().setMaster("local[*]").setAppName("FirstDemo")

    val sc = new SparkContext(conf)

    val rdd = sc.parallelize(Array(5,10,30))

    println(rdd.reduce(_+_))

    val lines = sc.textFile("src/main/scala/cancer.csv")

    println(lines.count())

    val spark = org.apache.spark.sql.SparkSession.builder
                .master("local")
                .appName("Spark CSV Reader")
                .getOrCreate

    val df = spark.read.format("csv").option("header", "true").load("src/main/scala/cancer.csv")
    //println(df.head())
    val df2 = df.withColumn("Age", df("Age at Initial Pathologic Diagnosis").cast(IntegerType))
    //println(df2.printSchema())

    //println("avg: "+ df2.select(avg("Age")).collect()(0)(0))

    val dfAvg = df2.agg(avg(df2("Age"))).withColumnRenamed("avg(Age)", "Average")
    println(dfAvg.show())
    println(dfAvg.printSchema())
    //val dfAvg2 = dfAvg.withColumn("Average",col("Average").cast("String"))
    val dfAvg2 = dfAvg.withColumn("Average", round(col("Average"),2).cast("String"))
    println(dfAvg2.printSchema())
    println(dfAvg2.show())

    //val result = dfAvg.withColumn("Average", dfAvg["Average"])

    //val resultDF = dfAvg.withColumn("Average", toString(dfAvg("Average")))

    val rows: RDD[Row] = dfAvg2.rdd

    //dfAvg2.write.text("src/main/scala/Average")


    rows.coalesce(1).saveAsTextFile("src/main/scala/Average")










  }

}
