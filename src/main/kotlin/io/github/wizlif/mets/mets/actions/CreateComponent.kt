package io.github.wizlif.mets.mets.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


class CreateComponent : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        e.createFiles()
    }
}

