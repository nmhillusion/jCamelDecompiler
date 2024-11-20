package service

import java.nio.file.Path

/**
 * created by: nmhillusion
 *
 *
 * created date: 2024-11-19
 */
interface FileService {
    fun chooseBasePath(parent: java.awt.Component, onSelectDirectory: (Path) -> Unit)
}
