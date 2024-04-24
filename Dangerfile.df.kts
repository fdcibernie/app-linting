import org.w3c.dom.Element
import org.w3c.dom.Node
import systems.danger.kotlin.*
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

val danger = Danger(args)

danger.onGitHub {
    val detektReportPath = "app/build/reports/detekt/detekt.xml" // replace with your actual Detekt report path
    val detektReportFile = File(detektReportPath)

    if (detektReportFile.exists()) {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(detektReportFile)
        doc.documentElement.normalize()

        val errorList = doc.getElementsByTagName("error")

        for (i in 0 until errorList.length) {
            val errorNode = errorList.item(i)
            if (errorNode.nodeType == Node.ELEMENT_NODE) {
                val errorElement = errorNode as Element
                val source = errorElement.getAttribute("source")
                val message = errorElement.getAttribute("message")
                val comment = "$source: $message"
                warn(comment)
                // danger.github.issue.createComment(comment)
            }
        }
    } else {
        fail("Detekt report not found at $detektReportPath")
    }
}
