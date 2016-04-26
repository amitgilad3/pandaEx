package repository

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterEach, FunSuiteLike, Matchers, Suite}


trait IntegrationTestsTempDbCreator extends FunSuiteLike with Matchers with BeforeAndAfterEach
with ScalaFutures  {
  this: Suite =>
  val  tempDbName = "bigpanda_analytics" + scala.util.Random.nextInt()
  var tempEventRepo:EventRepository = _


  def recreateRepositories: Unit = {
    tempEventRepo = new EventRepository {
      override def dbName = tempDbName
    }

  }
  override def beforeEach {

    recreateRepositories
    super.beforeEach
  }
  override def afterEach {
    super.afterEach
  }


}
