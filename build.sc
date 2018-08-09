import mill._
import mill.scalajslib._
import mill.scalalib._

trait Module extends ScalaModule {
  def scalaVersion = "2.12.6"
  val endpointsVer = "0.6.0"
  val circeVer = "0.9.3"
  val scalatagsVer = "0.6.7"
}

trait Test extends TestModule {
  override def moduleDeps =
    if (this == shared.test) super.moduleDeps
    else super.moduleDeps :+ shared.test
  override def ivyDeps = Agg(ivy"org.scalatest::scalatest::3.0.5")
  def testFrameworks = Seq("org.scalatest.tools.Framework")
}

trait JsModule extends Module with ScalaJSModule {
  override def scalaJSVersion = "0.6.24"

  object test extends Test with Tests
}

trait JvmModule extends Module {
  val http4sVer = "0.18.15"
  override def ivyDeps =
    super.ivyDeps() ++ Agg(
      ivy"org.scala-js::scalajs-stubs:${shared.scalaJSVersion()}",
    )

  object test extends Test with Tests
}

object shared extends JsModule {
  override def ivyDeps =
    super.ivyDeps() ++ Agg(
      ivy"io.monix::monix::3.0.0-RC1",
      ivy"org.julienrf::endpoints-algebra:$endpointsVer",
      ivy"org.julienrf::endpoints-algebra-circe:$endpointsVer",
      ivy"io.circe::circe-core::$circeVer",
      ivy"io.circe::circe-generic::$circeVer",
    )
}

object back extends JvmModule {
  override def moduleDeps = Seq(shared)
  override def mainClass = Some("com.crimzie.workshop.Server")
  override def ivyDeps =
    super.ivyDeps() ++ Agg(
      ivy"org.julienrf::endpoints-akka-http-server:$endpointsVer",
      ivy"com.lihaoyi::scalatags:$scalatagsVer",
      ivy"com.github.pathikrit::better-files:3.6.0",
    )
}

object front extends JsModule {
  override def moduleDeps = Seq(shared)
  override def ivyDeps =
    super.ivyDeps() ++ Agg(
      ivy"com.lihaoyi::scalarx::0.4.0",
    )
}

object jvmfront extends JvmModule {
  override def moduleDeps = Seq(front)
  override def mainClass = Some("com.crimzie.workshop.Main")
  override def ivyDeps =
    super.ivyDeps() ++ Agg(
      ivy"org.julienrf::endpoints-sttp-client:$endpointsVer",
      ivy"com.softwaremill.sttp::async-http-client-backend-monix:1.3.0-RC5",
    )
}

object jsfront extends JsModule {
  override def moduleDeps = Seq(front)
  override def ivyDeps =
    super.ivyDeps() ++ Agg(
      ivy"org.julienrf::endpoints-xhr-client::$endpointsVer",
      ivy"org.julienrf::endpoints-xhr-client-circe::$endpointsVer",
      ivy"org.scala-js::scalajs-dom::0.9.2",
      ivy"com.lihaoyi::scalatags::$scalatagsVer",
    )
}
