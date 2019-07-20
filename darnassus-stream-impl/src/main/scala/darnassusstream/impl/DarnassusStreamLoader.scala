package darnassusstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import darnassusstream.api.DarnassusStreamService
import darnassus.api.DarnassusService
import com.softwaremill.macwire._

//class DarnassusStreamLoader extends LagomApplicationLoader {
//
//  override def load(context: LagomApplicationContext): LagomApplication =
//    new DarnassusStreamApplication(context) {
//      override def serviceLocator: NoServiceLocator.type = NoServiceLocator
//    }
//
//  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
//    new DarnassusStreamApplication(context) with LagomDevModeComponents
//
//  override def describeService = Some(readDescriptor[DarnassusStreamService])
//}
//
//abstract class DarnassusStreamApplication(context: LagomApplicationContext)
//  extends LagomApplication(context)
//    with AhcWSComponents {
//
//  // Bind the service that this server provides
//  override lazy val lagomServer: LagomServer = serverFor[DarnassusStreamService](wire[DarnassusStreamServiceImpl])
//
//  // Bind the DarnassusService client
//  lazy val darnassusService: DarnassusService = serviceClient.implement[DarnassusService]
//}
