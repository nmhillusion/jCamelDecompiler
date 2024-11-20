package serviceImpl

import constant.IconPath.fileIcon
import constant.IconPath.folderIcon
import java.io.File
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.filechooser.FileView

/**
 * created by: nmhillusion
 * <p>
 * created date: 2024-11-20
 */
class CustomFileView : FileView() {
    override fun getIcon(f: File): Icon {
        // Customize icon based on file type
        return if (f.isDirectory) {
            ImageIcon(
                folderIcon.toString()
            )
        } else if (f.name.endsWith(".txt")) {
            ImageIcon(
                fileIcon.toString()
            )
        } else {
            ImageIcon(
                fileIcon.toString()
            )
        }
    }

    override fun getTypeDescription(f: File): String? {
        // Customize type description
        return if (f.isDirectory) {
            "Directory"
        } else if (f.name.endsWith(".txt")) {
            "Text Document"
        } else {
            null // Use default type description
        }
    }
}