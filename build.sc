import mill._
import mill.scalajslib._
import mill.scalalib._

trait Module extends ScalaModule {
  override def scalacOptions: Target[Seq[String]] =
    Seq("-deprecation", "-feature")
  def scalaVersion = "2.12.6"
  val monixVersion = "3.0.0-RC1"
  val betterFilesVersion = "3.6.0"
  val endpointsVersion = "0.6.0"
  val circeVersion = "0.9.3"
  val ahcbmVersion = "1.3.0-RC5"
  val scalatagsVersion = "0.6.7"
  val outwatchVersion = "1.0.0-RC2"
}

trait Test extends TestModule {
  override def moduleDeps: Seq[JavaModule] =
    if (this == shared.test) super.moduleDeps
    else super.moduleDeps :+ shared.test
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    Agg(ivy"org.scalatest::scalatest::3.0.5")
  def testFrameworks: Target[Seq[String]] =
    Seq("org.scalatest.tools.Framework")
}

trait JsModule extends Module with ScalaJSModule {
  override def scalaJSVersion: Target[String] = "0.6.25"

  object test extends Test with Tests
}

trait JvmModule extends Module {
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"org.scala-js::scalajs-stubs:${shared.scalaJSVersion()}",
    )

  object test extends Test with Tests
}

object shared extends JsModule {
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"org.julienrf::endpoints-algebra:$endpointsVersion",
      ivy"org.julienrf::endpoints-algebra-circe:$endpointsVersion",
      ivy"io.circe::circe-core::$circeVersion",
      ivy"io.circe::circe-generic::$circeVersion",
    )
}

object back extends JvmModule {
  override def moduleDeps: Seq[JavaModule] = Seq(shared)
  override def mainClass: Target[Some[String]] =
    Some("com.crimzie.workshop.Main")
  //override def runClasspath = T { super.runClasspath() ++ jsfront.fastOpt() }
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"io.monix::monix::$monixVersion",
      ivy"org.julienrf::endpoints-akka-http-server:$endpointsVersion",
      ivy"com.lihaoyi::scalatags:$scalatagsVersion",
      ivy"com.github.pathikrit::better-files:$betterFilesVersion",
    )
}

object jvmclient extends JvmModule {
  override def moduleDeps: Seq[JavaModule] = Seq(shared)
  override def mainClass: Target[Some[String]] =
    Some("com.crimzie.workshop.Main")
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"io.monix::monix::$monixVersion",
      ivy"org.julienrf::endpoints-sttp-client:$endpointsVersion",
      ivy"com.softwaremill.sttp::async-http-client-backend-monix:$ahcbmVersion",
    )
}

object jsclient extends JsModule {
  override def moduleDeps: Seq[JavaModule] = Seq(shared)
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"org.julienrf::endpoints-xhr-client::$endpointsVersion",
      ivy"org.julienrf::endpoints-xhr-client-circe::$endpointsVersion",
    )
}

object jsfront extends JsModule {
  override def moduleDeps: Seq[JavaModule] = Seq(shared)
  override def moduleKind: Target[ModuleKind] =
    T { ModuleKind.CommonJSModule }
  override def mainClass: Target[Some[String]] =
    Some("com.crimzie.workshop.Main")
  override def ivyDeps: Target[Loose.Agg[Dep]] =
    super.ivyDeps() ++ Agg(
      ivy"org.julienrf::endpoints-xhr-client::$endpointsVersion",
      ivy"org.julienrf::endpoints-xhr-client-circe::$endpointsVersion",
      ivy"io.github.outwatch::outwatch::$outwatchVersion",
    )
}
