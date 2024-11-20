package serviceImpl

import service.FileService
import tech.nmhillusion.n2mix.helper.log.LogHelper
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-11-19
 */
class FileServiceImpl : FileService {
    override fun chooseBasePath(parent: java.awt.Component, onSelectDirectory: (Path) -> Unit) {
        SwingUtilities.invokeLater {
            val fileChooser = JFileChooser()
            fileChooser.isMultiSelectionEnabled = false
            fileChooser.setBounds(0, 0, 800, 600)
            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            fileChooser.fileView = CustomFileView()
//            fileChooser.updateUI()

            val dialogResult = fileChooser.showOpenDialog(parent)

            if (dialogResult == JFileChooser.APPROVE_OPTION) {
                val chosenPath = fileChooser.selectedFile.toPath()

                LogHelper.getLogger(this).info("Chosen path: {}", chosenPath)

                onSelectDirectory(chosenPath)
            }
        }
    }
}
