package io.github.wizlif.mets.mets.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


class CreateFileComponent : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        e.createFiles(createFolder = false)
    }
}
