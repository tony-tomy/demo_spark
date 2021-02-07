import pyspark
import pyspark.sql
from pyspark import SparkContext, SparkConf
conf = SparkConf().setAppName("test").setMaster("local")
sc = SparkContext(conf=conf)

cancer = sc.textFile("Average")
print(cancer.collect() == ['58.69'])

# def test_spark_context_fixture_test1():
#     cancer = sc.textFile("Average")
#
#
#     assert cancer.collect() == ['58.69']
#
# def test_spark_context_fixture_test2():
#     cancer = sc.textFile("Average")
#
#
#     assert cancer.collect() == ['58.69']
#
# def test_spark_context_fixture_test3():
#     cancer = sc.textFile("Average")
#
#
#     assert cancer.collect() == ['58.69']
#
# def test_spark_context_fixture_test4():
#     cancer = sc.textFile("Average")
#
#
#     assert cancer.collect() == ['58.69']
