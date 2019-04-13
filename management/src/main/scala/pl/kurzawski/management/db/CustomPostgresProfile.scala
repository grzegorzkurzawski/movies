package pl.kurzawski.management.db

import java.sql.Timestamp

import com.github.tminglei.slickpg.PgArraySupport
import org.joda.time.{DateTime, DateTimeZone}
import slick.jdbc.PostgresProfile

trait CustomPostgresProfile extends PostgresProfile with PgArraySupport {

  override val api: CustomColumnMapperApi.type = CustomColumnMapperApi

  object CustomColumnMapperApi extends API with ArrayImplicits with SimpleArrayPlainImplicits {
    implicit lazy val dateTimeColumnType: BaseColumnType[DateTime] =
      MappedColumnType.base[DateTime, Timestamp]({ dt =>
        new Timestamp(dt.getMillis)
      }, { ts =>
        new DateTime(ts.getTime, DateTimeZone.UTC)
      })
  }
}

object CustomPostgresProfile extends CustomPostgresProfile
