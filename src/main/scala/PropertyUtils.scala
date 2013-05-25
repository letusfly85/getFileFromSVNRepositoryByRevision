import java.io.{File, FileInputStream}
import java.util.Properties

import Tapper._

class PropertyUtils {

  def load(path: String): Properties = {
    new Properties().tap(_.load(new FileInputStream(new File(path).getAbsolutePath)))
  }

}
