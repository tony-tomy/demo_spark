
import org.apache.spark._
import org.apache.log4j._


object HandsOnAmountSpent {

  def customerPricePairs(line: String) : (Int, Float) = {
    val fields = line.split(",")
    (fields(0).toInt, fields(2).toFloat)
  }

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local","HandsOnAmountSpent")

    val lines = sc.textFile("data/customer-orders.csv")

    val mappedInput = lines.map(customerPricePairs)

    val totalByCustomer = mappedInput.reduceByKey( (x,y) => x + y )

    val flipped = totalByCustomer.map( x => ( x._2, x._1))

    val sortedCustomer = flipped.sortByKey()

    val result = sortedCustomer.collect()

    for (output <- result) {
      val customer = output._2
      val amount = output._1
      val formattedAmount = f"$amount%.2f "
      println(s"$customer spent $formattedAmount")
    }


  }

}
