import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import constant.IconPath
import serviceImpl.FileServiceImpl
import java.nio.file.Files
import javax.imageio.ImageIO
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTextField

@Composable
fun App(window_: ComposeWindow) {
    val fileService by remember { mutableStateOf(FileServiceImpl()) }
    var text by remember { mutableStateOf("Hello, World!") }

//    MaterialTheme {
//        Column(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
//            Button(onClick = {
//            }) {
//                Text(text)
//            }
//            TextField(value = text, onValueChange = { text = it })
//        }
//    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        ) {
            SwingPanel(
                background = Color.White,
                modifier = Modifier.fillMaxSize(),
                factory = {
                    JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.Y_AXIS)
                        add(
                            JTextField(text)
                        )
                        add(
                            JButton("Click here").apply {
                                setSize(200, 30)
                                addActionListener {
                                    text = fileService.chooseBasePath(window_)?.toString() ?: "No path selected"
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}

fun main() {
    singleWindowApplication(
        title = "jCamelDecoder",
        icon = ImageIO
            .read(Files.newInputStream(IconPath.defaultIcon))
            .toPainter()
    ) {
        App(this.window)
    }
}
