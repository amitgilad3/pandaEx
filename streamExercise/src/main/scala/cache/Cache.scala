package cache

import com.typesafe.config.ConfigFactory
import net.sf.ehcache.config.Configuration
import net.sf.ehcache.{ Cache => Ehcache, CacheManager }
import org.slf4j.LoggerFactory

import scalacache.ScalaCache
import scalacache.ehcache.EhcacheCache

object Cache {
  private val logger = LoggerFactory.getLogger(getClass)
  private lazy val config = ConfigFactory.load()
  private lazy val cacheManagerName = config.getString("cache.name")
  //implicit lazy val cache = ScalaCache( new CacheManager())

  private lazy val cacheManager: net.sf.ehcache.CacheManager = {
    val config = new Configuration()
    config.setName(cacheManagerName)
    CacheManager.create()
  }

  private def underlying(cacheName: String) = {
    val cache = new Ehcache(cacheName, 0, false, false, 0, 0)
    cacheManager.addCacheIfAbsent(cache)
    logger.info(s"Cache initialized with name: $cacheName")
    cache
  }

  def getOrCreateCache(cacheName: String = cacheManagerName) = {
    val cache = {
      if (cacheManager.cacheExists(cacheName)) {
        ScalaCache(EhcacheCache(cacheManager.getCache(cacheName)))
      } else {
        ScalaCache(EhcacheCache(underlying(cacheName)))
      }
    }
    cache
  }
  implicit lazy val cache = getOrCreateCache()

}
