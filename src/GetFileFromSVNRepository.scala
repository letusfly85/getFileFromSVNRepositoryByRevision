import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import java.lang.reflect.Array
import java.util.Collections
import org.apache.commons.io._
import java.io.{ByteArrayInputStream, File}
import scala.file.Path

import org.tmatesoft.svn.core.io.SVNRepository
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.SVNDepth
import org.tmatesoft.svn.core.SVNDirEntry
import org.tmatesoft.svn.core.SVNNodeKind
import org.tmatesoft.svn.core.SVNProperties
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory

class GetFileFromSVNRepository {

  //クラス内で使い回す環境変数
  val pu = new PropertyUtils
  val prop = pu.load(".properties")
  val username = prop.getProperty("svnUserName")
  val password = prop.getProperty("svnPassWord")
  val baseUrl  = new String(prop.getProperty("svnBaseUrl").getBytes("UTF-8"))

  /**
   * SVNからファイルデータを取得する
   *
   * @param filePath
   * @param revision
   */
  def getFileFromSVNRepository(filePath: String, revision: Long) {
    //ダウンロード先のフォルダ内をクリーンする
    FileUtils.cleanDirectory(new File("work"))

    //SVNへの接続情報を取得する
    DAVRepositoryFactory.setup()
    var repository: SVNRepository = null

    try {
      val authManager: ISVNAuthenticationManager = SVNWCUtil.createDefaultAuthenticationManager(username, password)
      repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(baseUrl))
      repository.setAuthenticationManager(authManager)
    }

    //SVNに対して、ファイルパスとリビジョンを指定してバイナリデータを要求する
    val out: ByteArrayInputStream = new ByteOutputStream()
    var data: Array[Byte] = null

    repository.getFile(
      filePath,
      revision,
      SVNProperties.wrap(Collections.EMPTY_MAP),
      out
    )

    //取得データの書き込み
    data = out.toByteArray

    //TODO scalaxを利用しないようする（commons-langを利用する）
    val buf = Path("work", (new File(filePath)).getName)
    buf.write(data)
  }
}
