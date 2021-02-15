
import org.apache.spark.sql._
import org.apache.log4j._
import org.apache.spark.sql.functions._

object HandsOnFriendsByAgeDS2 {

  case class FakeFriends(id:Int, name:String, age:Int, friends:Long)

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .appName("HandsOnFriendsByAgeDS2")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val schemaFakeFriends = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("data/fakefriends.csv")
      .as[FakeFriends]

    schemaFakeFriends.printSchema()

    val friendsByAge = schemaFakeFriends.select("age", "friends")

    friendsByAge.groupBy("age").avg("friends").show()

    friendsByAge.groupBy("age").agg(round(avg("friends"), 2)).show()

    friendsByAge.groupBy("age").agg(round(avg("friends"),2).alias("friends_avg")).show()

    spark.stop()
  }

}
