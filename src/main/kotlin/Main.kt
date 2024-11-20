import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import constant.IconPath
import serviceImpl.FileServiceImpl
import java.awt.Color
import java.awt.Dimension
import java.nio.file.Files
import javax.imageio.ImageIO
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

@Composable
fun App(window_: ComposeWindow) {
    val fileService by remember { mutableStateOf(FileServiceImpl()) }
    var text by remember { mutableStateOf("Hello, World!") }

    SwingPanel(
        modifier = Modifier
            .size(600.dp, 400.dp),
        factory = {
            JPanel()
                .apply {
                    layout = BoxLayout(this, BoxLayout.X_AXIS)
                    add(
                        JPanel().apply {
                            layout = BoxLayout(this, BoxLayout.Y_AXIS)
                            add(JLabel("Base Directory"))
                            add(
                                JLabel(text).apply {
                                    size = Dimension(400, 30)
                                    background = Color.YELLOW
                                    isOpaque = true
                                    foreground = Color(0xff0000)
                                }
                            )
                        }
                    )
//                    size = Dimension(600, 400)
                    add(JButton("Click").apply {
                        size = Dimension(100, 30)
                        addActionListener {
                            fileService.chooseBasePath(window_) { path -> text = path.toString() }
                        }
                    })
                }
        })
}

fun main() {
    singleWindowApplication(
        title = "jCamelDecoder",
        icon = ImageIO
            .read(Files.newInputStream(IconPath.defaultIcon))
            .toPainter(),
        state = WindowState(
            placement = WindowPlacement.Floating, size = DpSize(600.dp, 400.dp),
            position = WindowPosition(
                Alignment.Center
            )
        ),
    ) {
        App(this.window)
    }
}
