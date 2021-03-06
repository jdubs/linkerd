package io.buoyant.transformer
package k8s

import io.buoyant.namer._
import java.net.InetAddress

class LocalNodeTransformerInitializer extends TransformerInitializer {
  val configClass = classOf[LocalNodeTransformerConfig]
  override val configId = "io.l5d.k8s.localnode"
}

case class LocalNodeTransformerConfig(hostNetwork: Option[Boolean])
  extends TransformerConfig {

  override def mk(): NameTreeTransformer = {
    if (hostNetwork.getOrElse(false)) {
      val nodeName = sys.env.getOrElse(
        "NODE_NAME",
        throw new IllegalArgumentException(
          "NODE_NAME env variable must be set to the node's name"
        )
      )
      new MetadataFiltertingNameTreeTransformer(Metadata.nodeName, nodeName)
    } else {
      val ip = sys.env.getOrElse(
        "POD_IP",
        throw new IllegalArgumentException(
          "POD_IP env variable must be set to the pod's IP"
        )
      )
      val local = InetAddress.getByName(ip)
      new SubnetLocalTransformer(local, Netmask("255.255.255.0"))
    }
  }

}
