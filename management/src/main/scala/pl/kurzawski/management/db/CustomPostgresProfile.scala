package pl.kurzawski.management.db

import com.github.tminglei.slickpg.PgArraySupport
import slick.jdbc.PostgresProfile

trait CustomPostgresProfile extends PostgresProfile with PgArraySupport {

  override val api: CustomColumnMapperApi.type = CustomColumnMapperApi

  object CustomColumnMapperApi extends API with ArrayImplicits with SimpleArrayPlainImplicits
}

object CustomPostgresProfile extends CustomPostgresProfile
