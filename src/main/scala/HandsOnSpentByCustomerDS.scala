
import org.apache.spark.sql.types.{StructType, IntegerType,DoubleType}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.log4j._

object HandsOnSpentByCustomerDS {

  case class CustomerOrders(cust_ID: Int, item_ID: Int, amountSpent: Double)

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("HandsOnSpentByCustomerDS")
      .getOrCreate()

    val customerOrdersSchema = new StructType()
      .add("cust_ID", IntegerType, nullable = true)
      .add("item_ID", IntegerType, nullable = true)
      .add("amountSpent", DoubleType, nullable = true)

    import spark.implicits._
    val ds = spark.read
      .schema(customerOrdersSchema)
      .csv("data/customer-orders.csv")
      .as[CustomerOrders]

    val totalByCust = ds
      .groupBy("cust_ID")
      .agg(round(sum("amountSpent"),2)
      .alias("totalSpent"))

    val results = totalByCust.sort("totalSpent")

    results.show(totalByCust.count.toInt)
  }

}
